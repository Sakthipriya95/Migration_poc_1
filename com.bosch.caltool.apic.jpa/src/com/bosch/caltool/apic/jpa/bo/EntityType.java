/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristicValue;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TMandatoryAttr;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.database.entity.apic.TUsecaseFavorite;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
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
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Type of entity
 *
 * @author bne4cob
 */
public enum EntityType implements IEntityType<ApicObject, ApicDataProvider> {


                                                                             /**
                                                                              * Super Group - TabvAttrSuperGroup
                                                                              */
                                                                             SUPER_GROUP(TabvAttrSuperGroup.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getSuperGroup(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbSuperGroup(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_SUPER_GROUP;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Group - TabvAttrGroup
                                                                              */
                                                                             GROUP(TabvAttrGroup.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getGroup(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbGroup(entityID).getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_GROUP;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Attribute - TabvAttribute
                                                                              */
                                                                             ATTRIBUTE(TabvAttribute.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getAttribute(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbAttribute(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_ATTRIBUTE;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public String getEntityTypeString() {
                                                                                 return ApicConstants.ATTR_NODE_TYPE;
                                                                               }
                                                                             },

                                                                             /**
                                                                              * Icdm-954 Characteristic -
                                                                              * TCharacteristic
                                                                              */
                                                                             CHARACTERISTIC(TCharacteristic.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getCharacteristic(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbCharacteristic(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_CHARACTERISTICS;
                                                                               }


                                                                             },

                                                                             /**
                                                                              * CharacteristicValue -
                                                                              * TCharacteristicValue
                                                                              */
                                                                             CHARACTERISTIC_VALUE(TCharacteristicValue.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getCharacteristicValue(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbCharacteristicValue(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_CHAR_VALUE;
                                                                               }


                                                                             },
                                                                             /**
                                                                              * Attribute value - TabvAttrValue
                                                                              */
                                                                             ATTRIB_VALUE(TabvAttrValue.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getAttrValue(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbValue(entityID).getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_ATTRIB_VALUE;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Attribute/Value dependency -
                                                                              * TabvAttrDependency
                                                                              */
                                                                             ATTR_N_VAL_DEP(TabvAttrDependency.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 final TabvAttrDependency dbDep =
                                                                                     dataProvider.getEntityProvider()
                                                                                         .getDbDependency(
                                                                                             entityID.longValue());
                                                                                 if ((dbDep != null) && (dbDep
                                                                                     .getTabvAttrValue() == null)) {
                                                                                   return dataProvider
                                                                                       .getAttrDependency(entityID);
                                                                                 }

                                                                                 return dataProvider
                                                                                     .getValueDependency(entityID);

                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbDependency(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_ATTR_N_VAL_DEP;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Project ID Card - TabvProjectidcard
                                                                              */
                                                                             PIDC(TabvProjectidcard.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidc(entityID.longValue());
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPIDC(entityID).getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PIDC;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public String getEntityTypeString() {
                                                                                 return ApicConstants.PIDC_NODE_TYPE;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Project Variant - TabvProjectVariant
                                                                              */
                                                                             VARIANT(TabvProjectVariant.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidcVaraint(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPidcVariant(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_VARIANT;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Project Sub Variant -
                                                                              * TabvProjectSubVariant
                                                                              */
                                                                             SUB_VARIANT(TabvProjectSubVariant.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidcSubVaraint(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPidcSubVariant(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_SUB_VARIANT;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Project Attribute - TabvProjectAttr
                                                                              */
                                                                             PROJ_ATTR(TabvProjectAttr.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidcAttribute(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPidcAttr(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PROJ_ATTR;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Variant Attribute - TabvVariantsAttr
                                                                              */
                                                                             VAR_ATTR(TabvVariantsAttr.class) {

                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidcVaraintAttr(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPidcVarAttr(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_VAR_ATTR;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Sub Variant Attribute -
                                                                              * TabvProjSubVariantsAttr
                                                                              */
                                                                             SUBVAR_ATTR(TabvProjSubVariantsAttr.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidcSubVaraintAttr(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPidcSubVarAttr(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_SUBVAR_ATTR;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Node accesss - TabvApicNodeAccess
                                                                              */
                                                                             NODE_ACCESS(TabvApicNodeAccess.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getAllNodeAccRights()
                                                                                     .get(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbNodeAccess(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_NODE_ACCESS;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Use case group - TabvUseCaseGroup
                                                                              */
                                                                             USE_CASE_GROUP(TabvUseCaseGroup.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getUseCaseGroup(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbUseCaseGroup(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_USE_CASE_GROUP;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Use case - TabvUseCase
                                                                              */
                                                                             USE_CASE(TabvUseCase.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getUseCase(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbUseCase(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_USE_CASE;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public String getEntityTypeString() {
                                                                                 return ApicConstants.UC_NODE_TYPE;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Use case Section - TabvUseCaseSection
                                                                              */
                                                                             USE_CASE_SECT(TabvUseCaseSection.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getUseCaseSection(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbUseCaseSection(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_USE_CASE_SECTION;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Use case item - attribute mapping entity
                                                                              * - TabvUcpAttr
                                                                              */
                                                                             UCP_ATTR(TabvUcpAttr.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return null;

                                                                                 // TODO
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return 0;// Version is not available
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_UCP_ATTR;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * PidcDetStructure - TabvPidcDetStructure
                                                                              */
                                                                             PIDC_DET_STRUCT(TabvPidcDetStructure.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPidcDetStructure(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPidcDetStructure(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PIDC_STRUCT;
                                                                               }
                                                                             },

                                                                             /**
                                                                              * icdm-474 dcn for top level entiti Top
                                                                              * level entity TABV_TOP_LEVEL_ENTITIES
                                                                              */
                                                                             TOP_LEVEL_ENTITY(TabvTopLevelEntity.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long primaryKey) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getTopEntList().get(primaryKey);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_TOP_LEVEL_ENTITY;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbTopLevelEntity(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public boolean stopCommandForEntityUpdate() {
                                                                                 // Do not block updates to this entity.
                                                                                 // Top level entity is used only to
                                                                                 // identify new objects via CQN.
                                                                                 return false;
                                                                               }
                                                                             },

                                                                             /**
                                                                              * icdm-977 dcn for additional links T_Link
                                                                              */
                                                                             T_LINK(TLink.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long primaryKey) {
                                                                                 // TODO
                                                                                 return dataProvider.getDataCache()
                                                                                     .getTopEntList().get(primaryKey);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_LINKS;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbLink(entityID).getVersion();
                                                                               }


                                                                             },
                                                                             /**
                                                                              * icdm-1027 dcn for additional links
                                                                              * TUsecaseFavorite
                                                                              */
                                                                             T_UCFAV(TUsecaseFavorite.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long primaryKey) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getTopEntList().get(primaryKey);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_UCFAV;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbFavUcItem(entityID)
                                                                                     .getVersion();
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Apic User
                                                                              */
                                                                             APIC_USER(TabvApicUser.class) {

                                                                               /**
                                                                                * Returns ApicUser business object
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long primaryKey) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getAllApicUsersMap()
                                                                                     .get(primaryKey);
                                                                               }

                                                                               /**
                                                                                * Returns order
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_APIC_USER;
                                                                               }

                                                                               /**
                                                                                * Returns entity's version
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbApicUser(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                             },
                                                                             /**
                                                                              * PIDC Version
                                                                              */
                                                                             PIDC_VERSION(TPidcVersion.class) {

                                                                               /**
                                                                                * Returns ApicUser business object
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long primaryKey) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getPidcVersion(primaryKey);
                                                                               }

                                                                               /**
                                                                                * Returns order
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PIDC_VERSION;
                                                                               }

                                                                               /**
                                                                                * Returns entity's version
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPIDCVersion(entityID)
                                                                                     .getVersion();
                                                                               }
                                                                             },

                                                                             /**
                                                                              * PIDC A2L
                                                                              */
                                                                             PIDC_A2L(TPidcA2l.class) {

                                                                               /**
                                                                                * Returns ApicUser business object
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long primaryKey) {
                                                                                 return dataProvider
                                                                                     .getPidcA2l(primaryKey);
                                                                               }

                                                                               /**
                                                                                * Returns order
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PIDC_A2L;
                                                                               }

                                                                               /**
                                                                                * Returns entity's version
                                                                                * <p>
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPIDCA2l(entityID)
                                                                                     .getVersion();
                                                                               }
                                                                             },
                                                                             /**
                                                                              * Focus Matrix
                                                                              */
                                                                             FOCUS_MATRIX(TFocusMatrix.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getFocusMatrix(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbFocuMatrix(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_FOCUS_MATRIX;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Focus Matrix Version
                                                                              */
                                                                             // ICDM-2569
                                                                             FOCUS_MATRIX_VERSION(TFocusMatrixVersion.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getFocusMatrixVersion(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbFocuMatrixVersion(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_FOCUS_MATRIX_VERSION;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Focus Matrix Version Attribute
                                                                              */
                                                                             // ICDM-2569
                                                                             FOCUS_MATRIX_VERSION_ATTR(TFocusMatrixVersionAttr.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getFocusMatrixVersionAttr(
                                                                                         entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbFocuMatrixVersionAttr(
                                                                                         entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_FOCUS_MATRIX_VERSION_ATTR;
                                                                               }
                                                                             },
                                                                             /**
                                                                              * ICDM-1836 Mandatory attr
                                                                              */
                                                                             MANDATORY_ATTR(TMandatoryAttr.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getMandatoryAttr(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getMandatoryAttr(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_MAND_ATTR;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public String getEntityTypeString() {
                                                                                 return ApicConstants.MAND_ATTR_TYPE;
                                                                               }

                                                                             },


                                                                             /**
                                                                              * ICDM-1844 Alias definition type
                                                                              */
                                                                             ALIAS_DEFINITION(TAliasDefinition.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getAliasDefMap().get(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbAliasDefinition(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_ALIAS_DEF;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public String getEntityTypeString() {
                                                                                 return ApicConstants.ALIAS_DEF_TYPE;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * ICDM-1844 Alias definition type
                                                                              */
                                                                             ALIAS_DETAIL(TAliasDetail.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getDataCache()
                                                                                     .getAliasDetail(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbAliasDetail(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_ALIAS_DETAIL;
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public String getEntityTypeString() {
                                                                                 return ApicConstants.ALIAS_DETAIL_TYPE;
                                                                               }

                                                                             },
                                                                             /**
                                                                              * Predefined Attribute Value-
                                                                              * TGroupAttrValue
                                                                              */
                                                                             PREDEFND_ATTR_VALUE(TPredefinedAttrValue.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPredfnAttrValue(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPredAttrValue(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PREDEFINED_ATTR_VALUE;
                                                                               }


                                                                             },
                                                                             /**
                                                                              * Predefined Attribute Value-
                                                                              * TGroupAttrValue
                                                                              */
                                                                             PREDEFND_VALIDITY(TPredefinedValidity.class) {

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public ApicObject getDataObject(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider
                                                                                     .getPredfnValidity(entityID);
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public long getVersion(
                                                                                   final ApicDataProvider dataProvider,
                                                                                   final Long entityID) {
                                                                                 return dataProvider.getEntityProvider()
                                                                                     .getDbPredValidity(entityID)
                                                                                     .getVersion();
                                                                               }

                                                                               /**
                                                                                * {@inheritDoc}
                                                                                */
                                                                               @Override
                                                                               public int getOrder() {
                                                                                 return ORDER_PREDEFINED_VALIDITY;
                                                                               }


                                                                             };

  /**
   * Entity processing order for APIC_USER
   */
  private static final int ORDER_APIC_USER = 0;
  /**
   * Order
   */
  private static final int ORDER_CHARACTERISTICS = 1;
  /**
   * Order
   */
  private static final int ORDER_CHAR_VALUE = 2;
  /**
   * Order
   */
  private static final int ORDER_SUPER_GROUP = 3;
  /**
   * Order
   */
  private static final int ORDER_GROUP = 4;
  /**
   * Order
   */
  private static final int ORDER_ATTRIBUTE = 5;
  /**
   * Order
   */
  private static final int ORDER_ATTRIB_VALUE = 6;
  /**
   * Order
   */
  private static final int ORDER_ATTR_N_VAL_DEP = 7;
  /**
   * Order T_PREDEFINED_ATTR_VALUES
   */
  private static final int ORDER_PREDEFINED_ATTR_VALUE = 8;
  /**
   * Order T_GROUP_ATTR_VALIDITY
   */
  private static final int ORDER_PREDEFINED_VALIDITY = 9;
  /**
   * Order
   */
  private static final int ORDER_USE_CASE_GROUP = 10;
  /**
   * Order
   */
  private static final int ORDER_USE_CASE = 11;
  /**
   * Order
   */
  private static final int ORDER_USE_CASE_SECTION = 12;
  /**
   * Order
   */
  private static final int ORDER_UCP_ATTR = 13;
  /**
   * Order
   */
  private static final int ORDER_PIDC = 14;
  /**
   * Order
   */
  private static final int ORDER_NODE_ACCESS = 15;
  /**
   * Order
   */
  private static final int ORDER_PIDC_VERSION = 16;
  /**
   * Order
   */
  private static final int ORDER_PROJ_ATTR = 17;
  /**
   * Order
   */
  private static final int ORDER_VARIANT = 18;
  /**
   * Order
   */
  private static final int ORDER_VAR_ATTR = 19;
  /**
   * Order
   */
  private static final int ORDER_SUB_VARIANT = 20;
  /**
   * Order
   */
  private static final int ORDER_SUBVAR_ATTR = 21;
  /**
   * Order
   */
  private static final int ORDER_PIDC_STRUCT = 22;


  /**
   * Order
   */
  private static final int ORDER_TOP_LEVEL_ENTITY = 23;
  /**
   * Order
   */
  private static final int ORDER_LINKS = 24;
  /**
   * Order
   */
  private static final int ORDER_UCFAV = 25;
  /**
   * Order
   */
  private static final int ORDER_PIDC_A2L = 26;
  /**
   * Order
   */
  // ICDM-2569
  private static final int ORDER_FOCUS_MATRIX_VERSION = 27;
  /**
   * Order
   */
  // ICDM-2569
  private static final int ORDER_FOCUS_MATRIX_VERSION_ATTR = 28;
  /**
   * Order
   */
  private static final int ORDER_FOCUS_MATRIX = 29;
  /**
   * Order
   */
  private static final int ORDER_MAND_ATTR = 30;
  /**
   * Order
   */
  private static final int ORDER_ALIAS_DEF = 31;
  /**
   * Order
   */
  private static final int ORDER_ALIAS_DETAIL = 32;


  /**
   * Entity class for this enumeration value
   */
  private Class<?> entityClass;

  /**
   * Constructor
   *
   * @param entityClass entity class
   */
  EntityType(final Class<?> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * @return the entity class of this enumeration value
   */
  @Override
  public Class<?> getEntityClass() {
    return this.entityClass;
  }

  /**
   * Retrieves the entity type value for the given entity class
   *
   * @param entityClass entity class
   * @return the entity type
   */
  public static EntityType getEntityType(final Class<?> entityClass) {
    for (EntityType eType : EntityType.values()) {
      if (eType.getEntityClass().equals(entityClass)) {
        return eType;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getEntityTypeString() {
    return name();
  }


  /**
   * @return true for TOP_LEVEL_ENTITY.Modifying the data for TOP_LEVEL_ENTITY should not stop the command execution.
   */
  @Override
  public boolean stopCommandForEntityUpdate() {
    return true;
  }
}
