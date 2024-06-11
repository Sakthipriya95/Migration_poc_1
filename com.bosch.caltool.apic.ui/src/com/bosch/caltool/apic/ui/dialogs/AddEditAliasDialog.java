package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AliasDetailServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * This class provides UI to add Alias Dialog
 */
public class AddEditAliasDialog extends AbstractDialog {


  /**
   * Button instance for save
   */
  private Button saveBtn;

  /**
   * Composite instance for the dialog
   */
  private Composite composite;

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
   * Composite instance
   */
  private Composite top;

  // ICDM-1397
  /**
   * Text for adding comment when a value is edited
   */
  private Text aliasName;
  private final AliasDef aliasDefinition;
  private final Attribute selAttr;
  private final AliasDetail aliasDetail;
  private final AttributeValue selectedVal;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param aliasDefinition aliasDefinition
   * @param selectedAttr selectedAttr
   * @param selectedVal selectedVal
   * @param aliasDetail aliasDetail
   */
  public AddEditAliasDialog(final Shell parentShell, final AliasDef aliasDefinition, final Attribute selectedAttr,
      final AttributeValue selectedVal, final AliasDetail aliasDetail) {

    super(parentShell);
    this.aliasDefinition = aliasDefinition;
    this.selAttr = selectedAttr;
    this.selectedVal = selectedVal;
    this.aliasDetail = aliasDetail;
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

    if (CommonUtils.isNotNull(this.selAttr)) {

      // Set the title
      setTitle("Add attribute alias");

      // Set the message
      setMessage("Alias Name for Attribute : " + this.selAttr.getName());
    }
    else {
      // Set the title
      setTitle("Add value alias");

      // Set the message
      setMessage("Alias Name for Value : " + this.selectedVal.getNameRaw());
    }

    return contents;
  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-153
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell title
    newShell.setText("Add/Edit " + (CommonUtils.isNotNull(this.selAttr) ? "Attribute" : "Value") + " Alias");
    super.configureShell(newShell);
    super.setHelpAvailable(true);
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
   * This method creates the dialog ui
   *
   * @param top
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter Details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    // ICDM-1397
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Alias Name:");
    this.aliasName = getFormToolkit().createText(this.form.getBody(), null, SWT.BORDER);
    this.aliasName.setFocus();
    if (this.aliasDetail != null) {
      this.aliasName.setText(this.aliasDetail.getAliasName());
    }
    GridData commentGridData = GridDataUtil.getInstance().createGridData();
    this.aliasName.setLayoutData(commentGridData);
    this.aliasName.addModifyListener(modifyevent -> checkSaveBtnEnable());
  }


  @Override
  protected void okPressed() {
    boolean done = this.aliasDetail == null ? createAlias() : updateAlias();
    if (done) {
      super.okPressed();
    }
  }

  /**
   * Update command for existing data
   *
   * @return
   */
  private boolean updateAlias() {

    AliasDetail det = this.aliasDetail.clone();
    det.setAliasName(this.aliasName.getText());

    boolean done = false;
    try {
      new AliasDetailServiceClient().update(det);
      done = true;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return done;
  }

  /**
   * Insert command
   */
  private boolean createAlias() {

    AliasDetail det = new AliasDetail();
    det.setAliasName(this.aliasName.getText());
    if (CommonUtils.isNotNull(this.selAttr)) {
      det.setAttrId(this.selAttr.getId());
    }
    else {
      det.setValueId(this.selectedVal.getId());
    }
    det.setAdId(this.aliasDefinition.getId());

    boolean done = false;
    try {
      new AliasDetailServiceClient().create(det);
      done = true;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return done;


  }

  /**
   * Checks if the save button should be enabled
   */
  private void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(validateTextFields());
  }

  /**
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {

    return ((this.selAttr != null) || (this.selectedVal != null)) && (this.aliasName.getText() != null) &&
        !this.aliasName.getText().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

}
