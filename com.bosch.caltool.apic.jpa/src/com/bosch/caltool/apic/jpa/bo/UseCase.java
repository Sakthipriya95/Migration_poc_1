/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents usecase as stored in the database table TABV_USE_CASES
 *
 * @author bne4cob
 */
public class UseCase extends AbstractUseCaseItem {

  /**
   * Initial capacity of tooltip String builder
   */
  private static final int SB_TOOLTIP_INITSIZE = 50;

  /**
   * Sync lock for use case attribute mapping
   */
  // ICDM-1123 : added since loading attributes mappings done via a separate thread during startup
  private final Object ucAttrSyncLock = new Object();

  /**
   * Sections of this use case
   */
  private Set<UseCaseSection> ucSectionSet;

  /**
   * Attributes directly mapped to this use case
   */
  private Set<Attribute> ucAttrSet;


  /**
   * @return the ucAttrSet
   */
  public Set<Attribute> getUcAttrSet() {
    return this.ucAttrSet;
  }


  /**
   * @param ucAttrSet the ucAttrSet to set
   */
  public void setUcAttrSet(final Set<Attribute> ucAttrSet) {
    this.ucAttrSet = ucAttrSet;
  }

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param ucID unique id of this Use Case
   */
  public UseCase(final ApicDataProvider apicDataProvider, final Long ucID) {
    super(apicDataProvider, ucID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbUseCase(getID()).getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getNameEng() {
    // Get english name for the use case
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCase(getID()).getNameEng();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getNameGer() {
    // Get german name for the use case
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCase(getID()).getNameGer();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getDescEng() {
    // Get english description for the use case
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCase(getID()).getDescEng();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getDescGer() {
    // Get german description for the use case
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCase(getID()).getDescGer();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getCreatedUser() {
    // Get created user for the use case
    if (isIdValid()) {
      return getEntityProvider().getDbUseCase(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Calendar getCreatedDate() {
    // Get usecase created date
    if (isIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbUseCase(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Calendar getModifiedDate() {
    // Get usecase modified date
    if (isIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbUseCase(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getModifiedUser() {
    // Get modified user for the usecase
    if (isIdValid()) {
      return getEntityProvider().getDbUseCase(getID()).getModifiedUser();
    }
    return "";

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean isIdValid() {
    // check if the usecase is valid
    return getEntityProvider().getDbUseCase(getID()) != null;
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return the sections of this use case
   */
  public final SortedSet<UseCaseSection> getUseCaseSectionSet(final boolean includeDeleted) {
    return new TreeSet<UseCaseSection>(getUseCaseSections(includeDeleted));
  }

  /**
   * Returns the set of sections directly below this use case. The set is initialised when the method is invoked for the
   * first time.
   *
   * @return use case section set
   */
  protected final Set<UseCaseSection> getUseCaseSections() {
    if (this.ucSectionSet == null) {
      this.ucSectionSet = new HashSet<UseCaseSection>();
      for (TabvUseCaseSection dbSection : getEntityProvider().getDbUseCase(getID()).getTabvUseCaseSections()) {
        if (dbSection.getTabvUseCaseSection() == null) {
          this.ucSectionSet.add(getDataCache().getUseCaseSection(dbSection.getSectionId()));
        }
      }
    }
    return this.ucSectionSet;
  }

  /**
   * @param includeDeleted include deleted sections
   * @return set of sections
   */
  private Set<UseCaseSection> getUseCaseSections(final boolean includeDeleted) {
    if (includeDeleted) {
      return getUseCaseSections();
    }
    final Set<UseCaseSection> sectionSet = new HashSet<UseCaseSection>();
    for (UseCaseSection ucs : getUseCaseSections()) {
      if (ucs.isDeleted()) {
        continue;
      }
      sectionSet.add(ucs);
    }
    return sectionSet;

  }

  /**
   * Get a sorted set of the Usecase AccessRights
   *
   * @return NodeAccessRight
   */
  public final SortedSet<NodeAccessRight> getAccessRights() {
    // Get node access rights fo the usecase
    return getDataLoader().getNodeAccessRights(getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isDeleted() {
    // Check if the usecase is deleted
    if (isIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbUseCase(getID()).getDeletedFlag());
    }
    return false;
  }


  /**
   * @return the attributes mapped to this section
   */
  protected final Set<Attribute> getAttributes() {
    // Get attributes mapped to the usecase section
    synchronized (this.ucAttrSyncLock) {
      // Icdm-467 Use case DCN
      if (this.ucAttrSet == null) {
        this.ucAttrSet = new HashSet<Attribute>();
        // Check For Null of Tabv Attributes and Use case
        if ((getEntityProvider().getDbUseCase(getID()) != null) &&
            (getEntityProvider().getDbUseCase(getID()).getTabvUcpAttrs() != null)) {
          addToUcAttrSet();
        }

      }
    }
    return this.ucAttrSet;
  }


  /**
   * 
   */
  private void addToUcAttrSet() {
    for (TabvUcpAttr dbUcpAttr : getEntityProvider().getDbUseCase(getID()).getTabvUcpAttrs()) {
      if (null != dbUcpAttr.getTabvAttribute()) {
        this.ucAttrSet.add(getDataCache().getAttribute(dbUcpAttr.getTabvAttribute().getAttrId()));
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isMapped(final Attribute attr) {
    if (!isIdValid()) {
      return false;
    }
    if (canMapAttributes()) {
      if (getAttributes().contains(attr)) {
        return true;
      }
    }
    else {
      for (UseCaseSection ucSection : getUseCaseSections(false)) {
        if (ucSection.isMapped(attr)) {
          return true;

        }
      }
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean canMapAttributes() {
    if (isIdValid() && !isDeleted() && !isParentLevelDeleted()) {
      return getUseCaseSections().isEmpty();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final SortedSet<AbstractUseCaseItem> getMappableItems() {
    final SortedSet<AbstractUseCaseItem> retSet = new TreeSet<AbstractUseCaseItem>();
    if (canMapAttributes()) {
      retSet.add(this);
    }
    else {
      for (UseCaseSection ucSection : getUseCaseSections(true)) {
        retSet.addAll(ucSection.getMappableItems());
      }
    }
    return retSet;

  }

  /**
   * Returns the mapping entity in table TABV_UCP_ATTR for the given Use Case <-> Attribute mapping
   *
   * @param attr attribute
   * @return TabvUcpAttr entity
   */
  protected final TabvUcpAttr getMappingEntity(final Attribute attr) {
    for (TabvUcpAttr ucpAttr : getEntityProvider().getDbUseCase(getID()).getTabvUcpAttrs()) {
      if (ucpAttr.getTabvAttribute().getAttrId() == attr.getAttributeID().longValue()) {
        return ucpAttr;
      }
    }
    return null;
  }

  // ICDM-301
  @Override
  public boolean isAllUCItemsMapped(final Attribute attribute) {
    // Check for all usecase mappings
    final SortedSet<AbstractUseCaseItem> usecaseItems = getMappableItems();
    boolean isAllUCItemsMapped = true;
    for (AbstractUseCaseItem abstractUseCaseItem : usecaseItems) {
      if (!abstractUseCaseItem.isMapped(attribute)) {
        isAllUCItemsMapped = false;
      }
    }
    return isAllUCItemsMapped;
  }

  // ICDM-301
  @Override
  public boolean isAllUCItemsUnMapped(final Attribute attribute) {
    // Get the mapping information of the usecase items
    final SortedSet<AbstractUseCaseItem> usecaseItems = getMappableItems();
    boolean isAllUCItemsUnMapped = true;
    for (AbstractUseCaseItem abstractUseCaseItem : usecaseItems) {
      if (abstractUseCaseItem.isMapped(attribute)) {
        isAllUCItemsUnMapped = false;
        break;
      }
    }
    return isAllUCItemsUnMapped;
  }

  // ICDM-301
  @Override
  public boolean isAnyUCItemMapped(final Attribute attribute) {
    // Check if any usecase item is mapped
    final SortedSet<AbstractUseCaseItem> usecaseItems = getMappableItems();
    boolean isAnyUCItemMapped = false;
    for (AbstractUseCaseItem abstractUseCaseItem : usecaseItems) {
      if (abstractUseCaseItem.isMapped(attribute)) {
        isAnyUCItemMapped = true;
        break;
      }
    }
    return isAnyUCItemMapped;
  }

  // iCDM-368
  /**
   * Checks whether the UseCase is editable- modifying name,desc,add section delete use case
   */
  @Override
  public boolean isModifiable() {
    // Check whether the usecase or its parent is dleetd
    if (isDeleted() || isParentLevelDeleted()) {
      return false;
    }
    // If Owner, then its allowed to add use case sections and also edit use case
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.isOwner()) {
      return true;
    }
    return false;
  }

  // ICDM-2610
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifyCellAllowed() {
    if (isDeleted() || isParentLevelDeleted()) {
      return false;
    }
    // If Owner, then its allowed to add use case sections and also edit use case
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess()) {
      return true;
    }
    return false;
  }

  /**
   * Checks whether, the mappings are editable -modifying the attributes of the use case
   *
   * @return true, if allowed to modify mapping
   */
  @Override
  public boolean isMappingModifiable() {
    // If UseCase has write access,then the attributes are modifiable
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && (curUserAccRight.hasWriteAccess()) && (!isParentLevelDeleted())) {
      return true;
    }
    return false;
  }


  /**
   * @return The parent group of this use case
   */
  @Override
  public AbstractUseCaseItem getParent() {
    final TabvUseCaseGroup dbUcGrp = getEntityProvider().getDbUseCase(getID()).getTabvUseCaseGroup();
    if (dbUcGrp != null) {
      return getDataCache().getUseCaseGroup(dbUcGrp.getGroupId());
    }
    return null;
  }

  /**
   * @return NodeAccessRight icdm-368 access Rights
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    return getDataCache().getNodeAccRights(getID());
  }


  /**
   * icdm-368 access Rights
   *
   * @return whether the User can edit the access rights Write, Grant,Owner Column for the use case rights
   */
  public boolean canModifyAccessRights() {
    // ICDM-1418
    if (getDataCache().getDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasGrantOption() && !isDeleted()) {
      return true;
    }
    return false;
  }


  // Icdm-467 Use case DCN
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getID().intValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    if (getID().intValue() == ((UseCase) obj).getID().intValue()) {
      return true;
    }
    return false;
  }

  /**
   * @return SortedSet of links
   */
  public SortedSet<Link> getLinks() {
    return getDataCache().getLinks(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.USE_CASE;
  }

  /**
   * {@inheritDoc} returns child usecase section set
   */
  @Override
  public SortedSet<AbstractUseCaseItem> getChildUCItems() {
    // Child itmes of the usecases section
    SortedSet<AbstractUseCaseItem> childSet = new TreeSet<AbstractUseCaseItem>();
    childSet.addAll(getUseCaseSectionSet(false));
    return childSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixRelevant(final boolean checkParent) {
    boolean isRelevant = false;
    if (isIdValid()) {
      // check whether the usecase is focus matrix relevant
      // Set the focus matrix relevance information
      isRelevant = ApicConstants.YES.equals(getEntityProvider().getDbUseCase(getID()).getFocusMatrixRelevant());
      if (checkParent && !isRelevant && (null != getParent())) {
        isRelevant = getParent().isFocusMatrixRelevant(true);
      }
    }
    return isRelevant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<AbstractUseCaseItem> getChildFocusMatrixItems() {
    // If the usecase is focus matrix relevant,
    // Get the child focus matrix items
    SortedSet<AbstractUseCaseItem> childSet = new TreeSet<AbstractUseCaseItem>();
    if (isFocusMatrixRelevant(true)) {
      for (UseCaseSection useCaseSection : getUseCaseSectionSet(false)) {
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
  private Set<UseCaseSection> getFocusMatrixRelevantUseCaseSections() {
    // Get the focus matrix relevant usecase sections
    final Set<UseCaseSection> resultSectionSet = new HashSet<UseCaseSection>();

    for (UseCaseSection useCaseSection : getUseCaseSections()) {
      if ((useCaseSection.isFocusMatrixRelevant(true) || !useCaseSection.getChildFocusMatrixItems().isEmpty()) &&
          !useCaseSection.isDeleted() && !useCaseSection.getMappableItems().isEmpty()) {
        resultSectionSet.add(useCaseSection);
      }
    }
    return resultSectionSet;

  }


  /**
   * This method used to get Focus matrix list for the given attribute
   *
   * @param attribute
   * @return
   */
  private List<FocusMatrixDetails> getFocusMatrix(final Attribute attribute) {
    ApicDataProvider apicDataProvider = (ApicDataProvider) getDataProvider();
    return apicDataProvider.getFocusMatrix(getID(), null, attribute.getID(), null);
  }

  /**
   * Checking the availablity of focus matrix while un mapping the data
   */
  @Override
  public boolean isFocusMatrixAvailableWhileUnMapping(final Attribute attribute) {
    boolean isFocusMatrixAvailable = false;
    List<FocusMatrixDetails> focusMatrixList = getFocusMatrix(attribute);
    for (FocusMatrixDetails focusMatrixDetails : focusMatrixList) {
      Long ucpaId = focusMatrixDetails.getUcpaId();
      if (null != ucpaId) {
        isFocusMatrixAvailable = true;
        break;
      }
    }
    return isFocusMatrixAvailable;
  }

  /**
   * Checking the availablity of focus matrix while mapping the data
   */
  @Override
  public boolean isFocusMatrixAvailableWhileMapping(final Attribute attribute) {
    return CommonUtils.isNotEmpty(getFocusMatrix(attribute));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    // Tooltip for usecase
    // String builder for usecase tooltip
    StringBuilder toolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);

    // Name of the usecase
    toolTip.append("Name : ").append(getName());

    // Descriptio for the usecase
    String desc = getDescription();
    if (null != desc) {
      toolTip.append("\nDescription : ").append(desc);
    }

    // Focus matrix relevance information
    // List of owners
    toolTip.append("\nFocus Matrix Relevant : ").append(isFocusMatrixRelevantStr());
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
   * @return usecase owners
   */
  // ICDM - 2286 Display owners list in tooltip
  public String getListofOwners() {
    SortedSet<NodeAccessRight> rights = getAccessRights();
    StringBuilder ownerToolTip = new StringBuilder(SB_TOOLTIP_INITSIZE);
    for (NodeAccessRight accessRight : rights) {
      // Check the access rights of the user,
      // If the user is the owner , then display the name in tooltip
      if (accessRight.isOwner()) {
        String ownerName = accessRight.getApicUser().getDisplayName();
        ownerToolTip.append(ownerName).append("\n\t\t");
      }
    }
    return ownerToolTip.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getLastConfirmationDate() {
    // Get last confirmation date for the use case
    if (isIdValid()) {
      Timestamp timestamp = getEntityProvider().getDbUseCase(getID()).getLastConfirmationDate();
      if (timestamp != null) {
        return ApicUtil.timestamp2calendar(timestamp);
      }
    }
    return null;
  }

  /**
   * @return whetherer usecase is up to date
   */
  @Override
  public boolean isUpToDate() {
    Calendar lastConfirmationDate = getLastConfirmationDate();
    if (null == lastConfirmationDate) {
      return false;
    }
    long diffInDays = TimeUnit.MILLISECONDS
        .toDays(Math.abs(Calendar.getInstance().getTimeInMillis() - lastConfirmationDate.getTimeInMillis()));
    Long intervalDays = Long
        .valueOf(((ApicDataProvider) getDataProvider()).getParameterValue(ApicConstants.USECASE_UP_TO_DATE_INTERVAL));

    return diffInDays < intervalDays;
  }
}
