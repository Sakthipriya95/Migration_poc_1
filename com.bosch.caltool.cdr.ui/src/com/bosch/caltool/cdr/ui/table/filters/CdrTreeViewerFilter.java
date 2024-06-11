/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import java.util.Set;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author rgo7cob Icdm-549
 */
public class CdrTreeViewerFilter extends AbstractViewerFilter {

  /**
   * is function flag
   */
  private boolean funFlag;
  /**
   * is param flag
   */
  private boolean paramFlag;
  /**
   * is root flag
   */
  private boolean rootFlag;
  /**
   * selected node value
   */
  private String selectedNode;
  ReviewResultClientBO resultData;


  // Holds the filter text

  private Long selectedWpId;

  private String selectedResponsible;

  private String selectedResponsibilityType;

  private final CustomFilterGridLayer<T> filterGridLayer;

  private final ReviewResultBO reviewResultBO;


  /**
   * @param resultData Review Result Data
   * @param reviewResultBO Bo object
   * @param filterGridLayer CustomFilterGridLayer
   */
  public CdrTreeViewerFilter(final ReviewResultClientBO resultData, final ReviewResultBO reviewResultBO,
      final CustomFilterGridLayer<T> filterGridLayer) {
    this.resultData = resultData;
    this.filterGridLayer = filterGridLayer;
    this.reviewResultBO = reviewResultBO;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // check for instance
    if (element instanceof CDRResultParameter) {
      final CDRResultParameter cdrResParam = (CDRResultParameter) element;
      // if param flag is true , check for param name match
      if (checkRootNode()) {
        return true;
      }
      else if (this.paramFlag) {
        return cdrResParam.getName().equals(this.selectedNode);
      }
      // if fun flag is true , check for fun name match
      else if (this.funFlag) {
        return this.resultData.getFunctionName(cdrResParam).equals(this.selectedNode);
      }
      else if (CdrTreeViewerFilter.this.rootFlag) {
        return true;
      }
      else if ((CdrTreeViewerFilter.this.selectedWpId != null) &&
          (CdrTreeViewerFilter.this.selectedResponsible == null) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(cdrResParam.getParamId()) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(cdrResParam.getParamId())
              .containsKey(CdrTreeViewerFilter.this.selectedWpId)) {
        return true;
      }
      else if (checkRespTypeMatches(cdrResParam) || checkResposibilityMatches(cdrResParam)) {
        return true;
      }
      else {
        if ((CdrTreeViewerFilter.this.selectedResponsible != null) && (CdrTreeViewerFilter.this.selectedWpId != null) &&
            isWpRespAvailableForParam(cdrResParam.getParamId())) {
          return true;
        }
      }

    }
    return false;
  }

