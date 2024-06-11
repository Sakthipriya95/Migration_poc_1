/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.model.a2l.Par2Wp;
import com.bosch.caltool.icdm.model.a2l.VarGrp2Wp;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author dmr1cob
 */
public class A2LWpRespImportParser {

  /**
   * bosch email id suffix
   */
  private static final String BOSCH_MAIL_SUFFIX = "bosch.com";
  /**
   *
   */
  private static final String ROBERT_BOSCH = "Robert Bosch";
  private final A2LWPInfoBO a2lWpInfoBo;
  private final A2LFileInfoBO a2lFileInfoBo;

  /**
   * @param a2lWpInfoBo {@link A2LWPInfoBO}
   */
  public A2LWpRespImportParser(final A2LWPInfoBO a2lWpInfoBo) {
    this.a2lWpInfoBo = a2lWpInfoBo;
    this.a2lFileInfoBo = a2lWpInfoBo.getA2lFileInfoBo();
  }

  /**
   * @param paramName Parameter/label name
   * @param wpName workpackage name
   * @param varGrpName variant group name
   * @param respVal responsibility name
   * @param respTypeVal resp type
   * @param isInputFileVarGrp input file var grp
   * @param selA2lVarGrpId selected a2l var grp id
   * @param importInputFileData input file data
   * @param unmatchedVarGrp unmatched variant group
   * @param respAndTypeMap resp and resp type map
   * @param invalidRespType invalid resp type
   * @param respWithMultipleType resp with multiple type
   */
  public void labelBasedParsing(final ImportA2lWpRespInputFileData inputFileData,
      final SortedSet<String> unmatchedVarGrp, final Map<String, String> respAndTypeMap,
      final SortedSet<String> invalidRespType, final SortedSet<String> respWithMultipleType) {
    if (!CommonUtils.isEmptyString(inputFileData.getParamName()) &&
        !CommonUtils.isEmptyString(inputFileData.getWpName())) {
      Par2Wp parWp = new Par2Wp();
      A2LParameter a2lParam = this.a2lFileInfoBo.getA2lParamByName(inputFileData.getParamName());
      // Only params available in selected a2l are imported
      if (a2lParam != null) {
        parWp.setA2lParamId(a2lParam.getParamId());
        parWp.setFuncName(a2lParam.getDefFunction() == null ? null : a2lParam.getDefFunction().getName());
        parWp.setParamName(inputFileData.getParamName());
        parWp.setWpName(inputFileData.getWpName());
        // set resp name and type
        setRespNameAndType(inputFileData.getRespName(), inputFileData.getRespTypeName(), respAndTypeMap,
            invalidRespType, respWithMultipleType, parWp);
        // set var grp
        setVarGrp(inputFileData.getVarGrpName(), inputFileData.isInputFileVarGrp(), inputFileData.getA2lVarGrpId(),
            unmatchedVarGrp, parWp);
        inputFileData.getImportA2lWpRespData().getParamWpRespMap().put(inputFileData.getParamName(), parWp);
      }
    }
  }

  /**
   * @param varGrpName
   * @param isInputFileVarGrp
   * @param selA2lVarGrpId
   * @param unmatchedVarGrp
   * @param parWp
   */
  private void setVarGrp(final String varGrpName, final boolean isInputFileVarGrp, final Long selA2lVarGrpId,
      final SortedSet<String> unmatchedVarGrp, final Par2Wp parWp) {
    if (isInputFileVarGrp) {
      // check if vargrp exists in a2l , if not skip those params
      Long a2lVarGrpId = getA2lVarId(varGrpName);
      if (a2lVarGrpId == null) {
        unmatchedVarGrp.add(varGrpName);
      }
      else {
        parWp.setVarGrpId(a2lVarGrpId);
      }
    }
    else {
      parWp.setVarGrpId(selA2lVarGrpId);
    }
  }

