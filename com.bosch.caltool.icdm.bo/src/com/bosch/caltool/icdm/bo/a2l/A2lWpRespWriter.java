/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.common.bo.a2l.A2lObjectIdentifierValidator;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFileExportServiceInput;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ice.ara2l.a2lextender.writer.A2LWriter;
import com.bosch.ice.ara2l.a2lparser.aomparser.component.CharacteristicImpl;
import com.bosch.ice.ara2l.a2lparser.aomparser.component.GroupImpl;
import com.bosch.ice.ara2l.a2lparserapi.aam.AAMException;
import com.bosch.ice.ara2l.a2lparserapi.aam.ModuleModifier;
import com.bosch.ice.ara2l.a2lparserapi.aom.A2LDocument;
import com.bosch.ice.ara2l.a2lparserapi.aom.AOMException;
import com.bosch.ice.ara2l.a2lparserapi.aom.Group;
import com.bosch.ice.ara2l.a2lparserapi.aom.Module;

/**
 * @author rgo7cob
 */
public class A2lWpRespWriter {


  /**
   * Grpoup Description suffix
   */
  private static final String GROUP_DESC_SUFFIX = "_from_ICDM";
  /**
   * Grpoup Description suffix for PAL tool
   */
  private static final String GROUP_DESC_SUFFIX_FROM_PAL = "_from_PAL";
  /**
   * root resp Desc
   */
  private static final String ROOT_RESP_DESC = "_Responsibilities_from_ICDM";
  /**
   * root wp desc
   */
  private static final String ROOT_WP_DESC = "_Workpackages_from_ICDM";


  /**
   * root wp desc
   */
  private static final String ROOT_RESP_FUNC_DESC = "_RespFunc_from_ICDM";

  /**
   * root wp desc
   */
  private static final String ROOT_WP_RESP_DESC = "_RespWp_from_ICDM";

  /**
   * root wp desc
   */
  private static final String ROOT_WP_RESP_FUNC_DESC = "_RespWpFunc_from_ICDM";


  private final A2LFileExportServiceInput a2lExpServInp;
  private final Map<String, Characteristic> a2lcharMap;
  private final Map<String, List<String>> respFuncMap = new HashMap<>();

  private final Map<String, List<String>> wpRespMap = new HashMap<>();

  private final Map<String, List<String>> wpRespFuncMap = new HashMap<>();
  private final Map<String, A2lWorkPackage> a2lWorkPackageMap;
  private final Map<String, A2lResponsibility> a2lResponsibilityMap = new HashMap<>();


  /**
   * @param a2lExpServInp
   * @param a2lCharMap
   * @param a2lRespModel
   * @param a2lWorkPackageMap
   */
  public A2lWpRespWriter(final A2LFileExportServiceInput a2lExpServInp, final Map<String, Characteristic> a2lCharMap,
      final Map<String, A2lWorkPackage> a2lWorkPackageMap, final A2lResponsibilityModel a2lRespModel) {
    this.a2lExpServInp = a2lExpServInp;
    this.a2lcharMap = a2lCharMap;
    this.a2lWorkPackageMap = a2lWorkPackageMap;

    createA2lRespMap(a2lRespModel);
  }


  /**
   * @param a2lRespModel
   * @return
   */
  private void createA2lRespMap(final A2lResponsibilityModel a2lRespModel) {
    Map<Long, A2lResponsibility> a2lRespMap = a2lRespModel.getA2lResponsibilityMap();

    for (A2lResponsibility a2lResp : a2lRespMap.values()) {
      this.a2lResponsibilityMap.put(a2lResp.getName(), a2lResp);

    }

  }


