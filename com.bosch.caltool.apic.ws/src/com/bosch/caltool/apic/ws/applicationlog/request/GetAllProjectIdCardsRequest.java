/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import com.bosch.caltool.apic.ws.GetAllProjectIdCards;


/**
 * Represents a textual output of the PidcAccessRightRequest request of a users session.
 *
 * @author imi2si
 * @since 1.19
 */
public class GetAllProjectIdCardsRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   *
   * @param searchConditions The PidcAccessRightType request object
   */
  public GetAllProjectIdCardsRequest(final GetAllProjectIdCards... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final GetAllProjectIdCards locRequest = (GetAllProjectIdCards) requestObject;

    return "GetAllProjectIdCards [localSessID=" + locRequest.getSessID() + "]";
  }
}
