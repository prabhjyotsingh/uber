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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created for org.prabhu.ambari on 03/08/16.
 */
public class UberBillsExtract extends AbstractIT {

  private static final Logger LOG = LoggerFactory.getLogger(UberBillsExtract.class);

  long waitTime = 2000;
  protected static final long MAX_PARAGRAPH_TIMEOUT_SEC = 60;
  private Map<String, String> monthMap = new HashMap<>();

  @Before
  public void startUp() {
    driver = WebDriverManager.getWebDriver("https://riders.uber.com/trips", null);
    monthMap.put("01", "January");
    monthMap.put("02", "February");
    monthMap.put("03", "March");
    monthMap.put("04", "April");
    monthMap.put("05", "May");
    monthMap.put("06", "June");
    monthMap.put("07", "July");
    monthMap.put("08", "August");
    monthMap.put("09", "September");
    monthMap.put("10", "October");
    monthMap.put("11", "November");
    monthMap.put("12", "December");
  }

  @After
  public void shutDown() {
    driver.close();
  }


  @Test
  public void testDownUberBill() {
    try {

      List<String> monthArray = new ArrayList<String>() {{
        add(monthMap.get("12"));
        add(monthMap.get("11"));
        add(monthMap.get("10"));
        add(monthMap.get("06"));
        add(monthMap.get("07"));
        add(monthMap.get("08"));
        add(monthMap.get("09"));
      }};
      sleep(waitTime, false);

      driver.findElement(By.xpath(".//*[@name='textInputValue']"))
          .sendKeys("uname");
      driver.findElement(By.xpath(".//*[@id='app-body']/div/div[1]/form/button")).click();
      driver.findElement(By.xpath(".//*[@id='password']")).sendKeys("password");
      driver.findElement(By.xpath(".//*[@id='app-body']/div/div[1]/form/button")).click();

      sleep(waitTime, false);

      List<String> tripIds = new ArrayList<String>();

      List<WebElement> tripElements = driver
          .findElements(By.xpath(".//body/div/div/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div"));
      tripIds.addAll(getTripIdForThisPage(tripElements, monthArray));

      WebDriverManager.downLoadsDir = WebDriverManager.downLoadsDir + "uber/";

      StringBuilder csvFile = new StringBuilder();
      StringBuilder errorUrl = new StringBuilder();
      Integer nos = 1;
      for (String urlId : tripIds) {
        try {
          driver.get(urlId);
          if (!pollingWait(By.xpath(".//body/div/div/div[2]/div/div/div/div[2]/div"),
              MAX_PARAGRAPH_TIMEOUT_SEC).getText().equalsIgnoreCase("cancelled")) {
            sleep(waitTime, false);
            csvFile.append(nos).append(",").append(urlId).append(",");
            String date = pollingWait(
                By.xpath(".//body/div/div/div[2]/div/div/div/div/span"),
                MAX_PARAGRAPH_TIMEOUT_SEC).getText()
                .split(",")[0];
            csvFile.append("\"").append(date).append("\",").append("Uber,");

            driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
            List<WebElement> subTotalDivs = driver
                .findElements(By.xpath(".//div[contains(.,'Subtotal')]"));
            String amount = subTotalDivs.get(subTotalDivs.size() - 2).getText().split("₹")[1];
            driver.switchTo().defaultContent();

            csvFile.append(amount).append(",");
            InstallationUtils.takeScreenshot(urlId, date, driver);
            nos++;
          }
        } catch (Exception e) {
          errorUrl.append("failed to process this URL" + urlId).append("\n");
        }
        csvFile.append("\n");
      }

      System.out.println(errorUrl);
      System.out.println(csvFile);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  List<String> getTripIdForThisPage(List<WebElement> tripElements, List<String> monthArray) {
    List<String> tripIds = new ArrayList<String>();
    for (WebElement webElement : tripElements) {
      if (webElement.findElement(By.xpath("div/div[2]/div[2]")).getText().contains("₹") &&
          monthArray.contains(
              webElement.findElement(By.xpath("div/div[2]/div[1]")).getText().split(" ")[1])
      ) {
        if (!InstallationUtils.xpathExists(webElement, "div[2]/div[2]/div[3]/a", driver)) {
          webElement.findElement(By.xpath("div[1]/div[1]")).click();
        }
        tripIds
            .add(webElement.findElement(By.xpath("div[2]/div[2]/div[3]/a")).getAttribute("href"));
      }
    }

    WebElement lastElement = tripElements.get(tripElements.size() - 1);
    if (monthArray
        .contains(lastElement.findElement(By.xpath("div/div[2]/div[1]")).getText().split(" ")[1])) {
      driver.findElement(By.xpath(".//body/div/div/div[2]/div[1]/div[1]/div[1]/div[1]/div[3]/div"
          + "[2]")).click();
      sleep(waitTime, false);
      tripElements = driver
          .findElements(By.xpath(".//body/div/div/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div"));
      tripIds.addAll(getTripIdForThisPage(tripElements, monthArray));
    }

    return tripIds;
  }

}
