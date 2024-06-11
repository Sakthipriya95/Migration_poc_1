/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.AliasDefLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.UCP_ATTR_MAPPING_FLAGS;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PIDCVersionReport;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.SubVariantReport;
import com.bosch.caltool.icdm.model.apic.pidc.VariantReport;
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;


/**
 * @author bne4cob
 */
public class PidcLoader extends AbstractBusinessObject<Pidc, TabvProjectidcard> {

  /**
   * @param serviceData Service Data
   */
  public PidcLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC, TabvProjectidcard.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Pidc createDataObject(final TabvProjectidcard entity) throws DataException {
    Pidc data = new Pidc();
    setCommonFields(data, entity);

    data.setNameValueId(entity.getTabvAttrValue().getValueId());
    AttributeValue nameAttrVal =
        new AttributeValueLoader(getServiceData()).getDataObjectByID(entity.getTabvAttrValue().getValueId());
    data.setName(nameAttrVal.getName());
    data.setNameEng(nameAttrVal.getTextValueEng());
    data.setNameGer(nameAttrVal.getTextValueGer());

    if (null != entity.getTaliasDefinition()) {
      data.setAliasDefId(entity.getTaliasDefinition().getAdId());
    }
    if (null != entity.getProRevId()) {
      data.setProRevId(entity.getProRevId());
    }
    data.setDescription(nameAttrVal.getDescription());
    data.setDescEng(nameAttrVal.getDescriptionEng());
    data.setDescGer(nameAttrVal.getDescriptionGer());
    data.setClearingStatus(nameAttrVal.getClearingStatus());
    data.setDeleted(nameAttrVal.isDeleted());

    data.setAprjId(entity.getAprjId());
    data.setVcdmTransferDate(timestamp2String(entity.getVcdmTransferDate()));
    data.setVcdmTransferUser(entity.getVcdmTransferUser());
    // Flag for Fetching Include Reviews from Older PidcVersion
    data.setInclRvwOfOldVers(yOrNToBoolean(entity.getInclRvwOfOldVers()));
    return data;
  }

  /**
   * @return
   */
  Set<Long> internalGetAllPidcIds() {
    Set<Long> retSet = new HashSet<>();

    final TypedQuery<TabvProjectidcard> query =
        getEntMgr().createNamedQuery(TabvProjectidcard.NQ_GET_ALL_PIDCS, TabvProjectidcard.class);
    final List<TabvProjectidcard> retList = query.getResultList();

    for (TabvProjectidcard entity : retList) {
      retSet.add(entity.getProjectId());
    }

    return retSet;
  }


  public Set<Long> getAllPidcIds() {
    Object data = getServiceData().retrieveData(getClass(), "ALL_PIDCS");
    if (data == null) {
      data = internalGetAllPidcIds();
      getServiceData().storeData(getClass(), "ALL_PIDCS", data);
    }
    return (Set<Long>) data;
  }

  /**
   * Get all Pidc
   *
   * @return level attributes. key - attribute ID, value - attribute
   * @throws DataException if attribute could not be found
   */
  public Map<Long, Pidc> getAllPidc() throws DataException {
    Map<Long, Pidc> retMap = new HashMap<>();
    for (Long pidc : getAllPidcIds()) {
      retMap.put(pidc, getDataObjectByID(pidc));
    }
    return retMap;
  }

  /**
   * @param nameValID name value id of pidc
   * @return Pidc
   * @throws DataException Exception
   */
  public Pidc getPidcByValID(final Long nameValID) throws DataException {

    final TypedQuery<TabvProjectidcard> query =
        getEntMgr().createNamedQuery(TabvProjectidcard.NQ_GET_PIDC_BY_VALID, TabvProjectidcard.class);
    query.setParameter("valId", nameValID);
    List<TabvProjectidcard> resultList = query.getResultList();
    if (!resultList.isEmpty()) {
      return createDataObject(resultList.get(0));
    }
    return null;
  }

