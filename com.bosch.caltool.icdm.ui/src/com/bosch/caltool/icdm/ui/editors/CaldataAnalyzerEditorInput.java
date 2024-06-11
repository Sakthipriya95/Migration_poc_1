/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerEditorInput implements IEditorInput {

  /**
   * Access rights BO
   */
  private final NodeAccessPageDataHandler nodeAccessBO = new NodeAccessPageDataHandler(CaldataAnalyzerDummyIModel.INSTANCE);

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getAdapter(final Class<T> arg0) {
    // Not Applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // Not Applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Not Applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "Caldata Analyzer";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // Not Applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    // Not Applicable
    return null;
  }

  /**
   * @return the nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

}
