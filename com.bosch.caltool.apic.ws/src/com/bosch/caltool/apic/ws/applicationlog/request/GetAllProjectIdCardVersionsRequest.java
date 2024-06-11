/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import com.bosch.caltool.apic.ws.AllProjectIdCardVersType;
import com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions;


/**
 * Represents a textual output of the PidcAccessRightRequest request of a users session.
 *
 * @author imi2si
 * @since 1.19
 */
public class GetAllProjectIdCardVersionsRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   *
   * @param searchConditions The PidcAccessRightType request object
   */
  public GetAllProjectIdCardVersionsRequest(final GetAllProjectIdCardVersions... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final GetAllProjectIdCardVersions locRequest = (GetAllProjectIdCardVersions) requestObject;
    final AllProjectIdCardVersType pidcVersion = locRequest.getGetAllProjectIdCardVersions();

    return "AllProjectIdCardVersType [localSessID=" + pidcVersion.getSessID() + "]";


  }
}
