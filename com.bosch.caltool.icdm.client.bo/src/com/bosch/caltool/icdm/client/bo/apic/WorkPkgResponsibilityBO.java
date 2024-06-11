/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.a2l.RespValidationBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class WorkPkgResponsibilityBO {

  private final PidcVersionBO pidcVersBO;

  private A2lResponsibilityModel a2lRespModel;
  // Initialize the map - not doing so created NPE
  private Map<Long, A2lWorkPackage> workPackgsMap = new HashMap<>();

  private final RespValidationBO respValidationBO;

  /**
   * @param pidcVerBO PidcVersionBO
   */
  public WorkPkgResponsibilityBO(final PidcVersionBO pidcVerBO) {
    this.pidcVersBO = pidcVerBO;
    this.respValidationBO = new RespValidationBO(this);
  }

  /**
   * load A2l Resp for pidc.
   *
   * @param refreshNeeded is refresh needed
   * @return A2lResponsibilityModel object
   */
  public A2lResponsibilityModel loadA2lRespForPidc(final boolean refreshNeeded) {
    try {
      // to check if A2lResponsibilityModel is already loaded
      if (CommonUtils.isNull(this.a2lRespModel) || refreshNeeded) {
        this.a2lRespModel = new A2lResponsibilityServiceClient().getByPidc(this.pidcVersBO.getPidc().getId());
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return this.a2lRespModel;
  }

  /**
   * @return Map<Long, A2lWorkPackage>
   */
  public Map<Long, A2lWorkPackage> getWorkPackages() {
    if (this.workPackgsMap.isEmpty()) {
      try {
        this.workPackgsMap =
            new A2lWorkPackageServiceClient().getWpByPidcVers(this.pidcVersBO.getPidcVersion().getId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return this.workPackgsMap;
  }

  /**
   * @param a2lRespUpdationData a2l resp and boschGrpUsers bosch group users to be created/updated
   * @return created bosch group users
   */
  public A2lRespMaintenanceData createUpdateA2lRespWithBoschGrpUsers(final A2lRespMaintenanceData a2lRespUpdationData) {

    try {
      return new A2lResponsibilityServiceClient().maintainA2lResp(a2lRespUpdationData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * @param respToCreate - A2lResponsibility to be created
   * @return creaetd A2lResponsibility
   */
  public A2lResponsibility createA2lResp(final A2lResponsibility respToCreate) {
    try {
      return new A2lResponsibilityServiceClient().create(respToCreate);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @param respToEdit - A2lResp to be edited
   * @return updated A2lResp
   */
  public A2lResponsibility editA2lResp(final A2lResponsibility respToEdit) {
    try {
      List<A2lResponsibility> respList = new ArrayList<>();
      respList.add(respToEdit);
      Map<Long, A2lResponsibility> updatedMap = new A2lResponsibilityServiceClient().update(respList);
      return updatedMap.get(respToEdit.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Check if exists.
   *
   * @param respToCreate the resp to create
   * @return true, if successful
   */
  public boolean checkIfRespExists(final A2lResponsibility respToCreate) {
    Map<Long, A2lResponsibility> existingMap = getA2lRespModel().getA2lResponsibilityMap();
    for (A2lResponsibility obj : existingMap.values()) {
      if (A2lResponsibilityCommon.isSame(obj, respToCreate)) {
        return true;
      }
    }
    return false;
  }


  /**
   * to check if customer /Other combination exist
   *
   * @param respToCreate responsibility to be created
   * @return boolean whether resp already exists
   */
  public boolean checkIfCustomerOrOthersExists(final A2lResponsibility respToCreate) {
    Map<Long, A2lResponsibility> existingMap = getA2lRespModel().getA2lResponsibilityMap();
    for (A2lResponsibility obj : existingMap.values()) {
      if (A2lResponsibilityCommon.isCutomerAndOtherRespSame(obj, respToCreate)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Check if user id is already mapped to a2l responsibility
   *
   * @param respToEdit the resp to edit
   * @return object if successful
   */
  public boolean checkIfUserIdExists(final A2lResponsibility respToEdit) {
    Map<Long, A2lResponsibility> existingMap = getA2lRespModel().getA2lResponsibilityMap();
    for (A2lResponsibility obj : existingMap.values()) {
      if (respToEdit.getUserId().equals(obj.getUserId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if Department is already mapped to a2l responsibility
   *
   * @param respToEdit the resp to edit
   * @return object if successful
   */
  public boolean checkIfDepartmentIdExists(final A2lResponsibility respToEdit) {
    Map<Long, A2lResponsibility> existingMap = getA2lRespModel().getA2lResponsibilityMap();
    if (respToEdit.getId() != null) {
//    edit the existing department
      for (A2lResponsibility obj : existingMap.values()) {
        if (CommonUtils.isNotEqual(respToEdit.getId(), obj.getId()) && (obj.getLDepartment() != null) &&
            respToEdit.getLDepartment().equalsIgnoreCase(obj.getLDepartment())) {
          return true;
        }
      }
    }
    else {
//    create a new department
      for (A2lResponsibility obj : existingMap.values()) {
        if ((obj.getLDepartment() != null) && respToEdit.getLDepartment().equalsIgnoreCase(obj.getLDepartment())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return the A2lResponsibilityModel
   */
  public A2lResponsibilityModel getA2lRespModel() {
    return this.a2lRespModel;
  }


  /**
   * @param a2lRespModel the a2lRespModel to set
   */
  public void setA2lRespModel(final A2lResponsibilityModel a2lRespModel) {
    this.a2lRespModel = a2lRespModel;
  }

  /**
   * @return pidc id
   */
  public Long getPidcId() {
    return this.pidcVersBO.getPidc().getId();
  }


  /**
   * @return PidcVersionBO
   */
  public PidcVersionBO getPidcVersBO() {
    return this.pidcVersBO;
  }

  /**
   * @param a2lResp A2lResponsibility
   * @return String
   */
  public String getRespFirstName(final A2lResponsibility a2lResp) {

    if (a2lResp.getUserId() != null) {
      return this.a2lRespModel.getUserMap().get(a2lResp.getUserId()).getFirstName();
    }

    else if (!CommonUtils.isEmptyString(a2lResp.getLFirstName())) {
      return a2lResp.getLFirstName();
    }
    return "";
  }

  /**
   * @param a2lResp A2lResponsibility
   * @return String
   */
  public String getRespLastName(final A2lResponsibility a2lResp) {

    if (a2lResp.getUserId() != null) {
      return this.a2lRespModel.getUserMap().get(a2lResp.getUserId()).getLastName();
    }

    else if (!CommonUtils.isEmptyString(a2lResp.getLLastName())) {
      return a2lResp.getLLastName();
    }
    return "";
  }

  /**
   * @param a2lResp A2lResponsibility
   * @return String
   */
  public String getRespDeptName(final A2lResponsibility a2lResp) {

    if (a2lResp.getUserId() != null) {
      return this.a2lRespModel.getUserMap().get(a2lResp.getUserId()).getDepartment();
    }

    else if (!CommonUtils.isEmptyString(a2lResp.getLDepartment())) {
      return a2lResp.getLDepartment();
    }
    return "";
  }


  /**
   * disable edit option if chosen responsibility is a default responsibility or is of Bosch Type with user details or
   * icdm qnaire config attr value is 'BEG' and 'Load FC2WP assignments in A2L files' attribute value is set to true
   *
   * @param selA2lResp A2lResponsibility
   * @return boolean
   */
  public boolean isEditNotApplicable(final A2lResponsibility selA2lResp) {
    return A2lResponsibilityCommon.isDefaultResponsibility(selA2lResp) ||
        (CommonUtils.isEqual(WpRespType.RB.getCode(), selA2lResp.getRespType()) && (null != selA2lResp.getUserId())) ||
        isBEGResp(selA2lResp);
  }

  /**
   * @param selA2lResp selected a2l resp
   * @return true if icdm qnaire config attr is BEG
   */
  public boolean isBEGResp(final A2lResponsibility selA2lResp) {
    return CommonUtils.isEqual(ApicConstants.ALIAS_NAME_RB_BEG, selA2lResp.getAliasName()) &&
        this.pidcVersBO.isQnaireConfigBEG();
  }

  /**
   * @param selA2lResp A2lResponsibility
   * @return boolean
   * @throws ApicWebServiceException Exception
   */
  public boolean canEditResp(final A2lResponsibility selA2lResp) throws ApicWebServiceException {
    return new CurrentUserBO().hasNodeOwnerAccess(getPidcId()) && !isEditNotApplicable(selA2lResp);
  }

  /**
   * @return boolean
   * @throws ApicWebServiceException Exception
   */
  public boolean canAddResp() throws ApicWebServiceException {
    return new CurrentUserBO().hasNodeOwnerAccess(getPidcId());
  }

  /**
   * @return the pidcVersObj
   */
  public PidcVersion getPidcVersObj() {
    return this.pidcVersBO.getPidcVersion();
  }

  /**
   * @return the workPackgsMap
   */
  public Map<Long, A2lWorkPackage> getWorkPackgsMap() {
    return this.workPackgsMap;
  }


  /**
   * @return RespValidationBO
   */
  public RespValidationBO getRespValidationBO() {
    return this.respValidationBO;
  }

  /**
   * @param pidcA2lId Long
   * @return boolean
   */
  public boolean isPidcA2lInVer(final Long pidcA2lId) {
    for (PidcA2lFileExt pidcA2lFileExt : this.pidcVersBO.getAllA2lByPidc(getPidcId()).values()) {
      if ((null != pidcA2lFileExt.getPidcA2l()) && pidcA2lFileExt.getPidcA2l().getId().equals(pidcA2lId)) {
        return true;
      }
    }
    return false;
  }
}
