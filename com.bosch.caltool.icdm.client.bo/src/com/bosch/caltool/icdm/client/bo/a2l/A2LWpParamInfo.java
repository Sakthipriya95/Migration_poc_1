/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.Arrays;
import java.util.List;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author apj4cob
 */
public class A2LWpParamInfo implements Cloneable, Comparable<A2LWpParamInfo> {

  private final String paramName;
  private String funcName = "";
  private String functionVer = "";
  private String bcName;
  /**
   * Wp Resp Id
   */
  private Long wpRespId;

  private final Long paramId;


  private Long variantGroupId;

  private Long a2lWpParamMappingId;

  private Long a2lRespId;

  /**
   * WP Name at customer
   */
  private String wpNameCust;

  private boolean isLABParam;

  /**
   * WP Resp Inherited flag
   */
  private boolean wpRespInherited;

  /**
   * WP Name Inherited flag
   */
  private boolean wpNameInherited;
  /**
   * Compliance flag
   */

  private final boolean complianceParam;

  private final boolean isBlackList;

  /**
   * flag to indicate is QssdParameter
   */
  private final boolean isQssdParameter;
  /**
   * flag to indicate isReadOnly
   */
  private final boolean isReadOnlyParameter;
  /**
   * flag to indicate Dependent characteristic
   */
  private final boolean isDependentParameter;
  /**
   * dependent characteristics names
   */
  private final List<String> depCharNames;

  /**
   * Instantiates a new a 2 L wp param info.
   *
   * @param paramId the param id
   * @param paramName String
   * @param func Function
   * @param complianceParam boolean
   * @param isBlackList isBlackListParameter
   * @param isQssdParameter isQssdParameter
   */
  public A2LWpParamInfo(final A2LParameter a2lParam) {
    this.paramId = a2lParam.getParamId();
    this.paramName = a2lParam.getName();
    Function func = a2lParam.getDefFunction();
    if (func != null) {
      this.funcName = func.getName();
      this.functionVer = func.getFunctionVersion();
    }
    this.complianceParam = a2lParam.isComplianceParam();
    this.isBlackList = a2lParam.isBlackList();
    this.isQssdParameter = a2lParam.isQssdParameter();
    this.isReadOnlyParameter = a2lParam.getCharacteristic().isReadOnly();
    this.isDependentParameter = a2lParam.getCharacteristic().isDependentCharacteristic();
    this.depCharNames = a2lParam.getCharacteristic().getDependentCharacteristic() == null ? null
        : Arrays.asList(a2lParam.getCharacteristic().getDependentCharacteristic().getCharacteristicName());
  }

  /**
   * @return "Y" if the parameter is read-only, otherwise "N"
   */
  public String getReadOnlyLabel() {
    return CommonUtils.getBooleanCode(isReadOnly());
  }

  /**
   * @return the isReadOnlyParameter
   */
  public boolean isReadOnly() {
    return this.isReadOnlyParameter;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @return the functionVer
   */
  public String getFunctionVer() {
    return this.functionVer;
  }

  /**
   * @return the bcName
   */
  public String getBcName() {
    return this.bcName;
  }


  /**
   * @param bcName the bcName to set
   */
  public void setBcName(final String bcName) {
    this.bcName = bcName;
  }

  /**
   * @return the wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }

  /**
   * @param wpRespId the wpRespId to set
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
  }


  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }


  /**
   * @return the wpNameCust
   */
  public String getWpNameCust() {
    return this.wpNameCust;
  }

  /**
   * @param wpNameCust the wpNameCust to set
   */
  public void setWpNameCust(final String wpNameCust) {
    this.wpNameCust = wpNameCust;
  }

  /**
   * @return variantGroupId
   */
  public Long getVariantGroupId() {
    return this.variantGroupId;
  }

  /**
   * @param variantGroupId set variantGroupId
   */
  public void setVariantGroupId(final Long variantGroupId) {
    this.variantGroupId = variantGroupId;
  }


  /**
   * @return the a2lWpParamMappingId
   */
  public Long getA2lWpParamMappingId() {
    return this.a2lWpParamMappingId;
  }


  /**
   * @param a2lWpParamMappingId the a2lWpParamMappingId to set
   */
  public void setA2lWpParamMappingId(final Long a2lWpParamMappingId) {
    this.a2lWpParamMappingId = a2lWpParamMappingId;
  }

  /**
   * @return the isLabParam
   */
  public boolean isLABParam() {
    return this.isLABParam;
  }


  /**
   * Sets the LAB param.
   *
   * @param isLABParam the new LAB param
   */
  public void setLABParam(final boolean isLABParam) {
    this.isLABParam = isLABParam;
  }


  /**
   * @return the wpRespInherited
   */
  public boolean isWpRespInherited() {
    return this.wpRespInherited;
  }


  /**
   * @param wpRespInherited the wpRespInherited to set
   */
  public void setWpRespInherited(final boolean wpRespInherited) {
    this.wpRespInherited = wpRespInherited;
  }


  /**
   * @return the wpNameInherited
   */
  public boolean isWpNameInherited() {
    return this.wpNameInherited;
  }


  /**
   * @param wpNameInherited the wpNameInherited to set
   */
  public void setWpNameInherited(final boolean wpNameInherited) {
    this.wpNameInherited = wpNameInherited;
  }


  /**
   * @return the complianceParam
   */
  public boolean isComplianceParam() {
    return this.complianceParam;
  }

  /**
   * Checks for resp assigned.
   *
   * @return true, if successful
   */
  public boolean hasRespAssigned() {
    return isWpRespInherited() ? false : this.a2lRespId != null;
  }

  /**
   * Checks for name at cust assigned.
   *
   * @return true, if successful
   */
  public boolean hasNameAtCustAssigned() {
    return isWpNameInherited() ? false : this.wpNameCust != null;
  }

  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }


  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LWpParamInfo object) {
    return ModelUtil.compare(getParamName(), object.getParamName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() == this.getClass()) {
      return ModelUtil.isEqual(getParamName(), ((A2LWpParamInfo) obj).getParamName());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getParamId());
  }


  /**
   * @return the isBlackList
   */
  public boolean isBlackList() {
    return this.isBlackList;
  }

  /**
   * @return the isQssdParameter
   */
  public boolean isQssdParameter() {
    return this.isQssdParameter;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public A2LWpParamInfo clone() {
    A2LWpParamInfo paramInfo = null;
    try {
      paramInfo = (A2LWpParamInfo) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // NA
    }
    return paramInfo;
  }

  /**
   * @return "Y" if the parameter is dependent, otherwise "N"
   */
  public String getDependentLabel() {
    return CommonUtils.getBooleanCode(isDependentParameter());
  }

  /**
   * @return the isDependentParameter
   */
  public boolean isDependentParameter() {
    return this.isDependentParameter;
  }


  /**
   * @return the depCharNames
   */
  public List<String> getDepCharNames() {
    return this.depCharNames;
  }
}
