/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards; // NOPMD by bne4cob on 3/11/14 5:22 PM

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.wizards.pages.FileSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewCalDataWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewFilesSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.RuleSetSltnPage;
import com.bosch.caltool.cdr.ui.wizards.pages.SSDRuleSelectionPage;
import com.bosch.caltool.cdr.ui.wizards.pages.WorkpackageSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.a2l.WpRespLabelResponse;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantWrapper;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.review.FileData;
import com.bosch.caltool.icdm.model.cdr.review.PidcData;
import com.bosch.caltool.icdm.model.cdr.review.ResultData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.cdr.review.RulesData;
import com.bosch.caltool.icdm.model.cdr.review.UserData;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.FileParserConstants.INPUT_FILE_TYPE;
import com.bosch.caltool.labfunparser.textparser.InputFileParser;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author bru2cob
 */
public class CalDataReviewWizard extends Wizard { // NOPMD by bne4cob on 3/11/14 5:22 PM

  /**
   * constant for Start Calibration Data Review 1/4
   */
  private static final String CAL_DATA_RVW_1_4 = "Start Calibration Data Review 1/4";
  /**
   * Constant for erroe msg while saving results
   */
  private static final String ERROR_MSG_SAVING_RESULTS = "Error occured while saving results";

  /**
   * CODE if the attribute is not set
   */
  public static final String CODE_FOR_ATTRNOTSET = "-2";

  /**
   * CODE if the attribute is yes but value not set
   */
  public static final String CODE_FOR_ATTRVALUENOTSET = "-1";

  /**
   * Temp directory string
   */
  private static final String TEMP_DIR = "java.io.tmpdir";
  /**
   * Wizard optional page - A2l file selection
   */
  private FileSelectionWizardPage a2lSelWizPage;

  // Needed for 3.0.0 CDR

  private PidcA2LBO pidcA2LBO;

  private A2LFileInfoBO a2lEditiorDataHandler;

  private CDRWizardUIModel cdrWizardUIModel = new CDRWizardUIModel();

  private PidcTreeNodeHandler pidcTreeNodeHandler;

  private Map<Long, PidcVersionAttribute> pidcVersionAttributeMap = new HashMap<>();

  private SortedSet<RuleSet> allRuleSet = new TreeSet<>();


  private boolean hasVariant;


  private boolean errorOccuredinReview;

  /**
   * Wizard Page - 1 - Project selection
   */
  private ProjectDataSelectionWizardPage projectSelWizPage;


  /**
   * Wizard Page - 2 - Workpackage selection
   */
  private WorkpackageSelectionWizardPage wpSelWizPage;

  /**
   * Wizard Page - 3 - Review files selection
   */
  private ReviewFilesSelectionWizardPage filesSelWizPage;


  /**
   * Wizard Page - 4 - Review caldata progress
   */
  private ReviewCalDataWizardPage prgressWizPage;


  /**
   * to check whether the review is from pidc tree
   */
  private boolean a2lFileAvailable;

  /**
   * to check whether the contents are changed when performing cancel
   */
  private boolean contentChanged;

  /**
   * Instance of A2LEditorDataProvider
   */
  private A2LEditorDataProvider a2lEditorDataProvider;
  /**
   * true when an a2l is changed
   */
  private boolean isA2lFileChanged;
  /**
   * true if it is delta review
   */
  private final boolean deltaReview;

  private RuleSetSltnPage ruleSetSelPage;
  private final DELTA_REVIEW_TYPE deltaReviewType;
  private SSDRuleSelectionPage ssdRuleSelPage;

  private ReviewInput reviewData;


  private Long parentCDRResultId;


  private Map<WpRespModel, List<Long>> workPackageRespMap = new HashMap<>();
  // iCDM-2355
  /**
   * the preference instance to store the location of cdfx file while Review
   */
  private final IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

  /**
   * @param a2lFileAvailable true if review is started through a2l in pidc tree
   * @param isDeltaReview is delta review
   * @param deltaReviewType delta Review Type
   */
  public CalDataReviewWizard(final boolean a2lFileAvailable, final boolean isDeltaReview,
      final DELTA_REVIEW_TYPE deltaReviewType) {
    super();
    this.a2lFileAvailable = a2lFileAvailable;
    this.deltaReview = isDeltaReview;
    this.deltaReviewType = deltaReviewType;
    this.reviewData = new ReviewInput();
  }

  /**
   * @param a2lFileAvailable true if a2l file already available
   * @param cdrParentResult cdrresult ojbect
   * @param isDeltaReview is delta review
   * @param deltaReviewType delta Review Type
   */
  public CalDataReviewWizard(final boolean a2lFileAvailable, final CDRReviewResult cdrParentResult,
      final boolean isDeltaReview, final DELTA_REVIEW_TYPE deltaReviewType) {
    super();
    this.a2lFileAvailable = a2lFileAvailable;
    this.deltaReview = isDeltaReview;
    this.deltaReviewType = deltaReviewType;

  }

  /**
   * @param a2lFileAvailable true if a2l file already available
   * @param canceledCDRResult canceled CDR Result
   * @param cdrParentResult cdrresult ojbect
   * @param isDeltaReview is delta review
   * @param deltaReviewType delta Review Type
   */
  public CalDataReviewWizard(final boolean a2lFileAvailable, final CDRReviewResult canceledCDRResult,
      final CDRReviewResult cdrParentResult, final boolean isDeltaReview, final DELTA_REVIEW_TYPE deltaReviewType) {
    super();
    this.a2lFileAvailable = a2lFileAvailable;
    this.deltaReview = isDeltaReview;
    this.deltaReviewType = deltaReviewType;
  }

  /**
   * @return the hasVariant
   */
  public boolean isHasVariant() {
    return this.hasVariant;
  }


  /**
   * @param pidvVersionId
   * @param includeDeleted
   * @throws ApicWebServiceException
   */
  public void setHasVaraint(final Long pidvVersionId, final boolean includeDeleted) throws ApicWebServiceException {
    this.hasVariant = new CDRHandler().hasVariant(pidvVersionId, includeDeleted);
  }

  /**
   * @return the cdrWizardUIModel
   */
  public CDRWizardUIModel getCdrWizardUIModel() {
    return this.cdrWizardUIModel;
  }


