/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPDefBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CalendarDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author dja7cob
 */
public class EditFC2WPMappingDialog extends AbstractDialog {


  private static final String SELECT = "Select";

  private static final String SELECT_USER = "Select User";

  private static final int OK_PRESSED = 0;
  /**
   * AddValidityAttrValDialog Title
   */
  private static final String DIALOG_TITLE = "Assignment FC-WP";
  /**
   * the maximum length for the text field
   */
  private static final int MAX_TEXT_BOX_SIZE = 4000;

  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Button instance
   */
  private Button saveBtn;
  /**
   * Section instance
   */
  private Section fc2wpSection;
  /**
   * Section instance
   */
  private Section cocSection;

  private Section contactsSection;
  /**
   * Form form
   */
  private Form fc2WpForm;

  /**
   * Form form
   */
  private Form cocForm;

  /**
   * Form form
   */
  private Form contactsForm;
  private Text funcText;
  private Text wpText;
  private Text agreedOnText;

  private Text agreementCocRespText;
  private Text firstContactText;
  private Text secondContactText;
  private Section infoSection;
  private Form infoForm;
  private Text commentsText;
  private Text fc2WpInfoText;
  private Date agreeWithCocDate;

  private Long wpDivIdToSave;
  private final Set<Function> newFuncDetailsSet = new HashSet<>();
  private Long firstContactIdToSave;
  private Long secondContctIdToSave;
  private Long agreeWithCocRespUserIdToSave;
  private List<FC2WPMapping> fc2wpList = new ArrayList<>();
  private FC2WPMapping selfc2wpMapping;
  private final FC2WPDef fc2wpDef;
  private final FC2WPMappingWithDetails fc2wpVersObj;
  private final FC2WPDefBO fc2wpDefBO;
  private Button useWpDefcheckBox;
  private Button isAgreeWithCoCCheckBox;
  private Button isDeletedcheckBox;
  private boolean sameWorkPkg;
  private boolean isMultipleUpdate;
  boolean sameCoC;
  boolean sameAgreedOn;
  boolean sameResponsible;
  private Button useCurrCoc;
  private boolean sameComment;
  private boolean sameFc2WpInfo;
  private boolean sameWPDef;
  private Button useCurrWpDef;
  private boolean sameFirstContact;
  private boolean sameSecondContact;
  private Button firstContactSearch;
  private Button delfirstContact;
  private Button delSecondContact;
  private Button secondContactSearch;
  private Button delWp;
  private boolean sameDeleted;
  private Button useCurrDeleted;
  private Button delAgreedOn;
  private Button delRespAgreement;
  private boolean isAddFc2Wp;
  private Button delFunction;
  private Button wpSearch;
  private Button calendar;
  private Button respSearch;

  /**
   * @return the funcText
   */
  public Text getFuncText() {
    return this.funcText;
  }


  /**
   * @param funcText the funcText to set
   */
  public void setFuncText(final Text funcText) {
    this.funcText = funcText;
  }

  /**
   * @return the wpDivIdToSave
   */
  public Long getWpDivIdToSave() {
    return this.wpDivIdToSave;
  }

  /**
   * @return the fc2wpVersObj
   */
  public FC2WPMappingWithDetails getFc2wpVersObj() {
    return this.fc2wpVersObj;
  }


  /**
   * @return the isMultipleUpdate
   */
  public boolean isMultipleUpdate() {
    return this.isMultipleUpdate;
  }

  /**
   * @return the newFuncDetailsSet
   */
  public Set<Function> getNewFuncDetailsSet() {
    return this.newFuncDetailsSet;
  }


  /**
   * Constructor
   *
   * @param parentShell the parent shell
   * @param list the fc2wp mapping
   * @param selFc2wpMapDetail selected Fc2WpMapDetail
   * @param selFc2wpDefBo selected Fc2WPDefBO
   * @param selFc2wpDef selected FC2WP Def
   */
  public EditFC2WPMappingDialog(final Shell parentShell, final List<FC2WPMapping> list, final FC2WPDefBO selFc2wpDefBo,
      final FC2WPMappingWithDetails selFc2wpMapDetail, final FC2WPDef selFc2wpDef) {
    super(parentShell);
    this.fc2wpList = list;
    this.selfc2wpMapping = list.get(0);
    this.fc2wpDef = selFc2wpDef;
    this.fc2wpVersObj = selFc2wpMapDetail;
    this.fc2wpDefBO = selFc2wpDefBo;
    if (this.fc2wpList.size() > 1) {
      setDiffValues();
      this.isMultipleUpdate = true;
    }
  }

