/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.FeatureValueModel;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * Interface that performs rule insert/update/delete/fetch for Function based rules and RuleSet Rules
 *
 * @author jvi6cob
 */
public interface IRuleManager {

  /**
   * to create rules in SSD based on the information in the model
   *
   * @param model - to be filled
   * @param apicDataProvider ApicDataProvider
   * @param paramName parameter name
   * @return - 0 when rule gets created and populate CDRModel with ruleid == -1 when label is not defined in SSD == -2
   *         when rule validation fails * @throws Exception - exception during insert are thrown
   * @throws IcdmException exception during insert
   */
  SSDMessage createRule(final CDRRule model, final String paramName) throws IcdmException;

  /**
   * to create more than one rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @param paramName String
   * @return - {@link SSDMessage}
   * @throws IcdmException exception during creation
   */
  SSDMessage createMultipleRules(final List<CDRRule> cdrRules) throws IcdmException;

  /**
   * to update an exiting rule
   *
   * @param model - model to be filled with data for update
   * @return 0 on success
   * @throws IcdmException exception during update
   */
  SSDMessage updateRule(final CDRRule model) throws IcdmException;

  /**
   * to update more than one rule at the same time
   *
   * @param cdrRules - list of rules to be created
   * @return - {@link SSDMessage}
   * @throws IcdmException exception
   */
  SSDMessage updateMultipleRules(final List<CDRRule> cdrRules) throws IcdmException;

  /**
   * to delete an exiting rule
   *
   * @param model CDRRule to be filled with data for update
   * @param apicDataProvider ApicDataProvider
   * @return 0 on success
   * @throws IcdmException exception during update
   */
  SSDMessage deleteRule(final CDRRule model) throws IcdmException;

  /**
   * to delete the rules
   *
   * @param cdrRules - list of rules to be deleted
   * @return - message
   * @throws IcdmException exception
   */
  SSDMessage deleteMultipleRules(final List<CDRRule> cdrRules) throws IcdmException;

  /**
   * to read rule for one parameter
   *
   * @param labelName - parameter name
   * @return list of cdrrules
   * @throws IcdmException if there is an error while reading rules
   */
  List<CDRRule> readRule(final String labelName) throws IcdmException;

  /**
   * Method for fetching the rules Method to read rules from database for a list of labels
   *
   * @param labelNames - name of the funcions
   * @return - populated model. Map of Label name and its rules
   * @throws IcdmException
   */
  Map<String, List<CDRRule>> readRule(final List<String> labelNames) throws IcdmException;

  // ICDM-1476
  /**
   * Read all rules from SSD database, for the parameter collection
   *
   * @return - populated model. Map of Label name and its rules
   * @throws IcdmException
   */
  Map<String, List<CDRRule>> readAllRules() throws IcdmException;

  /**
   * Method to read rules based on the list of labels supplied
   *
   * @param labelNames - parameter names
   * @param dependencies - feature value combination
   * @return - CDR rules
   * @throws IcdmException
   */
  Map<String, List<CDRRule>> readRuleForDependency(final List<String> labelNames,
      final List<FeatureValueModel> dependencies)
      throws IcdmException;

  /**
   * Method to get rules history
   *
   * @param rule selected rule
   * @return history of selected rule
   * @throws IcdmException
   */
  List<CDRRuleExt> getRuleHistory(final ReviewRule rule) throws IcdmException;

  /**
   * Method to get compli rule history
   *
   * @param rule selected rule
   * @return history of selected rule
   * @throws IcdmException
   */
  List<CDRRuleExt> getRuleHistoryForNodeCompli(final ReviewRule rule) throws IcdmException;

}
