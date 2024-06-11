/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ProjectStructureModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author hnu1cob
 */
public class ProjectStructureValidator extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData ServiceData
   */
  public ProjectStructureValidator(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Validates whether the given Project Structure is valid, throws error when the ids in the project structure are
   * invalid
   *
   * @param inputModel ProjectStructureModel
   * @throws IcdmException exception
   */

  public void validateStructure(final ProjectStructureModel inputModel) throws IcdmException {
    Long pidcId = inputModel.getPidcId();
    Long pidcVersId = inputModel.getPidcVersId();
    Long pidcA2lId = inputModel.getPidcA2lId();
    Long varId = inputModel.getVarId();

    // Validate the IDs
    validatePidcId(pidcId);
    validatePidcVersId(pidcVersId);
    validatePidcVarID(varId);
    validatePidcA2l(pidcA2lId);

    // Validate the hierarchy
    validatePidcVersWithPidcId(pidcVersId, pidcId);
    validatePidcA2lWithPidcAndVersId(pidcA2lId, pidcVersId);
    validatePidcVarWithPidcVers(varId, pidcVersId);
    validatePidcVarWithA2l(varId, pidcA2lId);
  }

  /**
   * @param varId
   * @param pidcVersId
   * @param pidcA2lId
   * @throws IcdmException
   */
  private void validatePidcVarWithA2l(final Long varId, final Long pidcA2lId) throws IcdmException {
    if (isPidcVariantAvailable(varId) && CommonUtils.isNotNull(pidcA2lId)) {
      Map<Long, PidcVariant> variants = new PidcVariantLoader(getServiceData()).getA2lMappedVariants(pidcA2lId,false);

      if (variants.isEmpty() || !variants.containsKey(varId)) {
        throw new InvalidInputException("PIDC VariantID is invalid for the Pidc A2l ID");
      }
    }
  }

  /**
   * @param varId
   * @return
   */
  private boolean isPidcVariantAvailable(final Long varId) {
    return CommonUtils.isNotNull(varId) && CommonUtils.isNotEqual(0L, varId);
  }

  /**
   * @param varId
   * @param pidcVersId
   * @throws IcdmException
   */
  private void validatePidcVarWithPidcVers(final Long varId, final Long pidcVersId) throws IcdmException {
    if (isPidcVariantAvailable(varId) && CommonUtils.isNotNull(pidcVersId)) {
      Map<Long, PidcVariant> variants = new PidcVariantLoader(getServiceData()).getVariants(pidcVersId, false);

      if (variants.isEmpty()) {
        throw new InvalidInputException("PIDC Version does not contain variants");
      }

      if (!variants.containsKey(varId)) {
        throw new InvalidInputException("PIDC VariantID is invalid for the PIDC Version ID");
      }
    }
  }


  /**
   * @param pidcA2lId
   * @param pidcVersId
   * @param pidcId
   * @throws IcdmException
   */
  private void validatePidcA2lWithPidcAndVersId(final Long pidcA2lId, final Long pidcVersId) throws IcdmException {
    if (CommonUtils.isNotNull(pidcA2lId) && CommonUtils.isNotNull(pidcVersId)) {
      PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);

      if (CommonUtils.isNotEqual(pidcA2l.getPidcVersId(), pidcVersId)) {
        throw new InvalidInputException("PIDC A2L ID is invalid for PIDC Version ID");
      }
    }
  }

  /**
   * @param pidcVersId
   * @param pidcId
   * @throws DataException
   */
  private void validatePidcVersWithPidcId(final Long pidcVersId, final Long pidcId) throws IcdmException {
    if (CommonUtils.isNotNull(pidcId) && CommonUtils.isNotNull(pidcVersId)) {
      PidcVersion pidcVersion = new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersId);

      if (CommonUtils.isNotEqual(pidcVersion.getPidcId(), pidcId)) {
        throw new InvalidInputException("PIDC Version ID is invalid for PIDC ID");
      }
    }
  }

  private void validatePidcA2l(final Long pidcA2lId) throws InvalidInputException {
    if (CommonUtils.isNotNull(pidcA2lId)) {
      new PidcA2lLoader(getServiceData()).validateId(pidcA2lId);
    }
  }

  private void validatePidcVarID(final Long varId) throws InvalidInputException {
    if (isPidcVariantAvailable(varId)) {
      new PidcVariantLoader(getServiceData()).validateId(varId);
    }
  }

  private void validatePidcVersId(final Long pidcVersId) throws InvalidInputException {
    if (CommonUtils.isNotNull(pidcVersId)) {
      new PidcVersionLoader(getServiceData()).validateId(pidcVersId);
    }
  }

  private void validatePidcId(final Long pidcId) throws InvalidInputException {
    if (CommonUtils.isNotNull(pidcId)) {
      new PidcLoader(getServiceData()).validateId(pidcId);
    }
  }
}
