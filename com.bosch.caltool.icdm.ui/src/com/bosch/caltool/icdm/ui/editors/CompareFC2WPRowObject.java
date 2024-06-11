/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;


/**
 * @author
 */
public class CompareFC2WPRowObject implements Comparable<CompareFC2WPRowObject> {

  /**
   *
   */
  private static final int REF_COMPARE_COL_INDEX = 2;

  /**
   *
   */
  private static final int COMP_COL_START_INDEX = 20;

  /**
   *
   */
  private static final int DYNAMIC_COL_COUNT = 17;

  private String funcName;

  private String funcDesc;

  private final FC2WPColumnDataMapper columnDataMapper = new FC2WPColumnDataMapper();


  // To be used only for sorting or filtering to improve performance
  private Boolean diff = Boolean.FALSE;

  private Boolean wpDiff = Boolean.FALSE;

  private Boolean contactDiff = Boolean.FALSE;

  private Boolean isAgreedDiff = Boolean.FALSE;

  private Boolean isInIcdm = Boolean.FALSE;

  private Boolean isFcInSdom = Boolean.FALSE;

  private Boolean hasRelaventPTType = Boolean.FALSE;

  private Boolean isFcWithParams = Boolean.FALSE;

  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @return the funcDesc
   */
  public String getFuncDesc() {
    return this.funcDesc;
  }


