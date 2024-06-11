/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Group;
import com.bosch.calmodel.a2ldata.module.system.constant.SystemConstant;
import com.bosch.caltool.a2l.jpa.bo.A2LBaseComponentFunctions;
import com.bosch.caltool.a2l.jpa.bo.A2LBaseComponents;
import com.bosch.caltool.a2l.jpa.bo.A2LResponsibility;
import com.bosch.caltool.a2l.jpa.bo.A2LSystemConstant;
import com.bosch.caltool.a2l.jpa.bo.A2LSystemConstantValues;
import com.bosch.caltool.a2l.jpa.bo.A2LWpResponsibility;
import com.bosch.caltool.a2l.jpa.bo.FCBCUsage;
import com.bosch.caltool.a2l.jpa.bo.FCToWP;
import com.bosch.caltool.a2l.jpa.bo.ICDMA2LGroup;
import com.bosch.caltool.a2l.jpa.bo.WPResponsibility;
import com.bosch.caltool.apic.jpa.bo.A2LFile;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.dmframework.bo.AbstractDataLoader;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.AbstractEntityProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.a2l.MvSdomSyscon;
import com.bosch.caltool.icdm.database.entity.a2l.MvSdomSysconValue;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;
import com.bosch.caltool.icdm.database.entity.a2l.TSdomBc;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;
import com.bosch.caltool.icdm.database.entity.a2l.Ta2lFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * A2L data provider.
 *
 * @author DMO5COB
 */
public class A2LDataProvider extends AbstractDataProvider {

  /**
   * fourth object count
   */
  private static final int OBJ_FOUR = 4;

  /**
   * third object count
   */
  private static final int OBJ_THREE = 3;

  /**
   * second object count
   */
  private static final int OBJ_TWO = 2;

  /**
   * Entity provider for A2L module
   */
  private final A2LEntityProvider entProvider;

  /**
   * all System constants in all modules
   */
  private final Map<String, A2LSystemConstant> systemConstMap;

  /**
   * All System constant values
   */
  private final Map<String, A2LSystemConstantValues> sysConstValueMap;
  /**
   * All BCs corresponding to an A2l File ICdm-949 Change from Sorted Set to Map
   */
  private final Map<Long, Map<String, A2LBaseComponents>> a2lBcMap;

  /**
   * Data cache for a2l module
   */
  private final A2LDataCache dataCache;

  // ICDM 531
  /**
   * APIC Data provider
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * Query parameter 1
   */
  private static final int QUERY_PARAM_1 = 1;

  /**
   * Query parameter 2
   */
  private static final int QUERY_PARAM_2 = 2;

  /**
   * Query parameter 1
   */
  private static final int QUERY_PARAM_3 = 3;

  /**
   * Root id not present so 0l
   */
  public static final Long ROOT_ID_NOT_PRESENT = 0L;

  /**
   * Constructor
   *
   * @param apicDataProvider apicDataProvider
   */
  public A2LDataProvider(final ApicDataProvider apicDataProvider) {

    this.entProvider = new A2LEntityProvider(getLogger(), A2LEntityType.values());

    this.apicDataProvider = apicDataProvider;

    this.dataCache = new A2LDataCache(this);

    // initialize the system constants Maps
    this.systemConstMap = new HashMap<String, A2LSystemConstant>();
    this.sysConstValueMap = new HashMap<String, A2LSystemConstantValues>();
    // ICdm-949 Change from Sorted Set to Map
    this.a2lBcMap = new HashMap<Long, Map<String, A2LBaseComponents>>();

    // Fetch all system constants info
    fetchAllSystemConstants();

  }

  /**
   * @return logger
   */
  @Override
  protected final ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

