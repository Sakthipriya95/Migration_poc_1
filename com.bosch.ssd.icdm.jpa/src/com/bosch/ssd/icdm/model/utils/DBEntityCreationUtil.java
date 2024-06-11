/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.bosch.ssd.icdm.common.utility.CommonUtil;
import com.bosch.ssd.icdm.entity.TSsd2TempFeaval;
import com.bosch.ssd.icdm.entity.TempIcdmFeaval;
import com.bosch.ssd.icdm.entity.TempLabel;
import com.bosch.ssd.icdm.entity.TempLabelsList;
import com.bosch.ssd.icdm.entity.TempRuleIdOEM;
import com.bosch.ssd.icdm.entity.VLdb2Comp;
import com.bosch.ssd.icdm.entity.VLdb2DcmData;
import com.bosch.ssd.icdm.entity.VLdb2MaturityLevel;
import com.bosch.ssd.icdm.entity.VLdb2ProjectLabel;
import com.bosch.ssd.icdm.entity.VLdb2ProjectRelease;
import com.bosch.ssd.icdm.entity.VLdb2ProjectRevision;
import com.bosch.ssd.icdm.entity.VLdb2Ssd2;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.service.utility.DBQueryUtils;

/**
 * This utility class contains the entity creation for the persist features within the interface module
 *
 * @author SSN9COB
 */
public final class DBEntityCreationUtil {

  /**
  *
  */
  private static final String TRUNCATED_TXT = "(truncated)... ";

  /**
   * Maximum allowed length of History Descr data. Should be less than VARCHAR2 limit
   */
  private static final int HISTORIE_DESCR_MAX_LENGTH = 3900;

  /**
   *
   */
  private static final String CREATED_BY_ICDM = "Created by iCDM";

  /**
   *
   */
  private static final String LABEL_INSERTED_FROM_ICDM_CLIENT = "Label inserted from icdm client";

  private static final String MANUALTYPE = "M";
  private static final BigDecimal FINISHEDSTATE = new BigDecimal(5);
  private static final BigDecimal EDITSTATE = new BigDecimal(2);

  private DBEntityCreationUtil() {
    // Hidden Constructor
  }

  /**
   * Returns TempLabel Entity for the DB persist actions
   *
   * @param cdrRule Rule Model
   * @param nodeId NodeId
   * @param seqId Seqid
   * @return entity
   */
  public static TempLabel createTempLabel(final CDRRule cdrRule, final BigDecimal nodeId, final BigDecimal seqId) {
    TempLabel label = new TempLabel();
    label.setSeqId(seqId);
    label.setLabel(cdrRule.getParameterName());
    label.setUpperLabel(cdrRule.getParameterName().toUpperCase(Locale.ENGLISH));
    label.setUnit(cdrRule.getUnit());
    label.setNodeId(nodeId);
    return label;
  }

  /**
   * Return TempIcdmFeaVal entity
   *
   * @param depModel feature value model
   * @param labId label id
   * @return entity
   */
  public static TempIcdmFeaval createTempICDMFeaVal(final FeatureValueModel depModel, final BigDecimal labId) {
    TempIcdmFeaval tempFeaval = new TempIcdmFeaval();
    tempFeaval.setFeatureId(depModel.getFeatureId());
    tempFeaval.setValueId(depModel.getValueId());
    tempFeaval.setLabId(labId);

    return tempFeaval;
  }


  /**
   * Return TSSD2TempFeaVal entity
   *
   * @param depModel feature valuemodel
   * @param labObjId rule id
   * @return entity
   */
  public static TSsd2TempFeaval createTSSD2TempFeaVal(final FeatureValueModel depModel, final BigDecimal labObjId) {
    TSsd2TempFeaval fea = new TSsd2TempFeaval();
    fea.setFeatureId(depModel.getFeatureId());
    fea.setLabObjId(labObjId);
    fea.setValueId(depModel.getValueId());
    fea.setOperatorId(depModel.getOperatorId());
    return fea;
  }

  /**
   * Returnds TempLabelsList entity
   *
   * @param label label
   * @param function func
   * @param typ type
   * @param subTyp sub type
   * @param ssdClass class
   * @return entity
   */
  public static TempLabelsList createTempLabelsList(final String label, final String function, final String typ,
      final String subTyp, final String ssdClass) {
    TempLabelsList tempLabel = new TempLabelsList();
    tempLabel.setLabel(label);
    tempLabel.setUpperLabel(label.toUpperCase(Locale.ENGLISH));
    tempLabel.setFunction(function);
    tempLabel.setTyp(typ);
    tempLabel.setSubTyp(subTyp);
    tempLabel.setTempno(BigDecimal.ONE);
    tempLabel.setRemarks(LABEL_INSERTED_FROM_ICDM_CLIENT);
    tempLabel.setSsdClass(ssdClass);
    // Add other set methods if needed
    return tempLabel;
  }

