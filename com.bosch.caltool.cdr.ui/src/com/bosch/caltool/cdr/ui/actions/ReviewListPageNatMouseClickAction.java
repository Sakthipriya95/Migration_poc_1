/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewListPage;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * This class provides the double click action for PIDC review results NAT table
 *
 * @author mkl2cob
 */
public class ReviewListPageNatMouseClickAction implements IMouseClickAction {

  /**
   * PIDCRvwResultsDtlsPage instance
   */
  private final ReviewListPage pidcRvwResultsDtlsPage;

  /**
   * @param pidcRvwResultsDtlsPage PIDCRvwResultsDtlsPage
   */
  public ReviewListPageNatMouseClickAction(final ReviewListPage pidcRvwResultsDtlsPage) {
    this.pidcRvwResultsDtlsPage = pidcRvwResultsDtlsPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable arg0, final MouseEvent arg1) {
    // get the composite layer
    CompositeLayer compositeLayer = (CompositeLayer) arg0.getLayer();
    // get the CustomFilterGridLayer for the child layer "Grid"
    CustomFilterGridLayer customFilterGridLayer =
        (CustomFilterGridLayer) compositeLayer.getChildLayerByRegionName("Grid");
    // get the selection layer
    final SelectionLayer selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
    // row position
    int rowPosition = LayerUtil.convertRowPosition(arg0, arg0.getRowPositionByY(arg1.y), selectionLayer);
    // get the row object
    ReviewVariantModel cdrRvwVar = (ReviewVariantModel) this.pidcRvwResultsDtlsPage.getDataRprtFilterGridLayer()
        .getBodyDataProvider().getRowObject(rowPosition);
    ReviewResultData cdrRes = cdrRvwVar.getReviewResultData();

    if (cdrRes.getCdrReviewResult().getRvwStatus().equalsIgnoreCase(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
      // if review status is open, then open the wizard
      CdrActionSet pidcActionSet = new CdrActionSet();
      pidcActionSet.openDeltaOrCancelledReview(cdrRes.getCdrReviewResult(), new CDRHandler(), null);
    }
    else {
      // otherwise open the results editor
      try {
        CdrActionSet actionSet = new CdrActionSet();
        actionSet.openRvwRestEditorBasedOnObjInstance(cdrRvwVar);
      }
      catch (Exception e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExclusive() {
    // TODO Auto-generated method stub
    return false;
  }

}
