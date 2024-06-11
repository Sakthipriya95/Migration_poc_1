/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter.RELEVANT_TYPE;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class PidcRmDefinitionLoader extends AbstractBusinessObject<PidcRmDefinition, TPidcRmDefinition> {

  /**
   * @param serviceData serviceData
   */
  public PidcRmDefinitionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_RM_DEFINITION, TPidcRmDefinition.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  // TODO change this back to 'proctected' after migrating CmdModPidcVersion and CmdModPidcRmDefinition
  public PidcRmDefinition createDataObject(final TPidcRmDefinition dbPidRmDef) throws DataException {
    PidcRmDefinition pidRmDef = new PidcRmDefinition();

    pidRmDef.setId(dbPidRmDef.getPidcRmId());
    pidRmDef.setPidcVersId(dbPidRmDef.getTPidcVersion().getPidcVersId());
    pidRmDef.setRmNameEng(dbPidRmDef.getRmNameEng());
    pidRmDef.setRmNameGer(dbPidRmDef.getRmNameGer());
    pidRmDef.setRmDescEng(dbPidRmDef.getRmDescEng());
    pidRmDef.setRmDescGer(dbPidRmDef.getRmDescGer());
    String name = getLangSpecTxt(dbPidRmDef.getRmNameEng(), dbPidRmDef.getRmNameGer());
    pidRmDef.setName(name);
    String desc = getLangSpecTxt(dbPidRmDef.getRmNameGer(), dbPidRmDef.getRmDescGer());
    pidRmDef.setDesc(desc);

    setVarIds(dbPidRmDef, pidRmDef);
    setSubVarIds(dbPidRmDef, pidRmDef);
    if (dbPidRmDef.getIsVariant() != null) {
      pidRmDef.setIsVariant(dbPidRmDef.getIsVariant());
    }
    return pidRmDef;
  }

  /**
   * @param dbPidRmDef
   * @param pidRmDef
   */
  private void setSubVarIds(final TPidcRmDefinition dbPidRmDef, final PidcRmDefinition pidRmDef) {
    Set<TabvProjectSubVariant> tabvProjectSubVariants = dbPidRmDef.getTabvProjectSubVariants();

    if (tabvProjectSubVariants != null) {
      List<Long> varIds;
      if (pidRmDef.getVarIds() == null) {
        varIds = new ArrayList<>();
      }
      else {
        varIds = pidRmDef.getVarIds();
      }
      for (TabvProjectSubVariant tabvProjectVariant : tabvProjectSubVariants) {
        varIds.add(tabvProjectVariant.getSubVariantId());
        pidRmDef.setVarIds(varIds);

      }

    }

  }

  /**
   * @param dbPidRmDef
   * @param pidRmDef
   */
  private void setVarIds(final TPidcRmDefinition dbPidRmDef, final PidcRmDefinition pidRmDef) {
    Set<TabvProjectVariant> tabvProjectVariants = dbPidRmDef.getTabvProjectVariants();
    List<Long> varIds;
    if (tabvProjectVariants != null) {
      if (pidRmDef.getVarIds() == null) {
        varIds = new ArrayList<>();
      }
      else {
        varIds = pidRmDef.getVarIds();
      }
      for (TabvProjectVariant tabvProjectVariant : tabvProjectVariants) {
        varIds.add(tabvProjectVariant.getVariantId());
        pidRmDef.setVarIds(varIds);

      }

    }
  }

  /**
   * @param pidVersId pid versid
   * @return the list of rm defintions
   * @throws DataException DataException
   */
  public SortedSet<PidcRmDefinition> getPidRmDefintions(final Long pidVersId) throws DataException {
    SortedSet<PidcRmDefinition> pidRmDefintions = new TreeSet<>();

    PidcVersionLoader loader = new PidcVersionLoader(getServiceData());
    TPidcVersion pidcVersionObj = loader.getEntityObject(pidVersId);

    Set<TPidcRmDefinition> dbRmDefs = pidcVersionObj.gettPidcRmDefinitions();
    if (dbRmDefs != null) {

      for (TPidcRmDefinition dbRmDef : dbRmDefs) {
        pidRmDefintions.add(createDataObject(dbRmDef));
      }
    }
    return pidRmDefintions;
  }

  /**
   * Checks if is pidc risk evaluation is empty for pidcVersion.
   *
   * @param pidVersId the pid vers id
   * @return true, if is pidc rm relevant
   * @throws DataException DataException
   */
  public boolean isPidcRmEmpty(final long pidVersId) throws DataException {
    SortedSet<PidcRmDefinition> defList = getPidRmDefintions(pidVersId);
    for (PidcRmDefinition def : defList) {
      PidcRmProjCharacterLoader rmpidcLoader = new PidcRmProjCharacterLoader(getServiceData());
      Map<Long, PidcRmProjCharacter> pidRmProjChars = rmpidcLoader.getPidRmProjChar(def.getId());
      for (PidcRmProjCharacter projChar : pidRmProjChars.values()) {
        // If Y/N is set for atleast one entry, the sheet is NOT emtpy
        if ((projChar.getRelevant() != null) && (RELEVANT_TYPE.getType(projChar.getRelevant()) != RELEVANT_TYPE.NA)) {
          return false;
        }
      }
    }
    return true;
  }

}
