package com.bosch.caltool.icdm.bo.a2l;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.WpResp;


/**
 * Loader class for WP Responsibility
 *
 * @author apj4cob
 */
public class WpRespLoader extends AbstractBusinessObject<WpResp, TWpResp> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public WpRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WP_RESP, TWpResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WpResp createDataObject(final TWpResp entity) throws DataException {
    WpResp object = new WpResp();

    setCommonFields(object, entity);
    object.setRespName(entity.getRespName());

    return object;
  }

  /**
   * Get all WP Responsibility records in system
   *
   * @return Map. Key - id, Value - WpResp object
   * @throws DataException error while retrieving data
   */
  public Map<Long, WpResp> getAll() throws DataException {
    Map<Long, WpResp> objMap = new ConcurrentHashMap<>();
    TypedQuery<TWpResp> tQuery = getEntMgr().createNamedQuery(TWpResp.GET_ALL, TWpResp.class);
    List<TWpResp> dbObj = tQuery.getResultList();
    for (TWpResp entity : dbObj) {
      objMap.put(entity.getRespId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Gets the all by code.
   *
   * @return the all by code
   * @throws DataException the data exception
   */
  public Map<String, WpResp> getAllByCode() throws DataException {
    Map<String, WpResp> objMap = new ConcurrentHashMap<>();
    TypedQuery<TWpResp> tQuery = getEntMgr().createNamedQuery(TWpResp.GET_ALL, TWpResp.class);
    List<TWpResp> dbObj = tQuery.getResultList();
    for (TWpResp entity : dbObj) {
      objMap.put(entity.getRespName(), createDataObject(entity));
    }
    return objMap;
  }

}
