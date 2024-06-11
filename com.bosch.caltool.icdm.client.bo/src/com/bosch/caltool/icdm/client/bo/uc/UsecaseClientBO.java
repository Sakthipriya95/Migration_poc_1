/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.uc.UsecaseCommonBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;

/**
 * @author mkl2cob
 */
public class UsecaseClientBO extends IUseCaseItemClientBO {

  /**
   * Initial capacity of tooltip String builder
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;
  /**
   * UseCase
   */
  private UseCase useCase;
  /**
   * Sections of this use case
   */
  private Set<UseCaseSectionClientBO> ucSectionSet;
  /**
   * parent group
   */
  private final UseCaseGroupClientBO useCaseGroupClientBO;

  private final UsecaseCommonBO ucCommonBO;
  /**
   * Set<Attribute>
   */
  private Set<Attribute> attrSet;
  /**
   * UsecaseEditorModel
   */
  private UsecaseEditorModel usecaseEditorModel;

  /**
   * @param useCase UseCase
   * @param useCaseGroupClientBO UseCaseGroupClientBO
   */
  public UsecaseClientBO(final UseCase useCase, final UseCaseGroupClientBO useCaseGroupClientBO) {
    super(useCase);
    this.useCase = useCase;
    this.useCaseGroupClientBO = useCaseGroupClientBO;
    this.ucCommonBO = new UsecaseCommonBO(useCase, useCaseGroupClientBO.getUcGCommonBO());
  }

  /**
   * @return boolean
   */
  @Override
  public boolean isDeleted() {
    return this.useCase.isDeleted();
  }


