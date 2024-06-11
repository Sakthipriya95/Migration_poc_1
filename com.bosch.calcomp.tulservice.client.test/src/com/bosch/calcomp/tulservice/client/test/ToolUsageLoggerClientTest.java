/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.client.test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.tulservice.client.ToolUsageLoggerClient;
import com.bosch.calcomp.tulservice.internal.model.ToolCategory;
import com.bosch.calcomp.tulservice.internal.model.ToolEvents;
import com.bosch.calcomp.tulservice.internal.model.ToolName;
import com.bosch.calcomp.tulservice.internal.model.ToolUsageStat;
import com.bosch.calcomp.tulservice.model.ToolLogData;
import com.bosch.calcomp.tulservice.utils.ToolUsageStatUtils;


/**
 * @author GDH9COB
 */
public class ToolUsageLoggerClientTest {
  
  private final ILoggerAdapter tulLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("ToolUsageClient"));

  private ToolLogData toolData;
  private final ToolUsageLoggerClient client = new ToolUsageLoggerClient(tulLogger);

  /**
   * 
   */
  @Rule
  public Timeout globalTimeOut = Timeout.seconds(5000);

  /**
   */
  @Before
  public void setToolUsageStat() {

    this.toolData = new ToolLogData(System.getProperty("user.name"), ToolCategory.PROCESSING,
        ToolName.CHECKSSD.getTool(), "V3.1.0", ToolEvents.STARTED);
    this.toolData.setFeature("One File Check");
    this.toolData.setMisc("Test call 1 for Cal Tools");
    this.toolData.setJobID(UUID.randomUUID().toString());

  }

  /**
   * Test method for
   */

  @Test(expected = NullPointerException.class)
  public void givenNullDataNPEThrown() {
    this.client.postToolUsageData(null);
  }

  /**
   * Positive testcase when data is given data should be updated
   */
  @Test
  public void givenToolDataOkResponseForSuccess() {
   
     assertTrue(this.client.postToolUsageData(this.toolData));


  }

  /**
   * Test incomplete json input
   */
  @Test
  public void incompleteJsonInputTest() {
    ToolUsageStat toolUsageStat=ToolUsageStatUtils.getInstance().createPreFilledToolUsageStat();
    String json = ToolUsageStatUtils.constructJSONString(Arrays.asList(toolUsageStat));

   assertFalse( this.client.postToolUsageStatJSON(json));
  }


  /**
   *
   */

  @Test
  public void givenMultipleToolDataOkResponseForSuccess() {
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(3);

      TestTool t1 = new TestTool("CheckSSD1");
      TestTool t2 = new TestTool("CheckSSD2");
      TestTool t3 = new TestTool("CheckSSD3");
      TestTool t4 = new TestTool("CheckSSD4");
      TestTool t5 = new TestTool("CheckSSD5");

      Future<Boolean> future1 = executorService.submit(t1);
      Future<Boolean> future2 = executorService.submit(t2);
      Future<Boolean> future3 = executorService.submit(t3);
      Future<Boolean> future4 = executorService.submit(t4);
      Future<Boolean> future5 = executorService.submit(t5);

      assertTrue(future1.get());
      assertTrue(future2.get());
      assertTrue(future3.get());
      assertTrue(future4.get());
      assertTrue(future5.get());

      executorService.shutdown();

    }
    catch (Exception e) {
     tulLogger.error(e.getMessage());
    }

  }


}
