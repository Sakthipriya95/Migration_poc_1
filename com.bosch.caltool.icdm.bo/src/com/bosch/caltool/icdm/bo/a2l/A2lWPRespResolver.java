/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.model.a2l.A2lResp;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;

/**
 * The Class A2lWPRespResolver.
 *
 * @author rgo7cob
 */
public class A2lWPRespResolver extends AbstractSimpleBusinessObject {

  /** The root id. */
  private Long rootId;

  /** The type ID. */
  private Long typeID;

  /** The pidc A 2 l. */
  private final PidcA2l pidcA2l;

  /** The wp type attr value id. */
  private final Long wpTypeAttrValueId;

  /** The wp root grp attr value id. */
  private final Long wpRootGrpAttrValueId;

  /** The mapping source id. */
  private final Long mappingSourceId;

  /** The grp mapping id. */
  private final Long grpMappingId;

  /** The a 2 l group list. */
  private final List<A2LGroup> a2lGroupList;

  /** The a 2 l wp mapping. */
  private final A2lWpMapping a2lWpMapping;

  /** The div attr value id. */
  private final Long divAttrValueId;

  /**
   * constant for error message when creating A2l Resp.
   */
  public static final String ERROR_CREATING_A2L_RESP = "Error during creation of responsibilities for A2L file";

  /**
   * Instantiates a new a 2 l WP resp resolver.
   *
   * @param pidcA2l the pidc A 2 l
   * @param a2lWpMapping the a 2 l wp mapping
   * @param serviceData the service data
   */
  public A2lWPRespResolver(final PidcA2l pidcA2l, final A2lWpMapping a2lWpMapping, final ServiceData serviceData) {
    super(serviceData);
    this.pidcA2l = pidcA2l;
    this.a2lWpMapping = a2lWpMapping;
    this.mappingSourceId = a2lWpMapping.getMappingSourceId();
    this.grpMappingId = a2lWpMapping.getGroupMappingId();
    this.a2lGroupList = a2lWpMapping.getA2lGroupList();
    this.wpTypeAttrValueId = a2lWpMapping.getWpTypeAttrValueId();
    this.wpRootGrpAttrValueId = a2lWpMapping.getWpRootGrpAttrValueId();
    this.divAttrValueId = a2lWpMapping.getDivAttrValueId();
  }

  /**
   * Sets Map ,Key-Wp Resp id Value-WpResp in A2lWpMapping Object.
   *
   * @throws DataException the data exception
   */
  private void setWpRespMap() throws DataException {
    if (!CommonUtils.isNotEmpty(this.a2lWpMapping.getWpRespMap())) {
      this.a2lWpMapping.getWpRespMap().putAll(new WpRespLoader(getServiceData()).getAll());
    }
  }

  /**
   * Fetch A 2 l wp resp.
   *
   * @return the a 2 l resp
   * @throws DataException the data exception
   */
  private A2lResp fetchA2lWpResp() throws DataException {
    // root Group Value id
    this.typeID = this.wpTypeAttrValueId;
    this.rootId = this.wpRootGrpAttrValueId;
    if (CommonUtils.isNull(this.rootId)) {
      this.rootId = 0L;
    }
    // get A2l responsibility
    A2lResp a2lResp = new A2lRespLoader(getServiceData()).getA2lResp(this.pidcA2l.getId(), this.typeID, this.rootId);
    if (a2lResp != null) {
      setA2lRespAndMap(a2lResp);
    }
    return a2lResp;
  }

  /**
   * Gets the root id.
   *
   * @return the rootId
   */
  private Long getRootId() {
    return this.rootId;
  }

  /**
   * Gets the type ID.
   *
   * @return the typeID
   */
  private Long getTypeID() {
    return this.typeID;
  }

  /**
   * Gets the mapping source id.
   *
   * @return the mappingSourceId
   */
  public Long getMappingSourceId() {
    return this.mappingSourceId;
  }


