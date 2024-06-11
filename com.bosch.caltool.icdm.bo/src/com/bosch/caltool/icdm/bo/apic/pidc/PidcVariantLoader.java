/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantsInputData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;


/**
 * @author bne4cob
 */
public class PidcVariantLoader extends AbstractBusinessObject<PidcVariant, TabvProjectVariant> {

  /**
   * @param serviceData Service Data
   */
  public PidcVariantLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.VARIANT, TabvProjectVariant.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcVariant createDataObject(final TabvProjectVariant entity) throws DataException {
    PidcVariant data = new PidcVariant();

    setCommonFields(data, entity);
    data.setNameValueId(entity.getTabvAttrValue().getValueId());
    AttributeValueLoader attrValLdr = new AttributeValueLoader(getServiceData());
    AttributeValue attrVal = attrValLdr.getDataObjectByID(entity.getTabvAttrValue().getValueId());
    data.setName(attrVal.getName());
    data.setDescription(ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(), attrVal.getDescriptionEng(),
        attrVal.getDescriptionGer(), null));

    data.setDeleted(isDeleted(entity));
    data.setPidcVersionId(entity.getTPidcVersion().getPidcVersId());
    data.setVersion(entity.getVersion());

    return data;
  }

  /**
   * @param pidcVersionId Pidc Version ID
   * @param includeDeleted include Deleted variants
   * @return Map of variants. Key - Variant ID, Value - data object
   * @throws DataException error while creating data
   */
  public Map<Long, PidcVariant> getVariants(final Long pidcVersionId, final boolean includeDeleted)
      throws DataException {

    getLogger().debug("Loading variants of PIDC Version {}", pidcVersionId);

    Map<Long, PidcVariant> retMap = new HashMap<>();
    PidcVariant data;
    for (TabvProjectVariant entity : new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId)
        .getTabvProjectVariants()) {

      if (includeDeleted || !isDeleted(entity)) {
        data = createDataObject(entity);
        retMap.put(data.getId(), data);
      }

    }

    getLogger().debug("Pidc Version: {}. Variants Count = {}", pidcVersionId, retMap.size());

    return retMap;
  }

  /**
   * @param pidcVersionId PIDC Version ID
   * @param includeDeleted include Deleted variants
   * @return true if pidc varsion has non deleted variants
   */
  public boolean hasVariants(final Long pidcVersionId, final boolean includeDeleted) {
    getLogger().debug("Checking has variants in PIDC Version : {}", pidcVersionId);

    boolean result = false;

    List<TabvProjectVariant> tabvProjectVariants =
        new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId).getTabvProjectVariants();
    if (null != tabvProjectVariants) {
      for (TabvProjectVariant entity : tabvProjectVariants) {
        if (includeDeleted || !isDeleted(entity)) {
          result = true;
          break;
        }
      }
    }

    getLogger().debug("Pidc Version: {}. Has Variants = {}", result);

    return result;
  }

  private boolean isDeleted(final TabvProjectVariant entity) {
    return CommonUtilConstants.CODE_YES.equals(entity.getDeletedFlag());
  }

  /**
   * @param sdomPverAttrValueId value id of sdom pver attribute
   * @param pidcVersionId pidc version Id
   * @return pidc variants belonging to the input sdom pver
   * @throws DataException error while loading data
   */
  public SortedSet<PidcVariant> getPidcVariant(final long sdomPverAttrValueId, final Long pidcVersionId)
      throws DataException {
    SortedSet<PidcVariant> pidcVariantSet = new TreeSet<>();
    TypedQuery<TabvProjectVariant> query =
        getEntMgr().createNamedQuery(TabvProjectVariant.NQ_GET_PIDC_VARIANT, TabvProjectVariant.class);
    query.setParameter("valueId", sdomPverAttrValueId);
    query.setParameter("pidcVersId", pidcVersionId);
    List<TabvProjectVariant> dbObj = query.getResultList();

    for (TabvProjectVariant entity : dbObj) {
      pidcVariantSet.add(createDataObject(entity));
    }
    return pidcVariantSet;
  }


  /**
   * Get all variants of a pidc version, mapped to an a2l file
   *
   * @param pidcA2lId PIDC A2L mapping ID
   * @param includeDeletedVar boolean value
   * @return map of pidc variants. key - variant ID, value - variant
   * @throws IcdmException error while loading data
   */
  public Map<Long, PidcVariant> getA2lMappedVariants(final Long pidcA2lId, final boolean includeDeletedVar) throws IcdmException {
    Map<Long, Map<Long, PidcVariant>> varMap = getA2lMappedVariants(new HashSet<>(Arrays.asList(pidcA2lId)),includeDeletedVar);
    return varMap.getOrDefault(pidcA2lId, new HashMap<>());
  }
  
  /**
   * TO check if there is any review result available for the given a2l and var ids
   * 
   * @param pidcA2lId as Pidc A2l ID
   * @param pidcVarId as Pidc Varaint ID
   * @return boolean true if the review count is more than 0
   */
  public boolean hasReviewForA2lAndVariant(final Long pidcA2lId,final Long pidcVarId){
    TypedQuery<Long> typeQuery =
        getEntMgr().createNamedQuery(TRvwResult.GET_COUNT_OF_RVW_BY_A2L_AND_VARID, Long.class).setParameter("pidcVarId", pidcVarId).setParameter("pidcA2lId", pidcA2lId);
    Long rvwCount = typeQuery.getSingleResult();
    return rvwCount > 0 ;
  }

  /**
   * Get all variants, mapped to the given a2l files.
   *
   * @param pidcA2lIdSet set of PIDC A2L mapping ID
   * @param includeDeleted holds boolean value
   * @return map of pidc variants. key - pidc a2l ID, value - Map of variant ID, value - variant
   * @throws IcdmException error while loading data
   */
  public Map<Long, Map<Long, PidcVariant>> getA2lMappedVariants(final Set<Long> pidcA2lIdSet, final boolean includeDeleted) throws IcdmException {
    Map<Long, Map<Long, PidcVariant>> retMap = new HashMap<>();

    Long pverAttrId = new AttributeLoader(getServiceData()).getLevelAttrId((long) ApicConstants.SDOM_PROJECT_NAME_ATTR);

    Map<Long, PidcVersionAttributeModel> projAttrModelMap = new HashMap<>();

    for (Long pidcA2lId : pidcA2lIdSet) {
      PidcA2l pidcA2l = new PidcA2lLoader(getServiceData()).getDataObjectByID(pidcA2lId);
      Long pidcVersId = pidcA2l.getPidcVersId();
      if (pidcVersId == null) {
        throw new InvalidInputException("A2L file " + pidcA2l.getName() + " is not mapped to any PIDC version");
      }

      PidcVersionAttributeModel projModel = projAttrModelMap.computeIfAbsent(pidcVersId, k -> {
        try {
          return new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS, includeDeleted);
        }
        catch (IcdmException e) {
          getLogger().error(e.getMessage(), e);
        }
        return null;
      });

      if (projModel == null) {
        continue;
      }

      Map<Long, PidcVariant> varMap = doGetA2lMappedVariants(pverAttrId, projModel, pidcA2l.getSdomPverName());

      getLogger().debug("Pidc A2L ID {} : Mapped variants = {}", pidcA2l.getId(), varMap.size());

      retMap.put(pidcA2lId, varMap);

    }

    return retMap;
  }

  /**
   * Get all variants, mapped to the given a2l files.
   *
   * @param pverName sdom pver name
   * @param pidcVersId pidc vers id
   * @return map of pidc variants. key - pidc a2l ID, value - Map of variant ID, value - variant
   * @throws IcdmException error while loading data
   */
  public Map<Long, PidcVariant> getSdomPverMappedVariants(final String pverName, final Long pidcVersId)
      throws IcdmException {


    if (pidcVersId == null) {
      throw new InvalidInputException("A2L file is not mapped to any PIDC version");
    }
    Map<Long, PidcVersionAttributeModel> projAttrModelMap = new HashMap<>();
    PidcVersionAttributeModel projModel = projAttrModelMap.computeIfAbsent(pidcVersId, k -> {
      try {
        return new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS, false);
      }
      catch (IcdmException e) {
        getLogger().error(e.getMessage(), e);
      }
      return null;
    });

    if (projModel == null) {
      return null;
    }

    Long pverAttrId = new AttributeLoader(getServiceData()).getLevelAttrId((long) ApicConstants.SDOM_PROJECT_NAME_ATTR);
    Map<Long, PidcVariant> varMap = doGetA2lMappedVariants(pverAttrId, projModel, pverName);

    getLogger().debug("Sdom pver name{} : Mapped variants = {}", pverName, varMap.size());

    return varMap;
  }

  private Map<Long, PidcVariant> doGetA2lMappedVariants(final Long pverAttrId,
      final PidcVersionAttributeModel projModel, final String reqPver) {
    PidcVersionAttribute pidcVersPverAttr = projModel.getPidcVersAttr(pverAttrId);

    Map<Long, PidcVariant> varMap = new HashMap<>();
    if (pidcVersPverAttr.isAtChildLevel()) {
      for (PidcVariant var : projModel.getVariantMap().values()) {
        PidcVariantAttribute varPverAttr = projModel.getVariantAttribute(var.getId(), pverAttrId);
        if ((varPverAttr != null) && CommonUtils.isEqual(reqPver, varPverAttr.getValue())) {
          varMap.put(var.getId(), var);
        }
      }
    }
    else {
      varMap.putAll(projModel.getVariantMap());
    }
    return varMap;
  }

  /**
   * Check whether data reivews are done against the given variant
   *
   * @param pidVarId variant Long
   * @return Boolean, true if reviews are available
   * @throws DataException error while loading data
   */
  public Boolean hasReviews(final Long pidVarId) throws DataException {
    PidcVariant pidVar = getDataObjectByID(pidVarId);
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidVar.getPidcVersionId());
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    TabvProjectidcard tabvPidc = pidcLoader.getEntityObject(pidcVersion.getPidcId());
    Set<TPidcVersion> tPidcVersions = tabvPidc.getTPidcVersions();
    for (TPidcVersion tPidcVersion : tPidcVersions) {
      // check for versions other then the current one of the pidc var
      if (tPidcVersion.getPidcVersId() != pidVar.getPidcVersionId().longValue()) {
        boolean hasReviews = checkAllVar(pidVar, tPidcVersion);
        if (hasReviews) {
          return hasReviews;
        }
      }
    }

    return Boolean.FALSE;
  }

  /**
   * @param pidVar
   * @param tPidcVersion
   * @return
   * @throws DataException
   */
  private boolean checkAllVar(final PidcVariant pidVar, final TPidcVersion tPidcVersion) throws DataException {
    Set<TRvwVariant> variantsWithResults = getVariantsWithResults(tPidcVersion);

    for (TRvwVariant varWithRes : variantsWithResults) {
      if (varWithRes.getTabvProjectVariant().getTabvAttrValue().getTextvalueEng().equals(pidVar.getName())) {
        return true;
      }
    }


    return false;
  }


  /**
   * Get the variants which has results
   *
   * @param pidcVer pidc version
   * @return set of variants
   * @throws DataException error while loading data
   */
  public Set<TRvwVariant> getVariantsWithResults(final TPidcVersion pidcVer) throws DataException {
    Set<TRvwVariant> variantSet = new HashSet<>();

    for (TRvwResult cdrResult : getCDRResults(pidcVer)) {
      if (null != cdrResult.getTRvwResults()) {
        for (TRvwVariant rvwVar : cdrResult.getTRvwVariants()) {
          variantSet.add(rvwVar);
        }
      }
    }
    return variantSet;
  }

  /**
   * @param pidcVer
   * @return
   * @throws DataException
   */
  private Set<TRvwResult> getCDRResults(final TPidcVersion pidcVer) throws DataException {
    Set<TRvwResult> cdrResultSet = new HashSet<>();
    CDRReviewResultLoader pidcCdrLoader = new CDRReviewResultLoader(getServiceData());
    Map<String, Map<Long, CDRReviewResult>> cdrResultsForPidcVer =
        pidcCdrLoader.getCdrResultsForPidcVer(pidcVer.getPidcVersId());
    for (Map<Long, CDRReviewResult> cdrResultMap : cdrResultsForPidcVer.values()) {
      for (CDRReviewResult cdrReviewResult : cdrResultMap.values()) {
        TRvwResult tRvwResult = pidcCdrLoader.getEntityObject(cdrReviewResult.getId());
        cdrResultSet.add(tRvwResult);
      }
    }
    return cdrResultSet;
  }

  /**
   * @param varIds Set of variant Ids
   * @return map of key - name value id of variant, value-variant id
   * @throws DataException Exception
   */
  public Map<Long, Long> getVarIdsForNameValIds(final Set<Long> varIds) throws DataException {
    Map<Long, Long> nameValIdVarIdMap = new HashMap<>();
    getDataObjectByID(varIds).values().stream()
        .forEach(var -> nameValIdVarIdMap.put(var.getNameValueId(), var.getId()));
    return nameValIdVarIdMap;
  }

  /**
   * Get all pidc variants of pidc version, including the variant attributes, for given attribute list
   *
   * @param pidcVersId PIDC version ID
   * @param attrIdSet set of attrribute IDs
   * @return Map - key variant Id, value variant with variant attributes
   * @throws IcdmException exception while invoking service
   */
  public Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> getVariantsWithAttrs(
      final Long pidcVersId, final Set<Long> attrIdSet)
      throws IcdmException {

    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> retMap = new HashMap<>();

    PidcVersionAttributeModel projAttrMdl =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS, false);

    projAttrMdl.getVariantMap().forEach((vId, var) -> {
      ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> varObj = new ProjectObjectWithAttributes<>();

      Map<Long, PidcVariantAttribute> varAttrMap =
          attrIdSet.stream().map(aId -> projAttrMdl.getVariantAttribute(vId, aId)).filter(Objects::nonNull)
              .collect(Collectors.toMap(PidcVariantAttribute::getAttrId, vAttr -> vAttr));

      varObj.setProjectObject(var);
      varObj.setProjectAttrMap(varAttrMap);

      retMap.put(vId, varObj);
    });

    return retMap;
  }

  /**
   * @param pidcA2lId PIDC Variant ID
   * @return the extend path - the tree structure of the PIDC Variant
   * @throws DataException error while retrieving data
   */
  private String getPidcTreePath(final PidcVariant pidcVariant) throws DataException {
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(pidcVariant.getPidcVersionId());

    return pidcVersLdr.getPidcTreePath(pidcVers.getId()) + pidcVers.getName() + "->";
  }

  /**
   * @param variantId PIDC Variant ID
   * @return extended name of this PIDC Variant
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long variantId) throws DataException {
    PidcVariant pidcVariant = getDataObjectByID(variantId);
    return EXTERNAL_LINK_TYPE.PIDC_VARIANT.getTypeDisplayText() + ": " + getPidcTreePath(pidcVariant) +
        pidcVariant.getName();
  }

  public PidcVariantsInputData getPidcVariantsInputData(final List<Long> elementIdList) throws IcdmException {
    PidcVariantsInputData pidcVariantInputData = new PidcVariantsInputData();
    Map<Long, PidcVariant> pidcVariantMap = new HashMap<>();

    // Fill Variant map by checking whether input is variant id
    fillVariantMap(elementIdList, pidcVariantInputData, pidcVariantMap);

    // fill pidc version information
    if (pidcVariantInputData.isVariantAvailable() && CommonUtils.isNotEmpty(pidcVariantMap)) {
      PidcVersion pidcVers = new PidcVersionLoader(getServiceData())
          .getDataObjectByID(pidcVariantMap.values().iterator().next().getPidcVersionId());
      pidcVariantInputData.setPidcVersion(pidcVers);
    }

    // set pidc variant map to Response - pidcvariantinputdata
    pidcVariantInputData.setPidcVariantMap(pidcVariantMap);
    return pidcVariantInputData;
  }

  /**
   * @param pidcVersionId , Id of pidc Version
   * @param a2lFileId , Id of A2l File
   * @return Set of PidcVariants
   * @throws DataException, if data not available for Id
   */
  public SortedSet<PidcVariant> getPidcVarForPidcVersAndA2l(final Long pidcVersionId, final Long a2lFileId) throws DataException {
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    Long sdomPverValId = attrValLoader.getSdomPverAttrValueId(pidcVersionId, a2lFileId);

    SortedSet<PidcVariant> pidcVariants = getPidcVariant(sdomPverValId, pidcVersionId);
    //Return all the pidc variants for Pidc version, if sdom pver attribute is not customized to variant level
    if (pidcVariants.isEmpty()) {
      pidcVariants = new TreeSet<>(getVariants(pidcVersionId, false).values());
    }
    return pidcVariants;
  }

  /**
   * @param elementIdList
   * @param pidcVariantInputData
   * @param pidcVariantMap
   * @throws DataException
   */
  private void fillVariantMap(final List<Long> elementIdList, final PidcVariantsInputData pidcVariantInputData,
      final Map<Long, PidcVariant> pidcVariantMap)
      throws DataException {
    if (elementIdList.size() == 1) {
      Long elementId = elementIdList.get(0);
      if (isValidId(elementId)) {
        PidcVariant pidcVar = getDataObjectByID(elementId);
        pidcVariantMap.put(pidcVar.getId(), pidcVar);
      }
      else {
        PidcVersion pidcVers = new PidcVersionLoader(getServiceData()).getDataObjectByID(elementId);
        pidcVariantInputData.setPidcVersion(pidcVers);
        pidcVariantInputData.setVariantAvailable(false);
      }
    }
    else {
      pidcVariantMap.putAll(getDataObjectByID(new HashSet<>(elementIdList)));
    }
  }
}
