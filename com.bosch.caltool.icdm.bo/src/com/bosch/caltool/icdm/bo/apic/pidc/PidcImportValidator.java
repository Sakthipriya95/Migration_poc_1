/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;

import org.apache.poi.ss.usermodel.DateUtil;

import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateAndNumValidator;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author dja7cob
 */
public class PidcImportValidator extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData Service Data
   */
  public PidcImportValidator(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param excelAttr excel attribute
   * @param pidcVerAttr pidcversion attribute
   * @return boolean to check whether any attribute field is changed
   */
  public boolean isAttrFieldChanged(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    return isAttrValUsedFlagChanged(excelAttr, pidcVerAttr) || isPartNumChange(excelAttr, pidcVerAttr) ||
        isSpecLinkChange(excelAttr, pidcVerAttr) || isAdInfochange(excelAttr, pidcVerAttr);
  }

  /**
   * @param excelAttr
   * @param pidcVerAttr
   * @return
   */
  private boolean isAttrValUsedFlagChanged(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    return isUsedFlagChange(excelAttr, pidcVerAttr) || isAttrValChange(excelAttr, pidcVerAttr);
  }

  /**
   * @param excelAttr
   * @param pidcVerAttr
   * @return
   */
  private boolean isAdInfochange(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    return !isEqual(excelAttr.getAdditionalInfoDesc(), pidcVerAttr.getAdditionalInfoDesc());
  }

  /**
   * @param excelAttr
   * @param pidcVerAttr
   * @return
   */
  private boolean isSpecLinkChange(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    return !isEqual(excelAttr.getSpecLink(), pidcVerAttr.getSpecLink());
  }

  /**
   * @param excelAttr
   * @param pidcVerAttr
   * @return
   */
  private boolean isPartNumChange(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    return !isEqual(excelAttr.getPartNumber(), pidcVerAttr.getPartNumber());
  }

  private boolean isEqual(final String str1, final String str2) {
    // If both values are empty or null they should be considered as equal for import
    if (CommonUtils.isEmptyString(str1) && CommonUtils.isEmptyString(str2)) {
      return true;
    }
    return str1.equals(str2);
  }


  /**
   * @param excelAttr
   * @param pidcVerAttr
   * @return
   */
  private boolean isAttrValChange(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    String attrVal = null;
    if (pidcVerAttr instanceof PidcVersionAttribute) {
      if (pidcVerAttr.isAtChildLevel()) {
        attrVal = ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
      }
      else {
        attrVal = pidcVerAttr.getValue();
      }
    }
    else if (pidcVerAttr instanceof PidcVariantAttribute) {
      if (pidcVerAttr.isAtChildLevel()) {
        attrVal = ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME;
      }
      else {
        attrVal = pidcVerAttr.getValue();
      }
    }
    else if (pidcVerAttr instanceof PidcSubVariantAttribute) {
      attrVal = pidcVerAttr.getValue();
    }
    return !CommonUtils.isEqual(excelAttr.getValue(), attrVal);
  }

  /**
   * @param excelAttr
   * @param pidcVerAttr
   * @return
   */
  private boolean isUsedFlagChange(final IProjectAttribute excelAttr, final IProjectAttribute pidcVerAttr) {
    return !CommonUtils.isEqual(excelAttr.getUsedFlag(),
        PROJ_ATTR_USED_FLAG.getType(pidcVerAttr.getUsedFlag()).getDbType());
  }

  /**
   * @param pidcAttr pidc attribute
   * @return boolean whether attribute is a division attribute
   */
  public boolean isQnaireConfigAttr(final IProjectAttribute pidcAttr) {
    return (null != pidcAttr.getValueId()) && pidcAttr.getAttrId()
        .equals(Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR)));
  }

  /**
   * @param pidcAttr pidc attribute
   * @return boolean whther division attribute is used in review
   */
  public boolean isQnaireConfigAttrUsedInRvw(final IProjectAttribute pidcAttr) {
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    Long pidcVersionId = null;
    if (pidcAttr instanceof PidcVersionAttribute) {
      pidcVersionId = ((PidcVersionAttribute) pidcAttr).getPidcVersId();
    }
    else if (pidcAttr instanceof PidcVariantAttribute) {
      pidcVersionId = ((PidcVariantAttribute) pidcAttr).getPidcVersionId();
    }
    else if (pidcAttr instanceof PidcSubVariantAttribute) {
      pidcVersionId = ((PidcSubVariantAttribute) pidcAttr).getPidcVersionId();
    }
    return attrValLoader.isQnaireConfigValUsedInRvw(pidcVersionId);
  }

  /**
   * @param pidcAttr pidc attribute
   * @return flag to check edit is allowed
   */
  public boolean isAttrEditNotAllowed(final IProjectAttribute pidcAttr) {
    return (null != pidcAttr) && (!isModifiable(pidcAttr));
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean isModifiable(final IProjectAttribute pidcAttr) {
    NodeAccessLoader nodeAccessLoader = new NodeAccessLoader(getServiceData());
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Long pidcVersionId = null;
    if (pidcAttr instanceof PidcVersionAttribute) {
      pidcVersionId = ((PidcVersionAttribute) pidcAttr).getPidcVersId();
    }
    else if (pidcAttr instanceof PidcVariantAttribute) {
      pidcVersionId = ((PidcVariantAttribute) pidcAttr).getPidcVersionId();
    }
    else if (pidcAttr instanceof PidcSubVariantAttribute) {
      pidcVersionId = ((PidcSubVariantAttribute) pidcAttr).getPidcVersionId();
    }
    try {
      PidcVersion pidcVer = pidcVerLoader.getDataObjectByID(pidcVersionId);
      NodeAccess userNodeAccess = nodeAccessLoader.getAllNodeAccessForCurrentUser().get(pidcVer.getPidcId());
      if (validateUserNodeAccess(userNodeAccess) && checkPidcVerStatus(pidcVer) &&
          (attrLoader.getDataObjectByID(pidcAttr.getAttrId()).getLevel() <= 0)) {
        return true;
      }
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);

    }

    return false;
  }

  /**
   * @param pidcVer
   * @return
   */
  private boolean checkPidcVerStatus(final PidcVersion pidcVer) {
    return !pidcVer.isDeleted() && !pidcVer.getPidStatus().equals(ApicConstants.PIDC_STATUS_ID_LOCKED_STR);
  }

  /**
   * @param userNodeAccess
   * @return
   */
  private boolean validateUserNodeAccess(final NodeAccess userNodeAccess) {
    return (null != userNodeAccess) && userNodeAccess.isWrite();
  }

  /**
   * @param excelAttr excel attribute
   * @param attribute attribute
   * @return boolean whether value type is valid
   */
  public boolean isValueTypeValid(final IProjectAttribute excelAttr, final Attribute attribute) {
    boolean isValidAttrType = true;
    String excelVal = excelAttr.getValue();
    String attrValType = attribute.getValueType();
    if ((null != excelVal) && !excelVal.isEmpty()) {
      // By default, for TEXT flag is true
      if (attrValType.equals(AttributeValueType.HYPERLINK.toString())) {
        isValidAttrType = validateHyperlink(excelVal);
      }
      else if (attrValType.equals(AttributeValueType.NUMBER.toString())) {
        isValidAttrType = validateNumber(excelVal);
      }
      else if (attrValType.equals(AttributeValueType.DATE.toString())) {
        try {
          validateDate(excelAttr, attribute);
          isValidAttrType = true;
        }
        catch (ParseException | IcdmException e) {
          getLogger().error(e.getMessage(), e);
          isValidAttrType = false;
        }
      }
      else if (attrValType.equals(AttributeValueType.BOOLEAN.toString())) {
        isValidAttrType = validateBoolean(excelVal);
      }
      else if (attrValType.equals(AttributeValueType.ICDM_USER.toString())) {
        try {
          isValidAttrType = validateUserType(excelVal);
        }
        catch (DataException e) {
          getLogger().error(e.getMessage(), e);
        }
      }
    }
    return isValidAttrType;
  }

  /**
   * @param displayName
   * @return
   * @throws DataException
   */
  private boolean validateUserType(final String displayName) throws DataException {
    String displayNameToCompare = displayName.replaceAll(",", "").trim();
    SortedSet<User> allApicUsers = new UserLoader(getServiceData()).getAllApicUsers(false);
    for (User user : allApicUsers) {
      if (user.getDescription().replaceAll(",", "").trim().equals(displayNameToCompare)) {
        return true;
      }
    }
    UserInfo userInfo = null;
    try {
      userInfo = new LdapAuthenticationWrapper().getUserDetailsForFullName(displayNameToCompare);
    }
    catch (LdapException e) {
      getLogger().error(e.getMessage(), e);
    }
    return (userInfo != null);
  }

  /**
   * @param value
   * @return
   */
  private boolean validateNumber(final String value) {
    boolean isValidAttrType;
    try {
      new BigDecimal(value);
      isValidAttrType = true;
    }
    catch (NumberFormatException nfe) {
      isValidAttrType = false;
    }
    return isValidAttrType;
  }

  /**
   * @param value
   * @return
   */
  private boolean validateHyperlink(final String value) {
    boolean isValidAttrType;
    String url = value;
    if (CommonUtils.isValidURLFormat(value)) {
      url = CommonUtils.formatUrl(value);
    }
    isValidAttrType = CommonUtils.isValidHyperlinkFormat(url);
    return isValidAttrType;
  }

  /**
   * @param value
   * @return
   */
  private boolean validateBoolean(final String value) {
    boolean isValidAttrType;
    if ("true".equalsIgnoreCase(value.trim()) || "false".equalsIgnoreCase(value.trim())) {
      isValidAttrType = true;
    }
    else {
      isValidAttrType = false;
    }
    return isValidAttrType;
  }

  /**
   * @param excelAttr
   * @param attribute
   * @throws ParseException
   * @throws IcdmException
   */
  private void validateDate(final IProjectAttribute excelAttr, final Attribute attribute)
      throws ParseException, IcdmException {
    String format = attribute.getFormat();
    if ((format == null) || " ".equals(format)) {
      format = "yyyy-MM-dd HH:mm:ss";
    }
    String temp = DateAndNumValidator.getInstance().formatDate(format);
    SimpleDateFormat sdf = new SimpleDateFormat(temp, Locale.getDefault(Locale.Category.FORMAT));
    sdf.setLenient(false);
    Date parsedDate;
    String strDate;
    if (!excelAttr.getValue().contains(".")) {
      try {
        parsedDate = DateUtil.getJavaDate(Double.parseDouble(excelAttr.getValue()));
        strDate = sdf.format(parsedDate);
        // set the parsed date value to excelAttr since it holds the double value
        excelAttr.setValue(strDate);
      }
      catch (Exception exp) {
        // For invalid date values
        throw new IcdmException(exp.getMessage(), exp);
      }
    }
    else {
      parsedDate = sdf.parse(excelAttr.getValue());
      strDate = sdf.format(parsedDate);
      if (!strDate.equals(excelAttr.getValue())) {
        throw new ParseException("Invalid Date", strDate.length());
      }
    }
  }

  /**
   * @param excelAttr Excel attribute
   * @param pidcAttr Pidc/var/subvar attribute
   * @return flag to indicate spec link can be edited
   */
  public boolean isInvalidSpecLinkEdit(final IProjectAttribute excelAttr, final IProjectAttribute pidcAttr) {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    try {
      return (null != excelAttr.getSpecLink()) && !excelAttr.getSpecLink().trim().isEmpty() &&
          !attrLoader.getDataObjectByID(pidcAttr.getAttrId()).isWithSpecLink();
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);
    }
    return false;
  }

  /**
   * @param excelAttr Imported attr (pidc/var/subvar)
   * @param attr pidc/var/subvar attr
   * @return flag to indicate modified value is valid
   */
  public boolean isModifiedValueValid(final IProjectAttribute excelAttr, final Attribute attr) {

    String modValue = excelAttr.getValue();
    if (null == modValue) {
      return true;
    }
    return !(modValue.equals(ApicConstants.VARIANT_ATTR_DISPLAY_NAME) ||
        modValue.equals(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME) || modValue.equals(ApicConstants.HIDDEN_VALUE));
  }

  /**
   * @param pidcAttrValId pidc/var/subvar attr val id
   * @return boolean to indicate attr val is deleted or rejected
   */
  public boolean isAttrValDelRejected(final Long pidcAttrValId) {
    if (null != pidcAttrValId) {
      AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
      try {
        AttributeValue pidcAttrVal = attrValLoader.getDataObjectByID(pidcAttrValId);
        if ((null != pidcAttrVal) && (null != pidcAttrVal.getValueType()) && pidcAttrVal.isDeleted()) {
          return true;
        }
      }
      catch (DataException e) {
        getLogger().error(e.getMessage(), e);
      }
    }
    return false;
  }

  /**
   * @param excelAttr imported pidc/var/sub var attr
   * @param projAttr project attribute
   * @param attr attribute
   * @return flag to indicate used flag combination in excel is valid
   */
  public boolean isUsedFlagValComboInValid(final IProjectAttribute excelAttr, final IProjectAttribute projAttr,
      final Attribute attr) {
    // For boolean attributes it should not be possible to set Used flag as "YES" and value to "FALSE"
    // And Used flag "NO" with "TRUE" or "FALSE" should also not possible - not possible via UI
    if ((AttributeValueType.BOOLEAN == AttributeValueType.getType(attr.getValueTypeId())) && hasUsedFlag(excelAttr)) {
      return true;
    }
    if ((null != projAttr.getUsedFlag()) &&
        !projAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType()) &&
        (null != excelAttr.getUsedFlag()) &&
        excelAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType())) {
      return true;
    }
    if (null == excelAttr.getValue()) {
      return false;
    }
    if ((null != projAttr.getUsedFlag()) && ((null == excelAttr.getUsedFlag()) ||
        ((null != excelAttr.getUsedFlag()) && excelAttr.getUsedFlag().isEmpty()))) {
      return true;
    }


    return (!excelAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType()) &&
        !excelAttr.getValue().isEmpty()) ||
        CommonUtils.isEqual(excelAttr.getUsedFlag(), ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType());

  }

  /**
   * @param excelAttr
   * @return
   */
  private boolean hasUsedFlag(final IProjectAttribute excelAttr) {
    return ((null != excelAttr.getUsedFlag()) && (null != excelAttr.getValue())) && isExcelAttrUsedFlagSame(excelAttr);
  }

  /**
   * @param excelAttr
   * @return
   */
  private boolean isExcelAttrUsedFlagSame(final IProjectAttribute excelAttr) {
    return isExcelAttrUsedFlagYes(excelAttr) || isExcelAttrUsedFlagNo(excelAttr);
  }

  /**
   * @param excelAttr
   * @return
   */
  private boolean isExcelAttrUsedFlagNo(final IProjectAttribute excelAttr) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(excelAttr.getUsedFlag()) &&
        !excelAttr.getValue().isEmpty();
  }

  /**
   * @param excelAttr
   * @return
   */
  private boolean isExcelAttrUsedFlagYes(final IProjectAttribute excelAttr) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(excelAttr.getUsedFlag()) &&
        ApicConstants.BOOLEAN_FALSE_STRING.equals(excelAttr.getValue());
  }


}
