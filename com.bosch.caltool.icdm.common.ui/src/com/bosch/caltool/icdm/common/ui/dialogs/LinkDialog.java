/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * This dialog is to add or edit link
 *
 * @author mkl2cob
 */
public class LinkDialog extends AbstractDialog {

  /**
  *
  */
  private static final int DESC_FIELD_TEXT_SIZE = 2000;
  /**
   * Number of columns of the grid layout
   */
  private static final int GRID_NUM_COLS = 3;

  /**
   * label for link
   */
  protected Label valueLink;
  /**
   * text field for link
   */
  protected Text linkText;

  /**
   * label for eng desc
   */
  protected Label valueDescengLabel;
  /**
   * Text field for eng desc
   */
  protected Text descEngText;

  /**
   * label for german desc
   */
  protected Label valueDescGerLabel;
  /**
   * Text field for german desc
   */
  protected Text descGermText;

  /**
   * Top composite
   */
  private Composite top;

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
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  /**
   * Links Tab viewer
   */
  private GridTableViewer linksTabViewer;


  /**
   * Form instance
   */
  private Form form;

  /**
   * decoration for link text field
   */
  private ControlDecoration linkDec;
  /**
   * decoration for eng desc text field
   */
  private ControlDecoration engDec;
  /**
   * save button instance
   */
  private Button saveBtn;
  private final boolean createDescGer;

  /**
   * @param parentShell parent shell
   * @param createDescGer
   */
  public LinkDialog(final Shell parentShell, final boolean createDescGer) {
    super(parentShell);
    this.createDescGer = createDescGer;
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
    setTitle("");

    // Set the message
    setMessage("");

    return contents;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;
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
    gridLayout.numColumns = GRID_NUM_COLS;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();


    this.valueLink = getFormToolkit().createLabel(this.form.getBody(), "Link");
    this.linkText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.linkText.setLayoutData(txtGrid);
    this.linkText.setFocus();

    this.linkDec = new ControlDecoration(this.linkText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.linkDec, IUtilityConstants.MANDATORY_MSG);

    getFormToolkit().createLabel(this.form.getBody(), "");
    if (this.createDescGer) {
      this.valueDescengLabel = getFormToolkit().createLabel(this.form.getBody(), "Description (English): ");
    }
    else {
      this.valueDescengLabel = getFormToolkit().createLabel(this.form.getBody(), "Description : ");
    }
    this.descEngText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.descEngText.setLayoutData(txtGrid);
    this.descEngText.setTextLimit(DESC_FIELD_TEXT_SIZE);
    this.engDec = new ControlDecoration(this.descEngText, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.engDec, IUtilityConstants.MANDATORY_MSG);

    if (this.createDescGer) {

      getFormToolkit().createLabel(this.form.getBody(), "");
      this.valueDescGerLabel = getFormToolkit().createLabel(this.form.getBody(), "Description (German): ");
      this.descGermText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
      this.descGermText.setLayoutData(txtGrid);
      this.descGermText.setTextLimit(DESC_FIELD_TEXT_SIZE);
    }

    getFormToolkit().createLabel(this.form.getBody(), "");
    this.linkText.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {

        Validator.getInstance().validateNDecorate(LinkDialog.this.engDec, LinkDialog.this.linkDec,
            LinkDialog.this.descEngText, LinkDialog.this.linkText, true);
        if (!CommonUtils.isValidHyperlinkFormat(LinkDialog.this.linkText.getText())) {
          LinkDialog.this.decorators.showErrDecoration(LinkDialog.this.linkDec, IUtilityConstants.INVALID_LINK, true);
        }
        checkSaveBtnEnable();
      }
    });
    this.descEngText.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {

        Validator.getInstance().validateNDecorate(LinkDialog.this.engDec, LinkDialog.this.linkDec,
            LinkDialog.this.descEngText, LinkDialog.this.linkText, true);

        checkSaveBtnEnable();
      }
    });
  }

  /**
   * enables or disables save button based on validation
   */
  private void checkSaveBtnEnable() {
    this.saveBtn.setEnabled(validateFields());
  }

  /**
   * validates link & eng desc field
   *
   * @return
   */
  private boolean validateFields() {
    String value;

    if (!this.linkText.isDisposed()) {
      value = this.linkText.getText().trim();
      // ICDM-1500
      if (CommonUtils.isValidURLFormat(value)) {
        value = CommonUtils.formatUrl(this.linkText.getText().trim());
      }

      final String valDescEng = this.descEngText.getText();

      if (CommonUtils.isValidHyperlinkFormat(value) && !"".equals(value.trim()) && !"".equals(valDescEng.trim())) {
        return true;
      }

    }
    return false;
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

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    // ICDM-112
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * @return the linksTabViewer
   */
  public GridTableViewer getLinksTabViewer() {
    return this.linksTabViewer;
  }

  /**
   * @param linksTabViewer the linksTabViewer to set
   */
  public void setLinksTabViewer(final GridTableViewer linksTabViewer) {
    this.linksTabViewer = linksTabViewer;
  }

  /**
   * @param descEnglish String
   * @param linkData LinkData
   * @return boolean
   */
  protected boolean checkForDuplicateLinkDesc(final String descEnglish, final LinkData linkData) {
    if ((null != getLinksTabViewer()) && (getLinksTabViewer().getInput() != null)) {
      SortedSet<LinkData> input = (SortedSet<LinkData>) getLinksTabViewer().getInput();
      SortedSet<LinkData> tempList = new TreeSet<>(input);
      if (CommonUtils.isNotNull(linkData)) {
        // remove the current link data object during edit
        tempList.remove(linkData);
      }
      for (LinkData linkDataItem : tempList) {
        if (CommonUtils.isEqual(linkDataItem.getNewDescEng(), descEnglish)) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * @return the createDescGer
   */
  public boolean isCreateDescGer() {
    return this.createDescGer;
  }
}
