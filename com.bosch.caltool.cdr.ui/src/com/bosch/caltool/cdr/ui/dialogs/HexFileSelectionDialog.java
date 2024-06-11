/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.views.providers.HexFileSelectionRowProvider;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.DSTSelectionDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CompliReviewUsingHexData;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.VcdmDataSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author svj7cob
 */
public class HexFileSelectionDialog extends AbstractDialog {


  /**
   * String literal for invalid hex file error message
   */
  private static final String INVALID_HEX_PIDC_ERR_MSG = "Please choose a valid hex file!\n";
  /**
   * String literal for Duplicate Pidc Vers/Var Hex combination error message title
   */
  private static final String DUPLICATE_HEX_PIDC_VER_VAR_TITLE = "Duplicate HEX & Pidc Version/Variant Found";
  /**
   * String literal for invalid hex file error dialog's title
   */
  private static final String INVALID_FILE_ERR_DIALOG_TITLE = "Invalid File";
  /**
   * String literal for Duplicate Pidc Vers/Var Hex combination error message prefix
   */
  private static final String DUPLICATE_HEX_PIDC_VERS_VAR_FOUND_MSG = "Duplicate HEX & Pidc Version/Variant Found.\n";
  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * save button
   */
  private Button uploadBtn;
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
  private Image browseButtonImage;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * a2l name text
   */
  private Text hexNameText;

  /**
   * pidc version name text
   */
  private Text pidcObjNameText;

  /**
   * selc a2l file
   */
  protected String hexFilePath;

  /**
   * Width of dialog
   */
  private static final int WIDTH_OF_DIALOG = 730;
  /**
   * Height of dialog
   */
  private static final int HEIGHT_OF_DIALOG = 500;

  /**
   * parent dialog
   */
  private final CompliReviewDialog compliReviewDialog;

  private String pidcVersName;

  private Long pidcVersId;

  private String pidcVariantName;

  private Long pidcVariantId;

  private boolean isSamePidcElement;

  /**
   * Dst file text
   */
  private Text dstFileText;

  private String dstName;

  private String dstSource;

  /**
   * Dst file browse button
   */
  private Button dstBrowseBtn;
  /**
   * Selected vcdm revision
   */
  private VCDMDSTRevision selRevision;
  /**
   * Constant for Dst not available for varaint name
   */
  private static final String DST_NOT_AVAILABLE = "No DST's available for the selected variant name";
  private Button browseBtn;
  private int frameX;
  private int frameY;

  private Button pidcVersBrowseBtn;

  private Map<Long, PidcVariant> pidcVarMap;
  /**
   * list of hex file names selected from the file dialog
   */
  private final List<String> hexFileNameList = new ArrayList<>();
  /**
   * Absolute path of selected hex files
   */
  private String fileFilterPath;
  /**
   * key->hex file name,value->absolute path of hex file
   */
  private final Map<String, String> hexAbsolutePathMap = new HashMap<>();
  /**
   * Duplicate Hex File error message string
   */
  private final StringBuilder duplicateFileErrMsg = new StringBuilder("");
  /**
   * key->dst name,value->dst full source path
   */
  private final Map<String, String> dstFullSourceNameMap = new HashMap<>();


  /**
   * @param parentShell shell
   * @param dialog CompliReviewDialog
   */
  public HexFileSelectionDialog(final Shell parentShell, final CompliReviewDialog dialog) {
    super(parentShell);
    this.compliReviewDialog = dialog;
  }

