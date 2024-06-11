/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.listeners.ReviewFilesSelectionPageListener;
import com.bosch.caltool.cdr.ui.wizard.page.validator.ReviewFilesSelectionWizardPageValidator;
import com.bosch.caltool.cdr.ui.wizard.pages.resolver.ReviewFilesSelectionPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author adn1cob
 */
public class ReviewFilesSelectionWizardPage extends WizardPage {

  /**
   * Number of files selected
   */
  public static final int NUM_FILES_SELECTED = 1;
  /**
   * Constant to define size 1
   */
  public static final int SIZE_ONE = 1;

  /**
   * String constant for CDR_Repeated_Parameters
   */
  private static final String CDR_REPEATED_PARAMETERS = "CDR_Repeated_Parameters";
  /**
   * hex file extension
   */
  public static final String HEX = "hex";
  /**
   * Title for the wizard page
   */
  private static final String PAGE_TITLE = "Select Files to Review";
  /**
   * Description for the wizard page
   */
  public static final String PAGE_DESCRIPTION = "Please add desired files to the selection below." + "\n" +
      "Either one HEX file or many DCM/PaCo/CDF files can be reviewed.";
  /**
   * selected file
   */
  private String selectedFile[];
  /**
   * List of selected files
   */
  private final Set<String> selectedFilesPath = new TreeSet<String>();
  /**
   * Instance of calDataReviewWizard
   */
  private CalDataReviewWizard calDataReviewWizard;
  /**
   * array of deleted files
   */
  private String[] deletedFiles;
  /**
   * List of selected files
   */
  // ICDM-2355
  private Table filesList;
  /**
   * file delete button
   */
  private Button deleteFileBt;
  /**
   * selected file extension
   */
  private String selFileExtension;
  /**
   * String having names of files repeated
   */
  private String repeatedFiles;


  /**
   * String[] having names of files
   */
  private static final String fileNames[] = new String[] {
      "HEX files(*.HEX)",
      "DCM Files (*.DCM)",
      "PaCo Files (*.XML)",
      "CDFx Files (*.CDFX)",
      "All Calibration Data Files (*.HEX, *.DCM, *.XML, *.CDFX)" };
  /**
   * String[] having extn of files
   */
  private static final String fileExtensions[] =
      new String[] { "*.HEX", "*.DCM", "*.xml", "*.CDFx", "*.HEX;*.DCM;*.xml;*.CDFx" };

  /**
   * String[] having names of files
   */
  private static final String fileNamesForMonica[] = new String[] { "DCM Files (*.DCM)" };
  /**
   * String[] having extn of files
   */
  private static final String fileExtensionsForMonica[] = new String[] { "*.DCM" };
  /**
   * add button
   */
  private Button addFileButton;

  /**
   * @return the addFileButton
   */
  public Button getAddFileButton() {
    return this.addFileButton;
  }

  /**
   * hex file selected or not
   */
  private boolean hexFileSel;
  /**
   * Get the eclipse preference store
   */
  private transient final IPreferenceStore preference = PlatformUI.getPreferenceStore();


  /**
   * @return the hexFileSel
   */
  public boolean isHexFileSel() {
    return this.hexFileSel;
  }


  /**
   * @param hexFileSel the hexFileSel to set
   */
  public void setHexFileSel(final boolean hexFileSel) {
    this.hexFileSel = hexFileSel;
  }

  /**
   * ICDM 636 Rule to run the jobs in sequence.
   */
  private final MutexRule mutexRule = new MutexRule();

  private Composite tabComp;

  /**
   * @return the tabComp
   */
  public Composite getTabComp() {
    return this.tabComp;
  }

  /**
   * // Icdm-739 display invalid functions and labels
   */
  private List invalidFileList;

  /**
   * This flag is true if all the parameters does not have rules
   */
  protected boolean noValidRuleFlag;
  private Label dcmText;

  /**
   * @return the dcmText
   */
  public Label getDcmText() {
    return this.dcmText;
  }


