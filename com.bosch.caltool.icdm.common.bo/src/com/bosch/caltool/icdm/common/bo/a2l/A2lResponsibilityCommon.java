/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;

/**
 * @author bne4cob
 */
public final class A2lResponsibilityCommon {

  private A2lResponsibilityCommon() {
    // private constructor to avoid instant creation. Remove this later, if required.
  }

  /**
   * Checks if the input is a default responsibility.
   *
   * @param a2lResp A2l Responsibility
   * @return true, if is default responsibility
   */
  public static final boolean isDefaultResponsibility(final A2lResponsibility a2lResp) {
    if (null != a2lResp) {
      return WpRespType.RB.getAliasBase().equals(a2lResp.getAliasName()) ||
          WpRespType.CUSTOMER.getAliasBase().equals(a2lResp.getAliasName()) ||
          WpRespType.OTHERS.getAliasBase().equals(a2lResp.getAliasName());
    }
    return false;
  }

  /**
   * Get the responsibility type for the given responsibility
   *
   * @param a2lResp A2lResponsibility
   * @return WpRespEnum type
   */
  public static final WpRespType getRespType(final A2lResponsibility a2lResp) {
    return a2lResp == null ? null : WpRespType.getType(a2lResp.getRespType());
  }

  /**
   * Business logic level responsiblity check. NOTE : This is different from A2lResponsibility.isEqual() method.
   * <p>
   * Both inputs should NOT be <code>null</code>.
   *
   * @param resp1 A2lResponsibility 1
   * @param resp2 A2lResponsibility 2
   * @return if the two responsibility objects are same
   */
  public static final boolean isSame(final A2lResponsibility resp1, final A2lResponsibility resp2) {
    // If primary key is already available and primary key are same, then objects are same
    if ((resp1.getId() != null)  && CommonUtils.isEqual(resp1, resp2)) {
      return true;
    }
    // If resp type is same, then check further. else, objects are different
    if (CommonUtils.isNotEmptyString(resp1.getRespType()) && CommonUtils.isEqual(resp1.getRespType(), resp2.getRespType())) {
      return isRespTypeOrAliasNameSame(resp1, resp2);
    }

    return false;
  }

  /**
   * @param resp1 A2lResponsibility
   * @param resp2 A2lResponsibility
   * @return boolean
   */
  private static boolean isRespTypeOrAliasNameSame(final A2lResponsibility resp1, final A2lResponsibility resp2) {
    // If both records are default responsibility, they are same
    if (isDefaultResponsibility(resp1) && isDefaultResponsibility(resp2)) {
      return true;
    }
    // If alias name is available and are same, then both objects are same
    String als1 = CommonUtils.checkNull(resp1.getAliasName());
    String als2 = CommonUtils.checkNull(resp2.getAliasName());
    if (CommonUtils.isNotEmptyString(als1) && als1.equalsIgnoreCase(als2)) {
      return true;
    }
    // If type=BOSCH, objects are same if user id is same
    if (WpRespType.RB.getCode().equals(resp1.getRespType())) {
      return (resp1.getUserId() != null) && resp1.getUserId().equals(resp2.getUserId());
    }

    // for other types, objects are same if combination of first name and last name is same provided atleast one of
    // them is set
    return isSameFnLn(resp1, resp2);
  }

  /**
   * @param a2lResp1 A2lResponsibility 1
   * @param a2lResp2 A2lResponsibility 2
   * @return if the two responsibility objects are same
   */
  public static boolean isCutomerAndOtherRespSame(final A2lResponsibility a2lResp1, final A2lResponsibility a2lResp2) {

    String respType1 = CommonUtils.checkNull(a2lResp1.getRespType());
    String respType2 = CommonUtils.checkNull(a2lResp2.getRespType());
    String depart1 = CommonUtils.checkNull(a2lResp1.getLDepartment());
    String depart2 = CommonUtils.checkNull(a2lResp2.getLDepartment());
    String fName1 = CommonUtils.checkNull(a2lResp1.getLFirstName());
    String fName2 = CommonUtils.checkNull(a2lResp2.getLFirstName());
    String lName1 = CommonUtils.checkNull(a2lResp1.getLLastName());
    String lName2 = CommonUtils.checkNull(a2lResp2.getLLastName());

    return respType1.equalsIgnoreCase(respType2) && depart1.equalsIgnoreCase(depart2) &&
        fName1.equalsIgnoreCase(fName2) && lName1.equalsIgnoreCase(lName2);
  }

  /**
   * @param resp1
   * @param resp2
   */
  private static boolean isSameFnLn(final A2lResponsibility resp1, final A2lResponsibility resp2) {
    String fn1 = CommonUtils.checkNull(resp1.getLFirstName());
    String ln1 = CommonUtils.checkNull(resp1.getLLastName());

    if (CommonUtils.isNotEmptyString(fn1) || CommonUtils.isNotEmptyString(ln1)) {
      // Uniqueness is combination of First name and last name
      String unique1 = fn1 + ":" + ln1;
      String unique2 = CommonUtils.checkNull(resp2.getLFirstName()) + ":" + CommonUtils.checkNull(resp2.getLLastName());

      return unique1.equalsIgnoreCase(unique2);
    }
    return false;
  }

}
