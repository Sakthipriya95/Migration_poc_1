package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;


/**
 * Loader class for RvwQnaireAnswer
 *
 * @author gge6cob
 */
public class RvwQnaireAnswerLoader extends AbstractBusinessObject<RvwQnaireAnswer, TRvwQnaireAnswer> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwQnaireAnswerLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_QNAIRE_ANS, TRvwQnaireAnswer.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwQnaireAnswer createDataObject(final TRvwQnaireAnswer entity) throws DataException {
    RvwQnaireAnswer object = new RvwQnaireAnswer();

    setCommonFields(object, entity);

    object.setQuestionId(entity.getTQuestion().getQId());
    object.setResult(entity.getResult());
    object.setMeasurement(entity.getMeasurement());
    object.setSeries(entity.getSeries());
    object.setRemark(entity.getRemark());
    if (CommonUtils.isNotEmpty(entity.getTQnaireAnsOpenPoints())) {
      Set<Long> openPoints = new HashSet<>();
      for (TRvwQnaireAnswerOpl temp : entity.getTQnaireAnsOpenPoints()) {
        openPoints.add(temp.getOpenPointsId());
      }
      object.setOplId(openPoints);
    }
    if (null != entity.getTRvwQnaireRespVersion()) {
      object.setQnaireRespVersId(entity.getTRvwQnaireRespVersion().getQnaireRespVersId());
    }
    // 493630 - Questionare name should appear instead of id in Log info
    object.setName(ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(), entity.getTQuestion().getQNameEng(),
        entity.getTQuestion().getQNameGer(), null));
    // To Load Question Result Options
    if (null != entity.getTQuestionResultOption()) {
      object.setSelQnaireResultOptID(entity.getTQuestionResultOption().getQResultOptionId());
      object.setSelQnaireResultAssement(entity.getTQuestionResultOption().getQResultType());
    //setting allow to finsh WP
      object.setResultAlwFinishWPFlag(yOrNToBoolean(entity.getTQuestionResultOption().getqResultAlwFinishWP()));
    }

    return object;
  }

  /**
   * Get all RvwQnaireAnswer records in system
   *
   * @return Map. Key - id, Value - RvwQnaireAnswer object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwQnaireAnswer> getAll() throws DataException {
    Map<Long, RvwQnaireAnswer> objMap = new ConcurrentHashMap<>();
    TypedQuery<TRvwQnaireAnswer> tQuery =
        getEntMgr().createNamedQuery(TRvwQnaireAnswer.GET_ALL, TRvwQnaireAnswer.class);
    List<TRvwQnaireAnswer> dbObj = tQuery.getResultList();
    for (TRvwQnaireAnswer entity : dbObj) {
      objMap.put(entity.getRvwAnswerId(), createDataObject(entity));
    }
    return objMap;
  }

}
