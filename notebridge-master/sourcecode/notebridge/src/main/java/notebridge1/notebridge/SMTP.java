package notebridge1.notebridge;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public enum SMTP {
    INSTANCE;

    public void sendEmail(String subject, String body) {
        // SMTP server details
        String host = "smtp.elasticemail.com";
        int port = 587; // Replace with your SMTP server port
        String username = "d.chitoraga@student.utwente.nl";
        String password = "5C4D2726A8B3216CA4FAE538CD7914C2BDA8";

        // Sender and recipient email addresses
        String senderEmail = "d.chitoraga@student.utwente.nl";
        String recipientEmail = "notebridge.g1@gmail.com";

        try {
            // Set properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create a session with authentication
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public void sendEmail(String subject, String body, String email) {
        // SMTP server details
        String host = "smtp.elasticemail.com";
        int port = 587; // Replace with your SMTP server port
        String username = "d.chitoraga@student.utwente.nl";
        String password = "5C4D2726A8B3216CA4FAE538CD7914C2BDA8";

        // Sender and recipient email addresses
        String senderEmail = "d.chitoraga@student.utwente.nl";
        String recipientEmail = email;

        try {
            // Set properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            // Create a session with authentication
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body+"\n\n\n\n\n\nIf you don't want to receive any emails from us, click the link below:");

            // Send the message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
