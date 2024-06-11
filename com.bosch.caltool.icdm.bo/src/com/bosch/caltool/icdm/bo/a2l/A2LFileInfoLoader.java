/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.a2l.MvSdomSyscon;
import com.bosch.caltool.icdm.database.entity.a2l.MvSdomSysconValue;
import com.bosch.caltool.icdm.database.entity.a2l.TSdomBc;
import com.bosch.caltool.icdm.database.entity.a2l.Ta2lFunction;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lFileinfo;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lVcdmVersion;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.caltool.icdm.model.a2l.VCDMA2LFileDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;

/**
 * Load A2L file info based on A2l FileId. Uses Serialization to cache A2LFileInfo.
 *
 * @author gge6cob
 */
public class A2LFileInfoLoader extends AbstractBusinessObject<A2LFile, MvTa2lFileinfo> {

  /**
   * Instantiates a new a2L file info loader.
   *
   * @param inputData serviceData
   */
  public A2LFileInfoLoader(final ServiceData inputData) {
    super(inputData, "A2L File", MvTa2lFileinfo.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2LFile createDataObject(final MvTa2lFileinfo entity) throws DataException {
    A2LFile object = new A2LFile();

    object.setId(entity.getId());
    object.setAsap2version(entity.getAsap2version());
    object.setA2lfilesize(entity.getA2lfilesize());
    object.setA2lfilechecksum(entity.getA2lfilechecksum());
    object.setHeaderversion(entity.getHeaderversion());
    object.setProjectlongidentifier(entity.getProjectlongidentifier());
    object.setFilename(entity.getFilename());
    object.setHeadercomment(entity.getHeadercomment());
    object.setHeaderprojectno(entity.getHeaderprojectno());
    object.setProjectname(entity.getProjectname());
    object.setSdomPverName(entity.getSdomPverName());
    object.setSdomPverVariant(entity.getSdomPverVariant());
    object.setSdomPverVersid(entity.getSdomPverVersid());
    object.setVcdmA2lfileId(entity.getVcdmA2lfileId());
    object.setSdomPverRevision(entity.getSdomPverRevision());
    object.setFiledate(timestamp2String(entity.getFiledate()));
    object.setNumCompli(entity.getNumCompli());
    object.setNumParam(entity.getNumParam());
    return object;
  }


  /**
   * Gets the a 2 l base components.
   *
   * @param a2lFileId the a 2 l file id
   * @return the a 2 l base components
   */
  public Map<String, A2LBaseComponents> getA2lBaseComponents(final Long a2lFileId) {
    // BC
    final List<TSdomBc> listBc = fetchAllBc(a2lFileId);
    Map<Long, A2LBaseComponents> bcMap = getAllBcData(listBc);

    // FC
    final List<Object[]> listfn = fetchBCAndFunctions(a2lFileId);

    // BC-FC
    Map<String, A2LBaseComponents> bcFcMap = loadFcBcData(listfn, bcMap);

    return bcFcMap;
  }

  /**
   * Query For Fetching all the Bc's For the A2l File by passing the File Id.
   *
   * @param a2lFileId the a 2 l file id
   * @return all the Bc's Icdm-383
   */

  private List<TSdomBc> fetchAllBc(final Long a2lFileId) {
    getLogger().debug("fetching the Bc Information ...");
    final TypedQuery<TSdomBc> qbc = getEntMgr().createQuery("select bc from TSdomBc bc,Ta2lBc b,Ta2lModule module " +
        "where  bc.id = b.sdomBcId and module.moduleId=b.moduleId and module.fileId= :FILEID", TSdomBc.class);
    qbc.setParameter("FILEID", BigDecimal.valueOf(a2lFileId));
    setQueryHints(qbc);
    return qbc.getResultList();
  }

  /**
   * Method to retrieve usage of the BC
   *
   * @param bcName String
   * @return List<FCBCUsage>
   */
  public List<FCBCUsage> getBCUsage(final String bcName) {
    getLogger().debug("fetching bc usage for bc '" + bcName + "' ...");

    final Query query = getEntMgr().createNativeQuery(
        "select sbc.name, sbc.Variant, edst.aprj_version_num, edst.Customer_Name, edst.Element_Name as VCDM_APRJ," +
            " min(edst.EaseeDst_Cre_User) from T_SDOM_BCs sbc, TA2L_BCs abc, TA2L_Modules mods," +
            " TA2L_FileInfo finf, TABV_CalDataFiles cdf, TABE_DataSets edst" + " where sbc.name = ? and" +
            " nvl(edst.customer_name, ' ') <> '-- TEST --' and " +
            "sbc.ID = abc.SDOM_BC_ID and abc.module_id = mods.module_id and finf.id = mods.file_id and " +
            "cdf.A2L_Info_ID = finf.ID and cdf.EaseeDst_ID = edst.EaseeDst_ID group by sbc.name, edst.aprj_version_num, " +
            "sbc.Variant, edst.Customer_Name, edst.Element_Name order by sbc.name, sbc.Variant, " +
            "edst.aprj_version_num, edst.Customer_Name, edst.Element_Name");
    query.setParameter(1, bcName);

    @SuppressWarnings("unchecked")
    final List<Object[]> resultList = query.getResultList();
    final List<FCBCUsage> fcbcUsageList = new ArrayList<>();
    FCBCUsage fcbcUsage;
    String val;
    UserLoader userLoader = new UserLoader(getServiceData());
    for (Object[] objects : resultList) {
      fcbcUsage = new FCBCUsage();
      fcbcUsage.setName(objects[0] == null ? null : objects[0].toString());
      fcbcUsage.setFuncVersion(objects[1] == null ? null : objects[1].toString());
      fcbcUsage.setVcdmAprjId(objects[2] == null ? null : objects[2].toString());
      fcbcUsage.setCustomerName(objects[3] == null ? null : objects[3].toString());
      fcbcUsage.setVcdmAprj(objects[4] == null ? null : objects[4].toString());
      val = objects[5] == null ? null : objects[5].toString();
      if (CommonUtils.isNotNull(val)) {
        fcbcUsage.setCreatedUser(getCreatedUser(val, userLoader));
      }
      fcbcUsageList.add(fcbcUsage);
    }
    return fcbcUsageList;
  }

  /**
   * Method to retrieve usage of the FC
   *
   * @param fcName String
   * @return List<FCBCUsage>
   */
  public List<FCBCUsage> getFCUsage(final String fcName) {
    getLogger().debug("fetching fc usage for fc '" + fcName + "' ...");

    final Query query = getEntMgr().createNativeQuery(
        "select fnc.name, fnc.functionversion, edst.aprj_version_num ,edst.Customer_Name, edst.Element_Name as VCDM_APRJ," +
            " min(edst.EaseeDst_Cre_User) from TA2L_Functions fnc, TA2L_Modules mods, TA2L_FileInfo finf," +
            " TABV_CalDataFiles cdf, TABE_DataSets edst where fnc.name = ? " +
            "and fnc.functionversion like '%.%.%' and nvl(edst.customer_name, ' ') <> '-- TEST --' " +
            "and fnc.module_id = mods.module_id and finf.id = mods.file_id and cdf.A2L_Info_ID = finf.ID " +
            "and cdf.EaseeDst_ID = edst.EaseeDst_ID group by fnc.name ,edst.aprj_version_num, fnc.functionversion ," +
            " edst.Customer_Name, edst.Element_Name order by fnc.name,edst.aprj_version_num, fnc.functionversion," +
            " edst.Customer_Name, edst.Element_Name");
    query.setParameter(1, fcName);

    @SuppressWarnings("unchecked")
    final List<Object[]> resultList = query.getResultList();
    final List<FCBCUsage> fcbcUsageList = new ArrayList<>();
    FCBCUsage fcbcUsage;
    String val;
    UserLoader userLoader = new UserLoader(getServiceData());
    for (Object[] objects : resultList) {
      fcbcUsage = new FCBCUsage();
      fcbcUsage.setName(objects[0] == null ? null : objects[0].toString());
      fcbcUsage.setFuncVersion(objects[1] == null ? null : objects[1].toString());
      fcbcUsage.setVcdmAprjId(objects[2] == null ? null : objects[2].toString());
      fcbcUsage.setCustomerName(objects[3] == null ? null : objects[3].toString());
      fcbcUsage.setVcdmAprj(objects[4] == null ? null : objects[4].toString());
      val = objects[5] == null ? null : objects[5].toString();
      if (CommonUtils.isNotNull(val)) {
        fcbcUsage.setCreatedUser(getCreatedUser(val, userLoader));
      }
      fcbcUsageList.add(fcbcUsage);
    }
    return fcbcUsageList;
  }

  /**
   * @param vcdmA2lFileID Long
   * @return List<VCDMApplicationProject>
   */
  public List<VCDMApplicationProject> getDataSets(final Long vcdmA2lFileID) {
    // query to fetch the contents of TA2L_FileInfo for the given vCDM A2L FileID to get the SDOM PVER VersID
    // there can be only one entry for a particular vCDM A2L FileID !
    final String qsSdomPverVersID = "select afi1 from MvTa2lFileinfo afi1 where afi1.vcdmA2lfileId = :vcdmA2lfileId";

    final TypedQuery<MvTa2lFileinfo> qSdomPverVersID = getEntMgr().createQuery(qsSdomPverVersID, MvTa2lFileinfo.class);
    // set the parameter
    qSdomPverVersID.setParameter("vcdmA2lfileId", BigDecimal.valueOf(vcdmA2lFileID));
    // execute the query
    final List<MvTa2lFileinfo> fileInfos = qSdomPverVersID.getResultList();

    VcdmDataSetLoader vCDMdatasetLoader = new VcdmDataSetLoader(getServiceData());
    return vCDMdatasetLoader.getDataSets(fileInfos);

  }

  /**
   * @param fcbcUsage
   * @param val
   * @param userLoader
   * @return String display name of created user
   */
  private String getCreatedUser(final String val, final UserLoader userLoader) {
    try {
      User user = userLoader.getDataObjectByUserName(val);
      return user.getDescription();
    }
    catch (Exception e) {
      getLogger().error("User not found" + val, e);
      return val;
    }
  }

  /**
   * Icdm-383 Add All the Bc's of the A2l File in a Map.
   *
   * @param listBc the list bc
   * @return all the Bc data
   */
  private Map<Long, A2LBaseComponents> getAllBcData(final List<TSdomBc> listBc) {
    final Map<Long, A2LBaseComponents> bcMap = new HashMap<>();
    for (TSdomBc ta2lFcBc : listBc) {
      A2LBaseComponents bcObj = new A2LBaseComponents();
      bcObj.setBcName(ta2lFcBc.getName());
      bcObj.setBcVersion(ta2lFcBc.getVariant());
      bcObj.setRevision(ta2lFcBc.getRevision());
      bcObj.setState(ta2lFcBc.getLifecycleState());
      bcObj.setLongName(ta2lFcBc.getDescription());

      bcMap.put(ta2lFcBc.getId(), bcObj);
    }
    getLogger().debug("Bc Information fetched : " + bcMap.size());
    return bcMap;
  }

  /**
   * Method does DB fetch of the Bc's Which has FC's Icdm-383.
   *
   * @param a2lFileId the a 2 l file id
   * @return the list
   */
  private List<Object[]> fetchBCAndFunctions(final Long a2lFileId) {
    getLogger().debug("fetching the BC-FC information ...");
    final Query qfns = getEntMgr().createQuery("SELECT sbc,fn FROM Ta2lFunction fn ,Ta2lBc bc, " +
        "TSdomBc sbc , Ta2lModule module where sbc.id = bc.sdomBcId and bc.id=fn.a2lBcId " +
        "and bc.moduleId=fn.moduleId and module.moduleId=bc.moduleId and module.fileId= :FILEID");
    qfns.setParameter("FILEID", BigDecimal.valueOf(a2lFileId));
    setQueryHints(qfns);
    return qfns.getResultList();
  }


  /**
   * Method puts the DB data into Fc,Bc data model Icdm-383.
   *
   * @param listfn listfn
   * @param a2lFileId the a 2 l file id
   * @param allBcData Bc Data
   * @return the map
   */
  private Map<String, A2LBaseComponents> loadFcBcData(final List<Object[]> listfn,
      final Map<Long, A2LBaseComponents> allBcData) {
    // Icdm-949 changed to Concurrent HAshMap from Sorted Set
    final Map<String, A2LBaseComponents> newBCMap = new ConcurrentHashMap<>();
    // Map For Bc Objects
    Map<Long, A2LBaseComponents> bcMap = allBcData;
    // Map For FC Objects
    Map<BigDecimal, A2LBaseComponentFunctions> fcMap = new HashMap<>();
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
        fcObj = new A2LBaseComponentFunctions();
        fcObj.setFunctionId(fcData.getFunctionId());
        fcObj.setFunctionversion(fcData.getFunctionversion());
        fcObj.setLongidentifier(fcData.getLongidentifier());
        fcObj.setModuleId(fcData.getModuleId());
        fcObj.setName(fcData.getName());
        fcObj.setSdomBcId(fcData.getA2lBcId());
        fcMap.put(fcData.getFunctionId(), fcObj);
      }
      bcObj.getFunctionMap().put(fcObj.getName(), fcObj);
      newBCMap.put(bcObj.getBcName(), bcObj);
    }

    // Add the Bc Objects Which do not have the FC's
    for (A2LBaseComponents a2lBcObj : bcMap.values()) {
      newBCMap.put(a2lBcObj.getBcName(), a2lBcObj);
    }
    getLogger().debug("BC-FC information fetched : " + newBCMap.size());
    return newBCMap;
  }

