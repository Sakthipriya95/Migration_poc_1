/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.AliasDefLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyDetails;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttrSuperGroupLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.attr.CharacteristicLoader;
import com.bosch.caltool.icdm.bo.apic.attr.CharacteristicValueLoader;
import com.bosch.caltool.icdm.bo.apic.attr.MandatoryAttrLoader;
import com.bosch.caltool.icdm.bo.apic.attr.PredefinedAttrValueLoader;
import com.bosch.caltool.icdm.bo.apic.attr.PredefinedValidityLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.LinkLoader;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseGroupLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.MandatoryAttr;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.LevelAttrInfo;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectIdCardAllVersInfoType;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;

/**
 * @author mkl2cob
 */
public class PidcVersionLoader extends AbstractBusinessObject<PidcVersion, TPidcVersion> {

  /**
   *
   */
  private static final String SESSKEY_RESTRICTED_PIDC_VERSIONS = "EXCLUDED_PIDC_VERSIONS";

  /**
   * @param serviceData ServiceData
   */
  public PidcVersionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_VERSION, TPidcVersion.class);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcVersion createDataObject(final TPidcVersion entity) throws DataException {
    PidcVersion data = new PidcVersion();

    // set the fields
    setCommonFields(data, entity);

    data.setPidcId(entity.getTabvProjectidcard().getProjectId());

    Pidc pidc = (new PidcLoader(getServiceData())).getDataObjectByID(entity.getTabvProjectidcard().getProjectId());

    data.setName(pidc.getName() + " (" + entity.getVersName() + ")");
    data.setDeleted(pidc.isDeleted());

    data.setDescription(ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(), entity.getVersDescEng(),
        entity.getVersDescGer(), null));

    Date lastConfirmationDate = timestamp2Date(entity.getLastConfirmationDate());
    data.setLastConfirmationDate(DateFormat.formatDateToString(lastConfirmationDate, DateFormat.DATE_FORMAT_15));

    // Task 271558
    // creating calendar instance
    Calendar lastConfrmCalendar = Calendar.getInstance();
    if (null != lastConfirmationDate) {
      lastConfrmCalendar.setTime(lastConfirmationDate);
      // get the interval days between last confirmation and valid date from TABV_COMMON_PARAMS
      Long intervalDays =
          Long.valueOf((new CommonParamLoader(getServiceData())).getValue(CommonParamKey.PIDC_UP_TO_DATE_INTERVAL));
      // add the interval days to find out the last valid date
      lastConfrmCalendar.add(Calendar.DAY_OF_MONTH, intervalDays.intValue());
      data.setLastValidDate(DateFormat.formatDateToString(lastConfrmCalendar.getTime(), DateFormat.DATE_FORMAT_15));
    }

    data.setUpToDate(isUpToDate(entity));
    data.setPidStatus(entity.getPidStatus());
    data.setProRevId(entity.getProRevId());

    data.setVersionName(entity.getVersName());
    data.setVersDescEng(entity.getVersDescEng());
    data.setVersDescGer(entity.getVersDescGer());

    if (null != entity.getTPidcVers()) {
      data.setParentPidcVerId(entity.getTPidcVers().getPidcVersId());
    }

    return data;
  }

  /**
   * check if it is Up to date or not
   *
   * @param entity TPidcVersion
   * @return boolean
   */
  private boolean isUpToDate(final TPidcVersion entity) {
    Calendar lastConfirmationDate;
    long diffInDays = 0;
    Long intervalDays = null;
    try {
      lastConfirmationDate = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
          timestamp2String(entity.getLastConfirmationDate()));
      if (null == lastConfirmationDate) {
        return false;
      }
      diffInDays = DateUtil.differenceInDays(lastConfirmationDate, Calendar.getInstance());
      CommonParamLoader paramLoader = new CommonParamLoader(getServiceData());
      intervalDays = Long.valueOf(paramLoader.getValue(CommonParamKey.PIDC_UP_TO_DATE_INTERVAL));
      return diffInDays < intervalDays;
    }
    catch (IcdmException exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    return false;
  }

  /**
   * Get Active pidc version of the given PIDC
   *
   * @param pidcId PIDC ID
   * @return Active version
   * @throws DataException if object cannot be created
   */
  public PidcVersion getActivePidcVersion(final Long pidcId) throws DataException {
    PidcVersion ret = null;
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    pidcLoader.validateId(pidcId);

    TabvProjectidcard dbPidc = pidcLoader.getEntityObject(pidcId);
    for (TPidcVersion dbVers : dbPidc.getTPidcVersions()) {
      if (CommonUtils.isEqual(dbVers.getProRevId(), dbPidc.getProRevId())) {
        ret = createDataObject(dbVers);
        break;
      }
    }
    if (ret == null) {
      // Corrupted pidc
      throw new DataException(
          "Active version does not exist for PIDC " + pidcLoader.getDataObjectByID(pidcId).getName());
    }

    return ret;
  }

  /**
   * Get all pidc versions of the given PIDC
   *
   * @param pidcId PIDC ID
   * @return map of all versions. Key - Pidc version ID, value object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersion> getAllPidcVersions(final Long pidcId) throws DataException {
    Map<Long, PidcVersion> retMap = new HashMap<>();


    TabvProjectidcard dbPidc = (new PidcLoader(getServiceData())).getEntityObject(pidcId);
    for (TPidcVersion dbVers : dbPidc.getTPidcVersions()) {
      PidcVersion data = createDataObject(dbVers);
      retMap.put(data.getId(), data);
    }

    return retMap;
  }

  /**
   * @return Map. Key - Active Pidc Version, Value - Map of structure attributes, with key as structure level and value
   *         as pidc attribute object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionInfo> getAllActiveVersionsWithStructureAttributes() throws DataException {
    return getActiveVersionWithStructureAttributes(null);
  }


  /**
   * Fetch all PIDC versions in the system based on active flag
   *
   * @param activeFlag possible values Y, N, ALL
   * @return Map. Key - Pidc Version ID, Value - Map of structure attributes, with key as structure level and value as
   *         pidc attribute object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionInfo> getPidcVersionInfo(final String activeFlag) throws DataException {
    return getPidcVersionInfo(null, null, activeFlag);
  }

  /**
   * fetch based on pidcids and activeflag
   *
   * @param pidcIdSet set of PIDC IDs
   * @param activeFlag possible values Y, N, ALL
   * @return Map. Key - Pidc Version ID, Value - Map of structure attributes, with key as structure level and value as
   *         pidc attribute object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionInfo> getPidcVersionInfoByPidcId(final Set<Long> pidcIdSet, final String activeFlag)
      throws DataException {
    return getPidcVersionInfo(pidcIdSet, null, activeFlag);
  }

  /**
   * Fetch the active PIDC version with structure attributes for the given PIDCs
   *
   * @param pidcIdSet set of PIDC IDs
   * @return Map. Key - Active Pidc Version ID, Value - Map of structure attributes, with key as structure level and
   *         value as pidc attribute object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionInfo> getActiveVersionWithStructureAttributes(final Set<Long> pidcIdSet)
      throws DataException {
    return getPidcVersionInfo(pidcIdSet, null, null);
  }

  /**
   * Fetch the active PIDC version with structure attributes for the given PIDCs
   *
   * @param pidcVersIdSet set of PIDC Version IDs
   * @param activeFlag possible values Y, N, ALL
   * @return Map. Key - Pidc Version ID, Value - Map of structure attributes, with key as structure level and value as
   *         pidc attribute object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionInfo> getPidcVersionInfo(final Set<Long> pidcVersIdSet, final String activeFlag)
      throws DataException {

    return getPidcVersionInfo(null, pidcVersIdSet, activeFlag);
  }

  /**
   * Fetch the active PIDC version with structure attributes for the given PIDCs
   *
   * @param pidcIdSet set of PIDC IDs
   * @return Map. Key - Pidc Version ID, Value - Map of structure attributes, with key as structure level and value as
   *         pidc attribute object
   * @throws DataException if object cannot be created
   */
  private Map<Long, PidcVersionInfo> getPidcVersionInfo(final Set<Long> pidcIdSet, final Set<Long> pidcVersIdSet,
      final String activeFlag)
      throws DataException {

    getLogger().debug("Fetching PidcVersionInfo...  Input PIDCs = {}, PIDC Versions = {}, activeFlag = {}", pidcIdSet,
        pidcVersIdSet, activeFlag);

    String activeFlagToUse = (activeFlag == null) || CommonUtils.isEmptyString(activeFlag)
        ? CommonUtilConstants.CODE_YES : activeFlag.toUpperCase(Locale.ENGLISH);
    getLogger().debug("  active Flag to use = {}", activeFlagToUse);

    final TypedQuery<TabvProjectAttr> qProjStrucAttrs =
        preparePidcStructAttrQuery(pidcIdSet, pidcVersIdSet, activeFlagToUse);

    // Find level attribute details
    Map<Long, Attribute> lvlAttrMap = new AttributeLoader(getServiceData()).getAllLevelAttributes();
    // Structure level attr IDs
    Map<Long, Long> lvlAttrIdMap =
        lvlAttrMap.values().stream().filter(a -> a.getLevel() >= AttributeLoader.MIN_STRUCT_ATTR_LEVEL)
            .collect(Collectors.toMap(Attribute::getId, Attribute::getLevel));

    // Build version info map from the query result
    Map<Long, PidcVersionInfo> retMap = buildPidcVersionInfoMap(qProjStrucAttrs, activeFlagToUse, lvlAttrIdMap);

    // Remove recods with invalid structure/'hidden to user' with structure levels
    removePidcWithInvalidOrNotRelevantStructure(retMap, lvlAttrMap);

    getLogger().debug("pidc version with structure retrieval completed. PIDC versions = {}", retMap.size());

    return retMap;
  }

  /**
   * Prepare the JPA query for the structure attributes, using the given inputs
   *
   * @param pidcIdSet
   * @param pidcVersIdSet
   * @param activeFlagToUse
   * @return
   */
  private TypedQuery<TabvProjectAttr> preparePidcStructAttrQuery(final Set<Long> pidcIdSet,
      final Set<Long> pidcVersIdSet, final String activeFlagToUse) {

    final TypedQuery<TabvProjectAttr> qProjStrucAttrs;

    if (CommonUtils.isNotEmpty(pidcIdSet)) {
      String queryName = null;
      if (CommonUtilConstants.CODE_YES.equals(activeFlagToUse)) {
        queryName = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ACT_VERS_BY_PIDC;
      }
      else if (CommonUtilConstants.CODE_NO.equals(activeFlagToUse)) {
        queryName = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_NON_ACT_VERS_BY_PIDC;
      }
      else if (CommonUtilConstants.CODE_ALL.equals(activeFlagToUse)) {
        queryName = TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_VERS_BY_PIDC;
      }

      qProjStrucAttrs = getEntMgr().createNamedQuery(queryName, TabvProjectAttr.class);
      qProjStrucAttrs.setParameter("pidcIDColl", pidcIdSet);
    }
    else if (CommonUtils.isNotEmpty(pidcVersIdSet)) {
      qProjStrucAttrs =
          getEntMgr().createNamedQuery(TabvProjectAttr.NQ_GET_STRUCT_ATTR_BY_PIDC_VERS_ID, TabvProjectAttr.class);
      qProjStrucAttrs.setParameter("pidcVersId", pidcVersIdSet);
    }
    else if (CommonUtilConstants.CODE_NO.equals(activeFlagToUse)) {
      qProjStrucAttrs = getEntMgr().createNamedQuery(TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_NON_ACTIVE_VERS,
          TabvProjectAttr.class);

    }
    else if (CommonUtilConstants.CODE_ALL.equals(activeFlagToUse)) {
      qProjStrucAttrs =
          getEntMgr().createNamedQuery(TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_VERS, TabvProjectAttr.class);
    }
    else {
      qProjStrucAttrs =
          getEntMgr().createNamedQuery(TabvProjectAttr.NQ_GET_STRUCT_ATTR_FOR_ALL_ACT_VERS, TabvProjectAttr.class);
    }
    return qProjStrucAttrs;
  }


  /**
   * Build PIDC Version info map from structure attribute entity results, from the query
   *
   * @param activeFlagToUse
   * @param qProjStrucAttrs
   * @param lvlAttrIdMap
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionInfo> buildPidcVersionInfoMap(final TypedQuery<TabvProjectAttr> qProjStrucAttrs,
      final String activeFlagToUse, final Map<Long, Long> lvlAttrIdMap)
      throws DataException {

    TPidcVersion dbPidcVers;
    Long pidcVersId;

    PidcVersionAttributeLoader pidcAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    Map<Long, PidcVersionInfo> retMap = new HashMap<>();
    PidcVersionInfo retObj;

    for (TabvProjectAttr dbProjAttr : qProjStrucAttrs.getResultList()) {

      dbPidcVers = dbProjAttr.getTPidcVersion();
      pidcVersId = dbPidcVers.getPidcVersId();
      if (isHiddenToCurrentUser(pidcVersId)) {
        getLogger().debug("  pidc version with structure : PIDC version {} is hidden to the user", pidcVersId);
        continue;
      }

      retObj = retMap.get(pidcVersId);

      if (retObj == null) {
        retObj = new PidcVersionInfo();
        retObj.setPidc(pidcLoader.getDataObjectByID(dbPidcVers.getTabvProjectidcard().getProjectId()));
        retObj.setPidcVersion(getDataObjectByID(pidcVersId));
        boolean isActive = CommonUtils.isEqual(retObj.getPidc().getProRevId(), retObj.getPidcVersion().getProRevId());
        retObj.setActive(isActive);


        if ((CommonUtilConstants.CODE_YES.equals(activeFlagToUse) && isActive) ||
            (CommonUtilConstants.CODE_NO.equals(activeFlagToUse) && !isActive) ||
            CommonUtilConstants.CODE_ALL.equals(activeFlagToUse)) {
          retMap.put(pidcVersId, retObj);
        }

      }

      PidcVersionAttribute pidcAttr = pidcAttrLoader.createDataObject(dbProjAttr);
      retObj.getLevelAttrMap().put(lvlAttrIdMap.get(pidcAttr.getAttrId()), pidcAttr);
    }

    getLogger().debug("PIDC Version info objects created from query result. PIDC versions count = {}", retMap.size());

    return retMap;
  }

  /**
   * Remove PIDC info recods with 'hidden to user' or invalid structure levels
   *
   * @param pidcVersionInfoMap
   * @param lvlAttrMap
   * @throws DataException
   */
  private void removePidcWithInvalidOrNotRelevantStructure(final Map<Long, PidcVersionInfo> pidcVersionInfoMap,
      final Map<Long, Attribute> lvlAttrMap)
      throws DataException {

    getLogger().debug("Verifying structure integrity, attr visibility now. of Pidc Version info objects ...");

    long lvlMin = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long lvlMax = new AttributeLoader(getServiceData()).getMaxStructAttrLevel();

    Iterator<Entry<Long, PidcVersionInfo>> itr = pidcVersionInfoMap.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<Long, PidcVersionInfo> entry = itr.next();
      if (validateStructAttrs(entry.getValue().getLevelAttrMap(), lvlMin, lvlMax, lvlAttrMap,
          entry.getValue().getPidcVersion()) < 0) {

        getLogger().debug("  pidc version with structure : Removing pidc version with ID '{}'", entry.getKey());
        itr.remove();
      }
    }

    getLogger().debug("No. of Pidc Version info objects after verification = {}", pidcVersionInfoMap.size());
  }

  /**
   * @param selAPRJName vCDM APRJ Name
   * @return Set<PidcVersion>
   * @throws DataException if object cannot be created
   */
  public SortedSet<PidcVersion> getAprjPIDCs(final String selAPRJName) throws DataException {
    final TypedQuery<Long> qPidcVerId;
    qPidcVerId = getEntMgr().createNamedQuery(TabvProjectAttr.NQ_FIND_APRJ_PIDC_BY_TEXT_VAL, Long.class);
    qPidcVerId.setParameter("selAPRJName", selAPRJName.toUpperCase(Locale.getDefault()));
    SortedSet<PidcVersion> pidcVersionSet = new TreeSet<>();
    for (Long pidId : qPidcVerId.getResultList()) {
      pidcVersionSet.add(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidId));
    }
    return pidcVersionSet;
  }

  /**
   * Fetch the active PIDC version with structure attributes for the given PIDCs
   *
   * @param model PidcVersionAttributeModel
   * @return Map. Key - Active Pidc Version, Value - Map of structure attributes, with key as structure level and value
   *         as pidc attribute object
   * @throws DataException if object cannot be created
   */
  PidcVersionInfo getPidcVersionWithStructureAttributes(final PidcVersionAttributeModel model) throws DataException {

    PidcVersionInfo retObj;

    PidcVersionAttributeLoader pidcAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    PidcVersion pidcVers = model.getPidcVersion();
    Pidc pidc = model.getPidc();

    Map<Long, Attribute> levelAttrMap = attrLoader.getAllLevelAttributes();
    long levelMin = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long levelMax = attrLoader.getMaxStructAttrLevel();

    retObj = new PidcVersionInfo();
    try {

      retObj.setPidcVersion(pidcVers);
      retObj.setPidc(pidc);

      Map<Long, PidcVersionAttribute> structAttrMap = pidcAttrLoader.getStructureAttributes(model);
      int status = validateStructAttrs(structAttrMap, levelMin, levelMax, levelAttrMap, pidcVers);
      if (status < 0) {
        // If pidc version is hidden, do not add the PIDC version to ret collection
        return null;
      }
      retObj.setLevelAttrMap(structAttrMap);

    }
    catch (DataException exp) {
      // If active version could not be found, pidc is corrupted, continue with next PIDC
      getLogger().error(exp.getMessage(), exp);
    }


    return retObj;

  }

  private int validateStructAttrs(final Map<Long, PidcVersionAttribute> structAttrMap, final long levelMin,
      final long levelMax, final Map<Long, Attribute> levelAttrMap, final PidcVersion activeVers) {

    int status = 0;// No problems

    for (long level = levelMin; level <= levelMax; level++) {
      PidcVersionAttribute projAttr = structAttrMap.get(level);
      Attribute attr = levelAttrMap.get(level);
      if (projAttr == null) {

        getLogger().error("PIDC " + activeVers.getName() + " - Level Attribute " + attr.getName() + " undefined");

        // Add dummy attribute
        projAttr = new PidcVersionAttribute();
        projAttr.setAttrId(attr.getId());
        projAttr.setName(attr.getName());
        projAttr.setValue("NULL");
        projAttr.setValueId(-1L);

        structAttrMap.put(level, projAttr);
        if (status == 0) {
          status = 1;// Dummy attributes added
        }
      }
      else if (AbstractProjectAttributeLoader.isDetailsHiddenToCurrentUser(projAttr)) {
        getLogger().info("PIDC " + activeVers.getName() + " - Level Attribute " + attr.getName() + " is hidden.");
        status = -1;// Level attribute is hidden for the user. Treat this PIDC as hidden
        break;
      }

    }
    return status;

  }

  /**
   * @return restricted pidc versions
   */
  private Set<Long> getRestrictedPidcVersions() {

    Object data = getServiceData().retrieveData(getClass(), SESSKEY_RESTRICTED_PIDC_VERSIONS);
    if (data == null) {

      getLogger().debug("fetching restricted list of pidc version ids...");

      Set<Long> excVersSet = new HashSet<>();

      Set<Long> quotHidValSet = (new AttributeValueLoader(getServiceData())).getQuotationHiddenValueIds();

      if (!quotHidValSet.isEmpty()) {
        TypedQuery<Long> pidcVrsnIdsQry =
            getEntMgr().createNamedQuery(TabvProjectAttr.GET_PIDCVERSIONIDS_FOR_VALUEID, Long.class);
        pidcVrsnIdsQry.setParameter("valIDs", quotHidValSet);
        excVersSet.addAll(pidcVrsnIdsQry.getResultList());
      }

      getLogger().debug("Restricted pidc version count = {}", excVersSet.size());

      data = excVersSet;
      getServiceData().storeData(getClass(), SESSKEY_RESTRICTED_PIDC_VERSIONS, excVersSet);

    }

    return (Set<Long>) (data);
  }


  /**
   * Check whether the given PIDC version is among the restricted versions
   *
   * @param pidcVersId PIDC Version
   * @return true, if PIDC Version is restricted
   */
  boolean isRestrictedPidcVersion(final Long pidcVersId) {
    return getRestrictedPidcVersions().contains(pidcVersId);
  }

  /**
   * Checks whether the given PIDC Version is hidden to current user
   *
   * @param pidcVersId PIDC Version ID
   * @return true, if hidden
   * @throws DataException error while retrieving data
   */
  public boolean isHiddenToCurrentUser(final Long pidcVersId) throws DataException {

    // If the attribute value for a PIDC-Version is quotation (hidden), the PIDC is shown only for users with any kind
    // of access rights on the PIDC (Read, Write, Owner, Grant) and for users with APIC_WRITE or APIC_READ_ALL access
    if (isRestrictedPidcVersion(pidcVersId)) {

      getLogger().debug("PIDC Version '{}' is restricted", pidcVersId);

      // If user is not valid return hidden
      if (!getServiceData().isAuthenticatedUser()) {
        getLogger().debug("  Restricted PIDC '{}': User NOT authenticated. PIDC is hidden", pidcVersId);
        return true;
      }
      // if the user has apic write access then the pidc version is not hidden
      ApicAccessRightLoader accessRightLoader = new ApicAccessRightLoader(getServiceData());
      if ((accessRightLoader.isCurrentUserApicReadAll() || accessRightLoader.isCurrentUserApicWrite())) {
        getLogger().debug("  Restricted PIDC '{}': User has APIC_WRITE or APIC_READ_ALL access. PIDC is visible",
            pidcVersId);
        return false;
      }

      Long pidcId = getEntityObject(pidcVersId).getTabvProjectidcard().getProjectId();

      // Check node access node access is atleast 'read' then not hidden
      boolean hasRead = (new NodeAccessLoader(getServiceData()).isCurrentUserRead(pidcId));

      if (!hasRead) {
        getLogger().debug("  Restricted PIDC '{}': PIDC hidden to user as no node access 'read' rights", pidcVersId);
      }

      return !hasRead;
    }

    return false;
  }

  /**
   * @return Key - PIDC ID, Value - Active version of the PIDC
   * @throws DataException erro while creating entity
   */
  public Map<Long, PidcVersion> getAllNonDeletedActiveVersions() throws DataException {
    Map<Long, PidcVersion> retMap = new HashMap<>();

    TypedQuery<TPidcVersion> qry = getServiceData().getEntMgr()
        .createNamedQuery(TPidcVersion.NQ_GET_ACTIVE_NON_DELETED_VERSIONS, TPidcVersion.class);
    for (TPidcVersion entity : qry.getResultList()) {
      retMap.put(entity.getTabvProjectidcard().getProjectId(), createDataObject(entity));
    }

    return retMap;
  }


  /**
   * @return
   * @throws DataException
   */
  Set<PidcVersion> getVersionsByAttribute(final Set<Long> attrIdSet) throws DataException {
    Set<PidcVersion> retSet = new HashSet<>();
    String attrStr = CommonUtils.join(attrIdSet, ",");
    String qryStr =
        "select pvers.PIDC_VERS_ID from t_pidc_version pvers, tabv_projectidcard pidc, TABV_ATTR_VALUES val " +
            "where pidc.pro_rev_id = pvers.pro_rev_id and pvers.project_id  = pidc.project_id    " +
            "  and pidc.value_id = val.value_id and val.DELETED_FLAG = 'N'                          " +
            "  and pvers.pidc_vers_id in                                                             " +
            "  (                                                                                 " +
            "    select PIDC_VERS_ID from tabv_project_attr where attr_id in (" + attrStr + ")         " +
            "    UNION                                                                            " +
            "    select PIDC_VERS_ID from tabv_variants_attr where attr_id in (" + attrStr + ")           " +
            "    UNION                                                                                " +
            "    select PIDC_VERS_ID from TABV_PROJ_SUB_VARIANTS_ATTR where attr_id in  (" + attrStr + ") " +
            "  )                                                                                ";


    Query qry = getServiceData().getEntMgr().createNativeQuery(qryStr);
    for (Object result : qry.getResultList()) {
      retSet.add(getDataObjectByID(Long.valueOf(result.toString())));
    }
    return retSet;
  }


  /**
   * @return
   * @throws DataException
   */
  Set<PidcVersion> getVersionsByUsedFlag(final Set<Long> attrIdSet, final String usedFlagCode) throws DataException {
    Set<PidcVersion> retSet = new HashSet<>();
    String attrStr = CommonUtils.join(attrIdSet, ",");
    String qryStr =
        "select pvers.PIDC_VERS_ID from t_pidc_version pvers, tabv_projectidcard pidc, TABV_ATTR_VALUES val " +
            "where pidc.pro_rev_id = pvers.pro_rev_id and pvers.project_id  = pidc.project_id    " +
            "  and pidc.value_id = val.value_id and val.DELETED_FLAG = 'N'                          " +
            "  and pvers.pidc_vers_id in                                                             " +
            "  (                                                                                 " +
            "    select PIDC_VERS_ID from tabv_project_attr where attr_id in (" + attrStr + ")      " +
            "                   and Used = '" + usedFlagCode + "'         " +
            "    UNION                                                                            " +
            "    select PIDC_VERS_ID from tabv_variants_attr where attr_id in (" + attrStr + ")           " +
            "                   and used = '" + usedFlagCode + "'         " +
            "    UNION                                                                                " +
            "    select PIDC_VERS_ID from TABV_PROJ_SUB_VARIANTS_ATTR where attr_id in  (" + attrStr + ") " +
            "                   and used = '" + usedFlagCode + "'         " +
            "  )                                                                                ";


    Query qry = getServiceData().getEntMgr().createNativeQuery(qryStr);
    for (Object result : qry.getResultList()) {
      retSet.add(getDataObjectByID(Long.valueOf(result.toString())));
    }
    return retSet;
  }

  /**
   * @return
   * @throws DataException
   */
  Set<PidcVersion> getVersionsByValueIds(final Set<Long> valueIdSet) throws DataException {

    EntityManager entMgr = getNewEntMgr();

    persistValIdToTempTable(entMgr, valueIdSet);

    Set<PidcVersion> retSet = new HashSet<>();

    Query qry = entMgr.createNamedQuery(TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_VALUE_IDS);
    for (Object result : qry.getResultList()) {
      retSet.add(getDataObjectByID(Long.valueOf(result.toString())));
    }
    return retSet;
  }

  /**
   * Get active PIDC Versions using PIDC name value ID
   *
   * @param nameValIdSet name ids
   * @return set of pidc versions
   * @throws DataException any error
   */
  Set<PidcVersion> getVersionsByName(final Set<Long> nameValIdSet) throws DataException {

    EntityManager entMgr = getNewEntMgr();

    persistValIdToTempTable(entMgr, nameValIdSet);

    TypedQuery<TPidcVersion> query =
        entMgr.createNamedQuery(TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_PIDC_NAME, TPidcVersion.class);

    Set<PidcVersion> retSet = new HashSet<>();
    for (TPidcVersion entity : query.getResultList()) {
      retSet.add(createDataObject(entity));
    }

    return retSet;

  }


  /**
   * Maximum number of expression allowed in oracle is 1000. Since attr val set may contain more than 1000 records
   * storing it in temp table
   *
   * @param entMgr
   * @param nameValIdSet
   */
  private void persistValIdToTempTable(final EntityManager entMgr, final Set<Long> nameValIdSet) {
    entMgr.getTransaction().begin();

    GttObjectName tempParam;

    for (Long attrValId : nameValIdSet) {
      tempParam = new GttObjectName();
      tempParam.setId(attrValId);
      entMgr.persist(tempParam);

    }

    entMgr.flush();
  }

  /**
   * @param yesAttrIds - attribute Ids of the boolean type attributes with usedflag = Yes
   * @param noAttrIds - attribute Ids of the boolean type attributes with usedflag = No
   * @return set of PidcVersions
   * @throws DataException - Error while fetching PidcVersions
   */
  public Set<PidcVersion> getPidcWithBooleanValues(final Set<Long> yesAttrIds, final Set<Long> noAttrIds)
      throws DataException {
    Set<PidcVersion> pidcVersSet = new HashSet<>();
    String yesAttrs = CommonUtils.join(yesAttrIds, ",");
    String noAttrs = CommonUtils.join(noAttrIds, ",");
    String queryStr =
        "select pvers.PIDC_VERS_ID from t_pidc_version pvers, tabv_projectidcard pidc, TABV_ATTR_VALUES val " +
            "where pidc.pro_rev_id = pvers.pro_rev_id and pvers.project_id  = pidc.project_id               " +
            "  and pidc.value_id = val.value_id and val.DELETED_FLAG = 'N'  and pvers.pidc_vers_id in     ( " +
            "    select PIDC_VERS_ID from tabv_project_attr where attr_id in (" + yesAttrs + " )            " +
            " and Used = 'Y'                                                                                " +
            "    UNION                                                                                      " +
            "    select PIDC_VERS_ID from tabv_project_attr where attr_id in  (" + noAttrs + " )            " +
            " and Used = 'N'                                                                                " +
            "    UNION                                                                                      " +
            "    select PIDC_VERS_ID from tabv_variants_attr where attr_id in (" + yesAttrs + " )           " +
            " and used = 'Y'                                                                                " +
            "    UNION                                                                                      " +
            "    select PIDC_VERS_ID from tabv_variants_attr where attr_id in (" + noAttrs + " )            " +
            "  and used = 'N'                                                                               " +
            "    UNION                                                                                      " +
            "    select PIDC_VERS_ID from TABV_PROJ_SUB_VARIANTS_ATTR where attr_id in ( " + yesAttrs + " ) " +
            " and used = 'Y'                                                                                " +
            "    UNION                                                                                      " +
            "    select PIDC_VERS_ID from TABV_PROJ_SUB_VARIANTS_ATTR where attr_id in ( " + noAttrs + " )  " +
            " and used = 'N' " + "  ) ";
    Query query = getServiceData().getEntMgr().createNativeQuery(queryStr);
    List resultList = query.getResultList();
    for (Object result : resultList) {
      pidcVersSet.add(getDataObjectByID(Long.valueOf(result.toString())));
    }
    return pidcVersSet;
  }

  /**
   * Get active PIDC Versions using variant name value ID
   *
   * @param usedFlag used flag
   * @param nameValIdSet name ids
   * @return set of pidc versions
   * @throws DataException any error
   */
  Set<PidcVersion> getVersionsByVarName(final String usedFlag, final Set<Long> nameValIdSet) throws DataException {

    TypedQuery<TPidcVersion> query;
    EntityManager entMgr = getNewEntMgr();
    if (nameValIdSet.isEmpty()) {
      String quryName = ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(usedFlag)
          ? TPidcVersion.NQ_GET_PIDC_VERSIONS_WITH_VARIANTS : TPidcVersion.NQ_GET_PIDC_VERSIONS_WITHOUT_VARIANTS;
      query = entMgr.createNamedQuery(quryName, TPidcVersion.class);
    }
    else {
      persistValIdToTempTable(entMgr, nameValIdSet);
      query = entMgr.createNamedQuery(TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_VAR_NAME, TPidcVersion.class);
    }

    Set<PidcVersion> retSet = new HashSet<>();
    for (TPidcVersion entity : query.getResultList()) {
      retSet.add(createDataObject(entity));
    }

    return retSet;

  }

  /**
   * Get active PIDC Versions using sub variant name value ID
   *
   * @param usedFlag used flag
   * @param nameValIdSet name ids
   * @return set of pidc versions
   * @throws DataException any error
   */
  Set<PidcVersion> getVersionsBySubVarName(final String usedFlag, final Set<Long> nameValIdSet) throws DataException {

    TypedQuery<TPidcVersion> query;
    EntityManager entMgr = getNewEntMgr();
    if (nameValIdSet.isEmpty()) {
      String quryNaem = ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(usedFlag)
          ? TPidcVersion.NQ_GET_PIDC_VERSIONS_WITH_SUBVARIANTS : TPidcVersion.NQ_GET_PIDC_VERSIONS_WITHOUT_SUBVARIANTS;
      query = entMgr.createNamedQuery(quryNaem, TPidcVersion.class);
    }
    else {
      persistValIdToTempTable(entMgr, nameValIdSet);
      query = entMgr.createNamedQuery(TPidcVersion.NQ_GET_PIDC_VERSIONS_BY_SUBVAR_NAME, TPidcVersion.class);
    }

    Set<PidcVersion> retSet = new HashSet<>();
    for (TPidcVersion entity : query.getResultList()) {
      retSet.add(createDataObject(entity));
    }

    return retSet;

  }


  /**
   * @param pidcId
   * @param activePidcVerId
   * @return
   * @throws DataException
   */
  public boolean isOtherPidcVerPresent(final Long pidcId, final Long activePidcVerId) {
    TabvProjectidcard dbPidc = (new PidcLoader(getServiceData())).getEntityObject(pidcId);
    Set<TPidcVersion> otherPidcVerSet = dbPidc.getTPidcVersions();
    otherPidcVerSet.remove((new PidcVersionLoader(getServiceData())).getEntityObject(activePidcVerId));
    return !otherPidcVerSet.isEmpty();
  }


  /**
   * Fetch PidcDetStructure for given pidc version id
   *
   * @param pidcVersionId
   * @return Map<Long,PidcDetStructure>
   * @throws DataException
   */
  public Map<Long, PidcDetStructure> getPidcDetStructure(final Long pidcVersionId) throws DataException {

    TPidcVersion tPidcVersion = getEntityObject(pidcVersionId);

    List<TabvPidcDetStructure> list = tPidcVersion.getTabvPidcDetStructures();

    Map<Long, PidcDetStructure> pidcDetStructMap = new HashMap<>();
    for (TabvPidcDetStructure detStruct : list) {
      PidcDetStructureLoader loader = new PidcDetStructureLoader(getServiceData());
      PidcDetStructure pidcDetStruct = loader.getDataObjectByID(detStruct.getPdsId());
      if (pidcDetStruct != null) {
        pidcDetStructMap.put(pidcDetStruct.getPidAttrLevel(), pidcDetStruct);
      }
    }

    return pidcDetStructMap;

  }

  /**
   * @param pidcVersionId - pidc version id
   * @return PidcVersionWithDetails object
   * @throws IcdmException Exception during fetching data for pidc version
   */
  public PidcVersionWithDetails getPidcVersionWithDetails(final Long pidcVersionId) throws IcdmException {
    PidcVersionWithDetailsInput flags = new PidcVersionWithDetailsInput();
    flags.setPidcVersionId(pidcVersionId);
    return getPidcVersionWithDetails(flags, "ALL", new HashSet<Long>());
  }


  /**
   * @param input PidcVersDtlsLoaderInput
   * @param quotationRelevantFlag Quotation Relevant Flag
   * @param usecaseGroupIds Usecase Group Ids
   * @return PidcVersionWithDetails
   * @throws IcdmException Exception during fetching of
   */
  public PidcVersionWithDetails getPidcVersionWithDetails(final PidcVersionWithDetailsInput input,
      final String quotationRelevantFlag, final Set<Long> usecaseGroupIds)
      throws IcdmException {

    getLogger().info(
        "Fetching Pidc version with details started. Input: {} quotationRelevantFlag={} usecaseGroupIds {}", input,
        quotationRelevantFlag, usecaseGroupIds);

    PidcVersionWithDetails retObj = new PidcVersionWithDetails();

    PidcVersionAttributeModel model =
        new ProjectAttributeLoader(getServiceData()).createModel(input.getPidcVersionId());

    fillPidcVersionInfo(input, retObj, model);

    // fill attributes , variants, sub-variant info
    fillAttrVarSubVarInfo(input, retObj, model);

    fillAliasDefModel(input, retObj);


    fillCharAndAttrValLinks(input, retObj, model);

    fillAttrGroupModel(input, retObj);

    fillPidcDetStruct(input, retObj);

    fillAttrLinks(input, retObj);

    // fill predefined validity map
    fillPredefinedAttrDepModel(input, retObj, model);

    fillAttrDepnModel(input, retObj);

    fillAllMandatoryAttributes(input, retObj);

    fillUsecaseModel(input, retObj);

    applyQueryParamFilter(quotationRelevantFlag, usecaseGroupIds, retObj);

    getLogger().info("Fetching Pidc version with details completed.");

    return retObj;
  }


  /**
   * @param quotationRelevantFlag
   * @param usecaseGroupIds
   * @param retObj
   * @throws IcdmException
   */
  private void applyQueryParamFilter(final String quotationRelevantFlag, final Set<Long> usecaseGroupIds,
      final PidcVersionWithDetails retObj)
      throws IcdmException {
    boolean isQuotationRelevantValid =
        (CommonUtils.isNotEmptyString(quotationRelevantFlag) && CommonUtils.isNotEqual("ALL", quotationRelevantFlag));
    if (CommonUtils.isNotEmpty(usecaseGroupIds)) {
//      check for valid usecaseGroupIds
      UseCaseGroupLoader groupLoader = new UseCaseGroupLoader(getServiceData());
      Map<Long, UseCaseGroup> allUsecaseGroups = groupLoader.getAll();
      if (CommonUtils.isNotEmpty(allUsecaseGroups)) {
        String invalidUsecaseGroupIds = usecaseGroupIds.stream().filter(ucgId -> !allUsecaseGroups.containsKey(ucgId))
            .map(String::valueOf).collect(Collectors.joining(","));
        if (CommonUtils.isNotEmptyString(invalidUsecaseGroupIds)) {
          throw new InvalidInputException("Invalid QueryParam::usecaseGroupIds=" + invalidUsecaseGroupIds);
        }
      }
      Map<Long, Boolean> ucpAttrs =
          new UcpAttrLoader(getServiceData()).getUcpAttrIdsByGroupIds(usecaseGroupIds, isQuotationRelevantValid);
//      pidcVersionAttributeMap is used in the external API for the attributes response
      retObj.getPidcVersionAttributeMap().entrySet()
          .removeIf(pidcVersionAttribute -> !ucpAttrs.containsKey(pidcVersionAttribute.getKey()));
//      quotation relevant filter on the attributes must be applied based on the usecase groups
      if (isQuotationRelevantValid) {
        filterUcgAttrsOnQuotationRelevant(quotationRelevantFlag, retObj, ucpAttrs);
      }
    }
    else if (isQuotationRelevantValid) {
//      quotation relevant filter on the attributes is applied without considering the usecase groups
//      quotationRelevantUcAttrIdSet = overall quotation relevant attributes
      Set<Long> quotationRelevantUcAttrIdSet = retObj.getProjectUsecaseModel().getQuotationRelevantUcAttrIdSet();
      if (quotationRelevantFlag.equals(CommonUtilConstants.CODE_YES)) {
        retObj.getPidcVersionAttributeMap().entrySet()
            .removeIf(pidcVersionAttribute -> !isQuotationRelevantAttribute(quotationRelevantUcAttrIdSet,
                pidcVersionAttribute.getKey()));
      }
      else if (quotationRelevantFlag.equals(CommonUtilConstants.CODE_NO)) {
        retObj.getPidcVersionAttributeMap().entrySet()
            .removeIf(pidcVersionAttribute -> isQuotationRelevantAttribute(quotationRelevantUcAttrIdSet,
                pidcVersionAttribute.getKey()));
      }
    }
  }


  /**
   * Apply usecase groups quotation relevant filter. Any attribute within the given usecase groups and the
   * tabv_ucp_attrs.mappings_flag=1 is a quotation relevant attribute
   *
   * @param quotationRelevantFlag
   * @param retObj
   * @param ucpAttrs
   */
  private void filterUcgAttrsOnQuotationRelevant(final String quotationRelevantFlag,
      final PidcVersionWithDetails retObj, final Map<Long, Boolean> ucpAttrs) {
    if (quotationRelevantFlag.equals(CommonUtilConstants.CODE_YES)) {
      retObj.getPidcVersionAttributeMap().entrySet()
          .removeIf(pidcVersionAttribute -> !ucpAttrs.get(pidcVersionAttribute.getKey()));
    }
    else if (quotationRelevantFlag.equals(CommonUtilConstants.CODE_NO)) {
      retObj.getPidcVersionAttributeMap().entrySet()
          .removeIf(pidcVersionAttribute -> ucpAttrs.get(pidcVersionAttribute.getKey()));
    }
  }

  /**
   * Apply overall quotation relevant filter. Any attribute with tabv_ucp_attrs.mappings_flag=1 is a quotation relevant
   * attribute
   *
   * @param quotationRelevantUcAttrIdSet
   * @param pidcVersionAttributeId
   * @return boolean - quotation relevant or not
   */
  private boolean isQuotationRelevantAttribute(final Set<Long> quotationRelevantUcAttrIdSet,
      final Long pidcVersionAttributeId) {
    return quotationRelevantUcAttrIdSet.contains(pidcVersionAttributeId);
  }

  /**
   * @param input
   * @param pidcVersionWithDetails
   * @param model
   * @throws DataException
   */
  private void fillPidcVersionInfo(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails, final PidcVersionAttributeModel model)
      throws DataException {
    if (input.isPidcVersionInfoNeeded()) {
      // get pidc version info
      getLogger().debug("Getting PIDC version info for pidc version ID : {}", input.getPidcVersionId());
      Map<Long, PidcVersionInfo> versionInfoMap =
          getActiveVersionWithStructureAttributes(new HashSet<>(Arrays.asList(model.getPidc().getId())));

      // set pidc version object
      pidcVersionWithDetails.setPidcVersionInfo(versionInfoMap.get(model.getPidcVersion().getId()) == null
          ? getPidcVersionWithStructureAttributes(model) : versionInfoMap.get(model.getPidcVersion().getId()));
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @param model
   * @throws DataException
   */
  private void fillAttrVarSubVarInfo(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails, final PidcVersionAttributeModel model)
      throws DataException {
    // fill variant map
    pidcVersionWithDetails.setPidcVariantMap(model.getVariantMap());

    // fill subvariant map
    pidcVersionWithDetails.setPidcSubVariantMap(model.getSubVariantMap());

    // fill pidcversion attribute map
    pidcVersionWithDetails.setPidcVersionAttributeDefinedMap(new HashMap<>(model.getPidcVersAttrMap()));
    pidcVersionWithDetails.setPidcVersionAttributeMap(model.getPidcVersAttrMap());

    // fill other attributes
    getLogger().debug("Fill PIDC Version attributes for pidc version ID : {}", input.getPidcVersionId());
    new PidcVersionAttributeLoader(getServiceData()).fillPidcVersionAttributes(pidcVersionWithDetails,
        input.getPidcVersionId());

    // fill pidcvariant attribute map
    pidcVersionWithDetails.setPidcVariantAttributeMap(model.getAllVariantAttributeMap());

    // fill pidcsubvariant attribute map
    pidcVersionWithDetails.setPidcSubVariantAttributeMap(model.getAllSubVariantAttrMap());


    // fill invisible attributes for pidc version level
    pidcVersionWithDetails.setPidcVersInvisibleAttrSet(model.getPidcVersInvisibleAttrSet());

    // fill invisible attributes for pidc variant level
    fillInvisibleAttrVarLevel(pidcVersionWithDetails, model);


    // fill all attributes for variant - including invisible attributes
    getLogger().debug("Fill PIDC variant attributes for pidc version ID : {}", input.getPidcVersionId());
    new PidcVariantAttributeLoader(getServiceData()).fillPidcVariantAttributes(pidcVersionWithDetails, model);

    // fill invisible attributes for pidc sub variant level
    fillInvisibleAttrSubVarLevel(pidcVersionWithDetails, model);

    // fill all attributers for subvariant - including invisible attributes
    getLogger().debug("Fill PIDC sub-variant attributes for pidc version ID : {}", input.getPidcVersionId());
    new PidcSubVariantAttributeLoader(getServiceData()).fillPidcSubVariantAttributes(pidcVersionWithDetails, model);

    // fill attribute values for invisible attributes
    getLogger().debug("Fill attribute values for invisible attributes for pidc version ID : {}",
        input.getPidcVersionId());
    setAttrValuesForInvisibleAttrs(model);

    // fill attribute values
    pidcVersionWithDetails.setAttributeValueMap(model.getRelevantAttrValueMap());
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @throws DataException
   */
  private void fillUsecaseModel(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails)
      throws DataException {
    if (input.isUsecaseModelNeeded()) {
      Long pidcId = pidcVersionWithDetails.getPidcVersionInfo().getPidc().getId();
      getLogger().debug("Fill project use case model for PIDC : {}", pidcId);
      pidcVersionWithDetails.setProjectUsecaseModel(new PidcLoader(getServiceData()).getProjectUsecaseModel(pidcId));
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @throws DataException
   */
  private void fillAllMandatoryAttributes(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails)
      throws DataException {
    if (input.isMandatoryAttrNeeded()) {
      // set all mandatory attributes
      getLogger().debug("set all mandatory attributes");
      Map<Long, Map<Long, MandatoryAttr>> mandatoryAttrMap =
          new MandatoryAttrLoader(getServiceData()).getAllMandatoryAttributesForAllAttributes();
      pidcVersionWithDetails.setMandatoryAttrMap(mandatoryAttrMap);
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @throws DataException
   */
  private void fillAttrDepnModel(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails)
      throws DataException {
    if (input.isAttrDepnModelNeeded()) {
      getLogger().debug("fill dependencies for all attributes");
      AttributeLoader attrLoader = new AttributeLoader(getServiceData());
      AttrNValueDependencyLoader depLoader = new AttrNValueDependencyLoader(getServiceData());
      // fill dependencies for all attributes
      AttrNValueDependencyDetails attrDependencyDetails = depLoader.fillAttrDependencyDetails();

      pidcVersionWithDetails.setAttrDependenciesMap(attrDependencyDetails.getAttrDependenciesMap());
      pidcVersionWithDetails.setAttrDepValMap(attrDependencyDetails.getDepAttrVals());
      pidcVersionWithDetails.setAttrRefDependenciesMap(attrDependencyDetails.getAttrRefDependenciesMap());
      pidcVersionWithDetails.setAttrDependenciesMapForAllAttr(attrDependencyDetails.getAttrDependenciesMapForAllAttr());
      // collect level attributes in a map
      Map<Long, PidcVersionAttribute> retMap = new HashMap<>();
      attrLoader.getAllLevelAttributeIds().forEach(
          (level, attrId) -> retMap.put(level, pidcVersionWithDetails.getPidcVersionAttributeMap().get(attrId)));
      pidcVersionWithDetails.setAllLevelAttrMap(retMap);
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @param model
   * @throws DataException
   */
  private void fillPredefinedAttrDepModel(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails, final PidcVersionAttributeModel model)
      throws DataException {
    if (input.isPredefinedAttrModelNeeded()) {
      getLogger().debug("fill predefined validity map");
      Map<Long, PredefinedValidity> predefValMap = new PredefinedValidityLoader(getServiceData()).getAll();
      pidcVersionWithDetails.setPredefinedValidityMap(predefValMap);

      getLogger().debug("fill all the predefined values corresponding to values");
      Map<Long, Map<Long, PredefinedAttrValue>> mapOfPredefinedVals = new HashMap<>();
      AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());
      for (AttributeValue attrValue : model.getRelevantAttrValueMap().values()) {
        Map<Long, PredefinedAttrValue> predefAttrValMap = new HashMap<>();
        // fill all the predefined values corresponding to values
        List<TPredefinedAttrValue> predefAttrValList =
            attributeValueLoader.getEntityObject(attrValue.getId()).gettGroupAttrValue();
        if (CommonUtils.isNotEmpty(predefAttrValList)) {
          AttributeLoader attrLoader = new AttributeLoader(getServiceData());
          PredefinedAttrValueLoader predefinedAttrValueLoader = new PredefinedAttrValueLoader(getServiceData());
          loadPredefinedAttrValMap(predefAttrValMap, predefAttrValList, attrLoader, predefinedAttrValueLoader);
        }
        if (!predefAttrValMap.isEmpty()) {
          mapOfPredefinedVals.put(attrValue.getId(), predefAttrValMap);
        }
      }
      pidcVersionWithDetails.setPreDefAttrValMap(mapOfPredefinedVals);
    }
  }


  /**
   * @param predefAttrValMap
   * @param predefAttrValList
   * @param attrLoader
   * @param predefinedAttrValueLoader
   * @throws DataException
   */
  private void loadPredefinedAttrValMap(final Map<Long, PredefinedAttrValue> predefAttrValMap,
      final List<TPredefinedAttrValue> predefAttrValList, final AttributeLoader attrLoader,
      final PredefinedAttrValueLoader predefinedAttrValueLoader)
      throws DataException {
    for (TPredefinedAttrValue tPredefinedAttrValue : predefAttrValList) {
      if (!attrLoader.getDataObjectByID(tPredefinedAttrValue.getPreDefinedAttr().getAttrId()).isDeleted()) {
        predefAttrValMap.put(tPredefinedAttrValue.getPreAttrValId(),
            predefinedAttrValueLoader.getDataObjectByID(tPredefinedAttrValue.getPreAttrValId()));
      }
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   */
  private void fillAttrLinks(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails) {
    if (input.isAttrLinksNeeded()) {
      // fill all links
      getLogger().debug("fill all links");
      Set<Long> linkSet = new LinkLoader(getServiceData()).getNodesWithLink(ApicConstants.ATTR_NODE_TYPE);
      pidcVersionWithDetails.setLinkSet(linkSet);
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @throws DataException
   */
  private void fillPidcDetStruct(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails)
      throws DataException {
    if (input.isPidcDetStructNeeded()) {
      // fill pidc details structure
      getLogger().debug("fill pidc details structure of pidc version ID : {}", input.getPidcVersionId());
      Map<Long, PidcDetStructure> pidcDetStructMap =
          new PidcVersionLoader(getServiceData()).getPidcDetStructure(input.getPidcVersionId());
      pidcVersionWithDetails.setPidcDetailsStructmap(pidcDetStructMap);
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @throws DataException
   */
  private void fillAttrGroupModel(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails)
      throws DataException {
    if (input.isAttrGroupModelNeeded()) {
      getLogger().debug("Fill all attribute super groups and groups");
      // create attribute group model to fetch attr groups and super groups
      AttrGroupModel groupModel = new AttrSuperGroupLoader(getServiceData()).getAttrGroupModel(true);

      // fill all attribute super groups
      pidcVersionWithDetails.setAllAttributeSuperGroup(groupModel.getAllSuperGroupMap());

      // fill all attribute groups
      pidcVersionWithDetails.setAllAttributeGroupMap(groupModel.getAllGroupMap());
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @param model
   * @throws DataException
   */
  private void fillCharAndAttrValLinks(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails, final PidcVersionAttributeModel model)
      throws DataException {
    if (input.isCharacteristicsModelNeeded()) {
      Set<Long> attValLinks = new HashSet<>();

      AttributeValueLoader attrValueLoader = new AttributeValueLoader(getServiceData());
      CharacteristicValueLoader charValueLoader = new CharacteristicValueLoader(getServiceData());

      // fill characteristic values
      getLogger().debug("Find characteristic values and link info for attribute values in pidc version ID : {}",
          input.getPidcVersionId());
      for (AttributeValue attrValue : model.getRelevantAttrValueMap().values()) {
        if (attrValue.getCharacteristicValueId() != null) {
          com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue charactersiticValue =
              charValueLoader.getDataObjectByID(attrValue.getCharacteristicValueId());
          pidcVersionWithDetails.getAllCharactersiticValueMap().put(charactersiticValue.getId(), charactersiticValue);
        }
        // check links available for attribute value id
        List<TLink> linkList = attrValueLoader.getEntityObject(attrValue.getId()).gettLinks();
        if ((linkList != null) && !linkList.isEmpty()) {
          attValLinks.add(attrValue.getId());
        }
      }
      // fill attr val ids with links
      pidcVersionWithDetails.setAttValLinks(attValLinks);

      // fill charactersitics
      getLogger().debug("fill charactersitics in pidc version ID : {}", input.getPidcVersionId());
      pidcVersionWithDetails.getAllCharacteristicMap()
          .putAll(new CharacteristicLoader(getServiceData()).getAttrMappedCharacteristics());
    }
  }


  /**
   * @param pidcVersionWithDetails
   * @param model
   */
  private void fillInvisibleAttrSubVarLevel(final PidcVersionWithDetails pidcVersionWithDetails,
      final PidcVersionAttributeModel model) {
    for (PidcSubVariant subVariant : model.getSubVariantMap().values()) {
      pidcVersionWithDetails.getSubVariantInvisbleAttributeMap().put(subVariant.getId(),
          model.getSubVariantInvisbleAttributeSet(subVariant.getId()));
    }
  }


  /**
   * @param pidcVersionWithDetails
   * @param model
   */
  private void fillInvisibleAttrVarLevel(final PidcVersionWithDetails pidcVersionWithDetails,
      final PidcVersionAttributeModel model) {
    for (PidcVariant variant : model.getVariantMap().values()) {
      pidcVersionWithDetails.getVariantInvisbleAttributeMap().put(variant.getId(),
          model.getVariantInvisbleAttributeSet(variant.getId()));
    }
  }


  /**
   * @param input
   * @param pidcVersionWithDetails
   * @throws DataException
   */
  private void fillAliasDefModel(final PidcVersionWithDetailsInput input,
      final PidcVersionWithDetails pidcVersionWithDetails)
      throws DataException {
    if (input.isAliasDefModelNeeded()) {
      Long pidcId = pidcVersionWithDetails.getPidcVersionInfo().getPidc().getId();

      getLogger().debug("Getting alias definition details for PIDC ID : {}", pidcId);
      // get alias defn details
      pidcVersionWithDetails.setAliasDefnModel(new AliasDefLoader(getServiceData()).getAliasDefnDetails(pidcId));
    }
  }


  /**
   * @param pidcVersionId Long
   * @return Map<Long, FocusMatrixVersion>
   * @throws DataException exception while creating data object
   */
  public Map<Long, FocusMatrixVersion> getFocusMatrixVersions(final Long pidcVersionId) throws DataException {
    Map<Long, FocusMatrixVersion> focusMatrixMap = new HashMap<>();
    TPidcVersion pidcVersion = getEntityObject(pidcVersionId);
    Set<TFocusMatrixVersion> tFocusMatrixVersionAttr = pidcVersion.getTFocusMatrixVersions();
    FocusMatrixVersionLoader focusMatrixLoader = new FocusMatrixVersionLoader(getServiceData());
    for (TFocusMatrixVersion tFocusMatrixAttr : tFocusMatrixVersionAttr) {
      focusMatrixMap.put(tFocusMatrixAttr.getFmVersId(),
          focusMatrixLoader.getDataObjectByID(tFocusMatrixAttr.getFmVersId()));
    }
    return focusMatrixMap;
  }

  /**
   * @param pidcVersId PIDC Version ID
   * @return the extend path - the tree structure of the PIDC
   * @throws DataException error while retrieving data
   */
  public String getPidcTreePath(final Long pidcVersId) throws DataException {
    StringBuilder ret = new StringBuilder();

    Map<Long, PidcVersionAttribute> structAttrMap =
        new PidcVersionAttributeLoader(getServiceData()).getStructureAttributes(pidcVersId);

    for (Long lvl : new TreeSet<>(structAttrMap.keySet())) {
      PidcVersionAttribute pAttr = structAttrMap.get(lvl);
      ret.append(pAttr.getValue()).append("->");
    }

    return ret.toString();
  }

  /**
   * @param pidcVersId PIDC Version ID
   * @return extended name of this PIDC Version
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long pidcVersId) throws DataException {
    final PidcVersion pidcVer = getDataObjectByID(pidcVersId);
    return EXTERNAL_LINK_TYPE.PIDC_VERSION.getTypeDisplayText() + ": " + getPidcTreePath(pidcVersId) +
        pidcVer.getName();
  }


  /**
   * Get all PIDC versions in the system, except those hidden to the user
   *
   * @return map of pidc versions. Key PIDC version ID, value data object
   * @throws DataException error while retrieving data
   */
  public Map<Long, PidcVersion> getAll() throws DataException {
    TypedQuery<TPidcVersion> query =
        getEntMgr().createNamedQuery(TPidcVersion.NQ_GET_ALL_PIDC_VERSIONS, TPidcVersion.class);
    Map<Long, PidcVersion> retMap = new HashMap<>();
    for (TPidcVersion tPidcVersion : query.getResultList()) {
      long pidcVersId = tPidcVersion.getPidcVersId();
      if (!isHiddenToCurrentUser(pidcVersId)) {
        retMap.put(pidcVersId, createDataObject(tPidcVersion));
      }
    }

    return retMap;

  }

  /**
   * Get all PIDC versions in the system, except those hidden to the user. Soap service migration to new framework
   * ProjectIdCardInfoType Equivalent object for Soap responce
   *
   * @return List of ProjectIdCardInfoType.
   * @throws DataException error while retrieving data
   */
  public List<ProjectIdCardAllVersInfoType> getAllPidcVersions() throws DataException {
    Map<Pidc, List<PidcVersion>> pidcVersionMap = fillPidcVersMap(getAll().values());
    PidcVersionAttributeLoader pidcVersionAttributeLoader = new PidcVersionAttributeLoader(getServiceData());
    List<ProjectIdCardAllVersInfoType> projIdCardInfoTypeList = new ArrayList<>();
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel = (new AttributeLoader(getServiceData())).getMaxStructAttrLevel();
    for (Entry<Pidc, List<PidcVersion>> dbProjectIdCardVer : pidcVersionMap.entrySet()) {
      Pidc pidCard = dbProjectIdCardVer.getKey();
      PidcVersion activeVersion = getActivePidcVersion(pidCard.getId());
      Map<Long, PidcVersionAttribute> structureAttributes =
          pidcVersionAttributeLoader.getStructureAttributes(activeVersion.getId());
      List<PidcVersion> pidcVersionsList = dbProjectIdCardVer.getValue();

      ProjectIdCardAllVersInfoType projectIdCardAllVersInfoType = new ProjectIdCardAllVersInfoType();

      String pidcName = pidCard.getName();

      // set the PIDC ID from active pidc Version Pidc
      projectIdCardAllVersInfoType.setId(pidCard.getId());

      // use the english name of the PIDC as the name
      projectIdCardAllVersInfoType.setName(pidcName);

      // get the PIDC version number
      projectIdCardAllVersInfoType.setVersionNumber(activeVersion.getProRevId());


      // get the PIDC version
      projectIdCardAllVersInfoType.setChangeNumber(pidCard.getVersion());

      // get deleted flag of the PIDC
      projectIdCardAllVersInfoType.setDeleted(activeVersion.isDeleted());

      // get create and modify user of the PIDC
      projectIdCardAllVersInfoType.setCreateUser(activeVersion.getCreatedUser());
      projectIdCardAllVersInfoType.setModifyUser(activeVersion.getModifiedUser());

      // get create and modify date of the PIDC
      projectIdCardAllVersInfoType.setCreateDate(activeVersion.getCreatedDate());
      projectIdCardAllVersInfoType.setModifyDate(activeVersion.getModifiedDate());

      // Add information regarding the clearing status
      String clearingStatus = pidCard.getClearingStatus();
      CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
      projectIdCardAllVersInfoType.setClearingStatus(clStatus.getUiText());

      // Attribute isCleraed()
      projectIdCardAllVersInfoType.setCleared(clStatus == CLEARING_STATUS.CLEARED);

      // Send the pidc Version Id rather than the Pidc ID
      projectIdCardAllVersInfoType.setLevelAttrInfoList(getLevelAttrInfo(structureAttributes, minLevel, maxLevel));

      fillPidcVersionType(pidCard, pidcVersionsList, projectIdCardAllVersInfoType);
      projIdCardInfoTypeList.add(projectIdCardAllVersInfoType);
    }
    return projIdCardInfoTypeList;
  }


  /**
   * @param pidCard
   * @param pidcVersionsList
   * @param projectIdCardInfoType
   */
  private void fillPidcVersionType(final Pidc pidCard, final List<PidcVersion> pidcVersionsList,
      final ProjectIdCardAllVersInfoType projectIdCardInfoType) {
    List<PidcVersionType> pidcVersionTypeList = new ArrayList<>();
    for (PidcVersion pidcVersion : pidcVersionsList) {
      PidcVersionType pidcVersionType = new PidcVersionType();
      pidcVersionType.setDescription(pidcVersion.getDescription());
      pidcVersionType.setDescriptionE(pidcVersion.getVersDescEng());
      pidcVersionType.setDescriptionG(pidcVersion.getVersDescGer());
      pidcVersionType.setActive(pidCard.getProRevId().equals(pidcVersion.getProRevId()));
      pidcVersionType.setChangeNumber(pidcVersion.getVersion());
      pidcVersionType.setLongName(pidcVersion.getVersionName());
      pidcVersionType.setPidcVersionId(pidcVersion.getId());
      pidcVersionType.setProRevId(pidcVersion.getProRevId());
      if (pidcVersion.getPidStatus() == null) {
        pidcVersionType.setVersionStatus("");
      }
      else {
        pidcVersionType.setVersionStatus(PidcVersionStatus.getStatus(pidcVersion.getPidStatus()).getUiStatus());
      }
      pidcVersionTypeList.add(pidcVersionType);
    }
    projectIdCardInfoType.setPidcVersionTypeList(pidcVersionTypeList);
  }

  /**
   * @param structureAttributes
   * @param minLevel
   * @param maxLevel
   * @return
   */
  private List<LevelAttrInfo> getLevelAttrInfo(final Map<Long, PidcVersionAttribute> structureAttributes,
      final long minLevel, final long maxLevel) {

    List<LevelAttrInfo> levelAttrInfoList = new ArrayList<>();

    for (Long level = minLevel; level <= maxLevel; level++) {

      PidcVersionAttribute structAttr = structureAttributes.get(level);

      LevelAttrInfo levelAttrInfo = new LevelAttrInfo();
      levelAttrInfo.setLevelNo(level);
      levelAttrInfo.setLevelAttrId(structAttr.getAttrId());
      levelAttrInfo.setLevelAttrValueId(structAttr.getValueId());
      levelAttrInfo.setLevelName(structAttr.getValue());
      levelAttrInfoList.add(levelAttrInfo);
    }
    return levelAttrInfoList;
  }


  private Map<Pidc, List<PidcVersion>> fillPidcVersMap(final Collection<PidcVersion> dbProjectIdVersions)
      throws DataException {
    Map<Pidc, List<PidcVersion>> pidcVersionMap = new ConcurrentHashMap<>();
    PidcLoader loader = new PidcLoader(getServiceData());
    Map<Long, List<PidcVersion>> pidcIdMap = new HashMap<>();
    for (PidcVersion pidcVersion : dbProjectIdVersions) {
      List<PidcVersion> pidcVersionList = new ArrayList<>();

      if (pidcIdMap.get(pidcVersion.getPidcId()) == null) {
        pidcIdMap.put(pidcVersion.getPidcId(), pidcVersionList);
        pidcVersionMap.put(loader.getDataObjectByID(pidcVersion.getPidcId()), pidcVersionList);
      }
      else {
        pidcVersionList = pidcIdMap.get(pidcVersion.getPidcId());
      }
      pidcVersionList.add(pidcVersion);
    }

    return pidcVersionMap;
  }


  public Map<Long, ExternalPidcVersionInfo> setExtPidcVersInfoMapDetails(final Map<Long, PidcVersionInfo> versInfoMap) {
    Map<Long, ExternalPidcVersionInfo> extversAttr = new HashMap<>();

    versInfoMap.forEach((id, obj) -> extversAttr.put(id, setExtPidcVersInfoDetails(obj)));

    return extversAttr;
  }


  public ExternalPidcVersionInfo setExtPidcVersInfoDetails(final PidcVersionInfo versAttr) {

    ExternalPidcVersionInfo extversAttr = new ExternalPidcVersionInfo();
    try {
      extversAttr.setHidden(isHiddenToCurrentUser(versAttr.getPidcVersion().getId()));
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);
    }
    extversAttr.setPidcVersion(versAttr.getPidcVersion());
    extversAttr.setPidc(versAttr.getPidc());
    extversAttr.setLevelAttrMap(versAttr.getLevelAttrMap());

    return extversAttr;

  }

  /**
   * @param model
   * @throws DataException
   */
  private void setAttrValuesForInvisibleAttrs(final PidcVersionAttributeModel model) throws DataException {

    AttributeValueLoader valLoader = new AttributeValueLoader(getServiceData());
    // pidc level
    for (Long attrId : model.getPidcVersInvisibleAttrSet()) {

      PidcVersionAttribute versAttr = model.getPidcVersAttrMap().get(attrId);
      if ((versAttr != null) && (versAttr.getValueId() != null) &&
          (model.getRelevantAttrValue(versAttr.getValueId()) == null)) {
        model.addRelevantAttrValue(valLoader.getDataObjectByID(versAttr.getValueId()));

      }
    }

    // variant level
    for (PidcVariant variant : model.getVariantMap().values()) {

      for (Long attrId : model.getVariantInvisbleAttributeSet(variant.getId())) {

        PidcVariantAttribute varAttr = model.getVariantAttribute(variant.getId(), attrId);
        if ((varAttr != null) && (varAttr.getValueId() != null) &&
            (model.getRelevantAttrValue(varAttr.getValueId()) == null)) {
          model.addRelevantAttrValue(valLoader.getDataObjectByID(varAttr.getValueId()));

        }
      }

    }

    // subvariant level
    for (PidcSubVariant subVariant : model.getSubVariantMap().values()) {

      for (Long attrId : model.getSubVariantInvisbleAttributeSet(subVariant.getId())) {

        PidcSubVariantAttribute subVarAttr = model.getSubVariantAttribute(subVariant.getId(), attrId);
        if ((subVarAttr != null) && (subVarAttr.getValueId() != null) &&
            (model.getRelevantAttrValue(subVarAttr.getValueId()) == null)) {
          model.addRelevantAttrValue(valLoader.getDataObjectByID(subVarAttr.getValueId()));
        }
      }

    }


  }

  /**
   * Validates if the current has read access rights to pidc version
   *
   * @param pidcVersId pidc version id
   * @throws IcdmException Validation errors
   */
  public void validatePidcVersionReadAccess(final Long pidcVersId) throws IcdmException {
    if (isHiddenToCurrentUser(pidcVersId)) {
      throw new UnAuthorizedAccessException("Insufficient access rights to PIDC Version with ID : " + pidcVersId);
    }
  }

  /**
   * @param pidcVersId
   * @return
   * @throws IcdmException
   */
  public Long getDivIdByPidcVersId(final Long pidcVersId) throws IcdmException {

    PidcVersionAttributeModel pidcVersAttrModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L1_PROJ_ATTRS);
    Long divAttrId =
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
    return pidcVersAttrModel.getPidcVersAttr(divAttrId).getValueId();
  }

  /**
   * Check is there exists multiple Focus Matrix Versions for the PIDC
   *
   * @return boolean true if the PIDC version has multiple Focus Matrix Versions, else false
   */
  public boolean hasMultipleFocusMatrixVersForPidcVers(final Long pidcVersId) {
    // check if there exist any other focus matrix version apart from the Working set
    return (getEntityObject(pidcVersId).getTFocusMatrixVersions().size() > 1);
  }


}