  /**
   * Fetch all system constants from DB
   */
  private void fetchAllSystemConstants() {

    getLogger().debug("fetching all system constants from database ...");

    Query qSysCon = getEm().createQuery("SELECT mv FROM MvSdomSyscon mv LEFT OUTER JOIN FETCH mv.mvSdomSysconValues ");
    setQueryHints(qSysCon);
    try {
      @SuppressWarnings("unchecked")
      List<MvSdomSyscon> dbSysConList = qSysCon.getResultList();
      for (MvSdomSyscon dbsysCon : dbSysConList) {
        // ICDM-2627
        if (this.systemConstMap.containsKey(dbsysCon.getSyskonName())) {
          // Already collected. Values are identified together with constants.
          continue;

        }
        createSysConObjects(dbsysCon);
      }

      getLogger().debug("System constants fetched from database: " + this.systemConstMap.size());
    }
    catch (NoResultException exp) {
      getLogger().error("No system constants found in database", exp);
    }
  }

  /**
   * @param pidcA2lID pidcA2lID
   * @param wpType wpType
   * @param wpRoot wpRoot
   * @return the a2lResp
   */
  @Deprecated
  public A2LResponsibility getA2lResp(final Long pidcA2lID, final Long wpType, final Long wpRoot) {
    Query typeQuery;
    if (wpRoot > ROOT_ID_NOT_PRESENT) {
      typeQuery = getEm().createNamedQuery(TA2lResp.FIND_QUERY_WITH_ROOT, TA2lResp.class);
      typeQuery.setParameter(QUERY_PARAM_3, wpRoot);
    }
    else {
      typeQuery = getEm().createNamedQuery(TA2lResp.FIND_QUERY_WITHOUT_ROOT, TA2lResp.class);
    }
    typeQuery.setParameter(QUERY_PARAM_1, pidcA2lID);
    typeQuery.setParameter(QUERY_PARAM_2, wpType);


    try {
      final List<Object[]> ta2lRep = typeQuery.getResultList();
      // Build BO for the result set
      if ((ta2lRep != null) && (ta2lRep.size() > 0)) {
        Object[] dbA2lRep = ta2lRep.get(0);
        return new A2LResponsibility(this, ((BigDecimal) dbA2lRep[0]).longValue());
      }
    }
    catch (Exception exp) {
      return null;
    }
    return null;
  }

  /**
   * @param dbsysCon
   */
  private void createSysConObjects(final MvSdomSyscon dbsysCon) {

    A2LSystemConstant sysCon =
        new A2LSystemConstant(this, dbsysCon.getSyskonName(), dbsysCon.getBezEn(), dbsysCon.getBezDe());

    for (MvSdomSysconValue valueObj : dbsysCon.getMvSdomSysconValues()) {
      // ICDM-2627
      sysCon.addSysConValue(
          new A2LSystemConstantValues(this, valueObj.getSyskonWert(), valueObj.getSyskonWertBeschreibung(), sysCon));
    }
    this.systemConstMap.put(sysCon.getSysconName(), sysCon);
  }


  /**
   * @return the dataCache
   */
  @Override
  public A2LDataCache getDataCache() {
    return this.dataCache;
  }

  /**
   * @return the entProvider
   */
  public A2LEntityProvider getEntProvider() {
    return this.entProvider;
  }

  /**
   * @return A2L System Constants map
   */
  public Map<String, A2LSystemConstant> getAllSysConstants() {
    return this.systemConstMap;
  }

  /**
   * @return A2L System Constant Values map
   */
  public Map<String, A2LSystemConstantValues> getAllSysConstValues() {
    return this.sysConstValueMap;
  }

