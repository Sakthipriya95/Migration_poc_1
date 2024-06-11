package com.bosch.caltool.icdm.common.ui.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Label Provider Class
 *
 * @author pdh2cob
 */
public class BoschUserGridTableLabelProvider implements ITableLabelProvider {

  private final A2lResponsibilityModel a2lResponsibilityModel;


  /**
   * @param a2lResponsibilityModel - resp model
   */
  public BoschUserGridTableLabelProvider(final A2lResponsibilityModel a2lResponsibilityModel) {
    this.a2lResponsibilityModel = a2lResponsibilityModel;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener listener) {

    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {

    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener listener) {

    // NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String value = "";
    if (element instanceof A2lRespBoschGroupUser) {
      A2lRespBoschGroupUser a2lRespBoschGroupUser = (A2lRespBoschGroupUser) element;
      User user = this.a2lResponsibilityModel.getUserMap().get(a2lRespBoschGroupUser.getUserId());
      switch (columnIndex) {
        case 0:
          value = user.getFirstName() + "," + user.getLastName();
          break;
        case 1:
          value = user.getName();
          break;
        case 2:
          value = user.getDepartment();
          break;
        default:
          break;
      }
    }
    return value;
  }

}
