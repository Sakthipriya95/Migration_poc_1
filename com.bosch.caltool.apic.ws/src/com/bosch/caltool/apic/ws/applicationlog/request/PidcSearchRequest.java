/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.PidcSearchConditionType;

/**
 * @author imi2si
 * @since 1.19
 */
public class PidcSearchRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcSearchConditionType object
   * 
   * @param searchConditions The GetPidcDiffsType request object
   */
  public PidcSearchRequest(final PidcSearchConditionType... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final PidcSearchConditionType locRequest = (PidcSearchConditionType) requestObject;

    return "PidcSearchConditionType [localAttributeId=" + locRequest.getAttributeId() + ", localAttributeValueIds=" +
        Arrays.toString(locRequest.getAttributeValueIds()) + ", localUsedFlag=" + locRequest.getUsedFlag() + "]";
  }
}
