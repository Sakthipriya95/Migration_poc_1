/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.utils;

import java.lang.management.ManagementFactory;
import java.util.Locale;

import com.bosch.calcomp.tulservice.internal.model.MachineType;
import com.sun.management.OperatingSystemMXBean;

import oshi.SystemInfo;


/**
 * @author GDH9COB
 *
 */
public final class SystemInfoUtil {
  
  private static final SystemInfoUtil SINGLETON_INSTANCE = new SystemInfoUtil();
  private final SystemInfo systemInfo = new SystemInfo();
  private final OperatingSystemMXBean operatingSystemMXBean =
      ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

  private SystemInfoUtil() {}

  /**
   * @return
   */
  public static SystemInfoUtil getInstance() {
    return SINGLETON_INSTANCE;
  }

  /**
   * @return
   */
  public int getAvailableProcessors() {
    return operatingSystemMXBean.getAvailableProcessors();
  }

  /**
   * @return
   */
  public int getClockRate() {
    return (int) (getSystemInfo().getHardware().getProcessor().getMaxFreq() / 1000000L);
  }

  /**
   * @return
   */
  public int getCpuLoad() {
    int processCpuLoad = (int) (operatingSystemMXBean.getSystemCpuLoad() * 100D);
    return processCpuLoad <= 0
        ? (int) (getSystemInfo().getHardware().getProcessor()
            .getSystemCpuLoadBetweenTicks(new long[oshi.hardware.CentralProcessor.TickType.values().length]) * 100D)
        : processCpuLoad;
  }

  /**
   * @return
   */
  public String getName() {
    return getSystemInfo().getOperatingSystem().getFamily();
  }

  /**
   * @return
   */
  public String getVersion() {
    return getSystemInfo().getOperatingSystem().getVersionInfo().toString();
  }

  /**
   * @return
   */
  public String getLanguage() {
    return Locale.getDefault().getLanguage();
  }

  /**
   * @return
   */
  public int getFreeRamSize() {
    return convertBytesToMegaBytes(operatingSystemMXBean.getFreePhysicalMemorySize());
  }

  /**
   * @return
   */
  public int getTotalRamSize() {
    return convertBytesToMegaBytes(operatingSystemMXBean.getTotalPhysicalMemorySize());
  }

  /**
   * 
   * @param noOfBytes
   * @return
   */
  private int convertBytesToMegaBytes(long noOfBytes) {
    return (int) (noOfBytes / 1048576L);
  }

  /**
   * @return
   */
  public MachineType getMachineType() {
    String hostName = getSystemInfo().getOperatingSystem().getNetworkParams().getHostName();
    if (hostName.toLowerCase().endsWith("vm")) {
      return MachineType.LOCAL_VM;
    }
    return MachineType.PC;
  }

  /**
   * @return
   */
  public SystemInfo getSystemInfo() {
    return systemInfo;
  }

}
