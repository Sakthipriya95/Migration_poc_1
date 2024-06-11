/**
 *
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.system.constant.SystemConstant;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.a2l.WpResp;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LParamPropsServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgBcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;


/**
 * A2LEditorDataProvider - This class provides data for the iCDM A2l editor. Later this class can be extended to provide
 * additional information
 */
public class A2LFileInfoBO {

  /** The a 2 l file info. */
  private A2LFileInfo a2lFileInfo;

  /** The a 2 l wp mapping. */
  private A2lWpMapping a2lWpMapping;

  /** The param map. */
  // Icdm-586
  private final Map<String, A2LParameter> a2lParamMap = new HashMap<>();

  /** sorted set of parameters that are not assigned to a FUNC. */
  private SortedSet<A2LParameter> unAssignedParams;

  /** key - base parameter , value - map of func name(String),A2L parameter. */
  private ConcurrentMap<String, ConcurrentMap<String, Characteristic>> baseAndVarCodedParamsMap;

  /** The a 2 l file. */
  private final A2LFile a2lFile;
  /**
   * All BCs corresponding to an A2l File ICdm-949 Change from Sorted Set to Map
   */
  private Map<String, A2LBaseComponents> a2lBcMap = new HashMap<>();

  /**
   * Parameter properties, retrieved by the web service call
   */
  private Map<String, ParamProperties> paramProps = new HashMap<>();

  /**
   * Map of all functions of a2l. Key - function name. Value - function
   */
  private final Map<String, Function> allFunctionMap = new HashMap<>();
  /**
   * Map of all functions of a2l. Key - function name in upper case. Value - function
   */
  private final Map<String, Function> allFuntionUcaseMap = new HashMap<>();
  /**
   * Set Of all component package loaded from the system
   */
  private SortedSet<RuleSet> ruleset;

  private final Map<Long, Set<String>> ruleSetParamNameMap = new HashMap<>();

  private final Map<Long, CompPkgData> compPkgDataMap = new HashMap<>();

  private final Set<Long> compPkgWithLinkSet = new HashSet<>();

  private boolean isDataRvwRprtFrmA2LRespWP = false;

  /**
   * enum to declare the sort columns.
   */
  public enum SortGroupColumns {

                                /** Group Name column. */
                                SORT_GROUP_NAME,

                                /** Work Package Name. */
                                SORT_GROUP_LONG,

                                /** Work Package Number. */
                                SORT_NUM_REF,

                                /** Work Package Number. */
                                SORT_RESP

  }

  /**
   * enum to declare the sort columns.
   */
  public enum SortWPColumns {

                             /** Group Name column. */
                             SORT_WP_GROUP_NAME,

                             /** Work Package Name. */
                             SORT_WP_NAME,

                             /** Work Package Number. */
                             SORT_WP_NUMBER,

                             /** Resp Sort. */
                             SORT_RESP
  }

  /**
   * Instantiates a new a 2 L editor model BO.
   *
   * @param a2lFile the a 2 l file
   * @param a2lFileInfo the a 2 l file info
   */
  public A2LFileInfoBO(final A2LFile a2lFile, final A2LFileInfo a2lFileInfo) {
    this.a2lFile = a2lFile;
    if (a2lFileInfo == null) {
      fetchA2LFileInfo();
    }
    if (null != this.a2lFileInfo) {
      initialiseParamMaps();
    }
  }

  /**
   * @param ruleSetId ruleSetId
   * @return set of rule set parameters
   */
  public Set<String> getRuleSetParamNameSet(final Long ruleSetId) {
    Set<String> paramNameSet =
        this.ruleSetParamNameMap.computeIfAbsent(ruleSetId, k -> getAllParamNamesForRuleSet(ruleSetId));

    Set<String> retSet = new TreeSet<>();
    if (paramNameSet != null) {
      retSet.addAll(paramNameSet);
    }
    return retSet;
  }

