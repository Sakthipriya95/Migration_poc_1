package com.bosch.caltool.icdm.bo.bc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TSdomBc;
import com.bosch.caltool.icdm.model.bc.SdomBc;


/**
 * Loader class for SdomBc
 *
 * @author say8cob
 */
public class SdomBcLoader extends AbstractBusinessObject<SdomBc, TSdomBc> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public SdomBcLoader(final ServiceData serviceData) {
    super(serviceData, "Base Component", TSdomBc.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SdomBc createDataObject(final TSdomBc entity) throws DataException {
    SdomBc object = new SdomBc();

    object.setId(entity.getId());
    object.setName(entity.getName());
    object.setVariant(entity.getVariant());
    object.setRevision(entity.getRevision());
    object.setDescription(entity.getDescription());
    object.setLifecycleState(entity.getLifecycleState());

    return object;
  }

  /**
   * Get all SdomBc records in system
   *
   * @return Map. Key - id, Value - SdomBc object
   * @throws DataException error while retrieving data
   */
  public Set<SdomBc> getAllDistinctBcName() throws DataException {
    // Load all distinct BC
    Set<SdomBc> objSet = new HashSet<>();
    TypedQuery<Object[]> tQuery = getEntMgr().createNamedQuery(TSdomBc.GET_ALL_DISTINCT_VALUES, Object[].class);
    List<Object[]> dbObj = tQuery.getResultList();
    for (Object[] dbBc : dbObj) {
      SdomBc bc = new SdomBc();
      bc.setName(dbBc[0].toString());
      bc.setDescription(dbBc[1].toString());
      objSet.add(bc);
    }
    return objSet;
  }

  /**
   * Get all SdomBc records in system
   *
   * @return Map. Key - id, Value - SdomBc object
   * @throws DataException error while retrieving data
   */
  public Map<String, SdomBc> getMapofDistinctBcs() throws DataException {
    Map<String, SdomBc> objSet = new HashMap<>();
    TypedQuery<Object[]> tQuery = getEntMgr().createNamedQuery(TSdomBc.GET_ALL_DISTINCT_VALUES, Object[].class);
    List<Object[]> dbObj = tQuery.getResultList();
    for (Object[] dbBc : dbObj) {
      SdomBc bc = new SdomBc();
      bc.setName(dbBc[0].toString());
      bc.setDescription(dbBc[1].toString());
      objSet.put(dbBc[0].toString(), bc);
    }
    return objSet;
  }

  /**
   * @param bcName
   * @return
   */
  public String getBcDescByBCName(final String bcName) {
    return (String) getEntMgr().createNamedQuery(TSdomBc.GET_BY_NAME).setParameter("bcName", bcName).getSingleResult();
  }

}
