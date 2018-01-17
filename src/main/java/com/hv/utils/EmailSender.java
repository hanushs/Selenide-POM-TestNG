package com.hv.utils;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.xml.XmlTest;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by shanush on 1/11/2018.
 */
public class EmailSender {
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class);

    public static void sendEmailTo(final XmlTest testContext, String... emails) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("pentaho.auto.qa", "p@ssw0rd1");
                    }
                });

        try {

            for (String email : emails) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("pentaho.auto.qa@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(email));
                message.setSubject(testContext.getName() + " TEST RESULTS");

                String suiteName = testContext.getSuite().getName();
                String testName = testContext.getName();
                Multipart multipart = new MimeMultipart();
                MimeBodyPart textBodyPart = new MimeBodyPart();
                StringBuilder sb = new StringBuilder();
                sb.append(testContext.getName() + " has been executed. For results see attached file");
                textBodyPart.setText(sb.toString());
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                Thread.sleep(3000);
                File file = new File("test-output\\" + suiteName + "\\" + testName + ".html");
                while(!file.exists()){
                    LOGGER.info("File " + file + " doean't exists. Waiting for 1 sec");
                    Thread.sleep(1000);
                }
                DataSource source = new FileDataSource(file);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(testName + " report.html");
                multipart.addBodyPart(attachmentBodyPart);
                multipart.addBodyPart(textBodyPart);
                message.setContent(multipart);


                Transport.send(message);

                System.out.println("Email sent to " + email);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static String testsParse(String testsToParse) {
        List<String> passed = new ArrayList<>();
        passed = Arrays.asList(testsToParse.split("],"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passed.size(); i++) {
            sb.append(passed.get(i).replace("[pri", "+").replace("[", "").split("[+]")[0]).append("\n");
        }
        return sb.toString();
    }

    private static String getReportSource(String suiteName) {
        File report = new File("test-output//" + suiteName + "//" + suiteName + ".html");
        StringBuilder sb = new StringBuilder();
        try {
            Scanner scanner = new Scanner(report);
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
