/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.PidcAccessRightType;


/**
 * Represents a textual output of the PidcAccessRightRequest request of a users session.
 * 
 * @author imi2si
 * @since 1.19
 */
public class PidcAccessRightRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   * 
   * @param searchConditions The PidcAccessRightType request object
   */
  public PidcAccessRightRequest(final PidcAccessRightType... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final PidcAccessRightType locRequest = (PidcAccessRightType) requestObject;


    return "PidcAccessRightType [localPidcId=" + Arrays.toString(locRequest.getPidcId()) + ", localShowGrant=" +
        locRequest.getShowGrant() + ", localShowOwner=" + locRequest.getShowOwner() + ", localShowWrite=" +
        locRequest.getShowWrite() + "]";
  }
}
