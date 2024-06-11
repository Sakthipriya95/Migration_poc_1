/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;

/**
 * @author say8cob
 */
public class ReviewResultOutlineTreeContentProvider implements ITreeContentProvider {


  private final ReviewResultBO reviewResultBO;

  private final ReviewResultClientBO resultData;

  /**
   * @param editorInput
   */
  public ReviewResultOutlineTreeContentProvider(final ReviewResultEditorInput editorInput) {
    this.reviewResultBO = editorInput.getDataHandler();
    this.resultData = editorInput.getResultData();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if ((parentElement instanceof String) && "ROOT".equals(parentElement)) {
      return new Object[] { ApicConstants.A2L_WORK_PKG, ApicConstants.WP_RESPONSIBILITY };
    }
    return getTreeElements(parentElement);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    if ((inputElement instanceof String) && "ROOT".equals(inputElement)) {
      return new Object[] { ApicConstants.FC_CONST, ApicConstants.A2L_WORK_PKG, ApicConstants.WP_RESPONSIBILITY };
    }
    return getTreeElements(inputElement);
  }


  private Object[] getTreeElements(final Object parentElement) {
    if (isA2lWp(parentElement)) {
      return getA2lWorkPackagesNode();
    }
    // RESP Node - Fetches all the available Resposibility Types
    else if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.WP_RESPONSIBILITY)) {
      return this.reviewResultBO.getRespTypeAndRespMap().keySet().toArray();
    }
    // Resposible Node - Fetches WP for the Resposible
    else if ((parentElement instanceof A2lResponsibility) &&
        this.reviewResultBO.getRespAndA2lWPMap().containsKey(((A2lResponsibility) parentElement).getName())) {
      return getWPBasedOnResp(((A2lResponsibility) parentElement).getName());
    }
    // Responsibility Type Node - Fetches all the Resp for given Type
    else if ((parentElement instanceof String) &&
        this.reviewResultBO.getRespTypeAndRespMap().containsKey(parentElement)) {
      return getRespBasedOnRespType((String) parentElement);
    }
    else if (isFC(parentElement)) {
      return this.resultData.getFunctions().toArray();
    }
    return new Object[0];
  }


  /**
   * @return array of workpackages
   */
  public Object[] getA2lWorkPackagesNode() {
    return this.reviewResultBO.getA2lWpSet().stream().sorted().toArray();
  }


  /**
   * Method to get the WorkPkg based on selected Resp name in outline view
   *
   * @param respName as input
   * @return list of A2lWorkPackage
   */
  public Object[] getWPBasedOnResp(final String respName) {
    SortedSet<RvwResultWPandRespModel> wpBasedOnRespName =
        new TreeSet<>((final RvwResultWPandRespModel o1, final RvwResultWPandRespModel o2) -> ApicUtil
            .compare(o1.getA2lWorkPackage().getName(), o2.getA2lWorkPackage().getName()));
    wpBasedOnRespName.addAll(this.reviewResultBO.getRespAndA2lWPMap().get(respName));
    return wpBasedOnRespName.toArray();
  }

  /**
   * Method to get the Responsibility based on selected Resp Type in outline view
   *
   * @param respTypeName as input
   * @return list of A2lResponsibility
   */
  public Object[] getRespBasedOnRespType(final String respTypeName) {
    SortedSet<A2lResponsibility> respBasedOnRespType = new TreeSet<>(
        (final A2lResponsibility o1, final A2lResponsibility o2) -> ApicUtil.compare(o1.getName(), o2.getName()));
    respBasedOnRespType.addAll(this.reviewResultBO.getRespTypeAndRespMap().get(respTypeName));
    return respBasedOnRespType.toArray();
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

    if (((element instanceof String) && this.reviewResultBO.getRespTypeAndRespMap().containsKey(element)) ||
        (element instanceof A2lResponsibility)) {
      return true;
    }
    return isA2lWp(element) || isWpResp(element) || isFC(element);
  }


  /**
   * @param element
   * @return
   */
  private boolean isFC(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.FC_CONST) &&
        CommonUtils.isNotEmpty(this.resultData.getFunctions());
  }


  /**
   * @param element
   * @return
   */
  private boolean isWpResp(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.WP_RESPONSIBILITY) &&
        CommonUtils.isNotEmpty(this.reviewResultBO.getRespAndA2lWPMap());
  }


  /**
   * @param element
   * @return
   */
  private boolean isA2lWp(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.A2L_WORK_PKG) &&
        CommonUtils.isNotEmpty(this.reviewResultBO.getA2lWpSet());
  }


}
