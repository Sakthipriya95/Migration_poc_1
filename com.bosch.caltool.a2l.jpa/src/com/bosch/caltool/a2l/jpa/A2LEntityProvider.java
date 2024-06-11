/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.AbstractEntityProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGrpParam;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wp;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;


/**
 * @author bne4cob
 */
public class A2LEntityProvider extends AbstractEntityProvider {

  /**
   * logger
   */
  protected ILoggerAdapter logger;

  /**
   * Constructor
   *
   * @param logger
   * @param entityTypes
   */
  public A2LEntityProvider(final ILoggerAdapter logger, final IEntityType<?, ?>... entityTypes) {
    super(entityTypes);
    this.logger = logger;
  }

  /**
   * @param objID objID
   * @return TFc2wp entity
   */
  public TFc2wp getDbTFc2wp(final Long objID) {
    return getEm().find(TFc2wp.class, objID);
  }

  /**
   * @param objID objID
   * @return the TA2lResp Object
   */
  public TA2lResp getDbA2lResp(final Long objID) {
    return getEm().find(TA2lResp.class, objID);
  }


  /**
   * @param objID objID
   * @return the TA2lGroup Object
   */
  public TA2lGroup getDbA2lGrp(final Long objID) {
    return getEm().find(TA2lGroup.class, objID);
  }

  /**
   * @param objID objID
   * @return the TA2lGrpParam obj
   */
  public TA2lGrpParam getDbA2lGrpParam(final Long objID) {
    return getEm().find(TA2lGrpParam.class, objID);
  }

  /**
   * @param objID objID
   * @return the TWpResp obj
   */
  public TWpResp getDbWpResp(final Long objID) {
    return getEm().find(TWpResp.class, objID);
  }

  /**
   * @param objID objID
   * @return the TA2lWpResp obj
   */
  public TA2lWpResp getDbA2lWpResp(final Long objID) {
    return getEm().find(TA2lWpResp.class, objID);
  }

  /**
   * @param pidcA2lId primary key
   * @return the entity
   */
  public synchronized TPidcA2l getDbPIDCA2l(final long pidcA2lId) {
    return getEm().find(TPidcA2l.class, pidcA2lId);
  }


  /**
   * @param valueID primary key
   * @return the entity
   */
  public synchronized TabvAttrValue getDbValue(final Long valueID) {
    return getEm().find(TabvAttrValue.class, valueID);
  }

  /**
   * @param paramID primary key
   * @return the entity
   */
  public synchronized TParameter getDbParameter(final Long paramID) {
    return getEm().find(TParameter.class, paramID);
  }

  /**
   * @param wrkPkgId wrkPkgId
   * @return TWorkpackage
   */
  public TWorkpackage getDbIcdmWorkPackage(final Long wrkPkgId) {
    return getEm().find(TWorkpackage.class, wrkPkgId);
  }

  /**
   * @param objID objID
   * @return the TFc2wpDefinition obj
   */
  public TFc2wpDefinition getDbFC2WPDef(final Long objID) {
    return getEm().find(TFc2wpDefinition.class, objID);
  }

  /**
   * @param objID objID
   * @return the TFc2wpDefVersion obj
   */
  public TFc2wpDefVersion getDbFC2WPVers(final Long objID) {
    return getEm().find(TFc2wpDefVersion.class, objID);
  }
}
