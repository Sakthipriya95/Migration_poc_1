/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public class PidcVersionAttributeBO extends AbstractProjectAttributeBO {

  private final PidcVersionAttribute pidcVersAttr;

  private final PidcVersionBO pidcVersionBO;

  /**
   * @param pidcVersAttr Pidc Version Attribute
   * @param pidcVersionBO Pidc Version Handler
   */
  public PidcVersionAttributeBO(final PidcVersionAttribute pidcVersAttr, final PidcVersionBO pidcVersionBO) {
    super(pidcVersAttr, pidcVersionBO);
    this.pidcVersAttr = pidcVersAttr;
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * iCDM-957 <br>
   * Get the tooltip text, if any value is set for the pidCard attribute.<br>
   * Returns null (to disable tool tip) if value is not set!
   *
   * @return the tooltip for the value set along with description, <Variant> as tooltip if moved to variant and as
   *         <sub-variant> if moved to sub-variant.
   */
  @Override
  public String getValueTooltip() {

    if (!getDefaultValueDisplayName().isEmpty()) {
      StringBuilder tooltip = new StringBuilder("Value : ");
      // iCDM-2094
      String defaultValueDisplayName = getDefaultValueDisplayName(true);
      tooltip.append(defaultValueDisplayName);
      String valDesc = getValueDescription();
      if (!CommonUtils.isEmptyString(valDesc)) {
        if (this.pidcVersAttr.getValueType().equals(AttributeValueType.ICDM_USER.toString())) {
          tooltip.append("\nDescription :\n").append(valDesc);
        }
        else {
          tooltip.append("\nDescription :").append(valDesc);
        }
      }
      return tooltip.toString();
    }
    // return null , to disable tooltip when no value is set
    return null;

  }


  @Override
  public boolean isModifiable() {

    boolean isModifiable = false;
    NodeAccess curUserAccRight;
    try {
      curUserAccRight = this.currentUser.getNodeAccessRight(this.projObjBO.getPidcVersion().getPidcId());

      // the attribute can be modified if the user can modify the PIDC
      if ((curUserAccRight != null) && curUserAccRight.isWrite() &&
          new ApicDataBO().isPidcUnlockedInSession(this.projObjBO.getPidcVersion())) {
        // structure attributes can not be modified
        isModifiable = !((this.projObjBO.getPidcDataHandler().getAttributeMap().get(this.projectAttr.getAttrId())
            .getLevel() > 0) || this.projObjBO.isDeleted() ||
            (this.projObjBO.getPidcVersion().getPidStatus().equals(PidcVersionStatus.LOCKED.getDbStatus())));

      }
    }
    catch (ApicWebServiceException ex) {

      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return isModifiable;
  }


  /**
   * returns the display name of the value
   */
  @Override
  public String getDefaultValueDisplayName(final boolean showUnit) {

    if (this.pidcVersAttr.isAtChildLevel()) {
      return ApicConstants.VARIANT_ATTR_DISPLAY_NAME;
    }
    NodeAccess access;
    try {
      access = this.currentUser.getNodeAccessRight(this.pidcVersionBO.getPidcVersion().getPidcId());
      if ((access != null) && !access.isRead() && this.pidcVersAttr.isAttrHidden()) {
        return ApicConstants.HIDDEN_VALUE;
      }

      // Applicable only for Imported PIDC
      if (this.pidcVersAttr.getValueId() != null) {
        return this.pidcVersAttr.getValue();
      }
      return getDisplayValueFromAttribute();

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return "";
  }

  /**
   * @return the display value from attribute
   */
  protected final String getDisplayValueFromAttribute() {
    if (this.pidcVersAttr.getValue() == null) {
      return "";
    }
    return this.pidcVersAttr.getValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canMoveDown() {
    return getPidcVersAttr().isCanMoveDown();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canTransferToVcdm() {
    return getPidcVersAttr().isTransferToVcdm();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isVisible() {
    return !this.pidcVersionBO.getPidcDataHandler().getPidcVersInvisibleAttrSet()
        .contains(this.projectAttr.getAttrId());
  }


  /**
   * @return the pidcVersAttr
   */
  public PidcVersionAttribute getPidcVersAttr() {
    return this.pidcVersAttr;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixApplicable() {
    return this.pidcVersAttr.isFocusMatrixApplicable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDefaultValueDisplayName() {
    return getDefaultValueDisplayName(false);

  }


}
