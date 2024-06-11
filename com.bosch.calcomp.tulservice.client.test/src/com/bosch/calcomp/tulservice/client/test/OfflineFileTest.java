/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.client.test;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.tulservice.client.OfflineDataScheduler;
import com.bosch.calcomp.tulservice.client.ToolUsageLoggerClient;
import com.bosch.calcomp.tulservice.internal.model.ToolName;
import com.bosch.calcomp.tulservice.internal.model.ToolUsageStat;
import com.bosch.calcomp.tulservice.utils.MessageConstants;
import com.bosch.calcomp.tulservice.utils.OfflineDataFileHandler;
import com.bosch.calcomp.tulservice.utils.ToolUsageStatUtils;

/**
 * @author TAB1JA
 *
 */
public class OfflineFileTest {

  private ToolUsageStat toolUsageStat;
  private static final ILoggerAdapter TUL_LOGGER =
      new Log4JLoggerAdapterImpl(LogManager.getLogger(ToolUsageLoggerClient.class));
  /**
   * setting up tool usage instance
   */
  @Before
  public void settoolUsageStat() {

    
   this.toolUsageStat = ToolUsageStatUtils.getInstance().createPreFilledToolUsageStat();

    this.toolUsageStat.setJobID(UUID.randomUUID().toString());
    this.toolUsageStat.setUsername(System.getProperty("user.name"));
    this.toolUsageStat.setTool(ToolName.CHECKSSD.getTool());
    this.toolUsageStat.setToolVersion("V3.1.0");
    this.toolUsageStat.setToolCategory("processing"/* toolData.getToolCategory() */);
    this.toolUsageStat.setArtifactInfo("Test call for test case execution");
    this.toolUsageStat.setComponent("Excel Report");
    this.toolUsageStat.setEvent("processing");
    this.toolUsageStat.setFeature("One File Check");
    this.toolUsageStat.setMisc("Test call for test case execution");
    this.toolUsageStat.setSchemaVer(MessageConstants.SCHEMA_VERSION_SPEC);
    this.toolUsageStat.setIsTest("True");
  }
  
  /**
   * test case to check offline file creation in case of post call failure
   */
  @Test
  public void tulOfflinefileCreateTest()  {
    
    try {
      OfflineDataFileHandler.setFileLocation(this.toolUsageStat);
      String json = ToolUsageStatUtils.constructJSONString(Arrays.asList(toolUsageStat));
      OfflineDataScheduler.getInstance().scheduleTimer(false, this.toolUsageStat.getFilePath(), json,true,TUL_LOGGER);;
      File[] listOfFiles = OfflineDataFileHandler.getListOfFiles(this.toolUsageStat.getFilePath());
      Assert.assertTrue(listOfFiles.length>0);
    }
    catch (IOException e) {
      TUL_LOGGER.error(e.getMessage());
    }
  }
  
  
  /**
   * test case to check if offlines files are posted then it should get deleted
   */
  @Test
  public void tulOfflinefileDeleteTest()  {
    
    try {
      OfflineDataFileHandler.setFileLocation(this.toolUsageStat);
      String json = ToolUsageStatUtils.constructJSONString(Arrays.asList(toolUsageStat));
      OfflineDataScheduler.getInstance().scheduleTimer(false, this.toolUsageStat.getFilePath(), json,true,TUL_LOGGER);
      Thread. sleep(90000);
      File[] listOfFiles = OfflineDataFileHandler.getListOfFiles(this.toolUsageStat.getFilePath());
      Assert.assertTrue(listOfFiles.length==0);
    }
    catch (IOException | InterruptedException e) {
      TUL_LOGGER.error(e.getMessage());
    }
    
  }
}
