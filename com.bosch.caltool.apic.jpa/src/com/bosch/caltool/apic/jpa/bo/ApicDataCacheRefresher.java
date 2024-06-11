/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.bo.AliasDetail.ALIAS_DETAIL_TYPE;
import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayChangeEvent;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.notification.ICacheRefresher;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Refreshes APIC data cache for the changed data received
 *
 * @author bne4cob
 */
final class ApicDataCacheRefresher implements ICacheRefresher {

  /**
   * Data provider
   */
  private final ApicDataProvider dataProvider;

  /**
   * Constructor
   *
   * @param dataProvider apic data provider
   */
  public ApicDataCacheRefresher(final ApicDataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  /**
   * Refreshes the entity identified by the primary key and the class. Calling this method will also update the related
   * data model collections and trigger the GUI notification
   *
   * @param event details of changed data
   */
  @Override
  public void refreshDataCache(final DisplayChangeEvent event) {
    if (event.isEmpty()) {
      return;
    }

    getLogger().debug("Refreshing APIC data cache for the display change event ID : " + event.getEventID());

    final SortedSet<ChangedData> changedDataSet = new TreeSet<ChangedData>(event.getChangedData().values());
    EntityType itemType;

    for (ChangedData item : changedDataSet) {
      if (!(item.getEntityType() instanceof EntityType)) {
        continue;
      }
      if (getLogger().getLogLevel() == ILoggerAdapter.LEVEL_DEBUG) {
        getLogger().debug("Processing Entity - id :" + item.getPrimaryKey() + "; type :" + item.getEntityType() +
            "; change type :" + item.getChangeType());
      }

      itemType = (EntityType) item.getEntityType();
      if (itemType == null) {
        getLogger().warn("Entity :" + item + " skipped as entity not defined in EntityType");
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        continue;
      }
      // Capture the details of old object
      final ApicObject apicObject = itemType.getDataObject(this.dataProvider, item.getPrimaryKey());

      // For the Deleted Object When Trying to Redo Removing from the Cache
      if ((apicObject == null) || (item.getChangeType() == ChangeType.DELETE)) {
        getLogger().warn("Apic object :" + item + " skipped as apic object not found");
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        continue;
      }


      synchronized (this) {

        if (item.getChangeType() == ChangeType.UPDATE) {
          item.setOldDataDetails(apicObject.getObjectDetails());
        }

        refreshBO(event, itemType, item, apicObject);
      }


    }
  }

  /**
   * Refreshes the business object and its children. If new child objects are found, they are added to the DCE
   *
   * @param event display change event
   * @param itemType item type
   * @param item ChangedData item
   * @param apicObject business object
   */
  private void refreshBO(final DisplayChangeEvent event, final EntityType itemType, final ChangedData item,
      final ApicObject apicObject) {


    Map<Long, ?> projAttrsBefore = null;
    Set<Attribute> ucAttrMapBefore = null;
    Set<UseCaseGroup> childGrpBefore = null;
    Set<UseCase> childUcBefore = null;
    Set<UseCaseSection> childUcsBefore = null;
    Map<Long, PIDCVersion> allPidcVerBefore = null;
    Map<Long, PIDCA2l> pidcA2LMappingBefore = null;
    Map<Long, PIDCVariant> allVariantBefore = null;
    Map<Long, PIDCDetStructure> allDetStrBefore = null;
    Map<Long, PIDCSubVariant> allSubVariantBefore = null;

    // Capture the old children, to identify the new children
    switch (itemType) {
      case PIDC:
        PIDCard pidc = this.dataProvider.getPidc(item.getPrimaryKey());
        if (pidc.isVersionsLoaded()) {
          allPidcVerBefore = pidc.getAllVersionsMap();
          pidcA2LMappingBefore = pidc.getDefinedPidcA2lMap();
        }
        break;
      case PIDC_VERSION:
        projAttrsBefore = getProjAttrs(item);
        allDetStrBefore = getProjDetStructAttrs(item);
        allVariantBefore = getVariants(item);

        break;
      case VARIANT:
        projAttrsBefore = getProjAttrs(item);
        allSubVariantBefore = getSubVariants(item);
        break;
      case SUB_VARIANT:
        projAttrsBefore = getProjAttrs(item);
        break;
      case USE_CASE_GROUP:
        final UseCaseGroup useCaseGroup = getDataCache().getUseCaseGroup(item.getPrimaryKey());
        childGrpBefore = useCaseGroup.getChildGroups();
        childUcBefore = useCaseGroup.getUseCases();
        break;
      case USE_CASE:
        final UseCase useCase = getDataCache().getUseCase(item.getPrimaryKey());
        ucAttrMapBefore = useCase.getAttributes();
        childUcsBefore = useCase.getUseCaseSections();
        break;
      case USE_CASE_SECT:
        final UseCaseSection ucs = getDataCache().getUseCaseSection(item.getPrimaryKey());
        ucAttrMapBefore = ucs.getAttributes();
        childUcsBefore = ucs.getChildSections();
        break;
      case NODE_ACCESS:
        if (item.getChangeType() == ChangeType.DELETE) {
          getDataCache().getUserNodeAccRights().remove(item.getPrimaryKey());
        }
        break;
      case ALIAS_DETAIL:
        final AliasDetail aliasDetail = getDataCache().getAliasDetail(item.getPrimaryKey());
        aliasDetail.getAliasDefinition().fillAliasDetMap(true);
        break;
      default:
        break;
    }

    // Refresh the entity in the entity manager and data model
    Object entity = getEntityProvider().getEm().find(item.getEntityClass(), item.getPrimaryKey());
    if (item.getChangeType() == ChangeType.UPDATE) {
      // Refresh is not executed for delete as it will throw a EntityNotFound exception. Inserted records are not
      // identified by EclipseLink directly
      // TODO check these conditions for Undo operations

      final long oldVersion = itemType.getVersion(this.dataProvider, item.getPrimaryKey());

      entity = getEntityProvider().refreshCacheObject(entity);

      final long newVersion = itemType.getVersion(this.dataProvider, item.getPrimaryKey());
      if (oldVersion == newVersion) {
        event.getChangedData().remove(Long.valueOf(item.getPrimaryKey()));
        return;
      }

    }

    apicObject.refresh();

    // Refresh data model cache for child entities
    refreshBOChildren(event, allPidcVerBefore, allVariantBefore, allDetStrBefore, allSubVariantBefore, projAttrsBefore,
        ucAttrMapBefore, childGrpBefore, childUcBefore, childUcsBefore, pidcA2LMappingBefore, item, entity);
  }

  /**
   * @param item
   * @return map of Long, PIDCDetStructure
   */
  private Map<Long, PIDCDetStructure> getProjDetStructAttrs(final ChangedData item) {
    Map<Long, PIDCDetStructure> retMap = new HashMap<>();

    if (CommonUtils.isEqual(item.getEntityType(), EntityType.PIDC_VERSION)) {
      PIDCVersion pidcVers = this.dataProvider.getDataCache().getPidcVersion(item.getPrimaryKey());
      if ((pidcVers != null) && pidcVers.isChildrenLoaded()) {
        // Structure is iterated and added, since the struct map method in PIDC Version returns key as the level number,
        // not structure ID
        for (PIDCDetStructure struct : pidcVers.getVirtualLevelAttrs().values()) {
          retMap.put(struct.getID(), struct);
        }
      }

    }
    return retMap;
  }

  /**
   * Refresh logic for the Child Elements of the business object ICDM-467
   *
   * @param event DisplayChangeEvent
   * @param allDetStrBefore
   * @param projAttrsBefore proj Attrs Before
   * @param ucAttrMapBefore uc Attr Map Before
   * @param childGrpBefore child Grp Before
   * @param childUcBefore child Uc Before
   * @param childUcsBefore child Ucs Before
   * @param pidcA2LMappingBefore
   * @param item ChangedData item
   * @param entity entity
   */
  private void refreshBOChildren(final DisplayChangeEvent event, final Map<Long, PIDCVersion> allPidcVerBefore,
      final Map<Long, PIDCVariant> allVariantBefore, final Map<Long, PIDCDetStructure> allDetStrBefore,
      final Map<Long, PIDCSubVariant> allSubVariantBefore, final Map<Long, ?> projAttrsBefore,
      final Set<Attribute> ucAttrMapBefore, final Set<UseCaseGroup> childGrpBefore, final Set<UseCase> childUcBefore,
      final Set<UseCaseSection> childUcsBefore, final Map<Long, PIDCA2l> pidcA2LMappingBefore, final ChangedData item,
      final Object entity) {

    switch ((EntityType) item.getEntityType()) {
      case SUPER_GROUP:
        getDataLoader().fetchGroups((TabvAttrSuperGroup) entity, event.getChangedData());
        break;

      case GROUP:
        getDataLoader().fetchAttributes((TabvAttrGroup) entity, event.getChangedData());
        break;

      // Icdm-474 dcn for top level entitied
      case TOP_LEVEL_ENTITY:
        refreshTopLevelEntity(entity, event.getChangedData());
        break;

      case ATTRIBUTE:
        getDataLoader().fetchAttrDependencies((TabvAttribute) entity, event.getChangedData());
        fetchAttrValues((TabvAttribute) entity, event.getChangedData());
        getDataLoader().fetchPidcStructure(item.getPrimaryKey());
        break;

      case PIDC:
        findPidcVersions(item, allPidcVerBefore, event.getChangedData());
        findPidcA2lChanges(item, pidcA2LMappingBefore, event.getChangedData());
        break;

      case PIDC_VERSION:
        findNewVariants(item, allVariantBefore, event.getChangedData());
        findDetStructAttrChanges(item, allDetStrBefore, event.getChangedData());
        addNewProjAttrs(item, projAttrsBefore, event.getChangedData());
        break;
      case VARIANT:
        findNewSubVariants(item, allSubVariantBefore, event.getChangedData());
        addNewProjAttrs(item, projAttrsBefore, event.getChangedData());
        break;
      case SUB_VARIANT:
        addNewProjAttrs(item, projAttrsBefore, event.getChangedData());
        break;
      case USE_CASE_GROUP:
        addNewChildGrp(item, childGrpBefore);
        addNewChildUC(item, childUcBefore);
        break;

      case USE_CASE:
      case USE_CASE_SECT:
        addNewUcAttr(item, ucAttrMapBefore, event.getChangedData());
        addNewChildUCS(item, childUcsBefore);
        break;
      case ALIAS_DEFINITION:
        final AliasDefinition aliasDef = getDataCache().getAliasDefMap().get(item.getPrimaryKey());
        aliasDef.fillAliasDetMap(true);
        break;
      case ALIAS_DETAIL:
        caseAliasDetail(item);

        break;
      default:
        break;

      // TODO remove the items from data model, if delete is notified
      // e.g. 1. Var attr moved to pidc 2. undo insert command
      // Delete command does not requires this since this internally is a flag change
    }
  }

  /**
   * @param item
   */
  private void caseAliasDetail(final ChangedData item) {
    if (item.getChangeType() == ChangeType.DELETE) {

      final AliasDetail aliasDetail = getDataCache().getAliasDetail(item.getPrimaryKey());
      if ((aliasDetail.getAliasType() == ALIAS_DETAIL_TYPE.ATTRIBUTE_ALIAS) && aliasDetail.getAliasDefinition()
          .getAliasDetAttrMap().containsKey(aliasDetail.getAttribute().getAttributeID())) {

        aliasDetail.getAliasDefinition().getAliasDetAttrMap().remove(aliasDetail.getAttribute().getAttributeID());
      }
      if ((aliasDetail.getAliasType() == ALIAS_DETAIL_TYPE.VALUE_ALIAS) && aliasDetail.getAliasDefinition()
          .getAliasDetAttrValMap().containsKey(aliasDetail.getAttrValue().getValueID())) {
        aliasDetail.getAliasDefinition().getAliasDetAttrValMap().remove(aliasDetail.getAttrValue().getValueID());
      }
    }
  }

  /**
   * @param item
   * @param pidcA2LMappingBefore
   * @param changedData
   */
  private void findPidcA2lChanges(final ChangedData item, final Map<Long, PIDCA2l> pidcA2LMappingBefore,
      final Map<Long, ChangedData> changedData) {
    if (CommonUtils.isEqual(item.getEntityType(), EntityType.PIDC)) {
      PIDCard pidc = this.dataProvider.getDataCache().getPidc(item.getPrimaryKey());
      if (!pidc.isVersionsLoaded()) {
        return;
      }

      Map<Long, PIDCA2l> pidcA2LMappingAfter = pidc.getDefinedPidcA2lMap();

      // Add changed data for new items
      for (Long newPidcA2lID : CommonUtils.getDifference(pidcA2LMappingAfter, pidcA2LMappingBefore).keySet()) {
        changedData.put(newPidcA2lID,
            new ChangedData(ChangeType.INSERT, newPidcA2lID, TPidcA2l.class, DisplayEventSource.DATABASE));


        // To handle new uploaded a2l files. When a2l files are uploaded, PIDCA2l record is also created.
        // If SDOM PVER - A2L files are already cached, add the new A2L file also. If
        // caching has not taken place, the new A2L file will be retrieved with the next database call.
        // Add the new A2L file to the cache, only if the a2l files are retrieved from DB
        PIDCA2l pidcA2L = pidcA2LMappingAfter.get(newPidcA2lID);
        Long a2lID = pidcA2L.getA2LId();
        Map<Long, A2LFile> retMap =
            this.dataProvider.getDataCache().getPverAllA2LMap(pidc.getID(), pidcA2L.getSdomPverName(), false);
        if ((retMap != null) && (retMap.get(a2lID) != null)) {
          A2LFile a2lFile = new A2LFile(this.dataProvider, a2lID);
          a2lFile.setPidcA2l(pidcA2L);
          retMap.put(a2lFile.getID(), a2lFile);
        }

      }

      // Add changed data for deleted items
      for (Long removedPidcA2lID : CommonUtils.getDifference(pidcA2LMappingBefore, pidcA2LMappingAfter).keySet()) {
        changedData.put(removedPidcA2lID,
            new ChangedData(ChangeType.DELETE, removedPidcA2lID, TPidcA2l.class, DisplayEventSource.DATABASE));
      }

    }
  }

  /**
   * @param item
   * @param allDetStrBefore
   * @param changedData
   */
  private void findDetStructAttrChanges(final ChangedData item, final Map<Long, PIDCDetStructure> allDetStrBefore,
      final Map<Long, ChangedData> changedData) {
    if (CommonUtils.isEqual(item.getEntityType(), EntityType.PIDC_VERSION)) {
      PIDCVersion pidcVers = this.dataProvider.getDataCache().getPidcVersion(item.getPrimaryKey());
      if ((pidcVers == null) || !pidcVers.isChildrenLoaded()) {
        return;
      }

      Map<Long, PIDCDetStructure> allDetStrAfter = getProjDetStructAttrs(item);

      // Add changed data for new items
      for (Long newDetStructID : CommonUtils.getDifference(allDetStrAfter, allDetStrBefore).keySet()) {
        changedData.put(newDetStructID, new ChangedData(ChangeType.INSERT, newDetStructID, TabvPidcDetStructure.class,
            DisplayEventSource.DATABASE));
      }

      // Add changed data for deleted items
      for (Long removedDetSructID : CommonUtils.getDifference(allDetStrBefore, allDetStrAfter).keySet()) {
        changedData.put(removedDetSructID, new ChangedData(ChangeType.DELETE, removedDetSructID,
            TabvPidcDetStructure.class, DisplayEventSource.DATABASE));
      }

    }

  }

  /**
   * @param item
   * @param allSubVariantBefore
   * @param changedData
   */
  private void findNewSubVariants(final ChangedData item, final Map<Long, PIDCSubVariant> allSubVariantBefore,
      final Map<Long, ChangedData> changedData) {
    if (CommonUtils.isEqual(item.getEntityType(), EntityType.VARIANT)) {
      PIDCVariant pidcVar = this.dataProvider.getDataCache().getPidcVaraint(item.getPrimaryKey());
      if (!pidcVar.isChildrenLoaded()) {
        return;
      }

      for (Long newSubVarID : CommonUtils.getDifference(getSubVariants(item), allSubVariantBefore).keySet()) {
        changedData.put(newSubVarID,
            new ChangedData(ChangeType.INSERT, newSubVarID, TabvProjectSubVariant.class, DisplayEventSource.DATABASE));
      }

    }

  }

  /**
   * @param item
   * @param allVariantBefore
   * @param changedData
   */
  private void findNewVariants(final ChangedData item, final Map<Long, PIDCVariant> allVariantBefore,
      final Map<Long, ChangedData> changedData) {

    if (CommonUtils.isEqual(item.getEntityType(), EntityType.PIDC_VERSION)) {
      PIDCVersion pidcVers = this.dataProvider.getDataCache().getPidcVersion(item.getPrimaryKey());
      if ((pidcVers == null) || !pidcVers.isChildrenLoaded()) {
        return;
      }

      for (Long newVarID : CommonUtils.getDifference(getVariants(item), allVariantBefore).keySet()) {
        changedData.put(newVarID,
            new ChangedData(ChangeType.INSERT, newVarID, TabvProjectVariant.class, DisplayEventSource.DATABASE));
      }

    }

  }

  /**
   * @param item
   * @param allPidcVerBefore
   * @param changedData
   */
  private void findPidcVersions(final ChangedData item, final Map<Long, PIDCVersion> allPidcVerBefore,
      final Map<Long, ChangedData> changedDataMap) {

    PIDCard pidc = this.dataProvider.getPidc(item.getPrimaryKey());

    // If active version is not changed or versions are not loaded at all, return
    if ((!item.hasDirectChanges(this.dataProvider) ||
        !item.getDirectChanges(this.dataProvider).containsKey(PIDCard.FLD_PIDC_PRO_REV_ID)) && !pidc.isVersionsLoaded()) {
      return;
    }

    pidc.resetVersions();

    if (pidc.isVersionsLoaded()) {
      final Map<Long, PIDCVersion> allPidcVerAfter = pidc.getAllVersionsMap();

      for (Entry<Long, PIDCVersion> entry : allPidcVerAfter.entrySet()) {
        if (!allPidcVerBefore.containsKey(entry.getKey())) {
          changedDataMap.put(entry.getValue().getID(), new ChangedData(ChangeType.INSERT, entry.getValue().getID(),
              TPidcVersion.class, DisplayEventSource.DATABASE));
        }
      }
    }
  }

  /**
   * Refresh the Top level entities PIDC, UCG,Super Group
   *
   * @param entity
   */
  private void refreshTopLevelEntity(final Object entity, final Map<Long, ChangedData> changedDataMap) {
    final TabvTopLevelEntity toplevelEntity = (TabvTopLevelEntity) entity;
    if (toplevelEntity.getEntityName().equals(ApicConstants.PIDC_NODE_TYPE)) {
      getDataLoader().fetchPidc(changedDataMap);
    }
    else if (toplevelEntity.getEntityName().equals(ApicConstants.ATTR_SUPER_GROUP)) {
      getDataLoader().fetchAllAttributes(getEntityProvider().getEm(), changedDataMap);
    }
    else if (toplevelEntity.getEntityName().equals(ApicConstants.TOP_LEVEL_UCG)) {
      getDataLoader().fetchUCGroups(changedDataMap);
    }
    else if (toplevelEntity.getEntityName().equals(ApicConstants.APIC_USER)) {
      getDataLoader().fetchAllUsers(getEntityProvider().getEm());
    }
  }


  /**
   * Icdm-467 Adds to new Section to a Use Case or Section
   *
   * @param item ChangedData item
   * @param childUcsBefore child Ucs Before
   */
  private void addNewChildUCS(final ChangedData item, final Set<UseCaseSection> childUcsBefore) {
    UseCaseSection ucs;

    // Add the new sections to the parent use case
    if (item.getEntityType() == EntityType.USE_CASE) {
      final UseCase useCase = getDataCache().getUseCase(item.getPrimaryKey());

      for (TabvUseCaseSection dbUcs : this.dataProvider.getEntityProvider().getDbUseCase(useCase.getID())
          .getTabvUseCaseSections()) {
        // ICDM-1034
        ucs = getDataCache().getUseCaseSection(dbUcs.getSectionId());
        if (!childUcsBefore.contains(ucs) && (ucs.getParent() instanceof UseCase)) {
          useCase.getUseCaseSections().add(ucs);
        }
      }
    }

    // Add the new sections to the parent section
    else {
      final UseCaseSection ucSect = getDataCache().getUseCaseSection(item.getPrimaryKey());
      for (TabvUseCaseSection dbUcs : this.dataProvider.getEntityProvider().getDbUseCaseSection(ucSect.getID())
          .getTabvUseCaseSections()) {
        // ICDM-1034
        ucs = getDataCache().getUseCaseSection(dbUcs.getSectionId());
        if (!childUcsBefore.contains(ucs)) {
          ucSect.getChildSections().add(ucs);
        }
      }
    }

  }

  /**
   * Icdm-467 Adds to new USeCase child to a Group
   *
   * @param item ChangedData item
   * @param childUcBefore child Uc Before
   */
  private void addNewChildUC(final ChangedData item, final Set<UseCase> childUcBefore) {
    final UseCaseGroup useCaseGroup = getDataCache().getUseCaseGroup(item.getPrimaryKey());
    UseCase usecase;

    for (TabvUseCase dbUc : this.dataProvider.getEntityProvider().getDbUseCaseGroup(useCaseGroup.getID())
        .getTabvUseCases()) {
      // ICDM-1034
      usecase = getDataCache().getUseCase(dbUc.getUseCaseId());
      if (!childUcBefore.contains(usecase)) {
        useCaseGroup.getUseCases().add(usecase);
      }
    }

  }

  /**
   * Icdm-467 Adds to new Child Group
   *
   * @param item ChangedData item
   * @param childGrpBefore child Grp Before
   */
  private void addNewChildGrp(final ChangedData item, final Set<UseCaseGroup> childGrpBefore) {
    final UseCaseGroup useCaseGroup = getDataCache().getUseCaseGroup(item.getPrimaryKey());
    UseCaseGroup ucg;

    for (TabvUseCaseGroup childDbUcg : this.dataProvider.getEntityProvider().getDbUseCaseGroup(useCaseGroup.getID())
        .getTabvUseCaseGroups()) {
      ucg = new UseCaseGroup(this.dataProvider, childDbUcg.getGroupId());
      if (!childGrpBefore.contains(ucg)) {
        useCaseGroup.getChildGroups().add(ucg);
      }
    }


  }

  /**
   * Icdm-467 Adds new Attr to Use case Or section
   *
   * @param item ChangedData item
   * @param ucAttrMapBefore uc Attr Map Before
   * @param changedDataMap changedData Map
   */
  @SuppressWarnings("null")
  private void addNewUcAttr(final ChangedData item, final Set<Attribute> ucAttrMapBefore,
      final Map<Long, ChangedData> changedDataMap) {

    final Set<Attribute> ucAttrAfter = new HashSet<Attribute>();
    // ICDM-1034 Added to avoid concurrent modification exception during removal
    final Set<Attribute> ucAttrBefore = new HashSet<Attribute>();
    ucAttrBefore.addAll(ucAttrMapBefore);
    UseCase useCase = null;
    UseCaseSection ucs = null;
    if (item.getEntityType() == EntityType.USE_CASE) {
      useCase = setUcAttr(item, ucAttrAfter);
    }
    else {
      ucs = setUcsAttr(item, ucAttrAfter);
    }

    // Add the newly mapped attributes to the use case or use case section
    for (Attribute attr : ucAttrAfter) {
      if (!ucAttrMapBefore.contains(attr)) {
        changedDataMap.put(attr.getID(),
            new ChangedData(ChangeType.INSERT, attr.getID(), TabvUcpAttr.class, DisplayEventSource.DATABASE));
        if ((null != useCase) && (item.getEntityType() == EntityType.USE_CASE)) {
          useCase.getAttributes().add(attr);
        }
        else if ((null != ucs) && (item.getEntityType() == EntityType.USE_CASE_SECT)) {
          ucs.getAttributes().add(attr);
        }

      }
    }

    // Remove all the missing attributes from the use case/sections as they were removed in the remote client
    for (Attribute attr : ucAttrBefore) {
      if (!ucAttrAfter.contains(attr)) {
        changedDataMap.put(attr.getID(),
            new ChangedData(ChangeType.DELETE, attr.getID(), TabvUcpAttr.class, DisplayEventSource.DATABASE));
        if ((null != useCase) && (item.getEntityType() == EntityType.USE_CASE)) {
          useCase.getAttributes().remove(attr);
        }
        else if ((null != ucs) && (item.getEntityType() == EntityType.USE_CASE_SECT)) {
          ucs.getAttributes().remove(attr);
        }

        // TODO remove the items from data model, if delete is notified
        // e.g. 1. Var attr moved to pidc 2. undo insert command
        // Delete command does not requires this since this internally is a flag change

      }
    }
  }


  /**
   * @param item ChangedData item
   * @param uc Attr After
   * @return UseCaseSection
   */
  private UseCaseSection setUcsAttr(final ChangedData item, final Set<Attribute> ucAttrAfter) {
    UseCaseSection ucs;
    ucs = getDataCache().getUseCaseSection(item.getPrimaryKey());
    if ((this.dataProvider.getEntityProvider().getDbUseCaseSection(item.getPrimaryKey()) != null) &&
        (this.dataProvider.getEntityProvider().getDbUseCaseSection(item.getPrimaryKey()).getTabvUcpAttrs() != null)) {
      for (TabvUcpAttr dbUcpAttr : this.dataProvider.getEntityProvider().getDbUseCaseSection(item.getPrimaryKey())
          .getTabvUcpAttrs()) {
        if (null != dbUcpAttr.getTabvAttribute()) {
          ucAttrAfter.add(getDataCache().getAttribute(dbUcpAttr.getTabvAttribute().getAttrId()));
        }
      }
    }
    return ucs;
  }

  /**
   * @param item ChangedData item
   * @param uc Attr After
   * @return Use Case
   */
  private UseCase setUcAttr(final ChangedData item, final Set<Attribute> ucAttrAfter) {
    final UseCase useCase = getDataCache().getUseCase(item.getPrimaryKey());
    if ((this.dataProvider.getEntityProvider().getDbUseCase(item.getPrimaryKey()) != null) &&
        (this.dataProvider.getEntityProvider().getDbUseCase(item.getPrimaryKey()).getTabvUcpAttrs() != null)) {
      for (TabvUcpAttr dbUcpAttr : this.dataProvider.getEntityProvider().getDbUseCase(item.getPrimaryKey())
          .getTabvUcpAttrs()) {
        if (null != dbUcpAttr.getTabvAttribute()) {
          ucAttrAfter.add(getDataCache().getAttribute(dbUcpAttr.getTabvAttribute().getAttrId()));
        }
      }
    }
    return useCase;
  }

  private Map<Long, PIDCVariant> getVariants(final ChangedData item) {
    Map<Long, PIDCVariant> retMap = new HashMap<>();

    if (CommonUtils.isEqual(item.getEntityType(), EntityType.PIDC_VERSION)) {
      PIDCVersion pidcVers = this.dataProvider.getDataCache().getPidcVersion(item.getPrimaryKey());
      if ((pidcVers != null) && pidcVers.isChildrenLoaded()) {
        retMap.putAll(pidcVers.getVariantsMap());
      }

    }
    return retMap;
  }

  private Map<Long, PIDCSubVariant> getSubVariants(final ChangedData item) {
    Map<Long, PIDCSubVariant> retMap = new HashMap<>();

    if (CommonUtils.isEqual(item.getEntityType(), EntityType.VARIANT)) {
      PIDCVariant variant = this.dataProvider.getDataCache().getPidcVaraint(item.getPrimaryKey());
      if (variant.isChildrenLoaded()) {
        retMap.putAll(variant.getSubVariantsMap());
      }

    }
    return retMap;
  }

  /**
   * Get the project attributes for PIDC/Variant/ Sub variant
   *
   * @param item ChangedData item
   * @return Map<Long, IPIDCAttribute>
   */
  private Map<Long, ?> getProjAttrs(final ChangedData item) {

    Map<Long, ?> retMap = new HashMap<>();
    AbstractDataObject dataObject = item.getEntityType().getDataObject(this.dataProvider, item.getPrimaryKey());
    if (dataObject instanceof AbstractProjectObject) {
      AbstractProjectObject absProjAttrColln = (AbstractProjectObject) dataObject;
      if (!absProjAttrColln.isChildrenLoaded()) {
        return retMap;
      }
    }

    switch ((EntityType) item.getEntityType()) {
      case PIDC_VERSION:
        final PIDCVersion pidcVer = getDataCache().getPidcVersion(item.getPrimaryKey());
        retMap = pidcVer.getDefinedAttributes();
        break;
      case VARIANT:
        final PIDCVariant variant = getDataCache().getPidcVaraint(item.getPrimaryKey());
        retMap = variant.getAttributes();
        break;
      case SUB_VARIANT:
        final PIDCSubVariant subVar = getDataCache().getPidcSubVaraint(item.getPrimaryKey());
        retMap = subVar.getAttributes();
        break;

      default:
        retMap = new HashMap<Long, IPIDCAttribute>();
        break;
    }
    return retMap;
  }


  /**
   * Add new project attributes to display change event object
   *
   * @param item ChangedData item
   * @param projAttrsBefore proj Attrs Before
   * @param changeDataMap changeData Map
   */
  private void addNewProjAttrs(final ChangedData item, final Map<Long, ?> projAttrsBefore,
      final Map<Long, ChangedData> changeDataMap) {
    final Map<Long, ?> projAttrsAfter = getProjAttrs(item);
    Class<?> attrEntityClass = null;
    switch ((EntityType) item.getEntityType()) {
      case PIDC:
        attrEntityClass = EntityType.PIDC.getEntityClass();
        break;
      case PIDC_VERSION:
        attrEntityClass = EntityType.PIDC_VERSION.getEntityClass();
        break;
      case VARIANT:
        attrEntityClass = EntityType.VAR_ATTR.getEntityClass();
        break;
      case SUB_VARIANT:
        attrEntityClass = EntityType.SUBVAR_ATTR.getEntityClass();
        break;
      default:
        break;
    }
    for (Entry<Long, ?> entry : projAttrsAfter.entrySet()) {
      if (!projAttrsBefore.containsKey(entry.getKey())) {
        IPIDCAttribute projAttr = (IPIDCAttribute) entry.getValue();
        changeDataMap.put(projAttr.getPidcAttrID(),
            new ChangedData(ChangeType.INSERT, projAttr.getPidcAttrID(), attrEntityClass, DisplayEventSource.DATABASE));
      }
    }
  }

  /**
   * Fetch the values of the given attribute
   *
   * @param dbAttr DB attribute
   * @param changeDataMap the change data from Display Change Event
   */
  private void fetchAttrValues(final TabvAttribute dbAttr, final Map<Long, ChangedData> changeDataMap) {
    for (TabvAttrValue dbAttrValue : dbAttr.getTabvAttrValues()) {
      if (!getDataCache().getAllAttrValuesMap().containsKey(dbAttrValue.getValueId())) {
        // The new values are not added to the map here, as they are done by getAttrValue() method.
        changeDataMap.put(dbAttrValue.getValueId(), new ChangedData(ChangeType.INSERT, dbAttrValue.getValueId(),
            TabvAttrValue.class, DisplayEventSource.DATABASE));
      }
    }
  }

  /**
   * Wrapper method for logger
   *
   * @return ILoggerAdapter
   */
  private ILoggerAdapter getLogger() {
    return this.dataProvider.getLogger();
  }

  /**
   * Wrapper method for DataCache
   *
   * @return DataCache
   */
  private DataCache getDataCache() {
    return this.dataProvider.getDataCache();
  }

  /**
   * Wrapper method for EntityProvider
   *
   * @return EntityProvider
   */
  private EntityProvider getEntityProvider() {
    return this.dataProvider.getEntityProvider();
  }

  /**
   * Wrapper method for DataLoader
   *
   * @return DataLoader
   */
  private DataLoader getDataLoader() {
    return this.dataProvider.getDataLoader();
  }
}
