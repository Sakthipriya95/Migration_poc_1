/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * New Content Provider for PIDCGrpdAttrChangesDialog table viewer
 *
 * @author dmo5cob
 */
public class PIDCGrpdAttrChangesContentProvider implements ITreeContentProvider {

  private final List<GroupdAttrPredefAttrModel> grpAttrPredefAttrModelList;
  private SortedSet<IProjectAttribute> predefAttrList = new TreeSet<>();
  private final Map<IProjectAttribute, Long> grpAttrValMap;
  private final SortedSet<PredefinedAttrValue> predefAttrValList = new TreeSet<>();
  private final Map<Long, PidcVariant> variantMap;
  private final Map<Long, PidcSubVariant> subVariantMap;

  /**
   * @param grpAttrPredefAttrModelList
   * @param grpAttrValMap
   * @param predefAttrValList
   * @param subVariantMap
   * @param variantMap
   */
  public PIDCGrpdAttrChangesContentProvider(final List<GroupdAttrPredefAttrModel> grpAttrPredefAttrModelList,
      final Map<IProjectAttribute, Long> grpAttrValMap, final List<PredefinedAttrValue> predefAttrValList,
      final Map<Long, PidcVariant> variantMap, final Map<Long, PidcSubVariant> subVariantMap) {

    this.grpAttrPredefAttrModelList = grpAttrPredefAttrModelList;
    this.grpAttrValMap = grpAttrValMap;
    this.predefAttrValList.addAll(predefAttrValList);
    this.variantMap = variantMap;
    this.subVariantMap = subVariantMap;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // Not applicable
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // Not applicable
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang. Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    return getChildren(inputElement);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    if (parentElement instanceof Collection) {
      boolean attrValflag = false;

      for (Object obj : (Collection) parentElement) {
        if (obj instanceof AttributeValue) {
          attrValflag = true;
          break;
        }
      }
      if (attrValflag) {
        Collection<AttributeValue> details = (Collection<AttributeValue>) parentElement;

        List<AttributeValue> validAttrVal = new ArrayList<>();

        for (AttributeValue attrVal : details) {
          if (hasChildren(attrVal)) {
            validAttrVal.add(attrVal);
          }
        }
        return validAttrVal.toArray();
      }
      Collection<GroupdAttrPredefAttrModel> details = (Collection<GroupdAttrPredefAttrModel>) parentElement;

      List<IProjectAttribute> validAttrVal = new ArrayList<>();

      for (GroupdAttrPredefAttrModel model : details) {
        if (hasChildren(model.getGroupedAttribute())) {
          this.grpAttrValMap.put(model.getGroupedAttribute(), model.getGroupedAttribute().getValueId());
          validAttrVal.add(model.getGroupedAttribute());
        }
      }
      return validAttrVal.toArray();
    }
    else if (parentElement instanceof AttributeValue) {
      return this.predefAttrValList.toArray();
    }
    else if (parentElement instanceof IProjectAttribute) {

      return this.predefAttrList.toArray();
    }
    return new Object[0];
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    if (element instanceof AttributeValue) {
      return CommonUtils.isNotEmpty(this.predefAttrValList);
    }
    else if (element instanceof IProjectAttribute) {
      IProjectAttribute groupedAttr = (IProjectAttribute) element;
      SortedSet<IProjectAttribute> returnList = getChildItems(groupedAttr);
      return CommonUtils.isNotEmpty(returnList);

    }
    return false;
  }

  /**
   * @param groupedAttr
   * @return
   */
  private SortedSet<IProjectAttribute> getChildItems(final IProjectAttribute groupedAttr) {
    this.predefAttrList = new TreeSet<>();

    if (groupedAttr instanceof PidcVersionAttribute) {
      for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrModelList) {
        if (model.getGroupedAttribute() instanceof PidcVersionAttribute) {
          getPidcAttrInstance(groupedAttr, model);
        }
      }
    }
    else if ((groupedAttr instanceof PidcVariantAttribute) &&
        !this.variantMap.get(((PidcVariantAttribute) groupedAttr).getVariantId()).isDeleted()) {
      for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrModelList) {
        if (model.getGroupedAttribute() instanceof PidcVariantAttribute) {
          getPidcVarAttrInstance(groupedAttr, model);
        }
      }
    }
    else if ((groupedAttr instanceof PidcSubVariantAttribute) &&
        !this.variantMap.get(((PidcSubVariantAttribute) groupedAttr).getVariantId()).isDeleted() &&
        !this.subVariantMap.get(((PidcSubVariantAttribute) groupedAttr).getSubVariantId()).isDeleted()) {
      for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrModelList) {
        if (model.getGroupedAttribute() instanceof PidcSubVariantAttribute) {
          getPidcSubVarAttrInstance(groupedAttr, model);
        }
      }
    }
    return this.predefAttrList;
  }

  /**
   * @param groupedAttr
   * @param model
   */
  private void getPidcVarAttrInstance(final IProjectAttribute groupedAttr, final GroupdAttrPredefAttrModel model) {
    PidcVariantAttribute modelVarAttr = (PidcVariantAttribute) model.getGroupedAttribute();
    PidcVariantAttribute grpVarAttr = (PidcVariantAttribute) groupedAttr;
    if ((null != modelVarAttr) && modelVarAttr.getId().equals(grpVarAttr.getId())) {
      this.predefAttrList.addAll(model.getPredefAttrValMap().keySet());
    }
  }

  /**
   * @param groupedAttr
   * @param model
   */
  private void getPidcSubVarAttrInstance(final IProjectAttribute groupedAttr, final GroupdAttrPredefAttrModel model) {
    PidcSubVariantAttribute modelSubVarAttr = (PidcSubVariantAttribute) model.getGroupedAttribute();
    PidcSubVariantAttribute grpSubVarAttr = (PidcSubVariantAttribute) groupedAttr;
    if ((null != modelSubVarAttr) && modelSubVarAttr.getSubVariantId().equals(grpSubVarAttr.getSubVariantId()) &&
        modelSubVarAttr.getId().equals(grpSubVarAttr.getId())) {
      this.predefAttrList.addAll(model.getPredefAttrValMap().keySet());
    }
  }

  /**
   * @param groupedAttr
   * @param model
   */
  private void getPidcAttrInstance(final IProjectAttribute groupedAttr, final GroupdAttrPredefAttrModel model) {
    PidcVersionAttribute modelPidcAttr = (PidcVersionAttribute) model.getGroupedAttribute();
    PidcVersionAttribute grpPidcAttr = (PidcVersionAttribute) groupedAttr;
    if ((null != modelPidcAttr) && modelPidcAttr.getId().equals(grpPidcAttr.getId())) {
      this.predefAttrList.addAll(model.getPredefAttrValMap().keySet());
    }

  }
}
