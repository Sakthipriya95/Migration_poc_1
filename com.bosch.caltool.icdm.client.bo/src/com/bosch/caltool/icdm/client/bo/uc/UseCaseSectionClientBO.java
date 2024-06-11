/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.uc.UsecaseSectionCommonBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author mkl2cob
 */
public class UseCaseSectionClientBO extends IUseCaseItemClientBO {

  /**
   * UseCase
   */
  private final UseCaseSection useCaseSection;
  /**
   * UsecaseClientBO
   */
  private final UsecaseClientBO usecaseClientBO;
  /**
   * Set<UseCaseSectionClientBO>
   */
  private Set<UseCaseSectionClientBO> childSectionSet;
  /**
   * Set<Attribute>
   */
  private Set<Attribute> ucsAttrSet;
  /**
   * UsecaseEditorModel
   */
  private final UsecaseEditorModel usecaseEditorModel;
  /**
   * UsecaseSection CommonBO instance
   */
  private final UsecaseSectionCommonBO ucSCommonBO;

  /**
   * @param usecaseEditorModel UsecaseEditorModel
   * @param useCaseSection UseCase
   * @param usecaseClientBO UsecaseClientBO
   */
  public UseCaseSectionClientBO(final UsecaseEditorModel usecaseEditorModel, final UseCaseSection useCaseSection,
      final UsecaseClientBO usecaseClientBO) {
    super(useCaseSection);
    this.usecaseEditorModel = usecaseEditorModel;
    this.useCaseSection = useCaseSection;
    this.usecaseClientBO = usecaseClientBO;
    this.ucSCommonBO = new UsecaseSectionCommonBO(useCaseSection, usecaseEditorModel, usecaseClientBO.getUcCommonBO());

  }

  /**
   * @return boolean
   */
  @Override
  public boolean isDeleted() {
    return this.useCaseSection.isDeleted();
  }

  /**
   * @return String
   */
  @Override
  public String getName() {
    return this.useCaseSection.getName();
  }


  /**
   * @return the useCaseSection
   */
  public UseCaseSection getUseCaseSection() {
    return this.useCaseSection;
  }

  /**
   * @return SortedSet<IUseCaseItemClientBO>
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getMappableItems() {
    final SortedSet<IUseCaseItemClientBO> mappableItemSet = new TreeSet<>();
    if (canMapAttributes()) {
      mappableItemSet.add(this);
    }
    else {
      for (UseCaseSectionClientBO ucsChild : getChildSections(true)) {
        mappableItemSet.addAll(ucsChild.getMappableItems());
      }
    }
    return mappableItemSet;
  }

  /**
   * @param includeDeleted boolean
   * @return Set<UseCaseSectionClientBO>
   */
  public Set<UseCaseSectionClientBO> getChildSections(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildSections(this.usecaseEditorModel);
    }

