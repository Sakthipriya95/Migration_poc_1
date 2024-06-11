/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author pdh2cob
 */
public class PidcSubVariantAttributeBO extends AbstractProjectAttributeBO {


  private final PidcSubVariantAttribute projectSubVarAttr;

  private final PidcSubVariantBO pidcSubVariantBO;

  /**
   * @param projectSubVarAttr Pidc Sub-Variant Attribute
   * @param pidcSubVarHandler Pidc Sub-Variant Handler
   */
  public PidcSubVariantAttributeBO(final PidcSubVariantAttribute projectSubVarAttr,
      final PidcSubVariantBO pidcSubVariantBO) {
    super(projectSubVarAttr, pidcSubVariantBO);
    this.projectSubVarAttr = projectSubVarAttr;
    this.pidcSubVariantBO = pidcSubVariantBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    return super.isModifiable() && !(this.pidcSubVariantBO.getPidcDataHandler().getVariantMap()
        .get(this.pidcSubVariantBO.getPidcSubVariant().getPidcVariantId()).isDeleted() ||
        this.pidcSubVariantBO.getPidcSubVariant().isDeleted());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDefaultValueDisplayName(final boolean showUnit) {
    NodeAccess access;
    try {
      access = this.currentUser.getNodeAccessRight(this.pidcSubVariantBO.getPidcVersion().getPidcId());
      if ((access != null) && !access.isRead() && this.projectSubVarAttr.isAttrHidden()) {
        return ApicConstants.HIDDEN_VALUE;
      }

      // Applicable only for Imported PIDC
      if (this.projectSubVarAttr.getValueId() != null) {
        return this.projectSubVarAttr.getValue();
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
    if (this.pidcSubVariantBO.getPidcDataHandler().getAttributeValueMap()
        .get(this.projectSubVarAttr.getValueId()) == null) {
      return "";
    }
    return this.pidcSubVariantBO.getPidcDataHandler().getAttributeValueMap().get(this.projectSubVarAttr.getValueId())
        .getTextValueEng();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canMoveDown() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canTransferToVcdm() {
    PidcVersionAttribute pidcAttr =
        this.pidcSubVariantBO.getPidcDataHandler().getPidcVersAttrMap().get(getProjectSubVarAttr().getAttrId());
    return pidcAttr.isFocusMatrixApplicable();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isVisible() {
    if (this.pidcSubVariantBO.getPidcDataHandler().getSubVariantInvisbleAttributeMap()
        .get(this.projectSubVarAttr.getSubVariantId()) != null) {
      return !this.pidcSubVariantBO.getPidcDataHandler().getSubVariantInvisbleAttributeMap()
          .get(this.projectSubVarAttr.getSubVariantId()).contains(this.projectAttr.getAttrId());
    }
    return true;
  }


  /**
   * @return the projectSubVarAttr
   */
  public PidcSubVariantAttribute getProjectSubVarAttr() {
    return this.projectSubVarAttr;
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
        this.pidcSubVariantBO.getPidcDataHandler().getPidcVersAttrMap().get(getProjectSubVarAttr().getAttrId());
    return pidcAttr.isFocusMatrixApplicable();
  }


}
