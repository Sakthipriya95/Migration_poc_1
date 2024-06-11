/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.uc;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;

/**
 * @author dja7cob
 */
public class UseCaseGroupCommonBO extends IUsecaseItemCommonBO {


  private final UseCaseGroupCommonBO parentUcg;
  private final UseCaseGroup useCaseGroup;
  private UsecaseTreeGroupModel ucTreeModel;
  /**
   * child usecases
   */
  private final Set<UsecaseCommonBO> childUcBOSet = new HashSet<>();

  /**
   * child groups
   */
  private final Set<UseCaseGroupCommonBO> childUcgBOSet = new HashSet<>();

  /**
   * @param useCaseGroup UseCaseGroup
   * @param ucTreeModel UsecaseTreeGroupModel
   * @param parentUcg
   */
  public UseCaseGroupCommonBO(final UseCaseGroup useCaseGroup, final UsecaseTreeGroupModel ucTreeModel,
      final UseCaseGroupCommonBO parentUcg) {
    super(useCaseGroup);
    this.useCaseGroup = useCaseGroup;
    this.parentUcg = parentUcg;
    this.ucTreeModel = ucTreeModel;
    createChildSetAndParent();
  }

  /**
   * @param childGroupSetMap
   * @param childUsecaseSetMap
   */
  private void createChildSetAndParent() {
    // create child usecase group set
    Set<Long> childUCGSet = this.ucTreeModel.getChildGroupSetMap().get(this.useCaseGroup.getId());
    if (null != childUCGSet) {
      for (Long childGroupId : childUCGSet) {
        this.childUcgBOSet.add(new UseCaseGroupCommonBO(this.ucTreeModel.getUseCaseGroupMap().get(childGroupId),
            this.ucTreeModel, this.parentUcg));
      }
    }
    // create child usecase set
    Set<Long> childUCSet = this.ucTreeModel.getChildUsecaseSetMap().get(this.useCaseGroup.getId());
    if (null != childUCSet) {
      for (Long childUCId : childUCSet) {
        this.childUcBOSet.add(new UsecaseCommonBO(this.ucTreeModel.getUsecaseMap().get(childUCId), this));
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.useCaseGroup.getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String user) {
    this.useCaseGroup.setCreatedUser(user);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.useCaseGroup.getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.useCaseGroup.setModifiedUser(modifiedUser);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.useCaseGroup.getCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    this.useCaseGroup.setCreatedDate(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.useCaseGroup.getModifiedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String date) {
    this.useCaseGroup.setModifiedDate(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.useCaseGroup.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.useCaseGroup.setId(objId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.useCaseGroup.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.useCaseGroup.setVersion(version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.useCaseGroup.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.useCaseGroup.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.useCaseGroup.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.useCaseGroup.setDescription(description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IUsecaseItemCommonBO getParent() {
    UseCaseGroup parentucg = this.ucTreeModel.getUseCaseGroupMap().get(this.useCaseGroup.getParentGroupId());
    UseCaseGroupCommonBO parentUcgCommonBO = null;
    if (null != parentucg) {
      parentUcgCommonBO = new UseCaseGroupCommonBO(parentucg, this.ucTreeModel, getParentUcG(parentucg));
    }
    return parentUcgCommonBO;
  }

  /**
   * {@inheritDoc}
   */
  private UseCaseGroupCommonBO getParentUcG(final UseCaseGroup ucG) {
    // get the usecase group from data cache
    UseCaseGroup parentucg = this.ucTreeModel.getUseCaseGroupMap().get(ucG.getParentGroupId());
    UseCaseGroupCommonBO parentUcgCommonBO = null;
    if (null != parentucg) {
      parentUcgCommonBO = new UseCaseGroupCommonBO(parentucg, this.ucTreeModel, getParentUcG(parentucg));
    }
    return parentUcgCommonBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMapped(final Attribute attr) {

    // Check whether child groups are mapped
    for (UseCaseGroupCommonBO childUcg : getChildGroups(false)) {
      if (childUcg.isMapped(attr)) {
        return true;
      }
    }

    // Check whether use cases are mapped
    for (UsecaseCommonBO ucase : getUseCases(false)) {
      if (ucase.isMapped(attr)) {
        return true;
      }
    }

    // if no mapping done, then return false
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUsecaseItemCommonBO> getMappableItems() {
    // initialise the treeset of abstract use case item - empty tree set
    return new TreeSet<>();
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of use cases
   */
  public final Set<UsecaseCommonBO> getUseCases(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildUcBOSet();
    }
    final Set<UsecaseCommonBO> ucSet = new HashSet<>();

    for (UsecaseCommonBO ucase : getChildUcBOSet()) {
      if (ucase.isDeleted()) {
        continue;
      }
      ucSet.add(ucase);
    }

    return ucSet;
  }

  /**
   * @return
   */
  private Set<UsecaseCommonBO> getChildUcBOSet() {
    return this.childUcBOSet;
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of Child Groups
   */
  public final Set<UseCaseGroupCommonBO> getChildGroups(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildUcgBOSet();
    }

    final Set<UseCaseGroupCommonBO> ucgSet = new HashSet<>();

    for (UseCaseGroupCommonBO childUcg : getChildUcgBOSet()) {
      if ((childUcg == null) || childUcg.isDeleted()) {
        continue;
      }
      ucgSet.add(childUcg);
    }

    return ucgSet;

  }

  /**
   * @return
   */
  private Set<UseCaseGroupCommonBO> getChildUcgBOSet() {
    return this.childUcgBOSet;
  }


  /**
   * @return the useCaseGroup
   */
  public UseCaseGroup getUseCaseGroup() {
    return this.useCaseGroup;
  }


  /**
   * @return the ucTreeModel
   */
  public UsecaseTreeGroupModel getUcTreeModel() {
    return this.ucTreeModel;
  }


  /**
   * @param ucTreeModel the ucTreeModel to set
   */
  public void setUcTreeModel(final UsecaseTreeGroupModel ucTreeModel) {
    this.ucTreeModel = ucTreeModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixRelevant(final boolean checkParent) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUsecaseItemCommonBO> getChildUCItems() {
    SortedSet<IUsecaseItemCommonBO> childSet = new TreeSet<>();
    if (CommonUtils.isEmptySet(getChildGroupSet(false))) {
      childSet.addAll(getUseCaseSet(false));
    }
    else {
      childSet.addAll(getChildGroupSet(false));
    }
    return childSet;
  }

  /**
   * Get the sorted set of the Child Groups in this group.
   *
   * @param includeDeleted whether the deleted items to be included or not
   * @return the childGroupSet
   */
  public final SortedSet<UseCaseGroupCommonBO> getChildGroupSet(final boolean includeDeleted) {
    return new TreeSet<>(getChildGroups(includeDeleted));
  }

  /**
   * Get the sorted set of the use cases in this group.
   *
   * @param includeDeleted whether the deleted items to be included or not
   * @return the use Case Set
   */
  public final SortedSet<UsecaseCommonBO> getUseCaseSet(final boolean includeDeleted) {
    return new TreeSet<>(getUseCases(includeDeleted));
  }

}