  /**
   * Find PIDC Ids with matching name from DB
   *
   * @param pidcName String based on which the matching value for PIDC ids to be fetched
   * @return Set of PIDC ids that match with the provided name
   */
  public Set<Long> getPidcIdByName(final String pidcName) {
    getLogger().debug("Fetching PIDC IDs with matching name '{}'", pidcName);

    // Add wild cards at each end to match
    String pidcNameCriteria = "%" + pidcName + "%";

    final TypedQuery<Long> query = getEntMgr().createNamedQuery(TabvProjectidcard.NQ_GET_PIDC_ID_BY_NAME, Long.class);
    query.setParameter("pidcName", pidcNameCriteria);

    List<Long> resultList = query.getResultList();

    getLogger().debug("PIDC IDs fetched: {}", resultList);

    return new HashSet<>(resultList);
  }

  /**
   * @param pidcId attribute id
   * @return alias name
   * @throws DataException Exception
   */
  public AliasDef getAliasDefinition(final Long pidcId) throws DataException {
    Pidc pidcObj = getDataObjectByID(pidcId);

    return null == pidcObj.getAliasDefId() ? null
        : new AliasDefLoader(getServiceData()).getDataObjectByID(pidcObj.getAliasDefId());
  }


  /**
   * iCDM-890 <br>
   * Fetch the PIDC names with the owners - for which the attribute value is used in ProjectId card
   *
   * @param valueId AttrValue ID to search
   * @return Map of Owner and PIDC names in which the valID is used
   * @throws IcdmException as exception
   */
  public Map<String, Map<String, Map<String, Long>>> getPidcUsersUsingAttrValue(final Long valueId)
      throws IcdmException {
    final Query nativeQuery = getEntMgr().createNamedQuery(TPidcVersion.NNQ_PIDC_VERS_FOR_ATTR_VALUE);
    nativeQuery.setParameter(1, valueId);
    nativeQuery.setParameter(2, valueId);
    nativeQuery.setParameter(3, valueId);

    final List<Object[]> resultList = nativeQuery.getResultList();

    // Collect the pidcs and users
    Map<String, Map<String, Map<String, Long>>> userPidcMap = new HashMap<>();
    String userName;
    String pidcVersName;
    long pid;
    long pidcVerId;
    for (Object[] resObj : resultList) {
      // first obj is user name
      userName = (String) resObj[ApicConstants.COLUMN_INDEX_0];
      // second obj is pidc id
      pid = ((java.math.BigDecimal) resObj[ApicConstants.COLUMN_INDEX_1]).longValue();
      // third obj is pidc version name
      pidcVersName = (String) resObj[ApicConstants.COLUMN_INDEX_2];
      // fourth object is PIDC Version Id
      pidcVerId = ((java.math.BigDecimal) resObj[ApicConstants.COLUMN_INDEX_3]).longValue();
      Pidc pidCard = getDataObjectByID(pid);
      if (pidCard != null) {
        if (userPidcMap.get(userName) == null) {
          Map<String, Map<String, Long>> newPidcMap = new HashMap<>();
          Map<String, Long> pidcVerNamIdMap = new HashMap<>();
          pidcVerNamIdMap.put(pidcVersName, pidcVerId);
          newPidcMap.put(pidCard.getName(), pidcVerNamIdMap);
          userPidcMap.put(userName, newPidcMap);
        }
        else {
          Map<String, Map<String, Long>> currUserPidcMap = userPidcMap.get(userName);
          Map<String, Long> pidcVerNameIdMap = currUserPidcMap.get(pidCard.getName());
          addToPidcVerMap(pidcVersName, pidcVerId, pidCard, currUserPidcMap, pidcVerNameIdMap);
        }
      }
    }
    return userPidcMap;
  }

