package com.bosch.caltool.icdm.bo.cdr;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TA2lDepParam;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.A2lDepParam;

/**
 * Loader class for A2lDepParam
 *
 * @author UKT1COB
 */
public class A2lDepParamLoader extends AbstractBusinessObject<A2lDepParam, TA2lDepParam> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lDepParamLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_DEP_PARAM, TA2lDepParam.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lDepParam createDataObject(final TA2lDepParam entity) throws DataException {

    A2lDepParam A2lDepParam = new A2lDepParam();

    setCommonFields(A2lDepParam, entity);

    A2lDepParam.setA2lFileId(entity.getA2lFileId().longValue());

    A2lDepParam.setParamName(entity.getParamName());
    A2lDepParam.setDependsOnParamName(entity.getDependsOnParamName());

    return A2lDepParam;
  }

  /**
   * @return
   */
  public List<TA2lDepParam> getByA2lFileId(final Long a2lFileId) {

    final TypedQuery<TA2lDepParam> typeQuery =
        getEntMgr().createNamedQuery(TA2lDepParam.NQ_GET_BY_A2L_FILE_ID, TA2lDepParam.class);
    typeQuery.setParameter("a2lFileId", a2lFileId);
    return typeQuery.getResultList();
  }

}