  public void writeA2lWpResp(final A2LDocument a2lDoc, final Map<String, Set<String>> wpParamMap,
      final Map<String, Set<String>> respParamMap, String filePath, final String modifiedA2lPath)
      throws IcdmException {
    boolean isA2lWithGroups = false;
    try {
      Module a2LModule = a2lDoc.getA2LProject().getModule();
      Collection<Group> groupList = a2LModule.getGroups();

      if (CommonUtils.isNotEmpty(groupList)) {
        isA2lWithGroups = true;
        String tempModifiedA2lPath = modifiedA2lPath.replace(".A2L", "_TEMP.A2L");
        clearIcdmRespGroups(groupList);
        A2LWriter tempA2lWriter = new A2LWriter(filePath, tempModifiedA2lPath, a2lDoc);
        tempA2lWriter.write();
        filePath = tempModifiedA2lPath;
      }
      createGroups(a2LModule, wpParamMap, respParamMap);

      A2LWriter a2lWriter = new A2LWriter(filePath, modifiedA2lPath, a2lDoc);
      a2lWriter.write();

      File tempA2lFile = new File(filePath);
      if (tempA2lFile.exists() && isA2lWithGroups) {
        // Delete the file
        if (tempA2lFile.delete()) {
          CDMLogger.getInstance().info("File deleted successfully: " + filePath);
        }
        else {
          CDMLogger.getInstance().info("Failed to delete the file: " + filePath);
        }
      }
      else {
        CDMLogger.getInstance().info("File not found/A2L without groups: " + filePath);
      }
    }
    catch (Exception exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }

  }

  /**
   * @param groupList
   */
  private void clearIcdmRespGroups(final Collection<Group> groupList) {
    List<String> icdmSubGroups =
        groupList.stream().filter(group -> CommonUtils.isNotEmptyString(group.getLongIdentifier()) &&
            group.getLongIdentifier().contains(GROUP_DESC_SUFFIX) && group.isRoot()).flatMap(rootGroup -> {
              if (CommonUtils.isNotEmpty(rootGroup.getSubGroups())) {
                return rootGroup.getSubGroups().keySet().stream();
              }
              else {
                return Stream.empty();
              }
            }).collect(Collectors.toList());

    groupList.removeIf(
        group -> icdmSubGroups.contains(group.getName()) || (CommonUtils.isNotEmptyString(group.getLongIdentifier()) &&
            (group.getLongIdentifier().contains(GROUP_DESC_SUFFIX_FROM_PAL) ||
                (group.getLongIdentifier().contains(GROUP_DESC_SUFFIX) && group.isRoot()))));
  }

  private void constructWpRespMap(final Map<String, Set<String>> wpParamMap,
      final Map<String, Set<String>> respParamMap, final Map<String, Characteristic> a2lCharMap) {


    for (Entry<String, Set<String>> wpParamEntry : wpParamMap.entrySet()) {
      String wpName = wpParamEntry.getKey();
      for (String labelName : wpParamEntry.getValue()) {
        for (Entry<String, Set<String>> respEntry : respParamMap.entrySet()) {
          String respName = respEntry.getKey();

          if (respEntry.getValue().contains(labelName)) {
            addWpResps(a2lCharMap, wpName, labelName, respName);

          }


        }

      }
    }


  }


  /**
   * @param a2lCharMap
   * @param wpName
   * @param labelName
   * @param respName
   */
  private void addWpResps(final Map<String, Characteristic> a2lCharMap, final String wpName, final String labelName,
      final String respName) {
    String wpNameModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(wpName);

    String respNameModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(respName);
    String wpRespName = respNameModified + wpNameModified;
    List<String> wpRespLabelList = this.wpRespMap.get(wpRespName);
    if (wpRespLabelList == null) {
      wpRespLabelList = new ArrayList<>();
    }
    wpRespLabelList.add(labelName);

    this.wpRespMap.put(wpRespName, wpRespLabelList);
    Characteristic characteristic = a2lCharMap.get(labelName);
    if (characteristic != null) {
      addWpRespfuncList(this.wpRespFuncMap, wpName, labelName, respName, characteristic);
    }
  }


  /**
   * @param wpRespFuncMap
   * @param wpName
   * @param labelName
   * @param respName
   * @param characteristic
   */
  private void addWpRespfuncList(final Map<String, List<String>> wpRespFuncMap, final String wpName,
      final String labelName, final String respName, final Characteristic characteristic) {
    Function defFunction = characteristic.getDefFunction();
    if (defFunction != null) {

      String wpNameModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(wpName);

      String respNameModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(respName);

      String funcModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(defFunction.getName());

      String respWpFuncName = respNameModified + wpNameModified + funcModified;

      List<String> wpRespFuncLabList = wpRespFuncMap.get(respWpFuncName);

      wpRespFuncMap.computeIfAbsent(respWpFuncName, key -> new ArrayList<>());
      if (wpRespFuncLabList == null) {
        wpRespFuncLabList = new ArrayList<>();
      }
      wpRespFuncLabList.add(labelName);
      wpRespFuncMap.put(respWpFuncName, wpRespFuncLabList);
    }
  }


