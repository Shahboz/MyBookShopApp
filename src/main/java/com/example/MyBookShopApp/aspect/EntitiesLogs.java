package com.example.MyBookShopApp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import java.util.logging.Level;
import java.util.logging.Logger;


@Aspect
@Component
public class EntitiesLogs {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Pointcut(value = "execution(public void com.example.MyBookShopApp.service.*Service.save(..))")
    public void saveObjectsPointcut() {
        // Do nothing
    }

    @Before("saveObjectsPointcut()")
    public void beforeSavingObject(JoinPoint object) {
        logger.log(Level.INFO, "Saving object of class {0} ...", object.getArgs()[0].getClass().getSimpleName());
    }

    @After("saveObjectsPointcut()")
    public void saveObjectsPointcutAdvice(JoinPoint object) {
        logger.log(Level.FINE, "Object of class {0} is saved.", object.getArgs()[0].getClass().getSimpleName());
    }

}