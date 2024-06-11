package com.bosch.caltool.apic.ui.views.providers;

import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributesDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Property source class for Attribute and value dependency in Attributes editor
 *
 * @author pdh2cob
 */
public class AttrDependencyTablePropertiesViewSource extends AbstractDataObjectPropertySource<AttrNValueDependency> {

  private final AttrNValueDependency attrDependency;

  private AttributeValue dependentAttrValue;

  private final AttributesDataHandler attributesDataHandler;

  private final AttrNValueDependencyClientBO attrNValueDependencyClientBO;

  private static final String DEP_ATTR = "Dependency Attribute";

  private static final String DEP_ATTR_VALUE = "Dependency AttributeValue";

  private static final String MODIFIABLE = "Modifiable";

  private static final String DELETED = "Deleted";

  private static final String CHANGE_CMNT = "Change Comment";

  private static final String ATTRIB = "Attribute";


  /**
   * @param attrDependency - seleted attrnValueDependency
   * @param attributesDataHandler - instance of attributes data handler for attributes editor
   */
  public AttrDependencyTablePropertiesViewSource(final AttrNValueDependency attrDependency,
      final AttributesDataHandler attributesDataHandler) {
    super(attrDependency);
    this.attrDependency = attrDependency;
    this.attributesDataHandler = attributesDataHandler;
    this.attrNValueDependencyClientBO = new AttrNValueDependencyClientBO(attrDependency);
  }


  /**
   * @param descField
   * @return proeperty value
   */
  private String getPropertyValue(final String descField) {
    String result = "";
    boolean isValueDep = false;
    if (this.attrDependency.getAttributeId() == null) {
      isValueDep = true;
    }


    if (ATTRIB.equals(descField) && !isValueDep) {
      result = this.attributesDataHandler.getAttrsMap().get(this.attrDependency.getAttributeId()).getName();
    }
    else if (DEP_ATTR.equals(descField)) {
      result = this.attributesDataHandler.getAttrsMap().get(this.attrDependency.getDependentAttrId()).getName();
    }

    else if (DEP_ATTR_VALUE.equals(descField) && (this.attrDependency.getDependentValueId() != null)) {
      result = getAttrValue().getName();

    }
    else if (MODIFIABLE.equals(descField)) {
      try {
        result = CommonUtils.getDisplayText(new CurrentUserBO().hasApicWriteAccess());
      }
      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
      }
    }
    else if (DELETED.equals(descField)) {
      result = CommonUtils.getDisplayText(this.attrDependency.isDeleted());
    }


    else if (CHANGE_CMNT.equals(descField) && (null != this.attrDependency.getChangeComment())) {
      result = this.attrDependency.getChangeComment().replace("\n", " ");

    }
    else if (PROP_TITLE.equals(descField)) {
      result = getTitle();
    }

    return result;
  }


  private AttributeValue getAttrValue() {

    return this.dependentAttrValue == null ? this.attrNValueDependencyClientBO.getDependencyValue()
        : this.dependentAttrValue;


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String result = getPropertyValue(propKey);
    return result == null ? "" : result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    String result = "";
    if (this.attrDependency.getAttributeId() != null) {
      result = "ATTR DEP : " +
          this.attributesDataHandler.getAttrsMap().get(this.attrDependency.getDependentAttrId()).getName();

    }
    else {
      String attrValue = getAttrValue().getName();

      result = "VALUE DEP : " + attrValue;

    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    return new String[] { ATTRIB, DEP_ATTR, DEP_ATTR_VALUE, MODIFIABLE, DELETED, CHANGE_CMNT };

  }

}
