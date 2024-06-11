package com.bosch.caltool.icdm.bo.cdr;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;


/**
 * Loader class for Review Result WorkPackage Resp
 *
 * @author say8cob
 */
public class RvwWpRespLoader extends AbstractBusinessObject<RvwWpResp, TRvwWpResp> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwWpRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_WP_RESP, TRvwWpResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwWpResp createDataObject(final TRvwWpResp entity) throws DataException {
    RvwWpResp object = new RvwWpResp();

    setCommonFields(object, entity);
    object.setA2lRespId(entity.gettA2lResponsibility() != null ? entity.gettA2lResponsibility().getA2lRespId() : null);
    object.setResultId(entity.getTRvwResult() != null ? entity.getTRvwResult().getResultId() : null);
    object.setA2lWpId(entity.getTA2lWorkPackage() != null ? entity.getTA2lWorkPackage().getA2lWpId() : null);
    object.setName(entity.getTA2lWorkPackage() != null ? entity.getTA2lWorkPackage().getWpName() : null);
    object.setDescription(entity.getTA2lWorkPackage() != null ? entity.getTA2lWorkPackage().getWpDesc() : null);

    return object;
  }


  /**
   * Get Review WP Resp records using ResultId
   *
   * @param entityObject review result*
   * @return Map. Key - id, Value - RvwWpResp object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwWpResp> getByResultObj(final TRvwResult entityObject) throws DataException {
    Map<Long, RvwWpResp> objMap = new ConcurrentHashMap<>();
    Set<TRvwWpResp> dbObj = entityObject.getTRvwWpResps();
    for (TRvwWpResp entity : dbObj) {
      objMap.put(entity.getRvwWpRespId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param a2lWpId as a2l workpackage id
   * @param a2lRespId as a2l responsibility id
   * @param rvwResId as review result id
   * @return ReviewWPResp object or nul if the combination not found
   * @throws IcdmException as exception
   */
  public RvwWpResp getMatchingRvwWpResp(Long a2lWpId,Long a2lRespId, Long rvwResId) throws IcdmException {
    TRvwResult rvwResult = new CDRReviewResultLoader(getServiceData()).getEntityObject(rvwResId);
   
    for (TRvwWpResp tRvwWpResp :  rvwResult.getTRvwWpResps()) {
      if(a2lRespId.equals(tRvwWpResp.gettA2lResponsibility().getA2lRespId()) && a2lWpId.equals(tRvwWpResp.getTA2lWorkPackage().getA2lWpId())) {
        return getDataObjectByID(tRvwWpResp.getRvwWpRespId());
      }
    }
    return null;
  }
}
