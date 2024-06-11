/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * Property value resolution for project attributes
 *
 * @author rvu1cob
 */
public class PIDCPagePropertiesViewSource extends AbstractDataObjectPropertySource<IProjectAttribute> {

  /**
   * Effective attribute value alias property
   */
  private static final String PROP_EFFECTIVE_VALUE_ALIAS = "Effective Value alias";
  /**
   * Effective attribute alias property
   */
  private static final String PROP_EFFECTIVE_ATTRIBUTE_ALIAS = "Effective Attribute alias";
  /**
   * Attribute super group property
   */
  private static final String PROP_ATTR_SUPER_GROUP = "Super Group";
  /**
   * Attribute group property property
   */
  private static final String PROP_ATTR_GROUP = "Group";
  /**
   * Specification property
   */
  private static final String PROP_SPECIFICATION = "Specification";
  /**
   * Part number property
   */
  private static final String PROP_PART_NUMBER = "Part Number";
  /**
   * Normalized property
   */
  private static final String PROP_ATTR_NORMALIZED = "Normalized";
  /**
   * Unit property
   */
  private static final String PROP_ATTR_UNIT = "Unit";
  /**
   * Value type property
   */
  private static final String PROP_ATTR_VAL_TYPE = "Value Type";
  /**
   * Value property
   */
  private static final String PROP_ATTR_VALUE = "Value";
  /**
   * Used property
   */
  private static final String PROP_ATTR_USED = "Used";
  /**
   * EADM name property
   */
  private static final String PROP_EADM_NAME = "EADM Name";
  /**
   * PIDC Alias definition property
   */
  private static final String PROP_PIDC_ALIAS = "PIDC Alias Definition";
  /**
   * Mandatory attribute property
   */
  private static final String PROP_MANDATORY = "Mandatory";
  /**
   * Property is not applicable
   */
  private static final String INFO_MESSAGE = "Not Applicable";
  /**
   * Attribute creation date
   */
  private static final String PROP_ATTR_CREATION_DATE = "Attribute Creation Date";
  /**
   * Focus Matrix applicable flag
   */
  private static final String PROP_FOCMTRIX_APPLICABLE = "Focus Matrix Applicable";
  /**
   * Transfer to vcdm property
   */
  private static final String PROP_TRANSFER_TO_VCDM = "Transfer to vCDM";
  /**
   * PIDC ID
   */
  private static final String PROP_PIDC_ID = "PIDC ID";
  /**
   * PIDC Version ID
   */
  private static final String PROP_PIDC_VERSION_ID = "PIDC Version ID";
  /**
   * Instance of abstract project attributebo
   */
  private final AbstractProjectAttributeBO projAttrBo;

  /**
   * Constructor
   *
   * @param rowObject defines Project Attribute
   */
  public PIDCPagePropertiesViewSource(final PidcNattableRowObject rowObject) {
    super(rowObject.getProjectAttributeHandler().getProjectAttr());
    this.projAttrBo = rowObject.getProjectAttributeHandler();
  }

  /**
   * @return Title title
   */
  @Override
  protected String getTitle() {
    final StringBuilder res = new StringBuilder();
    // get the selected object
    IProjectAttribute projAttr = getDataObject();
    // decide on the type of Project attribute and append the type string to the title
    if (projAttr instanceof PidcVersionAttribute) {
      res.append("PROJATTR : ");
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      res.append("VARATTR : ");
    }
    // ICDM-175
    else if (projAttr instanceof PidcSubVariantAttribute) {
      res.append("SUBVARATTR : ");
    }

    res.append(projAttr.getName()).append(" - PIDC : ")
        .append(this.projAttrBo.getProjectObjectBO().getPidcVersion().getName());

    return res.toString();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String result = getProjAttrPropValue(propKey);
    if (result == null) {
      result = getAttrPropValue(propKey);
    }
    return result;
  }