  /**
   * @param respVal
   * @param respTypeVal
   * @param respAndTypeMap
   * @param invalidRespType
   * @param respWithMultipleType
   * @param parWp
   */
  private void setRespNameAndType(final String respVal, final String respTypeVal,
      final Map<String, String> respAndTypeMap, final SortedSet<String> invalidRespType,
      final SortedSet<String> respWithMultipleType, final Par2Wp parWp) {
    String respTypeCode = WpRespType.RB.getCode();
    parWp.setRespTypeCode(respTypeCode);
    if (CommonUtils.isEmptyString(respVal)) {
      parWp.setRespName(ROBERT_BOSCH);
    }
    else {
      // value is set if resp type column is available in input excel
      parWp.setExcelWPRespTypeColAvail(CommonUtils.isNotEmptyString(respTypeVal));
      parWp.setRespName(respVal);
      parWp.setRespTypeCode(
          setRespTypeCode(invalidRespType, respTypeCode, respTypeVal, respAndTypeMap, respWithMultipleType, respVal));
    }
  }

  /**
   * @param invalidRespType
   * @param respTypeCode
   * @param respTypeVal
   * @param respTypeCode2
   * @param respAndTypeMap
   * @param respWithMultipleType
   * @param respVal
   * @return
   */
  public String setRespTypeCode(final SortedSet<String> invalidRespType, String respTypeCode, final String respTypeVal,
      final Map<String, String> respAndTypeMap, final SortedSet<String> respWithMultipleType, final String respVal) {
    if (CommonUtils.isNotEmptyString(respTypeVal)) {
      // get the responsibility type
      WpRespType typeFromUI = WpRespType.getTypeFromUI(respTypeVal);
      if (null == typeFromUI) {
        invalidRespType.add(respTypeVal);
      }
      else {
        respTypeCode = typeFromUI.getCode();
        fillRespTypeCollection(respAndTypeMap, respWithMultipleType, respVal, respTypeCode);
      }
    }
    return respTypeCode;
  }

  /**
   * @param respAndTypeMap resp and resp type map
   * @param respWithMultipleType resp with multiple type
   * @param respVal resp value
   * @param respTypeCode resp type
   */
  public void fillRespTypeCollection(final Map<String, String> respAndTypeMap,
      final SortedSet<String> respWithMultipleType, final String respVal, final String respTypeCode) {
    String respTypeInMap = respAndTypeMap.get(respVal);
    if (respTypeInMap == null) {
      respAndTypeMap.put(respVal, respTypeCode);
    }
    else if (CommonUtils.isNotEqual(respTypeInMap, respTypeCode)) {
      respWithMultipleType.add(respVal);
    }
  }

  /**
   * @param varGrpName variant group name
   * @return variant group id
   */
  public Long getA2lVarId(final String varGrpName) {
    Collection<A2lVariantGroup> a2lVarGrps = this.a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap().values();
    for (A2lVariantGroup selVarGrp : a2lVarGrps) {
      if (selVarGrp.getName().equalsIgnoreCase(varGrpName)) {
        return selVarGrp.getId();
      }
    }
    return null;
  }