  /**
   * @param cdrWizardUIModel the cdrWizardUIModel to set
   */
  public void setCdrWizardUIModel(final CDRWizardUIModel cdrWizardUIModel) {
    setA2lFileAvailable(cdrWizardUIModel.getA2lFileId() != null);
    this.cdrWizardUIModel = cdrWizardUIModel;
  }


  /**
   * @return the a2lSelWizPage
   */
  public FileSelectionWizardPage getA2lSelWizPage() {
    return this.a2lSelWizPage;
  }


  /**
   * @return the projectSelWizPage
   */
  public ProjectDataSelectionWizardPage getProjectSelWizPage() {
    return this.projectSelWizPage;
  }


  /**
   * @return the wpSelWizPage
   */
  public WorkpackageSelectionWizardPage getWpSelWizPage() {
    return this.wpSelWizPage;
  }


  /**
   * @return the filesSelWizPage
   */
  public ReviewFilesSelectionWizardPage getFilesSelWizPage() {
    return this.filesSelWizPage;
  }


  /**
   * @return the ruleSetSelPage
   */
  public RuleSetSltnPage getRuleSetSelPage() {
    return this.ruleSetSelPage;
  }


  /**
   * @param ruleSetSelPage the ruleSetSelPage to set
   */
  public void setRuleSetSelPage(final RuleSetSltnPage ruleSetSelPage) {
    this.ruleSetSelPage = ruleSetSelPage;
  }


  /**
   * @return the parentCDRResultId
   */
  public Long getParentCDRResultId() {
    return this.parentCDRResultId;
  }


  /**
   * @param parentCDRResultId the parentCDRResultId to set
   */
  public void setParentCDRResultId(final Long parentCDRResultId) {
    this.parentCDRResultId = parentCDRResultId;
  }


  /**
   * @return the reviewData
   */
  public ReviewInput getReviewData() {
    return this.reviewData;
  }


  /**
   * @param reviewData the reviewData to set
   */
  public void setReviewData(final ReviewInput reviewData) {
    this.reviewData = reviewData;
  }

  /**
   * @return the isDeltaReview
   */
  public boolean isDeltaReview() {

    return this.deltaReview;
  }

  /**
   * @return true if project data delta review
   */
  public boolean isProjectDataDeltaReview() {
    return this.deltaReview && (this.deltaReviewType == DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW);
  }

  /**
   * @return true if project data delta review
   */
  @Override
  public IWizardPage getNextPage(final IWizardPage currentPage) {
    // ICDM-1800
    if (currentPage instanceof ProjectDataSelectionWizardPage) {
      ProjectDataSelectionWizardPage myPage = (ProjectDataSelectionWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }
    }
    else if (currentPage instanceof WorkpackageSelectionWizardPage) {
      WorkpackageSelectionWizardPage myPage = (WorkpackageSelectionWizardPage) currentPage;
      if (!myPage.isPageComplete()) {
        return currentPage;
      }

      if (myPage.getSsdRuleButton().getSelection()) {
        return this.ssdRuleSelPage;
      }

      if (myPage.isNextPageRuleSetPage()) {

        // Task 231283
        this.ruleSetSelPage.enableDisableRuleSetPage(!getCdrWizardUIModel().isCommonRulesPrimary());
        return this.ruleSetSelPage;
      }
      return this.filesSelWizPage;
    }

    else if (currentPage instanceof SSDRuleSelectionPage) {
      if (this.wpSelWizPage.isNextPageRuleSetPage()) {
        // Task 231283
        this.ruleSetSelPage.enableDisableRuleSetPage(!getCdrWizardUIModel().isCommonRulesPrimary());
        return this.ruleSetSelPage;

      }
      return this.filesSelWizPage;
    }
    else if (currentPage instanceof ReviewFilesSelectionWizardPage) {
      return getNxtPageToRvwFileSelWizardPage(currentPage);
    }
    return super.getNextPage(currentPage);

  }

  /**
   * @param currentPage
   * @return
   */
  private IWizardPage getNxtPageToRvwFileSelWizardPage(final IWizardPage currentPage) {
    ReviewFilesSelectionWizardPage myPage = (ReviewFilesSelectionWizardPage) currentPage;
    myPage.getErrorMessage();
    IWizardPage nxtPageToMyPage = null;
    if (!myPage.isPageComplete() || myPage.getCalDataReviewWizard().isErrorOccuredinReview()) {
      nxtPageToMyPage = currentPage;
    }
    if (getCdrWizardUIModel().getReviewOutput() != null) {
      nxtPageToMyPage = this.prgressWizPage;
    }
    return nxtPageToMyPage;
  }

  @Override
  public void addPages() {
    setNeedsProgressMonitor(true);
    // Set tile
    setWindowTitle("Calibration Data Review");

    // Add optional page
    // if delta review started from homepage , first page should be result selection
    if (!isProjectDataDeltaReview()) {
      if (this.deltaReview && (getParentCDRResultId() == null)) {
        this.a2lSelWizPage = new FileSelectionWizardPage("CDR result Selection", this.deltaReview);
        addPage(this.a2lSelWizPage);
      }
      // If normal review is started from home page or application tool bar
      else if (!this.a2lFileAvailable) {
        this.a2lSelWizPage = new FileSelectionWizardPage("A2L file Selection", this.deltaReview);
        addPage(this.a2lSelWizPage);
      }
    }
    // Add page-1 for the wizard
    this.projectSelWizPage = new ProjectDataSelectionWizardPage(CAL_DATA_RVW_1_4);
    addPage(this.projectSelWizPage);
    // Add page-2 for the wizard
    this.wpSelWizPage = new WorkpackageSelectionWizardPage("Start Calibration Data Review 2/4");
    addPage(this.wpSelWizPage);

    this.ssdRuleSelPage = new SSDRuleSelectionPage("SSD Rule Selection page");
    addPage(this.ssdRuleSelPage);

    this.ruleSetSelPage =
        new RuleSetSltnPage("Select the Rule Set", null, null, new RuleSet(), new ArrayList<RuleSet>(), true);
    addPage(this.ruleSetSelPage);
    // Add page-3 for the wizard
    this.filesSelWizPage = new ReviewFilesSelectionWizardPage("Start Calibration Data Review 3/4");
    addPage(this.filesSelWizPage);

    // Add page-4 for the wizard
    this.prgressWizPage = new ReviewCalDataWizardPage("Start Calibration Data Review 4/4");
    addPage(this.prgressWizPage);

  }


