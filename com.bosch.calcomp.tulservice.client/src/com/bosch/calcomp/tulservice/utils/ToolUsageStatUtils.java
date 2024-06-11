/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.tulservice.exception.ToolUsageLoggingException;
import com.bosch.calcomp.tulservice.internal.model.Context;
import com.bosch.calcomp.tulservice.internal.model.ToolUsageStat;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;

/**
 * Utility class to get ToolUsageStat object
 * 
 * @author GDH9COB
 */
public class ToolUsageStatUtils {

  private static final ToolUsageStatUtils SINGLETON_INSTANCE = new ToolUsageStatUtils();

  private static PasswordService passwordService = new PasswordService();

  private static final String TUL_POST_KEY = "TUL.POST_ENDPOINT_URL";

  private ToolUsageStatUtils() {}

  /**
   * @return Singleton instance of ToolUsageStatUtils
   */
  public static ToolUsageStatUtils getInstance() {
    return SINGLETON_INSTANCE;
  }

  /**
   * @return an instance of ToolUsageStat
   */
  public ToolUsageStat createEmptyToolUsageStat() {
    return new ToolUsageStat();
  }

  /**
   * @return an instance of ToolUsageStat with prefilled system data
   */
  public ToolUsageStat createPreFilledToolUsageStat() {
    ToolUsageStat usageStat = createEmptyToolUsageStat();
    usageStat.setTimestamp(getTimeStampInSeconds());
    usageStat.setContext(getSystemContext());

    return usageStat;
  }

  /**
   * @return Context
   */
  public Context getSystemContext() {

    SystemInfoUtil systemInfoInstance = SystemInfoUtil.getInstance();
    Context context = new Context();
    context.setOsType(systemInfoInstance.getName().toLowerCase());
    context.setOsVersion(systemInfoInstance.getVersion());
    context.setOsLanguage(systemInfoInstance.getLanguage());
    context.setTotalCores(systemInfoInstance.getAvailableProcessors());
    context.setCpuLoad(systemInfoInstance.getCpuLoad());
    context.setClockRate(systemInfoInstance.getClockRate());
    context.setFreeRAM(systemInfoInstance.getFreeRamSize());
    context.setTotalRAM(systemInfoInstance.getTotalRamSize());
    context.setMachine(systemInfoInstance.getMachineType().name());

    return context;
  }

  /**
   * @return time stamp in seconds
   */
  private long getTimeStampInSeconds() {
    return TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
  }

  /**
   * @param toolUsageStatList Tool Usage Stat List
   * @return serialized JSON String
   */
  public static String constructJSONString(List<ToolUsageStat> toolUsageStatList) {

    try {
      return JacksonHelper.getJacksonObjectMapper().writeValueAsString(toolUsageStatList);
    }
    catch (Exception e) {
      throw new ToolUsageLoggingException(e.getMessage());
    }
  }

  /**
   * Connect to the password WS to get the Url to fetch the compli labels
   * 
   * @return value for the key
   */
  public static String getResponseUrl(ILoggerAdapter logger) {
    try {
      return passwordService.getPassword(TUL_POST_KEY);
    }
    catch (PasswordNotFoundException e) {
      logger.error(e.getMessage());
      return "";
    }
    
  }
  
  
  
  public static String constructDataString(String filename)
  {
     StringBuilder jsonBuilder = new StringBuilder();
    
    try {
      jsonBuilder.append(new String(Files.readAllBytes(Paths.get(filename))));
    }
    catch (IOException e) {
      throw new ToolUsageLoggingException(e.getMessage());
    }
    return jsonBuilder.toString();
  
  }
}
