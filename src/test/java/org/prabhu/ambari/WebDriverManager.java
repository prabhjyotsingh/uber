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

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class WebDriverManager {

  public final static Logger LOG = LoggerFactory.getLogger(WebDriverManager.class);

  public static String downLoadsDir = "";

  public static WebDriver getWebDriver(String url) {
    WebDriver driver = null;

    if (driver == null) {
      try {
        FirefoxBinary ffox = new FirefoxBinary();
        ffox.setTimeout(120000);
        int firefoxVersion = WebDriverManager.getFirefoxVersion();
        LOG.info("Firefox version " + firefoxVersion + " detected");

        downLoadsDir = "/tmp/";

        String tempPath = downLoadsDir + "firebug/";

        downloadFireBug(firefoxVersion, tempPath);

        final String firebugPath = tempPath + "firebug.xpi";
        final String firepathPath = tempPath + "firepath.xpi";

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
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/x-ustar,application/octet-stream,application/zip,text/csv,text/plain");
        profile.setPreference("network.proxy.type", 0);

//        profile.addExtension(new File(firebugPath));
//        profile.addExtension(new File(firepathPath));

        driver = new FirefoxDriver(ffox, profile);
      } catch (Exception e) {
        LOG.error("Exception in WebDriverManager while FireFox Driver ", e);
      }
    }

    if (url.equals("")) {
      url = "http://c6401.ambari.apache.org:8080";
    }
    driver.get(url);
    driver.manage().window().setSize(new Dimension(1000, 1000));
    return driver;
  }

  private static void downloadFireBug(int firefoxVersion, String tempPath) {
    String firebugUrlString = null;
    if (firefoxVersion < 23)
      firebugUrlString = "http://getfirebug.com/releases/firebug/1.11/firebug-1.11.4.xpi";
    else if (firefoxVersion >= 23 && firefoxVersion < 30)
      firebugUrlString = "http://getfirebug.com/releases/firebug/1.12/firebug-1.12.8.xpi";
    else if (firefoxVersion >= 30)
      firebugUrlString = "http://getfirebug.com/releases/firebug/2.0/firebug-2.0.7.xpi";


    LOG.info("firebug version: " + firefoxVersion + ", will be downloaded to " + tempPath);
    try {
      File firebugFile = new File(tempPath + "firebug.xpi");
      URL firebugUrl = new URL(firebugUrlString);
      if (!firebugFile.exists()) {
        FileUtils.copyURLToFile(firebugUrl, firebugFile);
      }


      File firepathFile = new File(tempPath + "firepath.xpi");
      URL firepathUrl = new URL("https://addons.cdn.mozilla.net/user-media/addons/11900/firepath-0.9.7.1-fx.xpi");
      if (!firepathFile.exists()) {
        FileUtils.copyURLToFile(firepathUrl, firepathFile);
      }

    } catch (IOException e) {
      LOG.error("Download of firebug version: " + firefoxVersion + ", falied in path " + tempPath);
    }
    LOG.info("Download of firebug version: " + firefoxVersion + ", successful");
  }

  public static int getFirefoxVersion() {
    try {
      String firefoxVersionCmd = "firefox -v";
      if (System.getProperty("os.name").startsWith("Mac OS")) {
        firefoxVersionCmd = "/Applications/Firefox.app/Contents/MacOS/" + firefoxVersionCmd;
      }
      String versionString = (String) CommandExecutor.executeCommandLocalHost(firefoxVersionCmd, false, ProcessData.Types_Of_Data.OUTPUT);
      return Integer.valueOf(versionString.replaceAll("Mozilla Firefox", "").trim().substring(0, 2));
    } catch (Exception e) {
      LOG.error("Exception in WebDriverManager while getWebDriver ", e);
      return -1;
    }
  }
}
