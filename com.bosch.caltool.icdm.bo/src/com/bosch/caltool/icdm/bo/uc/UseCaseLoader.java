/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.uc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.bo.uc.IUsecaseItemCommonBO;
import com.bosch.caltool.icdm.common.bo.uc.UseCaseGroupCommonBO;
import com.bosch.caltool.icdm.common.bo.uc.UsecaseCommonBO;
import com.bosch.caltool.icdm.common.bo.uc.UsecaseSectionCommonBO;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UseCaseItemType;
import com.bosch.caltool.icdm.model.uc.UseCaseSectionItemType;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseSectionType;
import com.bosch.caltool.icdm.model.uc.UsecaseType;
import com.bosch.caltool.icdm.model.uc.UsecaseTypeModel;

/**
 * @author bne4cob
 */
public class UseCaseLoader extends AbstractBusinessObject<UseCase, TabvUseCase> {


  /**
   * @param serviceData Service Data
   */
  public UseCaseLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.USE_CASE, TabvUseCase.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UseCase createDataObject(final TabvUseCase entity) throws DataException {


    UseCase object = new UseCase();

    setCommonFields(object, entity);

    object.setName(getLangSpecTxt(entity.getNameEng(), entity.getNameGer()));
    object.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));

    object.setGroupId(entity.getTabvUseCaseGroup().getGroupId());
    object.setNameEng(entity.getNameEng());
    object.setNameGer(entity.getNameGer());
    object.setDescEng(entity.getDescEng());
    object.setDescGer(entity.getDescGer());
    object.setDeleted(ApicConstants.CODE_YES.equals(entity.getDeletedFlag()));
    object.setFocusMatrixYn(ApicConstants.CODE_YES.equals(entity.getFocusMatrixRelevant()));
    object.setLastConfirmationDate(timestamp2String(entity.getLastConfirmationDate()));

    return object;
  }

  /**
   * Get all Usecase records in system
   *
   * @return Map. Key - id, Value - UseCase object
   * @throws DataException error while retrieving data
   */
  public Map<Long, UseCase> getAll() throws DataException {
    Map<Long, UseCase> objMap = new ConcurrentHashMap<>();
    TypedQuery<TabvUseCase> tQuery = getEntMgr().createNamedQuery(TabvUseCase.GET_ALL_USE_CASES, TabvUseCase.class);
    List<TabvUseCase> dbObj = tQuery.getResultList();
    for (TabvUseCase entity : dbObj) {
      objMap.put(entity.getUseCaseId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param usecaseId usecaseId
   * @return leaf nodes. Key - item ID, Value - Object
   * @throws DataException error while creating data
   */
  public Map<Long, IUseCaseItem> getLeafNodes(final Long usecaseId) throws DataException {
    TabvUseCase entity = getEntityObject(usecaseId);
    if (entity == null) {
      throw new DataNotFoundException(getTypeName() + " with ID '" + usecaseId + "' not found");
    }
    Map<Long, IUseCaseItem> retMap = new HashMap<>();
    if (!ApicConstants.CODE_YES.equals(entity.getDeletedFlag())) {
      if (entity.getTabvUseCaseSections().isEmpty()) {
        retMap.put(entity.getUseCaseId(), createDataObject(entity));
      }
      else {
        addLeafNodes(entity.getTabvUseCaseSections(), retMap);
      }
    }
    return retMap;
  }

  /**
   * @param tabvUseCaseSections
   * @param retMap
   * @throws DataException
   */
  private void addLeafNodes(final List<TabvUseCaseSection> tabvUseCaseSections, final Map<Long, IUseCaseItem> retMap)
      throws DataException {
    UseCaseSectionLoader ucsLdr = new UseCaseSectionLoader(getServiceData());
    for (TabvUseCaseSection dbUcSection : tabvUseCaseSections) {
      if (!ApicConstants.CODE_YES.equals(dbUcSection.getDeletedFlag())) {
        retMap.putAll(ucsLdr.getLeafNodes(dbUcSection.getSectionId()));
      }
    }
  }

  /**
   * @param useCaseId Use Case ID
   * @return true, if use case is focus matrix relevant
   */
  public boolean isFocusMatrixRelevant(final long useCaseId) {
    TabvUseCase entity = getEntityObject(useCaseId);
    if (ApicConstants.CODE_YES.equals(entity.getDeletedFlag())) {
      return false;
    }
    if (ApicConstants.CODE_YES.equals(entity.getFocusMatrixRelevant())) {
      return !isDeleted(useCaseId);
    }
    return false;
  }

  /**
   * @param useCaseId Use Case ID
   * @return true, if use case is deleted
   */
  public boolean isDeleted(final long useCaseId) {
    TabvUseCase entity = getEntityObject(useCaseId);
    if (ApicConstants.CODE_YES.equals(entity.getDeletedFlag())) {
      return true;
    }
    return new UseCaseGroupLoader(getServiceData()).isDeleted(entity.getTabvUseCaseGroup().getGroupId());
  }

  /**
   * @param idSet
   * @return
   * @throws IcdmException
   */
  public Set<UsecaseType> getUsecaseTypes(final Set<Long> idSet) throws IcdmException {
    Set<UsecaseType> ucTypeSet = new HashSet<>();
    UseCaseGroupLoader ucGrpLoader = new UseCaseGroupLoader(getServiceData());
    Map<com.bosch.caltool.icdm.model.uc.UseCaseGroup, Set<com.bosch.caltool.icdm.model.uc.UseCase>> ucGUcMap =
        ucGrpLoader.getRootUcGUcs(false);

    Set<UseCase> allUseCases = new HashSet<>();
    for (Set<com.bosch.caltool.icdm.model.uc.UseCase> ucSet : ucGUcMap.values()) {
      allUseCases.addAll(ucSet);
    }
    for (UseCase uc : allUseCases) {
      if (!uc.isDeleted()) {
        if ((null != idSet) && !idSet.isEmpty()) {
          if (idSet.contains(uc.getId())) {
            ucTypeSet.add(createUcType(uc));
          }
        }
        else {
          ucTypeSet.add(createUcType(uc));
        }
      }
    }
    return ucTypeSet;
  }

  /**
   * @param uc
   * @return
   * @throws IcdmException
   */
  private UsecaseType createUcType(final UseCase useCase) throws IcdmException {
    UsecaseDetailsModelLoader ucModelLoader = new UsecaseDetailsModelLoader(getServiceData());
    UsecaseDetailsModel ucDetailsModel = ucModelLoader.getUsecaseDetailsModel();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    com.bosch.caltool.icdm.model.uc.UseCaseGroup ucGroup =
        ucDetailsModel.getUseCaseGroupMap().get(useCase.getGroupId());
    UsecaseType ucType = new UsecaseType();
    setUcTypeFields(ucType, useCase, ucGroup);

    UsecaseCommonBO ucBO = new UsecaseCommonBO(useCase,
        new UseCaseGroupCommonBO(ucGroup, ucDetailsModel, getParentUcG(ucGroup, ucDetailsModel)));
    ucBO.setUsecaseEditorModel(ucDetailsModel.getUsecaseDetailsModelMap().get(ucBO.getUseCase().getId()));
    Set<IUsecaseItemCommonBO> useCaseItems = ucBO.getMappableItems();
    Set<UseCaseItemType> ucItemTypeSet = new HashSet<>();
    if ((null != useCaseItems) && !useCaseItems.isEmpty()) {
      for (IUsecaseItemCommonBO ucItem : useCaseItems) {
        UseCaseItemType ucSItemType = new UseCaseItemType();
        setUcItemTypeModelFields(ucSItemType, ucItem, ucDetailsModel, attrLoader.getAllAttributes(), null);
        ucItemTypeSet.add(ucSItemType);
      }
    }
    ucType.getUcItemTypeSet().addAll(ucItemTypeSet);
    return ucType;
  }

  /**
   * @param ucType
   * @param useCase
   * @param ucGroup
   * @param allAttrMap
   * @return
   * @throws IcdmException
   */
  private void setUcTypeFields(final UsecaseTypeModel ucType, final UseCase useCase, final UseCaseGroup ucGroup)
      throws IcdmException {
    ucType.setUcId(useCase.getId());
    ucType.setUcGroupName(ucGroup.getName());
    if ((ucType.getUcGroupName() == null) || "".equals(ucType.getUcGroupName())) {
      ucType.setUcGroupName("???");
    }
    setNameDesc(ucType, useCase.getNameEng(), useCase.getNameGer(), useCase.getDescEng(), useCase.getDescGer());
    ucType.setDeleted(useCase.isDeleted());

    ucType.setChangeNumber(useCase.getVersion());

    ucType.setCreatedUser(useCase.getCreatedUser());
    if ((null != useCase.getCreatedDate()) && !useCase.getCreatedDate().isEmpty()) {
      ucType.setCreatedDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, useCase.getCreatedDate()));
    }

    ucType.setModifiedUser(useCase.getModifiedUser());
    if ((null != useCase.getModifiedDate()) && !useCase.getModifiedDate().isEmpty()) {
      ucType.setModifiedDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, useCase.getModifiedDate()));
    }
  }

  /**
   * @param ucItemType
   * @param nameEng
   * @param nameGer
   * @param descEng
   * @param descGer
   */
  private void setNameDesc(final UsecaseTypeModel ucItemType, final String nameEngUc, final String nameGerUc,
      final String descEngUc, final String descGerUc) {
    ucItemType.setNameEng(nameEngUc);
    if (CommonUtils.isNotEmptyString(nameGerUc)) {
      ucItemType.setNameGer(nameGerUc);
    }
    else {
      ucItemType.setNameGer(nameEngUc);
    }
    ucItemType.setDescEng(descEngUc);
    if (CommonUtils.isNotEmptyString(descGerUc)) {
      ucItemType.setDescGer(descGerUc);
    }
    else {
      ucItemType.setDescGer(descEngUc);
    }
  }

  /**
   * @param ucGroup
   * @param ucDetailsModel
   * @return
   */
  private UseCaseGroupCommonBO getParentUcG(final com.bosch.caltool.icdm.model.uc.UseCaseGroup ucGroup,
      final UsecaseDetailsModel ucDetailsModel) {
    // get the usecase group from data cache
    com.bosch.caltool.icdm.model.uc.UseCaseGroup parentucg =
        ucDetailsModel.getUseCaseGroupMap().get(ucGroup.getParentGroupId());
    UseCaseGroupCommonBO parentUcgClientBO = null;
    if (null != parentucg) {
      parentUcgClientBO = new UseCaseGroupCommonBO(parentucg, ucDetailsModel, getParentUcG(parentucg, ucDetailsModel));
    }
    return parentUcgClientBO;
  }

  /**
   * @param idSet
   * @return
   * @throws IcdmException
   */
  public Set<UsecaseSectionType> getUsecaseSectionWithTree(final Set<Long> idSet) throws IcdmException {
    Set<UsecaseSectionType> ucTypeSet = new HashSet<>();
    UseCaseGroupLoader ucGrpLoader = new UseCaseGroupLoader(getServiceData());
    Map<com.bosch.caltool.icdm.model.uc.UseCaseGroup, Set<com.bosch.caltool.icdm.model.uc.UseCase>> ucGUcMap =
        ucGrpLoader.getRootUcGUcs(false);

    Set<UseCase> allUseCases = new HashSet<>();
    for (Set<com.bosch.caltool.icdm.model.uc.UseCase> ucSet : ucGUcMap.values()) {
      allUseCases.addAll(ucSet);
    }
    for (UseCase uc : allUseCases) {
      if (!uc.isDeleted()) {
        if ((null != idSet) && !idSet.isEmpty()) {
          if (idSet.contains(uc.getId())) {
            ucTypeSet.add(createUcSType(uc));
          }
        }
        else {
          ucTypeSet.add(createUcSType(uc));
        }
      }
    }
    return ucTypeSet;
  }

  /**
   * @param uc
   * @return
   * @throws IcdmException
   */
  private UsecaseSectionType createUcSType(final UseCase useCase) throws IcdmException {
    UsecaseDetailsModelLoader ucModelLoader = new UsecaseDetailsModelLoader(getServiceData());
    UsecaseDetailsModel ucDetailsModel = ucModelLoader.getUsecaseDetailsModel();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    com.bosch.caltool.icdm.model.uc.UseCaseGroup ucGroup =
        ucDetailsModel.getUseCaseGroupMap().get(useCase.getGroupId());
    UsecaseSectionType ucSType = new UsecaseSectionType();
    setUcTypeFields(ucSType, useCase, ucGroup);

    UsecaseCommonBO ucBO = new UsecaseCommonBO(useCase,
        new UseCaseGroupCommonBO(ucGroup, ucDetailsModel, getParentUcG(ucGroup, ucDetailsModel)));
    ucBO.setUsecaseEditorModel(ucDetailsModel.getUsecaseDetailsModelMap().get(ucBO.getUseCase().getId()));
    Set<UsecaseSectionCommonBO> useCaseSectionItems = ucBO.getUseCaseSections(true);
    Set<UseCaseSectionItemType> ucSItemTypeSet = new HashSet<>();
    for (IUsecaseItemCommonBO ucItem : useCaseSectionItems) {
      UseCaseSectionItemType ucSItemType = new UseCaseSectionItemType();
      setUcItemTypeModelFields(ucSItemType, ucItem, ucDetailsModel, attrLoader.getAllAttributes(), ucSItemTypeSet);
      ucSItemTypeSet.add(ucSItemType);
    }
    ucSType.getUcSItemTypeSet().addAll(ucSItemTypeSet);
    return ucSType;
  }

  /**
   * @param ucSItemType
   * @param ucItem
   * @param ucDetailsModel
   * @param ucSItemTypeSet2
   * @param allAttributes
   * @return
   * @throws IcdmException
   */
  private void setUcItemTypeModelFields(final UsecaseTypeModel ucSItemType, final IUsecaseItemCommonBO useCaseItemBO,
      final UsecaseDetailsModel ucDetailsModel, final Map<Long, Attribute> allAttrMap,
      final Set<UseCaseSectionItemType> ucSItemTypeSet)
      throws IcdmException {

    ucSItemType.setUcId(useCaseItemBO.getId());
    if (useCaseItemBO instanceof UsecaseCommonBO) {
      com.bosch.caltool.icdm.model.uc.UseCase uc = ((UsecaseCommonBO) useCaseItemBO).getUseCase();
      setNameDesc(ucSItemType, uc.getNameEng(), uc.getNameGer(), uc.getDescEng(), uc.getDescGer());
      ((UsecaseCommonBO) useCaseItemBO)
          .setUsecaseEditorModel(ucDetailsModel.getUsecaseDetailsModelMap().get(uc.getId()));
      Set<Long> mappedAttrs =
          ucDetailsModel.getUsecaseDetailsModelMap().get(uc.getId()).getUcItemAttrMapIncDel().get(uc.getId());
      if ((null != mappedAttrs) && !mappedAttrs.isEmpty()) {
        if (ucSItemType instanceof UseCaseItemType) {
          ((UseCaseItemType) ucSItemType).getMappedAttrIds().addAll(mappedAttrs);
        }
        else if (ucSItemType instanceof UseCaseSectionItemType) {
          ((UseCaseSectionItemType) ucSItemType).getMappedAttrIds().addAll(mappedAttrs);
        }
      }
    }
    else if (useCaseItemBO instanceof UsecaseSectionCommonBO) {
      com.bosch.caltool.icdm.model.uc.UseCaseSection ucS = ((UsecaseSectionCommonBO) useCaseItemBO).getUseCaseSection();
      setNameDesc(ucSItemType, ucS.getNameEng(), ucS.getNameGer(), ucS.getDescEng(), ucS.getDescGer());
      ((UsecaseSectionCommonBO) useCaseItemBO)
          .setUsecaseEditorModel(ucDetailsModel.getUsecaseDetailsModelMap().get(ucS.getUseCaseId()));

      if (ucSItemType instanceof UseCaseSectionItemType) {
        if ((null != ((UsecaseSectionCommonBO) useCaseItemBO).getChildSections(true)) &&
            !((UsecaseSectionCommonBO) useCaseItemBO).getChildSections(true).isEmpty()) {
          Set<UsecaseSectionCommonBO> chilsUcSItemsSet =
              ((UsecaseSectionCommonBO) useCaseItemBO).getChildSections(true);
          for (IUsecaseItemCommonBO ucItem : chilsUcSItemsSet) {
            com.bosch.caltool.icdm.model.uc.UseCaseSection childUcS =
                ((UsecaseSectionCommonBO) ucItem).getUseCaseSection();
            UseCaseSectionItemType childUcSItemType = new UseCaseSectionItemType();
            setUcItemTypeModelFields(childUcSItemType, ucItem, ucDetailsModel, allAttrMap, ucSItemTypeSet);
            Set<Long> mappedAttrs = ucDetailsModel.getUsecaseDetailsModelMap().get(childUcS.getUseCaseId())
                .getUcItemAttrMapIncDel().get(childUcS.getId());
            if ((null != mappedAttrs) && !mappedAttrs.isEmpty()) {
              childUcSItemType.getMappedAttrIds().addAll(mappedAttrs);
            }
            ((UseCaseSectionItemType) ucSItemType).getUcSectionItemType().add(childUcSItemType);
          }
        }
        else {
          Set<Long> mappedAttr = ucDetailsModel.getUsecaseDetailsModelMap().get(ucS.getUseCaseId())
              .getUcItemAttrMapIncDel().get(ucS.getId());
          if ((null != mappedAttr) && !mappedAttr.isEmpty()) {
            ((UseCaseSectionItemType) ucSItemType).getMappedAttrIds().addAll(mappedAttr);
          }
        }
      }
      else {
        Set<Long> mappedAttr = ucDetailsModel.getUsecaseDetailsModelMap().get(ucS.getUseCaseId())
            .getUcItemAttrMapIncDel().get(ucS.getId());
        if (CommonUtils.isNotEmpty(mappedAttr)) {
          ((UseCaseItemType) ucSItemType).getMappedAttrIds().addAll(mappedAttr);
        }
      }
    }

    ucSItemType.setCreatedUser(useCaseItemBO.getCreatedUser());
    if ((null != useCaseItemBO.getCreatedDate()) && !useCaseItemBO.getCreatedDate().isEmpty()) {
      ucSItemType.setCreatedDate(
          DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, useCaseItemBO.getCreatedDate()));
    }
    ucSItemType.setModifiedUser(useCaseItemBO.getModifiedUser());
    if ((null != useCaseItemBO.getModifiedDate()) && !useCaseItemBO.getModifiedDate().isEmpty()) {
      ucSItemType.setModifiedDate(
          DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, useCaseItemBO.getModifiedDate()));
    }
    ucSItemType.setChangeNumber(useCaseItemBO.getVersion());
  }


  /**
   * @param ucId long uc case primary key
   * @param pidcVersId PIDC Version ID
   * @return extended name of this usecase
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long ucId, final Long pidcVersId) throws DataException {
    final UseCase usecase = getDataObjectByID(ucId);
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidcVersId);
    return EXTERNAL_LINK_TYPE.PIDC_VERSION.getTypeDisplayText() + " (Use Case): " +
        pidcVersionLoader.getPidcTreePath(pidcVersId) + pidcVersion.getName() + "->" + usecase.getName();
  }
}
