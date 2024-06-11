/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Use Case Section-This class represents usecase section as stored in the database table TABV_USE_CASE_SECTIONS
 *
 * @author bne4cob
 */
public class UseCaseSection extends AbstractUseCaseItem {

  /**
   * Child groups
   */
  private Set<UseCaseSection> childSectionSet;

  /**
   * Attributes directly mapped to this use case
   */
  private Set<Attribute> ucsAttrSet;

  /**
   * Sync lock for use case section attribute mapping
   */
  // ICDM-1123 : added since loading attributes mappings done via a separate thread during startup
  private final Object ucAttrSyncLock = new Object();

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param sectionID unique id of this Section
   */
  public UseCaseSection(final ApicDataProvider apicDataProvider, final Long sectionID) {
    super(apicDataProvider, sectionID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbUseCaseSection(getID()).getVersion();
  }

  /**
   * {@inheritDoc} returns English name of usecase section
   */
  @Override
  public final String getNameEng() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseSection(getID()).getNameEng();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";

  }

  /**
   * {@inheritDoc} returns German name of usecase section
   */
  @Override
  public final String getNameGer() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseSection(getID()).getNameGer();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc} returns English description of usecase section
   */
  @Override
  public final String getDescEng() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseSection(getID()).getDescEng();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc} returns German description of usecase section
   */
  @Override
  public final String getDescGer() {
    if (isIdValid()) {
      String returnValue = getEntityProvider().getDbUseCaseSection(getID()).getDescGer();
      if (returnValue == null) {
        returnValue = "";
      }
      return returnValue;
    }

    return "";
  }

  /**
   * {@inheritDoc} returns created user
   */
  @Override
  public final String getCreatedUser() {
    if (isIdValid()) {
      return getEntityProvider().getDbUseCaseSection(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * {@inheritDoc} returns created date
   */
  @Override
  public final Calendar getCreatedDate() {
    if (isIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbUseCaseSection(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc} returns modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    if (isIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbUseCaseSection(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * {@inheritDoc} returns modified user
   */
  @Override
  public final String getModifiedUser() {
    if (isIdValid()) {
      return getEntityProvider().getDbUseCaseSection(getID()).getModifiedUser();
    }
    return "";

  }


  /**
   * {@inheritDoc} returns true if the usecase section id is valid
   */
  @Override
  protected final boolean isIdValid() {
    return getEntityProvider().getDbUseCaseSection(getID()) != null;
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return the sorted set of child sections
   */
  public final SortedSet<UseCaseSection> getChildSectionSet(final boolean includeDeleted) {
    return new TreeSet<UseCaseSection>(getChildSections(includeDeleted));
  }

  /**
   * @param includeDeleted whether the deleted items to be included or not
   * @return set of child sections
   */
  private Set<UseCaseSection> getChildSections(final boolean includeDeleted) {
    if (includeDeleted) {
      return getChildSections();
    }

    final Set<UseCaseSection> childUcsSet = new HashSet<UseCaseSection>();
    for (UseCaseSection ucs : getChildSections()) {
      if (ucs.isDeleted()) {
        continue;
      }
      childUcsSet.add(ucs);
    }
    return childUcsSet;
  }

  /**
   * Returns the set of sections directly below this section. The set is initialised when the method is invoked for the
   * first time.
   *
   * @return set of child sections
   */
  protected final Set<UseCaseSection> getChildSections() {
    if (this.childSectionSet == null) {
      this.childSectionSet = new HashSet<UseCaseSection>();
      if (null != getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCaseSections()) {
        for (TabvUseCaseSection dbUcs : getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCaseSections()) {
          this.childSectionSet.add(getDataCache().getUseCaseSection(dbUcs.getSectionId()));
        }
      }
    }
    return this.childSectionSet;
  }

  /**
   * @return the attributes mapped to this section
   */
  protected final Set<Attribute> getAttributes() {
    synchronized (this.ucAttrSyncLock) {
      // Icdm-467 Use case DCN
      if (this.ucsAttrSet == null) {
        this.ucsAttrSet = new HashSet<Attribute>();
        // Check For Null of Section and Attributes
        if ((getEntityProvider().getDbUseCaseSection(getID()) != null) &&
            (getEntityProvider().getDbUseCaseSection(getID()).getTabvUcpAttrs() != null)) {
          addToUcsAttrSet();
        }
      }
    }
    return this.ucsAttrSet;
  }

  /**
   * 
   */
  private void addToUcsAttrSet() {
    for (TabvUcpAttr dbUcpAttr : getEntityProvider().getDbUseCaseSection(getID()).getTabvUcpAttrs()) {
      if (null != dbUcpAttr.getTabvAttribute()) {
        this.ucsAttrSet.add(getDataCache().getAttribute(dbUcpAttr.getTabvAttribute().getAttrId()));
      }
    }
  }


  /**
   * Checks whether the given attribute to any one of use case points in this use case section. This will also consider
   * the use case points in child sections
   *
   * @param attr attribute
   * @return true/false
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
      for (UseCaseSection ucsChild : getChildSections(false)) {
        if (ucsChild.isMapped(attr)) {
          return true;
        }
      }
    }

    return false;
  }

  // ICDM-336
  /**
   * @return if the section has section parent
   */
  private final boolean hasSectionParent() {
    final TabvUseCaseSection dbUcSection = getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCaseSection();
    // if dbUcSection is null then no parent
    if (dbUcSection != null) {
      return true;
    }
    return false;
  }

  /**
   * @return use case section if section is parent or use case
   */
  @Override
  public final AbstractUseCaseItem getParent() {
    if (hasSectionParent()) {
      final TabvUseCaseSection dbUcSection = getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCaseSection();
      return getDataCache().getUseCaseSection(dbUcSection.getSectionId());
    }
    // Icdm-358
    final TabvUseCase dbUseCase = getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCas();
    return getDataCache().getUseCase(dbUseCase.getUseCaseId());

  }

  /**
   * {@inheritDoc} returns true if usecase section is a leaf node
   */
  @Override
  protected final boolean canMapAttributes() {
    if (isIdValid() && !isDeleted() && !isParentLevelDeleted()) {
      final List<TabvUseCaseSection> childUcsList =
          getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCaseSections();
      if ((childUcsList == null) || (childUcsList.isEmpty())) {
        return true;
      }
    }

    return false;
  }

  /**
   * {@inheritDoc} returns sorted set of mappable usecase items under the usecase section
   */
  @Override
  public final SortedSet<AbstractUseCaseItem> getMappableItems() {
    final SortedSet<AbstractUseCaseItem> mappableItemSet = new TreeSet<AbstractUseCaseItem>();
    if (canMapAttributes()) {
      mappableItemSet.add(this);
    }
    else {
      for (UseCaseSection ucsChild : getChildSections(true)) {
        mappableItemSet.addAll(ucsChild.getMappableItems());
      }
    }
    return mappableItemSet;
  }

  /**
   * {@inheritDoc} returns true if usecase section is deleted
   */
  @Override
  public final boolean isDeleted() {
    if (isIdValid()) {
      return ApicConstants.YES.equals(getEntityProvider().getDbUseCaseSection(getID()).getDeletedFlag());
    }
    return false;
  }

  /**
   * Returns the mapping entity in table TABV_UCP_ATTR for the given Use Case Section <-> Attribute mapping
   *
   * @param attr attribute
   * @return TabvUcpAttr entity
   */
  protected final TabvUcpAttr getMappingEntity(final Attribute attr) {
    for (TabvUcpAttr ucpAttr : getEntityProvider().getDbUseCaseSection(getID()).getTabvUcpAttrs()) {
      if (ucpAttr.getTabvAttribute().getAttrId() == attr.getAttributeID().longValue()) {
        return ucpAttr;
      }
    }
    return null;
  }

  // ICDM-301
  /**
   * {@inheritDoc} returns true if all usecase items under this usecase are mapped to the attribute
   */
  @Override
  public boolean isAllUCItemsMapped(final Attribute attribute) {
    final SortedSet<AbstractUseCaseItem> usecaseItems = getMappableItems();
    boolean isAllUCItemsMapped = true;
    for (AbstractUseCaseItem abstractUseCaseItem : usecaseItems) {
      if (!abstractUseCaseItem.isMapped(attribute)) {
        isAllUCItemsMapped = false;
        break;
      }
    }
    return isAllUCItemsMapped;
  }


  // ICDM-301
  /**
   * {@inheritDoc} returns true if all usecase items under this usecase are not mapped to the attribute
   */
  @Override
  public boolean isAllUCItemsUnMapped(final Attribute attribute) {
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
  /**
   * {@inheritDoc} returns true if atleast one usecase item under this usecase is mapped to the attribute
   */
  @Override
  public boolean isAnyUCItemMapped(final Attribute attribute) {
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

  // Icdm-368
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
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
   * @return NodeAccessRight icdm-368 additional Check made
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    AbstractUseCaseItem ucItem = getParent();
    while (ucItem instanceof UseCaseSection) {
      ucItem = ucItem.getParent();
    }
    return getDataCache().getNodeAccRights(ucItem.getID());
  }

  /**
   * Checks whether, the mappings are editable -modifying the attributes of the use case section
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
    if (getID().intValue() == ((UseCaseSection) obj).getID().intValue()) {
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
    return EntityType.USE_CASE_SECT;
  }

  /**
   * {@inheritDoc} returns child usecase section set
   */
  @Override
  public SortedSet<AbstractUseCaseItem> getChildUCItems() {
    SortedSet<AbstractUseCaseItem> childSet = new TreeSet<AbstractUseCaseItem>();
    childSet.addAll(getChildSectionSet(false));
    return childSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFocusMatrixRelevant(final boolean checkParent) {
    boolean isRelevant = false;
    if (isIdValid()) {
      isRelevant = ApicConstants.YES.equals(getEntityProvider().getDbUseCaseSection(getID()).getFocusMatrixRelevant());
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

    SortedSet<AbstractUseCaseItem> childSet = new TreeSet<AbstractUseCaseItem>();
    if (isFocusMatrixRelevant(true)) {
      for (UseCaseSection useCaseSection : getChildSectionSet(false)) {
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

    final Set<UseCaseSection> resultSectionSet = new HashSet<UseCaseSection>();
    for (UseCaseSection useCaseSection : getChildSectionSet(false)) {
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
    return apicDataProvider.getFocusMatrix(
        getEntityProvider().getDbUseCaseSection(getID()).getTabvUseCas().getUseCaseId(), getID(), attribute.getID(),
        null);
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
  public Calendar getLastConfirmationDate() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUpToDate() {
    // TODO Auto-generated method stub
    return false;
  }
}
