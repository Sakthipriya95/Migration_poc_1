package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;

// ICDM-293
/**
 * @author mkl2cob
 */
public class UseCaseItemPropertiesViewAdapterFactory implements IAdapterFactory {

  @Override
  public Object getAdapter(final Object adaptableObject, final Class adapterType) {
    if (adapterType != IPropertySource.class) {
      return null;
    }
    if ((adaptableObject instanceof IUseCaseItemClientBO)) {
      return new UseCaseItemPropertiesViewSource((IUseCaseItemClientBO) adaptableObject);
    }

    else if (adaptableObject instanceof FavUseCaseItemNode) {
      FavUseCaseItemNode favNode = (FavUseCaseItemNode) adaptableObject;
      return new UseCaseItemPropertiesViewSource(favNode.getUseCaseItem());
    }
    return null;
  }

  @Override
  public Class[] getAdapterList() {
    return new Class[] { IPropertySource.class };
  }

}
