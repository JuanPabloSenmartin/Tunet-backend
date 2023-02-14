package tunet.Mail;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailManager {
    public static void sendMail(String to, String header, String text) {
        String from = "tunetenterprise@gmail.com";
        String password = "cgcsslbwzfsfluaz";

        try{
            Properties p = new Properties();
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.setProperty("mail.smtp.starttls.enable", "true");
            p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            p.setProperty("mail.smtp.port", "587");
            p.setProperty("mail.smtp.user",from);
            p.setProperty("mail.smtp.auth", "true");
            Session s = Session.getDefaultInstance(p);
            MimeMessage mensaje = new MimeMessage(s);
            mensaje.setFrom(new InternetAddress(from));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mensaje.setSubject(header);
            mensaje.setText(text);
            Transport t = s.getTransport("smtp");
            t.connect(from,password);
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
        } catch (MessagingException e) {

        }

    }
}
