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
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.IUseCaseItem;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;


/**
 * @author bne4cob
 */
public class UseCaseSectionLoader extends AbstractBusinessObject<UseCaseSection, TabvUseCaseSection> {

  /**
   * @param serviceData Service Data
   */
  public UseCaseSectionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.USE_CASE_SECT, TabvUseCaseSection.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UseCaseSection createDataObject(final TabvUseCaseSection entity) throws DataException {


    UseCaseSection data = new UseCaseSection();
    setCommonFields(data, entity);

    data.setNameEng(entity.getNameEng());
    data.setNameGer(entity.getNameGer());
    data.setDescEng(entity.getDescEng());
    data.setDescGer(entity.getDescGer());
    data.setDeleted(ApicConstants.YES.equals(entity.getDeletedFlag()));
    data.setFocusMatrixYn(ApicConstants.YES.equals(entity.getFocusMatrixRelevant()));
    data.setName(getLangSpecTxt(entity.getNameEng(), entity.getNameGer()));
    data.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));

    if (entity.getTabvUseCaseSection() != null) {
      data.setParentSectionId(entity.getTabvUseCaseSection().getSectionId());
    }
    if (entity.getTabvUseCas() != null) {
      data.setUseCaseId(entity.getTabvUseCas().getUseCaseId());
    }

    return data;
  }

  /**
   * @param sectionId sectionId
   * @return leaf nodes. Key - item ID, Value - Object
   * @throws DataException error while creating data
   */
  public Map<Long, IUseCaseItem> getLeafNodes(final Long sectionId) throws DataException {
    Map<Long, IUseCaseItem> retMap = new HashMap<>();
    TabvUseCaseSection entity = getEntityObject(sectionId);
    if (!ApicConstants.YES.equals(entity.getDeletedFlag())) {
      if (entity.getTabvUseCaseSections().isEmpty()) {
        retMap.put(entity.getSectionId(), createDataObject(entity));
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

    for (TabvUseCaseSection dbUcSection : tabvUseCaseSections) {
      if (!ApicConstants.YES.equals(dbUcSection.getDeletedFlag())) {
        if (dbUcSection.getTabvUseCaseSections().isEmpty()) {
          retMap.put(dbUcSection.getSectionId(), createDataObject(dbUcSection));
        }
        else {
          addLeafNodes(dbUcSection.getTabvUseCaseSections(), retMap);
        }
      }
    }

  }

  /**
   * @param sectionId Use case section Id
   * @return true, if section is focus matrix relevant
   */
  public boolean isFocusMatrixRelevant(final long sectionId) {
    TabvUseCaseSection entity = getEntityObject(sectionId);
    if (ApicConstants.CODE_YES.equals(entity.getDeletedFlag())) {
      return false;
    }
    if (ApicConstants.CODE_YES.equals(entity.getFocusMatrixRelevant())) {
      return !isDeleted(sectionId);
    }
    if (entity.getTabvUseCaseSection() == null) {
      return new UseCaseLoader(getServiceData()).isFocusMatrixRelevant(entity.getTabvUseCas().getUseCaseId());
    }
    return isFocusMatrixRelevant(entity.getTabvUseCaseSection().getSectionId());
  }

  /**
   * @param sectionId Use Case section ID
   * @return true, if section is deleted
   */
  public boolean isDeleted(final long sectionId) {
    TabvUseCaseSection entity = getEntityObject(sectionId);
    if (ApicConstants.YES.equals(entity.getDeletedFlag())) {
      return true;
    }
    if (entity.getTabvUseCaseSection() == null) {
      return new UseCaseLoader(getServiceData()).isDeleted(entity.getTabvUseCas().getUseCaseId());
    }
    return isDeleted(entity.getTabvUseCaseSection().getSectionId());
  }

  /**
   * Get all Usecase Section records in system
   *
   * @return Map. Key - id, Value - UseCaseSection object
   * @throws DataException error while retrieving data
   */
  public Map<Long, UseCaseSection> getAll() throws DataException {
    Map<Long, UseCaseSection> objMap = new ConcurrentHashMap<>();
    TypedQuery<TabvUseCaseSection> tQuery =
        getEntMgr().createNamedQuery(TabvUseCaseSection.GET_ALL_USE_CASE_SECTION, TabvUseCaseSection.class);
    List<TabvUseCaseSection> dbObj = tQuery.getResultList();
    for (TabvUseCaseSection entity : dbObj) {
      objMap.put(entity.getSectionId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param usecaseId Long
   * @param editorModel UsecaseEditorModel
   * @throws DataException error while retrieving data
   */
  public void getUsecaseSections(final Long usecaseId, final UsecaseEditorModel editorModel) throws DataException {
    Map<Long, UseCaseSection> ucSectionsMap = new ConcurrentHashMap<>();
    Map<Long, Set<Long>> childUCSMap = new ConcurrentHashMap<>();
    Set<Long> firstLevelUCS = new HashSet<>();
    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    TabvUseCase tabvUsecase = ucLoader.getEntityObject(usecaseId);
    List<TabvUseCaseSection> dbObj = tabvUsecase.getTabvUseCaseSections();

    for (TabvUseCaseSection entity : dbObj) {
      // iterate through usecase section list
      if (null == entity.getTabvUseCaseSection()) {
        // identify the first level usecase sections
        firstLevelUCS.add(entity.getSectionId());
        ucSectionsMap.put(entity.getSectionId(), createDataObject(entity));
        Set<Long> childUCSId = new HashSet<>();
        getChildUCSDetails(ucSectionsMap, childUCSMap, entity, childUCSId);
      }
    }
    editorModel.getUcSectionMap().putAll(ucSectionsMap);
    editorModel.getChildSectionsMap().putAll(childUCSMap);
    editorModel.getFirstLevelUCSIDSet().addAll(firstLevelUCS);
  }

  /**
   * @param ucSectionsMap
   * @param childUCSMap
   * @param entity
   * @param childUCSId
   * @throws DataException
   */
  private void getChildUCSDetails(final Map<Long, UseCaseSection> ucSectionsMap, final Map<Long, Set<Long>> childUCSMap,
      final TabvUseCaseSection entity, final Set<Long> childUCSId)
      throws DataException {
    if (null != entity.getTabvUseCaseSections()) {
      for (TabvUseCaseSection childUCS : entity.getTabvUseCaseSections()) {
        // iterate through the child usecase sections
        ucSectionsMap.put(childUCS.getSectionId(), createDataObject(childUCS));
        childUCSId.add(childUCS.getSectionId());
        // if there are child for the child section
        processNextLevelChildSections(ucSectionsMap, childUCSMap, childUCS);
      }
      childUCSMap.put(entity.getSectionId(), childUCSId);
    }
  }

  /**
   * @param ucSectionsMap
   * @param childUCSMap
   * @param childUCS
   * @throws DataException
   */
  private void processNextLevelChildSections(final Map<Long, UseCaseSection> ucSectionsMap,
      final Map<Long, Set<Long>> childUCSMap, final TabvUseCaseSection childUCS)
      throws DataException {
    if (null != childUCS.getTabvUseCaseSections()) {

      Set<Long> grandChildUCSIdSet = new HashSet<>();
      getChildUCSDetails(ucSectionsMap, childUCSMap, childUCS, grandChildUCSIdSet);

    }
  }


  /**
   * @param ucCaseSecId long uc case sec primary key
   * @param pidcVersId PIDC Version ID
   * @return extended name of this use case section
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long ucCaseSecId, final Long pidcVersId) throws DataException {
    final UseCaseSection usecaseSec = getDataObjectByID(ucCaseSecId);
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidcVersId);
    return EXTERNAL_LINK_TYPE.PIDC_VERSION.getTypeDisplayText() + " (Use Case Section): " +
        pidcVersionLoader.getPidcTreePath(pidcVersId) + pidcVersion.getName() + "->" + usecaseSec.getName();
  }

}
