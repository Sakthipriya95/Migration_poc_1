/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.AddLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.EditLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.sorter.LinkTableSorter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.ui.views.providers.LinkTableLabelProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.Region;
import com.bosch.caltool.icdm.model.wp.WPResourceDetails;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.sorters.WPDivCdlTableSorter;
import com.bosch.caltool.icdm.ui.sorters.WorkPackageDetailsWrapper;
import com.bosch.caltool.icdm.ui.views.providers.WPDivCdlTableLabelProvider;
import com.bosch.caltool.icdm.ui.wizards.pages.FCWorkPackageCreationWizPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RegionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivServiceCdlClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageResourceServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextBoxContentDisplay;

/**
 * @author apj4cob
 */
public class WPDivDetailsDialog extends AbstractDialog {

  /** Width of main dialog box. */
  private static final int DIALOG_WIDTH = 937;
  /** Width of main dialog box. */
  private static final int DIALOG_HEIGHT = 600;
  /** The folder. */
  private TabFolder folder;
  /** The section one. */
  private Section sectionOne;
  /** The section two. */
  private Section sectionTwo;
  /** The section three. */
  private Section sectionThree;
  /** The section three. */
  private Section complianceSection;
  /** The form one. */
  private Form formOne;
  /** The form one. */
  private Form formTwo;
  /** The form three. */
  private Form formThree;
  /** The form three. */
  private Form complianceForm;
  /** The form toolkit. */
  private FormToolkit formToolkit;
  /**
   * Button instance for save
   */
  private Button saveBtn;
  /**
   * /** Flag to indicate Add / Edit operation
   */
  private final boolean addFlag;
  private Combo divisionCombo;
  private Combo resCombo;
  private Text primaryContactText;
  private Text secContactText;
  private final FCWorkPackageCreationWizPage wpCreationPage;
  /**
   * Primary contact for workpackage division
   */
  protected User selectedprimaryUser;
  /**
   * Secondary contact for workpackage division
   */

  protected User selectedSecUser;
  private SortedSet<WPResourceDetails> resSet;
  private static final String SELC_USER = "Select User";
  private Text wpIdText;
  private final WorkPkg selWP;
  private Text comment;
  private Action editLinkAction;
  private Action actnDeleteLink;
  private Action editCdlAction;
  private Action actnDeleteCdl;
  private Action actnCdl;
  private GridTableViewer cdlTabViewer;
  private GridTableViewer linkTabViewer;
  private final List<WorkpackageDivisionCdl> delCdlList = new ArrayList<>();
  private final List<WorkpackageDivisionCdl> addCdlList = new ArrayList<>();
  private final List<WorkpackageDivisionCdl> editCdlList = new ArrayList<>();
  private final List<Long> uniqueRegionList = new ArrayList<>();
  private Map<Long, User> userMap = new HashMap<>();
  private final Map<Long, Region> regionMap = new HashMap<>();
  /**
   * Sorter instance
   */
  private LinkTableSorter linksTabSorter;
  private AbstractViewerSorter cdlTabSorter;
  /**
   * CONSTANT FOR "null,null"
   */
  private static final String STR_NULL_NULL = "null,null";
  private static final int FORM_COL_COUNT = 3;
  private static final int COMMENT_TEXT_SIZE = 4000;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  private Map<Long, AttributeValue> attrValues;
  private TabItem tab1;

  /** iccRelevant CheckBox */
  private Button iccRelevantCheckBox;
  /** variable to hold the value of division attribute value */
  private String divAttrValId = "";

  private Button crpObdRelevantCheckBox;
  private Text crpObdCommentText;
  private Button crpEmissionRelevantCheckBox;
  private Text crpEmissionCommentText;
  private Button crpSoundRelevantCheckBox;
  private Text crpSoundCommentText;