  /**
   * {@inheritDoc} perform finish
   */
  @Override
  public boolean performFinish() {

    ReviewOutput reviewOutput = getCdrWizardUIModel().getReviewOutput();
    if (CommonUtils.isNotNull(reviewOutput)) {
      CdrActionSet actionSet = new CdrActionSet();
      // Trigger notification to review Participants
      Set<User> toUserList = this.prgressWizPage.getToUserList();
      if (this.prgressWizPage.getMailChkBox().getSelection() && CommonUtils.isNotEmpty(toUserList)) {
        actionSet.notifyRvwParticipants(toUserList, reviewOutput);
      }
      if (this.prgressWizPage.getEnableButton().getSelection()) {
        actionSet.openRvwRestEditorBasedOnObjInstance(reviewOutput);
      }
    }

    return true;
  }

  /**
   * Sends mail to hotline is mapping is missing
   */
  protected void sendMailToHotline() {

    MailHotline mailHotline;
    try {
      mailHotline = getHotlineNotifier();
      mailHotline.setSubject("#CLEARING-Mapping between iCDM attribute values and SSD values required");
      StringBuilder sbContent = new StringBuilder(200);
      sbContent.append(
          "<html> Hello Hotline, <br><br> The following attribute's/value's in iCDM are not mapped to SSD feature's/value's. Please do the needful. <br><br> ");
      Set<AttributeValueModel> attrWithoutMapping = getCdrWizardUIModel().getAttrWithoutMapping();
      for (AttributeValueModel attrVal : attrWithoutMapping) {
        // check the cases: 1. attribute is USED but value not set 2. attribute is NOT USED
        // in either of the above cases, no need to trigger an email to hotline since user action is sufficient
        if (attrVal.getValue().getId() < 0) {
          return;
        }
        sbContent.append("<b>Attribute name : </b>" + attrVal.getAttr().getName() + " <b>Attribute ID : </b>" +
            attrVal.getAttr().getId() + "<br> <b> Attribute value : </b>" + attrVal.getValue().getName() +
            " <b>Attribute value ID : </b>" + attrVal.getValue().getId() + "<br><br>");
      }

      sbContent.append(
          "<br><br> Thank you! <br><br><br> <font size=2>This is an auto-generated mail from iCDM client.</font><br><br></html>");

      mailHotline.setContent(sbContent.toString());
      mailHotline.sendMail();
    }
    catch (LdapException | ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * hotline Notifier
   */
  private MailHotline getHotlineNotifier() throws LdapException, ApicWebServiceException {
    String fromAddr =
        new LdapAuthenticationWrapper().getUserDetails(new CurrentUserBO().getUserName()).getEmailAddress();
    String status = null;
    String toAddr = null;
    try {
      // get the HOTLINE address f
      toAddr = new CommonDataBO().getParameterValue(CommonParamKey.ICDM_HOTLINE_TO);
      // get notification status
      status = new CommonDataBO().getParameterValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    if (ApicUtil.compare(status, ApicConstants.CODE_YES) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return new MailHotline(fromAddr, toAddr, true/* automatic notification enabled */);
    }
    // Set details and send mail
    return new MailHotline(fromAddr, toAddr, false/* automatic notification disabled */);
  }


  /**
   * @return get a2leditordata
   */
  public A2LEditorDataProvider getA2lEditorDP() {
    return this.a2lEditorDataProvider;
  }

  /**
   * @param a2lEditorDP set the a2leditordata
   */
  public void setA2lEditorDP(final A2LEditorDataProvider a2lEditorDP) {
    this.a2lEditorDataProvider = a2lEditorDP;

  }

  /**
   * @return true can finish
   */
  @Override
  public boolean canFinish() {
    // Validation to be performed after integration with SSD
    return this.prgressWizPage.isPageComplete() && !this.cdrWizardUIModel.isExceptioninWizard();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performCancel() {
    ReviewOutput reviewOutput = getCdrWizardUIModel().getReviewOutput();

    if (!this.prgressWizPage.isPageComplete() && this.a2lFileAvailable && isContentChanged()) {
      String dialogMessage = "Do you want to save filled information?";
      final StringBuilder progressMessage = new StringBuilder("Saving Inputs ...");
      if ((getCdrWizardUIModel().getAttrWithoutMapping() != null) &&
          !getCdrWizardUIModel().getAttrWithoutMapping().isEmpty()) {
        CalDataReviewWizard.this.sendMailToHotline();// trigger mail only when ssd feature value mapping not found
      }
      saveRvwdInfo(dialogMessage, progressMessage);

      return true;
    }
    else if (MessageDialogUtils.getConfirmMessageDialog("Cancel",
        "The data entered will be lost when the wizard is closed. Select OK to close.")) {
      if (reviewOutput == null) {
        return true;
      }
      try {
        new CDRHandler().deleteReviewResult(reviewOutput.getCdrResult());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      return true;
    }

    return false;
  }

  /**
   * @param dialogMessage
   * @param progressMessage
   */
  private void saveRvwdInfo(final String dialogMessage, final StringBuilder progressMessage) {
    if (!isProjectDataDeltaReview() && MessageDialogUtils.getConfirmMessageDialogWithYesNo("Save", dialogMessage)) {
      try {
        getContainer().run(true, false, (final IProgressMonitor monitor) -> {

          if (null == getCdrWizardUIModel().getPidcA2lId()) {
            MessageDialogUtils.getErrorMessageDialog("A2l File not selected",
                "Review information cannot be saved with No A2L file");
            return;
          }
          monitor.beginTask(progressMessage.toString(), 100);
          monitor.worked(50);
          Display.getDefault().syncExec(() -> {
            CalDataReviewWizard.this.projectSelWizPage.setDataForCancelPressed();
            CalDataReviewWizard.this.wpSelWizPage.setDataForCancelPressed();
            CalDataReviewWizard.this.ruleSetSelPage.setDataForCancelPressed();
            CalDataReviewWizard.this.ssdRuleSelPage.setDataForCancelPressed();
            CalDataReviewWizard.this.filesSelWizPage.setDataForCancelPressed();
          });
          try {
            ReviewVariantWrapper reviewVariantWrapper =
                new CDRHandler().saveCancelledResult(createReviewInputModel(getCdrWizardUIModel()));
            manipulatePreferenceStore(reviewVariantWrapper.getCdrReviewResult().getId());
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
        });
      }
      catch (InvocationTargetException | InterruptedException exp) {
        Thread.currentThread().interrupt();
        CDMLogger.getInstance().errorDialog(ERROR_MSG_SAVING_RESULTS, exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * This method is to get the WpResp for selection in Workpackage selection page
   *
   * @param pidcA2lId as selected Pidc A2l id
   * @param variantId as selected variant
   * @return map of wprespmodel and list of param ids
   */
  public Map<WpRespModel, List<Long>> getWorkPackageRespBasedOnPidcA2lIdAndVarId(final Long pidcA2lId,
      final Long variantId) {

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

  /**
   * This method fetches the location of cdfx from the wizard and store it into the eclipse preference store
   *
   * @param cdrResult to store the primary object id into preference
   */
  // ICDM-2355
  private void manipulatePreferenceStore(final Long rvwResultId) {
    final List<String> wizardList = new ArrayList<>();
    Display.getDefault().syncExec(() -> {
      TableItem[] items = CalDataReviewWizard.this.filesSelWizPage.getFilesList().getItems();
      List list = new ArrayList<>();
      for (TableItem item : items) {
        String filePath = item.getText();
        String fullPath = FilenameUtils.getFullPath(filePath);
        if (!CommonUtils.isEmptyString(fullPath) && !System.getProperty(TEMP_DIR).equals(fullPath)) {
          list.add(filePath);
        }
      }
      wizardList.addAll(list);
    });
    IPreferenceStore preference = getPreference();
    String cdrResultId = String.valueOf(rvwResultId);
    String filePathInLocalPreference;

    String[] wizardFilePathStringArray = wizardList.toArray(new String[] {});

    setPreferenceVal(preference, cdrResultId, wizardFilePathStringArray);

    // remove unwanted file path in the preference against the review result id
    filePathInLocalPreference = preference.getString(cdrResultId);
    if (!CommonUtils.isEmptyString(filePathInLocalPreference)) {
      String[] filePathPreferenceArray = filePathInLocalPreference.split(",");
      List<String> uiList = Arrays.asList(wizardFilePathStringArray);
      List<String> preferenceList = CommonUtils.getCustomArrayList(false, ",");
      for (String filePathPreference : filePathPreferenceArray) {
        if (uiList.contains(filePathPreference)) {
          preferenceList.add(filePathPreference);
        }
      }
      preference.setValue(cdrResultId,
          ((preferenceList == null) || preferenceList.isEmpty()) ? "" : preferenceList.toString());
    }
  }

  /**
   * @param preference
   * @param cdrResultId
   * @param wizardFilePathStringArray
   */
  private void setPreferenceVal(final IPreferenceStore preference, final String cdrResultId,
      final String[] wizardFilePathStringArray) {
    String filePathInLocalPreference;
    // setting in preference
    for (String filePath : wizardFilePathStringArray) {
      // if the filepath in the wizard in the new
      filePathInLocalPreference = preference.getString(cdrResultId);
      if (CommonUtils.isEmptyString(filePathInLocalPreference)) {
        // storing the filepath against the cdr-result-id for the first time
        preference.setValue(cdrResultId, filePath);
      }
      else {
        if (!filePathInLocalPreference.contains(filePath)) {
          StringBuilder filePathWithDelimiter = new StringBuilder();
          filePathWithDelimiter.append(filePathInLocalPreference).append(',').append(filePath);
          preference.setValue(cdrResultId, String.valueOf(filePathWithDelimiter));
        }
      }
    }
  }


  /**
   * @param isAvailable , set if a2l file is not selected
   */
  public void setA2lFileAvailable(final boolean isAvailable) {
    this.a2lFileAvailable = isAvailable;
  }


  /**
   * @return the contentChanged
   */
  public boolean isContentChanged() {
    return this.contentChanged || this.ruleSetSelPage.isContentChanged();
  }


  /**
   * @param contentChanged the contentChanged to set
   */
  public void setContentChanged(final boolean contentChanged) {
    this.contentChanged = contentChanged;
  }

  /**
   * @param filePath filePath
   */
  public void getFunLabFromParser(final String filePath) {
    final INPUT_FILE_TYPE inpFileType = getInputFileType(filePath);

    InputFileParser parser = new InputFileParser(ParserLogger.getInstance(), filePath);
    try {
      parser.parse();
    }
    catch (ParserException exp) {
      CDMLogger.getInstance().errorDialog("Parser Exception : " + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    Map<String, Characteristic> characteristicMap = getA2lEditiorDataHandler().getCharacteristicsMap();
    List<String> functionList = new ArrayList<>();
    List<String> paramList = new ArrayList<>();

    if (CommonUtils.isNotNull(parser)) {
      if (inpFileType == INPUT_FILE_TYPE.FUN) {
        functionList = parser.getFunctions();
      }
      else if (inpFileType == INPUT_FILE_TYPE.LAB) {
        paramList = parser.getLabels();

        // get labels from functions
        List<String> funcList = parser.getFunctions();
        List<String> paramListFromFunc = new ArrayList<>();
        if (CommonUtils.isNotEmpty(funcList)) {
          funcList.stream()
              .forEach(func -> paramListFromFunc.addAll(this.a2lEditiorDataHandler.getParamListfromFunction(func)));
          paramList.addAll(paramListFromFunc);
        }

        // get labels from groups
        List<String> grpList = parser.getGroups();
        if (CommonUtils.isNotEmpty(grpList)) {
          paramList.addAll(this.a2lEditiorDataHandler.getParamListFromGroups(grpList));
        }
      }
    }
    setFunFileFunctions(inpFileType, functionList);
    setLabFileFunctions(inpFileType, characteristicMap, paramList);
    this.wpSelWizPage.enableReviewFunctionLists();
  }

  /**
   * @param inpFileType
   * @param functionList
   */
  private void setFunFileFunctions(final INPUT_FILE_TYPE inpFileType, final List<String> functionList) {
    if ((inpFileType == INPUT_FILE_TYPE.FUN) && (this.wpSelWizPage.getReviewFuncsSet().isEmpty())) {
      getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.FUN_FILE.getDbType());
      this.wpSelWizPage.getWorkPackageSelectionWizardPageListener().addWPFuncName(functionList);
      getCdrWizardUIModel().getLabelList().clear();
    }
    else if ((inpFileType == INPUT_FILE_TYPE.FUN) && (!this.wpSelWizPage.getReviewFuncsSet().isEmpty())) {
      getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.FUN_FILE.getDbType());
      List<String> funcNames = new ArrayList<>();
      for (String function : functionList) {
        if (!this.wpSelWizPage.getReviewFuncsSet().contains(function)) {
          funcNames.add(function);
        }
      }
      this.wpSelWizPage.getWorkPackageSelectionWizardPageListener().addWPFuncName(funcNames);
    }
  }


  /**
   * @param inpFileType
   * @param characteristicMap
   * @param paramList
   */
  private void setLabFileFunctions(final INPUT_FILE_TYPE inpFileType,
      final Map<String, Characteristic> characteristicMap, final List<String> paramList) {
    if ((inpFileType == INPUT_FILE_TYPE.LAB) && (this.wpSelWizPage.getReviewFuncsSet().isEmpty())) {
      // ICDM-2026
      SortedSet<String> funcNames = new TreeSet<>();
      List<String> unassParamList = new ArrayList<>();

      addFuncNames(paramList, characteristicMap, funcNames, unassParamList);
      paramList.removeAll(unassParamList);

      List<String> wpFuncs = new ArrayList<>();
      wpFuncs.addAll(funcNames);
      if (!unassParamList.isEmpty()) {
        wpFuncs.add(ApicConstants.NOT_ASSIGNED);
        getCdrWizardUIModel().getUnassignedParamsInReview().addAll(unassParamList);
        getCdrWizardUIModel().getLabelList().clear();
      }
      else if (!paramList.isEmpty()) {
        getCdrWizardUIModel().setLabelList(paramList);
      }
      else if (paramList.isEmpty() && funcNames.isEmpty()) {
        CDMLogger.getInstance().infoDialog(
            "This is not a valid LAB-File. Parameters in LAB file are not found in A2L or the first line of a lab-file has just the text [LABEL]. In each following line one parameter name must appear",
            Activator.PLUGIN_ID);
      }
      this.wpSelWizPage.getWorkPackageSelectionWizardPageListener().addWPFuncName(wpFuncs);
      getCdrWizardUIModel().setLabelList(paramList);
      getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType());
    }
    else if ((inpFileType == INPUT_FILE_TYPE.LAB) && (!this.wpSelWizPage.getReviewFuncsSet().isEmpty())) {
      setLabelListFromLABFile(characteristicMap, paramList);
    }
  }

  /**
   * @param characteristicMap
   * @param paramList
   */
  private void setLabelListFromLABFile(final Map<String, Characteristic> characteristicMap,
      final List<String> paramList) {
    // ICDM-2026
    SortedSet<String> funcNames = new TreeSet<>();
    List<String> unassParamList = new ArrayList<>();
    addFuncNames(paramList, characteristicMap, funcNames, unassParamList);
    paramList.removeAll(unassParamList);

    List<String> funcNamesList = new ArrayList<>();
    for (String funcName : funcNames) {
      if (!this.wpSelWizPage.getReviewFuncsSet().contains(funcName)) {
        funcNamesList.add(funcName);
      }
    }
    this.wpSelWizPage.getWorkPackageSelectionWizardPageListener().addWPFuncName(funcNamesList);
    if (!unassParamList.isEmpty()) {
      getCdrWizardUIModel().getUnassignedParamsInReview().addAll(unassParamList);
      getCdrWizardUIModel().getLabelList().clear();
    }
    else if (!paramList.isEmpty()) {
      getCdrWizardUIModel().setLabelList(paramList);
    }
    getCdrWizardUIModel().setLabelList(paramList);
    getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType());
  }

  /**
   * @param paramList
   * @param characteristicMap
   * @param funcNames
   */
  private void addFuncNames(final List<String> paramList, final Map<String, Characteristic> characteristicMap,
      final SortedSet<String> funcNames, final List<String> unassParamList) {
    for (String paramName : paramList) {
      Characteristic charObj = characteristicMap.get(paramName);
      if (charObj != null) {
        Function funct = charObj.getDefFunction();
        if (funct != null) {
          funcNames.add(funct.getName());
        }
        else {
          unassParamList.add(paramName);
        }
      }
    }
  }


  /**
   * Method to convert the cdrWizardModel to ReviewInput for CDR review
   *
   * @param cdrWizardUIMod input data
   * @return reviewInput model
   */
  public ReviewInput createReviewInputModel(final CDRWizardUIModel cdrWizardUIMod) {

    ReviewInput reviewInput = new ReviewInput();
    // Setting Pidc Data from A2l File selection
    PidcData pidcData = new PidcData();
    UserData userData = new UserData();
    FileData fileData = new FileData();
    RulesData rulesData = new RulesData();
    ResultData resultData = new ResultData();

    if (CDRConstants.REVIEW_STATUS.OPEN.getUiType().equals(cdrWizardUIMod.getReviewStatus())) {
      resultData.setCanceledResultId(this.cdrWizardUIModel.getCancelledResultId());

      if (((null != this.cdrWizardUIModel.getDeltaReviewType()) &&
          (DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType().equals(this.cdrWizardUIModel.getDeltaReviewType()))) ||
          DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType().equals(this.cdrWizardUIModel.getDeltaReviewType())) {
        reviewInput.setDeltaReviewType(this.cdrWizardUIModel.getDeltaReviewType());
        reviewInput.setDeltaReview(true);
        if (this.cdrWizardUIModel.getParentResultId() != null) {
          resultData.setParentResultId(this.cdrWizardUIModel.getParentResultId());
        }
      }
    }
    else if (isProjectDataDeltaReview()) {
      setPrjctDeltaRvwDetails(cdrWizardUIMod, reviewInput, pidcData);
    }
    else if (isDeltaReview()) {
      setDeltaRvwDetails(reviewInput, resultData);
    }


    if (cdrWizardUIMod.getPidcA2lId() != null) {
      pidcData.setPidcA2lId(cdrWizardUIMod.getPidcA2lId());
    }
    if (cdrWizardUIMod.getA2lFileId() != null) {
      pidcData.setA2lFileId(cdrWizardUIMod.getA2lFileId());
    }


    // Setting ProjectData Selection Page
    setPrjDataSelPageData(cdrWizardUIMod, reviewInput, pidcData, userData);


    // Setting WorkPackage File selection Page
    setWpFileSelPageData(cdrWizardUIMod, reviewInput, fileData, rulesData);

    // Setting RuleSetSln Page Data
    setRuleSetPageData(cdrWizardUIMod, rulesData);

    // Setting SSD Rule File Selection Page
    setSSDRuleFileSelPageData(cdrWizardUIMod, rulesData);

    // Setting Review File Selection Page Data
    setReviwFileSelPageData(cdrWizardUIMod, reviewInput, fileData);

    // Setting Reslut Data
    settingResultData(cdrWizardUIMod, reviewInput, pidcData, userData, fileData, rulesData, resultData);

    return reviewInput;
  }

  /**
   * @param cdrWizardUIMod
   * @param reviewInput
   * @param pidcData
   */
  private void setPrjctDeltaRvwDetails(final CDRWizardUIModel cdrWizardUIMod, final ReviewInput reviewInput,
      final PidcData pidcData) {
    if (cdrWizardUIMod.getSourcePidcVerId() != null) {
      pidcData.setSourcePidcVerId(cdrWizardUIMod.getSourcePidcVerId());
    }
    if (cdrWizardUIMod.getSourcePIDCVariantId() != null) {
      pidcData.setSourcePIDCVariantId(cdrWizardUIMod.getSourcePIDCVariantId());
    }
    reviewInput.setDeltaReview(true);
    reviewInput.setDeltaReviewType(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType());
  }

  /**
   * @param cdrWizardUIMod
   * @param rulesData
   */
  private void setRuleSetPageData(final CDRWizardUIModel cdrWizardUIMod, final RulesData rulesData) {
    if (cdrWizardUIMod.getPrimaryRuleSetId() != null) {
      rulesData.setPrimaryRuleSetId(cdrWizardUIMod.getPrimaryRuleSetId());
    }
  }

  /**
   * @param reviewInput
   * @param resultData
   */
  private void setDeltaRvwDetails(final ReviewInput reviewInput, final ResultData resultData) {
    if (this.cdrWizardUIModel.getParentResultId() != null) {
      resultData.setParentResultId(this.cdrWizardUIModel.getParentResultId());
    }
    reviewInput.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
    reviewInput.setDeltaReview(true);
  }

  /**
   * @param cdrWizardUIMod
   * @param rulesData
   */
  private void setSSDRuleFileSelPageData(final CDRWizardUIModel cdrWizardUIMod, final RulesData rulesData) {
    if (cdrWizardUIMod.getSsdReleaseId() != null) {
      rulesData.setSsdReleaseId(cdrWizardUIMod.getSsdReleaseId());
    }
    if (cdrWizardUIMod.getSsdRuleFilePath() != null) {
      rulesData.setSsdRuleFilePath(cdrWizardUIMod.getSsdRuleFilePath());

    }
  }

  /**
   * @param cdrWizardUIMod
   * @param reviewInput
   * @param pidcData
   * @param userData
   */
  private void setPrjDataSelPageData(final CDRWizardUIModel cdrWizardUIMod, final ReviewInput reviewInput,
      final PidcData pidcData, final UserData userData) {
    if (cdrWizardUIMod.getReviewType() != null) {
      reviewInput.setReviewType(cdrWizardUIMod.getReviewType());
    }
    if (cdrWizardUIMod.getA2lWpDefVersId() != null) {
      reviewInput.setA2lWpDefVersId(cdrWizardUIMod.getA2lWpDefVersId());
    }
    if (cdrWizardUIMod.getDescription() != null) {
      reviewInput.setDescription(cdrWizardUIMod.getDescription());
    }
    if (cdrWizardUIMod.getSelectedPidcVariantId() != null) {
      pidcData.setSelPIDCVariantId(cdrWizardUIMod.getSelectedPidcVariantId());
    }
    if (cdrWizardUIMod.getSelAuditorId() != null) {
      userData.setSelAuditorId(cdrWizardUIMod.getSelAuditorId());
    }
    if (cdrWizardUIMod.getSelCalEngineerId() != null) {
      userData.setSelCalEngineerId(cdrWizardUIMod.getSelCalEngineerId());
    }
    if (cdrWizardUIMod.getSelParticipantsIds() != null) {
      userData.setSelParticipantsIds(cdrWizardUIMod.getSelParticipantsIds());
    }
  }

  /**
   * @param cdrWizardUIMod
   * @param reviewInput
   * @param fileData
   * @param rulesData
   */
  private void setWpFileSelPageData(final CDRWizardUIModel cdrWizardUIMod, final ReviewInput reviewInput,
      final FileData fileData, final RulesData rulesData) {
    if (cdrWizardUIMod.getA2lGroupName() != null) {
      reviewInput.setA2lGroupName(cdrWizardUIMod.getA2lGroupName());
    }
    if ((cdrWizardUIMod.getA2lGroupNameList() != null) && !cdrWizardUIMod.getA2lGroupNameList().isEmpty()) {
      reviewInput.setA2lGroupNameList(cdrWizardUIMod.getA2lGroupNameList());
    }
    if (cdrWizardUIMod.getFunLabFilePath() != null) {
      fileData.setFunLabFilePath(cdrWizardUIMod.getFunLabFilePath());
    }
    setRuleSetPageData(cdrWizardUIMod, rulesData);
    if ((cdrWizardUIMod.getSecondaryRuleSetIds() != null) && !cdrWizardUIMod.getSecondaryRuleSetIds().isEmpty()) {
      rulesData.setSecondaryRuleSetIds(cdrWizardUIMod.getSecondaryRuleSetIds());
    }
    if (cdrWizardUIMod.getSourceType() != null) {
      reviewInput.setSourceType(cdrWizardUIMod.getSourceType());
    }
    if (cdrWizardUIMod.isCommonRulesPrimary()) {
      rulesData.setCommonRulesPrimary(cdrWizardUIMod.isCommonRulesPrimary());
    }
    if ((cdrWizardUIMod.getSelReviewFuncs() != null) && !cdrWizardUIMod.getSelReviewFuncs().isEmpty() &&
        !cdrWizardUIMod.getSourceType().equals(CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType()) &&
        !cdrWizardUIMod.getSourceType().equals(CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType())) {
      reviewInput.setSelReviewFuncs(cdrWizardUIMod.getSelReviewFuncs());
    }

    String obdFlag = cdrWizardUIMod.getObdFlag();
    if (CommonUtils.isNotNull(obdFlag)) {
      reviewInput.setObdFlag(obdFlag);
    }
  }

  /**
   * @param cdrWizardUIMod
   * @param reviewInput
   * @param fileData
   */
  private void setReviwFileSelPageData(final CDRWizardUIModel cdrWizardUIMod, final ReviewInput reviewInput,
      final FileData fileData) {
    if (cdrWizardUIMod.getSelFilesPath() != null) {
      fileData.setSelFilesPath(cdrWizardUIMod.getSelFilesPath());
    }
    if (cdrWizardUIMod.isFilesToBeReviewed()) {
      reviewInput.setFilesToBeReviewed(cdrWizardUIMod.isFilesToBeReviewed());
    }
  }

  /**
   * @param cdrWizardUIMod
   * @param reviewInput
   * @param pidcData
   * @param userData
   * @param fileData
   * @param rulesData
   * @param resultData
   */
  private void settingResultData(final CDRWizardUIModel cdrWizardUIMod, final ReviewInput reviewInput,
      final PidcData pidcData, final UserData userData, final FileData fileData, final RulesData rulesData,
      final ResultData resultData) {
    if (cdrWizardUIMod.isStartReviewType()) {
      resultData.setStartReviewType(cdrWizardUIMod.isStartReviewType());
    }
    if (cdrWizardUIMod.isOffReviewType()) {
      resultData.setOffReviewType(cdrWizardUIMod.isOffReviewType());
    }
    if (cdrWizardUIMod.isOnlyLockedOffReview()) {
      resultData.setOnlyLockedOffReview(cdrWizardUIMod.isOnlyLockedOffReview());
    }
    if (cdrWizardUIMod.isOnlyLockedStartResults()) {
      resultData.setOnlyLockedStartResults(cdrWizardUIMod.isOnlyLockedStartResults());
    }
    if (cdrWizardUIMod.isCommonRulesSecondary()) {
      rulesData.setCommonRulesSecondary(cdrWizardUIMod.isCommonRulesSecondary());
    }
    if (cdrWizardUIMod.getWpDivId() != null) {
      reviewInput.setWpDivId(cdrWizardUIMod.getWpDivId());
    }
    if (cdrWizardUIMod.getReviewVersion() != null) {
      reviewInput.setReviewVersion(cdrWizardUIMod.getReviewVersion());
    }
    if (cdrWizardUIMod.getCdrReviewResult() != null) {
      reviewInput.setCdrReviewResult(cdrWizardUIMod.getCdrReviewResult());
    }
    if (cdrWizardUIMod.getRvwVariant() != null) {
      reviewInput.setRvwVariant(cdrWizardUIMod.getRvwVariant());
    }
    if (cdrWizardUIMod.getRvwWpAndRespModelSet() != null) {
      reviewInput.setRvwWpAndRespModelSet(cdrWizardUIMod.getRvwWpAndRespModelSet());
    }
    if (cdrWizardUIMod.getSelectedWpRespList() != null) {
      reviewInput.setSelectedWpRespList(cdrWizardUIMod.getSelectedWpRespList());
    }
    if (cdrWizardUIMod.getFunctionMap() != null) {
      reviewInput.setFunctionMap(cdrWizardUIMod.getFunctionMap());
    }
    if (cdrWizardUIMod.getWpRespName() != null) {
      reviewInput.setWpRespName(cdrWizardUIMod.getWpRespName());
    }
    reviewInput.setPidcData(pidcData);
    reviewInput.setUserData(userData);
    reviewInput.setFileData(fileData);
    reviewInput.setRulesData(rulesData);
    reviewInput.setResultData(resultData);
  }


  /**
   * @param fileName
   */
  private INPUT_FILE_TYPE getInputFileType(final String fileName) {
    if (fileName.endsWith(".fun") || fileName.endsWith(".FUN")) {
      return INPUT_FILE_TYPE.FUN;
    }
    return INPUT_FILE_TYPE.LAB;
  }


  /**
   * @param pidcVer
   * @param pidcVar
   */
  public void fillProjectDataDeltaaReviewWizardData(final PidcVariant pidcVar, final PidcVersion pidcVer) {
    setProjDataProjectPageData(pidcVar, pidcVer);
  }


  /**
   * @param pidcVer
   * @param pidcVar
   */
  private void setProjDataProjectPageData(final PidcVariant pidcVar, final PidcVersion pidcVer) {
    if (pidcVar != null) {
      this.cdrWizardUIModel.setSourcePIDCVariantId(pidcVar.getId());
      this.cdrWizardUIModel.setSelectedPidcVariantId(pidcVar.getId());
      this.cdrWizardUIModel.setSelectedPidcVariantName(pidcVar.getName());
    }
    if (pidcVer != null) {
      this.cdrWizardUIModel.setSourcePidcVerId(pidcVer.getId());
    }
  }


  /**
   * @return the preference
   */
  // ICDM-2355
  private IPreferenceStore getPreference() {
    return this.prefStore;
  }

  /**
   * @return the a2lEditiorDataHandler
   */
  public A2LFileInfoBO getA2lEditiorDataHandler() {
    return this.a2lEditiorDataHandler;
  }


  /**
   * @param a2lEditiorDataHandler the a2lEditiorDataHandler to set
   */
  public void setA2lEditiorDataHandler(final A2LFileInfoBO a2lEditiorDataHandler) {
    this.a2lEditiorDataHandler = a2lEditiorDataHandler;
  }


  /**
   * @return the a2lFile
   */
  public PidcA2LBO getPidcA2LBO() {
    return this.pidcA2LBO;
  }


  /**
   * @param a2lFile the a2lFile to set
   */
  public void setPidcA2LBO(final PidcA2LBO pidcA2LBO) {
    this.pidcA2LBO = pidcA2LBO;
  }


  /**
   * @return the ssdRuleSelPage
   */
  public SSDRuleSelectionPage getSsdRuleSelPage() {
    return this.ssdRuleSelPage;
  }


  /**
   * @param ssdRuleSelPage the ssdRuleSelPage to set
   */
  public void setSsdRuleSelPage(final SSDRuleSelectionPage ssdRuleSelPage) {
    this.ssdRuleSelPage = ssdRuleSelPage;
  }


  /**
   * @return the prgressWizPage
   */
  public ReviewCalDataWizardPage getPrgressWizPage() {
    return this.prgressWizPage;
  }


  /**
   * @param prgressWizPage the prgressWizPage to set
   */
  public void setPrgressWizPage(final ReviewCalDataWizardPage prgressWizPage) {
    this.prgressWizPage = prgressWizPage;
  }


  /**
   * @return the a2lEditorDataProvider
   */
  public A2LEditorDataProvider getA2lEditorDataProvider() {
    return this.a2lEditorDataProvider;
  }


  /**
   * @param a2lEditorDataProvider the a2lEditorDataProvider to set
   */
  public void setA2lEditorDataProvider(final A2LEditorDataProvider a2lEditorDataProvider) {
    this.a2lEditorDataProvider = a2lEditorDataProvider;
  }


  /**
   * @return the calDataRvw14
   */
  public static String getCalDataRvw14() {
    return CAL_DATA_RVW_1_4;
  }


  /**
   * @return the errorMsgSavingResults
   */
  public static String getErrorMsgSavingResults() {
    return ERROR_MSG_SAVING_RESULTS;
  }


  /**
   * @return the tempDir
   */
  public static String getTempDir() {
    return TEMP_DIR;
  }


  /**
   * @return the prefStore
   */
  public IPreferenceStore getPrefStore() {
    return this.prefStore;
  }

  /**
   * @return the a2lFileAvailable
   */
  public boolean isA2lFileAvailable() {
    return this.a2lFileAvailable;
  }


  /**
   * @return the deltaReviewType
   */
  public DELTA_REVIEW_TYPE getDeltaReviewType() {
    return this.deltaReviewType;
  }


  /**
   * @param a2lSelWizPage the a2lSelWizPage to set
   */
  public void setA2lSelWizPage(final FileSelectionWizardPage a2lSelWizPage) {
    this.a2lSelWizPage = a2lSelWizPage;
  }


  /**
   * @param projectSelWizPage the projectSelWizPage to set
   */
  public void setProjectSelWizPage(final ProjectDataSelectionWizardPage projectSelWizPage) {
    this.projectSelWizPage = projectSelWizPage;
  }


  /**
   * @param wpSelWizPage the wpSelWizPage to set
   */
  public void setWpSelWizPage(final WorkpackageSelectionWizardPage wpSelWizPage) {
    this.wpSelWizPage = wpSelWizPage;
  }


  /**
   * @param filesSelWizPage the filesSelWizPage to set
   */
  public void setFilesSelWizPage(final ReviewFilesSelectionWizardPage filesSelWizPage) {
    this.filesSelWizPage = filesSelWizPage;
  }


  /**
   * @return the pidcTreeNodeHandler
   */
  public PidcTreeNodeHandler getPidcTreeNodeHandler() {
    return this.pidcTreeNodeHandler;
  }


  /**
   * @param pidcTreeNodeHandler the pidcTreeNodeHandler to set
   */
  public void setPidcTreeNodeHandler(final PidcTreeNodeHandler pidcTreeNodeHandler) {
    this.pidcTreeNodeHandler = pidcTreeNodeHandler;
  }


  /**
   * @return the pidcVersionAttributeMap
   */
  public Map<Long, PidcVersionAttribute> getPidcVersionAttributeMap() {
    return this.pidcVersionAttributeMap;
  }


  /**
   * @param pidcVersionAttributeMap the pidcVersionAttributeMap to set
   */
  public void setPidcVersionAttributeMap(final Map<Long, PidcVersionAttribute> pidcVersionAttributeMap) {
    this.pidcVersionAttributeMap = pidcVersionAttributeMap;
  }


  /**
   * @return the allRuleSet
   */
  public SortedSet<RuleSet> getAllRuleSet() {
    return this.allRuleSet;
  }


  /**
   * @param allRuleSet the allRuleSet to set
   */
  public void setAllRuleSet(final SortedSet<RuleSet> allRuleSet) {
    this.allRuleSet = allRuleSet;
  }


  /**
   * @return the errorOccuredinReview
   */
  public boolean isErrorOccuredinReview() {
    return this.errorOccuredinReview;
  }


  /**
   * @param errorOccuredinReview the errorOccuredinReview to set
   */
  public void setErrorOccuredinReview(final boolean errorOccuredinReview) {
    this.errorOccuredinReview = errorOccuredinReview;
  }


  /**
   * @return the workPackageRespMap
   */
  public Map<WpRespModel, List<Long>> getWorkPackageRespMap() {
    if (((this.workPackageRespMap == null) || this.workPackageRespMap.isEmpty()) &&
        (getCdrWizardUIModel().getPidcA2lId() != null)) {
      setWorkPackageRespMap(getWorkPackageRespBasedOnPidcA2lIdAndVarId(getCdrWizardUIModel().getPidcA2lId(),
          getCdrWizardUIModel().getSelectedPidcVariantId()));
    }
    return this.workPackageRespMap;
  }


  /**
   * @param workPackageRespMap the workPackageRespMap to set
   */
  public void setWorkPackageRespMap(final Map<WpRespModel, List<Long>> workPackageRespMap) {
    this.workPackageRespMap = workPackageRespMap;
  }


  /**
   * @return the isA2lFileChanged
   */
  public boolean isA2lFileChanged() {
    return this.isA2lFileChanged;
  }


  /**
   * @param isA2lFileChanged the isA2lFileChanged to set
   */
  public void setA2lFileChanged(final boolean isA2lFileChanged) {
    this.isA2lFileChanged = isA2lFileChanged;
  }

}
