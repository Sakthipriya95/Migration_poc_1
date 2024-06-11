/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * Property view source for project objects - PIDC, version, variant, sub-variant
 *
 * @author bne4cob
 */
public class ProjectObjectPropertiesViewSource extends AbstractDataObjectPropertySource<IProjectObject> {

  /**
   * Key for PIDC Description
   */
  private static final String PROP_PIDC_DESC = "PIDC Description";

  /**
   * Key for PIDC Version's status
   */
  private static final String PROP_VERS_STATUS = "Version Status";

  /**
   * Key for PIDC Version's is active
   */
  private static final String PROP_VERS_IS_ACTIVE = "Is Active";

  /**
   * Key for vCDM Last Transfer User
   */
  private static final String PROP_VCDM_LAST_TRNSFR_USR = "vCDM Last Transfer User";

  /**
   * Key for vCDM Last Transfer Date
   */
  private static final String PROP_VCDM_LAST_TRNSFR_DATE = "vCDM Last Transfer Date";

  /**
   * Key for Pidc alias name
   */
  private static final String PIDC_ALIAS_NAME = "PIDC Alias Name";

  /**
   * Key for Pidc Version Link
   */
  private static final String PIDC_VERSION_LINK = "PIDC Version Link";

  /**
   * Key for Pidc Version Link Target
   */
  private static final String PIDC_VERSION_LINK_TARGET = "PIDC Version Link(Target)";

  /**
   * Key for Variant Version Link
   */
  private static final String VARIANT_VERSION_LINK = "Variant Version Link";

  /**
   * Key for Variant Version Link Target
   */
  private static final String VARIANT_VERSION_LINK_TARGET = "Variant Version Link(Target)";

  /**
   * Key for Variant Version Link
   */
  private static final String SUB_VARIANT_VERSION_LINK = "Sub Variant Version Link";

  /**
   * Key for Variant Version Link Target
   */
  private static final String SUB_VARIANT_VERSION_LINK_TARGET = "Sub Variant Version Link(Target)";
  /**
   * instance of project bo
   */
  private final AbstractProjectObjectBO projectObjectBO;


  /**
   * Constructor
   *
   * @param projObjBo - instance of AbstractProjectObjectBO
   * @param projectObject project object
   */
  public ProjectObjectPropertiesViewSource(final AbstractProjectObjectBO projObjBo,
      final IProjectObject projectObject) {
    super(projectObject);
    this.projectObjectBO = projObjBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    IProjectObject projObj = getDataObject();
    if (projObj instanceof PidcVersion) {
      return isPidcVersionValid() ? getPidcVersPropertyValue(propKey) : "";
    }
    else if (projObj instanceof PidcVariant) {
      return getVariantVersPropertyValue(propKey, projObj);
    }
    else if (projObj instanceof PidcSubVariant) {
      return getSubVariantVersPropertyValue(propKey, projObj);
    }
    return null;
  }

  /**
   * @param propKey
   * @param projObj
   * @return
   */
  private String getSubVariantVersPropertyValue(final String propKey, final IProjectObject projObj) {
    String value = "";
    LinkCreator linkCreator = new LinkCreator(projObj);
    if (SUB_VARIANT_VERSION_LINK.equals(propKey)) {
      value = linkCreator.getDisplayText();
    }
    else if (SUB_VARIANT_VERSION_LINK_TARGET.equals(propKey)) {
      value = linkCreator.getUrl();
    }
    return value;
  }

  /**
   * @param propKey
   * @param projObj
   * @return
   */
  private String getVariantVersPropertyValue(final String propKey, final IProjectObject projObj) {
    String value = "";
    LinkCreator linkCreator = new LinkCreator(projObj);
    if (VARIANT_VERSION_LINK.equals(propKey)) {
      value = linkCreator.getDisplayText();
    }
    else if (VARIANT_VERSION_LINK_TARGET.equals(propKey)) {
      value = linkCreator.getUrl();
    }
    return value;
  }