  /**
   * @param parentShell Shell
   * @param wpCreationPage FCWorkPackageCreationWizPage
   * @param selWP WorkPkg
   * @param addFlag boolean
   */
  public WPDivDetailsDialog(final Shell parentShell, final FCWorkPackageCreationWizPage wpCreationPage,
      final WorkPkg selWP, final boolean addFlag) {
    super(parentShell);
    this.wpCreationPage = wpCreationPage;
    this.selWP = selWP;
    this.addFlag = addFlag;
    /** if it is not add scenario, fetch the division attribute value of selected division */
    if (!addFlag) {
      this.divAttrValId = wpCreationPage.getSelWPDiv().getWorkPkgDetails().getDivAttrValId().toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Title modified
    newShell.setText("Work Package Details");
    // Width modified
    newShell.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * Creates the dialog's contents.
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {

    final Composite composite = new Composite(parent, SWT.None);
    initializeDialogUnits(composite);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    parent.setLayout(gridLayout);
    parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.folder = new TabFolder(composite, SWT.NONE);
    this.folder.setLayout(gridLayout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.folder.setLayoutData(gridData);
    this.tab1 = new TabItem(this.folder, SWT.NONE);
    this.tab1.setText("WP Details");
    createSectionOne(getFormToolkit());
    this.tab1.setControl(this.sectionOne);
    // Compliance Tab
    TabItem complianceTab = new TabItem(this.folder, SWT.NONE);
    complianceTab.setText("Compliance");
    createComplianceSection(getFormToolkit());
    complianceTab.setControl(this.complianceSection);
    // Calibration Domain Lead Tab
    TabItem tab2 = new TabItem(this.folder, SWT.NONE);
    tab2.setText("Calibration Domain Lead");
    createSectionTwo(getFormToolkit(), gridLayout);
    tab2.setControl(this.sectionTwo);
    // Links Tab
    TabItem tab3 = new TabItem(this.folder, SWT.NONE);
    tab3.setText("Links");
    createSectionThree(getFormToolkit(), gridLayout);
    tab3.setControl(this.sectionThree);
    createButtonBar(parent);
    return composite;
  }


  /**
   * This method initializes formToolkit.
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Creates the section one.
   *
   * @param toolkit This method initializes sectionOne
   */
  private void createSectionOne(final FormToolkit toolkit) {

    this.sectionOne = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOne.setText(" ");
    this.sectionOne.setDescription("");
    this.sectionOne.setExpanded(true);
    this.sectionOne.getDescriptionControl().setEnabled(false);
    createFormOne(toolkit);
    this.sectionOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    this.sectionOne.setClient(this.formOne);

  }

  /**
   * Creates the compliance section.
   *
   * @param toolkit This method initializes compliance section
   * @param gridLayout the grid layout
   */
  private void createComplianceSection(final FormToolkit toolkit) {
    this.complianceSection = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.complianceSection.setText("");
    this.complianceSection.setDescription("");
    this.complianceSection.setExpanded(true);
    this.complianceSection.getDescriptionControl().setEnabled(false);
    createComplianceForm(toolkit);
    this.complianceSection.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    this.complianceSection.setClient(this.complianceForm);
  }

  /**
   * Creates the section two.
   *
   * @param toolkit This method initializes sectionTwo
   * @param gridLayout the grid layout
   */
  private void createSectionTwo(final FormToolkit toolkit, final GridLayout gridLayout) {

    this.sectionTwo = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionTwo.setText("Calibration Domain Lead ");
    this.sectionTwo.setDescription(" ");
    this.sectionTwo.setExpanded(true);
    this.sectionTwo.getDescriptionControl().setEnabled(false);
    this.sectionTwo.setLayout(gridLayout);
    this.sectionTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());

    createFormTwo(toolkit, gridLayout);

    this.sectionTwo.setClient(this.formTwo);

  }

  /**
   * Creates the section three.
   *
   * @param toolkit the toolkit
   * @param gridLayout the grid layout
   */
  private void createSectionThree(final FormToolkit toolkit, final GridLayout gridLayout) {
    this.sectionThree = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionThree.setText("Links");
    this.sectionThree.setDescription("");
    this.sectionThree.setExpanded(true);
    this.sectionThree.getDescriptionControl().setEnabled(false);
    this.sectionThree.setLayout(gridLayout);
    this.sectionThree.setLayoutData(getGridDataWithOutGrabExcessVSpace());

    createFormThree(toolkit, gridLayout);

    this.sectionThree.setClient(this.formThree);

  }

  private void createLinkToolBarAction(final Section sectnThree) {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(sectnThree);

    // create add action
    Action actnLink = new Action("Add Link") {

      @Override
      public void run() {
        AddLinkDialog addLink =
            new AddLinkDialog(Display.getCurrent().getActiveShell(), WPDivDetailsDialog.this.linkTabViewer, true);
        addLink.open();
      }
    };
    // Image for add action
    actnLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(actnLink);
    actnLink.setEnabled(true);
    // create edit action
    this.editLinkAction = new Action("Edit Link") {

      @Override
      public void run() {
        final IStructuredSelection selection =
            (IStructuredSelection) WPDivDetailsDialog.this.linkTabViewer.getSelection();
        if ((null != selection) && !selection.isEmpty()) {
          final LinkData linkData = (LinkData) selection.getFirstElement();
          EditLinkDialog editLink = new EditLinkDialog(Display.getCurrent().getActiveShell(), linkData,
              WPDivDetailsDialog.this.linkTabViewer, true);

          editLink.open();
        }
      }
    };
    // Image for add action
    this.editLinkAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarManager.add(this.editLinkAction);
    this.editLinkAction.setEnabled(false);
    // create delete action
    this.actnDeleteLink = new Action("Delete Link") {

      @Override
      public void run() {
        IStructuredSelection selection = (IStructuredSelection) WPDivDetailsDialog.this.linkTabViewer.getSelection();
        if ((null != selection) && !selection.isEmpty()) {
          // ICDM-1502
          LinkData linkData = (LinkData) selection.getFirstElement();

          @SuppressWarnings("unchecked")
          SortedSet<LinkData> input = (SortedSet<LinkData>) WPDivDetailsDialog.this.linkTabViewer.getInput();

          linkData.setOprType(CommonUIConstants.CHAR_CONSTANT_FOR_DELETE);

          WPDivDetailsDialog.this.linkTabViewer.setInput(input);// to invoke input changed
          WPDivDetailsDialog.this.linkTabViewer.refresh();

          WPDivDetailsDialog.this.linkTabViewer.setSelection(null);
        }
      }
    };
    // Image for add action
    this.actnDeleteLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    toolBarManager.add(this.actnDeleteLink);
    this.actnDeleteLink.setEnabled(false);
    toolBarManager.update(true);
    sectnThree.setTextClient(toolbar);
  }

  /**
   * Gets the grid data with out grab excess V space.
   *
   * @return the grid data with out grab excess V space
   */
  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }

  /**
   * This method initializes formOne.
   *
   * @param toolkit the toolkit
   * @param gridLayout the grid layout
   */
  private void createFormOne(final FormToolkit toolkit) {
    this.formOne = toolkit.createForm(this.sectionOne);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = FORM_COL_COUNT;
    this.formOne.getBody().setLayout(gridLayout);

    this.formOne.getBody().setLayoutData(getGridDataWithOutGrabExcessVSpace());
    createControlsForWPDet();
    prePopulateValues();
  }

  /**
   * This method initializes compliance form.
   *
   * @param toolkit the toolkit
   * @param gridLayout the grid layout
   */
  private void createComplianceForm(final FormToolkit toolkit) {
    this.complianceForm = toolkit.createForm(this.complianceSection);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.complianceForm.getBody().setLayout(gridLayout);
    this.complianceForm.getBody().setLayoutData(getGridDataWithOutGrabExcessVSpace());
    createControlsForComplianceForm();
    prePopulateComplianceValues();
  }

  /**
  *
  */
  protected void createControlsForWPDet() {
    GridData gridData = getGridData();
    createWPNameControl(gridData);
    new Label(this.formOne.getBody(), SWT.NONE).setText("");
    createDivComboControl();
    new Label(this.formOne.getBody(), SWT.NONE).setText("");
    createResComboControl();
    createWPIDControl(gridData);
    createPrimContactCtrl(gridData);
    createSecContactCtrl(gridData);
    createSecIccRelevantData(gridData);
    createCmntCtrl(gridData);
  }

  /**
   * Create compliance form controls
   */
  protected void createControlsForComplianceForm() {
    GridData gridData = getGridData();
    createCrpObdRelevantControl(gridData);
    createCrpObdCommentControl(gridData);
    createCrpEmissionRelevantControl(gridData);
    createCrpEmissionCommentControl(gridData);
    createCrpSoundRelevantControl(gridData);
    createCrpSoundCommentControl(gridData);
  }

  private void createCrpObdRelevantControl(final GridData gridData) {
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = false;
    LabelUtil.getInstance().createLabel(this.complianceForm.getBody(), "Relevant for CRP OBD: ");

    this.crpObdRelevantCheckBox = new Button(this.complianceForm.getBody(), SWT.CHECK);
    this.crpObdRelevantCheckBox.setLayoutData(gridData);
    this.crpObdRelevantCheckBox.setEnabled(true);

    this.crpObdRelevantCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
  }

  private void createCrpObdCommentControl(final GridData gridData) {
    getFormToolkit().createLabel(this.complianceForm.getBody(), "Comment CRP OBD: ");
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.complianceForm.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, COMMENT_TEXT_SIZE, gridData);
    this.crpObdCommentText = textBoxContentDisplay.getText();
    final GridData gDForCmt = new GridData();
    gDForCmt.verticalSpan = 15;
    gDForCmt.heightHint = 15;
    gDForCmt.verticalAlignment = GridData.FILL;
    gDForCmt.horizontalAlignment = GridData.FILL;
    gDForCmt.grabExcessHorizontalSpace = true;
    gDForCmt.grabExcessVerticalSpace = true;
    this.crpObdCommentText.setLayoutData(gDForCmt);
    this.crpObdCommentText.addModifyListener(evnt -> checkSaveBtnEnable());
  }

