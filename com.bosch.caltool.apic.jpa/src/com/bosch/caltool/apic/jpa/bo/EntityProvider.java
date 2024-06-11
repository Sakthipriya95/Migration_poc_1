/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.ApicJpaActivator;
import com.bosch.caltool.dmframework.bo.AbstractEntityProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPst;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPstCont;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lFileinfo;
import com.bosch.caltool.icdm.database.entity.apic.MvTabeDataset;
import com.bosch.caltool.icdm.database.entity.apic.MvVcdmDatasetsWorkpkgStat;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristicValue;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TMandatoryAttr;
import com.bosch.caltool.icdm.database.entity.apic.TMessage;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcChangeHistory;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.database.entity.apic.TRmCategory;
import com.bosch.caltool.icdm.database.entity.apic.TRmProjectCharacter;
import com.bosch.caltool.icdm.database.entity.apic.TSsdFeature;
import com.bosch.caltool.icdm.database.entity.apic.TSsdValue;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValueType;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;
import com.bosch.caltool.icdm.database.entity.apic.TabvUcpAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseSection;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;

/**
 * Class to interact with entities
 *
 * @author BNE4COB
 */
class EntityProvider extends AbstractEntityProvider {

  /**
   * DGS_TEST
   */
  private static final String DB_URL = "jdbc:oracle:thin:@rb-engsrv32.de.bosch.com:38550:dgstest";

  /**
   * Logger
   */
  protected ILoggerAdapter logger;

  /**
   * Creates the data provider instance
   *
   * @param logger logger
   * @param dbUsername db user name
   * @param dbPassword db password
   * @param entityTypes entity types
   */
  public EntityProvider(final ILoggerAdapter logger, final String dbUsername, final String dbPassword,
      final IEntityType<?, ?>[] entityTypes) {

    super(entityTypes);
    this.logger = logger;

    Map<Object, Object> emfProperties = new ConcurrentHashMap<Object, Object>();
    emfProperties.put("javax.persistence.jdbc.password", dbPassword);
    emfProperties.put("javax.persistence.jdbc.user", dbUsername);
    emfProperties.put("javax.persistence.jdbc.url", DB_URL);
    emfProperties.put("eclipselink.logging.level", "FINE");
    emfProperties.put("eclipselink.logging.file", "C:/temp/APIC_DataProvider_JPA.log");
    emfProperties.put(PersistenceUnitProperties.CLASSLOADER, ApicJpaActivator.class.getClassLoader());

    PersistenceProvider persProvider = new PersistenceProvider();
    EntityManagerFactory emf = persProvider.createEntityManagerFactory("com.bosch.caltool.apic.jpa", emfProperties);

    ObjectStore.getInstance().setEntityManagerFactory(emf);
  }

  /**
   * Constructor
   *
   * @param logger logger
   * @param emf entity manager Factory
   * @param entityTypes entity types
   */
  public EntityProvider(final ILoggerAdapter logger, final EntityManagerFactory emf,
      final IEntityType<?, ?>... entityTypes) {

    super(entityTypes);
    this.logger = logger;

    if ((ObjectStore.getInstance().getEntityManagerFactory() == null) ||
        !ObjectStore.getInstance().getEntityManagerFactory().isOpen()) {
      ObjectStore.getInstance().setEntityManagerFactory(emf);
    }
    else {
      ObjectStore.getInstance().rebuild();
    }
  }

  /**
   * @param attributeID primary key
   * @return the entity
   */
  protected synchronized TabvAttribute getDbAttribute(final Long attributeID) {
    return getEm().find(TabvAttribute.class, attributeID);
  }

  /**
   * Icdm-954
   *
   * @param attrCharID primary key
   * @return the entity
   */
  protected TCharacteristic getDbCharacteristic(final Long attrCharID) {
    synchronized (this) {
      return getEm().find(TCharacteristic.class, attrCharID);
    }
  }

