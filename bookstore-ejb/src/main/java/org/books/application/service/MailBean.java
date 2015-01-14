package org.books.application.service;

import static java.lang.ProcessBuilder.Redirect.to;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.books.persistence.entity.Order;

/**
 *
 * @author AWy
 */
@Stateless
public class MailBean {
    @Resource(name="mail/bookstore") private Session mailSession;

    public void sendMailOrderChanged(final Order order) {
	try {
	    sendMail(order.getCustomer().getEmail(), "Order Status changed",
		    "Your order status changed to " + order.getStatus() + " for order " + order.getNumber());
	} catch (MessagingException ex) {
	    Logger.getLogger(OrderServiceBean.class.getName()).log(Level.WARNING, "Could not send email", ex);
	}
    }
    public void sendMail(String to, String subject, String text) throws MessagingException {
	// Create the message object
     MimeMessage message = new MimeMessage(mailSession);

     // Adjust the recipients. Here we have only one
     // recipient. The recipient's address must be
     // an object of the InternetAddress class.
     message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));

     // Set the message's subject
     message.setSubject(subject);

     // Insert the message's body
     message.setText(text);

     // This is not mandatory, however, it is a good
     // practice to indicate the software which
     // constructed the message.
     message.setHeader("X-Mailer", "Bookstore Mailer");

     // Adjust the date of sending the message
     Date timeStamp = new Date();
     message.setSentDate(timeStamp);

     // Use the 'send' static method of the Transport
     // class to send the message
     Transport.send(message);
    }
}
