/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author NDV4KOR
 */
public class DataAssessmentDownloadDialog extends TitleAreaDialog {


  /**
   * File path selected
   */
  protected String filePath;


  /**
   * @param parentShell Shell
   * @param input Object
   * @param contentProvider IStructuredContentProvider
   * @param labelProvider ILabelProvider
   * @param message String
   */
  public DataAssessmentDownloadDialog(final Shell shell) {
    super(shell);
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    Composite composite;
    getShell().setText("Data Assessment Download Dialog");
    setTitle("Data Assessment Baseline");
    setMessage("Please select Destination filepath for Data Assessment : ", IMessageProvider.INFORMATION);
    composite = (Composite) super.createDialogArea(parent);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    composite.setLayout(new GridLayout());
    Group grp = new Group(composite, SWT.NONE);
    org.eclipse.swt.layout.GridLayout gridLayou1t = new org.eclipse.swt.layout.GridLayout();
    grp.setLayout(gridLayou1t);
    gridLayou1t.numColumns = 3;
    LabelUtil.getInstance().createLabel(grp, "Destination :");
    final Text fileText = TextUtil.getInstance().createText(grp, false, "");
    fileText.setEditable(false);
    fileText.setEnabled(true);

    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.verticalAlignment = GridData.FILL;

    txtGridData.widthHint = 450;
    fileText.setLayoutData(txtGridData);
    fileText.setEditable(false);
    this.filePath = CommonUtils.getUserDirPath() + "\\Downloads";

    fileText.setText(this.filePath);

    Button browseBtn = new Button(grp, SWT.CENTER);
    // image for browse button
    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));

    browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
        dialog.setFilterPath(CommonUtils.getUserDirPath());
        String selectedDestinationPath = dialog.open();
        if (CommonUtils.isNotEmptyString(selectedDestinationPath)) {
          DataAssessmentDownloadDialog.this.filePath = selectedDestinationPath;
        }
        fileText.setText(DataAssessmentDownloadDialog.this.filePath);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // NA
      }

    });
    return composite;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


}
