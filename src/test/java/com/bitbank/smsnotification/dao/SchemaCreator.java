package com.bitbank.smsnotification.dao;

import com.bitbank.smsnotification.domain.DeliveryReport;
import com.bitbank.smsnotification.domain.UfnMessageEntity;
import com.bitbank.smsnotification.domain.message.IncomingSmsMessage;
import com.bitbank.smsnotification.domain.message.InputSmsDistributionEntity;
import com.bitbank.smsnotification.domain.message.SmsMessage;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.util.Properties;

public class SchemaCreator {

    public static void main(String[] args) {
        Configuration config = new Configuration();

        Properties properties = new Properties();

        properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        properties.put("hibernate.connection.url", "jdbc:oracle:thin:@192.168.103.11:1521/esb");
        properties.put("hibernate.connection.username", "smsnotif");
        properties.put("hibernate.connection.password", "Geen$type91");
        properties.put("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.id.new_generator_mappings", "true");
        config.setProperties(properties);

        config.addAnnotatedClass(SmsMessage.class);
        config.addAnnotatedClass(DeliveryReport.class);
        config.addAnnotatedClass(UfnMessageEntity.class);
        config.addAnnotatedClass(InputSmsDistributionEntity.class);
        config.addAnnotatedClass(IncomingSmsMessage.class);

        SchemaExport schemaExport = new SchemaExport(config);
        schemaExport.setDelimiter(";");

        /**Just dump the schema SQLs to the console , but not execute them ***/
        schemaExport.create(true, false);
    }

}