  /**
   * Sets the query hints.
   *
   * @param qBC the new query hints
   */
  private void setQueryHints(final Query qBC) {
    qBC.setHint(ApicConstants.READ_ONLY, "true");
    qBC.setHint(ApicConstants.FETCH_SIZE, "5000");
    qBC.setHint(ApicConstants.STORE_MODE, "REFRESH");
    qBC.setHint(ApicConstants.SHARED_CACHE, "false");
  }

  /**
   * Gets all A 2 l system constants.
   *
   * @return all A 2 l system constants
   */
  public Map<String, A2LSystemConstant> getAllA2lSystemConstants() {

    getLogger().debug("fetching all system constants from database ...");
    TypedQuery<MvSdomSyscon> tQuery1 = getEntMgr().createNamedQuery(MvSdomSyscon.GET_ALL, MvSdomSyscon.class);
    setQueryHints(tQuery1);
    // System constant
    List<MvSdomSyscon> dbSysConList = tQuery1.getResultList();
    Map<String, A2LSystemConstant> systemConstMap = new HashMap<>();
    for (MvSdomSyscon dbsysCon : dbSysConList) {
      A2LSystemConstant sysCon = new A2LSystemConstant();
      sysCon.setSysconName(dbsysCon.getSyskonName());
      sysCon.setLongNameEng(dbsysCon.getBezEn());
      sysCon.setLongNameGer(dbsysCon.getBezDe());
      sysCon.setLongName(
          getServiceData().getLanguageObj() == Language.ENGLISH ? dbsysCon.getBezEn() : dbsysCon.getBezDe());
      systemConstMap.put(dbsysCon.getSyskonName(), sysCon);
    }
    // System constant values
    TypedQuery<Object[]> tQuery2 = getEntMgr().createNamedQuery(MvSdomSysconValue.GET_ALL, Object[].class);
    setQueryHints(tQuery1);

    List<Object[]> dbSysConValueList = tQuery2.getResultList();
    for (Object[] valueObj : dbSysConValueList) {
      String syskonWert = (String) valueObj[0];
      String sysKonWertBeschreibung = (String) valueObj[1];
      String syskonName = (String) valueObj[2];
      A2LSystemConstant sysCon = systemConstMap.get(syskonName);
      if (sysCon != null) {
        A2LSystemConstantValues sysConVal = new A2LSystemConstantValues();
        sysConVal.setValue(syskonWert);
        sysConVal.setValueDescription(sysKonWertBeschreibung);
        sysConVal.setSysconName(sysCon.getSysconName());
        sysConVal.setSysconLongName(sysCon.getLongName());
        sysCon.getSysConValues().put(sysCon.getSysconName() + ":" + sysConVal.getValue(), sysConVal);
      }
    }
    return systemConstMap;
  }