  /**
   * @param sysConstSet system constants
   * @return A2LSystemConstantValues// ICDM-205
   */
  public SortedSet<A2LSystemConstantValues> getSystemConstantDetails(final Set<SystemConstant> sysConstSet) {

    // ICDM-2627
    SortedSet<A2LSystemConstantValues> retSet = new TreeSet<A2LSystemConstantValues>();

    String sysKonName;
    String sysKonVal;

    int newSysConst = 0;
    int newSysConstVal = 0;

    getLogger().debug("Retrieving ICDM system constants for a2l system constant objects");

    for (SystemConstant systemConstant : sysConstSet) {

      // Get system constant object
      sysKonName = systemConstant.getName();
      A2LSystemConstant a2lSysConst = this.systemConstMap.get(sysKonName);
      if (a2lSysConst == null) {
        // New system constant found
        getLogger().debug("New system constant found - {}", sysKonName);
        a2lSysConst = new A2LSystemConstant(this, sysKonName, "", "");
        this.systemConstMap.put(sysKonName, a2lSysConst);
        newSysConst++;
      }

      // Get value object
      sysKonVal = systemConstant.getValue();
      A2LSystemConstantValues a2lSysConstVal = a2lSysConst.getSysConValue(sysKonVal);
      if (a2lSysConstVal == null) {
        // new value found
        getLogger().debug("New system constant value found - {}; SysKon - {}", sysKonVal, sysKonName);
        a2lSysConstVal = new A2LSystemConstantValues(this, sysKonVal, "", a2lSysConst);
        a2lSysConst.addSysConValue(a2lSysConstVal);
        newSysConstVal++;
      }

      retSet.add(a2lSysConstVal);

    }

    getLogger().debug("Total iCDM system constants - {}; New system constants count - {}; New values count - {}",
        retSet.size(), newSysConst, newSysConstVal);

    return retSet;
  }


  /**
   * Method to retrieve usage of the FC
   *
   * @param fcName String
   * @return List<FCBCUsage>
   */
  public List<FCBCUsage> getFCUsage(final String fcName) {

    getLogger().debug("fetching fc usage for fc '" + fcName + "' ...");

    final String nativeQuery =
        "select fnc.name, fnc.functionversion, edst.Customer_Name, edst.Element_Name as VCDM_APRJ," +
            " min(edst.EaseeDst_Cre_User) from TA2L_Functions fnc, TA2L_Modules mods, TA2L_FileInfo finf," +
            " TABV_CalDataFiles cdf, TABE_DataSets edst where fnc.name = ? " +
            "and fnc.functionversion like '%.%.%' and nvl(edst.customer_name, ' ') <> '-- TEST --' " +
            "and fnc.module_id = mods.module_id and finf.id = mods.file_id and cdf.A2L_Info_ID = finf.ID " +
            "and cdf.EaseeDst_ID = edst.EaseeDst_ID group by fnc.name , fnc.functionversion ," +
            " edst.Customer_Name, edst.Element_Name order by fnc.name, fnc.functionversion," +
            " edst.Customer_Name, edst.Element_Name";
    final Query query = getEm().createNativeQuery(nativeQuery);
    query.setParameter(1, fcName);
    @SuppressWarnings("unchecked")
    final List<Object[]> resultList = query.getResultList();
    final List<FCBCUsage> fcbcUsages = convertToFCBCUsage(resultList);

    getLogger().debug("fc usage fetched : " + fcbcUsages.size());

    return fcbcUsages;
  }

  /**
   * Method to retrieve usage of the BC
   *
   * @param bcName String
   * @return List<FCBCUsage>
   */
  public List<FCBCUsage> getBCUsage(final String bcName) {

    getLogger().debug("fetching bc usage for bc '" + bcName + "' ...");

    final String nativeQuery = "select sbc.name, sbc.Variant, edst.Customer_Name, edst.Element_Name as VCDM_APRJ," +
        " min(edst.EaseeDst_Cre_User) from T_SDOM_BCs sbc, TA2L_BCs abc, TA2L_Modules mods," +
        " TA2L_FileInfo finf, TABV_CalDataFiles cdf, TABE_DataSets edst" + " where sbc.name = ? and" +
        " nvl(edst.customer_name, ' ') <> '-- TEST --' and " +
        "sbc.ID = abc.SDOM_BC_ID and abc.module_id = mods.module_id and finf.id = mods.file_id and " +
        "cdf.A2L_Info_ID = finf.ID and cdf.EaseeDst_ID = edst.EaseeDst_ID group by sbc.name, " +
        "sbc.Variant, edst.Customer_Name, edst.Element_Name order by sbc.name, sbc.Variant, " +
        "edst.Customer_Name, edst.Element_Name";
    final Query query = getEm().createNativeQuery(nativeQuery);
    query.setParameter(1, bcName);
    @SuppressWarnings("unchecked")
    final List<Object[]> resultList = query.getResultList();
    final List<FCBCUsage> fcbcUsages = convertToFCBCUsage(resultList);

    getLogger().debug("bc usage fetched : " + fcbcUsages.size());

    return fcbcUsages;
  }

