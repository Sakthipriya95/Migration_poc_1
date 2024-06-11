package com.bosch.caltool.icdm.ui.views.providers;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;

/**
 * @author rvu1cob
 */
// ICDM-57
public class PropertyViewAdapterFactory implements IAdapterFactory { // NOPMD by adn1cob on 4/3/14 9:20 AM


  @Override
  public final Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) { // NOPMD
                                                                                                                        // by
                                                                                                                        // adn1cob
                                                                                                                        // on
                                                                                                                        // 4/3/14
                                                                                                                        // 9:19
                                                                                                                        // AM
    // Icdm-633 Use A2LParameter Object
    if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2LParameter)) {
      return new ParameterPropertiesViewSource((A2LParameter) adaptableObject);
    }

    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof Function)) {
      return new FCFormPagePropertiesViewSource((Function) adaptableObject);
    }

    // ICDM-81
    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2LSystemConstantValues)) {
      return new SysConstFormPagePropertiesViewSource((A2LSystemConstantValues) adaptableObject);
    }
    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2LBaseComponents)) {
      return new BCFormPagePropertiesViewSource((A2LBaseComponents) adaptableObject);
    }

    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2LBaseComponentFunctions)) {
      return new BCFCFormPagePropertiesViewSource((A2LBaseComponentFunctions) adaptableObject);
    }

    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2lWpObj)) {
      return new WorkPackageFormPagePropertiesViewSource((A2lWpObj) adaptableObject);
    }
    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2lWorkPackageGroup)) {
      return new WorkPackageGroupFormPagePropertiesViewSource((A2lWorkPackageGroup) adaptableObject);
    }

    else if ((adapterType == IPropertySource.class) && (adaptableObject instanceof A2LGroup)) {
      return new A2LGroupFormPagePropertiesViewSource((A2LGroup) adaptableObject);
    }
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public final Class[] getAdapterList() {

    return new Class[] { IPropertySource.class };
  }


}
