/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.ReviewDataViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewDataInputModel;
import com.bosch.caltool.icdm.model.cdr.ReviewDataParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.cdr.ReviewDataServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ShowReviewDataViewAction extends Action {


  private final Object selectedElement;
  private ParameterDataProvider paramDataProvider;
  private Map<String, ReviewParamResponse> reviewDataResponse;
  private String paramName;
  private String varCodedParamName;

  /**
   * @param enable enable
   * @param imageDesc imageDesc
   * @param firstElement Selected Element
   * @param paramDataProvider
   */
  public ShowReviewDataViewAction(final boolean enable, final ImageDescriptor imageDesc, final Object firstElement,
      final ParameterDataProvider paramDataProvider) { 
    // Icdm-1379- new Class for Review Data View part.
    super.setEnabled(enable);
    super.setImageDescriptor(imageDesc);
    super.setText("Show Review Data");
    this.selectedElement = firstElement;
    this.paramDataProvider = paramDataProvider;
  }

  /**
   * initilaize param Data Provider
   */
  private void initailizeParamDataProvider() {
      try {

        ReviewDataServiceClient reviewDataServiceClient = new ReviewDataServiceClient();
        ReviewDataInputModel reviewDataInputModel = new ReviewDataInputModel();
        List<String> paramNames = new ArrayList<>();
        if (this.selectedElement instanceof CDRResultParameter) {
          CDRResultParameter cDRResultParameter = (CDRResultParameter) this.selectedElement;
          paramNames.add(cDRResultParameter.getName());
          getVariantCodedParamNames(cDRResultParameter.getName(),paramNames);
        }
        
        else if (this.selectedElement instanceof String) {
          paramNames.add(this.selectedElement.toString());
          getVariantCodedParamNames(this.selectedElement.toString(),paramNames);
        }

        if (this.selectedElement instanceof IParameter) {
          paramNames.add(((IParameter) this.selectedElement).getName());
          getVariantCodedParamNames(((IParameter) this.selectedElement).getName(),paramNames);
        }
        
        if (this.selectedElement instanceof A2LParameter) {
          paramNames.add(((A2LParameter) this.selectedElement).getName());
          getVariantCodedParamNames(((A2LParameter) this.selectedElement).getName(),paramNames);
        }
       if (this.selectedElement instanceof ReviewRule) {
          paramNames.add(((ReviewRule) this.selectedElement).getParameterName());
          getVariantCodedParamNames(((ReviewRule) this.selectedElement).getParameterName(),paramNames);
        }

       reviewDataInputModel.setParamName(paramNames);
          
        if(!CommonUtils.isEmptyString(this.varCodedParamName)) {
          reviewDataInputModel.setVarCodedParam(this.varCodedParamName);
        }
        
        ReviewDataParamResponse reviewDataParamResponse = reviewDataServiceClient.getReviewData(reviewDataInputModel);
        this.reviewDataResponse = reviewDataParamResponse.getReviewParamResponse();

        if (this.reviewDataResponse != null && this.paramDataProvider == null) {
          ParameterRulesResponse singleParamRules = reviewDataParamResponse.getParameterRulesResponse();
          this.paramDataProvider = new ParameterDataProvider(singleParamRules);
        }
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

    }

  /**
   * @param varCodedParamName
   * @param paramNames
   * @return
   */
  public List<String> getVariantCodedParamNames(String paramName, List<String> paramNames) {
   
    String baseParamName = paramName;
    if (ApicUtil.isVariantCoded(paramName)) {
      this.varCodedParamName = paramName;
      baseParamName = ApicUtil.getBaseParamName(this.varCodedParamName);
      paramNames.add(baseParamName);
    }
    this.paramName = baseParamName;
    return paramNames;
  }
      
  @Override
  public void run() {
    try {
      initailizeParamDataProvider();
      final ReviewDataViewPart reviewDtVwPrt = (ReviewDataViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().showView(CommonUIConstants.REVIEW_DATA_VIEW_ID);
      // ICDM-1086
      List<ReviewRule> rules = new ArrayList<>();
      List<Long> allVarcodedId = new ArrayList<>();
      showReviewData(reviewDtVwPrt, rules, allVarcodedId);
    }
    catch (PartInitException | ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Updates the view part UI
   *
   * @param reviewDtVwPrt
   * @param rules
   * @param allVarcodedId
   * @throws ApicWebServiceException
   */
  private void showReviewData(final ReviewDataViewPart reviewDtVwPrt, final List<ReviewRule> rules,
      final List<Long> allVarcodedId)
      throws ApicWebServiceException {
    if (this.selectedElement != null) {
      if (this.selectedElement instanceof ReviewRule) {
        checkAndUpdateDataView((ReviewRule) this.selectedElement, reviewDtVwPrt, rules, allVarcodedId);
      }
      else {
      checkAndUpdateDataView(this.paramName, reviewDtVwPrt, rules,
            this.varCodedParamName);
      }
    }
  }

  /**
   * @param firstElement
   * @param reviewDtVwPrt
   * @param rules
   * @param varCodedParamName
   * @param paramName
   * @param paramId
   */
  private void checkAndUpdateDataView(final String paramName, final ReviewDataViewPart reviewDtVwPrt,
      final List<ReviewRule> rules, final String varCodedParamName) {
    boolean hasDepen;
    if (paramName != null) {

      hasDepen = this.paramDataProvider.hasDependencyCheckByName(paramName);
      List<ReviewRule> cdrRules = this.paramDataProvider.getRuleList(paramName);
      if (cdrRules != null) {
        for (ReviewRule cdrRule : cdrRules) {
          checkDependency(rules, cdrRule, hasDepen);
        }
      }
    }
    updateDataReviewViewUI(reviewDtVwPrt, rules, paramName, varCodedParamName);
  }

  /**
   * iCDM-713 <br>
   * Method to check and add based on dependencies
   *
   * @param rules
   * @param cdrRule
   */
  private void checkDependency(final List<ReviewRule> rules, final ReviewRule cdrRule, final boolean hasDepen) {
    if (cdrRule.getDependencyList().isEmpty() && hasDepen) {
      cdrRule.setDependenciesForDisplay("Default Rule");
    }
    rules.add(cdrRule);
  }

  /**
   * Get the CDR function parameter from the given input. <br>
   * A2LParameter - get the object using name <br>
   * CDRResultParameter - get the associated function parameter <br>
   * CDRFuncParameter parameter - return directly
   *
   * @param selElement
   * @param cdrFuncParameter
   * @return CDRResultParameter
   * @throws ApicWebServiceException
   */
  private IParameter getCDRFnParam(final Object selElement) throws ApicWebServiceException {
    String labelName;


    IParameter cdrFuncParameter = null;
    if (selElement instanceof A2LParameter) {
      A2LParameter a2lParam = (A2LParameter) selElement;
      labelName = a2lParam.getName();
      if (null != a2lParam.getDefFunction()) {
        cdrFuncParameter = new ParameterDataProvider<>(null).getParamByName(labelName, a2lParam.getType());
      }

    }
    if (selElement instanceof CDRResultParameter) {
      CDRResultParameter resParam = (CDRResultParameter) selElement;
      cdrFuncParameter = new ParameterDataProvider<>(null).getParamByName((resParam).getName(), resParam.getpType());
    }
    if (selElement instanceof IParameter) {
      cdrFuncParameter = (IParameter) selElement;
    }

    if (selElement instanceof String) {
      cdrFuncParameter = new ParameterDataProvider<>(null).getParamOnlyByName(selElement.toString());
    }
    return cdrFuncParameter;
  }

  /**
   * update the Review data view with the given input
   *
   * @param reviewDtVwPrt view part
   * @param rules list of rules of the parameter
   * @param paramName parameter name
   * @param paramIds param ID
   * @param varCodedParamName varCodedParamName
   */
  protected void updateDataReviewViewUI(final ReviewDataViewPart reviewDtVwPrt, final List<ReviewRule> rules,
      final String paramName, final String varCodedParamName) {
    if (!reviewDtVwPrt.getScrolledForm().isDisposed()) {
      // Set text to srolled form
      reviewDtVwPrt.getScrolledForm().setText("PAR: " + paramName);
    }
    reviewDtVwPrt.resetUIControls(true);
    if (rules != null) {
      reviewDtVwPrt.fillUIControls(rules, paramName, varCodedParamName, this.reviewDataResponse);
    }

  }

  /**
   * @param firstElement
   * @param reviewDtVwPrt
   * @param rules
   * @param object
   * @param paramId
   * @param hasDepen
   */
  private void checkAndUpdateDataView(final ReviewRule rule, final ReviewDataViewPart reviewDtVwPrt,
      final List<ReviewRule> rules, final List<Long> paramIds)
      throws ApicWebServiceException {
    boolean hasDepen = false;
    Long paramId = 0L;
    String paramName = rule.getParameterName();
    Parameter funcParam = this.paramDataProvider.getParamByName(rule.getParameterName(), rule.getValueType());
    // iCDM-713

    if (funcParam != null) {
      paramId = funcParam.getId();
      hasDepen = this.paramDataProvider.hasDependency(funcParam);
    }
    checkDependency(rules, rule, hasDepen);
    paramIds.add(paramId);
    updateDataReviewViewUI(reviewDtVwPrt, rules, paramName, null);
  }
}
