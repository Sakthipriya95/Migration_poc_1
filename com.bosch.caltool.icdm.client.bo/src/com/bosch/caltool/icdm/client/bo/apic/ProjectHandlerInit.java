/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author pdh2cob
 */
public class ProjectHandlerInit {


  private AbstractProjectObjectBO projObjBO;

  private final PidcVersion pidcVersion;

  private final String level;

  private final IProjectObject projectObj;

  /**
   * @param pidcVersion
   * @param projectObj
   * @param pidcDataHandler
   * @param level
   */
  public ProjectHandlerInit(final PidcVersion pidcVersion, final IProjectObject projectObj,
      final PidcDataHandler pidcDataHandler, final String level) {
    this.pidcVersion = pidcVersion;
    this.level = level;
    this.projectObj = projectObj;
    initialize(pidcDataHandler);
  }


  /**
   * Method to initalize handler
   *
   * @param pidcDataHandler
   * @param projectObj
   */
  public void initialize(final PidcDataHandler pidcDataHandler) {
    if (this.level.equals(ApicConstants.LEVEL_PIDC_VERSION)) {
      this.projObjBO = new PidcVersionBO(this.pidcVersion, pidcDataHandler);
    }
    else if (this.level.equals(ApicConstants.LEVEL_PIDC_VARIANT)) {
      this.projObjBO = new PidcVariantBO(this.pidcVersion, (PidcVariant) this.projectObj, pidcDataHandler);
    }
    else if (this.level.equals(ApicConstants.LEVEL_PIDC_SUB_VARIANT)) {
      this.projObjBO = new PidcSubVariantBO(this.pidcVersion, (PidcSubVariant) this.projectObj, pidcDataHandler);
    }
  }

  /**
   * @return the Project Object BO
   */
  public AbstractProjectObjectBO getProjectObjectBO() {
    return this.projObjBO;
  }

}
