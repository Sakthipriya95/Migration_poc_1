/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;


/**
 * @author jvi6cob
 */
public class PidcNattableRowObject implements Comparable<PidcNattableRowObject> {

  // Attribute instance
  private Attribute attribute;
  // pidc column data mapper
  private final PidcColumnDataMapper columnDataMapper;
  // project attribute handler
  private AbstractProjectAttributeBO projectAttributeHandler;


  /**
   * @param projectAttrHandler
   * @param pidcVersion
   * @param pidcDataModel
   */
  public PidcNattableRowObject(final AbstractProjectAttributeBO projectAttrHandler,
      final PidcDataHandler pidcDataModel) {
    this.columnDataMapper = new PidcColumnDataMapper(projectAttrHandler, pidcDataModel);
    initialize(projectAttrHandler);
  }


  /**
   * @param projectAttrHandler
   * @param pidcVersion
   * @param pidcDataModel
   */
  private void initialize(final AbstractProjectAttributeBO projectAttrHandler) {
    if (projectAttrHandler instanceof PidcVersionAttributeBO) {
      this.projectAttributeHandler =
          new PidcVersionAttributeBO(((PidcVersionAttributeBO) projectAttrHandler).getPidcVersAttr(),
              (PidcVersionBO) projectAttrHandler.getProjectObjectBO());
    }
    else if (projectAttrHandler instanceof PidcVariantAttributeBO) {
      this.projectAttributeHandler =
          new PidcVariantAttributeBO(((PidcVariantAttributeBO) projectAttrHandler).getProjectVarAttr(),
              (PidcVariantBO) projectAttrHandler.getProjectObjectBO());
    }
    else if (projectAttrHandler instanceof PidcSubVariantAttributeBO) {
      this.projectAttributeHandler =
          new PidcSubVariantAttributeBO(((PidcSubVariantAttributeBO) projectAttrHandler).getProjectSubVarAttr(),
              (PidcSubVariantBO) projectAttrHandler.getProjectObjectBO());
    }
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
   * @return Whether addition succeeded
   */
  public void addProjectAttribute(final IProjectAttribute pidcAttribute) {
    this.columnDataMapper.addNewColumnIndex(pidcAttribute);
  }

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
    PidcNattableRowObject compareObj = (PidcNattableRowObject) obj;
    return this.attribute.equals(compareObj.getAttribute());
  }


  /**
   * @return the columnDataMapper
   */
  public PidcColumnDataMapper getColumnDataMapper() {
    return this.columnDataMapper;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcNattableRowObject compareRowObj) {
    // comaparator
    return ApicUtil.compare(getAttribute().getName(), compareRowObj.getAttribute().getName());
  }


  /**
   * @return the projectAttributeHandler
   */
  public AbstractProjectAttributeBO getProjectAttributeHandler() {
    return this.projectAttributeHandler;
  }


}
