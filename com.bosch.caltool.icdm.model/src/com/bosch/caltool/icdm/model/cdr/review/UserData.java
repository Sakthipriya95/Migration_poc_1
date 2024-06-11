/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bru2cob
 */
public class UserData {

  /**
   * selected calibration eng id
   */
  private Long selCalEngineerId;
  /**
   * selected author id
   */
  private Long selAuditorId;
  /**
   * Set of selected participants id
   */
  private Set<Long> selParticipantsIds;

  /**
   * @return the selCalEngineerId
   */
  public Long getSelCalEngineerId() {
    return this.selCalEngineerId;
  }

  /**
   * @param selCalEngineerId the selCalEngineerId to set
   */
  public void setSelCalEngineerId(final Long selCalEngineerId) {
    this.selCalEngineerId = selCalEngineerId;
  }

  /**
   * @return the selAuditorId
   */
  public Long getSelAuditorId() {
    return this.selAuditorId;
  }

  /**
   * @param selAuditorId the selAuditorId to set
   */
  public void setSelAuditorId(final Long selAuditorId) {
    this.selAuditorId = selAuditorId;
  }

  /**
   * @return the selParticipantsIds
   */
  public Set<Long> getSelParticipantsIds() {
    return this.selParticipantsIds;
  }

  /**
   * @param selParticipantsIds the selParticipantsIds to set
   */
  public void setSelParticipantsIds(final Set<Long> selParticipantsIds) {
    this.selParticipantsIds = selParticipantsIds == null ? null : new HashSet<>(selParticipantsIds);
  }

}
