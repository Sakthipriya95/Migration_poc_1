/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;

import java.io.Serializable;

/**
 * @author bne4cob
 */
public final class ModelInfo implements Serializable {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = 4657584065078570753L;

  private Long modelId;
  private IModelType type;

  /**
   * Empty constructor
   */
  public ModelInfo() {
    // Empty constructor - Mandatory, to support json conversion
    super();
  }

  /**
   * @param modelId model ID
   * @param type model type
   */
  public ModelInfo(final Long modelId, final IModelType type) {
    super();
    this.modelId = modelId;
    this.type = type;
  }

  /**
   * @return the modelId
   */
  public Long getModelId() {
    return this.modelId;
  }

  /**
   * @param modelId the modelId to set
   */
  public void setModelId(final Long modelId) {
    this.modelId = modelId;
  }

  /**
   * @return the type
   */
  public IModelType getType() {
    return this.type;
  }

  /**
   * @param type the type to set
   */
  public void setType(final IModelType type) {
    this.type = type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [modelId=" + this.modelId + ", type=" + this.type + "]";
  }


}
