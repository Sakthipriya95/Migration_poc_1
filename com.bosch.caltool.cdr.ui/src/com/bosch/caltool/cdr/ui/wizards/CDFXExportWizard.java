/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.pages.CDFXExportInputWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.CdfxExportStatisticsPage;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class CDFXExportWizard extends Wizard {

  /**
   * Page 1
   */
  private CDFXExportInputWizardPage cdfxExportInputWizardPage;

  /**
   * Page 2
   */
  private CdfxExportStatisticsPage cdfxStatisticsWizardPage;

  private CDFXExportWizardData cdfxExportWizardData;

  private Map<WpRespModel, List<Long>> workPackageRespMap = new HashMap<>();


  /**
   * @param pidcTreeNode pidcTreeNode
   * @param pidcA2l PIDC A2L file
   * @param pidcVers Pidc Version
   * @param pidc Pidc object
   * @param cdfxReadinessConditionStr Readiness condition
   * @param pidcVariant selected variant
   * @param wpRespModel workpackage and resposnibility model
   * @param containPreSelctedWP flag value that says if the wp is selected from A2L view
   */
  public CDFXExportWizard(final PidcTreeNode pidcTreeNode, final String cdfxReadinessConditionStr,
      final List<WpRespModel> wpRespModel, final boolean containPreSelctedWP) {
    this.cdfxExportWizardData = new CDFXExportWizardData();
    this.cdfxExportWizardData.setPidcA2l(pidcTreeNode.getPidcA2l());
    this.cdfxExportWizardData.setPidcVers(pidcTreeNode.getPidcVersion());
    this.cdfxExportWizardData.setPidc(pidcTreeNode.getPidc());
    this.cdfxExportWizardData.setCdfxReadinessConditionStr(cdfxReadinessConditionStr);
    this.cdfxExportWizardData.setWpRespModels(wpRespModel);
    this.cdfxExportWizardData.setDefaultPidcVariant(pidcTreeNode.getPidcVariant());
    this.cdfxExportWizardData.setContainPreSelectedWP(containPreSelctedWP);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    if ((this.cdfxExportWizardData.getOutputDirecPath() != null) &&
        !this.cdfxExportWizardData.getOutputDirecPath().isEmpty() &&
        this.cdfxStatisticsWizardPage.getAutoOpenDirCheckbox().getSelection()) {
      CommonUiUtils.openFile(this.cdfxExportWizardData.getOutputDirecPath());
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFinish() {
    // validate the fields before finish
    return getContainer().getCurrentPage() == this.cdfxStatisticsWizardPage;
  }


  /**
   * @return true if 'Export' button can be enabled
   */
  public boolean validateFields() {
    boolean variantsAvailable = (this.cdfxExportWizardData.variantsAvailable() &&
        (CommonUtils.isNotNull(this.cdfxExportWizardData.getPidcVariant()) ||
            !CommonUtils.isNullOrEmpty(this.cdfxExportWizardData.getPidcVariants()))) ||
        !this.cdfxExportWizardData.variantsAvailable();
    return variantsAvailable && checkIfWpListNotEmpty();
  }


  /**
   * @return
   */
  private boolean checkIfWpListNotEmpty() {
    if (this.cdfxExportInputWizardPage.getWorkPackageRadio().getSelection()) {
      return (this.cdfxExportInputWizardPage.getWrkPkgList().getItemCount() > 0);
    }
    return true;
  }

  /**
   * @return the workPackageRespMap
   */
  public Map<WpRespModel, List<Long>> getWorkPackageRespMap() {
    Set<PidcVariant> selectedVariantList = getCdfxExportWizardData().getPidcVariants();
    if (CommonUtils.isNotEmpty(selectedVariantList)) {
      selectedVariantList.forEach(variant -> fetchWpRespMap(variant.getId()));
    }
    else {
      fetchWpRespMap(Boolean.TRUE.equals(getCdfxExportWizardData().isVariantsAvailable())
          ? getCdfxExportWizardData().getPidcVariant().getId() : 0L);
    }

    return this.workPackageRespMap;
  }

  /**
   * @param pidcVarId
   */
  private void fetchWpRespMap(final Long pidcVarId) {
    if (((this.workPackageRespMap == null) || this.workPackageRespMap.isEmpty()) &&
        (getCdfxExportWizardData().getPidcA2l().getId() != null)) {
      setWorkPackageRespMap(getWpRespModel(getCdfxExportWizardData().getPidcA2l().getId(), pidcVarId));
    }
  }

  /**
   * This method is to get the WpResp for selection in Workpackage selection page
   *
   * @param pidcA2lId as selected Pidc A2l id
   * @param variantId as selected variant
   * @return map of wprespmodel and list of param ids
   */
  public Map<WpRespModel, List<Long>> getWpRespModel(final Long pidcA2lId, final Long variantId) {

    Map<WpRespModel, List<Long>> resolveWpRespLabels = new HashMap<>();
    try {
      List<WpRespLabelResponse> wpRespLabResponse =
          new CDRHandler().getWorkPackageRespBasedOnPidcA2lIdAndVarId(pidcA2lId, variantId);
      resolveWpRespLabels = resolveWpRespLabels(wpRespLabResponse);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return resolveWpRespLabels;
  }

  /**
   * This method to get Wp Resp Map based on wpRespLabResponse
   *
   * @param wpRespLabResponse as input
   */
  private Map<WpRespModel, List<Long>> resolveWpRespLabels(final List<WpRespLabelResponse> wpRespLabResponse) {

    Map<WpRespModel, List<Long>> wpRespLabMap = new HashMap<>();
    for (WpRespLabelResponse wpRespLabelResponse : wpRespLabResponse) {
      long paramId = wpRespLabelResponse.getParamId();
      WpRespModel wpRespModel = wpRespLabelResponse.getWpRespModel();
      if (wpRespModel.getA2lResponsibility() != null) {
        wpRespModel.setWpRespName(wpRespModel.getA2lResponsibility().getName());
      }
      List<Long> paramIdList = new ArrayList<>();
      if (wpRespLabMap.get(wpRespModel) == null) {
        wpRespLabMap.put(wpRespModel, paramIdList);
      }
      else {
        paramIdList = wpRespLabMap.get(wpRespModel);
      }
      paramIdList.add(paramId);
    }
    // For Adding parameter count in the WpRespModel
    wpRespLabMap.entrySet().forEach(wpMap -> wpMap.getKey().setParamCount((long) wpMap.getValue().size()));

    return wpRespLabMap;
  }

  @Override
  public void addPages() {
    // Set tile
    setWindowTitle("100% CDFx Export");

    // add the review report page
    this.cdfxExportInputWizardPage =
        new CDFXExportInputWizardPage("A2L File -" + this.cdfxExportWizardData.getPidcA2l().getName());
    addPage(this.cdfxExportInputWizardPage);

    // add the review statistics page
    this.cdfxStatisticsWizardPage = new CdfxExportStatisticsPage("Export Summary", this.cdfxExportWizardData);
    addPage(this.cdfxStatisticsWizardPage);
  }

  @Override
  public IWizardPage getNextPage(final IWizardPage currentPage) {
    if (currentPage instanceof CDFXExportInputWizardPage) {
      CDFXExportInputWizardPage myPage = (CDFXExportInputWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
      return this.cdfxStatisticsWizardPage;
    }
    return super.getNextPage(currentPage);
  }


  /**
   * @return the cdfxExportWizardData
   */
  public CDFXExportWizardData getCdfxExportWizardData() {
    return this.cdfxExportWizardData;
  }


  /**
   * @param cdfxExportWizardData the cdfxExportWizardData to set
   */
  public void setCdfxExportWizardData(final CDFXExportWizardData cdfxExportWizardData) {
    this.cdfxExportWizardData = cdfxExportWizardData;
  }

  /**
   * @param workPackageRespMap the workPackageRespMap to set
   */
  public void setWorkPackageRespMap(final Map<WpRespModel, List<Long>> workPackageRespMap) {
    this.workPackageRespMap = workPackageRespMap;
  }

  /**
   * @return CDFXExportInputWizardPage
   */
  public CDFXExportInputWizardPage getCdfxExportInputWizardPage() {
    return this.cdfxExportInputWizardPage;
  }

  /**
   * @return ReviewStatisticsWizardPage
   */
  public CdfxExportStatisticsPage getReviewStatisticsWizardPage() {
    return this.cdfxStatisticsWizardPage;
  }

}
