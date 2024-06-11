/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author bne4cob
 */
public class FC2WPVersionLoader extends AbstractBusinessObject<FC2WPVersion, TFc2wpDefVersion> {

  /**
   * Constant for FC2WP
   */
  private static final String FC2WP = "FC2WP";
  /**
   * Name for working set version
   */
  public static final String WORKING_SET = "Working Set";
  private static final String VERSION_STR = "Version ";

  /**
   * @param inputData ServiceData
   */
  public FC2WPVersionLoader(final ServiceData inputData) {
    super(inputData, MODEL_TYPE.FC2WP_DEF_VERS, TFc2wpDefVersion.class);
  }

  /**
   * @param fc2wpDefID fc2wp Def ID
   * @return set of FC to WP versions
   * @throws DataException when input data is invalid
   */
  public Set<FC2WPVersion> getVersionsByDefID(final Long fc2wpDefID) throws DataException {
    Set<FC2WPVersion> retSet = new HashSet<>();

    TFc2wpDefinition dbDef = (new FC2WPDefLoader(getServiceData())).getEntityObject(fc2wpDefID);
    for (TFc2wpDefVersion dbVers : dbDef.getTFc2wpDefVersions()) {
      FC2WPVersion vers = createDataObject(dbVers);
      retSet.add(vers);
    }

    return retSet;
  }

  @Override
  protected FC2WPVersion createDataObject(final TFc2wpDefVersion dbVers) throws DataException {
    FC2WPVersion vers = new FC2WPVersion();

    setCommonFields(vers, dbVers);

    vers.setFcwpDefId(dbVers.getTFc2wpDefinition().getFcwpDefId());

    vers.setDescription(getLangSpecTxt(dbVers.getDescEng(), dbVers.getDescGer()));
    vers.setDescEng(dbVers.getDescEng());
    vers.setDescGer(dbVers.getDescGer());

    if (isWorkingSetVersion(dbVers)) {
      vers.setVersionName(WORKING_SET);
    }
    else {
      vers.setVersionName(VERSION_STR + dbVers.getMajorVersionNum() + "." + dbVers.getMinorVersionNum());
    }
    vers.setMajorVersNo(dbVers.getMajorVersionNum());
    vers.setMinorVersNo(dbVers.getMinorVersionNum());

    FC2WPDef def =
        (new FC2WPDefLoader(getServiceData())).getDataObjectByID(dbVers.getTFc2wpDefinition().getFcwpDefId());
    vers.setName(def.getName() + " - " + def.getDivisionName() + " (" + vers.getVersionName() + ")");

    vers.setArchReleaseSdom(dbVers.getArchReleaseSdom());
    vers.setActive(isActiveVersion(dbVers));

    // Version 0.0 is working set version
    vers.setWorkingSet(isWorkingSetVersion(dbVers));

    return vers;
  }

  /**
   * @param dbVers
   * @return
   */
  private boolean isActiveVersion(final TFc2wpDefVersion dbVers) {
    return CommonUtilConstants.CODE_YES.equals(dbVers.getActiveFlag());
  }

  /**
   * @param dbVers
   * @return
   */
  private boolean isWorkingSetVersion(final TFc2wpDefVersion dbVers) {
    return (0L == dbVers.getMajorVersionNum()) && (0L == dbVers.getMinorVersionNum());
  }

  /**
   * @param fc2wpDefID fc2wp Definition ID
   * @return Working Set FC2WP Version
   * @throws DataException when input data is invalid
   */
  public FC2WPVersion getWorkingSetVersion(final Long fc2wpDefID) throws DataException {

    TFc2wpDefinition dbDef = (new FC2WPDefLoader(getServiceData())).getEntityObject(fc2wpDefID);
    for (TFc2wpDefVersion dbVers : dbDef.getTFc2wpDefVersions()) {
      if (isWorkingSetVersion(dbVers)) {
        return createDataObject(dbVers);
      }
    }
    return null;
  }

  /**
   * @param fc2WpName Value ID of name attribute value
   * @param divValueId value ID of division attribute value
   * @return active FC2WP Version
   * @throws IcdmException exception
   */
  public FC2WPVersion getActiveVersionByValueID(final String fc2WpName, final Long divValueId) throws IcdmException {

    TFc2wpDefinition dbDef = (new FC2WPDefLoader(getServiceData())).getEntityObject(fc2WpName, divValueId);
    for (TFc2wpDefVersion dbVers : dbDef.getTFc2wpDefVersions()) {
      if (isActiveVersion(dbVers)) {
        return createDataObject(dbVers);
      }
    }

    return null;
  }

  /**
   * Find active version by pidc version.
   *
   * @param pidcVersId the pidc vers id
   * @return the FC2WP version
   * @throws DataException the data exception
   */
  public FC2WPVersion findActiveVersionByPidcVersion(final Long pidcVersId) throws IcdmException {

    // Qnaire config attr
    String paramVal = (new CommonParamLoader(getServiceData())).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR);
    Long divAttrId = Long.valueOf(paramVal);
    // Division Attribute value not set
    PidcVersionAttributeLoader pidcVersAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    AttributeValue divAttrValue = pidcVersAttrLoader.getPidcQnaireConfigAttrVal(pidcVersId);
    if (divAttrValue == null) {
      AttributeLoader loader = new AttributeLoader(getServiceData());
      throw new IcdmException("FC2WP.DIV_ATTR_NOT_SET", loader.getDataObjectByID(divAttrId).getName());
    }

    Set<TFc2wpDefinition> defSet =
        new AttributeValueLoader(getServiceData()).getEntityObject(divAttrValue.getId()).gettFc2wpDefinitions();
    TFc2wpDefinition relvDefEntity = getDefRelevantForQnaire(defSet);
    if (relvDefEntity == null) {
      // No definition exists for the given division

      throw new DataException(
          new MessageLoader(getServiceData()).getMessage(FC2WP, "NO_DEF_FOUND", divAttrValue.getTextValueEng()));
    }

    TFc2wpDefVersion fc2wpVers = getActiveVersionForDef(relvDefEntity);
    if (fc2wpVers == null) {
      // No active version exists
      throw new DataException(new MessageLoader(getServiceData()).getMessage(FC2WP, "NO_ACTIVE_VERSION", null));
    }
    return createDataObject(fc2wpVers);
  }

  /**
   * Gets the def relevant for qnaire.
   *
   * @param defSet the def set
   * @return the def relevant for qnaire
   * @throws DataException the data exception
   */
  private TFc2wpDefinition getDefRelevantForQnaire(final Set<TFc2wpDefinition> defSet) throws DataException {
    if (CommonUtils.isNotEmpty(defSet)) {
      for (TFc2wpDefinition defEntity : defSet) {
        if (CommonUtilConstants.CODE_YES.equals(defEntity.getRelvForQnaireFlag())) {
          return defEntity;
        }
      }
    }
    return null;
  }

  /**
   * Gets the active version.
   *
   * @param dbDef the db def
   * @return Working Set FC2WP Version
   * @throws DataException when input data is invalid
   */
  private TFc2wpDefVersion getActiveVersionForDef(final TFc2wpDefinition dbDef) {
    if (CommonUtils.isNotEmpty(dbDef.getTFc2wpDefVersions())) {
      for (TFc2wpDefVersion dbVers : dbDef.getTFc2wpDefVersions()) {
        if (isActiveVersion(dbVers)) {
          return dbVers;
        }
      }
    }
    return null;
  }


}
