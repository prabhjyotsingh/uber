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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.FileUtils;
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

public class InstallationUtils {

  public final static Logger LOG = LoggerFactory.getLogger(InstallationUtils.class);

  public static void sleep(long millis, boolean logOutput) {
    if (logOutput) {
      LOG.info("Starting sleeping for " + (millis / 1000) + " seconds...");
      LOG.info("Caller: " + Thread.currentThread().getStackTrace()[2]);
    }
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      LOG.error("Exception in WebDriverManager while getWebDriver ", e);
    }
    if (logOutput) {
      LOG.info("Finished.");
    }
  }

  public static String getPrivateKey(String keyFile) {
    try {
      String content = new String(Files.readAllBytes(Paths.get(keyFile)));
      return  content;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static void executeCommand(String command) {
    CommandLine cmdLine = CommandLine.parse("bash -c");
    cmdLine.addArgument(command, false);
    DefaultExecutor executor = new DefaultExecutor();
    executor.setWatchdog(new ExecuteWatchdog(-1));

    try {
      int exitVal = executor.execute(cmdLine);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static WebElement pollingWait(final By locator, final long timeWait, WebDriver driver) {
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

  public static void takeScreenshot(String urlId, String date, WebDriver driver) {
    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(screenshot,
          new File(WebDriverManager.downLoadsDir + date + "--" + urlId.split("/")[4] + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Boolean xpathExists(WebElement webElement, String xpath, WebDriver driver) {
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
    boolean exists = webElement.findElements(By.xpath(xpath)).size() != 0;
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    return exists;
  }
}
