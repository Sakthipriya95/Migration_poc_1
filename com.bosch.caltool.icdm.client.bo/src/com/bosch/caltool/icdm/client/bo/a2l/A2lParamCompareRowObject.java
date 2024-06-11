/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class A2lParamCompareRowObject implements Comparable<A2lParamCompareRowObject> {

  /**
   * parameter name
   */
  private String paramName;
  /**
   * parameter id
   */
  private Long paramId;
  /**
   * Instance of A2lParamColumnDataMapper
   */
  private final A2lParamColumnDataMapper a2lColumnDataMapper;

  Map<Long, Long> a2lWpDefVarGrpMap = new HashMap<>();


  /**
   * @param a2lHandlerMap map of a2l wp def version and A2LWPInfoBO
   * @param a2lWpDefVarGrpMap map of a2l wp def id and var grp id
   */
  public A2lParamCompareRowObject(final Map<Long, A2LWPInfoBO> a2lHandlerMap, final Map<Long, Long> a2lWpDefVarGrpMap) {
    this.a2lColumnDataMapper = new A2lParamColumnDataMapper(a2lHandlerMap);
    this.a2lWpDefVarGrpMap = a2lWpDefVarGrpMap;
  }


  /**
   * @param a2lWpDefVersId1 fist a2l wp def id
   * @param a2lWpDefVersId2 second a2l wp def id
   * @param a2lVarGrp1 first variant group
   * @param a2lVarGrp2 second variant group
   * @return true if resp of both a2l params are different
   */
  public boolean isRespDiff(final Long a2lWpDefVersId1, final Long a2lWpDefVersId2, final A2lVariantGroup a2lVarGrp1,
      final A2lVariantGroup a2lVarGrp2) {
    A2LWPInfoBO a2lWpInfoBo = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId1);
    A2LWpParamInfo paramInfo = getParamInfoUsinfDefId(a2lWpDefVersId1, a2lVarGrp1);
    String respName1 = "";
    if (paramInfo != null) {
      respName1 = a2lWpInfoBo.getWpRespUser(paramInfo);
    }
    A2LWPInfoBO a2lWpInfoBo2 = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId2);
    A2LWpParamInfo paramInfo2 = getParamInfoUsinfDefId(a2lWpDefVersId2, a2lVarGrp2);
    String respName2 = "";
    if (paramInfo2 != null) {
      respName2 = a2lWpInfoBo2.getWpRespUser(paramInfo2);
    }
    return !CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(respName1), CommonUtils.checkNull(respName2));
  }

  /**
   * @param paramInfo A2LWpParamInfo for first a2l
   * @param paramInfo2 A2LWpParamInfo for second a2l
   * @return true if Function of both a2l params are different
   */
  public boolean isFuncDiff(final A2LWpParamInfo paramInfo, final A2LWpParamInfo paramInfo2) {
    String funcName1 = "";
    if (paramInfo != null) {
      funcName1 = paramInfo.getFuncName();
    }
    String funcName2 = "";
    if (paramInfo2 != null) {
      funcName2 = paramInfo2.getFuncName();
    }
    return !CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(funcName1), CommonUtils.checkNull(funcName2));
  }

  /**
   * @param paramInfo fist a2l A2LWpParamInfo
   * @param paramInfo2 second a2l A2LWpParamInfo
   * @return true if Function Version of both a2l params are different
   */
  public boolean isFuncVerDiff(final A2LWpParamInfo paramInfo, final A2LWpParamInfo paramInfo2) {
    String funcVerName1 = "";
    if (paramInfo != null) {
      funcVerName1 = paramInfo.getFunctionVer();
    }
    String funcVerName2 = "";
    if (paramInfo2 != null) {
      funcVerName2 = paramInfo2.getFunctionVer();
    }
    return !CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(funcVerName1), CommonUtils.checkNull(funcVerName2));
  }

  /**
   * @param paramInfo fist a2l A2LWpParamInfo
   * @param paramInfo2 second a2l A2LWpParamInfo
   * @return true if BC of both a2l params are different
   */
  public boolean isBCDiff(final A2LWpParamInfo paramInfo, final A2LWpParamInfo paramInfo2) {
    String bcName1 = "";
    if (paramInfo != null) {
      bcName1 = paramInfo.getBcName();
    }
    String bcName2 = "";
    if (paramInfo2 != null) {
      bcName2 = paramInfo2.getBcName();
    }
    return !CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(bcName1), CommonUtils.checkNull(bcName2));
  }

  /**
   * @param paramInfo fist a2l A2LWpParamInfo
   * @param paramInfo2 second a2l A2LWpParamInfo
   * @param a2lWpInfoBo first a2l
   * @param a2lWpInfoBo2 second a2l
   * @return true if Name at Customer of both a2l params are different
   */
  public boolean isNameAtCustDiff(final A2LWpParamInfo paramInfo, final A2LWpParamInfo paramInfo2,
      final A2LWPInfoBO a2lWpInfoBo, final A2LWPInfoBO a2lWpInfoBo2) {
    String nameAtCust1 = "";
    if (paramInfo != null) {
      nameAtCust1 = a2lWpInfoBo.getWpNameCust(paramInfo);
    }

    String nameAtCust2 = "";
    if (paramInfo2 != null) {
      nameAtCust2 = a2lWpInfoBo2.getWpNameCust(paramInfo2);
    }
    return !CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(nameAtCust1), CommonUtils.checkNull(nameAtCust2));
  }


  /**
   * @return if wp and resp of a2l params are different
   */
  public boolean isComputedDiff() {
    // check flag values
    int index2;
    Boolean diff = Boolean.FALSE;
    for (int index1 = 0; index1 < (this.a2lColumnDataMapper.getA2lHandlerMap().keySet().size()); index1++) {
      Long a2lWpDefVersId1 = (Long) this.a2lColumnDataMapper.getA2lHandlerMap().keySet().toArray()[index1];
      if (this.a2lColumnDataMapper.getA2lHandlerMap().keySet().size() == 1) {
        A2lVariantGroup a2lVarGrp1 = this.a2lColumnDataMapper.getColumnIndexA2lVarGrpMap()
            .get(this.a2lColumnDataMapper.getColumnA2lWpDefVersIdIndexMap().get(a2lWpDefVersId1));
        A2lVariantGroup a2lVarGrp2 = this.a2lColumnDataMapper.getColumnIndexA2lVarGrpMap()
            .get(this.a2lColumnDataMapper.getColumnA2lWpDefVersIdIndexMap().get(a2lWpDefVersId1) - 6);

        if (isWpOrRespDiff(a2lWpDefVersId1, a2lWpDefVersId1, a2lVarGrp1, a2lVarGrp2)) {
          diff = Boolean.TRUE;
          break;
        }
      }
      else {
        for (index2 = index1 + 1; index2 < (this.a2lColumnDataMapper.getA2lHandlerMap().keySet().size()); index2++) {
          Long a2lWpDefVersId2 = (Long) this.a2lColumnDataMapper.getA2lHandlerMap().keySet().toArray()[index2];
          A2lVariantGroup a2lVarGrp1 = this.a2lColumnDataMapper.getColumnIndexA2lVarGrpMap()
              .get(this.a2lColumnDataMapper.getColumnA2lWpDefVersIdIndexMap().get(a2lWpDefVersId1));
          A2lVariantGroup a2lVarGrp2 = this.a2lColumnDataMapper.getColumnIndexA2lVarGrpMap()
              .get(this.a2lColumnDataMapper.getColumnA2lWpDefVersIdIndexMap().get(a2lWpDefVersId2));
          if (isWpOrRespDiff(a2lWpDefVersId1, a2lWpDefVersId2, a2lVarGrp1, a2lVarGrp2)) {
            diff = Boolean.TRUE;
            break;
          }
        }
        if (diff.equals(Boolean.TRUE)) {
          break;
        }
      }
    }
    return diff;
  }


  /**
   * @param a2lWpDefVersId1
   * @param a2lWpDefVersId2
   * @param a2lVarGrp1
   * @param a2lVarGrp2
   * @return
   */
  private boolean isWpOrRespDiff(final Long a2lWpDefVersId1, final Long a2lWpDefVersId2,
      final A2lVariantGroup a2lVarGrp1, final A2lVariantGroup a2lVarGrp2) {
    return isWpDiff(a2lWpDefVersId1, a2lWpDefVersId2, a2lVarGrp1, a2lVarGrp2) ||
        isRespDiff(a2lWpDefVersId1, a2lWpDefVersId2, a2lVarGrp1, a2lVarGrp2);
  }

  /**
   * @param a2lWpDefVersId1 fist a2l wp def id
   * @param a2lWpDefVersId2 second a2l wp def id
   * @param a2lVarGrp1 first a2l variant group
   * @param a2lVarGrp2 second a2l variant group
   * @return true if wp name of both a2l params are different
   */
  public boolean isWpDiff(final Long a2lWpDefVersId1, final Long a2lWpDefVersId2, final A2lVariantGroup a2lVarGrp1,
      final A2lVariantGroup a2lVarGrp2) {

    A2LWPInfoBO a2lWpInfoBo = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId1);
    A2LWpParamInfo paramInfo = getParamInfoUsinfDefId(a2lWpDefVersId1, a2lVarGrp1);
    String wpName1 = "";
    if (paramInfo != null) {
      wpName1 = a2lWpInfoBo.getWPName(paramInfo);
    }
    A2LWPInfoBO a2lWpInfoBo2 = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId2);
    A2LWpParamInfo paramInfo2 = getParamInfoUsinfDefId(a2lWpDefVersId2, a2lVarGrp2);
    String wpName2 = "";
    if (paramInfo2 != null) {
      wpName2 = a2lWpInfoBo2.getWPName(paramInfo2);
    }
    return !CommonUtils.isEqualIgnoreCase(CommonUtils.checkNull(wpName1), CommonUtils.checkNull(wpName2));
  }


  /**
   * @param colIndex corresponding column
   * @return func name of param for specific a2l
   */
  public String getFuncName(final int colIndex) {
    A2LWpParamInfo paramInfo = getParamInfo(colIndex);
    if (paramInfo == null) {
      return "";
    }
    return paramInfo.getFuncName();
  }

  /**
   * @param colIndex corresponding column
   * @return func vers of param for specific a2l
   */
  public String getFuncVers(final int colIndex) {
    A2LWpParamInfo paramInfo = getParamInfo(colIndex);
    if (paramInfo == null) {
      return "";
    }
    return paramInfo.getFunctionVer();
  }

  /**
   * @param colIndex corresponding column
   * @return bc name of param for specific a2l
   */
  public String getBcName(final int colIndex) {
    A2LWpParamInfo paramInfo = getParamInfo(colIndex);
    if (paramInfo == null) {
      return "";
    }
    return paramInfo.getBcName();
  }

  /**
   * @param colIndex corresponding column
   * @return wp name of param for specific a2l
   */
  public String getWpName(final int colIndex) {

    A2LWpParamInfo paramInfo = getParamInfo(colIndex);
    if (paramInfo == null) {
      return "";
    }
    A2LWPInfoBO a2lWpInfoBo = getA2lWpInfoBO(colIndex);
    return a2lWpInfoBo.getWPName(paramInfo);
  }


  /**
   * @param colIndex corresponding column
   * @return resp name of param for specific a2l
   */
  public String getRespName(final int colIndex) {

    A2LWpParamInfo paramInfo = getParamInfo(colIndex);
    if (paramInfo == null) {
      return "";
    }
    A2LWPInfoBO a2lWpInfoBo = getA2lWpInfoBO(colIndex);
    return a2lWpInfoBo.getWpRespUser(paramInfo);
  }

  /**
   * @param colIndex corresponding column
   * @return name at cust of param for specific a2l
   */
  public String getNameAtCust(final int colIndex) {

    A2LWpParamInfo paramInfo = getParamInfo(colIndex);
    if (paramInfo == null) {
      return "";
    }
    A2LWPInfoBO a2lWpInfoBo = getA2lWpInfoBO(colIndex);
    return a2lWpInfoBo.getWpNameCust(paramInfo);
  }


  /**
   * @param colIndex corresponding column
   * @return A2LWPInfoBO for specific a2l
   */
  public A2LWPInfoBO getA2lWpInfoBO(final int colIndex) {
    Long a2lWpDefVersId = this.a2lColumnDataMapper.getColumnIndexA2lWpDefVersIdMap().get(colIndex);
    return this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId);
  }

  /**
   * @param colIndex corresponding column
   * @return paraminfo for specific a2l
   */
  public A2LWpParamInfo getParamInfo(final int colIndex) {
    Long a2lWpDefVersId = this.a2lColumnDataMapper.getColumnIndexA2lWpDefVersIdMap().get(colIndex);
    A2LWPInfoBO a2lWpInfoBo;
    A2lVariantGroup a2lVarGrp;
    Long varGrpId;
    a2lVarGrp = this.a2lColumnDataMapper.getColumnIndexA2lVarGrpMap().get(colIndex);
    if (a2lVarGrp == null) {
      a2lWpInfoBo = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId);
      varGrpId = null;
    }
    else {
      a2lWpInfoBo = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lVarGrp.getWpDefnVersId());
      varGrpId = a2lVarGrp.getId();
    }
    Map<Long, A2LWpParamInfo> paramInfoMap;
    if (varGrpId == null) {
      paramInfoMap = a2lWpInfoBo.getA2lWParamInfoMap();
    }
    else {
      paramInfoMap = a2lWpInfoBo.getA2lWParamInfoForVarGrp().get(varGrpId);
    }
    return paramInfoMap.get(this.paramId);
  }

  /**
   * @param a2lWpDefVersId a2lWpDefVersId
   * @return param info for given a2lWpDefVersId
   */
  private A2LWpParamInfo getParamInfoUsinfDefId(final Long a2lWpDefVersId, final A2lVariantGroup a2lVarGrp) {
    A2LWPInfoBO a2lWpInfoBo = this.a2lColumnDataMapper.getA2lHandlerMap().get(a2lWpDefVersId);
    Map<Long, A2LWpParamInfo> paramInfoMap;
    if (a2lVarGrp == null) {
      paramInfoMap = a2lWpInfoBo.getA2lWParamInfoMap();
    }
    else {
      paramInfoMap = a2lWpInfoBo.getA2lWParamInfoForVarGrp().get(a2lVarGrp.getId());
    }
    return paramInfoMap.get(this.paramId);
  }


  /**
   * @param colIndex corresponding column
   * @return true if corresponding a2l is working set
   */
  public boolean isModifiable(final int colIndex) {
    return false;
  }

  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getParamId());
  }

  /**
   * {@inheritDoc} returns true if ID's of the attributes are same or both null
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    A2lParamCompareRowObject compareObj = (A2lParamCompareRowObject) obj;

    return ModelUtil.isEqual(getParamId(), compareObj.getParamId()) &&
        ModelUtil.isEqual(getParamName(), compareObj.getParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lParamCompareRowObject compareRowObj) {
    int ret = ModelUtil.compare(getParamName(), compareRowObj.getParamName());
    return ret == 0 ? ModelUtil.compare(getParamId(), compareRowObj.getParamId()) : ret;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }


  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }


  /**
   * @param a2lWpDefVersId wp def version id
   * @param a2lVarGrp a2l variant group
   */
  public void addA2lColumnData(final Long a2lWpDefVersId, final A2lVariantGroup a2lVarGrp) {
    this.a2lColumnDataMapper.addNewA2LColumnsIndex(a2lWpDefVersId, a2lVarGrp);
  }

  /**
   * @return the a2lColumnDataMapper
   */
  public A2lParamColumnDataMapper getA2lColumnDataMapper() {
    return this.a2lColumnDataMapper;
  }


  /**
   * @param colIndex int
   * @return Object
   */
  public Object getColumnData(final int colIndex) {
    String flagString = this.a2lColumnDataMapper.getColumnIndexFlagMap().get(colIndex);
    String returnValue = "";
    switch (flagString) {
      case IUIConstants.FUNCTION_NAME:

        returnValue = getFuncName(colIndex);
        break;
      case IUIConstants.FUNCTION_VERS:

        returnValue = getFuncVers(colIndex);
        break;
      case IUIConstants.BC:

        returnValue = getBcName(colIndex);
        break;
      case IUIConstants.WORK_PACKAGE:

        returnValue = getWpName(colIndex);
        break;
      case IUIConstants.RESPONSIBILTY:

        returnValue = getRespName(colIndex);
        break;
      case IUIConstants.NAME_AT_CUSTOMER:

        returnValue = getNameAtCust(colIndex);
        break;
      default:
        break;

    }
    return returnValue;
  }
}
