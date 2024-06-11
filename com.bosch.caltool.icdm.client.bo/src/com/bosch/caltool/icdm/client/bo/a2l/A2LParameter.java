/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.calmodel.a2ldata.AbstractA2LObject;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider.SSD_CLASS;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author rgo7cob Icdm-586
 */
public class A2LParameter extends AbstractA2LObject {


  /**
   * @author rgo7cob for Sort the Parametres
   */
  public enum SortColumns {
                           /**
                            * Sort Based On the Name
                            */
                           SORT_CHAR_NAME,
                           /**
                            * Sort Based on Long name
                            */
                           SORT_CHAR_LONGNAME,
                           /**
                            * Sort Based on Type
                            */
                           SORT_CHAR_TYPE,
                           /**
                            * Sort Based on Def Fun
                            */
                           SORT_CHAR_DEFFUNC,
                           /**
                            * Sort Based on Unit
                            */
                           SORT_CHAR_UNIT,
                           /**
                            * Sort Based on class
                            */
                           SORT_CHAR_CLASS,
                           /**
                            * Sort Based on code
                            */
                           SORT_CHAR_CODE,
                           /**
                            * Sort Based on caldataphy value
                            */
                           SORT_CHAR_VALUE,
                           /**
                           *
                           */
                           // ICDM-2439
                           SORT_PARAM_TYPE_COMPLIANCE,
                           /**
                            * Sort Based on status
                            */
                           SORT_CHAR_STATUS
  }


  private final Characteristic characteristic;

  private final boolean codeWord;

  private final ParameterClass pclass;

  private CalData calData;

  private String status;

  // ICDM-841
  private boolean labParam;

  private HistoryEntry latestHistoryEntry;

  /**
   * ssd class instance
   */
  private final SSD_CLASS ssdClass;
  /**
   * Param Id
   */
  private Long paramId;

  private String bcName;

  private final boolean isBlackList;

  /**
   * Qssd Parameter
   */
  private final boolean isQssdParameter;

  /**
   * @param paramId Long
   * @param characteristic characteristic
   * @param parameterClass pclass
   * @param codeWord true for codeWords
   * @param ssdClass ssdClass
   * @param isBlackList true for blacklisted parameter
   * @param isQssdParameter true for QSSD parameter
   */
  public A2LParameter(final Long paramId, final Characteristic characteristic, final String parameterClass,
      final boolean codeWord, final String ssdClass, final boolean isBlackList, final boolean isQssdParameter) {
    super();
    this.paramId = paramId;
    this.characteristic = characteristic;
    this.pclass = ParameterClass.getParamClass(parameterClass);
    this.codeWord = codeWord;
    this.ssdClass = SSD_CLASS.getSsdClass(ssdClass);
    this.isBlackList = isBlackList;
    this.isQssdParameter = isQssdParameter;
  }


  /**
   * @return the characteristic
   */
  public Characteristic getCharacteristic() {
    return this.characteristic;
  }


  /**
   * @return the isBlackList
   */
  public boolean isBlackList() {
    return this.isBlackList;
  }


  /**
   * @return the name of the Characteristics
   */
  @Override
  public String getName() {
    return this.characteristic.getName();
  }


  /**
   * Gets the long identifier of the characteristic.
   *
   * @return the long identifier of the characteristic.
   */
  public String getLongIdentifier() {
    return this.characteristic.getLongIdentifier();
  }

  /**
   * @return the Unit value
   */
  public String getUnit() {
    return this.characteristic.getCompuMethodRef().getUnit();
  }


  /**
   * @return the def function
   */
  public Function getDefFunction() {
    return this.characteristic.getDefFunction();
  }

  /**
   * @return the type
   */
  public String getType() {
    return this.characteristic.getType();
  }

  /**
   * @return the pclass String
   */
  public String getPclassString() {
    if (this.pclass != null) {
      return this.pclass.getText();
    }
    return "";
  }

  /**
   * @return the pclass
   */
  public ParameterClass getPclass() {
    return this.pclass;
  }


