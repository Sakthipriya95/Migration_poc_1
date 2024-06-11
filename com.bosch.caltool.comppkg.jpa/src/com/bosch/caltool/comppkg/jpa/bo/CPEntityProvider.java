/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import com.bosch.caltool.dmframework.bo.AbstractEntityProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.a2l.TSdomBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCpRuleAttr;

/**
 * Class to interact with entities
 * 
 * @author BNE4COB
 */
class CPEntityProvider extends AbstractEntityProvider {

  /**
   * Constructor
   * 
   * @param entityTypes entity types
   */
  public CPEntityProvider(final IEntityType<?, ?>... entityTypes) {
    super(entityTypes);
  }

  /**
   * @param compPkgId primary key
   * @return the entity
   */
  protected synchronized TCompPkg getDbCompPkg(final Long compPkgId) { // NOPMD by adn1cob on 6/30/14 10:19 AM
    return getEm().find(TCompPkg.class, compPkgId);
  }

  /**
   * @param compBcId primary key
   * @return the entity
   */
  protected synchronized TCompPkgBc getDbCompPkgBc(final Long compBcId) { // NOPMD by adn1cob on 6/30/14 10:19 AM
    return getEm().find(TCompPkgBc.class, compBcId);
  }

  /**
   * @param compBcFcId primary key
   * @return the entity
   */
  protected synchronized TCompPkgBcFc getDbCompPkgBcFc(final Long compBcFcId) { // NOPMD by adn1cob on 6/30/14 10:19 AM
    return getEm().find(TCompPkgBcFc.class, compBcFcId);
  }

  /**
   * @param cpRuleAttrID primary key
   * @return the entity
   */
  protected TCpRuleAttr getDbCompPkgRuleAttr(final Long cpRuleAttrID) {
    return getEm().find(TCpRuleAttr.class, cpRuleAttrID);
  }

  /**
   * @param bcId primary key
   * @return the entity
   */
  public synchronized TSdomBc getDbSdomBc(final Long bcId) { // NOPMD by adn1cob on 6/30/14 10:19 AM
    return getEm().find(TSdomBc.class, bcId);
  }

}