  /**
   * @param pverName pver name
   * @return map of a2l files associated to a pidc
   * @throws IcdmException
   */
  public Map<Long, A2LFile> getPverA2lFiles(final String pverName) throws IcdmException {
    final TypedQuery<MvTa2lFileinfo> qDbA2LFiles =
        getEntMgr().createNamedQuery(MvTa2lFileinfo.NQ_GET_PVER_A2L_FILES, MvTa2lFileinfo.class);
    qDbA2LFiles.setParameter("pver", pverName);
    final List<MvTa2lFileinfo> dbA2LFiles = qDbA2LFiles.getResultList();
    Map<Long, A2LFile> retMap = new HashMap<>();
    for (MvTa2lFileinfo pverA2l : dbA2LFiles) {

      retMap.put(pverA2l.getId(), createDataObject(pverA2l));
    }
    return retMap;
  }

  /**
   * @param a2lFileCheckSum a2lfilechecksum
   * @return set of VCDMA2LFileDetail
   */
  public Set<VCDMA2LFileDetail> getVCDMA2LFileDetails(final String a2lFileCheckSum) {

    getLogger().debug("fetching vCDM A2LFile details from database ...");

    final TypedQuery<MvTa2lVcdmVersion> vcdmVersionQuery =
        getEntMgr().createNamedQuery(MvTa2lVcdmVersion.NQ_FIND_VCDM_VERSION, MvTa2lVcdmVersion.class);
    vcdmVersionQuery.setParameter("a2lCSum", a2lFileCheckSum);

    final List<MvTa2lVcdmVersion> mvVCDMVersions = vcdmVersionQuery.getResultList();

    Set<VCDMA2LFileDetail> vcdma2lFileDetails = new HashSet<>();
    for (MvTa2lVcdmVersion mvTa2lVcdmVersion : mvVCDMVersions) {
      VCDMA2LFileDetail vcdma2lFileDetail = new VCDMA2LFileDetail();
      vcdma2lFileDetail.setOriginalFileName(mvTa2lVcdmVersion.getOriginalFile());
      vcdma2lFileDetail.setOriginalDate(timestamp2String(mvTa2lVcdmVersion.getOriginalDate()));
      vcdma2lFileDetail.setPst(mvTa2lVcdmVersion.getPst());
      vcdma2lFileDetails.add(vcdma2lFileDetail);
    }

    getLogger().debug("VCDMVersions fetched from database: " + vcdma2lFileDetails.size() + " for the a2lFileCheckSum " +
        a2lFileCheckSum);

    return vcdma2lFileDetails;
  }

