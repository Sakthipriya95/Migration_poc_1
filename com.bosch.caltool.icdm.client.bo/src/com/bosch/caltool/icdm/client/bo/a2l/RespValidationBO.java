/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;

/**
 * @author apj4cob
 */
public class RespValidationBO {

  
  private static final String RESPONSIBILITY_NAME_IS_EMPTY = "Responsibility name is empty";
  private final WorkPkgResponsibilityBO wpRespBO;
  private static final int RESP_NAME_LENGTH = 50;


  private static final int RESP_DEPT_LENGTH = 30;

  /**
   * @param wpRespBO WorkPkgResponsibilityBO
   */
  public RespValidationBO(final WorkPkgResponsibilityBO wpRespBO) {
    this.wpRespBO = wpRespBO;
  }

  /**
   * To validate department name entered directly in nat table,for customer/other types
   *
   * @param deptName String
   * @param selA2lResp A2lResponsibility
   * @return error message
   */
  public String validateLDept(final String deptName, final A2lResponsibility selA2lResp) {
    String errorMsg = "";
    if (CommonUtils.isEmptyString(CommonUtils.checkNull(deptName)) &&
        CommonUtils.isEmptyString(selA2lResp.getLLastName()) && CommonUtils.isEmptyString(selA2lResp.getLFirstName())) {
      errorMsg = RESPONSIBILITY_NAME_IS_EMPTY;
    }
    if (isLDeptTooLong(CommonUtils.checkNull(deptName))) {
      errorMsg = "Allowed length is " + RESP_DEPT_LENGTH + ".";
    }
    return errorMsg;
  }

  /**
   * To validate last name directly entered in nat table;for customer/other types
   *
   * @param respName String
   * @param a2lResp A2lResponsibility
   * @return String
   */
  public String validateLLastName(final String respName, final A2lResponsibility a2lResp) {
    String errorMsg = "";
    a2lResp.setLLastName(respName);
    if (CommonUtils.isEmptyString(CommonUtils.checkNull(respName)) &&
        CommonUtils.isEmptyString(a2lResp.getLDepartment()) && CommonUtils.isEmptyString(a2lResp.getLFirstName())) {
      errorMsg = RESPONSIBILITY_NAME_IS_EMPTY;
    }
    if (isLNameDuplicate(a2lResp)) {
      errorMsg = "Responsibility name already exists.";
    }
    if (isLNameTooLong(respName)) {
      errorMsg = "Responsibility name is too long. Allowed length is " + RESP_NAME_LENGTH + ".";
    }
    return errorMsg;
  }


  /**
   * To validate first name directly entered in nat table for customer/other types
   *
   * @param respName String
   * @param a2lResp A2lResponsibility
   * @return String
   */
  public String validateLFirstName(final String respName, final A2lResponsibility a2lResp) {
    String errorMsg = "";
    a2lResp.setLFirstName(respName);
    if (CommonUtils.isEmptyString(CommonUtils.checkNull(respName)) &&
        CommonUtils.isEmptyString(a2lResp.getLDepartment()) && CommonUtils.isEmptyString(a2lResp.getLLastName())) {
      errorMsg = RESPONSIBILITY_NAME_IS_EMPTY;
    }
    if (isLNameDuplicate(a2lResp)) {
      errorMsg = "Responsibility name already exists.";
    }
    if (isLNameTooLong(respName)) {
      errorMsg = "Responsibility name is too long. Allowed length is " + RESP_NAME_LENGTH + ".";
    }
    return errorMsg;
  }

  /**
   * @param a2lResp1 A2lResponsibility object for which first/last name is being modified
   * @return boolean
   */
  public boolean isLNameDuplicate(final A2lResponsibility a2lResp1) {
    boolean flag = false;
    for (A2lResponsibility a2lResp2 : this.wpRespBO.getA2lRespModel().getA2lResponsibilityMap().values()) {
      if (CommonUtils.isNotEqual(a2lResp1, a2lResp2) && A2lResponsibilityCommon.isSame(a2lResp1, a2lResp2)) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  /**
   * @return the wpNameLength
   */
  public static int getRespNameLength() {
    return RESP_NAME_LENGTH;
  }


  /**
   * @param respName String
   * @return boolean
   */
  public boolean isLNameTooLong(final String respName) {
    return (CommonUtils.checkNull(respName).length() > RESP_NAME_LENGTH);

  }

  /**
   * @param deptName String
   * @return
   */
  private boolean isLDeptTooLong(final String deptName) {
    return (CommonUtils.checkNull(deptName).length() > RESP_DEPT_LENGTH);
  }

  /**
   * @param respName String
   * @return boolean
   */
  public boolean isLNameEmpty(final String respName) {
    return CommonUtils.isEmptyString(CommonUtils.checkNull(respName));
  }
}
