import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Main {
    private static final String baseUrl ="https://www.tokopedia.com/p/handphone-tablet/handphone";
    private static final String csvPath ="C:output.csv";
    public static void main(String[] args) {
        //Chrome Setup
//        System.setProperty("webdriver.chrome.driver", "C:\\WebDriver\\bin\\chromedriver.exe");
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--window-size=1920,1080");
//        chromeOptions.addArguments("--disable-extensions");
//        chromeOptions.addArguments("--proxy-server='direct://'");
//        chromeOptions.addArguments("--proxy-bypass-list=*");
//        chromeOptions.addArguments("--start-maximized");
//        chromeOptions.addArguments("--headless");
//        chromeOptions.addArguments("--disable-gpu");
//        chromeOptions.addArguments("--disable-dev-shm-usage");
//        chromeOptions.addArguments("--no-sandbox");
//        chromeOptions.addArguments("--ignore-certificate-errors");
//        chromeOptions.addArguments("headless");
//        chromeOptions.addArguments("window-size=1920,1080");
//        WebDriver driver = new ChromeDriver(chromeOptions);

        //Firefox headless setup
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        //firefoxOptions.setHeadless(true);
        //Set Path of Firefox driver
        System.setProperty("webdriver.gecko.driver", "C:\\WebDriver\\gecko\\bin\\geckodriver.exe");
        WebDriver driver = new FirefoxDriver(firefoxOptions);
        //load web page
        driver.get(baseUrl);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        //scroll browser to trigger onload data
        js.executeScript("window.scrollBy(0,1000)");

        //wait until all item loaded this page
        List<WebElement> result = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[@class='css-16vw0vn' and div[@class='css-11s9vse' and not(div[@class='css-nysll7'])]]"),60));

//        List<WebElement> listBarang =driver.findElements(By.xpath("//a[@class='css-89jnbj' and not(div[@class='css-nysll7'])]"));
//        List<WebElement> listBarang =driver.findElements(By.xpath("//div[@class='css-16vw0vn' and div[@class='css-11s9vse' and not(div[@class='css-nysll7'])]]"));\

        //Mapping data item
        System.out.println(result.size());
        int idx =0;
        for(WebElement webElement:result){
            System.out.print(++idx);

            //wait until item picture fully loaded
            Boolean imageResult = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.attributeContains(webElement.findElement(By.tagName("img")),"class","success"));
            String imageUrl =webElement.findElement(By.tagName("img")).getAttribute("src");
            System.out.println(imageUrl);

            //scroll every 2 row of item
            if(idx%10==0){
                js.executeScript("window.scrollBy(0,1000)");
            }

        }

        driver.findElement(By.xpath("//button[@data-unf='pagination-item' and text()='2']")).click();
        js.executeScript("window.scrollBy(0,-1000)");

//        System.out.println(driver.findElements(By.xpath("//div[@class='css-16vw0vn' and div[@class='css-11s9vse' and not(div[@class='css-nysll7'])]]")).size());

        List<WebElement> result2 = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[@class='css-16vw0vn' and div[@class='css-11s9vse' and not(div[@class='css-nysll7'])]]"),65));
        int idx2 =0;
        for(WebElement webElement:result2){
            System.out.print(++idx2);
//
            //wait until item picture fully loaded
            Boolean imageResult = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.attributeContains(webElement.findElement(By.tagName("img")),"class","success"));
            String imageUrl =webElement.findElement(By.tagName("img")).getAttribute("src");
            System.out.println(imageUrl);

//            //scroll every 2 row of item
            if(idx2%10==0){
                js.executeScript("window.scrollBy(0,-1000)");
            }
//
        }

        driver.close();


    }
}