  /**
   * @param resultList
   * @return
   */
  private List<FCBCUsage> convertToFCBCUsage(final List<Object[]> resultList) {
    final List<FCBCUsage> fcbcUsageList = new ArrayList<FCBCUsage>();
    FCBCUsage fcbcUsage;
    String val;
    for (Object[] objects : resultList) {

      fcbcUsage = new FCBCUsage(this.apicDataProvider);
      for (int i = 0; i < objects.length; i++) {
        val = ApicConstants.EMPTY_STRING;
        if (objects[i] != null) {
          val = objects[i].toString();
        }
        if (i == 0) {
          fcbcUsage.setName(val);
        }
        else if (i == 1) {
          fcbcUsage.setVersion(val);
        }
        else if (i == OBJ_TWO) {
          fcbcUsage.setCustomerName(val);
        }
        else if (i == OBJ_THREE) {
          fcbcUsage.setVcdmAprj(val);
        }
        else if (i == OBJ_FOUR) {
          fcbcUsage.setCreatedUser(val);
        }
      }
      fcbcUsageList.add(fcbcUsage);
    }
    return fcbcUsageList;
  }


  /**
   * Icdm-383
   *
   * @param a2lFile - a2l file
   * @return // ICDM-204
   */
  public SortedSet<A2LBaseComponents> getA2lBCInfo(final A2LFile a2lFile) {
    return new TreeSet<A2LBaseComponents>(getA2lBCMapInfo(a2lFile).values());
  }


  /**
   * Icdm-949 new map for A2lBcObj
   *
   * @param a2lFile a2lFile
   * @return a Hash Map Contaning Bc name as Key and A2LBaseComponent as Value
   */
  public Map<String, A2LBaseComponents> getA2lBCMapInfo(final A2LFile a2lFile) {
    if (this.a2lBcMap.containsKey(a2lFile.getA2LFileID())) {
      return this.a2lBcMap.get(a2lFile.getA2LFileID());
    }
    // Fetch All the BC Information for the A2l File
    final Map<BigDecimal, A2LBaseComponents> allBcData = fetchAllBc(a2lFile);
    // Fetch the Fc values For the Bc's
    fetchBCAndFunctions(a2lFile, allBcData);
    return this.a2lBcMap.get(a2lFile.getA2LFileID());
  }

  /**
   * Query For Fetching all the Bc's For the A2l File by passing the File Id
   *
   * @param a2lFile
   * @return all the Bc's Icdm-383
   */

  private Map<BigDecimal, A2LBaseComponents> fetchAllBc(final A2LFile a2lFile) {

    getLogger().debug("fetching the Bc Information ...");
    final TypedQuery<TSdomBc> qbc = getEm().createQuery("select bc from TSdomBc bc,Ta2lBc b,Ta2lModule module " +
        "where  bc.id = b.sdomBcId and module.moduleId=b.moduleId and module.fileId= :FILEID", TSdomBc.class);
    qbc.setParameter("FILEID", BigDecimal.valueOf(a2lFile.getA2LFileID()));
    setQueryHints(qbc);

    return allBcData(qbc.getResultList());
  }


  /**
   * Icdm-383 Add All the Bc's of the A2l File in a Map
   *
   * @param listfn
   * @param a2lFile
   * @return all the Bc data
   */
  @Deprecated
  private Map<BigDecimal, A2LBaseComponents> allBcData(final List<TSdomBc> listBc) {
    final Map<BigDecimal, A2LBaseComponents> bcMap = new HashMap<BigDecimal, A2LBaseComponents>();
    for (TSdomBc ta2lFcBc : listBc) {
      A2LBaseComponents bcObj = new A2LBaseComponents(ta2lFcBc.getName(), ta2lFcBc.getVariant(), ta2lFcBc.getRevision(),
          ta2lFcBc.getLifecycleState(), ta2lFcBc.getDescription());
      bcMap.put(BigDecimal.valueOf(ta2lFcBc.getId()), bcObj);
    }
    getLogger().debug("Bc Information fetched : " + bcMap.size());
    return bcMap;
  }

