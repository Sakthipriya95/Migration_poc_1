/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;


/**
 * @author dmo5cob
 */
public class OutlineQuestionnareTreeViewContentProvider implements ITreeContentProvider {

  private final ReviewQuestionaireEditorInput editorInput;


  private final Comparator<Question> questionComp = new Comparator<Question>() {

    @Override
    public int compare(final Question o1, final Question o2) {
      String ques1PaddedNumber = OutlineQuestionnareTreeViewContentProvider.this.editorInput.getQnaireDefBo()
          .getPaddedQuestionNumber(o1.getId());
      String ques2PaddedNumber = OutlineQuestionnareTreeViewContentProvider.this.editorInput.getQnaireDefBo()
          .getPaddedQuestionNumber(o2.getId());
      String ques1DispName = ques1PaddedNumber.concat(o1.getName());
      String ques2DispName = ques2PaddedNumber.concat(o2.getName());
      return ApicUtil.compare(ques1DispName, ques2DispName);
    }
  };

  /**
  *
  */
  public OutlineQuestionnareTreeViewContentProvider(final ReviewQuestionaireEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    if (inputElement instanceof String) {
      QuestionnaireVersion questionnaireVersion = this.editorInput.getSelQuestionareVersion();
      return new Object[] { questionnaireVersion };
    }
    else if (inputElement instanceof QuestionnaireVersion) {
      SortedSet<Question> quesHeadingSet = new TreeSet<>(this.questionComp);
      quesHeadingSet.addAll(this.editorInput.getQnaireDefBo().getFirstLevelQuestionsWithoutDeletedQuestions());
      return quesHeadingSet.toArray();
    }
    else if ((inputElement instanceof Question) && ((Question) inputElement).getHeadingFlag()) {
      Question headingQuestion = (Question) inputElement;
      if (!headingQuestion.getDeletedFlag()) {
        SortedSet<Question> headingQues = getChildHeadings(headingQuestion);
        return headingQues.toArray();
      }
    }
    return new Object[0];

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof String) {
      return this.editorInput.getQnaireDefBo().getAllVersions().toArray();
    }
    else if (parentElement instanceof QuestionnaireVersion) {
      TreeSet<Question> quesHeadingSet = new TreeSet<>(this.questionComp);
      quesHeadingSet.addAll(this.editorInput.getQnaireDefBo().getFirstLevelQuestionsWithoutDeletedQuestions());
      return quesHeadingSet.toArray();
    }
    else if ((parentElement instanceof Question) && ((Question) parentElement).getHeadingFlag()) {
      Question headingQuestion = (Question) parentElement;
      Set<Question> headingQues = getChildHeadings(headingQuestion);
      return headingQues.toArray();
    }

    return new Object[0];

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object element) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object element) {
    if (element instanceof String) {
      return !this.editorInput.getQnaireDefBo().getAllVersions().isEmpty();
    }
    else if (element instanceof QuestionnaireVersion) {
      return !this.editorInput.getQnaireDefBo().getFirstLevelQuestionsWithoutDeletedQuestions().isEmpty();
    }
    else if ((element instanceof Question) && ((Question) element).getHeadingFlag()) {
      Question headingQuestion = (Question) element;
      Set<Question> headingQues = getChildHeadings(headingQuestion);
      return !headingQues.isEmpty();
    }
    return false;
  }

  /**
   * @param headingQuestion
   * @return
   */
  private SortedSet<Question> getChildHeadings(final Question headingQuestion) {


    SortedSet<Question> quesHeadingSet = new TreeSet<>(this.questionComp);

    for (Long quesId : this.editorInput.getQnaireDefBo().getChildQuestions().get(headingQuestion.getId())) {
      Question ques = this.editorInput.getQnaireDefBo().getQnaireDefModel().getQuestionMap().get(quesId);
      if ((ques != null) && ques.getHeadingFlag() && !ques.getDeletedFlag()) {
        quesHeadingSet.add(ques);
      }
    }

    return quesHeadingSet;
  }
}
