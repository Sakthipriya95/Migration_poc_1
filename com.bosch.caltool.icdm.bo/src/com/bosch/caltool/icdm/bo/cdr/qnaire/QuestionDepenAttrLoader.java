package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;


/**
 * Loader class for QuestionConfig
 *
 * @author NIP4COB
 */
public class QuestionDepenAttrLoader extends AbstractBusinessObject<QuestionDepenAttr, TQuestionDepenAttribute> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionDepenAttrLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTION_DEPEN_ATTR, TQuestionDepenAttribute.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected QuestionDepenAttr createDataObject(final TQuestionDepenAttribute entity) throws DataException {
    QuestionDepenAttr object = new QuestionDepenAttr();

    setCommonFields(object, entity);

    object.setQId(entity.getTQuestion().getQId());
    object.setAttrId(entity.getTabvAttribute().getAttrId());

    return object;
  }

  /**
   * @param questionId questionId
   * @return
   * @throws DataException error during loading data
   */
  public Map<Long, QuestionDepenAttr> getDepenAttrMap(final Long questionId) throws DataException {
    Map<Long, QuestionDepenAttr> retMap = new HashMap<>();
    Set<TQuestionDepenAttribute> qDepenAttributes =
        new QuestionLoader(getServiceData()).getEntityObject(questionId).getTQuestionDepenAttributes();
    for (TQuestionDepenAttribute tQuestionDepenAttribute : qDepenAttributes) {
      retMap.put(tQuestionDepenAttribute.getTabvAttribute().getAttrId(), createDataObject(tQuestionDepenAttribute));
    }
    return retMap;
  }
}
