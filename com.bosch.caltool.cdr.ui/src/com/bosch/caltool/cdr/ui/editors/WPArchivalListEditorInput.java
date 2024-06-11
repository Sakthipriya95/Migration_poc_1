/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.WPArchivalsListEditorDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.WpArchival;


/**
 * @author ukt1cob
 */
public class WPArchivalListEditorInput implements IEditorInput {


  /**
   * constant for string "Workpackage Archivals of "
   */
  private static final String WP_ARCHIVALS_OF_STR = "Workpackage Archivals of ";

  private IStructuredSelection selectedWPArchivals;

  private final WPArchivalsListEditorDataHandler dataHandler;

  private Set<WpArchival> wpArchivals = new HashSet<>();

  /**
   * Editor input creation
   *
   * @param pidcTreeNode pidc tree node
   */
  public WPArchivalListEditorInput(final PidcTreeNode pidcTreeNode) {
    this.dataHandler = new WPArchivalsListEditorDataHandler(pidcTreeNode);
    this.wpArchivals = this.dataHandler.getWpArchivals();
  }

  /**
   * @return the dataHandler
   */
  public WPArchivalsListEditorDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return WP_ARCHIVALS_OF_STR + getsEffectiveName();
  }

  /**
   * gets the Name of WP Archivals Editor
   *
   * @return effective name
   */
  // ICDM-2177
  public String getsEffectiveName() {
    return getDataHandler().getEffectiveName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return WP_ARCHIVALS_OF_STR + getsEffectiveName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.dataHandler == null) ? 0 : this.dataHandler.hashCode());
    result = (prime * result) + ((this.selectedWPArchivals == null) ? 0 : this.selectedWPArchivals.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WPArchivalListEditorInput other = (WPArchivalListEditorInput) obj;

    return CommonUtils.isEqual(getsEffectiveName(), other.getsEffectiveName());
  }


  /**
   * @return the selectedWPArchivals
   */
  public IStructuredSelection getSelectedWPArchivals() {
    return this.selectedWPArchivals;
  }


  /**
   * @param selectedWPArchivals the selectedWPArchivals to set
   */
  public void setSelectedWPArchivals(final IStructuredSelection selectedWPArchivals) {
    this.selectedWPArchivals = selectedWPArchivals;
  }


  /**
   * @return the wpArchivals
   */
  public Set<WpArchival> getWpArchivals() {
    return this.wpArchivals;
  }


  /**
   * @param wpArchivals the wpArchivals to set
   */
  public void setWpArchivals(final Set<WpArchival> wpArchivals) {
    this.wpArchivals = wpArchivals;
  }


}
