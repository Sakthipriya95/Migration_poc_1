/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.fc2wp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * The Class FC2WPMappingResult.
 *
 * @author gge6cob
 */
public class FC2WPMappingResult {

  /**
   * enum to declare the sort columns.
   */
  public enum SortColumns {

                           /** Name column. */
                           SORT_FC_NAME,

                           /** Description column. */
                           SORT_FC_LONG_NAME,

                           /** Workpackage column. */
                           SORT_WP,

                           /** Resource column. */
                           SORT_RESOURCE,

                           /** WP_ID column. */
                           SORT_WP_ID,

                           /** BC column. */
                           SORT_BC,

                           /** PT-Type column. */
                           SORT_PT_TYPE,

                           /** Contact1 column. */
                           SORT_CONTACT_1,

                           /** Contact2 column. */
                           SORT_CONTACT_2,

                           /** isAgreedWithCOC column. */
                           SORT_IS_COC_AGREED,

                           /** AgreedOn column. */
                           SORT_AGREED_ON,

                           /** RespForAgreement column. */
                           SORT_RESP_AGREEMNT,

                           /** Comments column. */
                           SORT_COMMENTS,

                           /** isInICDMA2L column. */
                           SORT_ISINICDM_A2L,

                           /** Is FC in SDOM column. */
                           SORT_IS_FC_IN_SDOM,

                           /** isDeleted column. */
                           SORT_ISDELETED,
                           /** created date column. */
                           SORT_CREATED_DATE,
                           /** modified date column. */
                           SORT_MODIFIED_DATE,

                           /** FC with PARAMS column. */
                           SORT_FC_WITH_PARAMS,

                           /** FC2WP Info column. */
                           SORT_FC2WP_INFO


  }

  /** The fc2wp vers obj. */
  private FC2WPMappingWithDetails fc2wpVersObj = null;

  private HashMap<Long, String> mappedPTtypes;

  /**
   * FC2WPVersMapping fc2wpObj.
   *
   * @param fc2wpVersObj the fc2wpversobj
   */
  public FC2WPMappingResult(final FC2WPMappingWithDetails fc2wpVersObj) {
    this.fc2wpVersObj = fc2wpVersObj;
    retreivePTtypes();
  }

  /**
   * Gets the fc2wp data set.
   *
   * @return the fc2wpDataSet
   */
  public Set<FC2WPMapping> getFc2wpDataSet() {
    return new HashSet<>(this.fc2wpVersObj.getFc2wpMappingMap().values());
  }

  /**
   * Gets the first contact effective.
   *
   * @param funcName the func name
   * @return the first contact effective
   */
  public String getFirstContactEffective(final String funcName) {
    String ret = "";
    Long userId = null;
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (null != mapping) {
      if (mapping.isUseWpDef()) {
        WorkPackageDivision wpDet = this.fc2wpVersObj.getWpDetMap().get(mapping.getWpDivId());
        if (wpDet != null) {
          userId = wpDet.getContactPersonId();
        }
      }
      else {
        userId = mapping.getContactPersonId();
      }

      if (userId != null) {
        User user = this.fc2wpVersObj.getUserMap().get(userId);
        ret = getUserDisplayName(user);
      }
    }
    return ret;
  }

  private String getUserDisplayName(final User user) {
    final StringBuilder displayName = new StringBuilder();

    if (!CommonUtils.isEmptyString(user.getLastName())) {
      displayName.append(user.getLastName()).append(", ");
    }
    if (!CommonUtils.isEmptyString(user.getFirstName())) {
      displayName.append(user.getFirstName());
    }

    if (!CommonUtils.isEmptyString(user.getDepartment())) {
      displayName.append(" (").append(user.getDepartment()).append(")");
    }

    if (CommonUtils.isEmptyString(displayName.toString())) {
      displayName.append(user.getName());
    }


    return displayName.toString();
  }

  /**
   * Gets the workpackage.
   *
   * @param funcName the func name
   * @return the workpackage
   */
  public String getWorkpackage(final String funcName) {
    String retStr = "";
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if ((null != mapping) && this.fc2wpVersObj.getWpDetMap().containsKey(mapping.getWpDivId())) {
      WorkPackageDivision wpDet = this.fc2wpVersObj.getWpDetMap().get(mapping.getWpDivId());
      retStr = wpDet.getWpName();
    }
    return !CommonUtils.isEmptyString(retStr) ? retStr : "";
  }

  /**
   * Gets the resource.
   *
   * @param funcName the func name
   * @return the resource
   */
  public String getResource(final String funcName) {
    String retStr = "";
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if ((mapping != null) && this.fc2wpVersObj.getWpDetMap().containsKey(mapping.getWpDivId())) {
      WorkPackageDivision wpDet = this.fc2wpVersObj.getWpDetMap().get(mapping.getWpDivId());
      retStr = wpDet.getWpResource();
    }
    return !CommonUtils.isEmptyString(retStr) ? retStr : "";
  }


