/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.tul;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.tulservice.client.ToolUsageLoggerClient;
import com.bosch.calcomp.tulservice.internal.model.ToolCategory;
import com.bosch.calcomp.tulservice.internal.model.ToolEvents;
import com.bosch.calcomp.tulservice.model.ToolLogData;
import com.bosch.caltool.icdm.common.bo.tul.TULConstants.TUL_FEATURE;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author TRL1COB
 */
public abstract class AbstractIcdmTULInvoker {

  /**
   * Unique ID to identify the start and end of TUL invoker
   */
  private final String jobID;
  /**
   * Name of the tool that invoked TUL
   */
  private final String tool;
  /**
   * Category of tool that invoked TUL
   */
  private final String toolCategory;
  /**
   * Feature of the tool that invoked TUL
   */
  private final String toolFeature;
  /**
   * Version of the tool
   */
  protected String toolVersion;
  /**
   * Tool component that invoked TUL
   */
  protected String toolComponent;
  /**
   * NT-ID of the user
   */
  protected String userName;

  private final ILoggerAdapter tulLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("ToolUsageClient"));

  /**
   * @param toolCategory2 Category of the tool
   * @param toolFeature Feature of the tool
   */
  public AbstractIcdmTULInvoker(final ToolCategory toolCategory2, final TUL_FEATURE toolFeature) {
    this.jobID = UUID.randomUUID().toString();
    this.tool = TULConstants.ICDM_TOOL;
    this.toolCategory = toolCategory2.name();
    this.toolFeature = toolFeature.getName();
  }


  /**
   * Frame ToolLogData Model and invoke post call to TUL client
   *
   * @param event Name of the event being logged into TUL
   * @param artifactInfoList Information on the artifacts that was created by the tool
   * @param misc Additional information on the tool that invoked TUL
   */
  public void postToolData(final ToolEvents event, final List<String> artifactInfoList, final String misc) {
    ToolLogData toolLogData = new ToolLogData();
    toolLogData.setJobID(this.jobID);
    toolLogData.setUsername(this.userName);
    toolLogData.setTool(this.tool);
    toolLogData.setToolCategory(this.toolCategory);
    toolLogData.setToolVersion(this.toolVersion);
    toolLogData.setComponent(this.toolComponent);
    toolLogData.setFeature(this.toolFeature);
    toolLogData.setEvent(event.name());
    if (CommonUtils.isNotEmpty(artifactInfoList)) {
      // convert the list of artifact names into comma separated string
      StringBuilder artifactInfo = new StringBuilder("Files created: ");
      artifactInfo.append(artifactInfoList.stream().collect(Collectors.joining(", ")));
      toolLogData.setArtifactInfo(artifactInfo.toString());
    }
    toolLogData.setMisc(misc);
    invokePostCall(toolLogData);
  }


  /**
   * Invoke post call to TUL client
   *
   * @param toolData ToolLogData model that contains information on data that needs to be logged
   */
  private void invokePostCall(final ToolLogData toolData) {
    ToolUsageLoggerClient client = new ToolUsageLoggerClient(this.tulLogger);
    client.postToolUsageData(toolData);
  }

}
