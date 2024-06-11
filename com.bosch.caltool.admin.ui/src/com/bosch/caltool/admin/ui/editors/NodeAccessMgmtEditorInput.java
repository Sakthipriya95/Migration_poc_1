/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author say8cob
 */
public class NodeAccessMgmtEditorInput implements IEditorInput {


  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getAdapter(final Class<T> arg0) {
    // N.A.
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // N.A.
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // N.A.
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "Node Access Rights Management";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // N.A.
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return "Use this editor to manage access to various iCDM nodes";
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
