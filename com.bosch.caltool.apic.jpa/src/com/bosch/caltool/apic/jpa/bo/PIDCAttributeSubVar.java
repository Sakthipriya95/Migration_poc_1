/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * PIDCAttributeSubVar.java- This class represents subvariant attribute
 *
 * @author adn1cob
 */
@Deprecated
public class PIDCAttributeSubVar extends IPIDCAttribute {

  /**
   * Sub-VariantID
   */
  private final Long subVariantId;
  /**
   * Store used info (set during import)
   */
  private String usedInfo;
  /**
   * Store value id (set during import)
   */
  private Long valueID;


  /**
   * @return the subVariantId
   */
  public Long getSubVariantId() {
    return this.subVariantId;
  }

  /**
   * Constructor
   *
   * @param apicDataProvider dataprovider
   * @param pidcSubVarAttrID subVariantAttrID to be modified
   */
  public PIDCAttributeSubVar(final ApicDataProvider apicDataProvider, final Long pidcSubVarAttrID) {
    super(apicDataProvider, pidcSubVarAttrID);

    this.subVariantId =
        getEntityProvider().getDbPidcSubVarAttr(pidcSubVarAttrID).getTabvProjectSubVariant().getSubVariantId();

  }

  /**
   * Constructor
   *
   * @param apicDataProvider dataprovider
   * @param attrID AttributeID
   * @param variantID variant to which the sub-variant beloongs to
   * @param subVariantId the subVariant for which the attribute is created
   */
  public PIDCAttributeSubVar(final ApicDataProvider apicDataProvider, final Long attrID, final Long variantID,
      final Long subVariantId) {
    super(apicDataProvider, null, attrID,
        apicDataProvider.getEntityProvider().getDbPidcVariant(variantID).getTPidcVersion().getPidcVersId());

    this.subVariantId = subVariantId;

  }


  /**
   * {@inheritDoc} returns PIDCard object of the subvariant attribute
   */
  @Override
  public PIDCVersion getPidcVersion() {

    if (this.pidcVersID != null) {
      return getDataCache().getPidcVersion(this.pidcVersID);
    }
    else if (getID() != null) {
      return getDataCache()
          .getPidcVersion(getEntityProvider().getDbPidcSubVarAttr(getID()).getTPidcVersion().getPidcVersId());
    }
    else {
      return null;
    }
  }

  /**
   * {@inheritDoc} returns TabvAttribute
   */
  @Override
  protected TabvAttribute getDbAttribute() {
    return getEntityProvider().getDbPidcSubVarAttr(getID()).getTabvAttribute();
  }

  /**
   * {@inheritDoc} returns TabvAttrValue
   */
  @Override
  protected TabvAttrValue getDbAttrValue() {
    return getEntityProvider().getDbPidcSubVarAttr(getID()).getTabvAttrValue();
  }

  /**
   * ICDM-479 checks whether the attribute is visible in pidcard
   *
   * @return isvisible
   */
  @Override
  public boolean isVisible() {
    return !getPidcSubVariant().getInvisiblePIDCSubvarAttributesMap().containsKey(getAttribute().getID());
  }

  /**
   * {@inheritDoc} return used information in String
   */
  @Override
  protected String getUsedInfo() {


    if ((this.usedInfo != null) && !this.usedInfo.isEmpty()) {
      // Applicable only for Imported PIDC
      return this.usedInfo;
      //
    }
    return getEntityProvider().getDbPidcSubVarAttr(getID()).getUsed();
  }

  /**
   * {@inheritDoc} returns Project revision id
   */
  @Override
  protected Long getProRevId() {
    return Long.valueOf(getEntityProvider().getDbPidcSubVarAttr(getID()).getTPidcVersion().getProRevId().longValue());
  }


  /**
   * @return the SubVariant of this Variant attribute
   */
  public PIDCSubVariant getPidcSubVariant() {
    return getDataCache().getPidcSubVaraint(this.subVariantId);
  }

