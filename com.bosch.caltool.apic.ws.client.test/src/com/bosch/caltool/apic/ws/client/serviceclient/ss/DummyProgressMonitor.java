/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author bne4cob
 */
class DummyProgressMonitor implements IProgressMonitor {

  /**
   * {@inheritDoc}
   */
  @Override
  public void beginTask(final String arg0, final int arg1) {
    // No implemntation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void done() {
    // No implemntation

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void internalWorked(final double arg0) {
    // No implemntation

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCanceled() {
    // No implemntation
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCanceled(final boolean arg0) {
    // No implemntation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTaskName(final String arg0) {
    // No implemntation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void subTask(final String arg0) {
    // No implemntation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void worked(final int arg0) {
    // No implemntation
  }

}