  /**
   * @param inputFileData {@link ImportA2lWpRespInputFileData}
   * @param funcWpRespMap func wp resp map
   * @param unmatchedVarGrp unmatched var grp
   * @param respAndTypeMap resp and type map
   * @param invalidRespType invalid resp type
   * @param respWithMultipleType responsibility with multiple type
   * @throws InvalidInputException exception
   */
  public void funcBasedParsing(final ImportA2lWpRespInputFileData inputFileData,
      final Map<String, Map<String, String>> funcWpRespMap, final SortedSet<String> unmatchedVarGrp,
      final Map<String, String> respAndTypeMap, final SortedSet<String> invalidRespType,
      final SortedSet<String> respWithMultipleType)
      throws InvalidInputException {
    if (!CommonUtils.isEmptyString(inputFileData.getFuncName()) &&
        !CommonUtils.isEmptyString(inputFileData.getWpName())) {
      // Only functions available in selected a2l are imported
      Function function = this.a2lFileInfoBo.getAllFunctionMap().get(inputFileData.getFuncName());
      String respTypeCode = WpRespType.RB.getCode();
      if (CommonUtils.isEmptyString(inputFileData.getRespName())) {
        inputFileData.setRespName(ROBERT_BOSCH);
      }
      else {
        respTypeCode = setRespTypeCode(invalidRespType, respTypeCode, inputFileData.getRespTypeName(), respAndTypeMap,
            respWithMultipleType, inputFileData.getRespName());
      }
      if ((null != function) && funcWpRespMap.containsKey(inputFileData.getFuncName())) {
        Map<String, String> wpRespMap = funcWpRespMap.get(inputFileData.getFuncName());
        if (!(wpRespMap.containsKey(inputFileData.getWpName()) &&
            wpRespMap.get(inputFileData.getWpName()).equalsIgnoreCase(inputFileData.getRespName()))) {
          throw new InvalidInputException(
              "Multiple workpackages are identified for function " + inputFileData.getFuncName());
        }
      }
      else if (null != function) {
        Map<String, String> wpRespMap = new HashMap<>();
        wpRespMap.put(inputFileData.getWpName(), inputFileData.getRespName());
        funcWpRespMap.put(inputFileData.getFuncName(), wpRespMap);

        // for a func wp and resp are taken over for all the params of the function
        Set<String> paramList = this.a2lFileInfoBo.getParamListfromFunction(inputFileData.getFuncName());


        // set the a2l var grp id
        Long varGrpId = setA2lVarGrp(inputFileData.isInputFileVarGrp(), inputFileData.getA2lVarGrpId(), unmatchedVarGrp,
            inputFileData.getVarGrpName());

        // set par 2 wp list
        setPar2WpList(inputFileData.getImportA2lWpRespData(), inputFileData.getFuncName(), inputFileData.getWpName(),
            inputFileData.getRespName(), respTypeCode, paramList, varGrpId);
      }
    }
  }

  /**
   * @param isExcelVarGrp
   * @param selA2lVarGrpId
   * @param unmatchedVarGrp
   * @param varGrpName
   * @return
   */
  private Long setA2lVarGrp(final boolean isExcelVarGrp, final Long selA2lVarGrpId,
      final SortedSet<String> unmatchedVarGrp, final String varGrpName) {
    Long varGrpId = null;
    if (isExcelVarGrp) {
      // check if vargrp exists in a2l , if not skip those params
      // TO-DO
      Long a2lVarGrpId = getA2lVarId(varGrpName);
      if (a2lVarGrpId == null) {
        unmatchedVarGrp.add(varGrpName);
      }
      else {
        varGrpId = a2lVarGrpId;
      }
    }
    else {
      varGrpId = selA2lVarGrpId;
    }
    return varGrpId;
  }

  /**
   * @param importInputFileData
   * @param funcName
   * @param wpName
   * @param respName
   * @param respTypeCode
   * @param paramList
   * @param varGrpId
   */
  private void setPar2WpList(final ImportA2lWpRespData importInputFileData, final String funcName, final String wpName,
      final String respName, final String respTypeCode, final Set<String> paramList, final Long varGrpId) {
    for (String paramName : paramList) {
      Par2Wp parWp = new Par2Wp();
      A2LParameter a2lParam = this.a2lFileInfoBo.getA2lParamByName(paramName);
      parWp.setA2lParamId(a2lParam.getParamId());
      parWp.setFuncName(funcName);
      parWp.setParamName(paramName);
      parWp.setWpName(wpName);
      parWp.setRespName(respName);
      parWp.setVarGrpId(varGrpId);
      parWp.setRespTypeCode(respTypeCode);
      importInputFileData.getParamWpRespMap().put(paramName, parWp);
    }
  }

