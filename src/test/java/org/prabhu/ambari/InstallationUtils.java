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

  public static String getPrivateKey() {
    return "-----BEGIN RSA PRIVATE KEY-----\n"
        + "MIIEowIBAAKCAQEAwf4QUH8xfdUhMWeDIJVO3l9ukXyBLM+Wghs822hnkBuyXIeV\n"
        + "yFhxNfdgP+Y0VGctnlObBTLGEt9ixKiklSZ+Tf+T+8hNkL3E1KLDO8B1kFuCayJi\n"
        + "9D0yUz10XP0L5Bj5Gty7wQi+j5VRDchhqPgtw/nYEpaGgXmfVEZNCPmHP1bIoUKD\n"
        + "HZFTmCCgBsWZRvyQRAQYwmeZ4OFpc3X3LCoLVBm6+pzp95qivPz5L/AnloLhGGom\n"
        + "4+TrOsnZh1Wd9MNLSw+hru1B9eDNzNy5zZQp3UixuG0c1D+jsRhBVpRvBjyZ792d\n"
        + "4d6G6it4yALMfBh0fRMUfGHTZb5IoG0IPMQxiQIDAQABAoIBACMJvW6cmpbMsPMY\n"
        + "bCGtdhJKEFFsuHQchmW3f7VtVQ89QWjfAFmwR7pklNjVvmvkOuQW27ADrKzp9g/p\n"
        + "Gdjqm/OAjvzx/7h93hg90Z9wMovBS3wIOPE+QGGyftrwxX4MWKnWZn8GEk0ivgIM\n"
        + "SD9ZnAKClNk+oMqv6ewxymYtk5UHyX4CQ8YxPvnRyO8rglJA4zU6agE3vFeFQP/u\n"
        + "eOkymD9pzvMB3CMhU307q2gP/f3VCFguJdKO841T/FkfClyR9Wtsgng6vT6qTcfw\n"
        + "9PdAqXHOrwjDvSmgfdFFFdP2/5MtBGBZF39F3Ne6IfBpS+IcsyDcJCI1QiIYlRNX\n"
        + "a9JHLcUCgYEA/l61N5IUzWbw1SHTRBR1Hgb+a2+98AjDcYqosbgLfpQo3uFDyfnk\n"
        + "CwrY9KqwaSvn0dMmhQnwU51IE+2+l8st19sPhPMVddYXH2n3uyKGRRGPMDAhtMWf\n"
        + "GVYZgGvRz976bUlTUWUtcHcv+633TNNnaO7GHLkkx47bcK+t88T32jcCgYEAwzxO\n"
        + "lMk1sRlQx3psbcVZ2Kls5e3SOUqOB15mWfEMVITlzOu4FiEbVv9CbuNtKNjhu7Ln\n"
        + "J5mIf2MJiD3W4OK/hA9671KOxiV2wNQ0XkbtyLazR+LwSwaRU7V8KsJMuH8d2b73\n"
        + "/qo1ggf+X084iqL9tUBqZwbLBVJtH51BSw+vcj8CgYEA6iT3no2DRIdrTGT0eYhz\n"
        + "Pg6mBvM23UrTzSIbomuNeRicfnzQz4yM03VKu/yaolTd8RDyNigt5mmyvVVsyYEn\n"
        + "U7i7kl/H8038vtfmR4XCHrXJHkxP2nzGtKXCl6mn2jagQhTq8tO1ff7YVk2OhFNc\n"
        + "rwSLrEjoiCEB8bpk3y0CEQUCgYBfK6e2ubrVoNyS0OfIPtrEVhrCnsLKsMA5cVf9\n"
        + "Yha7oKkQ0ptDFlJofzgYK/8LWWX4hIZP9HTofBOqeVKk/4OSUaWAwkLc7mhMKWV5\n"
        + "y6OgIweT9FlkiUgQgTiM83hIu5aGjaQAXWKGB3WFam3KjxeT4lm77UDMUFjnf1XT\n"
        + "zEqfKwKBgCYHwNXo4eX2+v0Myk+zSaDF1hO8BorWANNSdkj3MlAncUfouzLRwigj\n"
        + "7k7OlCSflm2tYNFtdhheZf8Bu2eUm+/uqelrgKJJW4gaV9ILtFOQWayZ7F6yrvRp\n"
        + "UJp++mpVj67cxQmzxRHvVGtW3vXShcqjWuLBZ/nZEnioZl8Zb3PA\n"
        + "-----END RSA PRIVATE KEY-----\n";
  }
}
