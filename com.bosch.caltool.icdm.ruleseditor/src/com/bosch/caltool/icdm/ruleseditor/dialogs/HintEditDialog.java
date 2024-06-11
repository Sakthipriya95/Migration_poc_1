/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;

// ICDM-1055
/**
 * This dialog to edit the Hint
 *
 * @author rgo7cob
 */
public class HintEditDialog extends AbstractDialog {

  /**
   * Review Comment Dialog
   */
  private static final String HINT_EDIT = "Edit the Hint Contents";
  /**
   * Top composite instance
   */
  private Composite top;
  /**
   * composite instance
   */
  private Composite composite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  /**
   * String to store review comments
   */
  private String reviewComment;

  /**
   * Text field for review comments
   */
  private Text hintText;

  /**
   * Result Parameter for which comment is to be updated - null in case of multiple params
   */
  private final IParameter selectedParam;
  /**
   * Save button
   */
  private Button saveBtn;
  /**
   * Details page
   */
  private final DetailsPage detailsPage;

  /**
   * Constant for Hint Height
   */
  private static final int HINT_HEIGHT = 100;

  /**
   * Constant for Max Char in Hint Text Area
   */
  private static final int MAX_HINT_LEN = 4000;

  /**
   * Constant for Grifd number of Columns
   */
  private static final int GRID_NUM_COL = 2;

  /**
   * the maximum length of text box content listener
   */
  private static final int MAX_LENGTH = 4000;

  /**
   * @param parentShell parent shell
   * @param selectedParam2 Result Parameter for which comment is to be updated - null in case of multiple params
   * @param detailsPage detailsPage
   */
  public HintEditDialog(final Shell parentShell, final IParameter selectedParam2, final DetailsPage detailsPage) {
    super(parentShell);
    this.selectedParam = selectedParam2;
    this.detailsPage = detailsPage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean close() {
    if (this.saveBtn.isEnabled() &&
        MessageDialogUtils.getConfirmMessageDialog("Save the Data", "Do you want to Save the Changes?")) {
      okPressed();
    }
    return super.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(HINT_EDIT);

    // Set the message
    setMessage("Edit the Hint comments for the selected parameter", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Hint Edit Dialog");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
  }

  /**
   * create composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createSection();
  }

  /**
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Edit Hint Text");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();

    this.section.setClient(this.form);

  }

  /**
   * create form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    if (CommonUtils.isNotNull(this.selectedParam)) {
      getFormToolkit().createLabel(this.form.getBody(), "Parameter :");
      getFormToolkit().createLabel(this.form.getBody(), this.selectedParam.getName());
    }
    getFormToolkit().createLabel(this.form.getBody(), "Review Hint:");

    // ICDM-2007 (Parent task : ICDM-1774)
    // grid data to remove the extra space at the end of the dialog
    gridData.heightHint = HINT_HEIGHT;
    gridData.verticalAlignment = GridData.END;

    TextBoxContentDisplay textBoxContentDisplay = new TextBoxContentDisplay(this.form.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH, gridData);
    this.hintText = textBoxContentDisplay.getText();

    if (this.selectedParam.getParamHint() != null) {
      this.hintText.setText(this.selectedParam.getParamHint());
    }
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = GRID_NUM_COL;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
    addModifyListener();

  }

  /**
   * Modify listener for text field
   */
  private void addModifyListener() {

    this.hintText.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        HintEditDialog.this.saveBtn.setEnabled(false);
        boolean condition1 = CommonUtils.isNotNull(HintEditDialog.this.hintText.getText()) &&
            !HintEditDialog.this.hintText.getText().isEmpty() && !CommonUtils
                .isEqual(HintEditDialog.this.hintText.getText(), HintEditDialog.this.selectedParam.getParamHint());
        boolean condition2 =
            (HintEditDialog.this.hintText.getText() != null) && HintEditDialog.this.hintText.getText().isEmpty() &&
                (HintEditDialog.this.selectedParam.getParamHint() != null);
        if (condition1 || condition2) {
          HintEditDialog.this.saveBtn.setEnabled(true);
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    if (this.hintText.getText().length() > MAX_HINT_LEN) {
      CDMLogger.getInstance().warnDialog("The Hint Length Cannot be more than 4000", Activator.PLUGIN_ID);
      return;
    }
    if (this.selectedParam instanceof Parameter) {
      try {

        Parameter param = ((Parameter) (this.selectedParam)).clone();
        CommonUtils.shallowCopy(param, this.selectedParam);
        param.setParamHint(this.hintText.getText());
        ParameterServiceClient client = new ParameterServiceClient();
        Parameter updatedParam = client.update(param);

        CommonUtils.shallowCopy(this.selectedParam, updatedParam);
        this.detailsPage.setSelectedParam(this.selectedParam);
        this.detailsPage.getHintTxtArea().setText(this.selectedParam.getParamHint());
      }
      catch (ApicWebServiceException | CloneNotSupportedException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    this.saveBtn.setEnabled(false);
    super.okPressed();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    this.reviewComment = "";
    super.cancelPressed();
  }

  /**
   * Get the review comments entered by the user
   *
   * @return Review comment
   */
  public String getReviewComments() {
    return this.reviewComment;
  }

  // Icdm-327
  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

}
