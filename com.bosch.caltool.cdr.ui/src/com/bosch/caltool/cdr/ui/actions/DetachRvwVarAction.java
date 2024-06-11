/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author rgo7cob
 */
public class DetachRvwVarAction extends Action {

  /**
   *
   */
  private static final String CONFIRM_MESS =
      "This will delete the mapping between the variant and result.Click ok to continue.";
  /**
   *
   */
  private static final String CONFIRM_TITLE = "Press ok to detach the variant from result";
  /**
   * menu manager
   */
  private final IMenuManager manager;
  /**
   * cdr result element
   */
  private final Object firstElement;
  /**
   * viewer viewer
   */
  private final TreeViewer viewer;
  private RvwVariant rvwVar;

  /**
   * @param manager manager
   * @param firstElement firstElement
   * @param viewer viewer
   */
  public DetachRvwVarAction(final IMenuManager manager, final Object firstElement, final TreeViewer viewer) {
    super();
    this.manager = manager;
    this.firstElement = firstElement;
    this.viewer = viewer;
    setProperties();
  }


  /**
   * set the properties
   */
  private void setProperties() {
    setText("Detach result from the variant");
    this.rvwVar = getReviewVar(this.firstElement);
    // Check for null review var and check for diff veraint
    if ((this.rvwVar != null) && CommonUtils.isNotNull(this.rvwVar.getVariantId()) && this.rvwVar.isLinkedVariant()) {
      this.manager.add(this);
      final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.REMOVE_16X16);
      setImageDescriptor(imageDesc);
      setEnabled(true);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    if (MessageDialogUtils.getConfirmMessageDialog(CONFIRM_TITLE, CONFIRM_MESS)) {
      createDelRvwVarCommad();
    }
    super.run();
  }


  /**
   * @param element firstElement
   * @return the cdr result.
   */
  private RvwVariant getReviewVar(final Object element) {
    if (element instanceof PidcTreeNode) {
      PidcTreeNode pidcTreeNode = (PidcTreeNode) element;
      return pidcTreeNode.getReviewVarResult();
    }
    return null;
  }


  /**
   * create rvw varaint commad
   *
   * @param reviewVar
   * @param result
   */
  private void createDelRvwVarCommad() {
    // Command for deleting the review variant
    RvwVariantServiceClient client = new RvwVariantServiceClient();
    try {
      client.delete(this.rvwVar);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

    // ICDM-2081
    if (null != this.viewer) {
      this.viewer.refresh();
    }
  }


}