  /**
   * @param pidcId
   * @return set of attribute ids mapped to usecases of given pidc id
   * @throws DataException
   */
  public ProjectUsecaseModel getProjectUsecaseModel(final Long pidcId) throws DataException {

    UsecaseFavoriteLoader loader = new UsecaseFavoriteLoader(getServiceData());
    Map<Long, UsecaseFavorite> useCaseFavMap = loader.getProjFavoriteUseCases(pidcId);

    ProjectUsecaseModel ucModel = new UcpAttrLoader(getServiceData()).getUcpaForUCFavorites(useCaseFavMap, false);
    fillQuotationRelevantAttrData(ucModel);
    return ucModel;
  }


  private void fillQuotationRelevantAttrData(final ProjectUsecaseModel ucModel) throws DataException {

    Set<Long> quotRelevantUcpAttrSet = new HashSet<>();
    Map<Long, UcpAttr> ucpAttrMap = new UcpAttrLoader(getServiceData()).getAll();

    Map<Long, Set<Long>> quotationRelevantUcAttrIdMap = new HashMap<>();

    ucpAttrMap.values().forEach(ucpAttr -> {
      if ((ucpAttr.getMappingFlags() != null) &&
          UCP_ATTR_MAPPING_FLAGS.QUOTATION_RELEVANT.isSet(ucpAttr.getMappingFlags())) {
        quotRelevantUcpAttrSet.add(ucpAttr.getAttrId());
        setQuotationRelUcAttrMap(quotationRelevantUcAttrIdMap, ucpAttr);
      }
    });

    ucModel.getQuotationRelevantUcAttrIdSet().addAll(quotRelevantUcpAttrSet);
    ucModel.getQuotationRelevantUcAttrIdMap().putAll(quotationRelevantUcAttrIdMap);
  }


  private void setQuotationRelUcAttrMap(final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap,
      final UcpAttr ucpAttr) {
    // to set section id if the attribute is mapped to Usecase section
    if (null != ucpAttr.getSectionId()) {
      fillQuotationRelAttrMap(quotationRelevantUcAttrIdMap, ucpAttr.getSectionId(), ucpAttr.getAttrId());
    }
    else {
      // to set usecase id if the attribute is mapped to Usecase
      fillQuotationRelAttrMap(quotationRelevantUcAttrIdMap, ucpAttr.getUseCaseId(), ucpAttr.getAttrId());
    }
  }

  /**
   * @param quotationRelevantUcAttrIdMap
   * @param ucpAttr
   */
  private void fillQuotationRelAttrMap(final Map<Long, Set<Long>> quotationRelevantUcAttrIdMap, final Long id,
      final Long attrId) {
    if (quotationRelevantUcAttrIdMap.containsKey(id)) {
      Set<Long> attrIdSet = quotationRelevantUcAttrIdMap.get(id);
      attrIdSet.add(attrId);
    }
    else {
      Set<Long> attrIdSet = new HashSet<>();
      attrIdSet.add(attrId);
      quotationRelevantUcAttrIdMap.put(id, attrIdSet);
    }
  }

  /**
   * @param pidcVersName
   * @param pidcVerId
   * @param pidCard
   * @param currUserPidcMap
   * @param pidcVerNameIdMap
   */
  private void addToPidcVerMap(final String pidcVersName, final Long pidcVerId, final Pidc pidCard,
      final Map<String, Map<String, Long>> currUserPidcMap, final Map<String, Long> pidcVerNameIdMap) {
    if (pidcVerNameIdMap == null) {
      Map<String, Long> pidcVerNameId = new HashMap<>();
      pidcVerNameId.put(pidcVersName, pidcVerId);
      currUserPidcMap.put(pidCard.getName(), pidcVerNameId);
    }
    else {
      pidcVerNameIdMap.put(pidcVersName, pidcVerId);
    }
  }

  /**
   * @param pidcVersionID
   * @throws DataException
   */
  public PIDCVersionReport fetchPidcVersionInformation(final long pidcVersionID) throws IcdmException {
    PIDCVersionReport pidcReport = new PIDCVersionReport();

    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    if (!pidcVersionLoader.isHiddenToCurrentUser(pidcVersionID)) {
      constructPidcVerReport(pidcVersionLoader.getDataObjectByID(pidcVersionID), pidcReport);
    }
    return pidcReport;
  }

