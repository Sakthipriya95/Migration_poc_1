/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.cdr.RvwResEditorInputData;
import com.bosch.caltool.icdm.common.ui.editors.IIcdmEditorInput;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;


/**
 * Input class for ReviewParamEditor
 *
 * @author mkl2cob
 */
public class ReviewResultEditorInput implements IIcdmEditorInput, ILinkSelectionProvider {

  private CDRReviewResult reviewResult;

  private CDRResultParameter selectedParam;

  private CDRResultFunction selectedFunc;

  ReviewResultClientBO resultData;

  private CDRResultFunction resultFunction;
  private RvwVariant rvwVariant;

  private A2lWorkPackage parentA2lWorkpackage;

  private A2lResponsibility parentA2lResponsible;

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc} returns the name of the function
   */
  @Override
  public String getName() {
    String name = "";
    if (CommonUtils.isNotNull(this.reviewResult)) {
      name = this.reviewResult.getName();
    }
    if (CommonUtils.isNotNull(this.rvwVariant)) {
      name = this.rvwVariant.getName();
    }
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc} returns the name of the function
   */
  @Override
  public String getToolTipText() {
    return this.reviewResult != null ? this.reviewResult.getName() : "";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }


    // iCDM-208
    return checkForSameResultIds(obj);

  }

  /**
   * @param obj
   * @return boolean
   */
  private boolean checkForSameResultIds(final Object obj) {
    ReviewResultEditorInput other = (ReviewResultEditorInput) obj;

    Long resultId1 = getResultId(this.reviewResult, this.rvwVariant);
    Long resultId2 = getResultId(other.getReviewResult(), other.getRvwVariant());
    return CommonUtils.isNotNull(resultId1) && CommonUtils.isNotNull(resultId2) &&
        ((resultId1.longValue() == resultId2.longValue()));
  }

  /**
   * @param rvwVariant2
   * @param reviewResult2
   * @return Long
   */
  private Long getResultId(final CDRReviewResult result, final RvwVariant rvwVar) {
    Long resultId = null;
    if (CommonUtils.isNull(result)) {
      if (CommonUtils.isNotNull(rvwVar)) {
        resultId = rvwVar.getResultId().longValue();
      }
    }
    else {
      resultId = result.getId().longValue();
    }
    return resultId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.reviewResult.getId().intValue();
  }


  // iCDM-1241
  /**
   * {@inheritDoc}
   */
  @Override
  public Object getEditorInputSelection() {
    RvwResEditorInputData editorInputData = new RvwResEditorInputData();
    if (CommonUtils.isNotNull(this.rvwVariant)) {
      editorInputData.setRvwVariant(this.rvwVariant);
    }
    if (CommonUtils.isNotNull(this.reviewResult)) {
      editorInputData.setRvwResult(this.reviewResult);
    }
    if (CommonUtils.isNotNull(this.parentA2lWorkpackage)) {
      editorInputData.setA2lWorkpackageId(this.parentA2lWorkpackage.getId());
    }
    if (CommonUtils.isNotNull(this.parentA2lResponsible)) {
      editorInputData.setA2lRespId(this.parentA2lResponsible.getId());
    }
    return editorInputData;
  }


  /**
   * @return the reviewResult
   */
  public CDRReviewResult getReviewResult() {
    return this.reviewResult;
  }


  /**
   * @param reviewResult the reviewResult to set
   */
  public void setReviewResult(final CDRReviewResult reviewResult) {
    this.reviewResult = reviewResult;
  }

  /**
   * @param selectedParam selected parameter
   */
  public void setCdrReviewResultParam(final CDRResultParameter selectedParam) {
    this.selectedParam = selectedParam;

  }

  /**
   * @return the selectedParam
   */

  public CDRResultParameter getCdrReviewResultParam() {
    return this.selectedParam;
  }


  /**
   * @return the selectedFunc
   */
  public CDRResultFunction getSelectedFunc() {
    return this.selectedFunc;
  }


  /**
   * @param selectedFunc the selectedFunc to set
   */
  public void setSelectedFunc(final CDRResultFunction selectedFunc) {
    this.selectedFunc = selectedFunc;
  }

  /**
   * @return the resultData
   */
  public ReviewResultClientBO getResultData() {
    return this.resultData;
  }


  /**
   * @param resultData the resultData to set
   */
  public void setResultData(final ReviewResultClientBO resultData) {
    this.resultData = resultData;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewResultBO getDataHandler() {
    return getResultData().getResultBo();
  }


  /**
   * @return the rvwVariant
   */
  public RvwVariant getRvwVariant() {
    return this.rvwVariant;
  }


  /**
   * @param rvwVariant the rvwVariant to set
   */
  public void setRvwVariant(final RvwVariant rvwVariant) {
    this.rvwVariant = rvwVariant;
  }


  /**
   * @return the resultFunction
   */
  public CDRResultFunction getResultFunction() {
    return this.resultFunction;
  }


  /**
   * @param resultFunction the resultFunction to set
   */
  public void setResultFunction(final CDRResultFunction resultFunction) {
    this.resultFunction = resultFunction;
  }


  /**
   * @return the parentA2lWorkpackage
   */
  public A2lWorkPackage getParentA2lWorkpackage() {
    return this.parentA2lWorkpackage;
  }


  /**
   * @param parentA2lWorkpackage the parentA2lWorkpackage to set
   */
  public void setParentA2lWorkpackage(final A2lWorkPackage parentA2lWorkpackage) {
    this.parentA2lWorkpackage = parentA2lWorkpackage;
  }


  /**
   * @return the parentA2lResponsible
   */
  public A2lResponsibility getParentA2lResponsible() {
    return this.parentA2lResponsible;
  }


  /**
   * @param parentA2lResponsible the parentA2lResponsible to set
   */
  public void setParentA2lResponsible(final A2lResponsibility parentA2lResponsible) {
    this.parentA2lResponsible = parentA2lResponsible;
  }


}
