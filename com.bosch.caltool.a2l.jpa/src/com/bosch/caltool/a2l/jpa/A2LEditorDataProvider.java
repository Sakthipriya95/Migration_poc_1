/**
 *
 */
package com.bosch.caltool.a2l.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.system.constant.SystemConstant;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.caltool.a2l.jpa.bo.A2LGroup;
import com.bosch.caltool.a2l.jpa.bo.A2LParameter;
import com.bosch.caltool.a2l.jpa.bo.A2LResponsibility;
import com.bosch.caltool.a2l.jpa.bo.A2LWpResponsibility;
import com.bosch.caltool.a2l.jpa.bo.FCToWP;
import com.bosch.caltool.a2l.jpa.bo.WorkPackage;
import com.bosch.caltool.a2l.jpa.bo.WorkPackageGroup;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;


/**
 * A2LEditorDataProvider - This class provides data for the iCDM A2l editor. Later this class can be extended to provide
 * additional information
 */
@Deprecated
public class A2LEditorDataProvider {

  /**
   * A2LResponsibility instance
   */
  private A2LResponsibility a2lResp;

  /**
   * A2L file info
   */
  private final A2LFileInfo a2lFileInfo;


  /**
   * S
   *
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


  /**
   * Mapping source
   */
  private Long mappingSourceID;

  /**
   * Root group
   */
  private Group rootGroup;

  /**
   * Root group name
   */
  private String wpRootGroupName;

  /**
   * A2Lgroup (a2l.jpa BO) list
   */
  private final List<A2LGroup> a2lGroupList = new ArrayList<A2LGroup>();

  // icdm-276
  private final Map<String, WorkPackageGroup> workPackageGroupMap = new TreeMap<String, WorkPackageGroup>();

  // iCDM-602, Supporting changes
  private final Map<Long, WorkPackage> wpMap = new TreeMap<Long, WorkPackage>();

  private final Map<String, A2LGroup> a2lGrpMap = new HashMap<String, A2LGroup>();

  // icdm-276
  private final List<WorkPackage> workPackageList = new ArrayList<WorkPackage>();

  // Icdm-586
  private final Map<String, A2LParameter> paramMap = new HashMap<String, A2LParameter>();

  /**
   * sorted set of parameters that are not assigned to a FUNC
   */
  private SortedSet<A2LParameter> unAssignedParams;

  /**
   * key - base parameter , value - map of func name(String),A2L parameter
   */
  private ConcurrentMap<String, ConcurrentMap<String, Characteristic>> baseAndVarCodedParamsMap;

  /**
   * a2l Resp Wp map
   */
  private SortedSet<A2LWpResponsibility> a2lWpRespSet = new TreeSet<>();

  private final Map<String, CDRConstants.WPResponsibilityEnum> wpGrpRespMap = new ConcurrentHashMap<>();


  /**
   * @return the a2lWpResp
   */
  public SortedSet<A2LWpResponsibility> getA2lWpRespSet() {
    return this.a2lWpRespSet;
  }


  /**
   * @return the paramSet //Icdm-586
   */
  public Map<String, A2LParameter> getParamMap() {
    return this.paramMap;
  }


  // iCDM-581 supporting changes
  /**
   * A2LEditorDataProvider Constructor
   *
   * @param a2lFileInfo a2lModel
   */
  public A2LEditorDataProvider(final A2LFileInfo a2lFileInfo) {
    this.a2lFileInfo = a2lFileInfo;
    initialiseParamMaps();
  }