  /**
   * Method does DB fetch of the Bc's Which has FC's Icdm-383
   *
   * @param a2lFile the File Selected
   * @param allBcData
   */
  @SuppressWarnings("unchecked")
  private void fetchBCAndFunctions(final A2LFile a2lFile, final Map<BigDecimal, A2LBaseComponents> allBcData) {
    getLogger().debug("fetching the BC-FC information ...");
    final Query qfns = getEm().createQuery("SELECT sbc,fn FROM Ta2lFunction fn ,Ta2lBc bc, " +
        "TSdomBc sbc , Ta2lModule module where sbc.id = bc.sdomBcId and bc.id=fn.a2lBcId " +
        "and bc.moduleId=fn.moduleId and module.moduleId=bc.moduleId and module.fileId= :FILEID");
    qfns.setParameter("FILEID", BigDecimal.valueOf(a2lFile.getA2LFileID()));
    setQueryHints(qfns);
    loadFcBcData(qfns.getResultList(), a2lFile, allBcData);
  }


  /**
   * Method puts the DB data into Fc,Bc data model Icdm-383
   *
   * @param listfn listfn
   * @param a2lFile a2lFile
   * @param allBcData Bc Data
   */
  private void loadFcBcData(final List<Object[]> listfn, final A2LFile a2lFile,
      final Map<BigDecimal, A2LBaseComponents> allBcData) {
    // Icdm-949 changed to Concurrent HAshMap from Sorted Set
    final Map<String, A2LBaseComponents> newBCMap = new ConcurrentHashMap<String, A2LBaseComponents>();
    // Map For Bc Objects
    Map<BigDecimal, A2LBaseComponents> bcMap = allBcData;
    // Map For FC Objects
    Map<BigDecimal, A2LBaseComponentFunctions> fcMap = new HashMap<BigDecimal, A2LBaseComponentFunctions>();
    A2LBaseComponentFunctions fcObj;
    A2LBaseComponents bcObj;
    TSdomBc bcData;
    Ta2lFunction fcData;
    // Add all the Bc's which are having the Fc's
    for (Object[] ta2lFcBc : listfn) {
      bcData = (TSdomBc) ta2lFcBc[0];
      fcData = (Ta2lFunction) ta2lFcBc[1];
      bcObj = bcMap.get(bcData.getId());
      // Create Fc's For the Bc
      fcObj = fcMap.get(fcData.getFunctionId());
      if (fcObj == null) {
        fcObj = new A2LBaseComponentFunctions(fcData.getFunctionId(), fcData.getFunctionversion(),
            fcData.getLongidentifier(), fcData.getModuleId(), fcData.getName(), fcData.getA2lBcId());
        fcMap.put(fcData.getFunctionId(), fcObj);
      }
      bcObj.getFunctionMap().put(fcObj.getName(), fcObj);
      newBCMap.put(bcObj.getBcName(), bcObj);
    }

    // Add the Bc Objects Which do not have the FC's
    for (A2LBaseComponents a2lBcObj : bcMap.values()) {
      newBCMap.put(a2lBcObj.getBcName(), a2lBcObj);
    }
    this.a2lBcMap.put(a2lFile.getA2LFileID(), newBCMap);
    getLogger().debug("BC-FC information fetched : " + this.a2lBcMap.size());
  }

  /**
   * @param qBC
   */
  private void setQueryHints(final Query qBC) {
    qBC.setHint(A2lJPAConstants.READ_ONLY, "true");
    qBC.setHint(A2lJPAConstants.FETCH_SIZE, "5000");
    qBC.setHint(A2lJPAConstants.STORE_MODE, "REFRESH");
    qBC.setHint(A2lJPAConstants.SHARED_CACHE, "false");
  }


