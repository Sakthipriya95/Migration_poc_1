/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.DownloadCompliRvwInputDataAction;
import com.bosch.caltool.cdr.ui.actions.ImportCompliRvwInputDataAction;
import com.bosch.caltool.cdr.ui.adapter.CompliRvwGenBtnSelAdapter;
import com.bosch.caltool.cdr.ui.views.providers.HexFileSelectionRowProvider;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.SdomPverServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Class to upload the hex file path into a dialog and checks for compliance review
 *
 * @author svj7cob
 */
public class CompliReviewDialog extends AbstractDialog {

  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * save button
   */
  private Button generateBtn;
  /**
   * report Error Button to Send Mail to hotline
   */
  private Button reportErrorBtn;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * browse button image
   */
  private Image browseIcon;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * a2l name text
   */
  private Text a2lNameText;
  /**
   * zip export file path
   */
  private Text zipExportPathText;
  /**
   * selc a2l file
   */
  protected String a2lFilePath;
  /**
   * selc a2l file
   */
  private PidcA2l pidcA2l;
  /**
   * the given a2l file
   */
  private File file;

  /**
   * the given a2l file
   */
  private boolean isNONSDOM;

  /**
   * Width of dialog
   */
  private static final int WIDTH_OF_DIALOG = 1200;
  /**
   * Height of dialog
   */
  private static final int HEIGHT_OF_DIALOG = 1000;

  /**
   * Initial state of Button to generate the zip report
   */
  public static final String GENERATE_BUTTON_CONSTANT = "Execute";

  private GridTableViewer tabViewer;

  private Text pverNameText;

  private Text pverVariantText;

  private Text pverRevisionText;

  private Text webFlowIdText;

  private Text pidcVersionText;
  protected PidcVersion selPidcVrsn;
  private final List<HexFileSelectionRowProvider> hexFileRowProviderList = new ArrayList<>();

  private PidcVersion pidcVersion;
  private PidcVariant pidcVariant;
  /**
   * Default Text of HEX File path
   */
  private static final String DEFAULT_TEXT_HEX_PATH = "<Choose HEX File>";
  /**
   * Default Text of Pidc Version File path
   */
  private static final String DEFAULT_TEXT_PIDC_VERS = "<Choose Pidc Version/Variant>";
  private Button varBrowseBtn;

  private Button revBrowseBtn;

  private boolean isProcessFlagSet = false;

  private CompliReviewUsingHexData compliHexData;

  private String executionId;

  private String sid;

  private String errMsg;

  private String errCode;

  private boolean isCompliReportStartedFromA2l;

  /**
   * @return the isNONSDOM
   */
  public boolean isNONSDOM() {
    return this.isNONSDOM;
  }

  /**
   * @return the compliHexData
   */
  public CompliReviewUsingHexData getCompliHexData() {
    return this.compliHexData;
  }


  /**
   * @param compliHexData the compliHexData to set
   */
  public void setCompliHexData(final CompliReviewUsingHexData compliHexData) {
    this.compliHexData = compliHexData;
  }


  /**
   * Key - dst name Value - variant name
   */
  private final ConcurrentHashMap<String, String> dstNameMap = new ConcurrentHashMap<String, String>();
  /**
   * Key - table index Value - dst name
   */
  private final ConcurrentHashMap<Integer, String> dstDetailsMap = new ConcurrentHashMap<Integer, String>();
  private Button pverNameBrowseBtn;
  private Button pidcVerBrowseBtn;
  private Button a2lBrowseBtn;
  private boolean fromContextMenu;
  private Button openZipBtn;
  private int frameX;
  private int frameY;
  private DropFileListener a2lFileDropFileListener;
  private DropFileListener zipFileDropFileListener;
  private Button oneFilePerCheckRadio;
  private Button predecessorCheckbox;
  private Button singleFileWithRedSumRadio;
  private Button singleFileWithSumRadio;

  /**
   * @return the predecessorCheckbox
   */
  public Button getPredecessorCheckbox() {
    return this.predecessorCheckbox;
  }

  /**
   * @param predecessorCheckbox the predecessorCheckbox to set
   */
  public void setPredecessorCheckbox(final Button predecessorCheckbox) {
    this.predecessorCheckbox = predecessorCheckbox;
  }

  /**
   * @return the oneFilePerCheckRadio
   */
  public Button getOneFilePerCheckRadio() {
    return this.oneFilePerCheckRadio;
  }

  /**
   * @param oneFilePerCheckRadio the oneFilePerCheckRadio to set
   */
  public void setOneFilePerCheckRadio(final Button oneFilePerCheckRadio) {
    this.oneFilePerCheckRadio = oneFilePerCheckRadio;
  }

  /**
   * @return the singleFileWithSumRadio
   */
  public Button getSingleFileWithSumRadio() {
    return this.singleFileWithSumRadio;
  }

