/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.PidcVersionConsInfo;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants.FM_VERS_STATUS;


/**
 * Project version consistency evaluation
 *
 * @author bne4cob
 */
public class PidcVersionEvaluator implements IValidator<PidcVersionConsInfo> {

  /**
   * PIDC Version Entity
   */
  private final TPidcVersion dbVersion;

  /**
   * map of structure attributes
   */
  private final ConcurrentMap<Long, String> strAttrMap;

  /**
   * All attributes defined in this version
   */
  private final ConcurrentMap<Long, String> allVersAttrMap = new ConcurrentHashMap<>();

  /**
   * Resultset
   */
  private final SortedSet<PidcVersionConsInfo> resultSet = new TreeSet<>();

  /**
   * Attributes of this version, that are defined at variant level
   */
  private final ConcurrentMap<Long, String> varAttrIDMap = new ConcurrentHashMap<>();


  /**
   * Constructor
   *
   * @param dbVersion PIDC Version Entity
   * @param strAttrMap map of structure attributes
   */
  public PidcVersionEvaluator(final TPidcVersion dbVersion, final ConcurrentMap<Long, String> strAttrMap) {

    this.dbVersion = dbVersion;
    this.strAttrMap = strAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() {

    // Find different type of attributes (e.g. attributes defined at variant level)
    findAttributes();

    // validate version details like - missing structure level attributes
    validateVersion();

    // Validate variants
    for (TabvProjectVariant dbVariant : this.dbVersion.getTabvProjectVariants()) {
      VariantEvaluator varEval = new VariantEvaluator(dbVariant, this.varAttrIDMap);
      varEval.validate();
      this.resultSet.addAll(varEval.getResult());
    }

    // Validate FM
    validateFocusMatrix();
  }


  /**
   * Validate Focus Matrix structure
   */
  private void validateFocusMatrix() {
    Set<TFocusMatrixVersion> dbFMVersions = this.dbVersion.getTFocusMatrixVersions();

    if ((dbFMVersions == null) || dbFMVersions.isEmpty()) {
      addErrMsgLine(ErrorType.FM_VERS_MISSING, ErrorLevel.FM_VERSION, null, null);
      return;
    }

    // Check for WS version
    boolean wsFound = false;
    for (TFocusMatrixVersion dbFMVers : dbFMVersions) {
      if (FM_VERS_STATUS.getStatus(dbFMVers.getStatus()) == FM_VERS_STATUS.WORKING_SET) {
        wsFound = true;
        break;
      }
    }
    if (!wsFound) {
      addErrMsgLine(ErrorType.FM_VERS_NO_WS, ErrorLevel.FM_VERSION, null, null);
    }

  }

  /**
   * Find attributes in PIDC version, that are marked as 'variant level'
   *
   * @return map, key - attribute ID; value attribute name
   */
  private ConcurrentMap<Long, String> findAttributes() {

    for (TabvProjectAttr dbProjAttr : this.dbVersion.getTabvProjectAttrs()) {
      TabvAttribute dbattr = dbProjAttr.getTabvAttribute();
      this.allVersAttrMap.put(dbattr.getAttrId(), dbattr.getAttrNameEng());

      if (CommonUtilConstants.CODE_YES.equals(dbProjAttr.getIsVariant())) {
        this.varAttrIDMap.put(dbattr.getAttrId(), dbattr.getAttrNameEng());
      }
    }
    return this.varAttrIDMap;
  }

  /**
   * Validate PIDC version information
   */
  private void validateVersion() {
    for (Entry<Long, String> strAttrEntry : this.strAttrMap.entrySet()) {
      if (!this.allVersAttrMap.containsKey(strAttrEntry.getKey())) {
        addErrMsgLine(ErrorType.PIDVERS_STRUCT_MISSING, ErrorLevel.PIDC_ATTR, strAttrEntry.getKey(),
            strAttrEntry.getValue());
      }
    }
  }

  /**
   *
   */
  private void addErrMsgLine(final ErrorType type, final ErrorLevel level, final Long attrId, final String attrName) {
    PidcVersionConsInfo error = new PidcVersionConsInfo();

    error.setProjectID(this.dbVersion.getTabvProjectidcard().getProjectId());
    error.setProjectName(this.dbVersion.getTabvProjectidcard().getTabvAttrValue().getTextvalueEng());

    error.setPidcVersID(this.dbVersion.getPidcVersId());
    error.setPidcVersName(this.dbVersion.getVersName());

    error.setAttrID(attrId);
    error.setAttrName(attrName);

    error.setErrorType(type);
    error.setErrorLevel(level);

    // Set the parent PIDC details
    error.setCreatedUserParent(this.dbVersion.getTabvProjectidcard().getCreatedUser());
    error.setCreatedDateParent(ApicUtil.timestamp2calendar(this.dbVersion.getTabvProjectidcard().getCreatedDate()));
    error.setLastModifiedUserParent(this.dbVersion.getTabvProjectidcard().getModifiedUser());
    error.setLastModifiedDateParent(
        ApicUtil.timestamp2calendar(this.dbVersion.getTabvProjectidcard().getModifiedDate()));

    this.resultSet.add(error);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<PidcVersionConsInfo> getResult() {
    return new TreeSet<>(this.resultSet);
  }


}
