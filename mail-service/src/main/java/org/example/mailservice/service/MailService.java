package org.example.mailservice.service;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.mailservice.dto.Mail;
import org.example.mailservice.repository.MailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@RequiredArgsConstructor
@Service
public class MailService {
    private static final Logger logger = LogManager.getLogger(MailService.class);

    private final MailRepository mailRepository;
    private final Session session;

    @Value("${email.password}")
    private String password;

    @Value("${email.username}")
    private String username;

    public MailService(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
        this.session = createMailSession();
    }

    private Session createMailSession() {
        return Session.getInstance(
                getMailProperties(),
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        )
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mail.ru");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        return properties;
    }

    public boolean isEmailUnique(@Email final String email) {
        return !mailRepository.existsByEmail(email);
    }

    public void sendMessageInMail(@Email final String email, final Long id) throws MessagingException {
        if (email == null) return;
        sendEmailSafely(email, id);
    }

    private void sendEmailSafely(final String email, final Long id) throws MessagingException {
        try {
            sendEmail(email, id);
            handleEmailRegistration(email);
        } catch (MessagingException e) {
            logger.error("Ошибка отправки на {}", email, e);
            throw e;
        }
    }

    private void sendEmail(final String email, final Long id) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject("Уведомление о заказе");
        message.setText("Заказ " + id + " был успешно взят");

        Transport.send(message);
        logger.info("Сообщение отправлено на {}", email);
    }

    private void handleEmailRegistration(String email) {
        if (mailRepository.existsByEmail(email)) {
            logger.info("Почта {} уже в базе", email);
        } else {
            mailRepository.save(new Mail(email));
            logger.info("Почта {} сохранена в базу", email);
        }
    }
}