  /**
   * @param pidcVer pidcard version
   * @param functionSet fs
   * @param a2lParserGroupSet a2lParserGroupSet
   * @param apicDP apicDataProvider
   * @param a2ledDataProvider a2leditorDataProvider
   */
  // ICDM-209 and ICDM-210 starts
  public void fetchWpInformation(final PIDCVersion pidcVer, final SortedSet<Function> functionSet,
      final SortedSet<Group> a2lParserGroupSet, final ApicDataProvider apicDP,
      final A2LEditorDataProvider a2ledDataProvider, final FC2WPMappingWithDetails mappingByVersion) {

    a2ledDataProvider.setMappingSourceID(null);

    Long pidcWpAttrVal;
    PIDCAttribute wpTypeAttr = getPidcWpAttr(pidcVer);
    if ((wpTypeAttr != null) && (wpTypeAttr.getAttributeValue() != null) &&
        (ApicUtil.compare(wpTypeAttr.getIsUsed(), ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()) == 0)) {
      pidcWpAttrVal = wpTypeAttr.getAttributeValue().getValueID();
      a2ledDataProvider.setMappingSourceID(pidcWpAttrVal);

      setFcWpData(pidcVer, functionSet, a2lParserGroupSet, this.apicDataProvider, a2ledDataProvider, mappingByVersion);

    }
  }

  /**
   * @param pidcVer pidcVerion
   * @return the pidc attr
   */
  public PIDCAttribute getPidcWpAttr(final PIDCVersion pidcVer) {
    Map<Long, PIDCAttribute> pidcattrMap = pidcVer.getAttributes(false);
    long wpAttrId = Long.valueOf(this.apicDataProvider.getParameterValue(ApicConstants.WP_TYPE_ATTR_ID));
    PIDCAttribute wpTypeAttr = pidcattrMap.get(wpAttrId);
    return wpTypeAttr;
  }

  /**
   * @param a2lFile
   * @param pidcVer
   * @param functionSet
   * @param a2lParserGroupSet
   * @param apicDataProvider
   * @param a2leditorDataProvider
   * @param fcToWpValueIdOne
   * @param fcToWpValueIdTwo
   */
  private void setFcWpData(final PIDCVersion pidcVer, final SortedSet<Function> functionSet,
      final SortedSet<Group> a2lParserGroupSet, final ApicDataProvider apicDataProvider,
      final A2LEditorDataProvider a2ledDataProvider, final FC2WPMappingWithDetails mappingByVersion) {
    List<FCToWP> fcToWpList;


    if (Long.valueOf(apicDataProvider.getParameterValue(ApicConstants.GROUP_MAPPING_ID))
        .equals(a2ledDataProvider.getMappingSourceID())) {
      a2ledDataProvider.fetchFromGroupMapping(a2lParserGroupSet, pidcVer, apicDataProvider);
    }
    else {
      fcToWpList = fetchFcToWp(mappingByVersion);
      a2ledDataProvider.fetchWpValues(fcToWpList, functionSet, getLanguage());
    }

  }

  /**
   * @param functionSet
   * @param string
   * @return
   */
  private List<FCToWP> fetchFcToWp(final FC2WPMappingWithDetails mappingByVersion) {

    final List<FCToWP> fcToWpList = new ArrayList<FCToWP>();
    if (null != mappingByVersion) {
      Collection<FC2WPMapping> fc2wpValues = mappingByVersion.getFc2wpMappingMap().values();
      FCToWP fcwp;
      for (FC2WPMapping fc2Wp : fc2wpValues) {
        WorkPackageDivision wpDetail = mappingByVersion.getWpDetMap().get(fc2Wp.getWpDivId());
        if (wpDetail != null) {
          User user1 = mappingByVersion.getUserMap().get(fc2Wp.getContactPersonId());
          String contact1 = "", contact2 = "";
          if (user1 != null) {
            contact1 = user1.getFirstName();
          }
          User user2 = mappingByVersion.getUserMap().get(fc2Wp.getContactPersonSecondId());

          if (user2 != null) {
            contact2 = user2.getFirstName();
          }

          String wpResource = null == wpDetail.getWpResource() ? "" : wpDetail.getWpResource();
          fcwp = new FCToWP(getLanguage(), fc2Wp.getWpDivId(), contact1, contact2, null, wpResource,
              wpDetail.getWpName().trim(), null, wpDetail.getWpIdMcr(), fc2Wp.getFunctionName());
          fcToWpList.add(fcwp);
        }
      }
    }
    return fcToWpList;
  }