  private void constructRespFuncMap(final Map<String, Set<String>> respParamMap,
      final Map<String, Characteristic> a2lCharMap) {


    for (Entry<String, Set<String>> respEntry : respParamMap.entrySet()) {

      String respName = respEntry.getKey();

      for (String respLabel : respEntry.getValue()) {

        addwpRespFunc(a2lCharMap, respName, respLabel);


      }


    }


  }


  /**
   * @param a2lCharMap
   * @param respName
   * @param respLabel
   */
  private void addwpRespFunc(final Map<String, Characteristic> a2lCharMap, final String respName,
      final String respLabel) {
    Characteristic characteristic = a2lCharMap.get(respLabel);

    if (characteristic != null) {
      Function defFunction = characteristic.getDefFunction();
      if (defFunction != null) {
        String funcName = defFunction.getName();
        String respNameModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(respName);

        String funcModified = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(funcName);

        String respFuncName = respNameModified + funcModified;

        List<String> labelList = this.respFuncMap.get(respFuncName);

        if (labelList == null) {
          labelList = new ArrayList<>();
        }
        labelList.add(characteristic.getName());
        this.respFuncMap.put(respFuncName, labelList);


      }

    }
  }


  /**
   * @param a2lModule
   * @param a2lDoc
   * @param wpParamMap
   * @param respParamMap
   * @param isA2lWithGrps
   * @throws AOMException
   * @throws AAMException
   * @throws IcdmException
   */
  private void createGroups(final Module a2lModule, final Map<String, Set<String>> wpParamMap,
      final Map<String, Set<String>> respParamMap)
      throws AOMException, AAMException, IcdmException {


    GroupImpl newRootGroupWp = new GroupImpl();
    GroupImpl newRootGroupResp = new GroupImpl();

    newRootGroupWp.setName(ApicConstants.ROOT_GROUP_WORKPACKAGES);
    newRootGroupWp.setLongIdentifier(ROOT_WP_DESC);
    newRootGroupWp.isRoot(true);

    newRootGroupResp.setName(ApicConstants.ROOT_GROUP_RESPONSIBILITIES);
    newRootGroupResp.setLongIdentifier(ROOT_RESP_DESC);
    newRootGroupResp.isRoot(true);

    addWpGroups(a2lModule, wpParamMap, newRootGroupWp);


    addRespGrps(a2lModule, respParamMap, newRootGroupResp);

    if (this.a2lExpServInp.isRespFunc()) {
      constructRespFuncMap(respParamMap, this.a2lcharMap);
      addRespFuncGrp(a2lModule);
    }

    if (this.a2lExpServInp.isWpResp()) {
      constructWpRespMap(wpParamMap, respParamMap, this.a2lcharMap);
      addWpRespGrp(a2lModule);
    }

    if (this.a2lExpServInp.isWpRespFunc()) {
      constructWpRespMap(wpParamMap, respParamMap, this.a2lcharMap);
      addwpRespFuncGrp(a2lModule);
    }

  }

  /**
   * @param a2lModule
   */
  private void addwpRespFuncGrp(final Module a2lModule) throws AAMException, AOMException {
    GroupImpl newRootGroupwpRespfunc = new GroupImpl();
    newRootGroupwpRespfunc.setName(ApicConstants.ROOT_GROUP_RESP_WP_FUNC);
    newRootGroupwpRespfunc.setLongIdentifier(ROOT_WP_RESP_FUNC_DESC);
    newRootGroupwpRespfunc.isRoot(true);
    ((ModuleModifier) a2lModule).add(newRootGroupwpRespfunc);
    CharacteristicImpl newChar;
    GroupImpl newGroup;

    for (Entry<String, List<String>> respFuncEntry : this.wpRespFuncMap.entrySet()) {

      newGroup = new GroupImpl();
      newGroup.setName(respFuncEntry.getKey());
      newGroup.setLongIdentifier(respFuncEntry.getKey() + GROUP_DESC_SUFFIX);

      List<String> labelSet = respFuncEntry.getValue();

      for (String labelName : labelSet) {

        newChar = new CharacteristicImpl();
        newChar.setName(labelName);
        newGroup.addRefCharacteristic(newChar);
      }
      newGroup.isRoot(false);
      newGroup.setRefCharacteristicModified(true);

      ((ModuleModifier) a2lModule).add(newGroup);
      newRootGroupwpRespfunc.addSubGroups(newGroup);
      // SubGroup in WP-Rootgroup hängen
      newRootGroupwpRespfunc.isSubGroupModified();

    }


  }


