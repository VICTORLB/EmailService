package com.example.emailservice.consumer;

import com.example.emailservice.dtos.EmailRecordDto;
import com.example.emailservice.models.EmailModel;
import com.example.emailservice.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

@Component
public class EmailConsumer {

    private EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${broker.queue.email.name}", ackMode = "MANUAL")
    public void listenEmailQueue(@Payload EmailRecordDto emailDto, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception{

        try{
            var emailModel = new EmailModel();
            BeanUtils.copyProperties(emailDto, emailModel);
            System.out.println("Sending email to new user: [" + emailDto.emailTo() + "]");
            emailService.sendEmail(emailModel);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, true);
        }

    }

}
