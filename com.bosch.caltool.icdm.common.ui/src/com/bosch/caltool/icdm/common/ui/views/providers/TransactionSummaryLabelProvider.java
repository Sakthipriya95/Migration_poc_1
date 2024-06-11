package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.cns.CnsChangeEventSummary;
import com.bosch.caltool.icdm.common.ui.views.TransactionSummaryViewPart;

/**
 * @author dmo5cob
 */
public class TransactionSummaryLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {
    return null;
  }

  @Override
  public String getToolTipText(final Object element) {
    return ((CnsChangeEventSummary) element).getSummaryToolTip();
  }


  /**
   * @param element Object
   * @return String
   */
  public String getSummaryToolTipText(final Object element) {
    return ((CnsChangeEventSummary) element).getSummaryToolTip();
  }

  @Override
  public String getColumnText(final Object element, final int columnIndex) {

    CnsChangeEventSummary evt = (CnsChangeEventSummary) element;

    switch (columnIndex) {
      case TransactionSummaryViewPart.COLUMN_EVENT_ID:
        return evt.getEventID().toString();

      case TransactionSummaryViewPart.COLUMN_SERVICE_ID:
        return evt.getServiceID();

      case TransactionSummaryViewPart.COLUMN_CREATED_DATE:
        return evt.getCreatedDate();

      case TransactionSummaryViewPart.COLUMN_DATA_SIZE:
        return Integer.toString(evt.getSize());

      case TransactionSummaryViewPart.COLUMN_CHANGE_COUNT:
        return Integer.toString(evt.getChangeCount());

      case TransactionSummaryViewPart.COLUMN_EVENT_SUMMARY:
        return evt.getSummaryToolTip();
      default:
        return null;
    }
  }


}