  /**
   * Gets the wp-MCR ID.
   *
   * @param funcName the func name
   * @return the wp id MCR
   */
  public String getWpIdMCR(final String funcName) {
    String retStr = "";
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if ((mapping != null) && this.fc2wpVersObj.getWpDetMap().containsKey(mapping.getWpDivId())) {
      WorkPackageDivision wpDet = this.fc2wpVersObj.getWpDetMap().get(mapping.getWpDivId());
      retStr = wpDet.getWpIdMcr();
    }
    return !CommonUtils.isEmptyString(retStr) ? retStr : "";
  }

  /**
   * Gets the pt-type UI string.
   *
   * @param funcName the key
   * @return the pt-type UI string
   */
  public String getPTtypeUIString(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    TreeSet<String> list = new TreeSet<>();
    if (null != mapping) {
      for (Long id : mapping.getPtTypeSet()) {
        list.add(this.mappedPTtypes.get(id));
      }
      return mapping.getPtTypeSet() != null ? StringUtils.join(list, ',') : "";
    }
    return "";
  }

  /**
   * Gets the checks if is CoC agreed UI string.
   *
   * @param funcName the key
   * @return the checks if is CoC agreed UI string
   */
  public String getIsCoCAgreedUIString(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (null != mapping) {
      return mapping.isAgreeWithCoc() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
    }
    return "";
  }

  /**
   * Gets the second contact effective.
   *
   * @param funcName the key
   * @return the second contact effective
   */
  public String getSecondContactEffective(final String funcName) {
    String ret = "";
    Long userId = null;
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (null != mapping) {
      if (mapping.isUseWpDef()) {
        WorkPackageDivision wpDet = this.fc2wpVersObj.getWpDetMap().get(mapping.getWpDivId());
        if (wpDet != null) {
          userId = wpDet.getContactPersonSecondId();
        }
      }
      else {
        userId = mapping.getContactPersonSecondId();
      }

      if (userId != null) {
        User user = this.fc2wpVersObj.getUserMap().get(userId);
        ret = getUserDisplayName(user);
      }
    }
    return ret;

  }

