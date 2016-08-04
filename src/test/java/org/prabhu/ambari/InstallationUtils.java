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
    return "-----BEGIN RSA PRIVATE KEY-----\n" +
        "MIIEowIBAAKCAQEAt8hfHuBOaN4SzDO007ktC0p13dnDu1sC6qlecIJlmsmlle2Z\n" +
        "oft7xZnOXXO3948zciR7dkvLVqV3cBT64detcOyxfnL9yJJx1jg91xT4s12cRGr7\n" +
        "xHQ5xM3Triplwbbdx2LLQsbk1bPobIG92Osc8RaTx9x+qNH2yu/+sCMayPFBFN+R\n" +
        "QhejdDUXfB45p3F0ZsipwQTjJr9M4Cm41W8/Y294RK4g1GZ+Og5cvuAudykb2vgD\n" +
        "E5y1g9D2z8Qcq7N2nkky0kcYtYugPPk3c+AuH/no2Cu/1Ql6DpDRc3N7LHDUfXHP\n" +
        "G3N18VEk6RyobnkVcam1xoaQ1bGdVFoyQlcfnQIDAQABAoIBAG6JFDTk4iQOlS2+\n" +
        "V58T+/STh0q0pBW2io2K6qMuul+bsnz3V/Tl5a6WcFkitV4vAdk/+gXPVnA5uxlt\n" +
        "dOuKpeWKZsLP9LXC5rbaQs9u7JoszkUUN3MUGH628P8KvwMAsmkukWQLbizeNQe2\n" +
        "DPI/jruuQZnukzuEdTPfnsZfhsed1f1NcyXdpDRA+z/tJbPJKpllbxvRJvvv4FYC\n" +
        "ZTSxfGcdnjxpJzhuf7bhX4BFDQL6cpRgITQTKVj7Cvhelsv87HlrZ20mlI516IsI\n" +
        "NEeTeQWo0tDpiislPSs05CSXhl7zNqYnw6BcRQPFF3l4ZMZ1QKgsWrCApbbpNfmQ\n" +
        "YZfr9SECgYEA3CVYrow+nlwtpuB/wEMuoDKTLeBkTeTfXCTV/snEwAkfGIP1c4wy\n" +
        "eSq3FtsiEUSQ9FATKzY6sPW8kty2jiGwvnkqQqMjRdPHZxUMz2ofx9y2+Fxmroo3\n" +
        "gacGGAKnORCbjY3laPUlT0nRb8dQ59xyZVETgh7T+Yd6iy/lSniTAJUCgYEA1bbq\n" +
        "pao8lSO828HjtazuPxiMRna3mEvWprZ9gxFU02cbud20XD6PegNnNVtF9FvpD7IA\n" +
        "CgI7MKIh85vOesWozeMLg4Fhu5XVnASewRTQRmfT0bT97yve2OILNLnBKmTFVdat\n" +
        "faCEFjJSTuwCC8XmFy6JhMLlIzdjCltVj6j/OOkCgYBbPlNqTRBrHHODdgsdCfta\n" +
        "RHwCCL4qFtGI5G+ZodEdcupjg9YI5TPKSq0GGR8enxehW0sGnvxmegOFr1suv8H8\n" +
        "wEWGxw0IlQVRsUTU9vxjHxTrDYk1kG3/G9YHv5nFqh9+5TAPd4PWQBdBSBA9eX4k\n" +
        "hh0a0mIzSza8V8amPiFXnQKBgQCZLK0pZ8ZcLm0iJOmMQRHRb0q7BkO4iEKk4o86\n" +
        "7aUakAAzS1AF/WDgVqgWegC9j1KPyUh14/uAc/RZogZvwFOonV6XNphR1qZvWHBg\n" +
        "ewZ8L9aFq2+FiLAESp81fx2PwQIe7+sQ64dAUXjk0Ar3XTP40YYG/Zk1kHEP7z53\n" +
        "5OxoYQKBgHjzh+tSzytAERAO/FpcTALgGHg3TRguzYwZyaEBJYtosjjHYN8DrXQH\n" +
        "IS5IhxcNnoBdnGOuYvtFefsgq4QkLEo8/KInXMKQG7OSn9JdTntcU0FWzLfFWoRC\n" +
        "mGKu8r8Qyf2H1A59B2qGW8ooiaqqrYYXhAkdmhxd8m1jF/tcEqKm\n" +
        "-----END RSA PRIVATE KEY-----\n";
  }
}
