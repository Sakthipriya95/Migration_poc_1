/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author hnu1cob
 */
public class UnmapA2lAdminEditorInput implements IEditorInput {

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
    return "Unmap A2L from PIDC Version";
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
    return "Use this editor to unmap A2L from PIDC Version for the user request";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    // Override equals, to return true, for 2 editor inputs of same type.
    // Prevents multiple instances of the this editor
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
}
