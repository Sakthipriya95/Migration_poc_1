/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.Collection;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author bne4cob
 */
public class ProjectAttributeLoader extends AbstractSimpleBusinessObject {

  /**
   * Specifies level to which model is to be loaded. Every level includes loading data preceding the level
   *
   * @author bne4cob
   */
  public enum LOAD_LEVEL {
                          /**
                           * Load pidc version attributes too
                           */
                          L1_PROJ_ATTRS,
                          /**
                           * Load variants too
                           */
                          L2_VARIANTS,
                          /**
                           * Load variant attributes too
                           */
                          L3_VAR_ATTRS,
                          /**
                           * Load sub variants too
                           */
                          L4_SUB_VARIANTS,
                          /**
                           * Load sub variant attributes too
                           */
                          L5_SUBVAR_ATTRS,
                          /**
                           * Load everything
                           */
                          L6_ALL;
  }

  /**
   * Load level. Set to default
   */
  private LOAD_LEVEL loadLevel = LOAD_LEVEL.L5_SUBVAR_ATTRS;
  /**
   * Output
   */
  private PidcVersionAttributeModel model;

  /**
   * @param serviceData Service Data
   */
  public ProjectAttributeLoader(final ServiceData serviceData) {
    super(serviceData);

  }

  /**
   * @param pidcVersionID pidc Version ID
   * @return model
   * @throws IcdmException error while retrieving data
   */
  public PidcVersionAttributeModel createModel(final Long pidcVersionID) throws IcdmException {
    return createModel(pidcVersionID, LOAD_LEVEL.L5_SUBVAR_ATTRS);
  }

  /**
   * @param pidcVersionID pidc Version ID
   * @param loadLvl Load level
   * @return model
   * @throws IcdmException error while retrieving data
   */
  public PidcVersionAttributeModel createModel(final Long pidcVersionID, final LOAD_LEVEL loadLvl)
      throws IcdmException {
    return createModel(pidcVersionID, loadLvl, true);
  }

  /**
   * @param pidcVersionID pidc Version ID
   * @param loadLvl Load level
   * @param includeDeleted if true, deleted variants and sub variants are also considered
   * @return model
   * @throws IcdmException error while retrieving data
   */
  public PidcVersionAttributeModel createModel(final Long pidcVersionID, final LOAD_LEVEL loadLvl,
      final boolean includeDeleted)
      throws IcdmException {

    long startTime = System.currentTimeMillis();

    getLogger().debug("Creating project attribute model for PIDC Version ID {} ...", pidcVersionID);

    this.loadLevel = loadLvl == null ? LOAD_LEVEL.L5_SUBVAR_ATTRS : loadLvl;

    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(pidcVersionID);
    if (pidcVersLdr.isHiddenToCurrentUser(pidcVers.getId())) {
      throw new UnAuthorizedAccessException(
          "The PIDC Version with ID " + pidcVers.getId() + " is not accessible to current user");
    }

    Pidc pidc = (new PidcLoader(getServiceData())).getDataObjectByID(pidcVers.getPidcId());

    this.model = new PidcVersionAttributeModel(pidcVers, pidc, includeDeleted);
    loadDetails();

    getLogger().debug("Project attribute model created. Time taken = {}", System.currentTimeMillis() - startTime);

    return this.model;
  }

  /**
   * @param pidcID pidc ID
   * @param pidcVersID pidc version id
   * @return model
   * @throws IcdmException error while retrieving data
   */
  public PidcVersionAttributeModel createModelByPidc(final Long pidcID, final Long pidcVersID) throws IcdmException {

    long startTime = System.currentTimeMillis();

    getLogger().debug("Creating project attribute model for PIDC/PIDC Version ID {}/{} ...", pidcID, pidcVersID);


    Long pidcIdToUse = pidcID;
    if ((null == pidcIdToUse) && (null == pidcVersID)) {
      throw new InvalidInputException("Either PIDC ID or PIDC Version ID should be passed as input");
    }
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers;
    if (null == pidcVersID) {
      pidcVers = pidcVersLdr.getActivePidcVersion(pidcIdToUse);
    }
    else {
      pidcVers = pidcVersLdr.getDataObjectByID(pidcVersID);
    }
    if (pidcIdToUse == null) {
      // if pidc id is not provided
      pidcIdToUse = pidcVers.getPidcId();
    }
    if (pidcVersLdr.isHiddenToCurrentUser(pidcVers.getId())) {
      throw new UnAuthorizedAccessException("The PIDC with ID " + pidcIdToUse + " is not accessible to current user");
    }
    Pidc pidc = (new PidcLoader(getServiceData())).getDataObjectByID(pidcIdToUse);

    this.model = new PidcVersionAttributeModel(pidcVers, pidc, true);
    loadDetails();

    getLogger().debug("Project attribute model created. Time taken = {}", System.currentTimeMillis() - startTime);

    return this.model;
  }

