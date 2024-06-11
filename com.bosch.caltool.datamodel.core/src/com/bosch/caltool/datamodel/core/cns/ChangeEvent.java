/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core.cns;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.datamodel.core.IModelType;

/**
 * @author bne4cob
 */
public class ChangeEvent implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -885860352789135308L;

  private long changeId;

  private String serviceId;

  private String clientSessionId;

  /**
   * Key - {@link IModelType#getTypeCode()}, Value data - Map - key : primary key, value - change data
   */
  private HashMap<String, Map<Long, ChangeData<?>>> changeDataMap = new HashMap<>();

  /**
   * @return the changeId
   */
  public long getChangeId() {
    return this.changeId;
  }

  /**
   * @param changeId the changeId to set
   */
  public void setChangeId(final long changeId) {
    this.changeId = changeId;
  }

  /**
   * @return the serviceId
   */
  public String getServiceId() {
    return this.serviceId;
  }


  /**
   * @param serviceId the serviceId to set
   */
  public void setServiceId(final String serviceId) {
    this.serviceId = serviceId;
  }

  /**
   * @return the clientSessionId
   */
  public String getClientSessionId() {
    return this.clientSessionId;
  }

  /**
   * @param clientSessionId the clientSessionId to set
   */
  public void setClientSessionId(final String clientSessionId) {
    this.clientSessionId = clientSessionId;
  }

  /**
   * @return the changeDataMap
   */
  public Map<String, Map<Long, ChangeData<?>>> getChangeDataMap() {
    return this.changeDataMap;
  }

  /**
   * @param changeDataMap the changeDataMap to set
   */
  public void setChangeDataMap(final Map<String, Map<Long, ChangeData<?>>> changeDataMap) {
    this.changeDataMap = new HashMap<>(changeDataMap);
  }


}
