/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;

import java.util.Collection;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;

/**
 * @author dmo5cob
 */

public interface IMapperChangeData {

  /**
   * Mapper to convert to collection of ChangeData
   *
   * @param data
   * @return mapped data
   */
  Collection<ChangeData<IModel>> map(Object data);


}
