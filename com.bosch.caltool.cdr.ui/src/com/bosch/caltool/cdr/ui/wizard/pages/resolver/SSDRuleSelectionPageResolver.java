/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.SSDRuleSelectionPage;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class SSDRuleSelectionPageResolver implements IReviewUIDataResolver {

  /**
   * {@inheritDoc}
   */
  @Override
  public void processNextPressed() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processBackPressed() {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CalDataReviewWizard getInput() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fillUIData(final CalDataReviewWizard calDataReviewWizard) {
    SSDRuleSelectionPage ssdRuleSelPage = calDataReviewWizard.getSsdRuleSelPage();
    CDRWizardUIModel cdrWizardUIModel = calDataReviewWizard.getCdrWizardUIModel();

    if (cdrWizardUIModel.getSsdRuleFilePath() != null) {
      ssdRuleSelPage.getSsdFileTxt().setText(cdrWizardUIModel.getSsdRuleFilePath());
    }
    else {
      Long ssdReleaseId = cdrWizardUIModel.getSsdReleaseId();
      if ((ssdReleaseId != null) && (cdrWizardUIModel.getMappedSSDReleases() != null)) {
        List<FeatureValueICDMModel> feaValueModel = new ArrayList<>();

        for (SSDReleaseIcdmModel ssdReleaseModel : cdrWizardUIModel.getMappedSSDReleases()) {
          if (ssdReleaseModel.getReleaseId().equals(ssdReleaseId)) {
            ssdRuleSelPage.getSsdTabViewer().setSelection(new StructuredSelection(ssdReleaseModel), true);
            ssdReleaseModel.getDependencyList().forEach(featureValueModel -> {
              FeatureValueICDMModel featureValueICDMModel = new FeatureValueICDMModel();
              featureValueICDMModel.setFeatureId(featureValueModel.getFeatureId());
              featureValueICDMModel.setFeatureText(featureValueModel.getFeatureText());
              featureValueICDMModel.setValueId(featureValueModel.getValueId());
              featureValueICDMModel.setValueText(featureValueModel.getValueText());
              feaValueModel.add(featureValueICDMModel);
            });
          }
        }

        List<SSDFeatureICDMAttrModel> ssdFeaAttrList;
        try {
          ssdFeaAttrList = new CDRHandler().getSSDFeatureICDMAttrModelList(feaValueModel);
          ssdRuleSelPage.getFeaValTabViewer().setInput(ssdFeaAttrList);

        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

}
