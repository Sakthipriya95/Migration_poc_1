/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.IRuleManager;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.apic.jpa.caldataimport.ICalDataImporterObject;
import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;


/**
 * Interface for ParameterCollection (Function, RuleSet, CP..)
 *
 * @author adn1cob
 * @param <P> obj extending AbstractParameter
 */
public interface IParameterCollection<P extends IParameter<?>> {

  /**
   * Get all paramaters (filters variant coded parameters)
   *
   * @param includeRules true to fetch rules along with parameters
   * @return Map of parameters
   * @throws SsdInterfaceException
   */
  Map<String, P> getAllParameters(boolean includeRules) throws SsdInterfaceException;

  /**
   * Checks if the collection applicable for versions
   *
   * @return true if has versions (eg: func versions)
   */
  boolean hasVersions();

  /**
   * Checks if parameter properties are editable
   *
   * @return true, if properties can be modified
   */
  boolean isParamPropsModifiable();

  /**
   * Checks whether parameter(s) can be added or removed directly from this collection
   *
   * @return true, if parameter(s) can be added or removed
   */
  boolean isParamMappingModifiable();

  /**
   * Checks if it is allowed to import rules
   *
   * @return true for RuleSet, false for Function
   */
  boolean isRuleImportAllowed();

  /**
   * Returns caldata importer object
   *
   * @return implementation of <code>ICalDataImporterObject</code> if rule import is allowed, else <code>null</code>
   */
  ICalDataImporterObject getCaldataImportObject();

  /**
   * Get Sorted set of Parameters
   *
   * @return Parameters set
   * @throws SsdInterfaceException
   */
  SortedSet<P> getSortedParameters() throws SsdInterfaceException;

  /**
   * Get a sorted set of the NodeAccessRights for this function
   *
   * @return NodeAccessRight set
   */
  SortedSet<NodeAccessRight> getAccessRights();

  /**
   * Checks if the CDR Function is modifiable
   *
   * @return true if function params is modifiable
   */
  boolean isModifiable();

  /**
   * Get the current user's access right on this function
   *
   * @return The NodeAccessRight of the current user If the user has no special access rights return NULL
   */
  NodeAccessRight getCurrentUserAccessRights();

  /**
   * @return true if the user can modify access rights of the function
   */
  boolean canModifyAccessRights();

  /**
   * @return boolean if the user has the access to change the owner flag
   */
  boolean canModifyOwnerRights();

  /**
   * @return boolean if the user has the access to create and modify rules
   */
  boolean isRulesModifiable();

  /**
   * Return the rule Manager
   *
   * @return the ruleManager
   */
  IRuleManager getRuleManager();

  /**
   * Get Parameter obj for the give Param name and type
   *
   * @param paramName paramName
   * @param paramType type
   * @return AbstractParameter
   * @throws SsdInterfaceException
   */
  P getParameter(final String paramName, final String paramType) throws SsdInterfaceException;

  /**
   * @return primary key
   */
  Long getID();

  /**
   * @return tooltip
   */
  String getToolTip();

  /**
   * @return name
   */
  String getName();

  /**
   * @return Whether it is function / rule set . Can be used in the title description
   */
  String getObjectTypeName();

  /**
   * Get the data object of this paremter collection
   *
   * @return the associated data object of this parameter collection
   */
  AbstractDataObject getDataObject();

  /**
   * Enable the refresh of the parameters in the parameter collection, when change is not done using command framework.
   * E.g. after adding new parameter via import in component pacakges
   */
  void enableParameterRefresh();

}