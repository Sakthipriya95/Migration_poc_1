package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * Label Accumulator class for nattable
 *
 * @author PDH2COB
 */
public class PidcCoCWpNattableLabelAccumulator extends ColumnOverrideLabelAccumulator {

  private final ILayer layer;
  private final IRowDataProvider<IProjectCoCWP> bodyDataProvider;

  /**
   * @param customFilterGridLayer custom filter grid layer for coc natable
   */
  public PidcCoCWpNattableLabelAccumulator(final CustomFilterGridLayer<IProjectCoCWP> customFilterGridLayer) {
    super(customFilterGridLayer.getBodyDataLayer());
    this.layer = customFilterGridLayer.getBodyDataLayer();
    this.bodyDataProvider = customFilterGridLayer.getBodyDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    IProjectCoCWP projCocWp = this.bodyDataProvider.getRowObject(rowPosition);

    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }

    if (projCocWp.isDeleted()) {
      configLabels.addLabel(ApicUiConstants.DELETED_COC_WP_LABEL);
    }

  }
}
