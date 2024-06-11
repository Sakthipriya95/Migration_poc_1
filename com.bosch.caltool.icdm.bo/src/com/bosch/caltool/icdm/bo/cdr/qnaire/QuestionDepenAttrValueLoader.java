package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;


/**
 * Loader class for QuestionDepenAttrValue
 *
 * @author NIP4COB
 */
public class QuestionDepenAttrValueLoader
    extends AbstractBusinessObject<QuestionDepenAttrValue, TQuestionDepenAttrValue> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionDepenAttrValueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTION_DEPEN_ATTR_VALUE, TQuestionDepenAttrValue.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected QuestionDepenAttrValue createDataObject(final TQuestionDepenAttrValue entity) throws DataException {
    QuestionDepenAttrValue object = new QuestionDepenAttrValue();

    setCommonFields(object, entity);

    object.setQAttrDepId(entity.getTQuestionDepenAttribute().getTabvAttribute().getAttrId());
    object.setQCombiNum(entity.getQCombiNum());
    object.setValueId(entity.getTabvAttrValue().getValueId());

    return object;
  }

}
