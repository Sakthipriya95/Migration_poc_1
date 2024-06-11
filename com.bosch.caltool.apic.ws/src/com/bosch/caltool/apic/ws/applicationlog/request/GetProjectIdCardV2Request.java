/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.GetProjectIdCardV2;


/**
 * Represents a textual output of the PidcAccessRightRequest request of a users session.
 *
 * @author svj7cob
 * @since 1.19
 */
public class GetProjectIdCardV2Request extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   *
   * @param searchConditions The PidcAccessRightType request object
   */
  public GetProjectIdCardV2Request(final GetProjectIdCardV2... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final GetProjectIdCardV2 locRequest = (GetProjectIdCardV2) requestObject;

    return "GetProjectIdCard [localSessID=" + locRequest.getSessID() + ", localProjectIdCardID=" +
        locRequest.getProjectIdCardID() + ", localUseCaseID=" + Arrays.toString(locRequest.getUseCaseID()) + "]";
  }
}
