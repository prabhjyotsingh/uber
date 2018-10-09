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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbariInstallViaJenkins extends AbstractIT {

  private static final Logger LOG = LoggerFactory.getLogger(AmbariInstallViaJenkins.class);


  String COOKIE_NAME = "JSESSIONID.b1a2e8e1";
  String COOKIE_VALUE = "node01j823vvu6iwta7ct1vj55jfha138368.node0";
  String HDP_URL = "http://release.eng.hortonworks.com/hwre-api/versioninfo?stack=HDP&stack_version=3.0.2.0&per_page=10";
  Long SLEEP_DURATION = 5l;


  @After
  public void cleanup() {
    driver.close();
  }

  @Test
  public void install() throws Exception {
    try {

      GsonBuilder gsonBuilder = new GsonBuilder();
      Gson gson = gsonBuilder.create();

      String hdpVersion = getVersionNumber(HDP_URL, gson);
      Cookie ck = new Cookie(COOKIE_NAME, COOKIE_VALUE);
      driver = WebDriverManager.getWebDriver(
          "http://linux-jenkins.qe.hortonworks.com:8080/job/Nightly-Start-EC2-Run-HDP/", ck);

      driver.findElement(By.xpath(
          "//div[@id='tasks']/div/a[@class='task-link'][contains(.,'Build with Parameters')]"))
          .click();
      sleep(2000);

      //ClusterName
      Date date = new Date();
      String pattern = "yyyy-MM-dd";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      String cluserName = "prabhu-" + simpleDateFormat.format(date) + "-" + date.getTime();

      Integer textIdx = 0;
      Integer selectIdx = 0;

      emptyTextAndPut(textIdx++, cluserName);

      //UserName
      emptyTextAndPut(textIdx++, "prabhjyot.singh");

      //Ami
      emptyTextAndPut(textIdx++,
          "registry.eng.hortonworks.com/hortonworks/hdp-centos7.4:" + hdpVersion);

      //PLATFORM
      updateSelectValue(selectIdx++, "RHEL7.4");

      //IMAGE_TYPE
      updateSelectValue(selectIdx++, "hwqe.xlarge");

      //LIFETIME
      emptyTextAndPut(textIdx++, "120");

      //numberOfInstance
      emptyTextAndPut(textIdx++, "3");

      //RUN_INSTALLER
      updateSelectValue(selectIdx++, "deployng");

      emptyTextAndPut(textIdx++, "");
      //GS_BRANCH
      emptyTextAndPut(textIdx++, "hdp2");

      //HMC_VERSION =1
      //ENABLE_MULTIPLE_CLUSTERS = no

      //HMC_RPM_URL
      emptyTextAndPut(textIdx++, "");

      selectIdx = 5;
      //INSTALL_HDP
      updateSelectValue(selectIdx++, "yes");
      selectIdx = 9;
      //SECURITY
      updateSelectValue(selectIdx++, "yes");
      selectIdx = 11;
      //INSTALL_HBASE
      updateSelectValue(selectIdx++, "yes");
      //INSTALL_TEMPLETON
      updateSelectValue(selectIdx++, "yes");
      //INSTALL_OOZIE
      updateSelectValue(selectIdx++, "no");
      //INSTALL_HIVE
      updateSelectValue(selectIdx++, "yes");
      //INSTALL_HCAT
      updateSelectValue(selectIdx++, "yes");
      //INSTALL_SQOOP
      updateSelectValue(selectIdx++, "no");
      //INSTALL_SQOOP2
      updateSelectValue(selectIdx++, "no");

      //INSTALL_FLUME
      emptyTextAndPut(textIdx++, "no");
      //INSTALL_STORM
      updateSelectValue(selectIdx++, "no");
      //INSTALL_KNOX
      emptyTextAndPut(textIdx++, "yes");

      //INSTALL_KAFKA
      emptyTextAndPut(textIdx++, "yes");

      textIdx = 17;
      //REPO_FILE
      emptyTextAndPut(textIdx++,
          "http://s3.amazonaws.com/dev.hortonworks.com/HDP/centos7/3.x/BUILDS/" + hdpVersion
              + "/hdpbn.repo");

      //RUN_QE_TETS
      selectIdx = 21;
      updateSelectValue(selectIdx++, "yes");

      //VERSION
      emptyTextAndPut(textIdx++, "2");

      //SHUTDOWN_CLUSTER
      updateSelectValue(selectIdx++, "no");
      //TESTSUITE_FILE
      emptyTextAndPut(textIdx++, "ZeppelinSSO");
      //AMBARI_TESTSNAMES
      emptyTextAndPut(textIdx++, "");
      //EMAIL
      emptyTextAndPut(textIdx++, "prabhjyot.singh@hortonworks.com");
      //HMC_DEPLOY_REPORT
      updateSelectValue(selectIdx++, "no");
      //INSTALL_JDK
      updateSelectValue(selectIdx++, "no");
      //SETUP_HMC_TESTS
      updateSelectValue(selectIdx++, "no");
      //PUBLISH_TEST_RESULTS
      updateSelectValue(selectIdx++, "no");
      //TEST_FRAMEWORK
      updateSelectValue(selectIdx++, "pytest");
      //IS_HMC_DEPLOY_TEST
      emptyTextAndPut(textIdx++, "no");
      //IS_HA_TEST
      emptyTextAndPut(textIdx++, "yes");
      //ENABLE_HA_COMPONENTS
      emptyTextAndPut(textIdx++, "all");
      //IS_RU_TEST
      emptyTextAndPut(textIdx++, "no");
      //UPGRADE_FROM_REPO
      emptyTextAndPut(textIdx++, "");
      //SETUP_AMBARI_TESTS
      updateSelectValue(selectIdx++, "no");
      //AMBARI_DEPLOY_REPORT
      updateSelectValue(selectIdx++, "no");
      //IS_AMBARI_DEPLOY_TEST
      emptyTextAndPut(textIdx++, "no");
      //DATABASE_FLAVOR
      updateSelectValue(selectIdx++, "maria");
      //OOZIE_USE_EXTERNAL_DB
      updateSelectValue(selectIdx++, "yes");
      //FILTER_PATH
      emptyTextAndPut(textIdx++, "");
      //CERTIFICATION_TAG
      emptyTextAndPut(textIdx++, "hdp3");
      //JDK_VERSION
      emptyTextAndPut(textIdx++, "OpenJDK8");
      //UMASK
      emptyTextAndPut(textIdx++, "Default");
      //MR_FRAMEWORK           
      updateSelectValue(selectIdx++, "yarn-tez");
      //AMBARI_DB
      updateSelectValue(selectIdx++, "maria");
      //CLUSTER_TYPE
      updateSelectValue(selectIdx++, "ycloud");
      //RUN_ON_LABEL
      emptyTextAndPut(textIdx++, "qe-launch-on-ycloud");
      //POSTGRES_VERSION
      updateSelectValue(selectIdx++, "8");
      //NANO_WAIT_TIME
      emptyTextAndPut(textIdx++, "600");
      //DATATEAMTEST_BRANCH
      emptyTextAndPut(textIdx++, "hdp3");
      //TEMPLETON_BRANCH
      emptyTextAndPut(textIdx++, "2.6-maint");
      //PIG_BRANCH
      emptyTextAndPut(textIdx++, "2.6-maint");
      //ACCUMULO_BRANCH
      emptyTextAndPut(textIdx++, "2.6-maint");
      //NOTIFY_ON_FAILURE
      emptyTextAndPut(textIdx++, "no");
      //AMBARI_BRANCH
      emptyTextAndPut(textIdx++, "ambari2.7-maint");
      //CLIENT_OS
      emptyTextAndPut(textIdx++, "LINUX");
      //CLIENT_PORT
      emptyTextAndPut(textIdx++, "5566");
      //BROWSER
      emptyTextAndPut(textIdx++, "firefox");
      //CLIENT
      emptyTextAndPut(textIdx++, "localhost");
      //WIRE_ENCRYPTION
      updateSelectValue(selectIdx++, "no");
      //PUSH_AMBARIARTIFACTS
      emptyTextAndPut(textIdx++, "");
      //STACK_UPGRADE_TO
      emptyTextAndPut(textIdx++, "");
      //UPGRADE_TO
      emptyTextAndPut(textIdx++, "");
      //INSTALL_PHOENIX
      updateSelectValue(selectIdx++, "yes");
      //NUM_OF_CLUSTERS
      emptyTextAndPut(textIdx++, "1");
      //INSTALL_FALCON
      emptyTextAndPut(textIdx++, "no");
      //INSTALL_PRISM
      emptyTextAndPut(textIdx++, "no");
      //ADDITIONAL_FALCON_SEVERS
      emptyTextAndPut(textIdx++, "");
      //ADDITIONAL_FALCON_COLOS
      emptyTextAndPut(textIdx++, "");
      //PRISM_HOST
      emptyTextAndPut(textIdx++, "");
      //RUN_ACTIVE_MQ
      emptyTextAndPut(textIdx++, "");
      //ACTIVE_MQ_ADDRESS
      emptyTextAndPut(textIdx++, "");
      //FALCON_COLO
      emptyTextAndPut(textIdx++, "");
      //FALCON_MODE
      emptyTextAndPut(textIdx++, "standalone");
      //KERBEROS_SERVER_TYPE
      emptyTextAndPut(textIdx++, "MIT");
      //AD_SERVER_HOST
      emptyTextAndPut(textIdx++, "ad-nano.qe.hortonworks.com");
      //REALM
      emptyTextAndPut(textIdx++, "EXAMPLE.COM");
      //AMBARI_VERSION
      emptyTextAndPut(textIdx++,
          "http://dev.hortonworks.com.s3.amazonaws.com/ambari/centos7/2.x/updates/2.7.0.0/ambariqe.repo");
      //EC2_ACCOUNT
      emptyTextAndPut(textIdx++, "hwqe");
      //EC2_SECURITY_GROUP
      emptyTextAndPut(textIdx++, "hwqe");
      //INSTALL_XASECURE
      emptyTextAndPut(textIdx++, "yes");
      //INSTALL_SLIDER
      emptyTextAndPut(textIdx++, "no");
      //INSTALL_ACCUMULO
      emptyTextAndPut(textIdx++, "no");
      //USER_KERBEROS_SERVER_TYPE
      emptyTextAndPut(textIdx++, "MIT");
      //USER_REALM	
      emptyTextAndPut(textIdx++, "EXAMPLE.COM");
      //AMBARI_OLD_BRANCH
      emptyTextAndPut(textIdx++, "none");
      //FALCON_REGRESSION_REPO
      emptyTextAndPut(textIdx++, "git@github.com:hortonworks/falcon.git");
      //FALCON_REGRESSION_BRANCH
      emptyTextAndPut(textIdx++, "pixie-dust-regression");
      //EC2_DRY_RUN
      emptyTextAndPut(textIdx++, "false");
      //SIDE_BY_SIDE_INSTALL
      updateSelectValue(selectIdx++, "no");
      //BASE_REPO_URL
      emptyTextAndPut(textIdx++, "");
      //XA_DATABASE_FLAVOR
      updateSelectValue(selectIdx++, "maria");
      //INSTALL_HUE
      emptyTextAndPut(textIdx++, "no");
      //START_STORM_SERVICES
      updateSelectValue(selectIdx++, "yes");
      //ENABLE_HDFS_HA
      emptyTextAndPut(textIdx++, "no");
      //ENABLE_WPR
      emptyTextAndPut(textIdx++, "no");
      //ENABLE_DROP_RESPONSE
      emptyTextAndPut(textIdx++, "no");
      //LOG_COLLECTION
      updateSelectValue(selectIdx++, "yes");
      //HUE_USE_EXTERNAL_DB
      updateSelectValue(selectIdx++, "no");
      //OPENSTACK_WAIT_TIME
      emptyTextAndPut(textIdx++, "1200");
      //OPENSTACK_DISKS_PER_INSTANCE
      emptyTextAndPut(textIdx++, "2");
      //OPENSTACK_DISK_SIZE
      emptyTextAndPut(textIdx++, "100");

      //asadasd
      //asdad
      //asda
      //OPENSTACK_ACCOUNT_NAME
      emptyTextAndPut(textIdx++, "qe-nat");
      //OPENSTACK_MAX_THREADS
      emptyTextAndPut(textIdx++, "4");

      //OPENSTACK_NUMBER_OF_TRIES
      emptyTextAndPut(textIdx++, "5");

      //PARENT_RESULT_ID
      emptyTextAndPut(textIdx++, "656103");

      //NUM_OF_SPLITS
      emptyTextAndPut(textIdx++, "1");

      //SPLIT_NUM
      emptyTextAndPut(textIdx++, "1");

      //INSTALL_SPARK
      updateSelectValue(selectIdx++, "no");

      //INSTALL_SPARK2
      emptyTextAndPut(textIdx++, "yes");

      //DESTROY_ON_FAIL
      updateSelectValue(selectIdx++, "no");

      //STACK
      emptyTextAndPut(textIdx++, "3.0.0.0");

      //SPLIT_ID
      emptyTextAndPut(textIdx++, "1188311");

      //DASHBOARD_HOST
      emptyTextAndPut(textIdx++, "dashboard.qe.hortonworks.com");

      //TDE
      updateSelectValue(selectIdx++, "yes");

      //HDP_REPO_BASEURL
      emptyTextAndPut(textIdx++,
          "http://s3.amazonaws.com/dev.hortonworks.com/HDP/centos7/3.x/BUILDS/" + hdpVersion);

      //HDP_UTILS_REPO_BASEURL
      emptyTextAndPut(textIdx++, "");

      //HUE_DB
      emptyTextAndPut(textIdx++, "mysql");

      //INSTALL_ATLAS
      updateSelectValue(selectIdx++, "no");

      //EC2_DISK_SZIE
      emptyTextAndPut(textIdx++, "200");

      //EC2_NUM_OF_DISKS
      emptyTextAndPut(textIdx++, "2");

      //MAX_EC2_RETRIES
      emptyTextAndPut(textIdx++, "5");

      //AMBARI_CUSTOM_USER
      emptyTextAndPut(textIdx++, "root");

      //AMBARI_AGENT_USER
      emptyTextAndPut(textIdx++, "root");

      //RUN_INDIVIDUAL_TESTS
      emptyTextAndPut(textIdx++, "");

      //TRACE_LOGGING
      updateSelectValue(selectIdx++, "no");

      //GATHERLOG_ON_FAILURE
      updateSelectValue(selectIdx++, "no");

      //YCLOUD_ACCOUNT_NAME
      emptyTextAndPut(textIdx++, "None");

      //YCLOUD_ENV
      emptyTextAndPut(textIdx++, "prod");

      //CUSTOMIZED_SERVICES_USERS
      emptyTextAndPut(textIdx++, "False");

      //MOTD_ENABLE
      emptyTextAndPut(textIdx++, "False");

      //AMBARI_2WAY_SSL
      emptyTextAndPut(textIdx++, "yes");

      //PUBLISH_TEST_CASE_STATUS
      updateSelectValue(selectIdx++, "yes");

      //IS_TMP_NOEXEC
      emptyTextAndPut(textIdx++, "no");

      //PUBLISH_TEST_CASE_TIMEOUT
      emptyTextAndPut(textIdx++, "10");

      //DN_NONROOT
      emptyTextAndPut(textIdx++, "no");

      //PWD_ENCRYPT
      emptyTextAndPut(textIdx++, "False");

      //RUN_PRIV_CONTAINER
      updateSelectValue(selectIdx++, "no");

      //AMBARI_SERVER_HTTPS
      emptyTextAndPut(textIdx++, "yes");

      //CUSTOM_PIDS
      emptyTextAndPut(textIdx++, "False");

      //USE_BLUEPRINT
      emptyTextAndPut(textIdx++, "YES");

      //ERASURE_CODING
      emptyTextAndPut(textIdx++, "no");

      //VIDEO_RECORDING
      emptyTextAndPut(textIdx++, "true");

      //RUN_QE_TESTS_ONLY
      updateSelectValue(selectIdx++, "no");

      //CLOUDBREAK_IDENTITY_SERVER
      emptyTextAndPut(textIdx++, "https://qa-identity.sequenceiq.com");

      //CLOUDBREAK_USERNAME
      emptyTextAndPut(textIdx++, "ferenc.schneider@sequenceiq.com");

      //CLOUDBREAK_PASSWORD
      emptyTextAndPut(textIdx++, "inttest-15");

      //CLOUDBREAK_SERVER
      emptyTextAndPut(textIdx++, "https://qa-cloudbreak-api.sequenceiq.com");

      //CLOUDBREAK_TEST_SUITES
      emptyTextAndPut(textIdx++,
          "classpath:/testsuites/resourcetests.yaml,classpath:/testsuites/openstack-resourcetests.yaml,classpath:/testsuites/openstack/credandsmoke/openstack-clustercreate-startstop-updown.yaml,classpath:/testsuites/nativeos/credandsmoke/nativeos-clustercreate-startstop-updown.yaml");

      //RUN_MARKER_VERSION
      emptyTextAndPut(textIdx++, "");

      //RUN_MARKER_LIST
      emptyTextAndPut(textIdx++, "");

      //ADDITIONAL_HDP_REPO
      emptyTextAndPut(textIdx++, "");

      //RERUN_UNTIL_FAILURE
      emptyTextAndPut(textIdx++, "no");

      //LOGSEARCH_ENABLE
      //emptyTextAndPut(textIdx++, "no");

      //LOGSEARCH_BASE_URL
      emptyTextAndPut(textIdx++, "http://172.22.122.198:61888");

      //LOGSEARCH_ZK_HOSTS
      emptyTextAndPut(textIdx++, "172.22.122.196:2181,172.22.122.20:2181,172.22.122.2:2181/solr");

      //INSTALL_HS_INTERACTIVE
      emptyTextAndPut(textIdx++, "no");

      //IS_UPGRADE_CLUSTER
      emptyTextAndPut(textIdx++, "no");

      //INSTALL_ZEPPELIN
      updateSelectValue(selectIdx++, "yes");

      //INSTALL_LOGSEARCH
      updateSelectValue(selectIdx++, "no");

      //IS_S3_DEFAULT_FS
      emptyTextAndPut(textIdx++, "no");

      //QE_INFRA_BRANCH
      emptyTextAndPut(textIdx++, "origin/master");

      //INSTALL_HDF
      updateSelectValue(selectIdx++, "no");

      //INSTALL_NIFI
      updateSelectValue(selectIdx++, "no");

      //HDF_REPO_BASEURL
      emptyTextAndPut(textIdx++, "");

      //SETUP_MANAGEMENT_PACK
      updateSelectValue(selectIdx++, "no");

      //MANAGEMENT_PACK_LINK
      emptyTextAndPut(textIdx++, "");

      //INSTALL_HWQE_GRPC
      updateSelectValue(selectIdx++, "no");

      //HWQE_GRPC_VERSION
      emptyTextAndPut(textIdx++, "0.0.1");

      //HWQE_GRPC_PORT
      emptyTextAndPut(textIdx++, "50051");

      //INFRA_FRAMEWORK
      updateSelectValue(selectIdx++, "default");

      //HDP_JSON_FILE
      emptyTextAndPut(textIdx++, "None");

      //BuildPriority
      updateSelectValue(selectIdx++, "5");

      //CBD_VERSION
      emptyTextAndPut(textIdx++, "master");

      //CLOUD_PROVIDER
      updateSelectValue(selectIdx++, "openstack-hwx");

      //INSTALL_AMBARI_INFRA
      updateSelectValue(selectIdx++, "yes");

      //USER_DATA_SCRIPT
      emptyTextAndPut(textIdx++, "");

      //KDC_HOST
      emptyTextAndPut(textIdx++, "");

      //GENERATE_KEYTABS
      updateSelectValue(selectIdx++, "no");

      //KERBEROS_ADMIN_PRINCIPAL
      emptyTextAndPut(textIdx++, "");

      //KERBEROS_ADMIN_PWD
      emptyTextAndPut(textIdx++, "");

      //ADDITIONAL_REALMS
      emptyTextAndPut(textIdx++, "");

      //ADDITIONAL_REALMS_KDC
      emptyTextAndPut(textIdx++, "");

      //SERVICES_CHECKS
      updateSelectValue(selectIdx++, "no");

      //USE_BAKED_IMAGE
      updateSelectValue(selectIdx++, "yes");

      //BAKED_IMAGE_TYPE
      emptyTextAndPut(textIdx++, "hdp");

      //INSTALL_DRUID
      emptyTextAndPut(textIdx++, "no");

      //LOGSERVER
      updateSelectValue(selectIdx++, "qelog.hortonworks.com");

      //COVERAGE_COMPONENT
      emptyTextAndPut(textIdx++, "None");

      //COVERAGE_PER_TEST_CLASS
      emptyTextAndPut(textIdx++, "false");

      //STACK_TYPE
      emptyTextAndPut(textIdx++, "");

      //RUN_ID
      emptyTextAndPut(textIdx++, "107553");

      //TEST_JSON_FILE
      emptyTextAndPut(textIdx++, "HDP-3.0.json");

      //YCLOUD_QUEUE
      emptyTextAndPut(textIdx++, "HDP_3_0_0_0-integration-tests");

      //ADDITIONAL_AMBARI_PROPS
      emptyTextAndPut(textIdx++, "");

      //AMBARI_AGENT_LOG_LEVEL
      emptyTextAndPut(textIdx++, "");

      //INSTALL_BEACON
      updateSelectValue(selectIdx++, "no");

      //BEACON_REPO_URL
      emptyTextAndPut(textIdx++, "");

      //INSTALL_HCP
      updateSelectValue(selectIdx++, "no");

      //HCP_REPO_BASEURL
      emptyTextAndPut(textIdx++, "");

      //INSTALL_STREAMLINE
      updateSelectValue(selectIdx++, "no");

      //INSTALL_REGISTRY
      updateSelectValue(selectIdx++, "no");

      //STREAMLINE_DB
      updateSelectValue(selectIdx++, "mysql");

      //REGISTRY_DB
      updateSelectValue(selectIdx++, "mysql");

      //USE_INFRA_V2
      updateSelectValue(selectIdx++, "no");

      //INFRA_V2_BRANCH
      emptyTextAndPut(textIdx++, "master");

      //RELEASE_CONFIG_ID
      emptyTextAndPut(textIdx++, "2256");

      //TEST_COMPONENT_NAME
      emptyTextAndPut(textIdx++, "ZeppelinSpark2");

      //RELEASE_NAME
      emptyTextAndPut(textIdx++, "HDP-3.0.0.0");

      //LOG_COLLECTION_V2
      emptyTextAndPut(textIdx++, "no");

      //NIFI_QE_BRANCH
      emptyTextAndPut(textIdx++, "master");

      //SCHEMA_REGISTRY_QE_BRANCH
      emptyTextAndPut(textIdx++, "master");

      //STREAMLINE_QE_BRANCH
      emptyTextAndPut(textIdx++, "master");

      //INSTALL_DP
      emptyTextAndPut(textIdx++, "no");

      //INSTALL_DLM
      emptyTextAndPut(textIdx++, "no");

      //INSTALL_DSS
      emptyTextAndPut(textIdx++, "no");

      //ENABLE_DSX_PRE_SETUP
      updateSelectValue(selectIdx++, "no");

      //BUILD_UTILS_BRANCH
      emptyTextAndPut(textIdx++, "master");

      //INSTALL_LIVY
      updateSelectValue(selectIdx++, "no");

      //INSTALL_LIVY2
      updateSelectValue(selectIdx++, "yes");

      //DP_USE_TEST_LDAP
      emptyTextAndPut(textIdx++, "no");

      //DP_SEPARATE_KNOX_CONFIG
      emptyTextAndPut(textIdx++, "false");

      //ENABLE_KNOX_SSO
      updateSelectValue(selectIdx++, "yes");

      //INSTALL_SUPERSET
      updateSelectValue(selectIdx++, "no");

      //DEPLOYNG_BRANCH
      emptyTextAndPut(textIdx++, "ambari2.7-maint");

      //PURGE_MPACK
      emptyTextAndPut(textIdx++, "no");

      //ENABLE_MIXED_OS
      updateSelectValue(selectIdx++, "no");

      //SECONDARY_PLATFORM
      updateSelectValue(selectIdx++, "");

      //INSTALL_MR
      updateSelectValue(selectIdx++, "yes");

      //INSTALL_HDFS
      updateSelectValue(selectIdx++, "yes");

      //INSTALL_BIGSQL
      updateSelectValue(selectIdx++, "no");

      //INSTALL_AMS
      updateSelectValue(selectIdx++, "yes");

      //TEMPLATE
      emptyTextAndPut(textIdx++, "https://s3.amazonaws.com/hdc-cfn/cbd-advanced-1.7.0.template");

      //WORKLOAD_TYPE
      emptyTextAndPut(textIdx++, "HDP26_TP_HIVE_SPARK");

      //USE_CLI
      emptyTextAndPut(textIdx++, "yes");

      //TASKREPORTER_VERSION
      emptyTextAndPut(textIdx++, "latest");

      //TASKREPORTER_BRANCH	
      emptyTextAndPut(textIdx++, "master");

      //SAME_TDE_KEY
      emptyTextAndPut(textIdx++, "False");

      //BIGSQL_REPO
      emptyTextAndPut(textIdx++, "");

      //INSTALL_DP_PROFILER_AGENT
      updateSelectValue(selectIdx++, "no");

      //DP_PROFILER_AGENT_DB
      updateSelectValue(selectIdx++, "mysql");

      //ENABLE_DOCKER_ON_YARN
      updateSelectValue(selectIdx++, "no");

      //INSTALL_NIFI_REGISTRY
      updateSelectValue(selectIdx++, "no");

      //SPLITS_LABEL
      emptyTextAndPut(textIdx++, "");

      //ENABLE_DP_KNOX_PROXY
      updateSelectValue(selectIdx++, "no");

      //ENABLE_GPU
      updateSelectValue(selectIdx++, "no");

      //ENABLE_KNOX_PROXY
      updateSelectValue(selectIdx++, "no");

      //DLM_CLOUD_TYPE
      emptyTextAndPut(textIdx++, "none");

      //DLM_CLOUD_S3_ENCRYPTION_TYPE
      updateSelectValue(selectIdx++, "none");

      //STORM_QE_BRANCH
      emptyTextAndPut(textIdx++, "master");

      //RERUNS
      emptyTextAndPut(textIdx++, "");

      //DEPLOYNG_UI_BRANCH
      emptyTextAndPut(textIdx++, "");

      //ENABLE_FEDERATION
      updateSelectValue(selectIdx++, "no");

      //NO_OF_NAMESPACES
      emptyTextAndPut(textIdx++, "2");

      //INSTALL_DAS
      emptyTextAndPut(textIdx++, "no");

      //DP_REPO_URL
      emptyTextAndPut(textIdx++, "");

      //DSS_REPO_URL
      emptyTextAndPut(textIdx++, "");

      driver.findElements(By.xpath("//form/table//button[contains(.,'Build')]")).get(0).click();
    } catch (Exception e) {
      handleException("Exception in InstallUI while install ", e);
    }
  }

  private void emptyTextAndPut(Integer index, String value) {
    driver.findElements(By.xpath("//form/table//input[@type='text']")).get(index).clear();
    sleep(SLEEP_DURATION);
    driver.findElements(By.xpath("//form/table//input[@type='text']")).get(index).sendKeys(value);
  }

  private void updateSelectValue(Integer index, String value) {
    sleep(SLEEP_DURATION);
    (new Select(driver.findElements(By.xpath("//form/table//select")).get(index)))
        .selectByValue(value);

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
    Integer minorVersionNumber = 0;
    String versionNumber = null;
    try {
      while ((line = rd.readLine()) != null) {
        Map<String, Map> build = gson.fromJson(line, Map.class);

        for (Map.Entry<String, Map> entry : build.entrySet()) {
          Map<String, Map> buildInfo = entry.getValue();
          try {
            if (((Map) buildInfo.get("platforms").get("centos7")).get("compile_status")
                .equals("pass")) {
              Integer temp = new Integer(((String) entry.getKey()).split("-")[1]);
              if (temp > minorVersionNumber) {
                minorVersionNumber = temp;
                versionNumber = entry.getKey();
              }
            }
          }catch (NullPointerException e){
            //ignore
          }
        }
        break;
      }
    } finally {
      rd.close();
    }

    return versionNumber;
  }

}
