/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;

import java.util.Collection;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author bne4cob
 */
@FunctionalInterface
public interface IMapper {

  /**
   * @param data
   * @return mapped data
   */
  Collection<IModel> map(Object data);
}