  private void createCrpEmissionRelevantControl(final GridData gridData) {
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = false;
    LabelUtil.getInstance().createLabel(this.complianceForm.getBody(), "");
    LabelUtil.getInstance().createLabel(this.complianceForm.getBody(), "Relevant for CRP Emission: ");

    this.crpEmissionRelevantCheckBox = new Button(this.complianceForm.getBody(), SWT.CHECK);
    this.crpEmissionRelevantCheckBox.setLayoutData(gridData);
    this.crpEmissionRelevantCheckBox.setEnabled(true);

    this.crpEmissionRelevantCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
  }

  private void createCrpEmissionCommentControl(final GridData gridData) {
    getFormToolkit().createLabel(this.complianceForm.getBody(), "Comment CRP Emission: ");
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.complianceForm.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, COMMENT_TEXT_SIZE, gridData);
    this.crpEmissionCommentText = textBoxContentDisplay.getText();
    final GridData gDForCmt = new GridData();
    gDForCmt.verticalSpan = 15;
    gDForCmt.heightHint = 15;
    gDForCmt.verticalAlignment = GridData.FILL;
    gDForCmt.horizontalAlignment = GridData.FILL;
    gDForCmt.grabExcessHorizontalSpace = true;
    gDForCmt.grabExcessVerticalSpace = true;
    this.crpEmissionCommentText.setLayoutData(gDForCmt);
    this.crpEmissionCommentText.addModifyListener(evnt -> checkSaveBtnEnable());
  }

  private void createCrpSoundRelevantControl(final GridData gridData) {
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = false;
    LabelUtil.getInstance().createLabel(this.complianceForm.getBody(), "");
    LabelUtil.getInstance().createLabel(this.complianceForm.getBody(), "Relevant for CRP Sound: ");

    this.crpSoundRelevantCheckBox = new Button(this.complianceForm.getBody(), SWT.CHECK);
    this.crpSoundRelevantCheckBox.setLayoutData(gridData);
    this.crpSoundRelevantCheckBox.setEnabled(true);

    this.crpSoundRelevantCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
  }

  private void createCrpSoundCommentControl(final GridData gridData) {
    getFormToolkit().createLabel(this.complianceForm.getBody(), "Comment CRP Sound: ");
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.complianceForm.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, COMMENT_TEXT_SIZE, gridData);
    this.crpSoundCommentText = textBoxContentDisplay.getText();
    final GridData gDForCmt = new GridData();
    gDForCmt.verticalSpan = 15;
    gDForCmt.heightHint = 15;
    gDForCmt.verticalAlignment = GridData.FILL;
    gDForCmt.horizontalAlignment = GridData.FILL;
    gDForCmt.grabExcessHorizontalSpace = true;
    gDForCmt.grabExcessVerticalSpace = true;
    this.crpSoundCommentText.setLayoutData(gDForCmt);
    this.crpSoundCommentText.addModifyListener(evnt -> checkSaveBtnEnable());
  }

  /**
   * @param gridData
   */
  private void createCmntCtrl(final GridData gridData) {
    getFormToolkit().createLabel(this.formOne.getBody(), "Comment:");
    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.formOne.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, COMMENT_TEXT_SIZE, gridData);
    this.comment = textBoxContentDisplay.getText();
    final GridData gDForCmt = new GridData();
    gDForCmt.verticalSpan = 25;
    gDForCmt.verticalAlignment = GridData.FILL;
    gDForCmt.grabExcessHorizontalSpace = true;
    gDForCmt.horizontalAlignment = GridData.FILL;
    gDForCmt.heightHint = 10;
    this.comment.setLayoutData(gDForCmt);
    this.comment.addModifyListener(evnt -> {
      if (null != this.saveBtn) {
        this.saveBtn.setEnabled(validateTextFields());
      }
    });
  }

  /**
   * @param gridData
   */
  private void createSecContactCtrl(final GridData gridData) {
    getFormToolkit().createLabel(this.formOne.getBody(), "Secondary Contact :");
    this.secContactText = getFormToolkit().createText(this.formOne.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.secContactText.setLayoutData(gridData);
    this.secContactText.setEditable(false);
    final Button secCtBrowse = new Button(this.formOne.getBody(), SWT.PUSH);
    secCtBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    secCtBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    secCtBrowse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(), SELC_USER,
            "Select Primary contact", SELC_USER, "Select", false, false);
        userDialog.setSelectedUser(null);
        userDialog.setAddDummyUser(true);
        userDialog.open();
        WPDivDetailsDialog.this.selectedSecUser = userDialog.getSelectedUser();
        if (WPDivDetailsDialog.this.selectedSecUser != null) {
          final String selUserName = WPDivDetailsDialog.this.selectedSecUser.getLastName().concat(", ")
              .concat(WPDivDetailsDialog.this.selectedSecUser.getFirstName())
              .concat(WPDivDetailsDialog.this.selectedSecUser.getName());
          if (!STR_NULL_NULL.equalsIgnoreCase(selUserName)) {
            WPDivDetailsDialog.this.secContactText.setText(WPDivDetailsDialog.this.selectedSecUser.getDescription());
            checkSaveBtnEnable();
          }
        }
      }
    });
  }


  /**
   * @param gridData
   */
  private void createSecIccRelevantData(final GridData gridData) {

    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = false;
    LabelUtil.getInstance().createLabel(this.formOne.getBody(), "ICC relevant");

    this.iccRelevantCheckBox = new Button(this.formOne.getBody(), SWT.CHECK);
    this.iccRelevantCheckBox.setLayoutData(gridData);
    this.iccRelevantCheckBox.setEnabled(false);
    // For edit scenario, check if the selected division is BEG. if yes, enable the checkbox
    try {
      if (!this.addFlag && !(CommonUtils.isEmptyString(this.divAttrValId)) &&
          this.divAttrValId.equals(new CommonDataBO().getParameterValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID))) {
        this.iccRelevantCheckBox.setEnabled(true);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    getFormToolkit().createLabel(this.formOne.getBody(), " ");
    this.iccRelevantCheckBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });

  }

  /**
   * @param gridData
   */
  private void createPrimContactCtrl(final GridData gridData) {
    getFormToolkit().createLabel(this.formOne.getBody(), "Primary Contact :");
    this.primaryContactText = getFormToolkit().createText(this.formOne.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.primaryContactText.setLayoutData(gridData);
    this.primaryContactText.setEditable(false);
    final Button primCtBrowse = new Button(this.formOne.getBody(), SWT.PUSH);
    primCtBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    primCtBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    primCtBrowse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog userDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(), SELC_USER,
            "Select Primary contact", SELC_USER, "Select", false, false);
        userDialog.setSelectedUser(null);
        userDialog.setAddDummyUser(true);
        userDialog.open();
        WPDivDetailsDialog.this.selectedprimaryUser = userDialog.getSelectedUser();
        if (WPDivDetailsDialog.this.selectedprimaryUser != null) {
          final String selUserName = WPDivDetailsDialog.this.selectedprimaryUser.getLastName().concat(", ")
              .concat(WPDivDetailsDialog.this.selectedprimaryUser.getFirstName())
              .concat(WPDivDetailsDialog.this.selectedprimaryUser.getName());
          if (!STR_NULL_NULL.equalsIgnoreCase(selUserName)) {
            WPDivDetailsDialog.this.primaryContactText
                .setText(WPDivDetailsDialog.this.selectedprimaryUser.getDescription());
            checkSaveBtnEnable();
          }
        }
      }
    });
  }

  /**
   * @param gridData
   */
  private void createWPIDControl(final GridData gridData) {
    getFormToolkit().createLabel(this.formOne.getBody(), "WP-ID (MCR) :");
    this.wpIdText = getFormToolkit().createText(this.formOne.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.wpIdText.setLayoutData(gridData);
    new Label(this.formOne.getBody(), SWT.NONE).setText("");
    this.wpIdText.addModifyListener(evnt -> {
      if (null != this.saveBtn) {
        this.saveBtn.setEnabled(validateTextFields());
      }
    });
  }

  /**
   *
   */
  private void createResComboControl() {
    getFormToolkit().createLabel(this.formOne.getBody(), "Resource :");
    this.resCombo = new Combo(this.formOne.getBody(), SWT.READ_ONLY);
    this.resCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    new Label(this.formOne.getBody(), SWT.NONE).setText("");
    this.resCombo.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    this.resCombo.select(0);
    this.resSet = invokeWebserviceToFetchResources();
    if (null != this.resSet) {
      for (WPResourceDetails wpResourceDetails : this.resSet) {
        this.resCombo.add(wpResourceDetails.getWpResCode());
      }
    }
    this.resCombo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        checkSaveBtnEnable();
      }
    });
  }

  /**
   *
   */
  private void createDivComboControl() {
    ControlDecoration comboValTypeDec;
    new Label(this.formOne.getBody(), SWT.NONE).setText("Division : ");
    this.divisionCombo = new Combo(this.formOne.getBody(), SWT.READ_ONLY);
    this.divisionCombo.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.divisionCombo.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    this.divisionCombo.select(0);
    // get the qnaire config attribute
    CommonDataBO dataBO = new CommonDataBO();
    Long attrId;
    this.attrValues = new HashMap<>();
    try {
      attrId = Long.valueOf(dataBO.getParameterValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR));
      Map<Long, Map<Long, AttributeValue>> attrValMap = new AttributeValueServiceClient().getValuesByAttribute(attrId);
      this.attrValues = attrValMap.get(attrId);
      for (AttributeValue attrVal : this.attrValues.values()) {
        this.divisionCombo.add(attrVal.getName());
      }
    }
    catch (NumberFormatException | ApicWebServiceException ex) {
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

    this.divisionCombo.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {
        String divItem = WPDivDetailsDialog.this.divisionCombo
            .getItem(WPDivDetailsDialog.this.divisionCombo.getSelectionIndex()).trim();
        WPDivDetailsDialog.this.iccRelevantCheckBox.setEnabled(false);
        WPDivDetailsDialog.this.iccRelevantCheckBox.setSelection(false);
        for (AttributeValue attrVal : WPDivDetailsDialog.this.attrValues.values()) {
          try {
            if (attrVal.getName().equals(divItem) && attrVal.getId().toString()
                .equals(new CommonDataBO().getParameterValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID))) {
              WPDivDetailsDialog.this.iccRelevantCheckBox.setEnabled(true);
              break;
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
        checkSaveBtnEnable();
      }
    });


    comboValTypeDec = new ControlDecoration(this.divisionCombo, SWT.LEFT);
    this.decorators.showReqdDecoration(comboValTypeDec, IUtilityConstants.MANDATORY_MSG);
  }

  /**
   * @param gridData
   */
  private void createWPNameControl(final GridData gridData) {
    Text workPackage;
    getFormToolkit().createLabel(this.formOne.getBody(), "Work Package :");
    workPackage = getFormToolkit().createText(this.formOne.getBody(), null, SWT.SINGLE | SWT.BORDER);
    workPackage.setLayoutData(gridData);
    workPackage.setText(this.selWP.getName());
    workPackage.setEditable(false);
    workPackage.addModifyListener(evnt -> {
      if (null != this.saveBtn) {
        this.saveBtn.setEnabled(validateTextFields());
      }
    });
  }

  /**
   * @return
   */
  private SortedSet<WPResourceDetails> invokeWebserviceToFetchResources() {
    WorkPackageResourceServiceClient rClient = new WorkPackageResourceServiceClient();
    SortedSet<WPResourceDetails> resurceSet = null;
    try {
      Set<WPResourceDetails> resourceSet = rClient.getAllWpRes();
      resurceSet = new TreeSet<>(resourceSet);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog("Error while fetching resources :" + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }
    return resurceSet;
  }

  /**
  *
  */
  private void prePopulateValues() {
    if (!this.addFlag) {
      if (null != this.wpCreationPage.getSelWPDiv().getDivisionName()) {
        this.divisionCombo.select(this.divisionCombo.indexOf(this.wpCreationPage.getSelWPDiv().getDivisionName()));
      }
      this.divisionCombo.setEnabled(false);
      if (null != this.wpCreationPage.getSelWPDiv().getResourceName()) {
        this.resCombo.select(this.resCombo.indexOf(this.wpCreationPage.getSelWPDiv().getResourceName()));
      }

      this.wpIdText.setText(
          null == this.wpCreationPage.getSelWPDiv().getMCR() ? "" : this.wpCreationPage.getSelWPDiv().getMCR());

      this.primaryContactText.setText(this.wpCreationPage.getSelWPDiv().getPrimaryContact());
      this.secContactText.setText(this.wpCreationPage.getSelWPDiv().getSecondaryContact());
      if (this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getWpdComment() != null) {
        this.comment.setText(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getWpdComment());
      }
      this.iccRelevantCheckBox.setSelection(
          CommonUtils.isEqualIgnoreCase(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getIccRelevantFlag(),
              CommonUIConstants.ICC_RELEVANT_Y));
    }

  }

  private void prePopulateComplianceValues() {
    if (!this.addFlag) {
      this.crpObdRelevantCheckBox.setSelection(
          CommonUtils.getBooleanType(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpObdRelevantFlag()));
      if (CommonUtils.isNotNull(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpObdComment())) {
        this.crpObdCommentText.setText(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpObdComment());
      }
      this.crpEmissionRelevantCheckBox.setSelection(CommonUtils
          .getBooleanType(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpEmissionRelevantFlag()));
      if (CommonUtils.isNotNull(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpEmissionComment())) {
        this.crpEmissionCommentText
            .setText(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpEmissionComment());
      }
      this.crpSoundRelevantCheckBox.setSelection(
          CommonUtils.getBooleanType(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpSoundRelevantFlag()));
      if (CommonUtils.isNotNull(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpSoundComment())) {
        this.crpSoundCommentText.setText(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getCrpSoundComment());
      }
    }
  }

  /**
   * @return the cdlTabViewer
   */
  public GridTableViewer getCdlTabViewer() {
    return this.cdlTabViewer;
  }


  /**
   * @param cdlTabViewer the cdlTabViewer to set
   */
  public void setCdlTabViewer(final GridTableViewer cdlTabViewer) {
    this.cdlTabViewer = cdlTabViewer;
  }

  /**
   * @return the linkTabViewer
   */
  public GridTableViewer getLinkTabViewer() {
    return this.linkTabViewer;
  }

  /**
   * @return GridData of text field
   */
  protected GridData getGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * @return the editLinkAction
   */
  public Action getEditLinkAction() {
    return this.editLinkAction;
  }


  /**
   * @param editLinkAction the editLinkAction to set
   */
  public void setEditLinkAction(final Action editLinkAction) {
    this.editLinkAction = editLinkAction;
  }


  /**
   * @return the delCdlSet
   */
  public List<WorkpackageDivisionCdl> getDelCdlSet() {
    return this.delCdlList;
  }


  /**
   * @return List<WorkpackageDivisionCdl>
   */
  public List<WorkpackageDivisionCdl> getEditCdlList() {
    return this.editCdlList;
  }


  /**
   * @return the addCdlList
   */
  public List<WorkpackageDivisionCdl> getAddCdlList() {
    return this.addCdlList;
  }


  /**
   * @return List<Long>
   */
  public List<Long> getUniqueRegionList() {
    return this.uniqueRegionList;
  }


  /**
   * @return Map<Long, ApicUser>
   */
  public Map<Long, User> getUserMap() {
    return this.userMap;
  }


  /**
   * @param userMap Map<Long, ApicUser>
   */
  public void setUserMap(final Map<Long, User> userMap) {
    this.userMap = userMap;
  }


  /**
   * Checks if the save button should be enabled
   */
  private void checkSaveBtnEnable() {
    if (null != this.saveBtn) {
      this.saveBtn.setEnabled(validateTextFields());
    }
  }

  /**
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {

    String divItem = this.divisionCombo.getItem(this.divisionCombo.getSelectionIndex()).trim();

    return !"".equals(divItem) && !ApicConstants.DEFAULT_COMBO_SELECT.equals(divItem);

  }


  /**
   * This method initializes formTwo.
   *
   * @param toolkit the toolkit
   */
  private void createFormTwo(final FormToolkit toolkit, final GridLayout gridLayout) {
    this.cdlTabSorter = new WPDivCdlTableSorter(this);
    this.formTwo = toolkit.createForm(this.sectionTwo);
    createCdlToolBarAction(this.sectionTwo);
    this.formTwo.getBody().setLayout(gridLayout);
    this.formTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    // create cdl table
    this.cdlTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formTwo.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(100));
    createCdlColumn();
    this.cdlTabViewer.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

        if (null != WPDivDetailsDialog.this.saveBtn) {
          WPDivDetailsDialog.this.saveBtn.setEnabled(true);
        }
      }

      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof List<?>) {
          return ((List<?>) inputElement).toArray();
        }
        return (Object[]) inputElement;
      }
    });

    this.cdlTabViewer.setLabelProvider(new WPDivCdlTableLabelProvider(this));
    // Invoke TableViewer Column sorters
    invokeCdlColumnSorter(this.cdlTabSorter);
    addCdlTableSelectionListener();
    // get all region
    getAllRegionWS();
    if (!this.addFlag) {
      setExistingCdlValues();
    }
  }

  /**
   *
   */
  private void addCdlTableSelectionListener() {
    this.cdlTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection =
            (IStructuredSelection) WPDivDetailsDialog.this.cdlTabViewer.getSelection();
        if ((null != selection) && !selection.isEmpty()) {
          if (WPDivDetailsDialog.this.delCdlList.contains(selection.getFirstElement())) {
            WPDivDetailsDialog.this.actnDeleteCdl.setEnabled(false);
            WPDivDetailsDialog.this.editCdlAction.setEnabled(false);
          }
          else {
            WPDivDetailsDialog.this.actnCdl.setEnabled(true);
            WPDivDetailsDialog.this.actnDeleteCdl.setEnabled(true);
            WPDivDetailsDialog.this.editCdlAction.setEnabled(true);
          }
        }
      }
    });
  }

  /**
  *
  */
  private void addLinkTableSelectionListener() {
    this.linkTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection =
            (IStructuredSelection) WPDivDetailsDialog.this.linkTabViewer.getSelection();
        if ((null != selection) && !selection.isEmpty()) {
          if (((LinkData) selection.getFirstElement()).getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
            WPDivDetailsDialog.this.actnDeleteLink.setEnabled(false);
            WPDivDetailsDialog.this.editLinkAction.setEnabled(false);
          }
          else {
            WPDivDetailsDialog.this.actnDeleteLink.setEnabled(true);
            WPDivDetailsDialog.this.editLinkAction.setEnabled(true);
            WPDivDetailsDialog.this.actnDeleteLink.setEnabled(true);
          }
        }
      }
    });
  }

  /**
   *
   */
  private void setExistingCdlValues() {
    try {
      Set<WorkpackageDivisionCdl> cdlSet = new WorkPackageDivServiceCdlClient()
          .getCdlByDivId(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getId());
      List<WorkpackageDivisionCdl> cdlList = new ArrayList<>();
      for (WorkpackageDivisionCdl obj : cdlSet) {
        cdlList.add(obj);
        this.userMap.put(obj.getUserId(), new UserServiceClient().getApicUserById(obj.getUserId()));
        this.uniqueRegionList.add(obj.getRegionId());
      }
      this.cdlTabViewer.setInput(cdlList);
      this.cdlTabViewer.refresh();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * get all region
   */
  private void getAllRegionWS() {
    RegionServiceClient client = new RegionServiceClient();
    try {
      Map<String, Region> tempRegionMap = client.getAllRegion();
      for (Region reg : tempRegionMap.values()) {
        this.regionMap.put(reg.getId(), reg);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog("Error while fetching region :" + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  private void setExistingLinkValues() {
    SortedSet<LinkData> linkDataCollection = new TreeSet<>();
    LinkServiceClient linkServiceClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkServiceClient.getNodesWithLink(MODEL_TYPE.WORKPACKAGE_DIVISION);
      boolean hasLinks = nodesWithLink.contains(this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode = null;
        allLinksByNode = linkServiceClient.getAllLinksByNode(
            this.wpCreationPage.getSelWPDiv().getWorkPkgDetails().getId(), MODEL_TYPE.WORKPACKAGE_DIVISION);
        for (Link link : allLinksByNode.values()) {
          linkDataCollection.add(new LinkData(link));
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.linkTabViewer.setInput(linkDataCollection);
    this.linkTabViewer.refresh();
  }


  private void createCdlColumn() {
    // Region Column
    final GridViewerColumn regCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.cdlTabViewer, "Region", 200);
    regCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(regCol.getColumn(), 0,
        this.cdlTabSorter, this.cdlTabViewer));
    // Calibration Domain Lead Column
    final GridViewerColumn cdlCol = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.cdlTabViewer,
        "Calibration Domain Lead (CDL)", 400);
    cdlCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(cdlCol.getColumn(), 1,
        this.cdlTabSorter, this.cdlTabViewer));
  }

  /**
   * @param sectionTwo2
   */
  private void createCdlToolBarAction(final Section sectnTwo) {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(sectnTwo);
    // create add action
    this.actnCdl = new Action("Add Cdl") {

      @Override
      public void run() {
        AddEditWpDivCdlDialog addCdl =
            new AddEditWpDivCdlDialog(Display.getDefault().getActiveShell(), true, WPDivDetailsDialog.this);
        addCdl.open();
      }
    };
    // Image for add action
    this.actnCdl.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(this.actnCdl);
    this.actnCdl.setEnabled(true);
    // create edit action
    this.editCdlAction = new Action("Edit Cdl") {

      @Override
      public void run() {
        final IStructuredSelection selection = WPDivDetailsDialog.this.cdlTabViewer.getStructuredSelection();
        if ((null != selection) && !selection.isEmpty()) {
          AddEditWpDivCdlDialog addCdl =
              new AddEditWpDivCdlDialog(Display.getDefault().getActiveShell(), false, WPDivDetailsDialog.this);
          addCdl.open();
        }
      }
    };
    // Image for add action
    this.editCdlAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarManager.add(this.editCdlAction);
    this.editCdlAction.setEnabled(false);
    // create delete action
    this.actnDeleteCdl = new Action("Delete Cdl") {

      @Override
      public void run() {
        IStructuredSelection selection = WPDivDetailsDialog.this.cdlTabViewer.getStructuredSelection();
        if ((null != selection) && !selection.isEmpty()) {
          WorkpackageDivisionCdl cdlDel = (WorkpackageDivisionCdl) selection.getFirstElement();
          WPDivDetailsDialog.this.delCdlList.add(cdlDel);
          if (WPDivDetailsDialog.this.addCdlList.contains(cdlDel)) {
            WPDivDetailsDialog.this.addCdlList.remove(cdlDel);
          }
          if (WPDivDetailsDialog.this.editCdlList.contains(cdlDel)) {
            WPDivDetailsDialog.this.editCdlList.remove(cdlDel);
          }
          WPDivDetailsDialog.this.cdlTabViewer.setSelection(null);
          WPDivDetailsDialog.this.cdlTabViewer.setInput(WPDivDetailsDialog.this.cdlTabViewer.getInput());
          WPDivDetailsDialog.this.cdlTabViewer.refresh();
        }
      }
    };
    // Image for add action
    this.actnDeleteCdl.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    toolBarManager.add(this.actnDeleteCdl);
    this.actnDeleteCdl.setEnabled(false);
    toolBarManager.update(false);
    this.sectionTwo.setTextClient(toolbar);
  }

  /**
   * Creates the form three.
   *
   * @param toolkit the toolkit
   */
  private void createFormThree(final FormToolkit toolkit, final GridLayout gridLayout) {
    this.linksTabSorter = new LinkTableSorter();
    this.formThree = toolkit.createForm(this.sectionThree);
    createLinkToolBarAction(this.sectionThree);

    this.formThree.getBody().setLayout(gridLayout);
    this.formThree.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    // create link table
    this.linkTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.formThree.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(100));
    createLinkColumn();
    this.linkTabViewer.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (null != WPDivDetailsDialog.this.saveBtn) {
          WPDivDetailsDialog.this.saveBtn.setEnabled(true);
        }
      }

      @Override
      public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof SortedSet<?>) {
          return ((SortedSet<?>) inputElement).toArray();
        }
        return (Object[]) inputElement;
      }
    });

    this.linkTabViewer.setLabelProvider(new LinkTableLabelProvider());
    // Invoke TableViewer Column sorters
    invokeLinkColumnSorter(this.linksTabSorter);

    if (!this.addFlag) {
      setExistingLinkValues();
    }
    addLinkTableSelectionListener();
  }


  /**
   * Add sorter for the table columns
   */
  private void invokeLinkColumnSorter(final AbstractViewerSorter sorter) {
    this.linkTabViewer.setComparator(sorter);
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeCdlColumnSorter(final AbstractViewerSorter sorter) {
    this.cdlTabViewer.setComparator(sorter);
  }

  private void createLinkColumn() {
    // Link Column
    final GridViewerColumn linkColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linkTabViewer, "Link", 200);
    linkColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(linkColumn.getColumn(), 0, this.linksTabSorter, this.linkTabViewer));
    // DescEng Column
    final GridViewerColumn descEngColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linkTabViewer, "Description(Eng)", 154);
    descEngColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descEngColumn.getColumn(), 1, this.linksTabSorter, this.linkTabViewer));
    // DescGer Column
    final GridViewerColumn descGerColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.linkTabViewer, "Description(Ger)", 150);
    descGerColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descGerColumn.getColumn(), 2, this.linksTabSorter, this.linkTabViewer));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean isTextFieldValid = validateTextFields();
    if (this.addFlag && (!isTextFieldValid || validateWpDivDuplication())) {
      showErrorMessageDialog(isTextFieldValid);
    }
    else {
      // Save WP details
      WorkPackageDivision wpDetObj = saveWPDetails();

      if (null != wpDetObj) {
        // Save CDL details
        if (CommonUtils.isNotEmpty(this.addCdlList) || CommonUtils.isNotEmpty(this.editCdlList) ||
            CommonUtils.isNotEmpty(this.delCdlList)) {
          saveWPDivCdl(wpDetObj);
        }
        // Save Link details
        if (CommonUtils.isNotNull(this.linkTabViewer.getInput())) {
          saveWPDivLink(wpDetObj);
        }
      }
      super.okPressed();
    }
  }

  private boolean validateWpDivDuplication() {
    boolean isWpDivInValid = false;
    if (CommonUtils.isNotEmpty(this.wpCreationPage.getWpDivDetails())) {
      isWpDivInValid = this.wpCreationPage.getWpDivDetails().stream().anyMatch(wpDiv -> wpDiv.getDivName()
          .equalsIgnoreCase(this.divisionCombo.getItem(this.divisionCombo.getSelectionIndex()).trim()));
    }
    return isWpDivInValid;
  }

  /**
   * @param isTextFieldValid
   */
  private void showErrorMessageDialog(final boolean isTextFieldValid) {
    if (!isTextFieldValid) {
      this.folder.setSelection(this.tab1);
      MessageDialogUtils.getErrorMessageDialog("WP Details-Division Missing",
          "Please select Division in WP Details tab to proceed with Save operation");
    }
    else {
      MessageDialogUtils.getErrorMessageDialog("WP Details-Division Duplication",
          "The selected division is already mapped to the selected workpackage.");
    }
  }

  /**
   * @param wpDetObj
   */
  @SuppressWarnings("unchecked")
  private void saveWPDivLink(final WorkPackageDivision wpDetObj) {
    CommonUiUtils.getInstance().createMultipleLinkService((SortedSet<LinkData>) this.linkTabViewer.getInput(),
        wpDetObj.getId(), MODEL_TYPE.WORKPACKAGE_DIVISION);
  }

  /**
   * @param wpDetObj
   */
  private void saveWPDivCdl(final WorkPackageDivision wpDetObj) {
    // Delete existing CDLs
    deleteCdl();
    // Edit existing CDLs
    updateCdl();
    // Add new CDLs
    createNewCdl(wpDetObj);
  }

  /**
   *
   */
  private void deleteCdl() {
    for (WorkpackageDivisionCdl updtWpDivCdl : this.delCdlList) {
      if (updtWpDivCdl.getId() != null) {
        // delete
        try {
          new WorkPackageDivServiceCdlClient().delete(updtWpDivCdl.getId());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   *
   */
  private void updateCdl() {
    for (WorkpackageDivisionCdl updtWpDivCdl : this.editCdlList) {
      if (updtWpDivCdl.getId() != null) {
        // update
        try {
          new WorkPackageDivServiceCdlClient().update(updtWpDivCdl);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * @param wpDetObj
   */
  private void createNewCdl(final WorkPackageDivision wpDetObj) {
    for (WorkpackageDivisionCdl newWpDivCdl : this.addCdlList) {
      if (newWpDivCdl.getId() == null) {
        // create
        try {
          newWpDivCdl.setWpDivId(wpDetObj.getId());
          new WorkPackageDivServiceCdlClient().create(newWpDivCdl);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   *
   */
  private WorkPackageDivision saveWPDetails() {
    WorkPackageDivision wpDet = null;
    if (this.addFlag) {
      WorkPackageDivision wpDetails = new WorkPackageDivision();
      setWpDivDetails(wpDetails);


      // Service for WorkpackageDivision create
      try {
        WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();

        wpDet = servClient.create(wpDetails);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog("Error while creating work package details :" + exp.getMessage(), exp,
            Activator.PLUGIN_ID);
      }
    }
    else {


      // Service for WorkpackageDivision update
      try {
        WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();
        WorkPackageDetailsWrapper wpDetWrappr = this.wpCreationPage.getSelWPDiv();
        wpDet = setWpDivDetails(wpDetWrappr.getWorkPkgDetails());
        servClient.update(wpDet);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog("Error while updating work package details :" + exp.getMessage(), exp,
            Activator.PLUGIN_ID);
      }
    }
    return wpDet;
  }

  /**
   * @param wpDetails
   */
  private WorkPackageDivision setWpDivDetails(final WorkPackageDivision wpDetails) {
    String divItem = this.divisionCombo.getItem(this.divisionCombo.getSelectionIndex()).trim();
    String resItem = this.resCombo.getItem(this.resCombo.getSelectionIndex()).trim();
    Long resId = null;
    if (null != this.resSet) {
      for (WPResourceDetails wpResourceDetails : this.resSet) {
        if (wpResourceDetails.getWpResCode().equals(resItem)) {
          resId = wpResourceDetails.getWpResId();
        }
      }
    }
    String mcrText = this.wpIdText.getText();
    Long divAttrValueId = null;
    for (AttributeValue attrVal : this.attrValues.values()) {
      if (attrVal.getName().equals(divItem)) {
        divAttrValueId = attrVal.getId();
        break;
      }
    }
    wpDetails.setWpName(this.selWP.getName());
    wpDetails.setWpId(this.selWP.getId());
    wpDetails.setContactPersonId(
        null == this.selectedprimaryUser ? wpDetails.getContactPersonId() : this.selectedprimaryUser.getId());
    wpDetails.setContactPersonSecondId(
        null == this.selectedSecUser ? wpDetails.getContactPersonSecondId() : this.selectedSecUser.getId());
    wpDetails.setDivAttrValId(divAttrValueId);
    setIccRelevantFlagValue(wpDetails, divAttrValueId);
    wpDetails.setWpResId(resId);
    wpDetails.setWpIdMcr(mcrText);
    wpDetails.setWpdComment(this.comment.getText());
    setComplianceTabData(wpDetails);
    return wpDetails;
  }

  private void setIccRelevantFlagValue(final WorkPackageDivision wpDetails, final Long divAttrValueId) {
    /** set the ICC relevant flag to set the value in the table */
    try {
      String iccRelValFlag =
          this.iccRelevantCheckBox.getSelection() ? CommonUIConstants.ICC_RELEVANT_Y : CommonUIConstants.ICC_RELEVANT_N;
      if ((CommonUtils.isNotEqual(divAttrValueId,
          Long.parseLong((new CommonDataBO().getParameterValue(CommonParamKey.BEG_CAL_PROJ_ATTR_VAL_ID))))) &&
          CommonUtils.isEqual(iccRelValFlag, CommonUIConstants.ICC_RELEVANT_N)) {
        wpDetails.setIccRelevantFlag(null);
      }
      else {
        wpDetails.setIccRelevantFlag(iccRelValFlag);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  private void setComplianceTabData(final WorkPackageDivision wpDetails) {
    wpDetails.setCrpObdRelevantFlag(CommonUtils.getBooleanCode(this.crpObdRelevantCheckBox.getSelection()));
    wpDetails.setCrpObdComment(this.crpObdCommentText.getText());
    wpDetails.setCrpEmissionRelevantFlag(CommonUtils.getBooleanCode(this.crpEmissionRelevantCheckBox.getSelection()));
    wpDetails.setCrpEmissionComment(this.crpEmissionCommentText.getText());
    wpDetails.setCrpSoundRelevantFlag(CommonUtils.getBooleanCode(this.crpSoundRelevantCheckBox.getSelection()));
    wpDetails.setCrpSoundComment(this.crpSoundCommentText.getText());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * @return Map<Long, Region>
   */
  public Map<Long, Region> getRegionMap() {
    return this.regionMap;
  }
}