  /**
   *
   */
  private void setDiffValues() {
    boolean sameWPFlag = true;
    boolean sameCoCFlag = true;
    boolean sameAgreedOnFlag = true;
    boolean sameResponsibleFlag = true;
    boolean sameCommentFlag = true;
    boolean sameWPDefFlag = true;
    boolean sameFirstContactFlag = true;
    boolean sameSecondContactFlag = true;
    boolean sameDeletedFlag = true;
    boolean sameFc2WpInfoFlag = true;

    FC2WPMapping fc2wp = this.fc2wpList.get(0);

    Long firstContact1 = null;
    Long secondContact1 = null;
    if (fc2wp.isUseWpDef() &&
        (null != EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()))) {

      firstContact1 =
          EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()).getContactPersonId();
      secondContact1 =
          EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()).getContactPersonSecondId();
    }
    else {
      firstContact1 = fc2wp.getContactPersonId();
      secondContact1 = fc2wp.getContactPersonSecondId();
    }

    // Iterate through the rules
    for (FC2WPMapping selFc2wp : this.fc2wpList) {
      sameWPFlag = checkWpFlag(sameWPFlag, selFc2wp, fc2wp);
      sameAgreedOnFlag = checkAgreedOnFlag(sameAgreedOnFlag, selFc2wp, fc2wp);
      sameCoCFlag = checkCoCFlag(sameCoCFlag, selFc2wp, fc2wp);
      sameResponsibleFlag = checkRespFlag(sameResponsibleFlag, selFc2wp, fc2wp);
      sameCommentFlag = checkCommentFlag(sameCommentFlag, selFc2wp, fc2wp);
      sameFc2WpInfoFlag = checkFc2WpInfoFlag(sameFc2WpInfoFlag, selFc2wp, fc2wp);
      sameWPDefFlag = checkWpDefFlag(sameWPDefFlag, selFc2wp, fc2wp);
      sameFirstContactFlag = checkFirstContactFlag(sameFirstContactFlag, fc2wp, firstContact1, selFc2wp);
      sameSecondContactFlag = checkSecContactFlag(sameSecondContactFlag, fc2wp, secondContact1, selFc2wp);
      sameDeletedFlag = checkDeletedFlag(sameDeletedFlag, fc2wp, selFc2wp);
    }

    // Set all rule details to wizard data
    this.sameWorkPkg = sameWPFlag;
    this.sameCoC = sameCoCFlag;
    this.sameAgreedOn = sameAgreedOnFlag;
    this.sameResponsible = sameResponsibleFlag;
    this.sameComment = sameCommentFlag;
    this.sameWPDef = sameWPDefFlag;
    this.sameFirstContact = sameFirstContactFlag;
    this.sameSecondContact = sameSecondContactFlag;
    this.sameDeleted = sameDeletedFlag;
    this.sameFc2WpInfo = sameFc2WpInfoFlag;
  }


  /**
   * @param sameWPFlag
   * @param sameWPFlag
   * @param fc2wp
   * @param selFc2wp
   * @param wpName1
   * @return
   */
  private boolean checkWpFlag(final boolean sameWPFlag, final FC2WPMapping selFc2wp, final FC2WPMapping fc2wp) {
    if (sameWPFlag) {
      String wpName1 = null;
      String wpName2 = null;
      if ((null != this.fc2wpVersObj.getWpDetMap()) &&
          (null != this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()))) {
        wpName1 = this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()).getWpName();
      }
      if ((null != this.fc2wpVersObj.getWpDetMap()) &&
          (null != this.fc2wpVersObj.getWpDetMap().get(selFc2wp.getWpDivId()))) {
        wpName2 = this.fc2wpVersObj.getWpDetMap().get(selFc2wp.getWpDivId()).getWpName();
      }
      if (!CommonUtils.isEqual(wpName1, wpName2)) {
        return false;
      }
    }
    return sameWPFlag;
  }


  /**
   * @param sameCoCFlag
   * @param selFc2wp
   * @param fc2wp
   * @return
   */
  private boolean checkCoCFlag(final boolean sameCoCFlag, final FC2WPMapping selFc2wp, final FC2WPMapping fc2wp) {
    if (sameCoCFlag && !CommonUtils.isEqual(fc2wp.isAgreeWithCoc(), selFc2wp.isAgreeWithCoc())) {
      return false;
    }
    return sameCoCFlag;
  }


  /**
   * @param sameAgreedOnFlag
   * @param sameAgreedOnFlag
   * @param reportDate1
   * @param selFc2wp
   * @param fc2wp
   * @return
   */
  private boolean checkAgreedOnFlag(final boolean sameAgreedOnFlag, final FC2WPMapping selFc2wp,
      final FC2WPMapping fc2wp) {
    SimpleDateFormat df = new SimpleDateFormat(DateFormat.DATE_FORMAT_09, Locale.getDefault(Locale.Category.FORMAT));
    String reportDate1 = null;
    String reportDate2 = null;

    if (null != fc2wp.getAgreeWithCocDate()) {
      reportDate1 = df.format(fc2wp.getAgreeWithCocDate());
    }
    if (null != selFc2wp.getAgreeWithCocDate()) {
      reportDate2 = df.format(selFc2wp.getAgreeWithCocDate());
    }
    if (sameAgreedOnFlag && !CommonUtils.isEqual(reportDate1, reportDate2)) {
      return false;
    }
    return sameAgreedOnFlag;
  }


  /**
   * @param sameResponsibleFlag
   * @param selFc2wp
   * @param fc2wp
   * @return
   */
  private boolean checkRespFlag(final boolean sameResponsibleFlag, final FC2WPMapping selFc2wp,
      final FC2WPMapping fc2wp) {
    if (sameResponsibleFlag &&
        !CommonUtils.isEqual(fc2wp.getAgreeWithCocRespUserId(), selFc2wp.getAgreeWithCocRespUserId())) {
      return false;
    }
    return sameResponsibleFlag;
  }


  /**
   * @param sameCommentFlag
   * @param selFc2wp
   * @param fc2wp
   * @return
   */
  private boolean checkCommentFlag(final boolean sameCommentFlag, final FC2WPMapping selFc2wp,
      final FC2WPMapping fc2wp) {
    if (sameCommentFlag && !CommonUtils.isEqual(fc2wp.getComments(), selFc2wp.getComments())) {
      return false;
    }

    return sameCommentFlag;
  }


  /**
   * @param sameFc2WpInfoFlag
   * @param selFc2wp
   * @param fc2wp
   * @return
   */
  private boolean checkFc2WpInfoFlag(final boolean sameFc2WpInfoFlag, final FC2WPMapping selFc2wp,
      final FC2WPMapping fc2wp) {
    if (sameFc2WpInfoFlag && !CommonUtils.isEqual(fc2wp.getFc2wpInfo(), selFc2wp.getFc2wpInfo())) {
      return false;
    }
    return sameFc2WpInfoFlag;
  }


  /**
   * @param sameFirstContactFlag
   * @param fc2wp
   * @param firstContact1
   * @param selFc2wp
   * @return
   */
  private boolean checkFirstContactFlag(final boolean sameFirstContactFlag, final FC2WPMapping fc2wp,
      final Long firstContact1, final FC2WPMapping selFc2wp) {
    Long firstContact2 = null;
    if (selFc2wp.isUseWpDef() && (null != selFc2wp.getWpDivId()) &&
        (null != EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()))) {
      firstContact2 =
          EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(selFc2wp.getWpDivId()).getContactPersonId();
    }
    else {
      firstContact2 = selFc2wp.getContactPersonId();
    }
    if (sameFirstContactFlag && !CommonUtils.isEqual(firstContact1, firstContact2)) {
      return false;
    }
    return sameFirstContactFlag;
  }


  /**
   * @param sameSecondContactFlag
   * @param fc2wp
   * @param secondContact1
   * @param selFc2wp
   * @return
   */
  private boolean checkSecContactFlag(final boolean sameSecondContactFlag, final FC2WPMapping fc2wp,
      final Long secondContact1, final FC2WPMapping selFc2wp) {
    Long secondContact2 = null;
    if (selFc2wp.isUseWpDef() && (null != selFc2wp.getWpDivId()) &&
        (null != EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(fc2wp.getWpDivId()))) {
      secondContact2 =
          EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(selFc2wp.getWpDivId()).getContactPersonSecondId();
    }
    else {
      secondContact2 = selFc2wp.getContactPersonSecondId();
    }
    if (sameSecondContactFlag && !CommonUtils.isEqual(secondContact1, secondContact2)) {
      return false;
    }
    return sameSecondContactFlag;
  }


  /**
   * @param sameWPDefFlag
   * @param selFc2wp
   * @param fc2wp
   * @return
   */
  private boolean checkWpDefFlag(final boolean sameWPDefFlag, final FC2WPMapping selFc2wp, final FC2WPMapping fc2wp) {
    if (sameWPDefFlag && !CommonUtils.isEqual(fc2wp.isUseWpDef(), selFc2wp.isUseWpDef())) {
      return false;
    }
    return sameWPDefFlag;
  }


  /**
   * @param sameDeletedFlag
   * @param fc2wp
   * @param selFc2wp
   * @return
   */
  private boolean checkDeletedFlag(final boolean sameDeletedFlag, final FC2WPMapping fc2wp,
      final FC2WPMapping selFc2wp) {
    if (sameDeletedFlag && !CommonUtils.isEqual(fc2wp.isDeleted(), selFc2wp.isDeleted())) {
      return false;
    }
    return sameDeletedFlag;
  }


  /**
   * @param parentShell parent shell
   * @param selFc2wpDefBo selected Fc2wpDefBo
   * @param selFc2wpMapDetail selected Fc2wp Map Detail
   * @param selFc2wpDef selected Fc2wp Def
   * @param isAddFc2Wp flag to check whether it is add or delete operation
   */
  public EditFC2WPMappingDialog(final Shell parentShell, final FC2WPDefBO selFc2wpDefBo,
      final FC2WPMappingWithDetails selFc2wpMapDetail, final FC2WPDef selFc2wpDef, final boolean isAddFc2Wp) {
    super(parentShell);
    this.isAddFc2Wp = isAddFc2Wp;
    this.fc2wpDef = selFc2wpDef;
    this.fc2wpVersObj = selFc2wpMapDetail;
    this.fc2wpDefBO = selFc2wpDefBo;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);

    // Set the title
    if (this.isAddFc2Wp) {
      setTitle("Add new FC-WP");
    }
    else {
      setTitle("Edit entry for  FC-WP");
    }
    // Set the message
    String message;
    StringBuilder dialogMsg = new StringBuilder();
    dialogMsg.append("Please select the Function/Workpackage combination");
    message = dialogMsg.toString();
    if (this.isMultipleUpdate) {
      dialogMsg.append("\n").append("Function names : ");
      for (FC2WPMapping fc2wp : this.fc2wpList) {
        dialogMsg.append(fc2wp.getFunctionName()).append(",");
      }
      message = dialogMsg.substring(0, dialogMsg.length() - 1);
    }
    setMessage(message);
    return contents;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    super.configureShell(newShell);
  }


  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "OK", true);
    // ICDM-112
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    return this.top;
  }


  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.makeColumnsEqualWidth = true;
    this.composite.setLayout(layout);
    this.composite.setLayoutData(gridData);
    // create Assignment FC2WP section
    createFC2WPSection();
    // create Assignment CoC section
    createCocSection();
    // Create contacts section
    createContactsSection();
    // Create info section
    createInfoSection();

    validateFields();
  }


  /**
  *
  */
  private void createFC2WPSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.fc2wpSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Assignment  FC-WP");
    this.fc2wpSection.getDescriptionControl().setEnabled(false);
    this.fc2wpSection.setLayoutData(gridData);
    // create fc2wp form
    createFC2WPForm();
    this.fc2wpSection.setClient(this.fc2WpForm);
  }


  /**
   *
   */
  private void createFC2WPForm() {
    this.fc2WpForm = this.formToolkit.createForm(this.fc2wpSection);
    GridLayout layout = new GridLayout();
    layout.numColumns = 4;
    layout.makeColumnsEqualWidth = false;

    this.fc2WpForm.getBody().setLayout(layout);

    createFunctionField();
    createWpField();
    createDeletedField();
  }


  /**
   *
   */
  private void createFunctionField() {
    LabelUtil.getInstance().createLabel(this.fc2WpForm.getBody(), "Function*");
    GridData gridDataFunc = GridDataUtil.getInstance().createGridData();
    gridDataFunc.grabExcessVerticalSpace = false;
    this.funcText = TextUtil.getInstance().createText(this.fc2WpForm.getBody(), gridDataFunc, true, "");
    this.funcText.setSize(300, 20);
    this.funcText.setEditable(false);
    if (this.isMultipleUpdate) {
      this.funcText.setText("<Multiple Values>");
    }
    else {
      if (!this.isAddFc2Wp && (null != this.selfc2wpMapping) && (null != this.selfc2wpMapping.getFunctionName())) {
        this.funcText.setText(this.selfc2wpMapping.getFunctionName());
      }
    }

    if (this.isAddFc2Wp) {
      Button funcSearch = new Button(this.fc2WpForm.getBody(), SWT.PUSH);
      funcSearch.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
      funcSearch.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {

          FuncSelectionDialog funcSelDialog = new FuncSelectionDialog(Display.getCurrent().getActiveShell(), true,
              EditFC2WPMappingDialog.this.fc2wpVersObj.getFc2wpMappingMap().keySet(), EditFC2WPMappingDialog.this);
          funcSelDialog.open();
          if ((null != EditFC2WPMappingDialog.this.funcText) &&
              !EditFC2WPMappingDialog.this.funcText.getText().isEmpty()) {
            EditFC2WPMappingDialog.this.delFunction.setEnabled(true);
            enableAllFields();
            EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
          }
        }
      });

      this.delFunction = new Button(this.fc2WpForm.getBody(), SWT.PUSH);
      this.delFunction.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
      this.delFunction.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          EditFC2WPMappingDialog.this.funcText.setText("");
          EditFC2WPMappingDialog.this.newFuncDetailsSet.clear();
          disableAllFields();
          EditFC2WPMappingDialog.this.saveBtn.setEnabled(false);
        }
      });
      this.delFunction.setEnabled(CommonUtils.isNotEmptyString(this.funcText.getText()));
    }
    else {

      LabelUtil.getInstance().createLabel(this.fc2WpForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.fc2WpForm.getBody(), "");
    }
  }


  /**
   *
   */
  private void createWpField() {
    LabelUtil.getInstance().createLabel(this.fc2WpForm.getBody(), "Work Package");
    GridData gridDataWp = GridDataUtil.getInstance().createGridData();
    gridDataWp.grabExcessVerticalSpace = false;
    this.wpText = TextUtil.getInstance().createText(this.fc2WpForm.getBody(), gridDataWp, true, "");
    this.wpText.setSize(300, 20);
    this.wpText.setEditable(false);
    if (null != this.selfc2wpMapping) {
      this.wpDivIdToSave = this.selfc2wpMapping.getWpDivId();
      if (this.isMultipleUpdate && !this.sameWorkPkg) {
        this.wpText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      }
      else if ((null != this.fc2wpVersObj.getWpDetMap()) &&
          (null != this.fc2wpVersObj.getWpDetMap().get(this.selfc2wpMapping.getWpDivId()))) {
        this.wpText.setText(this.fc2wpVersObj.getWpDetMap().get(this.selfc2wpMapping.getWpDivId()).getWpName());
      }
    }
    // Search Icon for WP under Assignment FC-WP
    createSearchForWp();
    // Delete icon for WP under Assignment FC-WP
    cretaeDeleteForWp();
  }


  /**
   *
   */
  private void createDeletedField() {
    LabelUtil.getInstance().createLabel(this.fc2WpForm.getBody(), "Deleted");
    this.isDeletedcheckBox = new Button(this.fc2WpForm.getBody(), SWT.CHECK);
    this.isDeletedcheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (EditFC2WPMappingDialog.this.useCurrDeleted != null) {
          EditFC2WPMappingDialog.this.useCurrDeleted.setSelection(false);
        }
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
      }
    });
    boolean initialSel = this.isDeletedcheckBox.getSelection();
    if (this.isMultipleUpdate && !this.sameDeleted) {

      this.useCurrDeleted = new Button(this.fc2WpForm.getBody(), SWT.CHECK);
      this.useCurrDeleted.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      this.useCurrDeleted.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      this.useCurrDeleted.setSelection(true);
      this.useCurrDeleted.addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent event) {
          if ((EditFC2WPMappingDialog.this.isDeletedcheckBox.getSelection() != initialSel) &&
              EditFC2WPMappingDialog.this.useCurrDeleted.getSelection()) {
            EditFC2WPMappingDialog.this.isDeletedcheckBox.setSelection(initialSel);
          }
          EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        }
      });

    }
    else {
      if (null != this.selfc2wpMapping) {
        this.isDeletedcheckBox.setSelection(this.selfc2wpMapping.isDeleted());
        LabelUtil.getInstance().createLabel(this.fc2WpForm.getBody(), "");
      }
    }
  }


  /**
   *
   */
  private void cretaeDeleteForWp() {
    this.delWp = new Button(this.fc2WpForm.getBody(), SWT.PUSH);
    this.delWp.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.delWp.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        deleteWP();
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
      }
    });

    this.delWp.setEnabled(CommonUtils.isNotEmptyString(this.wpText.getText()));
  }


  /**
   *
   */
  private void createSearchForWp() {
    this.wpSearch = new Button(this.fc2WpForm.getBody(), SWT.PUSH);
    this.wpSearch.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.wpSearch.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        WPSelectionDialog wpSelDialog = new WPSelectionDialog(Display.getCurrent().getActiveShell(),
            EditFC2WPMappingDialog.this, EditFC2WPMappingDialog.this.fc2wpDef.getDivisionValId());
        wpSelDialog.open();
        if (wpSelDialog.isSaveSuccess()) {
          if (null != wpSelDialog.getSelWpDivId()) {
            EditFC2WPMappingDialog.this.wpDivIdToSave = wpSelDialog.getSelWpDivId();
            EditFC2WPMappingDialog.this.wpText.setText(wpSelDialog.getSelWpName());
            checkNResetAgreeWithCoC();
            EditFC2WPMappingDialog.this.delWp.setEnabled(true);
            if (EditFC2WPMappingDialog.this.useWpDefcheckBox.getSelection()) {
              setContFirstFrmWPDiv(EditFC2WPMappingDialog.this.wpDivIdToSave);
              setContSecondFrmWPDIv(EditFC2WPMappingDialog.this.wpDivIdToSave);
            }
            EditFC2WPMappingDialog.this.useWpDefcheckBox.setEnabled(true);
            if (!EditFC2WPMappingDialog.this.useWpDefcheckBox.getSelection()) {
              enableAndSetContacts();
            }
          }
          else {
            deleteWP();
          }
          EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        }
      }
    });
  }


  /**
  *
  */
  private void enableAndSetContacts() {
    EditFC2WPMappingDialog.this.firstContactText.setEnabled(true);
    EditFC2WPMappingDialog.this.secondContactText.setEnabled(true);
    EditFC2WPMappingDialog.this.firstContactSearch.setEnabled(true);
    EditFC2WPMappingDialog.this.secondContactSearch.setEnabled(true);
    EditFC2WPMappingDialog.this.delfirstContact.setEnabled((null != EditFC2WPMappingDialog.this.firstContactText) &&
        !EditFC2WPMappingDialog.this.firstContactText.getText().isEmpty());
    EditFC2WPMappingDialog.this.delSecondContact.setEnabled((null != EditFC2WPMappingDialog.this.secondContactText) &&
        !EditFC2WPMappingDialog.this.secondContactText.getText().isEmpty());

    if (EditFC2WPMappingDialog.this.useCurrWpDef != null) {
      EditFC2WPMappingDialog.this.useCurrWpDef.setSelection(false);
      EditFC2WPMappingDialog.this.useCurrWpDef.setEnabled(false);
      EditFC2WPMappingDialog.this.firstContactText.setText("");
      EditFC2WPMappingDialog.this.secondContactText.setText("");
    }
  }

  /**
   *
   */
  private void deleteWP() {
    EditFC2WPMappingDialog.this.wpDivIdToSave = null;
    EditFC2WPMappingDialog.this.wpText.setText("");
    EditFC2WPMappingDialog.this.delWp.setEnabled(false);
    if (EditFC2WPMappingDialog.this.useWpDefcheckBox.getSelection()) {
      EditFC2WPMappingDialog.this.firstContactText.setText("");
      EditFC2WPMappingDialog.this.firstContactIdToSave = null;
      EditFC2WPMappingDialog.this.secondContactText.setText("");
      EditFC2WPMappingDialog.this.secondContctIdToSave = null;
      EditFC2WPMappingDialog.this.delfirstContact.setEnabled(false);
      EditFC2WPMappingDialog.this.delSecondContact.setEnabled(false);
    }
  }

  /**
  *
  */
  private void createCocSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.cocSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Assignment CoC");
    this.cocSection.getDescriptionControl().setEnabled(false);
    this.cocSection.setLayoutData(gridData);
    // create table form
    createCocForm();
    this.cocSection.setClient(this.cocForm);
  }

  /**
   * Create table form
   */
  private void createCocForm() {
    this.cocForm = this.formToolkit.createForm(this.cocSection);
    createAgreedWithCocCheckBox();
    createAgreedOn();
    createRespForAgrmnt();
  }


  /**
   * @param layout
   */
  private void createAgreedWithCocCheckBox() {
    GridLayout layout = new GridLayout();
    LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "Agreed with CoC");
    this.isAgreeWithCoCCheckBox = new Button(this.cocForm.getBody(), SWT.CHECK);
    this.isAgreeWithCoCCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (EditFC2WPMappingDialog.this.isAgreeWithCoCCheckBox.getSelection()) {
          EditFC2WPMappingDialog.this.agreedOnText
              .setText(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, Calendar.getInstance()));
          EditFC2WPMappingDialog.this.delAgreedOn.setEnabled(true);
        }
        if (EditFC2WPMappingDialog.this.useCurrCoc != null) {
          EditFC2WPMappingDialog.this.useCurrCoc.setSelection(false);
        }
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
      }
    });
    final boolean initialSel = this.isAgreeWithCoCCheckBox.getSelection();

    LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");

    if (this.isMultipleUpdate && !this.sameCoC) {
      resetUseCurrCocBtn(layout, initialSel);
    }
    else {
      layout.numColumns = 4;
      layout.makeColumnsEqualWidth = false;
      if (null != this.selfc2wpMapping) {
        this.isAgreeWithCoCCheckBox.setSelection(this.selfc2wpMapping.isAgreeWithCoc());
      }
    }
    this.cocForm.getBody().setLayout(layout);
    if ((this.isMultipleUpdate && this.sameCoC) || !this.isMultipleUpdate) {
      LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
    }
  }


  /**
   * @param layout
   * @param initialSel
   */
  private void resetUseCurrCocBtn(final GridLayout layout, final boolean initialSel) {
    // if the reveiw methods are different , create a new radio button
    LabelUtil.getInstance().createLabel(this.cocForm.getBody(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    this.useCurrCoc = new Button(this.cocForm.getBody(), SWT.CHECK);
    this.useCurrCoc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.useCurrCoc.setSelection(true);
    layout.numColumns = CommonUIConstants.COLUMN_INDEX_6;
    this.useCurrCoc.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if ((EditFC2WPMappingDialog.this.isAgreeWithCoCCheckBox.getSelection() != initialSel) &&
            EditFC2WPMappingDialog.this.useCurrCoc.getSelection()) {
          EditFC2WPMappingDialog.this.isAgreeWithCoCCheckBox.setSelection(initialSel);
        }
      }
    });
    LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
  }


  /**
   *
   */
  private void createAgreedOn() {
    LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "Agreed On");
    GridData gridDataDate = GridDataUtil.getInstance().createGridData();
    gridDataDate.grabExcessVerticalSpace = false;
    this.agreedOnText = TextUtil.getInstance().createText(this.cocForm.getBody(), gridDataDate, true, "");
    this.agreedOnText.setSize(300, 20);
    this.agreedOnText.setEditable(false);
    if (this.isMultipleUpdate && !this.sameAgreedOn) {
      this.agreedOnText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else if ((null != this.selfc2wpMapping) && (null != this.selfc2wpMapping.getAgreeWithCocDate())) {
      SimpleDateFormat df = new SimpleDateFormat(DateFormat.DATE_FORMAT_09, Locale.getDefault(Locale.Category.FORMAT));
      String reportDate = df.format(this.selfc2wpMapping.getAgreeWithCocDate());
      this.agreedOnText.setText(reportDate);
      this.agreeWithCocDate = this.selfc2wpMapping.getAgreeWithCocDate();
    }

    this.calendar = new Button(this.cocForm.getBody(), SWT.PUSH);
    this.calendar.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CALENDAR_16X16));
    this.calendar.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        CalendarDialog calDailog = new CalendarDialog();
        calDailog.addCalendarDialogFc2Wp(EditFC2WPMappingDialog.this.cocForm.getBody(),
            EditFC2WPMappingDialog.this.agreedOnText, DateFormat.DATE_FORMAT_09,
            EditFC2WPMappingDialog.this.delAgreedOn);
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
      }
    });
    this.delAgreedOn = new Button(this.cocForm.getBody(), SWT.PUSH);
    this.delAgreedOn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.delAgreedOn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        EditFC2WPMappingDialog.this.agreeWithCocDate = null;
        EditFC2WPMappingDialog.this.agreedOnText.setText("");
        EditFC2WPMappingDialog.this.delAgreedOn.setEnabled((null != EditFC2WPMappingDialog.this.agreedOnText) &&
            !EditFC2WPMappingDialog.this.agreedOnText.getText().isEmpty());
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
      }
    });
    this.delAgreedOn.setEnabled(CommonUtils.isNotEmptyString(this.agreedOnText.getText()));
    if (this.isMultipleUpdate && !this.sameCoC) {
      LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
    }
  }


  /**
   *
   */
  private void createRespForAgrmnt() {
    LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "Resp. for Agreement");
    GridData gridDataResp = GridDataUtil.getInstance().createGridData();
    gridDataResp.grabExcessVerticalSpace = false;
    this.agreementCocRespText = TextUtil.getInstance().createText(this.cocForm.getBody(), gridDataResp, true, "");
    this.agreementCocRespText.setSize(300, 20);
    this.agreementCocRespText.setEditable(false);
    if (this.isMultipleUpdate && !this.sameResponsible) {
      this.agreementCocRespText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else if ((null != EditFC2WPMappingDialog.this.selfc2wpMapping) &&
        (null != EditFC2WPMappingDialog.this.selfc2wpMapping.getAgreeWithCocRespUserId())) {
      this.agreeWithCocRespUserIdToSave = EditFC2WPMappingDialog.this.selfc2wpMapping.getAgreeWithCocRespUserId();
      User cocRespUser =
          this.fc2wpVersObj.getUserMap().get(EditFC2WPMappingDialog.this.selfc2wpMapping.getAgreeWithCocRespUserId());
      if (null != cocRespUser) {
        EditFC2WPMappingDialog.this.agreementCocRespText.setText(cocRespUser.getDescription());
      }
      else {
        EditFC2WPMappingDialog.this.agreementCocRespText.setText("");
        this.agreeWithCocRespUserIdToSave = null;
      }
    }

    this.respSearch = new Button(this.cocForm.getBody(), SWT.PUSH);
    this.respSearch.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.respSearch.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        FC2WPContactPersonDialog agreementRespDialog =
            new FC2WPContactPersonDialog(Display.getCurrent().getActiveShell(), SELECT_USER, SELECT_USER,
                "Select Responsible for Agreement", SELECT, false, EditFC2WPMappingDialog.this.fc2wpDefBO);
        agreementRespDialog.open();

        // Ok Pressed
        if (agreementRespDialog.getReturnCode() == OK_PRESSED) {
          String agreementResp = respDialogOkPressed(agreementRespDialog);
          EditFC2WPMappingDialog.this.agreementCocRespText.setText(agreementResp);
          EditFC2WPMappingDialog.this.delRespAgreement
              .setEnabled((null != EditFC2WPMappingDialog.this.agreementCocRespText) &&
                  !EditFC2WPMappingDialog.this.agreementCocRespText.getText().isEmpty());
          EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        }
      }
    });
    this.delRespAgreement = new Button(this.cocForm.getBody(), SWT.PUSH);
    this.delRespAgreement.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.delRespAgreement.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        EditFC2WPMappingDialog.this.agreeWithCocRespUserIdToSave = null;
        EditFC2WPMappingDialog.this.agreementCocRespText.setText("");
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        EditFC2WPMappingDialog.this.delRespAgreement
            .setEnabled((null != EditFC2WPMappingDialog.this.agreementCocRespText) &&
                !EditFC2WPMappingDialog.this.agreementCocRespText.getText().isEmpty());
      }
    });
    EditFC2WPMappingDialog.this.delRespAgreement
        .setEnabled((null != EditFC2WPMappingDialog.this.agreementCocRespText) &&
            !EditFC2WPMappingDialog.this.agreementCocRespText.getText().isEmpty());
    if (this.isMultipleUpdate && !this.sameCoC) {
      LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.cocForm.getBody(), "");
    }
  }


  private void checkNResetAgreeWithCoC() {
    for (FC2WPMapping fc2wpToUpdate : this.fc2wpList) {
      if (isWpModified(fc2wpToUpdate) || isWpDeletedFlagModified(fc2wpToUpdate) || isRespModified(fc2wpToUpdate)) {
        this.isAgreeWithCoCCheckBox.setSelection(false);
        break;
      }
    }
  }


  /**
   * @param fc2wpToUpdate
   * @return
   */
  private boolean isRespModified(final FC2WPMapping fc2wpToUpdate) {
    return CommonUtils.isNotEqual(this.secondContactText.getText(),
        this.fc2wpVersObj.getUserMap().get(fc2wpToUpdate.getContactPersonSecondId())) ||
        CommonUtils.isNotEqual(this.firstContactText.getText(),
            this.fc2wpVersObj.getUserMap().get(fc2wpToUpdate.getContactPersonId()));
  }


  /**
   * @return
   */
  private boolean isWpDeletedFlagModified(final FC2WPMapping fc2wpToUpdate) {
    return CommonUtils.isNotEqual(this.isDeletedcheckBox.getSelection(), fc2wpToUpdate.isDeleted());
  }


  /**
   * @param fc2wpToUpdate
   * @return
   */
  private boolean isWpModified(final FC2WPMapping fc2wpToUpdate) {
    return (null != fc2wpToUpdate.getWpDivId()) && CommonUtils.isNotEqual(this.wpText.getText(),
        this.fc2wpVersObj.getWpDetMap().get(fc2wpToUpdate.getWpDivId()).getWpName());
  }

  /**
   * @param agreementRespDialog
   * @param agreementResp
   * @return
   */
  private String respDialogOkPressed(final FC2WPContactPersonDialog agreementRespDialog) {
    String agreementResp = "";
    if (null == agreementRespDialog.getSelectedUser()) {
      if ((null != EditFC2WPMappingDialog.this.selfc2wpMapping) &&
          (null != EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonId())) {
        User cocRespUser =
            this.fc2wpVersObj.getUserMap().get(EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonId());
        agreementResp = cocRespUser.getDescription();
        EditFC2WPMappingDialog.this.agreeWithCocRespUserIdToSave = cocRespUser.getId();
      }
    }
    else {
      if (agreementRespDialog.getSelectedUser().getId().equals(ApicConstants.APIC_DUMMY_USER_ID)) {
        agreementResp = "";
        EditFC2WPMappingDialog.this.agreeWithCocRespUserIdToSave = null;
      }
      else {
        agreementResp = agreementRespDialog.getSelectedUser().getDescription();
        EditFC2WPMappingDialog.this.agreeWithCocRespUserIdToSave = agreementRespDialog.getSelectedUser().getId();
      }
    }
    return agreementResp;
  }


  /**
  *
  */
  private void createContactsSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.contactsSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Contact Persons");
    this.contactsSection.getDescriptionControl().setEnabled(false);
    this.contactsSection.setLayoutData(gridData);
    createContactsForm();
    this.contactsSection.setClient(this.contactsForm);
  }

  /**
  *
  */
  private void createContactsForm() {
    this.contactsForm = this.formToolkit.createForm(this.contactsSection);
    GridLayout layout = new GridLayout();

    createUseWpDefaultsCheckBox(layout);
    createFirstContactField();
    createSecondContactField();

    addListenerForUseWpDefCheckBox();

    if (this.isMultipleUpdate) {
      enableOrDisableFieldsForMultUpdate();
    }
  }


  /**
   * @param layout
   */
  private void createUseWpDefaultsCheckBox(final GridLayout layout) {
    LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "Use WP Defaults");
    this.useWpDefcheckBox = new Button(this.contactsForm.getBody(), SWT.CHECK);
    this.useWpDefcheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (EditFC2WPMappingDialog.this.useCurrWpDef != null) {
          EditFC2WPMappingDialog.this.useCurrWpDef.setSelection(false);
        }
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
      }
    });
    LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
    if (this.isMultipleUpdate && !this.sameWPDef) {
      resetUseCurrWpDefBtn(layout);
    }
    else {
      layout.numColumns = 4;
      layout.makeColumnsEqualWidth = false;
      if (null != this.selfc2wpMapping) {
        this.useWpDefcheckBox.setSelection(this.selfc2wpMapping.isUseWpDef());
      }
    }
    this.contactsForm.getBody().setLayout(layout);
    LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
  }


  /**
   * @param layout
   */
  private void resetUseCurrWpDefBtn(final GridLayout layout) {
    // if the reveiw methods are different , create a new radio button
    LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    this.useCurrWpDef = new Button(this.contactsForm.getBody(), SWT.CHECK);
    this.useCurrWpDef.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.useCurrWpDef.setSelection(true);
    layout.numColumns = CommonUIConstants.COLUMN_INDEX_6;
    this.useCurrWpDef.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (EditFC2WPMappingDialog.this.useCurrWpDef.getSelection()) {
          setValuesWhenChckBoxSlctd();
        }
        else if (!EditFC2WPMappingDialog.this.useCurrWpDef.getSelection()) {
          setValuesWhenChckBoxUnslctd();
        }
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
      }
    });
  }


  /**
   *
   */
  private void setValuesWhenChckBoxSlctd() {
    EditFC2WPMappingDialog.this.useWpDefcheckBox.setSelection(false);
    if (!EditFC2WPMappingDialog.this.sameWorkPkg) {
      EditFC2WPMappingDialog.this.useWpDefcheckBox.setEnabled(false);
      EditFC2WPMappingDialog.this.firstContactText.setEnabled(false);
      EditFC2WPMappingDialog.this.secondContactText.setEnabled(false);
      EditFC2WPMappingDialog.this.firstContactSearch.setEnabled(false);
      EditFC2WPMappingDialog.this.secondContactSearch.setEnabled(false);
      EditFC2WPMappingDialog.this.delfirstContact.setEnabled(false);
      EditFC2WPMappingDialog.this.delSecondContact.setEnabled(false);
    }
    if (EditFC2WPMappingDialog.this.isMultipleUpdate && !EditFC2WPMappingDialog.this.sameFirstContact) {
      EditFC2WPMappingDialog.this.firstContactText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    if (EditFC2WPMappingDialog.this.isMultipleUpdate && !EditFC2WPMappingDialog.this.sameSecondContact) {
      EditFC2WPMappingDialog.this.secondContactText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
  }


  /**
   *
   */
  private void setValuesWhenChckBoxUnslctd() {
    if (EditFC2WPMappingDialog.this.sameWorkPkg) {
      EditFC2WPMappingDialog.this.useWpDefcheckBox.setEnabled(true);
      EditFC2WPMappingDialog.this.firstContactText.setEnabled(true);
      EditFC2WPMappingDialog.this.secondContactText.setEnabled(true);
      EditFC2WPMappingDialog.this.firstContactSearch
          .setEnabled((null != EditFC2WPMappingDialog.this.firstContactText) &&
              !EditFC2WPMappingDialog.this.firstContactText.getText().isEmpty());
      EditFC2WPMappingDialog.this.secondContactSearch
          .setEnabled((null != EditFC2WPMappingDialog.this.secondContactText) &&
              !EditFC2WPMappingDialog.this.secondContactText.getText().isEmpty());


      EditFC2WPMappingDialog.this.delfirstContact.setEnabled(true);
      EditFC2WPMappingDialog.this.delSecondContact.setEnabled(true);
    }
    EditFC2WPMappingDialog.this.firstContactText.setText("");
    EditFC2WPMappingDialog.this.secondContactText.setText("");
  }


  /**
   *
   */
  private void createFirstContactField() {
    LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "First Contact");
    GridData gridDataFirstContact = GridDataUtil.getInstance().createGridData();
    gridDataFirstContact.grabExcessVerticalSpace = false;
    this.firstContactText =
        TextUtil.getInstance().createText(this.contactsForm.getBody(), gridDataFirstContact, true, "");
    this.firstContactText.setSize(300, 20);
    this.firstContactText.setEditable(false);
    if (this.isMultipleUpdate && !this.sameFirstContact) {
      this.firstContactText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      if (this.useWpDefcheckBox.getSelection() && (null != this.selfc2wpMapping)) {
        setContFirstFrmWPDiv(this.selfc2wpMapping.getWpDivId());
      }
      else {
        setFirstCntctFromFc2wpMapping();
      }
    }

    this.firstContactSearch = new Button(this.contactsForm.getBody(), SWT.PUSH);
    this.firstContactSearch.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.firstContactSearch.setEnabled(true);
    this.firstContactSearch.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        FC2WPContactPersonDialog firstContactDialog =
            new FC2WPContactPersonDialog(Display.getCurrent().getActiveShell(), SELECT_USER, SELECT_USER,
                "Select First Contact Person", SELECT, false, EditFC2WPMappingDialog.this.fc2wpDefBO);
        firstContactDialog.open();

        if (firstContactDialog.getReturnCode() == OK_PRESSED) {
          String firstCnctName = firstCnctOkPressed(firstContactDialog);
          EditFC2WPMappingDialog.this.firstContactText.setText(firstCnctName);
          EditFC2WPMappingDialog.this.delfirstContact
              .setEnabled((null != EditFC2WPMappingDialog.this.firstContactText) &&
                  !EditFC2WPMappingDialog.this.firstContactText.getText().isEmpty());
          EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        }
      }
    });
    this.firstContactSearch.setEnabled(!this.useWpDefcheckBox.getSelection());


    this.delfirstContact = new Button(this.contactsForm.getBody(), SWT.PUSH);
    this.delfirstContact.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.delfirstContact.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        EditFC2WPMappingDialog.this.firstContactIdToSave = null;
        EditFC2WPMappingDialog.this.firstContactText.setText("");
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        EditFC2WPMappingDialog.this.delfirstContact.setEnabled((null != EditFC2WPMappingDialog.this.firstContactText) &&
            !EditFC2WPMappingDialog.this.firstContactText.getText().isEmpty() &&
            !EditFC2WPMappingDialog.this.useWpDefcheckBox.getSelection());
      }
    });
    this.delfirstContact.setEnabled((null != this.firstContactText) && !this.firstContactText.getText().isEmpty() &&
        !this.useWpDefcheckBox.getSelection());


    if (this.isMultipleUpdate && !this.sameWPDef) {
      LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
    }
  }


  /**
   *
   */
  private void createSecondContactField() {
    LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "Second Contact");
    GridData gridDataSecndContact = GridDataUtil.getInstance().createGridData();
    gridDataSecndContact.grabExcessVerticalSpace = false;
    this.secondContactText =
        TextUtil.getInstance().createText(this.contactsForm.getBody(), gridDataSecndContact, true, "");
    this.secondContactText.setSize(300, 20);
    this.secondContactText.setEditable(false);
    if (this.isMultipleUpdate && !this.sameSecondContact) {
      this.secondContactText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      if (this.useWpDefcheckBox.getSelection() && (null != this.selfc2wpMapping)) {
        setContSecondFrmWPDIv(this.selfc2wpMapping.getWpDivId());
      }
      else {
        setSecondCntctFrmFc2WpMapping();
      }
    }
    this.secondContactSearch = new Button(this.contactsForm.getBody(), SWT.PUSH);
    this.secondContactSearch.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNC_SEARCH_28X30));
    this.secondContactSearch.setEnabled(true);
    this.secondContactSearch.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        FC2WPContactPersonDialog secondContactDialog =
            new FC2WPContactPersonDialog(Display.getCurrent().getActiveShell(), SELECT_USER, SELECT_USER,
                "Select Second Contact Person", SELECT, false, EditFC2WPMappingDialog.this.fc2wpDefBO);
        secondContactDialog.open();

        if (secondContactDialog.getReturnCode() == OK_PRESSED) {
          String secondCnctName = secondCnctOkPressed(secondContactDialog);
          EditFC2WPMappingDialog.this.secondContactText.setText(secondCnctName);
          EditFC2WPMappingDialog.this.delSecondContact
              .setEnabled((null != EditFC2WPMappingDialog.this.secondContactText) &&
                  !EditFC2WPMappingDialog.this.secondContactText.getText().isEmpty());
          EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        }
      }
    });
    this.delSecondContact = new Button(this.contactsForm.getBody(), SWT.PUSH);
    this.delSecondContact.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    this.delSecondContact.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        EditFC2WPMappingDialog.this.secondContctIdToSave = null;
        EditFC2WPMappingDialog.this.secondContactText.setText("");
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        EditFC2WPMappingDialog.this.delSecondContact
            .setEnabled((null != EditFC2WPMappingDialog.this.secondContactText) &&
                !EditFC2WPMappingDialog.this.secondContactText.getText().isEmpty() &&
                !EditFC2WPMappingDialog.this.useWpDefcheckBox.getSelection());
      }
    });
    this.delSecondContact.setEnabled((null != this.secondContactText) && !this.secondContactText.getText().isEmpty() &&
        !this.useWpDefcheckBox.getSelection());

    this.secondContactSearch.setEnabled(!this.useWpDefcheckBox.getSelection());
    if (this.isMultipleUpdate && !this.sameWPDef) {
      LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
      LabelUtil.getInstance().createLabel(this.contactsForm.getBody(), "");
    }
  }


  /**
   *
   */
  private void addListenerForUseWpDefCheckBox() {
    this.useWpDefcheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (EditFC2WPMappingDialog.this.useWpDefcheckBox.getSelection()) {

          if (null != EditFC2WPMappingDialog.this.wpDivIdToSave) {

            boolean result = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Set to Default",
                "Do you want to delete the current contact persons and reuse the Default?");
            if (result) {
              EditFC2WPMappingDialog.this.firstContactSearch.setEnabled(false);
              EditFC2WPMappingDialog.this.secondContactSearch.setEnabled(false);
              EditFC2WPMappingDialog.this.delfirstContact.setEnabled(false);
              EditFC2WPMappingDialog.this.delSecondContact.setEnabled(false);
              EditFC2WPMappingDialog.this.firstContactText.setText("");
              EditFC2WPMappingDialog.this.firstContactIdToSave = null;
              EditFC2WPMappingDialog.this.secondContactText.setText("");
              EditFC2WPMappingDialog.this.secondContctIdToSave = null;
              setContFirstFrmWPDiv(EditFC2WPMappingDialog.this.wpDivIdToSave);
              setContSecondFrmWPDIv(EditFC2WPMappingDialog.this.wpDivIdToSave);
              EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
            }
            else {
              setValuesIfNoDefaultWP();
            }
          }
          else {
            EditFC2WPMappingDialog.this.firstContactSearch.setEnabled(false);
            EditFC2WPMappingDialog.this.secondContactSearch.setEnabled(false);
            EditFC2WPMappingDialog.this.delfirstContact.setEnabled(false);
            EditFC2WPMappingDialog.this.delSecondContact.setEnabled(false);
            EditFC2WPMappingDialog.this.firstContactText.setText("");
            EditFC2WPMappingDialog.this.firstContactIdToSave = null;
            EditFC2WPMappingDialog.this.secondContactText.setText("");
            EditFC2WPMappingDialog.this.secondContctIdToSave = null;
            EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
          }
        }
        else {
          setValuesIfNoDefaultWP();
          EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
        }
      }
    });
  }


  /**
   *
   */
  private void enableOrDisableFieldsForMultUpdate() {
    if ((!this.sameWorkPkg) || ((this.useCurrWpDef != null) && this.useCurrWpDef.getSelection())) {
      disableContactFields();
    }
    if (this.sameWorkPkg && (this.useCurrWpDef != null) && this.useCurrWpDef.getSelection()) {
      enableContactFields();
    }
  }


  /**
   *
   */
  private void enableContactFields() {
    this.useWpDefcheckBox.setEnabled(true);
    if (this.firstContactText != null) {
      this.firstContactText.setEnabled(true);
    }
    if (this.secondContactText != null) {
      this.secondContactText.setEnabled(true);
    }
    this.firstContactSearch.setEnabled(true);
    this.secondContactSearch.setEnabled(true);
    this.delfirstContact.setEnabled(true);
    this.delSecondContact.setEnabled(true);
    this.useCurrWpDef.setEnabled(true);
  }


  /**
   *
   */
  private void disableContactFields() {
    this.useWpDefcheckBox.setEnabled(false);
    if (this.firstContactText != null) {
      this.firstContactText.setEnabled(false);
    }
    if (this.secondContactText != null) {
      this.secondContactText.setEnabled(false);
    }

    this.firstContactSearch.setEnabled(false);
    this.secondContactSearch.setEnabled(false);
    this.delfirstContact.setEnabled(false);
    this.delSecondContact.setEnabled(false);
    if ((this.useCurrWpDef != null) && this.useCurrWpDef.getSelection()) {
      this.useCurrWpDef.setEnabled(false);
    }
  }


  /**
  *
  */
  private void createInfoSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.infoSection =
        SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Additional Information");
    this.infoSection.getDescriptionControl().setEnabled(false);
    this.infoSection.setLayoutData(gridData);
    // create table form
    createInfoForm();
    this.infoSection.setClient(this.infoForm);
  }


  /**
  *
  */
  private void createInfoForm() {
    this.infoForm = this.formToolkit.createForm(this.infoSection);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.makeColumnsEqualWidth = false;
    this.infoForm.getBody().setLayout(layout);

    LabelUtil.getInstance().createLabel(this.infoForm.getBody(), "Comments");

    GridData gridDataText = new GridData();
    gridDataText.grabExcessHorizontalSpace = true;
    gridDataText.horizontalAlignment = GridData.FILL;
    gridDataText.verticalAlignment = GridData.FILL;
    gridDataText.verticalSpan = 2;
    gridDataText.heightHint = 80;
    gridDataText.widthHint = 300;
    gridDataText.grabExcessVerticalSpace = true;
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.infoForm.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, gridDataText);
    if (this.isMultipleUpdate && !this.sameComment) {
      boxContentDisplay.getText().setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else {
      if (null != this.selfc2wpMapping) {
        boxContentDisplay.getText().setText(CommonUtils.checkNull(this.selfc2wpMapping.getComments()));
      }
    }
    this.commentsText = boxContentDisplay.getText();

    LabelUtil.getInstance().createLabel(this.infoForm.getBody(), "");
    LabelUtil.getInstance().createLabel(this.infoForm.getBody(), "FC2WP Information");
    TextBoxContentDisplay infoBoxContentDisplay = new TextBoxContentDisplay(this.infoForm.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_TEXT_BOX_SIZE, gridDataText);
    if (this.isMultipleUpdate && !this.sameFc2WpInfo) {
      infoBoxContentDisplay.getText().setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
    }
    else if (null != this.selfc2wpMapping) {
      infoBoxContentDisplay.getText().setText(CommonUtils.checkNull(this.selfc2wpMapping.getFc2wpInfo()));
    }
    this.fc2WpInfoText = infoBoxContentDisplay.getText();
    ModifyListener listener = e -> EditFC2WPMappingDialog.this.saveBtn.setEnabled(true);
    boxContentDisplay.getText().addModifyListener(listener);
    infoBoxContentDisplay.getText().addModifyListener(listener);
  }


  /**
  *
  */
  private void validateFields() {
    if (EditFC2WPMappingDialog.this.isAddFc2Wp) {
      if ((null != EditFC2WPMappingDialog.this.funcText) && !EditFC2WPMappingDialog.this.funcText.getText().isEmpty()) {
        enableAllFields();
      }
      else {
        disableAllFields();
      }
    }
  }


  /**
  *
  */
  private void disableAllFields() {
    this.wpSearch.setEnabled(false);
    this.isDeletedcheckBox.setEnabled(false);
    this.useWpDefcheckBox.setEnabled(false);
    this.firstContactSearch.setEnabled(false);
    this.secondContactSearch.setEnabled(false);
    this.isAgreeWithCoCCheckBox.setEnabled(false);
    this.calendar.setEnabled(false);
    this.respSearch.setEnabled(false);
    this.commentsText.setEditable(false);
    this.fc2WpInfoText.setEditable(false);
  }


  /**
  *
  */
  private void enableAllFields() {
    this.wpSearch.setEnabled(true);
    this.isDeletedcheckBox.setEnabled(true);
    this.useWpDefcheckBox.setEnabled(true);
    this.firstContactSearch.setEnabled(true);
    this.secondContactSearch.setEnabled(true);
    this.isAgreeWithCoCCheckBox.setEnabled(true);
    this.calendar.setEnabled(true);
    this.respSearch.setEnabled(true);
    this.commentsText.setEditable(true);
    this.fc2WpInfoText.setEditable(true);
  }


  /**
   * @param firstContactDialog
   * @param firstCnctName
   * @return
   */
  private String firstCnctOkPressed(final FC2WPContactPersonDialog firstContactDialog) {
    String firstCnctName = "";
    if (null == firstContactDialog.getSelectedUser()) {
      firstCnctName = setFirstCntNameForSlctdUser(firstCnctName);
    }
    else {
      if (firstContactDialog.getSelectedUser().getId().equals(ApicConstants.APIC_DUMMY_USER_ID)) {
        firstCnctName = "";
        EditFC2WPMappingDialog.this.firstContactIdToSave = null;
      }
      else {
        firstCnctName = firstContactDialog.getSelectedUser().getDescription();
        EditFC2WPMappingDialog.this.firstContactIdToSave = firstContactDialog.getSelectedUser().getId();
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
      }
      if ((EditFC2WPMappingDialog.this.useCurrWpDef != null) &&
          EditFC2WPMappingDialog.this.useCurrWpDef.getSelection()) {
        EditFC2WPMappingDialog.this.useCurrWpDef.setSelection(false);
      }
    }
    return firstCnctName;
  }


  /**
   * @param firstCnctName
   * @return
   */
  private String setFirstCntNameForSlctdUser(String firstCnctName) {
    if (EditFC2WPMappingDialog.this.isMultipleUpdate && !EditFC2WPMappingDialog.this.sameFirstContact) {
      firstCnctName = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    else {

      if ((null != EditFC2WPMappingDialog.this.selfc2wpMapping) &&
          (null != EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonId())) {
        User firstContactUser =
            this.fc2wpVersObj.getUserMap().get(EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonId());
        if (null != firstContactUser) {
          firstCnctName = firstContactUser.getDescription();
          EditFC2WPMappingDialog.this.firstContactIdToSave = firstContactUser.getId();
          EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        }
        else {
          firstCnctName = "";
          EditFC2WPMappingDialog.this.firstContactIdToSave = null;
        }
      }
    }
    return firstCnctName;
  }

  /**
   * @param secondContactDialog
   * @param secondCnctName
   * @return
   */
  private String secondCnctOkPressed(final FC2WPContactPersonDialog secondContactDialog) {
    String secondCnctName = "";
    if (null == secondContactDialog.getSelectedUser()) {
      secondCnctName = setScndCntctNameForSlctedUser(secondCnctName);
    }
    else {
      if (secondContactDialog.getSelectedUser().getId().equals(ApicConstants.APIC_DUMMY_USER_ID)) {
        secondCnctName = "";
        EditFC2WPMappingDialog.this.secondContctIdToSave = null;
      }
      else {
        secondCnctName = secondContactDialog.getSelectedUser().getDescription();
        EditFC2WPMappingDialog.this.secondContctIdToSave = secondContactDialog.getSelectedUser().getId();
        EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
      }
      if ((EditFC2WPMappingDialog.this.useCurrWpDef != null) &&
          EditFC2WPMappingDialog.this.useCurrWpDef.getSelection()) {
        EditFC2WPMappingDialog.this.useCurrWpDef.setSelection(false);
      }
    }
    return secondCnctName;
  }


  /**
   * @param secondCnctName
   * @return
   */
  private String setScndCntctNameForSlctedUser(String secondCnctName) {
    if (EditFC2WPMappingDialog.this.isMultipleUpdate && !EditFC2WPMappingDialog.this.sameSecondContact) {
      secondCnctName = CommonUIConstants.DISP_TEXT_USE_CUR_VAL;
    }
    else {

      if (null != EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonSecondId()) {
        User secondContactUser =
            this.fc2wpVersObj.getUserMap().get(EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonSecondId());
        if (null != secondContactUser) {
          secondCnctName = secondContactUser.getDescription();
          EditFC2WPMappingDialog.this.secondContctIdToSave = secondContactUser.getId();
          EditFC2WPMappingDialog.this.checkNResetAgreeWithCoC();
        }
        else {
          secondCnctName = "";
          EditFC2WPMappingDialog.this.secondContctIdToSave = null;
        }
      }
    }
    return secondCnctName;
  }

  /**
   * @param long1
   */
  private void setContSecondFrmWPDIv(final Long wpDivId) {
    EditFC2WPMappingDialog.this.secondContactText.setText("");
    if (null != EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(wpDivId)) {
      if (null != (EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(wpDivId)).getContactPersonSecondId()) {
        User secondContactUser = this.fc2wpVersObj.getUserMap()
            .get((EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(wpDivId)).getContactPersonSecondId());
        if (null != secondContactUser) {
          EditFC2WPMappingDialog.this.secondContactText.setText(secondContactUser.getDescription());
        }
        else {
          EditFC2WPMappingDialog.this.secondContactText.setText("");
          this.secondContctIdToSave = null;
        }
      }
      else {
        EditFC2WPMappingDialog.this.secondContactText.setText("");
        this.secondContctIdToSave = null;
      }
    }
  }

  /**
   *
   */
  private void setContFirstFrmWPDiv(final Long wpDivId) {
    EditFC2WPMappingDialog.this.firstContactText.setText("");
    if (null != EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(wpDivId)) {
      if (null != (EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(wpDivId)).getContactPersonId()) {
        User firstContactUser = this.fc2wpVersObj.getUserMap()
            .get(EditFC2WPMappingDialog.this.fc2wpVersObj.getWpDetMap().get(wpDivId).getContactPersonId());
        if (null != firstContactUser) {
          EditFC2WPMappingDialog.this.firstContactText.setText(firstContactUser.getDescription());
        }
        else {
          EditFC2WPMappingDialog.this.firstContactText.setText("");
          this.firstContactIdToSave = null;
        }
      }
      else {
        EditFC2WPMappingDialog.this.firstContactText.setText("");
        this.firstContactIdToSave = null;
      }
    }
  }

  /**
   */
  private void setFirstCntctFromFc2wpMapping() {
    if ((null != EditFC2WPMappingDialog.this.selfc2wpMapping) &&
        (null != EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonId())) {

      User firstContactUser =
          this.fc2wpVersObj.getUserMap().get(EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonId());
      if (null != firstContactUser) {
        EditFC2WPMappingDialog.this.firstContactText.setText(firstContactUser.getDescription());
        this.firstContactIdToSave = firstContactUser.getId();
      }
      else {
        EditFC2WPMappingDialog.this.firstContactText.setText("");
        this.firstContactIdToSave = null;
      }
    }
    else {
      EditFC2WPMappingDialog.this.firstContactText.setText("");
      this.firstContactIdToSave = null;
    }
  }

  /**
   *
   */
  private void setSecondCntctFrmFc2WpMapping() {
    if ((null != EditFC2WPMappingDialog.this.selfc2wpMapping) &&
        (null != EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonSecondId())) {
      User secondContactUser =
          this.fc2wpVersObj.getUserMap().get(EditFC2WPMappingDialog.this.selfc2wpMapping.getContactPersonSecondId());
      if (null != secondContactUser) {
        EditFC2WPMappingDialog.this.secondContactText.setText(secondContactUser.getDescription());
        this.secondContctIdToSave = secondContactUser.getId();
      }
      else {
        EditFC2WPMappingDialog.this.secondContactText.setText("");
        this.secondContctIdToSave = null;
      }
    }
    else {
      EditFC2WPMappingDialog.this.secondContactText.setText("");
      this.secondContctIdToSave = null;
    }
  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    if (this.isAddFc2Wp) {
      addNewFc2Wp();
    }
    else {

      List<FC2WPMapping> fc2wpCloneList = new ArrayList<>();
      for (FC2WPMapping fc2wpToUpdate : this.fc2wpList) {
        FC2WPMapping fc2WpMappingClone = null;
        try {
          fc2WpMappingClone = fc2wpToUpdate.clone();
        }
        catch (CloneNotSupportedException e) {
          CDMLogger.getInstance().error("Cloning FC2WP mapping failed", e, Activator.PLUGIN_ID);
        }
        setFc2WpMappingDataToClone(fc2WpMappingClone);
        fc2wpCloneList.add(fc2WpMappingClone);
      }

      FC2WPMappingWithDetails fc2wpVersMapping = updateFC2WPthruService(fc2wpCloneList);
      StringBuilder logger = new StringBuilder();
      if ((!fc2wpCloneList.isEmpty()) && (fc2wpVersMapping != null)) {
        for (FC2WPMapping fc2wpToUpdate : this.fc2wpList) {
          FC2WPMapping fc2wpMappingUpdated = fc2wpVersMapping.getFc2wpMappingMap().get(fc2wpToUpdate.getFunctionName());
          setFC2WPMAppingUpdated(fc2wpToUpdate, fc2wpMappingUpdated);
          logger.append(fc2wpToUpdate.getFunctionName() + ";");
        }
        mergeFc2WpVersMapping(fc2wpVersMapping);
      }

      CDMLogger.getInstance().info("FC2WP mapping updated for " + logger.toString(), Activator.PLUGIN_ID);
    }
    super.okPressed();
  }


  /**
   *
   */
  private void addNewFc2Wp() {
    if (CommonUtils.isNotEmpty(this.newFuncDetailsSet)) {
      List<FC2WPMapping> newFc2WpMappingList = new ArrayList<>();
      for (Function func : this.newFuncDetailsSet) {
        FC2WPMapping newFc2wp = createFc2wpObj(func);
        newFc2WpMappingList.add(newFc2wp);
      }

      // Create DB objects in webservice service
      FC2WPMappingWithDetails fc2wpVersMapping = createFC2WPthruService(newFc2WpMappingList);
      if (fc2wpVersMapping != null) {
        mergeFc2WpVersMapping(fc2wpVersMapping);
      }

    }
  }

  /**
   * @param newFc2WpMappingList
   * @return
   */
  private FC2WPMappingWithDetails createFC2WPthruService(final List<FC2WPMapping> newFc2WpMappingList) {
    FC2WPMappingWithDetails fc2wpVersMapping = null;

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
    try {
      fc2wpVersMapping = servClient.createFC2WPMapping(newFc2WpMappingList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return fc2wpVersMapping;
  }


  /**
   * @param func create new fc2wpMapping
   * @return
   */
  private FC2WPMapping createFc2wpObj(final Function func) {
    FC2WPMapping newFc2wp = new FC2WPMapping();
    newFc2wp.setFcwpVerId(this.fc2wpDefBO.getFc2wpVersion().getId());
    newFc2wp.setFunctionName(func.getName());
    newFc2wp.setFunctionId(func.getId());
    newFc2wp.setWpDivId(this.wpDivIdToSave);
    newFc2wp.setDeleted(this.isDeletedcheckBox.getSelection());
    newFc2wp.setAgreeWithCoc(this.isAgreeWithCoCCheckBox.getSelection());
    if ((null != this.agreedOnText.getText()) && !this.agreedOnText.getText().isEmpty()) {
      SimpleDateFormat formatter =
          new SimpleDateFormat(DateFormat.DATE_FORMAT_09, Locale.getDefault(Locale.Category.FORMAT));
      try {
        this.agreeWithCocDate = formatter.parse(this.agreedOnText.getText());
        newFc2wp.setAgreeWithCocDate(this.agreeWithCocDate);
      }
      catch (ParseException e) {
        CDMLogger.getInstance().error("Date not valid", Activator.PLUGIN_ID);
      }
    }

    newFc2wp.setAgreeWithCocRespUserId(this.agreeWithCocRespUserIdToSave);
    newFc2wp.setUseWpDef(this.useWpDefcheckBox.getSelection());
    newFc2wp.setContactPersonId(this.firstContactIdToSave);
    newFc2wp.setContactPersonSecondId(this.secondContctIdToSave);
    newFc2wp.setComments(this.commentsText.getText());
    newFc2wp.setFc2wpInfo(this.fc2WpInfoText.getText());
    return newFc2wp;
  }

  /**
   * @param fc2wpVersMapping
   */
  private void mergeFc2WpVersMapping(final FC2WPMappingWithDetails fc2wpVersMapping) {
    this.fc2wpVersObj.getBcMap().putAll(fc2wpVersMapping.getBcMap());
    this.fc2wpVersObj.getFc2wpMappingMap().putAll(fc2wpVersMapping.getFc2wpMappingMap());
    this.fc2wpVersObj.getPtTypeMap().putAll(fc2wpVersMapping.getPtTypeMap());
    this.fc2wpVersObj.getUserMap().putAll(fc2wpVersMapping.getUserMap());
    this.fc2wpVersObj.getWpDetMap().putAll(fc2wpVersMapping.getWpDetMap());
  }

  /**
   * @param fc2wpCloneList
   * @return
   */
  private FC2WPMappingWithDetails updateFC2WPthruService(final List<FC2WPMapping> fc2wpCloneList) {
    FC2WPMappingWithDetails fc2wpVersMapping = null;

    FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
    try {
      fc2wpVersMapping = servClient.updateFC2WPMapping(fc2wpCloneList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return fc2wpVersMapping;
  }

  /**
   * @param fc2WpMappingClone
   */
  private void setFc2WpMappingDataToClone(final FC2WPMapping fc2WpMappingClone) {
    fc2WpMappingClone.setFunctionName(this.funcText.getText());
    if (!CommonUtils.isEqual(this.wpText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setWpDivId(this.wpDivIdToSave);
    }
    if ((this.useCurrDeleted == null) || !this.useCurrDeleted.getSelection()) {
      fc2WpMappingClone.setDeleted(this.isDeletedcheckBox.getSelection());
    }
    if ((this.useCurrCoc == null) || !this.useCurrCoc.getSelection()) {
      fc2WpMappingClone.setAgreeWithCoc(this.isAgreeWithCoCCheckBox.getSelection());
    }
    if ((null != this.agreedOnText.getText()) && !this.agreedOnText.getText().isEmpty() &&
        !CommonUtils.isEqual(this.agreedOnText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      parseAgreedOnDate();
    }
    if (!CommonUtils.isEqual(this.agreedOnText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setAgreeWithCocDate(this.agreeWithCocDate);
    }
    if (!CommonUtils.isEqual(this.agreementCocRespText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setAgreeWithCocRespUserId(this.agreeWithCocRespUserIdToSave);
    }
    if ((this.useCurrWpDef == null) || !this.useCurrWpDef.getSelection()) {
      fc2WpMappingClone.setUseWpDef(this.useWpDefcheckBox.getSelection());
    }
    if (!CommonUtils.isEqual(this.firstContactText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setContactPersonId(this.firstContactIdToSave);
    }
    if (!CommonUtils.isEqual(this.secondContactText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setContactPersonSecondId(this.secondContctIdToSave);
    }
    if (!CommonUtils.isEqual(this.commentsText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setComments(this.commentsText.getText());
    }
    if (!CommonUtils.isEqual(this.fc2WpInfoText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2WpMappingClone.setFc2wpInfo(this.fc2WpInfoText.getText());
    }
  }


  /**
   *
   */
  private void parseAgreedOnDate() {
    SimpleDateFormat formatter =
        new SimpleDateFormat(DateFormat.DATE_FORMAT_09, Locale.getDefault(Locale.Category.FORMAT));
    try {
      this.agreeWithCocDate = formatter.parse(this.agreedOnText.getText());
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error("Date not valid", Activator.PLUGIN_ID);
    }
  }

  /**
   * @param fc2wpMappingToUpdate
   * @param fc2wpMappingUpdated
   */
  private void setFC2WPMAppingUpdated(final FC2WPMapping fc2wpMappingToUpdate, final FC2WPMapping fc2wpMappingUpdated) {
    fc2wpMappingToUpdate.setFunctionName(fc2wpMappingUpdated.getFunctionName());
    if (!CommonUtils.isEqual(this.wpText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setWpDivId(fc2wpMappingUpdated.getWpDivId());
    }
    if ((this.useCurrDeleted == null) || !this.useCurrDeleted.getSelection()) {
      fc2wpMappingToUpdate.setDeleted(fc2wpMappingUpdated.isDeleted());
    }
    if ((this.useCurrCoc == null) || !this.useCurrCoc.getSelection()) {
      fc2wpMappingToUpdate.setAgreeWithCoc(fc2wpMappingUpdated.isAgreeWithCoc());
    }
    if (!CommonUtils.isEqual(this.agreedOnText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setAgreeWithCocDate(fc2wpMappingUpdated.getAgreeWithCocDate());
    }
    if (!CommonUtils.isEqual(this.agreementCocRespText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setAgreeWithCocRespUserId(fc2wpMappingUpdated.getAgreeWithCocRespUserId());
    }
    if ((this.useCurrWpDef == null) || !this.useCurrWpDef.getSelection()) {
      fc2wpMappingToUpdate.setUseWpDef(fc2wpMappingUpdated.isUseWpDef());
    }
    if (!CommonUtils.isEqual(this.firstContactText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setContactPersonId(fc2wpMappingUpdated.getContactPersonId());
    }
    if (!CommonUtils.isEqual(this.secondContactText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setContactPersonSecondId(fc2wpMappingUpdated.getContactPersonSecondId());
    }
    if (!CommonUtils.isEqual(this.commentsText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setComments(fc2wpMappingUpdated.getComments());
    }
    if (!CommonUtils.isEqual(this.fc2WpInfoText.getText(), CommonUIConstants.DISP_TEXT_USE_CUR_VAL)) {
      fc2wpMappingToUpdate.setFc2wpInfo(fc2wpMappingUpdated.getFc2wpInfo());
    }
    fc2wpMappingToUpdate.setBcID(fc2wpMappingUpdated.getBcID());
    fc2wpMappingToUpdate.setFcwpVerId(fc2wpMappingUpdated.getFcwpVerId());
    fc2wpMappingToUpdate.setFunctionDesc(fc2wpMappingUpdated.getFunctionDesc());
    fc2wpMappingToUpdate.setFunctionId(fc2wpMappingUpdated.getFunctionId());
    fc2wpMappingToUpdate.setId(fc2wpMappingUpdated.getId());
    fc2wpMappingToUpdate.setPtTypeSet(fc2wpMappingUpdated.getPtTypeSet());
    fc2wpMappingToUpdate.setUsedInIcdm(fc2wpMappingUpdated.isUsedInIcdm());
    fc2wpMappingToUpdate.setVersion(fc2wpMappingUpdated.getVersion());
  }

  /**
   *
   */
  private void setValuesIfNoDefaultWP() {
    EditFC2WPMappingDialog.this.firstContactSearch.setEnabled(true);
    EditFC2WPMappingDialog.this.secondContactSearch.setEnabled(true);
    EditFC2WPMappingDialog.this.firstContactText.setEnabled(true);
    EditFC2WPMappingDialog.this.useWpDefcheckBox.setSelection(false);
    EditFC2WPMappingDialog.this.secondContactText.setEnabled(true);
    EditFC2WPMappingDialog.this.firstContactText.setText("");
    EditFC2WPMappingDialog.this.secondContactText.setText("");
    if (EditFC2WPMappingDialog.this.isMultipleUpdate && !EditFC2WPMappingDialog.this.sameFirstContact) {
      if (this.sameWorkPkg && (this.useCurrWpDef == null)) {
        EditFC2WPMappingDialog.this.firstContactText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      }
    }
    else {
      setFirstCntctFromFc2wpMapping();
    }

    if (EditFC2WPMappingDialog.this.isMultipleUpdate && !EditFC2WPMappingDialog.this.sameSecondContact) {
      if (this.sameWorkPkg && (this.useCurrWpDef == null)) {
        EditFC2WPMappingDialog.this.secondContactText.setText(CommonUIConstants.DISP_TEXT_USE_CUR_VAL);
      }
    }
    else {
      setSecondCntctFrmFc2WpMapping();
    }
    this.delfirstContact.setEnabled((null != this.firstContactText) && !this.firstContactText.getText().isEmpty());
    this.delSecondContact.setEnabled((null != this.secondContactText) && !this.secondContactText.getText().isEmpty());

  }

}