  /**
   * @param dataObjectByID
   * @param pidcReport
   * @throws IcdmException
   */
  private void constructPidcVerReport(final PidcVersion pidcVersion, final PIDCVersionReport pidcReport)
      throws IcdmException {
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    Pidc pidc = pidcLoader.getDataObjectByID(pidcVersion.getPidcId());
    pidcReport.setProjectName(pidc.getName());
    pidcReport.setProjectDescription(pidc.getDescription());
    pidcReport.setPidcID(pidc.getId());
    pidcReport.setPidcVersion(pidcVersion.getVersionName());
    pidcReport.setPidcVersionID(pidcVersion.getId());

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Comparator<PidcVersionAttribute> pidcVerAttrComparator =
        (final PidcVersionAttribute pidcAttr1, final PidcVersionAttribute pidcAttr2) -> {
          // To make level attributes come first, normal attributes (i.e attributes with level null) and APRJ and SDOM
          // PVer attributes(i.e attributes with level less than 0) are assigned a level 5 and then compared. 5 is
          // assigned since highest level as of writing this code is 4.
          try {
            return comparePidcAttr(attrLoader, pidcAttr1, pidcAttr2);
          }
          catch (DataException e) {
            getLogger().error("Error retrieving attribute level", e);
          }
          return 0;
        };

    PidcVersionAttributeLoader pidcVerAttrLoader = new PidcVersionAttributeLoader(getServiceData());
    attrLoader.getAllAttributes(false).size();
    List<PidcVersionAttribute> pidcVerAttrList = new ArrayList<>();
    pidcVerAttrList.addAll(pidcVerAttrLoader.getPidcVersionAttribute(pidcVersion.getId()).values());
    Collections.sort(pidcVerAttrList, pidcVerAttrComparator);
    AttributeValueLoader atrValLoader = new AttributeValueLoader(getServiceData());
    Map<String, String> pidcAttrMap = new LinkedHashMap<>();
    for (PidcVersionAttribute pidcVerAttr : pidcVerAttrList) {
      if ((null == pidcVerAttr.getId()) || !pidcVerAttr.isAtChildLevel()) {
        pidcAttrMap.put(pidcVerAttr.getName(), extractAttrVal(pidcVerAttr, atrValLoader));
      }
    }
    constructPidcVarReport(pidcVersion, pidcReport, atrValLoader);
    pidcReport.setPidcAttrMap(pidcAttrMap);
  }

  /**
   * @param attrLoader
   * @param pidcAttr1
   * @param pidcAttr2
   * @return
   * @throws DataException
   */
  private int comparePidcAttr(final AttributeLoader attrLoader, final PidcVersionAttribute pidcAttr1,
      final PidcVersionAttribute pidcAttr2)
      throws DataException {
    Integer pidcAttrLvl1 = attrLoader.getDataObjectByID(pidcAttr1.getAttrId()).getLevel().intValue();
    Integer pidcAttrLvl2 = attrLoader.getDataObjectByID(pidcAttr2.getAttrId()).getLevel().intValue();

    Integer attrLvl5 = 5;
    pidcAttrLvl1 = pidcAttrLvl1 == 0 ? attrLvl5 : checkAttrLvl(pidcAttrLvl1, attrLvl5);
    pidcAttrLvl2 = pidcAttrLvl2 == 0 ? attrLvl5 : checkAttrLvl(pidcAttrLvl2, attrLvl5);


    int result = pidcAttrLvl1.compareTo(pidcAttrLvl2);
    if (result == 0) {
      result = ApicUtil.compare(pidcAttr1.getName(), pidcAttr2.getName());
    }
    return result;
  }