  private void loadDetails() throws DataException {

    getLogger().debug("Project Object Model Load level : {}", this.loadLevel);

    if (satisfiesLevel(LOAD_LEVEL.L1_PROJ_ATTRS)) {
      fillAllAttributes();
      fillPidcVersionAttributes();
    }
    if (satisfiesLevel(LOAD_LEVEL.L2_VARIANTS)) {
      fillVariants();
    }
    if (satisfiesLevel(LOAD_LEVEL.L3_VAR_ATTRS)) {
      fillVariantAttributes();
    }
    if (satisfiesLevel(LOAD_LEVEL.L4_SUB_VARIANTS)) {
      fillSubVariants();
    }
    if (satisfiesLevel(LOAD_LEVEL.L5_SUBVAR_ATTRS)) {
      fillSubVariantAttributes();
    }
    fillAttributeValues();

  }

  private boolean satisfiesLevel(final LOAD_LEVEL required) {
    return this.loadLevel.ordinal() >= required.ordinal();
  }

  /**
   * @throws DataException
   */
  private void fillAttributeValues() throws DataException {

    // PIDC Version level
    fillAttributeValues(this.model.getPidcVersAttrMap().values());

    for (Long varId : this.model.getVariantMap().keySet()) {
      // Variant level
      fillAttributeValues(this.model.getVariantAttributeMap(varId).values());

      for (Long svarId : this.model.getSubVariantMap(varId).keySet()) {
        // Sub variant level
        fillAttributeValues(this.model.getSubVariantAttributeMap(svarId).values());
      }
    }
  }

  /**
   * @param <D>
   * @throws DataException
   */
  private <D extends IProjectAttribute> void fillAttributeValues(final Collection<D> projAttrCol) throws DataException {
    AttributeValueLoader attrValLdr = new AttributeValueLoader(getServiceData());
    for (D projAttr : projAttrCol) {
      if ((projAttr.getValueId() != null) && (this.model.getRelevantAttrValue(projAttr.getValueId()) == null)) {
        this.model.addRelevantAttrValue(attrValLdr.getDataObjectByID(projAttr.getValueId()));
      }
    }
  }

  /**
   * @throws DataException
   */
  private void fillSubVariantAttributes() throws DataException {
    (new PidcSubVariantAttributeLoader(getServiceData())).loadAttributes(this.model);
  }

  /**
   * @throws DataException
   */
  private void fillSubVariants() throws DataException {
    PidcSubVariantLoader subVarLdr = new PidcSubVariantLoader(getServiceData());
    for (Long varId : this.model.getVariantMap().keySet()) {
      this.model.setSubVariantMap(varId, subVarLdr.getSubVariants(varId, this.model.isDeletedIncluded()));
    }
  }

  /**
   * @throws DataException
   */
  private void fillVariantAttributes() throws DataException {
    (new PidcVariantAttributeLoader(getServiceData())).loadAttributes(this.model);
  }

  /**
   * @throws DataException
   */
  private void fillVariants() throws DataException {
    this.model.setVariantMap(new PidcVariantLoader(getServiceData()).getVariants(this.model.getPidcVersion().getId(),
        this.model.isDeletedIncluded()));
  }

  /**
   * @throws DataException
   */
  private void fillPidcVersionAttributes() throws DataException {
    new PidcVersionAttributeLoader(getServiceData()).loadAttributes(this.model);
  }

  /**
   * @throws DataException
   */
  private void fillAllAttributes() throws DataException {
    this.model.setAllAttrMap(new AttributeLoader(getServiceData()).getAllAttributes());
  }

  /**
   * Check if there is any attribute mapped to the Focus Matrix for the PIDC version
   *
   * @params pidc version id
   * @return boolean true if there is even a single attr linked to the focus matrix else false
   */
  public boolean hasAttributesMappedToFocusMatrixForPidc(final Long pidcVersId) {
    TPidcVersion tPidcVersion = new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersId);

    if (tPidcVersion != null) {
      List<TabvProjectAttr> projectAttrList = tPidcVersion.getTabvProjectAttrs();
      for (TabvProjectAttr projectAttr : projectAttrList) {
        // if even one attr is mapped to focus matrix then return true
        String focusMatrixYn = projectAttr.getFocusMatrixYn();
        if (CommonUtils.isNotEmptyString(focusMatrixYn) &&
            CommonUtils.isEqual(CommonUtilConstants.CODE_YES, focusMatrixYn)) {
          return true;
        }
      }
    }
    return false;
  }

}
