/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;

import com.bosch.caltool.apic.ui.actions.AttributesActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.AttributesEditorInput;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.PIDCSearchEditorInput;
import com.bosch.caltool.apic.ui.editors.pages.FocusMatrixPage;
import com.bosch.caltool.apic.ui.table.filters.FocusMatrixToolBarActionSet;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.dialogs.ReviewResultExportDialog;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditorInput;
import com.bosch.caltool.cdr.ui.editors.QnaireRespEditorInput;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditorInput;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.cdr.ui.jobs.CompareHexWithRwDataExportJob;
import com.bosch.caltool.cdr.ui.jobs.QuestResponseExportJob;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.views.FavoritesViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ui.action.A2LActionSet;
import com.bosch.caltool.icdm.ui.action.FC2WPNatToolBarActionSet;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditor;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditorInput;
import com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2LCompareEditor;
import com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2LCompareEditorInput;
import com.bosch.caltool.icdm.ui.editors.a2lcomparison.A2lParamComparePage;
import com.bosch.caltool.icdm.ui.editors.pages.FC2WPNatFormPage;
import com.bosch.caltool.icdm.ui.editors.pages.WPLabelAssignNatPage;
import com.bosch.caltool.icdm.ui.views.A2LDetailsPage;
import com.bosch.caltool.usecase.ui.actions.UseCaseActionSet;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;
import com.bosch.caltool.usecase.ui.views.UseCaseDetailsViewPart;
import com.bosch.caltool.usecase.ui.views.UseCaseTreeViewPart;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * Handler class for ReviewParamEditor open icon.
 *
 * @author rgo7cob Icdm-630
 */
public class ReportExportHandler extends AbstractHandler {


  private static final String REGEX = "[^a-zA-Z0-9]+";

  private static final String SAVE_EXCEL_REPORT = "Save Excel Report";
  /**
   * Rule to run the jobs in sequence.
   */
  private final MutexRule mutexRule = new MutexRule();
  // ICDM-796
  /** editorinput instance. */
  IEditorInput editorInput;

  /** active editor. */
  IEditorPart activeEditor;

  /** selected view instance. */
  IWorkbenchPart viewPart;

  /** pid action set instance. */
  PIDCActionSet pidActionSet;

  /** usecase action set instance. */
  UseCaseActionSet ucActionSet;

  /** cdr action set for invoking the excel export. */
  CdrActionSet cdrActionSet;

  FocusMatrixToolBarActionSet fmActionSet;

