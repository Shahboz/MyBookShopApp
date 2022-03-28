package com.example.MyBookShopApp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;


@Aspect
@Component
public class EntitiesLogs {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "execution(public void com.example.MyBookShopApp.service.*Service.save(..))")
    public void saveObjectsPointcut() {}

    @Before("saveObjectsPointcut()")
    public void beforeSavingObject(JoinPoint object) {
        logger.info("Saving object of class " + object.getArgs()[0].getClass().getSimpleName() + "...");
    }

    @After("saveObjectsPointcut()")
    public void saveObjectsPointcutAdvice(JoinPoint object) {
        logger.info("Object of class " + object.getArgs()[0].getClass().getSimpleName() + " is saved.");
    }

}