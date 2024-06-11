/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.caldataimport.ICalDataImporterObject;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.ICalDataImportParamDetailsLoader;
import com.bosch.caltool.icdm.bo.caldataimport.RuleSetParamDetailsLoader;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;


/**
 * Project Specific Rule Set <br>
 *
 * @author adn1cob
 */
public class RuleSet extends AbstractParameterCollection<RuleSetParameter>
    implements Comparable<RuleSet>, ICalDataImporterObject, ILinkableObject {


  private RuleSetParamDetailsLoader ruleSetParamDetailsLoader;


  /**
   * @param dataProvider dataProvider
   * @param objID entityid
   */
  public RuleSet(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getEntityProvider().getDbRuleSet(getID()).getRsetName();
  }

  /**
   * {@inheritDoc} <br>
   * Get description based on language <br>
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getDescEng(), getDescGer(),
        ApicConstants.EMPTY_STRING);
  }


  /**
   * Get the description in english
   *
   * @return desc english
   */
  public String getDescEng() {
    return getEntityProvider().getDbRuleSet(getID()).getDescEng();
  }

  /**
   * Get the description in German
   *
   * @return desc german
   */
  public String getDescGer() {
    return getEntityProvider().getDbRuleSet(getID()).getDescGer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RULE_SET;
  }

  /**
   * @return the created user
   */
  @Override
  public final String getCreatedUser() {
    return getEntityProvider().getDbRuleSet(getID()).getCreatedUser();
  }

  /**
   * @return the created date
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRuleSet(getID()).getCreatedDate());
  }

  /**
   * @return the modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRuleSet(getID()).getModifiedDate());
  }

  /**
   * @return the modified user
   */
  @Override
  public final String getModifiedUser() {
    return getEntityProvider().getDbRuleSet(getID()).getModifiedUser();
  }

  /**
   * @return true if marked as deleted
   */
  public boolean isDeleted() {
    return CommonUtils.isEqual(ApicConstants.YES, getEntityProvider().getDbRuleSet(getID()).getDeletedFlag());
  }

  /**
   * iCDM-1522 Checks if the Rule Set has minimum READ access
   *
   * @return true if ruleset is deleted or does not have read/write access for the user
   */
  public boolean isRestricted() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight == null) || !(curUserAccRight.hasReadAccess() || curUserAccRight.hasWriteAccess()) ||
        isDeleted()) {
      return true;
    }
    return false;
  }


  /**
   * @return the attr value
   */
  public AttributeValue getAttrValue() {
    TabvAttrValue tabvAttrValue = getEntityProvider().getDbRuleSet(getID()).getTabvAttrValue();

    return getApicDataProvider().getAttrValue(tabvAttrValue.getValueId());
  }

  /**
   * Get current user access rights for this ruleset
   *
   * @return User access right for the node
   */
  @Override
  public NodeAccessRight getCurrentUserAccessRights() {
    return getApicDataProvider().getNodeAccRight(getID());
  }

  /**
   * Check if the rule set is modifiable
   */
  @Override
  public boolean isModifiable() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess() && !isDeleted()) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, RuleSetParameter> getAllParameters(final boolean includeRules) throws SsdInterfaceException {
    if (this.paramsMap == null) {
      // fetch params, and filter variant coded params
      this.paramsMap = filterVariantCodedParams(fetchRuleSetParameters());
    }
    // fetch rules if not already loaded
    if (includeRules && !hasRulesLoaded()) {
      fetchRules();
    }
    return this.paramsMap;
  }


  /**
   * Fetch Rule Set parameters
   *
   * @return Map of RuleSetParameter
   */
  private Map<String, RuleSetParameter> fetchRuleSetParameters() {

    Map<String, RuleSetParameter> rsParamMap = new ConcurrentHashMap<String, RuleSetParameter>();
    List<TRuleSetParam> dbParams = getEntityProvider().getDbRuleSet(getID()).getTRuleSetParams();
    // Create RuleSetParameter objects and add to cache
    for (TRuleSetParam tRuleSetParam : dbParams) {
      RuleSetParameter rsParam = getDataCache().getRuleSetParam(tRuleSetParam.getRsetParamId());
      if (rsParam == null) {
        rsParam = new RuleSetParameter(getDataProvider(), tRuleSetParam.getRsetParamId());
        // add it to all rule set params map
        getDataCache().addRuleSetParameter(rsParam);
      }
      // add param to the return set for this function
      rsParamMap.put(tRuleSetParam.getTParameter().getName(), rsParam);
    }
    return rsParamMap;
  }


  /**
   * Fetch rules (from ssd) for the Rule Set and add to DataCache against the RuleSet id
   *
   * @throws SsdInterfaceException
   */
  protected void fetchRules() throws SsdInterfaceException {
    // Get the list of params
    List<String> paramList = new ArrayList<String>();
    for (RuleSetParameter rsParam : this.paramsMap.values()) {
      paramList.add(rsParam.getName());
    }
    // fetch rules from ssd
    Map<String, List<CDRRule>> rulesMap = getDataLoader().fetchRuleSetRules(paramList, getSsdNodeID());
    // also add to cache, against Rule set id
    getApicDataProvider().addCDRRulesToRuleSet(getID(), rulesMap);
    // set the flag
    this.rulesFetched = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleSet other) {
    return ApicUtil.compare(getName(), other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasVersions() {
    // not applicable, return false
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isParamPropsModifiable() {
    // param props are non editable from RuleSet
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isParamMappingModifiable() {
    // List of parameters can be modified for a rule.
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getSsdNodeID() {
    return getEntityProvider().getDbRuleSet(getID()).getSsdNodeId();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getSsdVersNodeID() {
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SSDCase getSsdUseCase() {
    return SSDCase.REVIEW;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ParameterClass getSsdParamClass() {
    return ParameterClass.CUSTSPEC;
  }


  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Collection<IAttributeMappedObject> getAttrMappedObjects() throws SsdInterfaceException {
    Collection<IAttributeMappedObject> attrs = new TreeSet<IAttributeMappedObject>();
    for (IAttributeMappedObject iAttributeMappedObject : getAllParameters(false).values()) {
      attrs.add(iAttributeMappedObject);
    }
    return attrs;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ICalDataImportParamDetailsLoader getParamDetailsLoader() {
    if (null == this.ruleSetParamDetailsLoader) {
      // this.ruleSetParamDetailsLoader = new RuleSetParamDetailsLoader(this, getDataProvider());
    }
    return this.ruleSetParamDetailsLoader;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean enableLabelListCreation() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getObjectTypeName() {
    return ApicConstants.RULE_SET;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IRuleManager getRuleManager() {
    if (this.ruleManager == null) {
      // this.ruleManager = new RuleSetRuleManager(this);
    }
    return this.ruleManager;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isInputParamsChecked() {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractImportRuleManager getImportRuleManager() {
    return null;// new RulesetImportRuleManager();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRulesModifiable() {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ICalDataImporterObject getCaldataImportObject() {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuleSet getDataObject() {
    return this;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void enableParameterRefresh() {
    // Not applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addParamsFromFile() {
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractDataCommand getCreateParamsCommands(final List<String> paramsTobeAdded) {
    if (CommonUtils.isNotEmpty(paramsTobeAdded)) {
      Map<String, CDRFunction> filteredParamFuncObjMap = new HashMap<>();
      Map<String, CDRFuncParameter> filteredParamNameObjMap = new HashMap<>();

      for (String paramName : paramsTobeAdded) {
        // filteredParamFuncObjMap.put(paramName, this.ruleSetParamDetailsLoader.getParamFuncObjMap().get(paramName));
        // filteredParamNameObjMap.put(paramName, this.ruleSetParamDetailsLoader.getParamNameObjMap().get(paramName));
      }

      return new CmdModMultipleRuleSetParam(getDataProvider(), this, null, filteredParamFuncObjMap,
          filteredParamNameObjMap);
    }

    return null;
  }

}
