package com.tander.camelFileProcessor.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Getter
@Setter
@AllArgsConstructor
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private JavaMailSenderImpl mailSender;

    public void send(String textLetter){
        logger.info("Send letter...");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailSender.getUsername());
        mailMessage.setTo(mailSender.getUsername());
        mailMessage.setSubject("info");
        mailMessage.setText(textLetter);
        try {
            mailSender.send(mailMessage);
            logger.info("Send letter success");
        }
        catch (MailException e){
            logger.error("Error sending email: ", e);
        }
    }

    public String createLetter(int current_ProcessedFile, int count_XMLFile, int count_TXTFile, int count_UNKNOWFile, long work_time){
        return "Обработано " + current_ProcessedFile + " сообщений."
                + "\n Из них " +
                + count_XMLFile + " формата XML, " +
                + count_TXTFile + " формата TXT, " +
                + count_UNKNOWFile + " неизвестного формата"
                + "\n Время обработки пачки сообщений - " + work_time + " с.";
    }
}