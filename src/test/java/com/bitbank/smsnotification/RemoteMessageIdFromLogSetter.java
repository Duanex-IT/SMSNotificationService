package com.bitbank.smsnotification;

import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteMessageIdFromLogSetter {

    public static final String TRYING_TO_SEND_STR = "Trying to send message: SmsMessage{messageId=";
    public static final String MESSAGE_SUBMITTED_STR = "Message submitted, message_id is ";

    public static void main(String[] args) throws IOException {
        String updateSql = "update ns_sms_request set remoteMessageId='%s' where messageId=%s and remoteMessageId is null;";
        Map<Long, String> linkMap = new HashMap<>();

        String path = "d:\\job\\temp\\2013-12-11\\smslogs\\";
        List<String> files = getFilesList(path);

        for (String file: files) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String currentLine = bufferedReader.readLine();
            while (currentLine != null) {//avoiding last string, because it is header
                //processing
                if (currentLine.contains(TRYING_TO_SEND_STR)) {
                    int pos = currentLine.indexOf(TRYING_TO_SEND_STR) + TRYING_TO_SEND_STR.length();
                    try {
                        Long msgId = Long.valueOf(currentLine.substring(pos, pos+4));

                        if (msgId >= 9115l && msgId <= 9287l && ",".equals(currentLine.substring(pos+4, pos+5))) {
                            String remoteId = null;
                            do {
                                currentLine = bufferedReader.readLine();
                                if (currentLine.contains(MESSAGE_SUBMITTED_STR)) {
                                    int remIdIndex = currentLine.indexOf(MESSAGE_SUBMITTED_STR) + MESSAGE_SUBMITTED_STR.length();
                                    remoteId = currentLine.substring(remIdIndex, remIdIndex+11);
                                }
                            } while (remoteId == null || currentLine.contains(TRYING_TO_SEND_STR));

                            if (remoteId != null) {
                                linkMap.put(msgId, remoteId);
                            }
                        }
                    } catch (NullPointerException | NumberFormatException e) {

                    }
                }

                currentLine = bufferedReader.readLine();
            }
        }

        System.out.println("rows count: "+linkMap.size());
        for (Long mesId: linkMap.keySet()) {
            System.out.println(String.format(updateSql, linkMap.get(mesId), mesId));
        }
    }

    /**
     * Получить список файлов по указанному полному пути к каталогу.
     * duplicated from dataProcessor
     * @param specifyPathTheFolder Полный путь к каталогу (можно без финального символа '/')
     * @return Список файлов (с указанем полного пути).
     * @throws IOException
     */
    public static List<String> getFilesList(String specifyPathTheFolder) throws IOException {
        List<String> listFiles = new ArrayList<String>();
        if (!StringUtils.isEmpty(specifyPathTheFolder)) {
            File pathObject = new File(specifyPathTheFolder);

            if (!pathObject.exists()) {
                throw new IOException("Cannot access " + specifyPathTheFolder + ": No such file or directory.");
            }

            File[] arrayFile = pathObject.listFiles();
            for (File elementFile : arrayFile) {
                if (elementFile.isFile()) {
                    listFiles.add(elementFile.toString());
                }
            }
        }
        return listFiles;
    }

}
