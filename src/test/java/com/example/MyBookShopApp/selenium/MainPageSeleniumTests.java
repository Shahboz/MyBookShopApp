package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource("/application-test.properties")
class MainPageSeleniumTests {

    private static ChromeDriver driver;

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "D:\\Programs\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    void testMainPageAccess() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callMainPage()
                .pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    void testMainPageSearchByQuery() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callMainPage()
                .pause()
                .setFullScreen()
                .setUpSearchToken("As")
                .submit()
                .pause();

        assertTrue(driver.getPageSource().contains("Angela's Ashes"));
    }

    @Test
    void checkChaptersNavigate() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        List<String> urlChapters = mainPage
                .callMainPage()
                .getUrlChapters();

        for (String urlChapter : urlChapters) {
            mainPage
                    .callPage(urlChapter)
                    .pause()
                    .callInternPage()
                    .pause();
        }

        assertThat(urlChapters).hasSize(5);
    }

}