  /**
   * Project Attribute related properties' values
   *
   * @param propKey key
   * @param projAttr project attribute
   * @return value, if applicable, else null
   */
  private String getProjAttrPropValue(final String propKey) {
    String result = null;

    if (PROP_MANDATORY.equals(propKey)) {
      result = CommonUtils.getDisplayText(this.projAttrBo.isMandatory());
    }
    else if (PROP_ATTR_USED.equals(propKey)) {
      result = this.projAttrBo.getIsUsedEnum().getUiType();

    }
    else if (PROP_ATTR_VALUE.equals(propKey)) {
      result = this.projAttrBo.getDefaultValueDisplayName();
    }

    else if (PROP_PART_NUMBER.equals(propKey)) {
      result = getPartNumber();
    }
    else if (PROP_SPECIFICATION.equals(propKey)) {
      result = getSpecLink();
    }
    else if (PROP_PIDC_ALIAS.equals(propKey)) {
      AliasDef aliasDef =
          this.projAttrBo.getProjectObjectBO().getPidcDataHandler().getAliasDefModel().getAliasDefnition();
      result = aliasDef == null ? "" : aliasDef.getName();
    }
    else if (PROP_EFFECTIVE_ATTRIBUTE_ALIAS.equals(propKey) || PROP_EFFECTIVE_VALUE_ALIAS.equals(propKey)) {
      result = this.projAttrBo.getAliasName();
    }
    else if (PROP_FOCMTRIX_APPLICABLE.equals(propKey)) {
      result = CommonUtils.getDisplayText(this.projAttrBo.isFocusMatrixApplicable());
    }
    else if (PROP_TRANSFER_TO_VCDM.equals(propKey)) {
      result = CommonUtils.getDisplayText(this.projAttrBo.canTransferToVcdm());
    }
    else if (PROP_PIDC_ID.equals(propKey)) {
      result = String.valueOf(this.projAttrBo.getProjectObjectBO().getPidcVersion().getPidcId());
    }
    else if (PROP_PIDC_VERSION_ID.equals(propKey)) {
      result = String.valueOf(this.projAttrBo.getProjectObjectBO().getPidcVersion().getId());
    }

    return result;
  }


  /**
   * Attribute related properties' values
   *
   * @param propKey key
   * @param projAttr project attribute
   * @return value, if applicable, else null
   */
  private String getAttrPropValue(final String propKey) {
    String result = null;
    Attribute attribute = this.projAttrBo.getProjectObjectBO().getPidcDataHandler()
        .getAttribute(this.projAttrBo.getProjectAttr().getAttrId());
    if (PROP_ATTR_VAL_TYPE.equals(propKey)) {
      result = attribute.getValueType();
    }
    else if (PROP_ATTR_UNIT.equals(propKey)) {
      result = attribute.getUnit();
    }
    else if (PROP_ATTR_NORMALIZED.equals(propKey)) {
      result = CommonUtils.getDisplayText(attribute.isNormalized());
    }
    else if (PROP_ATTR_GROUP.equals(propKey)) {
      result =
          this.projAttrBo.getProjectObjectBO().getPidcDataHandler().getAttrGroup(attribute.getAttrGrpId()).getName();
    }
    else if (PROP_ATTR_SUPER_GROUP.equals(propKey)) {
      AttrGroup attrGroup =
          this.projAttrBo.getProjectObjectBO().getPidcDataHandler().getAttrGroup(attribute.getAttrGrpId());
      result = this.projAttrBo.getProjectObjectBO().getPidcDataHandler().getAttrSuperGroup(attrGroup.getSuperGrpId())
          .getName();
    }
    // ICDM-1560
    else if (PROP_EADM_NAME.equals(propKey)) {
      result = attribute.getEadmName();
    }
    else if (PROP_ATTR_CREATION_DATE.equals(propKey)) {
      result = attribute.getCreatedDate();
    }
    return result;

  }


  /**
   * @return specification link, if project attribute has spec link flag set
   */
  private String getSpecLink() {
    return CommonUtils.isNotEmptyString(this.projAttrBo.getProjectAttr().getSpecLink())
        ? this.projAttrBo.getProjectAttr().getSpecLink() : PIDCPagePropertiesViewSource.INFO_MESSAGE;
  }

  /**
   * @return part number, if project attribute has part number flag set
   */
  private String getPartNumber() {
    return CommonUtils.isNotEmptyString(this.projAttrBo.getProjectAttr().getPartNumber())
        ? this.projAttrBo.getProjectAttr().getPartNumber() : PIDCPagePropertiesViewSource.INFO_MESSAGE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    // defines all the description fields for PIDC Attr Page row objects
    return new String[] {
        PROP_PIDC_ID,
        PROP_PIDC_VERSION_ID,
        PROP_MANDATORY,
        PROP_ATTR_USED,
        PROP_ATTR_VALUE,
        PROP_ATTR_UNIT,
        PROP_ATTR_VAL_TYPE,
        PROP_PART_NUMBER,
        PROP_SPECIFICATION,
        PROP_EADM_NAME,
        PROP_ATTR_NORMALIZED,
        PROP_ATTR_GROUP,
        PROP_ATTR_SUPER_GROUP,
        PROP_PIDC_ALIAS,
        PROP_EFFECTIVE_ATTRIBUTE_ALIAS,
        PROP_EFFECTIVE_VALUE_ALIAS,
        PROP_FOCMTRIX_APPLICABLE,
        PROP_TRANSFER_TO_VCDM,
        PROP_ATTR_CREATION_DATE };
  }

}
