/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.PlatformUI;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;

/**
 * Drop target adapter for reference value field in Rule info section. Sets the caldata object from the source to the
 * reference value. Also updates other fields in the rule, based on the caldata source.
 *
 * @author bne4cob
 */
public final class RuleInfoRefValueDropListener<D extends IParameterAttribute, P extends IParameter>
    extends DropTargetAdapter {

  /**
   * ruleInfoSection
   */
  private final RuleInfoSection<D, P> ruleInfoSection;

  /**
   * @param ruleInfoSection RuleInfoSection
   */
  public RuleInfoRefValueDropListener(final RuleInfoSection<D, P> ruleInfoSection) {
    super();
    this.ruleInfoSection = ruleInfoSection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragEnter(final DropTargetEvent event) {
    if (event.detail == DND.DROP_DEFAULT) {
      event.detail = DND.DROP_COPY;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dragOperationChanged(final DropTargetEvent event) {
    if (event.detail == DND.DROP_DEFAULT) {
      event.detail = DND.DROP_COPY;
    }
  }

  /**
   * Obtain the CalData object from the dropped item to be set to the reference value
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void drop(final DropTargetEvent event) {

    if (!this.ruleInfoSection.getParamCollDataProvider().isModifiable(this.ruleInfoSection.getCdrFunction())) {
      CDMLogger.getInstance().errorDialog("Insufficient Privileges", Activator.PLUGIN_ID);
      return;
    }
    final Object dragData = event.data;
    if (dragData instanceof StructuredSelection) {
      final IStructuredSelection structuredSelection = (StructuredSelection) dragData;
      final Object firstElement = structuredSelection.getFirstElement();
      // From SeriesStatisticsView or from another rule
      if (firstElement instanceof SeriesStatisticsInfo) {
        dropSeriesStatisticsInfo((SeriesStatisticsInfo) firstElement);

      }
      // From ScratchPad
      else if (firstElement instanceof ScratchPadDataFetcher) {
        ScratchPadDataFetcher scratchPadDataProvider = (ScratchPadDataFetcher) firstElement;
        dropSeriesStatisticsInfo(scratchPadDataProvider.getSeriesStatsInfo());
      }
    }
  }

  /**
   * Drop the series statistics info to the reference value
   *
   * @param seriesStatisticsInfo seriesStatisticsInfo
   */
  private void dropSeriesStatisticsInfo(final SeriesStatisticsInfo seriesStatisticsInfo) {
    if (seriesStatisticsInfo != null) {
      // unset the flag to know that the reference value is dragged
      this.ruleInfoSection.setRefValTyped(false);
      final CalData calData = seriesStatisticsInfo.getCalData();
      CalDataType calDataPhyValType = seriesStatisticsInfo.getCalDataPhyValType();
      final String droppedParamName = calData.getShortName();
      final String droppedParamType = calData.getCalDataPhy().getType();

      // Source parameter and destination parameter are same
      if (this.ruleInfoSection.getSelectedParam().getName().equals(droppedParamName) &&
          this.ruleInfoSection.getSelectedParam().getType().equals(droppedParamType)) {
        setSeriesInfoWithUnitRemark(calData);
      }
      // Source and destination parameters are different, but same type.
      // Copying of caldata is allowed after the confirmation from the user
      else if (this.ruleInfoSection.getSelectedParam().getType().equals(droppedParamType)) {
        MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            "Copy Confirmation", null, "Parameter names are different.Do you really want to copy the CalData value?",
            MessageDialog.QUESTION_WITH_CANCEL, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL },
            0);
        dialog.open();
        final int btnSel = dialog.getReturnCode();
        if (btnSel == Window.OK) {
          copyCalDataFromDiffParam(calData, calDataPhyValType);
        }
      }
      // Source and destination parameters are of differnt type and name, Show error message.
      // Copying is NOT allowed in this case
      else {
        CDMLogger.getInstance().errorDialog(RuleInfoSection.SRC_DEST_PAR_TYP_DIFF, Activator.PLUGIN_ID);
      }
    }
    // set the flag to further modification through typing
    this.ruleInfoSection.setRefValTyped(true);
  }

  /**
   * Copy the caldata object from a different parameter, but same type
   *
   * @param calData CalData source caldata object
   * @param calDataType CalDataType type of caldata
   */
  private void copyCalDataFromDiffParam(final CalData calData, final CalDataType calDataType) {
    switch (calDataType) {
      case IMPORT_CDFX:
      case IMPORT_DCM:
      case IMPORT_PACO:
        // For Caldata imported from file, set the unit, remarks etc to the rule
        copyCalDataThroScratchPadImport(calData);
        break;

      case AVERAGE:
      case MIN:
      case MAX:
      case PEAK:
      case MEDIAN:
      case LOWER_QUARTILE:
      case UPPER_QUARTILE:
      case VALUE:
        caseValue(calData);
        break;

      default:
        createSetCalDataCopy(calData);
    }
    this.ruleInfoSection.onRefValTextModification();
    this.ruleInfoSection.removeListenerFromTextField(this.ruleInfoSection.getRefValueTxt());
    this.ruleInfoSection.addListener(this.ruleInfoSection.getRefValueTxt(), this.ruleInfoSection.getRefValCalDataObj(),
        true);
  }

  /**
   * @param calData
   */
  private void caseValue(final CalData calData) {
    // set the unit alone when copied from series statistics
    createSetCalDataCopy(calData);
    this.ruleInfoSection.getUnitText().setText(this.ruleInfoSection.getRefValCalDataObj().getCalDataPhy().getUnit());// ICDM-1052
    this.ruleInfoSection.setSelectedUnit(this.ruleInfoSection.getRefValCalDataObj().getCalDataPhy().getUnit());
  }

  /**
   * copy CalData through drag and drop of caldata from scratch pad
   *
   * @param calData CalData
   */
  private void copyCalDataThroScratchPadImport(final CalData calData) {
    // iCDM-2071
    // making a copy of the caldata object
    CalData calDataCopy = CalDataUtil.createCopy(calData, CDMLogger.getInstance());
    // changing the name of the parameter in caldata object
    calDataCopy.getCalDataPhy().setName(this.ruleInfoSection.getSelectedParam().getName());
    this.ruleInfoSection.setRefValCalDataObj(calDataCopy);
    if (this.ruleInfoSection.getRefCalAttr() != null) {
      this.ruleInfoSection.getRefCalAttr().setCalData(this.ruleInfoSection.getRefValCalDataObj());
    }
    this.ruleInfoSection.getRefValueTxt()
        .setText(this.ruleInfoSection.getRefValCalDataObj().getCalDataPhy().getSimpleDisplayValue());
    // setting unit,remark, maturity level
    this.ruleInfoSection.setValues();
  }

  /**
   * @param calData CalData
   */
  private void createSetCalDataCopy(final CalData calData) {
    // iCDM-2071
    // making a copy of the caldata object
    CalData calDataCopy = CalDataUtil.createCopyWithoutHistory(calData, CDMLogger.getInstance());
    // changing the name of the parameter in caldata object
    calDataCopy.getCalDataPhy().setName(this.ruleInfoSection.getSelectedParam().getName());
    this.ruleInfoSection.setRefValCalDataObj(calDataCopy);
    if (this.ruleInfoSection.getRefCalAttr() != null) {
      this.ruleInfoSection.getRefCalAttr().setCalData(this.ruleInfoSection.getRefValCalDataObj());
    }
    this.ruleInfoSection.getRefValueTxt()
        .setText(this.ruleInfoSection.getRefValCalDataObj().getCalDataPhy().getSimpleDisplayValue());
  }

  /**
   * ICDM-1126 copying caldata
   *
   * @param calData CalData
   */
  private void setSeriesInfoWithUnitRemark(final CalData calData) {
    this.ruleInfoSection.setRefValCalDataObj(calData);
    if (this.ruleInfoSection.getRefCalAttr() != null) {
      this.ruleInfoSection.getRefCalAttr().setCalData(this.ruleInfoSection.getRefValCalDataObj());
    }
    this.ruleInfoSection.getRefValueTxt()
        .setText(this.ruleInfoSection.getRefValCalDataObj().getCalDataPhy().getSimpleDisplayValue());
    // ICDM-1125
    this.ruleInfoSection.setValues();
    this.ruleInfoSection.onRefValTextModification();
    this.ruleInfoSection.removeListenerFromTextField(this.ruleInfoSection.getRefValueTxt());

    this.ruleInfoSection.addListener(this.ruleInfoSection.getRefValueTxt(), this.ruleInfoSection.getRefValCalDataObj(),
        true);
  }

}
