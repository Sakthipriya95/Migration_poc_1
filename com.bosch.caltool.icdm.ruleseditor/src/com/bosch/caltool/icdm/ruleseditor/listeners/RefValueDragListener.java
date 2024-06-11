/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Text;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.util.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.pages.RuleInfoSection;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class RefValueDragListener<D extends IParameterAttribute, P extends IParameter> {


  private final ParameterDataProvider paramDataProvider;
  private final ParamCollectionDataProvider paramCollDataProvider;


  /**
   * @param paramDataProvider paramDataProvider
   * @param paramCollDataProvider
   */
  public RefValueDragListener(final ParameterDataProvider paramDataProvider,
      final ParamCollectionDataProvider paramCollDataProvider) {
    this.paramDataProvider = paramDataProvider;
    this.paramCollDataProvider = paramCollDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  public void dragRefValue(final Transfer[] transferTypes, final Text textField, final String appendField,
      final RuleInfoSection ruleInfoSection) { // NOPMD

    final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    final DragSource source = new DragSource(textField, operations);
    source.setTransfer(transferTypes);
    source.addDragListener(new DragSourceListener() {


      @Override
      public void dragStart(final DragSourceEvent event) {
        final String value = textField.getText();
        if (("").equals(value) || ("n.a.").equalsIgnoreCase(value) || value.endsWith("%")) {
          event.doit = false;
        }
        else {
          event.doit = true;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {
        IParameter selectedParam = ruleInfoSection.getSelectedParam();
        CalData calData;
        // iCDM-2071
        calData = com.bosch.caltool.icdm.common.util.CalDataUtil
            .createCopyWithoutHistory(ruleInfoSection.getRefValCalDataObj(), CDMLogger.getInstance());
        if (null != calData) {
          CalData calDataObject;
          try {
            calDataObject = getCalDataHistoryDetails(new CurrentUserBO().getUserName(), calData, appendField,
                ruleInfoSection.getSelectedCdrRule(), ruleInfoSection.getCdrFunction(), selectedParam);
            event.data = calDataObject;
            SeriesStatisticsInfo calDataProvider;

            calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.REF_VALUE);
            calDataProvider.setDataSetName(ruleInfoSection.getSelectedCdrRule().getDependenciesForDisplay());
            final StructuredSelection struSelection = new StructuredSelection(calDataProvider);
            LocalSelectionTransfer.getTransfer().setSelection(struSelection);
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }


        }
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        ruleInfoSection.getRefValueTxt().setFocus();
      }
    });
  }

  /**
   * Creates the CaldataHistory components for CDR rule using the given inputs, and adds to the given CalData object
   *
   * @param currentUserName username
   * @param calData caldata object
   * @param appendField string to be appended
   * @param cdrRule rule instance
   * @param paramCol ruleSet/cdrFunction object
   * @param selectedParam
   * @return caldata object set with history
   */
  private CalData getCalDataHistoryDetails(final String currentUserName, final CalData calData,
      final String appendField, final ReviewRule cdrRule, final ParamCollection paramCol,
      final IParameter selectedParam) {
    DataElement dataElement;
    final CalDataHistory calDataHistory = new CalDataHistory();

    final List<HistoryEntry> historyEntryList = new ArrayList<HistoryEntry>();
    calDataHistory.setHistoryEntryList(historyEntryList);

    final HistoryEntry historyEntry = new HistoryEntry();
    historyEntryList.add(historyEntry);

    // State
    dataElement = new DataElement();

    if (null == cdrRule) {
      // Default is prelimCalibrated
      dataElement.setValue("prelimCalibrated");
    }
    else {
      dataElement.setValue(RuleMaturityLevel.getIcdmMaturityLvlFromImportFileTxt(cdrRule.getMaturityLevel()));
    }
    historyEntry.setState(dataElement);

    // Date - current date
    dataElement = new DataElement();
    SimpleDateFormat cdfxFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    dataElement.setValue(cdfxFormat.format(new Date()));
    historyEntry.setDate(dataElement);

    // User name - current user
    dataElement = new DataElement();
    dataElement.setValue(currentUserName);
    historyEntry.setPerformedBy(dataElement);

    // Remark
    dataElement = new DataElement();

    StringBuilder remark = new StringBuilder();
    if (null == cdrRule) {
      remark.append(appendField).append(" from iCDM Calibration Data Review rule.");
    }
    else {
      // If rule is given, add the hint of rule to the append field in remarks
      if (!CommonUtils.isEmptyString(appendField)) {
        remark.append(appendField).append("\n\n");
      }
      remark.append(null == cdrRule.getHint() ? "" : cdrRule.getHint());
    }
    dataElement.setValue(remark.toString());
    historyEntry.setRemark(dataElement);

    // Context
    dataElement = new DataElement();
    if ((null == cdrRule) || (null == cdrRule.getParameterName())) {
      dataElement.setValue("iCDM " + appendField + " from Calibration Data Review");
    }
    else {
      IParameter param = selectedParam;

      dataElement.setValue(param.getpClassText());
    }
    historyEntry.setContext(dataElement);

    // Program Identifier
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setProgramIdentifier(dataElement);

    // Project
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setProject(dataElement);

    // Data Identifier
    dataElement = new DataElement();
    if (paramCol == null) {
      dataElement.setValue("iCDM - CalDataReview");
    }
    else {
      dataElement.setValue(
          "iCDM - " + this.paramCollDataProvider.getObjectTypeName(paramCol) + "(" + paramCol.getName() + ")");
    }
    historyEntry.setDataIdentifier(dataElement);

    dataElement = getTargetVariant(cdrRule, selectedParam);

    historyEntry.setTargetVariant(dataElement);

    // Test object
    dataElement = new DataElement();
    dataElement.setValue("");
    historyEntry.setTestObject(dataElement);

    calData.setCalDataHistory(calDataHistory);
    return calData;
  }


  /**
   * @param reviewRule
   * @param ruleSet
   * @return
   */
  private DataElement getTargetVariant(final ReviewRule reviewRule, final IParameter selectedParam) {

    // Target Variant
    DataElement dataElement = new DataElement();
    if (null == reviewRule) {
      dataElement.setValue("");
    }
    else {


      if (this.paramDataProvider.hasDependency(selectedParam) && reviewRule.getDependencyList().isEmpty()) {
        dataElement.setValue("Default Rule");
      }
      else if (this.paramDataProvider.hasDependency(selectedParam) && !reviewRule.getDependencyList().isEmpty()) {
        dataElement.setValue(this.paramDataProvider.getAttrValString(reviewRule));
      }
      else {
        dataElement.setValue("");
      }
    }
    return dataElement;
  }

}
