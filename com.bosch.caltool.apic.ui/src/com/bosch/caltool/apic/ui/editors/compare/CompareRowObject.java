/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.Map;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;


/**
 * @author jvi6cob
 */
public class CompareRowObject implements Comparable<CompareRowObject> {

  /**
   * Attribute instance
   */
  private Attribute attribute;

  /**
   * column data mapper instance
   */
  private final ColumnDataMapper columnDataMapper;

  /**
   * To be used only for sorting or filtering to improve performance
   */
  private Boolean diff = Boolean.FALSE;

  /**
   * project attribute util instance
   */
  private final ProjectAttributeUtil compareEditorUtil = new ProjectAttributeUtil();

  /**
   * @param projectHandlerMap
   */
  public CompareRowObject(final Map<Long, AbstractProjectObjectBO> projectHandlerMap) {
    this.columnDataMapper = new ColumnDataMapper(projectHandlerMap);
  }

  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return this.attribute;
  }


  /**
   * @param attribute the attribute to set
   */
  public void setAttribute(final Attribute attribute) {
    this.attribute = attribute;
  }


  /**
   * @param pidcAttribute to remove
   */
  public void addProjectAttribute(final IProjectAttribute pidcAttribute) {
    this.columnDataMapper.addNewColumnIndex(pidcAttribute);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.attribute.hashCode();
  }

  /**
   * {@inheritDoc} returns true if ID's of the attributes are same or both null
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
    CompareRowObject compareObj = (CompareRowObject) obj;
    return this.attribute.equals(compareObj.getAttribute());
  }


  /**
   * @return the columnDataMapper
   */
  public ColumnDataMapper getColumnDataMapper() {
    return this.columnDataMapper;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompareRowObject compareRowObj) {
    // comaparator
    return ApicUtil.compare(getAttribute().getName(), compareRowObj.getAttribute().getName());
  }


  /**
   * Returns true is used flags/attr values are different
   *
   * @return true if diff
   */
  public Object getComputedIsDiff() {
    // check flag values
    int index2;
    this.diff = Boolean.FALSE;
    outer: for (int index1 = 0; index1 < this.columnDataMapper.getIpidcAttrs().size(); index1++) {
      for (index2 = index1 + 1; index2 < this.columnDataMapper.getIpidcAttrs().size(); index2++) {
        if (isUsedFlagDiff(index1, index2)) {
          this.diff = Boolean.TRUE;
          break outer;
        }
        if (isValueDiff(index1, index2)) {
          this.diff = Boolean.TRUE;
          break outer;
        }
      }
    }
    return this.diff;
  }


  /**
   * Checks whether values of attrs are diff
   *
   * @param index1 attr1 position
   * @param index2 attr2 position
   * @return
   */
  private boolean isValueDiff(final int index1, final int index2) {
    String attrVal1 = null;
    String attrVal2 = null;
    // pidc attribute1
    IProjectAttribute ipidcAttribute1 = (IProjectAttribute) (this.columnDataMapper.getIpidcAttrs().toArray())[index1];
    // pidc attribute2
    IProjectAttribute ipidcAttribute2 = (IProjectAttribute) (this.columnDataMapper.getIpidcAttrs().toArray())[index2];

    // compare the attribute display names
    if ((ipidcAttribute1 != null) && (ipidcAttribute2 != null)) {
      if (null != this.compareEditorUtil
          .getProjectAttributeHandler(ipidcAttribute1,
              this.columnDataMapper.getProjectHandlerMap().get(this.compareEditorUtil.getID(ipidcAttribute1)))
          .getDefaultValueDisplayName(true)) {
        attrVal1 = this.compareEditorUtil
            .getProjectAttributeHandler(ipidcAttribute1,
                this.columnDataMapper.getProjectHandlerMap().get(this.compareEditorUtil.getID(ipidcAttribute1)))
            .getDefaultValueDisplayName(true);
      }

      if (null != this.compareEditorUtil
          .getProjectAttributeHandler(ipidcAttribute2,
              this.columnDataMapper.getProjectHandlerMap().get(this.compareEditorUtil.getID(ipidcAttribute2)))
          .getDefaultValueDisplayName(true)) {
        attrVal2 = this.compareEditorUtil
            .getProjectAttributeHandler(ipidcAttribute2,
                this.columnDataMapper.getProjectHandlerMap().get(this.compareEditorUtil.getID(ipidcAttribute2)))
            .getDefaultValueDisplayName(true);
      }
//      compare attribute names
      if (!CommonUtils.isEqualIgnoreCase(attrVal1, attrVal2)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Checks whether used flag of attrs are diff
   *
   * @param index1 attr1 position
   * @param index2 attr3 position
   * @return
   */
  private boolean isUsedFlagDiff(final int index1, final int index2) {
    if ((this.columnDataMapper.getIpidcAttrs().toArray()[index1] != null) &&
        (this.columnDataMapper.getIpidcAttrs().toArray()[index2] != null)) {
      final String attr1UsedFlg =
          ((IProjectAttribute) (this.columnDataMapper.getIpidcAttrs().toArray())[index1]).getUsedFlag();
      final String attr2UsedFlg =
          ((IProjectAttribute) (this.columnDataMapper.getIpidcAttrs().toArray())[index2]).getUsedFlag();
      if (!CommonUtils.checkNull(attr1UsedFlg).equals(attr2UsedFlg)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the diff
   */
  public Boolean getDiff() {
    return this.diff;
  }


  /**
   * @param diff the diff to set
   */
  public void setDiff(final Boolean diff) {
    this.diff = diff;
  }


}
