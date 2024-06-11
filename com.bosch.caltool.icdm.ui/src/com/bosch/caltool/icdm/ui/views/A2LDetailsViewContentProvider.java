/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.model.a2l.A2LDetailsStructureModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author bru2cob
 */
public class A2LDetailsViewContentProvider implements ITreeContentProvider {

  A2LDetailsStructureModel detailsStrucModel;

  /**
   * @param detailsStrucModel
   */
  public A2LDetailsViewContentProvider(final A2LDetailsStructureModel detailsStrucModel) {
    this.detailsStrucModel = detailsStrucModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getChildren(final Object inputElement) {

    if (inputElement instanceof A2lVariantGroup) {
      A2lVariantGroup varGrp = (A2lVariantGroup) inputElement;
      SortedSet<PidcVariant> pidcVarSet = new TreeSet<>();

      if (this.detailsStrucModel.getMappedVariantsMap().get(varGrp.getId()) != null) {
        for (PidcVariant pidcVarnt : this.detailsStrucModel.getMappedVariantsMap().get(varGrp.getId())) {
          pidcVarSet.add(pidcVarnt);
        }
        return pidcVarSet.toArray();
      }
    }
    else if (inputElement instanceof String) {
      ArrayList<Object> grpArrayList = new ArrayList<>();
      SortedSet<A2lVariantGroup> varGroupSet = new TreeSet<>();

      if (this.detailsStrucModel.getUnmappedVariants().isEmpty()) {
        addVariantGroups(grpArrayList, varGroupSet);
      }
      else {
        addVariantGroups(grpArrayList, varGroupSet);
        grpArrayList.addAll(this.detailsStrucModel.getUnmappedVariants());
        return grpArrayList.toArray();
      }
      return varGroupSet.toArray();
    }
    return null;
  }

  /**
   * @param grpArrayList
   * @param varGroupSet
   */
  private void addVariantGroups(final ArrayList<Object> grpArrayList, final SortedSet<A2lVariantGroup> varGroupSet) {
    for (A2lVariantGroup varGrp : this.detailsStrucModel.getA2lVariantGrpMap().values()) {
      varGroupSet.add(varGrp);
    }
    grpArrayList.addAll(varGroupSet);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    // Long object is the indication for displaying ROOT node
    // (a2l wp def version )
    if (inputElement instanceof Long) {

      return new Object[] { ApicUiConstants.STRUCTURE_VIEW_DEFAULT_NODE };
    }

    // Return an empty array if no nodes are available
    return new Object[] {};
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getParent(final Object arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasChildren(final Object inputElement) {
    if (inputElement instanceof A2lWpDefnVersion) {
      if (!this.detailsStrucModel.getA2lMappedVariantsMap().isEmpty() ||
          !this.detailsStrucModel.getA2lVariantGrpMap().values().isEmpty()) {
        return true;
      }
    }
    else if (inputElement instanceof A2lVariantGroup) {
      A2lVariantGroup varGrp = (A2lVariantGroup) inputElement;
      if ((varGrp.getId() != null) && (this.detailsStrucModel.getMappedVariantsMap().get(varGrp.getId()) != null) &&
          !this.detailsStrucModel.getMappedVariantsMap().get(varGrp.getId()).isEmpty()) {
        return true;
      }
    }
    else if (inputElement instanceof String) {
      return true;
    }
    return false;
  }

}