  /**
   * Creates the A 2 l resp grp params.
   *
   * @param characteristicsMap the characteristics map
   * @return true if the a2l Group Resp Params are created successfully or already exiting
   * @throws IcdmException the icdm exception
   */
  public boolean createA2lRespGrpParams(final Map<String, Characteristic> characteristicsMap) throws IcdmException {
    // Fill Wp Resp Map in a2l wp mapping object
    setWpRespMap();
    A2lResp a2lResp = null;
    // WP type attribute
    if (this.wpTypeAttrValueId != null) {
      // Fetch A2l Wp Resp from Db
      a2lResp = fetchA2lWpResp();
    }
    // If A2l Resp are not created when group is existing
    if ((a2lResp == null) && CommonUtils.isEqual(this.mappingSourceId, this.grpMappingId) &&
        CommonUtils.isNotEmpty(this.a2lGroupList)) {
      // ICDM-2602
      A2LRespGroupParamsInserter a2lRespGroupInserter = new A2LRespGroupParamsInserter(getServiceData(),
          this.pidcA2l.getA2lFileId(), getRootId(), this.pidcA2l, getTypeID(), this.grpMappingId, this);
      // call command to insert a2l responsibility
      if (a2lRespGroupInserter.insertResp(this.a2lGroupList, characteristicsMap, this.a2lWpMapping.getWpRespMap())) {
        // Get the newly created a2l resp and fill the map
        fetchA2lWpResp();
      }
      else {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets A2l Resp in A2l Wp Mapping object and fill the map *
   *
   * @param a2lResp the new WP respin editor dp
   * @throws DataException the data exception
   */
  private void setA2lRespAndMap(final A2lResp a2lResp) throws DataException {
    this.a2lWpMapping.setA2lResp(a2lResp);
    if (a2lResp != null) {
      Map<Long, A2LWpRespExt> a2lWpRespMap =
          new A2lWpRespLoader(getServiceData()).getByA2lRespExt(a2lResp.getId(), this.divAttrValueId);
      this.a2lWpMapping.setA2lWpRespMap(a2lWpRespMap);
      setWpGrpRespMap(a2lWpRespMap);
      setLabelMapForA2lRespWp(a2lWpRespMap);
    }
  }

  /**
   * set the label map for the A2l wp resp.
   *
   * @param a2lWpRespMap the a 2 l wp resp map
   */
  public void setLabelMapForA2lRespWp(final Map<Long, A2LWpRespExt> a2lWpRespMap) {

    for (A2LWpRespExt a2lRespWp : a2lWpRespMap.values()) {
      if (a2lRespWp.isA2lGrp()) {
        A2LGroup a2lGroup = this.a2lWpMapping.getA2lGrpMap().get(a2lRespWp.getIcdmA2lGroup().getGrpName());
        if (a2lGroup != null) {
          fetchA2lGrpLabMap(a2lRespWp, a2lGroup);
        }
      }
      else {
        A2lWorkPackageGroup wpGrp = this.a2lWpMapping.getWorkPackageGroupMap().get(a2lRespWp.getWpResource());
        if (wpGrp != null) {
          for (Long wpId : wpGrp.getWorkPackage()) {
            a2lRespWp.getWorkpackageMap().put(this.a2lWpMapping.getWpMap().get(wpId).getWpName(),
                this.a2lWpMapping.getWpMap().get(wpId).getWpNumber());
          }
        }
      }
    }
  }

  /**
   * Fetch A 2 l grp lab map.
   *
   * @param a2lRespWp the a 2 l resp wp
   * @param a2lGroup the a 2 l group
   * @param a2lWpMapping2
   */
  private void fetchA2lGrpLabMap(final A2LWpRespExt a2lWpRespExt, final A2LGroup a2lGroup) {
    if (a2lGroup.getSubGrpMap().isEmpty()) {
      if (!this.a2lWpMapping.getA2lWpRespGrpLabelMap().containsKey(a2lWpRespExt.getA2lWpResp().getId())) {
        this.a2lWpMapping.getA2lWpRespGrpLabelMap().put(a2lWpRespExt.getA2lWpResp().getId(), new HashMap<>());
      }
      this.a2lWpMapping.getA2lWpRespGrpLabelMap().get(a2lWpRespExt.getA2lWpResp().getId())
          .putAll(a2lGroup.getLabelMap());
    }
    else {
      List<String> grpList = a2lGroup.getSubGrpMap().get(a2lGroup.getGroupName());
      for (String a2lGrp : grpList) {
        if (a2lGrp != null) {
          // Call the method recursively
          fetchA2lGrpLabMap(a2lWpRespExt, this.a2lWpMapping.getA2lGrpMap().get(a2lGrp));
        }
      }
    }
  }


  /**
   * Sets the wp grp resp map.
   *
   * @param a2lWpRespMap the a 2 l wp resp map
   */
  private void setWpGrpRespMap(final Map<Long, A2LWpRespExt> a2lWpRespMap) {
    for (A2LWpRespExt a2lWpResp : a2lWpRespMap.values()) {
      this.a2lWpMapping.getWpGrpRespMap().put(a2lWpResp.getName(), WpRespType
          .getType(this.a2lWpMapping.getWpRespMap().get(a2lWpResp.getA2lWpResp().getWpRespId()).getRespName()));
    }
  }
}