  /**
   * Check, if the pidc attribute ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the pidc attributeID is valid
   */
  private boolean attrIdValid() {
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()) != null;
    }
    return false;
  }

  /**
   * {@inheritDoc} returns modified user
   */
  @Override
  public String getModifiedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getModifiedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} returns modified date
   */
  @Override
  public Calendar getModifiedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcSubVarAttr(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * Method to check if subvariant attribute can be modified. Deletion check is done for variant and subvariant
   * {@inheritDoc} returns true if the subvariant attribute can be modified
   */
  @Override
  public boolean isModifiable() {
    return super.isModifiable() && !(getPidcVariant().isDeleted() || getPidcSubVariant().isDeleted());
  }

  /**
   * Method to check if subvariant attribute is modifiable. Deletion check for subvariant is not done
   *
   * @return true if the subvariant attribute can be modified
   */
  public boolean isAttributeModifiable() {

    // attribute is not modifiable if parent variant is deleted
    return super.isModifiable() && !getPidcVariant().isDeleted();
  }


  /**
   * {@inheritDoc} returns display name of the attribute value
   */
  @Override
  public String getDefaultValueDisplayName(final boolean showUnit) {

    if (!isReadable() && isHidden()) {
      return ApicConstants.HIDDEN_VALUE;
    }

    // Applicable only for Imported PIDC
    if (this.valueID != null) {
      return getDataCache().getAttrValue(this.valueID).getValue();
    }
    return getDisplayValueFromAttribute(showUnit);
  }

  /**
   * @return PIDCVariant object of the subvariant attribute
   */
  public PIDCVariant getPidcVariant() {
    return getDataCache().getPidcVaraint(
        getEntityProvider().getDbPidcSubVariant(this.subVariantId).getTabvProjectVariant().getVariantId());
  }

  /**
   * {@inheritDoc} returns false always as a sub-variant attribute can not be variant
   */
  @Override
  public boolean isVariant() {
    // a sub-variant attribute can not be variant
    return false;
  }

  /**
   * {@inheritDoc} returns created user
   */
  @Override
  public String getCreatedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} returns created date
   */
  @Override
  public Calendar getCreatedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcSubVarAttr(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc} returns part number
   */
  @Override
  public String getPartNumber() {
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getPartNumber();
    }
    return null;
  }

  /**
   * {@inheritDoc} returns specification link
   */
  @Override
  public String getSpecLink() {
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getSpecLink();
    }
    return null;
  }

  /**
   * {@inheritDoc} returns description for additional info
   */
  @Override
  public String getAdditionalInfoDesc() {
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getDescription();
    }
    return null;
  }

  /**
   * @param usedInfo the usedInfo to set
   */
  public void setUsedInfo(final String usedInfo) {
    this.usedInfo = usedInfo;
  }


  /**
   * @return the valueID
   */
  @Override
  public Long getValueID() {
    return this.valueID;
  }


  /**
   * @param valueID the valueID to set
   */
  public void setValueID(final Long valueID) {
    this.valueID = valueID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * method has been made abstract in IPIDC attribute
   */
  @Override
  public boolean canMoveDown() {
    return true;
  }

  /**
   * Icdm-883 new method to identify if the attribute is structed {@inheritDoc}
   */
  @Override
  public boolean isStructuredAttr() {
    final PIDCVersion pidcVer = getPidcVersion();
    if (pidcVer == null) {
      return false;
    }

    for (PIDCDetStructure detStruct : pidcVer.getVirtualLevelAttrs().values()) {
      if (detStruct.getAttrID().longValue() == getAttribute().getID().longValue()) {
        return true;
      }

    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isHidden() {
    return null == getParentLevelAttr() ? false : getParentLevelAttr().isHidden();
  }

  /**
   * @return This method returns the PIDC level attribute
   */
  private PIDCAttributeVar getParentLevelAttr() {
    return getPidcVariant().getVarAttribute(getAttribute().getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canTransferToVcdm() {
    // checks if the project level attribute is transferable
    return null == getParentLevelAttr() ? false : getParentLevelAttr().canTransferToVcdm();
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2241
  @Override
  public boolean isFocusMatrixApplicable() {
    return null == getParentLevelAttr() ? false : getParentLevelAttr().isFocusMatrixApplicable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPartNumber(final ApicUser user) {
    // Get part number of subvariant attribute
    if (!isReadable() && isHiddenToUser(user) && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getPartNumber();
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSpecLink(final ApicUser user) {
    // Get specification link of subvariant attribute
    if (!isReadable() && isHiddenToUser(user) && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getSpecLink();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAdditionalInfoDesc(final ApicUser user) {
    if (!isReadable() && isHiddenToUser(user) && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcSubVarAttr(getID()).getDescription();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPredefinedAttribute() {
    // Auto-generated method stub
    return false;
  }
}
