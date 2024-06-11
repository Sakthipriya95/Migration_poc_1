/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import com.bosch.caltool.apic.ws.GetPidcDiffsType;


/**
 * Represents a textual output of the GetPidcDiffsType request of a users session.
 * 
 * @author imi2si
 * @since 1.19
 */
public class PidcDiffRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n GetPidcDiffsType object
   * 
   * @param searchConditions The GetPidcDiffsType request object
   */
  public PidcDiffRequest(final GetPidcDiffsType... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final GetPidcDiffsType locRequest = (GetPidcDiffsType) requestObject;


    return "GetPidcDiffsType [localPidcID=" + locRequest.getPidcID() + ", localOldPidcChangeNumber=" +
        locRequest.getOldPidcChangeNumber() + ", localNewPidcChangeNumber=" + locRequest.getNewPidcChangeNumber() + "]";
  }
}
