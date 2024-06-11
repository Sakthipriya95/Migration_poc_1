/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author say8cob
 */
public class OpenLinkFromMessageDialog extends Dialog {

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private final String displayMessage;

  private final String filePath;

  private final String dialogType;

  private final int logLevel;

  /**
   * @param parentShell as shell
   * @param displayMsg as display message
   * @param filePath as input file path
   * @param dialogType as dialog type like - INFO/WARN/ERROR
   * @param logLevel to find the Logger level
   */
  public OpenLinkFromMessageDialog(final Shell parentShell, final String displayMsg, final String filePath,
      final String dialogType, final int logLevel) {
    super(parentShell);
    this.displayMessage = displayMsg;
    this.filePath = filePath;
    this.dialogType = dialogType;
    this.logLevel = logLevel;
  }

  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(this.dialogType);
  }

  @Override
  protected Point getInitialSize() {
    return new Point(550, 200);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    createForm(container);
    return container;
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
   * This method initializes form
   */
  private void createForm(final Composite parent) {
    // Background color for dialog
    Color systemColor = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    Form form = getFormToolkit().createForm(parent);
    form.setBackground(systemColor);
    form.getBody().setLayout(gridLayout);
    Label dispMessage = getFormToolkit().createLabel(form.getBody(), this.displayMessage);
    dispMessage.setBackground(systemColor);

    final GridLayout subGridLayout = new GridLayout();
    subGridLayout.numColumns = 2;
    Form subForm = getFormToolkit().createForm(form.getBody());
    subForm.setBackground(systemColor);
    subForm.getBody().setLayout(subGridLayout);

    Label linkLabel = getFormToolkit().createLabel(subForm.getBody(), "Download Path : ");
    linkLabel.setBackground(systemColor);
    Link filePathLink = new Link(subForm.getBody(), SWT.NONE);
    String message = "<a>" + this.filePath + "</a>";
    filePathLink.setText(message);
    filePathLink.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
    filePathLink.setTouchEnabled(true);
    filePathLink.addListener(SWT.Selection, event -> {
      try {
        Desktop.getDesktop().open(new File(OpenLinkFromMessageDialog.this.filePath));
      }
      catch (IOException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    });
    StringBuilder logMsg = new StringBuilder();
    logMsg.append(this.displayMessage);
    logMsg.append("\n");
    logMsg.append("\n");
    logMsg.append("Download Path : ");
    logMsg.append(this.filePath);

    if (ILoggerAdapter.LEVEL_INFO == this.logLevel) {
      CDMLogger.getInstance().info(logMsg.toString(), Activator.PLUGIN_ID);
    }
    else if (ILoggerAdapter.LEVEL_WARN == this.logLevel) {
      CDMLogger.getInstance().warn(logMsg.toString(), Activator.PLUGIN_ID);
    }
    else if (ILoggerAdapter.LEVEL_ERROR == this.logLevel) {
      CDMLogger.getInstance().error(logMsg.toString(), Activator.PLUGIN_ID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
  }

}
