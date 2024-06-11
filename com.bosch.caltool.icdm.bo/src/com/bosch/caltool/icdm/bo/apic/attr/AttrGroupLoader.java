/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;


/**
 * @author bne4cob
 */
public class AttrGroupLoader extends AbstractBusinessObject<AttrGroup, TabvAttrGroup> {

  /**
   * @param serviceData Service Data
   */
  public AttrGroupLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.GROUP, TabvAttrGroup.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AttrGroup createDataObject(final TabvAttrGroup entity) throws DataException {
    AttrGroup ret = new AttrGroup();

    ret.setId(entity.getGroupId());
    ret.setSuperGrpId(entity.getTabvAttrSuperGroup().getSuperGroupId());

    ret.setName(getLangSpecTxt(entity.getGroupNameEng(), entity.getGroupNameGer()));
    ret.setNameEng(entity.getGroupNameEng());
    ret.setNameGer(entity.getGroupNameGer());
    ret.setDescription(getLangSpecTxt(entity.getGroupDescEng(), entity.getGroupDescGer()));
    ret.setDescriptionEng(entity.getGroupDescEng());
    ret.setDescriptionGer(entity.getGroupDescGer());

    ret.setDeleted(CommonUtilConstants.CODE_YES.equals(entity.getDeletedFlag()));

    ret.setCreatedDate(timestamp2Date(entity.getCreatedDate()));
    ret.setCreatedUser(entity.getCreatedUser());
    ret.setModifiedDate(timestamp2Date(entity.getModifiedDate()));
    ret.setModifiedUser(entity.getModifiedUser());
    ret.setVersion(entity.getVersion());

    return ret;
  }


  /**
   * @param superGroupId Super Group ID
   * @param includeDeleted if true, deleted Group are also included
   * @return key - group ID, value - group
   * @throws DataException if attribute could not be found
   */
  public Map<Long, AttrGroup> getGroupsBySuperGroup(final Long superGroupId, final boolean includeDeleted)
      throws DataException {

    Map<Long, AttrGroup> retMap = new HashMap<>();

    TabvAttrSuperGroup dbSuperGrp = (new AttrSuperGroupLoader(getServiceData())).getEntityObject(superGroupId);

    if (dbSuperGrp == null) {
      return retMap;
    }

    for (TabvAttrGroup entity : dbSuperGrp.getTabvAttrGroups()) {
      if (includeDeleted || CommonUtilConstants.CODE_NO.equals(entity.getDeletedFlag())) {
        retMap.put(entity.getGroupId(), createDataObject(entity));
      }
    }
    return retMap;
  }

  /**
   * Get all AttrGroup records in system
   *
   * @return Map. Key - id, Value - AttrGroup object
   * @throws DataException error while retrieving data
   */
  public Map<Long, AttrGroup> getAll() throws DataException {
    Map<Long, AttrGroup> objMap = new ConcurrentHashMap<>();
    TypedQuery<TabvAttrGroup> tQuery = getEntMgr().createNamedQuery(TabvAttrGroup.GET_ALL, TabvAttrGroup.class);
    List<TabvAttrGroup> dbObj = tQuery.getResultList();
    for (TabvAttrGroup entity : dbObj) {
      objMap.put(entity.getGroupId(), createDataObject(entity));
    }
    return objMap;
  }


}
