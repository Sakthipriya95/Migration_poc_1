package com.bosch.caltool.icdm.bo.apic.pidc;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoProvider;
import com.bosch.caltool.icdm.bo.a2l.A2lWPRespResolver;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * Loader class for PidcA2l.
 *
 * @author pdh2cob
 */
public class PidcA2lLoader extends AbstractBusinessObject<PidcA2l, TPidcA2l> {


  /**
   *
   */
  private static final String CDFX_DELIVERIES_ATTACHED_ERROR_CODE = "UNMAP_A2L.CDFX_DELIVERIES_ATTACHED";

  /** The Constant PIDC_VER_ID. */
  private static final String PIDC_VER_ID = "pidcVersId";

  /** The Constant SDOM_PVER_NAME. */
  private static final String SDOM_PVER_NAME = "sdomPverName";

  private static final String A2L_WP_MAPPING_ERROR =
      "Cannot modify A2L mapping since Work Package Definitions are available for the following A2L file(s) " + "\n";

  private static final String MODIFY_A2L_MAPPING_ERROR =
      "Cannot modify A2L mapping since review results are available for the following A2L file(s) " + "\n";

  /**
   * Constructor.
   *
   * @param serviceData Service Data
   */
  public PidcA2lLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_A2L, TPidcA2l.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcA2l createDataObject(final TPidcA2l entity) throws DataException {
    PidcA2l object = new PidcA2l();

    setCommonFields(object, entity);

    object.setProjectId(entity.getTabvProjectidcard().getProjectId());
    object.setPidcVersId(entity.getTPidcVersion() != null ? entity.getTPidcVersion().getPidcVersId() : null);
    object.setA2lFileId(entity.getMvTa2lFileinfo().getId());
    object.setSdomPverName(entity.getSdomPverName());
    object.setSdomPverRevision(entity.getMvTa2lFileinfo().getSdomPverRevision());
    object.setVcdmA2lName(entity.getVcdmA2lName());
    object.setSdomPverVarName(entity.getMvTa2lFileinfo().getSdomPverVariant());
    if ((null != entity.getVcdmA2lName()) && !entity.getVcdmA2lName().isEmpty()) {
      object.setName(entity.getVcdmA2lName());
    }
    else {
      object.setName(entity.getMvTa2lFileinfo().getFilename());
    }
    StringBuilder pidcA2lName = new StringBuilder();
    if (null != object.getSdomPverVarName()) {
      pidcA2lName.append(object.getSdomPverVarName());
      pidcA2lName.append(" : ");
    }
    pidcA2lName.append(object.getName());
    object.setDescription(pidcA2lName.toString());

    object.setVcdmA2lDate(timestamp2String(entity.getVcdmA2lDate()));
    object.setSsdSoftwareVersion(entity.getSsdSoftwareVersion());
    object.setSsdSoftwareVersionId(entity.getSsdSoftwareVersionID());
    object.setSsdSoftwareProjId(entity.getSsdSoftwareProjID());
    object.setCompliParamCount(entity.getMvTa2lFileinfo().getNumCompli());
    object.setActive(yOrNToBoolean(entity.getIsActive()));
    object.setActiveWpParamPresentFlag(yOrNToBoolean(entity.getActiveWpParamPresentFlag()));
    object.setWpParamPresentFlag(yOrNToBoolean(entity.getWpParamPresentFlag()));
    object.setWorkingSetModified(yOrNToBoolean(entity.getIsWorkingSetModifiedFlag()));
    object.setAssignedDate(timestamp2String(entity.getAssignedDate()));
    object.setAssignedUser(entity.getAssignedUser());

    return object;
  }

  /**
   * Checks for A 2 l files.
   *
   * @param pidcVersionId pidc Version Id
   * @return true, if a2l files are mapped
   */
  public boolean hasA2lFiles(final Long pidcVersionId) {
    boolean ret = false;
    for (TPidcA2l entity : new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId).getTabvPidcA2ls()) {
      if (entity.getMvTa2lFileinfo() != null) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  // check if the a2l is available for the given pidc
  /**
   * @param a2lFileID a2l file id
   * @param projectID pidc id
   * @return PidcA2l
   * @throws DataException Data Not Found Exception
   */
  public PidcA2l getPidcA2l(final Long a2lFileID, final Long projectID) throws DataException {
    final TypedQuery<TPidcA2l> qPidcA2l;
    qPidcA2l = getEntMgr().createNamedQuery(TPidcA2l.GET_PIDC_A2L, TPidcA2l.class);
    qPidcA2l.setParameter("a2lFileId", a2lFileID);
    qPidcA2l.setParameter("projectId", projectID);
    PidcA2l pidcA2l = null;
    for (TPidcA2l pidcA2lEntity : qPidcA2l.getResultList()) {
      pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lEntity.getPidcA2lId());
    }
    return pidcA2l;
  }

