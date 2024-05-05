package com.bitbank.smsnotification.input;

import com.allaire.util.InvalidColumnIndexException;
import com.allaire.util.InvalidRowIndexException;
import com.allaire.util.SimpleRecordSet;
import com.allaire.wddx.WddxDeserializationException;
import com.allaire.wddx.WddxDeserializer;
import com.bitbank.bitutils.utils.ResourceUtils;
import com.bitbank.bitutils.utils.SecurityUtils;
import com.bitbank.loyaltycommons.domain.CanonicalRequest;
import com.bitbank.loyaltycommons.domain.CanonicalResponse;
import com.bitbank.loyaltycommons.domain.ErrorStatus;
import com.bitbank.notificator.core.sender.EmailSender;
import com.bitbank.smsnotification.dao.UfnMessageDao;
import com.bitbank.smsnotification.domain.*;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import com.bitbank.smsnotification.service.SmsSchedulerService;
import com.bitbank.smsnotification.utils.SiebelUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.feature.Features;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("UfnInputController")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
public class UfnInputController {

    public static final String IDENTIF_VERSION = "version";
    public static final String IDENTIF_VERSION_VALUE = "1.0.1";
    private static Logger log = Logger.getLogger(UfnInputController.class);

    @Autowired
    private SmsSchedulerService smsService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ResourceUtils resources;

    @Autowired
    private SiebelUtils siebelUtils;

    @Autowired
    private UfnMessageDao ufnMessageDao;

    @Autowired
    private PropertiesConfiguration properties;

    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public static final String CONTROLLER_PATH = "/ufn";
    private static final String METHOD_OPERATION_PERFORMED = "/operationPerformed";

    private static final String HPAN_CARDNUMBER = "HPAN";

    @Value(value = "${ClientInfoService.URL}")
    private String clientInfoUrl;