  /**
   * Gets the checks if is in ICDMA2LUI string.
   *
   * @param funcName the key
   * @return the checks if is in ICDMA2LUI string
   */
  public String getIsInICDMA2LUIString(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (mapping == null) {
      return "";
    }
    return mapping.isUsedInIcdm() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * @param funcName the key
   * @return the checks if is in Is FC In Sdom UI string
   */
  public String getIsFcInSdomUIString(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (mapping == null) {
      return "";
    }
    return mapping.isFcInSdom() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * Gets the checks if is deleted UI string.
   *
   * @param funcName the key
   * @return the checks if is deleted UI string
   */
  public String getIsDeletedUIString(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (mapping == null) {
      return "";
    }
    return mapping.isDeleted() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * Gets the checks if is WP assigned.
   *
   * @param funcName the func name
   * @return the checks if is WP assigned
   */
  public boolean getIsWPAssigned(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    String retStr = "";
    if (this.fc2wpVersObj.getWpDetMap().containsKey(mapping.getWpDivId())) {
      WorkPackageDivision wpDet = this.fc2wpVersObj.getWpDetMap().get(mapping.getWpDivId());
      retStr = wpDet.getWpName();
    }
    return !CommonUtils.isEmptyString(retStr);
  }


  /**
   * Gets the checks if is contact assigned.
   *
   * @param funcName the func name
   * @return the checks if is contact assigned
   */
  public boolean getIsContactAssigned(final String funcName) {
    return !CommonUtils.isEmptyString(getFirstContactEffective(funcName)) ||
        !CommonUtils.isEmptyString(getSecondContactEffective(funcName));
  }

  /**
   * @param funcName the key
   * @return the checks if FC with params is Y or N
   */
  public String isFcWithParams(final String funcName) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (mapping == null) {
      return null;
    }
    return mapping.isFcWithParams() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * Compare to.
   *
   * @param value1 the value1
   * @param value2 the value2
   * @param sortColumn column to be sorted
   * @return compareResult
   */

  public int compareTo(final FC2WPMapping value1, final FC2WPMapping value2, final SortColumns sortColumn) {
    String key1 = value1.getFunctionName();
    String key2 = value2.getFunctionName();
    int compareResult;
    switch (sortColumn) {
      case SORT_FC_NAME:
        compareResult = ApicUtil.compare(value1.getFunctionName(), value2.getFunctionName());
        break;
      case SORT_FC_LONG_NAME:
        compareResult = ApicUtil.compare(value1.getFunctionDesc(), value2.getFunctionDesc());
        break;
      case SORT_WP:
        compareResult = ApicUtil.compare(getWorkpackage(key1), getWorkpackage(key2));
        break;
      case SORT_RESOURCE:
        compareResult = ApicUtil.compare(getResource(key1), getResource(key2));
        break;
      case SORT_WP_ID:
        compareResult = ApicUtil.compare(getWpIdMCR(key1), getWpIdMCR(key2));
        break;
      case SORT_BC:
        compareResult = ApicUtil.compare(getBC(key1), getBC(key2));
        break;
      case SORT_PT_TYPE:
        compareResult = ApicUtil.compare(getPTtypeUIString(key1), getPTtypeUIString(key2));
        break;
      case SORT_CONTACT_1:
        compareResult = ApicUtil.compare(getFirstContactEffective(key1), getFirstContactEffective(key2));
        break;
      case SORT_CONTACT_2:
        compareResult = ApicUtil.compare(getSecondContactEffective(key1), getSecondContactEffective(key2));
        break;
      case SORT_IS_COC_AGREED:
        compareResult = ApicUtil.compare(value1.isAgreeWithCoc(), value2.isAgreeWithCoc());
        break;
      case SORT_AGREED_ON:
        compareResult = ApicUtil.compare(value1.getAgreeWithCocDate(), value2.getAgreeWithCocDate());
        break;
      case SORT_RESP_AGREEMNT:
        compareResult = ApicUtil.compare(getAgreeWithCocRespUserDisplay(key1), getAgreeWithCocRespUserDisplay(key2));
        break;
      case SORT_COMMENTS:
        compareResult = ApicUtil.compare(value1.getComments(), value2.getComments());
        break;
      case SORT_ISINICDM_A2L:
        compareResult = ApicUtil.compare(value1.isUsedInIcdm(), value2.isUsedInIcdm());
        break;
      case SORT_IS_FC_IN_SDOM:
        compareResult = ApicUtil.compare(value1.isFcInSdom(), value2.isFcInSdom());
        break;
      case SORT_ISDELETED:
        compareResult = ApicUtil.compare(value1.isDeleted(), value2.isDeleted());
        break;
      case SORT_CREATED_DATE:
        compareResult = ApicUtil.compare(value1.getCreatedDate(), value2.getCreatedDate());
        break;
      case SORT_MODIFIED_DATE:
        compareResult = ApicUtil.compare(value1.getModifiedDate(), value2.getModifiedDate());
        break;
      case SORT_FC_WITH_PARAMS:
        compareResult = ApicUtil.compare(value1.isFcWithParams(), value2.isFcWithParams());
        break;
      case SORT_FC2WP_INFO:
        compareResult = ApicUtil.compare(value1.getFc2wpInfo(), value2.getFc2wpInfo());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(value1.getFunctionName(), value2.getFunctionName());
    }

    return compareResult;
  }

  /**
   * Gets the checks if is relevant PT-type.
   *
   * @param funcName the func name
   * @param list2 the list2
   * @return the checks if is relevant PT-type
   */
  public boolean getIsRelevantPTtype(final String funcName, final List<String> list2) {
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (CommonUtils.isNotEmpty(mapping.getPtTypeSet())) {
      for (Long id : mapping.getPtTypeSet()) {
        if (list2.contains(this.mappedPTtypes.get(id))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Retreive PTtypes.
   */
  private void retreivePTtypes() {
    this.mappedPTtypes = new HashMap<>();
    if ((this.fc2wpVersObj != null) && CommonUtils.isNotEmpty(this.fc2wpVersObj.getPtTypeMap())) {
      for (long ptTypeID : this.fc2wpVersObj.getPtTypeMap().keySet()) {
        this.mappedPTtypes.put(ptTypeID, this.fc2wpVersObj.getPtTypeMap().get(ptTypeID).getPtTypeName());
      }
    }
  }

  /**
   * @param funcName function name (Key)
   * @return display name of responsible user
   */
  public String getAgreeWithCocRespUserDisplay(final String funcName) {
    String ret = "";
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (null != mapping) {
      Long userId = mapping.getAgreeWithCocRespUserId();
      if (userId != null) {
        User user = this.fc2wpVersObj.getUserMap().get(userId);
        ret = getUserDisplayName(user);
      }
    }
    return ret;
  }

  /**
   * Gets the BC information.
   *
   * @param funcName the func name
   * @return the bc name
   */
  public String getBC(final String funcName) {
    String ret = "";
    FC2WPMapping mapping = this.fc2wpVersObj.getFc2wpMappingMap().get(funcName);
    if (null != mapping) {
      Long bcID = mapping.getBcID();
      if (this.fc2wpVersObj.getBcMap().containsKey(bcID)) {
        ret = this.fc2wpVersObj.getBcMap().get(bcID).getName();
      }
    }
    return ret;
  }

  /**
   * Gets the sorted mapping result.
   *
   * @return the sorted mapping result
   */
  public SortedSet<FC2WPMapping> getSortedMappingResult() {
    final SortedSet<FC2WPMapping> reportData = new TreeSet<>(
        (final FC2WPMapping param1, final FC2WPMapping param2) -> compareTo(param1, param2, SortColumns.SORT_FC_NAME));
    reportData.addAll(getFc2wpDataSet());
    return reportData;
  }

  /**
   * @param dateInStr String
   * @return String
   */

  public String setDateFormat(final String dateInStr) {
    SimpleDateFormat formatter =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_04, Locale.getDefault(Locale.Category.FORMAT));
    try {
      Date date = formatter.parse(dateInStr);
      String formattedDate = "";
      if (date != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
      }
      return formattedDate;
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error("Date not valid", Activator.PLUGIN_ID);
    }

    return "";
  }
}