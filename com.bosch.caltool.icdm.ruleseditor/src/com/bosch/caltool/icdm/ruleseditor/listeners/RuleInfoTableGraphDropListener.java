/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParamInfo;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;


/**
 * Drop target adapter for table graph viewer in Rule info section. Sets the caldata object from the source to the the
 * viewer.
 *
 * @author bne4cob
 */
public final class RuleInfoTableGraphDropListener extends DropTargetAdapter {

  /**
   * ruleInfoSection
   */
  private final RuleInfoSection ruleInfoSection;

  /**
   * @param ruleInfoSection RuleInfoSection
   */
  public RuleInfoTableGraphDropListener(final RuleInfoSection ruleInfoSection) {
    super();
    this.ruleInfoSection = ruleInfoSection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragEnter(final DropTargetEvent dropTargetEvent) {
    if (dropTargetEvent.detail == DND.DROP_DEFAULT) {
      dropTargetEvent.detail = DND.DROP_COPY;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragOperationChanged(final DropTargetEvent dropTargetEvent) {
    if (dropTargetEvent.detail == DND.DROP_DEFAULT) {
      dropTargetEvent.detail = DND.DROP_COPY;
    }
  }

  /**
   * Get the caldata object from the dropped item, and add to the table graph composite, as a new graph item. Caldata
   * values of the same paramter type can be added
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void drop(final DropTargetEvent dropTargetEvent) {
    final IStructuredSelection structuredSelection = (StructuredSelection) dropTargetEvent.data;
    final Object firstElement = structuredSelection.getFirstElement();

    CalData calData = null;
    String droppedParamType = null;
    String labelPrefix = null;

    // Drop From SeriesStatisticsView
    if (firstElement instanceof SeriesStatisticsInfo) {
      SeriesStatisticsInfo seriesStatisticsInfo = (SeriesStatisticsInfo) firstElement;
      calData = seriesStatisticsInfo.getCalData();
      droppedParamType = calData.getCalDataPhy().getType();
      labelPrefix = seriesStatisticsInfo.getCalDataPhyValType().getLabel();

    }
    // Drop From ParametersPage in A2L Editor
    // TODO: check whether this is valid scenario
    else if (firstElement instanceof com.bosch.caltool.icdm.client.bo.a2l.A2LParamInfo) {
      A2LParamInfo a2lParamInfo = (A2LParamInfo) firstElement;
      calData = a2lParamInfo.getA2lParam().getCalData();
      droppedParamType = calData.getCalDataPhy().getType();
      labelPrefix = "Value";
    }
    // Drop From ScratchPad
    else if (firstElement instanceof ScratchPadDataFetcher) {
      ScratchPadDataFetcher scratchPadDataProvider = (ScratchPadDataFetcher) firstElement;
      SeriesStatisticsInfo seriesStatisticsInfo = scratchPadDataProvider.getSeriesStatsInfo();
      calData = seriesStatisticsInfo.getCalData();
      droppedParamType = calData.getCalDataPhy().getType();
      labelPrefix = seriesStatisticsInfo.getCalDataPhyValType().getLabel();
    }

    // Add the item to the table graph, if the parameter types are the same
    // Else show an error message
    if (this.ruleInfoSection.getSelectedParam().getType().equals(droppedParamType)) {
      addToTableGraphComponent(calData, labelPrefix);
    }
    else {
      CDMLogger.getInstance().errorDialog(RuleInfoSection.SRC_DEST_PAR_TYP_DIFF, Activator.PLUGIN_ID);
    }

  }

  /**
   * Add the caldata object to table graph composite.<br>
   * Add the new component, with different graph color and label.
   *
   * @param calData calData retrieved
   * @param labelPrefix label prefix
   */
  private void addToTableGraphComponent(final CalData calData, final String labelPrefix) {
    if (calData != null) {
      CalDataAttributes calAttr = new CalDataAttributes(calData, this.ruleInfoSection.getGraphColor());
      this.ruleInfoSection.setGraphColor(this.ruleInfoSection.getGraphColor() + 1);
      calAttr.setShowDifferenceIndicator(true);
      calAttr.setLabelPrefix(" (" + labelPrefix + ") ");
      this.ruleInfoSection.getCalDataComparison().addCalDataAttr(calAttr);
      try {
        this.ruleInfoSection.getCalDataTableGraphComposite()
            .fillTableAndGraph(this.ruleInfoSection.getCalDataComparison());
      }
      catch (CalDataTableGraphException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

}