    //todo errorcode=1 will not repeate sms!
    @POST
    @Path(METHOD_OPERATION_PERFORMED)
    @Produces(MediaType.TEXT_PLAIN_VALUE)
    @Consumes(MediaType.TEXT_XML_VALUE)
    public @ResponseBody String transactionPerformedNotification(@RequestBody String wddxPacket) {
        log.debug("Got new wddxPacket: "+wddxPacket);
        Date requestDate = new Date();
        List<Map<String, Object>> request = null;
        List<SmsMessage> smsMessages = new ArrayList<>();
        List<UfnMessageEntity> ufnMessages = new ArrayList<>();
        String response = null;

        try {
            request = parseUfsWddxAsMaps(wddxPacket);

            for (Map<String, Object> transactionInfo: request) {

                String phone = null;
                try {
                    String smsText = makeSmsText(transactionInfo);

                    String hashCardNum = securityUtils.getMd5Hash((String) transactionInfo.get(HPAN_CARDNUMBER));
                    transactionInfo.put(HPAN_CARDNUMBER, hashCardNum);

                    // Проверка принадлежит ли sms к программе лояльности.
                    boolean isLoyaltyProgram = checkingTheLoyaltyProgram(transactionInfo);

                    // Если не принадлежит к программе лояльности, то отправить sms.
                    if (!isLoyaltyProgram) {
                        phone = siebelUtils.getPhoneByCardHash(hashCardNum);
                        if (StringUtils.isNotEmpty(phone)) {
                            SmsMessage sms = new SmsMessage(CONTROLLER_PATH+METHOD_OPERATION_PERFORMED, null, requestDate, MessagePriority.NORMAL, SmsMessageType.CLIENT_NOTIFICATION);
                            sms.setPhone(phone);
                            sms.setSmsText(smsText);

                            //save request to DB, we should hash card number!
                            sms.setSourceData(transactionInfo.toString());
                            smsMessages.add(sms);

                            try {
                                ufnMessages.add(createUfnMessageEntity(transactionInfo, sms));
                            } catch (Exception e) {
                                log.error("Could not add unf entity for analysis", e);
                            }
                        } else {
                            response = "ErrorKod=1\nPhone number not found for card number: "+transactionInfo.get(HPAN_CARDNUMBER);
                            ufnMessages.add(createUfnMessageEntity(transactionInfo, phone));
                        }
                    } else {
                        response = "ErrorKod=0";
                    }
                } catch (JAXBException | NoSuchAlgorithmException | SOAPFaultException e) {
                    log.error("Couldn't take phone for card", e);
                    ufnMessages.add(createUfnMessageEntity(transactionInfo, phone));
                    response = "ErrorKod=1\nExceptions is: "+e.getMessage();
                }
            }

            if (!smsMessages.isEmpty()) {
                try {
                    smsService.addSmsMessagesToQueue(smsMessages);

                    response = "ErrorKod=0";
                } catch (HibernateException e) {
                    log.error("Failed save messages", e);
                    emailSender.sendErrorMail(e);
                    response = "ErrorKod=1\nException message is: "+e.getMessage();
                }
            }

            try {
                ufnMessageDao.save(ufnMessages);//todo on getPhone error card number is not hashed
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                emailSender.sendErrorMail(e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response = "ErrorKod=1\nExceptions is: "+e;
        }

        return response;
    }

    private UfnMessageEntity createUfnMessageEntity(Map<String, Object> transactionInfo, SmsMessage sms) {
        return new UfnMessageEntity(transactionInfo, sms, properties.getString("messagesender.implementation.class"));
    }

    private UfnMessageEntity createUfnMessageEntity(Map<String, Object> transactionInfo, String phone) {
        return new UfnMessageEntity(transactionInfo, phone, properties.getString("messagesender.implementation.class"));
    }

    protected List<Map<String, Object>> parseUfsWddxAsMaps(String wddxPacket) throws SAXException, IOException, SQLException, ParserConfigurationException, WddxDeserializationException, InvalidColumnIndexException, InvalidRowIndexException {
        List<Map<String, Object>> result = new ArrayList<>();

        InputSource wddxSource = new InputSource(new StringReader(wddxPacket));

        WddxDeserializer wddxDeserializer =
                new WddxDeserializer(SAXParserFactory.newInstance().newSAXParser()
                        .getParser().getClass().getName());

        Vector<SimpleRecordSet> recordSetList = (Vector<SimpleRecordSet>) wddxDeserializer.deserialize(wddxSource);

        for (SimpleRecordSet recordSet: recordSetList) {
            result.add(wddxRecordSetToMap(recordSet));
        }

        return result;
    }

    protected Map<String, Object> wddxRecordSetToMap(SimpleRecordSet recordSet) throws SQLException, InvalidColumnIndexException, InvalidRowIndexException {
        Map<String, Object> resultMap = new HashMap<>(recordSet.getRowCount());

        for (int row=0; row<recordSet.getRowCount(); row++) {
                resultMap.put((String) recordSet.getField(row, 0), recordSet.getField(row, 1));
        }

        return resultMap;
    }

    protected String makeSmsText(Map<String, Object> params) {
        return makeSmsText((String)params.get("RESP"), (String)params.get("TXNCODE"), params);
    }

    protected String makeSmsText(String status, String transactionCode, Map<String, Object> params) {
        Object[] paramsArr = new Object[7];
        paramsArr[0] = getLastDigitsFromCard((String) params.get(HPAN_CARDNUMBER));
        paramsArr[1] = params.get("REQAMT");
        paramsArr[2] = "UAH";//todo Currency.getInstance((String) params.get("CURRENCY")/*"UAH"*/).getDisplayName();
        paramsArr[3] = formatDate((String) params.get("FLD_012"));
        paramsArr[4] = params.get("ADDRESS_NAME");
        Object netBal = params.get("NETBAL");

        if (netBal instanceof Double) {
            paramsArr[5] = String.format("%.2f", netBal);
        } else {
            paramsArr[5] = params.get("NETBAL");
        }

        try {
            Double percent = Double.valueOf((String)params.get("REQAMT")) * properties.getDouble("ufn.operation.komission");//percent
            paramsArr[6] = String.format("%.2f", percent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            emailSender.sendErrorMail(e);
            throw e;
        }

        try {
            return resources.getMessage("ufn.sms.text."+status+"."+transactionCode, paramsArr);
        } catch (NoSuchMessageException e) {
            return resources.getMessage("ufn.sms.text."+status, paramsArr);
        }
    }

    protected String getLastDigitsFromCard(String cardNum) {
        return "*"+cardNum.substring(cardNum.length()-4);
    }

    protected String formatDate(String dateStr) {
        try {
            Date inputDate = inputDateFormat.parse(dateStr);
            DateTime dt = new DateTime(inputDate.getTime());
            return outputDateFormat.format(dt.plusHours(properties.getInt("ufn.operations.add.hours", 0)).toDate());
        } catch (ParseException e) {
            return dateStr;
        }
    }

    /**
     * Проверка sms на принадлежность к программе лояльности.
     *
     * @param parameterMap Параметры sms
     * @return Результат выполнения.
     */
    private boolean checkingTheLoyaltyProgram(Map<String, Object> parameterMap) {
        boolean result = false;
        log.debug("checkingTheLoyaltyProgram() start");

        // Получить значение параметра "url" для взаимодействия с InventorySystem.
        String addressUrl = properties.getString("loyaltymwservice.url.identification");
        if (StringUtils.isNotEmpty(addressUrl)) {
            try {
                // Подготовить запрос для передачи на сервер.
                CanonicalRequest canonicalRequest = new CanonicalRequest();
                // Формирование заголовка запроса.
                canonicalRequest.getHeader().put(IDENTIF_VERSION, IDENTIF_VERSION_VALUE);
                // Формирование параметров тела запроса.
                for (String parameterKey : parameterMap.keySet()) {
                    Object object = parameterMap.get(parameterKey);
                    if (object != null) {
                        canonicalRequest.getBody().put(parameterKey, object.toString());
                    }
                }

                WebClient client = WebClient.create(addressUrl);
                client.type(javax.ws.rs.core.MediaType.APPLICATION_XML);
                client.accept(javax.ws.rs.core.MediaType.APPLICATION_XML);
                WebClient.getConfig(client).getInInterceptors().add(new LoggingInInterceptor());
                WebClient.getConfig(client).getOutInterceptors().add(new LoggingOutInterceptor());

                // Передать запрос на сервер.
                Response clientResponse = client.post(canonicalRequest);
                // Прочитать ответ от сервера.
                CanonicalResponse canonicalResponse = clientResponse.readEntity(CanonicalResponse.class);
                if (canonicalResponse != null) {
                    ErrorStatus errorStatus = null;
                    if (canonicalResponse.getError() != null) {
                        errorStatus = canonicalResponse.getError();
                    }
                    if (errorStatus != null) {
                        log.debug("canonicalResponse.ErrorCode=" + errorStatus.getErrorCode()
                                + " ResultCode.ErrorText='" + errorStatus.getErrorText() + "'");
                        // ErrorCode = 0, sms принадлежит к программе лояльности.
                        // ErrorCode > 0, внутренний сбой сервиса, но sms принадлежит к программе лояльности.
                        // ErrorCode < 0, критическая ошибка сервиса.
                        result = (errorStatus.getErrorCode() > -1);
                    }
                }
            } catch (Exception e) {
                log.error("Exception", e);
            }
        }
        log.debug("checkingTheLoyaltyProgram() finish");
        return result;
    }
}
