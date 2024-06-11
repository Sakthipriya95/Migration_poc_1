/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.table.filters.FocusMatrixToolBarActionSet;
import com.bosch.caltool.apic.ui.table.filters.FocusMatrixToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.PIDCOutlinePage;
import com.bosch.caltool.apic.ui.views.providers.FocusMatrixTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.FocusMatrixTreeViewLabelProvider;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixVersionClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixVersionClientBO.FM_REVIEW_STATUS;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseFavNodesMgr;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.combo.BasicObjectCombo;
import com.bosch.caltool.icdm.common.ui.dialogs.AddLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CalendarDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CommentsDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.EditLinkDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * Fourth Page of PIDCEditor
 */
public class FocusMatrixPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String REMARKS_CONST = "Remarks";
  /**
   * Static cols in the page (4 columns)
   */
  private static final int STATIC_COL_INDEX = 4;
  /**
   * PIDCEditor instance
   */
  private final PIDCEditor editor;
  /**
   * Label instance
   */
  private static final String FOCUS_MATRIX_LABEL = "Focus Matrix";

  /**
   * Composite weight
   */
  private static final int BTM_COMP_WEIGHT2 = 4;

  /**
   * Composite weight
   */
  private static final int BTM_COMP_WEIGHT1 = 2;

  /**
   * Main page columns
   */
  private static final int SASHFORM_COLS = 2;

  /**
   * SashForm instance
   */
  private SashForm mainComposite;

  /**
   * Form instance
   */
  private Form nonScrollableForm;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * ICDM-1596
   */
  private OutlineViewPart outlineViewPart;

  /**
   * Class containing the NATTable section
   */
  private final FocusMatrixPageNATTableSection natTableSection = new FocusMatrixPageNATTableSection(this);

  /**
   * left form
   */
  private ScrolledForm leftForm;
  /**
   * TreeViewer instance
   */
  private TreeViewer ucTreeViewer;

  /**
   * FocusMatrixToolBarActionSet
   */
  private FocusMatrixToolBarActionSet toolBarActionSet;

  /**
   * FocusMatrixToolBarFilters
   */
  private FocusMatrixToolBarFilters toolBarFilters;
  /**
   * Instance of toolbar manager
   */
  private ToolBarManager toolBarManager;
  /**
   * Instance of section
   */
  private Section sectionLeft;
  private ScrolledForm leftRvwForm;
  private Section sectionRvwLeft;
  private Text rvwByTextField;
  private Text rvwonTextField;
  private Text remarkTextField;
  private Button yesOption;
  private Button noOption;
  private Link link;
  private BasicObjectCombo<FocusMatrixVersionClientBO> comboFmVers;
  private Button btnLoadFmVers;
  private Button rvwdByBrowseBtn;
  private Button rvwTimeSelBtn;
  private Button rvwLinkEditBtn;
  private Button rvwRemarkEditBtn;
  private FocusMatrixUseCaseRootNode fmRootNode;
  private SortedSet<FavUseCaseItemNode> projectFavNodes;

  /**
   * Constructor
   *
   * @param editor PIDCEditor
   */
  public FocusMatrixPage(final FormEditor editor) {
    // call the super constructor
    super(editor, FOCUS_MATRIX_LABEL, FOCUS_MATRIX_LABEL);
    this.editor = (PIDCEditor) editor;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FormEditor getEditor() {
    return this.editor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    setPageTitleText();
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());


    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = SASHFORM_COLS;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    // create the main composite
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    this.toolBarFilters = new FocusMatrixToolBarFilters();

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
    createToolBarAction();

    // make the tree viewer as a selection provider
    getSite().setSelectionProvider(this.ucTreeViewer);
  }

  /**
   *
   */
  // ICDM-2612
  private void setPageTitleText() {
    FocusMatrixVersionClientBO focusMatrixVersionClientBO = getSelectedFmVersion();
    if (focusMatrixVersionClientBO != null) {
      this.nonScrollableForm.setText(CommonUtils.concatenate(this.editor.getPartName().replaceAll("&", "&&"), " - ",
          FOCUS_MATRIX_LABEL, " : ", focusMatrixVersionClientBO.getName()));
    }
  }

  /**
   * ICDM-1629
   */
  public void createToolBarAction() {

    // initialise tool bar manager
    this.toolBarManager = new ToolBarManager(SWT.FLAT);


    ToolBar toolbar = this.toolBarManager.createControl(this.natTableSection.getSectionRight());
    this.toolBarActionSet =
        new FocusMatrixToolBarActionSet(this.natTableSection.getUcFilterGridLayer(), getDataHandler());


    final Separator separator = new Separator();
    // show invisible attributes action
    this.toolBarActionSet.showAllAttrAction(this.toolBarManager, this.toolBarFilters);
    // ICDM-1624
    this.toolBarManager.add(separator);
    // show mapped unmapped attribtes actions
    this.toolBarActionSet.showUnMappedAttrAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.showMappedAttrAction(this.toolBarManager, this.toolBarFilters);
    // show focus matrix relevant actions
    this.toolBarActionSet.showRelavantFMAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    // ICDM-2614
    // show focus matrix history
    this.toolBarActionSet.createFocusMatrixHistoryAction(this.toolBarManager,
        getEditorInput().getSelectedPidcVersion());
    this.toolBarManager.update(true);

    this.natTableSection.getSectionRight().setTextClient(toolbar);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * creating the content of the form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // initiate the form tool kit
    this.formToolkit = managedForm.getToolkit();

    createBottomComposites();
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * This method initializes composite
   */
  private void createBottomComposites() {

    createLeftComposite();

    this.natTableSection.createRightComposite();

    this.ucTreeViewer.setSelection(new StructuredSelection(this.fmRootNode), true);

    this.mainComposite.setWeights(new int[] { BTM_COMP_WEIGHT1, BTM_COMP_WEIGHT2 });
  }

  /**
   * This method initializes compositeOne
   */
  private void createLeftComposite() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    SashForm compositeOne = new SashForm(this.mainComposite, SWT.VERTICAL);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    createFmVersionSection(this.formToolkit, compositeOne);
    createLeftTreeSection(this.formToolkit, gridLayout, compositeOne);
    createReviewSection(this.formToolkit, compositeOne);
    compositeOne.setLayout(gridLayout);
    compositeOne.setLayoutData(gridData);

    compositeOne.setWeights(new int[] { 2, 3, 3 });

  }


  /**
   * @param formToolkit2
   * @param compositeOne
   */
  // ICDM-2612
  private void createFmVersionSection(final FormToolkit formToolkit2, final SashForm compositeOne) {

    Section sectionFmVers = formToolkit2.createSection(compositeOne,
        Section.DESCRIPTION | ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TITLE_BAR);
    sectionFmVers.setText("Focus Matrix Version");
    sectionFmVers.setDescription("Select a focus matrix version and click Load button");
    sectionFmVers.setExpanded(true);

    Form form = createLeftFmVersForm(formToolkit2, sectionFmVers);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    sectionFmVers.setLayoutData(gridData);

    sectionFmVers.setClient(form);

  }

  /**
   * @param frmToolkt
   * @param sectionFmVers
   */
  private Form createLeftFmVersForm(final FormToolkit frmToolkt, final ExpandableComposite ec) {
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    Form form = frmToolkt.createForm(ec);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    form.getBody().setLayout(gridLayout);
    form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());


    this.comboFmVers = new BasicObjectCombo<>(form.getBody(), SWT.READ_ONLY);
    this.comboFmVers.setLayoutData(gridData);
    try {
      this.comboFmVers.setElements(getEditorInput().getFmDataHandler().getFocusMatrixVersionsSorted());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.comboFmVers.select(this.comboFmVers.getIndex(getSelectedFmVersion()));
    this.comboFmVers.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent evnt) {

        FocusMatrixVersionClientBO selFmVers = FocusMatrixPage.this.comboFmVers.getSelectedItem();
        FocusMatrixPage.this.btnLoadFmVers
            .setEnabled(!selFmVers.equals(getEditorInput().getFmDataHandler().getSelFmVersion()));

      }
    });

    this.btnLoadFmVers = new Button(form.getBody(), SWT.PUSH);
    this.btnLoadFmVers.setText("Load");
    this.btnLoadFmVers.setToolTipText("Load the selected focus matrix version");
    this.btnLoadFmVers.setEnabled(false);

    this.btnLoadFmVers.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        getEditorInput().getFmDataHandler().setSelFmVersion(FocusMatrixPage.this.comboFmVers.getSelectedItem());
        if (!CommonUtils.isEqual(FocusMatrixPage.this.natTableSection.getModel(), getSelectedFmVersion())) {
          FocusMatrixPage.this.natTableSection.setModel(getSelectedFmVersion());
        }
        populateFmVersInPage();
        FocusMatrixPage.this.btnLoadFmVers.setEnabled(false);
      }
    });

    return form;

  }


  private void populateFmVersInPage() {
    setPageTitleText();

    this.ucTreeViewer.setSelection(new StructuredSelection(this.fmRootNode), true);

    fillFmReviewFields();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  // ICDM-2612
  public PIDCEditorInput getEditorInput() {
    return (PIDCEditorInput) super.getEditorInput();
  }


  /**
   * @param formToolkit2
   * @param compositeOne
   */
  private void createReviewSection(final FormToolkit formToolkit2, final Composite compositeOne) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.sectionRvwLeft =
        formToolkit2.createSection(compositeOne, ExpandableComposite.CLIENT_INDENT | ExpandableComposite.TITLE_BAR);
    this.sectionRvwLeft.setText("Focus Matrix Review");
    this.sectionRvwLeft.setExpanded(true);
    createLeftRevviewForm(formToolkit2, this.sectionRvwLeft);
    this.sectionRvwLeft.setLayoutData(gridData);
    this.sectionRvwLeft.setClient(this.leftRvwForm);

  }

  /**
   * @param frmToolkit
   * @param gridLayout
   * @param ec
   */
  private void createLeftRevviewForm(final FormToolkit frmToolkit, final ExpandableComposite ec) {

    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    this.leftRvwForm = frmToolkit.createScrolledForm(ec);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.leftRvwForm.getBody().setLayout(gridLayout);
    this.leftRvwForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

    frmToolkit.createLabel(this.leftRvwForm.getBody(), "Reviewed By");
    frmToolkit.createLabel(this.leftRvwForm.getBody(), "");
    this.rvwByTextField = frmToolkit.createText(this.leftRvwForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.rvwByTextField.setLayoutData(gridData);
    this.rvwByTextField.setEditable(false);
    this.rvwdByBrowseBtn = frmToolkit.createButton(this.leftRvwForm.getBody(), "", SWT.PUSH);
    this.rvwdByBrowseBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    this.rvwdByBrowseBtn.setToolTipText("Select User");
    this.rvwdByBrowseBtn.setEnabled(validateEditPriviledge());
    this.rvwdByBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // ICDM-2487 P1.27.101
        ApicDataBO apicBo = new ApicDataBO();
        CurrentUserBO currUser = new CurrentUserBO();
        // ICDM-2487 P1.27.101
        try {
          if (!apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion()) &&
              (currUser.hasApicWriteAccess() &&
                  currUser.hasNodeWriteAccess(getEditorInput().getSelectedPidcVersion().getPidcId()))) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(getEditorInput().getSelectedPidcVersion());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

        // ICDM-2354
        if (apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion())) {
          final UserSelectionDialog partsDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
              "Add User", "Add user", "Add user", "Add", true, false);
          partsDialog.setSelectedMultipleUser(null);
          int openDialogStatus = partsDialog.open();
          List<User> selectedUsers = partsDialog.getSelectedMultipleUser();
          if ((selectedUsers != null) && !selectedUsers.isEmpty() && (openDialogStatus == 0)) {
            User selectedUser = selectedUsers.get(0);
            FocusMatrixPage.this.rvwByTextField.setText(selectedUser.getDescription());
            FocusMatrixVersionServiceClient fmVersionServiceClient = new FocusMatrixVersionServiceClient();
            FocusMatrixVersion fmVersion = getSelectedFmVersion().getFmVersion();
            FocusMatrixVersion clonedFmVersion = fmVersion.clone();
            clonedFmVersion.setReviewedUser(selectedUser.getId());
            try {
              fmVersionServiceClient.update(clonedFmVersion);
            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
          }
        }
      }
    });

    frmToolkit.createLabel(this.leftRvwForm.getBody(), "Reviewed On");
    frmToolkit.createLabel(this.leftRvwForm.getBody(), "");
    this.rvwonTextField = frmToolkit.createText(this.leftRvwForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.rvwonTextField.setLayoutData(gridData);
    this.rvwonTextField.setEditable(false);
    this.rvwTimeSelBtn = frmToolkit.createButton(this.leftRvwForm.getBody(), "", SWT.PUSH);
    this.rvwTimeSelBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CALENDAR_16X16));
    this.rvwTimeSelBtn.setEnabled(

        validateEditPriviledge());
    // Add Selection listnerlto Time stamp button.
    this.rvwTimeSelBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        ApicDataBO apicBo = new ApicDataBO();
        CurrentUserBO currUser = new CurrentUserBO();
        // ICDM-2487 P1.27.101
        try {
          if (!apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion()) &&
              (currUser.hasApicWriteAccess() &&
                  currUser.hasNodeWriteAccess(getEditorInput().getSelectedPidcVersion().getPidcId()))) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(getEditorInput().getSelectedPidcVersion());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

        // ICDM-2354
        if (apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion())) {
          CalendarDialog calDailog = new CalendarDialog();
          calDailog.addCalendarDialog(FocusMatrixPage.this.leftRvwForm.getBody(), FocusMatrixPage.this.rvwonTextField,
              "yyyy-MMM-dd ");
        }
      }
    });

    FocusMatrixPage.this.rvwonTextField.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        try {
          DateFormat df12 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_12,
              Locale.getDefault(Locale.Category.FORMAT));
          DateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
              Locale.getDefault(Locale.Category.FORMAT));
          String existingDateStr = null;
          Date existingDate = null;
          if (null != getSelectedFmVersion().getFmVersion().getReviewedDate()) {
            existingDateStr = getSelectedFmVersion().getFmVersion().getReviewedDate();
            existingDate = df15.parse(existingDateStr);
            existingDateStr = df12.format(existingDate);
          }

          if (!FocusMatrixPage.this.rvwonTextField.getText().isEmpty()) {
            Date setDate = df12.parse(FocusMatrixPage.this.rvwonTextField.getText());
            if ((null != existingDate) &&
                !CommonUtils.isEqualIgnoreCase(existingDateStr, FocusMatrixPage.this.rvwonTextField.getText()) &&
                setDate.before(existingDate)) {
              MessageDialogUtils.getErrorMessageDialog("InValid",
                  "Review date cannot be lesser than the previous set date");
              FocusMatrixPage.this.rvwonTextField.setText(existingDateStr);
            }
            else if (setDate.after(new Date())) {
              MessageDialogUtils.getErrorMessageDialog("InValid",
                  "Review date cannot be greater than the current date");
              if (null != existingDateStr) {
                FocusMatrixPage.this.rvwonTextField.setText(existingDateStr);
              }
              else {
                FocusMatrixPage.this.rvwonTextField.setText("");
              }
            }
            else {
              if (!CommonUtils.isEqualIgnoreCase(existingDateStr, FocusMatrixPage.this.rvwonTextField.getText())) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(setDate);
                FocusMatrixVersionServiceClient fmVersionServiceClient = new FocusMatrixVersionServiceClient();
                FocusMatrixVersion fmVersion = getSelectedFmVersion().getFmVersion();
                FocusMatrixVersion clonedFmVersion = fmVersion.clone();
                clonedFmVersion.setReviewedDate(
                    ApicUtil.getFormattedDate(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15, cal));
                fmVersionServiceClient.update(clonedFmVersion);


              }
            }
          }
        }
        catch (ParseException | ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    });
    frmToolkit.createLabel(this.leftRvwForm.getBody(), "Link");
    frmToolkit.createLabel(this.leftRvwForm.getBody(), "");
    this.link = new Link(this.leftRvwForm.getBody(), SWT.BORDER);
    this.link.setText("<A></A>");
    this.link.setLayoutData(gridData);
    this.link.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event arg0) {
        if (!FocusMatrixPage.this.link.getText().isEmpty()) {
          String linkText = FocusMatrixPage.this.link.getText();
          linkText = linkText.replace("<A>", "").replace("</A>", "");
          CommonActionSet action = new CommonActionSet();
          action.openExternalBrowser(linkText);
        }
      }
    });
    this.rvwLinkEditBtn = frmToolkit.createButton(this.leftRvwForm.getBody(), "", SWT.PUSH);
    this.rvwLinkEditBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12));
    this.rvwLinkEditBtn.setToolTipText("Add Link");
    this.rvwLinkEditBtn.setEnabled(validateEditPriviledge());
    this.rvwLinkEditBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        ApicDataBO apicBo = new ApicDataBO();
        CurrentUserBO currUser = new CurrentUserBO();
        // ICDM-2487 P1.27.101
        try {
          if (!apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion()) &&
              (currUser.hasApicWriteAccess() &&
                  currUser.hasNodeWriteAccess(getEditorInput().getSelectedPidcVersion().getPidcId()))) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(getEditorInput().getSelectedPidcVersion());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

        // ICDM-2354
        if (apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion())) {
          String newLinkSet;
          int openDialogStatus;
          String fmVersLink = getSelectedFmVersion().getFmVersion().getLink();
          if (fmVersLink != null) {
            LinkData linkData = new LinkData(null);
            linkData.setNewLink(fmVersLink);
            linkData.setNewDescEng(fmVersLink);
            final EditLinkDialog linkDialog =
                new EditLinkDialog(Display.getCurrent().getActiveShell(), linkData, null, true);
            openDialogStatus = linkDialog.open();
            newLinkSet = linkDialog.getLinkData().getNewLink();
          }
          else {
            final AddLinkDialog linkDialog = new AddLinkDialog(Display.getCurrent().getActiveShell(), null, true);
            openDialogStatus = linkDialog.open();
            newLinkSet = linkDialog.getLinkData().getNewLink();
          }
          if ((null != newLinkSet) && (openDialogStatus == 0)) {
            FocusMatrixPage.this.link.setText("<A>" + newLinkSet + "</A>");

            FocusMatrixVersionServiceClient fmVersionServiceClient = new FocusMatrixVersionServiceClient();
            FocusMatrixVersion fmVersion = getSelectedFmVersion().getFmVersion();
            FocusMatrixVersion clonedFmVersion = fmVersion.clone();
            clonedFmVersion.setLink(newLinkSet);
            try {
              fmVersionServiceClient.update(clonedFmVersion);
            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
          }
        }


      }

    });

    frmToolkit.createLabel(this.leftRvwForm.getBody(), "Remark");
    frmToolkit.createLabel(this.leftRvwForm.getBody(), "");

    GridData gridData1 = GridDataUtil.getInstance().getGridData();
    gridData1.heightHint = 50;

    this.remarkTextField = frmToolkit.createText(this.leftRvwForm.getBody(), null, SWT.MULTI | SWT.BORDER);
    this.remarkTextField.setLayoutData(gridData1);

    this.remarkTextField.setEditable(false);
    this.rvwRemarkEditBtn = frmToolkit.createButton(this.leftRvwForm.getBody(), "", SWT.PUSH);
    this.rvwRemarkEditBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.EDIT_12X12));
    this.rvwRemarkEditBtn.setToolTipText("Add Remark");
    this.rvwRemarkEditBtn.setEnabled(

        validateEditPriviledge());
    this.rvwRemarkEditBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        ApicDataBO apicBo = new ApicDataBO();
        CurrentUserBO currUser = new CurrentUserBO();
        // ICDM-2487 P1.27.101
        try {
          if (!apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion()) &&
              (currUser.hasApicWriteAccess() &&
                  currUser.hasNodeWriteAccess(getEditorInput().getSelectedPidcVersion().getPidcId()))) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(getEditorInput().getSelectedPidcVersion());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

        // ICDM-2354
        if (apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion())) {

          final CommentsDialog remarksDialog = new CommentsDialog(Display.getCurrent().getActiveShell(), 4000,
              FocusMatrixPage.this.remarkTextField.getText());
          remarksDialog.setSectionTitle(REMARKS_CONST);
          remarksDialog.setTitleText(REMARKS_CONST);
          remarksDialog.setTitleMessageText("Enter the remarks");
          remarksDialog.setRemarkLableText("Remark : ");
          int openDialogStatus = remarksDialog.open();
          if ((null != remarksDialog.getComments()) && (openDialogStatus == 0)) {
            FocusMatrixPage.this.remarkTextField.setText(remarksDialog.getComments());

            FocusMatrixVersionServiceClient fmVersionServiceClient = new FocusMatrixVersionServiceClient();
            FocusMatrixVersion fmVersion = getSelectedFmVersion().getFmVersion();
            FocusMatrixVersion clonedFmVersion = fmVersion.clone();
            clonedFmVersion.setRemark(remarksDialog.getComments());
            try {
              fmVersionServiceClient.update(clonedFmVersion);
            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
          }
        }

      }
    });

    Group grp = new Group(this.leftRvwForm.getBody(), SWT.NONE);
    GridLayout gridLayout2 = new GridLayout();
    gridLayout2.numColumns = 1;
    frmToolkit.createLabel(grp, "Review executed and confirmed by a reviewer ?");
    grp.setLayout(gridLayout2);
    grp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.yesOption = frmToolkit.createButton(grp, "Yes", SWT.RADIO);
    this.yesOption.setEnabled(

        validateEditPriviledge());

    this.yesOption.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (FocusMatrixPage.this.yesOption.getSelection()) {
          ApicDataBO apicBo = new ApicDataBO();
          CurrentUserBO currUser = new CurrentUserBO();
          // ICDM-2487 P1.27.101
          try {
            if (!apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion()) &&
                (currUser.hasApicWriteAccess() &&
                    currUser.hasNodeWriteAccess(getEditorInput().getSelectedPidcVersion().getPidcId()))) {
              final PIDCActionSet pidcActionSet = new PIDCActionSet();
              pidcActionSet.showUnlockPidcDialog(getEditorInput().getSelectedPidcVersion());
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }

          // ICDM-2354
          if (apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion())) {
            String rvwStatus = getSelectedFmVersion().getFmVersion().getRvwStatus();
            if ((null == getSelectedFmVersion().getFmVersion().getReviewedUser()) ||
                (null == getSelectedFmVersion().getFmVersion().getReviewedDate())) {
              MessageDialogUtils.getInfoMessageDialog(ApicUiConstants.EDIT_NOT_ALLOWED,
                  "Please enter the 'Reviewed By' and 'Reviewed On' details to proceed !");
              FocusMatrixPage.this.yesOption.setSelection(false);
              FocusMatrixPage.this.noOption
                  .setSelection(CommonUtils.isEqual(rvwStatus, FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NO));
            }
            else {

              FM_REVIEW_STATUS rvwStatusEnum = FocusMatrixVersionClientBO.FM_REVIEW_STATUS.getStatus(rvwStatus);
              if (((rvwStatusEnum != FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NOT_DEFINED) &&
                  (rvwStatusEnum != FocusMatrixVersionClientBO.FM_REVIEW_STATUS.YES)) ||
                  (rvwStatusEnum == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NOT_DEFINED)) {

                FocusMatrixVersionServiceClient fmVersionServiceClient = new FocusMatrixVersionServiceClient();
                FocusMatrixVersion fmVersion = getSelectedFmVersion().getFmVersion();
                FocusMatrixVersion clonedFmVersion = fmVersion.clone();
                clonedFmVersion.setRvwStatus(FocusMatrixVersionClientBO.FM_REVIEW_STATUS.YES.getStatusStr());
                try {
                  fmVersionServiceClient.update(clonedFmVersion);
                }
                catch (ApicWebServiceException exp) {
                  CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
                }

              }
            }
          }
          else {
            String rvwStatus = getSelectedFmVersion().getFmVersion().getRvwStatus();
            FM_REVIEW_STATUS status = FocusMatrixVersionClientBO.FM_REVIEW_STATUS.getStatus(rvwStatus);
            FocusMatrixPage.this.yesOption.setSelection(status == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.YES);
            FocusMatrixPage.this.noOption.setSelection(status == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NO);
          }
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // TODO Auto-generated method stub

      }
    });

    this.noOption = frmToolkit.createButton(grp, "No", SWT.RADIO);
    this.noOption.setEnabled(validateEditPriviledge());

    this.noOption.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        if (FocusMatrixPage.this.noOption.getSelection()) {
          ApicDataBO apicBo = new ApicDataBO();
          CurrentUserBO currUser = new CurrentUserBO();
          // ICDM-2487 P1.27.101
          try {
            if (!apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion()) &&
                (currUser.hasApicWriteAccess() &&
                    currUser.hasNodeWriteAccess(getEditorInput().getSelectedPidcVersion().getPidcId()))) {
              final PIDCActionSet pidcActionSet = new PIDCActionSet();
              pidcActionSet.showUnlockPidcDialog(getEditorInput().getSelectedPidcVersion());
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }

          // ICDM-2354
          if (apicBo.isPidcUnlockedInSession(getEditorInput().getSelectedPidcVersion())) {
            if (FocusMatrixPage.this.noOption.getSelection()) {
              new PIDCActionSet().resetRvwStatusToNo(getSelectedFmVersion().getFmVersion(), FocusMatrixPage.this);
            }
          }
          else {
            String rvwStatus = getSelectedFmVersion().getFmVersion().getRvwStatus();
            FM_REVIEW_STATUS status = FocusMatrixVersionClientBO.FM_REVIEW_STATUS.getStatus(rvwStatus);
            FocusMatrixPage.this.noOption.setSelection(status == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NO);
            FocusMatrixPage.this.yesOption.setSelection(status == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.YES);
          }
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // TODO Auto-generated method stub

      }
    });

    fillFmReviewFields();

  }


  // ICDM-2612
  private void fillFmReviewFields() {
    FocusMatrixVersion fmVersion = getSelectedFmVersion().getFmVersion();
    boolean modifiable = validateEditPriviledge();


    this.rvwByTextField
        .setText(null == fmVersion.getReviewedUser() ? "" : getSelectedFmVersion().getReviewedUser().getDescription());
    this.rvwdByBrowseBtn.setEnabled(modifiable);

    DateFormat df12 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_12,
        Locale.getDefault(Locale.Category.FORMAT));
    DateFormat df15 = new SimpleDateFormat(com.bosch.caltool.icdm.common.util.DateFormat.DATE_FORMAT_15,
        Locale.getDefault(Locale.Category.FORMAT));

    if (null != fmVersion.getReviewedDate()) {
      try {
        Date parsedDate = df15.parse(fmVersion.getReviewedDate());
        String formatdDateStr = df12.format(parsedDate);
        this.rvwonTextField.setText(formatdDateStr);
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    else {
      this.rvwonTextField.setText("");
    }
    this.rvwTimeSelBtn.setEnabled(modifiable);

    this.link.setText(null == fmVersion.getLink() ? "" : "<A>" + fmVersion.getLink() + "</A>");
    this.rvwLinkEditBtn.setEnabled(modifiable);

    this.remarkTextField.setText(null == fmVersion.getRemark() ? "" : fmVersion.getRemark());
    this.rvwRemarkEditBtn.setEnabled(modifiable);

    FM_REVIEW_STATUS status = FocusMatrixVersionClientBO.FM_REVIEW_STATUS.getStatus(fmVersion.getRvwStatus());
    this.yesOption.setSelection(status == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.YES);
    this.yesOption.setEnabled(modifiable);

    this.noOption.setSelection(status == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NO);
    this.noOption.setEnabled(modifiable);

  }

  /**
   * @param toolkit This method initializes sectionTwo
   * @param gridLayout
   * @param compositeOne
   */
  private void createLeftTreeSection(final FormToolkit toolkit, final GridLayout gridLayout,
      final Composite compositeOne) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionLeft = toolkit.createSection(compositeOne, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionLeft.setText("Use Cases");
    this.sectionLeft.setExpanded(true);
    this.sectionLeft.getDescriptionControl().setEnabled(false);
    createLeftForm(toolkit, gridLayout, this.sectionLeft);


    this.sectionLeft.setLayoutData(gridData);
    this.sectionLeft.setClient(this.leftForm);
  }

  /**
   * @param toolkit This method initializes form
   * @param gridLayout
   * @param parent
   */
  private void createLeftForm(final FormToolkit toolkit, final GridLayout gridLayout, final Section parent) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.leftForm = toolkit.createScrolledForm(parent);
    this.leftForm.getBody().setLayoutData(gridData);

    this.leftForm.getBody().setLayout(gridLayout);


    final PatternFilter filter = new PatternFilter();
    final FilteredTree tree = new FilteredTree(this.leftForm.getBody(), SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.ucTreeViewer = tree.getViewer();
    this.ucTreeViewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.ucTreeViewer.setAutoExpandLevel(2);

    // Set Content provider for the tree
    PIDCEditorInput editorInput = getEditorInput();

    this.projectFavNodes = getProjectFavNodes(editorInput.getFmDataHandler());
    this.fmRootNode = new FocusMatrixUseCaseRootNode(this.projectFavNodes);
    FocusMatrixTreeViewContentProvider provider = new FocusMatrixTreeViewContentProvider(
        getProjectSpecificUCItems(editorInput.getFmDataHandler(), this.projectFavNodes), this.fmRootNode,
        editorInput.getFmDataHandler());
    this.ucTreeViewer.setContentProvider(provider);

    // Set Label provider for the tree
    // Call to build tree using setInput(), EMPTY string object indicates to
    this.ucTreeViewer.setLabelProvider(new FocusMatrixTreeViewLabelProvider());
    // create root node in Content provider
    this.ucTreeViewer.setInput("");
    ColumnViewerToolTipSupport.enableFor(this.ucTreeViewer);

    this.ucTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {


      @Override
      public void selectionChanged(final SelectionChangedEvent event) {
        // construct the table based on selection
        constructUCItems(event.getSelection());
      }


    });


  }

  /**
   * @param focusMatrixDataHandler OutLineViewDataHandler
   * @param projectFavNodes
   * @param pidCard
   * @param ucList
   * @return
   */
  private List<IUseCaseItemClientBO> getProjectSpecificUCItems(final FocusMatrixDataHandler focusMatrixDataHandler,
      final SortedSet<FavUseCaseItemNode> projectFavNodes) {
    List<IUseCaseItemClientBO> ucList = new ArrayList<>();

    for (com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode favItem : projectFavNodes) {
      ucList.add(favItem.getUseCaseItem());
      if (null != favItem.getChildFavNodes()) {
        ucList = getProjectChildFavItems(ucList, favItem, focusMatrixDataHandler);
      }
      else {
        ucList = getProjectUCChildItems(ucList, favItem.getUseCaseItem(), focusMatrixDataHandler);
      }
    }
    return ucList;
  }

  /**
   * @param focusMatrixDataHandler OutLineViewDataHandler
   * @return SortedSet<FavUseCaseItemNode>
   */
  private SortedSet<FavUseCaseItemNode> getProjectFavNodes(final FocusMatrixDataHandler focusMatrixDataHandler) {
    UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(focusMatrixDataHandler.getUcDataHandler().getPidcversion(),
        focusMatrixDataHandler.getUcDataHandler());
    focusMatrixDataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);

    return focusMatrixDataHandler.getUcDataHandler().getRootProjectUcFavNodes();
  }

  /**
   * @param ucList
   * @param favItem
   * @param focusMatrixDataHandler
   */
  private List<IUseCaseItemClientBO> getProjectChildFavItems(final List<IUseCaseItemClientBO> ucList,
      final FavUseCaseItemNode favItem, final FocusMatrixDataHandler focusMatrixDataHandler) {
    if (null != favItem.getChildFavNodes()) {
      for (FavUseCaseItemNode node : favItem.getChildFavNodes()) {
        ucList.add(node.getUseCaseItem());
        getProjectChildFavItems(ucList, node, focusMatrixDataHandler);
      }
    }
    else {
      getProjectUCChildItems(ucList, favItem.getUseCaseItem(), focusMatrixDataHandler);
    }
    return ucList;
  }

  /**
   * @param ucList
   * @param useCaseItem
   * @param focusMatrixDataHandler
   */
  private List<IUseCaseItemClientBO> getProjectUCChildItems(final List<IUseCaseItemClientBO> ucList,
      final IUseCaseItemClientBO useCaseItem, final FocusMatrixDataHandler focusMatrixDataHandler) {

    Set<IUseCaseItemClientBO> childItemsBos = new HashSet<>();
    if (useCaseItem instanceof UseCaseGroupClientBO) {
      final SortedSet<UseCaseGroupClientBO> subGroupSet = ((UseCaseGroupClientBO) useCaseItem).getChildGroupSet(false);
      if (!subGroupSet.isEmpty()) {
        childItemsBos.addAll(subGroupSet);
      }
      childItemsBos.addAll(((UseCaseGroupClientBO) useCaseItem).getUseCaseSet(false));
    }
    if (useCaseItem instanceof UsecaseClientBO) {
      UsecaseClientBO ucClientBO = (UsecaseClientBO) useCaseItem;
      ucClientBO.setUsecaseEditorModel(fetchUseCaseDetailsModel(focusMatrixDataHandler, ucClientBO));
      childItemsBos.addAll(((UsecaseClientBO) useCaseItem).getUseCaseSectionSet(false));
    }
    if (useCaseItem instanceof UseCaseSectionClientBO) {
      childItemsBos.addAll(((UseCaseSectionClientBO) useCaseItem).getChildSectionSet(false));
    }
    for (IUseCaseItemClientBO ucItem : childItemsBos) {

      ucList.add(ucItem);
      getProjectUCChildItems(ucList, ucItem, focusMatrixDataHandler);

    }
    return ucList;
  }

  /**
   * @param focusMatrixDataHandler FocusMatrixDataHandler
   * @param ucClientBO UsecaseClientBO
   * @return UsecaseEditorModel
   */
  private UsecaseEditorModel fetchUseCaseDetailsModel(final FocusMatrixDataHandler focusMatrixDataHandler,
      final UsecaseClientBO ucClientBO) {

    UsecaseEditorModel usecaseEditorModel = focusMatrixDataHandler.getUcDataHandler().getUseCaseDetailsModel()
        .getUsecaseDetailsModelMap().get(ucClientBO.getUseCase().getId());
    return usecaseEditorModel;
  }

  /**
   * construct the table based on selection
   *
   * @param selected Object
   */
  private void constructUCItems(final Object selection) {
    // get the selected item
    final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
    Object selected = structuredSelection.getFirstElement();


    if (selected instanceof IUseCaseItemClientBO) {
      // if the selection is an usecase or usecase group or usecase section
      FocusMatrixPage.this.natTableSection.setSelectedUcItem((IUseCaseItemClientBO) (selected));
      // clear the list in the model
      FocusMatrixPage.this.natTableSection.getModel().getSelectedUCItemsList().clear();
      if (selected instanceof UseCaseGroupClientBO) {
        // if the selection is Usecase group
        addChildItems(FocusMatrixPage.this.natTableSection.getSelectedUcItem());
      }
      else {
        // if the selection is usecase or usecase section
        addToModel(FocusMatrixPage.this.natTableSection.getSelectedUcItem());
      }
      reconstructNatTable();
      setStatusBarMessage(false);
    }

    else if (selected instanceof FocusMatrixUseCaseRootNode) { // if the top most level of the tree is selected

      FocusMatrixPage.this.natTableSection.getModel().getSelectedUCItemsList().clear();
      FocusMatrixPage.this.natTableSection.setSelectedUcItem(null);

      // add all the child items under all usecase groups
      for (FavUseCaseItemNode ucGroup : ((FocusMatrixUseCaseRootNode) selected).getUseCaseGroups()) {
        addChildItems(ucGroup.getUseCaseItem());
      }
      // reconstruct NAT table and set status bar message
      reconstructNatTable();
      setStatusBarMessage(false);
    }
    else {
      // in case of empty selection
      FocusMatrixPage.this.natTableSection.setSelectedUcItem(null);
      FocusMatrixPage.this.natTableSection.getModel().getSelectedUCItemsList().clear();

      // reconstruct NAT table and set status bar message
      reconstructNatTable();
      setStatusBarMessage(false);
    }
  }

  /**
   * get child items and add relevant usecase items to the model
   */
  private void addChildItems(final IUseCaseItemClientBO iUseCaseItemClientBO) {
    if (CommonUtils.isNullOrEmpty(((UseCaseGroupClientBO) iUseCaseItemClientBO).getChildGroupSet(false))) {
      // add child usecase items if there are any
      for (IUseCaseItemClientBO useCaseItem : iUseCaseItemClientBO.getChildUCItems()) {
        addToModel(useCaseItem);
      }
    }
    else {
      for (UseCaseGroupClientBO grpChild : ((UseCaseGroupClientBO) iUseCaseItemClientBO).getChildGroupSet(false)) {
        // add child items of usecase groups
        addChildItems(grpChild);
      }
    }

  }

  /**
   * get the mappable items from the usecase item and add them to model if they are focus matrix relevant
   *
   * @param useCaseItem AbstractUseCaseItem
   */
  private void addToModel(final IUseCaseItemClientBO useCaseItem) {
    PIDCEditorInput editorInput = getEditorInput();
    // load the project favourite nodes
    List<IUseCaseItemClientBO> projectSpecificUCItems =
        getProjectSpecificUCItems(editorInput.getFmDataHandler(), this.projectFavNodes);

    // set usecase editor model in order to
    boolean ucItemNeeded = setUseCaseEditorModel(useCaseItem, editorInput, projectSpecificUCItems);
    if (ucItemNeeded) {
      for (IUseCaseItemClientBO uc : useCaseItem.getMappableItems()) {
        if (uc.isFocusMatrixRelevant(true) && projectSpecificUCItems.contains(uc)) {
          // add to the list in the model only in case of focus matrix relevant & project specific use case
          FocusMatrixPage.this.natTableSection.getModel().getSelectedUCItemsList().add(uc);
        }
      }
    }
  }

  /**
   * @param useCaseItem
   * @param editorInput
   * @param projectSpecificUCItems
   * @return boolean
   */
  private boolean setUseCaseEditorModel(final IUseCaseItemClientBO useCaseItem, final PIDCEditorInput editorInput,
      final List<IUseCaseItemClientBO> projectSpecificUCItems) {
    boolean ucItemAmongFav = true;
    if (useCaseItem instanceof UsecaseClientBO) {
      ucItemAmongFav = false;
      if (projectSpecificUCItems.contains(useCaseItem)) {
        UsecaseClientBO ucClientBO = (UsecaseClientBO) useCaseItem;
        ucClientBO.setUsecaseEditorModel(fetchUseCaseDetailsModel(editorInput.getFmDataHandler(), ucClientBO));
        ucItemAmongFav = true;
      }
    }
    return ucItemAmongFav;
  }

  /**
   * disposing and creating new table
   */
  private void reconstructNatTable() {
    // dispose the old NAT table
    this.natTableSection.getNatTable().dispose();
    if (this.toolBarManager != null) {
      this.toolBarManager.dispose();
    }
    // clear the label map
    this.natTableSection.getPropertyToLabelMap().clear();
    this.natTableSection.setUcFilterGridLayer(null);
    // create new NAT table
    try {
      this.natTableSection.createFocusMatrixTableViewer();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    createToolBarAction();
    this.natTableSection.getFilterTxt().setText("");
    this.natTableSection
        .setTotTableRowCount(this.natTableSection.getUcFilterGridLayer().getRowHeaderLayer().getPreferredRowCount());
    if (CommonUtils.isNotNull(this.outlineViewPart)) { // ICDM -1596
      if (this.outlineViewPart.getCurrentPage() instanceof PIDCOutlinePage) {
        PIDCOutlinePage pidcOutlinePage = (PIDCOutlinePage) this.outlineViewPart.getCurrentPage();
        pidcOutlinePage.refreshTreeViewer(true);// this is done to reset the selection
      }
    }

    FocusMatrixPage.this.natTableSection.getForm().getBody().pack();
    FocusMatrixPage.this.natTableSection.getSectionRight().layout();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {

    if (null != this.ucTreeViewer) {
      Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
      for (Entry<IModelType, Map<Long, ChangeData<?>>> changedDataMap : consChangeData.entrySet()) {
        if (changedDataMap.getKey() == MODEL_TYPE.FOCUS_MATRIX_VERSION) {
          // refresh the combo box
          try {
            this.comboFmVers.setElements(getEditorInput().getFmDataHandler().getFocusMatrixVersionsSorted());
            this.comboFmVers.select(this.comboFmVers.getIndex(getSelectedFmVersion()));
            if (!CommonUtils.isEqual(FocusMatrixPage.this.natTableSection.getModel(), getSelectedFmVersion())) {
              FocusMatrixPage.this.natTableSection.setModel(getSelectedFmVersion());
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
          // reconstruct the NAT table
          constructUCItems(this.ucTreeViewer.getSelection());
          fillFmReviewFields();
        }
        else if ((changedDataMap.getKey() == MODEL_TYPE.FOCUS_MATRIX) ||
            ((changedDataMap.getKey() == MODEL_TYPE.PROJ_ATTR) && isRemarkChanged(changedDataMap))) {
          this.natTableSection.getModel().resetFocusMatrixDefinitionLoaded();
          this.natTableSection.getNatTable().refresh();
        }
        else if ((changedDataMap.getKey() == MODEL_TYPE.ATTRIBUTE) ||
            (changedDataMap.getKey() == MODEL_TYPE.SUPER_GROUP) || (changedDataMap.getKey() == MODEL_TYPE.GROUP)) {
          // reconstruct the NAT table
          constructUCItems(this.ucTreeViewer.getSelection());
        }
        else {
          this.natTableSection.getModel().resetFocusMatrixDefinitionLoaded();
          this.natTableSection.getNatTable().refresh();
          // changes in usecase /usecase favorites
          this.projectFavNodes = getProjectFavNodes(getEditorInput().getFmDataHandler());
          this.fmRootNode = new FocusMatrixUseCaseRootNode(this.projectFavNodes);
          FocusMatrixTreeViewContentProvider provider = new FocusMatrixTreeViewContentProvider(
              getProjectSpecificUCItems(getEditorInput().getFmDataHandler(), this.projectFavNodes), this.fmRootNode,
              getEditorInput().getFmDataHandler());
          this.ucTreeViewer.setContentProvider(provider);

          // Set Label provider for the tree
          // Call to build tree using setInput(), EMPTY string object indicates to
          this.ucTreeViewer.setLabelProvider(new FocusMatrixTreeViewLabelProvider());
          // create root node in Content provider
          this.ucTreeViewer.setInput("");
          // set the selection to the root node
          this.ucTreeViewer.setSelection(new StructuredSelection(this.fmRootNode));

        }
      }
    }
  }

  /**
   * @param changedDataMap
   * @return
   */
  private boolean isRemarkChanged(final Entry<IModelType, Map<Long, ChangeData<?>>> changedDataMap) {
    for (Entry<Long, ChangeData<?>> chData : changedDataMap.getValue().entrySet()) {
      PidcVersionAttribute projAttrOld = (PidcVersionAttribute) chData.getValue().getOldData();
      PidcVersionAttribute projAttrNew = (PidcVersionAttribute) chData.getValue().getNewData();
      if ((projAttrOld != null) &&
          CommonUtils.isNotEqual(projAttrOld.getFmAttrRemark(), projAttrNew.getFmAttrRemark())) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    if (null == this.toolBarManager) {
      this.toolBarManager = new ToolBarManager(SWT.FLAT);
    }
    return this.toolBarManager;
  }


  /**
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  public void setStatusBarMessage(final boolean outlineSelection) {

    int totalItemCount = this.natTableSection.getTotTableRowCount();
    int filteredItemCount = this.natTableSection.getUcFilterGridLayer().getRowHeaderLayer().getPreferredRowCount();
    final StringBuilder buf = new StringBuilder(40);
    buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount).append(" records");
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      // in case of outline selection
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(ApicUiConstants.OUTLINE_TREE_VIEW).getSite();
      // get the status line manager from the outline
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    else {
      // get the status line manager from the editor
      statusLine = this.editor.getEditorSite().getActionBars().getStatusLineManager();
    }
    if (totalItemCount == filteredItemCount) {
      statusLine.setErrorMessage(null);
      statusLine.setMessage(buf.toString());
    }
    else {
      // show the message in red if the count is not equal
      statusLine.setErrorMessage(buf.toString());
    }
    statusLine.update(true);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    if (this.editor.getActivePage() == 3) {
      // update the status bar only if the active page is the current page
      super.updateStatusBar(outlineSelection);
      setStatusBarMessage(outlineSelection);
    }
  }

  /**
   * This method fetched selected rule
   *
   * @return Map<FocusMatrixAttribute, List<FocusMatrixUseCaseItem>>
   */
  public Map<FocusMatrixAttributeClientBO, List<FocusMatrixUseCaseItem>> fetchSelectedUcItems() {
    // get the selection layer
    SelectionLayer selectionLayer = this.natTableSection.getUcFilterGridLayer().getBodyLayer().getSelectionLayer();
    ConcurrentMap<FocusMatrixAttributeClientBO, List<FocusMatrixUseCaseItem>> attrUcItemMap = new ConcurrentHashMap<>();
    PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();

    for (PositionCoordinate positionCoordinate : selectedCellPositions) {
      if (positionCoordinate.columnPosition > CommonUIConstants.COLUMN_INDEX_3) {
        // Fetch selected Cell Row object
        FocusMatrixAttributeClientBO focusMatrixAttr = this.natTableSection.getUcFilterGridLayer().getBodyDataProvider()
            .getRowObject(positionCoordinate.rowPosition);
        List<FocusMatrixUseCaseItem> selectedUcItems = attrUcItemMap.get(focusMatrixAttr);
        if (selectedUcItems == null) {
          selectedUcItems = new ArrayList<>();
          attrUcItemMap.put(focusMatrixAttr, selectedUcItems);
        }
        // Not to get the selection for first two columns
        com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem ucItem = focusMatrixAttr.getFmUseCaseItemList()
            .get(this.natTableSection.getUcFilterGridLayer().getBodyLayer().getColumnHideShowLayer()
                .getColumnIndexByPosition(positionCoordinate.columnPosition - STATIC_COL_INDEX));

        if (ucItem != null) {
          // add to the selected usecase items list only if the ucItem is not null
          selectedUcItems.add(ucItem);
        }
      }
    }
    return attrUcItemMap;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (CommonUtils.isEqual(getSite().getPage().getActiveEditor(), getEditor())) {
      if (part instanceof OutlineViewPart) {
        this.outlineViewPart = (OutlineViewPart) part;
        // ICDM 1596
        outLineSelectionListener(selection);
        if (this.editor.getActivePage() == 3) {
          // set the status bar message only if the active page is the current page
          setStatusBarMessage(true);
        }
      }
    }
  }

  /**
   * This method invokes on the selection of Outline Tree node
   *
   * @param selection
   */
  private void outLineSelectionListener(final ISelection selection) {
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      // get the selection
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      filterBasedonSelectionInOutline(first);
    }
  }

  /**
   * @param first Object outline selection
   */
  private void filterBasedonSelectionInOutline(final Object first) {
    // Check if selection is SuperGroup
    if (first instanceof AttrSuperGroup) {
      this.natTableSection.getOutLineNatFilter().setSuperGroup(true);
      final AttrSuperGroup attrSuperGroup = (AttrSuperGroup) first;
      this.natTableSection.getOutLineNatFilter().setSelectedNode(attrSuperGroup.getName());
      this.natTableSection.getUcFilterGridLayer().getFilterStrategy().applyOutlineFilterInAllColumns(false);
      this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer()));
    }
    // Check if selection is Group
    else if (first instanceof AttrGroup) {
      this.natTableSection.getOutLineNatFilter().setGroup(true);
      final AttrGroup attrGroup = (AttrGroup) first;
      this.natTableSection.getOutLineNatFilter().setSelectedNode(attrGroup.getName());
      this.natTableSection.getUcFilterGridLayer().getFilterStrategy().applyOutlineFilterInAllColumns(false);
      this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer()));
    }
    // Check if selection is COMMON
    else if ((first instanceof AttrRootNode) || (first instanceof UseCaseRootNode) ||
        (first instanceof UserFavUcRootNode)) {
      this.natTableSection.getOutLineNatFilter().setCommon(true);
      this.natTableSection.getOutLineNatFilter().setSelectedNode("");
      this.natTableSection.getUcFilterGridLayer().getFilterStrategy().applyOutlineFilterInAllColumns(true);
      this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer()));
    }
    // check if this is an usecase item
    else if (first instanceof IUseCaseItemClientBO) {
      this.natTableSection.getOutLineNatFilter().setUseCaseItem((IUseCaseItemClientBO) first);
      this.natTableSection.getUcFilterGridLayer().getFilterStrategy().applyOutlineFilterInAllColumns(false);
      this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer()));
    }
    // ICDM-1035
    else if (first instanceof FavUseCaseItemNode) {
      this.natTableSection.getOutLineNatFilter().setFavUseCaseItem((FavUseCaseItemNode) first);
      this.natTableSection.getUcFilterGridLayer().getFilterStrategy().applyOutlineFilterInAllColumns(false);
      this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(this.natTableSection.getUcFilterGridLayer().getSortableColumnHeaderLayer()));
    }
  }


  /**
   * @return the toolBarFilters
   */
  public FocusMatrixToolBarFilters getToolBarFilters() {
    return this.toolBarFilters;
  }


  /**
   * @param toolBarFilters the toolBarFilters to set
   */
  public void setToolBarFilters(final FocusMatrixToolBarFilters toolBarFilters) {
    this.toolBarFilters = toolBarFilters;
  }


  /**
   * return true if the user can edit focus matrix version
   */
  private boolean validateEditPriviledge() {
    PIDCEditorInput editorInput = getEditorInput();
    try {
      return getSelectedFmVersion().isModifiable() && getSelectedFmVersion()
          .hasFocusMatrix(getProjectSpecificUCItems(editorInput.getFmDataHandler(), this.projectFavNodes));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @return selected Focus matrix version
   */
  // ICDM-2612
  private FocusMatrixVersionClientBO getSelectedFmVersion() {
    return getEditorInput().getFmDataHandler().getSelFmVersion();
  }


  /**
   * @return the rvwByTextField
   */
  public Text getRvwByTextField() {
    return this.rvwByTextField;
  }


  /**
   * @return the rvwonTextField
   */
  public Text getRvwonTextField() {
    return this.rvwonTextField;
  }


  /**
   * @return the remarkTextField
   */
  public Text getRemarkTextField() {
    return this.remarkTextField;
  }


  /**
   * @return the link
   */
  public Link getLink() {
    return this.link;
  }


  /**
   * @return the noOption
   */
  public Button getNoOption() {
    return this.noOption;
  }


  /**
   * @return the yesOption
   */
  public Button getYesOption() {
    return this.yesOption;
  }


  /**
   * @return the mainComposite
   */
  public SashForm getMainComposite() {
    return this.mainComposite;
  }


  /**
   * @return the formToolkit
   */
  public FormToolkit getFormToolkit() {
    return this.formToolkit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FocusMatrixDataHandler getDataHandler() {
    return getEditorInput().getFmDataHandler();
  }
}