  /**
   * @param isInputFileVarGrp
   * @param a2lVarGrpId
   * @param importInputFileData
   * @param varGrp2WpRespMap
   * @param wpRespMap
   * @param respAndTypeMap
   * @param invalidRespType
   * @param respWithMultipleType
   * @param unmatchedVarGrp
   * @param wpName
   * @param varGrpName
   * @param respName
   * @param respTypeCode
   */
  public void wpDefBasedParsing(final ImportA2lWpRespInputFileData inputFileData,
      final Map<String, Map<String, String>> varGrp2WpRespMap, final Map<String, String> wpRespMap,
      final Map<String, String> respAndTypeMap, final SortedSet<String> invalidRespType,
      final SortedSet<String> respWithMultipleType, final SortedSet<String> unmatchedVarGrp) {
    if (!CommonUtils.isEmptyString(inputFileData.getWpName())) {
      String respTypeCode = WpRespType.RB.getCode();
      if (CommonUtils.isEmptyString(inputFileData.getRespName())) {
        inputFileData.setRespName(ROBERT_BOSCH);
      }
      else {
        respTypeCode = setRespTypeCode(invalidRespType, respTypeCode, inputFileData.getRespTypeName(), respAndTypeMap,
            respWithMultipleType, inputFileData.getRespName());
      }

      setVarGrp(inputFileData, varGrp2WpRespMap, wpRespMap, unmatchedVarGrp, respTypeCode);
    }
  }

  /**
   * @param isInputFileVarGrp
   * @param a2lVarGrpId
   * @param importInputFileData
   * @param varGrp2WpRespMap
   * @param wpRespMap
   * @param unmatchedVarGrp
   * @param wpName
   * @param varGrpName
   * @param respName
   * @param respTypeCode
   */
  private void setVarGrp(final ImportA2lWpRespInputFileData inputFileData,
      final Map<String, Map<String, String>> varGrp2WpRespMap, final Map<String, String> wpRespMap,
      final SortedSet<String> unmatchedVarGrp, final String respTypeCode) {
    if (inputFileData.isInputFileVarGrp()) {
      // check if vargrp exists in a2l , if not skip those params
      Long varGrpId = getA2lVarId(inputFileData.getVarGrpName());
      if (varGrpId == null) {
        unmatchedVarGrp.add(inputFileData.getVarGrpName());
      }
      else {
        Map<String, String> varWpRespMap = varGrp2WpRespMap.get(inputFileData.getVarGrpName());
        if (varWpRespMap == null) {
          varWpRespMap = new HashMap<>();
        }
        if (!varWpRespMap.containsKey(inputFileData.getWpName())) {
          varWpRespMap.put(inputFileData.getWpName(), inputFileData.getRespName());
          varGrp2WpRespMap.put(inputFileData.getVarGrpName(), varWpRespMap);
          VarGrp2Wp varGrp = new VarGrp2Wp();
          varGrp.setRespName(inputFileData.getRespName());
          varGrp.setRespTypeCode(respTypeCode);
          varGrp.setVarGrpId(varGrpId);
          varGrp.setWpName(inputFileData.getWpName());
          varGrp.setExcelWPRespTypeColAvail(CommonUtils.isNotEmptyString(inputFileData.getRespTypeName()));
          inputFileData.getImportA2lWpRespData().getVarGrp2WpRespSet().add(varGrp);
        }
      }
    }
    else {
      VarGrp2Wp varGrp = new VarGrp2Wp();
      if (!wpRespMap.containsKey(inputFileData.getWpName())) {
        varGrp.setRespName(inputFileData.getRespName());
        varGrp.setRespTypeCode(respTypeCode);
        varGrp.setWpName(inputFileData.getWpName());
        varGrp.setVarGrpId(inputFileData.getA2lVarGrpId());
        wpRespMap.put(inputFileData.getWpName(), inputFileData.getRespName());
        inputFileData.getImportA2lWpRespData().getVarGrp2WpRespSet().add(varGrp);
      }
    }
  }

  /**
   * @param missingVarGrp
   * @param unmatchedVarGrp
   */
  public void showMissingVarGrps(final SortedSet<String> unmatchedVarGrp) {
    StringBuilder missingVarGrp = new StringBuilder();
    if (!unmatchedVarGrp.isEmpty()) {
      missingVarGrp.append(
          "The following variant groups are not available in selected a2l file and params mapped to it are skipped during import : ");
      for (String varGrp : unmatchedVarGrp) {
        missingVarGrp.append(varGrp).append(",");
      }
      CDMLogger.getInstance().infoDialog(missingVarGrp.toString().substring(0, missingVarGrp.length() - 1),
          Activator.PLUGIN_ID);
    }
  }