  /**
   * Create Vldb2Comp Entity
   *
   * @param ssd rule
   * @param depModel feature value model
   * @param dBQueryUtils dBQueryUtils
   * @return entity
   * @throws SSDiCDMInterfaceException Exception
   */
  public static VLdb2Comp createVLDB2Comp(final VLdb2Ssd2 ssd, final FeatureValueModel depModel,
      final DBQueryUtils dBQueryUtils)
      throws SSDiCDMInterfaceException {
    VLdb2Comp comp = new VLdb2Comp();

    comp.setFeatureId(depModel.getFeatureId());
    comp.setValueId(depModel.getValueId());
    comp.setLabObjId(ssd.getLabObjId());
    comp.setRevId(ssd.getRevId());

    if (dBQueryUtils.checkForValidOperator(depModel.getFeatureId().toString())) {
      comp.setOperatorId(depModel.getOperatorId());
    }
    else {
      comp.setOperatorId(null);
    }
    return comp;
  }


  /**
   * Returns Vldb2DcmData Entity
   *
   * @param ssd Rule
   * @param data dcm String
   * @param rowNumber int
   * @return entity
   */
  public static VLdb2DcmData createVLDB2DcmData(final VLdb2Ssd2 ssd, final String data, final int rowNumber) {
    VLdb2DcmData dcmData = new VLdb2DcmData();
    dcmData.setLabObjId(ssd.getLabObjId());
    dcmData.setRevId(ssd.getRevId());
    dcmData.setData(data);
    dcmData.setRowNumber(new BigDecimal(rowNumber));
    return dcmData;
  }

  /**
   * Returnds Vldb2MaturityLevel entity
   *
   * @param ssd rule
   * @param ssdMaturity maturity string
   * @return entity
   */
  public static VLdb2MaturityLevel createVLDB2MaturityLevel(final VLdb2Ssd2 ssd, final String ssdMaturity) {
    VLdb2MaturityLevel maturityLevel = new VLdb2MaturityLevel();
    maturityLevel.setRevId(ssd.getRevId());
    maturityLevel.setLabObjId(ssd.getLabObjId());
    maturityLevel.setMaturity(ssdMaturity);
    maturityLevel.setHistorie("N");
    return maturityLevel;
  }

  /**
   * Create VLDB2SSD Entity
   *
   * @param model CDRRule
   * @param creUser user
   * @param nodeId node
   * @param scope scope
   * @return entity
   */
  public static VLdb2Ssd2 createVldb2SSD2(final CDRRule model, final String creUser, final BigDecimal nodeId,
      final BigDecimal scope) {
    VLdb2Ssd2 ssd = new VLdb2Ssd2();
    ssd.setLabObjId(model.getRuleId());
    if ((model.getRevId() == null) || (model.getRevId().longValue() == 0)) {
      // new rule- first revision
      ssd.setRevId(BigDecimal.ONE);
    }
    else {
      // TODO check why null
      return null;
    }
    ssd.setHistorieDescr(CREATED_BY_ICDM);
    setValuesForEntity(model, creUser, ssd);
    // node id and scope for ssd given as fixed value for now
    ssd.setScope(scope);
    // SSD-330
    ssd.setObjId1(nodeId);

    return ssd;
  }

  /**
   * Set of common values for Create & Update Entity
   *
   * @param model
   * @param creUser
   * @param ssd
   */
  private static void setValuesForEntity(final CDRRule model, final String creUser, final VLdb2Ssd2 ssd) {
    // SSD-336 - hint set in internal adapter description
    ssd.setDataDescr(model.getHint());
    // SSD-357
    if (!CommonUtil.isEmptyString(model.getFormulaDesc())) {
      ssd.setFormulaDesc(model.getFormulaDesc());
    }
    if (!CommonUtil.isEmptyString(model.getFormula())) {
      ssd.setFormula(model.getFormula());
    }
    if (Objects.nonNull(model.getLowerLimit())) {
      ssd.setMin(model.getLowerLimit());
    }
    if (Objects.nonNull(model.getUpperLimit())) {
      ssd.setMax(model.getUpperLimit());
    }
    if (Objects.nonNull(model.getRefValue())) {
      ssd.setTyp(model.getRefValue());
    }
    ssd.setDim(model.getUnit());
    ssd.setLabLabId(model.getLabelId());
    // mark rules as finished for automatic
    ssd.setState(model.getReviewMethod().equalsIgnoreCase(MANUALTYPE) ? EDITSTATE : FINISHEDSTATE);
    // dcm2ssd is taken as N for now
    ssd.setDcm2ssd(model.isDcm2ssd() ? "Y" : "N");
    // SSD-330
    if (Objects.nonNull(model.getSsdCase())) {
      ssd.setCases(model.getSsdCase().getCharacter());
    }
    ssd.setCreUser(creUser);
    ssd.setModUser(creUser);
  }