  /**
   * @return boolean
   */
  public boolean isUpToDate() {
    Calendar lastConfirmationDate;
    try {
      lastConfirmationDate =
          DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, this.useCase.getLastConfirmationDate());

      if (null == lastConfirmationDate) {
        return false;
      }
      long diffInDays = TimeUnit.MILLISECONDS
          .toDays(Math.abs(Calendar.getInstance().getTimeInMillis() - lastConfirmationDate.getTimeInMillis()));
      CommonDataBO dataBO = new CommonDataBO();
      String upToDateInterval = dataBO.getParameterValue(CommonParamKey.USECASE_UP_TO_DATE_INTERVAL);

      Long intervalDays = Long.valueOf(upToDateInterval);
      return diffInDays < intervalDays;
    }
    catch (IcdmException | ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }
    return false;

  }

  /**
   * @return String
   */
  @Override
  public String getToolTip() {
    // String builder for usecase tooltip
    StringBuilder toolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);
    toolTip.append("Name : ").append(getName());

    String desc = this.useCase.getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }

    toolTip.append("\nFocus Matrix Relevant : ").append(getFocusMatrixRelevantStr());
    toolTip.append("\nIs Up to Date : ");

    if (isUpToDate()) {
      toolTip.append("Yes");
    }
    else {
      toolTip.append("No");
    }

    toolTip.append("\nUse case Owners : ").append(getListofOwners());
    return toolTip.toString();
  }


  /**
   * @return List of owners
   */
  public String getListofOwners() {
    NodeAccessServiceClient nodeAccessClient = new NodeAccessServiceClient();
    StringBuilder ownerToolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);

    try {
      // get the node access details
      NodeAccessDetails nodeAccRights = nodeAccessClient.getNodeAccessDetailsByNode(MODEL_TYPE.USE_CASE, getID());
      if ((null != nodeAccRights) && (null != nodeAccRights.getNodeAccessMap())) {
        // get the user map
        Map<Long, User> userMap = nodeAccRights.getUserMap();
        if (CommonUtils.isNotEmpty(nodeAccRights.getNodeAccessMap())) {
          Set<NodeAccess> nodeAccessForUsecase = nodeAccRights.getNodeAccessMap().values().iterator().next();
          for (NodeAccess nodeAccess : nodeAccessForUsecase) {
            // iterate through the node access rights
            if (nodeAccess.isOwner()) {
              // If the user is the owner , then display the name in tooltip
              ownerToolTip.append(userMap.get(nodeAccess.getUserId()).getDescription()).append("\n\t\t");
            }
          }
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return ownerToolTip.toString();

  }


  /**
   * @return the useCase
   */
  public UseCase getUseCase() {
    return this.useCase;
  }


  /**
   * @return SortedSet<IUseCaseItemClientBO>
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getMappableItems() {
    final SortedSet<IUseCaseItemClientBO> retSet = new TreeSet<>();
    if (canMapAttributes()) {
      retSet.add(this);
    }
    else {
      for (UseCaseSectionClientBO ucSection : getUseCaseSections(true)) {
        retSet.addAll(ucSection.getMappableItems());
      }
    }
    return retSet;
  }

  /**
   * Returns the set of sections directly below this use case. The set is initialised when the method is invoked for the
   * first time.
   *
   * @return use case section set
   */
  protected final Set<UseCaseSectionClientBO> getUseCaseSections() {
    if (this.ucSectionSet == null) {
      this.ucSectionSet = new HashSet<>();
      if (this.usecaseEditorModel != null) {
        for (Long sectionId : this.usecaseEditorModel.getFirstLevelUCSIDSet()) {
          UseCaseSection useCaseSection = this.usecaseEditorModel.getUcSectionMap().get(sectionId);
          this.ucSectionSet.add(new UseCaseSectionClientBO(this.usecaseEditorModel, useCaseSection, this));
        }
      }
    }
    return this.ucSectionSet;
  }

  /**
   * @param includeDeleted boolean
   * @return Set<UseCaseSectionClientBO>
   */
  public Set<UseCaseSectionClientBO> getUseCaseSections(final boolean includeDeleted) {
    if (includeDeleted) {
      return getUseCaseSections();
    }
    final Set<UseCaseSectionClientBO> sectionSet = new HashSet<>();
    for (UseCaseSectionClientBO ucs : getUseCaseSections()) {
      if (ucs.isDeleted()) {
        continue;
      }
      sectionSet.add(ucs);
    }
    return sectionSet;
  }

  /**
   * @param includeDeleted boolean
   * @return SortedSet<UseCaseSectionClientBO>
   */
  public SortedSet<UseCaseSectionClientBO> getUseCaseSectionSet(final boolean includeDeleted) {
    return new TreeSet<>(getUseCaseSections(includeDeleted));
  }

  /**
   * @return boolean
   */
  public boolean canMapAttributes() {
    return this.ucCommonBO.canMapAttributes();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IUseCaseItemClientBO getParent() {
    return this.useCaseGroupClientBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMapped(final Attribute attr) {
    return this.ucCommonBO.isMapped(attr);
  }

  /**
   * @return boolean
   */
  public boolean isMappingModifiable() {
    // If UseCase has write access,then the attributes are modifiable
    CurrentUserBO currentUser = new CurrentUserBO();
    Boolean hasWrite;
    try {
      hasWrite = currentUser.hasNodeWriteAccess(this.useCase.getId());

      return hasWrite && (!isParentLevelDeleted());

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }

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
      hasWrite = currentUser.hasNodeWriteAccess(this.useCase.getId());
      return hasWrite;

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * @return true if the user has access rights
   */
  @Override
  public boolean isModifiable() {
    // Check whether the usecase or its parent is dleetd
    if (isDeleted() || isParentLevelDeleted()) {
      return false;
    }
    // If Owner, then its allowed to add use case sections and also edit use case
    CurrentUserBO currentUser = new CurrentUserBO();
    Boolean hasOwner;
    try {
      hasOwner = currentUser.hasNodeOwnerAccess(this.useCase.getId());
      return hasOwner;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return false;
    }
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
    this.ucCommonBO.setUsecaseEditorModel(usecaseEditorModel);
  }

  /**
   * @return SortedSet<Link>
   */
  public SortedSet<Link> getLinks() {
    LinkServiceClient linkClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkClient.getNodesWithLink(MODEL_TYPE.USE_CASE);
      boolean hasLinks = nodesWithLink.contains(getID());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkClient.getAllLinksByNode(getID(), MODEL_TYPE.USE_CASE);
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
  public boolean isFocusMatrixAvailableWhileMapping(final AttributeClientBO attribute) {
    FocusMatrixServiceClient fmClient = new FocusMatrixServiceClient();
    FocusMatrixMappingData fmMappingData = new FocusMatrixMappingData();
    fmMappingData.setAttrId(attribute.getAttribute().getId());
    fmMappingData.setUseCaseId(getId());
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
    fmMappingData.setUseCaseId(getId());
    try {
      return fmClient.isFocusMatrixAvailableWhileUnMapping(fmMappingData);
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
    this.ucItem = useCase;
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
  public SortedSet<IUseCaseItemClientBO> getChildFocusMatrixItems() {
    // If the usecase is focus matrix relevant,
    // Get the child focus matrix items
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    if (isFocusMatrixRelevant(true)) {
      for (UseCaseSectionClientBO useCaseSection : getUseCaseSectionSet(false)) {
        if (!useCaseSection.getMappableItems().isEmpty()) {
          childSet.add(useCaseSection);
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
    // Get the focus matrix relevant usecase sections
    final Set<UseCaseSectionClientBO> resultSectionSet = new HashSet<>();

    for (UseCaseSectionClientBO useCaseSection : getUseCaseSections()) {
      if ((useCaseSection.isFocusMatrixRelevant(true) || !useCaseSection.getChildFocusMatrixItems().isEmpty()) &&
          !useCaseSection.isDeleted() && !useCaseSection.getMappableItems().isEmpty()) {
        resultSectionSet.add(useCaseSection);
      }
    }
    return resultSectionSet;

  }

  /**
   * Construct a map of use case items with hierarchy details as text
   *
   * @return Map<Long, String> - key: use case item ID, value: complete parent hierarchy
   */
  public Map<Long, String> getUcItemIdsWithHierarchyMap() {
    Map<Long, String> retMap = new HashMap<>();
    for (IUseCaseItemClientBO usecaseItem : getMappableItems()) {
      if (usecaseItem != null) {
        retMap.put(usecaseItem.getUcItem().getId(), constructUcNameWithHierarchy(usecaseItem, usecaseItem.getName()));
      }
    }
    return retMap;
  }

  /**
   * @param parentBo
   */
  private String constructUcNameWithHierarchy(final IUseCaseItemClientBO usecaseItemBo, final String ucName) {
    IUseCaseItemClientBO parentBo = usecaseItemBo.getParent();
    String retName = ucName;
    if (!(parentBo instanceof UseCaseGroupClientBO)) {
      retName = constructUcNameWithHierarchy(parentBo, parentBo.getName() + " >> " + ucName);
    }
    return retName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IUseCaseItemClientBO> getChildUCItems() {
    // Child itmes of the usecases section
    SortedSet<IUseCaseItemClientBO> childSet = new TreeSet<>();
    childSet.addAll(getUseCaseSectionSet(false));
    return childSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      return this.useCase.equals(((UsecaseClientBO) obj).getUseCase());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.useCase.hashCode();
  }

  /**
   *
   */
  public void clearChildMap() {
    this.ucSectionSet = null;
    if (null != this.attrSet) {
      this.attrSet.clear();
    }
  }


  /**
   * @return the ucCommonBO
   */
  public UsecaseCommonBO getUcCommonBO() {
    return this.ucCommonBO;
  }

  /**
   */
  public void resetAttrsInCommonBo() {
    this.ucCommonBO.setAttrSet(null);
  }


  /**
   * @return UseCaseGroupClientBO
   */
  public UseCaseGroupClientBO getUseCaseGroupClientBO() {
    return this.useCaseGroupClientBO;
  }
}
