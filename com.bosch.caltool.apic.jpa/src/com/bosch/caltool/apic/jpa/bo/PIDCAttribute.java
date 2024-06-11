package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents Attribute in PIDC as stored in TABV_PROJECT_ATTR
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
@Deprecated
public class PIDCAttribute extends IPIDCAttribute {

  /**
   * Store used info (set during import)
   */
  private String usedInfo;
  /**
   * Store value id (set during import)
   */
  private Long valueID;


  /**
   * Constructor, if attribute is defined for this PIDC
   *
   * @param apicDataProvider DataProvider
   * @param pidcAttrID PIDC attribute ID
   */
  public PIDCAttribute(final ApicDataProvider apicDataProvider, final Long pidcAttrID) {

    super(apicDataProvider, pidcAttrID);
    getDataCache().getAllPidcAttrMap().put(pidcAttrID, this);

  }

  /**
   * Constructor, if attribute is not defined for this PIDC
   *
   * @param apicDataProvider DataProvider
   * @param pidcAttrID PIDC attribute ID
   * @param attrID Attribute ID
   * @param pidcVersionID PIDC ID
   */
  public PIDCAttribute(final ApicDataProvider apicDataProvider, final Long pidcAttrID, final Long attrID,
      final Long pidcVersionID) {

    super(apicDataProvider, pidcAttrID, attrID, pidcVersionID);
  }

  /**
   * {@inheritDoc} returns TabvAttribute
   */
  @Override
  protected TabvAttribute getDbAttribute() {
    return getEntityProvider().getDbPidcAttr(getID()).getTabvAttribute();
  }

  /**
   * {@inheritDoc} returns TabvAttrValue
   */
  @Override
  protected TabvAttrValue getDbAttrValue() {
    return getEntityProvider().getDbPidcAttr(getID()).getTabvAttrValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    if (getPidcAttrID() != null) {
      return getEntityProvider().getDbPidcAttr(getPidcAttrID()).getVersion();
    }
    return 0L;
  }

  /**
   * {@inheritDoc} returns information string on whether the attribute is used in the PIDC
   */
  @Override
  protected String getUsedInfo() {

    if ((this.usedInfo != null) && !this.usedInfo.isEmpty()) {
      // Applicable only for Imported PIDC
      return this.usedInfo;
      //
    }
    if (getID() != null) {
      // attribute is defined in the PIDC, get the used flag
      return getEntityProvider().getDbPidcAttr(getID()).getUsed();
    }
    // attribute is not defined in the PIDC, return attributes Mandatory value
    if (getAttribute().isMandatory()) {
      return ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType();
    }
    return ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType();
  }

  /**
   * {@inheritDoc} returns the project revision id
   */
  @Override
  protected Long getProRevId() {
    return Long.valueOf(getEntityProvider().getDbPidcAttr(getID()).getTPidcVersion().getProRevId().longValue());
  }

  /**
   * ICDM-479 checks whether the attribute is visible in pidcard
   *
   * @return isvisible
   */
  @Override
  public boolean isVisible() {
    return !getPidcVersion().getInvisibleAttributes().contains(getAttribute());
  }

  /**
   * {@inheritDoc} returns the display name of the value
   */
  @Override
  public String getDefaultValueDisplayName(final boolean showUnit) {

    if (isVariant()) {
      return ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
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
   * {@inheritDoc} returns the PIDCard object
   */
  @Override
  public PIDCVersion getPidcVersion() {

    if (this.pidcVersID != null) {
      return getDataCache().getPidcVersion(this.pidcVersID);
    }
    else if (getID() != null) {
      return getDataCache()
          .getPidcVersion(getEntityProvider().getDbPidcAttr(getID()).getTPidcVersion().getPidcVersId());
    }
    else {
      return null;
    }
  }

  /**
   * {@inheritDoc} returns true if the value of the project can be modified
   */
  @Override
  public boolean isModifiable() {
    boolean isModifiable;

    isModifiable = super.isModifiable();

    if (isModifiable) {
      // check special conditions of this class

      // attribute is not modifiable if it is variant or sub variant
      isModifiable = !(isVariant());
    }

    return isModifiable;
  }

  /**
   * Check, if the pidc attribute ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the pidc attributeID is valid
   */
  // ICDM-86
  private boolean attrIdValid() {
    if (getID() != null) {
      return getEntityProvider().getDbPidcAttr(getID()) != null;
    }
    return false;
  }

  /**
   * {@inheritDoc} returns the modified user
   */
  // ICDM-86
  @Override
  public String getModifiedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbPidcAttr(getID()).getModifiedUser();
    }
    return "";
  }

  // ICDM-86
  /**
   * {@inheritDoc} returns the modified date
   */
  @Override
  public Calendar getModifiedDate() {

    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcAttr(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc} returns true if the attribute is variant attribute
   */
  @Override
  public boolean isVariant() {

    boolean isVariant;

    isVariant =
        ((getID() != null) && getEntityProvider().getDbPidcAttr(getID()).getIsVariant().equals(ApicConstants.YES)) ||
            (getAttribute().getAttrLevel() == ApicConstants.VARIANT_CODE_ATTR);

    return isVariant;
  }

  /**
   * {@inheritDoc} returns the created user
   */
  @Override
  public String getCreatedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbPidcAttr(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} returns the created date
   */
  @Override
  public Calendar getCreatedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcAttr(getID()).getCreatedDate());
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
      return getEntityProvider().getDbPidcAttr(getID()).getPartNumber();
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
      return getEntityProvider().getDbPidcAttr(getID()).getSpecLink();
    }
    return null;
  }

  /**
   * {@inheritDoc} returns description of additional info
   */
  @Override
  public String getAdditionalInfoDesc() {
    if (!isReadable() && isHidden() && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcAttr(getID()).getDescription();
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
   * {@inheritDoc} //ICDM 656 : This method checks for whether the attribute can be moved to from PIDC level to variant
   * level
   */
  @Override
  public boolean canMoveDown() {
    // ICDM-2430
    return getAttribute().canMoveDown();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return null;
  }

  /**
   * Icdm-883 {@inheritDoc}
   */
  @Override
  public boolean isStructuredAttr() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isHidden() {
    return (getID() != null) &&
        ApicConstants.YES.equals(getEntityProvider().getDbPidcAttr(getID()).getAttrHiddenFlag());
  }

  /**
   * {@inheritDoc}
   * <p>
   *
   * @return This flag tells if the pidcattr can be transfered to vcdm or not
   */
  @Override
  public boolean canTransferToVcdm() {
    return (getID() != null) &&
        ApicConstants.YES.equals(getEntityProvider().getDbPidcAttr(getID()).getTrnsfrVcdmFlag());

  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2241
  @Override
  public boolean isFocusMatrixApplicable() {
    return (getID() != null) && ApicConstants.YES.equals(getEntityProvider().getDbPidcAttr(getID()).getFocusMatrixYn());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPartNumber(final ApicUser user) {
    if (!isReadable() && isHiddenToUser(user) && !isVariant()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (getID() != null) {
      return getEntityProvider().getDbPidcAttr(getID()).getPartNumber();
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
      return getEntityProvider().getDbPidcAttr(getID()).getSpecLink();
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
      return getEntityProvider().getDbPidcAttr(getID()).getDescription();
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