/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.pidc.ComparePidcHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * PIDC Compare editor input class
 *
 * @author bru2cob
 */
public class PIDCCompareEditorInput implements IEditorInput {

  /**
   * List of objects to be compared
   */
  private List<IProjectObject> compareObjs = new ArrayList<>();


  private final ComparePidcHandler comparePidcHandler;

  private OutLineViewDataHandler outlineViewDataHandler;

  /**
   * @return the pidVersions
   */
  public List<IProjectObject> getCompareObjs() {
    return new ArrayList<>(this.compareObjs);
  }

  /**
   * @param pidVersions the pidVersions to set
   */
  public void setCompareObjs(final List<IProjectObject> pidVersions) {
    this.compareObjs =new ArrayList<>(pidVersions);
  }


  /**
   * @param projectObjectList
   * @param projObjBO
   */
  public PIDCCompareEditorInput(final List<IProjectObject> projectObjectList, final AbstractProjectObjectBO projObjBO) {
    this.compareObjs = projectObjectList;
    if (projectObjectList.get(0) instanceof PidcVersion) {
      this.comparePidcHandler = new ComparePidcHandler(this.compareObjs, projObjBO, true);
    }
    else {
      this.comparePidcHandler = new ComparePidcHandler(this.compareObjs, projObjBO, false);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // name for PIDC variant compare editor
    if (this.compareObjs.get(0) instanceof PidcVariant) {
      return "PIDC Variants Compare";
    }
    // name for PIDC sub-variant compare editor
    else if (this.compareObjs.get(0) instanceof PidcSubVariant) {
      return "PIDC SubVariants Compare";
    }
    // name for PIDC version compare
    return "PIDC Comapre";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    //Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    StringBuilder toolTip = new StringBuilder();
    // tooltip for PIDC variant compare editor
    if (this.compareObjs.get(0) instanceof PidcVariant) {
      toolTip.append("Project Variant Compare :\n");
      for (IProjectObject var : this.compareObjs) {
        toolTip.append(var.getName()).append("\n");
      }

    }
    // tooltip for PIDC sub-variant compare editor
    else if (this.compareObjs.get(0) instanceof PidcSubVariant) {
      toolTip.append("Project Sub-Variant Compare :\n");
      for (IProjectObject subVar : this.compareObjs) {
        toolTip.append(subVar.getName()).append("\n");
      }
    }
    // tooltip for PIDC version compare
    else {
      toolTip.append("Project Compare :\n");
      for (IProjectObject ver : this.compareObjs) {
        toolTip.append(ver.getName()).append("\n");
      }
    }
    return toolTip.toString();
  }


  /**
   * @return the comparePidcHandler
   */
  public ComparePidcHandler getComparePidcHandler() {
    return this.comparePidcHandler;
  }


  /**
   * @return the outlineViewDataHandler
   */
  public OutLineViewDataHandler getOutlineViewDataHandler() {
    return this.outlineViewDataHandler;
  }


  /**
   * @param outlineViewDataHandler the outlineViewDataHandler to set
   */
  public void setOutlineViewDataHandler(final OutLineViewDataHandler outlineViewDataHandler) {
    this.outlineViewDataHandler = outlineViewDataHandler;
  }


}
