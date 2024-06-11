/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.uc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;


/**
 * @author BNE4COB
 */
public class UseCaseGroupLoader extends AbstractBusinessObject<UseCaseGroup, TabvUseCaseGroup> {

  /**
   * @param serviceData Service Data
   */
  public UseCaseGroupLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.USE_CASE_GROUP, TabvUseCaseGroup.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected UseCaseGroup createDataObject(final TabvUseCaseGroup entity) throws DataException {


    UseCaseGroup object = new UseCaseGroup();

    setCommonFields(object, entity);

    object.setName(getLangSpecTxt(entity.getNameEng(), entity.getNameGer()));
    object.setDescription(getLangSpecTxt(entity.getDescEng(), entity.getDescGer()));
    if (null != entity.getTabvUseCaseGroup()) {
      object.setParentGroupId(entity.getTabvUseCaseGroup().getGroupId());
    }
    object.setNameEng(entity.getNameEng());
    object.setNameGer(entity.getNameGer());
    object.setDescEng(entity.getDescEng());
    object.setDescGer(entity.getDescGer());
    object.setDeleted(ApicConstants.YES.equals(entity.getDeletedFlag()));

    return object;

  }


  /**
   * Get all Usecase Group records in system
   *
   * @return Map. Key - id, Value - UseCaseGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, UseCaseGroup> getAll() throws DataException {
    Map<Long, UseCaseGroup> objMap = new ConcurrentHashMap<>();
    TypedQuery<TabvUseCaseGroup> tQuery =
        getEntMgr().createNamedQuery(TabvUseCaseGroup.GET_ALL, TabvUseCaseGroup.class);
    List<TabvUseCaseGroup> dbObj = tQuery.getResultList();
    for (TabvUseCaseGroup entity : dbObj) {
      objMap.put(entity.getGroupId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param groupId Use Case group ID
   * @return true, if group is deleted
   */
  public boolean isDeleted(final long groupId) {
    TabvUseCaseGroup entity = getEntityObject(groupId);
    if (ApicConstants.YES.equals(entity.getDeletedFlag())) {
      return true;
    }
    if (entity.getTabvUseCaseGroup() == null) {
      return false;
    }
    return isDeleted(entity.getTabvUseCaseGroup().getGroupId());
  }

  /**
   * Get all Usecase Group records in system
   *
   * @return Map. Key - id, Value - UseCaseGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, UseCaseGroup> getRootUCGroups() throws DataException {
    Map<Long, UseCaseGroup> objMap = new ConcurrentHashMap<>();
    TypedQuery<TabvUseCaseGroup> tQuery =
        getEntMgr().createNamedQuery(TabvUseCaseGroup.GET_ALL, TabvUseCaseGroup.class);
    List<TabvUseCaseGroup> dbObj = tQuery.getResultList();
    for (TabvUseCaseGroup entity : dbObj) {
      if (entity.getTabvUseCaseGroup() == null) {
        objMap.put(entity.getGroupId(), createDataObject(entity));
      }
    }
    return objMap;
  }

  /**
   * Get all Usecase Group records in system
   *
   * @param includeDeleted
   * @return Map. Key - id, Value - UseCaseGroup object
   * @throws DataException error while retrieving data
   */
  public Map<UseCaseGroup, Set<UseCase>> getRootUcGUcs(final boolean includeDeleted) throws DataException {
    TypedQuery<TabvUseCaseGroup> tQuery =
        getEntMgr().createNamedQuery(TabvUseCaseGroup.GET_ALL, TabvUseCaseGroup.class);
    List<TabvUseCaseGroup> dbObj = tQuery.getResultList();
    UseCaseLoader ucLoader = new UseCaseLoader(getServiceData());
    Map<UseCaseGroup, Set<UseCase>> ucGUcMap = new HashMap<>();
    for (TabvUseCaseGroup entity : dbObj) {
      if (entity.getTabvUseCaseGroup() == null) {

        SortedSet<UseCase> ucSet = new TreeSet<>();

        if (ApicConstants.CODE_YES.equals(entity.getDeletedFlag())) {
          if (includeDeleted) {
            UseCaseGroup usecaseGrp = createDataObject(entity);
            for (TabvUseCase ucEntity : entity.getTabvUseCases()) {
              if (ApicConstants.CODE_YES.equals(ucEntity.getDeletedFlag())) {
                // Already include deleted is true here. Line number 139 is taking care of this.
                ucSet.add(ucLoader.createDataObject(ucEntity));
              }
              else {
                ucSet.add(ucLoader.createDataObject(ucEntity));
              }
            }
            ucGUcMap.put(usecaseGrp, ucSet);
          }
        }
        else {
          for (TabvUseCase ucEntity : entity.getTabvUseCases()) {
            if (ApicConstants.CODE_YES.equals(ucEntity.getDeletedFlag())) {
              if (includeDeleted) {
                ucSet.add(ucLoader.createDataObject(ucEntity));
              }
            }
            else {
              ucSet.add(ucLoader.createDataObject(ucEntity));
            }
          }
          UseCaseGroup usecaseGrp = createDataObject(entity);
          ucGUcMap.put(usecaseGrp, ucSet);
        }
      }
    }
    return ucGUcMap;
  }

  /**
   * @param treeModel UsecaseTreeGroupModel
   */
  public void fetchChildrenAndRootGroupsMap(final UsecaseTreeGroupModel treeModel) {

    TypedQuery<TabvUseCaseGroup> tQuery =
        getEntMgr().createNamedQuery(TabvUseCaseGroup.GET_ALL, TabvUseCaseGroup.class);
    List<TabvUseCaseGroup> dbObj = tQuery.getResultList();
    for (TabvUseCaseGroup entity : dbObj) {
      if (entity.getTabvUseCaseGroup() == null) {
        // add root group ids
        treeModel.getRootUCGSet().add(entity.getGroupId());
      }
      if (CommonUtils.isNotEmpty(entity.getTabvUseCaseGroups())) {
        // add child uc groups
        Set<Long> childUCGroupSet = new HashSet<>();
        for (TabvUseCaseGroup childsUseCaseGroup : entity.getTabvUseCaseGroups()) {
          childUCGroupSet.add(childsUseCaseGroup.getGroupId());
        }
        treeModel.getChildGroupSetMap().put(entity.getGroupId(), childUCGroupSet);
      }

      if (CommonUtils.isNotEmpty(entity.getTabvUseCases())) {
        // add child usecases
        Set<Long> childUCSet = new HashSet<>();
        for (TabvUseCase childsUseCase : entity.getTabvUseCases()) {
          childUCSet.add(childsUseCase.getUseCaseId());
        }
        treeModel.getChildUsecaseSetMap().put(entity.getGroupId(), childUCSet);
      }
    }

  }


  /**
   * @param ucGrpId long uc group primary key
   * @param pidcVersId PIDC Version ID
   * @return extended name of this usecase group
   * @throws DataException error while retrieving data
   */
  public String getExtendedName(final Long ucGrpId, final Long pidcVersId) throws DataException {
    final UseCaseGroup usecaseGrp = getDataObjectByID(ucGrpId);
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidcVersId);
    return EXTERNAL_LINK_TYPE.PIDC_VERSION.getTypeDisplayText() + " (Use Case Group): " +
        pidcVersionLoader.getPidcTreePath(pidcVersId) + pidcVersion.getName() + "->" + usecaseGrp.getName();
  }
}
