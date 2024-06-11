/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author mkl2cob
 */
@Deprecated
public class A2LWPRespCreator {


  /**
   * Long
   */
  private final Long rootId;
  /**
   * Long
   */
  private final Long typeID;
  /**
   * EntityManager
   */
  private final EntityManager entityManager;
  /**
   * true if parameter insertion is needed
   */
  private boolean paramInsertNeeded;
  /**
   * pidc a2l id
   */
  private final Long pidcA2LId;
  /**
   * key- group name value- group long name
   */
  private final Map<String, String> a2lGrpMap;
  /**
   * list of
   */
  private List<TA2lGroup> ta2lGroupList;
  /**
   * mapping source string
   */
  private final Long mappingSource;
  private final Long defaultWPId;
  private final String currentUserId;
  private final Long a2lId;
  private final ApicDataProvider apicDataProvider;

  /**
   * Constructor
   *
   * @param a2lId PIDCA2l
   * @param rootId Long
   * @param typeID Long
   * @param pidcA2LId Long
   * @param a2lGrpMap Map<String, String> map of key- groupName, value - group long name
   * @param mappingSource String
   * @param defaultWPId Long
   * @param currentUserId String
   * @param entMgrToUse EntityManager
   * @param apicDataProvider
   */
  public A2LWPRespCreator(final Long a2lId, final Long rootId, final Long typeID, final Long pidcA2LId,
      final Map<String, String> a2lGrpMap, final Long mappingSource, final Long defaultWPId, final String currentUserId,
      final EntityManager entMgrToUse, final ApicDataProvider apicDataProvider) {

    this.a2lId = a2lId;
    this.rootId = rootId;
    this.typeID = typeID;
    this.pidcA2LId = pidcA2LId;
    this.a2lGrpMap = a2lGrpMap;
    this.mappingSource = mappingSource;
    this.defaultWPId = defaultWPId;
    this.currentUserId = currentUserId;
    this.entityManager = entMgrToUse;
    this.apicDataProvider = apicDataProvider;

  }

  /**
   * return true if the groups are not created
   */
  private boolean checkIfA2lGroupsAreToBeCreated() {
    return isA2lGrpEmpty(this.rootId);
  }

  /**
   * @param a2lId Long
   * @return list of TA2lGroup
   */
  public boolean isA2lGrpEmpty(final Long wpRootId) {
    final String query = "SELECT a2lGrp from TA2lGroup a2lGrp where a2lGrp.a2lId=  '" + this.a2lId +
        "' and a2lGrp.tabvAttrValue.valueId = '" + wpRootId + "'";
    final TypedQuery<TA2lGroup> typeQuery = this.entityManager.createQuery(query, TA2lGroup.class);
    this.ta2lGroupList = typeQuery.getResultList();
    return (this.ta2lGroupList == null) || this.ta2lGroupList.isEmpty();
  }

  /**
   * create entries in DB
   *
   * @return
   */
  boolean createA2LRespAndA2lWpResp() {
    try {
      TA2lResp dbA2LResp = createA2LRespEntity();

      if (CommonUtils.isEqual(this.mappingSource,
          Long.valueOf(this.apicDataProvider.getParameterValue(ApicConstants.GROUP_MAPPING_ID)))) {
        if (checkIfA2lGroupsAreToBeCreated()) {
          // if the groups are not already created in the database
          createA2LGroupsInDb(dbA2LResp);
        }
        else {
          // create wp resp for a2lGroups
          createA2LWPRespForGrps(dbA2LResp);
        }
      }
    }
    catch (Exception exp) {
      this.entityManager.getTransaction().rollback();
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return this.paramInsertNeeded;
  }

  /**
   *
   */


  /**
   * @return
   */
  private ILoggerAdapter getLogger() {
    ILoggerAdapter logger = ObjectStore.getInstance().getLogger();
    return logger;
  }


  /**
   * @param dbA2LResp
   */
  private void createA2LWPRespForGrps(final TA2lResp dbA2LResp) {
    getLogger().debug("Inserting responsibilities for groups ....");
    if (this.ta2lGroupList != null) {
      for (TA2lGroup group : this.ta2lGroupList) {
        createA2lWPForGrp(dbA2LResp, group);
      }
    }
  }

  /**
   * @param dbA2LResp dbA2LResp
   * @param group group
   */
  private void createA2lWPForGrp(final TA2lResp dbA2LResp, final TA2lGroup group) {
    TA2lWpResp dbA2LWpResp = new TA2lWpResp();
    dbA2LWpResp.setTA2lResp(dbA2LResp);
    dbA2LWpResp.setTWpResp(this.entityManager.find(TWpResp.class, this.defaultWPId));
    dbA2LWpResp.setTA2lGroup(group);
    dbA2LWpResp.setCreatedUser(this.currentUserId);
    this.entityManager.persist(dbA2LWpResp);
  }

  /**
   * create a2l groups in database
   *
   * @param dbA2LResp
   */
  private void createA2LGroupsInDb(final TA2lResp dbA2LResp) {
    getLogger().debug("Create groups in T_A2L_GROUP table...");

    for (Entry<String, String> group : this.a2lGrpMap.entrySet()) {
      TA2lGroup dbA2lGroup = new TA2lGroup();
      dbA2lGroup.setGrpName(group.getKey());
      dbA2lGroup.setGrpLongName(group.getValue());
      dbA2lGroup.setA2lId(this.a2lId);
      dbA2lGroup.setTabvAttrValue(this.entityManager.find(TabvAttrValue.class, this.rootId));
      dbA2lGroup.setCreatedUser(this.currentUserId);

      this.entityManager.persist(dbA2lGroup);

      createA2lWPForGrp(dbA2LResp, dbA2lGroup);
    }
    this.paramInsertNeeded = true;
  }

  /**
   * create TA2lResp
   *
   * @return
   */
  private TA2lResp createA2LRespEntity() {
    getLogger().debug("Create A2L_RESP entry in DB...");
    TA2lResp dbA2lResp = new TA2lResp();
    dbA2lResp.setTPidcA2l(this.entityManager.find(TPidcA2l.class, this.pidcA2LId));
    dbA2lResp.setWpRootId(this.rootId);
    dbA2lResp.setWpTypeId(this.typeID);
    dbA2lResp.setCreatedUser(this.currentUserId);

    this.entityManager.persist(dbA2lResp);
    return dbA2lResp;
  }
}
