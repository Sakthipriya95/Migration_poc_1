/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author bne4cob
 * @param <D> data created type
 */
public class DataCreationModel<D extends IModel> {

  private D dataCreated;
  private NodeAccess nodeAccess;

  /**
   * @return the dataCreated
   */
  public D getDataCreated() {
    return this.dataCreated;
  }

  /**
   * @param dataCreated the dataCreated to set
   */
  public void setDataCreated(final D dataCreated) {
    this.dataCreated = dataCreated;
  }

  /**
   * @return the nodeAccess
   */
  public NodeAccess getNodeAccess() {
    return this.nodeAccess;
  }

  /**
   * @param nodeAccess the nodeAccess to set
   */
  public void setNodeAccess(final NodeAccess nodeAccess) {
    this.nodeAccess = nodeAccess;
  }
}

