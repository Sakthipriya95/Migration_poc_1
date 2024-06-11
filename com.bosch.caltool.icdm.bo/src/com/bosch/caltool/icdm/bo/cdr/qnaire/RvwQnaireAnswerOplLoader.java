package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;


/**
 * Loader class for RvwQnaireAnswerOpl
 *
 * @author gge6cob
 */
public class RvwQnaireAnswerOplLoader extends AbstractBusinessObject<RvwQnaireAnswerOpl, TRvwQnaireAnswerOpl> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwQnaireAnswerOplLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_QNAIRE_ANSWER_OPL, TRvwQnaireAnswerOpl.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwQnaireAnswerOpl createDataObject(final TRvwQnaireAnswerOpl entity) throws DataException {
    RvwQnaireAnswerOpl object = new RvwQnaireAnswerOpl();

    setCommonFields(object, entity);
    object.setOpenPoints(entity.getOpenPoints());
    object.setMeasure(entity.getMeasure());
    object.setCompletionDate(timestamp2String(entity.getCompletionDate()));
    object.setResult(entity.getResult());
    if (null != entity.getTRvwQnaireAnswer()) {
      object.setRvwAnswerId(entity.getTRvwQnaireAnswer().getRvwAnswerId());
    }
    if (CommonUtils.isNotNull(entity.getTabvApicUser())) {
      object.setResponsible(entity.getTabvApicUser().getUserId());
      try {
        object.setResponsibleName(
            (new UserLoader(getServiceData()).getDataObjectByID(entity.getTabvApicUser().getUserId()))
                .getDescription());
      }
      catch (IcdmException e) {
        getLogger().error("Error getting user details for the Qnaire answer open point :" + object.getId(), e);
      }
    }
    return object;
  }

  /**
   * Get all RvwQnaireAnswerOpl records in system
   *
   * @return Map. Key - id, Value - RvwQnaireAnswerOpl object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwQnaireAnswerOpl> getAll() throws DataException {
    Map<Long, RvwQnaireAnswerOpl> objMap = new ConcurrentHashMap<>();
    TypedQuery<TRvwQnaireAnswerOpl> tQuery =
        getEntMgr().createNamedQuery(TRvwQnaireAnswerOpl.GET_ALL, TRvwQnaireAnswerOpl.class);
    List<TRvwQnaireAnswerOpl> dbObj = tQuery.getResultList();
    for (TRvwQnaireAnswerOpl entity : dbObj) {
      objMap.put(entity.getOpenPointsId(), createDataObject(entity));
    }
    return objMap;
  }

}
