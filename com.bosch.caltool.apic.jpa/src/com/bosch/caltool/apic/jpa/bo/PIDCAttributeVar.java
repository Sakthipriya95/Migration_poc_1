/**
 *
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents Variant attribute as stored in the database table TABV_VARIANTS_ATTR
 *
 * @author hef2fe
 */
@Deprecated
public class PIDCAttributeVar extends IPIDCAttribute {

  private final Long variantId;

  /**
   * Store used info (set during import)
   */
  private String usedInfo;
  /**
   * Store value id (set during import)
   */
  private Long valueID;


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }

  /**
   * Constructor
   *
   * @param apicDataProvider dataprovider
   * @param pidcVarAttrID variantAttrID to be modified
   */
  public PIDCAttributeVar(final ApicDataProvider apicDataProvider, final Long pidcVarAttrID) {
    super(apicDataProvider, pidcVarAttrID);

    this.variantId =
        apicDataProvider.getEntityProvider().getDbPidcVarAttr(pidcVarAttrID).getTabvProjectVariant().getVariantId();

  }

  /**
   * Constructor
   *
   * @param apicDataProvider dataprovider
   * @param attrID AttributeID
   * @param variantId the Variant for which the attribute is created
   */
  public PIDCAttributeVar(final ApicDataProvider apicDataProvider, final Long attrID, final Long variantId) {
    super(apicDataProvider, null, attrID,
        apicDataProvider.getEntityProvider().getDbPidcVariant(variantId).getTPidcVersion().getPidcVersId());

    this.variantId = variantId;

  }

  /**
   * {@inheritDoc} returns TabvAttribute of the variant attribute
   */
  @Override
  protected TabvAttribute getDbAttribute() {
    return getEntityProvider().getDbPidcVarAttr(getID()).getTabvAttribute();
  }

  /**
   * {@inheritDoc} returns TabvValue of the variant attribute
   */
  @Override
  protected TabvAttrValue getDbAttrValue() {
    return getEntityProvider().getDbPidcVarAttr(getID()).getTabvAttrValue();
  }

  /**
   * ICDM-479 checks whether the attribute is visible in pidcard
   *
   * @return isvisible
   */
  @Override
  public boolean isVisible() {
    return !getPidcVariant().getInvisiblePIDCVarAttributesMap().containsKey(getAttribute().getID());
  }

  /**
   * {@inheritDoc} returns info on whether the attribute is used or not
   */
  @Override
  protected String getUsedInfo() {

    if ((this.usedInfo != null) && !this.usedInfo.isEmpty()) {
      // Applicable only for Imported PIDC
      return this.usedInfo;
      //
    }
    return getEntityProvider().getDbPidcVarAttr(getID()).getUsed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getVersion().longValue();
    }
    return 0L;
  }

  /**
   * {@inheritDoc} return project revision id
   */
  @Override
  protected Long getProRevId() {
    return Long.valueOf(getEntityProvider().getDbPidcVarAttr(getID()).getTPidcVersion().getProRevId().longValue());
  }

  /**
   * {@inheritDoc} return display name of the variant attribute value
   */
  @Override
  public String getDefaultValueDisplayName(final boolean showUnit) {

    if (isVariant()) {
      return ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME;
    }
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
   * {@inheritDoc} return PIDCard object of the variant
   */
  @Override
  public PIDCVersion getPidcVersion() {

    if (this.pidcVersID != null) {
      return getDataCache().getPidcVersion(this.pidcVersID);
    }
    else if (getID() != null) {
      return getDataCache()
          .getPidcVersion(getEntityProvider().getDbPidcVarAttr(getID()).getTPidcVersion().getPidcVersId());
    }
    else {
      return null;
    }
  }

  /**
   * @return the Variant of this Variant attribute
   */
  public PIDCVariant getPidcVariant() {
    return getDataCache().getPidcVaraint(this.variantId);
  }

  /**
   * Check, if the pidc attribute ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the pidc attributeID is valid
   */
  // ICDM-86
  private boolean attrIdValid() {
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()) != null;
    }
    return false;
  }

  /**
   * {@inheritDoc} returns modified user
   */
  // ICDM-86
  @Override
  public String getModifiedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getModifiedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} returns modified date
   */
  // ICDM-86
  @Override
  public Calendar getModifiedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcVarAttr(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc} returns true if the variant attribute can be modified
   */
  @Override
  public boolean isModifiable() {
    // attribute is not modifiable if it is variant or sub variant ICDM-182
    return isAttributeModifiable() && !isVariant();
  }

  /**
   * {@inheritDoc} returns true if the attribute is sub variant
   */
  @Override
  public boolean isVariant() {
    // Check if the attribute is present in sub variant
    boolean isSubVariant;

    isSubVariant = ((getID() != null) &&
        getEntityProvider().getDbPidcVarAttr(getID()).getIsSubVariant().equals(ApicConstants.YES)) ||
        (getAttribute().getAttrLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR);

    return isSubVariant;
  }


  /**
   * Method to check if attribute is modifiable
   *
   * @return true if attribute is modifiable, false if not modifiable
   */
  public boolean isAttributeModifiable() {

    // attribute is not modifiable if variant is deleted
    return super.isModifiable() && !getPidcVariant().isDeleted();

  }


  /**
   * {@inheritDoc} returns created user
   */
  @Override
  public String getCreatedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} returns created date
   */
  @Override
  public Calendar getCreatedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcVarAttr(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc} returns part number
   */
  @Override
  public String getPartNumber() {
    // Get part number of variant attribute
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getPartNumber();
    }
    return null;
  }

  /**
   * {@inheritDoc} returns specification link
   */
  @Override
  public String getSpecLink() {
    // Get specification link of variant attribute
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getSpecLink();
    }
    return null;
  }

  /**
   * {@inheritDoc} returns description for additional information
   */
  @Override
  public String getAdditionalInfoDesc() {
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getDescription();
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
   * This method checks for whether the variant attr can be moved to subVar level
   */
  @Override
  public boolean canMoveDown() {
    return true;
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
    // checks if the project level attribute is hidden
    return null == getParentLevelAttr() ? false : getParentLevelAttr().isHidden();
  }

  /**
   * @return This method returns the PIDC level attribute
   */
  private PIDCAttribute getParentLevelAttr() {
    // returns the pidc level attribute
    return getPidcVersion().getAttributes(false).get(getAttribute().getID());
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
    // Get part number of variant attribute
    if (!isReadable() && isHiddenToUser(user) && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getPartNumber();
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSpecLink(final ApicUser user) {
    if (!isReadable() && isHiddenToUser(user) && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcVarAttr(getID()).getSpecLink();
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
      return getEntityProvider().getDbPidcVarAttr(getID()).getDescription();
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
