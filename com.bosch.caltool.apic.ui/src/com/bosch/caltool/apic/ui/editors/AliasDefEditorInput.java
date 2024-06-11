/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.apic.AliasDefEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.IIcdmEditorInput;
import com.bosch.caltool.icdm.model.apic.AliasDef;


/**
 * @author rgo7cob
 */
public class AliasDefEditorInput implements IIcdmEditorInput {


  /**
   * Editor ID
   */
  public static final String EDITOR_ID = "com.bosch.caltool.apic.ui.editors.AliasDefinitionEditor";

  /**
   * Main Data Handler
   */
  private final AliasDefEditorDataHandler dataHandler;

  /**
   * Access page data handler
   */
  private final NodeAccessPageDataHandler nodeAccess;

  /**
   * Outline view data handler
   */
  private OutLineViewDataHandler outlineDataHandler;

  /**
   * Constructor
   *
   * @param aliasDef AliasDef
   */
  public AliasDefEditorInput(final AliasDef aliasDef) {
    this.dataHandler = new AliasDefEditorDataHandler(aliasDef);
    this.nodeAccess = new NodeAccessPageDataHandler(aliasDef);
  }

  /**
   * @return the nodeAccess
   */
  public NodeAccessPageDataHandler getNodeAccess() {
    return this.nodeAccess;
  }

  /**
   * @return the aliasDef
   */
  public AliasDef getAliasDef() {
    return this.dataHandler.getAliasDef();
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
  public IPersistableElement getPersistable() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc} returns the name of the function
   */
  @Override
  public String getToolTipText() {
    return getAliasDef().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAliasDef().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getAliasDef().getId().intValue();
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

    AliasDefEditorInput other = (AliasDefEditorInput) obj;
    return getAliasDef().getId().longValue() == other.getAliasDef().getId().longValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AliasDefEditorDataHandler getDataHandler() {
    return this.dataHandler;
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