  /**
   * @param compPkgId comp Pkg Id
   * @return set of rule set parameters
   */
  public Set<CompPkgBc> getCompPkgBCSet(final Long compPkgId) {
    CompPkgData cpData = this.compPkgDataMap.computeIfAbsent(compPkgId, k -> getCompPkgData(compPkgId));

    Set<CompPkgBc> retSet = new TreeSet<>();
    if (cpData != null) {
      retSet.addAll(cpData.getBcSet());
    }
    return retSet;
  }

  /**
   * @param compPkgId comp Pkg Id
   * @return set of rule set parameters
   */
  public Set<Function> getCompPkgFunctions(final Long compPkgId) {
    CompPkgData cpData = this.compPkgDataMap.computeIfAbsent(compPkgId, k -> getCompPkgData(compPkgId));

    Set<Function> retSet = new HashSet<>();

    if (cpData != null) {
      for (CompPkgBc cpBc : cpData.getBcSet()) {
        Set<CompPkgFc> cpFcSet = cpData.getFcMap().get(cpBc.getId());
        if (CommonUtils.isNullOrEmpty(cpFcSet)) {
          // All functions are mapped to the comp packcage ( mapping type = <ALL>)
          A2LBaseComponents a2lBc = this.a2lBcMap.get(cpBc.getBcName());
          if (a2lBc != null) {
            a2lBc.getFunctionMap().keySet().stream().map(A2LFileInfoBO.this::getFunctionByName).filter(Objects::nonNull)
                .forEach(retSet::add);
          }
        }
        else {
          cpFcSet.stream().map(CompPkgFc::getName).map(A2LFileInfoBO.this::getFunctionByName).filter(Objects::nonNull)
              .forEach(retSet::add);
        }
      }
    }
    return retSet;
  }

