/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;


/**
 * @author bne4cob
 */
public class AttrSuperGroupLoader extends AbstractBusinessObject<AttrSuperGroup, TabvAttrSuperGroup> {

  /**
   * @param serviceData Service Data
   */
  public AttrSuperGroupLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.SUPER_GROUP, TabvAttrSuperGroup.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AttrSuperGroup createDataObject(final TabvAttrSuperGroup entity) throws DataException {
    AttrSuperGroup ret = new AttrSuperGroup();

    ret.setId(entity.getSuperGroupId());

    ret.setName(getLangSpecTxt(entity.getSuperGroupNameEng(), entity.getSuperGroupNameGer()));
    ret.setNameEng(entity.getSuperGroupNameEng());
    ret.setNameGer(entity.getSuperGroupNameGer());
    ret.setDescription(getLangSpecTxt(entity.getSuperGroupDescEng(), entity.getSuperGroupDescGer()));
    ret.setDescriptionEng(entity.getSuperGroupDescEng());
    ret.setDescriptionGer(entity.getSuperGroupDescGer());

    ret.setDeleted(CommonUtilConstants.CODE_YES.equals(entity.getDeletedFlag()));

    ret.setCreatedDate(timestamp2Date(entity.getCreatedDate()));
    ret.setCreatedUser(entity.getCreatedUser());
    ret.setModifiedDate(timestamp2Date(entity.getModifiedDate()));
    ret.setModifiedUser(entity.getModifiedUser());
    ret.setVersion(entity.getVersion());

    return ret;
  }


  /**
   * @param includeDeleted if true, deleted super Groups are also included
   * @return key - super group ID, value - super group
   * @throws DataException if super group could not be found
   */
  public Map<Long, AttrSuperGroup> getAllSuperGroups(final boolean includeDeleted) throws DataException {
    Map<Long, AttrSuperGroup> retMap = new HashMap<>();

    TypedQuery<TabvAttrSuperGroup> qry =
        getEntMgr().createNamedQuery(TabvAttrSuperGroup.NQ_GET_ALL_SUPER_GROUPS, TabvAttrSuperGroup.class);

    for (TabvAttrSuperGroup entity : qry.getResultList()) {
      if (includeDeleted || CommonUtilConstants.CODE_NO.equals(entity.getDeletedFlag())) {
        retMap.put(entity.getSuperGroupId(), createDataObject(entity));
      }
    }

    return retMap;
  }

  /**
   * Fetch the attribute group model. Consists of attribute super groups, super groups and their hierarchy
   *
   * @param includeDeleted includeDeleted if true, deleted super Groups/groups are also included
   * @return AttrGroupModel
   * @throws DataException error during data retrieval
   */
  public AttrGroupModel getAttrGroupModel(final boolean includeDeleted) throws DataException {

    getLogger().info("Creating Attribute Group Model ...");

    // Fetch attribute super groups
    AttrGroupModel ret = new AttrGroupModel();
    ret.setAllSuperGroupMap(getAllSuperGroups(includeDeleted));

    // Fetch attribute groups
    Map<Long, AttrGroup> allGrpMap = new HashMap<>();
    Map<Long, Set<Long>> groupBySuperGroupMap = new HashMap<>();

    AttrGroupLoader grpLdr = new AttrGroupLoader(getServiceData());
    for (Long suprGrpId : ret.getAllSuperGroupMap().keySet()) {
      Map<Long, AttrGroup> grpMap = grpLdr.getGroupsBySuperGroup(suprGrpId, includeDeleted);
      allGrpMap.putAll(grpMap);
      groupBySuperGroupMap.put(suprGrpId, grpMap.keySet());
    }

    ret.setAllGroupMap(allGrpMap);
    ret.setGroupBySuperGroupMap(groupBySuperGroupMap);

    getLogger().info("Attribute Group Model created. Super groups = {}; Groups = {}", ret.getAllSuperGroupMap().size(),
        ret.getAllGroupMap().size());

    return ret;
  }


}
