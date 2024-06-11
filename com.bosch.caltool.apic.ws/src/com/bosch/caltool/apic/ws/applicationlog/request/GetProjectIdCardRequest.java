/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.GetProjectIdCard;


/**
 * Represents a textual output of the PidcAccessRightRequest request of a users session.
 *
 * @author imi2si
 * @since 1.19
 */
public class GetProjectIdCardRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   *
   * @param searchConditions The PidcAccessRightType request object
   */
  public GetProjectIdCardRequest(final GetProjectIdCard... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final GetProjectIdCard locRequest = (GetProjectIdCard) requestObject;

    return "GetProjectIdCard [localSessID=" + locRequest.getSessID() + ", localProjectIdCardID=" +
        locRequest.getProjectIdCardID() + ", localUseCaseID=" + Arrays.toString(locRequest.getUseCaseID()) + "]";
  }
}
