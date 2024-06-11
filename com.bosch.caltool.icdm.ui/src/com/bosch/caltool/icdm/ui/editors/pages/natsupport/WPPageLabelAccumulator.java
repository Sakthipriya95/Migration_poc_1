package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class provides labels for body datalayer of the GridLayer of the NatTable
 * 
 * @deprecated not used
 */
@Deprecated
public class WPPageLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * NAT table Data Layer
   */
  private final ILayer layer;
  private final PidcA2LBO pidcA2lBO;

  /**
   * Constructor
   *
   * @param layer data layer
   * @param pidcA2LBO
   */
  public WPPageLabelAccumulator(final ILayer layer, final PidcA2LBO pidcA2LBO) {
    super(layer);
    this.layer = layer;
    this.pidcA2lBO = pidcA2LBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the row object out of the dataprovider
    @SuppressWarnings("unchecked")
    IRowDataProvider<A2LWpRespExt> bodyDataProvider =
        (IRowDataProvider<A2LWpRespExt>) ((DataLayer) this.layer).getDataProvider();
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    else if (rowObject instanceof A2LWpRespExt) {

      // ICDM-2439
      if (columnPosition == CommonUIConstants.COLUMN_INDEX_3) {
        // 231957 - Npe check added for user rights null
        try {
          if (new CurrentUserBO().hasNodeOwnerAccess(this.pidcA2lBO.getPidcVersion().getPidcId())) {
            configLabels.addLabel("COMBO_RESP");
          }
          else {
            configLabels.addLabel("COMBO_RESP_READ_ONLY");
          }
        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.ui.Activator.PLUGIN_ID);
        }
      }

    }
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

  }


}
