/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.cocwp;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcSubVarCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVariantCocWp;
import com.bosch.caltool.icdm.database.entity.apic.cocwp.TPidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;

/**
 * @author say8cob
 */
public class PidcCocWpUpdationBO extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData as input
   */
  public PidcCocWpUpdationBO(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Method to update the entity related to PidcVersCocWp
   *
   * @param inputData as PidcVersCocWp
   */
  public void updatePidcVersCocWpReferenceEntity(final PidcVersCocWp inputData) {

    TPidcVersion tpidcVersion = new PidcVersionLoader(getServiceData()).getEntityObject(inputData.getPidcVersId());
    TPidcVersCocWp tpidcVersCocWP = new PidcVersCocWpLoader(getServiceData()).getEntityObject(inputData.getId());
    TWorkpackageDivision wrkPkgDiv =
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(inputData.getWPDivId());

    if (CommonUtils.isNotEmpty(tpidcVersion.gettPidcVersCocWp()) &&
        tpidcVersion.gettPidcVersCocWp().contains(tpidcVersCocWP)) {
      tpidcVersion.gettPidcVersCocWp().remove(tpidcVersCocWP);
    }

    if (CommonUtils.isNotEmpty(wrkPkgDiv.gettPidcVersCocWp()) &&
        wrkPkgDiv.gettPidcVersCocWp().contains(tpidcVersCocWP)) {
      wrkPkgDiv.gettPidcVersCocWp().remove(tpidcVersCocWP);
    }
    if (tpidcVersion.gettPidcVersCocWp() == null) {
      tpidcVersion.settPidcVersCocWp(new ArrayList<>());
    }
    if (wrkPkgDiv.gettPidcVersCocWp() == null) {
      wrkPkgDiv.settPidcVersCocWp(new ArrayList<>());
    }
    tpidcVersion.gettPidcVersCocWp().add(tpidcVersCocWP);
    wrkPkgDiv.gettPidcVersCocWp().add(tpidcVersCocWP);
  }

  /**
   * Method to update the entity related to PidcVariantCocWp
   *
   * @param inputData as PidcVariantCocWp
   */
  public void updatePidcVarsCocWpReferenceEntity(final PidcVariantCocWp inputData, final COMMAND_MODE commandMode) {

    // Add or update or delete Entity in parent entity based on the input action
    TabvProjectVariant tpidcVar = new PidcVariantLoader(getServiceData()).getEntityObject(inputData.getPidcVariantId());
    TWorkpackageDivision twrkPkgDiv =
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(inputData.getWPDivId());
    TPidcVariantCocWp tPidcVarCocWp = new PidcVariantCocWpLoader(getServiceData()).getEntityObject(inputData.getId());

    if (tpidcVar.gettPidcVarCocWp().contains(tPidcVarCocWp)) {
      tpidcVar.gettPidcVarCocWp().remove(tPidcVarCocWp);
    }

    if (twrkPkgDiv.gettPidcVariantCocWp().contains(tPidcVarCocWp)) {
      twrkPkgDiv.gettPidcVariantCocWp().remove(tPidcVarCocWp);
    }

    // If the operation is delete, then no need to add pidcVarCocWp entity to parent entity
    if (CommonUtils.isNotEqual(commandMode, COMMAND_MODE.DELETE)) {
      tpidcVar.gettPidcVarCocWp().add(tPidcVarCocWp);
      twrkPkgDiv.gettPidcVariantCocWp().add(tPidcVarCocWp);
    }
  }

  /**
   * Method to update the entity related to PidcSubVarCocWp
   *
   * @param inputData as PidcSubVarCocWp
   */
  public void updatePidcSubVarsCocWpReferenceEntity(final PidcSubVarCocWp inputData, final COMMAND_MODE commandMode) {

    TabvProjectSubVariant tpidcSubVar =
        new PidcSubVariantLoader(getServiceData()).getEntityObject(inputData.getPidcSubVarId());
    TPidcSubVarCocWp tPidcSubVarCocWp = new PidcSubVarCocWpLoader(getServiceData()).getEntityObject(inputData.getId());
    TWorkpackageDivision tWrkPkgDiv =
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(inputData.getWPDivId());

    if (tpidcSubVar.gettPidcSubVarCocWp().contains(tPidcSubVarCocWp)) {
      tpidcSubVar.gettPidcSubVarCocWp().remove(tPidcSubVarCocWp);
    }

    if (tWrkPkgDiv.gettPidcSubVarCocWp().contains(tPidcSubVarCocWp)) {
      tWrkPkgDiv.gettPidcSubVarCocWp().remove(tPidcSubVarCocWp);
    }

    // If the operation is delete, then no need to add pidcSubVarCocWp entity to parent entity
    if (CommonUtils.isNotEqual(COMMAND_MODE.DELETE, commandMode)) {
      tpidcSubVar.gettPidcSubVarCocWp().add(tPidcSubVarCocWp);
      tWrkPkgDiv.gettPidcSubVarCocWp().add(tPidcSubVarCocWp);
    }
  }

  /**
   * @param listOfUCAddedAsProjFav
   * @param tabvUseCaseGroup
   */
  public void setUcSecFromUcGrp(final List<TabvUseCaseSection> listOfUCAddedAsProjFav,
      final TabvUseCaseGroup tabvUseCaseGroup) {

    List<TabvUseCase> tabvUseCases = tabvUseCaseGroup.getTabvUseCases();
    if (CommonUtils.isNull(tabvUseCases)) {
      for (TabvUseCaseGroup childUcGrp : tabvUseCaseGroup.getTabvUseCaseGroups()) {
        setUcSecFromUcGrp(listOfUCAddedAsProjFav, childUcGrp);
      }
    }
    else {
      for (TabvUseCase tabvUseCase : tabvUseCases) {
        for (TabvUseCaseSection ucSec : tabvUseCase.getTabvUseCaseSections()) {
          setUcSecFromUcSec(listOfUCAddedAsProjFav, ucSec);
        }
      }
    }
  }


  /**
   * @param listOfUCAddedAsProjFav
   * @param tabvUseCas
   */
  public void setUcSecFromUcSec(final List<TabvUseCaseSection> listOfUCAddedAsProjFav, final TabvUseCaseSection ucSec) {

    List<TabvUseCaseSection> childUCSections = ucSec.getTabvUseCaseSections();
    if (CommonUtils.isNotNull(childUCSections)) {
      listOfUCAddedAsProjFav.add(ucSec);
      for (TabvUseCaseSection childUcSec : childUCSections) {
        setUcSecFromUcSec(listOfUCAddedAsProjFav, childUcSec);
      }
    }
    else {
      listOfUCAddedAsProjFav.add(ucSec);
    }
  }

}
