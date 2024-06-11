/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditorInput;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;

/**
 * @author say8cob
 */
public class ReviewListEditorOpenAction extends Action {

  /**
   * Action name
   */
  private static final String ACTION_NAME = "List of Review Results";

  private final PidcTreeNode pidcTreeNode;

  /**
   * @param pidcTreeNode selected Pidc Tree Node
   */
  public ReviewListEditorOpenAction(final PidcTreeNode pidcTreeNode) {
    this.pidcTreeNode = pidcTreeNode;
    // Properties of the context menu will be set here
    setProperties();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    final ReviewListEditorInput input = new ReviewListEditorInput(this.pidcTreeNode);
    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
          ReviewListEditor.EDITOR_ID);
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Method to set the context menu properties
   */
  private void setProperties() {
    setText(ACTION_NAME);
    PidcVariant pidcVariant = this.pidcTreeNode.getPidcVariant();
    if (CommonUtils.isNotNull(pidcVariant) && pidcVariant.isDeleted()) {
      setEnabled(false);
    }
    if (CommonUtils.isNotNull(this.pidcTreeNode.getPidcA2l()) && !this.pidcTreeNode.getPidcA2l().isActive()) {
      setEnabled(false);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return ImageManager.getImageDescriptor(ImageKeys.RVW_RES_MAIN_16X16);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    // Not applicable
    return null;
  }


}
