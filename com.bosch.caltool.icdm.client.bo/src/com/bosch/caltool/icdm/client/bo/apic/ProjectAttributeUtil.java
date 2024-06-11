/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author pdh2cob
 */
public class ProjectAttributeUtil {

  /**
   *
   */
  public ProjectAttributeUtil() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param projectAttribute
   * @param projObjBO
   * @return project attribute handler
   */
  public AbstractProjectAttributeBO getProjectAttributeHandler(final IProjectAttribute projectAttribute,
      final AbstractProjectObjectBO projObjBO) {
    AbstractProjectAttributeBO projectAttrHandler = null;
    if (projObjBO instanceof PidcVersionBO) {
      projectAttrHandler =
          new PidcVersionAttributeBO((PidcVersionAttribute) projectAttribute, (PidcVersionBO) projObjBO);
    }
    else if (projObjBO instanceof PidcVariantBO) {
      projectAttrHandler =
          new PidcVariantAttributeBO((PidcVariantAttribute) projectAttribute, (PidcVariantBO) projObjBO);
    }
    else if (projObjBO instanceof PidcSubVariantBO) {
      projectAttrHandler = new PidcSubVariantAttributeBO((PidcSubVariantAttribute) projectAttribute,
          (PidcSubVariantBO) projObjBO);
    }
    return projectAttrHandler;
  }


  /**
   * @param pidcAttribute
   * @return
   */
  public Long getID(final IProjectAttribute pidcAttribute) {
    Long id = null;
    if (pidcAttribute instanceof PidcVersionAttribute) {
      id = ((PidcVersionAttribute) pidcAttribute).getPidcVersId();
    }
    else if (pidcAttribute instanceof PidcVariantAttribute) {
      id = ((PidcVariantAttribute) pidcAttribute).getVariantId();
    }
    else if (pidcAttribute instanceof PidcSubVariantAttribute) {
      id = ((PidcSubVariantAttribute) pidcAttribute).getSubVariantId();
    }
    return id;
  }


  /**
   * @param projectObject
   * @return
   */
  public Long getPidcVersionId(final IProjectObject projectObject) {

    Long id = null;
    if (projectObject instanceof PidcVersion) {
      id = ((PidcVersion) projectObject).getId();
    }
    else if (projectObject instanceof PidcVariant) {
      id = ((PidcVariant) projectObject).getPidcVersionId();
    }
    else if (projectObject instanceof PidcSubVariant) {
      id = ((PidcSubVariant) projectObject).getPidcVersionId();
    }
    return id;

  }


  /**
   * @param projectAttr
   * @return
   */
  public String getProjectObjectLevel(final IProjectAttribute projectAttr) {
    if (projectAttr instanceof PidcVersionAttribute) {
      return ApicConstants.LEVEL_PIDC_VERSION;
    }
    else if (projectAttr instanceof PidcVariantAttribute) {
      return ApicConstants.LEVEL_PIDC_VARIANT;
    }
    else if (projectAttr instanceof PidcSubVariantAttribute) {
      return ApicConstants.LEVEL_PIDC_SUB_VARIANT;
    }
    return null;
  }

  public IProjectObject getProjectObject(final PidcDataHandler pidcDataHandler, final IProjectAttribute projectAttr) {
    if (projectAttr instanceof PidcVersionAttribute) {
      return pidcDataHandler.getPidcVersionInfo().getPidcVersion();
    }
    else if (projectAttr instanceof PidcVariantAttribute) {
      return pidcDataHandler.getVariantMap().get(getID(projectAttr));
    }
    else if (projectAttr instanceof PidcSubVariantAttribute) {
      return pidcDataHandler.getSubVariantMap().get(getID(projectAttr));
    }
    return null;
  }


}