  /**
   * Configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Hex File Selection");
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
    setTitle("Hex File");
    // Set the message
    setMessage("Enter Hex File Path", IMessageProvider.INFORMATION);
    return contents;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.uploadBtn = createButton(parent, IDialogConstants.OK_ID, "Add", false);
    this.uploadBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    loadPidcVarMap();
    fillPidcVersIfNoVariant();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    String hexPath = null;

    if (HexFileSelectionDialog.this.selRevision != null) {
      hexPath = fetchHexForDst();
    }
    // set hex files from dst in the table viewer
    if (hexPath != null) {
      HexFileSelectionRowProvider provider = createNSetHexRowProvider(hexPath);
      if (this.dstName != null) {
        this.compliReviewDialog.getDstDetailsMap().put(provider.getIndex(),
            getFullDstSource(HexFileSelectionDialog.this.dstFileText.getText()));
      }
      // HexFileSelectionDialog.this.selRevision
      HexFileSelectionDialog.this.selRevision = null;
    }
    // set stored hex files in the table viewer
    else {
      Set<String> hexFileNameSet = new HashSet<>();
      hexFileNameSet.addAll(this.hexFileNameList);
      setSelectedHexFiles(hexFileNameSet);
    }
    this.compliReviewDialog.getTabViewer().setInput(this.compliReviewDialog.getHexFileRowProviderList());
    this.compliReviewDialog.getTabViewer().refresh();
    super.okPressed();
  }

  /**
   * set selected hex files in table viewer
   *
   * @param hexFileNameSet
   */
  private void setSelectedHexFiles(final Set<String> hexFileNameSet) {
    for (String hexFileName : hexFileNameSet) {
      if (HexFileSelectionDialog.this.hexNameText.getText().contains(hexFileName)) {
        createNSetHexRowProvider(HexFileSelectionDialog.this.hexAbsolutePathMap.get(hexFileName));
      }
    }
  }

  /**
   * create entries for Pidc version/variant---> hex file selection table viewer
   *
   * @param hexPath
   * @return
   */
  private HexFileSelectionRowProvider createNSetHexRowProvider(final String hexPath) {
    HexFileSelectionRowProvider provider = new HexFileSelectionRowProvider(hexPath, FilenameUtils.getName(hexPath),
        HexFileSelectionDialog.this.getPidcVersName(), HexFileSelectionDialog.this.getPidcVersId(),
        HexFileSelectionDialog.this.getPidcVariantName(), HexFileSelectionDialog.this.getPidcVariantId());
    provider.setIndex(this.compliReviewDialog.getHexFileRowProviderList().size() + 1);
    this.compliReviewDialog.getHexFileRowProviderList().add(provider);
    return provider;
  }

  /**
   * Calls eASEE service method to upload the a2l file
   *
   * @param monitor
   * @return
   */
  private String fetchHexForDst() {
    VcdmDataSetServiceClient client = new VcdmDataSetServiceClient();
    try {
      // uploads the a2l file and returns version number
      return client.loadHexFile(HexFileSelectionDialog.this.selRevision.getDstID().longValue(),
          CommonUtils.getICDMTmpFileDirectoryPath());
    }
    catch (Exception ex) {
      CDMLogger.getInstance().error("Error occurred while loading Hex file from vCDM", ex, Activator.PLUGIN_ID);
    }
    return "";
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
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;

    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    this.browseButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    createPidcVersControl(txtGrid);
    // HEX file detail
    createHEXControl();
    createDSTFileControls();

    setFormMsg(false);
  }

  /**
   * Create DST file controls
   */
  private void createDSTFileControls() {

    LabelUtil.getInstance().createLabel(this.form.getBody(), "vCDM DST");
    this.dstFileText = TextUtil.getInstance().createText(this.form.getBody(), true, "");
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.widthHint = 350;
    this.dstFileText.setLayoutData(txtGridData);
    this.dstFileText.setEditable(false);

    this.dstBrowseBtn = new Button(this.form.getBody(), SWT.NONE);
    this.dstBrowseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for
    // browse
    // button
    this.dstFileText.setEnabled(false);
    this.dstBrowseBtn.setEnabled(false);
    // add selection listener
    this.dstBrowseBtn.addSelectionListener(dstListener());

  }