  /**
   * @param invalidRespType
   * @throws InvalidInputException
   */
  public void showInvalidRespTypes(final SortedSet<String> invalidRespType) throws InvalidInputException {
    StringBuilder invalidRespTypes = new StringBuilder();
    if (!invalidRespType.isEmpty()) {
      invalidRespTypes.append(
          "Only values 'Robert Bosch, Customer, Others' are allowed as Responsibility Type. Not valid values found in the file:\n");
      for (String respTypeString : invalidRespType) {
        invalidRespTypes.append(respTypeString).append(",");
      }
      throw new InvalidInputException(invalidRespTypes.toString().substring(0, invalidRespTypes.length() - 1));
    }
  }

  /**
   * @param respWithMultipleType SortedSet<String>
   * @throws InvalidInputException
   */
  public void showMutlipleTypeForSameResp(final SortedSet<String> respWithMultipleType) throws InvalidInputException {
    StringBuilder multipleRespTypeMsg = new StringBuilder();
    if (!respWithMultipleType.isEmpty()) {
      multipleRespTypeMsg.append(
          "Only one responsibility type for a responsible is valid.Following responsibles are assigned to different responsible types.  Please update your file and try again .\n");
      for (String respTypeString : respWithMultipleType) {
        multipleRespTypeMsg.append(respTypeString).append(",");
      }
      throw new InvalidInputException(multipleRespTypeMsg.toString().substring(0, multipleRespTypeMsg.length() - 1));
    }
  }

  /**
   * @param respAndTypeMapInExcel
   * @param prefixForResp
   * @throws InvalidInputException
   */
  public void showMisMatchOfRespTypeInDb(final Map<String, String> respAndTypeMapInExcel, final String prefixForResp)
      throws InvalidInputException {
    Map<String, String> respAndTypeMapInDB = new HashMap<>();
    Set<String> unMatchedResp = new HashSet<>();
    Set<String> mailRespsNotInRB = new HashSet<>();
    this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().values()
        .forEach(respEntry -> respAndTypeMapInDB.put(respEntry.getAliasName(), respEntry.getRespType()));


    respAndTypeMapInExcel.entrySet().forEach(entry -> {
      String respName = entry.getKey();
      String typeInDb = respAndTypeMapInDB.get(prefixForResp + respName);
      String respType = entry.getValue();
      if (typeInDb != null) {
        if (CommonUtils.isNotEqual(typeInDb, respType)) {
          unMatchedResp.add(respName);
        }
      }
      // look for email id in resp name
      if (respName.endsWith(BOSCH_MAIL_SUFFIX)) {
        // if so check if resp type is Robert Bosch
        if (WpRespType.RB != WpRespType.getType(respType)) {
          mailRespsNotInRB.add(respName);
        }
      }
    });

    if (CommonUtils.isNotEmpty(unMatchedResp)) {
      StringBuilder misMatchOfRespType = new StringBuilder();
      misMatchOfRespType.append(
          "Following responsibles are already assigned to different responsible types. Do you want to override the responsibility type?\n");
      for (String varGrp : unMatchedResp) {
        misMatchOfRespType.append(varGrp).append(",");
      }
      boolean confirm = MessageDialogUtils.getConfirmMessageDialog("Override Responsibility Type",
          misMatchOfRespType.toString().substring(0, misMatchOfRespType.length() - 1));
      if (!confirm) {
        throw new InvalidInputException("Import cannot continue due to mismatch of Responsibility types!");
      }
    }

    // if there are bosch email responsibilities with resp type other than 'Robert Bosch'
    if (CommonUtils.isNotEmpty(mailRespsNotInRB)) {
      StringBuilder respNotInRB = new StringBuilder();
      respNotInRB.append(
          "Following responsibles should belong to 'Robert Bosch' responsibility type. Please update excel file and continue the import!\n");
      for (String respName : mailRespsNotInRB) {
        respNotInRB.append(respName).append(",").append("\n");
      }
      // throw exception to stop the import
      throw new InvalidInputException(respNotInRB.toString().substring(0, respNotInRB.length() - 2));
    }
  }
}
