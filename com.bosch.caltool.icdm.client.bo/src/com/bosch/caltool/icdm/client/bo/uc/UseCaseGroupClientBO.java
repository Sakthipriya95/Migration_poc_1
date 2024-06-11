/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.uc.UseCaseGroupCommonBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class UseCaseGroupClientBO extends IUseCaseItemClientBO {

  /**
   * child groups
   */
  private final Set<UseCaseGroupClientBO> childUcgBOSet = new HashSet<>();


  /**
   * child usecases
   */
  private final Set<UsecaseClientBO> childUcBOSet = new HashSet<>();
  /**
   * UseCaseGroup
   */
  private final UseCaseGroup useCaseGroup;

  private final UseCaseGroupClientBO parentUcgClientBO;


  private final UseCaseGroupCommonBO ucGCommonBO;


  /**
   * @return the ucGCommonBO
   */
  public UseCaseGroupCommonBO getUcGCommonBO() {
    return this.ucGCommonBO;
  }

  /**
   * @param useCaseGroup UseCaseGroup
   * @param ucTreeModel UsecaseTreeGroupModel
   */
  public UseCaseGroupClientBO(final UseCaseGroup useCaseGroup, final UsecaseTreeGroupModel ucTreeModel,
      final UseCaseGroupClientBO parentUcgClientBO) {
    super(useCaseGroup);
    this.useCaseGroup = useCaseGroup;
    this.parentUcgClientBO = parentUcgClientBO;
    this.ucGCommonBO = new UseCaseGroupCommonBO(useCaseGroup, ucTreeModel, getParentUcG(useCaseGroup, ucTreeModel));
    createChildSetAndParent(ucTreeModel);
  }

  /**
   * {@inheritDoc}
   *
   * @param ucTreeModel
   */
  private UseCaseGroupCommonBO getParentUcG(final UseCaseGroup ucG, final UsecaseTreeGroupModel ucTreeModel) {
    // get the usecase group from data cache
    UseCaseGroup parentucg = ucTreeModel.getUseCaseGroupMap().get(ucG.getParentGroupId());
    UseCaseGroupCommonBO parentUcgClientBO = null;
    if (null != parentucg) {
      parentUcgClientBO = new UseCaseGroupCommonBO(parentucg, ucTreeModel, getParentUcG(parentucg, ucTreeModel));
    }
    return parentUcgClientBO;
  }

  /**
   * @param childGroupSetMap
   * @param childUsecaseSetMap
   */
  private void createChildSetAndParent(final UsecaseTreeGroupModel ucTreeModel) {
    // create child usecase group set
    Set<Long> childUCGSet = ucTreeModel.getChildGroupSetMap().get(this.useCaseGroup.getId());
    if (null != childUCGSet) {
      for (Long childGroupId : childUCGSet) {
        this.childUcgBOSet
            .add(new UseCaseGroupClientBO(ucTreeModel.getUseCaseGroupMap().get(childGroupId), ucTreeModel, this));
      }
    }
    // create child usecase set
    Set<Long> childUCSet = ucTreeModel.getChildUsecaseSetMap().get(this.useCaseGroup.getId());
    if (null != childUCSet) {
      for (Long childUCId : childUCSet) {
        this.childUcBOSet.add(new UsecaseClientBO(ucTreeModel.getUsecaseMap().get(childUCId), this));
      }
    }

  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of use cases
   */
  public final Set<UsecaseClientBO> getUseCases(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildUcBOSet();
    }
    final Set<UsecaseClientBO> ucSet = new HashSet<>();

    for (UsecaseClientBO ucase : getChildUcBOSet()) {
      if (ucase.isDeleted()) {
        continue;
      }
      ucSet.add(ucase);
    }

    return ucSet;
  }

  /**
   * Get the sorted set of the use cases in this group.
   *
   * @param includeDeleted whether the deleted items to be included or not
   * @return the use Case Set
   */
  public final SortedSet<UsecaseClientBO> getUseCaseSet(final boolean includeDeleted) {
    return new TreeSet<>(getUseCases(includeDeleted));
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of Child Groups
   */
  public final Set<UseCaseGroupClientBO> getChildGroups(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildUcgBOSet();
    }

    final Set<UseCaseGroupClientBO> ucgSet = new HashSet<>();

    for (UseCaseGroupClientBO childUcg : getChildUcgBOSet()) {
      if ((childUcg == null) || childUcg.isDeleted()) {
        continue;
      }
      ucgSet.add(childUcg);
    }

    return ucgSet;

  }


  /**
   * Get the sorted set of the Child Groups in this group.
   *
   * @param includeDeleted whether the deleted items to be included or not
   * @return the childGroupSet
   */
  public final SortedSet<UseCaseGroupClientBO> getChildGroupSet(final boolean includeDeleted) {
    return new TreeSet<>(getChildGroups(includeDeleted));
  }

  /**
   * @return boolean
   */
  @Override
  public boolean isDeleted() {
    return this.useCaseGroup.isDeleted();
  }

  /**
   * @return the childUcgBOSet
   */
  public Set<UseCaseGroupClientBO> getChildUcgBOSet() {
    return this.childUcgBOSet;
  }


  /**
   * @return the childUcBOSet
   */
  public Set<UsecaseClientBO> getChildUcBOSet() {
    return this.childUcBOSet;
  }


  /**
   * @return the useCaseGroup
   */
  public UseCaseGroup getUseCaseGroup() {
    return this.useCaseGroup;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      return this.useCaseGroup.equals(((UseCaseGroupClientBO) obj).getUseCaseGroup());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.useCaseGroup.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IUseCaseItemClientBO getParent() {
    return this.parentUcgClientBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMapped(final Attribute attr) {
    return this.ucGCommonBO.isMapped(attr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getMappableItems() {
    // initialise the treeset of abstract use case item - empty tree set
    return new TreeSet<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifyCellAllowed() {
    // Not applicable
    return false;
  }

  /**
   * @return boolean
   */
  @Override
  public boolean isModifiable() {
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (isDeleted() || isParentLevelDeleted() || !currentUser.hasApicWriteAccess()) {
        return false;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return true;

  }

  /**
   * @return if there is already a non deleted Use case we cannot create a sub group
   */
  public boolean canCreateSubGroup() {
    return getUseCases(false).isEmpty();
  }

  /**
   * Use Case Check icdm-368 made in model
   *
   * @return if there is already a non deleted Use case Child group we cannot create a Use Case
   */
  public boolean canCreateUseCase() {
    return getChildGroups(false).isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixAvailableWhileMapping(final AttributeClientBO attribute) {
    /**
     * Not applicable
     */
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixAvailableWhileUnMapping(final AttributeClientBO attribute) {
    /**
     * Not applicable
     */
    return false;
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
  public boolean isFocusMatrixRelevant(final boolean checkParent) {
    // Not Applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getChildFocusMatrixItems() {
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    if (CommonUtils.isNullOrEmpty(getChildGroupSet(false))) {
      childSet.addAll(getFocusMatrixRelevantUseCases(getUseCases(false)));
    }
    else {
      for (UseCaseGroupClientBO childGrp : getChildGroupSet(false)) {
        if (!childGrp.getChildFocusMatrixItems().isEmpty()) {
          childSet.add(childGrp);
        }
      }
    }
    return childSet;
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @param set
   * @return set of use cases
   */
  private final Set<UsecaseClientBO> getFocusMatrixRelevantUseCases(final Set<UsecaseClientBO> set) {

    Set<UsecaseClientBO> ucSet = new HashSet<>();

    for (UsecaseClientBO useCase : set) {
      if ((useCase.isFocusMatrixRelevant(true) || !useCase.getChildFocusMatrixItems().isEmpty()) &&
          !useCase.isDeleted() && !useCase.getMappableItems().isEmpty()) {
        ucSet.add(useCase);
      }
    }
    return ucSet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getChildUCItems() {
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    if (CommonUtils.isEmptySet(getChildGroupSet(false))) {
      childSet.addAll(getUseCaseSet(false));
    }
    else {
      childSet.addAll(getChildGroupSet(false));
    }
    return childSet;
  }
}