  /**
   * @return
   */
  private SelectionListener dstListener() {
    return new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        CurrentUserBO currentUserBO = new CurrentUserBO();
        try {
          NodeAccess curUserAccRight = currentUserBO
              .getNodeAccessRight(HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion().getPidcId());

          if (!(currentUserBO.hasApicWriteAccess() || ((curUserAccRight != null) && curUserAccRight.isRead()))) {
            MessageDialogUtils.getInfoMessageDialog("Insufficient privilege",
                "Insufficient privilege to view vCDM artifacts! ");
            return;
          }

          String pverName = HexFileSelectionDialog.this.compliReviewDialog.getPverNameText().getText();
          String pverVar = HexFileSelectionDialog.this.compliReviewDialog.getPverVariantText().getText();
          String pverRev = HexFileSelectionDialog.this.compliReviewDialog.getPverRevisionText().getText();
          if (pverName.isEmpty() || pverVar.isEmpty() || pverRev.isEmpty()) {
            CDMLogger.getInstance().infoDialog(
                "Pver Name , Pver Variant name and Pver revision should be available to fetch dsts",
                Activator.PLUGIN_ID);
          }
          else {
            VcdmDataSetServiceClient client = new VcdmDataSetServiceClient();
            Set<VCDMApplicationProject> dataSetModels;

            dataSetModels = client.getDataSet(pverName, pverVar, pverRev);


            if (CommonUtils.isNotEmpty(dataSetModels) &&
                (HexFileSelectionDialog.this.compliReviewDialog.getPidcVariant() != null)) {

              // selected variant name
              String varName = HexFileSelectionDialog.this.compliReviewDialog.getPidcVariant().getName();
              List<VCDMApplicationProject> projHavingSameVar = new ArrayList<>();

              fillVcdmApplicationMap(dataSetModels, varName, projHavingSameVar);

              if (projHavingSameVar.isEmpty()) {
                CDMLogger.getInstance().infoDialog(DST_NOT_AVAILABLE, Activator.PLUGIN_ID);
              }
              else {
                DSTSelectionDialog dstSelectionDialog = new DSTSelectionDialog(Display.getDefault().getActiveShell(),
                    projHavingSameVar, HexFileSelectionDialog.this.compliReviewDialog.getA2lFilePath());
                dstSelectionDialog.open();
                if (dstSelectionDialog.getReturnCode() == 0) {
                  HexFileSelectionDialog.this.selRevision = dstSelectionDialog.getVcdmdstRevision();
                  HexFileSelectionDialog.this.dstName = dstSelectionDialog.getSelectedTreePath()
                      .substring(dstSelectionDialog.getSelectedTreePath().indexOf(':') + 1);
                  HexFileSelectionDialog.this.dstSource = dstSelectionDialog.getSelectedTreePath().substring(0,
                      dstSelectionDialog.getSelectedTreePath().indexOf(':'));
                  // map to detect duplicate dst
                  HexFileSelectionDialog.this.dstFullSourceNameMap.put(HexFileSelectionDialog.this.dstName,
                      getFullDstSource(HexFileSelectionDialog.this.dstName));
                  if (HexFileSelectionDialog.this.isDuplicateDstFound()) {
                    MessageDialogUtils.getErrorMessageDialog("Duplicate DST & Pidc Version/Variant Found",
                        "Duplicate DST & Pidc Version/Variant Found. Please choose alternate input.");
                    return;
                  }

                  HexFileSelectionDialog.this.dstFileText.setText(HexFileSelectionDialog.this.dstName);

                  HexFileSelectionDialog.this.compliReviewDialog.getDstNameMap().put(
                      HexFileSelectionDialog.this.dstFullSourceNameMap.get(HexFileSelectionDialog.this.dstName),
                      varName);

                  // selected dst id
                  HexFileSelectionDialog.this.hexNameText.setEnabled(false);
                  HexFileSelectionDialog.this.browseBtn.setEnabled(false);

                }
                else {
                  HexFileSelectionDialog.this.dstSource = "";
                  HexFileSelectionDialog.this.dstFileText.setText("");
                  HexFileSelectionDialog.this.hexNameText.setEnabled(true);
                  HexFileSelectionDialog.this.browseBtn.setEnabled(true);
                }

                checkUploadBtnEnable();
              }

            }

            else {
              CDMLogger.getInstance().infoDialog(DST_NOT_AVAILABLE, Activator.PLUGIN_ID);
            }

          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // No implementation
      }
    };
  }