  /**
   * Icdm-1844
   *
   * @param aliasDefID primary key
   * @return the entity
   */
  protected TAliasDefinition getDbAliasDefinition(final Long aliasDefID) {
    synchronized (this) {
      return getEm().find(TAliasDefinition.class, aliasDefID);
    }
  }

  /**
   * Icdm-1844
   *
   * @param aliasDetID primary key
   * @return the entity
   */
  protected TAliasDetail getDbAliasDetail(final Long aliasDetID) {
    synchronized (this) {
      return getEm().find(TAliasDetail.class, aliasDetID);
    }
  }

  /**
   * @param featureID primary key
   * @return the entity
   */
  protected synchronized TSsdFeature getDbFeature(final Long featureID) {
    return getEm().find(TSsdFeature.class, featureID);
  }

  /**
   * @param valueID primary key
   * @return the entity
   */
  protected synchronized TSsdValue getDbFeatureValue(final Long valueID) {
    return getEm().find(TSsdValue.class, valueID);
  }

  /**
   * @param charValID primary key
   * @return the entity
   */
  protected TCharacteristicValue getDbCharacteristicValue(final Long charValID) {
    synchronized (this) {
      return getEm().find(TCharacteristicValue.class, charValID);
    }
  }


  /**
   * @param valueID primary key
   * @return the entity
   */
  protected synchronized TabvAttrValue getDbValue(final Long valueID) {
    return getEm().find(TabvAttrValue.class, valueID);
  }

  /**
   * Gets the DB Attribue ValueType object for the valueTypeID passed
   *
   * @param valueTypeID primary key
   * @return the entity
   */

  protected synchronized TabvAttrValueType getDbValueType(final Long valueTypeID) {
    return getEm().find(TabvAttrValueType.class, valueTypeID);
  }

  /**
   * @param groupID primary key
   * @return the entity
   */
  protected synchronized TabvAttrGroup getDbGroup(final Long groupID) {
    return getEm().find(TabvAttrGroup.class, groupID);
  }

  /**
   * @param superGroupID primary key
   * @return the entity
   */
  protected synchronized TabvAttrSuperGroup getDbSuperGroup(final Long superGroupID) {
    return getEm().find(TabvAttrSuperGroup.class, superGroupID);
  }

  /**
   * icdm-474 dcn for top level entity
   *
   * @param entId primary key
   * @return the entity
   */
  protected synchronized TabvTopLevelEntity getDbTopLevelEntity(final Long entId) {
    return getEm().find(TabvTopLevelEntity.class, entId);
  }

  /**
   * @param depenId primary key
   * @return the entity
   */
  protected synchronized TabvAttrDependency getDbDependency(final long depenId) {
    return getEm().find(TabvAttrDependency.class, depenId);
  }

  /**
   * @param pidcId primary key
   * @return the entity
   */
  protected synchronized TabvProjectidcard getDbPIDC(final long pidcId) {
    return getEm().find(TabvProjectidcard.class, pidcId);
  }

  /**
   * @param pidcVersId primary key
   * @return the entity
   */
  protected synchronized TPidcVersion getDbPIDCVersion(final long pidcVersId) {
    return getEm().find(TPidcVersion.class, pidcVersId);
  }


  /**
   * @param ucpaId primary key
   * @return the entity
   */
  protected TabvUcpAttr getDbUcpAttr(final long ucpaId) {
    synchronized (this) {
      return getEm().find(TabvUcpAttr.class, ucpaId);
    }
  }

  // ICDM-1435
  /**
   * @param pidcA2lId primary key
   * @return the entity
   */
  protected synchronized TPidcA2l getDbPIDCA2l(final long pidcA2lId) {
    return getEm().find(TPidcA2l.class, pidcA2lId);
  }


  /**
   * ICDM-763
   *
   * @param linkId primary key
   * @return the entity
   */
  protected synchronized TLink getDbLink(final long linkId) {
    return getEm().find(TLink.class, linkId);
  }

  /**
   * @param pidcVariantId primary key
   * @return the entity
   */
  protected synchronized TabvProjectVariant getDbPidcVariant(final long pidcVariantId) {
    return getEm().find(TabvProjectVariant.class, pidcVariantId);
  }

