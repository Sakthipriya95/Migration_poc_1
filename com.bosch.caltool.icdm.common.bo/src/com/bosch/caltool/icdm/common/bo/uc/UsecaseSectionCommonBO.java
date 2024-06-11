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
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;

/**
 * @author dja7cob
 */
public class UsecaseSectionCommonBO extends IUsecaseItemCommonBO {

  /**
   * UseCase
   */
  private final UseCaseSection useCaseSection;
  /**
   * UsecaseCommonBO
   */
  private final UsecaseCommonBO usecaseCommonBO;
  /**
   * Set<UsecaseSectionCommonBO>
   */
  private Set<UsecaseSectionCommonBO> childSectionSet;
  /**
   * Set<Attribute>
   */
  private Set<Attribute> ucsAttrSet;
  /**
   * UsecaseEditorModel
   */
  private UsecaseEditorModel usecaseEditorModel;

  /**
   * @param usecaseEditorModel UsecaseEditorModel
   * @param useCaseSection UseCase
   * @param usecaseCommonBO instance
   */
  public UsecaseSectionCommonBO(final UseCaseSection useCaseSection, final UsecaseEditorModel usecaseEditorModel,
      final UsecaseCommonBO usecaseCommonBO) {
    super(useCaseSection);
    this.useCaseSection = useCaseSection;
    this.usecaseCommonBO = usecaseCommonBO;
    this.usecaseEditorModel = usecaseEditorModel;
  }

  /**
   * @return boolean
   */
  @Override
  public boolean isDeleted() {
    return this.useCaseSection.isDeleted();
  }

  /**
   * @return the useCaseSection
   */
  public UseCaseSection getUseCaseSection() {
    return this.useCaseSection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      return this.useCaseSection.equals(((UsecaseSectionCommonBO) obj).getUseCaseSection());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.useCaseSection.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.useCaseSection.getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String user) {
    this.useCaseSection.setCreatedUser(user);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.useCaseSection.getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.useCaseSection.setModifiedUser(modifiedUser);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.useCaseSection.getCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    this.useCaseSection.setCreatedDate(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.useCaseSection.getModifiedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String date) {
    this.useCaseSection.setModifiedDate(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.useCaseSection.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.useCaseSection.setId(objId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.useCaseSection.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.useCaseSection.setVersion(version);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.useCaseSection.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.useCaseSection.setName(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.useCaseSection.getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.useCaseSection.setDescription(description);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IUsecaseItemCommonBO getParent() {
    if (this.useCaseSection.getParentSectionId() != null) {
      return new UsecaseSectionCommonBO(
          this.usecaseEditorModel.getUcSectionMap().get(this.useCaseSection.getParentSectionId()),
          this.usecaseEditorModel, this.usecaseCommonBO);
    }

    return this.usecaseCommonBO;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMapped(final Attribute attr) {
    if (canMapAttributes()) {
      if (!CommonUtils.isNullOrEmpty(getAttributes()) && getAttributes().contains(attr)) {
        return true;
      }
    }
    else {
      for (UsecaseSectionCommonBO ucsChild : getChildSections(false)) {
        if (ucsChild.isMapped(attr)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @param includeDeleted boolean
   * @return Set<UsecaseSectionCommonBO>
   */
  public Set<UsecaseSectionCommonBO> getChildSections(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildSections();
    }

    final Set<UsecaseSectionCommonBO> childUcsSet = new HashSet<>();
    for (UsecaseSectionCommonBO ucs : getChildSections()) {
      if (ucs.isDeleted()) {
        continue;
      }
      childUcsSet.add(ucs);
    }
    return childUcsSet;
  }

  /**
   * @param usecaseEditorModel UsecaseEditorModel
   * @return Set<UsecaseSectionCommonBO>
   */
  private Set<UsecaseSectionCommonBO> getChildSections() {
    if (null == this.childSectionSet) {
      this.childSectionSet = new HashSet<>();
      if (null != this.usecaseEditorModel.getChildSectionsMap().get(this.useCaseSection.getId())) {
        for (Long childSectionId : this.usecaseEditorModel.getChildSectionsMap().get(this.useCaseSection.getId())) {
          UseCaseSection childUCSection = this.usecaseEditorModel.getUcSectionMap().get(childSectionId);
          UsecaseSectionCommonBO childUcSection =
              new UsecaseSectionCommonBO(childUCSection, this.usecaseEditorModel, this.usecaseCommonBO);
          this.childSectionSet.add(childUcSection);
        }
      }
    }
    return this.childSectionSet;
  }


  /**
   * @return boolean
   */
  protected boolean canMapAttributes() {
    if (!isDeleted() && !isParentLevelDeleted()) {
      Set<Long> childIDSet = this.usecaseEditorModel.getChildSectionsMap().get(this.useCaseSection.getId());
      if ((childIDSet == null) || (childIDSet.isEmpty())) {
        return true;
      }
    }

    return false;
  }

  /**
   * @return Set<Attribute>
   */
  public Set<Attribute> getAttributes() {
    if (CommonUtils.isNullOrEmpty(this.ucsAttrSet)) {
      this.ucsAttrSet = new HashSet<>();
      for (UcpAttr ucpAttr : this.usecaseEditorModel.getUcpAttr().values()) {
        if (CommonUtils.isEqual(ucpAttr.getSectionId(), this.useCaseSection.getId())) {
          this.ucsAttrSet.add(this.usecaseEditorModel.getAttrMap().get(ucpAttr.getAttrId()));
        }
      }
    }
    return this.ucsAttrSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUsecaseItemCommonBO> getMappableItems() {
    final SortedSet<IUsecaseItemCommonBO> mappableItemSet = new TreeSet<>();
    if (canMapAttributes()) {
      mappableItemSet.add(this);
    }
    else {
      for (UsecaseSectionCommonBO ucsChild : getChildSections(true)) {
        mappableItemSet.addAll(ucsChild.getMappableItems());
      }
    }
    return mappableItemSet;
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
    boolean isRelevant = this.useCaseSection.getFocusMatrixYn();
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
    SortedSet<IUsecaseItemCommonBO> childSet = new TreeSet<>();
    childSet.addAll(getChildSectionSet(false));
    return childSet;
  }

  /**
   * @param includeDeleted boolean
   * @return SortedSet<UsecaseSectionCommonBO>
   */
  public SortedSet<UsecaseSectionCommonBO> getChildSectionSet(final boolean includeDeleted) {
    return new TreeSet<>(getChildSections(includeDeleted));
  }


}