  /**
   * @param singleFileWithSumRadio the singleFileWithSumRadioto set
   */
  public void setSingleFileWithSumRadio(final Button singleFileWithSumRadio) {
    this.singleFileWithSumRadio = singleFileWithSumRadio;
  }

  /**
   * @return the singleFileWithRedSumRadio
   */
  public Button getSingleFileWithRedSumRadio() {
    return this.singleFileWithRedSumRadio;

  }

  /**
   * @param singleFileWithRedSumRadio the singleFileWithRedSumRadio to set
   */
  public void setSingleFileWithRedSumRadio(final Button singleFileWithRedSumRadio) {
    this.singleFileWithRedSumRadio = singleFileWithRedSumRadio;
  }

  /**
   * @param parentShell shell
   */
  public CompliReviewDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * Configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("SSD Compliance Report of HEX File(s)");
    this.frameX = newShell.getSize().x - newShell.getClientArea().width;
    this.frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    super.configureShell(newShell);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle("SSD Compliance Report of HEX File(s)");
    // Set the message
    setMessage("Provide inputs to generate the report", IMessageProvider.INFORMATION);
    return contents;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.reportErrorBtn = createButton(parent, IDialogConstants.OPEN_ID, "Report Issue", false);
    this.generateBtn = createButton(parent, IDialogConstants.OPEN_ID, GENERATE_BUTTON_CONSTANT, false);
    this.generateBtn.setEnabled(false);
    createButton(parent, IDialogConstants.RETRY_ID, "Reset", false);
    createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
    reportErrorSelectionListener();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    if (IDialogConstants.RETRY_ID == buttonId) {
      if (!this.fromContextMenu) {
        this.pidcVersionText.setText("");
        this.a2lNameText.setText("");
        this.pverNameText.setText("");

        this.pverVariantText.setText("");
        this.pverVariantText.setEditable(false);
        this.varBrowseBtn.setEnabled(false);
        this.pverRevisionText.setEditable(false);
        this.revBrowseBtn.setEnabled(false);
        this.openZipBtn.setEnabled(false);
        this.zipExportPathText.setText("");
        this.pidcA2l = null;
        setPidcVersion(null);
        this.pverRevisionText.setText("");
        this.pverRevisionText.setEditable(false);
      }

      clearHexTableViewer();
      this.generateBtn.setText(GENERATE_BUTTON_CONSTANT);
      this.generateBtn.setEnabled(false);
      this.dstDetailsMap.clear();
      this.dstNameMap.clear();
      this.webFlowIdText.setText("");
      this.executionId = null;
      this.sid = null;
      this.errMsg = null;
      CompliReviewDialog.this.isProcessFlagSet = false;
    }
    super.buttonPressed(buttonId);

  }

  /**
   *
   */
  private void clearHexTableViewer() {
    CompliReviewDialog.this.hexFileRowProviderList.clear();
    CompliReviewDialog.this.tabViewer.setInput(getDefaultRowList());
    CompliReviewDialog.this.tabViewer.refresh();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;

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

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");
    createToolbarAction();
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * Create Toolbar Action
   */
  private void createToolbarAction() {
    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = toolBarManager.createControl(this.section);
    final Separator separator = new Separator();

    // Download action to download input/output files using execution id
    DownloadCompliRvwInputDataAction downloadInputDataAction = new DownloadCompliRvwInputDataAction(this);
    downloadInputDataAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DOWNLOAD_16X16));
    downloadInputDataAction.setToolTipText("Download Input/Output files using Execution ID");

    // Import action to import data into compli review dialog using execution id
    ImportCompliRvwInputDataAction importInputDataAction = new ImportCompliRvwInputDataAction(this);
    importInputDataAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DCM_UPLOAD_28X30));
    importInputDataAction.setToolTipText("Prefill inputs using Execution ID");

    toolBarManager.add(downloadInputDataAction);
    toolBarManager.add(separator);
    toolBarManager.add(importInputDataAction);
    toolBarManager.update(true);
    this.section.setTextClient(toolbar);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.browseIcon = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);

    createPidcVersionControl();
    createA2lControl();
    createPverControl();
    createWebFlowIdControl();
    createHexControl();
    createZipControl();
    createDatFileOptionSelection();
  }


  /**
   *
   */
  private void createDatFileOptionSelection() {
    final Group reviewTypeGrp = new Group(this.form.getBody(), SWT.NONE);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    reviewTypeGrp.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.verticalSpan = 1;
    gridData.horizontalSpan = 2;
    gridData.grabExcessHorizontalSpace = false;
    gridData.grabExcessVerticalSpace = false;
    reviewTypeGrp.setLayoutData(gridData);
    reviewTypeGrp.setText("DataFile Options");

    this.oneFilePerCheckRadio = new Button(reviewTypeGrp, SWT.RADIO);
    this.oneFilePerCheckRadio.setText("One file per check       ");
    this.oneFilePerCheckRadio.setSelection(true);
    this.oneFilePerCheckRadio.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });

    this.singleFileWithSumRadio = new Button(reviewTypeGrp, SWT.RADIO);
    this.singleFileWithSumRadio.setText("Single file with Summary      ");
    this.singleFileWithSumRadio.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });

    this.singleFileWithRedSumRadio = new Button(reviewTypeGrp, SWT.RADIO);
    this.singleFileWithRedSumRadio.setText("Single file with reduction and Summary       ");
    this.singleFileWithRedSumRadio.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });

    this.predecessorCheckbox = new Button(this.form.getBody(), SWT.CHECK);
    GridData checkboxGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
    this.predecessorCheckbox.setLayoutData(checkboxGridData);
    this.predecessorCheckbox.setText("Enable for Predecessor usage");
    this.predecessorCheckbox.setSelection(false);
    this.predecessorCheckbox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });
  }


  /**
   * @param txtGrid
   * @param inputStr
   */
  private void createPidcVersionControl() {
    getFormToolkit().createLabel(this.form.getBody(), "PIDC Version ");
    this.pidcVersionText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.pidcVersionText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.pidcVersionText.setEditable(false);
    this.pidcVersionText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        List<HexFileSelectionRowProvider> hexFileList = new ArrayList<>(CompliReviewDialog.this.hexFileRowProviderList);
        for (HexFileSelectionRowProvider hexFileSelectionRowProvider : hexFileList) {
          if (CommonUtils.isNotNull(CompliReviewDialog.this.getPidcVersion()) &&
              !CompliReviewDialog.this.getPidcVersion().getId().equals(hexFileSelectionRowProvider.getPidcVersId())) {
            clearHexTableViewer();
          }
        }
      }
    });


    this.pidcVerBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.pidcVerBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        PidcVersionVaraintSelDialog paramAttrDepDialog =
            new PidcVersionVaraintSelDialog(Display.getDefault().getActiveShell(), null, CompliReviewDialog.this);
        int result = paramAttrDepDialog.open();
        if (result == 0) {
          CompliReviewDialog.this.selPidcVrsn = paramAttrDepDialog.getSelPidcVer();
          CompliReviewDialog.this.pverNameText.setText("");
          if (paramAttrDepDialog.getSelPidcA2l() != null) {
            CompliReviewDialog.this.pidcA2l = paramAttrDepDialog.getSelPidcA2l();
            CompliReviewDialog.this.a2lNameText.setText(paramAttrDepDialog.getSelPidcA2l().getName());
            CompliReviewDialog.this.a2lFilePath = paramAttrDepDialog.getSelPidcA2l().getName();
            CompliReviewDialog.this.pverVariantText.setText(paramAttrDepDialog.getSelPidcA2l().getSdomPverVarName());
            CompliReviewDialog.this.pverNameText.setText(paramAttrDepDialog.getSelPidcA2l().getSdomPverName());
            CompliReviewDialog.this.pverRevisionText
                .setText(String.valueOf(paramAttrDepDialog.getSelPidcA2l().getSdomPverRevision()));
            CompliReviewDialog.this.revBrowseBtn.setEnabled(true);
            CompliReviewDialog.this.varBrowseBtn.setEnabled(true);
          }
          else {
            CompliReviewDialog.this.pidcA2l = null;
            CompliReviewDialog.this.a2lNameText.setText("");
            CompliReviewDialog.this.a2lFilePath = null;
            CompliReviewDialog.this.pverVariantText.setText("");
            CompliReviewDialog.this.pverNameText.setText("");
            CompliReviewDialog.this.pverRevisionText.setText("");
            CompliReviewDialog.this.revBrowseBtn.setEnabled(false);
            CompliReviewDialog.this.varBrowseBtn.setEnabled(false);
          }
        }
      }
    });
    this.pidcVerBrowseBtn.setImage(this.browseIcon);
    this.pidcVerBrowseBtn.setToolTipText("Select PIDC Version");
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  /**
   * create a2l info
   *
   * @param txtGrid gridData
   */
  private void createA2lControl() {
    getFormToolkit().createLabel(this.form.getBody(), "A2L File *");
    this.a2lNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.a2lNameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.a2lNameText.setEditable(false);

    // Adding Drop Listener for A2L file path textbox
    this.a2lFileDropFileListener = new DropFileListener(this.a2lNameText, new String[] { "*.a2l" });
    this.a2lFileDropFileListener.addDropFileListener(false);
    this.a2lFileDropFileListener.setEditable(true);

    this.a2lNameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
        autoFillZipFilePath();
      }
    });
    this.a2lBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.a2lBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Upload A2L File");
        fileDialog.setFilterExtensions(new String[] { "*.a2l" });
        CompliReviewDialog.this.a2lFilePath = fileDialog.open();
        if (CompliReviewDialog.this.a2lFilePath != null) {
          CompliReviewDialog.this.a2lNameText.setText(CompliReviewDialog.this.a2lFilePath);
          CompliReviewDialog.this.pidcA2l = null;
        }
      }
    });
    this.a2lBrowseBtn.setImage(this.browseIcon);
    this.a2lBrowseBtn.setToolTipText("Select a2l file");
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  private void createPverControl() {
    createPverNameControl();
    createPverVariantControl();
    createPverRevisionControl();
  }

  private void createWebFlowIdControl() {
    getFormToolkit().createLabel(this.form.getBody(), "WebFlow Job ID ");
    this.webFlowIdText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.webFlowIdText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.webFlowIdText.setToolTipText("Enter WebFlow Job Id ");
    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.setVisible(false);
    getFormToolkit().createLabel(this.form.getBody(), "");
  }


  private void createHexControl() {
    Label files = new Label(this.form.getBody(), SWT.NONE);
    files.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    files.setText("HEX Files to be reviewed *");
    this.tabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.tabViewer.setContentProvider(new ArrayContentProvider());
    this.tabViewer.getGrid().setLinesVisible(true);
    this.tabViewer.getGrid().setHeaderVisible(true);

    createColumns();

    this.tabViewer.setInput(getDefaultRowList());

    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        HexFileSelectionDialog dialog =
            new HexFileSelectionDialog(Display.getDefault().getActiveShell(), CompliReviewDialog.this);
        dialog.open();
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });
    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.ADD_16X16));
    browseBtn.setToolTipText("Add HEX file and pidc element");
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  /**
   * @return
   */
  private List<HexFileSelectionRowProvider> getDefaultRowList() {
    List<HexFileSelectionRowProvider> list = new ArrayList<>();
    HexFileSelectionRowProvider defaultProvider = new HexFileSelectionRowProvider(DEFAULT_TEXT_HEX_PATH,
        DEFAULT_TEXT_HEX_PATH, DEFAULT_TEXT_PIDC_VERS, 0L, DEFAULT_TEXT_PIDC_VERS, 0L);
    defaultProvider.setIndex(0);
    list.add(defaultProvider);
    return list;
  }

  private void createColumns() {

    createIndexColumn();

    createHexNameCol();

    createPidcVersionCol();

    createDeleteHexRowCol();

  }

  /**
   * @param txtGrid
   * @param inputStr
   */
  private void createPverNameControl() {
    getFormToolkit().createLabel(this.form.getBody(), "PVER Name *");
    this.pverNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.pverNameText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.pverNameText.setEditable(false);
    this.pverNameText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
        autoFillZipFilePath();
      }
    });


    this.pverNameBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.pverNameBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if ((CompliReviewDialog.this.selPidcVrsn != null) &&
            ((CompliReviewDialog.this.compliHexData.getPidcSdomPverSet() == null) ||
                CompliReviewDialog.this.compliHexData.getPidcSdomPverSet().isEmpty())) {
          CDMLogger.getInstance().errorDialog("PVER Name in SDOM attribute is not set in the selected PIDC Version",
              Activator.PLUGIN_ID);
        }
        else {
          SdomPverServiceClient serviceClient = new SdomPverServiceClient();
          try {
            PverSelectionDialog dialog =
                new PverSelectionDialog(Display.getDefault().getActiveShell(), serviceClient.getAllPverNames(),
                    CompliReviewDialog.this.selPidcVrsn, CompliReviewDialog.this.compliHexData);
            int result = dialog.open();
            if ((result == 0) && (null != dialog.getSelectedObject())) {
              String existingPverName = CompliReviewDialog.this.pverNameText.getText();
              if (!existingPverName.isEmpty() &&
                  !CommonUtils.isEqualIgnoreCase(existingPverName, dialog.getSelectedObject())) {
                CompliReviewDialog.this.revBrowseBtn.setEnabled(false);
                CompliReviewDialog.this.pverVariantText.setText("");
                CompliReviewDialog.this.pverRevisionText.setText("");
              }
              if (dialog.checkIsSDOM(dialog.getSelectedObject())) {
                CompliReviewDialog.this.varBrowseBtn.setEnabled(true);
              }
              else {
                CompliReviewDialog.this.varBrowseBtn.setEnabled(false);
                CompliReviewDialog.this.isNONSDOM = true;
              }
              CompliReviewDialog.this.pverNameText.setText(dialog.getSelectedObject());
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().errorDialog("Error in fetching Pver Names - " + exp.getMessage(), exp,
                Activator.PLUGIN_ID);
          }
        }
        validateFields(CompliReviewDialog.this.isNONSDOM);
      }
    });
    this.pverNameBrowseBtn.setImage(this.browseIcon);
    this.pverNameBrowseBtn.setToolTipText("Select Pver Name");
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  private void createPverVariantControl() {
    getFormToolkit().createLabel(this.form.getBody(), "PVER Variant *");
    this.pverVariantText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.pverVariantText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.pverVariantText.setEditable(false);
    this.pverVariantText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });

    this.varBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.varBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        String pverName = CompliReviewDialog.this.pverNameText.getText();

        try {
          SdomPverServiceClient serviceClient = new SdomPverServiceClient();
          PverSelectionDialog dialog = new PverSelectionDialog(Display.getDefault().getActiveShell(),
              serviceClient.getPverVariantNames(pverName), CompliReviewDialog.this.selPidcVrsn,
              CompliReviewDialog.this.compliHexData);
          dialog.setPverVar(true);
          int result = dialog.open();
          if ((result == 0) && (null != dialog.getSelectedObject())) {

            String existingPverVar = CompliReviewDialog.this.pverVariantText.getText();
            if (!existingPverVar.isEmpty() &&
                !CommonUtils.isEqualIgnoreCase(existingPverVar, dialog.getSelectedObject())) {
              CompliReviewDialog.this.pverRevisionText.setText("");
            }
            CompliReviewDialog.this.pverVariantText.setText(dialog.getSelectedObject());
            CompliReviewDialog.this.revBrowseBtn.setEnabled(true);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog("Error in fetching Pver Variants - " + exp.getMessage(), exp,
              Activator.PLUGIN_ID);
        }

        validateFields(CompliReviewDialog.this.isNONSDOM);
      }
    });
    this.varBrowseBtn.setImage(this.browseIcon);
    this.varBrowseBtn.setToolTipText("Select Pver Variant");
    this.varBrowseBtn.setEnabled(false);
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  private void createPverRevisionControl() {
    getFormToolkit().createLabel(this.form.getBody(), "PVER Revision *");
    this.pverRevisionText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.pverRevisionText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.pverRevisionText.setEditable(false);
    this.pverRevisionText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });


    this.revBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.revBrowseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        String pverVar = CompliReviewDialog.this.pverVariantText.getText();

        try {
          SdomPverServiceClient serviceClient = new SdomPverServiceClient();
          SortedSet<Long> pverVariantVersions =
              serviceClient.getPverVariantVersions(CompliReviewDialog.this.pverNameText.getText(), pverVar);
          SortedSet<String> versionsSet = new TreeSet<String>();
          for (Long selVal : pverVariantVersions) {
            versionsSet.add(selVal.toString());
          }
          PverSelectionDialog dialog = new PverSelectionDialog(Display.getDefault().getActiveShell(), versionsSet,
              CompliReviewDialog.this.selPidcVrsn, CompliReviewDialog.this.compliHexData);
          dialog.setPverRvsn(true);
          int result = dialog.open();
          if ((result == 0) && (null != dialog.getSelectedObject())) {
            CompliReviewDialog.this.pverRevisionText.setText(dialog.getSelectedObject());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog("Error in fetching Pver revision - " + exp.getMessage(), exp,
              Activator.PLUGIN_ID);
        }
        validateFields(CompliReviewDialog.this.isNONSDOM);
      }
    });
    this.revBrowseBtn.setImage(this.browseIcon);
    this.revBrowseBtn.setToolTipText("Select Pver Revision");
    this.revBrowseBtn.setEnabled(false);
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  /**
   *
   */
  private void createDeleteHexRowCol() {
    final GridViewerColumn deleteIconCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.tabViewer, 25);
    deleteIconCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return "";
      }

      @Override
      public Image getImage(final Object element) {
        Image editImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
        return editImage;
      }
    });
    addDeleteSelectionListener();
  }

  /**
   */
  private void addDeleteSelectionListener() {
    this.tabViewer.getGrid().addMouseListener(new MouseAdapter() {

      @Override
      public void mouseDown(final MouseEvent event) {
        final Point point = new Point(event.x, event.y);
        GridItem item = CompliReviewDialog.this.tabViewer.getGrid().getItem(point);
        if ((item != null) && !item.isDisposed() && (event.button == 1)) {
          final int columnIndex =
              GridTableViewerUtil.getInstance().getTabColIndex(event, CompliReviewDialog.this.tabViewer);
          if (columnIndex == 3) {
            IStructuredSelection selection = (IStructuredSelection) CompliReviewDialog.this.tabViewer.getSelection();
            if (!selection.isEmpty()) {
              HexFileSelectionRowProvider hexProvider = (HexFileSelectionRowProvider) selection.getFirstElement();
              int indexToRemove = hexProvider.getIndex();
              if (indexToRemove != 0) {
                CompliReviewDialog.this.hexFileRowProviderList.remove(indexToRemove - 1);
                Iterator<HexFileSelectionRowProvider> iterator =
                    CompliReviewDialog.this.hexFileRowProviderList.iterator();
                int i = 1;
                while (iterator.hasNext()) {
                  HexFileSelectionRowProvider provider = iterator.next();
                  provider.setIndex(i);
                  i++;
                }
                if (CompliReviewDialog.this.getHexFileRowProviderList().isEmpty()) {
                  CompliReviewDialog.this.getTabViewer().setInput(getDefaultRowList());
                }
                else {
                  CompliReviewDialog.this.getTabViewer().setInput(CompliReviewDialog.this.getHexFileRowProviderList());
                }
                CompliReviewDialog.this.getTabViewer().refresh();
                String dstName = CompliReviewDialog.this.dstDetailsMap.get(indexToRemove);
                if (null != dstName) {
                  CompliReviewDialog.this.dstNameMap.remove(dstName);
                }
                CompliReviewDialog.this.dstDetailsMap.remove(indexToRemove);

                checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
              }
            }
          }
        }
      }

    });
  }

  /**
   *
   */
  private void createPidcVersionCol() {
    final GridViewerColumn pidcVersCol = new GridViewerColumn(this.tabViewer, SWT.NONE);
    pidcVersCol.getColumn().setText("PIDC Element");
    pidcVersCol.getColumn().setWidth(318);
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    pidcVersCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof HexFileSelectionRowProvider) {
          HexFileSelectionRowProvider prov = (HexFileSelectionRowProvider) element;
          return prov.getPidcElementDisplay();
        }
        return DEFAULT_TEXT_PIDC_VERS;
      }
    });
  }


  /**
   *
   */
  private void createHexNameCol() {
    GridColumnGroup hexFileGroup = new GridColumnGroup(this.tabViewer.getGrid(), SWT.TOGGLE);
    hexFileGroup.setText("Hex File Selection");
    final GridViewerColumn hexFileNameCol = new GridViewerColumn(this.tabViewer, SWT.NONE);
    hexFileNameCol.getColumn().setText("Hex File Name *");
    hexFileNameCol.getColumn().setWidth(280);
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    hexFileNameCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof HexFileSelectionRowProvider) {
          HexFileSelectionRowProvider prov = (HexFileSelectionRowProvider) element;
          return prov.getHexFileName();
        }
        return DEFAULT_TEXT_HEX_PATH;
      }

      @Override
      public String getToolTipText(final Object element) {
        if (element instanceof HexFileSelectionRowProvider) {
          HexFileSelectionRowProvider prov = (HexFileSelectionRowProvider) element;
          return prov.getHexFilePath();
        }
        return "";
      }

    });
  }

  private void createZipControl() {
    getFormToolkit().createLabel(this.form.getBody(), "Output Zip File : ");
    this.zipExportPathText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.zipExportPathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.zipExportPathText.setEditable(false);

    // Drag drop output file path
    this.zipFileDropFileListener = new DropFileListener(this.zipExportPathText, new String[] { "*.zip" });
    this.zipFileDropFileListener.addDropFileListener(false);
    this.zipFileDropFileListener.setEditable(true);

    this.zipExportPathText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        checkGenerateBtnEnable(CompliReviewDialog.this.isNONSDOM);
      }
    });
    final Button browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    browseBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Save Zip Report to :");
        fileDialog.setFilterExtensions(new String[] { "*.zip" });
        fileDialog.setFilterNames(new String[] { "Zip Format (.zip)" });
        fileDialog.setOverwrite(true);
        fileDialog.setFilterIndex(0);

        if (null != CompliReviewDialog.this.zipExportPathText.getText()) {
          fileDialog
              .setFileName(FilenameUtils.getBaseName(CompliReviewDialog.this.zipExportPathText.getText()) + ".zip");
          fileDialog.setFilterPath(FilenameUtils.getFullPath(CompliReviewDialog.this.zipExportPathText.getText()));

        }
        String selectedFile = fileDialog.open();
        String fileExtn = "zip";
        if (selectedFile != null) {
          CompliReviewDialog.this.file = new File(CommonUtils.getCompletePdfFilePath(selectedFile, fileExtn));
          if (CommonUtils.checkIfFileOpen(CompliReviewDialog.this.file)) {
            CompliReviewDialog.this.zipExportPathText.setText("");
            MessageDialogUtils.getInfoMessageDialog("",
                "The selected target file is already open in another program. Please close the file and choose again.");
            return;
          }
          CompliReviewDialog.this.zipExportPathText.setText(selectedFile);
        }
      }
    });
    browseBtn.setImage(this.browseIcon);
    browseBtn.setToolTipText("Select zip export filepath");

    this.openZipBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.openZipBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (null != CompliReviewDialog.this.zipExportPathText.getText()) {
          if (CommonUtils.isFileAvailable(CompliReviewDialog.this.zipExportPathText.getText())) {
            CommonUiUtils.openFile(CompliReviewDialog.this.zipExportPathText.getText());
          }
          else {
            MessageDialogUtils.getInfoMessageDialog("", "Compare Hex : Zip file is not available!");
          }
        }

      }
    });
    this.openZipBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.FILE_16X16));
    this.openZipBtn.setToolTipText("Open zip file");
    this.openZipBtn.setEnabled(false);
  }

  /**
   *
   */
  private void createIndexColumn() {
    final GridViewerColumn indexCol = new GridViewerColumn(this.tabViewer, SWT.NONE);
    indexCol.getColumn().setText("#");
    indexCol.getColumn().setWidth(20);
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    indexCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Get text for name column
       */
      @Override
      public String getText(final Object element) {
        if (element instanceof HexFileSelectionRowProvider) {
          HexFileSelectionRowProvider prov = (HexFileSelectionRowProvider) element;
          return String.valueOf(prov.getIndex());
        }
        return "";
      }

    });
  }

  private void autoFillZipFilePath() {
    String a2lFilPath = CompliReviewDialog.this.a2lNameText.getText();
    String pverName = this.pverNameText.getText();

    if (!CommonUtils.isEmptyString(a2lFilPath) && !pverName.isEmpty()) {

      String dateTime =
          new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault(Locale.Category.FORMAT)).format(new Date());
      StringBuilder zipFileName = new StringBuilder();
      zipFileName.append("COMPLI_").append(pverName.toUpperCase(Locale.getDefault())).append("_").append(dateTime)
          .append(".zip");
      String zipFilePath = FilenameUtils.getFullPath(a2lFilPath);
      if (zipFilePath.isEmpty()) {
        zipFilePath = CommonUtils.getSystemUserTempDirPath();
      }
      String zipFullPath = zipFilePath + zipFileName;
      CompliReviewDialog.this.file = new File(CommonUtils.getCompletePdfFilePath(zipFullPath, ".zip"));
      if (CommonUtils.checkIfFileOpen(CompliReviewDialog.this.file)) {
        CompliReviewDialog.this.generateBtn.setEnabled(false);
        CompliReviewDialog.this.zipExportPathText.setText("");
        MessageDialogUtils.getInfoMessageDialog("",
            "'The selected target file is already open in another program. Please close the file and choose again.");
        return;
      }
      CompliReviewDialog.this.zipExportPathText.setText(zipFullPath);
    }
    else {
      CompliReviewDialog.this.zipExportPathText.setText("");
    }
  }

  /**
   * checks whether the save button can be enabled or not
   */
  private void checkGenerateBtnEnable(final boolean isNONSDOM) {
    this.generateBtn.setEnabled(validateFields(isNONSDOM));
    this.generateBtn.addSelectionListener(getSelectionAdapter());

  }

  /**
   * @return
   */
  private SelectionAdapter getSelectionAdapter() {
    return new CompliRvwGenBtnSelAdapter(this).getCompliReviewSelectionAdapter();
  }

  /**
   * Validate the a2l file name
   *
   * @return true if file name is available
   */
  protected boolean validateFields(final boolean isNONSDOM) {
    boolean isValid = false;
    if ((validateNames() && CommonUtils.isNotEmptyString(this.pverVariantText.getText()) &&
        CommonUtils.isNotEmptyString(this.pverRevisionText.getText()) &&
        CommonUtils.isNotEmptyString(this.zipExportPathText.getText())) ||
        (validateNames() && CommonUtils.isNotEmptyString(this.zipExportPathText.getText()) && isNONSDOM)) {
      CompliReviewDialog.this.isProcessFlagSet = false;
      isValid = !this.hexFileRowProviderList.isEmpty();
    }
    return isValid;
  }


  /**
   * @return
   */
  private boolean validateNames() {
    return CommonUtils.isNotEmptyString(this.a2lNameText.getText()) &&
        CommonUtils.isNotEmptyString(this.pverNameText.getText());
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
  protected Point getInitialSize() {
    return new Point(WIDTH_OF_DIALOG + this.frameX, HEIGHT_OF_DIALOG + this.frameY);
  }


  /**
   * @return the hexFileRowProviderList
   */
  public List<HexFileSelectionRowProvider> getHexFileRowProviderList() {
    return this.hexFileRowProviderList;
  }


  /**
   * @return the tabViewer
   */
  public GridTableViewer getTabViewer() {
    return this.tabViewer;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the pidcVersionText
   */
  public Text getPidcVersionText() {
    return this.pidcVersionText;
  }


  /**
   * @return the a2lNameText
   */
  public Text getA2lNameText() {
    return this.a2lNameText;
  }


  /**
   * @return the zipExportPathText
   */
  public Text getZipExportPathText() {
    return this.zipExportPathText;
  }


  /**
   * @return the isProcessFlagSet
   */
  public boolean isProcessFlagSet() {
    return this.isProcessFlagSet;
  }


  /**
   * @param isProcessFlagSet the isProcessFlagSet to set
   */
  public void setProcessFlagSet(final boolean isProcessFlagSet) {
    this.isProcessFlagSet = isProcessFlagSet;
  }


  /**
   * @return the generateBtn
   */
  public Button getGenerateBtn() {
    return this.generateBtn;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }

  /**
   * @return the a2lFilePath
   */
  public String getA2lFilePath() {
    return this.a2lFilePath;
  }

  public void setA2lFilePath(final String a2lFilePath) {
    this.a2lFilePath = a2lFilePath;
  }


  /**
   * @return the pverNameText
   */
  public Text getPverNameText() {
    return this.pverNameText;
  }


  /**
   * @return the pverVariantText
   */
  public Text getPverVariantText() {
    return this.pverVariantText;
  }


  /**
   * @return the pverRevisionText
   */
  public Text getPverRevisionText() {
    return this.pverRevisionText;
  }


  /**
   * @return the webFlowIdText
   */
  public Text getWebFlowIdText() {
    return this.webFlowIdText;
  }


  /**
   * @return the dstNameMap
   */
  public ConcurrentHashMap<String, String> getDstNameMap() {
    return this.dstNameMap;
  }


  /**
   * @return the dstDetailsMap
   */
  public ConcurrentHashMap<Integer, String> getDstDetailsMap() {
    return this.dstDetailsMap;
  }


  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * @param pidcTreeNode
   */
  public void setValues(final PidcTreeNode pidcTreeNode) {
    this.fromContextMenu = true;
    this.selPidcVrsn = pidcTreeNode.getPidcVersion();
    this.pidcVersion = pidcTreeNode.getPidcVersion();
    CompliReviewUsingHexData pidcData;
    PidcVersionServiceClient client = new PidcVersionServiceClient();
    try {
      pidcData = client.getPidcVersDetailsForCompliHex(this.selPidcVrsn.getId());
      setCompliHexData(pidcData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    this.pidcA2l = pidcTreeNode.getPidcA2l();
    this.pidcVersionText.setText(this.selPidcVrsn.getName());
    this.a2lNameText.setText(this.pidcA2l.getName());
    this.a2lFilePath = this.pidcA2l.getName();
    this.pverVariantText.setText(this.pidcA2l.getSdomPverVarName());
    this.pverNameText.setText(this.pidcA2l.getSdomPverName());
    this.pverRevisionText.setText(String.valueOf(this.pidcA2l.getSdomPverRevision()));
    this.pidcVerBrowseBtn.setEnabled(false);
    this.revBrowseBtn.setEnabled(false);
    this.varBrowseBtn.setEnabled(false);
    this.pverNameBrowseBtn.setEnabled(false);
    this.a2lBrowseBtn.setEnabled(false);
    setCompliReportStartedFromA2l(true);
  }

  private void reportErrorSelectionListener() {
    this.reportErrorBtn
        .addSelectionListener(new CompliReviewErrorReportBtnAdapter(this).getCompliReviewSelectionAdapter());
  }


  /**
   * @return the openZipBtn
   */
  public Button getOpenZipBtn() {
    return this.openZipBtn;
  }


  /**
   * @return the errorMessage
   */
  public String getExecutionId() {
    return this.executionId;
  }


  /**
   * @param errorMessage the errorMessage to set
   */
  public void setExecutionId(final String errorMessage) {
    this.executionId = errorMessage;
  }


  /**
   * @return the errMsg
   */
  public String getErrMsg() {
    return this.errMsg;
  }


  /**
   * @param errMsg the errMsg to set
   */
  public void setErrMsg(final String errMsg) {
    this.errMsg = errMsg;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean close() {
    return super.close();
  }

  /**
   * @return the isCompliReportStartedFromA2l
   */
  public boolean isCompliReportStartedFromA2l() {
    return this.isCompliReportStartedFromA2l;
  }


  /**
   * @param isCompliReportStartedFromA2l the isCompliReportStartedFromA2l to set
   */
  public void setCompliReportStartedFromA2l(final boolean isCompliReportStartedFromA2l) {
    this.isCompliReportStartedFromA2l = isCompliReportStartedFromA2l;
  }


  /**
   * @return the reportErrorBtn
   */
  public Button getReportErrorBtn() {
    return this.reportErrorBtn;
  }


  /**
   * @return the sid
   */
  public String getSid() {
    return this.sid;
  }


  /**
   * @param sid the sid to set
   */
  public void setSid(final String sid) {
    this.sid = sid;
  }


  /**
   * @return the errCode
   */
  public String getErrCode() {
    return this.errCode;
  }


  /**
   * @param errCode the errCode to set
   */
  public void setErrCode(final String errCode) {
    this.errCode = errCode;
  }
}
