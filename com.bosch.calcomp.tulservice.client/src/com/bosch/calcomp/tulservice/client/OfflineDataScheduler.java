/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.tulservice.utils.OfflineDataFileHandler;
import com.bosch.calcomp.tulservice.utils.ToolUsageStatUtils;

/**
 * @author TAB1JA
 */
public class OfflineDataScheduler extends TimerTask {


  private static  OfflineDataScheduler offlineDataScheduler;


  private Timer timer = null;

  private String serviceUrl;

  private String fileDirectory;

  private ILoggerAdapter logger;

  /**
   * @return OfflineDataScheduler
   */
//  get Instance ---> enum value (Enum class)
  public static OfflineDataScheduler getInstance() {

    if (offlineDataScheduler == null) {
      // To make thread safe
      synchronized (OfflineDataScheduler.class) {
        // check again as multiple threads
        // can reach above step
        if (offlineDataScheduler == null) {
          offlineDataScheduler = new OfflineDataScheduler();
        }
      }
    }
    return offlineDataScheduler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {

      File[] listOfFiles = OfflineDataFileHandler.getListOfFiles(this.fileDirectory);
      if (listOfFiles!=null &&listOfFiles.length >= 1) {

        int i = 0;
        for (i = 0; i < listOfFiles.length; i++) {

          if(listOfFiles[i]!=null)
          {
            String result = ToolUsageStatUtils.constructDataString(listOfFiles[i].toString());
  
            ToolUsageLoggerClient toolUsageLoggerClient = new ToolUsageLoggerClient(this.logger);
            boolean isPostToolUsageSuccessful = toolUsageLoggerClient.postToolUsageStatJSON(result);
            
            if(isPostToolUsageSuccessful) {
              Files.deleteIfExists(listOfFiles[i].toPath());
              continue;
            }
            
            scheduleTimer(isPostToolUsageSuccessful, null, result, false, this.logger);
  
  //          index starts with 0 --> So the File length will be i+1
            if ((i + 1) == listOfFiles.length) {
              stopScheduler();
            }
          }
          else
          {
            continue;
          }
        }
      }
    }
    catch (Exception e) {
      this.logger.error("error while timer task", e);
    }

  }

  /**
   * @param isPostToolUsageSuccessful
   * @param filePath
   * @param jsonString
   * @param isFromInvoker Flag to identify if the call is from Tool Invoker or Timer
   * @param logger - TUL Logger
   * @throws IOException - Exception
   */
  public void scheduleTimer(final boolean isPostToolUsageSuccessful, final String filePath, final String jsonString,
      final boolean isFromInvoker, final ILoggerAdapter logger)
      throws IOException {
    if (isPostToolUsageSuccessful && !isFromInvoker) {
      OfflineDataFileHandler.deleteOfflineFiles();
      logger.info("Post call is successful and offline files are deleted.");
    }
    else if (!isPostToolUsageSuccessful && isFromInvoker) {
      OfflineDataFileHandler.appendOfflineData(jsonString, filePath);
      OfflineDataScheduler.getInstance().startScheduledTask(filePath, ToolUsageStatUtils.getResponseUrl(logger), logger);

      logger.info("Post call is not successful. Offline data is written to file.");
    }

  }


  /**
   * @param filePath file Directory
   * @param tulUrl Url for api
   * @param tulLogger logger
   */
  private void startScheduledTask(final String filePath, final String tulUrl, final ILoggerAdapter tulLogger) {

    if (this.timer == null) {
      this.setServiceUrl(tulUrl);
      this.logger = tulLogger;
      this.fileDirectory = filePath;
      this.timer = new Timer();
      this.timer.scheduleAtFixedRate(this, 30, 30000);

    }
  }


  /**
  *
  */
  private void stopScheduler() {

    if ((this.timer != null) && (OfflineDataFileHandler.getListOfFiles(this.fileDirectory).length < 1)) {
      this.timer.cancel();
      offlineDataScheduler = null;
    }
  }

  /**
   * @return the serviceUrl
   */
  public String getServiceUrl() {
    return serviceUrl;
  }

  /**
   * @param serviceUrl the serviceUrl to set
   */
  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }
}