  /**
   * @param pidcRmDefIdId primary key
   * @return the entity
   */
  protected synchronized TPidcRmDefinition getDbPidcRmDef(final long pidcRmDefIdId) {
    return getEm().find(TPidcRmDefinition.class, pidcRmDefIdId);
  }

  /**
   * @param pidcSubVariantId primary key
   * @return the entity
   */

  protected synchronized TabvProjectSubVariant getDbPidcSubVariant(final long pidcSubVariantId) {
    return getEm().find(TabvProjectSubVariant.class, pidcSubVariantId);
  }

  /**
   * @param pidcAttrId primary key
   * @return the entity
   */
  protected synchronized TabvProjectAttr getDbPidcAttr(final long pidcAttrId) {
    return getEm().find(TabvProjectAttr.class, pidcAttrId);
  }

  /**
   * @param pidcVarAttrId primary key
   * @return the entity
   */
  protected synchronized TabvVariantsAttr getDbPidcVarAttr(final long pidcVarAttrId) {
    return getEm().find(TabvVariantsAttr.class, pidcVarAttrId);
  }

  /**
   * @param pidcSubVarAttrId primary key
   * @return the entity
   */

  protected synchronized TabvProjSubVariantsAttr getDbPidcSubVarAttr(final long pidcSubVarAttrId) {
    return getEm().find(TabvProjSubVariantsAttr.class, pidcSubVarAttrId);
  }

  /**
   * @param entityID primary key
   * @return the TPidcChangeHistory entity
   */
  protected synchronized TPidcChangeHistory getDbPidcChangeHistory(final long entityID) {
    return getEm().find(TPidcChangeHistory.class, entityID);
  }

  /**
   * @param pdsId primary key
   * @return the entity
   */
  protected synchronized TabvPidcDetStructure getDbPidcDetStructure(final long pdsId) {
    return getEm().find(TabvPidcDetStructure.class, pdsId);

  }

  /**
   * @param a2lFileID primary key
   * @return the entity
   */

  protected synchronized MvTa2lFileinfo getDbA2LFileInfo(final long a2lFileID) {
    return getEm().find(MvTa2lFileinfo.class, a2lFileID);
  }


  /**
   * @param dstId primary key
   * @return the entity
   */
  protected MvTabeDataset getDbDataSet(final long dstId) {
    synchronized (this) {
      return getEm().find(MvTabeDataset.class, dstId);
    }
  }

  /**
   * @param vcdmWorkPkgStatSetId the primary key
   * @return the VVcdmDatasetsWorkpkgStat entity
   */
  // ICDM-2469
  protected MvVcdmDatasetsWorkpkgStat getDbVcdmWorkpkgStatSet(final long vcdmWorkPkgStatSetId) {
    synchronized (this) {
      return getEm().find(MvVcdmDatasetsWorkpkgStat.class, vcdmWorkPkgStatSetId);
    }
  }

  /**
   * Get the node access entity
   *
   * @param nodeaccessID primary key
   * @return node access entity
   */
  protected synchronized TabvApicNodeAccess getDbNodeAccess(final long nodeaccessID) {
    return getEm().find(TabvApicNodeAccess.class, nodeaccessID);
  }

  /**
   * @param userID primary key
   * @return user entity
   */
  protected synchronized TabvApicUser getDbApicUser(final long userID) {
    return getEm().find(TabvApicUser.class, userID);
  }

  /**
   * @param accessRightID primary key
   * @return the apic access right entity
   */
  protected synchronized TabvApicAccessRight getDbApicAccessRight(final Long accessRightID) {
    return getEm().find(TabvApicAccessRight.class, accessRightID);
  }

  /**
   * @param groupID primary key
   * @return the entity
   */
  protected synchronized TabvUseCaseGroup getDbUseCaseGroup(final Long groupID) {
    return getEm().find(TabvUseCaseGroup.class, groupID);
  }

