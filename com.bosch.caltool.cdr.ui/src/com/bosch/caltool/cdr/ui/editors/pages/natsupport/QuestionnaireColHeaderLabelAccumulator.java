package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.QuestionDetailsPage;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;

/**
 * @author dmo5cob
 */
public final class QuestionnaireColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * QuestionDetailsPage instance
   */
  private final QuestionDetailsPage page;

  /**
   * @param layer ILayer
   * @param page QuestionDetailsPage
   */
  public QuestionnaireColHeaderLabelAccumulator(final ILayer layer, final QuestionDetailsPage page) {
    super(layer);
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    IRowDataProvider<Question> dataProvider =
        (IRowDataProvider<Question>) this.page.getCustomFilterGridLayer().getBodyDataLayer().getDataProvider();
    Question rowObject = dataProvider.getRowObject(rowPosition);

    // question header
    if (rowPosition == (this.page.getCustomFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) {
      configLabels.addLabel("COL_HEADER_LABEL");
    }

    // condition to apply the configuration only to column 6 for deleted attribute value
    if ((rowObject != null) && rowObject.getDeletedFlag()) {
      configLabels.addLabel("DELVALUE");
    }
  }
}