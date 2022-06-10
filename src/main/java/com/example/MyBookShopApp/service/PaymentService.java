package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.PaymentDto;
import com.example.MyBookShopApp.dto.ResultResponse;
import com.example.MyBookShopApp.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;


@Service
public class PaymentService {

    private final UserService userService;
    private final UserBooksService userBooksService;
    private final TransactionService transactionService;

    @Autowired
    public PaymentService(UserService userService, UserBooksService userBooksService, TransactionService transactionService) {
        this.userService = userService;
        this.userBooksService = userBooksService;
        this.transactionService = transactionService;
    }

    @Value("${robokassa.merchant.login}")
    private String metchantLogin;

    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;

    public String getPaymentUrl(List<Book> booksFromCookiesSlug) throws NoSuchAlgorithmException {
        int paymentSumTotal = booksFromCookiesSlug.stream().mapToInt(Book::getDiscountPrice).sum();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String invId = "5"; // just for testing
        messageDigest.update((metchantLogin + ":" + paymentSumTotal + ":" + invId + ":" + firstTestPass).getBytes());

        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + metchantLogin +
                "&InvId=" + invId +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + paymentSumTotal +
                "%SignatureValue=" + DatatypeConverter.printHexBinary(messageDigest.digest()).toUpperCase() +
                "&IsTest=1";
    }

    public ResultResponse processPayUserBooks(User user, List<Book> bookList) {
        ResultResponse result = new ResultResponse();
        if (bookList != null && bookList.size() > 0) {
            Integer paymentSumTotal = bookList.stream().mapToInt(Book::getDiscountPrice).sum();
            if (user.getBalance() >= paymentSumTotal) {
                // Списание средств
                user.setBalance(user.getBalance() - paymentSumTotal);
                userService.save(user);

                for (Book book : bookList) {
                    // Сохранение транзакции
                    Transaction transaction = new Transaction();
                    transaction.setUser(user);
                    transaction.setBook(book);
                    transaction.setTime(new Date());
                    transaction.setValue(-book.getDiscountPrice());
                    transaction.setDescription("Покупка книги '" + book.getTitle() + "'");

                    // Сохранение книги пользователя
                    result = userBooksService.changeBookStatus("PAID", book);
                    if (result.getResult()) {
                        transactionService.save(transaction);
                    } else {
                        break;
                    }
                }
            } else {
                result.setResult(false);
                result.setError("Не хватает средств на счету. Пополните счет на сумму " + (int) (paymentSumTotal - user.getBalance()) + " рублей!");
            }
        } else {
            result.setResult(false);
            result.setError("Корзина пуста!");
        }
        return result;
    }

    public ResultResponse processPayment(PaymentDto payment) {
        ResultResponse resultResponse = new ResultResponse();
        if (payment == null || payment.getSum() == null || payment.getSum() <= 0 || StringUtils.isEmpty(payment.getHash())) {
            resultResponse.setResult(false);
            resultResponse.setError("Некорректные параметры!");
        } else {
            User user = userService.getUserByHash(payment.getHash());
            if (user != null) {
                Transaction transaction = new Transaction();
                transaction.setUser(user);
                transaction.setTime(new Date(payment.getTime()));
                transaction.setValue(payment.getSum());
                transaction.setDescription("Пополнение через личный кабинет");
                transactionService.save(transaction);

                user.setBalance(user.getBalance() + payment.getSum());
                userService.save(user);

                resultResponse.setResult(true);
            } else {
                resultResponse.setResult(false);
                resultResponse.setError("Пользователь не найден");
            }
        }
        return resultResponse;
    }
}