  /**
   * @param useCaseID primary key
   * @return the entity
   */
  protected synchronized TabvUseCase getDbUseCase(final Long useCaseID) {
    return getEm().find(TabvUseCase.class, useCaseID);
  }

  /**
   * @param sectionID primary key
   * @return the entity
   */
  protected synchronized TabvUseCaseSection getDbUseCaseSection(final Long sectionID) {
    return getEm().find(TabvUseCaseSection.class, sectionID);
  }

  /**
   * @param fileID primary key
   * @return the entity
   */
  protected synchronized TabvIcdmFile getDbIcdmFile(final Long fileID) {
    return getEm().find(TabvIcdmFile.class, fileID);
  }

  /**
   * ICDM-1027
   *
   * @param favUcId Favourite usecase id
   * @return TUsecaseFavorite entity object
   */
  public TUsecaseFavorite getDbFavUcItem(final Long favUcId) {
    return getEm().find(TUsecaseFavorite.class, favUcId);
  }


  /**
   * ICdm-1272 return the TabvTooltip from DB
   *
   * @param toolTipID paramAttrID
   * @return the entity
   */
  public TMessage getDbToolTip(final Long toolTipID) {
    return getEm().find(TMessage.class, toolTipID);
  }

  /**
   * ICDM-1625
   *
   * @param fmId primary key
   * @return the entity
   */
  protected TFocusMatrix getDbFocuMatrix(final Long fmId) {
    synchronized (this) {
      return getEm().find(TFocusMatrix.class, fmId);
    }
  }

  /**
   * @param fmVersID primary key
   * @return the entity
   */
  // ICDM-2569
  protected TFocusMatrixVersion getDbFocuMatrixVersion(final Long fmVersID) {
    synchronized (this) {
      return getEm().find(TFocusMatrixVersion.class, fmVersID);
    }
  }

  /**
   * ICDM-1836
   *
   * @param maId mandatory attribute id
   * @return TMandatoryAttr
   */
  public TMandatoryAttr getDbMandatoryAttr(final Long maId) {
    return getEm().find(TMandatoryAttr.class, maId);
  }

  /**
   * @param preAttrValID primary key
   * @return the entity
   */
  protected TPredefinedAttrValue getDbPredAttrValue(final Long preAttrValID) {
    synchronized (this) {
      return getEm().find(TPredefinedAttrValue.class, preAttrValID);
    }
  }

  /**
   * @param validityID primary key
   * @return the entity
   */
  protected TPredefinedValidity getDbPredValidity(final Long validityID) {
    synchronized (this) {
      return getEm().find(TPredefinedValidity.class, validityID);
    }
  }

  /**
   * @param pKey id
   * @return TFocusMatrixVersionAttr entity
   */
  // ICDM-2569
  public TFocusMatrixVersionAttr getDbFocuMatrixVersionAttr(final Long pKey) {
    return getEm().find(TFocusMatrixVersionAttr.class, pKey);
  }

  /**
   * @param objID objID
   * @return TvcdmPst entity
   */
  public TvcdmPst getDbTvcdmPst(final Long objID) {
    return getEm().find(TvcdmPst.class, objID);
  }

  /**
   * @param objID objID
   * @return TvcdmPstCont entity
   */
  public TvcdmPstCont getDbTvcdmPstCont(final Long objID) {
    return getEm().find(TvcdmPstCont.class, objID);
  }

  /**
   * @param rbDataId id
   * @return TRmCategory
   */
  public TRmCategory getDbRbData(final Long rbDataId) {
    return getEm().find(TRmCategory.class, rbDataId);
  }

  /**
   * @param rmDefId id
   * @return TPidcRmDefinition
   */
  public TPidcRmDefinition getDbRmDef(final Long rmDefId) {
    return getEm().find(TPidcRmDefinition.class, rmDefId);
  }

  /**
   * @param projCharId id
   * @return TRmProjectCharacter
   */
  public TRmProjectCharacter getDbRmProjchar(final Long projCharId) {
    return getEm().find(TRmProjectCharacter.class, projCharId);
  }
}
