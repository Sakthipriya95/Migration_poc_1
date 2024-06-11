package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionResultOption;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionResultOption;


/**
 * Loader class for QuestionResult
 *
 * @author say8cob
 */
public class QuestionResultOptionLoader extends AbstractBusinessObject<QuestionResultOption, TQuestionResultOption> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionResultOptionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTION_RESULT_OPTION, TQuestionResultOption.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected QuestionResultOption createDataObject(final TQuestionResultOption entity) throws DataException {
    QuestionResultOption object = new QuestionResultOption();

    setCommonFields(object, entity);

    object.setQId(entity.getTQuestion().getQId());
    object.setQResultName(entity.getQResultName());
    object.setQResultType(entity.getQResultType());
    // setting allow to finsh WP
    object.setqResultAlwFinishWP(yOrNToBoolean(entity.getqResultAlwFinishWP()));

    return object;
  }

  /**
   * @param qID as input
   * @param resultName as input
   * @param resultType as input
   * @return QuestionResult
   * @throws DataException as exception
   */
  public List<TQuestionResultOption> getAllQuestionResultOptionsByQID(final Long qID) throws DataException {
    TQuestion tQuestion = new QuestionLoader(getServiceData()).getEntityObject(qID);
    return tQuestion.getTQuestionResultOptions();
  }

}