  /**
   * Checks if A 2 l id present for particular version.
   *
   * @param pidcVersionId pidc Version Id
   * @param a2lFile the A 2 l id
   * @return true, if a2l files are mapped
   * @throws DataException
   */
  public String hasA2lPidcVerMapping(final Long pidcVersionId, final Long a2lFileId) {
    Set<TPidcVersion> pidcVersSet = new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId)
        .getTabvProjectidcard().getTPidcVersions();
    for (TPidcVersion pidcVers : pidcVersSet) {
      for (TPidcA2l entity : pidcVers.getTabvPidcA2ls()) {
        if (entity.getMvTa2lFileinfo().getId() == a2lFileId) {
          return pidcVers.getVersName();
        }
      }
    }
    return null;
  }

  /**
   * Checks if is pidc ver deleted.
   *
   * @param pidcVersionId the pidc version id
   * @return true, if is pidc ver deleted
   * @throws DataException the data exception
   */
  public boolean isPidcVerDeleted(final Long pidcVersionId) throws DataException {
    return new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersionId).isDeleted();
  }

  /**
   * Checks for data review results.
   *
   * @param pidcVersionId pidc Version Id
   * @return true, review results are available
   */
  public boolean hasDataReviewResults(final Long pidcVersionId) {
    boolean ret = false;
    for (TPidcA2l entity : new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId).getTabvPidcA2ls()) {
      if ((entity.getMvTa2lFileinfo() != null) && !entity.getTRvwResults().isEmpty()) {
        ret = true;
        break;
      }
    }
    return ret;
  }


  /**
   * Get all PidcA2l records in system.
   *
   * @return Map. Key - id, Value - PidcA2l object
   * @throws DataException error while retrieving data
   */
  public Map<Long, PidcA2l> getAll() throws DataException {
    Map<Long, PidcA2l> objMap = new ConcurrentHashMap<>();
    TypedQuery<TPidcA2l> tQuery = getEntMgr().createNamedQuery(TPidcA2l.GET_ALL, TPidcA2l.class);
    List<TPidcA2l> dbObj = tQuery.getResultList();
    for (TPidcA2l entity : dbObj) {
      objMap.put(entity.getPidcA2lId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get all PidcA2l records in system.
   *
   * @param pidcId pidcId
   * @return Map. Key - id, Value - PidcA2l object
   * @throws DataException error while retrieving data
   */
  public Map<Long, PidcA2l> getAllByPidc(final Long pidcId) throws DataException {
    Set<TPidcA2l> pidcA2ls = new PidcLoader(getServiceData()).getEntityObject(pidcId).getTabvPidcA2ls();
    Map<Long, PidcA2l> objMap = new HashMap<>();
    for (TPidcA2l entity : pidcA2ls) {
      objMap.put(entity.getPidcA2lId(), createDataObject(entity));
    }
    return objMap;
  }


  /**
   * @param pidcA2lId pidca2lid
   * @return sorted set of CDRReviewResult
   * @throws DataException error while fetching data
   */
  public SortedSet<CDRReviewResult> getCDRResultsByPidcA2l(final Long pidcA2lId) throws DataException {
    TypedQuery<TRvwResult> tQuery = getEntMgr().createNamedQuery(TRvwResult.GET_ALL_BY_PIDC_A2L_ID, TRvwResult.class);
    tQuery.setParameter("pidcA2lId", pidcA2lId);
    tQuery.setHint(ApicConstants.FETCH_SIZE, "100");
    final List<TRvwResult> resultList = tQuery.getResultList();

    final SortedSet<CDRReviewResult> retSet = new TreeSet<>();
    CDRReviewResultLoader cdrResultLoader = new CDRReviewResultLoader(getServiceData());

    for (TRvwResult dbRes : resultList) {
      retSet.add(cdrResultLoader.getDataObjectByID(dbRes.getResultId()));
    }
    return retSet;
  }

  /**
   * Get PidcA2l for Sdom Pver.
   *
   * @param pidcVersionId pidcVersionId
   * @param sdomPverName sdom pver
   * @return Map. Key - pidcA2lId, Value - PidcA2l object
   * @throws DataException error while retrieving data
   */
  public Map<Long, PidcA2l> getAllBySdomPver(final Long pidcVersionId, final String sdomPverName) throws DataException {
    Map<Long, PidcA2l> retMap = new HashMap<>();
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion dbPidcVersion = pidcVersLoader.getEntityObject(pidcVersionId);
    List<TPidcA2l> pidcA2ls = dbPidcVersion.getTabvPidcA2ls();
    for (TPidcA2l entity : pidcA2ls) {
      if (entity.getSdomPverName().equals(sdomPverName)) {
        retMap.put(entity.getPidcA2lId(), createDataObject(entity));
      }
    }
    return retMap;
  }


  /**
   * Get PidcA2l availabilty for Sdom Pver.
   *
   * @param pidcVersionId pidcVersionId
   * @param sdomPverName sdom pver
   * @return Map. Key - pidcA2lId, Value - PidcA2l object
   */
  public boolean isA2lForSdomPverPresent(final Long pidcVersionId, final String sdomPverName) {
    TypedQuery<TPidcA2l> namedQuery = getEntMgr().createNamedQuery(TPidcA2l.GET_ALL_BY_PIDCVERS_ID, TPidcA2l.class);
    namedQuery.setParameter(PIDC_VER_ID, pidcVersionId);
    namedQuery.setParameter(SDOM_PVER_NAME, sdomPverName);
    List<TPidcA2l> pidcA2ls = namedQuery.getResultList();
    return !pidcA2ls.isEmpty();
  }

  /**
   * Gets the pidc A2L details.
   *
   * @param pidcA2lId the pidc A2L id
   * @return the pidc A2L details
   * @throws DataException the data exception
   */
  public PidcA2lFileExt getPidcA2LDetails(final Long pidcA2lId) throws DataException {
    PidcA2l pidcA2l = getDataObjectByID(pidcA2lId);
    PidcVersion pidcVersion;
    if (pidcA2l.getPidcVersId() == null) {
      pidcVersion = null;
    }
    else {
      pidcVersion = new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcA2l.getPidcVersId());
    }
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(pidcA2l.getA2lFileId());

    PidcA2lFileExt output = new PidcA2lFileExt();
    output.setA2lFile(a2lFile);
    output.setPidcVersion(pidcVersion);
    output.setPidcA2l(pidcA2l);

    return output;
  }


  /**
   * Gets the wp version mapping.
   *
   * @deprecated
   * @param pidcA2lId the pidc A 2 l id
   * @return the fc 2 wp version mapping
   * @throws IcdmException the icdm exception
   */
  @Deprecated
  public A2lWpMapping getWorkpackageMapping(final Long pidcA2lId) throws IcdmException {
    A2lWpMapping a2lWpMapping = new A2lWpMapping();
    PidcA2lFileExt pidcA2lExt = getPidcA2LDetails(pidcA2lId);
    Long pidcVersId = pidcA2lExt.getPidcVersion().getId();
    ProjectAttributeLoader attrLdr = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVersionAttribute = attrLdr.createModel(pidcVersId, LOAD_LEVEL.L1_PROJ_ATTRS);

    PidcVersionAttribute wpTypeAttr = pidcVersionAttribute.getPidcVersAttr(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_TYPE_ATTR_ID)));
    a2lWpMapping
        .setWpTypeAttrValueId((wpTypeAttr != null) && (wpTypeAttr.getValueId() != null) ? wpTypeAttr.getValueId() : 0l);

    PidcVersionAttribute qnaireConfigAttr = pidcVersionAttribute.getPidcVersAttr(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR)));
    a2lWpMapping.setDivAttrValueId((qnaireConfigAttr != null) && (qnaireConfigAttr.getValueId() != null) ? qnaireConfigAttr.getValueId() : 0l);

    a2lWpMapping.setGroupMappingId(getCommonParamGrpMappingId());

    // Attribute values
    if ((wpTypeAttr == null) || !wpTypeAttr.getUsedFlag().equals(ApicConstants.CODE_YES)) {
      getLogger().error("WP Type Attribute is not set for pidcVersion  : " + pidcVersId);
      a2lWpMapping.setWPAttrMissingError(true);
      a2lWpMapping.setErrorMessage("'Workpackage Type' Attribute is not set ");
      a2lWpMapping.setMappingSourceId(0l);
      return a2lWpMapping;
    }
    a2lWpMapping.setMappingSourceId(wpTypeAttr.getValueId());
    A2LFileInfo a2lFileContents = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(pidcA2lExt.getA2lFile());
    if ((a2lWpMapping.getMappingSourceId() != null) &&
        CommonUtils.isNotEqual(a2lWpMapping.getMappingSourceId(), a2lWpMapping.getGroupMappingId())) {
      if ((qnaireConfigAttr == null) || (qnaireConfigAttr.getValueId() == null)) {
        String qnaireConfigAttrName = new AttributeLoader(getServiceData()).getQnaireConfigAttr().getName();
        getLogger().error("'" + qnaireConfigAttrName + "' attribute is not set for pidcVersion  : " + pidcVersId);
        a2lWpMapping.setWPAttrMissingError(true);
        a2lWpMapping.setErrorMessage("'" + qnaireConfigAttrName + "' Attribute is not set ");
        return a2lWpMapping;
      }
      a2lWpMapping.setDivAttrValueId(qnaireConfigAttr.getValueId());
    }
    else {
      // A2l Group Fetch
      PidcVersionAttribute wpRootGrpAttr = pidcVersionAttribute.getPidcVersAttr(
          Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_ROOT_GROUP_ATTR_ID)));
      if (CommonUtils.isNull(wpRootGrpAttr) || CommonUtils.isNull(wpRootGrpAttr.getValueId())) {
        a2lWpMapping.setWPAttrMissingError(true);
        a2lWpMapping.setErrorMessage("'SW2CAL Root Group ' attribute not set");
      }
      else {
        a2lWpMapping.setWpRootGrpAttrValueId(wpRootGrpAttr.getValueId());
        fetchFromGroupMapping(a2lFileContents.getAllSortedGroups(), wpRootGrpAttr, a2lWpMapping, null);
      }
    }

    // resolve a2l wp responsibility
    resolveA2lWPResp(pidcA2lExt, a2lFileContents.getAllModulesLabels(), a2lWpMapping);
    return a2lWpMapping;
  }


  /**
   * @param pidcVersId
   * @return
   */
  private Long getCommonParamGrpMappingId() {
    return Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.GROUP_MAPPING_ID));
  }

  /**
   * icdm-272.
   *
   * @param a2lParserGroupSet groupset
   * @param wpRootGrpAttr the grp mapping attr value
   * @param a2lWpMapping the a 2 l wp mapping
   * @param grpValue
   */
  public void fetchFromGroupMapping(final SortedSet<Group> a2lParserGroupSet, final PidcVersionAttribute wpRootGrpAttr,
      final A2lWpMapping a2lWpMapping, final String grpValue) {
    // icdm-272
    final List<Group> groupsWithoutRefChar = new ArrayList<>();
    Group rootGroup = getRootGroup(a2lParserGroupSet, wpRootGrpAttr, a2lWpMapping, grpValue);
    for (Group group : a2lParserGroupSet) {
      if ((rootGroup != null) && group.getParentList().contains(rootGroup)) {
        handleParamsInsideGroup(group, a2lWpMapping);
      }
      else {
        // used to take care about parameter which are not assigned to a GROUP
        if ((rootGroup == null) || !group.getParentList().contains(rootGroup)) {
          groupsWithoutRefChar.add(group);
        }
      }
    }
    setGroupsWithoutRefCharData(groupsWithoutRefChar, a2lWpMapping);
    Collections.sort(a2lWpMapping.getA2lGroupList());
  }


  /**
   * Gets the a 2 l group list.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @return the a 2 l group list
   * @throws IcdmException the icdm exception
   */
  public List<A2LGroup> getA2lGroupList(final Long pidcA2lId) throws IcdmException {
    PidcA2lFileExt pidcA2lExt = getPidcA2LDetails(pidcA2lId);
    Long pidcVersId = pidcA2lExt.getPidcVersion().getId();
    ProjectAttributeLoader attrLdr = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVersionAttribute = attrLdr.createModel(pidcVersId, LOAD_LEVEL.L1_PROJ_ATTRS);
    PidcVersionAttribute wpRootGrpAttr = pidcVersionAttribute.getPidcVersAttr(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_ROOT_GROUP_ATTR_ID)));
    A2lWpMapping a2lWpMapping = new A2lWpMapping();
    a2lWpMapping.setWpRootGrpAttrValueId(wpRootGrpAttr != null ? wpRootGrpAttr.getValueId() : 0l);
    a2lWpMapping.setGroupMappingId(getCommonParamGrpMappingId());
    A2LFileInfo a2lFileContents = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(pidcA2lExt.getA2lFile());
    fetchFromGroupMapping(a2lFileContents.getAllSortedGroups(), wpRootGrpAttr, a2lWpMapping, null);
    return a2lWpMapping.getA2lGroupList();
  }

  /**
   * Gets the functions of label type.
   *
   * @param a2lFileContents
   * @param pidcA2lId the pidc A 2 l id
   * @param labelType the label type
   * @return the functions of label type
   * @throws DataException the data exception
   */
  private SortedSet<Function> getFunctionsOfLabelType(final A2LFileInfo a2lFileContents, final LabelType labelType) {
    final SortedSet<Function> funcListOfLabelType = new TreeSet<>();
    // iterate over all functions
    for (Function function : a2lFileContents.getAllSortedFunctions()) {
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
   * Gets the all functions.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @return all functions of the selected A2l file
   * @throws IcdmException the data exception
   */
  public String[] getAllFunctions(final Long pidcA2lId) throws IcdmException {
    PidcA2lFileExt pidcA2lExt = getPidcA2LDetails(pidcA2lId);
    A2LFileInfo a2lFileContents = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(pidcA2lExt.getA2lFile());
    final SortedSet<Function> finalfuncList = getFunctionsOfLabelType(a2lFileContents, LabelType.DEF_CHARACTERISTIC);
    Iterator<Function> functions = finalfuncList.iterator();
    String[] a2lFunctions = new String[finalfuncList.size()];
    String funcName;
    int index = 0;
    while (functions.hasNext()) {
      Function selFunc = functions.next();
      funcName = selFunc.getName();
      a2lFunctions[index] = funcName;
      index++;
    }
    return a2lFunctions;
  }

  /**
   * icdm-272
   *
   * @param groupsWithoutRefChar
   * @param a2lWpMapping
   */
  private void setGroupsWithoutRefCharData(final List<Group> groupsWithoutRefChar, final A2lWpMapping a2lWpMapping) {
    A2LGroup a2lGroup;
    Group parentElement;
    for (Group group : groupsWithoutRefChar) {
      // only sub groups of root group _workpackages and _responsibilities to be added
      if ((a2lWpMapping.getWpRootGroupName() != null) && CommonUtils.isNotEmpty(group.getParentList()) &&
          a2lWpMapping.getWpRootGroupName().equalsIgnoreCase(group.getParentList().get(0).getName())) {
        parentElement = group.getParentList().get(0);
        a2lGroup = a2lWpMapping.getA2lGrpMap().get(group.getName());
        if (a2lGroup == null) {
          a2lGroup = new A2LGroup();
          a2lGroup.setGroupName(group.getName());
          a2lGroup.setGroupLongName(group.getLongIdentifier());
          a2lWpMapping.getA2lGrpMap().put(group.getName(), a2lGroup);
        }
        final LabelList labelList = group.getLabelList(LabelType.REF_CHARACTERISTIC);
        addToLabelMap(a2lGroup, labelList);
        a2lGroup.setRootName(a2lWpMapping.getWpRootGroupName());
        final A2LGroup parentA2lGroup = a2lWpMapping.getA2lGrpMap().get(parentElement.getName());
        // icdm-469 , Null Check made for Parent group if null
        addToGroupList(a2lGroup, parentA2lGroup, a2lWpMapping);
      }
    }
    a2lWpMapping.getA2lGroupList().remove(a2lWpMapping.getA2lGrpMap().get(a2lWpMapping.getWpRootGroupName()));
  }

  /**
   * @param a2lGroup
   * @param parentA2lGroup
   * @param a2lWpMapping
   */
  private void addToGroupList(final A2LGroup a2lGroup, final A2LGroup parentA2lGroup, final A2lWpMapping a2lWpMapping) {
    if ((parentA2lGroup != null) && (parentA2lGroup.getSubGrpMap() != null)) {
      List<String> groups = parentA2lGroup.getSubGrpMap().get(parentA2lGroup.getGroupName());
      if (groups == null) {
        groups = new ArrayList<>();
      }
      groups.add(a2lGroup.getGroupName());
      parentA2lGroup.getSubGrpMap().put(parentA2lGroup.getGroupName(), groups);
      a2lWpMapping.getA2lGroupList().add(a2lGroup);
    }
  }

  /**
   * @param group
   * @param a2lWpMapping
   */
  private void handleParamsInsideGroup(final Group group, final A2lWpMapping a2lWpMapping) {
    final A2LGroup a2lGroup = new A2LGroup();
    a2lGroup.setGroupName(group.getName());
    a2lGroup.setGroupLongName(group.getLongIdentifier());
    // get REF_CHARACTERISTICs
    final LabelList labelList = group.getLabelList(LabelType.REF_CHARACTERISTIC);
    addToLabelMap(a2lGroup, labelList);

    // get parameter from FUNCTIONs
    final List<Function> functionsList = group.getFunctions();

    if (CommonUtils.isNotEmpty(functionsList)) {
      for (Function function : functionsList) {
        addToLabelMap(a2lGroup, function);
      }
    }
    a2lGroup.setRootName(a2lWpMapping.getWpRootGroupName());
    a2lWpMapping.getA2lGrpMap().put(group.getName(), a2lGroup);
    a2lWpMapping.getA2lGroupList().add(a2lGroup);
  }


  /**
   * @param a2lGroup
   * @param labelList
   */
  private void addToLabelMap(final A2LGroup a2lGroup, final LabelList labelList) {
    if (CommonUtils.isNotEmpty(labelList)) {
      for (String label : labelList) {
        a2lGroup.getLabelMap().put(label, label);
      }
    }
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


  /**
   * icdm-272
   *
   * @param groupset
   * @param grpMappingAttrValue
   * @param a2lWpMapping
   * @param grpValue
   * @param pidcVer
   * @param apicDataProvider
   * @return
   */
  private Group getRootGroup(final Set<Group> groupset, final PidcVersionAttribute grpMappingAttrValue,
      final A2lWpMapping a2lWpMapping, final String grpValue) {
    Group rootGroup = null;
    String attrValue = null;

    if (grpMappingAttrValue == null) {
      if (grpValue != null) {
        attrValue = grpValue;
      }
      else {
        return rootGroup;
      }
    }
    else {
      attrValue = grpMappingAttrValue.getValue();
    }
    for (Group group : groupset) {
      if (group.isRoot() && group.getName().equals(attrValue)) {
        final A2LGroup a2lGroup = new A2LGroup();
        a2lGroup.setGroupName(group.getName());
        a2lGroup.setGroupLongName(group.getLongIdentifier());
        a2lWpMapping.getA2lGroupList().add(a2lGroup);
        rootGroup = group;
        a2lGroup.setRootGroup(true);
        a2lWpMapping.setWpRootGroupName(group.getName());
        a2lWpMapping.getA2lGrpMap().put(a2lGroup.getGroupName(), a2lGroup);
      }
    }
    return rootGroup;
  }

  /**
   * Resolve A 2 l resp workpackage.
   *
   * @deprecated
   * @param pidcA2lId the pidc A 2 l id
   * @return the a 2 l wp mapping
   * @throws IcdmException the icdm exception
   */
  @Deprecated
  public A2lWpMapping resolveA2lRespWorkpackage(final Long pidcA2lId) throws IcdmException {
    A2lWpMapping a2lWpMapping = new A2lWpMapping();
    PidcA2lFileExt pidcA2lExt = getPidcA2LDetails(pidcA2lId);
    Long pidcVersId = pidcA2lExt.getPidcVersion().getId();

    ProjectAttributeLoader attrLdr = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVersionAttribute = attrLdr.createModel(pidcVersId, LOAD_LEVEL.L1_PROJ_ATTRS);

    PidcVersionAttribute wpTypeAttr = pidcVersionAttribute.getPidcVersAttr(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_TYPE_ATTR_ID)));
    a2lWpMapping.setWpTypeAttrValueId(wpTypeAttr != null ? wpTypeAttr.getValueId() : 0l);

    PidcVersionAttribute qnaireConfigAttr = pidcVersionAttribute.getPidcVersAttr(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR)));
    if ((qnaireConfigAttr != null) && (qnaireConfigAttr.getValueId() != null)) {
      a2lWpMapping.setDivAttrValueId(qnaireConfigAttr.getValueId());
    }
    else {
      a2lWpMapping.setDivAttrValueId(0l);
    }

    PidcVersionAttribute wpRootGrpAttr = pidcVersionAttribute.getPidcVersAttr(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_ROOT_GROUP_ATTR_ID)));
    a2lWpMapping.setWpRootGrpAttrValueId(
        (wpRootGrpAttr != null) && (wpRootGrpAttr.getValueId() != null) ? wpRootGrpAttr.getValueId() : 0l);

    a2lWpMapping
        .setMappingSourceId((wpTypeAttr != null) && (wpTypeAttr.getValueId() != null) ? wpTypeAttr.getValueId() : 0l);
    a2lWpMapping.setGroupMappingId(getCommonParamGrpMappingId());
    A2LFileInfo a2lFileContents = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(pidcA2lExt.getA2lFile());

    // load All a2l groups
    fetchFromGroupMapping(a2lFileContents.getAllSortedGroups(), wpRootGrpAttr, a2lWpMapping, null);

    // resolve a2l wp responsibility
    resolveA2lWPResp(pidcA2lExt, a2lFileContents.getAllModulesLabels(), a2lWpMapping);

    return a2lWpMapping;
  }

  /**
   * Resolve A 2 l resp workpackage.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @return the a 2 l wp mapping
   * @throws IcdmException the icdm exception
   */
  public void resolveA2lRespWpForAllGrpTypes(final Long pidcA2lId) throws IcdmException {

    A2lWpMapping a2lWpMapping = new A2lWpMapping();
    PidcA2lFileExt pidcA2lExt = getPidcA2LDetails(pidcA2lId);

    Long wpTypeAttrValId = getCommonParamGrpMappingId();
    a2lWpMapping.setWpTypeAttrValueId(wpTypeAttrValId);

    Long wpGrpId = Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_ROOT_GROUP));
    a2lWpMapping.setWpRootGrpAttrValueId(wpGrpId);
    AttributeValueLoader valLoader = new AttributeValueLoader(getServiceData());
    a2lWpMapping.setMappingSourceId(wpTypeAttrValId);
    a2lWpMapping.setGroupMappingId(wpTypeAttrValId);
    A2LFileInfo a2lFileContents = new A2LFileInfoProvider(getServiceData()).fetchA2LFileInfo(pidcA2lExt.getA2lFile());
    // resolve for grp type- wp
    // load All a2l groups
    SortedSet<Group> allSortedGroups = a2lFileContents.getAllSortedGroups();
    fetchFromGroupMapping(allSortedGroups, null, a2lWpMapping, valLoader.getDataObjectByID(wpGrpId).getName());

    Map<String, Characteristic> allModulesLabels = a2lFileContents.getAllModulesLabels();

    // resolve a2l wp responsibility
    resolveA2lWPResp(pidcA2lExt, allModulesLabels, a2lWpMapping);

    Long respGrpId = Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.RESP_ROOT_GROUP));


    A2lWpMapping respA2lWpMapping = new A2lWpMapping();
    respA2lWpMapping.setWpTypeAttrValueId(a2lWpMapping.getWpTypeAttrValueId());
    respA2lWpMapping.setDivAttrValueId(a2lWpMapping.getDivAttrValueId());
    respA2lWpMapping.setWpRootGrpAttrValueId(respGrpId);
    respA2lWpMapping.setMappingSourceId(a2lWpMapping.getMappingSourceId());
    respA2lWpMapping.setGroupMappingId(a2lWpMapping.getGroupMappingId());
    // resolve for grp type- resp
    // load All a2l groups
    fetchFromGroupMapping(allSortedGroups, null, respA2lWpMapping, valLoader.getDataObjectByID(respGrpId).getName());

    // resolve a2l wp responsibility
    resolveA2lWPResp(pidcA2lExt, allModulesLabels, respA2lWpMapping);

  }

  /**
   * @param a2lWpMapping
   * @param pidcA2lExt
   * @param allModulesLabels
   * @throws IcdmException
   */
  private void resolveA2lWPResp(final PidcA2lFileExt pidcA2lExt, final Map<String, Characteristic> allModulesLabels,
      final A2lWpMapping a2lWpMapping)
      throws IcdmException {
    // resolve A2lWpResp
    A2lWPRespResolver wpRespResolver = new A2lWPRespResolver(pidcA2lExt.getPidcA2l(), a2lWpMapping, getServiceData());
    boolean createA2lRespGrpParams = wpRespResolver.createA2lRespGrpParams(allModulesLabels);
    if (!createA2lRespGrpParams) {
      getLogger().error(A2lWPRespResolver.ERROR_CREATING_A2L_RESP);
    }
  }

  /**
   * @param pidcA2lId PIDC A2l ID
   * @return the extend path - the tree structure of the PIDC A2l
   * @throws DataException error while retrieving data
   */
  private String getPidcTreePath(final PidcA2l pidcA2l) throws DataException {
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(pidcA2l.getPidcVersId());

    return pidcVersLdr.getPidcTreePath(pidcVers.getId()) + pidcVers.getName() + "->" + pidcA2l.getSdomPverName() + "->";
  }

  /**
   * @param pidcA2lId PIDC A2l ID
   * @return extended name of this PIDC A2l
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long pidcA2lId) throws DataException {
    PidcA2l pidcA2l = getDataObjectByID(pidcA2lId);
    return EXTERNAL_LINK_TYPE.A2L_FILE.getTypeDisplayText() + ": " + getPidcTreePath(pidcA2l) + pidcA2l.getName();
  }

  /**
   * @param pidcId Long
   * @return Map<Long, PidcA2lFileExt>
   * @throws IcdmException Exception
   */
  public Map<Long, PidcA2lFileExt> getAllDetByPidcId(final Long pidcId) throws IcdmException {
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    // fetch all the pidcversion for the pidcId
    Map<Long, PidcVersion> allPidcVersionMap = pidcVersionLoader.getAllPidcVersions(pidcId);
    PidcA2lLoader loader = new PidcA2lLoader(getServiceData());

    A2LFileInfoLoader a2lFileLoader = new A2LFileInfoLoader(getServiceData());
    Map<Long, A2LFile> allA2lMap = new HashMap<>();
    Map<Long, PidcA2lFileExt> retMap = new HashMap<>();
    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    Long sdomPverAttrId = attrLdr.getLevelAttrId(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR));
    // Step 1.Get all Pver Name for all Pidc Version of a Pidc
    Set<String> pverNameSet = new AttributeValueLoader(getServiceData()).getPVERNameSet(pidcId, sdomPverAttrId);
    // Step 2.Get all a2l available in PVER
    for (String pverName : pverNameSet) {
      Map<Long, A2LFile> pverA2lMap = a2lFileLoader.getPverA2lFiles(pverName);
      allA2lMap.putAll(pverA2lMap);
    }
    // Step 3.Get all a2l mapped to a pidc version
    if (!pverNameSet.isEmpty()) {
      Map<Long, PidcA2l> pidcA2lFiles = loader.getAllByPidc(pidcId);

      for (PidcA2l pidcA2l : pidcA2lFiles.values()) {
        if (pverNameSet.contains(pidcA2l.getSdomPverName())) {
          PidcA2lFileExt pidcA2lFileExt = new PidcA2lFileExt();
          pidcA2lFileExt.setA2lFile(a2lFileLoader.getDataObjectByID(pidcA2l.getA2lFileId()));
          pidcA2lFileExt.setPidcA2l(pidcA2l);
          pidcA2lFileExt
              .setPidcVersion(pidcA2l.getPidcVersId() != null ? allPidcVersionMap.get(pidcA2l.getPidcVersId()) : null);

          retMap.put(pidcA2l.getA2lFileId(), pidcA2lFileExt);
        }
      }
      // Step 4:Get all unmapped a2l
      for (A2LFile a2lFile : allA2lMap.values()) {
        if (!retMap.containsKey(a2lFile.getId()) && pverNameSet.contains(a2lFile.getSdomPverName())) {

          PidcA2lFileExt pidcA2lFileExt = new PidcA2lFileExt();
          pidcA2lFileExt.setPidcA2l(null);
          pidcA2lFileExt.setA2lFile(a2lFile);
          pidcA2lFileExt.setPidcVersion(null);

          retMap.put(a2lFile.getId(), pidcA2lFileExt);
        }
      }
    }
    return retMap;
  }

  /**
   * @param pidcA2lIdSet pidcA2lId set
   * @return boolean value
   * @throws DataException Exception
   */
  public boolean getPidcA2lAssignmentValidation(final Set<Long> pidcA2lIdSet) throws DataException {
    StringBuilder reviewResultErrorMsg = new StringBuilder();
    StringBuilder wpMappingErrorMsg = new StringBuilder();
    for (Long pidcA2lId : pidcA2lIdSet) {
      TPidcA2l pidcA2lEntity = getEntityObject(pidcA2lId);
      String filename = pidcA2lEntity.getMvTa2lFileinfo().getFilename();
      if (CommonUtils.isNotEmpty(getCDRResultsByPidcA2l(pidcA2lId))) {
        reviewResultErrorMsg.append(filename).append("\n");
      }
      List<TA2lWpDefnVersion> a2lWpDefnVersEntity = pidcA2lEntity.gettA2lWpDefnVersions();
      for (TA2lWpDefnVersion ta2lWpDefnVersion : a2lWpDefnVersEntity) {
        if (CommonUtils.isNotEmpty(ta2lWpDefnVersion.getTA2lWpResponsibility())) {
          wpMappingErrorMsg.append(filename).append("\n");
          break;
        }
      }
    }
    if (reviewResultErrorMsg.length() != 0) {
      throw new DataException(MODIFY_A2L_MAPPING_ERROR + reviewResultErrorMsg);
    }
    if (wpMappingErrorMsg.length() != 0) {
      throw new DataException(A2L_WP_MAPPING_ERROR + wpMappingErrorMsg);
    }
    return true;
  }


  /**
   * @param pidcA2lId PIDC A2L id
   * @return UnmapA2LResponse
   * @throws IcdmException
   */
  public UnmapA2LResponse getUnmapA2LData(final Long pidcA2lId) throws IcdmException {
    UnmapA2LResponse unmapA2LResponse = new UnmapA2LResponse();
    PidcA2l pidcA2l = getDataObjectByID(pidcA2lId);
    // fill a2l file name
    unmapA2LResponse.setA2lFileName(pidcA2l.getName());
    TPidcA2l tPidcA2l = getEntityObject(pidcA2lId);

    // check if the pidc a2l has compliance reviews
    Query compliQuery = getEntMgr().createNativeQuery(
        "select count(*) from T_COMPLI_RVW_A2L comp " + "where comp.a2l_file_id =? " + "and comp.pidc_version_id=? ");
    compliQuery.setParameter(1, tPidcA2l.getMvTa2lFileinfo().getId());
    // when tPidcA2l.getTPidcVersion() is null, throw error
    if (null == tPidcA2l.getTPidcVersion()) {
      throw new IcdmException("UNMAP_A2L.NOT_MAPPED_TO_PIDC_VERSION");
    }
    compliQuery.setParameter(2, tPidcA2l.getTPidcVersion().getPidcVersId());
    BigDecimal compliReviewsCount = (BigDecimal) compliQuery.getSingleResult();
    if (compliReviewsCount.intValue() > 0) {
      throw new IcdmException("UNMAP_A2L.COMPLI_REVIEWS_ATTACHED");
    }

    // validare if there is cdfx delivery
    if (CommonUtils.isNotEmpty(tPidcA2l.gettCdfxDeliveryList())) {
      throw new IcdmException(CDFX_DELIVERIES_ATTACHED_ERROR_CODE);
    }

    // validate if there are any cdfx deliveries attached to review results
    if (CommonUtils.isNotEmpty(tPidcA2l.getTRvwResults())) {
      StringBuilder queryBuilder = new StringBuilder();
      queryBuilder.append("select count(*) from t_cdfx_delvry_param where rvw_result_id in (");
      for (TRvwResult tRvwResult : tPidcA2l.getTRvwResults()) {
        queryBuilder.append(tRvwResult.getResultId()).append(",");
      }
      queryBuilder.deleteCharAt(queryBuilder.length() - 1);
      queryBuilder.append(")");
      Query cdfxFromRvwResultsQuery = getEntMgr().createNativeQuery(queryBuilder.toString());
      BigDecimal cdfxFromRvwResultsCount = (BigDecimal) cdfxFromRvwResultsQuery.getSingleResult();
      if (cdfxFromRvwResultsCount.intValue() > 0) {
        throw new IcdmException(CDFX_DELIVERIES_ATTACHED_ERROR_CODE);
      }
    }
    // fill review result count
    unmapA2LResponse.setRvwResCount(tPidcA2l.getTRvwResults().size());


    // fill pidc version name
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(tPidcA2l.getTPidcVersion().getPidcVersId());
    unmapA2LResponse.setPidcVersName(pidcVersion.getName());
    // fill def version count
    unmapA2LResponse.setDefVersCount(tPidcA2l.gettA2lWpDefnVersions().size());
    // fill var group, wp-resp, param mapping count
    int variantGrpCount = 0;
    int wpRespCount = 0;

    for (TA2lWpDefnVersion a2lWpDefnVersion : tPidcA2l.gettA2lWpDefnVersions()) {
      variantGrpCount += a2lWpDefnVersion.getTA2lVariantGroups().size();
      wpRespCount += a2lWpDefnVersion.getTA2lWpResponsibility().size();
    }
    unmapA2LResponse.setVarGrpCount(variantGrpCount);
    unmapA2LResponse.setWpRespCombinationsCount(wpRespCount);

    // query to retrieve param mapping count
    Query paramMappingQuery =
        getEntMgr().createNativeQuery("select count(*) from t_a2l_wp_param_mapping where wp_resp_id " +
            "in (select wp_resp_id from t_a2l_wp_responsibility where wp_defn_vers_id " +
            "in (select wp_defn_vers_id from t_a2l_wp_defn_versions where pidc_a2l_id=?))");
    paramMappingQuery.setParameter(1, tPidcA2l.getPidcA2lId());
    Object paramMappingCount = paramMappingQuery.getSingleResult();
    BigDecimal bdParamMappingCount = (BigDecimal) paramMappingCount;
    unmapA2LResponse.setParamMappingCount(bdParamMappingCount.intValue());

    return unmapA2LResponse;
  }

}
