/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.GetAttributeValues;


/**
 * Represents a textual output of the PidcAccessRightRequest request of a users session.
 *
 * @author imi2si
 * @since 1.19
 */
public class GetAttributeValuesRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   *
   * @param searchConditions The PidcAccessRightType request object
   */
  public GetAttributeValuesRequest(final GetAttributeValues... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final GetAttributeValues locRequest = (GetAttributeValues) requestObject;

    return "GetAttributeValues [localSessID=" + locRequest.getSessID() + ", localAttributeIDs=" +
        Arrays.toString(locRequest.getAttributeIDs()) + "]";
  }
}
