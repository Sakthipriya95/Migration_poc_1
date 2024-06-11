package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.wp.WorkPkgLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResource;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.model.a2l.A2lWpResp;
import com.bosch.caltool.icdm.model.a2l.WpResp;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Loader class for A2L WP RESPONSIBILITY
 *
 * @author gge6cob
 */
public class A2lWpRespLoader extends AbstractBusinessObject<A2lWpResp, TA2lWpResp> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWpRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WP_RESP, TA2lWpResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lWpResp createDataObject(final TA2lWpResp entity) throws DataException {
    A2lWpResp object = new A2lWpResp();

    setCommonFields(object, entity);

    object.setA2lRespId(entity.getTA2lResp().getA2lRespId());
    object.setWpRespId(entity.getTWpResp().getRespId());
    object.setWpRespEnum(
        entity.getTWpResp() == null ? WpRespType.RB : WpRespType.getType(entity.getTWpResp().getRespName()));
    // nullable
    object.setWpId(entity.getTWorkpackage() != null ? entity.getTWorkpackage().getWpId() : 0l);
    if (null != entity.getTA2lGroup()) {
      object.setA2lGroupId(entity.getTA2lGroup().getGroupId());
    }

    return object;
  }

  /**
   * Get A2L WP RESPONSIBILITY by A2LResp Id records in system
   *
   * @param a2lRespId Long
   * @return Map. Key - id, Value - A2lWpResp object
   * @throws DataException error while retrieving data
   */
  public Map<Long, A2lWpResp> getByA2lResp(final Long a2lRespId) throws DataException {
    Map<Long, A2lWpResp> objMap = new ConcurrentHashMap<>();
    TypedQuery<TA2lWpResp> tQuery = getEntMgr().createNamedQuery(TA2lWpResp.GET_A2LWPRESP, TA2lWpResp.class);
    tQuery.setParameter("a2lRespId", a2lRespId);
    List<TA2lWpResp> dbObj = tQuery.getResultList();
    for (TA2lWpResp entity : dbObj) {
      objMap.put(entity.getA2lWpRespId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get A2L WP RESPONSIBILITY by A2LResp Id records in system.
   *
   * @param a2lRespId Long
   * @param divAttrValueId the div attr value id
   * @return Map. Key - id, Value - A2LWpRespExt object
   * @throws DataException error while retrieving data
   */
  public Map<Long, A2LWpRespExt> getByA2lRespExt(final Long a2lRespId, final Long divAttrValueId) throws DataException {
    Map<Long, A2LWpRespExt> objMap = new HashMap<>();
    TypedQuery<TA2lWpResp> tQuery = getEntMgr().createNamedQuery(TA2lWpResp.GET_A2LWPRESP, TA2lWpResp.class);
    tQuery.setParameter("a2lRespId", a2lRespId);
    List<TA2lWpResp> dbObj = tQuery.getResultList();
    for (TA2lWpResp entity : dbObj) {
      A2lWpResp a2lWpResp = createDataObject(entity);
      A2LWpRespExt a2lWpRespExt = new A2LWpRespExt();
      a2lWpRespExt.setA2lWpResp(a2lWpResp);
      if (null != entity.getTA2lGroup()) {
        a2lWpRespExt
            .setIcdmA2lGroup(new IcdmA2lGroupLoader(getServiceData()).getDataObjectByID(a2lWpResp.getA2lGroupId()));
      }
      a2lWpRespExt.setWorkPackage(entity.getTWorkpackage() != null
          ? new WorkPkgLoader(getServiceData()).getDataObjectByID(a2lWpResp.getWpId()) : null);
      a2lWpRespExt
          .setWpResource(entity.getTWorkpackage() != null ? getWpResource(divAttrValueId, a2lWpResp.getWpId()) : null);
      WpResp wpResp = new WpRespLoader(getServiceData()).getDataObjectByID(a2lWpResp.getWpRespId());
      a2lWpResp.setWpRespEnum(wpResp == null ? WpRespType.RB : WpRespType.getType(wpResp.getRespName()));

      // Name (Workpackage/A2lGroup)
      String wpName = a2lWpRespExt.getWorkPackage() != null ? a2lWpRespExt.getWorkPackage().getName().trim()
          : ApicConstants.EMPTY_STRING;
      a2lWpRespExt.setName(a2lWpRespExt.isA2lGrp() ? a2lWpRespExt.getIcdmA2lGroup().getGrpName() : wpName);

      objMap.put(entity.getA2lWpRespId(), a2lWpRespExt);
    }
    return objMap;
  }

  /**
   * Gets the wp resource.
   *
   * @param divId the div id
   * @param wpId the wp id
   * @return the wp group.
   */
  public String getWpResource(final Long divId, final Long wpId) {
    TypedQuery<TWorkpackageDivision> tQuery =
        getEntMgr().createNamedQuery(TWorkpackageDivision.NQ_FIND_RES_BY_DIV_ID, TWorkpackageDivision.class);
    tQuery.setParameter("divValueId", divId);
    tQuery.setParameter("wpId", wpId);

    if ((null != tQuery.getResultList()) && (null != tQuery.getResultList().get(0).getTWpResource())) {
      TWpResource tWpRes = tQuery.getResultList().get(0).getTWpResource();
      return tWpRes.getResourceCode().trim();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Get A2L WP RESPONSIBILITY by A2LResp Id records in system.
   *
   * @param objIds the obj ids
   * @param divAttrValueId the div attr value id
   * @return Map. Key - id, Value - A2LWpRespExt object
   * @throws DataException error while retrieving data
   */
  public Set<A2LWpRespExt> getAllByObjId(final Set<Long> objIds, final Long divAttrValueId) throws DataException {
    Set<A2LWpRespExt> objSet = new HashSet<>();
    for (Long objId : objIds) {
      TA2lWpResp entity = getEntityObject(objId);
      A2LWpRespExt a2lWpRespExt = new A2LWpRespExt();
      a2lWpRespExt.setA2lWpResp(createDataObject(entity));
      if (null != entity.getTA2lGroup()) {
        a2lWpRespExt.setIcdmA2lGroup(
            new IcdmA2lGroupLoader(getServiceData()).getDataObjectByID(a2lWpRespExt.getA2lWpResp().getA2lGroupId()));
      }
      a2lWpRespExt.setWorkPackage(entity.getTWorkpackage() != null
          ? new WorkPkgLoader(getServiceData()).getDataObjectByID(a2lWpRespExt.getA2lWpResp().getWpId()) : null);
      a2lWpRespExt.setWpResource(entity.getTWorkpackage() != null
          ? getWpResource(divAttrValueId, a2lWpRespExt.getA2lWpResp().getWpId()) : null);
      a2lWpRespExt.setDivAttrValueId(divAttrValueId);
      WpResp wpResp = new WpRespLoader(getServiceData()).getDataObjectByID(a2lWpRespExt.getA2lWpResp().getWpRespId());
      a2lWpRespExt.getA2lWpResp()
          .setWpRespEnum(wpResp == null ? WpRespType.RB : WpRespType.getType(wpResp.getRespName()));

      // Name (Workpackage/A2lGroup)
      String wpName = a2lWpRespExt.getWorkPackage() != null ? a2lWpRespExt.getWorkPackage().getName().trim()
          : ApicConstants.EMPTY_STRING;
      a2lWpRespExt.setName(a2lWpRespExt.isA2lGrp() ? a2lWpRespExt.getIcdmA2lGroup().getGrpName() : wpName);

      // Label Map <Resolved only during A2l loading : A2lWpRespResolver>
      objSet.add(a2lWpRespExt);
    }
    return objSet;
  }
}
