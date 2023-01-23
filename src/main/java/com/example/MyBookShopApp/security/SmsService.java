package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.UserContact;
import com.example.MyBookShopApp.service.UserContactService;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;


@Service
public class SmsService {

    @Value("${twilio.ACCOUNT_SID}")
    private String accountSid;

    @Value("${twilio.AUTH_TOKEN}")
    private String authToken;

    @Value("${twilio.TWILIO_NUMBER}")
    private String twilioNumber;

    @Value("${appEmail.email}")
    private String emailFrom;

    private final UserContactService userContactService;
    private final JavaMailSender javaMailSender;

    @Autowired
    public SmsService(UserContactService userContactService, JavaMailSender javaMailSender) {
        this.userContactService = userContactService;
        this.javaMailSender = javaMailSender;
    }

    private String generateCode() throws NoSuchAlgorithmException {
        Random random = SecureRandom.getInstanceStrong();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(random.nextInt(9));
        }
        sb.insert(3, " ");
        return sb.toString();
    }

    private void sendCode(UserContact userContact) {

        if (userContact.getType().equals("EMAIL")) {
            // Send Email message
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(userContact.getContact());
            mailMessage.setSubject("Bookstore email verification!");
            mailMessage.setText("Verification code is: " + userContact.getCode());
            javaMailSender.send(mailMessage);
        } else {
            // Send SMS code
            Twilio.init(accountSid, authToken);
        }
        userContactService.save(userContact);
    }

    public ResultResponse sendSecretCode(ContactConfirmationPayload contactConfirmation) throws NoSuchAlgorithmException {
        ResultResponse response = new ResultResponse();
        UserContact userContact = userContactService.getUserContactByContact(contactConfirmation.getContact());
        if (userContact != null) {
            long exceedMin = 0;
            // Смотрим на количество попыток ввода
            if (!userContactService.isExhausted(userContact)) {
                // Если не превышает количество попыток
                if (userContactService.isExpired(userContact)) {
                    // Если истек срок действия кода, генерируем и отправляем новый код
                    userContact.setCode(generateCode());
                    userContact.setCodeTrials(0);
                    userContact.setCodeTime(new Date());
                    sendCode(userContact);

                    response.setResult(true);
                } else {
                    if (userContactService.isTimeoutExceed(userContact)) {
                        // Если прошел таймаут, отправим текущий код
                        sendCode(userContact);

                        response.setResult(true);
                    } else {
                        // Если не прошел таймаут, то вычисляем время ожидания для повторной отправки
                        exceedMin = userContactService.getExceedMinuteRetryTimeout(userContact);

                        response.setResult(false);
                        response.setError("Повторно запросить код можно через " + exceedMin + " минут");
                    }
                }
            } else {
                // Если превышает количество попыток
                if (userContactService.isTimeoutExceed(userContact)) {
                    // Если прошло таймаут повторной отправки, генерируем и отправляем новый код
                    userContact.setCode(generateCode());
                    userContact.setCodeTrials(0);
                    userContact.setCodeTime(new Date());
                    sendCode(userContact);

                    response.setResult(true);
                } else {
                    // Если не прошло таймаут отправки, то вычисляем время ожидания для повторной отправки
                    exceedMin = userContactService.getExceedMinuteRetryTimeout(userContact);
                    response.setResult(false);
                    response.setError("Исчерпано количество попыток ввода кода подтверждения. Запросите новый код через " + exceedMin + " минут");
                }
            }
        } else {
            // Пользователь не найден
            response.setResult(false);
            response.setError("Контакт не найден");
        }
        return response;
    }

}