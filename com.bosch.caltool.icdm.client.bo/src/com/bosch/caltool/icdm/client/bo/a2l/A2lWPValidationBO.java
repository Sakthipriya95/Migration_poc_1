/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;

/**
 * @author pdh2cob
 */
public class A2lWPValidationBO {


  private A2LWPInfoBO a2lWpInfoBo;

  private final WorkPkgResponsibilityBO respBo;

  private static final int WP_NAME_LENGTH = 128;


  private static final int WP_DEFN_VERS_LENGTH = 50;

  /**
   * @param a2lWpInfoBo - instance of A2LWPInfoBO
   */
  public A2lWPValidationBO(final A2LWPInfoBO a2lWpInfoBo) {
    this.a2lWpInfoBo = a2lWpInfoBo;
    this.respBo = null;
  }

  /**
   * @param WrkPkgRespBo - instance of WorkPkgResponsibilityBO
   */
  public A2lWPValidationBO(final WorkPkgResponsibilityBO WrkPkgRespBo) {
    this.respBo = WrkPkgRespBo;
  }


  /**
   * @param wpName
   * @return error message
   */
  public String validateWPCustName(final String wpName) {
    String errorMsg = "";
    if (isWPNameTooLong(wpName)) {
      errorMsg = "Allowed length is " + WP_NAME_LENGTH + ".";
    }
    return errorMsg;
  }


  /**
   * @param wpName
   * @return
   */
  public String validateWPName(final String wpName) {
    String errorMsg = "";
    if (isWPNameEmpty(wpName)) {
      errorMsg = "Workpackage name is empty";
    }
    else if (((this.respBo == null) && isA2lWpNameDuplicate(wpName)) || isPidcWpNameDuplicate(wpName)) {
      errorMsg = "Workpackage name already exists.";
    }
    else if (isWPNameTooLong(wpName)) {
      errorMsg = "Workpackage name is too long. Allowed length is " + WP_NAME_LENGTH + ".";
    }
    return errorMsg;
  }

  /**
   * @param wpName - wp name
   * @return
   */

  public boolean isA2lWpNameDuplicate(final String wpName) {
    // validation fails if WP name is a duplicate , for a PIDC version WP name is unique
    boolean flag = false;
    for (A2lWpResponsibility wpResp : this.a2lWpInfoBo.getA2lWpDefnModel().getWpRespMap().values()) {
      if (wpName.equalsIgnoreCase(wpResp.getName()) && this.a2lWpInfoBo.isMappedToSelectedVarGrp(wpResp)) {
        flag = true;
        break;
      }
    }
    return flag;
  }


  /**
   * @param wpName String
   * @return boolean
   */
  public boolean isPidcWpNameDuplicate(final String wpName) {
    boolean flag = false;
    for (A2lWorkPackage wpResp : this.respBo.getWorkPackgsMap().values()) {
      if (wpName.equalsIgnoreCase(wpResp.getName())) {
        flag = true;
        break;
      }
    }
    return flag;
  }


  /**
   * @return the wpNameLength
   */
  public static int getWpNameLength() {
    return WP_NAME_LENGTH;
  }

  /**
   * @param wpDefnVersName
   * @return
   */
  public boolean isWpDefnVersDuplicate(final String wpDefnVersName) {
    boolean flag = false;
    for (A2lWpDefnVersion wpDefnVers : this.a2lWpInfoBo.getA2lWpDefnVersMap().values()) {
      if (wpDefnVersName.equalsIgnoreCase(wpDefnVers.getName())) {
        flag = true;
        break;

      }
    }
    return flag;
  }

  /**
   * @param wpDefnVersName
   * @return true if WPDefnVersion name's lengthe exceeds allowed no of characters
   */
  public boolean isWpDefnVersTooLong(final String wpDefnVersName) {
    return wpDefnVersName.length() >= WP_DEFN_VERS_LENGTH;
  }

  /**
   * @param wpName
   * @return true if WP name exceeds allowed no of characters
   */
  public boolean isWPNameTooLong(final String wpName) {
    return wpName.length() >= WP_NAME_LENGTH;

  }

  /**
   * @param wpName
   * @return true if WP name is empty
   */
  public boolean isWPNameEmpty(final String wpName) {
    return CommonUtils.isEmptyString(wpName);
  }


}