  /**
   * Update VLDB2SSD Entity
   *
   * @param oldRule old rule
   * @param model CDRRule
   * @param creUser user
   * @param nodeId node
   * @param deletedRule deletedRule
   * @return entity
   */
  public static VLdb2Ssd2 updateVldb2SSD2(final VLdb2Ssd2 oldRule, final CDRRule model, final String creUser,
      final BigDecimal nodeId, final boolean deletedRule) {
    VLdb2Ssd2 ssd = new VLdb2Ssd2();
    ssd.setLabObjId(oldRule.getLabObjId());
    ssd.setRevId(model.getRevId());
    ssd.setHistorieDescr(prepareHistorieDescr(deletedRule, oldRule, creUser));
    setValuesForEntity(model, creUser, ssd);
    // node id and scope for ssd given as fixed value for now
    ssd.setScope(oldRule.getScope());
    // SSD-330
    ssd.setObjId1(nodeId);

    return ssd;
  }

  private static String prepareHistorieDescr(final boolean deletedRule, final VLdb2Ssd2 oldSSD, final String creUser) {
    String historieDescr;

    if (deletedRule) {
      historieDescr =
          oldSSD.getHistorieDescr() + " deleted by " + oldSSD.getCreUser() + " and now recreated by " + creUser;
    }
    else {
      historieDescr = oldSSD.getHistorieDescr() + " Rev by " + creUser;
    }

    // Truncate the history if it crosses VARCHAR2 length limit. Remove the oldest history info, at the left most side
    if (historieDescr.length() > HISTORIE_DESCR_MAX_LENGTH) {
      historieDescr = TRUNCATED_TXT + historieDescr.substring(historieDescr.length() - 3900);
    }

    return historieDescr;
  }

  /**
   * Returns VLdb2ProjectLabel Entity
   *
   * @param label name
   * @param unit desc
   * @param proRevId number
   * @return VLdb2ProjectLabel
   */
  public static VLdb2ProjectLabel createVLDB2ProjectLabel(final String label, final String unit,
      final BigDecimal proRevId) {
    VLdb2ProjectLabel newlabelObj = new VLdb2ProjectLabel();
    newlabelObj.setLabel(label);
    newlabelObj.setUpperLabel(label.toUpperCase(Locale.ENGLISH));
    newlabelObj.setUnit(unit);
    newlabelObj.setProRevId(proRevId);
    return newlabelObj;
  }

  /**
   * Returns VLdb2ProjectLabel Entity
   *
   * @param versId number
   * @param maxRevId number
   * @param userName name
   * @return VLdb2ProjectLabel
   */
  public static VLdb2ProjectRevision createVLdb2ProjectRevision(final BigDecimal versId, final BigDecimal maxRevId,
      final String userName) {
    VLdb2ProjectRevision revision = new VLdb2ProjectRevision();
    revision.setVersId(versId);
    revision.setRevId(maxRevId);
    revision.setDescription("iCDM Client " + new Date());
    revision.setAsapFile("CompPckg retrieved by " + userName);
    return revision;
  }

  /**
   * Returns VLdb2ProjectRelease Entity
   *
   * @param proRevId rev id
   * @param maxRelId rel id
   * @param userName name
   * @param isNonSDOMSwRelease releaseType
   * @return VLdb2ProjectReleaserelease Type
   */
  public static VLdb2ProjectRelease createVldb2ProjectRelease(final BigDecimal proRevId, final BigDecimal maxRelId,
      final String userName, final boolean isNonSDOMSwRelease) {
    VLdb2ProjectRelease proj = new VLdb2ProjectRelease();
    proj.setProRevId(proRevId);
    proj.setRelId(maxRelId);
    StringBuilder relDesc = new StringBuilder();
    relDesc.append("Release from iCDM done by " + userName);
    if (isNonSDOMSwRelease) {
      relDesc.append(" for Non SDOM Software");
    }
    proj.setDescription(relDesc.toString());
    proj.setCreUser("ICDM_USER");
    proj.setRelTyp("i");
    return proj;
  }
  /**
   * Returns TempRuleIdOEM entity
   *
   * @param ruleId rule id
   * @param ruleRev rev
   * @return TempRuleIdOEM
   */
  public static TempRuleIdOEM createTempRuleIdOEM(final String ruleId, final String ruleRev) {
    TempRuleIdOEM tempLabel = new TempRuleIdOEM();
    if ((ruleRev != null) && (ruleId != null)) {
      tempLabel.setLabObjId(new BigDecimal(ruleId));
      tempLabel.setRevId(new BigDecimal(ruleRev));
    }
    else {
      return null;
    }
    return tempLabel;
  }
}
