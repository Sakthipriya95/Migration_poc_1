package com.bosch.caltool.icdm.bo.a2l;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResp;


/**
 * Loader class for A2L Responsibility
 *
 * @author apj4cob
 */
public class A2lRespLoader extends AbstractBusinessObject<A2lResp, TA2lResp> {

  /**
   * Root id not present so 0l
   */
  public static final Long ROOT_ID_NOT_PRESENT = 0L;

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_RESP, TA2lResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lResp createDataObject(final TA2lResp entity) throws DataException {
    A2lResp object = new A2lResp();

    setCommonFields(object, entity);

    object.setPidcA2lId(entity.getTPidcA2l().getPidcA2lId());
    object.setWpTypeId(entity.getWpTypeId());
    object.setWpRootId(entity.getWpRootId());
    object.setRespVarId(entity.getRespVarId());

    return object;
  }


  /**
   * Get A2LResponsibility based on inputs
   *
   * @param pidcA2lID pidcA2lID
   * @param wpTypeId wpType
   * @param wpRootId wpRoot
   * @return the a2lResp object
   * @throws DataException error while retrieving data
   */
  public A2lResp getA2lResp(final Long pidcA2lID, final Long wpTypeId, final Long wpRootId) throws DataException {
    TypedQuery<TA2lResp> typeQuery;
    if (wpRootId > ROOT_ID_NOT_PRESENT) {
      typeQuery = getEntMgr().createNamedQuery(TA2lResp.FIND_QUERY_WITH_ROOT, TA2lResp.class);
      typeQuery.setParameter("wpRootId", wpRootId);
    }
    else {
      typeQuery = getEntMgr().createNamedQuery(TA2lResp.FIND_QUERY_WITHOUT_ROOT, TA2lResp.class);
    }
    typeQuery.setParameter("pidcA2lId", pidcA2lID);
    typeQuery.setParameter("wpTypeId", wpTypeId);

    // Build BO for the result set
    final List<TA2lResp> ta2lRep = typeQuery.getResultList();
    // Build BO for the result set
    if (CommonUtils.isNotEmpty(ta2lRep)) {
      return createDataObject(ta2lRep.get(0));
    }
    return null;
  }
}
