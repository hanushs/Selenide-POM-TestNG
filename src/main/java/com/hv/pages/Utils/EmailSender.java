package com.hv.pages.Utils;

import com.hv.pages.base.LoginPage;
import org.apache.log4j.Logger;
import org.testng.ITestContext;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shanush on 1/11/2018.
 */
public class EmailSender {
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class);

    public static void sendEmailTo(final ITestContext testContext, String... emails) {

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
                message.setSubject(testContext.getCurrentXmlTest().getName() + " TEST RESULTS");
                String suiteName = testContext.getCurrentXmlTest().getSuite().getName();
                String testName = testContext.getName();
                Multipart multipart = new MimeMultipart();
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText("Results of running test "+ suiteName+"\n");
                MimeBodyPart attachmentBodyPart= new MimeBodyPart();
                Thread.sleep(3000);
                DataSource source = new FileDataSource("test-output\\" + suiteName + "\\" + testName + ".html");
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(suiteName + " report.html");
                multipart.addBodyPart(attachmentBodyPart);
                multipart.addBodyPart(textBodyPart);
                message.setContent(multipart);
//                message.setText(testContext.getCurrentXmlTest().getName() + " has failed with following methods been executed" +
//                        "\n\n" +
//                        "\n\n PASSED TESTS:" +
//                        "\n" + testsParse(testContext.getPassedTests().getAllMethods().toString()) +
//                        "\n\n FAILED TESTS: " + "\n" + testsParse(testContext.getFailedTests().getAllMethods().toString()));
//

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
            while(scanner.hasNext()){
                sb.append(scanner.next());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }


}
