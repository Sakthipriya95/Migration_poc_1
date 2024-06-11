/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.internal.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calcomp.tulservice.utils.MessageConstants;

/**
 * @author GDH9COB
 *
 */
public class Context {
  
  private String toolLanguage;
  private String machine;
  private String network;
  private Integer cpuLoad;
  private Integer totalCores;
  private Integer usedCores;
  private Integer clockRate;
  private Integer freeRAM;
  private Integer totalRAM;
  private String[] storageType;
  private Integer[] totalStorage;
  private Integer[] freeStorage;
  private String osType;
  private String osVersion;
  private String osLanguage;

  /**
   * @return
   */
  public String getToolLanguage() {
    return toolLanguage;
  }

  /**
   * @param toolLanguage
   */
  public void setToolLanguage(String toolLanguage) {
    this.toolLanguage = toolLanguage;
  }

  /**
   * @return
   */
  public String getMachine() {
    return machine;
  }

  /**
   * @param machine
   */
  public void setMachine(String machine) {
    this.machine = machine;
  }

  /**
   * @return
   */
  public String getNetwork() {
    return network;
  }

  /**
   * @param network
   */
  public void setNetwork(String network) {
    this.network = network;
  }

  /**
   * @return
   */
  public Integer getCpuLoad() {
    return cpuLoad;
  }

  /**
   * @param cpuLoad
   */
  public void setCpuLoad(Integer cpuLoad) {
    this.cpuLoad = cpuLoad;
  }

  /**
   * @return
   */
  public Integer getTotalCores() {
    return totalCores;
  }

  /**
   * @param totalCores
   */
  public void setTotalCores(Integer totalCores) {
    this.totalCores = totalCores;
  }

  /**
   * @return
   */
  public Integer getUsedCores() {
    return usedCores;
  }

  /**
   * @param usedCores
   */
  public void setUsedCores(Integer usedCores) {
    this.usedCores = usedCores;
  }

  /**
   * @return
   */
  public Integer getClockRate() {
    return clockRate;
  }

  /**
   * @param clockRate
   */
  public void setClockRate(Integer clockRate) {
    this.clockRate = clockRate;
  }

  /**
   * @return
   */
  public Integer getFreeRAM() {
    return freeRAM;
  }

  /**
   * @param freeRAM
   */
  public void setFreeRAM(Integer freeRAM) {
    this.freeRAM = freeRAM;
  }

  /**
   * @return
   */
  public Integer getTotalRAM() {
    return totalRAM;
  }

  /**
   * @param totalRAM
   */
  public void setTotalRAM(Integer totalRAM) {
    this.totalRAM = totalRAM;
  }

  /**
   * @return
   */
  public String[] getStorageType() {
    return storageType;
  }

  /**
   * @param storageType
   */
  public void setStorageType(String[] storageType) {
    this.storageType = storageType;
  }

  /**
   * @return the totalStorage
   */
  public Integer[] getTotalStorage() {
    return totalStorage;
  }

  
  /**
   * @param totalStorage the totalStorage to set
   */
  public void setTotalStorage(Integer[] totalStorage) {
    this.totalStorage = totalStorage;
  }

  
  /**
   * @return the freeStorage
   */
  public Integer[] getFreeStorage() {
    return freeStorage;
  }

  
  /**
   * @param freeStorage the freeStorage to set
   */
  public void setFreeStorage(Integer[] freeStorage) {
    this.freeStorage = freeStorage;
  }

  /**
   * @return
   */
  public String getOsType() {
    return osType;
  }

  /**
   * @param osType
   */
  public void setOsType(String osType) {
    this.osType = osType;
  }

  /**
   * @return
   */
  public String getOsVersion() {
    return osVersion;
  }

  /**
   * @param osVersion
   */
  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  /**
   * @return
   */
  public String getOsLanguage() {
    return osLanguage;
  }

  /**
   * @param osLanguage
   */
  public void setOsLanguage(String osLanguage) {
    this.osLanguage = osLanguage;
  }
  
  /**
   * @return
   */
  private Map<Object, String> getContextMap() {
    Map<Object, String> parameterValueMap = new HashMap<>();
    parameterValueMap.put(toolLanguage, MessageConstants.TOOL_LANGUAGE);
    parameterValueMap.put(machine, MessageConstants.MACHINE_TYPE);
    parameterValueMap.put(network, MessageConstants.NETWORK_TYPE);
    parameterValueMap.put(cpuLoad, MessageConstants.CPULOAD);
    parameterValueMap.put(totalCores, MessageConstants.TOTAL_CORES);
    parameterValueMap.put(usedCores, MessageConstants.USED_CORES);
    parameterValueMap.put(clockRate, MessageConstants.CLOCK_RATE);
    parameterValueMap.put(freeRAM, MessageConstants.FREE_RAM);
    parameterValueMap.put(totalRAM, MessageConstants.TOTAL_RAM);
    parameterValueMap.put(getArrayAsObject(storageType), MessageConstants.STORAGE_TYPE);
    parameterValueMap.put(getArrayAsObject(totalStorage), MessageConstants.TOTAL_STORAGE);
    parameterValueMap.put(getArrayAsObject(freeStorage), MessageConstants.FREE_STORAGE );
    parameterValueMap.put(osType, MessageConstants.OS_TYPE );
    parameterValueMap.put(osVersion, MessageConstants.OS_VERSION );
    parameterValueMap.put(osLanguage, MessageConstants.OS_LANGUAGE );
    
    return parameterValueMap;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    Map<Object, String> parameterMap = getContextMap();
    StringBuilder stringBuilder = new StringBuilder("{");
    int size = parameterMap.size() - 1;
    int count = 0;
    
    for (Entry<Object, String> entrySet : parameterMap.entrySet()) {
      Object value = entrySet.getKey();
      String parameter = entrySet.getValue();

      if (value != null) {
        stringBuilder.append(parameter);
        stringBuilder.append(":");

        if (value instanceof String && 
            !parameter.startsWith(MessageConstants.STORAGE_TYPE) && 
            !parameter.startsWith(MessageConstants.TOTAL_STORAGE) && 
            !parameter.startsWith(MessageConstants.FREE_STORAGE)) {
          stringBuilder.append("\"");
          stringBuilder.append(value);
          stringBuilder.append("\"");
        }
        else {
          stringBuilder.append(value);
        }
        count++;

        if (count < size) {
          stringBuilder.append(",");
        }
      }
    }
    
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
  
  private Object getArrayAsObject(Object[] array) {
    
    StringBuilder dataBuilder = new StringBuilder();
    dataBuilder.append("[");
    if (array != null && array.length > 0) {
      for (int index = 0; index < array.length; index++) {
        Object value = array[index];
        if (value instanceof String) {
          dataBuilder.append("\"");
          dataBuilder.append(value);
          dataBuilder.append("\"");
        }
        else {
          dataBuilder.append(value);
        }

        if (index < array.length - 1) {
          dataBuilder.append(",");
        }

      }
    }
    
    dataBuilder.append("]");
    return dataBuilder.toString();
    
  }

  
}