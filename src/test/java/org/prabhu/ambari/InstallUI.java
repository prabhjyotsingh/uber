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

import static org.prabhu.ambari.InstallationUtils.executeCommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallUI extends AbstractIT {

  private static final Logger LOG = LoggerFactory.getLogger(InstallUI.class);

  String host = "ctr-e135-1512069032975-5447-01-000002.hwx.site\n" +
    "ctr-e135-1512069032975-5447-01-000003.hwx.site";
  String SSH_KEY = "/Users/prabhjyot.singh/server/hw-qe-keypair.pem";
  String webHostUrl;
  String AMBARI_URL = "http://release.eng.hortonworks.com/portal/release/Ambari/releasedVersion/AMBARI-2.6.0.0/2.6.0.0/";
  String HDP_URL = "http://release.eng.hortonworks.com/portal/release/HDP/releasedVersion/2.6-maint/2.6.4.0/";

  @Before
  public void startUp() {
    webHostUrl = "http://" + host.split("\n")[0] + ":8080";
  }

  @After
  public void tearDown() {
//    driver.quit();
  }

  @Test
  public void testAngularDisplay() throws Exception {
    try {
      GsonBuilder gsonBuilder = new GsonBuilder();
      Gson gson = gsonBuilder.create();
      long waitTimeInSeconds = 10;

      List<String> hostList = Arrays.asList(host.split("\n"));
      for (String host : hostList) {
        String command =
            "ssh -o StrictHostKeyChecking=no -i " + SSH_KEY + " root@"
                + host
                + " 'yum install -y vim htop curl wget mlocate;/etc/init.d/iptables stop'";
        executeCommand(command);
      }

      URL url = new URL(AMBARI_URL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
        if (line.contains("var data =")) {
          String result = line.split("var data = ")[1].split(";$")[0];
          Map<String, Map> build = gson.fromJson(result, Map.class);

          for (Map.Entry<String, Map> entry : build.entrySet()) {
            Map<String, Map> buildInfo = entry.getValue();
            if (((Map) buildInfo.get("platforms").get("centos6")).get("status").equals("pass")) {
              String repoView = (String) ((Map) buildInfo.get("platforms").get("centos6"))
                  .get("repo_view");
              String command =
                  "ssh -o StrictHostKeyChecking=no -i " + SSH_KEY
                      + " root@" + hostList.get(0) + " 'echo \"" + repoView
                      + "\" > /etc/yum.repos.d/ambari.repo;"
                      + "yum install ambari-server -y;"
                      + "ambari-server setup -s;"
                      + "ambari-server start'";
              executeCommand(command);
              break;
            }
          }
          break;
        }
      }
      rd.close();

      String hdpVersion = "BUILDS/2.5.0.0-872";

      url = new URL(HDP_URL);
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while ((line = rd.readLine()) != null) {
        if (line.contains("var data =")) {
          String result = line.split("var data = ")[1].split(";$")[0];
          Map<String, Map> build = gson.fromJson(result, Map.class);

          for (Map.Entry<String, Map> entry : build.entrySet()) {
            Map<String, Map> buildInfo = entry.getValue();
            if (((Map) buildInfo.get("platforms").get("centos6")).get("status").equals("pass")) {
              hdpVersion = "BUILDS/" + entry.getKey();
              break;
            }
          }
          break;
        }
      }
      rd.close();

      driver = WebDriverManager.getWebDriver(webHostUrl);

      driver.findElement(By.className("login-user-name")).sendKeys("admin");
      driver.findElement(By.className("login-user-password")).sendKeys("admin");

      driver.findElement(By.className("login-btn")).click();

      pollingWait(By.className("create-cluster-button"), waitTimeInSeconds).click();
      pollingWait(By.className("ember-text-field"), waitTimeInSeconds).sendKeys("test");
      driver.findElement(By.className("btn-success")).click();

      List<String> removeOSArray = new ArrayList<String>();
      removeOSArray.add("debian7");
      removeOSArray.add("redhat-ppc7");
      removeOSArray.add("redhat7");
      removeOSArray.add("suse12");
      removeOSArray.add("suse11");
      removeOSArray.add("ubuntu12");
      removeOSArray.add("ubuntu14");
      removeOSArray.add("ubuntu16");

      for (String remove : removeOSArray) {
        pollingWait(By.xpath(
            "//*[@id='repoVersionInfoForm']/div/div[2]/div/div[contains(.,'" + remove
                + "')]/div[3]"), waitTimeInSeconds).click();
      }

      List<WebElement> baseURL = driver.findElements(By.xpath("//input[@type='text']"));
      for (Integer i = 0; i < baseURL.size(); i++) {
        if (i % 2 == 0) {
          sleep(500, false);
//          String urls = baseURL.get(i).getAttribute("value").split("BUILDS")[0];
          String urls = "http://s3.amazonaws.com/dev.hortonworks.com/HDP/centos6/2.x/";
          urls += hdpVersion;
          baseURL.get(i).clear();
          baseURL.get(i).sendKeys(urls);

        }
      }
      sleep(2000, false);
      driver.findElement(By.xpath(".//*[@id='select-stack']/button[2]")).click();
      pollingWait(By.xpath(".//*[@id='host-names']"), waitTimeInSeconds).sendKeys(host);
      driver.findElement(By.id("sshKey")).sendKeys(InstallationUtils.getPrivateKey(SSH_KEY));
      driver.findElement(By.className("btn-success")).click();

      pollingWait(By.xpath("//div//span[contains(.,'Host')]"), waitTimeInSeconds);
      pollingWait(By.xpath("//div//span[contains(.,'Host')]"), waitTimeInSeconds);
      pollingWait(By.xpath("//div//a[contains(.,'Installing (0)')]"), (20 * 60 * 1000));

      try {
        while (driver.findElement(By.xpath("//*[@id='confirm-hosts']/div[3]/button[2]"))
            .getAttribute("disabled") != null) {
          sleep(1000, false);
        }
        String hostConfirmation = pollingWait(By.xpath("//*[@id='confirm-hosts']/div[2]"),
            waitTimeInSeconds)
            .getText();
        if (hostConfirmation.contains("success")) {
          pollingWait(By.className("btn-success"), waitTimeInSeconds).click();
        } else {
          pollingWait(By.className("btn-success"), waitTimeInSeconds).click();
          sleep(500, false);
          pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
        }
      } catch (Exception e) {
        //ignore
      }

      sleep(5000, false);
      pollingWait(By.xpath("//th/input"), waitTimeInSeconds).click();
      sleep(5000, false);
      pollingWait(By.xpath("//th/input"), waitTimeInSeconds).click();
      sleep(2000, false);

      pollingWait(By.className("SPARK2"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.className("ZEPPELIN"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();

      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      sleep(1000, false);
      pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();

      try {
        pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTimeInSeconds).click();
      } catch (Exception e) {

      }

      sleep(5000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();
      sleep(2000, false);
//      pollingWait(By.xpath(".//*[@id='component_assign_table']//label[contains(.,'Livy')]"),
//          waitTime).click();
      sleep(5000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();
      sleep(5000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();

      pollingWait(By.xpath("//div//li/a[contains(.,'Hive')]"), waitTimeInSeconds).click();
      pollingWait(By.xpath("//div//li/a[contains(.,'Advanced')]"), waitTimeInSeconds).click();
      driver.findElement(By.xpath("//div/input[@type='password'][1]")).sendKeys("admin");
      driver.findElement(By.xpath("//div/input[@type='password'][2]")).sendKeys("admin");
      sleep(1000, false);

      pollingWait(By.xpath("//div//li/a[contains(.,'Ambari Metrics')]"), waitTimeInSeconds).click();
      driver.findElement(By.xpath("//div/input[@type='password'][1]")).sendKeys("admin");
      driver.findElement(By.xpath("//div/input[@type='password'][2]")).sendKeys("admin");
      sleep(1000, false);

      pollingWait(By.xpath("//div//li/a[contains(.,'SmartSense')]"), waitTimeInSeconds).click();
      pollingWait(By.xpath("//div//li/a[contains(.,'Activity Analysis')]"), waitTimeInSeconds)
          .click();
      driver.findElement(By.xpath("//div/input[@type='password'][1]")).sendKeys("admin");
      driver.findElement(By.xpath("//div/input[@type='password'][2]")).sendKeys("admin");

      sleep(5000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();
      sleep(5000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();
      sleep(5000, false);
      pollingWait(By.className("btn-success"), waitTimeInSeconds).click();

      LOG.info("testCreateNotebook Test executed");

    } catch (Exception e) {
      handleException("Exception in InstallUI while testAngularDisplay ", e);
    }

  }

}
