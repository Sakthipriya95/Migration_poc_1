/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCpRuleAttr;

/**
 * Type of entity
 *
 * @author bne4cob
 */
@Deprecated
public enum CPEntityType implements IEntityType<AbstractCPObject, CPDataProvider> {


                                                                                   /**
                                                                                    * Component Package - TCompPkg
                                                                                    */
                                                                                   COMP_PKG(TCompPkg.class) {

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public AbstractCPObject getDataObject(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getCompPkg(entityID);
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public long getVersion(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getEntityProvider()
                                                                                           .getDbCompPkg(entityID)
                                                                                           .getVersion();
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public int getOrder() {
                                                                                       return ORDER_COMP_PKG;
                                                                                     }

                                                                                   },
                                                                                   /**
                                                                                    * Component package BC - TCompPkgBc
                                                                                    */
                                                                                   COMP_PKG_BC(TCompPkgBc.class) {

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public AbstractCPObject getDataObject(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getCompPkgBc(entityID);
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public long getVersion(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getEntityProvider()
                                                                                           .getDbCompPkgBc(entityID)
                                                                                           .getVersion();
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public int getOrder() {
                                                                                       return ORDER_COMP_PKG_BC;
                                                                                     }
                                                                                   },
                                                                                   /**
                                                                                    * CDR Parameter - TRvwParameter
                                                                                    */
                                                                                   COMP_PKG_BC_FC(TCompPkgBcFc.class) {

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public AbstractCPObject getDataObject(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getCompPkgBcFc(entityID);
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public long getVersion(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getEntityProvider()
                                                                                           .getDbCompPkgBcFc(entityID)
                                                                                           .getVersion();
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public int getOrder() {
                                                                                       return ORDER_CP_BC_FC;
                                                                                     }
                                                                                   },
                                                                                   /**
                                                                                    * Component Package Rule attribute -
                                                                                    * TRvwParameter
                                                                                    */
                                                                                   COMP_PKG_RULE_ATTR(
                                                                                                      TCpRuleAttr.class) {

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public AbstractCPObject getDataObject(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getCompPkgRuleAttr(
                                                                                               entityID);
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public long getVersion(
                                                                                         final CPDataProvider dataProvider,
                                                                                         final Long entityID) {
                                                                                       return dataProvider
                                                                                           .getCompPkgRuleAttr(entityID)
                                                                                           .getVersion();
                                                                                     }

                                                                                     /**
                                                                                      * {@inheritDoc}
                                                                                      */
                                                                                     @Override
                                                                                     public int getOrder() {
                                                                                       return ORDER_CP_RULE_ATTR;
                                                                                     }
                                                                                   };

  /**
   * Component package order
   */
  private static final int ORDER_COMP_PKG = 1;
  /**
   * BC order
   */
  private static final int ORDER_COMP_PKG_BC = 2;
  /**
   * Fc entity order
   */
  private static final int ORDER_CP_BC_FC = 3;
  /**
   * Rule attribute entity order
   */
  private static final int ORDER_CP_RULE_ATTR = 4;


  /**
   * Entity class for this enumeration value
   */
  private Class<?> entityClass;

  /**
   * Constructor
   *
   * @param entityClass entity class
   */
  CPEntityType(final Class<?> entityClass) {
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
  public static CPEntityType getEntityType(final Class<?> entityClass) {
    for (CPEntityType eType : CPEntityType.values()) {
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
   * @param entClass entityClass
   * @return true for TOP_LEVEL_ENTITY.Modifying the data for TOP_LEVEL_ENTITY should not stop the command execution.
   */
  @Override
  public boolean stopCommandForEntityUpdate() {
    return true;
  }
}
