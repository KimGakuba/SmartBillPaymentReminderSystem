package smartbill.server.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    private static final String SENDER_EMAIL = "kimgakuba@gmail.com";

    // Use Gmail App Password, not normal Gmail password
    private static final String SENDER_PASSWORD = "xuvamkltefdbzhbd";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static void sendOTP(String recipientEmail, String username, String otp)
            throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipientEmail)
        );

        message.setSubject("SmartBill — Your OTP Verification Code");

        String body =
                "Dear " + username + ",\n\n" +
                "Your One-Time Password (OTP) is:\n\n" +
                "        " + otp + "\n\n" +
                "This OTP is valid for 5 minutes only.\n" +
                "Do not share it with anyone.\n\n" +
                "Smart Bill Payment Reminder System";

        message.setText(body);

        Transport.send(message);

        System.out.println("OTP email sent successfully to: " + recipientEmail);
    }
}