  private boolean checkRespTypeMatches(final CDRResultParameter resParam) {
    if ((CdrTreeViewerFilter.this.selectedResponsibilityType != null) &&
        (CdrTreeViewerFilter.this.selectedWpId == null) &&
        CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(resParam.getParamId()) &&
        CdrTreeViewerFilter.this.reviewResultBO.getRespTypeAndRespMap()
            .containsKey(CdrTreeViewerFilter.this.selectedResponsibilityType)) {
      Set<A2lResponsibility> a2lResponsibilitySet = CdrTreeViewerFilter.this.reviewResultBO.getRespTypeAndRespMap()
          .get(CdrTreeViewerFilter.this.selectedResponsibilityType);
      for (A2lResponsibility a2lResponsibility : a2lResponsibilitySet) { // To check if any of the resposibility
                                                                         // for the Resp Type matches
        if (CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(resParam.getParamId())
            .containsValue(a2lResponsibility.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean checkResposibilityMatches(final CDRResultParameter resParam) {
    return ((CdrTreeViewerFilter.this.selectedResponsible != null) && (CdrTreeViewerFilter.this.selectedWpId == null) &&
        CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(resParam.getParamId()) &&
        CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(resParam.getParamId())
            .containsValue(CdrTreeViewerFilter.this.selectedResponsible));
  }

  private boolean checkRootNode() {
    return (ApicConstants.A2L_WORK_PKG.equals(this.selectedNode) ||
        ApicConstants.WP_RESPONSIBILITY.equals(this.selectedNode) || ApicConstants.FC_CONST.equals(this.selectedNode));
  }


  /**
   * @param paramId
   * @return
   */
  private boolean isWpRespAvailableForParam(final Long paramId) {
    return CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(paramId) &&
        CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(paramId)
            .containsValue(CdrTreeViewerFilter.this.selectedResponsible) &&
        CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(paramId)
            .containsKey(CdrTreeViewerFilter.this.selectedWpId);
  }


  @Override
  public void setFilterText(final String text) {
    this.selectedNode = text;
    setFilterText(text, false);
  }

  /**
   * @param selection from the Tree Viewer
   */
  public void treeSelectionListener(final ISelection selection) {


    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      // if func , set the fun flag to true
      if (first instanceof CDRResultFunction) {
        final CDRResultFunction cdrResFun = (CDRResultFunction) first;
        this.selectedWpId = null;
        this.selectedResponsibilityType = null;
        this.selectedResponsible = null;
        this.setFilterText(cdrResFun.getName());
        setFunFlag(true);
        setParamFlag(false);
        setRootFlag(false);
        refreshFilter(false);
      }
      else if ((first instanceof String) && (ApicConstants.A2L_WORK_PKG.equals(first) ||
          ApicConstants.WP_RESPONSIBILITY.equals(first) || ApicConstants.FC_CONST.equals(first))) {
        this.setFilterText((String) first);
        refreshFilter(true);
      }
      // Review Result Work Packge
      else if (first instanceof RvwResultWPandRespModel) {
        RvwResultWPandRespModel resultWPandRespModel = (RvwResultWPandRespModel) first;
        this.selectedResponsibilityType = null;
        this.selectedWpId = resultWPandRespModel.getA2lWorkPackage().getId();
        this.selectedResponsible = CommonUtils.isNotNull(resultWPandRespModel.getA2lResponsibility())
            ? resultWPandRespModel.getA2lResponsibility().getName() : null;
        this.setFilterText(
            resultWPandRespModel.getA2lResponsibility() != null ? resultWPandRespModel.getA2lResponsibility().getName()
                : resultWPandRespModel.getA2lWorkPackage().getName());
        setFunFlag(false);
        setParamFlag(false);
        setRootFlag(false);
        refreshFilter(false);
      }
      // Responsible - A2lResponsibility
      else if (first instanceof A2lResponsibility) {
        A2lResponsibility a2lResp = (A2lResponsibility) first;
        this.selectedWpId = null;
        this.selectedResponsibilityType = null;
        this.selectedResponsible = a2lResp.getName();
        this.setFilterText(a2lResp.getName());
        setFunFlag(false);
        setParamFlag(false);
        setRootFlag(false);
        refreshFilter(false);
      }
      // Resposibility Type
      else if (first instanceof String) {
        this.selectedWpId = null;
        this.selectedResponsible = null;
        this.selectedResponsibilityType = ((String) first);
        this.setFilterText((String) first);
        setFunFlag(false);
        setParamFlag(false);
        setRootFlag(false);
        refreshFilter(false);
      }
      // if string , set the root flag to true

      else {
        clearFilter();
      }


    }

  }

  /**
   * Filter input logic specific for outline selection and filter if review result opened from pidctrenode
   *
   * @param a2lWorkPackage as input
   * @param a2lResponsibility as input
   */
  public void outlineTreeSelectionForPidcTree(final A2lWorkPackage a2lWorkPackage,
      final A2lResponsibility a2lResponsibility) {
    if ((a2lWorkPackage != null) && (a2lResponsibility != null)) {
      this.selectedWpId = a2lWorkPackage.getId();
      this.selectedResponsible = a2lResponsibility.getName();
      this.selectedResponsibilityType = null;
      this.setFilterText(a2lResponsibility.getName());
      setFunFlag(false);
      setParamFlag(false);
      setRootFlag(false);
      refreshFilter(false);
    }
    else if (a2lWorkPackage != null) {
      this.selectedWpId = a2lWorkPackage.getId();
      this.selectedResponsible = null;
      this.selectedResponsibilityType = null;
      this.setFilterText(a2lWorkPackage.getName());
      setFunFlag(false);
      setParamFlag(false);
      setRootFlag(false);
      refreshFilter(false);
    }
  }


  /**
  *
  */
  private void clearFilter() {
    this.selectedWpId = null;
    this.selectedResponsible = null;
    this.selectedResponsibilityType = null;
  }

  /**
  *
  */
  private void refreshFilter(final boolean flag) {
    if (null != this.filterGridLayer) {
      this.filterGridLayer.getComboGlazedListsFilterStrategy().applyOutlineFilterInAllColumns(flag);
      this.filterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
    }
  }

  /**
   * @return the funFlag
   */
  public boolean isFunFlag() {
    return this.funFlag;
  }

  /**
   * @param funFlag the funFlag to set
   */
  public void setFunFlag(final boolean funFlag) {
    this.funFlag = funFlag;
  }

  /**
   * @return the paramFlag
   */
  public boolean isParamFlag() {
    return this.paramFlag;
  }

  /**
   * @param paramFlag the paramFlag to set
   */
  public void setParamFlag(final boolean paramFlag) {
    this.paramFlag = paramFlag;
  }

  /**
   * @return the rootFlag
   */
  public boolean isRootFlag() {
    return this.rootFlag;
  }

  /**
   * @param rootFlag the rootFlag to set
   */
  public void setRootFlag(final boolean rootFlag) {
    this.rootFlag = rootFlag;
  }


  /**
   * iCDM-848 <br>
   *
   * @return matcher
   */
  public Matcher<CDRResultParameter> getOutlineMatcher() {
    return new CDRResultNatInputMatcher<>();
  }

  /**
   * iCDM-848 <br>
   * CDR Result NAT table input matcher
   */
  private class CDRResultNatInputMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof CDRResultParameter) {
        CDRResultParameter resParam = (CDRResultParameter) element;
        if (checkFnAndParamMatches(resParam) ||
            ((CdrTreeViewerFilter.this.selectedResponsible != null) &&
                CdrTreeViewerFilter.this.selectedResponsible.equals(ApicConstants.FC_DESC)) || // FC node
            CdrTreeViewerFilter.this.rootFlag || checkWpMatches(resParam) || checkRespTypeMatches(resParam) ||
            checkResposibilityMatches(resParam) || ((CdrTreeViewerFilter.this.selectedResponsible != null) && // WP Node
                (CdrTreeViewerFilter.this.selectedWpId != null) && isWpRespAvailableForParam(resParam.getParamId()))) {
          return true;
        }

      }
      return false;
    }

    private boolean checkFnAndParamMatches(final CDRResultParameter resParam) {
      // because function name is in both cases
      return ((CdrTreeViewerFilter.this.funFlag && CdrTreeViewerFilter.this.resultData.getFunctionName(resParam)
          .equalsIgnoreCase(CdrTreeViewerFilter.this.selectedNode)) ||
          (CdrTreeViewerFilter.this.paramFlag && resParam.getName().equals(CdrTreeViewerFilter.this.selectedNode)));
      // check for param name
    }

    private boolean checkWpMatches(final CDRResultParameter resParam) {
      return ((CdrTreeViewerFilter.this.selectedWpId != null) &&
          (CdrTreeViewerFilter.this.selectedResponsible == null) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(resParam.getParamId()) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(resParam.getParamId())
              .containsKey(CdrTreeViewerFilter.this.selectedWpId));
    }

    private boolean checkRespTypeMatches(final CDRResultParameter resParam) {
      if ((CdrTreeViewerFilter.this.selectedResponsibilityType != null) &&
          (CdrTreeViewerFilter.this.selectedWpId == null) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(resParam.getParamId()) &&
          CdrTreeViewerFilter.this.reviewResultBO.getRespTypeAndRespMap()
              .containsKey(CdrTreeViewerFilter.this.selectedResponsibilityType)) {
        Set<A2lResponsibility> a2lResponsibilitySet = CdrTreeViewerFilter.this.reviewResultBO.getRespTypeAndRespMap()
            .get(CdrTreeViewerFilter.this.selectedResponsibilityType);
        for (A2lResponsibility a2lResponsibility : a2lResponsibilitySet) { // To check if any of the resposibility
                                                                           // for the Resp Type matches
          if (CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(resParam.getParamId())
              .containsValue(a2lResponsibility.getName())) {
            return true;
          }
        }
      }
      return false;
    }

    private boolean checkResposibilityMatches(final CDRResultParameter resParam) {
      return ((CdrTreeViewerFilter.this.selectedResponsible != null) &&
          (CdrTreeViewerFilter.this.selectedWpId == null) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().containsKey(resParam.getParamId()) &&
          CdrTreeViewerFilter.this.reviewResultBO.getParamIdAndWpAndRespMap().get(resParam.getParamId())
              .containsValue(CdrTreeViewerFilter.this.selectedResponsible));
    }

  }

}