  /** message for FILE already opened. */
  private static final String FILE_ALREADY_OPEN_MSG =
      "Excel file already open, Please close the file and try export again";


  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    // ICDM-796
    this.editorInput = null;
    this.activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    this.viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
    this.pidActionSet = new PIDCActionSet();
    this.ucActionSet = new UseCaseActionSet();
    this.cdrActionSet = new CdrActionSet();
    this.fmActionSet = new FocusMatrixToolBarActionSet(null, null);
    // get the active editor's input
    if (this.activeEditor != null) {
      this.editorInput = this.activeEditor.getEditorInput();
    }
    // selection made on view
    if ((this.viewPart != null) && !(this.viewPart instanceof IEditorPart)) {
      viewPartActive();
    }
    // editor part enabled/disabled
    if (null != this.editorInput) {
      editorPartActive();
    }
    return null;
  }

  /**
   * ICDM-796 Export button enabled based on the current editor.
   */
  private void editorPartActive() {
    // ICDM-1539, Excel Export for Review Rule
    if (this.editorInput instanceof ReviewParamEditorInput) {
      ReviewParamEditorInput reviewParamEditorInput = (ReviewParamEditorInput) this.editorInput;
      ParameterDataProvider<?, ?> paramDataProvider = reviewParamEditorInput.getParamDataProvider();
      ParamCollectionDataProvider dataProvider = reviewParamEditorInput.getDataProvider();
      ParamCollection selectedObject = reviewParamEditorInput.getSelectedObject();
      this.cdrActionSet.excelExport(selectedObject, paramDataProvider, dataProvider);
    }
    else if (this.editorInput instanceof ReviewResultEditorInput) {
      excelReportAction(((ReviewResultEditorInput) this.editorInput).getResultData());
    }
    else if (this.editorInput instanceof ReviewListEditorInput) {
      this.cdrActionSet.exportSelectedReviews(((ReviewListEditorInput) this.editorInput).getSelectedResults());
    }
    else if (this.editorInput instanceof UseCaseEditorInput) {
      SortedSet<IUseCaseItemClientBO> items = new TreeSet<>();
      items.add(((UseCaseEditorInput) this.editorInput).getSelectedUseCase());
      this.ucActionSet.exportAction(items);
    }
    else if (this.editorInput instanceof PIDCSearchEditorInput) {// ICDM-1213
      PidcVersion pidcVer = ((PIDCSearchEditorInput) this.editorInput).getSelectedPidcVersion();
      // Export action
      this.pidActionSet.excelExport(pidcVer);
    }
    else if (this.editorInput instanceof PIDCEditorInput) {
      setExportActionForPIDC();
    }
    else if (this.editorInput instanceof AttributesEditorInput) {
      AttributesActionSet attrActionSet = new AttributesActionSet(null, null);
      attrActionSet.attrsExportAction();
    }
    else if (this.editorInput instanceof CdrReportEditorInput) {// ICDM-1703
      CdrReportDataHandler cdrData = ((CdrReportEditorInput) this.editorInput).getReportData();
      this.cdrActionSet.exportCdrReportToExcel(cdrData);
    }
    else if (this.editorInput instanceof CompHexWithCDFxEditorInput) {
      compareExcelReportAction(((CompHexWithCDFxEditorInput) this.editorInput).getCompHexDataHdlr());
    }
    else if ((this.editorInput instanceof QnaireRespEditorInput) &&
        (this.activeEditor instanceof QnaireResponseEditor)) {
      QnaireResponseEditor qnaireRespEditor = (QnaireResponseEditor) this.activeEditor;
      QnaireRespSummaryPage qnaireRespSummaryPage = qnaireRespEditor.getQuesSummaryPge();
      QnaireRespEditorInput qNaireResponseEditorInput = (QnaireRespEditorInput) this.editorInput;
      questNaireRespExcelReportAction(qNaireResponseEditorInput.getQnaireRespEditorDataHandler().getNameForPart(),
          qNaireResponseEditorInput.getReviewAnsSet(), qNaireResponseEditorInput.getQnaireRespEditorDataHandler(),
          qnaireRespSummaryPage.getVisibleColumns());
    }
    else if (this.editorInput instanceof FC2WPEditorInput) {
      setExportActionForFC2WP();
    }
    else if (this.editorInput instanceof A2LContentsEditorInput) {
      exportWpLavelAssignments();
    }
    else if (this.editorInput instanceof A2LCompareEditorInput) {
      exportA2lParamCompare();
    }
  }

  /**
   *
   */
  private void setExportActionForFC2WP() {
    FC2WPNatFormPage fc2wpPage = null;
    if (this.activeEditor instanceof FC2WPEditor) {
      FC2WPEditor fc2wpEditor = (FC2WPEditor) this.activeEditor;
      fc2wpPage = fc2wpEditor.getFC2WPNatFormPage();
    }
    FC2WPEditorInput fc2wpEditorInput = (FC2WPEditorInput) this.editorInput;
    FC2WPNatToolBarActionSet fc2wpActionSet = new FC2WPNatToolBarActionSet(null, null, fc2wpEditorInput);
    fc2wpActionSet.exportFC2WPReportToExcel((FC2WPEditorInput) this.editorInput, fc2wpPage);
  }

  /**
   *
   */
  private void setExportActionForPIDC() {
    PIDCEditorInput pidcEditorInputNew = (PIDCEditorInput) this.editorInput;
    PIDCEditor pidcEditor = (PIDCEditor) this.activeEditor;

    if (pidcEditor.getActivePageInstance() instanceof FocusMatrixPage) {
      FocusMatrixDataHandler fmDataHandler = pidcEditorInputNew.getFmDataHandler();
      this.fmActionSet.exportFocusMatrixAction(fmDataHandler);
    }
    else {
      PidcVersion pidcVer = pidcEditorInputNew.getSelectedPidcVersion();
      // Export action
      if (!pidcVer.isDeleted()) {
        this.pidActionSet.excelExport(pidcVer);
      }
    }
  }


  // ICDM-1994
  /**
   * Quest naire resp excel report action.
   *
   * @param qNaireName the given name of the questionnaire response
   * @param sortedSet the set of questions and answer
   * @param qnaireRespEditorDataHandler the qnaire resp editor data handler
   * @param visibleColmns
   */
  private void questNaireRespExcelReportAction(final String qNaireName, final SortedSet<RvwQnaireAnswer> sortedSet,
      final QnaireRespEditorDataHandler qnaireRespEditorDataHandler, final List<String> visibleColmns) {
    String fileName = qNaireName;
    fileName = FileNameUtil.formatFileName(fileName, ApicConstants.SPL_CHAR_PTRN);

    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText(SAVE_EXCEL_REPORT);
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setOverwrite(true);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    String fileSelected = fileDialog.open();
    String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
    if (fileSelected != null) {
      final File file = new File(CommonUtils.getCompleteFilePath(fileSelected, fileExtn));
      if (CommonUtils.checkIfFileOpen(file)) {
        MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN, FILE_ALREADY_OPEN_MSG);
        return;
      }
      Job questResponseExportJob = new QuestResponseExportJob(ReportExportHandler.this.mutexRule, sortedSet,
          qnaireRespEditorDataHandler, visibleColmns, fileSelected, fileExtn);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      questResponseExportJob.schedule();
    }
  }

  /**
   * ICDM-796 Export button enabled based on selection in the view part.
   */
  private void viewPartActive() {
    PidcVersion pidcVer;
    if (this.viewPart instanceof PIDTreeViewPart) {
      PidcTreeNode treeNode = ((PIDTreeViewPart) this.viewPart).getSelectedTreeNode();
      exportPidcTreeNodeToExcel(treeNode);
    }
    else if (this.viewPart instanceof PIDCDetailsViewPart) {
      if (this.editorInput instanceof PIDCEditorInput) {
        pidcVer = ((PIDCEditorInput) this.editorInput).getSelectedPidcVersion();
        // Export action
        if (!pidcVer.isDeleted()) {
          this.pidActionSet.excelExport(pidcVer);
        }
      }
    }
    else if (this.viewPart instanceof UseCaseDetailsViewPart) {
      if (this.editorInput instanceof UseCaseEditorInput) {
        SortedSet<IUseCaseItemClientBO> items = new TreeSet<>();
        items.add(((UseCaseEditorInput) this.editorInput).getSelectedUseCase());
        this.ucActionSet.exportAction(items);
      }
    }
    else if (this.viewPart instanceof FavoritesViewPart) {
      PidcTreeNode treeNode = ((FavoritesViewPart) this.viewPart).getSelectedTreeNode();
      exportPidcTreeNodeToExcel(treeNode);
    }
    else if (this.viewPart instanceof UseCaseTreeViewPart) {
      setExportActionForUsecase();
    }
  }

  /**
   *
   */
  private void setExportActionForUsecase() {
    SortedSet<IUseCaseItemClientBO> items = new TreeSet<>();
    if (((UseCaseTreeViewPart) this.viewPart).getSelectedUseCase() != null) {
      items.add(((UseCaseTreeViewPart) this.viewPart).getSelectedUseCase());
    }
    else if (((UseCaseTreeViewPart) this.viewPart).getSelectedUCNode() != null) {
      items.addAll((((UseCaseTreeViewPart) this.viewPart).getSelectedUCNode()).getUseCaseGroups(false));
    }
    else if (((UseCaseTreeViewPart) this.viewPart).getSelectedUcGroup() != null) {
      items.add(((UseCaseTreeViewPart) this.viewPart).getSelectedUcGroup());
    }
    this.ucActionSet.exportAction(items);
  }

  /**
   *
   */
  private void exportWpLavelAssignments() {
    if (this.activeEditor instanceof A2LContentsEditor) {
      IFormPage page = ((A2LContentsEditor) this.activeEditor).getActivePageInstance();
      if ((page instanceof A2LDetailsPage) || (page instanceof WPLabelAssignNatPage)) {
        A2LContentsEditorInput a2lEditorInput = ((A2LContentsEditorInput) this.editorInput);
        A2LActionSet a2LActionSet = new A2LActionSet();
        a2LActionSet.exportWPLabelPage(a2lEditorInput.getDataHandler().getA2lWpInfoBo());
      }
    }
  }

  /**
   * Export A2l Parameter Compare to excel
   */
  private void exportA2lParamCompare() {
    if (this.activeEditor instanceof A2LCompareEditor) {
      IFormPage page = ((A2LCompareEditor) this.activeEditor).getActivePageInstance();
      if ((page instanceof A2lParamComparePage)) {
        A2LActionSet a2LActionSet = new A2LActionSet();
        a2LActionSet.exportA2LParamCompare((A2lParamComparePage) page);
      }
    }
  }

  /**
   * Export pidc tree node to excel.
   *
   * @param treeNode the tree node
   */
  private void exportPidcTreeNodeToExcel(final PidcTreeNode treeNode) {
    if (treeNode != null) {
      switch (treeNode.getNodeType()) {
        case REV_RES_NODE:
          CDRReviewResult result = treeNode.getReviewResult();
          excelReportAction(result);
          break;
        case ACTIVE_PIDC_VERSION:
        case OTHER_PIDC_VERSION:
          this.pidActionSet.excelExport(treeNode.getPidcVersion());
          break;
        default:
          break;
      }
    }


  }

  /**
   * Icdm-630.
   *
   * @param resultClientBO the result client BO
   */
  private void excelReportAction(final ReviewResultClientBO resultClientBO) {
    ReviewResultExportDialog exportDiallog =
        new ReviewResultExportDialog(Display.getDefault().getActiveShell(), resultClientBO);
    exportDiallog.open();
  }

  /**
   * Excel report action.
   *
   * @param reviewResult the review result
   */
  private void excelReportAction(final CDRReviewResult reviewResult) {
    if (CDRConstants.REVIEW_STATUS.OPEN == CDRConstants.REVIEW_STATUS.getType(reviewResult.getRvwStatus())) {
      CDMLogger.getInstance().warnDialog("'Cancelled' Review cannot be exported", Activator.PLUGIN_ID);
      return;
    }
    Set<CDRReviewResult> results = new HashSet<>();
    results.add(reviewResult);
    ReviewResultExportDialog exportDiallog =
        new ReviewResultExportDialog(Display.getDefault().getActiveShell(), results);
    exportDiallog.open();

  }

  /**
   * Compare excel report action.
   *
   * @param compData the comp data
   */
  private void compareExcelReportAction(final CompHexWithCDFxDataHandler compData) {
    String varName = (compData.getSelctedVar()) == null ? "<NO-VARIANT>" : compData.getSelctedVar().getName();
    String fileName = "Comparison - " + compData.getHexFileName() + "  and \nReview Results of " +
        compData.getA2lFile().getFilename() + "\nVariant - " + varName;

    fileName = fileName.replaceAll(REGEX, "_");

    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText(SAVE_EXCEL_REPORT);
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setOverwrite(true);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    String fileSelected = fileDialog.open();
    String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
    if (fileSelected != null) {
      final File file = new File(CommonUtils.getCompleteFilePath(fileSelected, fileExtn));
      if (CommonUtils.checkIfFileOpen(file)) {
        MessageDialogUtils.getInfoMessageDialog(IMessageConstants.EXCEL_ALREADY_OPEN, FILE_ALREADY_OPEN_MSG);
        return;
      }

      Job job =
          new CompareHexWithRwDataExportJob(ReportExportHandler.this.mutexRule, compData, fileSelected, fileExtn, true);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      job.schedule();
    }
  }
}