  /**
   * create hex file control
   */
  private void createHEXControl() {
    getFormToolkit().createLabel(this.form.getBody(), "Input HEX File ");

    this.hexNameText = getFormToolkit().createText(this.form.getBody(), null,
        SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);

    this.hexNameText.setLayoutData(createGridData());

    this.hexNameText.setEditable(false);

    // Adding Drop Listener for hex file textbox
    addHexFileDropListener();
    this.hexNameText.addModifyListener(event -> checkUploadBtnEnable());
    this.browseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.browseBtn.addSelectionListener(new SelectionAdapter() {

      // String to form selected hex file text
      private final StringBuilder selHexFileName = new StringBuilder("");

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.MULTI);
        fileDialog.setText("Upload HEX File");
        fileDialog.setFilterExtensions(new String[] { "*.hex;*.s19" });

        HexFileSelectionDialog.this.hexFilePath = fileDialog.open();
        // Get absolute path of hex file
        HexFileSelectionDialog.this.fileFilterPath = fileDialog.getFilterPath();
        // store selected hex files
        HexFileSelectionDialog.this.hexFileNameList.addAll(Arrays.asList(fileDialog.getFileNames()));

        // below validation is to ensure user doesnt manually filter files other than above set extensions
        StringBuilder duplicateFileErrMsgPrefix = new StringBuilder(DUPLICATE_HEX_PIDC_VERS_VAR_FOUND_MSG).append("\n");
        StringBuilder invalidFileErrMsgPrefix = new StringBuilder(INVALID_HEX_PIDC_ERR_MSG);
        StringBuilder invalidFileErrMsg = new StringBuilder("");
        if (isDuplicateHexPidcEltFound()) {
          MessageDialogUtils.getErrorMessageDialog(DUPLICATE_HEX_PIDC_VER_VAR_TITLE,
              duplicateFileErrMsgPrefix.append(HexFileSelectionDialog.this.duplicateFileErrMsg)
                  .append("Please choose alternate input.").toString());

        }
        validateSelHexFiles(fileDialog, this.selHexFileName, invalidFileErrMsg,
            Arrays.asList(fileDialog.getFileNames()));

        if (CommonUtils.isNotEmptyString(invalidFileErrMsg.toString())) {
          MessageDialogUtils.getErrorMessageDialog(INVALID_FILE_ERR_DIALOG_TITLE,
              invalidFileErrMsgPrefix.append(invalidFileErrMsg).toString());
          return;
        }
        // if all the selected hex files are duplicate selHexFileName will be empty string
        if (CommonUtils.isEmptyString(this.selHexFileName.toString())) {
          return;
        }
        HexFileSelectionDialog.this.hexNameText.setText(this.selHexFileName.toString());
        enableDisableDstBrowseBtn();
      }
    });
    this.browseBtn.setImage(this.browseButtonImage);
    this.browseBtn.setToolTipText("Select HEX file");
  }

  /**
   * Enable disable hex file browse button
   */
  private void enableDisableDstBrowseBtn() {
    if (HexFileSelectionDialog.this.hexNameText.getText().isEmpty()) {
      HexFileSelectionDialog.this.dstBrowseBtn.setEnabled(true);
      HexFileSelectionDialog.this.dstFileText.setEnabled(true);
    }
    else {
      HexFileSelectionDialog.this.dstBrowseBtn.setEnabled(false);
      HexFileSelectionDialog.this.dstFileText.setEnabled(false);
    }
  }

  /**
   * valiadte hex file name
   *
   * @param fileDialog
   * @param isValidHexFile
   * @param selHexFileName
   * @param duplicateFileErrMsg
   * @param invalidFileErrMsg
   */
  private void validateSelHexFiles(final FileDialog fileDialog, final StringBuilder selHexFileName,
      final StringBuilder invalidFileErrMsg, final List<String> fileNames) {

    boolean isValidHexFile = false;
    Set<String> hexFileNameSet = new HashSet<>();
    hexFileNameSet.addAll(fileNames);
    for (String hexFileName : hexFileNameSet) {
      HexFileSelectionDialog.this.hexAbsolutePathMap.put(hexFileName,
          new File(HexFileSelectionDialog.this.fileFilterPath, hexFileName).getAbsolutePath());

      if ((hexFileName != null) && !hexFileName.isEmpty()) {
        isValidHexFile = isHexFileExtsnValid(fileDialog, hexFileName);
        setInvalidFileErrMsg(invalidFileErrMsg, isValidHexFile, hexFileName);
      }
      if (!this.duplicateFileErrMsg.toString().contains(hexFileName) &&
          !invalidFileErrMsg.toString().contains(hexFileName)) {
        setSelHexFileName(selHexFileName, hexFileName);
      }

    }
    if (CommonUtils.isEmptyString(selHexFileName.toString())) {
      this.hexFileNameList.clear();
      clearPidcEleText();
    }
    this.duplicateFileErrMsg.delete(0, this.duplicateFileErrMsg.length());
  }

  /**
   * validate hex file extension
   *
   * @param fileDialog
   * @param isValidHexFile
   * @param hexFileName
   * @return
   */
  private boolean isHexFileExtsnValid(final FileDialog fileDialog, final String hexFileName) {
    boolean isValidHexFile = false;
    for (String fileExt : fileDialog.getFilterExtensions()) {
      String[] ext = fileExt.split(";");
      for (String string : ext) {

        if (hexFileName.substring(hexFileName.lastIndexOf('.'))
            .equalsIgnoreCase(string.substring(string.lastIndexOf('.')))) {
          isValidHexFile = true;
          break;
        }
      }
    }
    return isValidHexFile;
  }


  /**
   * set selected hex file string in the text box
   *
   * @param selHexFileName
   * @param hexFileName
   */
  private void setSelHexFileName(final StringBuilder selHexFileName, final String hexFileName) {
    if ((HexFileSelectionDialog.this.hexAbsolutePathMap.get(hexFileName) != null) &&
        !selHexFileName.toString().contains(hexFileName)) {
      selHexFileName.append(HexFileSelectionDialog.this.hexAbsolutePathMap.get(hexFileName)).append("\n");
    }
  }

  /**
   * create invalid hex file error message
   *
   * @param invalidFileErrMsg
   * @param isValidHexFile
   * @param hexFileName
   */
  private void setInvalidFileErrMsg(final StringBuilder invalidFileErrMsg, final boolean isValidHexFile,
      final String hexFileName) {
    if (!isValidHexFile) {
      invalidFileErrMsg.append(hexFileName).append("\n");
    }
  }

  /**
   * Hex file drop listener
   */
  private void addHexFileDropListener() {
    DropFileListener hexFileDropFileListener =
        new DropFileListener(this.hexNameText, new String[] { "*.hex", "*.s19" });
    hexFileDropFileListener.addDropFileListener(false);
    hexFileDropFileListener.setEditable(true);
  }

  /**
   * @return Grid Data for selected hex file text box
   */
  private GridData createGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.widthHint = 200;
    gridData.heightHint = 80;
    return gridData;
  }


  /**
   * create a2l info
   *
   * @param txtGrid gridData
   */
  private void createPidcVersControl(final GridData txtGrid) {
    getFormToolkit().createLabel(this.form.getBody(), "Input Pidc Version/Variant : ");
    this.pidcObjNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);

    this.pidcObjNameText.setLayoutData(txtGrid);
    this.pidcObjNameText.setEditable(false);


    this.pidcObjNameText.addModifyListener(event -> checkUploadBtnEnable());

    this.pidcVersBrowseBtn = getFormToolkit().createButton(this.form.getBody(), "", SWT.PUSH);
    this.pidcVersBrowseBtn.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        String pverName = HexFileSelectionDialog.this.compliReviewDialog.getPverNameText().getText();

        if ((HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion() != null) && (pverName != null)) {
          PidcVariantServiceClient varSerClient = new PidcVariantServiceClient();
          try {
            HexFileSelectionDialog.this.pidcVarMap = varSerClient.getSdomPverMappedVariants(
                HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion().getId(),
                HexFileSelectionDialog.this.compliReviewDialog.getPverNameText().getText());

          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }

        if (HexFileSelectionDialog.this.compliReviewDialog.isCompliReportStartedFromA2l()) {
          setSelPidcVarVer();
        }
        else {
          PidcVersionVaraintSelDialog paramAttrDepDialog =
              new PidcVersionVaraintSelDialog(Display.getDefault().getActiveShell(), HexFileSelectionDialog.this,
                  HexFileSelectionDialog.this.getCompliReviewDialog());
          paramAttrDepDialog.open();

          if (!HexFileSelectionDialog.this.isSamePidcElement() &&
              (null != HexFileSelectionDialog.this.getCompliReviewDialog().getPidcVersion())) {
            MessageDialogUtils.getErrorMessageDialog("PidcVersion is different",
                "Please choose the same Pidc Version (" +
                    HexFileSelectionDialog.this.getCompliReviewDialog().getPidcVersion().getName() + ").");
            return;
          }
        }

        if (isDuplicateHexPidcEltFound()) {
          MessageDialogUtils.getErrorMessageDialog(DUPLICATE_HEX_PIDC_VER_VAR_TITLE,
              DUPLICATE_HEX_PIDC_VERS_VAR_FOUND_MSG);
          return;
        }
        enableDisableDstBrowseBasedOnDst();
      }


    });
    this.pidcVersBrowseBtn.setImage(this.browseButtonImage);
    this.pidcVersBrowseBtn.setToolTipText("Select Pidc Version/Variant");

  }

  /**
   * Enable/Disbale DST browse button
   */
  private void enableDisableDstBrowseBasedOnDst() {
    if ((HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion() != null) &&
        HexFileSelectionDialog.this.compliReviewDialog.getCompliHexData().isVcdmAprjValSet() &&
        (HexFileSelectionDialog.this.compliReviewDialog.getPidcVariant() != null)) {
      HexFileSelectionDialog.this.dstBrowseBtn.setEnabled(true);
      HexFileSelectionDialog.this.dstFileText.setEnabled(true);
    }
    else {
      HexFileSelectionDialog.this.dstBrowseBtn.setEnabled(false);
      HexFileSelectionDialog.this.dstFileText.setEnabled(false);
    }
  }

  /**
   * set selected pidc version /variant
   */
  private void setSelPidcVarVer() {
    PidcVariant selPidcVariant = null;
    PidcVersion selPidcVersion = null;
    if (HexFileSelectionDialog.this.pidcVarMap.isEmpty()) {
      selPidcVersion = HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion();
    }
    else {
      final PidcVariantSelectionDialog variantSelDialog =
          new PidcVariantSelectionDialog(Display.getCurrent().getActiveShell(),
              HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion().getId(),
              HexFileSelectionDialog.this.compliReviewDialog.getPidcA2l().getId());
      variantSelDialog.open();
      selPidcVariant = variantSelDialog.getSelectedVariant();
      selPidcVersion = HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion();
    }

    setPidcDetails(selPidcVariant, selPidcVersion);
  }

  private void loadPidcVarMap() {
    String pverName = HexFileSelectionDialog.this.compliReviewDialog.getPverNameText().getText();

    if ((HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion() != null) && (pverName != null)) {
      PidcVariantServiceClient varSerClient = new PidcVariantServiceClient();
      try {
        HexFileSelectionDialog.this.pidcVarMap = varSerClient.getSdomPverMappedVariants(
            HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion().getId(),
            HexFileSelectionDialog.this.compliReviewDialog.getPverNameText().getText());
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  private String getPidcElementText(final PidcVariant selVar, final PidcVersion selPidcVer) {
    if (CommonUtils.isNull(selVar)) {
      return selPidcVer.getName();
    }
    return selVar.getName() + " [ PIDC Version : " + selPidcVer.getName() + " ]";
  }

  private void fillPidcVersIfNoVariant() {
    if (HexFileSelectionDialog.this.compliReviewDialog.isCompliReportStartedFromA2l() &&
        HexFileSelectionDialog.this.pidcVarMap.isEmpty()) {
      PidcVersion selPidcVersion = HexFileSelectionDialog.this.compliReviewDialog.getPidcVersion();
      setPidcDetails(null, selPidcVersion);
      this.pidcVersBrowseBtn.setEnabled(false);
      setFormMsg(true);
    }
  }

  private void setFormMsg(final boolean isMsgDisplayed) {
    if (isMsgDisplayed) {
      this.form.setText("* PIDC Version is loaded by default since there is No-Variant available for this PIDC");
      this.form.setFont(new Font(this.form.getDisplay(), new FontData("Arial", 10, SWT.BOLD)));
    }
    else {
      this.form.setText("");
    }
  }

  /**
   * @param selPidcVariant
   * @param selPidcVersion
   */
  private void setPidcDetails(final PidcVariant selPidcVariant, final PidcVersion selPidcVersion) {
    HexFileSelectionDialog.this.compliReviewDialog.setPidcVersion(selPidcVersion);
    setPidcVersId(selPidcVersion.getId());
    setPidcVersName(selPidcVersion.getName());
    getPidcObjNameText().setText(getPidcElementText(selPidcVariant, selPidcVersion).trim());
    if (null != selPidcVariant) {
      setPidcVariantId(selPidcVariant.getId());
      setPidcVariantName(selPidcVariant.getName());
      HexFileSelectionDialog.this.compliReviewDialog.setPidcVariant(selPidcVariant);
    }
    CompliReviewUsingHexData pidcData;
    try {
      pidcData = new PidcVersionServiceClient().getPidcVersDetailsForCompliHex(selPidcVersion.getId());
      HexFileSelectionDialog.this.compliReviewDialog.setCompliHexData(pidcData);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return
   */
  private boolean isDuplicateHexPidcEltFound() {
    List<HexFileSelectionRowProvider> hexList =
        HexFileSelectionDialog.this.compliReviewDialog.getHexFileRowProviderList();

    String pidcVersIdKey = null == getPidcVersId() ? "" : String.valueOf(getPidcVersId());
    String pidcVarIdKey = null == getPidcVariantId() ? "" : String.valueOf(getPidcVariantId());


    String pidcElementKey;
    boolean isDuplicateHexPidc = false;
    Map<String, Boolean> duplicateHexMap = new HashMap<>();
    Set<String> hexFileNameSet = populateHexFileNameSet(this.hexFileNameList);

    // collect duplicate entries
    if (CommonUtils.isNotEmpty(hexFileNameSet)) {
      hexFileNameSet.addAll(this.hexFileNameList);
      for (String hexFileName : hexFileNameSet) {
        pidcElementKey = pidcVersIdKey + pidcVarIdKey + hexFileName;
        duplicateHexMap.put(pidcElementKey, checkDuplicateHexPidcEle(hexList, pidcElementKey, hexFileName));
      }
    }
    else {
      String hexFileName = "";
      pidcElementKey = pidcVersIdKey + pidcVarIdKey + hexFileName;
      isDuplicateHexPidc = checkDuplicateHexPidcEle(hexList, pidcElementKey, hexFileName);
    }

    return duplicateHexMap.containsValue(true) || isDuplicateHexPidc;

  }

  /**
   * create set out of selected hex file names to avoid duplicates
   *
   * @param hexFileNameArray2
   * @return
   */
  private Set<String> populateHexFileNameSet(final List<String> hexFileName) {
    Set<String> hexFileNameSet = new HashSet<>();
    if (CommonUtils.isNullOrEmpty(hexFileName) && CommonUtils.isNotEmptyString(this.hexNameText.getText())) {
      Collections.addAll(hexFileNameSet, this.hexNameText.getText().split("\n"));
    }
    if (CommonUtils.isNotEmpty(hexFileName)) {
      hexFileNameSet.addAll(hexFileName);
    }
    return hexFileNameSet;
  }

  /**
   * @param hexList
   * @param pidcElementKey
   * @param isDuplicateHexPidc
   * @param hexFileName
   * @return
   */
  private boolean checkDuplicateHexPidcEle(final List<HexFileSelectionRowProvider> hexList, final String pidcElementKey,
      final String hexFileName) {
    boolean isDuplicateHexPidc = false;
    for (HexFileSelectionRowProvider hexFileSelectionRowProvider : hexList) {
      if (hexFileSelectionRowProvider.getPidcElementKey().equals(pidcElementKey)) {
        isDuplicateHexPidc = true;
        this.duplicateFileErrMsg.append(hexFileName).append("\n");

        this.dstFileText.setText("");
        this.dstBrowseBtn.setEnabled(false);
        break;
      }
    }
    return isDuplicateHexPidc;
  }

  /**
   * clear text set for pidc element
   */
  private void clearPidcEleText() {
    setPidcVersId(null);
    setPidcVersName(null);
    getPidcObjNameText().setText("");
    setPidcVariantId(null);
    setPidcVariantName(null);
  }

  private boolean isDuplicateDstFound() {
    // get full source path of dst
    String dstSrc = HexFileSelectionDialog.this.dstFullSourceNameMap.get(this.dstName);

    String varName = HexFileSelectionDialog.this.compliReviewDialog.getDstNameMap().getOrDefault(dstSrc, "");

    return this.compliReviewDialog.getPidcVariant().getName().equalsIgnoreCase(varName);
  }

  /**
   * checks whether the save button can be enabled or not
   */
  private void checkUploadBtnEnable() {
    this.uploadBtn.setEnabled(validateFields());
  }

  /**
   * Validate the a2l file name
   *
   * @return true if file name is available
   */
  private boolean validateFields() {
    boolean isValid = false;
    if (CommonUtils.isNotEmptyString(this.hexNameText.getText()) ||
        CommonUtils.isNotEmptyString(this.dstFileText.getText())) {
      isValid = true;
    }
    return isValid;
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
   * @return the pidcObjNameText
   */
  public Text getPidcObjNameText() {
    return this.pidcObjNameText;
  }


  /**
   * @param pidcObjNameText the pidcObjNameText to set
   */
  public void setPidcObjNameText(final Text pidcObjNameText) {
    this.pidcObjNameText = pidcObjNameText;
  }


  /**
   * @return the pidcVersName
   */
  public String getPidcVersName() {
    return this.pidcVersName;
  }


  /**
   * @param pidcVersName the pidcVersName to set
   */
  public void setPidcVersName(final String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }


  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }


  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }


  /**
   * @return the pidcVariantName
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }


  /**
   * @param pidcVariantName the pidcVariantName to set
   */
  public void setPidcVariantName(final String pidcVariantName) {
    this.pidcVariantName = pidcVariantName;
  }


  /**
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }


  /**
   * @param pidcVariantId the pidcVariantId to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }


  /**
   * @return the compliReviewDialog
   */
  public CompliReviewDialog getCompliReviewDialog() {
    return this.compliReviewDialog;
  }


  /**
   * @return the isSamePidcElement
   */
  public boolean isSamePidcElement() {
    return this.isSamePidcElement;
  }


  /**
   * @param isSamePidcElement the isSamePidcElement to set
   */
  public void setSamePidcElement(final boolean isSamePidcElement) {
    this.isSamePidcElement = isSamePidcElement;
  }

  /**
   * @param dstSource
   * @param dstText
   * @return DST path for pdf report
   */
  private String getFullDstSource(final String dstText) {
    String finalDst = "";

    if ((dstText != null) && !dstText.isEmpty()) {
      finalDst = dstText.replaceFirst(":", ".");
      finalDst = finalDst.replaceFirst(":", ";");
      finalDst = this.dstSource + ":" + finalDst;
    }

    return finalDst;
  }

  /**
   * @param dataSetModels
   * @param varName
   * @param projHavingSameVar
   */
  private void fillVcdmApplicationMap(final Set<VCDMApplicationProject> dataSetModels, final String varName,
      final List<VCDMApplicationProject> projHavingSameVar) {
    for (VCDMApplicationProject appProj : dataSetModels) {
      Map<String, VCDMProductKey> vcdmVariants = appProj.getVcdmVariants();

      // get the variant which has same name in icdm and vcdm
      VCDMProductKey varWithSameName = vcdmVariants.computeIfAbsent(varName, vcdmVariants::get);
      // clear all variants
      vcdmVariants.clear();

      if (varWithSameName != null) {
        // add only the variant whcih is selected
        vcdmVariants.put(varName, varWithSameName);
        projHavingSameVar.add(appProj);
      }
    }
  }

  /**
   * @return the pidcVarMap
   */
  public Map<Long, PidcVariant> getPidcVarMap() {
    return this.pidcVarMap;
  }


  /**
   * @param pidcVarMap the pidcVarMap to set
   */
  public void setPidcVarMap(final Map<Long, PidcVariant> pidcVarMap) {
    this.pidcVarMap = pidcVarMap;
  }

}
