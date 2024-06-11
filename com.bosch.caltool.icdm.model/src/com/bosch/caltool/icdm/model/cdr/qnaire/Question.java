package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author NIP4COB
 */
public class Question implements Comparable<Question>, IDataObject {

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 128324230440528L;
  /**
   * Q Id
   */
  private Long id;
  /**
   * Qnaire Vers Id
   */
  private Long qnaireVersId;
  /**
   * Q Number
   */
  private Long qNumber;
  /**
   * Q Name Eng
   */
  private String qNameEng;
  /**
   * Q Name Ger
   */
  private String qNameGer;
  /**
   * Q Hint Eng
   */
  private String qHintEng;
  /**
   * Q Hint Ger
   */
  private String qHintGer;
  /**
   * Heading Flag
   */
  private boolean headingFlag;
  /**
   * Parent Q Id
   */
  private Long parentQId;
  /**
   * Deleted Flag
   */
  private boolean deletedFlag;

  /**
   * Result relevant for overall status
   */
  private boolean resultRelevantFlag;

  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;
  /**
   * Positive Result
   */
  private String positiveResult;
  /**
   * Dep Ques Id
   */
  private Long depQuesId;
  /**
   * Dep Ques Resp
   */
  private String depQuesResp;
  /**
   * Dep Q Result Opt Id
   */
  private Long depQResultOptId;

  private Long questionConfigId;
  private String name;
  private String description;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return qnaireVersId
   */
  public Long getQnaireVersId() {
    return this.qnaireVersId;
  }

  /**
   * @param qnaireVersId set qnaireVersId
   */
  public void setQnaireVersId(final Long qnaireVersId) {
    this.qnaireVersId = qnaireVersId;
  }

  /**
   * @return qNumber
   */
  public Long getQNumber() {
    return this.qNumber;
  }

  /**
   * @param qNumber set qNumber
   */
  public void setQNumber(final Long qNumber) {
    this.qNumber = qNumber;
  }

  /**
   * @return qNameEng
   */
  public String getQNameEng() {
    return this.qNameEng;
  }

  /**
   * @param qNameEng set qNameEng
   */
  public void setQNameEng(final String qNameEng) {
    this.qNameEng = qNameEng;
  }

  /**
   * @return qNameGer
   */
  public String getQNameGer() {
    return this.qNameGer;
  }

  /**
   * @param qNameGer set qNameGer
   */
  public void setQNameGer(final String qNameGer) {
    this.qNameGer = qNameGer;
  }

  /**
   * @return qHintEng
   */
  public String getQHintEng() {
    return this.qHintEng;
  }

  /**
   * @param qHintEng set qHintEng
   */
  public void setQHintEng(final String qHintEng) {
    this.qHintEng = qHintEng;
  }

  /**
   * @return qHintGer
   */
  public String getQHintGer() {
    return this.qHintGer;
  }

  /**
   * @param qHintGer set qHintGer
   */
  public void setQHintGer(final String qHintGer) {
    this.qHintGer = qHintGer;
  }

  /**
   * @return headingFlag
   */
  public boolean getHeadingFlag() {
    return this.headingFlag;
  }

  /**
   * @param headingFlag set headingFlag
   */
  public void setHeadingFlag(final boolean headingFlag) {
    this.headingFlag = headingFlag;
  }

  /**
   * @return parentQId
   */
  public Long getParentQId() {
    return this.parentQId;
  }

  /**
   * @param parentQId set parentQId
   */
  public void setParentQId(final Long parentQId) {
    this.parentQId = parentQId;
  }


  /**
   * @return the questionConfigId
   */
  public Long getQuestionConfigId() {
    return this.questionConfigId;
  }


  /**
   * @param questionConfigId the questionConfigId to set
   */
  public void setQuestionConfigId(final Long questionConfigId) {
    this.questionConfigId = questionConfigId;
  }

  /**
   * @return deletedFlag
   */
  public boolean getDeletedFlag() {
    return this.deletedFlag;
  }

  /**
   * @param deletedFlag set deletedFlag
   */
  public void setDeletedFlag(final boolean deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  /**
   * @return the resultRelevant
   */
  public boolean getResultRelevantFlag() {
    return this.resultRelevantFlag;
  }


  /**
   * @param resultRelevantFlag the resultRelevant to set
   */
  public void setResultRelevantFlag(final boolean resultRelevantFlag) {
    this.resultRelevantFlag = resultRelevantFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return positiveResult
   */
  public String getPositiveResult() {
    return this.positiveResult;
  }

  /**
   * @param positiveResult set positiveResult
   */
  public void setPositiveResult(final String positiveResult) {
    this.positiveResult = positiveResult;
  }

  /**
   * @return depQuesId
   */
  public Long getDepQuesId() {
    return this.depQuesId;
  }

  /**
   * @param depQuesId set depQuesId
   */
  public void setDepQuesId(final Long depQuesId) {
    this.depQuesId = depQuesId;
  }

  /**
   * @return depQuesResp
   */
  public String getDepQuesResp() {
    return this.depQuesResp;
  }

  /**
   * @param depQuesResp set depQuesResp
   */
  public void setDepQuesResp(final String depQuesResp) {
    this.depQuesResp = depQuesResp;
  }

  /**
   * @return depQResultOptId
   */
  public Long getDepQResultOptId() {
    return this.depQResultOptId;
  }

  /**
   * @param depQResultOptId set depQResultOptId
   */
  public void setDepQResultOptId(final Long depQResultOptId) {
    this.depQResultOptId = depQResultOptId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Question object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() == this.getClass()) {
      return ModelUtil.isEqual(getId(), ((Question) obj).getId());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

}
