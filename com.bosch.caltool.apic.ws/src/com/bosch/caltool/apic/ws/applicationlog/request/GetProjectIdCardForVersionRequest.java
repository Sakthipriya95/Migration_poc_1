/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;

import java.util.Arrays;

import com.bosch.caltool.apic.ws.GetProjectIdCardForVersion;
import com.bosch.caltool.apic.ws.ProjectIdCardVersType;

/**
 * @author imi2si
 */
public class GetProjectIdCardForVersionRequest extends AbstractRequest {

  /**
   * Default constructor which acceptes 1:n PidcAccessRightRequest object
   *
   * @param searchConditions The PidcAccessRightType request object
   */
  public GetProjectIdCardForVersionRequest(final GetProjectIdCardForVersion... searchConditions) {
    super((Object[]) searchConditions);
  }

  /**
   * {@inheritDoc}
   */
  
  
  @Override
  protected final String getSingleRequestString(final Object requestObject) {
    // method to return Request in a string
    final GetProjectIdCardForVersion locRequest = (GetProjectIdCardForVersion) requestObject;
    // fetch the project id card for the version
    final ProjectIdCardVersType pidcVersion = locRequest.getGetProjectIdCardForVersion();

    return "GetProjectIdCardForVersion [localSessId=" + pidcVersion.getSessId() + ", localProjectIdCardId=" +
        pidcVersion.getProjectIdCardId() + ", localPidcVersionId=" + pidcVersion.getPidcVersionId() +
        ", localUseCaseID=" + Arrays.toString(pidcVersion.getUseCaseID()) + "]";
  }
}