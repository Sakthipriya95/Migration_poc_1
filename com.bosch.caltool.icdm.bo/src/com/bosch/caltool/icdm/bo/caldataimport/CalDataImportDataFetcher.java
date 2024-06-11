/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.cdr.MultipleRuleSetParamCommand;
import com.bosch.caltool.icdm.bo.cdr.RuleSetLoader;
import com.bosch.caltool.icdm.bo.comppkg.CompPackageLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportInput;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RulesetParamMultiInput;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;

/**
 * @author rgo7cob
 */
public class CalDataImportDataFetcher extends AbstractSimpleBusinessObject {


  private CalDataImporter importer;

  /**
   * @param serviceData serviceData
   */
  public CalDataImportDataFetcher(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param inputfileStreamMap inputfileStreamMap
   * @param paramColType param collection type
   * @param paramColId param collection ID
   * @param funcVersion function version, if param collection is a function
   * @return import data
   * @throws IcdmException data retreval error
   */
  public CalDataImportData readInputfile(final Map<String, InputStream> inputfileStreamMap, final String paramColId,
      final String paramColType, final String funcVersion)
      throws IcdmException {

    return doReadInputFile(inputfileStreamMap, getParamColObject(Long.valueOf(paramColId), paramColType), funcVersion);

  }

  /**
   * @param paramCol
   * @return
   */
  private SSDCase getSSDCase(final ParamCollection paramCol) {
    if (paramCol instanceof Function) {
      return null;
    }
    return SSDCase.REVIEW;
  }


  /**
   * @param paramCol
   * @return
   */
  private ParameterClass getSSDParamClass(final ParamCollection paramCol) {
    if (paramCol instanceof Function) {
      return null;
    }
    return ParameterClass.CUSTSPEC;
  }

  /**
   * @param paramColId
   * @param paramColType
   * @throws DataException
   */
  private ParamCollection getParamColObject(final Long paramColId, final String paramColType) throws DataException {
    if (ApicConstants.FUNC_NODE_TYPE.equalsIgnoreCase(paramColType)) {
      return new FunctionLoader(getServiceData()).getDataObjectByID(paramColId);
    }

    if (ApicConstants.RULE_SET_NODE_TYPE.equalsIgnoreCase(paramColType)) {
      return new RuleSetLoader(getServiceData()).getDataObjectByID(paramColId);
    }

    if (ApicConstants.COMPONENT_PKG_NODE_TYPE.equalsIgnoreCase(paramColType)) {
      return new CompPackageLoader(getServiceData()).getDataObjectByID(paramColId);
    }
    return null;
  }

  /**
   * @param funcVersion
   * @param paramColId
   * @param paramColType
   * @throws DataException
   */
  private ICalDataImportParamDetailsLoader getFunctionDetailsLoader(final ParamCollection paramColObject,
      final String funcVersion) {

    if (paramColObject instanceof Function) {
      return new FunctionParamDetailsLoader((Function) paramColObject, funcVersion, getServiceData());
    }

    if (paramColObject instanceof RuleSet) {
      return new RuleSetParamDetailsLoader((RuleSet) paramColObject, getServiceData());
    }

    if (paramColObject instanceof CompPackage) {
      return new CompPkgParamDetailsLoader((CompPackage) paramColObject, getServiceData());
    }

    return null;
  }


  /**
   * @param importer
   * @param inputfileStreamMap
   * @param funcVersion
   * @return
   * @throws IcdmException
   */
  private CalDataImportData doReadInputFile(final Map<String, InputStream> inputfileStreamMap,
      final ParamCollection paramCol, final String funcVersion)
      throws IcdmException {

    this.importer = new CalDataImporter(paramCol, ParserLogger.getInstance(), getServiceData());

    this.importer.setInputFile(inputfileStreamMap);
    this.importer.setParamDetailsLoader(getFunctionDetailsLoader(paramCol, funcVersion));

    this.importer.readInputFile(getSSDParamClass(paramCol), getSSDCase(paramCol), inputfileStreamMap);
    return this.importer.getImportData();

  }

  /**
   * @param data import data
   * @return CalDataImportData
   * @throws IcdmException data retreval error
   */
  public CalDataImportData getCompareObjSet(final CalDataImportInput data) throws IcdmException {
    ParamCollection paramColObject = getParamColObject(Long.valueOf(data.getFuncId()), data.getParamColType());
    this.importer = new CalDataImporter(paramColObject, ParserLogger.getInstance(), getServiceData());
    this.importer.setParamDetailsLoader(getFunctionDetailsLoader(paramColObject, null));
    this.importer.setImportData(data.getCalDataImportData());
    this.importer.getModifyingData(paramColObject);
    return this.importer.getImportData();

  }

  /**
   * @param input Data Import Input
   * @return import summary object
   * @throws IcdmException data retreval error
   */
  public CalDataImportSummary createParamsAndRules(final CalDataImportInput input) throws IcdmException {
    ParamCollection paramColObject = getParamColObject(Long.valueOf(input.getFuncId()), input.getParamColType());
    this.importer = new CalDataImporter(paramColObject, ParserLogger.getInstance(), getServiceData());
    this.importer.setParamDetailsLoader(getFunctionDetailsLoader(paramColObject, null));
    this.importer.setImportData(input.getCalDataImportData());

    createRuleSetParams(paramColObject, input);

    AbstractImportRuleManager ruleImportManager = this.importer.getRuleImportManager(paramColObject);

    List<AbstractCommand> paramPropsCmdList = ruleImportManager.updateParamProps(
        input.getCalDataImportData().getNewParamPropsMap(), input.getCalDataImportData().getParamNameTypeMap(),
        input.getCalDataImportData().getOldParamClassMap(), input.getCalDataImportData().getCalDataCompMap());

    executeCommand(paramPropsCmdList);


    return this.importer.importCalData(paramColObject);

  }

  /**
   * @param paramPropsCommand
   * @throws IcdmException
   */
  private void executeCommand(final List<AbstractCommand> paramPropsCommand) throws IcdmException {
    if (CommonUtils.isNotEmpty(paramPropsCommand)) {
      // add command to command stack
      for (AbstractCommand cmd : paramPropsCommand) {
        ServiceData serviceData = cmd.getServiceData();
        CommandExecuter cmdExecute = serviceData.getCommandExecutor();
        cmdExecute.execute(cmd);
      }
    }
  }

  /**
   * @param paramColObject
   * @throws IcdmException
   */
  private void createRuleSetParams(final ParamCollection paramColObject, final CalDataImportInput input)
      throws IcdmException {

    if (paramColObject instanceof RuleSet) {
      CalDataImportData calDataImportData = input.getCalDataImportData();
      List<String> paramsTobeInserted = calDataImportData.getParamsTobeInserted();
      Map<String, Function> paramFuncObjMap = calDataImportData.getParamFuncObjMap();
      Map<String, Parameter> paramNameObjMap = calDataImportData.getParamNameObjMap();
      Set<RuleSetParameter> createRuleSetParamSet =
          createRuleSetParamSet(paramColObject, paramsTobeInserted, paramFuncObjMap, paramNameObjMap);

      createCommand(createRuleSetParamSet);

    }
  }

  /**
   * @param createRuleSetParamSet
   * @throws IcdmException
   */
  private void createCommand(final Set<RuleSetParameter> createRuleSetParamSet) throws IcdmException {
    RulesetParamMultiInput multiParamInput = new RulesetParamMultiInput();
    multiParamInput.setRuleSetParamtoInsert(createRuleSetParamSet);

    MultipleRuleSetParamCommand command = new MultipleRuleSetParamCommand(getServiceData(), multiParamInput);
    executeCommand(command);
  }

  /**
   * @param command to execute
   * @throws IcdmException error in executing command
   */
  private final void executeCommand(final AbstractSimpleCommand command) throws IcdmException {
    ServiceData serviceData = command.getServiceData();
    CommandExecuter cmdExecute = serviceData.getCommandExecutor();
    cmdExecute.execute(command);
  }


  /**
   * @param paramColObject
   * @param paramsTobeInserted
   * @param paramFuncObjMap
   * @param paramNameObjMap
   */
  private Set<RuleSetParameter> createRuleSetParamSet(final ParamCollection paramColObject,
      final List<String> paramsTobeInserted, final Map<String, Function> paramFuncObjMap,
      final Map<String, Parameter> paramNameObjMap) {

    Set<RuleSetParameter> ruleSetParamList = new HashSet<>();
    paramsTobeInserted.forEach(paramName -> {
      RuleSetParameter ruleSetParameter = new RuleSetParameter();

      ruleSetParameter.setName(paramName);
      ruleSetParameter.setDescription(paramNameObjMap.get(paramName).getDescription());
      ruleSetParameter.setLongName(paramNameObjMap.get(paramName).getLongName());
      ruleSetParameter.setParamId(paramNameObjMap.get(paramName).getId());
      ruleSetParameter.setFuncId(paramFuncObjMap.get(paramName).getId());
      ruleSetParameter.setRuleSetId(paramColObject.getId());
      ruleSetParamList.add(ruleSetParameter);

    });

    return ruleSetParamList;
  }

}
