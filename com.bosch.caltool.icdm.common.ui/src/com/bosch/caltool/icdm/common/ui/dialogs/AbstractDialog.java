/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.net.MalformedURLException;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PartInitException;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.rcputils.browser.BrowserUtil;

/**
 * @author mkl2cob
 */
public class AbstractDialog extends TitleAreaDialog {

  /**
   * help link key prefix for views
   */
  private static final String HELP_LINK_PREFIX = "DIALOG_";

  /**
   * @param parentShell Shell
   */
  public AbstractDialog(final Shell parentShell) {
    super(parentShell);
    setHelpAvailable(true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createHelpControl(final Composite parent) {
    Image helpImage = JFaceResources.getImage(DLG_IMG_HELP);
    if ((helpImage != null) && (null != getHelpLink())) {
      return createHelpImageButton(parent, helpImage);
    }

    return parent;
  }


  /*
   * Creates a button with a help image. This is only used if there is an image available.
   */
  private ToolBar createHelpImageButton(final Composite parent, final Image image) {
    ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);
    ((GridLayout) parent.getLayout()).numColumns++;
    toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

    ToolItem item = new ToolItem(toolBar, SWT.NONE);
    item.setImage(image);
    item.setToolTipText(getHelpLink().getDescription());
    item.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        openHelpWikiLink();
      }
    });
    return toolBar;
  }

  /**
   * opens Help Wiki link in browser
   */
  protected void openHelpWikiLink() {
    try {
      BrowserUtil.getInstance().openExternalBrowser(getHelpLink().getLinkUrl());
    }
    catch (PartInitException | MalformedURLException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return URL as String
   */
  protected Link getHelpLink() {
    String suffixForHelpKey = HELP_LINK_PREFIX + getClass().getSimpleName();
    return CommonUiUtils.INSTANCE.getHelpLink(suffixForHelpKey);
  }
}
