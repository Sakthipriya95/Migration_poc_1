/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;

/**
 * @author bne4cob
 */
public class DisplayChangeEvent {

  private CHANGE_SOURCE source;

  private Map<IModelType, Map<Long, ChangeData<?>>> consChangeDataMap = new HashMap<>();

  /**
   * @return the consChangeDataMap
   */
  public Map<IModelType, Map<Long, ChangeData<?>>> getConsChangeData() {
    return this.consChangeDataMap;
  }

  /**
   * @param consChangeDataMap the consChangeDataMap to set
   */
  public void setConsChangeData(final Map<IModelType, Map<Long, ChangeData<?>>> consChangeDataMap) {
    this.consChangeDataMap = consChangeDataMap;
  }

  /**
   * @return the source
   */
  public CHANGE_SOURCE getSource() {
    return this.source;
  }

  /**
   * @param source the source to set
   */
  public void setSource(final CHANGE_SOURCE source) {
    this.source = source;
  }
}
