/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * @author dmo5cob
 */
public class ArtifactsSelectionDialog extends ListSelectionDialog {


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
  public ArtifactsSelectionDialog(final Shell parentShell, final Object input,
      final IStructuredContentProvider contentProvider, final ILabelProvider labelProvider, final String message) {
    super(parentShell, input, contentProvider, labelProvider, message);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {

    org.eclipse.swt.layout.GridLayout gridLayout = new org.eclipse.swt.layout.GridLayout();
    parent.setLayout(gridLayout);
    Group grp = new Group(parent, SWT.NONE);
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
    txtGridData.widthHint = 350;
    fileText.setLayoutData(txtGridData);
    fileText.setEditable(false);
    this.filePath = CommonUtils.getUserDirPath();
    fileText.setText(this.filePath);

    Button browseBtn = new Button(grp, SWT.NONE);
    // image for browse button
    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));

    browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
        dialog.setFilterPath(CommonUtils.getUserDirPath());
        String selectedDestinationPath = dialog.open();
        if (CommonUtils.isNotEmptyString(selectedDestinationPath)) {
          ArtifactsSelectionDialog.this.filePath = selectedDestinationPath;
        }
        fileText.setText(ArtifactsSelectionDialog.this.filePath);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // NA
      }


    });
    return super.createContents(parent);
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


}
