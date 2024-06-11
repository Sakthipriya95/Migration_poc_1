package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;


/**
 * Loader class for Question
 *
 * @author NIP4COB
 */
public class QuestionLoader extends AbstractBusinessObject<Question, TQuestion> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public QuestionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.QUESTION, TQuestion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Question createDataObject(final TQuestion entity) throws DataException {
    Question object = new Question();

    setCommonFields(object, entity);

    object.setQnaireVersId(entity.getTQuestionnaireVersion().getQnaireVersId());
    object.setQNumber(entity.getQNumber());
    object.setQNameEng(entity.getQNameEng());
    object.setQNameGer(entity.getQNameGer());
    object.setName(getLangSpecTxt(entity.getQNameEng(), entity.getQNameGer()));
    object.setQHintEng(entity.getQHintEng());
    object.setQHintGer(entity.getQHintGer());
    object.setHeadingFlag(yOrNToBoolean(entity.getHeadingFlag()));
    object.setParentQId(entity.getTQuestion() == null ? null : entity.getTQuestion().getQId());
    object.setDeletedFlag(yOrNToBoolean(entity.getDeletedFlag()));
    object.setResultRelevantFlag(yOrNToBoolean(entity.getResultRelevantFlag()));
    object.setPositiveResult(entity.getPositiveResult());
    object.setDepQuesId(entity.getDepQuestion() == null ? null : entity.getDepQuestion().getQId());
    object.setDepQuesResp(entity.getDepQuesResponse());
    object.setDepQResultOptId(
        entity.getDepQResultOption() == null ? null : entity.getDepQResultOption().getQResultOptionId());
    object.setQuestionConfigId(entity.getTQuestionConfig() == null ? null : entity.getTQuestionConfig().getQconfigId());
    object.setDescription(getLangSpecTxt(entity.getQHintEng(), entity.getQHintGer()));
    return object;
  }


  /**
   * @param questionnaireVersionId questionnaireVersionId
   * @return SortedSet of Question
   * @throws DataException error during fetching questions
   */
  public SortedSet<Question> getAllQuestions(final Long questionnaireVersionId) throws DataException {
    getLogger().debug("Loading Questions of QuestionnaireVersion : " + questionnaireVersionId);
    SortedSet<Question> retSet = new TreeSet<>();
    Set<TQuestion> questionEntities =
        new QuestionnaireVersionLoader(getServiceData()).getEntityObject(questionnaireVersionId).getTQuestions();
    if (null != questionEntities) {
      for (TQuestion tQuestion : questionEntities) {
        retSet.add(createDataObject(tQuestion));
      }
    }
    return retSet;
  }

  /**
   * @param questionnaireVersionId questionnaireVersionId
   * @return first level of questions
   * @throws DataException error during fetching questions
   */
  public SortedSet<Question> getFirstLevelQuestions(final Long questionnaireVersionId) throws DataException {
    SortedSet<Question> firstLevel = new TreeSet<>();
    for (Question question : getAllQuestions(questionnaireVersionId)) {
      if (null == question.getParentQId()) {
        firstLevel.add(question);
      }
    }
    return firstLevel;
  }

  /**
   * @param questionnaireVersionId questionnaireVersionId
   * @param includeHeadings includeHeadings
   * @return All the questions under the version - if includeHeadings is true or return only questions which are not
   *         headings if the includeHeadings is false
   * @throws DataException error during fetching questions
   */
  public SortedSet<Question> getAllQuestions(final Long questionnaireVersionId, final boolean includeHeadings)
      throws DataException {
    SortedSet<Question> allQnsSet = getAllQuestions(questionnaireVersionId);
    SortedSet<Question> retSet = new TreeSet<>(allQnsSet);

    if (!includeHeadings) {
      for (Question qsn : allQnsSet) {
        if (qsn.getHeadingFlag()) {
          retSet.remove(qsn);
        }
      }
    }

    return retSet;
  }


  /**
   * @param questionnaireVersionId questionnaireVersionId
   * @param includeHeadings includeHeadings
   * @return A map containing all the questions under the version - including headings if includeHeadings is true, or
   *         only questions which are not headings if includeHeadings is false
   * @throws DataException error during fetching questions
   */
  public Map<Long, Question> getAllQuestionsAsMap(final Long questionnaireVersionId, final boolean includeHeadings)
      throws DataException {
    SortedSet<Question> allQnsSet = getAllQuestions(questionnaireVersionId);
    Map<Long, Question> questionMap = new HashMap<>();

    for (Question question : allQnsSet) {
      if (includeHeadings || !question.getHeadingFlag()) {
        questionMap.put(question.getId(), question);
      }
    }

    return questionMap;
  }


  /**
   * @param questionId questionId
   * @return
   * @throws DataException error during loading data
   */
  public Set<QuestionDepenAttr> getDepenAttr(final Long questionId) throws DataException {
    Set<QuestionDepenAttr> retSet = new HashSet<>();
    Set<TQuestionDepenAttribute> qDepenAttributes = getEntityObject(questionId).getTQuestionDepenAttributes();
    QuestionDepenAttrLoader qDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());

    for (TQuestionDepenAttribute tQuestionDepenAttribute : qDepenAttributes) {
      retSet.add(qDepAttrLoader.createDataObject(tQuestionDepenAttribute));
    }
    return retSet;
  }

  /**
   * @param questionId
   * @return
   * @throws IcdmException
   */
  public Set<Attribute> getAttributes(final Long questionId) throws IcdmException {
    Set<Attribute> retSet = new HashSet<>();
    Set<QuestionDepenAttr> aDepAttrSet = getDepenAttr(questionId);
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (QuestionDepenAttr qDepAttr : aDepAttrSet) {
      retSet.add(attrLoader.getDataObjectByID(qDepAttr.getAttrId()));
    }
    return retSet;
  }


  /**
   * @param questionId
   * @return
   * @throws DataException
   */
  public Map<QuestionDepenAttr, List<QuestionDepenAttrValue>> getAttrAndValMapForCombi(final Long questionId)
      throws DataException {
    Map<QuestionDepenAttr, List<QuestionDepenAttrValue>> retMap = new HashMap<>();
    QuestionDepenAttrLoader qDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());
    QuestionDepenAttrValueLoader qDepAttrValLoader = new QuestionDepenAttrValueLoader(getServiceData());

    Set<TQuestionDepenAttribute> qDepenAttributes = getEntityObject(questionId).getTQuestionDepenAttributes();

    for (TQuestionDepenAttribute tQuestionDepenAttribute : qDepenAttributes) {
      List<QuestionDepenAttrValue> valueList = new ArrayList<>();
      Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues = tQuestionDepenAttribute.getTQuestionDepenAttrValues();
      for (TQuestionDepenAttrValue tQdepAttrVal : tQuestionDepenAttrValues) {
        valueList.add(qDepAttrValLoader.getDataObjectByID(tQdepAttrVal.getDepenAttrValId()));
      }
      retMap.put(qDepAttrLoader.getDataObjectByID(tQuestionDepenAttribute.getQattrDepenId()), valueList);
    }
    return retMap;
  }

  /**
   * @param questionId
   * @return
   * @throws DataException
   */
  public SortedSet<Question> getChildQuestions(final Long questionId) throws DataException {
    SortedSet<Question> childQuestions = new TreeSet<>();
    TQuestion dbQues = getEntityObject(questionId);
    for (TQuestion child : dbQues.getTQuestions()) {
      childQuestions.add(createDataObject(child));
    }
    return childQuestions;
  }

  /**
   * @param questionId
   * @return
   * @throws DataException
   */
  public QuestAttrAndValDepModel getQuestAttrAndValDepModel(final Long questionId) throws DataException {
    QuestAttrAndValDepModel ret = new QuestAttrAndValDepModel();

    QuestionDepenAttrLoader qDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());
    QuestionDepenAttrValueLoader qDepAttrValLoader = new QuestionDepenAttrValueLoader(getServiceData());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());

    Set<TQuestionDepenAttribute> tQuestionDepenAttributes = getEntityObject(questionId).getTQuestionDepenAttributes();
    for (TQuestionDepenAttribute tQuestionDepenAttribute : tQuestionDepenAttributes) {
      ret.getAttributeMap().put(tQuestionDepenAttribute.getTabvAttribute().getAttrId(),
          attrLoader.getDataObjectByID(tQuestionDepenAttribute.getTabvAttribute().getAttrId()));
      String key = questionId + "" + tQuestionDepenAttribute.getTabvAttribute().getAttrId();
      ret.getqDepAttrMap().put(key, qDepAttrLoader.createDataObject(tQuestionDepenAttribute));
      Set<QuestionDepenAttrValue> qDepAttrValSet = new HashSet<>();
      for (TQuestionDepenAttrValue tQDepAttrVal : tQuestionDepenAttribute.getTQuestionDepenAttrValues()) {
        qDepAttrValSet.add(qDepAttrValLoader.createDataObject(tQDepAttrVal));
        ret.getAttributeValueMap().put(tQDepAttrVal.getTabvAttrValue().getValueId(),
            attrValLoader.getDataObjectByID(tQDepAttrVal.getTabvAttrValue().getValueId()));
      }
      ret.getqDepattrValueMap().put(tQuestionDepenAttribute.getQattrDepenId(), qDepAttrValSet);
    }
    return ret;
  }

  /**
   * Gets the all qn depn attr val model.
   *
   * @param qnaireVersId the qnaire vers id
   * @return the all qn depn attr val model
   * @throws DataException the data exception
   */
  public QuestAttrAndValDepModel getAllQnDepnAttrValModel(final Long qnaireVersId) throws DataException {
    QuestAttrAndValDepModel ret = new QuestAttrAndValDepModel();

    QuestionDepenAttrLoader qDepAttrLoader = new QuestionDepenAttrLoader(getServiceData());
    QuestionDepenAttrValueLoader qDepAttrValLoader = new QuestionDepenAttrValueLoader(getServiceData());
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    TQuestionnaireVersion tqnaireVersion =
        new QuestionnaireVersionLoader(getServiceData()).getEntityObject(qnaireVersId);
    for (TQuestion qn : tqnaireVersion.getTQuestions()) {
      Set<TQuestionDepenAttribute> tQuestionDepenAttributes = qn.getTQuestionDepenAttributes();
      for (TQuestionDepenAttribute tQuestionDepenAttribute : tQuestionDepenAttributes) {
        ret.getAttributeMap().put(tQuestionDepenAttribute.getTabvAttribute().getAttrId(),
            attrLoader.getDataObjectByID(tQuestionDepenAttribute.getTabvAttribute().getAttrId()));
        String key = Long.toString(qn.getQId()) + "" + tQuestionDepenAttribute.getTabvAttribute().getAttrId();
        ret.getqDepAttrMap().put(key, qDepAttrLoader.createDataObject(tQuestionDepenAttribute));
        Set<QuestionDepenAttrValue> qDepAttrValSet = new HashSet<>();
        for (TQuestionDepenAttrValue tQDepAttrVal : tQuestionDepenAttribute.getTQuestionDepenAttrValues()) {
          qDepAttrValSet.add(qDepAttrValLoader.createDataObject(tQDepAttrVal));
          ret.getAttributeValueMap().put(tQDepAttrVal.getTabvAttrValue().getValueId(),
              attrValLoader.getDataObjectByID(tQDepAttrVal.getTabvAttrValue().getValueId()));
        }
        ret.getqDepattrValueMap().put(tQuestionDepenAttribute.getQattrDepenId(), qDepAttrValSet);
      }
    }
    return ret;
  }
}
