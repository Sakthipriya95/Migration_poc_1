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
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;

/**
 * @author dja7cob
 */
public class UsecaseCommonBO extends IUsecaseItemCommonBO {

  private UseCase useCase;
  private final UseCaseGroupCommonBO useCaseGroupCommonBO;
  /**
   * Sections of this use case
   */
  private Set<UsecaseSectionCommonBO> ucSectionSet;
  /**
   * UsecaseEditorModel
   */
  private UsecaseEditorModel usecaseEditorModel;
  /**
   * Set<Attribute>
   */
  private Set<Attribute> attrSet;


  /**
   * @param useCase UseCase
   */
  public UsecaseCommonBO(final UseCase useCase, final UseCaseGroupCommonBO useCaseGroupCommonBO) {
    super(useCase);
    this.useCase = useCase;
    this.useCaseGroupCommonBO = useCaseGroupCommonBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.useCase.getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String user) {
    this.useCase.setCreatedUser(user);
  }

  /**
   * @param useCase the useCase to set
   */
  public void setUseCase(final UseCase useCase) {
    this.useCase = useCase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.useCase.getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.useCase.setModifiedUser(modifiedUser);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.useCase.getCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    this.useCase.setCreatedDate(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.useCase.getModifiedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String date) {
    this.useCase.setModifiedDate(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.useCase.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.useCase.setId(objId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.useCase.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.useCase.setVersion(version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.useCase.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.useCase.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.useCase.setDescription(description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IUsecaseItemCommonBO getParent() {
    return this.useCaseGroupCommonBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMapped(final Attribute attr) {

    if (canMapAttributes()) {
      if (getAttributes().contains(attr)) {
        return true;
      }
    }
    else {
      for (UsecaseSectionCommonBO ucSection : getUseCaseSections(false)) {
        if (ucSection.isMapped(attr)) {
          return true;

        }
      }
    }

    return false;
  }

  /**
   * @return SortedSet<IUsecaseItemCommonBO>
   */
  @Override
  public SortedSet<IUsecaseItemCommonBO> getMappableItems() {
    final SortedSet<IUsecaseItemCommonBO> retSet = new TreeSet<>();
    if (canMapAttributes()) {
      retSet.add(this);
    }
    else {
      for (UsecaseSectionCommonBO ucSection : getUseCaseSections(true)) {
        ucSection.setUsecaseEditorModel(this.usecaseEditorModel);
        retSet.addAll(ucSection.getMappableItems());
      }
    }
    return retSet;
  }

  /**
   * @param usecaseEditorModel UsecaseEditorModel
   * @param includeDeleted boolean
   * @return Set<UsecaseSectionCommonBO>
   */
  public Set<UsecaseSectionCommonBO> getUseCaseSections(final boolean includeDeleted) {
    if (includeDeleted) {
      return getUseCaseSections();
    }
    final Set<UsecaseSectionCommonBO> sectionSet = new HashSet<>();
    for (UsecaseSectionCommonBO ucs : getUseCaseSections()) {
      if (ucs.isDeleted()) {
        continue;
      }
      sectionSet.add(ucs);
    }
    return sectionSet;
  }

  /**
   * @return Set<Attribute>
   */
  public Set<Attribute> getAttributes() {
    if (CommonUtils.isNullOrEmpty(this.attrSet)) {
      this.attrSet = new HashSet<>();
      for (UcpAttr ucpAttr : this.usecaseEditorModel.getUcpAttr().values()) {
        this.attrSet.add(this.usecaseEditorModel.getAttrMap().get(ucpAttr.getAttrId()));
      }
    }
    return this.attrSet;
  }

  /**
   * @return
   */
  public boolean canMapAttributes() {
    if (!isDeleted() && !isParentLevelDeleted()) {
      return getUseCaseSections().isEmpty();
    }
    return false;
  }

  /**
   * Returns the set of sections directly below this use case. The set is initialised when the method is invoked for the
   * first time.
   *
   * @return use case section set
   */
  protected final Set<UsecaseSectionCommonBO> getUseCaseSections() {
    if (CommonUtils.isNullOrEmpty(this.ucSectionSet)) {
      this.ucSectionSet = new HashSet<>();
      for (Long sectionId : this.usecaseEditorModel.getFirstLevelUCSIDSet()) {

        UseCaseSection useCaseSection = this.usecaseEditorModel.getUcSectionMap().get(sectionId);
        UsecaseSectionCommonBO ucSection = new UsecaseSectionCommonBO(useCaseSection, this.usecaseEditorModel, this);
        this.ucSectionSet.add(ucSection);
      }
    }
    return this.ucSectionSet;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.useCase.getName();
  }


  /**
   * @return the useCase
   */
  public UseCase getUseCase() {
    return this.useCase;
  }

  /**
   * @return the usecaseEditorModel
   */
  public UsecaseEditorModel getUsecaseEditorModel() {
    return this.usecaseEditorModel;
  }


  /**
   * @param usecaseEditorModel the usecaseEditorModel to set
   */
  public void setUsecaseEditorModel(final UsecaseEditorModel usecaseEditorModel) {
    this.usecaseEditorModel = usecaseEditorModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixRelevant(final boolean checkParent) {
    // check whether the usecase is focus matrix relevant
    // Set the focus matrix relevance information
    boolean isRelevant = this.useCase.getFocusMatrixYn();
    if (checkParent && !isRelevant && (null != getParent())) {
      isRelevant = getParent().isFocusMatrixRelevant(true);
    }

    return isRelevant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUsecaseItemCommonBO> getChildUCItems() {
    // Child itmes of the usecases section
    SortedSet<IUsecaseItemCommonBO> childSet = new TreeSet<IUsecaseItemCommonBO>();
    childSet.addAll(getUseCaseSectionSet(false));
    return childSet;
  }

  /**
   * @param usecaseEditorModel UsecaseEditorModel
   * @param includeDeleted boolean
   * @return SortedSet<UsecaseSectionCommonBO>
   */
  public SortedSet<UsecaseSectionCommonBO> getUseCaseSectionSet(final boolean includeDeleted) {
    return new TreeSet<UsecaseSectionCommonBO>(getUseCaseSections(includeDeleted));
  }

  /**
   * @return the attrSet
   */
  public Set<Attribute> getAttrSet() {
    return this.attrSet;
  }


  /**
   * @param attrSet the attrSet to set
   */
  public void setAttrSet(final Set<Attribute> attrSet) {
    this.attrSet = attrSet;
  }
}
