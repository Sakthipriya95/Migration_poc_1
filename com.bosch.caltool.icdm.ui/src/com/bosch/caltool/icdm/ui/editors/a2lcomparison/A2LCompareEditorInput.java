/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.a2l.A2LCompareHandler;

/**
 * @author bru2cob
 */
public class A2LCompareEditorInput implements IEditorInput {

  private A2LCompareHandler paramCompareHandler;
  private A2LCompareJob a2lCompareJob;


  /**
   * @param paramCompareHandler A2LCompareHandler
   */
  public A2LCompareEditorInput(final A2LCompareHandler paramCompareHandler) {
    super();
    this.paramCompareHandler = paramCompareHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getAdapter(final Class<T> arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "A2l Parameter Compare";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * @return the paramCompareHandler
   */
  public A2LCompareHandler getParamCompareHandler() {
    return this.paramCompareHandler;
  }

  /**
   * @param job A2LCompareJob
   */
  public void setA2LCompareJob(final A2LCompareJob job) {
    this.a2lCompareJob = job;

  }

  /**
   * @return the a2lCompareJob
   */
  public final A2LCompareJob getA2lCompareJob() {
    return this.a2lCompareJob;
  }


  /**
   * @param paramCompareHandler the paramCompareHandler to set
   */
  public final void setParamCompareHandler(final A2LCompareHandler paramCompareHandler) {
    this.paramCompareHandler = paramCompareHandler;
  }

}
