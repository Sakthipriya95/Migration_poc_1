/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.views.providers;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;

// ICDM-528
/**
 * Adapter factory to display properties of Comp package specific BOs
 *
 * @author mkl2cob
 */
public class CompPropertyViewAdapterFactory implements IAdapterFactory {

  /**
   * Get the adapter for component package/CP BC or CP FCs
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {

    Object adapter = null;

    // Icdm-1025 Use the Equals instead of ==
    if (adapterType.equals(IPropertySource.class)) {
      // Component packages
      if (adaptableObject instanceof CompPackage) {
        adapter = new CompPkgPropertiesViewSource((CompPackage) adaptableObject);
      }
      // Component package functions
      else if (adaptableObject instanceof CompPkgBc) {
        adapter = new CompPkgBCPropertiesViewSource((CompPkgBc) adaptableObject);
      }
      // Component package functions
      else if (adaptableObject instanceof CompPkgFc) {
        adapter = new CompPkgBCFCPropertiesViewSource((CompPkgFc) adaptableObject);
      }
    }

    return adapter;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public final Class[] getAdapterList() {
    return new Class[] { IPropertySource.class };
  }

}
