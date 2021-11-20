import Model.Item;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String baseUrl ="https://www.tokopedia.com/p/handphone-tablet/handphone";
    private static final String csvPath ="D:\\output.csv";
    private static  final String webDriverPath ="C:\\WebDriver\\gecko\\bin\\geckodriver.exe";
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
        System.setProperty("webdriver.gecko.driver", webDriverPath);
        WebDriver driver = new FirefoxDriver(firefoxOptions);

        //load web page
        int numPage = 2;
        List<Item> itemList = new ArrayList<Item>();
        for(int i=1;i<=numPage;i++){
            itemList.addAll(crawl(driver, i));
        }
        driver.close();

        //to CSV
        Path paths = Paths.get(csvPath);
        try {
            writeCsvFromBean(paths,itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //to JSON
//        ObjectMapper mapper = new ObjectMapper();
////		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        String jsonResult = null;
//        try {
//            jsonResult = mapper.writeValueAsString(itemList);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        System.out.println(jsonResult);

    }

    public static List<Item> crawl(WebDriver driver,int page){
        //fetch web page
        driver.get(baseUrl+"?page="+page);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        //scroll browser to trigger onload data
        js.executeScript("window.scrollBy(0,1030)");

        //wait until all item loaded this page
        List<WebElement> result =null;
        try {
            result= new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='css-16vw0vn' and div[@class='css-11s9vse' and not(div[@class='css-nysll7'])]]"),10));
        }catch (TimeoutException e){
            e.printStackTrace();
        }

        //Mapping data item
        System.out.println(result.size());
        int idx =0;
        List<Item> itemList = new ArrayList<Item>();
        for(WebElement webElement:result){
            Item item = new Item();
            System.out.println(++idx);

            //wait until item picture fully loaded
            try {
                Boolean imageResult = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.attributeContains(webElement.findElement(By.tagName("img")),"class","success"));
            }catch (TimeoutException e){
                e.printStackTrace();
            }

            //System.out.println(webElement.getText());
            item.setItemName(webElement.findElement(By.cssSelector(".css-1bjwylw")).getText());
            item.setPrice(webElement.findElement(By.cssSelector(".css-o5uqvq")).getText());
            String shopDetail[] = webElement.findElement(By.cssSelector(".css-vbihp9")).getAttribute("innerText").split("\\r?\\n");
            item.setSellerLocation(shopDetail[0]);
            item.setSellerName(shopDetail[1]);
            WebElement ratingElement =webElement.findElement(By.className("css-11s9vse")).findElement(By.className("css-153qjw7")).findElement(By.tagName("div"));
            item.setRating(ratingElement.findElements(By.cssSelector("img[src='https://assets.tokopedia.net/assets-tokopedia-lite/v2/zeus/kratos/4fede911.svg']")).size()+"");
            item.setTotalRating(ratingElement.findElement(By.tagName("span")).getText().replace("(","").replace(")",""));
            //System.out.println(item.getRating());
            item.setImageUrl(webElement.findElement(By.tagName("img")).getAttribute("src"));
            itemList.add(item);


            //scroll every 2 row of item
            if(idx%10==0){
                js.executeScript("window.scrollBy(0,675)");
            }

        }
        return itemList;
    }

    public static void writeCsvFromBean(Path path,List<Item> itemList) throws Exception {
        Writer writer  = new FileWriter(path.toString());

        StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();


        sbc.write(itemList);
        writer.close();
    }
}
