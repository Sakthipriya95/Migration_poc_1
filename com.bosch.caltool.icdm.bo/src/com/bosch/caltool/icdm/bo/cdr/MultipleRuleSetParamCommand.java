package com.bosch.caltool.icdm.bo.cdr;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RulesetParamMultiInput;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Command class for PredefinedValidity
 *
 * @author dmo5cob
 */
public class MultipleRuleSetParamCommand extends AbstractSimpleCommand {


  /**
   * Input model
   */
  private final RulesetParamMultiInput inputData;

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public MultipleRuleSetParamCommand(final ServiceData serviceData, final RulesetParamMultiInput input)
      throws IcdmException {
    super(serviceData);
    this.inputData = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    for (RuleSetParameter ruleSetParam : this.inputData.getRuleSetParamtoInsert()) {
      RuleSetParameterCommand command =
          new RuleSetParameterCommand(getServiceData(), ruleSetParam, COMMAND_MODE.CREATE);
      executeChildCommand(command);
      ruleSetParam.setId(command.getObjId());
    }


    if (!this.inputData.getRuleSetParamtoDel().isEmpty()) {

      RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
      RuleSet ruleSet = null;

      List<String> paramNames = new ArrayList<>();
      for (RuleSetParameter ruleSetParam : this.inputData.getRuleSetParamtoDel()) {
        paramNames.add(ruleSetParam.getName());

      }
      // Use iterator to get the first element
      RuleSetParameter ruleSetParam = this.inputData.getRuleSetParamtoDel().iterator().next();
      ruleSet = ruleSetLoader.getDataObjectByID(ruleSetParam.getRuleSetId());
      RuleSetParameterLoader ruleSetParamLoader = new RuleSetParameterLoader(getServiceData());

      Long ssdNodeId = ruleSetParamLoader.getRuleSetEntity(ruleSet.getId()).getSsdNodeId();

      final Map<String, List<CDRRule>> rulesMap =
          new SSDServiceHandler(getServiceData()).readReviewRule(paramNames, ssdNodeId);


      for (RuleSetParameter ruleSetParamToDel : this.inputData.getRuleSetParamtoDel()) {
        RuleSetParameterCommand command =
            new RuleSetParameterCommand(getServiceData(), ruleSetParamToDel, COMMAND_MODE.DELETE);
        command.setCdrRuleMap(rulesMap);
        executeChildCommand(command);
      }

    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {

    return true;
  }

}
