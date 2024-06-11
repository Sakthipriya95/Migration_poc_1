/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * @author apj4cob
 */
public class PidcRuleSetRuleFileResolver extends AbstractSimpleBusinessObject {

  /**
   * Dir name for the root directory of ruleset rule ssd file export
   */
  private static final String DIR_NAME_RULESET_FILE_ROOT = "RULESET_RULE_FILE";

  /**
   *
   */
  private static final String INVALID_PIDC_ELEMENT_ID = "Invalid PIDC Element ID";

  private Map<Long, IProjectAttribute> projAttrMap;

  private PidcVersion selPidcVersion;

  private PidcVariant selPidcVariant;

  /**
   * @param serviceData ServiceData
   */
  public PidcRuleSetRuleFileResolver(final ServiceData serviceData) {
    super(serviceData);

  }

  /**
   * To get RuleSet object for given pidc element
   *
   * @param pidcElementId pidc element id
   * @return Rule Set object
   * @throws IcdmException Exception
   */
  public RuleSet getMandateRuleSetForPIDC(final Long pidcElementId) throws IcdmException {
    if (processValidElementId(pidcElementId)) {
      final Long mandateRuleSetID = Long
          .valueOf((new CommonParamLoader(getServiceData())).getValue(CommonParamKey.CDR_MANDATORY_RULESET_ATTR_ID));
      IProjectAttribute mandatePidcAttr = this.projAttrMap.get(mandateRuleSetID);
      RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
      if (mandatePidcAttr.getValueId() != null) {
        Long pidcRuleSetAttrValID = mandatePidcAttr.getValueId();
        Set<ParamCollection> allRuleSets = ruleSetLoader.getAllRuleSets();
        for (ParamCollection ruleSet : allRuleSets) {
          if (pidcRuleSetAttrValID.equals(((RuleSet) ruleSet).getAttrValId())) {
            return (RuleSet) ruleSet;
          }
        }

      }
      // throw error message mandatory rule set attribute is not defined for given pid element
      throw new InvalidInputException("Mandatory Rule Set attribute not defined", pidcElementId);
    }
    // invalid id
    throw new InvalidInputException(INVALID_PIDC_ELEMENT_ID, pidcElementId);
  }

  /**
   * To get Rule File for rule set corresponding to given pidc element
   *
   * @param pidcElementId pidc element id
   * @return CDRRulesWithFile
   * @throws IcdmException Exception
   */

  public CDRRulesWithFile getSsdFileByPidcElement(final Long pidcElementId) throws IcdmException {
    RuleSet mandateRuleSetForPIDC = getMandateRuleSetForPIDC(pidcElementId);
    if (!hasRuleSetReadAccess(mandateRuleSetForPIDC)) {
      throw new InvalidInputException("Insufficient privileges to access rule set");
    }
    File file = new File(Messages.getString("SERVICE_WORK_DIR"));
    if (!file.exists()) {
      file.mkdir();
    }
    file = new File(file.getAbsoluteFile() + File.separator + DIR_NAME_RULESET_FILE_ROOT);
    file.mkdir();
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    file = new File(file.getAbsoluteFile() + "\\rulesetrulefile_" + currentDate);
    file.mkdir();

    Long ssdNodeId = mandateRuleSetForPIDC.getSsdNodeId();
    RuleSetParameterLoader loader = new RuleSetParameterLoader(getServiceData());
    Map<String, RuleSetParameter> paramMap = loader.getAllRuleSetParams(mandateRuleSetForPIDC.getId());
    FeatureAttributeAdapterNew feaAttrAdapter = new FeatureAttributeAdapterNew(getServiceData());

    SortedSet<RuleSetParameter> ruleSetParameter = new TreeSet<>(paramMap.values());
    List<IParameter> paramsToRemove = new ArrayList<>();
    // resolve dependency list
    Set<FeatureValueModel> feaValueModelSet = getFeatureValModel(new HashSet<IParameter>(ruleSetParameter),
        feaAttrAdapter, paramMap.keySet(), paramsToRemove);

    // get the cdr rules with the file path for the rule set
    return new SSDServiceHandler(getServiceData()).readRulesandGetSSDFileDpndyForNode(
        new ArrayList<>(paramMap.keySet()), new ArrayList<>(feaValueModelSet), ssdNodeId, file.getAbsolutePath(), true);
  }


  /**
   * @param mandateRuleSetForPIDC RuleSet
   * @return boolean
   * @throws DataException Exception
   */
  private boolean hasRuleSetReadAccess(final RuleSet mandateRuleSetForPIDC) throws DataException {
    return (new NodeAccessLoader(getServiceData()).isCurrentUserRead(mandateRuleSetForPIDC.getId()));
  }

