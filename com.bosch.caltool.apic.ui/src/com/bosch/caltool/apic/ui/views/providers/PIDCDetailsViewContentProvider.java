/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.SortedSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author adn1cob
 */
public class PIDCDetailsViewContentProvider implements ITreeContentProvider {

  /**
   * PIDC for this view page
   */
  private final PidcVersion pidVersion;

  private final AbstractProjectObjectBO projObjBO;

  /**
   * @param pidcVersion
   * @param pidcDataHandler
   */
  public PIDCDetailsViewContentProvider(final AbstractProjectObjectBO projObjBO) {
    this.pidVersion = projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion();
    this.projObjBO = projObjBO;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // Not required
  }

  /**
   * Get the PIDC Node
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    // Long object is the indication for displaying ROOT node (Pidc version id)
    if (inputElement instanceof Long) {
      return new Object[] { this.pidVersion };
    }

    // Return an empty array if no nodes are available
    return new Object[] {};
  }

  /**
   * Get the children to parent node as given below
   * <p>
   * PIDC - if virtual structure is enabled, return child virtual nodes, else variants <br>
   * Virtual Nodes - child virtual nodes<br>
   * Variant Virtual Nodes/Viarants - Sub Variants
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object parentElement) {

    boolean includeDeleted = PIDCDetailsViewPart.isDeletedNodesDisplayEnabled();

    // ICDM-2198
    // flag to display variants with uncleared / missing mandatory values required
    boolean showOnlyUncleared = PIDCDetailsViewPart.isUnclearedNodesDisplayEnabled();

    // Generate children for PIDC
    if (parentElement instanceof PidcVersion) {
      // ICDM-1119
      return getPidcNodeChildren(includeDeleted, showOnlyUncleared);
    }

    // Generate children for Variant
    if (parentElement instanceof PidcVariant) {
      PidcVariant variant = (PidcVariant) parentElement;
      PidcVariantBO handler = new PidcVariantBO(this.pidVersion, variant, this.projObjBO.getPidcDataHandler());
      return handler.getSubVariantsSet(includeDeleted, showOnlyUncleared).toArray();
    }

    // Generate children for PIDCNode
    if (parentElement instanceof PIDCDetailsNode) {
      return getPidcDetailNodeChildren((PIDCDetailsNode) parentElement, includeDeleted, showOnlyUncleared);
    }

    // Return an empty array if no nodes are available
    return new Object[] {};
  }


  /**
   * Get child nodes of the PIDC node
   *
   * @param parentNode Parent Detail node
   * @param includeDeleted include deleted child nodes
   * @param showOnlyUncleared flag to display variants with uncleared / missing mandatory values required
   * @return array of child nodes
   */
  private Object[] getPidcNodeChildren(final boolean includeDeleted, final boolean showOnlyUncleared) {

    PidcVersionBO handler = (PidcVersionBO) this.projObjBO;
    // If display virtual structure flag is true, return virtual nodes, else return the variants
    if (PIDCDetailsViewPart.isVirtualStructureDisplayEnabled()) {
      final SortedSet<PIDCDetailsNode> pidcNodes = handler.getRootVirtualNodes(includeDeleted, showOnlyUncleared);
      return pidcNodes == null ? handler.getVariantsSet(includeDeleted, showOnlyUncleared).toArray()
          : pidcNodes.toArray();
    }

    return handler.getVariantsSet(includeDeleted, showOnlyUncleared).toArray();
  }


  /**
   * Get child nodes of the PIDC Detail node
   *
   * @param parentNode Parent Detail node
   * @param includeDeleted include deleted child nodes
   * @param showOnlyUncleared flag to display variants with uncleared / missing mandatory values required
   * @return array of child nodes
   */
  private Object[] getPidcDetailNodeChildren(final PIDCDetailsNode parentNode, final boolean includeDeleted,
      final boolean showOnlyUncleared) {
    // If parent node is a vairant node, return sub variants, otherwise return child virtual nodes
    if (parentNode.isVariantNode()) {
      if (parentNode.getSubVariants().isEmpty()) {
        PidcVariant variant = parentNode.getPidcVariant();
        PidcVariantBO handler = new PidcVariantBO(this.pidVersion, variant, this.projObjBO.getPidcDataHandler());
        return handler.getSubVariantsSet(includeDeleted, showOnlyUncleared).toArray();
      }
      return parentNode.getSubVariants().toArray();
    }
    return parentNode.getVisibleChildNodes().toArray();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object element) {
    // Not required
    return null;
  }

  /**
   * Returns true, if the node has children, if any of the condition below is satisfied
   * <p>
   * PIDC - if virtual structure is enabled, return for child virtual nodes, else variants <br>
   * Virtual Nodes - child virtual nodes<br>
   * Variant Virtual Nodes/Viarants - Sub Variants
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object element) {

    boolean includeDeleted = PIDCDetailsViewPart.isDeletedNodesDisplayEnabled();

    // ICDM-2198
    // flag to display variants with uncleared / missing mandatory values required
    boolean showOnlyUncleared = PIDCDetailsViewPart.isUnclearedNodesDisplayEnabled();

    if (element instanceof PidcVersion) {
      PidcVersionBO handler = (PidcVersionBO) this.projObjBO;
      return !handler.getVariantsMap(includeDeleted, showOnlyUncleared).isEmpty();
    }

    if (element instanceof PidcVariant) {
      PidcVariant variant = (PidcVariant) element;
      PidcVariantBO handler = new PidcVariantBO(this.pidVersion, variant, this.projObjBO.getPidcDataHandler());
      return !handler.getSubVariantsMap(includeDeleted, showOnlyUncleared).isEmpty();
    }

    if (element instanceof PIDCDetailsNode) {
      PIDCDetailsNode pidNode = (PIDCDetailsNode) element;

      if (pidNode.isVariantNode()) {

        PidcVariant variant = pidNode.getPidcVariant();
        PidcVariantBO handler = new PidcVariantBO(this.pidVersion, variant, this.projObjBO.getPidcDataHandler());
        return !handler.getSubVariantsMap(includeDeleted, showOnlyUncleared).isEmpty();
      }
      return !pidNode.getVisibleChildNodes().isEmpty();
    }

    return false;
  }
}
