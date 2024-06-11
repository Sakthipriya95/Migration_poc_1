package com.bosch.caltool.icdm.bo.uc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.FocusMatrixVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Loader class for Ucp Attr
 *
 * @author MKL2COB
 */
public class UcpAttrLoader extends AbstractBusinessObject<UcpAttr, TabvUcpAttr> {

  /**
   * GTT object name for the use case group id's
   */
  public static final String USE_CASE_GROUP_IDS_GTT_NAME = "TabvUseCaseGroup";


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public UcpAttrLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.UCP_ATTR, TabvUcpAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UcpAttr createDataObject(final TabvUcpAttr entity) throws DataException {
    UcpAttr object = new UcpAttr();

    setCommonFields(object, entity);

    Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(entity.getTabvAttribute().getAttrId());
    object.setAttrId(attr.getId());
    object.setName(attr.getName());
    object.setDescription(attr.getDescription());
    object.setMappingFlags(entity.getMappingFlags());

    if (null != entity.getTabvUseCas()) {
      object.setUseCaseId(entity.getTabvUseCas().getUseCaseId());
    }
    if (null != entity.getTabvUseCaseSection()) {
      object.setSectionId(entity.getTabvUseCaseSection().getSectionId());
    }

    return object;
  }

  /**
   * Get all Ucp Attr records in system
   *
   * @return Map. Key - id, Value - UcpAttr object
   * @throws DataException error while retrieving data
   */
  public Map<Long, UcpAttr> getAll() throws DataException {
    Map<Long, UcpAttr> objMap = new ConcurrentHashMap<>();
    TypedQuery<TabvUcpAttr> tQuery = getEntMgr().createNamedQuery(TabvUcpAttr.GET_ALL_UCP_ATTR, TabvUcpAttr.class);
    List<TabvUcpAttr> dbObj = tQuery.getResultList();
    for (TabvUcpAttr entity : dbObj) {
      objMap.put(entity.getUcpaId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get ucp attr records for the given use case group ids with quotation relevant flag.
   *
   * @param usecaseGroupIds usecase group ids
   * @return Map of key: attrId, value: isQuotationRelevantFlag
   * @throws IcdmException error while fetching backend data
   */
  public Map<Long, Boolean> getUcpAttrIdsByGroupIds(final Set<Long> usecaseGroupIds,
      final boolean applyQuotationRelevantFilter)
      throws IcdmException {
    Map<Long, Boolean> ucpAttrs = new HashMap<>();
    EntityManager entityManager = null;
    try {
      entityManager = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
      entityManager.getTransaction().begin();
      GttObjectName tempGrpId;
      // Create entities for all the use case group ids
      for (Long grpId : usecaseGroupIds) {
        tempGrpId = new GttObjectName();
        tempGrpId.setId(grpId);
        tempGrpId.setObjName(USE_CASE_GROUP_IDS_GTT_NAME);
        entityManager.persist(tempGrpId);
      }
      entityManager.flush();
//       Function will use temp GTT table as the input instead of providing the list of group ids through the function call
      Query attrIdByUcgIdQuery = entityManager.createNamedQuery(TabvUcpAttr.GET_UCP_ATTR_BY_USE_CASE_GROUP_IDS);
//       get_quotation_relevant_attributes="FALSE"::include all the usecase group's attributes
      attrIdByUcgIdQuery.setParameter(1, ApicConstants.BOOLEAN_FALSE_STRING);
      List allAttrResultList = attrIdByUcgIdQuery.getResultList();
      List quotationRelAttrResultList = null;
      if (CommonUtils.isNotEmpty(allAttrResultList)) {
//        get quotation relevant attributes only if atleast any one attribute is mapped to the usecase groups
//        and quotation relevant filter is applied
        if (applyQuotationRelevantFilter) {
          Query quotRelAttrIdByUcgIdQuery =
              entityManager.createNamedQuery(TabvUcpAttr.GET_UCP_ATTR_BY_USE_CASE_GROUP_IDS);
//        get_quotation_relevant_attributes="TRUE"::include only the quotation relevant usecase group's attributes
          quotRelAttrIdByUcgIdQuery.setParameter(1, ApicConstants.BOOLEAN_TRUE_STRING);
          quotationRelAttrResultList = quotRelAttrIdByUcgIdQuery.getResultList();
        }
        allAttrResultList.stream()
            .forEach(ucpAttrId -> ucpAttrs.put(CommonUtils.bigDecimalToLong(ucpAttrId), Boolean.FALSE));
        if (CommonUtils.isNotEmpty(quotationRelAttrResultList)) {
          quotationRelAttrResultList.stream().forEach(ucpAttrId -> {
            Long attrId = CommonUtils.bigDecimalToLong(ucpAttrId);
            if (ucpAttrs.containsKey(attrId)) {
//              update quotation relevant flag to true
              ucpAttrs.put(attrId, Boolean.TRUE);
            }
          });
        }
      }
      entityManager.getTransaction().rollback();
    }
    catch (Exception exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
    finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
    return ucpAttrs;
  }

  /**
   * @param usecaseId Long
   * @return Map<Long, UcpAttr>
   * @throws DataException exception while creating data object
   */
  public Map<Long, UcpAttr> getUCMappedAttributes(final Long usecaseId) throws DataException {
    Map<Long, UcpAttr> objMap = new ConcurrentHashMap<>();
    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    TabvUseCase tabvUsecase = ucLoader.getEntityObject(usecaseId);
    List<TabvUcpAttr> dbObj = tabvUsecase.getTabvUcpAttrs();
    for (TabvUcpAttr entity : dbObj) {
      objMap.put(entity.getUcpaId(), createDataObject(entity));
    }
    return objMap;

  }

  /**
   * @param ucId Use Case Id
   * @return set of attribute ID
   * @throws DataException error while creating data objects
   */
  public Set<Long> getMappedAttributesUseCase(final Long ucId) throws DataException {
    Set<Long> retSet = new HashSet<>();
    UseCaseLoader ucLdr = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader ucSectionLdr = new UseCaseSectionLoader(getServiceData());
    try {
      fillAttrFromUsecase(ucId, retSet, ucLdr, ucSectionLdr);
    }
    catch (DataException exp) {
      fillAttrFromUsecaseSection(ucId, retSet, ucSectionLdr);
      getLogger().error(exp.getLocalizedMessage(), exp);
    }


    getLogger().debug("Use Case Item ID : {} - total attributes linked = {}", ucId, retSet.size());
    return retSet;
  }

  /**
   * @param ucId
   * @param retSet
   * @param ucLdr
   * @param ucSectionLdr
   * @throws DataException
   */
  private void fillAttrFromUsecaseSection(final Long ucId, final Set<Long> retSet,
      final UseCaseSectionLoader ucSectionLdr)
      throws DataException {
    for (Entry<Long, IUseCaseItem> leafEntry : ucSectionLdr.getLeafNodes(ucId).entrySet()) {
      fillAttributes(ucSectionLdr.getEntityObject(leafEntry.getKey()).getTabvUcpAttrs(), retSet);
    }
  }

  /**
   * @param ucId
   * @param retSet
   * @param ucLdr
   * @param ucSectionLdr
   * @throws DataException
   */
  private void fillAttrFromUsecase(final Long ucId, final Set<Long> retSet, final UseCaseLoader ucLdr,
      final UseCaseSectionLoader ucSectionLdr)
      throws DataException {
    for (Entry<Long, IUseCaseItem> leafEntry : ucLdr.getLeafNodes(ucId).entrySet()) {
      if (leafEntry.getValue() instanceof UseCase) {
        fillAttributes(ucLdr.getEntityObject(leafEntry.getKey()).getTabvUcpAttrs(), retSet);
      }
      else {
        fillAttributes(ucSectionLdr.getEntityObject(leafEntry.getKey()).getTabvUcpAttrs(), retSet);
      }
    }
  }

  /**
   * @param useCaseId Use Case Id
   * @param includeDel
   * @return set of attribute ID
   * @throws DataException error while creating data objects
   */
  public Map<Long, Set<Long>> getMappedAttributes(final Long useCaseId, final boolean includeDel) throws DataException {
    Map<Long, Set<Long>> retSet = new HashMap<Long, Set<Long>>();
    UseCaseLoader ucLdr = new UseCaseLoader(getServiceData());
    UseCaseSectionLoader ucSectionLdr = new UseCaseSectionLoader(getServiceData());
    for (Entry<Long, IUseCaseItem> leafEntry : ucLdr.getLeafNodes(useCaseId).entrySet()) {
      if (leafEntry.getValue() instanceof UseCase) {
        retSet.put(leafEntry.getKey(),
            fillAttributes(ucLdr.getEntityObject(leafEntry.getKey()).getTabvUcpAttrs(), includeDel));
      }
      else {
        retSet.put(leafEntry.getKey(),
            fillAttributes(ucSectionLdr.getEntityObject(leafEntry.getKey()).getTabvUcpAttrs(), includeDel));
      }
    }
    getLogger().debug("Use Case ID : {} - total attributes linked = {}", useCaseId, retSet.size());
    return retSet;
  }

  private void fillAttributes(final List<TabvUcpAttr> entityList, final Set<Long> attrSet) {
    for (TabvUcpAttr entity : entityList) {
      TabvAttribute dbAttr = entity.getTabvAttribute();
      if (CommonUtilConstants.CODE_NO.equals(dbAttr.getDeletedFlag())) {
        attrSet.add(dbAttr.getAttrId());
      }
    }
  }

  private Set<Long> fillAttributes(final List<TabvUcpAttr> entityList, final boolean includeDel) {
    Set<Long> attrSet = new HashSet<>();
    for (TabvUcpAttr entity : entityList) {
      TabvAttribute dbAttr = entity.getTabvAttribute();
      if (CommonUtilConstants.CODE_YES.equals(dbAttr.getDeletedFlag())) {
        if (includeDel) {
          attrSet.add(dbAttr.getAttrId());
        }
      }
      else {
        attrSet.add(dbAttr.getAttrId());
      }
    }
    return attrSet;
  }

  /**
   * @param fmVersionId Long
   * @return Set<UcpAttr>
   * @throws DataException
   */
  public Set<UcpAttr> getUCPAForFocusMatrixVersion(final Long fmVersionId) throws DataException {
    Set<UcpAttr> ucpAttrSet = new HashSet<>();
    FocusMatrixVersionLoader fmVersionLoader = new FocusMatrixVersionLoader(getServiceData());
    TFocusMatrixVersion tFocusMatrixVersion = fmVersionLoader.getEntityObject(fmVersionId);
    Set<TUsecaseFavorite> tabvPidFavorites =
        tFocusMatrixVersion.getTPidcVersion().getTabvProjectidcard().getTUsecaseFavorites();
    for (TUsecaseFavorite tabvPidFavorite : tabvPidFavorites) {
      if (tabvPidFavorite.getTabvUseCaseGroup() != null) {
        addUcpaForGroup(ucpAttrSet, null, tabvPidFavorite.getTabvUseCaseGroup(), true);
      }
      if (tabvPidFavorite.getTabvUseCas() != null) {
        addUcpaForUsecase(ucpAttrSet, null, tabvPidFavorite.getTabvUseCas(), true);
      }
      if (tabvPidFavorite.getTabvUseCaseSection() != null) {
        addUcpaForUsecaseSection(ucpAttrSet, null, tabvPidFavorite.getTabvUseCaseSection(), true);
      }
    }
    return ucpAttrSet;
  }

  /**
   * @param useCaseFavMap given collection of usecasefavorites
   * @param includeDeleted
   * @return set of attribute ids mapped to given collection of usecasefavorites - Usecase, UsecaseGroup, UsecaseSection
   * @throws DataException
   */
  public ProjectUsecaseModel getUcpaForUCFavorites(final Map<Long, UsecaseFavorite> useCaseFavMap,
      final boolean includeDeleted)
      throws DataException {

    ProjectUsecaseModel model = new ProjectUsecaseModel();


    Set<UcpAttr> favUcpAttrSet = new HashSet<>();

    for (UsecaseFavorite useCaseFav : useCaseFavMap.values()) {

      if (useCaseFav.getGroupId() != null) {

        TabvUseCaseGroup ucGrp = new UseCaseGroupLoader(getServiceData()).getEntityObject(useCaseFav.getGroupId());

        // check to exclude deleted items based on flag
        if (!includeDeleted && (ModelUtil.isEqualIgnoreCase(ucGrp.getDeletedFlag(), ApicConstants.CODE_YES) ||
            isParentLevelForGrpDeleted(ucGrp))) {
          continue;
        }

        addUcpaForGroup(favUcpAttrSet, model.getProjectUsecaseIdSet(), ucGrp, includeDeleted);

        // add items higher in hierarchy
        addParentLevelItemsForGrp(ucGrp, model.getParentUsecaseItemIdSet());


      }
      else if (useCaseFav.getUseCaseId() != null) {
        TabvUseCase uc = new UseCaseLoader(getServiceData()).getEntityObject(useCaseFav.getUseCaseId());

        // check to exclude deleted items based on flag
        if (!includeDeleted && (ModelUtil.isEqualIgnoreCase(uc.getDeletedFlag(), ApicConstants.CODE_YES) ||
            isParentLevelForUCDeleted(uc))) {
          continue;
        }
        addUcpaForUsecase(favUcpAttrSet, model.getProjectUsecaseIdSet(), uc, includeDeleted);

        // add items higher in hierarchy
        addParentLevelItemsForUC(uc, model.getParentUsecaseItemIdSet());


      }
      else if (useCaseFav.getSectionId() != null) {
        TabvUseCaseSection ucSection =
            new UseCaseSectionLoader(getServiceData()).getEntityObject(useCaseFav.getSectionId());

        // check to exclude deleted items based on flag
        if (!includeDeleted && (ModelUtil.isEqualIgnoreCase(ucSection.getDeletedFlag(), ApicConstants.CODE_YES) ||
            isParentLevelForSectionDeleted(ucSection))) {
          continue;
        }
        addUcpaForUsecaseSection(favUcpAttrSet, model.getProjectUsecaseIdSet(), ucSection, includeDeleted);

        // add items higher in hierarchy
        addParentLevelItemsForSection(ucSection, model.getParentUsecaseItemIdSet());

      }

    }
    favUcpAttrSet.forEach(ucpa -> model.getProjectUsecaseAttrIdSet().add(ucpa.getAttrId()));

    return model;

  }


  public boolean isParentLevelForGrpDeleted(final TabvUseCaseGroup ucGrp) {

    TabvUseCaseGroup parentGrp = ucGrp.getTabvUseCaseGroup();

    if (CommonUtils.isNotNull(parentGrp)) {

      if (ModelUtil.isEqualIgnoreCase(parentGrp.getDeletedFlag(), ApicConstants.CODE_YES)) {
        return true;
      }
      isParentLevelForGrpDeleted(parentGrp);

    }

    return false;


  }


  public boolean isParentLevelForUCDeleted(final TabvUseCase usecase) {

    TabvUseCaseGroup parentGrp = usecase.getTabvUseCaseGroup();

    if (CommonUtils.isNotNull(parentGrp)) {

      if (ModelUtil.isEqualIgnoreCase(parentGrp.getDeletedFlag(), ApicConstants.CODE_YES)) {
        return true;
      }
      isParentLevelForGrpDeleted(parentGrp);

    }

    return false;


  }

  public boolean isParentLevelForSectionDeleted(final TabvUseCaseSection section) {

    TabvUseCaseSection parentSection = section.getTabvUseCaseSection();


    if (CommonUtils.isNotNull(parentSection)) {
      if (ModelUtil.isEqualIgnoreCase(parentSection.getDeletedFlag(), ApicConstants.CODE_YES)) {
        return true;
      }
      isParentLevelForSectionDeleted(parentSection);

    }
    TabvUseCase parentUsecase = section.getTabvUseCas();
    if (CommonUtils.isNotNull(parentUsecase)) {
      if (ModelUtil.isEqualIgnoreCase(parentUsecase.getDeletedFlag(), ApicConstants.CODE_YES)) {
        return true;
      }
      isParentLevelForUCDeleted(parentUsecase);
    }
    return false;

  }


  /**
   * @param ucGrp usecase group
   * @param idSet - id set with usecase item ids
   */
  private void addParentLevelItemsForGrp(final TabvUseCaseGroup ucGrp, final Set<Long> idSet) {

    if (CommonUtils.isNotNull(ucGrp.getTabvUseCaseGroup())) {

      TabvUseCaseGroup parentGrp = ucGrp.getTabvUseCaseGroup();

      idSet.add(parentGrp.getGroupId());

      addParentLevelItemsForGrp(parentGrp, idSet);
    }

  }

  private void addParentLevelItemsForUC(final TabvUseCase usecase, final Set<Long> idSet) {

    TabvUseCaseGroup parentGrp = usecase.getTabvUseCaseGroup();

    if (CommonUtils.isNotNull(parentGrp)) {

      idSet.add(parentGrp.getGroupId());

      addParentLevelItemsForGrp(parentGrp, idSet);
    }
  }

  private void addParentLevelItemsForSection(final TabvUseCaseSection ucSection, final Set<Long> idSet) {

    TabvUseCaseSection parentSection = ucSection.getTabvUseCaseSection();

    TabvUseCase parentUsecase = ucSection.getTabvUseCas();

    if (CommonUtils.isNotNull(parentSection)) {

      idSet.add(parentSection.getSectionId());
      addParentLevelItemsForSection(parentSection, idSet);
    }

    if (CommonUtils.isNotNull(parentUsecase)) {
      idSet.add(parentUsecase.getUseCaseId());
      addParentLevelItemsForUC(parentUsecase, idSet);
    }

  }


  /**
   * @param ucpAttrSet
   * @param tabvUseCaseSection
   * @throws DataException
   */
  private void addUcpaForUsecaseSection(final Set<UcpAttr> ucpAttrSet, final Set<Long> idSet,
      final TabvUseCaseSection tabvUseCaseSection, final boolean includeDeleted)
      throws DataException {
    if (idSet != null) {
      idSet.add(tabvUseCaseSection.getSectionId());
    }
    if (CommonUtils.isNotEmpty(tabvUseCaseSection.getTabvUseCaseSections())) {
      for (TabvUseCaseSection ucsSection : tabvUseCaseSection.getTabvUseCaseSections()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(ucsSection.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        addUcpaForUsecaseSection(ucpAttrSet, idSet, ucsSection, includeDeleted);
      }
    }
    else {
      addUCPAttrToSet(ucpAttrSet, tabvUseCaseSection.getTabvUcpAttrs());
    }
  }

  /**
   * @param ucpAttrSet
   * @param tabvUseCas
   * @throws DataException
   */
  private void addUcpaForUsecase(final Set<UcpAttr> ucpAttrSet, final Set<Long> idSet, final TabvUseCase tabvUseCas,
      final boolean includeDeleted)
      throws DataException {
    if (idSet != null) {
      idSet.add(tabvUseCas.getUseCaseId());
    }
    if (CommonUtils.isNotEmpty(tabvUseCas.getTabvUseCaseSections())) {
      for (TabvUseCaseSection ucsSection : tabvUseCas.getTabvUseCaseSections()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(ucsSection.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        addUcpaForUsecaseSection(ucpAttrSet, idSet, ucsSection, includeDeleted);
      }
    }
    else {
      addUCPAttrToSet(ucpAttrSet, tabvUseCas.getTabvUcpAttrs());
    }
  }

  /**
   * @param ucpAttrSet
   * @param tabvUcpAttrs
   * @throws DataException
   */
  private void addUCPAttrToSet(final Set<UcpAttr> ucpAttrSet, final List<TabvUcpAttr> tabvUcpAttrs)
      throws DataException {
    for (TabvUcpAttr tabvUcpAttr : tabvUcpAttrs) {
      ucpAttrSet.add(getDataObjectByID(tabvUcpAttr.getUcpaId()));
    }

  }

  /**
   * @param ucpAttrSet
   * @param tabvUseCaseGroup
   * @throws DataException
   */
  private void addUcpaForGroup(final Set<UcpAttr> ucpAttrSet, final Set<Long> idSet,
      final TabvUseCaseGroup tabvUseCaseGroup, final boolean includeDeleted)
      throws DataException {

    if (idSet != null) {
      idSet.add(tabvUseCaseGroup.getGroupId());
    }

    if (CommonUtils.isNotEmpty(tabvUseCaseGroup.getTabvUseCaseGroups())) {
      for (TabvUseCaseGroup ucGroup : tabvUseCaseGroup.getTabvUseCaseGroups()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(ucGroup.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        addUcpaForGroup(ucpAttrSet, idSet, ucGroup, includeDeleted);
      }
    }
    else {
      for (TabvUseCase usecase : tabvUseCaseGroup.getTabvUseCases()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(usecase.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        addUcpaForUsecase(ucpAttrSet, idSet, usecase, includeDeleted);
      }
    }
  }

  public Set<IUseCaseItem> getLeafNodes(final Map<Long, UsecaseFavorite> useCaseFavMap, final boolean includeDeleted)
      throws DataException {
    Set<IUseCaseItem> leafNodeSet = new HashSet<>();
    UcpAttrLoader ucpAttrLoader = new UcpAttrLoader(getServiceData());
    for (UsecaseFavorite useCaseFav : useCaseFavMap.values()) {
      if ((null == useCaseFav.getProjectId())) {
        // skip the private usecase nodes
        continue;
      }
      if (useCaseFav.getGroupId() != null) {
        TabvUseCaseGroup ucGrp = new UseCaseGroupLoader(getServiceData()).getEntityObject(useCaseFav.getGroupId());

        // check to exclude deleted items based on flag
        if (!includeDeleted && (ModelUtil.isEqualIgnoreCase(ucGrp.getDeletedFlag(), ApicConstants.CODE_YES) ||
            ucpAttrLoader.isParentLevelForGrpDeleted(ucGrp))) {
          continue;
        }
        getUseCaseGrpLeafNodes(ucGrp, leafNodeSet, includeDeleted);
      }
      else if (useCaseFav.getUseCaseId() != null) {
        TabvUseCase uc = new UseCaseLoader(getServiceData()).getEntityObject(useCaseFav.getUseCaseId());

        // check to exclude deleted items based on flag
        if (!includeDeleted && (ModelUtil.isEqualIgnoreCase(uc.getDeletedFlag(), ApicConstants.CODE_YES) ||
            ucpAttrLoader.isParentLevelForUCDeleted(uc))) {
          continue;
        }
        getUsecaseLeafNodes(uc, leafNodeSet, includeDeleted);
      }
      else if (useCaseFav.getSectionId() != null) {
        TabvUseCaseSection ucSection =
            new UseCaseSectionLoader(getServiceData()).getEntityObject(useCaseFav.getSectionId());

        // check to exclude deleted items based on flag
        if (!includeDeleted && (ModelUtil.isEqualIgnoreCase(ucSection.getDeletedFlag(), ApicConstants.CODE_YES) ||
            ucpAttrLoader.isParentLevelForSectionDeleted(ucSection))) {
          continue;
        }
        getUseCaseSecLeafNodes(ucSection, leafNodeSet, includeDeleted);
      }
    }
    return leafNodeSet;
  }

  /**
   * @param ucsSection
   * @param leafNodeSet
   * @param includeDeleted
   * @throws DataException
   */
  private void getUseCaseSecLeafNodes(final TabvUseCaseSection tabvUseCaseSection, final Set<IUseCaseItem> leafNodeSet,
      final boolean includeDeleted)
      throws DataException {
    if (CommonUtils.isNotEmpty(tabvUseCaseSection.getTabvUseCaseSections())) {
      for (TabvUseCaseSection ucsSection : tabvUseCaseSection.getTabvUseCaseSections()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(ucsSection.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        getUseCaseSecLeafNodes(ucsSection, leafNodeSet, includeDeleted);
      }
    }
    else {
      if (checkIfFMRelevant(tabvUseCaseSection)) {
        leafNodeSet.add(new UseCaseSectionLoader(getServiceData()).createDataObject(tabvUseCaseSection));
      }
    }
  }

  /**
   * @param uc
   * @param leafNodeSet
   * @param includeDeleted
   * @throws DataException
   */
  private void getUsecaseLeafNodes(final TabvUseCase tabvUseCas, final Set<IUseCaseItem> leafNodeSet,
      final boolean includeDeleted)
      throws DataException {
    if (CommonUtils.isNotEmpty(tabvUseCas.getTabvUseCaseSections())) {
      for (TabvUseCaseSection ucsSection : tabvUseCas.getTabvUseCaseSections()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(ucsSection.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        getUseCaseSecLeafNodes(ucsSection, leafNodeSet, includeDeleted);
      }
    }
    else {
      if (checkIfFMRelevant(tabvUseCas)) {
        leafNodeSet.add(new UseCaseLoader(getServiceData()).createDataObject(tabvUseCas));
      }
    }
  }

  /**
   * @param ucGrp
   * @param leafNodeSet
   * @param includeDeleted
   * @throws DataException
   */
  private void getUseCaseGrpLeafNodes(final TabvUseCaseGroup tabvUseCaseGroup, final Set<IUseCaseItem> leafNodeSet,
      final boolean includeDeleted)
      throws DataException {

    if (CommonUtils.isNotEmpty(tabvUseCaseGroup.getTabvUseCaseGroups())) {
      for (TabvUseCaseGroup ucGroup : tabvUseCaseGroup.getTabvUseCaseGroups()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(ucGroup.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        getUseCaseGrpLeafNodes(ucGroup, leafNodeSet, includeDeleted);
      }
    }
    else {
      for (TabvUseCase tabvUseCas : tabvUseCaseGroup.getTabvUseCases()) {
        if (!includeDeleted && ModelUtil.isEqualIgnoreCase(tabvUseCas.getDeletedFlag(), ApicConstants.CODE_YES)) {
          continue;
        }
        getUsecaseLeafNodes(tabvUseCas, leafNodeSet, includeDeleted);
      }
    }
  }

  /**
   * @param tabvUseCas
   * @return
   */
  private boolean checkIfFMRelevant(final TabvUseCase tabvUseCas) {
    return (null != tabvUseCas.getFocusMatrixRelevant()) &&
        tabvUseCas.getFocusMatrixRelevant().equals(ApicConstants.CODE_YES);
  }

  /**
   * @param tabvUseCaseSection
   * @return
   */
  private boolean checkIfFMRelevant(final TabvUseCaseSection tabvUseCaseSection) {
    if (checkIfUseCaseSecFMRelevant(tabvUseCaseSection, false)) {
      return true;
    }
    else {
      return checkIfUseCaseSecFMRelevant(tabvUseCaseSection, true);
    }
  }

  private boolean checkIfUseCaseSecFMRelevant(final TabvUseCaseSection tabvUseCaseSection, final boolean checkParent) {
    boolean isRelevant = (null != tabvUseCaseSection.getFocusMatrixRelevant()) &&
        tabvUseCaseSection.getFocusMatrixRelevant().equals(ApicConstants.CODE_YES);
    if (checkParent && !isRelevant &&
        ((null != tabvUseCaseSection.getTabvUseCas()) || (null != tabvUseCaseSection.getTabvUseCaseSection()))) {
      if (tabvUseCaseSection.getTabvUseCaseSection() != null) {
        isRelevant = checkIfUseCaseSecFMRelevant(tabvUseCaseSection.getTabvUseCaseSection(), true);
      }
      else if (tabvUseCaseSection.getTabvUseCas() != null) {
        isRelevant = checkIfFMRelevant(tabvUseCaseSection.getTabvUseCas());
      }
    }
    return isRelevant;

  }

}
