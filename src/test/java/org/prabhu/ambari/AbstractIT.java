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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.Keys.ENTER;
import static org.openqa.selenium.Keys.SHIFT;

abstract public class AbstractIT {
  protected WebDriver driver;

  protected final static Logger LOG = LoggerFactory.getLogger(AbstractIT.class);
  protected static final long MAX_BROWSER_TIMEOUT_SEC = 30;
  protected static final long MAX_PARAGRAPH_TIMEOUT_SEC = 60;

  protected void sleep(long millis, boolean logOutput) {
    if (logOutput) {
      LOG.info("Starting sleeping for " + (millis / 1000) + " seconds...");
      LOG.info("Caller: " + Thread.currentThread().getStackTrace()[2]);
    }
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (logOutput) {
      LOG.info("Finished.");
    }
  }

  protected boolean waitForText(final String txt, final By locator) {
    try {
      WebElement element = pollingWait(locator, MAX_BROWSER_TIMEOUT_SEC);
      return txt.equals(element.getText());
    } catch (TimeoutException e) {
      return false;
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

  public enum HelperKeys implements CharSequence {
    OPEN_PARENTHESIS(Keys.chord(Keys.SHIFT, "9")),
    EXCLAMATION(Keys.chord(Keys.SHIFT, "1")),
    PERCENTAGE(Keys.chord(Keys.SHIFT, "5")),
    SHIFT_ENTER(Keys.chord(SHIFT, ENTER));

    private final CharSequence keyCode;

    HelperKeys(CharSequence keyCode) {
      this.keyCode = keyCode;
    }

    public char charAt(int index) {
      return index == 0 ? keyCode.charAt(index) : '\ue000';
    }

    public int length() {
      return 1;
    }

    public CharSequence subSequence(int start, int end) {
      if (start == 0 && end == 1) {
        return String.valueOf(this.keyCode);
      } else {
        throw new IndexOutOfBoundsException();
      }
    }

    public String toString() {
      return String.valueOf(this.keyCode);
    }

  }

  protected void handleException(String message, Exception e) throws Exception {
    LOG.error(message, e);
    File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    LOG.error("ScreenShot::\ndata:image/png;base64," + new String(Base64.encodeBase64(FileUtils.readFileToByteArray(scrFile))));
    throw e;
  }

}