  /**
   * Get the current language
   *
   * @return Language
   */
  public Language getLanguage() {
    return getApicDataProvider().getLanguage();
  }

  /**
   * @return entity manager
   */
  private EntityManager getEm() {
    return this.entProvider.getEm();
  }

  /**
   * @return the apicDataProvider
   */
  public ApicDataProvider getApicDataProvider() {
    return this.apicDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractEntityProvider getEntityProvider() {
    return this.entProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractDataLoader getDataLoader() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param a2lRespID a2lRespID
   * @return the list of A2LWpResponsibility objects
   */
  public SortedSet<A2LWpResponsibility> getA2lWpResp(final Long a2lRespID) {
    SortedSet<A2LWpResponsibility> a2lRespSet = new TreeSet<>();

    final String query =
        "SELECT a2lwpResp from TA2lWpResp a2lwpResp where a2lwpResp.TA2lResp.a2lRespId = '" + a2lRespID + "'";
    final TypedQuery<TA2lWpResp> typeQuery = getEm().createQuery(query, TA2lWpResp.class);
    typeQuery.setHint("eclipselink.refresh", "true");
    final List<TA2lWpResp> ta2lWpRespList = typeQuery.getResultList();

    for (TA2lWpResp ta2lWpResp : ta2lWpRespList) {
      A2LWpResponsibility respObj = new A2LWpResponsibility(this, ta2lWpResp.getA2lWpRespId());
      a2lRespSet.add(respObj);
      addA2lRespWpToCache(a2lRespID, respObj);
    }
    return a2lRespSet;
  }

  /**
   * @param a2lRespID
   * @param respObj
   */
  private void addA2lRespWpToCache(final Long a2lRespID, final A2LWpResponsibility respObj) {
    SortedSet<A2LWpResponsibility> a2lWpRespSet = this.dataCache.getA2lWpRespMap().get(a2lRespID);
    if (a2lWpRespSet == null) {
      a2lWpRespSet = new TreeSet<>();

    }
    a2lWpRespSet.add(respObj);
    this.dataCache.getA2lWpRespMap().put(a2lRespID, a2lWpRespSet);
  }

  /**
   * @return the map of Long, Wp responsbility
   */
  public Map<Long, WPResponsibility> fetchAllWpResp() {
    final String query = "SELECT wpResp from TWpResp wpResp";
    final TypedQuery<TWpResp> typeQuery = getEm().createQuery(query, TWpResp.class);
    final List<TWpResp> ta2lWpRespList = typeQuery.getResultList();

    for (TWpResp ta2lWpResp : ta2lWpRespList) {
      new WPResponsibility(this, ta2lWpResp.getRespId());
    }
    return this.dataCache.getWpRespMap();
  }

  /**
   * @param pname String
   * @param pType String
   * @return TParameter
   */
  public TParameter fetchTParameter(final String pname, final String pType) {
    final String query =
        "SELECT tparam from TParameter tparam where tparam.name= '" + pname + "' and tparam.ptype='" + pType + "'";
    final TypedQuery<TParameter> typeQuery = getEm().createQuery(query, TParameter.class);

    TParameter resultParam = typeQuery.getSingleResult();
    return resultParam;
  }

  /**
   * @param a2lId a2lId
   * @return the list of icdm a2l grp
   */
  public List<ICDMA2LGroup> fetchA2lGrp(final Long a2lId) {

    List<ICDMA2LGroup> icdmA2lGrpList = new ArrayList<>();
    final String query = "SELECT a2lGrp from TA2lGroup a2lGrp where a2lGrp.a2lId=  '" + a2lId + "'";

    final TypedQuery<TA2lGroup> typeQuery = getEm().createQuery(query, TA2lGroup.class);
    final List<TA2lGroup> ta2lWpRespList = typeQuery.getResultList();

    for (TA2lGroup ta2lGrp : ta2lWpRespList) {
      ICDMA2LGroup respObj = new ICDMA2LGroup(this, ta2lGrp.getGroupId());
      icdmA2lGrpList.add(respObj);
    }
    return icdmA2lGrpList;

  }


}