  /**
   * Load A 2 L file info.
   */
  public void fetchA2LFileInfo() {
    if (this.a2lFileInfo == null) {
      try {
        this.a2lFileInfo = new A2LFileInfoProviderClient().fetchA2LFileInfo(this.a2lFile);
      }
      catch (IcdmException e) {
        CDMLogger.getInstance().errorDialog("Error retrieving A2LFileInfo \n" + e, e, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * ICDM-2270 initialise the base parameters and unassigned parameters collection.
   */
  private void initialiseParamMaps() {

    this.unAssignedParams = new TreeSet<>();
    this.baseAndVarCodedParamsMap = new ConcurrentHashMap<>();

    // iterate and fill the maps
    for (Characteristic characteristic : this.a2lFileInfo.getAllSortedLabels(true)) {
      Function defFunction = characteristic.getDefFunction();
      if (defFunction == null) {
        // Icdm-586
        A2LParameter param = new A2LParameter(null, characteristic, null, false, null, false, false);
        this.unAssignedParams.add(param);
      }
      String charName = characteristic.getName();
      // check if it is a variant coded parameter
      if (isVariantCoded(charName)) {
        addVarCodedParamsToMap(characteristic, defFunction);
      }
    }
  }


  /**
   * Adds the var coded params to map.
   *
   * @param characteristic the characteristic
   * @param defFunction the def function
   */
  private void addVarCodedParamsToMap(final Characteristic characteristic, final Function defFunction) {
    String baseParamName = getBaseParamName(characteristic.getName());
    if (this.baseAndVarCodedParamsMap.get(baseParamName) == null) {
      // if the base parameter is not present in the map
      ConcurrentMap<String, Characteristic> varCodedParamMap = new ConcurrentHashMap<>();
      varCodedParamMap.put(defFunction == null ? ApicConstants.UNASSIGNED_PARAM : defFunction.getName(),
          characteristic);
      this.baseAndVarCodedParamsMap.put(baseParamName, varCodedParamMap);
    }
    else {
      // if the base parameter is present in the map
      this.baseAndVarCodedParamsMap.get(baseParamName)
          .put(defFunction == null ? ApicConstants.UNASSIGNED_PARAM : defFunction.getName(), characteristic);
    }
  }

  /**
   * If the Variant coded param name is given then the last characters with [int] is removed and the base parameter name
   * is returned.
   *
   * @param varcodedParamName paramName
   * @return the Base parameter name from the Varaint coded param name.
   */
  public static String getBaseParamName(final String varcodedParamName) {
    return CommonUtils.concatenate(getBaseParamFirstName(varcodedParamName), getBaseParamLastName(varcodedParamName));
  }

  /**
   * Occurs in situation where parameter name is PT_drRmpTransP[0].Neg_C
   *
   * @param varcodedParamName varcodedParamName
   * @return the base param first name for example PT_drRmpTransP[0].Neg_C the first name is PT_drRmpTransP.
   */
  public static String getBaseParamFirstName(final String varcodedParamName) {
    return varcodedParamName.substring(0, varcodedParamName.lastIndexOf('['));
  }

  /**
   * Occurs in situation where parameter name is PT_drRmpTransP[0].Neg_C
   *
   * @param varcodedParamName varcodedParamName
   * @return the base parameter last name for example PT_drRmpTransP[0].Neg_C the first name is .Neg_C.
   */
  public static String getBaseParamLastName(final String varcodedParamName) {
    String lastName = "";
    if ((varcodedParamName.length() - 1) > varcodedParamName.lastIndexOf(']')) {
      lastName = varcodedParamName.substring(varcodedParamName.lastIndexOf(']') + 1, varcodedParamName.length());
    }
    return lastName;
  }


  /**
   * Gets the var coded map.
   *
   * @return the var coded map
   */
  public ConcurrentMap<String, ConcurrentMap<String, Characteristic>> getVarCodedMap() {
    return this.baseAndVarCodedParamsMap;

  }

  /**
   * Gets the param map.
   *
   * @param paramPropMap the param prop map - Can be null incase properties not available
   * @return the paramSet //Icdm-586
   */
  public Map<String, A2LParameter> getA2lParamMap(final Map<String, ParamProperties> paramPropMap) {
    if (CommonUtils.isNotEmpty(this.a2lParamMap)) {
      return this.a2lParamMap;
    }
    if (CommonUtils.isNotEmpty(paramPropMap)) {
      createA2LParams(getCharacteristics(), paramPropMap);
    }
    else {
      createA2LParams(getCharacteristics(), getA2LParamProperties());
    }
    return this.a2lParamMap;
  }

  /**
   * Gets the param map.
   *
   * @param cdrReport CDRReport model
   * @return param Map
   */
  public Map<String, A2LParameter> getA2lParamMapForWPResp(final CdrReport cdrReport) {
    A2LParameter parameter;
    ParamProperties props;
    Map<String, ParamProperties> paramPropMap = cdrReport.getParamPropsMap();
    if (CommonUtils.isNotEmpty(this.a2lParamMap)) {
      return this.a2lParamMap;
    }
    if (CommonUtils.isNotEmpty(paramPropMap)) {
      for (Characteristic character : this.a2lFileInfo.getAllModulesLabels().values()) {
        props = paramPropMap.get(character.getName());
        if (CommonUtils.isNotNull(props)) {
          parameter = new A2LParameter(props.getId(), character, props.getPClass(), props.isCodeWord(),
              props.getSsdClass(), props.isBlackList(), props.isQssdParameter());
          this.a2lParamMap.put(character.getName(), parameter);
        }
      }
    }
    return this.a2lParamMap;
  }

  /**
   * Icdm-586.
   *
   * @param characteristics characteristics
   * @param paramPropMap paramProps
   */
  private void createA2LParams(final Collection<Characteristic> characteristics,
      final Map<String, ParamProperties> paramPropMap) {

    A2LParameter parameter;
    ParamProperties props;
    for (Characteristic character : characteristics) {
      props = paramPropMap.get(character.getName());
      if (CommonUtils.isNotNull(props)) {
        parameter = new A2LParameter(props.getId(), character, props.getPClass(), props.isCodeWord(),
            props.getSsdClass(), props.isBlackList(), props.isQssdParameter());
      }
      else {
        parameter = new A2LParameter(null, character, null, false, null, false, false);
      }
      this.a2lParamMap.put(character.getName(), parameter);
    }
  }


  /**
   * new Common method to check if the parameter is Variant coded.
   *
   * @param paramName paramName
   * @return true if the parameter name is Varaint coded
   */
  public static boolean isVariantCoded(final String paramName) {
    int indexOf = paramName.lastIndexOf('[');
    if (indexOf != -1) {
      char numChar = paramName.charAt(indexOf + 1);
      return (numChar >= '0') && (numChar <= '9');
    }
    return false;
  }

  // ICDM-209 and ICDM-210
  /**
   * Gets the groups list.
   *
   * @return the groupsList
   */
  public SortedSet<Group> getGroupsList() {
    return this.a2lFileInfo.getAllSortedGroups();
  }

  /**
   * Gets the Characteristics of a2l.
   *
   * @return the characteristics
   */
  public SortedSet<Characteristic> getCharacteristics() {
    return this.a2lFileInfo.getAllSortedLabels(true);
  }

  // iCDM-575
  /**
   * Gets the characteristics map.
   *
   * @return the charsMap
   */
  public Map<String, Characteristic> getCharacteristicsMap() {
    return this.a2lFileInfo.getAllModulesLabels();
  }


  // ICdm-469
  /**
   * Gets the Special Functions of A2l.
   *
   * @return the systemConstList
   */
  public SortedSet<A2LParameter> getUnassignedParams() {
    return this.unAssignedParams;
  }

  /**
   * Gets the System constants of a2l.
   *
   * @return the systemConstList
   */
  // ICDM-2627
  public Set<SystemConstant> getSystemConstants() {
    return new HashSet<>(this.a2lFileInfo.getAllModulesSystemCons().values());
  }

  /**
   * Gets the System constants of a2l, sorted.
   *
   * @return the systemConstList
   */
  public SortedSet<SystemConstant> getSystemConstantsSorted() {
    return this.a2lFileInfo.getAllSystemCons();
  }

  /**
   * @return the allFunctionMap
   */
  public Map<String, Function> getAllFunctionMap() {
    if (this.allFunctionMap.isEmpty()) {
      this.allFunctionMap.putAll(this.a2lFileInfo.getAllModulesFunctions());
    }
    return this.allFunctionMap;
  }

  /**
   * @return the allFuntionUcaseMap
   */
  private Map<String, Function> getAllFuntionUcaseMap() {
    if (this.allFuntionUcaseMap.isEmpty()) {
      getAllFunctionMap()
          .forEach((funName, fun) -> this.allFuntionUcaseMap.put(funName.toUpperCase(Locale.getDefault()), fun));
    }
    return this.allFuntionUcaseMap;
  }

  /**
   * Gets list of all functions of the A2L.
   *
   * @return List<Function> function list
   */
  public SortedSet<Function> getAllSortedFunctions() {
    return new TreeSet<>(getAllFunctionMap().values());
  }

  /**
   * Get the functions with the given names (case insensitive search)
   *
   * @param funNameList names
   * @return List of functions
   */
  public List<Function> getFunctionByName(final List<String> funNameList) {
    return funNameList.stream().map(String::toUpperCase).map(getAllFuntionUcaseMap()::get).collect(Collectors.toList());
  }

  /**
   * Get the function with the given name(case insensitive search)
   *
   * @param funName name
   * @return function
   */
  public Function getFunctionByName(final String funName) {
    return getAllFuntionUcaseMap().get(funName.toUpperCase(Locale.getDefault()));
  }


  /**
   * Gets the mapping source ID.
   *
   * @return the mappingSourceID
   */
  public Long getMappingSourceID() {
    return this.a2lWpMapping == null ? null : this.a2lWpMapping.getMappingSourceId();
  }

  /**
   * @return true if A2L's work package resolution is via A2L groups; false if resolution is via FC2WP or if not defined
   */
  public boolean isGroupMappingType() {
    try {
      return CommonUtils.isEqual(getMappingSourceID(),
          Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.GROUP_MAPPING_ID)));
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * Gets the functions of label type.
   *
   * @param labelType the label type
   * @return the functions of label type
   */
  public SortedSet<Function> getFunctionsOfLabelType(final LabelType labelType) {
    final SortedSet<Function> funcListOfLabelType = new TreeSet<>();
    // iterate over all functions
    for (Function function : this.a2lFileInfo.getAllSortedFunctions()) {
      if (function != null) {
        final LabelList defLabel = function.getLabelList(labelType);
        if (CommonUtils.isNotEmpty(defLabel) || (labelType == null)) {
          funcListOfLabelType.add(function);
        }
      }
    }
    return funcListOfLabelType;
  }

  /**
   * Gets the property parameters.
   *
   * @return the property parameters
   */
  private Map<String, ParamProperties> getA2LParamProperties() {
    return this.paramProps;
  }

  /**
   * Fetch A 2 l param properties.
   *
   * @return the map
   */
  public Map<String, ParamProperties> fetchA2lParamProperties() {
    // Fetch a2l parameter properties using iCDM web service
    CDMLogger.getInstance()
        .info("Fetching a2l parameter properties using iCDM web service. A2L File ID = " + this.a2lFile.getId());

    try {
      A2LParamPropsServiceClient servClient = new A2LParamPropsServiceClient();
      // Fetch the data using iCDM web service
      this.paramProps = servClient.getA2LParamProps(this.a2lFile.getId());
      CDMLogger.getInstance()
          .info("A2L parameter properties retrieved. Number of parameters = " + this.paramProps.size());
    }
    catch (ApicWebServiceException exp) {
      // If web service is failed, return cancel status
      CDMLogger.getInstance().errorDialog("Could not retrieve the A2L parameter properties. " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
    return this.paramProps;
  }

  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


  /**
   * @param a2lFileInfo the a2lFileInfo to set
   */
  public void setA2lFileInfo(final A2LFileInfo a2lFileInfo) {
    this.a2lFileInfo = a2lFileInfo;
    if (a2lFileInfo != null) {
      initialiseParamMaps();
    }
  }


  /**
   * @return the a2lBcMap
   */
  public Map<String, A2LBaseComponents> getA2lBcMap() {
    return this.a2lBcMap;
  }


  /**
   * Icdm-383.
   *
   * @return // ICDM-204
   */
  public SortedSet<A2LBaseComponents> getA2lBCInfo() {
    return new TreeSet<>(getA2lBcMap().values());
  }

  /**
   * @param a2lBcMap the a2lBcMap to set
   */
  public void setA2lBcMap(final Map<String, A2LBaseComponents> a2lBcMap) {
    this.a2lBcMap = a2lBcMap;
  }

  /**
   * Gets the system constant details.
   *
   * @param sysConstSet the sys const set
   * @return the system constant details
   */
  public SortedSet<A2LSystemConstantValues> getSystemConstantDetails(final Set<SystemConstant> sysConstSet) {
    return new A2LDataBO().getSystemConstantDetails(sysConstSet);
  }


  /**
   * @return the wpRootGroupName
   */
  public String getWpRootGroupName() {
    return this.a2lWpMapping == null ? null : this.a2lWpMapping.getWpRootGroupName();
  }

  /**
   * Gets the param id by function.
   *
   * @param funName the fun name
   * @return parameters in the selected items
   */
  public Set<Long> getParamIdByFunction(final String funName) {
    Set<Long> paramIdSet = new HashSet<>();
    Function function = getFunctionByName(funName);
    if (function != null) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      // Get parameter names
      if (defCharRefList != null) {
        defCharRefList.stream().map(DefCharacteristic::getName)
            .forEach(param -> paramIdSet.add((this.a2lParamMap.get(param)).getParamId()));
      }
    }
    return paramIdSet;
  }


  /**
   * @param funName function name
   * @return set of params for the input function
   */
  public Set<String> getParamListfromFunction(final String funName) {
    return getParamListfromFunction(getFunctionByName(funName));
  }

  /**
   * @param function
   * @return
   */
  private Set<String> getParamListfromFunction(final Function function) {
    Set<String> paramSet = new HashSet<>();
    if (function != null) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      // Get parameter names
      if (defCharRefList != null) {
        defCharRefList.stream().map(DefCharacteristic::getName).forEach(paramSet::add);
      }
    }
    return paramSet;
  }

  /**
   * @param grpList - list of group names
   * @return Set of parameter names for given input
   */
  public Set<String> getParamListFromGroups(final List<String> grpList) {
    Set<String> paramSet = new HashSet<>();
    Map<String, Group> grps = this.a2lFileInfo.getAllModulesGroups();
    if (CommonUtils.isNotEmpty(grpList) && !CommonUtils.isNullOrEmpty(grps)) {
      for (String grp : grpList) {
        if (grps.get(grp) != null) {
          LabelList labelList = grps.get(grp).getFirstLabelList();
          if (CommonUtils.isNotEmpty(labelList)) {
            labelList.stream().forEach(paramSet::add);
          }
        }
      }
    }
    return paramSet;
  }

  /**
   * @return the A2LGroup groupList
   */
  public List<A2LGroup> getA2LGroupList() {
    return this.a2lWpMapping == null ? null : this.a2lWpMapping.getA2lGroupList();
  }


  // icdm-276
  /**
   * @return the workPackageList
   */
  public List<A2lWpObj> getWorkPackageList() {
    return this.a2lWpMapping == null ? null : this.a2lWpMapping.getWorkPackageList();
  }


  /**
   * @return the a2lWpMapping
   */
  public A2lWpMapping getA2lWpMapping() {
    return this.a2lWpMapping;
  }


  /**
   * @param a2lWpMapping the a2lWpMapping to set
   */
  public void setA2lWpMapping(final A2lWpMapping a2lWpMapping) {
    this.a2lWpMapping = a2lWpMapping;
  }


  /**
   * @return the paramProps
   */
  public Map<String, ParamProperties> getParamProps() {
    return this.paramProps;
  }


  /**
   * @param paramProps the paramProps to set
   */
  public void setParamProps(final Map<String, ParamProperties> paramProps) {
    this.paramProps = paramProps;
  }

  /**
   * @return the divAttrValueId
   */
  public Long getDivAttrValueId() {
    return this.a2lWpMapping == null ? 0l : this.a2lWpMapping.getDivAttrValueId();
  }

  /**
   * @return the wpTypeAttrValueId
   */
  public Long getWpTypeAttrValueId() {
    return this.a2lWpMapping == null ? 0l : this.a2lWpMapping.getWpTypeAttrValueId();
  }

  /**
   * Gets the wp root grp attr value id.
   *
   * @return the wp root grp attr value id
   */
  public Long getWpRootGrpAttrValueId() {
    return this.a2lWpMapping == null ? 0l : this.a2lWpMapping.getWpRootGrpAttrValueId();
  }

  /**
   * @return the groupMappingId
   */
  public Long getGroupMappingId() {
    return this.a2lWpMapping == null ? 0l : this.a2lWpMapping.getGroupMappingId();
  }

  /**
   * Gets the wp resp enum.
   *
   * @param wpResp the wp resp
   * @return the wp resp enum
   */
  public WpRespType getWpRespEnum(final WpResp wpResp) {
    return wpResp == null ? WpRespType.RB : WpRespType.getType(wpResp.getRespName());
  }

  /**
   * @param vCDMA2LFileId Long
   * @return List<VCDMApplicationProject>
   */
  public List<VCDMApplicationProject> getvCDMDatasets(final Long vCDMA2LFileId) {
    A2LFileInfoServiceClient client = new A2LFileInfoServiceClient();
    List<VCDMApplicationProject> dataSetModels;
    try {
      dataSetModels = client.getVcdmDataSets(vCDMA2LFileId);
      return dataSetModels;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return Collections.emptyList();
    }
  }


  /**
   * @return the wsRespMap
   */
  public Map<Long, WpResp> getWpRespMap() {
    return this.a2lWpMapping == null ? null : this.a2lWpMapping.getWpRespMap();
  }


  /**
   * Load all rule set.
   */
  public void loadAllRuleSet() {
    try {
      this.ruleset = new RuleSetServiceClient().getAllRuleSets();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param includeDeleted boolean
   * @return SortedSet<RuleSet>
   */
  public SortedSet<RuleSet> getAllUndelRuleSets(final boolean includeDeleted) {
    if (CommonUtils.isNullOrEmpty(this.ruleset)) {
      loadAllRuleSet();
    }
    SortedSet<RuleSet> ruleSetSortSet = new TreeSet<>(this.ruleset);
    // Remove the deleted rulesets, if they are not required
    if (!includeDeleted) {
      for (RuleSet ruleSet : this.ruleset) {
        if (ruleSet.isDeleted()) {
          ruleSetSortSet.remove(ruleSet);
        }
      }
    }
    return ruleSetSortSet;
  }

  /**
   * @param ruleSetId selected rule set id from a2l outline view
   * @return Set of parmeter names
   */
  private Set<String> getAllParamNamesForRuleSet(final Long ruleSetId) {
    Set<String> paramNames = new HashSet<>();
    try {
      paramNames = new RuleSetParameterServiceClient().getAllParamNames(ruleSetId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return paramNames;
  }

  /**
   * @param compPkgId selected component package id in the a2l outline view
   * @return CompPkgData
   */
  public CompPkgData getCompPkgData(final Long compPkgId) {
    CompPkgData cmpPkgData = new CompPkgData();
    try {
      cmpPkgData = new CompPkgBcServiceClient().getCompBcFcByCompId(compPkgId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return cmpPkgData;
  }

  /**
   * @return set of component package IDs with links
   */
  public Set<Long> getCpNodeWithLinks() {
    if (this.compPkgWithLinkSet.isEmpty()) {
      loadCpWithLinks();
    }
    return this.compPkgWithLinkSet;
  }

  private void loadCpWithLinks() {
    try {
      this.compPkgWithLinkSet.clear();
      this.compPkgWithLinkSet.addAll(new LinkServiceClient().getNodesWithLink(MODEL_TYPE.COMP_PKG));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Gets the a 2 l param by name.
   *
   * @param paramName the param name
   * @return the a 2 l param by name
   */
  public A2LParameter getA2lParamByName(final String paramName) {
    return this.a2lParamMap.get(paramName);
  }


  /**
   * @return the a2lFile
   */
  public String getA2lFileName() {
    return this.a2lFile.getFilename();
  }

  /**
   * @return the a2lFile
   */
  public Long getA2lFileId() {
    return this.a2lFile.getId();
  }


  /**
   * @return the isDataRvwRprtFrmA2LRespWP
   */
  public boolean isDataRvwRprtFrmA2LRespWP() {
    return this.isDataRvwRprtFrmA2LRespWP;
  }


  /**
   * @param isDataRvwRprtFrmA2LRespWP the isDataRvwRprtFrmA2LRespWP to set
   */
  public void setDataRvwRprtFrmA2LRespWP(final boolean isDataRvwRprtFrmA2LRespWP) {
    this.isDataRvwRprtFrmA2LRespWP = isDataRvwRprtFrmA2LRespWP;
  }

  /**
   * @return the a2lParamMap
   */
  public Map<String, A2LParameter> getA2lParamMap() {
    return this.a2lParamMap;
  }

}