package com.bosch.caltool.icdm.bo.bc;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TSdomFc;
import com.bosch.caltool.icdm.model.bc.SdomFc;


/**
 * Loader class for SdomFc
 *
 * @author say8cob
 */
public class SdomFcLoader extends AbstractBusinessObject<SdomFc, TSdomFc> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public SdomFcLoader(final ServiceData serviceData) {
    super(serviceData, "Function Component", TSdomFc.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SdomFc createDataObject(final TSdomFc entity) throws DataException {
    SdomFc object = new SdomFc();

    object.setId(entity.getId());
    object.setName(entity.getName());
    object.setVariant(entity.getVariant());
    object.setRevision(entity.getRevision());

    return object;
  }

  /**
   * @param bcName
   * @return Set of SDOMFcs
   * @throws DataException
   */
  public List<String> getSDOMFcByBCName(final String bcName) throws DataException {
    TypedQuery<String> tQuery =
        getEntMgr().createNamedQuery(TSdomFc.GET_BY_BC_NAME, String.class).setParameter("bcName", bcName);
    return tQuery.getResultList();
  }

}