  /**
   * PIDC Version Object's property values
   *
   * @param propKey key
   * @param result value
   * @return
   */
  private String getPidcVersPropertyValue(final String propKey) {
    String value = "";

    PidcVersion pidcVersion = (PidcVersion) getDataObject();
    LinkCreator linkCreator = new LinkCreator(pidcVersion);
    switch (propKey) {
      case PROP_PIDC_DESC:
        value = this.projectObjectBO.getPidcDataHandler().getPidcVersionInfo().getPidc().getDescription();
        break;
      case PROP_VERS_STATUS:
        value = PidcVersionStatus.getStatus(pidcVersion.getPidStatus()).getUiStatus();
        break;
      case PROP_VERS_IS_ACTIVE:
        value = this.projectObjectBO.isPidcVersionActive(pidcVersion) ? ApicConstants.USED_YES_DISPLAY
            : ApicConstants.USED_NO_DISPLAY;
        break;
      case PROP_VCDM_LAST_TRNSFR_USR:
        value = this.projectObjectBO.getPidcDataHandler().getPidcVersionInfo().getPidc().getVcdmTransferUser();
        break;
      case PROP_VCDM_LAST_TRNSFR_DATE:
        value = this.projectObjectBO.getPidcDataHandler().getPidcVersionInfo().getPidc().getVcdmTransferDate() == null
            ? "" : this.projectObjectBO.getPidcDataHandler().getPidcVersionInfo().getPidc().getVcdmTransferDate();
        break;
      case PIDC_ALIAS_NAME:
        if (this.projectObjectBO.getPidcDataHandler().getAliasDefModel() != null) {
          value = this.projectObjectBO.getPidcDataHandler().getAliasDefModel().getAliasDefnition() == null ? ""
              : this.projectObjectBO.getPidcDataHandler().getAliasDefModel().getAliasDefnition().getName();
        }
        break;
      case PIDC_VERSION_LINK:
        value = linkCreator.getDisplayText();
        break;
      case PIDC_VERSION_LINK_TARGET:
        value = linkCreator.getUrl();
        break;
      default:
        break;
    }
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    IProjectObject projObj = getDataObject();

    String prefix = "";
    if (projObj instanceof PidcVersion) {// ProjectID Card version
      prefix = "PIDCVERS: ";
    }
    else if (projObj instanceof PidcVariant) { // PIDC Variant
      prefix = "VARIANT: ";
    }
    else if (projObj instanceof PidcSubVariant) { // PIDC SubVariant
      prefix = "SUBVARIANT: ";
    }
    return prefix + projObj.getName();

  }

  /**
   * @return true if pidc version is valid
   */
  private boolean isPidcVersionValid() {
    return (getDataObject() instanceof PidcVersion) && CommonUtils
        .isEqual(this.projectObjectBO.getPidcVersion().getPidcId(), ((PidcVersion) getDataObject()).getPidcId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    String[] descFields;

    if (getDataObject() instanceof PidcVersion) {// ProjectID Card
      descFields = new String[] {
          PIDC_ALIAS_NAME,
          PROP_PIDC_DESC,
          PROP_VERS_STATUS,
          PROP_VERS_IS_ACTIVE,
          PROP_VCDM_LAST_TRNSFR_USR,
          PROP_VCDM_LAST_TRNSFR_DATE,
          PIDC_VERSION_LINK,
          PIDC_VERSION_LINK_TARGET };
    }
    else if (getDataObject() instanceof PidcVariant) {
      descFields = new String[] { VARIANT_VERSION_LINK, VARIANT_VERSION_LINK_TARGET };
    }
    else if (getDataObject() instanceof PidcSubVariant) {
      descFields = new String[] { SUB_VARIANT_VERSION_LINK, SUB_VARIANT_VERSION_LINK_TARGET };
    }
    else {
      descFields = super.getDescFields();
    }

    return descFields;
  }
}
