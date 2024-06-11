package com.bosch.caltool.icdm.common.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.views.SeriesStatisticsViewPart;
import com.bosch.caltool.icdm.common.ui.views.data.SeriesStatCache;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author dmo5cob
 */
public class SeriesStatisticsCacheHandler extends Action {

  /**
   * HANDLER_ID
   */
  private static final String HANDLER_ID = "com.bosch.caltool.icdm.common.ui.actions.SeriesStatisticsCacheHandler";

  /**
   * Label name
   */
  private final String labelName;

  /**
   * LabelInfoVO instance
   */
  private final LabelInfoVO labelInfoVO;

  // iCDM-1198
  /**
   * defines whether to clear the cache
   */
  private boolean clearCache;


  /**
   * @param labelName parameter name
   * @param obj LabelInfoVO
   */
  public SeriesStatisticsCacheHandler(final String labelName, final LabelInfoVO obj) {
    setId(HANDLER_ID);
    this.labelName = labelName;
    this.labelInfoVO = obj;

  }

  /**
   * iCDM-1198 <br>
   * Constructor to clear the cache
   *
   * @param clearCache trur to clear cache
   */
  public SeriesStatisticsCacheHandler(final boolean clearCache) {
    this.clearCache = clearCache;
    this.labelName = "";
    this.labelInfoVO = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    CDMCommonActionSet commActionSet = new CDMCommonActionSet();
    try {
      SeriesStatisticsViewPart seriesStatViewPart =
          (SeriesStatisticsViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .showView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.SERIES_STATISTICS_VIEW_ID);
      // iCDM-1198
      if (this.clearCache) {
        seriesStatViewPart.resetUIControls();
        SeriesStatCache.getInstance().getMapOfSeriesStatistics().clear();
        seriesStatViewPart.clearTableGraph();
      }
      else {
        if (null != this.labelInfoVO) {
          seriesStatViewPart.setLabelInfoVO(this.labelInfoVO);
        }
        commActionSet.updateSeriesStatisticsViewUI(seriesStatViewPart, this.labelName, true);
      }
    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }
}