  /**
   * @param a2lModule
   * @throws AAMException
   * @throws AOMException
   */
  private void addWpRespGrp(final Module a2lModule) throws AAMException, AOMException {
    GroupImpl newRootGroupwpResp = new GroupImpl();
    newRootGroupwpResp.setName(ApicConstants.ROOT_GROUP_RESP_WP);
    newRootGroupwpResp.setLongIdentifier(ROOT_WP_RESP_DESC);
    newRootGroupwpResp.isRoot(true);
    ((ModuleModifier) a2lModule).add(newRootGroupwpResp);
    CharacteristicImpl newChar;
    GroupImpl newGroup;

    for (Entry<String, List<String>> respFuncEntry : this.wpRespMap.entrySet()) {

      newGroup = new GroupImpl();
      newGroup.setName(respFuncEntry.getKey());
      newGroup.setLongIdentifier(respFuncEntry.getKey() + GROUP_DESC_SUFFIX);

      List<String> labelSet = respFuncEntry.getValue();

      for (String labelName : labelSet) {

        newChar = new CharacteristicImpl();
        newChar.setName(labelName);
        newGroup.addRefCharacteristic(newChar);
      }
      newGroup.isRoot(false);
      newGroup.setRefCharacteristicModified(true);

      ((ModuleModifier) a2lModule).add(newGroup);
      newRootGroupwpResp.addSubGroups(newGroup);
      // SubGroup in WP-Rootgroup hängen
      newRootGroupwpResp.isSubGroupModified();

    }

  }


  /**
   * @param a2lModule
   * @throws AOMException
   * @throws AAMException
   */
  private void addRespFuncGrp(final Module a2lModule) throws AOMException, AAMException {
    GroupImpl newRootGroupRespFunc = new GroupImpl();
    newRootGroupRespFunc.setName(ApicConstants.ROOT_GROUP_RESP_FUNC);
    newRootGroupRespFunc.setLongIdentifier(ROOT_RESP_FUNC_DESC);
    newRootGroupRespFunc.isRoot(true);
    ((ModuleModifier) a2lModule).add(newRootGroupRespFunc);
    CharacteristicImpl newChar;
    GroupImpl newGroup;

    for (Entry<String, List<String>> respFuncEntry : this.respFuncMap.entrySet()) {

      newGroup = new GroupImpl();
      newGroup.setName(respFuncEntry.getKey());
      newGroup.setLongIdentifier(respFuncEntry.getKey() + GROUP_DESC_SUFFIX);

      List<String> labelSet = respFuncEntry.getValue();

      for (String labelName : labelSet) {

        newChar = new CharacteristicImpl();
        newChar.setName(labelName);
        newGroup.addRefCharacteristic(newChar);
      }
      newGroup.isRoot(false);
      newGroup.setRefCharacteristicModified(true);

      ((ModuleModifier) a2lModule).add(newGroup);
      newRootGroupRespFunc.addSubGroups(newGroup);
      // SubGroup in WP-Rootgroup hängen
      newRootGroupRespFunc.isSubGroupModified();

    }

  }


