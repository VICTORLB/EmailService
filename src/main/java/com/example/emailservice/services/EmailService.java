package com.example.emailservice.services;

import com.example.emailservice.enums.StatusEmail;
import com.example.emailservice.models.EmailModel;
import com.example.emailservice.repositories.EmailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private EmailRepository emailRepository;
    private JavaMailSender mailSender;

    public EmailService(EmailRepository emailRepository, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Value(value = "${spring.mail.username}")
    private String emailFrom;


    @Transactional
    public EmailModel sendEmail(EmailModel emailModel){
        try {
            emailModel.setSendDataEmail(LocalDateTime.now());
            emailModel.setEmailFrom(emailFrom);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            mailSender.send(message);

            emailModel.setStatusEmail(StatusEmail.SENT);

            System.out.println("Saving new user: " + emailModel.getEmailTo() + ", with text: " + emailModel.getText());

            return emailRepository.save(emailModel);
        } catch ( MailException e ) {
            System.out.println("Email not sent to user: " + emailModel.getEmailTo() + ", with text: " + emailModel.getText());
            emailModel.setStatusEmail(StatusEmail.NOT_SENT);
        }
        return null;
    }
}
