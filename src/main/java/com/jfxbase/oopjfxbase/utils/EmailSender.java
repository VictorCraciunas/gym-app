package com.jfxbase.oopjfxbase.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

// To send an email I use javamail library which i introduced in pom.xml. We also need a SMTP server
public class EmailSender {

    public static void sendEmail( String givenEmail, String passwordFromDataBase) {
        String senderEmail = "victor.craciunas02@gmail.com"; // gmail mandatory
        String senderPassword = "axgn uqfn wfdo nxxh"; // password created with apps password from google (two factor authentication required)

        // We connect to the SMTP server. I use SMTP GMAIL-third party server with TLS
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        };
        Session session = Session.getInstance(properties, auth);

        try {
            //creating the email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(givenEmail));
            message.setSubject("Hello from JavaMail");
            message.setText("parola ta este " + passwordFromDataBase);
            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}

