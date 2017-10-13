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


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
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
    executor.setWatchdog(new ExecuteWatchdog(400000L));

    try {
      int exitVal = executor.execute(cmdLine);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