  /**
   * ICDM-2270 initialise the base parameters and unassigned parameters collection
   */
  private void initialiseParamMaps() {

    this.unAssignedParams = new TreeSet<A2LParameter>();
    this.baseAndVarCodedParamsMap = new ConcurrentHashMap<String, ConcurrentMap<String, Characteristic>>();

    // iterate and fill the maps
    for (Characteristic characteristic : this.a2lFileInfo.getAllSortedLabels(true)) {
      Function defFunction = characteristic.getDefFunction();
      if (defFunction == null) {
        // Icdm-586
        A2LParameter param = new A2LParameter(characteristic, null, false, null);
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
   * @param characteristic
   * @param defFunction
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

  // icdm-276
  /**
   * @return the workPackageGroupMap
   */
  public Map<String, WorkPackageGroup> getWorkPackageGroupMap() {
    return this.workPackageGroupMap;
  }

  /**
   * Get the workpackage map
   *
   * @return the wpMap
   */
  public Map<Long, WorkPackage> getWpMap() {
    return this.wpMap;
  }


  // icdm-276
  /**
   * @return the workPackageList
   */
  public List<WorkPackage> getWorkPackageList() {
    return this.workPackageList;
  }


  /**
   * @return the wpRootGroupName
   */
  public String getWpRootGroupName() {
    return this.wpRootGroupName;
  }


  /**
   * @param mappingSource the mappingSource to set
   */
  public void setMappingSourceID(final Long mappingSource) {
    this.mappingSourceID = mappingSource;
  }


  /**
   * @return the mappingSource
   */
  public Long getMappingSourceID() {
    return this.mappingSourceID;
  }

  /**
   * @return the A2LGroup groupList
   */
  public List<A2LGroup> getA2LGroupList() {
    return this.a2lGroupList;
  }


  /**
   * @param wpRootGroupName the wpRootGroupName to set
   */
  public void setWpRootGroupName(final String wpRootGroupName) {
    this.wpRootGroupName = wpRootGroupName;
  }

  // ICDM-209 and ICDM-210
  /**
   * @return the groupsList
   */
  public SortedSet<Group> getGroupsList() {
    return this.a2lFileInfo.getAllSortedGroups();
  }

  /**
   * Gets the Characteristics of a2l
   *
   * @return the characteristics
   */
  public SortedSet<Characteristic> getCharacteristics() {
    return this.a2lFileInfo.getAllSortedLabels(true);
  }

  // iCDM-575
  /**
   * @return the charsMap
   */
  public Map<String, Characteristic> getCharacteristicsMap() {

    return this.a2lFileInfo.getAllModulesLabels();
  }


  // ICdm-469
  /**
   * Gets the Special Functions of A2l
   *
   * @return the systemConstList
   */
  public SortedSet<A2LParameter> getUnassignedParams() {
    return this.unAssignedParams;
  }

  /**
   * Gets the System constants of a2l
   *
   * @return the systemConstList
   */
  // ICDM-2627
  public Set<SystemConstant> getSystemConstants() {
    return new HashSet<>(this.a2lFileInfo.getAllModulesSystemCons().values());
  }

  /**
   * Gets the System constants of a2l, sorted
   *
   * @return the systemConstList
   */
  public SortedSet<SystemConstant> getSystemConstantsSorted() {
    return this.a2lFileInfo.getAllSystemCons();
  }

  /**
   * Gets list of all functions of the A2L
   *
   * @return List<Function> function list
   */
  public SortedSet<Function> getAllFunctions() {
    return this.a2lFileInfo.getAllSortedFunctions();
  }

  /**
   * Returns map of all A2LGroup objects created for this a2l file
   *
   * @return the a2lGrpMap
   */
  public Map<String, A2LGroup> getA2lGroupMap() {
    return this.a2lGrpMap;
  }


  /**
   * Get function of specific label type ( eg: DEF_CHARACTERISTIC) If the labelType parameter is NULL, get all
   * functions.
   *
   * @param labelType type of label
   * @return List<Function>
   */
  public SortedSet<Function> getFunctionsOfLabelType(final LabelType labelType) {
    final SortedSet<Function> funcListOfLabelType = new TreeSet<Function>();
    // iterate over all functions
    for (Function function : this.a2lFileInfo.getAllSortedFunctions()) {
      if (function != null) {
        final LabelList defLabel = function.getLabelList(labelType);
        if (((defLabel != null) && (defLabel.size() > 0)) || (labelType == null)) {
          funcListOfLabelType.add(function);
        }
      }
    }
    return funcListOfLabelType;
  }


  /**
   * icdm-272
   *
   * @param a2lParserGroupSet groupset
   * @param pidcVer pid card version
   * @param apicDataProvider dataprovider
   */
  public void fetchFromGroupMapping(final SortedSet<Group> a2lParserGroupSet, final PIDCVersion pidcVer,
      final ApicDataProvider apicDataProvider) {
    this.a2lGroupList.clear();
    // icdm-272
    final List<Group> groupsWithoutRefChar = new ArrayList<Group>();
    final List<Group> groupList = setGroupInformation(a2lParserGroupSet, pidcVer, apicDataProvider);
    for (Group group : groupList) {
      if ((this.wpRootGroupName != null) && group.getParentList().contains(this.rootGroup)) {
        handleParamsInsideGroup(group);
      }
      else {
        // used to take care about parameter which are not assigned to a GROUP
        if (!(this.wpRootGroupName != null) && !group.getParentList().contains(this.rootGroup)) {
          groupsWithoutRefChar.add(group);
        }
      }
    }
    setGroupsWithoutRefCharData(groupsWithoutRefChar);
    Collections.sort(this.a2lGroupList, new A2lGroupComparator());
  }


  /**
   * @param group
   */
  private void handleParamsInsideGroup(final Group group) {
    final A2LGroup a2lGroup = new A2LGroup(group.getName(), group.getLongIdentifier());

    // get REF_CHARACTERISTICs
    final LabelList labelList = group.getLabelList(LabelType.REF_CHARACTERISTIC);
    addToLabelMap(a2lGroup, labelList);

    // get parameter from FUNCTIONs
    final List<Function> functionsList = group.getFunctions();

    if ((functionsList != null) && (functionsList.size() > 0)) {

      for (Function function : functionsList) {
        addToLabelMap(a2lGroup, function);
      }

    }
    a2lGroup.setRootName(this.wpRootGroupName);
    this.a2lGrpMap.put(group.getName(), a2lGroup);
    this.a2lGroupList.add(a2lGroup);
  }


  /**
   * @param a2lGroup
   * @param function
   */
  private void addToLabelMap(final A2LGroup a2lGroup, final Function function) {
    if (function.getLabelList(LabelType.DEF_CHARACTERISTIC) != null) {
      for (String funcLabel : function.getLabelList(LabelType.DEF_CHARACTERISTIC)) {
        a2lGroup.getLabelMap().put(funcLabel, funcLabel);
      }
    }
  }

  /*
   * get tha un assigned param map
   */
  public Map<String, Set<String>> getGroupUnassignedMap() {

    Map<String, Set<String>> grpParamMap = new ConcurrentHashMap<>();
    // get all a2l groups
    for (A2LGroup a2lGroup : this.a2lGroupList) {
      // get unassigned params
      for (A2LParameter a2lParam : this.unAssignedParams) {
        addToGroupParamMap(grpParamMap, a2lGroup, a2lParam);
      }
    }
    return grpParamMap;
  }


  /**
   * @param grpParamMap
   * @param a2lGroup
   * @param a2lParam
   */
  private void addToGroupParamMap(final Map<String, Set<String>> grpParamMap, final A2LGroup a2lGroup,
      final A2LParameter a2lParam) {
    if (a2lGroup.getLabelMap().get(a2lParam.getName()) != null) {
      Set<String> paramSet = grpParamMap.get(a2lGroup.getGroupName());
      if (paramSet == null) {
        paramSet = new HashSet<String>();
      }
      paramSet.add(a2lParam.getName());
      grpParamMap.put(a2lGroup.getGroupName(), paramSet);
    }
  }

  /**
   * icdm-272
   *
   * @param groupsWithoutRefChar
   */
  private void setGroupsWithoutRefCharData(final List<Group> groupsWithoutRefChar) {

    A2LGroup a2lGroup;
    Group parentElement;
    for (Group group : groupsWithoutRefChar) {
      if (group.getParentList().size() > 0) {
        parentElement = group.getParentList().get(0);
        a2lGroup = this.a2lGrpMap.get(group.getName());
        if (a2lGroup == null) {
          a2lGroup = new A2LGroup(group.getName(), group.getLongIdentifier());
          this.a2lGrpMap.put(group.getName(), a2lGroup);
        }
        final LabelList labelList = group.getLabelList(LabelType.REF_CHARACTERISTIC);
        addToLabelMap(a2lGroup, labelList);
        a2lGroup.setRootName(this.wpRootGroupName);
        final A2LGroup parentA2lGroup = this.a2lGrpMap.get(parentElement.getName());
        // icdm-469 , Null Check made for Parent group if null
        addToGroupList(a2lGroup, parentA2lGroup);
      }
    }
    this.a2lGroupList.remove(this.a2lGrpMap.get(this.wpRootGroupName));
  }


  /**
   * @param a2lGroup
   * @param parentA2lGroup
   */
  private void addToGroupList(final A2LGroup a2lGroup, final A2LGroup parentA2lGroup) {
    if ((parentA2lGroup != null) && (parentA2lGroup.getSubGrpMap() != null)) {
      List<A2LGroup> groups = parentA2lGroup.getSubGrpMap().get(parentA2lGroup.getGroupName());

      if (groups == null) {
        groups = new ArrayList<A2LGroup>();
      }
      groups.add(a2lGroup);
      parentA2lGroup.getSubGrpMap().put(parentA2lGroup.getGroupName(), groups);
      this.a2lGroupList.add(a2lGroup);
    }
  }


  /**
   * @param a2lGroup
   * @param labelList
   */
  private void addToLabelMap(final A2LGroup a2lGroup, final LabelList labelList) {
    if ((labelList != null) && (labelList.size() > 0)) {
      for (String label : labelList) {
        a2lGroup.getLabelMap().put(label, label);
      }
    }
  }

  /**
   * icdm-272
   *
   * @param groupset
   * @param pidcVer
   * @param apicDataProvider
   * @return
   */
  private List<Group> setGroupInformation(final Set<Group> groupset, final PIDCVersion pidcVer,
      final ApicDataProvider apicDataProvider) {
    final List<Group> grpList = new ArrayList<Group>();
    final String attrValue = fetchPidcAttrValue(pidcVer, apicDataProvider);
    for (Group group : groupset) {
      if (group.isRoot() && group.getName().equals(attrValue)) {
        final A2LGroup a2lGroup = new A2LGroup(group.getName(), group.getLongIdentifier());
        this.a2lGroupList.add(a2lGroup);
        this.rootGroup = group;
        this.wpRootGroupName = this.rootGroup.getName();
        this.a2lGrpMap.put(a2lGroup.getGroupName(), a2lGroup);
      }
      grpList.add(group);
    }
    return grpList;
  }

  /**
   * @param pidcVer
   * @param apicDataProvider
   */
  private String fetchPidcAttrValue(final PIDCVersion pidcVer, final ApicDataProvider apicDataProvider) {
    Map<Long, PIDCAttribute> pidcattrMap = pidcVer.getAttributes(false);

    PIDCAttribute wpTypeAttr = getRootGrpPidcAttr(apicDataProvider, pidcattrMap);
    if ((wpTypeAttr != null) && (wpTypeAttr.getAttributeValue() != null)) {
      return wpTypeAttr.getAttributeValue().getTextValueEng();
    }
    return null;
  }


  /**
   * @param apicDataProvider
   * @param pidcattrMap
   * @return
   */
  public PIDCAttribute getRootGrpPidcAttr(final ApicDataProvider apicDataProvider,
      final Map<Long, PIDCAttribute> pidcattrMap) {
    long wpAttrId = Long.valueOf(apicDataProvider.getParameterValue(ApicConstants.WP_ROOT_GROUP_ATTR_ID));
    PIDCAttribute wpTypeAttr = pidcattrMap.get(wpAttrId);
    return wpTypeAttr;
  }

  // icdm-276 Changes
  /**
   * @param fcToWpList fcToWpList
   * @param functionSet functionSet
   * @param currentLanguage currentLanguage
   */
  public void fetchWpValues(final List<FCToWP> fcToWpList, final SortedSet<Function> functionSet,
      final Language currentLanguage) {

    this.workPackageGroupMap.clear();
    this.workPackageList.clear();
    this.wpMap.clear();
    Map<String, Function> functionNames = new HashMap<String, Function>();
    String nameKey;
    for (Function function : functionSet) {
      functionNames.put(function.getName().toUpperCase(), function);
    }

    for (FCToWP fcToWpValues : fcToWpList) {
      if (!functionNames.containsKey(fcToWpValues.getFc().toUpperCase())) {
        continue;
      }
      // Fill Group
      WorkPackageGroup workPackageGroup = this.workPackageGroupMap.get(fcToWpValues.getWpRes().trim());

      if (workPackageGroup == null) {
        workPackageGroup = new WorkPackageGroup(fcToWpValues.getWpRes().trim());
        this.workPackageGroupMap.put(fcToWpValues.getWpRes().trim(), workPackageGroup);
      }
      nameKey = createNamekey(fcToWpValues);

      WorkPackage workPackage = workPackageGroup.getWorkPackageMap().get(nameKey);
      if (workPackage == null) {
        workPackage = new WorkPackage(currentLanguage, workPackageGroup, fcToWpValues, ApicConstants.FC_WP_MAPPING);
        this.workPackageList.add(workPackage);
        this.wpMap.put(workPackage.getFc2wp().getId(), workPackage);
        workPackageGroup.getWorkPackageMap().put(nameKey, workPackage);
      }
      Map<String, String> functionMap = workPackage.getFunctionMap();
      String functionName = functionMap.get(fcToWpValues.getFc());
      if (functionName == null) {
        functionMap.put(functionNames.get(fcToWpValues.getFc().toUpperCase()).getName(),
            functionNames.get(fcToWpValues.getFc().toUpperCase()).getName());
      }

    }
    Collections.sort(this.workPackageList, new WPComparator());
  }

  // icdm-276 Changes

  /**
   * @param nameKey
   * @param fcToWpValues
   * @return
   */
  private String createNamekey(final FCToWP fcToWpValues) {
    String nameKey = "";
    if ((fcToWpValues.getWpNameE() != null) && (fcToWpValues.getWpNameE().length() > 0)) {
      nameKey = fcToWpValues.getWpNameE().trim();
    }
    else if ((fcToWpValues.getWpNameG() != null) && (fcToWpValues.getWpNameG().length() > 0)) {
      nameKey = fcToWpValues.getWpNameG().trim();
    }
    return nameKey;
  }

  // icdm-276 Changes
  private static class WPComparator implements Comparator<WorkPackage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final WorkPackage wp1, final WorkPackage wp2) {
      // use datamodel compareTo methods
      int result = ApicUtil.compare(wp1.getWpGroup().getGroupName(), wp2.getWpGroup().getGroupName());
      if (result == 0) {
        result = ApicUtil.compare(wp1.getWpName(), wp2.getWpName());
      }
      return result;
    }

  }


  private static class A2lGroupComparator implements Comparator<A2LGroup> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final A2LGroup arg0, final A2LGroup arg1) {
      return arg0.getGroupName().compareTo(arg1.getGroupName());
    }

  }


  /**
   * Icdm-586
   *
   * @param characteristics characteristics
   * @param paramPropMap paramProps
   */
  public void createA2LParams(final Collection<Characteristic> characteristics,
      final Map<String, ParamProperties> paramPropMap) {

    A2LParameter parameter;
    ParamProperties props;
    for (Characteristic character : characteristics) {
      props = paramPropMap.get(character.getName());
      if (CommonUtils.isNotNull(props)) {
        parameter = new A2LParameter(character, props.getPClass(), props.isCodeWord(), props.getSsdClass());
      }
      else {
        parameter = new A2LParameter(character, null, false, null);
      }
      this.paramMap.put(character.getName(), parameter);

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

  /**
   * If the Variant coded param name is given then the last characters with [int] is removed and the base parameter name
   * is returned
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
   * @return
   */
  public ConcurrentMap<String, ConcurrentMap<String, Characteristic>> getVarCodedMap() {
    return this.baseAndVarCodedParamsMap;

  }

  // ICDM-1975
  /**
   * Get the param names for the group based mapping
   *
   * @param objList object list
   * @param a2lParams a2l params
   */
  public void getParamsForGrpWP(final List<Object> objList, final Collection<A2LParameter> paramInA2l,
      final List<A2LParameter> a2lParams) {
    Collection<String> paramNameSet = new TreeSet<String>();
    for (Object selObj : objList) {
      if (selObj instanceof A2LWpResponsibility) {
        A2LWpResponsibility a2lWpResp = (A2LWpResponsibility) selObj;
        paramNameSet.addAll(a2lWpResp.getLabelMap().keySet());
      }
    }
    for (String paramName : paramNameSet) {
      for (A2LParameter a2lParameter : paramInA2l) {
        if (a2lParameter.getName().equals(paramName)) {
          a2lParams.add(a2lParameter);
        }
      }
    }

  }

  /**
   * Get the param names for the fc2wp based mapping
   *
   * @param objList obj list
   * @param paramInA2l a2l params
   * @param a2lParams a2lParams
   */
  public void getParamsForFC2WP(final List<Object> objList, final Collection<A2LParameter> paramInA2l,
      final List<A2LParameter> a2lParams) {
    for (Object selObj : objList) {
      if (selObj instanceof A2LWpResponsibility) {
        A2LWpResponsibility a2lWpResp = (A2LWpResponsibility) selObj;
        WorkPackage wrkPkg = a2lWpResp.getWorkpackageMap().get(a2lWpResp.getWpName());
        for (String funcName : wrkPkg.getFunctionMap().values()) {
          getSelA2lParams(paramInA2l, a2lParams, funcName);
        }

      }
    }
  }


  /**
   * @param paramInA2l
   * @param a2lParams
   * @param funcName
   */
  private void getSelA2lParams(final Collection<A2LParameter> paramInA2l, final List<A2LParameter> a2lParams,
      final String funcName) {
    for (A2LParameter a2lParameter : paramInA2l) {
      if ((null != a2lParameter.getDefFunction()) && a2lParameter.getDefFunction().getName().equals(funcName)) {
        a2lParams.add(a2lParameter);
      }
    }
  }


  /**
   * @param a2lWpRespSet a2lWpRespSet
   */
  public void setA2lWpRespSet(final SortedSet<A2LWpResponsibility> a2lWpRespSet) {
    // Task 290992 : Mutable members should not be stored or returned directly
    if (null != a2lWpRespSet) {
      this.a2lWpRespSet = new TreeSet<>(a2lWpRespSet);
    }
  }

  /**
   * set the label map for the A2l wp resp
   */
  public void setLabelMapForA2lRespWp() {

    for (A2LWpResponsibility a2lRespWp : this.a2lWpRespSet) {
      if (a2lRespWp.isA2lGrp()) {
        A2LGroup a2lGroup = getA2lGroupMap().get(a2lRespWp.getIcdmA2lGroup().getGroupName());
        if (a2lGroup != null) {
          fetchA2lGrpLabMap(a2lRespWp, a2lGroup);
        }
      }

      else {
        WorkPackageGroup wpGrp = getWorkPackageGroupMap().get(a2lRespWp.getWpResource());
        if (wpGrp != null) {
          a2lRespWp.getWorkpackageMap().putAll(wpGrp.getWorkPackageMap());
        }
      }

    }

  }


  /**
   * @param a2lRespWp
   * @param a2lGroup
   */
  private void fetchA2lGrpLabMap(final A2LWpResponsibility a2lRespWp, final A2LGroup a2lGroup) {
    if (a2lGroup.getSubGrpMap().isEmpty()) {
      a2lRespWp.getLabelMap().putAll(a2lGroup.getLabelMap());
    }
    else {
      List<A2LGroup> grpList = a2lGroup.getSubGrpMap().get(a2lGroup.getGroupName());
      for (A2LGroup a2lGrp : grpList) {
        if (a2lGrp != null) {
          // Call the method recursively
          fetchA2lGrpLabMap(a2lRespWp, a2lGrp);
        }
      }
    }
  }


  /**
   * @param a2lWpRespSet a2lWpRespSet return the wp Grp Resp map
   */
  public void setWpGrpRespMap(final SortedSet<A2LWpResponsibility> a2lWpRespSet) {
    for (A2LWpResponsibility a2lWpResponsibility : a2lWpRespSet) {
      this.wpGrpRespMap.put(a2lWpResponsibility.getName(), a2lWpResponsibility.getResponbilityEnum());
    }
  }


  /**
   * @return the wpGrpRespMap
   */
  public Map<String, CDRConstants.WPResponsibilityEnum> getWpGrpRespMap() {
    return this.wpGrpRespMap;
  }


  /**
   * @return the a2lResp
   */
  public A2LResponsibility getA2lResp() {
    return this.a2lResp;
  }


  /**
   * @param a2lResp A2LResponsibility
   */
  public void setA2lResp(final A2LResponsibility a2lResp) {
    this.a2lResp = a2lResp;

  }


}
