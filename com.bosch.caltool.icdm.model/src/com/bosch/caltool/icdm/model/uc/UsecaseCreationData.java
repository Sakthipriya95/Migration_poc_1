/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;


/**
 * @author mkl2cob
 */
public class UsecaseCreationData {

  private UseCase usecase;

  private Long ownerId;

  /**
   * @return the usecase
   */
  public UseCase getUsecase() {
    return usecase;
  }

  /**
   * @param usecase the usecase to set
   */
  public void setUsecase(UseCase usecase) {
    this.usecase = usecase;
  }

  /**
   * @return the ownerId
   */
  public Long getOwnerId() {
    return ownerId;
  }

  /**
   * @param ownerId the ownerId to set
   */
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }
}
