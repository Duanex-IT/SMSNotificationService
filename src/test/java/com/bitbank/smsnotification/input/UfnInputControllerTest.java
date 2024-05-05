package com.bitbank.smsnotification.input;

import com.allaire.util.InvalidColumnIndexException;
import com.allaire.util.InvalidRowIndexException;
import com.allaire.util.SimpleRecordSet;
import com.allaire.wddx.WddxDeserializationException;
import com.allaire.wddx.WddxDeserializer;
import com.bitbank.bitutils.utils.SecurityUtils;
import com.bitbank.smsnotification.dao.SmsMessageDao;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class UfnInputControllerTest {
    //todo unignore tests!!!
    public static final String CARDNUMBER = "cardnumber";
    @Autowired
    private UfnInputController ufnInputController;

    @Autowired
    private SmsMessageDao smsDao;

    @Autowired
    private SecurityUtils securityUtils;

    private static String reqString = "<wddxPacket version='1.0'>" +
            "<header/>" +
            "<data>" +
            "<array length='1'>" +
            "<recordset rowCount='13' fieldNames='param,value' type='coldfusion.sql.QueryTable'>" +
            "<field name='param'>" +
            "<string>HPAN</string>" +
            "<string>REQAMT</string>" +
            "<string>RESP</string>" +
            "<string>FLD_012</string>" +
            "<string>CARDACCIDC</string>" +
            "<string>CURRENCY</string>" +
            "<string>NETBAL</string>" +
            "<string>ADDRESS_NAME</string>" +
            "<string>CNVT_CURRENCY</string>" +
            "<string>UTRNNO</string>" +
            "<string>REVERSAL</string>" +
            "<string>TXNCODE</string>" +
            "<string>CREDIT_LIMIT</string>" +
            "</field>" +
            "<field name='value'>" +
            "<string>"+CARDNUMBER+"</string>" +
            "<string>123.45</string>" +
            "<string>ET</string>" +
            "<string>20081022110355</string>" +
            "<string>TERMCODE</string>" +
            "<string>980</string>" +
            "<number>567.89</number>" +
            "<string>ABC street, 25, TC XYZ</string>" +
            "<string>980</string>" +
            "<string>012345</string>" +
            "<string>0</string>" +
            "<string>1</string>" +
            "<number>10000.00</number>" +
            "</field>" +
            "</recordset>" +
            "</array>" +
            "</data>" +
            "</wddxPacket>";

    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();//todo test apache cxf
    }

    @Ignore
    @Test
    public void mainPage() throws Exception {
        mockMvc.perform(post("/ufn/operationPerformed").contentType(MediaType.TEXT_XML).content(reqString))
                .andExpect(status().isOk())
                /*.andExpect(view().name("index"))*/;

        //delete inserted message
        List<SmsMessage> messages = smsDao.findAll();
        String text = "Карта cardnumber; Отказ: неуспешная авторизация; Дата: 22.10.2008 16:55;Место: ABC street, 25, TC XYZ. BitCredit";
        String source = "{HPAN=9a16f62d227ecc445cfdbb3928df890, REQAMT=123.45, ADDRESS_NAME=ABC street, 25, TC XYZ, CNVT_CURRENCY=980, RESP=ET, TXNCODE=1, NETBAL=567.89, CURRENCY=980, FLD_012=20081022110355, CREDIT_LIMIT=10000.0, REVERSAL=0, CARDACCIDC=TERMCODE, UTRNNO=012345}";
        for (SmsMessage sms: messages) {
            if (text.equals(sms.getSmsText()) && source.equals(sms.getSourceData())) {
                smsDao.delete(sms);
            }
        }
    }

    @Ignore
    @Test
    public void sendDataBadRequest() throws Exception {
        mockMvc.perform(post("/ufn/operationPerformed"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testParseUfsWddxAsMaps() throws SAXException, IOException, SQLException, ParserConfigurationException, InvalidRowIndexException, InvalidColumnIndexException, WddxDeserializationException {
        List<Map<String, Object>> result = ufnInputController.parseUfsWddxAsMaps(reqString);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(13, result.get(0).size());

    }

    @Test
    public void testWddxRecordSetToMap() throws SAXException, IOException, SQLException, ParserConfigurationException, WddxDeserializationException, InvalidColumnIndexException, InvalidRowIndexException {
        InputSource wddxSource = new InputSource(new StringReader(reqString));

        WddxDeserializer wddxDeserializer =
                new WddxDeserializer(SAXParserFactory.newInstance().newSAXParser()
                        .getParser().getClass().getName());

        Vector<SimpleRecordSet> recordSetList = (Vector<SimpleRecordSet>) wddxDeserializer.deserialize(wddxSource);
        Map<String, Object> result = ufnInputController.wddxRecordSetToMap(recordSetList.get(0));

        assertNotNull(result);
        assertEquals(13, result.size());
    }

    @Ignore
    @Test
    public void testRequestProcessing() throws Exception {
        String sourceData = "{HPAN=9a16f62d227ecc445cfdbb3928df890, REQAMT=123.45, ADDRESS_NAME=ABC street, 25, TC XYZ, CNVT_CURRENCY=980, RESP=ET, TXNCODE=1, NETBAL=567.89, CURRENCY=980, FLD_012=20081022110355, CREDIT_LIMIT=10000.0, REVERSAL=0, CARDACCIDC=TERMCODE, UTRNNO=012345}";

        //post data
        mockMvc.perform(post("/ufn/operationPerformed").contentType(MediaType.TEXT_XML).content(reqString))
                .andExpect(status().isOk());

        //check sms is in db
        List<SmsMessage> smss = smsDao.findWaitingForSend();

        //check sourceData has card number cashed
        for (SmsMessage sms: smss) {
            assertFalse(StringUtils.contains(sms.getSourceData(), CARDNUMBER));
            //delete inserted sms
            if (sourceData.equals(sms.getSourceData())) {
                smsDao.delete(sms);
            }
        }
    }

    @Test
    public void testMakeSmsText() {
        Map<String, Object> params = new HashMap<>(7);
        params.put("HPAN", "1234123412341234");
        params.put("REQAMT", "123.45");
        params.put("CURRENCY", "UAH");
        params.put("FLD_012", "20081022110355");
        params.put("ADDRESS_NAME", "ABC street, 25, TC XYZ");
        params.put("NETBAL", "567.89");
        params.put("CNVT_CURRENCY", "980");

        params.put("RESP", "AP");
        params.put("TXNCODE", "2");

        assertFalse(StringUtils.contains(ufnInputController.makeSmsText(params), "{"));
        assertEquals(ufnInputController.makeSmsText(params), ufnInputController.makeSmsText("AP", "3", params));

        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("AP", "2", params), "{"));
        assertEquals(ufnInputController.makeSmsText("AP", "2", params), ufnInputController.makeSmsText("AP", "3", params));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("OE", "1", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("AP", "21", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("AP", "28", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("AP", "0", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("AP", "1", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("AP", "11", params), "{"));

        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("SL", "11", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("LA", "11", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("LQ", "11", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("EP", "11", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("NF", "11", params), "{"));
        assertFalse(StringUtils.contains(ufnInputController.makeSmsText("ET", "11", params), "{"));
    }

    @Test
    public void testGetLastDigitsFromCard() {
        assertEquals("*1234", ufnInputController.getLastDigitsFromCard("09876543211234"));
        assertEquals("*2341", ufnInputController.getLastDigitsFromCard("2341"));
        assertEquals("*0000", ufnInputController.getLastDigitsFromCard("11*0000"));
    }

    @Test
    public void testFormatDate() {
        assertEquals("22.11.2012 01:00", ufnInputController.formatDate("201211212300"));
        assertEquals("01.01.2000 02:00", ufnInputController.formatDate("200001010000"));

        assertEquals("xxxyyy", ufnInputController.formatDate("xxxyyy"));
    }

    @Test
    public void testFormatDate2() throws NoSuchAlgorithmException {
        System.out.println(securityUtils.getMd5Hash("4646431100000248"));//97156cb2a128f57821cec97f76cc573
    }

}
