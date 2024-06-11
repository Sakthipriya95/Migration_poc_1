/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.actions.MonicaReviewActionSet;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.cdr.MonicaErrorMessage;
import com.bosch.caltool.icdm.client.bo.cdr.MonicaFileData;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.MonicaInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaInputModel;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutput;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.review.UserData;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.MonicaReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.monicareportparser.MonitoringToolParser;
import com.bosch.caltool.monicareportparser.data.MonitoringToolOutput;
import com.bosch.caltool.monicareportparser.data.ParameterInfo;
import com.bosch.caltool.monicareportparser.exception.MonicaRptParserException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author dmr1cob
 */
public class MonicaReviewDialog extends AbstractDialog {

  /**
   *
   */
  private static final String NEW_LINE = "\n";

  /**
   * Define table colums
   */
  private GridTableViewer monicaTableViewer;

  /**
   * String constant for select user
   */
  private static final String SELECT_USER = "Select User";
  /**
   * pidc A2l object
   */
  private final PidcA2l pidcA2l;
  /**
   * MoniCa Delta Review
   */
  private final boolean isDeltaReview;
  /**
   * form tool kit
   */
  private FormToolkit formToolkit;
  /**
   * cdrWizardUIModel
   */
  private CDRWizardUIModel cdrWizardUIModel;
  /**
   * text for description control
   */
  private Text descriptions;
  /**
   * decoration for description
   */
  private ControlDecoration txtDescription;
  /**
   * Instance of selected variant
   */
  protected PidcVariant selctedVar;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * Instance of save button
   */
  private Button saveBtn;
  /**
   * Composite composite
   */
  private Composite composite;

  private boolean isDefaultUsrSel = true;

  /**
   * Constant for mandatory msg
   */
  private static final String MSG_MANDATORY = "This field is mandatory.";

  /**
   * Text to display when user tries to modify the non-editable field.
   */
  private static final String EDIT_WARNING_TEXT = "Please use the browse button to modify the text";

  private static final int LIST_ROW_COUNT = 7;

  private final CDRHandler cdrHandler = new CDRHandler();
  /**
   * text for calibration engineer
   */
  private Text calEngText;
  /**
   * text for auditor
   */
  private Text auditor;
  /**
   * button for calibration engineer
   */
  private Button calibrationBrowse;
  /**
   * other participants list
   */
  private List participantsList;
  /**
   * add button for other participants
   */
  private Button addPartsBtn;
  /**
   * delete button for other participants
   */
  private Button partDelButton;

  private String auditorUserName;

  private String calEngUserName;

  /**
   * Array of selected other participants to be removed
   */
  private String[] selOthParts;
  /**
   * Sorted set of selected participants
   */
  private final SortedSet<User> selParticipants = new TreeSet<>();

  private MonicaReviewOutput monicaReviewOutput;

  private MonicaReviewActionSet actionSet;

  private final Set<MonicaFileData> monicaFileDataSet = new HashSet<>();

  private MonicaInputModel monicaInputModel;

  private final java.util.List<MonicaErrorMessage> monicaErrorMsgList = new ArrayList<>();

  private final java.util.List<MonicaErrorMessage> monicaWarnMsgList = new ArrayList<>();

  private static final String POSTFIX_MONICA_SHEET_NAME = "-iCDM_Check";

  private Map<Long, PidcVariant> variantsForVersion;

  private Label validationMsg;

  private Button wpFinished;

  /**
   * @param pidcA2l pidcA2l
   * @param shell shell
   */
  public MonicaReviewDialog(final PidcA2l pidcA2l, final Shell shell, final boolean isDeltaReview) {
    super(shell);
    this.pidcA2l = pidcA2l;
    this.isDeltaReview = isDeltaReview;
    setVaraintForVersions();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("MoniCa Review Report Upload");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);
  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("MoniCa Review Report Upload");

    // Set the message
    setMessage("Upload MoniCa Review report for A2l File " + this.pidcA2l.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.composite = getFormToolkit().createComposite(parent);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createGroups();
    return this.composite;
  }


  /**
   *
   */
  private void createGroups() {
    final Group descGroup = new Group(this.composite, SWT.NONE);
    descGroup.setLayout(new GridLayout());
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    descGroup.setLayoutData(gridData);
    descGroup.setText("Choose inputs for MoniCa Review Report Upload");
    final Composite firstRowComposite = createRowComposite(descGroup);
    createDescriptionControl(firstRowComposite);
    gridData.heightHint = 530;

    final Composite tableComp = new Composite(descGroup, SWT.NONE);
    tableComp.setLayout(new GridLayout(1, false));
    tableComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.validationMsg = new Label(tableComp, SWT.NONE);

    createToolBarActions(tableComp);
    createTable(tableComp);

    final Composite lastRowComposite = createRowComposite(descGroup);
    createCalibrationEngControl(lastRowComposite);
    createAuditorControl(lastRowComposite);
    createParticipantsControl(lastRowComposite);
    createWpFinishedControl(lastRowComposite);

    if (varSelectionNeeded()) {
      this.validationMsg.setText("* Variant selection is mandatory to perform the review.");
      this.validationMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      this.validationMsg.setVisible(false);
    }
  }

  /**
   * @param descGroup
   * @return
   */
  private Composite createRowComposite(final Composite comp) {
    final Composite firstRowComposite = new Composite(comp, SWT.NONE);
    firstRowComposite.setLayout(new GridLayout(3, false));
    firstRowComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    return firstRowComposite;
  }

  /**
   * ICDM 658 This method creates the description control
   *
   * @param workArea
   */

  private void createDescriptionControl(final Composite firstRowComposite) {

    LabelUtil.getInstance().createLabel(firstRowComposite, "Description ");
    this.descriptions = TextUtil.getInstance().createText(firstRowComposite, true, "");
    this.descriptions.addModifyListener(event -> enableDisableOKBtn());
    this.txtDescription = new ControlDecoration(this.descriptions, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtDescription, MSG_MANDATORY);
    GridData txtGridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    this.descriptions.setLayoutData(txtGridData);
    this.descriptions.setEditable(true);
  }

