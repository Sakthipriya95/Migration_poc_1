/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPDefBO;
import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;

/**
 * The Class FC2WPEditorInput.
 *
 * @author gge6cob
 */
public class FC2WPEditorInput implements IEditorInput, ILinkSelectionProvider {

  /** The fc2wp mapping. */
  private FC2WPMappingWithDetails fc2wpMapping;

  /** The fc2wp def. */
  private FC2WPDef fc2wpDef;

  /** The fc2wp version. */
  private FC2WPVersion fc2wpVersion;

  /** The fc2wp def BO. */
  private FC2WPDefBO fc2wpDefBO;

  private Map<Integer, String> natTableHeaderMap;

  private FC2WPMappingResult result;
  private final NodeAccessPageDataHandler nodeAccessBO;

  private FC2WPEditorDataHandler fc2wpEditorDataHandler;

  /**
   * Instantiates a new FC2WP editor input.
   *
   * @param newDef the new def
   * @param activeVers the active vers
   */
  public FC2WPEditorInput(final FC2WPDef newDef, final FC2WPVersion activeVers) {
    this.fc2wpVersion = activeVers;
    this.fc2wpDef = newDef;
    this.fc2wpDefBO = new FC2WPDefBO(this.fc2wpVersion);
    this.fc2wpMapping = this.fc2wpDefBO.getFc2wpMappingWithDetails();
    this.nodeAccessBO = new NodeAccessPageDataHandler(this.fc2wpDef);
    this.fc2wpEditorDataHandler = new FC2WPEditorDataHandler(this.fc2wpDefBO);
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
  public Object getEditorInputSelection() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return false;
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
    if ((this.fc2wpVersion != null) && (this.fc2wpDefBO.getFC2WPMappingWithDetailsList().size() == 1)) {
      return this.fc2wpVersion.getName();
    }
    return "FC2WP Compare";
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
    FC2WPEditorInput other = (FC2WPEditorInput) obj;
    return this.fc2wpVersion.getId().longValue() == other.getFc2wpVersion().getId().longValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.fc2wpVersion.getId().intValue();
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
    return "FC2WP";
  }

  /**
   * Gets the fc2wp def.
   *
   * @return the fc2wpDef
   */
  public FC2WPDef getFc2wpDef() {
    return this.fc2wpDef;
  }

  /**
   * Sets the fc2wp def.
   *
   * @param fc2wpDef the fc2wpDef to set
   */
  public void setFc2wpDef(final FC2WPDef fc2wpDef) {
    this.fc2wpDef = fc2wpDef;
  }

  /**
   * Gets the fc2wp version.
   *
   * @return the fc2wpVersion
   */
  public FC2WPVersion getFc2wpVersion() {
    return this.fc2wpVersion;
  }

  /**
   * Sets the fc2wp version.
   *
   * @param fc2wpVersion the fc2wpVersion to set
   */
  public void setFc2wpVersion(final FC2WPVersion fc2wpVersion) {
    this.fc2wpVersion = fc2wpVersion;
  }

  /**
   * Gets the fc2wp mapping.
   *
   * @return the fc2wpMapping
   */
  public FC2WPMappingWithDetails getFc2wpMapping() {
    return this.fc2wpMapping;
  }

  /**
   * Gets the FC2WP def BO.
   *
   * @return FC2WPDefBO of this FC2WPDefBO
   */
  public FC2WPDefBO getFC2WPDefBO() {
    if (this.fc2wpDefBO == null) {
      this.fc2wpDefBO = new FC2WPDefBO(this.fc2wpVersion);
    }
    return this.fc2wpDefBO;
  }

  /**
   * Gets the nat table header map.
   *
   * @return the nat table header map
   */
  public Map<Integer, String> getNatTableHeaderMap() {
    return this.natTableHeaderMap;
  }

  /**
   * Sets the nat table header map.
   *
   * @param natTableHeaderMap the nat table header map
   */
  public void setNatTableHeaderMap(final Map<Integer, String> natTableHeaderMap) {
    this.natTableHeaderMap = natTableHeaderMap;
  }

  /**
   * Sets the FC2WP mapping result.
   *
   * @param result the new FC2WP mapping result
   */
  public void setFC2WPMappingResult(final FC2WPMappingResult result) {
    this.result = result;
  }

  /**
   * Gets the FC2WP mapping result.
   *
   * @return the FC2WP mapping result
   */
  public FC2WPMappingResult getFC2WPMappingResult() {
    return this.result;
  }


  /**
   * @param fc2wpDefBO the fc2wpDefBO to set
   */
  public void setFc2wpDefBO(final FC2WPDefBO fc2wpDefBO) {
    this.fc2wpDefBO = fc2wpDefBO;
  }


  /**
   * @param fc2wpMapping the fc2wpMapping to set
   */
  public void setFc2wpMapping(final FC2WPMappingWithDetails fc2wpMapping) {
    this.fc2wpMapping = fc2wpMapping;
  }

  /**
   * @return nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

  /**
   * @return the fc2wpEditorDataHandler
   */
  public FC2WPEditorDataHandler getFc2wpEditorDataHandler() {
    return this.fc2wpEditorDataHandler;
  }

  /**
   * @param fc2wpEditorDataHandler the fc2wpEditorDataHandler to set
   */
  public void setFc2wpEditorDataHandler(final FC2WPEditorDataHandler fc2wpEditorDataHandler) {
    this.fc2wpEditorDataHandler = fc2wpEditorDataHandler;
  }
}