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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InstallUI extends AbstractIT {
    private static final Logger LOG = LoggerFactory.getLogger(InstallUI.class);


    @Before
    public void startUp() {
        driver = WebDriverManager.getWebDriver("http://172.22.90.98:8080");
    }

    @After
    public void tearDown() {
//    driver.quit();
    }

    @Test
    public void testAngularDisplay() throws Exception {
        try {

//      String hdpVersion = "BUILDS/2.5.0.0-872";
            String host = "prabhu-zep-18-1.novalocal";
            long waitTime = 10000;

            driver.findElement(By.className("login-user-name")).sendKeys("admin");
            driver.findElement(By.className("login-user-password")).sendKeys("admin");

            driver.findElement(By.className("login-btn")).click();

            pollingWait(By.className("create-cluster-button"), waitTime).click();
            pollingWait(By.className("ember-text-field"), waitTime).sendKeys("test");
            driver.findElement(By.className("btn-success")).click();

            //TODO wait for next screen


//      List<WebElement> baseURL = driver.findElements(By.xpath("//input[@type='text']"));
//      for (Integer i = 0; i < baseURL.size(); i++) {
//        if (i % 2 == 0) {
//          sleep(500, false);
//          String url = baseURL.get(i).getAttribute("value").split("BUILDS")[0];
//          url += hdpVersion;
//          baseURL.get(i).clear();
//          baseURL.get(i).sendKeys(url);
//
//        }
//      }
            sleep(2000, false);
            driver.findElement(By.xpath(".//*[@id='select-stack']/button[2]")).click();
            pollingWait(By.xpath(".//*[@id='host-names']"), waitTime).sendKeys(host);
            driver.findElement(By.id("sshKey")).sendKeys(InstallationUtils.getPrivateKey());
            driver.findElement(By.className("btn-success")).click();

            pollingWait(By.xpath("//div//span[contains(.,'Host')]"), waitTime);
            pollingWait(By.xpath("//div//span[contains(.,'Host')]"), waitTime);
            pollingWait(By.xpath("//div//a[contains(.,'Installing (0)')]"), (20 * 60 * 1000));

            try {
                pollingWait(By.xpath("//*[@id='confirm-hosts']/div[2][contains(.,'All host checks passed')]"), (5 *
                        60 * 1000));
            } catch (Exception e) {
                //ignore
            }

            pollingWait(By.className("btn-success"), waitTime).click();


            sleep(2000, false);
            pollingWait(By.xpath("//th/input"), waitTime).click();
            sleep(1000, false);
            pollingWait(By.xpath("//th/input"), waitTime).click();
            sleep(1000, false);
            pollingWait(By.className("ZEPPELIN"), waitTime).click();
            sleep(1000, false);
            pollingWait(By.className("btn-success"), waitTime).click();

            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            sleep(500, false);
            pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            try {
                pollingWait(By.xpath(".//*[@id='modal']/div[3]/button[2]"), waitTime).click();
            } catch (Exception e) {

            }


            sleep(2000, false);
            pollingWait(By.className("btn-success"), waitTime).click();
            sleep(2000, false);
//      pollingWait(By.xpath(".//*[@id='component_assign_table']//label[contains(.,'Livy')]"),
//          waitTime).click();
            sleep(500, false);
            pollingWait(By.className("btn-success"), waitTime).click();
            sleep(2000, false);
            pollingWait(By.className("btn-success"), waitTime).click();

            pollingWait(By.xpath("//div//li/a[contains(.,'Hive')]"), waitTime).click();
            pollingWait(By.xpath("//div//li/a[contains(.,'Advanced')]"), waitTime).click();
            driver.findElement(By.xpath("//div/input[@type='password'][1]")).sendKeys("asdf1234");
            driver.findElement(By.xpath("//div/input[@type='password'][2]")).sendKeys("asdf1234");
            sleep(500, false);
            pollingWait(By.className("btn-success"), waitTime).click();
            sleep(500, false);
            pollingWait(By.className("btn-success"), waitTime).click();
            sleep(5000, false);
            pollingWait(By.className("btn-success"), waitTime).click();


            LOG.info("testCreateNotebook Test executed");
        } catch (Exception e) {
            handleException("Exception in InstallUI while testAngularDisplay ", e);
        }
    }

}
