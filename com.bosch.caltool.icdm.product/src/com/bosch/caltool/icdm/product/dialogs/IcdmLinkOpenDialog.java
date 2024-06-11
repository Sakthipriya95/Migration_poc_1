/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.dialogs;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * Dialog to open an ICDM link. Paste the link to the text field and click OK.
 *
 * @author bne4cob
 */
// ICDM-2242
public class IcdmLinkOpenDialog extends AbstractDialog {

  /**
   * Diallog title
   */
  private static final String DIALOG_TITLE = "Open iCDM Link";

  /**
   * Minimum width of the dialog
   */
  private static final int DIALOG_MIN_WIDTH = 100;

  /**
   * Minimum height of the dialog
   */
  private static final int DIALOG_MIN_HEIGHT = 100;

  /**
   * Form toolkit
   */
  private FormToolkit formToolkit;

  /**
   * Link text field
   */
  private Text txtIcmdLink;

  /**
   * Control decoration for the link field
   */
  private ControlDecoration ctrlDec;


  /**
   * @param parentShell parent shell
   */
  public IcdmLinkOpenDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Paste the iCDM link below and click OK");

    return contents;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {

    final Composite top = (Composite) super.createDialogArea(parent);
    top.setLayout(new GridLayout());

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.minimumWidth = DIALOG_MIN_WIDTH;
    gridData.minimumHeight = DIALOG_MIN_HEIGHT;
    top.setLayoutData(gridData);

    createMainComposite(top);

    return top;
  }

  /**
   * Create the main composite and its contents
   */
  private void createMainComposite(final Composite top) {

    final Composite mainComposite = getFormToolkit().createComposite(top);
    GridLayout gridLayout = new GridLayout();
    mainComposite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    mainComposite.setLayoutData(gridData);
    // Create text field for Icdm link
    createLinkTextField(mainComposite);
  }

  /**
   * Create link text field
   *
   * @param comp parent composite
   */
  private void createLinkTextField(final Composite comp) {
    createLabelControl(comp, "iCDM Link (For example, 'icdm:cdrID,100')");
    this.txtIcmdLink = createTextField(comp);
    this.txtIcmdLink.setEnabled(true);
    this.txtIcmdLink.setEditable(true);
    String clipboardLink = getLinkFromClipboard();
    if (clipboardLink != null) {
      this.txtIcmdLink.setText(clipboardLink);
      this.txtIcmdLink.selectAll();
    }
    this.txtIcmdLink.addModifyListener(e -> enableOkButton());
    this.ctrlDec = new ControlDecoration(this.txtIcmdLink, SWT.LEFT | SWT.TOP);
    Decorators decor = new Decorators();
    decor.showErrDecoration(this.ctrlDec, "", false);

  }

  /**
   * Enable OK button only if ICDM link is provided and valid
   */
  private void enableOkButton() {
    Decorators decor = new Decorators();

    String linkText = this.txtIcmdLink.getText().trim();
    // Icdm link opener
    // ICDM-1649
    LinkProcessor linkOpener = new LinkProcessor(linkText);
    try {
      linkOpener.validateLink();
      getButton(IDialogConstants.OK_ID).setEnabled(true);
      decor.showErrDecoration(this.ctrlDec, "", false);
    }
    catch (ExternalLinkException exp) {
      // No logging of error required, since the text is typed
      getButton(IDialogConstants.OK_ID).setEnabled(false);
      decor.showErrDecoration(this.ctrlDec, exp.getMessage(), true);
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    this.txtIcmdLink.setFocus();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.txtIcmdLink.getText().trim().isEmpty()) {
      enableOkButton();
      return;
    }

    // ICDM-1649
    LinkProcessor linkOpener = new LinkProcessor(this.txtIcmdLink.getText().trim());
    try {
      if (linkOpener.openLink()) {
        // ICDM-2577 closing the intro page after editor is opened
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        super.okPressed();
      }
    }
    catch (ExternalLinkException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * Creates a label
   *
   * @param compparent composite
   * @param lblName label text
   */
  private void createLabelControl(final Composite comp, final String lblName) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);

  }

  /**
   * Creates a text field
   *
   * @param comp parent composite
   * @return Text the new field
   */
  private Text createTextField(final Composite comp) {
    final Text text = TextUtil.getInstance().createEditableText(this.formToolkit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  /**
   * @return FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Allow resizing
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * Find iCDM link from clipboard
   *
   * @return iCDM link from clipboard as plain text
   */
  private String getLinkFromClipboard() {
    String plainText = CommonUiUtils.getContentsFromClipBoard();

    if ((plainText != null) && plainText.startsWith(LinkRegistry.INSTANCE.getProtocolWithSep())) {
      getLogger().debug("Clipboard : iCDM Link found {}", plainText);
      return plainText;
    }
    return null;
  }

  /**
   * Logger
   *
   * @return CDMLogger
   */
  private CDMLogger getLogger() {
    return CDMLogger.getInstance();
  }

}