  /**
   * @return the code
   */
  public String getCodeWordString() {
    if (this.codeWord) {
      return ApicConstants.USED_YES_DISPLAY;
    }
    return ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * @return the ssdClass
   */
  public SSD_CLASS getSsdClass() {
    return this.ssdClass;
  }

  /**
   * @return the codeword
   */
  public boolean isCodeWord() {
    return this.codeWord;
  }


  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return the Compare Int
   */
  public int compareTo(final A2LParameter param2, final SortColumns sortColumn) {
    int compareResult;
    switch (sortColumn) {
      // ICDM-2439
      case SORT_PARAM_TYPE_COMPLIANCE:
        // use compare method for Strings
        // sorting the order by checking the occurence of the ComplianceParam, BlackList, QssdParameter , ReadOnly
        compareResult = AbstractA2LObject.compareBoolean(isComplianceParam(), param2.isComplianceParam());
        if (compareResult == 0) {
          compareResult = AbstractA2LObject.compareBoolean(isBlackList(), param2.isBlackList());
        }
        if (compareResult == 0) {
          compareResult = AbstractA2LObject.compareBoolean(isQssdParameter(), param2.isQssdParameter());
        }
        if (compareResult == 0) {
          compareResult = AbstractA2LObject.compareBoolean(getCharacteristic().isReadOnly(),
              param2.getCharacteristic().isReadOnly());
        }
        break;

      case SORT_CHAR_NAME:
        // use compare method for Strings
        compareResult = compareTo(param2);
        break;

      case SORT_CHAR_LONGNAME:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getLongIdentifier(), param2.getLongIdentifier());
        break;

      case SORT_CHAR_TYPE:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getType(), param2.getType());
        break;

      case SORT_CHAR_UNIT:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getUnit(), param2.getUnit());
        break;
      /* icdm-469 */
      case SORT_CHAR_DEFFUNC:
        // use compare method for Strings
        // Null Check added for the Characteristics which do not have functions icdm-469
        if ((getDefFunction() == null) && (param2.getDefFunction() == null)) {
          return compareTo(param2);
        }
        else if (getDefFunction() == null) {
          return -1;
        }
        else if (param2.getDefFunction() == null) {
          return 1;
        }
        else {
          compareResult = AbstractA2LObject.compare(getDefFunction().getName(), param2.getDefFunction().getName());
        }
        break;
      case SORT_CHAR_CODE:
        compareResult = AbstractA2LObject.compare(getCodeWordString(), param2.getCodeWordString());
        break;

      case SORT_CHAR_CLASS:
        compareResult = AbstractA2LObject.compare(getPclassString(), param2.getPclassString());
        break;
      case SORT_CHAR_VALUE:
        compareResult = compCalData(param2);
        break;
      case SORT_CHAR_STATUS:
        compareResult = AbstractA2LObject.compare(getStatus(), param2.getStatus());
        break;
      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the characteristic
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(param2);
    }
    return compareResult;
  }


  /**
   * @param param2
   * @return
   */
  private int compCalData(final A2LParameter param2) {
    int compareResult;
    String param1DisplayVal =
        (CommonUtils.isNotNull(getCalData()) && (CommonUtils.isNotNull(getCalData().getCalDataPhy())))
            ? getCalData().getCalDataPhy().getSimpleDisplayValue() : "";
    String param2DisplayVal =
        ((CommonUtils.isNotNull(param2.getCalData())) && (CommonUtils.isNotNull(param2.getCalData().getCalDataPhy())))
            ? param2.getCalData().getCalDataPhy().getSimpleDisplayValue() : "";
    compareResult = ApicUtil.compareStringAndNum(param1DisplayVal, param2DisplayVal);
    return compareResult;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    if ((this.calData != null) && (this.status == null)) {
      this.status = "";
      CalDataHistory calDataHistory = this.calData.getCalDataHistory();
      if ((calDataHistory != null) && (calDataHistory.getHistoryEntryList() != null) &&
          !calDataHistory.getHistoryEntryList().isEmpty()) {
        // Last Element from the HistoryEntryList is considered as the one with latest timestamp
        this.latestHistoryEntry =
            calDataHistory.getHistoryEntryList().get(calDataHistory.getHistoryEntryList().size() - 1);
        DataElement stateElement = this.latestHistoryEntry.getState();
        if (stateElement != null) {
          this.status = stateElement.getValue();
        }
      }
    }
    return this.status == null ? "" : this.status;
  }


  /**
   * @param status the status to set
   */
  public void setStatus(final String status) {
    this.status = status;
  }


  /**
   * @return the calDataPhy
   */
  public CalData getCalData() {
    return this.calData;
  }

  /**
   * @return if the param is compliant
   */
  public boolean isComplianceParam() {
    if (this.ssdClass == null) {
      return false;
    }
    return this.ssdClass.isCompliant();
  }

  /**
   * @return true, if the characteristic is located in the Calibration Memory
   */
  public boolean isInCalMemory() {
    return this.characteristic.isInCalMemory();
  }

  /**
   * @param calData the calDataPhy to set
   */
  public void setCalData(final CalData calData) {
    this.calData = calData;
  }

  // ICDM-841
  /**
   * @param isLABParam the isLABParam to set
   */
  public void setLABParam(final boolean isLABParam) {
    this.labParam = isLABParam;
  }

  /**
   * @return true if param is present in LAB file
   */
  public boolean isLABParam() {
    return this.labParam;
  }


  /**
   * @return the latestHistoryEntry
   */
  public HistoryEntry getLatestHistoryEntry() {
    return this.latestHistoryEntry;
  }

  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }


  /**
   * @param bcName BC name
   */
  public void setBCName(final String bcName) {
    this.bcName = bcName;

  }

  /**
   * @return the bcName
   */
  public String getBcName() {
    return this.bcName;
  }


  /**
   * @return the isQssdParameter
   */
  public boolean isQssdParameter() {
    return this.isQssdParameter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AbstractA2LObject a2lObject) {
    return ApicUtil.compare(getName(), a2lObject.getName());
  }
}
