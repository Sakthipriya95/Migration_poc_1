/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class PidcVariantAttributeBO extends AbstractProjectAttributeBO {

  private final PidcVariantAttribute projectVarAttr;

  private final PidcVariantBO variantHandler;

  /**
   * @param projectVarAttr Pidc Variant Attribute
   * @param variantHandler Pidc Variant Handler
   */
  public PidcVariantAttributeBO(final PidcVariantAttribute projectVarAttr, final PidcVariantBO variantHandler) {
    super(projectVarAttr, variantHandler);
    this.projectVarAttr = projectVarAttr;
    this.variantHandler = variantHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    return super.isModifiable() && !this.variantHandler.getPidcVariant().isDeleted();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDefaultValueDisplayName(final boolean showUnit) {

    if (this.projectVarAttr.isAtChildLevel()) {
      return ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME;
    }
    NodeAccess access;
    try {
      access = this.currentUser.getNodeAccessRight(this.variantHandler.getPidcVersion().getPidcId());
      if ((access != null) && !access.isRead() && this.projectVarAttr.isAttrHidden()) {
        return ApicConstants.HIDDEN_VALUE;
      }

      // Applicable only for Imported PIDC
      if (this.projectVarAttr.getValueId() != null) {
        return this.projectVarAttr.getValue();
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
    if (this.variantHandler.getPidcDataHandler().getAttributeValueMap().get(this.projectVarAttr.getValueId()) == null) {
      return "";
    }
    return this.variantHandler.getPidcDataHandler().getAttributeValueMap().get(this.projectVarAttr.getValueId())
        .getTextValueEng();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canMoveDown() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canTransferToVcdm() {
    PidcVersionAttribute pidcAttr =
        this.variantHandler.getPidcDataHandler().getPidcVersAttrMap().get(getProjectVarAttr().getAttrId());
    return pidcAttr.isTransferToVcdm();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isVisible() {
    if (this.variantHandler.getPidcDataHandler().getVariantInvisbleAttributeMap()
        .get(this.projectVarAttr.getVariantId()) != null) {
      return !this.variantHandler.getPidcDataHandler().getVariantInvisbleAttributeMap()
          .get(this.projectVarAttr.getVariantId()).contains(this.projectAttr.getAttrId());
    }
    return true;
  }


  /**
   * @return the projectVarAttr
   */
  public PidcVariantAttribute getProjectVarAttr() {
    return this.projectVarAttr;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDefaultValueDisplayName() {
    return getDefaultValueDisplayName(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixApplicable() {
    PidcVersionAttribute pidcAttr =
        this.variantHandler.getPidcDataHandler().getPidcVersAttrMap().get(getProjectVarAttr().getAttrId());
    return pidcAttr.isFocusMatrixApplicable();
  }


}
