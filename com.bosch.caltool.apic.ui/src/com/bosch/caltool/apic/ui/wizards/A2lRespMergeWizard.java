/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.MailException;
import com.bosch.caltool.icdm.common.util.OutlookMail;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lRespMergeData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityMergeModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class A2lRespMergeWizard extends Wizard {

  /**
  *
  */
  private static final String LINE_BREAK = "<br>";

  private final List<A2lResponsibility> selectedA2lWpRespList;

  private final A2lResponsibility destA2lResponsibility;

  private QnaireRespSelectionWizardPage qnaireRespSelectionWizardPage;

  private QnaireRespStatisticsWizardPage qnaireRespStatisticsWizardPage;

  private final Set<QnaireRespDetails> qnaireRespDetailsDupSet = new HashSet<>();

  private final Map<QnaireRespDetails, List<QnaireRespDetails>> qnaireRespDeatilsMap = new HashMap<>();

  private SortedSet<QnaireRespDetails> retainedQnaireRespDetailsSet = new TreeSet<>();

  private Set<Long> srcA2lRespIdSet = new HashSet<>();

  private final boolean isAdminPage;

  /**
   * @param selectedA2lWpRespList src a2l resp which needs to be merged
   * @param destA2lResponsibility destination a2l resp
   * @param isAdminPage flag to check whether it is admin page
   */
  public A2lRespMergeWizard(final List<A2lResponsibility> selectedA2lWpRespList,
      final A2lResponsibility destA2lResponsibility, final boolean isAdminPage) {
    this.selectedA2lWpRespList = selectedA2lWpRespList;
    this.destA2lResponsibility = destA2lResponsibility;
    this.isAdminPage = isAdminPage;
    fillData();
  }

  /**
   *
   */
  private void fillData() {
    List<Long> selectedA2lRespIdList =
        this.selectedA2lWpRespList.stream().map(A2lResponsibility::getId).collect(Collectors.toList());

    this.srcA2lRespIdSet = this.selectedA2lWpRespList.stream().map(A2lResponsibility::getId)
        .filter(id -> !id.equals(this.destA2lResponsibility.getId())).collect(Collectors.toSet());

    try {
      List<QnaireRespDetails> qnaireRespDetailsList =
          new RvwQnaireResponseServiceClient().getQnaireRespDetailsList(selectedA2lRespIdList);
      Set<QnaireRespDetails> qnaireRespDetailsSet = new HashSet<>();
      for (QnaireRespDetails qnaireRespDetails : qnaireRespDetailsList) {
        if (!qnaireRespDetailsSet.add(qnaireRespDetails)) {
          this.qnaireRespDetailsDupSet.add(qnaireRespDetails);
        }
        if (!this.qnaireRespDeatilsMap.containsKey(qnaireRespDetails)) {
          this.qnaireRespDeatilsMap.put(qnaireRespDetails, new ArrayList<QnaireRespDetails>());
        }
        this.qnaireRespDeatilsMap.get(qnaireRespDetails).add(qnaireRespDetails);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages() {
    // Set tile
    setWindowTitle("Merge A2l Responsibilities");

    StringBuilder description = new StringBuilder();

    description.append("A2l Responsibility to retain : ");
    description.append(this.destA2lResponsibility.getAliasName());
    if (!this.isAdminPage) {
      this.qnaireRespSelectionWizardPage =
          new QnaireRespSelectionWizardPage("Select Qnaire Response to retain", description.toString());
      addPage(this.qnaireRespSelectionWizardPage);
    }
    this.qnaireRespStatisticsWizardPage =
        new QnaireRespStatisticsWizardPage("Selected Qnaire Response(s)", description.toString());
    addPage(this.qnaireRespStatisticsWizardPage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getNextPage(final IWizardPage currentPage) {
    if (currentPage instanceof QnaireRespSelectionWizardPage) {
      if (!currentPage.isPageComplete()) {
        return currentPage;
      }
      return this.qnaireRespStatisticsWizardPage;
    }
    return super.getNextPage(currentPage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    try {
      if (this.isAdminPage) {
        Set<Long> rvwQnaireIdSet = this.retainedQnaireRespDetailsSet.stream()
            .map(val -> val.getRvwQnaireResponse().getId()).collect(Collectors.toSet());
        A2lResponsibilityMergeModel a2lResponsibilityMergeModel = new A2lResponsibilityMergeModel();
        a2lResponsibilityMergeModel.setA2lRespMergeFromIdSet(this.srcA2lRespIdSet);
        a2lResponsibilityMergeModel.setA2lRespMergeToId(this.destA2lResponsibility.getId());
        a2lResponsibilityMergeModel.setQnaireRespIdSet(rvwQnaireIdSet);
        mergeResponsibilitiesWithProgressBar(a2lResponsibilityMergeModel);
      }
      else {
        sentMail();
      }
    }
    catch (MailException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   * @param a2lResponsibilityMergeModel
   */
  private void mergeResponsibilitiesWithProgressBar(final A2lResponsibilityMergeModel a2lResponsibilityMergeModel) {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, false, monitor -> {
        monitor.beginTask("Merging the Responsibilities ...", 100);
        monitor.worked(20);
        mergeResponsibilityWS(a2lResponsibilityMergeModel);
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error("Error in invoking thread to open progress bar for Merging Responsibilities !", e,
          Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }
  }

  private void mergeResponsibilityWS(final A2lResponsibilityMergeModel a2lResponsibilityMergeModel) {
    try {
      new A2lResponsibilityServiceClient().mergeResponsibility(a2lResponsibilityMergeModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @throws ApicWebServiceException
   * @throws MailException
   */
  private void sentMail() throws ApicWebServiceException, MailException {
    A2lRespMergeData a2lRespMergeData = new A2lRespMergeData();
    a2lRespMergeData.setRetainedQnaireRespDetailsSet(this.retainedQnaireRespDetailsSet);
    a2lRespMergeData.setSelectedA2lWpRespList(this.selectedA2lWpRespList);
    a2lRespMergeData.setDestA2lResponsibility(this.destA2lResponsibility);

    String executionId = new A2lResponsibilityServiceClient().parseSelectedA2lRespStatToJson(a2lRespMergeData);

    OutlookMail mail = new OutlookMail(CDMLogger.getInstance());
    mail.setHighPriority(true);
    // Subject of email
    String subject = "Merge A2L Responsibilities";
    // Mail to address iCDM-Hotline.Clearing@de.bosch.com
    String mailToAddress = new CommonDataBO().getParameterValue(CommonParamKey.ICDM_HOTLINE_TO);

    StringBuilder mailContent = new StringBuilder();
    mailContent.append("<html>");
    mailContent.append("Hello Hotline,");
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);
    mailContent.append("Kindly merge the selected A2L Responsibilities");
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);
    mailContent.append("Execution ID : ");
    mailContent.append(executionId);
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);
    mailContent.append("Thank You!");
    mailContent.append(LINE_BREAK);
    mailContent.append(LINE_BREAK);
    mailContent.append("This is an auto generated mail from iCDM client");
    mailContent.append("</html>");

    // Compose mail
    mail.composeEmail(mailToAddress, subject, mailContent.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFinish() {
    // validate the fields before finish
    return getContainer().getCurrentPage() == this.qnaireRespStatisticsWizardPage;
  }

  /**
   * @return the qnaireRespSelectionWizardPage
   */
  public QnaireRespSelectionWizardPage getQnaireRespSelectionWizardPage() {
    return this.qnaireRespSelectionWizardPage;
  }


  /**
   * @param qnaireRespSelectionWizardPage the qnaireRespSelectionWizardPage to set
   */
  public void setQnaireRespSelectionWizardPage(final QnaireRespSelectionWizardPage qnaireRespSelectionWizardPage) {
    this.qnaireRespSelectionWizardPage = qnaireRespSelectionWizardPage;
  }


  /**
   * @return the qnaireRespStatisticsWizardPage
   */
  public QnaireRespStatisticsWizardPage getQnaireRespStatisticsWizardPage() {
    return this.qnaireRespStatisticsWizardPage;
  }


  /**
   * @param qnaireRespStatisticsWizardPage the qnaireRespStatisticsWizardPage to set
   */
  public void setQnaireRespStatisticsWizardPage(final QnaireRespStatisticsWizardPage qnaireRespStatisticsWizardPage) {
    this.qnaireRespStatisticsWizardPage = qnaireRespStatisticsWizardPage;
  }


  /**
   * @return the srcA2lWpRespList
   */
  public List<A2lResponsibility> getSelectedA2lWpRespList() {
    return this.selectedA2lWpRespList;
  }


  /**
   * @return the destA2lResponsibility
   */
  public A2lResponsibility getDestA2lResponsibility() {
    return this.destA2lResponsibility;
  }


  /**
   * @return the qnaireRespDetailsDupSet
   */
  public Set<QnaireRespDetails> getQnaireRespDetailsDupSet() {
    return this.qnaireRespDetailsDupSet;
  }


  /**
   * @return the qnaireRespDeatilsMap
   */
  public Map<QnaireRespDetails, List<QnaireRespDetails>> getQnaireRespDeatilsMap() {
    return this.qnaireRespDeatilsMap;
  }


  /**
   * @return the retainedQnaireRespDetailsSet
   */
  public SortedSet<QnaireRespDetails> getRetainedQnaireRespDetailsSet() {
    return this.retainedQnaireRespDetailsSet;
  }


  /**
   * @param retainedQnaireRespDetailsSet the retainedQnaireRespDetailsSet to set
   */
  public void setRetainedQnaireRespDetailsSet(final SortedSet<QnaireRespDetails> retainedQnaireRespDetailsSet) {
    this.retainedQnaireRespDetailsSet = retainedQnaireRespDetailsSet;
  }


}
