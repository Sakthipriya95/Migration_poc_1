package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeActionHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;

/**
 * This class provides a dialog to add a new attribute
 */
public class PIDCRenameDialog extends AbstractDialog {

  private Composite top;
  private FormToolkit formToolkit;
  private Composite composite;
  private Section section;
  private Form form;
  private Text nameEngText;
  private Text nameGerText;
  private Text descEngText;
  private Text descGermText;
  private Long nameValId;
  private Pidc pidc;
  private PidcVersion pidVersion;
  private PidcVariant pidVar;
  private PidcVersion pidcVersForVarData;
  /**
   * PIDCSubVariant instance
   */
  private PidcSubVariant pidSubVar;

  private ControlDecoration txtDescEngDec;
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  /**
   * the maximum length for the text,
   */
  private static final int MAX_LENGTH = 4000;
  private AttributeValue pidcVarAttrVal;

  /**
   * @param parentShell instance
   * @param pidcVersionInfo - pidc version info object
   */
  public PIDCRenameDialog(final Shell parentShell, final PidcVersion pidcVersion, final Pidc pidc) {
    super(parentShell);
    this.pidVersion = pidcVersion;
    this.pidc = pidc;
    this.nameValId = this.pidc.getNameValueId();
  }

  /**
   * @param pidcVersion TODO
   * @throws ApicWebServiceException
   */
  public PIDCRenameDialog(final Shell parentShell, final IProjectObject projObj, final PidcVersion pidcVersion)
      throws ApicWebServiceException {
    super(parentShell);
    if (projObj instanceof PidcVariant) {
      this.pidVar = (PidcVariant) projObj;
      AttributeValueServiceClient attrValClient = new AttributeValueServiceClient();
      this.pidcVarAttrVal = attrValClient.getById(this.pidVar.getNameValueId());
    }
    // ICDM-121
    else if (projObj instanceof PidcSubVariant) {
      this.pidSubVar = (PidcSubVariant) projObj;
      AttributeValueServiceClient attrValClient = new AttributeValueServiceClient();
      this.pidcVarAttrVal = attrValClient.getById(this.pidSubVar.getNameValueId());
    }
    this.pidcVersForVarData = pidcVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    if (CommonUtils.isNotNull(this.pidVersion)) {
      newShell.setText("Rename Project ID Card");
    }
    else if (CommonUtils.isNotNull(this.pidVar)) {
      newShell.setText("Rename Variant");
    }
    else if (CommonUtils.isNotNull(this.pidSubVar)) {
      newShell.setText(ApicUiConstants.RENAME_SUB_VARIANT);
    }
    super.configureShell(newShell);
    super.setHelpAvailable(true);
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
    if (CommonUtils.isNotNull(this.pidVersion)) {
      setTitle("Rename Project ID Card");
    }
    else if (CommonUtils.isNotNull(this.pidVar)) {
      setTitle("Rename Variant");
    }

    else if (CommonUtils.isNotNull(this.pidSubVar)) {
      setTitle(ApicUiConstants.RENAME_SUB_VARIANT);
    }
    // Set the message
    setMessage("Enter the details", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
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

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = getFormToolkit().createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setExpanded(true);
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);

    createNameEngField();

    if ((null != this.pidSubVar) || (null != this.pidVar)) {
      createNameGermField();
    }

    createDescEngField();

    createDescGerField();
  }

