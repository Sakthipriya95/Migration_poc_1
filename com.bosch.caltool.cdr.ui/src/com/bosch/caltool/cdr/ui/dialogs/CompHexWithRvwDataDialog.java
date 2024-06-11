/*
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import com.bosch.caltool.cdr.ui.jobs.CompareHexWithReviewDataJob;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.DSTSelectionDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.VcdmDataSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * compare hex with review data dialog ICDM-2474
 *
 * @author mkl2cob
 */
public class CompHexWithRvwDataDialog extends AbstractDialog {


  /**
   * pidcA2l
   */
  private final PidcA2l pidcA2l;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * Composite
   */
  private Composite composite;
  /**
   * Section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * Instance of selected variant
   */
  protected PidcVariant selctedVar;
  /**
   * Instance of save button
   */
  private Button saveBtn;
  /**
   * Variant text
   */
  private Text varText;
  /**
   * File text
   */
  private Text hexFileText;
  /**
   * Dst file text
   */
  private Text dstFileText;


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
   * Browse button
   */
  private Button browseBtn;
  private final PidcA2LBO pidcA2lBO;
  private TreeSet<PidcVariant> pidcVarSet;


  /**
   * Instantiates a new comp hex with rvw data dialog.
   *
   * @param pidcA2l the pidc A 2 l
   * @param shell the shell
   */
  public CompHexWithRvwDataDialog(final PidcA2l pidcA2l, final Shell shell) {
    super(shell);
    this.pidcA2l = pidcA2l;
    this.pidcA2lBO = new PidcA2LBO(pidcA2l.getId(), null);
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
    newShell.setText("Compare HEX with Review Data Dialog");
    super.configureShell(newShell);
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
    setTitle("Compare HEX file with Review Data");

    // Set the message
    setMessage("Generate report of HEX file comparison with latest review data from selected variant",
        IMessageProvider.INFORMATION);
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
    // create section
    createSection();

    return this.composite;
  }

