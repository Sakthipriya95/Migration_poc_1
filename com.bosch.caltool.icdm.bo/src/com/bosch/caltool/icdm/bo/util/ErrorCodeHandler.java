/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.util;

import java.util.Locale;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author and4cob
 */
public class ErrorCodeHandler extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData ServiceData
   */
  public ErrorCodeHandler(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param exp IcdmException
   * @return error message
   */
  public String getErrorMessage(final IcdmException exp) {
    String errorCode = exp.getErrorCode();

    if (isValidErrorMessageFormat(errorCode)) {
      String[] aErrCodeInfo = errorCode.split("\\.");
      String icdmMsg = null;
      if (aErrCodeInfo.length == 2) {
        icdmMsg = new MessageLoader(getServiceData()).getMessage(aErrCodeInfo[0], aErrCodeInfo[1], exp.getVarArgs());
      }
      icdmMsg = CommonUtils.checkNull(icdmMsg).trim();

      // If message could not be identified from error code, set the message as error code itself
      // Reason : message of error code not found in T_MESSAGES table
      if (CommonUtils.isEmptyString(icdmMsg)) {
        icdmMsg = errorCode;
      }
      return icdmMsg;
    }
    return CommonUtils.isEmptyString(errorCode) ? exp.getMessage() : errorCode;
  }

  /**
   * @param input error code
   * @return whether the eroor code is in valid format or not
   */
  public static boolean isValidErrorMessageFormat(final String input) {
    if (CommonUtils.isEmptyString(input)) {
      return false;
    }
    String errCode = input.toUpperCase(Locale.ENGLISH);
    return !errCode.contains(" ") && errCode.equals(input) && (errCode.split("\\.").length == 2);
  }
}
