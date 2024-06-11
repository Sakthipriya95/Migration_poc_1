package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionConfig;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionConfig;


/**
 * Loader class for QuestionConfig
 *
 * @author NIP4COB
 */
public class QuestionConfigLoader extends AbstractBusinessObject<QuestionConfig, TQuestionConfig> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionConfigLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTION_CONFIG, TQuestionConfig.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected QuestionConfig createDataObject(final TQuestionConfig entity) throws DataException {
    QuestionConfig object = new QuestionConfig();

    setCommonFields(object, entity);

    object.setQId(entity.getTQuestion().getQId());
    object.setResult(entity.getResult());
    object.setMeasurement(entity.getMeasurement());
    object.setSeries(entity.getSeries());
    object.setLink(entity.getLink());
    object.setOpenPoints(entity.getOpenPoints());
    object.setRemark(entity.getRemark());
    object.setMeasure(entity.getMeasure());
    object.setResponsible(entity.getResponsible());
    object.setCompletionDate(entity.getCompletionDate());

    return object;
  }

}
