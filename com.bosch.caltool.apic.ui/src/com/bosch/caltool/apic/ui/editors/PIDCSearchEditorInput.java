/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.PidcSearchEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * ICDM-1135
 *
 * @author bru2cob
 */
public class PIDCSearchEditorInput implements IEditorInput {

  /**
   * pidc search editor handler instance
   */
  private final PidcSearchEditorDataHandler dataHandler = new PidcSearchEditorDataHandler();
  /**
   * outline data handler instance
   */
  private OutLineViewDataHandler outlineDataHandler;


  /**
   * @return the dataHandler
   */
  public PidcSearchEditorDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class arg0) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // Only one instance of PIDC Scout is allowed
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "PIDC Scout";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return "PIDC Scout";
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (getClass() == obj.getClass());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @param dataHandler2
   */
  public void setOutlineDataHandler(final OutLineViewDataHandler outlineDataHandler) {
    this.outlineDataHandler = outlineDataHandler;

  }


  /**
   * @return the outlineDataHandler
   */
  public OutLineViewDataHandler getOutlineDataHandler() {
    return this.outlineDataHandler;
  }

  /**
   * @return selected PIDC version in the results table
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.dataHandler.getSelectedPidcVersion();
  }


}
