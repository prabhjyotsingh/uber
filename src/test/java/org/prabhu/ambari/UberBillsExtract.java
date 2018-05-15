/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.prabhu.ambari;

import com.google.common.base.Function;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created for org.prabhu.ambari on 03/08/16.
 */
public class UberBillsExtract extends AbstractIT {

  private static final Logger LOG = LoggerFactory.getLogger(UberBillsExtract.class);

  long waitTime = 2000;
  protected static final long MAX_PARAGRAPH_TIMEOUT_SEC = 60;

  @Before
  public void startUp() {
    driver = WebDriverManager.getWebDriver("https://riders.uber.com/trips", null);
  }

  @After
  public void shutDown() {
    driver.close();
  }


  @Test
  public void testDownUberBill() {
    try {

      List<String> monthArray = new ArrayList<String>() {{
        add("02");
        add("03");
      }};
      sleep(waitTime, false);

      driver.findElement(By.xpath(".//*[@name='textInputValue']"))
          .sendKeys("username");
      driver.findElement(By.xpath(".//*[@id='app-body']/div/div[1]/form/button")).click();
      driver.findElement(By.xpath(".//*[@id='password']")).sendKeys("password");
      driver.findElement(By.xpath(".//*[@id='app-body']/div/div[1]/form/button")).click();

      sleep(waitTime, false);

      List<String> tripIds = new ArrayList<String>();

      List<WebElement> tripElements = driver.findElements(
          By.xpath(".//*[@id='trips-table']/tbody/tr[contains(@class,\"trip-expand__origin\")]"));
      tripIds.addAll(getTripIdForThisPage(tripElements, monthArray));

      WebDriverManager.downLoadsDir = WebDriverManager.downLoadsDir + "uber/";

      StringBuilder csvFile = new StringBuilder();
      Integer nos = 1;
      for (String urlId : tripIds) {
        String tripUrl = "https://riders.uber.com/trips/";
        try {
          driver.get(tripUrl + urlId);
          csvFile.append(nos).append(",").append(urlId).append(",");

          String date = pollingWait(
              By.xpath(".//*[@id='slide-menu-content']//div[@class='page-lead']/div"),
              MAX_PARAGRAPH_TIMEOUT_SEC).getText()
              .split("on")[1];
          csvFile.append("\"").append(date).append("\",").append("Uber,");

          driver.switchTo().frame(driver.findElement(By.id("receipt-frame")));
          String amount = pollingWait(
              By.xpath(".//*[@id='app-content']/div/div/div/div/div[contains(.,'Subtotal')]"),
              MAX_PARAGRAPH_TIMEOUT_SEC)
              .getText().split("₹")[1];
          driver.switchTo().defaultContent();

          csvFile.append(amount).append(",");
          takeScreenshot(urlId, date);
          nos++;
        } catch (Exception e) {
          System.out.println("failed to process this URL" + tripUrl + urlId);
        }
        csvFile.append("\n");
      }

      System.out.println(csvFile);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected WebElement pollingWait(final By locator, final long timeWait) {
    Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
        .withTimeout(timeWait, TimeUnit.SECONDS)
        .pollingEvery(1, TimeUnit.SECONDS)
        .ignoring(NoSuchElementException.class);

    return wait.until(new Function<WebDriver, WebElement>() {
      public WebElement apply(WebDriver driver) {
        return driver.findElement(locator);
      }
    });
  }

  private void takeScreenshot(String urlId, String date) {
    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(screenshot,
          new File(WebDriverManager.downLoadsDir + date + "--" + urlId + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  List<String> getTripIdForThisPage(List<WebElement> tripElements, List<String> monthArray) {
    List<String> tripIds = new ArrayList<String>();
    for (WebElement webElement : tripElements) {
      if (webElement.findElement(By.className("text--right")).getText().contains("₹") &&
          monthArray.contains(webElement.findElement(By.xpath(".//td[2]")).getText().split("/")[0])
          ) {
        tripIds.add(webElement.getAttribute("data-target").split("trip-")[1]);
//        webElement.click();
      }
    }

    WebElement lastElement = tripElements.get(tripElements.size() - 1);
    if (monthArray
        .contains(lastElement.findElement(By.xpath(".//td[2]")).getText().split("/")[0])) {
      driver.findElement(By.xpath(".//*[@id='trips-pagination']/div[2]/a")).click();
      sleep(waitTime, false);
      tripElements = driver.findElements(
          By.xpath(".//*[@id='trips-table']/tbody/tr[contains(@class,\"trip-expand__origin\")]"));
      tripIds.addAll(getTripIdForThisPage(tripElements, monthArray));
    }

    return tripIds;
  }

}
