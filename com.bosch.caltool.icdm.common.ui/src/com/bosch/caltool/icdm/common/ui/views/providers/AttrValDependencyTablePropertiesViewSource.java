//package com.bosch.caltool.icdm.common.ui.views.providers;
//
//import org.eclipse.ui.views.properties.IPropertyDescriptor;
//import org.eclipse.ui.views.properties.IPropertySource;
//
//import com.bosch.caltool.icdm.common.ui.utils.ApicUtil;
//
///**
// * @author rvu1cob
// */
//public class AttrValDependencyTablePropertiesViewSource implements IPropertySource {
//
//
//  /**
//   * AttrValueDependency
//   */
////  private final AttrValueDependency attrValDependency;
//
//  /**
//   * property fields
//   */
//  private final String[] propDescFields = new String[] {
//      "Dependency Attribute",
//      "Dependency AttributeValue",
//      "Modifiable",
//      "Deleted",
//      "Created User",
//      "Created Date",
//      "Modified User",
//      "Modified Date",
//      "Change Comment" };
//
//  /**
//   * Constructor
//   *
//   * @param attrValDependency AttrValueDependency
//   */
//
//  public AttrValDependencyTablePropertiesViewSource(final AttrValueDependency attrValDependency) {
//    this.attrValDependency = attrValDependency;
//  }
//
//  @Override
//  public Object getEditableValue() {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  /**
//   * {@inheritDoc} return property descriptors
//   */
//  @Override
//  public IPropertyDescriptor[] getPropertyDescriptors() {
//    return ApicUtil.createPropertyDescFields(this.propDescFields);
//  }
//
//  /**
//   * {@inheritDoc} return property value
//   */
//  @Override
//  public Object getPropertyValue(final Object descField) {
//    String result = "";
//    if ("Dependency Attribute".equals(descField)) {
//      // for Dependency Attribute
//      result = this.attrValDependency.getDependencyAttribute().getAttributeName();
//    }
//    else if ("Dependency AttributeValue".equals(descField) && (this.attrValDependency.getDependencyValue() != null)) {
//      // for Dependency AttributeValue
//      result = this.attrValDependency.getDependencyValue().getValue();
//    }
//    else if ("Modifiable".equals(descField)) {
//      // for Modifiable field
//      result = String.valueOf(this.attrValDependency.isModifiable());
//    }
//    else if ("Deleted".equals(descField)) {
//      // for deleted flag
//      result = String.valueOf(this.attrValDependency.isDeleted());
//    }
//    // ICDM-136
//    else if ("Created User".equals(descField)) {
//      // for created user
//      result = this.attrValDependency.getCreatedUser();
//    }
//    else if ("Created Date".equals(descField)) {
//      // for created date
//      result = this.attrValDependency.getCreatedDate().getTime().toString();
//    }
//    else if ("Modified User".equals(descField)) {
//      // for modified user
//      result = this.attrValDependency.getModifiedUser();
//    }
//    else if ("Modified Date".equals(descField) && (this.attrValDependency.getModifiedDate() != null)) {
//      // for modified date
//      result = this.attrValDependency.getModifiedDate().getTime().toString();
//    }
//    else if ("Title".equals(descField)) {
//      // for title
//      result = "VALUE DEP : " + this.attrValDependency.getDependencyAttribute().getAttributeName();
//    }
//    // ICDM-1397
//    else if ("Change Comment".equals(descField)) {
//      // for change comment
//      result = this.attrValDependency.getChangeComment().replace("\n", " ");
//    }
//    return result;
//  }
//
//  @Override
//  public boolean isPropertySet(final Object descField) {
//    // TODO Auto-generated method stub
//    return false;
//  }
//
//  @Override
//  public void resetPropertyValue(final Object descField) {
//    // TODO Auto-generated method stub
//
//  }
//
//  @Override
//  public void setPropertyValue(final Object descField, final Object value) {
//    // TODO Auto-generated method stub
//
//  }
//
//}
