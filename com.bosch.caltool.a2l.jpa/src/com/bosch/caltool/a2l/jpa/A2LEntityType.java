/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGrpParam;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;

/**
 * ICDM-2600
 *
 * @author mkl2cob
 */
public enum A2LEntityType implements IEntityType<AbstractA2LObject, A2LDataProvider> {

                                                                                      /**
                                                                                       * CDR Review File - TRvwFile
                                                                                       */
                                                                                      A2L_RESP(TA2lResp.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbA2lResp(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_A2L_RESP;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * CDR Review File - TRvwFile
                                                                                       */
                                                                                      A2L_GROUP(TA2lGroup.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbA2lGrp(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_A2L_GROUP;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * CDR Review File - TRvwFile
                                                                                       */
                                                                                      A2L_GRP_PARAM(TA2lGrpParam.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbA2lGrpParam(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_A2L_GRP_PARAM;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * CDR Review File - TRvwFile
                                                                                       */
                                                                                      WP_RESP(TWpResp.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbWpResp(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_WP_RESP;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * CDR Review File - TRvwFile
                                                                                       */
                                                                                      A2L_WP_RESP(TA2lGrpParam.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbA2lWpResp(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_A2L_WP_RESP;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * FC2WP defintion -
                                                                                       * TFc2wpDefinition
                                                                                       */
                                                                                      FC2WP_DEF(TFc2wpDefinition.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbFC2WPDef(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_FC2WP_DEF;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * FC2WP defintion -
                                                                                       * TFc2wpDefinition
                                                                                       */
                                                                                      FC2WP_DEF_VERS(TFc2wpDefVersion.class) {

                                                                                        @Override
                                                                                        public AbstractA2LObject getDataObject(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final A2LDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntProvider()
                                                                                              .getDbFC2WPVers(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_FC2WP_DEF_VERS;
                                                                                        }
                                                                                      };


  private static final int ORDER_A2L_RESP = 1;
  private static final int ORDER_A2L_GROUP = 2;
  private static final int ORDER_A2L_GRP_PARAM = 3;
  private static final int ORDER_WP_RESP = 4;
  private static final int ORDER_A2L_WP_RESP = 5;
  private static final int ORDER_FC2WP_DEF = 6;
  private static final int ORDER_FC2WP_DEF_VERS = 6;
  /**
   * Entity class for this enumeration value
   */
  private Class<?> entityClass;

  A2LEntityType(final Class<?> entClass) {
    this.entityClass = entClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getEntityClass() {
    return this.entityClass;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getEntityTypeString() {
    return name();
  }

  /**
   * Retrieves the entity type value for the given entity class
   *
   * @param entityClass entity class
   * @return the entity type
   */
  public static A2LEntityType getEntityType(final Class<?> entityClass) {
    for (A2LEntityType eType : A2LEntityType.values()) {
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
  public boolean stopCommandForEntityUpdate() {
    return true;
  }
}
