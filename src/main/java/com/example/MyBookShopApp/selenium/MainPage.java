package com.example.MyBookShopApp.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.ArrayList;
import java.util.List;


public class MainPage {

    private String url = "http://localhost:8080/";
    private ChromeDriver driver;

    public MainPage(ChromeDriver driver) {
        this.driver = driver;
    }

    public MainPage callMainPage() {
        driver.get(url);
        return this;
    }

    public MainPage callPage(String url) {
        driver.get(url);
        return this;
    }

    public MainPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public MainPage setUpSearchToken(String token) {
        WebElement element = driver.findElement(By.id("query"));
        element.sendKeys(token);
        return this;
    }

    public MainPage submit() {
        WebElement element = driver.findElement(By.id("search"));
        element.submit();
        return this;
    }

    public List<String> getUrlChapters() {
        List<String> urlChapters = new ArrayList<>();
        WebElement menuItems = driver.findElement(By.id("navigate"));
        for (WebElement element : menuItems.findElements(By.tagName("li"))) {
            urlChapters.add(element.findElement(By.linkText(element.getText())).getAttribute("href"));
        }
        return urlChapters;
    }

    public MainPage callInternPage() {
        List<WebElement> allLinks = driver.findElements(By.cssSelector("div.Section a"));
        for (WebElement element : allLinks) {
            String firstValidUrl = element.getAttribute("href");
            if(firstValidUrl.indexOf("#") < 0) {
                System.out.println("Calling page " + firstValidUrl);
                this.callPage(firstValidUrl);
                return this;
            }
        }
        return this;
    }
}