  /**
   * create section
   */
  private void createSection() {
    this.section =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "Choose inputs for comparison");
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    // set the client
    this.section.setClient(this.form);
  }

  /**
   * create the form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // create the controls for variant selection
    createVariantControls();
    // create the controls for File Name selection
    createFileNameControls();
    // create the controls for dst File DST selection
    createDSTFileControls();
    // create the control to choose HistoryStatus
    final GridLayout gridLayout = new GridLayout();
    // 3 columns for the layout
    gridLayout.numColumns = 3;
    gridLayout.verticalSpacing = 10;
    gridLayout.horizontalSpacing = 10;
    gridLayout.makeColumnsEqualWidth = false;
    // set form layout
    this.form.getBody().setLayout(gridLayout);
    // set form data
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

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
    this.dstBrowseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        boolean canDownloadArtifacts = false;
        try {
          canDownloadArtifacts =
              new CurrentUserBO().canDownloadArtifacts(CompHexWithRvwDataDialog.this.pidcA2l.getProjectId());
          if (canDownloadArtifacts) {
            setVcdmDST();
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
        if (!canDownloadArtifacts) {
          MessageDialogUtils.getInfoMessageDialog("Insufficient privilege",
              "Insufficient privilege to view vCDM artifacts! ");
          return;
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // NA
      }
    });
  }


  /**
   * Gets the vcdm source.
   */
  private void setVcdmDST() {
    List<VCDMApplicationProject> dataSetModels;
    Long vCDMA2LFileId = CompHexWithRvwDataDialog.this.pidcA2lBO.getA2lFileBO().getVCDMA2LFileID();
    dataSetModels = CompHexWithRvwDataDialog.this.getvCDMDatasets(vCDMA2LFileId);
    if (CommonUtils.isNotEmpty(dataSetModels) && (CompHexWithRvwDataDialog.this.selctedVar != null)) {

      // selected variant name
      String varName = CompHexWithRvwDataDialog.this.selctedVar.getName();
      List<VCDMApplicationProject> projHavingSameVar = dataSetModels.stream()
          .filter(dst -> CommonUtils.isNotNull(dst.getVcdmVariants().get(varName))).collect(Collectors.toList());

      if (projHavingSameVar.isEmpty()) {
        CDMLogger.getInstance().infoDialog(CommonUtils.DST_NOT_AVAILABLE, Activator.PLUGIN_ID);
      }
      else {
        DSTSelectionDialog dstSelectionDialog = new DSTSelectionDialog(Display.getDefault().getActiveShell(),
            projHavingSameVar, CompHexWithRvwDataDialog.this.pidcA2lBO.getA2LFileName());
        dstSelectionDialog.open();

        // if any dst is selected assign dst values to relevant fields and disable hex file fields else enable both dst
        // and hex file fields
        if (dstSelectionDialog.getReturnCode() == 0) {
          CompHexWithRvwDataDialog.this.selRevision = dstSelectionDialog.getVcdmdstRevision();
          String dstName = dstSelectionDialog.getSelectedTreePath()
              .substring(dstSelectionDialog.getSelectedTreePath().indexOf(CommonUtils.VCDM_DELIMITER) + 1);
          CompHexWithRvwDataDialog.this.dstSource = dstSelectionDialog.getSelectedTreePath().substring(0,
              dstSelectionDialog.getSelectedTreePath().indexOf(CommonUtils.VCDM_DELIMITER));
          CompHexWithRvwDataDialog.this.dstFileText.setText(dstName);

          disableHexFileField();

        }

        // enable generate button if all relevant fields are selected
        enableDisableOKBtn();
      }

    }
    else {
      CDMLogger.getInstance().infoDialog(CommonUtils.DST_NOT_AVAILABLE, Activator.PLUGIN_ID);
    }
  }

  /**
   * Create variant controls
   */
  private void createVariantControls() {

    LabelUtil.getInstance().createLabel(this.form.getBody(), "Variant");
    this.varText = TextUtil.getInstance().createText(this.form.getBody(), false, "");
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    this.varText.setLayoutData(txtGridData);
    this.varText.setEditable(false);

    Button btnVariant = new Button(this.form.getBody(), SWT.NONE);
    btnVariant.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for
                                                                                                // browse
                                                                                                // button

    btnVariant.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // open dialog for selecting variant
        final PidcVariantSelectionDialog variantSelDialog = new PidcVariantSelectionDialog(
            Display.getCurrent().getActiveShell(), CompHexWithRvwDataDialog.this.pidcA2l.getId());
        variantSelDialog.open();
        final PidcVariant selectedVariant = variantSelDialog.getSelectedVariant();// variant selected from the dialog
        if (null != selectedVariant) {
          // check if newly selected PIDC variant is same as previous PIDC variant
          if (CommonUtils.isNotEqual(selectedVariant, CompHexWithRvwDataDialog.this.selctedVar)) {

            // check if Hex field is already selected then Dst field will be disabled
            if (CompHexWithRvwDataDialog.this.dstFileText.isEnabled()) {
              // clear dst field and enable hex field
              CompHexWithRvwDataDialog.this.dstFileText.setText("");
              enableHexFileField();
            }
            else if (CommonUtils.isEmptyString(CompHexWithRvwDataDialog.this.hexFileText.getText())) {
              enableDstfileField();
            }
          }
          // set the variant text and update the generate btn
          CompHexWithRvwDataDialog.this.varText.setText(selectedVariant.getName());
          CompHexWithRvwDataDialog.this.selctedVar = selectedVariant;
          // enable disable buttons
          enableDisableOKBtn();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // Not implemented
      }
    });

    btnVariant.setEnabled(false);

    if (varSelectionNeeded()) {
      // disable the button and the text field if the variants are not available
      btnVariant.setEnabled(true);
      this.varText.setEnabled(true);
    }
  }

  /**
   * @return
   */
  private boolean varSelectionNeeded() {
    try {
      this.pidcVarSet =
          new TreeSet<>(new PidcVariantServiceClient().getA2lMappedVariants(this.pidcA2lBO.getPidcA2lId()).values());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return CommonUtils.isNotEmpty(this.pidcVarSet);

  }

  /**
   * Create file name controls
   */
  private void createFileNameControls() {

    LabelUtil.getInstance().createLabel(this.form.getBody(), "HEX File");
    this.hexFileText = TextUtil.getInstance().createText(this.form.getBody(), true, "");
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.widthHint = 350;
    this.hexFileText.setLayoutData(txtGridData);
    this.hexFileText.setEditable(false);


    // Adding Drop Listener for hex file textbox
    DropFileListener hexFileDropFileListener = new DropFileListener(this.hexFileText, new String[] { "*.hex" });
    hexFileDropFileListener.addDropFileListener(false);
    hexFileDropFileListener.setEditable(true);

    this.hexFileText.addModifyListener((final ModifyEvent event) -> {
      CompHexWithRvwDataDialog.this.dstFileText.setEnabled(false);
      CompHexWithRvwDataDialog.this.dstBrowseBtn.setEnabled(false);
      enableDisableOKBtn();
    });

    this.browseBtn = new Button(this.form.getBody(), SWT.NONE);
    this.browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for
    // browse
    // button

    this.browseBtn.addSelectionListener(new SelectionListener() {


      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {

        FileDialog hexFileDialog = new FileDialog(Display.getCurrent().getActiveShell());
        hexFileDialog.setText("Choose Hex file");
        hexFileDialog.setFilterExtensions(new String[] { "*.hex" });
        hexFileDialog.setFilterNames(new String[] { "HEX File(*.hex)" });
        hexFileDialog.open();
        if (CommonUtils.isNotEmptyString(hexFileDialog.getFileName())) {
          CompHexWithRvwDataDialog.this.hexFileText
              .setText(hexFileDialog.getFilterPath() + File.separator + hexFileDialog.getFileName());
          // after selecting PIDC, if Hex field is selected, then Dst field will be disabled
          disableDstfileField();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // NA
      }
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Compare", true);
    this.saveBtn.setEnabled(false);
    // create button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * enable disable ok button
   */
  private void enableDisableOKBtn() {
    this.saveBtn.setEnabled(validateFields());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    CompHexMetaData metaData = new CompHexMetaData();
    String hexFilePath;
    // get the path to save the file
    if (CompHexWithRvwDataDialog.this.dstFileText.isEnabled() && (CompHexWithRvwDataDialog.this.selRevision != null)) {
      hexFilePath = fetchHexForDst();
      metaData.setHexFromVcdm(true);
      metaData.setSrcHexFilePath(hexFilePath);
      metaData.setVcdmDstSource(getFullDstSource(this.dstFileText.getText()));
      metaData.setVcdmDstVersId(CompHexWithRvwDataDialog.this.selRevision.getDstID());
    }
    else {
      hexFilePath = this.hexFileText.getText();
      metaData.setHexFromVcdm(false);
      metaData.setSrcHexFilePath(hexFilePath);
    }

    // start comparing job
    CompareHexWithReviewDataJob job = new CompareHexWithReviewDataJob("Comparsion of HEX file with Review Data",
        this.pidcA2lBO, this.selctedVar, metaData);
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    job.schedule();
    // ICDM-2608
    Job.getJobManager().resume();
    super.okPressed();
  }


  /**
   * @param dstSource
   * @param dstText
   * @return DST path for pdf report
   */
  private String getFullDstSource(final String dstText) {
    String finalDst = "";

    if ((dstText != null) && !dstText.isEmpty()) {
      finalDst = dstText.replaceFirst(CommonUtils.VCDM_DELIMITER, ".");
      finalDst = finalDst.replaceFirst(CommonUtils.VCDM_DELIMITER, ";");
      finalDst = this.dstSource + CommonUtils.VCDM_DELIMITER + finalDst;
    }
    return finalDst;
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
      return client.loadHexFile(CompHexWithRvwDataDialog.this.selRevision.getDstID(),
          CommonUtils.getICDMTmpFileDirectoryPath());
    }
    catch (Exception ex) {
      CDMLogger.getInstance().error("Error occurred while loading Hex file from vCDM", ex, Activator.PLUGIN_ID);
    }
    return "";
  }

  /**
   * validate feilds for ok button
   *
   * @return
   */
  private boolean validateFields() {
    if (varSelectionNeeded() && "".equals(this.varText.getText())) {
      return false;
    }
    return !("".equals(this.hexFileText.getText()) && "".equals(this.dstFileText.getText()));
  }

  /**
   * @param vCDMA2LFileId Long
   * @return List<VCDMApplicationProject>
   */
  public List<VCDMApplicationProject> getvCDMDatasets(final Long vCDMA2LFileId) {
    A2LFileInfoServiceClient client = new A2LFileInfoServiceClient();
    List<VCDMApplicationProject> dataSetModels;
    try {
      dataSetModels = client.getVcdmDataSets(vCDMA2LFileId);
      return dataSetModels;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return Collections.emptyList();
    }
  }

  /**
   * enable and disable Hex and Dst fields
   */
  private void enableHexFileField() {
    CompHexWithRvwDataDialog.this.browseBtn.setEnabled(true);
    CompHexWithRvwDataDialog.this.hexFileText.setEnabled(true);
  }

  private void disableHexFileField() {
    CompHexWithRvwDataDialog.this.browseBtn.setEnabled(false);
    CompHexWithRvwDataDialog.this.hexFileText.setEnabled(false);
  }

  private void enableDstfileField() {
    CompHexWithRvwDataDialog.this.dstFileText.setEnabled(true);
    CompHexWithRvwDataDialog.this.dstBrowseBtn.setEnabled(true);
  }

  private void disableDstfileField() {
    CompHexWithRvwDataDialog.this.dstBrowseBtn.setEnabled(false);
    CompHexWithRvwDataDialog.this.dstFileText.setEnabled(false);
  }
}