  /**
   * @param funcDesc the funcDesc to set
   */
  public void setFuncDesc(final String funcDesc) {
    this.funcDesc = funcDesc;
  }


  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
  }


  /**
   * @param fc2wpMapping
   * @param fc2wpMappingResult
   */
  public void addFC2WPMapping(final FC2WPMapping fc2wpMapping, final FC2WPMappingResult fc2wpMappingResult) {
    this.columnDataMapper.addNewColumnIndex(fc2wpMapping, fc2wpMappingResult);
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
    CompareFC2WPRowObject compareObj = (CompareFC2WPRowObject) obj;
    return this.funcName.equals(compareObj.getFuncName());
  }


  /**
   * @return the columnDataMapper
   */
  public FC2WPColumnDataMapper getColumnDataMapper() {
    return this.columnDataMapper;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompareFC2WPRowObject compareRowObj) {
    return ApicUtil.compare(this.funcName, compareRowObj.getFuncName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Returns true is values are different
   *
   * @return true if diff
   */
  public Object getComputedIsDiff() {
    FC2WPMapping refFc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(REF_COMPARE_COL_INDEX);
    FC2WPMappingResult refFc2wpMappingResult =
        getColumnDataMapper().getColumnIndexFC2WPMapResult().get(REF_COMPARE_COL_INDEX);
    this.diff = Boolean.FALSE;
    int colIndex = COMP_COL_START_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {

      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      FC2WPMappingResult fc2wpMappingResult = getColumnDataMapper().getColumnIndexFC2WPMapResult().get(colIndex);
      // do compare
      if (compareFc2WpMappingResult(refFc2wpMapping, refFc2wpMappingResult, fc2wpMapping, fc2wpMappingResult)) {
        return true;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.diff;
  }


  /**
   * @param refFc2wpMapping
   * @param fc2wpMapping
   * @return
   */
  private String getKeyVal(final FC2WPMapping refFc2wpMapping, final FC2WPMapping fc2wpMapping) {
    String key = null;
    if (fc2wpMapping != null) {
      key = fc2wpMapping.getFunctionName();
    }
    if (refFc2wpMapping != null) {
      key = refFc2wpMapping.getFunctionName();
    }
    return key;
  }


  /**
   * @param refFc2wpMapping
   * @param refFc2wpMappingResult
   * @param fc2wpMapping
   * @param fc2wpMappingResult
   * @param key
   */
  private boolean compareFc2WpMappingResult(final FC2WPMapping refFc2wpMapping,
      final FC2WPMappingResult refFc2wpMappingResult, final FC2WPMapping fc2wpMapping,
      final FC2WPMappingResult fc2wpMappingResult) {
    String key = getKeyVal(refFc2wpMapping, fc2wpMapping);
    if (isWpNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isResourceNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isWpIdNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isBcNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isPTTypeNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isFcNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isSCNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isAgreedNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isAgreedOnNotEqual(fc2wpMapping, refFc2wpMapping)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isRespNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isCommentNotEqual(fc2wpMapping, refFc2wpMapping)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isFc2WpInfoNotEqual(fc2wpMapping, refFc2wpMapping)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    return compareFc2WpForYorNVals(refFc2wpMappingResult, fc2wpMappingResult, key);
  }


  /**
   * @param refFc2wpMappingResult
   * @param fc2wpMappingResult
   * @param key
   */
  private boolean compareFc2WpForYorNVals(final FC2WPMappingResult refFc2wpMappingResult,
      final FC2WPMappingResult fc2wpMappingResult, final String key) {
    if (isInIcdmNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isFcInSdomNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isFcWithParamsNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    if (isDeletedNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
      this.diff = Boolean.TRUE;
      return this.diff;
    }
    return this.diff;
  }

  /**
   * @param fc2wpMappingResult FC2WPMappingResult-1
   * @param refFc2wpMappingResult FC2WPMappingResult-2
   * @param key FuncName
   * @return true if equal ,else false
   */
  public boolean isFcInSdomNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String isFcInSdom1 = "";
    String isFcInSdom2 = "";
    if (null != fc2wpMappingResult) {
      isFcInSdom1 = fc2wpMappingResult.getIsFcInSdomUIString(key);
    }
    if (null != refFc2wpMappingResult) {
      isFcInSdom2 = refFc2wpMappingResult.getIsFcInSdomUIString(key);
    }
    return CommonUtils.isNotEqual(isFcInSdom1, isFcInSdom2);
  }


  /**
   * @return
   */
  public Object getWpDiff() {
    FC2WPMappingResult refFc2wpMappingResult =
        getColumnDataMapper().getColumnIndexFC2WPMapResult().get(REF_COMPARE_COL_INDEX);
    FC2WPMapping refFc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(REF_COMPARE_COL_INDEX);
    int colIndex = COMP_COL_START_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {

      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      FC2WPMappingResult fc2wpMappingResult = getColumnDataMapper().getColumnIndexFC2WPMapResult().get(colIndex);
      String key = null;
      if (fc2wpMapping != null) {
        key = fc2wpMapping.getFunctionName();
      }
      if (refFc2wpMapping != null) {
        key = refFc2wpMapping.getFunctionName();
      }
      // do compare
      if (isWpNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
        this.wpDiff = Boolean.TRUE;
        break;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.wpDiff;
  }

  /**
   * @return
   */
  public Object getContactDiff() {
    FC2WPMappingResult refFc2wpMappingResult =
        getColumnDataMapper().getColumnIndexFC2WPMapResult().get(REF_COMPARE_COL_INDEX);
    FC2WPMapping refFc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(REF_COMPARE_COL_INDEX);
    this.diff = Boolean.FALSE;
    int colIndex = COMP_COL_START_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {

      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      FC2WPMappingResult fc2wpMappingResult = getColumnDataMapper().getColumnIndexFC2WPMapResult().get(colIndex);
      String key = getKeyVal(refFc2wpMapping, fc2wpMapping);

      if ((isFcNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) ||
          (isSCNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key))) {
        this.contactDiff = Boolean.TRUE;
        break;
      }

      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.contactDiff;
  }

  /**
   * @return
   */
  public Object getIsAgreedDiff() {
    FC2WPMappingResult refFc2wpMappingResult =
        getColumnDataMapper().getColumnIndexFC2WPMapResult().get(REF_COMPARE_COL_INDEX);
    FC2WPMapping refFc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(REF_COMPARE_COL_INDEX);
    int colIndex = COMP_COL_START_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {

      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      FC2WPMappingResult fc2wpMappingResult = getColumnDataMapper().getColumnIndexFC2WPMapResult().get(colIndex);
      String key = null;
      if (fc2wpMapping != null) {
        key = fc2wpMapping.getFunctionName();
      }
      if (refFc2wpMapping != null) {
        key = refFc2wpMapping.getFunctionName();
      }
      // do compare
      if (isAgreedNotEqual(fc2wpMappingResult, refFc2wpMappingResult, key)) {
        this.isAgreedDiff = Boolean.TRUE;
        break;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.isAgreedDiff;
  }

  /**
   * @return
   */
  public Object getIsInIcdmA2l() {
    int colIndex = REF_COMPARE_COL_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {
      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      if ((fc2wpMapping != null) && fc2wpMapping.isUsedInIcdm()) {
        this.isInIcdm = Boolean.TRUE;
        break;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.isInIcdm;
  }

  /**
   * @return true , if FcInSdom Flag is true else false
   */
  public boolean getIsFcInSdom() {
    int colIndex = REF_COMPARE_COL_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {
      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      if ((fc2wpMapping != null) && fc2wpMapping.isFcInSdom()) {
        this.isFcInSdom = Boolean.TRUE;
        break;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.isFcInSdom;
  }

  /**
   * @return true , if FcWithParams Flag is true else false
   */
  public boolean getIsFcWithParams() {
    int colIndex = REF_COMPARE_COL_INDEX;
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {
      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      if ((fc2wpMapping != null) && fc2wpMapping.isFcWithParams()) {
        this.isFcWithParams = Boolean.TRUE;
        break;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.isFcWithParams;
  }

  /**
   * @param relevantPTtypeSet
   * @return
   */
  public Object hasRelevantPTType(final Set<FC2WPRelvPTType> relevantPTtypeSet) {
    int colIndex = REF_COMPARE_COL_INDEX;
    List<String> list = new ArrayList<>();
    relevantPTtypeSet.forEach(t -> list.add(t.getPtTypeName()));
    while (colIndex < this.columnDataMapper.getColumnIndexFC2WPMap().size()) {
      FC2WPMapping fc2wpMapping = getColumnDataMapper().getColumnIndexFC2WPMap().get(colIndex);
      FC2WPMappingResult fc2wpMappingResult = getColumnDataMapper().getColumnIndexFC2WPMapResult().get(colIndex);
      if ((fc2wpMapping != null) && fc2wpMappingResult.getIsRelevantPTtype(fc2wpMapping.getFunctionName(), list)) {
        this.hasRelaventPTType = Boolean.TRUE;
        break;
      }
      colIndex = colIndex + DYNAMIC_COL_COUNT;
    }
    return this.hasRelaventPTType;
  }

  /**
   * @param agreeWithCocDate
   * @return
   */
  private String getDateFormat(final Date date) {
    String formattedDate = "";
    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
    }
    return formattedDate;
  }

  /**
   * @param configLabels
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   */
  public boolean isWpNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String wpName1 = "";
    String wpName2 = "";
    if (null != fc2wpMappingResult) {
      wpName1 = fc2wpMappingResult.getWorkpackage(key);
    }
    if (null != refFc2wpMappingResult) {
      wpName2 = refFc2wpMappingResult.getWorkpackage(key);
    }
    return (CommonUtils.isNotEqual(wpName1, wpName2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isResourceNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String resourse1 = "";
    String resourse2 = "";
    if (null != fc2wpMappingResult) {
      resourse1 = fc2wpMappingResult.getResource(key);
    }
    if (null != refFc2wpMappingResult) {
      resourse2 = refFc2wpMappingResult.getResource(key);
    }
    return (CommonUtils.isNotEqual(resourse1, resourse2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isWpIdNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String wpID1 = "";
    String wpID2 = "";
    if (null != fc2wpMappingResult) {
      wpID1 = fc2wpMappingResult.getWpIdMCR(key);
    }
    if (null != refFc2wpMappingResult) {
      wpID2 = refFc2wpMappingResult.getWpIdMCR(key);
    }
    return (CommonUtils.isNotEqual(wpID1, wpID2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isBcNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String bc1 = "";
    String bc2 = "";
    if (null != fc2wpMappingResult) {
      bc1 = fc2wpMappingResult.getBC(key);
    }
    if (null != refFc2wpMappingResult) {
      bc2 = refFc2wpMappingResult.getBC(key);
    }
    return (CommonUtils.isNotEqual(bc1, bc2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isPTTypeNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String ptType1 = "";
    String ptType2 = "";
    if (null != fc2wpMappingResult) {
      ptType1 = fc2wpMappingResult.getPTtypeUIString(key);
    }
    if (null != refFc2wpMappingResult) {
      ptType2 = refFc2wpMappingResult.getPTtypeUIString(key);
    }
    return (CommonUtils.isNotEqual(ptType1, ptType2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isFcNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String contact1 = "";
    String contact2 = "";
    if (null != fc2wpMappingResult) {
      contact1 = fc2wpMappingResult.getFirstContactEffective(key);
    }
    if (null != refFc2wpMappingResult) {
      contact2 = refFc2wpMappingResult.getFirstContactEffective(key);
    }
    return (CommonUtils.isNotEqual(contact1, contact2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isFcWithParamsNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String fcWithParams1 = "";
    String fcWithParams2 = "";
    if (null != fc2wpMappingResult) {
      fcWithParams1 = fc2wpMappingResult.isFcWithParams(key);
    }
    if (null != refFc2wpMappingResult) {
      fcWithParams2 = refFc2wpMappingResult.isFcWithParams(key);
    }
    return (CommonUtils.isNotEqual(fcWithParams1, fcWithParams2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isSCNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String secContact1 = "";
    String secContact2 = "";
    if (null != fc2wpMappingResult) {
      secContact1 = fc2wpMappingResult.getSecondContactEffective(key);
    }
    if (null != refFc2wpMappingResult) {
      secContact2 = refFc2wpMappingResult.getSecondContactEffective(key);
    }
    return (CommonUtils.isNotEqual(secContact1, secContact2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isAgreedNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String agreed1 = "";
    String agreed2 = "";
    if (null != fc2wpMappingResult) {
      agreed1 = fc2wpMappingResult.getIsCoCAgreedUIString(key);
    }
    if (null != refFc2wpMappingResult) {
      agreed2 = refFc2wpMappingResult.getIsCoCAgreedUIString(key);
    }
    return (CommonUtils.isNotEqual(agreed1, agreed2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isAgreedOnNotEqual(final FC2WPMapping fc2wpMapping, final FC2WPMapping refFc2wpMapping) {
    String agreedOn1 = "";
    String agreedOn2 = "";
    if (null != fc2wpMapping) {
      agreedOn1 = getDateFormat(fc2wpMapping.getAgreeWithCocDate());
    }
    if (null != refFc2wpMapping) {
      agreedOn2 = getDateFormat(refFc2wpMapping.getAgreeWithCocDate());
    }
    return (CommonUtils.isNotEqual(agreedOn1, agreedOn2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isRespNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String resp1 = "";
    String resp2 = "";
    if (null != fc2wpMappingResult) {
      resp1 = fc2wpMappingResult.getAgreeWithCocRespUserDisplay(key);
    }
    if (null != refFc2wpMappingResult) {
      resp2 = refFc2wpMappingResult.getAgreeWithCocRespUserDisplay(key);
    }
    return (CommonUtils.isNotEqual(resp1, resp2));
  }

  /**
   * @param fc2wpMapping
   * @param refFc2wpMapping
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isCommentNotEqual(final FC2WPMapping fc2wpMapping, final FC2WPMapping refFc2wpMapping) {
    String comment1 = "";
    String comment2 = "";
    if (null != fc2wpMapping) {
      comment1 = fc2wpMapping.getComments();
    }
    if (null != refFc2wpMapping) {
      comment2 = refFc2wpMapping.getComments();
    }
    return (CommonUtils.isNotEqual(comment1, comment2));
  }

  /**
   * @param fc2wpMapping
   * @param refFc2wpMapping
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isFc2WpInfoNotEqual(final FC2WPMapping fc2wpMapping, final FC2WPMapping refFc2wpMapping) {
    String fc2WpInfo1 = "";
    String fc2WpInfo2 = "";
    if (null != fc2wpMapping) {
      fc2WpInfo1 = fc2wpMapping.getFc2wpInfo();
    }
    if (null != refFc2wpMapping) {
      fc2WpInfo2 = refFc2wpMapping.getFc2wpInfo();
    }
    return (CommonUtils.isNotEqual(fc2WpInfo1, fc2WpInfo2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isInIcdmNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String isInIcdm1 = "";
    String isInIcdm2 = "";
    if (null != fc2wpMappingResult) {
      isInIcdm1 = fc2wpMappingResult.getIsInICDMA2LUIString(key);
    }
    if (null != refFc2wpMappingResult) {
      isInIcdm2 = refFc2wpMappingResult.getIsInICDMA2LUIString(key);
    }
    return (CommonUtils.isNotEqual(isInIcdm1, isInIcdm2));
  }

  /**
   * @param fc2wpMappingResult
   * @param refFc2wpMappingResult
   * @param key
   * @return
   */
  public boolean isDeletedNotEqual(final FC2WPMappingResult fc2wpMappingResult,
      final FC2WPMappingResult refFc2wpMappingResult, final String key) {
    String deleted1 = "";
    String deleted2 = "";
    if (null != fc2wpMappingResult) {
      deleted1 = fc2wpMappingResult.getIsDeletedUIString(key);
    }
    if (null != refFc2wpMappingResult) {
      deleted2 = refFc2wpMappingResult.getIsDeletedUIString(key);
    }
    return (CommonUtils.isNotEqual(deleted1, deleted2));
  }
}
