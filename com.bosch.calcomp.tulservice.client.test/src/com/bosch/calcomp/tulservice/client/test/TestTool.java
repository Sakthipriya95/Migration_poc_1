/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.client.test;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.tulservice.client.ToolUsageLoggerClient;
import com.bosch.calcomp.tulservice.internal.model.ToolName;
import com.bosch.calcomp.tulservice.model.ToolLogData;

/**
 * @author GDH9COB
 *
 */
public class TestTool implements Callable<Boolean> {
  
  private String taskName;
  private final ILoggerAdapter tulLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("ToolUsageClient"));

  
  /**
   * @param name
   */
  public TestTool(String name)
  {
      this.taskName = name;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public Boolean call() throws Exception {
    
    ToolLogData toolData = new ToolLogData(System.getProperty("user.name"), "processing", ToolName.CHECKSSD.getTool(), "V3.1.0");
    toolData.setFeature("One File Check");
    toolData.setMisc("Test call 1 for Cal Tools");

    ToolUsageLoggerClient client = new ToolUsageLoggerClient(tulLogger);
    return client.postToolUsageData(toolData);
  }

}