  /**
   * @return the invalidFileList
   */
  public List getInvalidFileList() {
    return this.invalidFileList;
  }


  private ReviewFilesSelectionPageResolver reviewFilesSelectionPageResolver;

  private ReviewFilesSelectionWizardPageValidator reviewFilesSelectionWizardPageValidator;

  private ReviewFilesSelectionPageListener reviewFilesSelectionPageListener;

  private final CDRHandler cdrHandler = new CDRHandler();

  private Table table;
  private DropFileListener rvwFileDropFileListener;

  /**
   * Constructor
   *
   * @param pageName title
   */
  public ReviewFilesSelectionWizardPage(final String pageName) {
    super(pageName);
    setTitle(PAGE_TITLE);
    setDescription(PAGE_DESCRIPTION);
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_WIZARD_PG3_67X57));
    setPageComplete(false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    initializeDialogUnits(parent);
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite composite = new Composite(scrollComp, SWT.NONE);
    final GridLayout layout1 = new GridLayout();
    layout1.marginTop = 10;
    composite.setLayout(parent.getLayout());
    GridData layoutDataComp = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    layoutDataComp.grabExcessHorizontalSpace = false;
    composite.setLayoutData(layoutDataComp);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);

    this.calDataReviewWizard = (CalDataReviewWizard) ReviewFilesSelectionWizardPage.this.getWizard();

    this.dcmText = new Label(composite, SWT.NONE);
    this.dcmText.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    this.dcmText.setText(
        "Please select your DCM file of the MoniCa export. Only these DCMs are valid for the review type 'MoniCa-Protocol'. ");
    this.dcmText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
    this.dcmText.setVisible(false);

    final Group wpSelGroup = new Group(composite, SWT.NONE);
    wpSelGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
    gridDataWp.grabExcessHorizontalSpace = true;
    gridDataWp.grabExcessVerticalSpace = false;
    gridDataWp.heightHint = 200;
    wpSelGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    wpSelGroup.setText("Files to be Reviewed");
    final Composite workArea = new Composite(wpSelGroup, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginTop = 0;
    workArea.setLayout(layout);
    GridData layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    layoutData.grabExcessHorizontalSpace = true;
    layoutData.grabExcessVerticalSpace = false;
    workArea.setLayoutData(layoutData);

    // ICDM-2355


    this.filesList = createFileControl(workArea);

    // composite holds file control button
    final Composite fileBtComposite = new Composite(workArea, SWT.NONE);
    final GridLayout layoutPartComp = new GridLayout();
    layoutPartComp.numColumns = 1;
    layoutPartComp.marginWidth = 0;
    layoutPartComp.marginTop = 0;
    fileBtComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    fileBtComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    fileBtComposite.setLayout(layoutPartComp);


    createAddBtn(fileBtComposite);

    createDeleteBtn(fileBtComposite);
    wpSelGroup.layout();
    wpSelGroup.pack();
    // Icdm-739 display invalid functions and labels
    createInvalidFunLabSection(wpSelGroup);

    this.reviewFilesSelectionWizardPageValidator = new ReviewFilesSelectionWizardPageValidator(this.calDataReviewWizard,
        this.calDataReviewWizard.getFilesSelWizPage());
    this.reviewFilesSelectionPageListener =
        new ReviewFilesSelectionPageListener(this.calDataReviewWizard, this.reviewFilesSelectionWizardPageValidator);
    this.reviewFilesSelectionPageResolver = new ReviewFilesSelectionPageResolver(this.calDataReviewWizard);
    this.reviewFilesSelectionPageListener.createActionListeners();
    scrollComp.setContent(composite);
    scrollComp.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
  }

  /**
   * @param subParent /Icdm-739 display invalid functions and labels
   */
  private void createInvalidFunLabSection(final Composite subParent) {
    final Group wpSelGroup = new Group(subParent, SWT.NONE);
    wpSelGroup.setLayout(new GridLayout());
    GridData gridDataWp = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    gridDataWp.grabExcessHorizontalSpace = true;
    gridDataWp.grabExcessVerticalSpace = true;
    wpSelGroup.setLayoutData(gridDataWp);
    // Icdm-715 Source for the Lab fun file
    wpSelGroup.setText("Note: The parameters/functions below are INVALID and will be skipped from review !");
    this.tabComp = new Composite(wpSelGroup, SWT.NONE);

    final GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    this.tabComp.setLayout(layout);
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    this.tabComp.setLayoutData(gridData);

    wpSelGroup.layout();
    wpSelGroup.pack();
    this.invalidFileList = new List(this.tabComp, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);

    final GridData gridData1 = new GridData();
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.verticalAlignment = GridData.FILL;
    gridData1.heightHint = 8 * this.invalidFileList.getItemHeight();
    this.invalidFileList.setLayoutData(gridData1);

  }

  /**
   * @param fileBtComposite
   * @param addFileButton
   */
  private void createDeleteBtn(final Composite fileBtComposite) {
    Image deleteButtonImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.DELETE_16X16);
    this.deleteFileBt = new Button(fileBtComposite, SWT.PUSH);
    this.deleteFileBt.setImage(deleteButtonImage);
    this.deleteFileBt.setEnabled(false);

  }

  /**
   * @param fileBtComposite
   * @return
   */
  private Button createAddBtn(final Composite fileBtComposite) {
    this.addFileButton = new Button(fileBtComposite, SWT.PUSH);
    Image addBtImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.ADD_16X16);
    this.addFileButton.setImage(addBtImage);
    this.addFileButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    this.addFileButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    return this.addFileButton;
  }

  /**
   * @param workArea parent composite
   * @return
   */
  // ICDM-2355
  private Table createFileControl(final Composite workArea) {
    this.table = new Table(workArea, SWT.V_SCROLL | SWT.MULTI);
    GridData filesData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
    filesData.grabExcessVerticalSpace = true;
    filesData.grabExcessHorizontalSpace = true;
    // Icdm-790 Resize the File List Section
    filesData.heightHint = 8 * this.table.getItemHeight();
    this.table.setLayoutData(filesData);
    // Drag drop review file
    this.rvwFileDropFileListener = new DropFileListener(this.table, getFileextensions());
    this.rvwFileDropFileListener.addDropFileListener(false);
    this.rvwFileDropFileListener.setEditable(true);

    this.table.addMouseMoveListener(new MouseMoveListener() {

      @Override
      public void mouseMove(final MouseEvent arg0) {
        String dropFilePath = ReviewFilesSelectionWizardPage.this.rvwFileDropFileListener.getDropFilePath();
        if (null != dropFilePath) {
          ReviewFilesSelectionWizardPage.this.rvwFileDropFileListener.setDropFilePath(null);
          String[] filenames = new String[1];
          filenames[0] = dropFilePath;
          ReviewFilesSelectionWizardPage.this.setSelectedFile(filenames);
          String[] fileNameExtn = dropFilePath.split("\\.");
          ReviewFilesSelectionWizardPage.this.setSelFileExtension(fileNameExtn[fileNameExtn.length - 1]);
          ReviewFilesSelectionWizardPage.this.tableRemove(dropFilePath,
              ReviewFilesSelectionWizardPage.this.getFilesList());
          if (!ReviewFilesSelectionWizardPage.this.isHexFileSel()) {
            ReviewFilesSelectionWizardPage.this.reviewFilesSelectionPageListener.addFilesToTable(filenames, filenames);
          }
          else {
            CDMLogger.getInstance().errorDialog(ReviewFilesSelectionWizardPage.PAGE_DESCRIPTION, Activator.PLUGIN_ID);
          }
        }
      }
    });
    return this.table;
  }

  /**
   * to update page containers
   */
  public void updatePageContainer() {
    getContainer().updateButtons();
  }


  /**
   * Set the selected files in the caldata model
   */
  public void nextPressed() {
    this.reviewFilesSelectionPageResolver.setInput(this.calDataReviewWizard);
    this.reviewFilesSelectionPageResolver.processNextPressed();
  }

  /**
   * Data for cancelled Review
   */
  public void setDataForCancelPressed() {
    this.reviewFilesSelectionPageResolver.setInput(this.calDataReviewWizard);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    return this.reviewFilesSelectionWizardPageValidator.canFlipToNextPage();
  }


  /**
   * Clear the fields when different a2l file is selected
   */
  public void clearFields() {
    // ICDM-2355
    if ((null != this.filesList) && (this.filesList.getItemCount() != 0)) {
      this.filesList.removeAll();
      this.selectedFilesPath.clear();
    }
  }


  /**
   *
   */
  public void backPressed() {
    this.reviewFilesSelectionPageResolver.processBackPressed();
  }


  // ICDM-2355
  /**
   * @param funcName
   * @param table
   */
  public void tableRemove(final String funcName, final Table table) {
    TableItem[] items = table.getItems();
    int index = -1;
    for (TableItem tableItem : items) {
      index++;
      if (funcName.equals(tableItem.getText())) {
        table.remove(index);
        break;
      }
    }
  }


  /**
   * @return the selectedFilesPath
   */
  public Set<String> getSelectedFilesPath() {
    return this.selectedFilesPath;
  }

  /**
   * Method to get the list of file list in the review file selection wizard
   *
   * @return Table of file list items
   */
  // ICDM-2355
  public Table getFilesList() {
    return this.filesList;
  }


  /**
   * @return the selectedFile
   */
  public String[] getSelectedFile() {
    return this.selectedFile;
  }


  /**
   * @param selectedFile the selectedFile to set
   */
  public void setSelectedFile(final String[] selectedFile) {
    this.selectedFile = selectedFile;
  }


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @param calDataReviewWizard the calDataReviewWizard to set
   */
  public void setCalDataReviewWizard(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
  }


  /**
   * @return the deletedFiles
   */
  public String[] getDeletedFiles() {
    return this.deletedFiles;
  }


  /**
   * @param deletedFiles the deletedFiles to set
   */
  public void setDeletedFiles(final String[] deletedFiles) {
    this.deletedFiles = deletedFiles;
  }


  /**
   * @return the deleteFileBt
   */
  public Button getDeleteFileBt() {
    return this.deleteFileBt;
  }


  /**
   * @param deleteFileBt the deleteFileBt to set
   */
  public void setDeleteFileBt(final Button deleteFileBt) {
    this.deleteFileBt = deleteFileBt;
  }


  /**
   * @return the selFileExtension
   */
  public String getSelFileExtension() {
    return this.selFileExtension;
  }


  /**
   * @param selFileExtension the selFileExtension to set
   */
  public void setSelFileExtension(final String selFileExtension) {
    this.selFileExtension = selFileExtension;
  }


  /**
   * @return the repeatedFiles
   */
  public String getRepeatedFiles() {
    return this.repeatedFiles;
  }


  /**
   * @param repeatedFiles the repeatedFiles to set
   */
  public void setRepeatedFiles(final String repeatedFiles) {
    this.repeatedFiles = repeatedFiles;
  }


  /**
   * @return the noValidRuleFlag
   */
  public boolean isNoValidRuleFlag() {
    return this.noValidRuleFlag;
  }


  /**
   * @param noValidRuleFlag the noValidRuleFlag to set
   */
  public void setNoValidRuleFlag(final boolean noValidRuleFlag) {
    this.noValidRuleFlag = noValidRuleFlag;
  }


  /**
   * @return the numFilesSelected
   */
  public static int getNumFilesSelected() {
    return NUM_FILES_SELECTED;
  }


  /**
   * @return the sizeOne
   */
  public static int getSizeOne() {
    return SIZE_ONE;
  }


  /**
   * @return the cdrRepeatedParameters
   */
  public static String getCdrRepeatedParameters() {
    return CDR_REPEATED_PARAMETERS;
  }


  /**
   * @return the hex
   */
  public static String getHex() {
    return HEX;
  }


  /**
   * @return the pageTitle
   */
  public static String getPageTitle() {
    return PAGE_TITLE;
  }


  /**
   * @return the pageDescription
   */
  public static String getPageDescription() {
    return PAGE_DESCRIPTION;
  }


  /**
   * @return the filenames
   */
  public static String[] getFilenames() {
    return fileNames;
  }


  /**
   * @return the fileextensions
   */
  public static String[] getFileextensions() {
    return fileExtensions;
  }


  /**
   * @return the filenamesformonica
   */
  public static String[] getFilenamesformonica() {
    return fileNamesForMonica;
  }


  /**
   * @return the fileextensionsformonica
   */
  public static String[] getFileextensionsformonica() {
    return fileExtensionsForMonica;
  }


  /**
   * @return the preference
   */
  public IPreferenceStore getPreference() {
    return this.preference;
  }


  /**
   * @return the mutexRule
   */
  public MutexRule getMutexRule() {
    return this.mutexRule;
  }


  /**
   * @param filesList the filesList to set
   */
  public void setFilesList(final Table filesList) {
    this.filesList = filesList;
  }


  /**
   * @param addFileButton the addFileButton to set
   */
  public void setAddFileButton(final Button addFileButton) {
    this.addFileButton = addFileButton;
  }


  /**
   * @param tabComp the tabComp to set
   */
  public void setTabComp(final Composite tabComp) {
    this.tabComp = tabComp;
  }


  /**
   * @param invalidFileList the invalidFileList to set
   */
  public void setInvalidFileList(final List invalidFileList) {
    this.invalidFileList = invalidFileList;
  }


  /**
   * @param dcmText the dcmText to set
   */
  public void setDcmText(final Label dcmText) {
    this.dcmText = dcmText;
  }


  /**
   * @return the table
   */
  public Table getTable() {
    return this.table;
  }


  /**
   * @param table the table to set
   */
  public void setTable(final Table table) {
    this.table = table;
  }


  /**
   * @return the reviewFilesSelectionPageResolver
   */
  public ReviewFilesSelectionPageResolver getReviewFilesSelectionPageResolver() {
    return this.reviewFilesSelectionPageResolver;
  }


  /**
   * @param reviewFilesSelectionPageResolver the reviewFilesSelectionPageResolver to set
   */
  public void setReviewFilesSelectionPageResolver(
      final ReviewFilesSelectionPageResolver reviewFilesSelectionPageResolver) {
    this.reviewFilesSelectionPageResolver = reviewFilesSelectionPageResolver;
  }


  /**
   * @return the reviewFilesSelectionWizardPageValidator
   */
  public ReviewFilesSelectionWizardPageValidator getReviewFilesSelectionWizardPageValidator() {
    return this.reviewFilesSelectionWizardPageValidator;
  }


  /**
   * @param reviewFilesSelectionWizardPageValidator the reviewFilesSelectionWizardPageValidator to set
   */
  public void setReviewFilesSelectionWizardPageValidator(
      final ReviewFilesSelectionWizardPageValidator reviewFilesSelectionWizardPageValidator) {
    this.reviewFilesSelectionWizardPageValidator = reviewFilesSelectionWizardPageValidator;
  }


  /**
   * @return the reviewFilesSelectionPageListener
   */
  public ReviewFilesSelectionPageListener getReviewFilesSelectionPageListener() {
    return this.reviewFilesSelectionPageListener;
  }


  /**
   * @param reviewFilesSelectionPageListener the reviewFilesSelectionPageListener to set
   */
  public void setReviewFilesSelectionPageListener(
      final ReviewFilesSelectionPageListener reviewFilesSelectionPageListener) {
    this.reviewFilesSelectionPageListener = reviewFilesSelectionPageListener;
  }

  /**
   * @return the cdrHandler
   */
  public CDRHandler getCdrHandler() {
    return this.cdrHandler;
  }

}