  /**
   * @param pidcVersion
   * @param pidcReport
   * @param atrValLoader
   * @throws IcdmException
   */
  private void constructPidcVarReport(final PidcVersion pidcVersion, final PIDCVersionReport pidcReport,
      final AttributeValueLoader atrValLoader)
      throws IcdmException {
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    PidcVariantAttributeLoader varAttrLoader = new PidcVariantAttributeLoader(getServiceData());
    for (PidcVariant pidcVariant : varLoader.getVariants(pidcVersion.getId(), false).values()) {
      VariantReport variantReport = new VariantReport();
      variantReport.setName(pidcVariant.getName());
      variantReport.setDescription(pidcVariant.getDescription());
      variantReport.setVariantID(pidcVariant.getId());

      Map<String, String> varAttrMap = new HashMap<>();
      for (PidcVariantAttribute varAttr : varAttrLoader.getVarAttrForVariant(pidcVariant.getId()).values()) {
        if ((null == varAttr.getId()) || !varAttr.isAtChildLevel()) {
          varAttrMap.put(varAttr.getName(), extractAttrVal(varAttr, atrValLoader));
        }
      }
      constructSubVariantReport(pidcVariant, variantReport, atrValLoader);
      variantReport.setPidcVarAttrMap(varAttrMap);
      if (null == pidcReport.getVariants()) {
        pidcReport.setVariants(new ArrayList<VariantReport>());
      }
      pidcReport.getVariants().add(variantReport);
    }
  }

  /**
   * @param pidcVariant
   * @param variantReport
   * @param atrValLoader
   * @throws IcdmException
   */
  private void constructSubVariantReport(final PidcVariant pidcVariant, final VariantReport variantReport,
      final AttributeValueLoader atrValLoader)
      throws IcdmException {
    PidcSubVariantLoader subvarLoader = new PidcSubVariantLoader(getServiceData());
    PidcSubVariantAttributeLoader subvarAttrLoader = new PidcSubVariantAttributeLoader(getServiceData());
    for (PidcSubVariant subvar : subvarLoader.getSubVariants(pidcVariant.getId(), false).values()) {

      SubVariantReport subVariantReport = new SubVariantReport();
      subVariantReport.setName(subvar.getName());
      subVariantReport.setDescription(subvar.getDescription());
      subVariantReport.setsVariantID(subvar.getId());

      Map<String, String> subVarAttrMap = new HashMap<>();
      for (PidcSubVariantAttribute svarAttr : subvarAttrLoader.getSubVarAttrForSubVarId(subvar.getId()).values()) {
        if ((null == svarAttr.getId()) || !svarAttr.isAtChildLevel()) {
          subVarAttrMap.put(svarAttr.getName(), extractAttrVal(svarAttr, atrValLoader));
        }
      }
      subVariantReport.setPidcSubVarAttrMap(subVarAttrMap);
      if (null == variantReport.getSubvariants()) {
        variantReport.setSubvariants(new ArrayList<SubVariantReport>());
      }
      variantReport.getSubvariants().add(subVariantReport);
    }
  }

  /**
   * @param atrValLoader
   * @param pidcVerAttr
   * @return
   * @throws DataException
   */
  private String extractAttrVal(final IProjectAttribute projAttr, final AttributeValueLoader atrValLoader)
      throws DataException {
    String value = "";
    String usedStr = projAttr.getUsedFlag();
    if (usedStr.equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType())) {
      value = ApicConstants.USED;
      if ((null != projAttr.getValue()) && !projAttr.getValue().trim().isEmpty()) {
        value = atrValLoader.getDataObjectByID(projAttr.getValueId()).getName();
      }
    }
    else if (usedStr.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
      value = ApicConstants.NOT_USED;
    }
    else if (usedStr.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {
      value = ApicConstants.JSON_UNDEFINED_VALUE;
    }
    if (projAttr.isAttrHidden()) {
      value = ApicConstants.HIDDEN_VALUE;
    }
    return value;
  }

  /**
   * @param pidcAttrLvl1
   * @param attr_Lvl_5
   * @return
   */
  private Integer checkAttrLvl(final Integer pidcAttrLvl1, final Integer attr_Lvl_5) {
    return pidcAttrLvl1 < 0 ? attr_Lvl_5 : pidcAttrLvl1;
  }
}
