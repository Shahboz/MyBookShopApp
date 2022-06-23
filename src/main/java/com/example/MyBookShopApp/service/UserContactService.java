package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.entity.UserContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class UserContactService {

    @Value("${sms.code.max.count}")
    private Integer codeMaxAttempt;

    @Value("${sms.code.validatiy}")
    private Integer codeValidatyTime;

    @Value("${sms.code.timeout}")
    private Integer codeTimeout;

    private final UserContactRepository userContactRepository;

    @Autowired
    public UserContactService(UserContactRepository userContactRepository) {
        this.userContactRepository = userContactRepository;
    }

    public void save(UserContact userContact) {
        this.userContactRepository.save(userContact);
    }

    public List<UserContact> getApprovedUserContacts(Integer userId) {
        return this.userContactRepository.findUserContactsByUserIdAndApproved(userId, 1);
    }

    public UserContact getUserContactByContact(String contact) {
        return this.userContactRepository.findUserContactByContact(contact);
    }

    public Boolean isExhausted(UserContact userContact) {
        return (userContact.getCodeTrials() >= codeMaxAttempt);
    }

    public Boolean isExpired(UserContact userContact) {
        Date currentDate = new Date();
        return currentDate.toInstant().isAfter(userContact.getCodeTime().toInstant().plus(Duration.ofMinutes(codeValidatyTime)));
    }

    public Boolean isTimeoutExceed(UserContact userContact) {
        Date currentDate = new Date();
        return currentDate.toInstant().isAfter(userContact.getCodeTime().toInstant().plus(Duration.ofMinutes(codeTimeout)));
    }

    public long getExceedMinuteRetryTimeout(UserContact userContact) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(userContact.getCodeTime());
        calendar.add(Calendar.MINUTE, codeTimeout);
        return Math.max(TimeUnit.MILLISECONDS.toMinutes(calendar.getTime().getTime() - new Date().getTime()), 0);
    }

    public Integer getExceedRetryCount(UserContact userContact) {
        return Math.max(codeMaxAttempt - userContact.getCodeTrials(), 0);
    }

    public void delete(UserContact contact) {
        userContactRepository.delete(contact);
    }

    public List<UserContact> getUserContactsByType(Integer userId, String contactType) {
        return userContactRepository.findUserContactsByUserIdAndType(userId, contactType);
    }

    public void deleteNotApproved(Integer userId) {
        userContactRepository.deleteUserContactsByUserIdAndApproved(userId, 0);
    }

}