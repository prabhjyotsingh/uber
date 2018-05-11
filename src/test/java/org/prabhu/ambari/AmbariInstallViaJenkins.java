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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbariInstallViaJenkins extends AbstractIT {

  private static final Logger LOG = LoggerFactory.getLogger(AmbariInstallViaJenkins.class);


  String COOKIE_VALUE = "node02k0n6ke0ctz416xr3tcfcdfym493884.node0";
  String AMBARI_URL = "http://release.eng.hortonworks.com/hwre-api/versioninfo?stack=AMBARI&stack_version=2.7.0.0&per_page=10";
  String HDP_URL = "http://release.eng.hortonworks.com/hwre-api/versioninfo?stack=HDP&stack_version=3.0.0.0&per_page=10";


  @Test
  public void testAngularDisplay() throws Exception {
    try {

      GsonBuilder gsonBuilder = new GsonBuilder();
      Gson gson = gsonBuilder.create();

      String ambariVersion = getVersionNumber(AMBARI_URL, gson);
      String hdpVersion = getVersionNumber(HDP_URL, gson);

      driver = WebDriverManager.getWebDriver(
          "http://linux-jenkins.qe.hortonworks.com:8080/job/Nightly-Start-EC2-Run-HDP/");

      Cookie ck = new Cookie("JSESSIONID.4bb8a9da", COOKIE_VALUE);
      driver.manage().addCookie(ck);
      driver.navigate().refresh();
      sleep(5000);

      driver.findElement(By.xpath(
          "//div[@id='tasks']/div/a[@class='task-link'][contains(.,'Build with Parameters')]"))
          .click();
      sleep(2000);

      Date date = new Date();

      //ClusterName
      String cluserName =
          "prabhu-" + date.getYear() + "-" + date.getMonth() + "-" + date.getDate() + "-" + date
              .getTime();
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(0)
          .sendKeys(cluserName);

      //UserName
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(1)
          .sendKeys("prabhjyot.singh");

      //Ami
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(2)
          .sendKeys("registry.eng.hortonworks.com/hortonworks/hdp-centos7:" + hdpVersion);

      //lifefime
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(3).sendKeys("100");
      //numberOfInstance
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(4).sendKeys("5");
      //RUN_INSTALLER
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(5)
          .sendKeys("deployng");
      //GS_BRANCH
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(6).sendKeys("hdp2");
      //HMC_VERSION
      driver.findElements(By.xpath("//form/table//input[@type='text']")).get(7).sendKeys("1");

    } catch (Exception e) {
      handleException("Exception in InstallUI while testAngularDisplay ", e);
    }

  }


  public void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      LOG.error("Exception in WebDriverManager while getWebDriver ", e);
    }
  }

  private String getVersionNumber(String URL, Gson gson) throws IOException {
    URL url = new URL(URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    try {
      while ((line = rd.readLine()) != null) {
        Map<String, Map> build = gson.fromJson(line, Map.class);

        for (Map.Entry<String, Map> entry : build.entrySet()) {
          Map<String, Map> buildInfo = entry.getValue();
          if (((Map) buildInfo.get("platforms").get("centos7")).get("compile_status")
              .equals("pass")) {
            return entry.getKey();
          }
        }
        break;
      }
    } finally {
      rd.close();
    }

    return null;
  }

}
