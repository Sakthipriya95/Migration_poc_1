/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.AllPidcDiffType;


/**
 * Represents a textual output of the AllPidcDiffRequest request of a users session.
 * 
 * @author imi2si
 * @since 1.19
 */
public class AllPidcDiffRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n AllPidcDiffType object
   * 
   * @param searchConditions The AllPidcDiffType request object
   */
  public AllPidcDiffRequest(final AllPidcDiffType... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    final AllPidcDiffType locRequest = (AllPidcDiffType) requestObject;

    return "AllPidcDiffType [localAttributeId=" + Arrays.toString(locRequest.getAttributeId()) + ", localPidcID=" +
        locRequest.getPidcID() + ", localOldPidcChangeNumber=" + locRequest.getOldPidcChangeNumber() +
        ", localNewPidcChangeNumber=" + locRequest.getNewPidcChangeNumber() + "]";
  }
}