  /**
   * To validate pidc element id as pid version/variant in case of
   *
   * @param pidcElemId
   * @param attrIdFeatureMap
   * @return boolean indicating whether id is valid or not
   * @throws IcdmException
   */
  private boolean processValidElementId(final Long pidcElemId) throws IcdmException {
    // PIDC version & variant Loaders
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());

    // check whether the input pidc element is version / variant
    if (varLoader.isValidId(pidcElemId)) {
      getLogger().debug("Pidc Element {} identified as variant", pidcElemId);
      this.selPidcVariant = varLoader.getDataObjectByID(pidcElemId);
      // consider deletion of element id for validation
      if (!this.selPidcVariant.isDeleted()) {
        this.projAttrMap = fetchProjtAttrs(this.selPidcVariant);
        return true;
      }
      throw new InvalidInputException(INVALID_PIDC_ELEMENT_ID, pidcElemId);
    }
    // check if the input element is pidc version and variants are not available for pid card,
    PidcVersionLoader verLoader = new PidcVersionLoader(getServiceData());
    if (verLoader.isValidId(pidcElemId)) {
      getLogger().debug("Pidc Element {} identified as PIDC Version", pidcElemId);
      this.selPidcVersion = verLoader.getDataObjectByID(pidcElemId);
      // consider deletion of element id for validation
      if (this.selPidcVersion.isDeleted()) {
        throw new InvalidInputException(INVALID_PIDC_ELEMENT_ID, pidcElemId);
      }
      PidcLoader pidcLoader = new PidcLoader(getServiceData());
      // check whether pid card is deleted or not
      if (pidcLoader.getDataObjectByID(this.selPidcVersion.getPidcId()).isDeleted()) {
        throw new InvalidInputException(INVALID_PIDC_ELEMENT_ID, pidcElemId);
      }
      this.projAttrMap = fetchProjtAttrs(this.selPidcVersion);
      return true;
    }
    return false;
  }

  /**
   * @param selPidcVersion1
   * @param verLoader
   * @param attProjattrMap
   * @throws IcdmException
   */
  private Map<Long, IProjectAttribute> fetchProjtAttrs(final PidcVersion selPidcVersion1) throws IcdmException {
    ProjectAttributeLoader projAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVerAttrModel =
        projAttrLoader.createModel(selPidcVersion1.getId(), LOAD_LEVEL.L3_VAR_ATTRS);
    // check whether the pid card has variant available and all the variants are not deleted
    if ((pidcVerAttrModel.getVariantMap() != null) && !isAllVariantDeleted(pidcVerAttrModel.getVariantMap())) {
      throw new InvalidInputException(INVALID_PIDC_ELEMENT_ID);
    }
    return new HashMap<>(pidcVerAttrModel.getPidcVersAttrMap());
  }

  /**
   * @param variantMap
   * @return
   */
  private boolean isAllVariantDeleted(final Map<Long, PidcVariant> variantMap) {
    // conter variable to check whether all variants for given pid element are deleted
    int count = 0;
    for (PidcVariant pidVar : variantMap.values()) {
      if (pidVar.isDeleted()) {
        count++;
      }
    }
    return (count == variantMap.size());
  }

  /**
   * @param selPidcVariant1
   * @param verLoader
   * @param projattrMap
   * @throws IcdmException
   */
  private Map<Long, IProjectAttribute> fetchProjtAttrs(final PidcVariant selPidcVariant1) throws IcdmException {
    PidcVersionAttributeModel pidcVerAttrModel = (new ProjectAttributeLoader(getServiceData()))
        .createModel(selPidcVariant1.getPidcVersionId(), LOAD_LEVEL.L3_VAR_ATTRS);
    return new HashMap<>(pidcVerAttrModel.getPidcVersAttrMap());
  }

  /**
   * @param treeSet of rule set parameter
   * @param faAdapter
   * @param labelNames names of parameter available in Rule Set
   * @param paramsToRemove
   * @return
   */
  private Set<FeatureValueModel> getFeatureValModel(final Set<IParameter> treeSet,
      final FeatureAttributeAdapterNew faAdapter, final Set<String> labelNames, final List<IParameter> paramsToRemove)
      throws IcdmException {
    Set<FeatureValueModel> feaValModelSet;
    if (CommonUtils.isNotNull(this.selPidcVariant)) {
      feaValModelSet = faAdapter.createAllFeaValModel(treeSet, this.selPidcVariant, labelNames, paramsToRemove);
    }
    else {
      feaValModelSet = faAdapter.createAllFeaValModel(treeSet, this.selPidcVersion);
    }
    return feaValModelSet;
  }

}