  /**
   *
   */
  private void createDescGerField() {
    getFormToolkit().createLabel(this.form.getBody(), "Description(German)");
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH, getTextAreaGridData());
    this.descGermText = boxContentDisplay.getText();
    if (CommonUtils.isNotNull(this.pidVersion)) {
      this.descGermText.setText((null == this.pidc.getDescGer()) ? "" : this.pidc.getDescGer());
    }
    else if (CommonUtils.isNotNull(this.pidVar) || CommonUtils.isNotNull(this.pidSubVar)) {
      this.descGermText
          .setText((null == this.pidcVarAttrVal.getDescriptionGer()) ? "" : this.pidcVarAttrVal.getDescriptionGer());
    }

  }

  /**
   *
   */
  private void createDescEngField() {
    getFormToolkit().createLabel(this.form.getBody(), "Description(English)");
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH, getTextAreaGridData());
    this.descEngText = boxContentDisplay.getText();

    if (CommonUtils.isNotNull(this.pidVersion)) {
      this.descEngText.setText((null == this.pidc.getDescEng()) ? "" : this.pidc.getDescEng());
    }
    else if (CommonUtils.isNotNull(this.pidVar) || CommonUtils.isNotNull(this.pidSubVar)) {
      this.descEngText
          .setText((null == this.pidcVarAttrVal.getDescriptionEng()) ? "" : this.pidcVarAttrVal.getDescriptionEng());
    }

    this.txtDescEngDec = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtDescEngDec, IUtilityConstants.MANDATORY_MSG);
    this.descEngText.addModifyListener(event -> checkSaveBtnEnable());
    getFormToolkit().createLabel(this.form.getBody(), "");
  }

  /**
   * ICDM 451 returns the layout needed for text area
   *
   * @return GridData
   */
  private GridData getTextAreaGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.verticalSpan = 2;
    gridData2.heightHint = 40;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   *
   */
  private void createNameGermField() {
    getFormToolkit().createLabel(this.form.getBody(), "Name(German)");
    this.nameGerText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameGerText.setLayoutData(GridDataUtil.getInstance().getGridData());
    if (CommonUtils.isNotNull(this.pidVersion)) {
      this.nameGerText.setText((null == this.pidc.getNameGer()) ? "" : this.pidc.getNameGer());
    }
    else if (CommonUtils.isNotNull(this.pidVar) || CommonUtils.isNotNull(this.pidSubVar)) {
      this.nameGerText
          .setText((null == this.pidcVarAttrVal.getTextValueGer()) ? "" : this.pidcVarAttrVal.getTextValueGer());
    }

  }

  /**
   *
   */
  private void createNameEngField() {
    if ((CommonUtils.isNotNull(this.pidSubVar)) && (CommonUtils.isNotNull(this.pidVar))) {
      getFormToolkit().createLabel(this.form.getBody(), "Name(English)");
    }
    else {
      getFormToolkit().createLabel(this.form.getBody(), "Name");
    }

    this.nameEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.nameEngText.setLayoutData(GridDataUtil.getInstance().getGridData());
    // ICDM-183
    this.nameEngText.setFocus();
    if (CommonUtils.isNotNull(this.pidVersion)) {
      this.nameEngText.setText((null == this.pidc.getNameEng()) ? "" : this.pidc.getNameEng());
    }
    else if (CommonUtils.isNotNull(this.pidVar) || CommonUtils.isNotNull(this.pidSubVar)) {
      this.nameEngText
          .setText((null == this.pidcVarAttrVal.getTextValueEng()) ? "" : this.pidcVarAttrVal.getTextValueEng());
    }

    ControlDecoration txtNameEngDec = new ControlDecoration(this.nameEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(txtNameEngDec, "This field is mandatory.");
    this.nameEngText.addModifyListener(event -> checkSaveBtnEnable());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final String nameEng = this.nameEngText.getText().trim();
    String nameGer = "";
    if (CommonUtils.isNotNull(this.nameGerText)) {
      nameGer = this.nameGerText.getText().trim();
    }
    if ((null == this.pidVar) || ((CommonUtils.isNotNull(this.pidVar)) &&
        CommonUiUtils.getInstance().validateVariantName(ApicConstants.VARIANT_CODE_ATTR, nameEng, nameGer))) {

      final String descEng = this.descEngText.getText().trim();
      final String descGer = this.descGermText.getText().trim();

      if (CommonUtils.isNotNull(this.pidVersion)) {
        PidcTreeActionHandler pidcTreeActionHandler = new PidcTreeActionHandler();
        pidcTreeActionHandler.updatePidcName(this.nameValId, nameEng, nameGer, descEng, descGer);
      }
      else if (CommonUtils.isNotNull(this.pidVar)) {

        this.pidcVarAttrVal.setTextValueEng(nameEng);
        this.pidcVarAttrVal.setTextValueGer(nameGer);
        this.pidcVarAttrVal.setDescriptionEng(descEng);
        this.pidcVarAttrVal.setDescriptionGer(descGer);
        PidcVariantData variantData = new PidcVariantData();
        variantData.setVarNameAttrValue(this.pidcVarAttrVal);
        variantData.setSrcPidcVar(this.pidVar);
        variantData.setPidcVersion(this.pidcVersForVarData);
        variantData.setNameUpdated(true);// for rename
        PidcVariantServiceClient pidcVarClient = new PidcVariantServiceClient();
        try {
          pidcVarClient.update(variantData);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      // ICDM-121
      else if (CommonUtils.isNotNull(this.pidSubVar)) {
        this.pidcVarAttrVal.setTextValueEng(nameEng);
        this.pidcVarAttrVal.setTextValueGer(nameGer);
        this.pidcVarAttrVal.setDescriptionEng(descEng);
        this.pidcVarAttrVal.setDescriptionGer(descGer);
        PidcSubVariantData variantData = new PidcSubVariantData();
        variantData.setSubvarNameAttrValue(this.pidcVarAttrVal);
        variantData.setSrcPidcSubVar(this.pidSubVar);
        variantData.setNameUpdated(true);// for rename
        PidcSubVariantServiceClient pidcVarClient = new PidcSubVariantServiceClient();
        try {
          pidcVarClient.update(variantData);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      super.okPressed();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }

  /**
   * Validates save button enable or disable
   */
  private void checkSaveBtnEnable() {
    getButton(IDialogConstants.OK_ID).setEnabled(validateFields());
  }

  /**
   * This method validates text and combo fields
   *
   * @return boolean
   */
  private boolean validateFields() {
    final String nameEng = this.nameEngText.getText();
    final String descEng = this.descEngText.getText();
    return (!"".equals(nameEng.trim()) && !"".equals(descEng.trim()));
  }
}