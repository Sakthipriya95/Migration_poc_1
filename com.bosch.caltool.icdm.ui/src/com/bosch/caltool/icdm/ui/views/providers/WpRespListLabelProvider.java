/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefinitionModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ui.dialogs.ShowWpRespListDialog;

/**
 * @author elm1cob
 */
public class WpRespListLabelProvider implements ITableLabelProvider {

  A2lWpDefinitionModel defModel;
  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * @param wpRespListDialog ShowWpRespListDialog
   */
  public WpRespListLabelProvider(final ShowWpRespListDialog wpRespListDialog) {
    this.defModel = wpRespListDialog.getA2lWpDefinitionModel();
    this.a2lWpInfoBo = wpRespListDialog.getA2lWpInfoBo();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object arg0, final String arg1) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object arg0, final int arg1) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) {
    String result = "";
    if (element instanceof A2lWpResponsibility) {
      A2lWpResponsibility wpData = (A2lWpResponsibility) element;
      switch (columnIndex) {
        case CommonUIConstants.COLUMN_INDEX_0:
          result = (wpData.getName() != null) ? wpData.getName() : "";
          break;
        case CommonUIConstants.COLUMN_INDEX_1:
          result = getA2lRespName(wpData);
          break;
        case CommonUIConstants.COLUMN_INDEX_2:
          result = wpData.getA2lRespId() != null ? this.a2lWpInfoBo.getRespTypeName(wpData) : "";
          break;
        case CommonUIConstants.COLUMN_INDEX_3:
          result = getVarGrpName(wpData);
          break;
        case CommonUIConstants.COLUMN_INDEX_4:
          result = wpData.getWpNameCust() != null ? wpData.getWpNameCust() : "";
          break;
        default:
          result = "";
          break;
      }
    }
    return result;
  }

  /**
   * @param result
   * @param wpData
   * @return
   */
  private String getVarGrpName(final A2lWpResponsibility wpData) {
    String result = ApicConstants.EMPTY_STRING;
    Map<Long, A2lVariantGroup> a2lVariantGrpMap = this.a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap();
    if ((wpData.getVariantGrpId() != null) && (a2lVariantGrpMap != null) &&
        (a2lVariantGrpMap.get(wpData.getVariantGrpId()) != null)) {
      result = a2lVariantGrpMap.get(wpData.getVariantGrpId()).getName();
    }
    return result;
  }

  /**
   * @param result
   * @param wpData
   * @return
   */
  private String getA2lRespName(final A2lWpResponsibility wpData) {
    String result = ApicConstants.EMPTY_STRING;
    Map<Long, A2lResponsibility> a2lResponsibilityMap =
        this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap();
    if (a2lResponsibilityMap != null) {
      result = (wpData.getA2lRespId() != null) ? a2lResponsibilityMap.get(wpData.getA2lRespId()).getName() : "";
    }
    return result;
  }


}
