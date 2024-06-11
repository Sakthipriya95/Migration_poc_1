/**
 *
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author adn1cob
 */
public class A2LBasicInfoPage extends FormPage {

  /**
   *
   */
  private static final String LABEL = "Label";

  private FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""

  private Composite a2lComposite;
  private Section section;
  private Form baseForm;

  private Section a2lHeaderSection;


  private final A2LFileInfo a2lFileInfo;

  /**
   * Editor Instance
   */
  private final A2LContentsEditor editor;

  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;

  private Form a2lDetailsForm;

  private final PidcA2LBO pidcA2LBO;


  /**
   * A2L info page, Constructor.
   *
   * @param editor editor
   * @param a2lFileInfo a2lFileInfo
   * @param pidcA2LBO the pidc A 2 LBO
   */
  public A2LBasicInfoPage(final FormEditor editor, final A2LFileInfo a2lFileInfo, final PidcA2LBO pidcA2LBO) {
    super(editor, "A2LInfo", Messages.getString("A2LInfoFormPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    // ICDM-249
    this.editor = (A2LContentsEditor) editor;
    this.a2lFileInfo = a2lFileInfo;
    this.pidcA2LBO = pidcA2LBO;
  }

  // ICDM-249
  @Override
  public void createPartControl(final Composite parent) {

    // ICDM-249
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText("A2L Information");
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   *
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createA2lComposite();

  }

  /**
   * This method initializes A2lComposite
   */
  private void createA2lComposite() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalSpan = 2;
    this.a2lComposite = this.nonScrollableForm.getBody();
    this.a2lComposite.setLayout(new GridLayout());
    this.a2lComposite.setLayoutData(gridData);
    createSection();
  }

  /**
   * This method initializes section
   */
  private void createSection() {

    final GridData gridData = getGridDataWithOutGrabExcessVSpace();
    this.section = this.formToolkit.createSection(this.a2lComposite,
        ExpandableComposite.TWISTIE | Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("File Information");
    this.section.setExpanded(true);

    createForm();
    this.section.setLayoutData(gridData);
    this.section.setClient(this.baseForm);
  }

  /**
   * This method initializes form
   */
  private void createForm() {

    final GridData gridDataOne = getGridData();
    final GridData gridDataTwo = getGridData();
    final GridData gridDataThree = getGridData();
    final GridData gridDataFour = getGridData();
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.baseForm = this.formToolkit.createForm(this.section);
    this.baseForm.getBody().setLayout(gridLayout);
    this.formToolkit.createLabel(this.baseForm.getBody(), "File Name");
    Text fileNameTextField = this.formToolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    fileNameTextField.setLayoutData(gridDataThree);
    fileNameTextField.setText(this.pidcA2LBO.getA2LFileName());
    fileNameTextField.setEditable(false);
    this.formToolkit.createLabel(this.baseForm.getBody(), "File Size");
    Text fileSizeTextField = this.formToolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    fileSizeTextField.setLayoutData(gridDataTwo);
    fileSizeTextField.setText(String.valueOf(this.a2lFileInfo.getA2lFileSize()));
    fileSizeTextField.setEditable(false);
    this.formToolkit.createLabel(this.baseForm.getBody(), "ASAP2 Version");
    Text asap2TextField = this.formToolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    asap2TextField.setLayoutData(gridDataOne);
    asap2TextField.setText(this.a2lFileInfo.getAsap2Version());
    asap2TextField.setEditable(false);
    // iCDM-1241
    this.formToolkit.createLabel(this.baseForm.getBody(), "Project ID Card");
    Text pidcTextField = this.formToolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    pidcTextField.setLayoutData(gridDataFour);
    pidcTextField.setText(((A2LContentsEditorInput) getEditorInput()).getPidcA2lBO().getPidcVersion().getName());
    pidcTextField.setEditable(false);
    createA2lDetailsComposite();

  }

  /**
   * @return
   */
  private GridData getGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * This method initializes a2lDetailsComposite
   */
  private void createA2lDetailsComposite() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.horizontalSpan = 2;
    Composite a2lDetailsComposite = this.formToolkit.createComposite(this.baseForm.getBody());
    a2lDetailsComposite.setLayout(new GridLayout());
    this.a2lHeaderSection = this.formToolkit.createSection(this.a2lComposite,
        ExpandableComposite.TWISTIE | Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.a2lHeaderSection.setText("A2L Details");
    this.a2lHeaderSection.setExpanded(true);
    createFormOne();
    this.a2lHeaderSection.setClient(this.a2lDetailsForm);
    this.a2lHeaderSection.setLayoutData(gridData);
  }

  /**
   * This method initializes form1
   */
  private void createFormOne() {

    final GridLayout gridLayout2 = new GridLayout();
    gridLayout2.numColumns = 2;
    gridLayout2.makeColumnsEqualWidth = true;
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.a2lDetailsForm = this.formToolkit.createForm(this.a2lHeaderSection);
    this.a2lDetailsForm.setLayoutData(gridData);
    createGroupOne();
    this.a2lDetailsForm.getBody().setLayout(gridLayout2);
    createGroupTwo();
  }

  /**
   * This method initializes group
   */
  private void createGroupOne() {

    final GridData gridDataOne = getGridData();
    final GridData gridDataTwo = getGridData();
    gridDataTwo.grabExcessVerticalSpace = false;

    final GridData gridDataThree = getGridData();
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    final GridData gridDataFour = getGridDataWithOutGrabExcessVSpace();
    Group a2lDetailsGroupOne = new Group(this.a2lDetailsForm.getBody(), SWT.NONE);
    a2lDetailsGroupOne.setLayoutData(gridDataFour);
    a2lDetailsGroupOne.setLayout(gridLayout);
    a2lDetailsGroupOne.setText("A2l Header");
    Label versionLabel = this.formToolkit.createLabel(a2lDetailsGroupOne, LABEL);
    versionLabel.setText("Version");
    Text versionTextField = this.formToolkit.createText(a2lDetailsGroupOne, null, SWT.SINGLE | SWT.BORDER);
    versionTextField.setLayoutData(gridDataThree);
    // 280020 null check for HeaderVersion
    if (this.a2lFileInfo.getHeaderVersion() != null) {
      versionTextField.setText(this.a2lFileInfo.getHeaderVersion());
    }
    versionTextField.setEditable(false);
    Label projectnoLabel = this.formToolkit.createLabel(a2lDetailsGroupOne, LABEL);
    projectnoLabel.setText("Project No.");
    Text projectnoTextField = this.formToolkit.createText(a2lDetailsGroupOne, null, SWT.SINGLE | SWT.BORDER);
    projectnoTextField.setLayoutData(gridDataOne);
    // 280020 null check for HeaderProjectNo
    if (this.a2lFileInfo.getHeaderProjectNo() != null) {
      projectnoTextField.setText(this.a2lFileInfo.getHeaderProjectNo());
    }
    projectnoTextField.setEditable(false);
    Label commentLabel = this.formToolkit.createLabel(a2lDetailsGroupOne, LABEL);
    commentLabel.setText("Comment");
    Text commentTextField = this.formToolkit.createText(a2lDetailsGroupOne, null, SWT.SINGLE | SWT.BORDER);
    commentTextField.setLayoutData(gridDataTwo);
    // 280020 null check for HeaderComment
    if (this.a2lFileInfo.getHeaderComment() != null) {
      commentTextField.setText(this.a2lFileInfo.getHeaderComment());
    }
    commentTextField.setEditable(false);
  }

  /**
   * @return
   */
  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }

  /**
   * This method initializes group1
   */
  private void createGroupTwo() {
    final GridData gridData = getGridData();
    final GridLayout gridLayout4 = new GridLayout();
    gridLayout4.numColumns = 2;
    final GridData gridData7 = getGridDataWithOutGrabExcessVSpace();
    Group a2lDetailsGroupTwo = new Group(this.a2lDetailsForm.getBody(), SWT.NONE);
    a2lDetailsGroupTwo.setLayoutData(gridData7);
    a2lDetailsGroupTwo.setLayout(gridLayout4);
    a2lDetailsGroupTwo.setText("A2L Project");
    Label nameLabel = this.formToolkit.createLabel(a2lDetailsGroupTwo, LABEL);
    nameLabel.setText("Name");
    Text nameTextField = this.formToolkit.createText(a2lDetailsGroupTwo, null, SWT.SINGLE | SWT.BORDER);
    nameTextField.setLayoutData(gridData);
    // 280020 null check for ProjectName
    if (this.a2lFileInfo.getProjectName() != null) {
      nameTextField.setText(this.a2lFileInfo.getProjectName());
    }
    nameTextField.setEditable(false);
    Label longNameLabel = this.formToolkit.createLabel(a2lDetailsGroupTwo, LABEL);
    longNameLabel.setText("Long Name");
    Text longNameTextField = this.formToolkit.createText(a2lDetailsGroupTwo, null, SWT.SINGLE | SWT.BORDER);
    longNameTextField.setLayoutData(gridData);
    // 280020 null check for ProjectLongIdentifier
    if (this.a2lFileInfo.getProjectLongIdentifier() != null) {
      longNameTextField.setText(this.a2lFileInfo.getProjectLongIdentifier());
    }
    longNameTextField.setEditable(false);
  }


}