  /**
   * @param a2lModule
   * @param respParamMap
   * @param newRootGroupResp
   * @param isA2lWithGrps
   * @throws AOMException
   * @throws AAMException
   * @throws IcdmException
   */
  private void addRespGrps(final Module a2lModule, final Map<String, Set<String>> respParamMap,
      final GroupImpl newRootGroupResp)
      throws AOMException, AAMException, IcdmException {

    CharacteristicImpl newChar;
    GroupImpl newGroup;
    A2lWpRespWriterValidator a2lWpRespValidator = new A2lWpRespWriterValidator();
    boolean rootGrpPresent = a2lWpRespValidator.isRootGrpResporWp(a2lModule, ApicConstants.ROOT_GROUP_RESPONSIBILITIES);
    if (rootGrpPresent) {
      throw new IcdmException("Root Group _Responsibilities is already present and the A2L Export is not possible");
    }
    else {
      ((ModuleModifier) a2lModule).add(newRootGroupResp);
    }
    for (Entry<String, Set<String>> respLabEntry : respParamMap.entrySet()) {

      newGroup = new GroupImpl();
      String respName = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(respLabEntry.getKey());

      A2lResponsibility a2lResponsibility = this.a2lResponsibilityMap.get(respLabEntry.getKey());


      newGroup.setName(respName);
      if ((a2lResponsibility != null) && (a2lResponsibility.getDescription() != null)) {
        String descMod =
            A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(a2lResponsibility.getDescription());
        newGroup.setLongIdentifier(descMod + GROUP_DESC_SUFFIX);
      }
      else {
        newGroup.setLongIdentifier(GROUP_DESC_SUFFIX);
      }

      Set<String> labelSet = respLabEntry.getValue();

      for (String labelName : labelSet) {
        newChar = new CharacteristicImpl();
        newChar.setName(labelName);
        newGroup.addRefCharacteristic(newChar);

      }
      newGroup.isRoot(false);
      newGroup.setRefCharacteristicModified(true);


      ((ModuleModifier) a2lModule).add(newGroup);
      newRootGroupResp.addSubGroups(newGroup); // SubGroup in WP-Rootgroup hängen
      newRootGroupResp.isSubGroupModified();

    }
  }

  /**
   * @param a2lModule
   * @param wpParamMap
   * @param newRootGroupWp
   * @param isA2lWithGrps
   * @throws AOMException
   * @throws AAMException
   * @throws IcdmException
   */
  private void addWpGroups(final Module a2lModule, final Map<String, Set<String>> wpParamMap,
      final GroupImpl newRootGroupWp)
      throws AOMException, AAMException, IcdmException {
    CharacteristicImpl newChar;
    GroupImpl newGroup;
    A2lWpRespWriterValidator a2lWpRespValidator = new A2lWpRespWriterValidator();
    boolean rootGrpPresent = a2lWpRespValidator.isRootGrpResporWp(a2lModule, ApicConstants.ROOT_GROUP_WORKPACKAGES);
    if (rootGrpPresent) {
      throw new IcdmException("Root Group _Workpackages is already present and the A2L Export is not possible");
    }
    else {
      ((ModuleModifier) a2lModule).add(newRootGroupWp);
    }
    for (Entry<String, Set<String>> wpLabEntry : wpParamMap.entrySet()) {

      newGroup = new GroupImpl();
      String wpLabelKey = wpLabEntry.getKey();
      String key = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(wpLabelKey);
      newGroup.setName(key);
      A2lWorkPackage a2lWorkPackage = this.a2lWorkPackageMap.get(wpLabelKey.toUpperCase(Locale.getDefault()));
      if ((a2lWorkPackage != null) && (a2lWorkPackage.getDescription() != null)) {
        String descMod = A2lObjectIdentifierValidator.clearGroupNamesFromSpecialChars(a2lWorkPackage.getDescription());
        newGroup.setLongIdentifier(descMod + GROUP_DESC_SUFFIX);
      }
      else {
        newGroup.setLongIdentifier(GROUP_DESC_SUFFIX);
      }

      Set<String> labelSet = wpLabEntry.getValue();

      for (String labelName : labelSet) {

        newChar = new CharacteristicImpl();
        newChar.setName(labelName);
        newGroup.addRefCharacteristic(newChar);
      }
      newGroup.isRoot(false);
      newGroup.setRefCharacteristicModified(true);

      ((ModuleModifier) a2lModule).add(newGroup);
      newRootGroupWp.addSubGroups(newGroup);
      // SubGroup in WP-Rootgroup hängen
      newRootGroupWp.isSubGroupModified();

    }
  }


}
