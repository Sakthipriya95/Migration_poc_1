/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.caldataimport.ICalDataImporterObject;
import com.bosch.caltool.apic.jpa.rules.bo.IParameterCollection;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;


/**
 * AbstractParameterCollection - (CDRFunction, RuleSet...) <br>
 *
 * @author adn1cob
 * @param <P> (CDRFuncParameter,RuleSetParameter)
 */
@Deprecated
public abstract class AbstractParameterCollection<P extends AbstractParameter<?>> extends AbstractCdrObject
    implements IParameterCollection<P> {

  /**
   * All params of CDRFunction or RuleSet
   */
  protected Map<String, P> paramsMap;

  /**
   * changed the pattern for the display removed $ to check if [1] is in middle iCDM-605 <br>
   * Defines Pattern for Variant coded parameter
   */
  private static final String PATTERN_VARIANT_CODE = "(\\[\\d+\\])";

  /**
   * Check if rules has been fetched
   */
  protected boolean rulesFetched;

  /**
   * RuleManager Reference
   */
  protected IRuleManager ruleManager;


  /**
   * @param cdrDataProvider data provider
   * @param objID primary key
   */
  protected AbstractParameterCollection(final AbstractDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public abstract Map<String, P> getAllParameters(boolean includeRules) throws SsdInterfaceException;

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract boolean hasVersions();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract boolean isParamPropsModifiable();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRuleImportAllowed() {
    return this instanceof ICalDataImporterObject;
  }


  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public SortedSet<P> getSortedParameters() throws SsdInterfaceException {
    final SortedSet<P> sortedParams = new TreeSet<P>();
    if (this.paramsMap == null) {
      this.paramsMap = getAllParameters(true);
    }
    sortedParams.addAll(this.paramsMap.values());
    return sortedParams;
  }

  /**
   * Filter the VARIANT CODED params
   *
   * @param allParamsMap all params
   * @return filtered map
   */
  // iCDM-605
  protected Map<String, P> filterVariantCodedParams(final Map<String, P> allParamsMap) {

    final Pattern pattern = Pattern.compile(PATTERN_VARIANT_CODE);
    final Map<String, P> filteredParamsMap = new ConcurrentHashMap<>();

    for (P param : allParamsMap.values()) {
      final Matcher matcher = pattern.matcher(param.getName());
      // skip variant coded labels
      if (!matcher.find()) {
        filteredParamsMap.put(param.getName(), param);
      }
    }
    return filteredParamsMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<NodeAccessRight> getAccessRights() {
    SortedSet<NodeAccessRight> resultSet;
    resultSet = getApicDataProvider().getNodeAccessRights(getID());
    return resultSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {

    // TODO: To check for additional rights (from SSD..?) based on further requirement
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();

    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NodeAccessRight getCurrentUserAccessRights() {
    return getApicDataProvider().getNodeAccRight(getID());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canModifyAccessRights() {
    // APIC_WRITE users will have access by default
    if (getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    // If user has GRANT option, then he can modify access right page
    return (curUserAccRight != null) && curUserAccRight.hasGrantOption();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canModifyOwnerRights() {
    // APIC_WRITE users will have access by default
    if (getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    return (curUserAccRight != null) && curUserAccRight.isOwner();
  }

  /**
   * Return if the rules has been already loaded
   *
   * @return true if already loaded
   */
  protected boolean hasRulesLoaded() {
    return this.rulesFetched;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public abstract IRuleManager getRuleManager();

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public P getParameter(final String paramName, final String paramType) throws SsdInterfaceException {
    return getAllParameters(false).get(paramName);
  }

}
