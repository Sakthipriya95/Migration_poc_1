package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * RvwQnaireAnswer Model class
 *
 * @author gge6cob
 */
public class RvwQnaireAnswerDummy extends RvwQnaireAnswer {

  /**
   *
   */
  private static final long serialVersionUID = 4823442650861004919L;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return getQuestionId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RvwQnaireAnswerDummy clone() {
    return (RvwQnaireAnswerDummy) super.clone();
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
    if ((getId() == -1l) || ((obj.getClass() == this.getClass()) && (((RvwQnaireAnswerDummy) obj).getId() == -1l))) {
      return false;
    }
    if ((obj.getClass() == this.getClass())) {
      return ModelUtil.isEqual(getId(), ((RvwQnaireAnswerDummy) obj).getId());
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

}
