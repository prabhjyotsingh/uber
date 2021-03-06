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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriver.SystemProperty;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.safari.SafariDriver;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebDriverManager {

  public final static Logger LOG = LoggerFactory.getLogger(WebDriverManager.class);

  public static String downLoadsDir = "";

  private static String GECKODRIVER_VERSION = "0.21.0";

  public static WebDriver getWebDriver(String url, Cookie cookie) {
    WebDriver driver = null;

    if (driver == null) {
      try {
        FirefoxBinary ffox = new FirefoxBinary();
        if ("true".equals(System.getenv("TRAVIS"))) {
          ffox.setEnvironmentProperty("DISPLAY", ":99"); // xvfb is supposed to
          // run with DISPLAY 99
        }
        int firefoxVersion = WebDriverManager.getFirefoxVersion();
        LOG.info("Firefox version " + firefoxVersion + " detected");

//        downLoadsDir = FileUtils.getTempDirectory().toString();
        downLoadsDir = "/tmp/";

        String tempPath = downLoadsDir + "/firefox/";

        downloadGeekoDriver(firefoxVersion, tempPath);

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", downLoadsDir);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("app.update.auto", false);
        profile.setPreference("app.update.enabled", false);
        profile.setPreference("dom.max_script_run_time", 0);
        profile.setPreference("dom.max_chrome_script_run_time", 0);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
            "application/x-ustar,application/octet-stream,application/zip,text/csv,text/plain");
        profile.setPreference("network.proxy.type", 0);

        System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, tempPath + "geckodriver");
        System.setProperty(SystemProperty.DRIVER_USE_MARIONETTE, tempPath + "geckodriver");

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(ffox);
        firefoxOptions.setProfile(profile);
        firefoxOptions.setLogLevel(FirefoxDriverLogLevel.INFO);

        Map<String, String> environment = new HashMap<>();
        if ("true".equals(System.getenv("TRAVIS"))) {
          environment.put("DISPLAY", ":99");
        }

        GeckoDriverService gecko = new GeckoDriverService.Builder()
            .usingPort(0)
            .withEnvironment(environment)
            .build();
        gecko.start();

        driver = new FirefoxDriver(gecko);

      } catch (Exception e) {
        LOG.error("Exception in WebDriverManager while FireFox Driver ", e);
      }
    }

    if (driver == null) {
      try {
        driver = new ChromeDriver();
      } catch (Exception e) {
        LOG.error("Exception in WebDriverManager while ChromeDriver ", e);
      }
    }

    if (driver == null) {
      try {
        driver = new SafariDriver();
      } catch (Exception e) {
        LOG.error("Exception in WebDriverManager while SafariDriver ", e);
      }
    }

    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    driver.get(url);
    if (cookie != null) {
      driver.manage().addCookie(cookie);
      driver.navigate().refresh();
    }

    driver.manage().window().maximize();
    return driver;
  }

  public static void downloadGeekoDriver(int firefoxVersion, String tempPath) {
    String geekoDriverUrlString =
        "https://github.com/mozilla/geckodriver/releases/download/v" + GECKODRIVER_VERSION
            + "/geckodriver-v" + GECKODRIVER_VERSION + "-";

    LOG.info("Geeko version: " + firefoxVersion + ", will be downloaded to " + tempPath);
    try {
      if (SystemUtils.IS_OS_WINDOWS) {
        if (System.getProperty("sun.arch.data.model").equals("64")) {
          geekoDriverUrlString += "win64.zip";
        } else {
          geekoDriverUrlString += "win32.zip";
        }
      } else if (SystemUtils.IS_OS_LINUX) {
        if (System.getProperty("sun.arch.data.model").equals("64")) {
          geekoDriverUrlString += "linux64.tar.gz";
        } else {
          geekoDriverUrlString += "linux32.tar.gz";
        }
      } else if (SystemUtils.IS_OS_MAC_OSX) {
        geekoDriverUrlString += "macos.tar.gz";
      }

      File geekoDriver = new File(tempPath + "geckodriver");
      File geekoDriverZip = new File(tempPath + "geckodriver.tar");
      File geekoDriverDir = new File(tempPath);
      URL geekoDriverUrl = new URL(geekoDriverUrlString);
      if (!geekoDriver.exists()) {
        FileUtils.copyURLToFile(geekoDriverUrl, geekoDriverZip);
        if (SystemUtils.IS_OS_WINDOWS) {
          Archiver archiver = ArchiverFactory.createArchiver("zip");
          archiver.extract(geekoDriverZip, geekoDriverDir);
        } else {
          Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
          archiver.extract(geekoDriverZip, geekoDriverDir);
        }
      }

    } catch (IOException e) {
      LOG.error("Download of Geeko version: " + firefoxVersion + ", falied in path " + tempPath);
    }
    LOG.info("Download of Geeko version: " + firefoxVersion + ", successful");
  }

  public static int getFirefoxVersion() {
    try {
      String firefoxVersionCmd = "firefox -v";
      if (System.getProperty("os.name").startsWith("Mac OS")) {
        firefoxVersionCmd = "/Applications/Firefox.app/Contents/MacOS/" + firefoxVersionCmd;
      }
      String versionString = (String) CommandExecutor
          .executeCommandLocalHost(firefoxVersionCmd, false, ProcessData.Types_Of_Data.OUTPUT);
      return Integer
          .valueOf(versionString.replaceAll("Mozilla Firefox", "").trim().substring(0, 2));
    } catch (Exception e) {
      LOG.error("Exception in WebDriverManager while getWebDriver ", e);
      return -1;
    }
  }
}