  // ICDM-1456
  /**
   * Fetches PVer variants for the SDOM PVer name
   *
   * @param sdomPverName sdom name
   * @return SDOMPver object
   */
  public SortedSet<String> getA2LFilePVERVars(final String sdomPverName) {
    final TypedQuery<String> qDbA2LFiles =
        getEntMgr().createNamedQuery(MvTa2lFileinfo.NQ_GET_PVER_VAR_A2L_FILES, String.class);
    qDbA2LFiles.setParameter("pver", sdomPverName.toUpperCase(Locale.getDefault()));
    // Get the results
    final List<String> resultList = qDbA2LFiles.getResultList();
    SortedSet<String> pVerVaraints = new TreeSet<>();
    pVerVaraints.addAll(resultList);
    getLogger().debug(
        "No of variants available for SDOM Pver from MvTa2lFileinfo " + sdomPverName + "-" + pVerVaraints.size());
    return pVerVaraints;
  }

  /**
   * @param vcdmA2lFileId vcdm a2l file id
   * @return is vcdm a2l file id vaild (associated with a2l)
   */
  public boolean isValidVcdmA2lFileId(final Long vcdmA2lFileId) {
    final TypedQuery<MvTa2lFileinfo> query =
        getEntMgr().createNamedQuery(MvTa2lFileinfo.NQ_GET_BY_VCDM_A2L_FILE_ID, MvTa2lFileinfo.class);
    query.setParameter("vcdmA2lFileId", vcdmA2lFileId);

    return !query.getResultList().isEmpty();
  }
}
