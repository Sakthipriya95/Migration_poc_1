/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.caldataimport.ICalDataImporterObject;
import com.bosch.caltool.apic.jpa.rules.bo.IParameterCollection;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * IParameterCollection implementation for NE type component package.
 *
 * @author bne4cob
 */
@Deprecated
public class CompPkgParamCollectionObject implements IParameterCollection<CompPkgParameter> {

  /**
   * Rule Manager
   */
  private IRuleManager ruleManager;

  /**
   * All params of component package
   */
  private Map<String, CompPkgParameter> paramsMap;

  /**
   * Associated component pacakge
   */
  private final CompPkg compPkg;

  /**
   * If true, the next invocation of getAllParameters will refresh parameters. <br>
   * Set to 'true' by default, to fill the parameters, during first access.
   */
  private boolean refreshParams = true;

  /**
   * Constructor
   *
   * @param compPkg associated component package
   */
  CompPkgParamCollectionObject(final CompPkg compPkg) {
    this.compPkg = compPkg;
  }


  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public Map<String, CompPkgParameter> getAllParameters(final boolean includeRules) throws SsdInterfaceException {
    if (this.paramsMap == null) {
      this.paramsMap = new ConcurrentHashMap<>();
      getRuleManager().readAllRules();
    }

    if (this.refreshParams) {
      Set<String> ruleParamsSet = getApicDataProvider().getRuleCache().getParamNamesFromRules(getSsdNodeID(), getID());
      Set<String> paramsToFetchSet = new HashSet<>();
      for (String parmName : ruleParamsSet) {
        if (this.paramsMap.containsKey(parmName)) {
          continue;
        }
        paramsToFetchSet.add(parmName);
      }
      createParamObjects(paramsToFetchSet);
      this.refreshParams = false;
    }


    return this.paramsMap;
  }

  /**
   * Create the parameter objects
   *
   * @param paramSet set of parameters
   */
  private void createParamObjects(final Set<String> paramSet) {

    // Create the CDRFuncParameter Objects
    Set<CDRFuncParameter> funcParamSet = getDataProvider().getCdrDataProvider().fetchFuncParams(paramSet);

    for (CDRFuncParameter funcParam : funcParamSet) {
      String paramName = funcParam.getName();
      if (ApicUtil.isVariantCoded(paramName)) {
        continue;
      }
      CompPkgParameter cpParam = this.paramsMap.get(paramName);

      // Create CompPkgParameter objects and add to cache
      if (cpParam == null) {
        // Since component package does not have direct parameter mapping, Tparameter.id is used as id for
        // CompPkgParameter
        cpParam = new CompPkgParameter(getDataProvider(), this.compPkg, funcParam.getID());
      }
      // add param to the map
      this.paramsMap.put(paramName, cpParam);
    }
  }

  /**
   * @return SSD Node ID of the component package object
   */
  public Long getSsdNodeID() {
    return this.compPkg.getCaldataImporterObject().getSsdNodeID();
  }

  /**
   * @return
   */
  private ApicDataProvider getApicDataProvider() {
    return this.compPkg.getApicDataProvider();
  }

  /**
   * @return CPDataProvider
   */
  private CPDataProvider getDataProvider() {
    return this.compPkg.getDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasVersions() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isParamPropsModifiable() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isParamMappingModifiable() {
    // List of parameters cannot be modified for a component package directly.
    // This can be changed only by maintaining BCs or FCs
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRuleImportAllowed() {
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public SortedSet<CompPkgParameter> getSortedParameters() throws SsdInterfaceException {
    return new TreeSet<>(getAllParameters(true).values());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IRuleManager getRuleManager() {
    // TODO Commented since this is causing build failure
    // if (this.ruleManager == null) {
    // this.ruleManager = new CompPkgRuleManager(this.compPkg);
    // }
    return this.ruleManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompPkgParameter getParameter(final String paramName, final String paramType) {
    return this.paramsMap.get(paramName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    return this.compPkg.isModifiable();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public NodeAccessRight getCurrentUserAccessRights() {
    return this.compPkg.getCurrentUserAccessRights();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getID() {
    return this.compPkg.getID();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    return this.compPkg.getToolTip();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.compPkg.getName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getObjectTypeName() {
    return this.compPkg.getCaldataImporterObject().getObjectTypeName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRulesModifiable() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<NodeAccessRight> getAccessRights() {
    return this.compPkg.getAccessRights();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canModifyAccessRights() {
    return this.compPkg.canModifyAccessRights();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canModifyOwnerRights() {
    return this.compPkg.canModifyOwnerRights();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ICalDataImporterObject getCaldataImportObject() {
    return this.compPkg.getCaldataImporterObject();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompPkg getDataObject() {
    return this.compPkg;
  }

  /**
   * Enable refresh of the parameters, once the rules are imported
   */
  @Override
  public void enableParameterRefresh() {
    this.refreshParams = true;
  }

}