    final Set<UseCaseSectionClientBO> childUcsSet = new HashSet<>();
    for (UseCaseSectionClientBO ucs : getChildSections(this.usecaseEditorModel)) {
      if (ucs.isDeleted()) {
        continue;
      }
      childUcsSet.add(ucs);
    }
    return childUcsSet;
  }

  /**
   * @param includeDeleted boolean
   * @return SortedSet<UseCaseSectionClientBO>
   */
  public SortedSet<UseCaseSectionClientBO> getChildSectionSet(final boolean includeDeleted) {
    return new TreeSet<>(getChildSections(includeDeleted));
  }

  /**
   * @param usecaseEditorModel UsecaseEditorModel
   * @return Set<UseCaseSectionClientBO>
   */
  private Set<UseCaseSectionClientBO> getChildSections(final UsecaseEditorModel usecaseEditorModel) {
    if (null == this.childSectionSet) {
      this.childSectionSet = new HashSet<>();
      for (Long childSectionId : usecaseEditorModel.getChildSectionsMap().get(this.useCaseSection.getId())) {
        UseCaseSection childUCSection = usecaseEditorModel.getUcSectionMap().get(childSectionId);
        this.childSectionSet.add(new UseCaseSectionClientBO(usecaseEditorModel, childUCSection, this.usecaseClientBO));
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
   * {@inheritDoc}
   */
  @Override
  public IUseCaseItemClientBO getParent() {
    if (this.useCaseSection.getParentSectionId() != null) {
      return new UseCaseSectionClientBO(this.usecaseEditorModel,
          this.usecaseEditorModel.getUcSectionMap().get(this.useCaseSection.getParentSectionId()),
          this.usecaseClientBO);
    }

    return this.usecaseClientBO;

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
      for (UseCaseSectionClientBO ucsChild : getChildSections(false)) {
        if (ucsChild.isMapped(attr)) {
          return true;
        }
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
  public boolean isModifyCellAllowed() {
    if (isDeleted() || isParentLevelDeleted()) {
      return false;
    }
    CurrentUserBO currentUser = new CurrentUserBO();
    Boolean hasWrite;
    try {
      hasWrite = currentUser.hasNodeWriteAccess(this.usecaseClientBO.getUseCase().getId());
      return hasWrite;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      return false;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    if (isDeleted() || isParentLevelDeleted()) {
      return false;
    }
    // If Owner, then its allowed to add use case sections and also edit use case
    // If Owner, then its allowed to add use case sections and also edit use case
    CurrentUserBO currentUser = new CurrentUserBO();
    Boolean hasOwner;
    try {
      hasOwner = currentUser.hasNodeOwnerAccess(this.usecaseClientBO.getID());
      return hasOwner;
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixAvailableWhileMapping(final AttributeClientBO attribute) {
    FocusMatrixServiceClient fmClient = new FocusMatrixServiceClient();
    FocusMatrixMappingData fmMappingData = new FocusMatrixMappingData();
    fmMappingData.setAttrId(attribute.getAttribute().getId());
    fmMappingData.setUseCaseId(getUseCaseSection().getUseCaseId());
    fmMappingData.setUseCaseSectionId(getId());
    try {
      return fmClient.isFocusMatrixAvailableWhileMapping(fmMappingData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixAvailableWhileUnMapping(final AttributeClientBO attribute) {
    FocusMatrixServiceClient fmClient = new FocusMatrixServiceClient();
    FocusMatrixMappingData fmMappingData = new FocusMatrixMappingData();
    fmMappingData.setAttrId(attribute.getAttribute().getId());
    fmMappingData.setUseCaseId(getUseCaseSection().getUseCaseId());
    fmMappingData.setUseCaseSectionId(getId());
    try {
      return fmClient.isFocusMatrixAvailableWhileUnMapping(fmMappingData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @return SortedSet<Link>
   */
  public SortedSet<Link> getLinks() {
    LinkServiceClient linkClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkClient.getNodesWithLink(MODEL_TYPE.USE_CASE_SECT);
      boolean hasLinks = nodesWithLink.contains(getID());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkClient.getAllLinksByNode(getID(), MODEL_TYPE.USE_CASE_SECT);
        return new TreeSet<>(allLinksByNode.values());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return new TreeSet<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      return this.useCaseSection.equals(((UseCaseSectionClientBO) obj).getUseCaseSection());
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
  public SortedSet<IUseCaseItemClientBO> getChildUCItems() {
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    childSet.addAll(getChildSectionSet(false));
    return childSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getChildFocusMatrixItems() {

    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    if (isFocusMatrixRelevant(true)) {
      for (UseCaseSectionClientBO useCaseSectionBO : getChildSectionSet(false)) {
        if (!useCaseSectionBO.getMappableItems().isEmpty()) {
          childSet.add(useCaseSectionBO);
        }
      }

    }
    else {
      childSet.addAll(getFocusMatrixRelevantUseCaseSections());
    }
    return childSet;
  }

  /**
   * @param includeDeleted include deleted sections
   * @return set of sections
   */
  private Set<UseCaseSectionClientBO> getFocusMatrixRelevantUseCaseSections() {

    final Set<UseCaseSectionClientBO> resultSectionSet = new HashSet<>();
    for (UseCaseSectionClientBO useCaseSectionBO : getChildSectionSet(false)) {
      if ((useCaseSectionBO.isFocusMatrixRelevant(true) || !useCaseSectionBO.getChildFocusMatrixItems().isEmpty()) &&
          !useCaseSectionBO.isDeleted() && !useCaseSectionBO.getMappableItems().isEmpty()) {
        resultSectionSet.add(useCaseSectionBO);
      }
    }
    return resultSectionSet;
  }

  /**
   * @return String
   */
  public String getFMToolTip() {
    StringBuilder toolTip = new StringBuilder();
    toolTip.append(getToolTip());
    toolTip.append("\nIs Up to Date : ");

    if (this.usecaseClientBO.isUpToDate()) {
      toolTip.append("Yes");
    }
    else {
      toolTip.append("No");
    }

    toolTip.append("\nUse case Owners : ").append(this.usecaseClientBO.getListofOwners());
    return toolTip.toString();
  }

}
