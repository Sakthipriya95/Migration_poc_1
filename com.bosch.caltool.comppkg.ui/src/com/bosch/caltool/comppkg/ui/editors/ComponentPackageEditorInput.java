/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.comppkg.ComponentPackageEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * ICDM-748
 *
 * @author bru2cob
 */
public class ComponentPackageEditorInput implements IEditorInput {


  private final NodeAccessPageDataHandler nodeAccessBO;
  private final ComponentPackageEditorDataHandler dataHandler;

  private CompPkgBc selectedPkgBc;


  /**
   * @param compPkg .
   */
  public ComponentPackageEditorInput(final CompPackage compPkg) {
    this.dataHandler = new ComponentPackageEditorDataHandler(compPkg);
    this.nodeAccessBO = new NodeAccessPageDataHandler(compPkg);
  }


  /**
   * @return the nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

  /**
   * @return the compPkgData
   */
  public CompPkgData getCompPkgData() {
    return this.dataHandler.getCompPkgData();
  }

  /**
   * @return the selectedPkgBc
   */
  public CompPkgBc getSelectedPkgBc() {
    return this.selectedPkgBc;
  }


  /**
   * @param selectedPkgBc the selectedPkgBc to set
   */
  public void setSelectedPkgBc(final CompPkgBc selectedPkgBc) {
    this.selectedPkgBc = selectedPkgBc;
  }


  /**
   * @return the selectedPkgFc
   */
  public CompPkgFc getSelectedPkgFc() {
    return this.selectedPkgFc;
  }


  /**
   * @param selectedPkgFc the selectedPkgFc to set
   */
  public void setSelectedPkgFc(final CompPkgFc selectedPkgFc) {
    this.selectedPkgFc = selectedPkgFc;
  }

  private CompPkgFc selectedPkgFc;


  /**
   * @return selected cmp pkg
   */
  public CompPackage getSelectedCmpPkg() {
    return getDataHandler().getComponentPackage();
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
    return getSelectedCmpPkg().getName();
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
    return "Component Package " + getSelectedCmpPkg().getName() + "\n" + getSelectedCmpPkg().getDescription();
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

    ComponentPackageEditorInput other = (ComponentPackageEditorInput) obj;
    return CommonUtils.isEqual(getSelectedCmpPkg(), other.getSelectedCmpPkg());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getSelectedCmpPkg().hashCode();
  }


  /**
   * @return the compPkgBo
   */
  public ComponentPackageEditorDataHandler getDataHandler() {
    return this.dataHandler;
  }
}