  /**
   *
   */
  private void createTable(final Composite comp) {

    this.monicaTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(comp,
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.monicaTableViewer.setContentProvider(new ArrayContentProvider());
    this.monicaTableViewer.getGrid().setLinesVisible(true);
    this.monicaTableViewer.getGrid().setHeaderVisible(true);
    createColumns();
    addTableSelectionListener();
  }


  private void createColumns() {
    createDcmCol();
    createMonicaCol();
    createSheetCol();
    createVarCol();
    createSelVarCol();
  }

  /**
   *
   */
  private void createDcmCol() {

    final GridViewerColumn dcmFile = new GridViewerColumn(this.monicaTableViewer, SWT.NONE);
    dcmFile.getColumn().setText("DCM File");
    dcmFile.getColumn().setWidth(150);
    ColumnViewerToolTipSupport.enableFor(this.monicaTableViewer, ToolTip.NO_RECREATE);
    dcmFile.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof MonicaFileData) {
          MonicaFileData prov = (MonicaFileData) element;
          return prov.getDcmFileName();
        }
        return "";
      }
    });

  }

  /**
   *
   */
  private void createMonicaCol() {
    final GridViewerColumn monicaFile = new GridViewerColumn(this.monicaTableViewer, SWT.NONE);
    monicaFile.getColumn().setText("MoniCa File");
    monicaFile.getColumn().setWidth(150);
    ColumnViewerToolTipSupport.enableFor(this.monicaTableViewer, ToolTip.NO_RECREATE);
    monicaFile.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof MonicaFileData) {
          MonicaFileData prov = (MonicaFileData) element;
          return prov.getMonicaFileName();
        }
        return "";
      }
    });
  }

  /**
   *
   */
  private void createSheetCol() {
    final GridViewerColumn sheet = new GridViewerColumn(this.monicaTableViewer, SWT.NONE);
    sheet.getColumn().setText("Sheet Name");
    sheet.getColumn().setWidth(150);
    ColumnViewerToolTipSupport.enableFor(this.monicaTableViewer, ToolTip.NO_RECREATE);
    sheet.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof MonicaFileData) {
          MonicaFileData prov = (MonicaFileData) element;
          return prov.getSheetName();
        }
        return "";
      }
    });
  }

  /**
   *
   */
  private void createVarCol() {
    final GridViewerColumn variantName = new GridViewerColumn(this.monicaTableViewer, SWT.NONE);
    variantName.getColumn().setText("Variant");
    variantName.getColumn().setWidth(150);
    ColumnViewerToolTipSupport.enableFor(this.monicaTableViewer, ToolTip.NO_RECREATE);
    variantName.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof MonicaFileData) {
          MonicaFileData prov = (MonicaFileData) element;
          return prov.getVariantName();
        }
        return "";
      }
    });
  }

  private void createSelVarCol() {
    final GridViewerColumn deleteIconCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.monicaTableViewer, 25);
    deleteIconCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return "";
      }

      @Override
      public Image getImage(final Object element) {
        if (varSelectionNeeded()) {
          return ImageManager.INSTANCE.getRegisteredImage(ImageKeys.FOLDER_ICON);
        }
        MonicaReviewDialog.this.actionSet.getActnSelectVariant().setEnabled(false);
        MonicaReviewDialog.this.actionSet.getActnVarCompare().setEnabled(false);
        return ImageManager.INSTANCE.getRegisteredImage(ImageKeys.FOLDER_DIS_ICON);

      }
    });
    addRowSelListener();
  }

  /**
   *
   */
  private void addRowSelListener() {

    this.monicaTableViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {
        final Point point = new Point(event.x, event.y);
        GridItem item = MonicaReviewDialog.this.monicaTableViewer.getGrid().getItem(point);
        if ((item != null) && !item.isDisposed() && (event.button == 1)) {
          final int columnIndex =
              GridTableViewerUtil.getInstance().getTabColIndex(event, MonicaReviewDialog.this.monicaTableViewer);
          if (columnIndex == 4) {
            IStructuredSelection selection =
                (IStructuredSelection) MonicaReviewDialog.this.monicaTableViewer.getSelection();
            fillVariantName(selection);
          }
        }
      }

    });
  }

  /**
   * @param selection instance of IStructuredSelection
   */
  public void fillVariantName(final IStructuredSelection selection) {
    if (!selection.isEmpty() && varSelectionNeeded()) {
      final PidcVariantSelectionDialog variantSelDialog = new PidcVariantSelectionDialog(
          Display.getCurrent().getActiveShell(), MonicaReviewDialog.this.pidcA2l.getId());
      variantSelDialog.setMonicaDialog(this);
      MonicaFileData provider = new MonicaFileData((MonicaFileData) selection.getFirstElement());
      variantSelDialog.setDataProvider(provider);
      variantSelDialog.open();
      final PidcVariant selectedVariant = variantSelDialog.getSelectedVariant();
      if ((null != selectedVariant) && !checkForDuplicateEntryForVariants(selectedVariant, provider)) {
        provider.setVariantName(selectedVariant.getName());
        provider.setVarId(selectedVariant.getId());
        MonicaReviewDialog.this.monicaFileDataSet.remove(selection.getFirstElement());
        MonicaReviewDialog.this.monicaFileDataSet.add(provider);
        // set the variant field
        MonicaReviewDialog.this.getTabViewer().refresh();
        MonicaReviewDialog.this.selctedVar = selectedVariant;
        // enable disable buttons
        enableDisableOKBtn();
      }
    }
  }

  /**
   * @param selectedVariant PidcVariant
   * @param selMonicaFileData MonicaFileData
   * @return boolean
   */
  public boolean checkForDuplicateEntryForVariants(final PidcVariant selectedVariant,
      final MonicaFileData selMonicaFileData) {
    Set<MonicaFileData> tmpMonicaFileDataList = new HashSet<>(this.monicaFileDataSet);
    MonicaFileData tempMoniCaFileData = new MonicaFileData(selMonicaFileData);
    tempMoniCaFileData.setVariantName(selectedVariant.getName());
    tempMoniCaFileData.setVarId(selectedVariant.getId());
    return !tmpMonicaFileDataList.add(tempMoniCaFileData);
  }

  /**
   * To validate Duplicate entry
   *
   * @param selMonicaFileData as input
   * @return boolean value
   */
  public boolean checkForDuplicateEntry(final MonicaFileData selMonicaFileData) {
    Set<MonicaFileData> tmpMonicaFileDataList = new HashSet<>(this.monicaFileDataSet);
    MonicaFileData tempMoniCaFileData = new MonicaFileData(selMonicaFileData);
    return !tmpMonicaFileDataList.add(tempMoniCaFileData);
  }


  /**
   * This method creates Attributes Section ToolBar actions
   */
  private void createToolBarActions(final Composite comp) {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(comp);
    toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
    this.actionSet = new MonicaReviewActionSet(MonicaReviewDialog.this);
    this.actionSet.createFileAddAction(toolBarManager);
    this.actionSet.createFileEditAction(toolBarManager);
    this.actionSet.deleteFileAction(toolBarManager);
    this.actionSet.createAddFolderSelectionOption(toolBarManager);
    this.actionSet.selectVariantAction(toolBarManager);
    this.actionSet.variantCompareAction(toolBarManager);
    toolBarManager.update(true);
  }


  /**
   * This method adds selection listener
   */
  public void addTableSelectionListener() {
    this.monicaTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) MonicaReviewDialog.this.monicaTableViewer.getSelection();
        if (null != selection) {
          if (selection.size() > 1) {
            MonicaReviewDialog.this.actionSet.getEditFileAction().setEnabled(false);
            MonicaReviewDialog.this.actionSet.getDeleteFileAction().setEnabled(true);
          }
          else {
            MonicaReviewDialog.this.actionSet.getEditFileAction().setEnabled(true);
            MonicaReviewDialog.this.actionSet.getDeleteFileAction().setEnabled(true);
            MonicaReviewDialog.this.actionSet.getActnSelectVariant().setEnabled(varSelectionNeeded());

          }
        }
      }
    });
  }


  /**
   * Create Calibration Engineer controls
   *
   * @param lastRowComposite composite
   */
  private void createCalibrationEngControl(final Composite lastRowComposite) {
    LabelUtil.getInstance().createLabel(lastRowComposite, "Calibration Engineer ");
    this.calEngText = TextUtil.getInstance().createText(lastRowComposite, true, "");
    try {
      this.calEngText.setText(this.cdrHandler.getCurrentApicUser().getDescription());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    GridData txtGridData = new GridData(SWT.FILL, SWT.NONE, true, false);
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.widthHint = 350;
    this.calEngText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    this.calEngText.setLayoutData(txtGridData);
    this.calEngText.setEditable(false);
    this.calEngText.addKeyListener(keyListener());
    this.calibrationBrowse = new Button(lastRowComposite, SWT.NONE);
    this.calibrationBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    this.calibrationBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    this.calibrationBrowse.addSelectionListener(calEngSelListener());
    if (this.isDefaultUsrSel) {
      try {
        UserData userData = new UserData();
        userData.setSelCalEngineerId(this.cdrHandler.getCurrentApicUser().getId());
        setCalEngUserName(this.cdrHandler.getCurrentApicUser().getName());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    this.calibrationBrowse.setEnabled(true);
    this.calibrationBrowse.setToolTipText("Select Calibration Engineer");
  }


  /**
   * @param workArea parent composite
   */
  private void createAuditorControl(final Composite lastRowComposite) {
    LabelUtil.getInstance().createLabel(lastRowComposite, "Auditor ");
    this.auditor = TextUtil.getInstance().createText(lastRowComposite, true, "");
    this.auditor.addKeyListener(keyListener());
    ControlDecoration txtAuditorNameDec = new ControlDecoration(this.auditor, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(txtAuditorNameDec, MSG_MANDATORY);
    this.auditor.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    final GridData auditorData = new GridData(SWT.FILL, SWT.NONE, true, false);
    auditorData.widthHint = new PixelConverter(this.auditor).convertWidthInCharsToPixels(25);
    this.auditor.setLayoutData(auditorData);
    this.auditor.setEditable(false);
    Button auditorBrowse = new Button(lastRowComposite, SWT.PUSH);
    auditorBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    auditorBrowse.setToolTipText("Select Auditor");
    auditorBrowse.addSelectionListener(auditSelListener());
  }


  private void createWpFinishedControl(final Composite lastRowComposite) {
    LabelUtil.getInstance().createLabel(lastRowComposite, "If possible,mark Work Package as finished");
    this.wpFinished = new Button(lastRowComposite, SWT.CHECK);
    this.wpFinished.setSelection(true);
    this.wpFinished.setEnabled(true);
    this.wpFinished
        .setToolTipText("The Work Package is marked as finished if all labels in the WP have maturity level 75%");

  }

  /**
   * @param workArea parent Composite
   */
  private void createParticipantsControl(final Composite lastRowComposite) {
    final Label participants = new Label(lastRowComposite, SWT.NONE);
    participants.setText("Other Participants : ");
    participants.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    this.participantsList = new List(lastRowComposite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
    final GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.heightHint = LIST_ROW_COUNT * this.participantsList.getItemHeight();
    this.participantsList.setLayoutData(data);
    participantKeyListener();
    // participantBtComposite for aligining the participants selection and delete buttons
    final Composite participantBtComp = new Composite(lastRowComposite, SWT.NONE);
    final GridLayout layoutPartComp = new GridLayout();
    layoutPartComp.numColumns = 1;
    layoutPartComp.makeColumnsEqualWidth = false;
    layoutPartComp.marginWidth = 1;
    layoutPartComp.marginTop = 1;
    participantBtComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    participantBtComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    participantBtComp.setLayout(layoutPartComp);
    // add and delete participants button
    createParticipantsButtons(participantBtComp);
  }


  /**
   * check delta Review and Set values
   */
  private void checkdeltaReview() {
    if (this.isDeltaReview && (null != getCdrWizardUIModel().getParentResultId())) {
      MonicaFileData monicaFileData = new MonicaFileData();

      Long selectVarId = getCdrWizardUIModel().getSelectedPidcVariantId();
      this.descriptions.setText(getCdrWizardUIModel().getDescription());
      String variantName = getCdrWizardUIModel().getSelectedPidcVariantName() == null ? ""
          : getCdrWizardUIModel().getSelectedPidcVariantName();

      String monicaFilePath = getCdrWizardUIModel().getSelMonicaFilesPath().stream()
          .filter(filePath -> filePath.contains(".xls") || filePath.contains(".XLS"))
          .filter(filePath -> filePath.contains(".xlsx") || filePath.contains(".XLSX")).collect(Collectors.joining());

      String sheetName = getCdrWizardUIModel().getSelMoniCaSheetName();

      String dcmFilePath = getCdrWizardUIModel().getSelFilesPath().stream()
          .filter(filePath -> filePath.contains(".DCM") || filePath.contains(".dcm")).collect(Collectors.joining());

      this.calEngUserName = getCdrWizardUIModel().getCalEngUserName();
      this.calEngText.setText(
          getCdrWizardUIModel().getCalEngUserFullName() == null ? "" : getCdrWizardUIModel().getCalEngUserFullName());
      this.auditorUserName = getCdrWizardUIModel().getAuditorUserName();
      this.auditor.setText(getCdrWizardUIModel().getAuditorUserFullName());
      getCdrWizardUIModel().getParticipantUserFullNameList()
          .forEach(participantName -> this.participantsList.add(participantName));

      File monicaFile = new File(monicaFilePath);
      File dcmFile = new File(dcmFilePath);
      monicaFileData.setMonicaFileName(monicaFile.getName());
      monicaFileData.setMonicaFilePath(monicaFilePath);
      monicaFileData.setDcmFileName(dcmFile.getName());
      monicaFileData.setDcmFIlePath(dcmFilePath);
      monicaFileData.setSheetName(sheetName);
      monicaFileData.setVariantName(variantName);
      monicaFileData.setVarId(selectVarId);
      monicaFileData.setIndex(this.monicaTableViewer.getGrid().getItemCount() + 1);
      monicaFileData.setDeltaReview(true);

      MonitoringToolParser monitoringToolParser = new MonitoringToolParser(CDMLogger.getInstance());
      java.util.List<String> allSheetNames = monitoringToolParser.getAllSheetNames(monicaFilePath);

      allSheetNames = allSheetNames.stream().filter(sheetNames -> sheetNames.contains(POSTFIX_MONICA_SHEET_NAME))
          .collect(Collectors.toList());

      monicaFileData.setAllMonicaSheets(allSheetNames);

      getMonicaFileDataSet().add(monicaFileData);
      getTabViewer().setInput(getMonicaFileDataSet());
      getTabViewer().refresh();
      enableDisableOKBtn();
    }
  }


  /**
   * @param participantsList
   * @param participantBtComp child composite
   */
  private void createParticipantsButtons(final Composite participantBtComp) {
    addButton(participantBtComp);
    delButton(participantBtComp);
  }

  /**
   * @param participantBtComp
   * @param addButtonImage
   */
  private void addButton(final Composite participantBtComp) {
    final Image addButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.ADD_16X16);
    this.addPartsBtn = new Button(participantBtComp, SWT.PUSH);
    this.addPartsBtn.setImage(addButtonImage);
    this.addPartsBtn.setToolTipText("Add Participants");
    this.addPartsBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog partsDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            "Add User", "Add user", "Add user", "Add", true, false);
        partsDialog.setSelectedMultipleUser(null);
        partsDialog.open();
        ArrayList<User> selectedUsers = (ArrayList<User>) partsDialog.getSelectedMultipleUser();
        if ((selectedUsers != null) && !selectedUsers.isEmpty()) {
          final Iterator<User> users = selectedUsers.iterator();
          while (users.hasNext()) {
            final User selectedUser = users.next();
            final String selUserName = selectedUser.getDescription();
            if ((getParticipantsList().indexOf(selUserName) == -1) &&
                (!ProjectDataSelectionWizardPage.STR_NULL_NULL.equalsIgnoreCase(selUserName))) {
              getSelParticipants().add(selectedUser);
              getParticipantsList().add(selUserName);
            }
          }
          // sorting of list items
          final String[] items = getParticipantsList().getItems();
          java.util.Arrays.sort(items);
          getParticipantsList().setItems(items);
        }
      }
    });
  }


  /**
   * @param participantBtComp
   */
  private void delButton(final Composite participantBtComp) {
    final Image deleteButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
    this.partDelButton = new Button(participantBtComp, SWT.PUSH);
    this.partDelButton.setImage(deleteButtonImage);
    this.partDelButton.setEnabled(false);
    this.partDelButton.setToolTipText("Remove Participants");
    this.partDelButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if ((getSelOthParts() != null) && (getSelOthParts().length != 0)) {
          removeSelectedParticipant();
        }
        if (getParticipantsList().getItemCount() == 0) {
          getSelParticipants().clear();
        }
        getPartDelButton().setEnabled(false);
      }

    });
  }


  /**
   * Remove selected participant
   */
  public void removeSelectedParticipant() {
    for (String partiName : getSelOthParts()) {
      final Iterator<User> participants = getSelParticipants().iterator();
      getParticipantsList().remove(partiName);
      while (participants.hasNext()) {
        final User user = participants.next();
        final String userName = user.getDescription();
        if (userName.equalsIgnoreCase(partiName)) {
          participants.remove();
        }
        break;
      }
    }
  }

  /**
   * @return
   */
  private KeyListener keyListener() {
    return new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyEvent) {
        if (!(((keyEvent.stateMask & SWT.CTRL) == SWT.CTRL) && (keyEvent.keyCode == 262144))) {
          MessageDialogUtils.getWarningMessageDialog(SELECT_USER, EDIT_WARNING_TEXT);
        }
      }

      @Override
      public void keyPressed(final KeyEvent keyEvent) {
        // Implementation in KeyReleased() method is sufficient
      }
    };
  }

  /**
   * @return SelectionAdapter
   */
  private SelectionAdapter calEngSelListener() {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog calibratnDialog =
            new UserSelectionDialog(Display.getCurrent().getActiveShell(), ProjectDataSelectionWizardPage.getSelcUser(),
                "Select Calibration Engineer", ProjectDataSelectionWizardPage.getSelcUser(), "Select", false, false);
        calibratnDialog.setSelectedUser(null);
        calibratnDialog.open();
        final User selectedUser = calibratnDialog.getSelectedUser();
        if (selectedUser != null) {
          final String selUserName = selectedUser.getDescription();
          if (!ProjectDataSelectionWizardPage.STR_NULL_NULL.equalsIgnoreCase(selUserName)) {
            setCalEngUserName(selectedUser.getName());
            getCalEngText().setText(selUserName);
            setDefaultUsrSel(false);
            enableDisableOKBtn();
          }
        }
      }
    };
  }

  /**
   * @return SelectionAdapter
   */
  private SelectionAdapter auditSelListener() {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final UserSelectionDialog selAuditorDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            SELECT_USER, "Select Auditor", SELECT_USER, "Select", false, true);
        selAuditorDialog.setSelectedUser(null);
        selAuditorDialog.open();
        final User selectedUser = selAuditorDialog.getSelectedUser();
        if (selectedUser != null) {
          final String selUserName = selectedUser.getDescription();
          if (!"null,null".equalsIgnoreCase(selUserName)) {
            getAuditor().setText(selUserName);
            setAuditorUserName(selectedUser.getName());
            enableDisableOKBtn();
          }
        }
      }

    };
  }

  /**
   * participantKeyListener
   */
  private void participantKeyListener() {
    this.participantsList.addListener(SWT.Selection, event -> {
      getPartDelButton().setEnabled(true);
      setSelOthParts(getParticipantsList().getSelection());
    });
    this.participantsList.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {

        final char character = event.character;
        if (character == SWT.DEL) {
          deleteOtherParticipants();
        }
      }

    });
  }


  /**
   *
   */
  private void deleteOtherParticipants() {
    if ((getSelOthParts() != null) && (getSelOthParts().length != 0)) {
      for (String partiName : getSelOthParts()) {
        getParticipantsList().remove(partiName);
        final Iterator<User> othParticipants = getSelParticipants().iterator();
        while (othParticipants.hasNext()) {
          final User user = othParticipants.next();
          final String userName = user.getDescription();
          if (userName.equalsIgnoreCase(partiName)) {
            othParticipants.remove();
            break;
          }
        }
      }
    }
    if (getParticipantsList().getItemCount() == 0) {
      getSelParticipants().clear();
    }
    getPartDelButton().setEnabled(false);
  }

  /**
   * enable disable ok button
   */
  public void enableDisableOKBtn() {

    this.saveBtn.setEnabled(validateFields());

  }

  /**
   * validate feilds for ok button
   *
   * @return
   */
  private boolean validateFields() {

    return (areAllVariantsAndDCMDataSet() && !"".equals(this.descriptions.getText().trim()) &&
        !"".equals(this.auditor.getText().trim()) && !"".equals(this.calEngText.getText().trim()));

  }

  /**
   *
   */
  private boolean areAllVariantsAndDCMDataSet() {
    Set<MonicaFileData> monicaDataSet = MonicaReviewDialog.this.getMonicaFileDataSet();
    if (!monicaDataSet.isEmpty()) {
      for (MonicaFileData monicaFileData : monicaDataSet) {
        if (varSelectionNeeded() &&
            ((null == monicaFileData.getVariantName()) || "".equalsIgnoreCase(monicaFileData.getVariantName()))) {
          this.validationMsg.setVisible(true);
          return false;
        }
        if (isDcmFileNameEmpty(monicaFileData) ||
            (isMonicaFileNameEmpty(monicaFileData) || isSheetNameEmpty(monicaFileData))) {
          this.validationMsg.setVisible(true);
          return false;

        }
      }
      this.validationMsg.setVisible(false);
      return true;
    }
    return false;
  }

  /**
   * @param monicaFileData
   * @return
   */
  private boolean isSheetNameEmpty(final MonicaFileData monicaFileData) {
    return (null == monicaFileData.getSheetName()) || "".equalsIgnoreCase(monicaFileData.getSheetName());
  }

  /**
   * @param monicaFileData
   * @return
   */
  private boolean isMonicaFileNameEmpty(final MonicaFileData monicaFileData) {
    return (null == monicaFileData.getMonicaFileName()) || "".equalsIgnoreCase(monicaFileData.getMonicaFileName());
  }

  /**
   * @param monicaFileData
   * @return
   */
  private boolean isDcmFileNameEmpty(final MonicaFileData monicaFileData) {
    return (null == monicaFileData.getDcmFileName()) || "".equalsIgnoreCase(monicaFileData.getDcmFileName());
  }

  /**
   * @return boolean value
   */
  public boolean varSelectionNeeded() {
    return this.variantsForVersion.size() > 0;
  }


  private void setVaraintForVersions() {
    try {
      this.variantsForVersion =
          new PidcVariantServiceClient().getVariantsForVersion(MonicaReviewDialog.this.pidcA2l.getPidcVersId(), false);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Submit", true);
    this.saveBtn.setEnabled(false);
    // create button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    checkdeltaReview();
  }

  @Override
  protected void okPressed() {
    if (CommonUtils.isEqualIgnoreCase(this.auditor.getText(), this.calEngText.getText())) {
      CDMLogger.getInstance().errorDialog(
          "For official reviews, the 'Calibration Engineer' and 'Auditor' should be different users",
          Activator.PLUGIN_ID);
      this.saveBtn.setEnabled(false);
      return;
    }
    // Clearig the Warn And Error Message
    this.monicaWarnMsgList.clear();
    this.monicaErrorMsgList.clear();
    // Set MonicaInputModel as service input
    this.monicaInputModel = setMonReviewInputData();
    // Execute the MoniCa Review
    submitWithProgressBar();
    // Fill Warning and Error Message if any
    fillWarnAndErrorMessage();
    // To Display Warning Message
    displayWarningMessages();
    // To Display Error Message
    if (!this.monicaErrorMsgList.isEmpty()) {
      displayErrorMessages();
      return;
    }

    // To open review result for completed reviews
    if (this.monicaReviewOutput != null) {
      for (MonicaReviewOutputData monicaReviewOutData : this.monicaReviewOutput.getMonicaReviewOutputDataList()) {
        // Check WP finished
        if (this.wpFinished.getSelection()) {
          new CDRReviewResultServiceClient().updateWorkpackageStatus(monicaReviewOutData.getCdrReviewResult());
        }
        new CdrActionSet().openRvwRestEditorBasedOnObjInstance(monicaReviewOutData);
      }
      super.okPressed();
    }
  }

  /**
   * Fill the warning message
   */
  private void fillWarnAndErrorMessage() {
    if (null != this.monicaReviewOutput) {
      for (MonicaReviewOutputData monicaReviewOutputData : this.monicaReviewOutput.getMonicaReviewOutputDataList()) {
        if ((null != monicaReviewOutputData) && (null != monicaReviewOutputData.getMonicaInputData())) {
          addMonicaWarnAndErrorMsg(monicaReviewOutputData);
        }
      }
    }
  }

  /**
   * @param monicaReviewOutputData
   */
  public void addMonicaWarnAndErrorMsg(final MonicaReviewOutputData monicaReviewOutputData) {
    if ((null != monicaReviewOutputData.getWarningMsg()) && !monicaReviewOutputData.getWarningMsg().isEmpty()) {
      addMonicaWarnMessage(monicaReviewOutputData.getMonicaInputData(), monicaReviewOutputData.getWarningMsg());
    }
    if ((null != monicaReviewOutputData.getErrorMsg()) && !monicaReviewOutputData.getErrorMsg().isEmpty()) {
      addMonicaErrorMessage(monicaReviewOutputData.getMonicaInputData(), monicaReviewOutputData.getErrorMsg());
    }
  }

  /**
   * @param monicaReviewOutputData
   */
  private void addMonicaWarnMessage(final MonicaInputData monicaInputData, final String warnMsg) {
    MonicaErrorMessage warnMessageObj = new MonicaErrorMessage();
    warnMessageObj.setMonicaFileName(monicaInputData.getMonicaExcelFileName());
    warnMessageObj.setSheetName(monicaInputData.getSelMoniCaSheet());
    warnMessageObj.setReason(warnMsg);
    this.monicaWarnMsgList.add(warnMessageObj);
  }

  /**
   * Fill the error message
   */
  private void displayErrorMessages() {
    StringBuilder errMsg = new StringBuilder();
    errMsg.append("The upload has not been successful for those reviews");

    for (MonicaErrorMessage monicaErrorMessage : this.monicaErrorMsgList) {
      errMsg.append(NEW_LINE);
      errMsg.append("MoniCa File : " + monicaErrorMessage.getMonicaFileName());
      errMsg.append(NEW_LINE);
      errMsg.append("Sheet Name : " + monicaErrorMessage.getSheetName());
      errMsg.append(NEW_LINE);
      errMsg.append("Reason : " + monicaErrorMessage.getReason());
    }

    CDMLogger.getInstance().errorDialog(errMsg.toString(), Activator.PLUGIN_ID);
  }

  private void displayWarningMessages() {
    if (!this.monicaWarnMsgList.isEmpty()) {
      StringBuilder errMsg = new StringBuilder();
      errMsg.append(
          "Here you see all parameters missing in the Excel and/or DCM file. A review result with parameters existing in both files will be created nevertheless.");

      for (MonicaErrorMessage monicaErrorMessage : this.monicaWarnMsgList) {
        errMsg.append(NEW_LINE);
        errMsg.append("MoniCa File : " + monicaErrorMessage.getMonicaFileName());
        errMsg.append(NEW_LINE);
        errMsg.append("Sheet Name : " + monicaErrorMessage.getSheetName());
        errMsg.append(NEW_LINE);
        errMsg.append("Reason : " + monicaErrorMessage.getReason());
      }

      CDMLogger.getInstance().warnDialog(errMsg.toString(), Activator.PLUGIN_ID);
    }
  }

  /**
   * Fill the Monica Review Input Data
   */
  private MonicaInputModel setMonReviewInputData() {
    ArrayList<MonicaInputData> monicaInputDataList = new ArrayList<>();

    MonicaInputModel monicaInputMod = new MonicaInputModel();
    monicaInputMod.setDescription(this.descriptions.getText());
    monicaInputMod.setPidcA2lId(this.pidcA2l.getId());
    monicaInputMod.setAudUserName(this.auditorUserName);
    monicaInputMod.setCalEngUserName(this.calEngUserName);
    monicaInputMod.setOwnUserName(this.calEngUserName);
    monicaInputMod.setReviewParticipants(getSelParticipants().stream().map(User::getName).collect(Collectors.toList()));

    for (MonicaFileData monicaFileData : this.monicaFileDataSet) {
      MonicaInputData monicaInputData = new MonicaInputData();
      monicaInputData.setDcmFileName(monicaFileData.getDcmFileName());
      monicaInputData.setDcmFilePath(monicaFileData.getDcmFIlePath());
      if (monicaFileData.isDeltaReview() && this.isDeltaReview && (null != getCdrWizardUIModel().getParentResultId())) {
        monicaInputData.setDeltaReviewType(DELTA_REVIEW_TYPE.DELTA_REVIEW.getDbType());
        monicaInputData.setDeltaReview(this.isDeltaReview);
        monicaInputData.setOrgResultId(getCdrWizardUIModel().getParentResultId());
      }
      monicaInputData.setMonicaExcelFileName(monicaFileData.getMonicaFileName());
      monicaInputData.setMonicaExcelFilePath(monicaFileData.getMonicaFilePath());
      monicaInputData.setSelMoniCaSheet(monicaFileData.getSheetName());
      if (null != monicaFileData.getVarId()) {
        monicaInputData.setVariantId(monicaFileData.getVarId());
      }

      monicaInputDataList.add(monicaInputData);
    }

    monicaInputMod.setMonicaInputDataList(monicaInputDataList);

    return monicaInputMod;

  }

  /**
   *
   */
  private void submitWithProgressBar() {
    Display.getDefault().syncExec(() -> {
      ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      try {
        dialog.run(true, true, monitor -> {
          monitor.beginTask("Uploading MoniCa Review Report...", 100);
          monitor.worked(20);
          MonicaReviewDialog.this.monicaReviewOutput = executeReview();
          monitor.worked(100);
          monitor.done();
        });
      }
      catch (InvocationTargetException | InterruptedException e) {
        CDMLogger.getInstance().error("Error in invoking thread to open progress bar for MoniCa Review Report Upload !",
            e);
        Thread.currentThread().interrupt();
      }

    });
  }


  /**
   * @param monicaInputData
   * @param errorMsg
   */
  private void addMonicaErrorMessage(final MonicaInputData monicaInputData, final String errorMsg) {
    MonicaErrorMessage errorMessageObj = new MonicaErrorMessage();
    errorMessageObj.setMonicaFileName(monicaInputData.getMonicaExcelFileName());
    errorMessageObj.setSheetName(monicaInputData.getSelMoniCaSheet());
    errorMessageObj.setReason(errorMsg);
    this.monicaErrorMsgList.add(errorMessageObj);
  }

  /**
   * executeReview
   */
  private MonicaReviewOutput executeReview() {

    java.util.List<MonicaInputData> invalidMonicaInputData = new ArrayList<>();
    boolean proceedUpload = true;
    for (MonicaInputData monicaInputData : this.monicaInputModel.getMonicaInputDataList()) {
      boolean isCommentPresent = false;
      try (FileInputStream monicaExcelStream = new FileInputStream(monicaInputData.getMonicaExcelFilePath())) {
        MonitoringToolParser monitoringToolParser = new MonitoringToolParser(CDMLogger.getInstance());
        monitoringToolParser.setInputStream(monicaExcelStream);
        // parse the sheet
        monitoringToolParser.parse(monicaInputData.getSelMoniCaSheet());
        // get the output
        MonitoringToolOutput output = monitoringToolParser.getOutput();
        // Set the values
        java.util.List<MonicaReviewData> monicaReviewDataList = new ArrayList<>();

        for (Map.Entry<String, ParameterInfo> labelParamInfo : output.getParamInfoMap().entrySet()) {
          MonicaReviewData monicaReviewData = new MonicaReviewData();
          monicaReviewData.setLabel(labelParamInfo.getValue().getLabelName());
          monicaReviewData
              .setStatus(labelParamInfo.getValue().getReviewStatus(labelParamInfo.getValue().getStatus()).toString());
          monicaReviewData.setComment(labelParamInfo.getValue().getComment());
          if (null != labelParamInfo.getValue().getCellCommentForParam()) {
            isCommentPresent = true;
            proceedUpload = false;
          }
          monicaReviewData.setReviewedBy(labelParamInfo.getValue().getReviewedBy());
          monicaReviewDataList.add(monicaReviewData);
        }

        monicaInputData.setMonicaObject(monicaReviewDataList);
        checkForErrorMessagesIfAny(invalidMonicaInputData, monicaInputData, isCommentPresent, monicaReviewDataList);
      }
      catch (MonicaRptParserException | IOException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        String errorMsg = exp.getMessage() + ".Please select a valid MoniCa report file for Upload";
        addMonicaErrorMessage(monicaInputData, errorMsg);
        this.saveBtn.setEnabled(false);
        invalidMonicaInputData.add(monicaInputData);
      }
      catch (NoSuchElementException exp1) {
        CDMLogger.getInstance().error(exp1.getLocalizedMessage(), exp1, Activator.PLUGIN_ID);
        // Added this error to handle empty cells in the monica excel report.
        String errorMsg =
            "There's an empty/invalid cell in the excel report. If you can't find an error, contact the iCDM hotline.";
        addMonicaErrorMessage(monicaInputData, errorMsg);
        this.saveBtn.setEnabled(false);
        invalidMonicaInputData.add(monicaInputData);
      }
      catch (Exception exp2) {
        CDMLogger.getInstance().error(exp2.getLocalizedMessage(), exp2, Activator.PLUGIN_ID);
        String errorMsg =
            "There's a problem with the files you want to use for the Review. Please contact the iCDM hotline.";
        addMonicaErrorMessage(monicaInputData, errorMsg);
        this.saveBtn.setEnabled(false);
        invalidMonicaInputData.add(monicaInputData);
      }
    }

    for (MonicaInputData monicaInputData : invalidMonicaInputData) {
      this.monicaInputModel.getMonicaInputDataList().remove(monicaInputData);
    }

    MonicaReviewServiceClient monicaReviewServiceClient = new MonicaReviewServiceClient();
    MonicaReviewOutput monicaReviewOut = null;
    try {
      monicaReviewOut = executeMonicaReportUpload(proceedUpload, monicaReviewServiceClient);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return null;
    }
    return monicaReviewOut;
  }

  /**
   * @param proceedUpload
   * @param monicaReviewServiceClient
   * @param monicaReviewOut
   * @return
   * @throws ApicWebServiceException
   */
  private MonicaReviewOutput executeMonicaReportUpload(final boolean proceedUpload,
      final MonicaReviewServiceClient monicaReviewServiceClient)
      throws ApicWebServiceException {
    MonicaReviewOutput monicaReviewOut = null;
    if (proceedUpload) {
      monicaReviewOut = monicaReviewServiceClient.executeMonicaReviewInternal(this.monicaInputModel);
      if (!isAnyReviewFailed(monicaReviewOut)) {
        CDMLogger.getInstance().infoDialog("MoniCa reports have been created successfully", Activator.PLUGIN_ID);
      }
    }
    return monicaReviewOut;
  }

  /**
   * @param invalidMonicaInputData
   * @param monicaInputData
   * @param isCommentPresent
   * @param monicaReviewDataList
   */
  private void checkForErrorMessagesIfAny(final java.util.List<MonicaInputData> invalidMonicaInputData,
      final MonicaInputData monicaInputData, final boolean isCommentPresent,
      final java.util.List<MonicaReviewData> monicaReviewDataList) {
    if (isCommentPresent && !monicaReviewDataList.isEmpty()) {
      String errorMsg =
          "'Comments/Notes' are present in the STATUS column of the sheet. Please remove 'Comments/Notes' from the sheet and try again.";
      addMonicaErrorMessage(monicaInputData, errorMsg);
      invalidMonicaInputData.add(monicaInputData);
    }
    if (monicaReviewDataList.isEmpty()) {
      String errorMsg =
          "No valid parameters present in the MoniCa Report file.Please select a valid MoniCa report file for Upload";
      addMonicaErrorMessage(monicaInputData, errorMsg);
      this.saveBtn.setEnabled(false);
    }
  }

  private boolean isAnyReviewFailed(final MonicaReviewOutput monicaReviewOut) {
    java.util.List<MonicaReviewOutputData> monicaReviewOutList = monicaReviewOut.getMonicaReviewOutputDataList();
    for (MonicaReviewOutputData Outputdata : monicaReviewOutList) {
      if (Outputdata.isReviewFailed()) {
        return true;
      }
    }
    return false;

  }

  /**
   * @return the auditor
   */
  public Text getAuditor() {
    return this.auditor;
  }

  /**
   * @param auditor the auditor to set
   */
  public void setAuditor(final Text auditor) {
    this.auditor = auditor;
  }

  /**
   * @return the auditorUserName
   */
  public String getAuditorUserName() {
    return this.auditorUserName;
  }

  /**
   * @param auditorUserName the auditorUserName to set
   */
  public void setAuditorUserName(final String auditorUserName) {
    this.auditorUserName = auditorUserName;
  }

  /**
   * @return the calEngUserName
   */
  public String getCalEngUserName() {
    return this.calEngUserName;
  }

  /**
   * @param calEngUserName the calEngUserName to set
   */
  public void setCalEngUserName(final String calEngUserName) {
    this.calEngUserName = calEngUserName;
  }

  /**
   * @return the calEngText
   */
  public Text getCalEngText() {
    return this.calEngText;
  }

  /**
   * @param calEngText the calEngText to set
   */
  public void setCalEngText(final Text calEngText) {
    this.calEngText = calEngText;
  }

  /**
   * @return the isDefaultUsrSel
   */
  public boolean isDefaultUsrSel() {
    return this.isDefaultUsrSel;
  }


  /**
   * @param isDefaultUsrSel the isDefaultUsrSel to set
   */
  public void setDefaultUsrSel(final boolean isDefaultUsrSel) {
    this.isDefaultUsrSel = isDefaultUsrSel;
  }

  /**
   * @return the calibrationBrowse
   */
  public Button getCalibrationBrowse() {
    return this.calibrationBrowse;
  }

  /**
   * @param calibrationBrowse the calibrationBrowse to set
   */
  public void setCalibrationBrowse(final Button calibrationBrowse) {
    this.calibrationBrowse = calibrationBrowse;
  }


  /**
   * @return the addPartsBtn
   */
  public Button getAddPartsBtn() {
    return this.addPartsBtn;
  }


  /**
   * @param addPartsBtn the addPartsBtn to set
   */
  public void setAddPartsBtn(final Button addPartsBtn) {
    this.addPartsBtn = addPartsBtn;
  }


  /**
   * @return the partDelButton
   */
  public Button getPartDelButton() {
    return this.partDelButton;
  }


  /**
   * @param partDelButton the partDelButton to set
   */
  public void setPartDelButton(final Button partDelButton) {
    this.partDelButton = partDelButton;
  }


  /**
   * @return the selOthParts
   */
  public String[] getSelOthParts() {
    return this.selOthParts;
  }


  /**
   * @param selOthParts the selOthParts to set
   */
  public void setSelOthParts(final String[] selOthParts) {
    this.selOthParts = selOthParts;
  }


  /**
   * @return the participantsList
   */
  public List getParticipantsList() {
    return this.participantsList;
  }


  /**
   * @param participantsList the participantsList to set
   */
  public void setParticipantsList(final List participantsList) {
    this.participantsList = participantsList;
  }


  /**
   * @return the selParticipants
   */
  public SortedSet<User> getSelParticipants() {
    return this.selParticipants;
  }


  /**
   * @return the cdrHandler
   */
  public CDRHandler getCdrHandler() {
    return this.cdrHandler;
  }


  /**
   * @return the descriptions
   */
  public Text getDescriptions() {
    return this.descriptions;
  }


  /**
   * @param descriptions the descriptions to set
   */
  public void setDescriptions(final Text descriptions) {
    this.descriptions = descriptions;
  }


  /**
   * @return the txtDescription
   */
  public ControlDecoration getTxtDescription() {
    return this.txtDescription;
  }


  /**
   * @param txtDescription the txtDescription to set
   */
  public void setTxtDescription(final ControlDecoration txtDescription) {
    this.txtDescription = txtDescription;
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
    this.cdrWizardUIModel = cdrWizardUIModel;
  }

  /**
   * @return the monicaTableViewer
   */
  public GridTableViewer getTabViewer() {
    return this.monicaTableViewer;
  }

  /**
   * @return the monicaFileDataList
   */
  public Set<MonicaFileData> getMonicaFileDataSet() {
    return this.monicaFileDataSet;
  }

  /**
   * @return the submit button
   */
  public Button getSaveBtn() {
    return this.saveBtn;
  }

  /**
   * @return pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }

  /**
   *
   */
  public void getMinimizedShell() {
    getShell().pack();
    getShell().setMinimized(true);
  }

}
