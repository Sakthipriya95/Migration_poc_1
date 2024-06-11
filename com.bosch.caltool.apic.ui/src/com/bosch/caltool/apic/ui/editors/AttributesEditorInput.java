/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.AttrGroupClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrSuperGroupClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;


/**
 * @author adn1cob
 */
public class AttributesEditorInput implements IEditorInput {

  AttributeClientBO attrBO;
  AttrGroupClientBO attrGrpBO;
  AttrSuperGroupClientBO attrSuprGrpBO;
  AttributeValueClientBO attrValBO;
  AttrNValueDependencyClientBO attrNValDepBO;
  OutLineViewDataHandler outlineDataHandler;

  /**
   * @return the attrBO
   */
  public AttributeClientBO getAttrBO() {
    return this.attrBO;
  }


  /**
   * @param attrBO the attrBO to set
   */
  public void setAttrBO(final AttributeClientBO attrBO) {
    if (null != attrBO) {
      this.attrBO = attrBO;
    }
  }


  /**
   * @return the attrGrpBO
   */
  public AttrGroupClientBO getAttrGrpBO() {
    return this.attrGrpBO;
  }


  /**
   * @param attrGrpBO the attrGrpBO to set
   */
  public void setAttrGrpBO(final AttrGroupClientBO attrGrpBO) {
    if (null != attrGrpBO) {
      this.attrGrpBO = attrGrpBO;
    }
  }


  /**
   * @return the attrSuprGrpBO
   */
  public AttrSuperGroupClientBO getAttrSuprGrpBO() {
    return this.attrSuprGrpBO;
  }


  /**
   * @param attrSuprGrpBO the attrSuprGrpBO to set
   */
  public void setAttrSuprGrpBO(final AttrSuperGroupClientBO attrSuprGrpBO) {
    if (null != attrSuprGrpBO) {
      this.attrSuprGrpBO = attrSuprGrpBO;
    }
  }


  /**
   * @return the attrNValDepBO
   */
  public AttrNValueDependencyClientBO getAttrNValDepBO() {
    return this.attrNValDepBO;
  }


  /**
   * @param attrNValDepBO the attrNValDepBO to set
   */
  public void setAttrNValDepBO(final AttrNValueDependencyClientBO attrNValDepBO) {
    if (null != attrNValDepBO) {
      this.attrNValDepBO = attrNValDepBO;
    }
  }


  /**
   * @return the attrValBO
   */
  public AttributeValueClientBO getAttrValBO() {
    return this.attrValBO;
  }


  /**
   * @param attrValBO the attrValBO to set
   */
  public void setAttrValBO(final AttributeValueClientBO attrValBO) {
    this.attrValBO = attrValBO;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return true;
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
    // TODO Auto-generated method stub
    return "Attributes";
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
    return "Attributes";
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
    // TODO Auto-generated method stub
    return super.hashCode();
  }


  /**
   * @return the outlineDataHandler
   */
  public OutLineViewDataHandler getOutlineDataHandler() {
    return this.outlineDataHandler;
  }


  /**
   * @param outlineDataHandler the outlineDataHandler to set
   */
  public void setOutlineDataHandler(final OutLineViewDataHandler outlineDataHandler) {
    this.outlineDataHandler = outlineDataHandler;
  }


}
