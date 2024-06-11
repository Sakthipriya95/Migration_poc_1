/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.Module;
import com.bosch.calmodel.a2ldata.module.axis.AxisDescription;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.compute.CompuMethod;
import com.bosch.calmodel.a2ldata.module.compute.CompuMethodRatFunc;
import com.bosch.calmodel.a2ldata.module.compute.CompuMethodTable;
import com.bosch.calmodel.a2ldata.module.compute.CompuTable;
import com.bosch.calmodel.a2ldata.module.compute.CompuVtab;
import com.bosch.calmodel.a2ldata.module.labels.AxisPts;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicCurve;
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicMap;
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicValue;
import com.bosch.calmodel.a2ldata.module.labels.CharacteristicValueBlock;
import com.bosch.calmodel.a2ldata.module.memory.layout.RecLayoutPar;
import com.bosch.calmodel.a2ldata.module.memory.layout.RecordLayout;
import com.bosch.calmodel.a2ldata.module.system.constant.SystemConstant;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataAxis;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.calmodel.caldataphy.CalDataPhyAxisPts;
import com.bosch.calmodel.caldataphy.CalDataPhyCurve;
import com.bosch.calmodel.caldataphy.CalDataPhyMap;
import com.bosch.calmodel.caldataphy.CalDataPhyValBlk;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.checkssd.datamodel.a2lvariable.A2LData;
import com.bosch.checkssd.datamodel.util.SSDModelUtils;


/**
 * LabelCreationUtils
 *
 * @author SMN6KOR
 */
public class LabelCreationUtils {

  private A2LFileInfo a2lFileInfo;

  private Map<String, CalData> calDataMap;

  private Map<String, String> labelCategoryMap;

  private final ILoggerAdapter logger;

  private static final String COMP_TABLE = "CompuMethodTable";

  private static final String VALUE = "VALUE";

  private static final String CURVE = "CURVE";

  private static final String AXIS_PTS = "AXIS_PTS";

  private static final String COMPU_METHOD = "COMPU_METHOD";

  private static final String COM_AXIS = "COM_AXIS";


  /**
   * @param a2lFileInfo a2l object
   * @param calDataMap cala data
   * @param plogger log instance
   */
  public LabelCreationUtils(final A2LFileInfo a2lFileInfo, final Map<String, CalData> calDataMap,
      final ILoggerAdapter plogger) {
    this.a2lFileInfo = a2lFileInfo;
    this.calDataMap = calDataMap;
    this.logger = plogger;
  }

  /**
   *
   */
  public void createA2lFileInfo() {

    if (this.a2lFileInfo == null) {
      this.a2lFileInfo = new A2LFileInfo();
      Module module = new Module();

      this.a2lFileInfo.getModuleMap().put("DIM", module);
      setA2lFileInfo(this.a2lFileInfo);
      if (module.getFunctionsMap() == null) {

        Map<String, Function> fucntionMap = new HashMap<>();
        Function function = new Function();
        function.setName("funcName");
        function.setFunctionVersion("1.0.0");

        fucntionMap.put("funcName", function);
        module.getFunctionsMap().putAll(fucntionMap);
      }

    }
  }


  /**
   * @param lblchar characteristic label
   */
  public void createCharacteristicValue(final Characteristic lblchar) {
    lblchar.setConversion(COMP_TABLE);
    Map<String, CompuMethod> compuMethodMap11 = new HashMap<>();
    CompuMethod compuuMethod = new CompuMethod();

    CompuMethodTable compuTab = new CompuMethodTable();
    compuTab.setCompuTab("CompuTab");
    CompuTable newCompuTabRef = new CompuVtab();
    String conversionType = "3";
    newCompuTabRef.setConversionType(conversionType);
    newCompuTabRef.setNumValPairs(1);
    compuTab.setCompuTabRef(createCompuVTabValue(newCompuTabRef));
    compuuMethod.setConversion(compuTab);
    byte compuType = 3;
    compuuMethod.setConversionType(compuType);
    compuMethodMap11.put(COMP_TABLE, compuuMethod);

    CalDataPhy caldataphy = null;
    if (this.calDataMap == null) {
      this.calDataMap = new HashMap<>();
    }
    caldataphy = new CalDataPhyValue();
    caldataphy.setName(lblchar.getName());
    caldataphy.setText(true);
    AtomicValuePhy value = new AtomicValuePhy("Value1");
    ((CalDataPhyValue) caldataphy).setAtomicValuePhy(value);
    CalData calData = new CalData();
    calData.setCalDataPhy(caldataphy);


    this.calDataMap.put(lblchar.getName(), calData);

    lblchar.setCompuMethodRef(compuMethodMap11, this.logger);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().put(lblchar.getName(), lblchar);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicValueMap().put(lblchar.getName(),
        ((CharacteristicValue) lblchar));


  }

  /**
   * @param labelName label
   * @param utilInst util instance
   * @return a2l characteristic
   */
  public Characteristic getCharacteristics(final String labelName, final SSDModelUtils utilInst) {
    if (this.a2lFileInfo == null) {
      this.a2lFileInfo = new A2LFileInfo();
      Module module = new Module();
      this.a2lFileInfo.getModuleMap().put("DIM", module);
      setA2lFileInfo(this.a2lFileInfo);
      A2LData a2lData = new A2LData(this.a2lFileInfo);

      utilInst.setA2lData(a2lData);

    }
    if (this.calDataMap == null) {
      this.calDataMap = new HashMap<>();
      utilInst.setCalDataMap(this.calDataMap);
    }
    Characteristic characteristic = null;
    if (!labelName.equalsIgnoreCase("EXISTS") && !labelName.equalsIgnoreCase("\"TRUE\"") &&
        !labelName.equalsIgnoreCase("\"FALSE\"")) {
      if ((!labelName.isEmpty() &&
          !this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().containsKey(labelName))) {
        characteristic = getLabelCategoryFromMap(labelName, characteristic);
      }
      else {
        if (this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().containsKey(labelName)) {
          characteristic = this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().get(labelName);
        }
      }
    }
    return characteristic;
  }

  /**
   * @param labelName
   * @param characteristic
   * @return
   */
  private Characteristic getLabelCategoryFromMap(final String labelName, Characteristic characteristic) {
    // get label category from the label category map

    String labelCategory = getLabelCategoryMap().get(labelName.trim());
    if (labelCategory != null) {
      switch (labelCategory.toUpperCase()) {
        case VALUE:
          characteristic = createCharacteristValue(labelName);
          break;
        case CURVE:
        case "CURVE_INDIVIDUAL":
        case "CURVE_FIXED":
          characteristic = createCharacteristicCurve(labelName, labelCategory);
          break;
        case "CURVE_GROUPED":
          characteristic = createCharacteristicCurve(labelName, labelCategory);
          break;
        case "MAP":
        case "MAP_INDIVIDUAL":
        case "MAP_FIXED":
          characteristic = createCharactertisticMap(labelName, labelCategory);
          break;
        case "AXIS_VALUES":
          characteristic = createCharactertisticAxisPts(labelName);
          break;
        case "VALUE_BLOCK":
          characteristic = createCharacteristicVblk(labelName);
          break;
        case "MAP_GROUPED":
          characteristic = createCharactertisticMap(labelName, labelCategory);
          break;
        case "ADJUSTABLE":
          characteristic = new CharacteristicValue();
          characteristic.setType("SYSTEMCONSTANT");
          if (this.calDataMap == null) {
            this.calDataMap = new HashMap<>();
          }
          SystemConstant value = new SystemConstant(labelName, "1.0");
          this.a2lFileInfo.getModuleMap().get("DIM").getSystemConstantsMap().put(labelName, value);
          break;

        default:
          characteristic = createCharacteristValue(labelName);
          break;
      }
    }
    return characteristic;
  }


  /**
   * @param labelName
   * @return
   */
  private Characteristic createCharacteristicVblk(final String labelName) {
    Characteristic characteristic = new CharacteristicValueBlock();
    characteristic.setName(labelName);
    characteristic.setType("VAL_BLK");
    characteristic.setConversion(COMP_TABLE);
    Map<String, CompuMethod> compuMethodMap11 = new HashMap<>();
    CompuMethod compuuMethod = new CompuMethod();

    CompuMethodTable compuTab = new CompuMethodTable();
    compuTab.setCompuTab("CompuTab");
    CompuTable newCompuTabRef = new CompuVtab();
    newCompuTabRef.setConversionType("TAB_VERB");
    newCompuTabRef.setNumValPairs(1);
    RecLayoutPar par = new RecLayoutPar();
    par.setType((byte) 3);
    par.setDataType((byte) 3);
    RecordLayout recordLayout = new RecordLayout();
    recordLayout.setName("Val_Wu8");
    RecLayoutPar[] recArray = new RecLayoutPar[1];
    recArray[0] = par;
    recordLayout.setParameter(recArray);
    characteristic.setRecordLayoutRef(recordLayout);
    compuTab.setCompuTabRef(createCompuVTabValue(newCompuTabRef));
    compuuMethod.setConversion(compuTab);
    byte compuType = 3;
    compuuMethod.setConversionType(compuType);
    compuMethodMap11.put(COMP_TABLE, compuuMethod);

    characteristic.setCompuMethodRef(compuMethodMap11, this.logger);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().put(labelName, characteristic);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicValueBlockMap().put(labelName,
        (CharacteristicValueBlock) characteristic);
    getCaldataPhy(characteristic);
    return characteristic;


  }


  /**
   * @param newCompuTabRef
   * @return
   */
  private CompuTable createCompuVTabValue(final CompuTable newCompuTabRef) {
    for (double i = 0.0; i < 19.0; i++) {

      ((CompuVtab) newCompuTabRef).setValue(0.0, "Value" + i);
    }
    return newCompuTabRef;
  }


  /**
   * @param labelName
   * @return
   */
  private Characteristic createCharactertisticAxisPts(final String labelName) {
    Characteristic characteristic = new AxisPts();
    characteristic.setName(labelName);
    characteristic.setType(AXIS_PTS);
    characteristic.setConversion(COMPU_METHOD);
    AxisDescription axisDescription = new AxisDescription();
    axisDescription.setAttribute(COM_AXIS);
    axisDescription.setConversion(COMPU_METHOD);
    RecLayoutPar par = new RecLayoutPar();
    par.setType((byte) 5);
    par.setDataType((byte) 5);
    characteristic = createRatFunc(characteristic, par);
    Map<String, AxisPts> axisPtsMap = new HashMap<>();
    axisPtsMap.put(labelName, (AxisPts) characteristic);
    ((AxisPts) characteristic).setAxisPtsRef(axisPtsMap, this.logger);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().put(labelName, characteristic);
    this.a2lFileInfo.getModuleMap().get("DIM").getAxisPtsMap().put(labelName, (AxisPts) characteristic);
    getCaldataPhy(characteristic);
    return characteristic;
  }


  /**
   * @param labelName
   * @param ma
   * @return
   */
  private Characteristic createCharactertisticMap(final String labelName, final String mapType) {


    Characteristic characteristic;
    characteristic = new CharacteristicMap();
    characteristic.setName(labelName);
    characteristic.setType("MAP");
    characteristic.setConversion(COMPU_METHOD);
    RecLayoutPar par = new RecLayoutPar();
    par.setType((byte) 4);
    par.setDataType((byte) 4);
    characteristic = createRatFunc(characteristic, par);
    AxisDescription axisDescription = new AxisDescription();
    if (mapType.equalsIgnoreCase("MAP") || (mapType.equalsIgnoreCase("MAP_INDIVIDUAL")) ||
        (mapType.equalsIgnoreCase("MAP_FIXED"))) {
      axisDescription.setAttribute("STD_AXIS");
    }
    else {
      axisDescription.setAttribute(COM_AXIS);
    }
    axisDescription.setConversion(COMPU_METHOD);
    Map<String, CompuMethod> compuMethodMap = new HashMap<>();
    compuMethodMap.put(COMPU_METHOD, characteristic.getCompuMethodRef());
    axisDescription.setCompuMethodRef(compuMethodMap, this.logger);
    ((CharacteristicMap) characteristic).addAxisDescription(axisDescription);
    ((CharacteristicMap) characteristic).addAxisDescription(axisDescription);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().put(labelName, characteristic);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicMapMap().put(labelName,
        ((CharacteristicMap) characteristic));
    getCaldataPhy(characteristic);
    return characteristic;
  }


  /**
   * @param labelName
   * @param curveType
   * @return
   */
  private Characteristic createCharacteristicCurve(final String labelName, final String curveType) {
    Characteristic characteristic;
    characteristic = new CharacteristicCurve();
    characteristic.setName(labelName);
    characteristic.setType(CURVE);
    characteristic.setConversion(COMPU_METHOD);
    RecLayoutPar par = new RecLayoutPar();
    par.setType((byte) 4);
    par.setDataType((byte) 4);
    characteristic = createRatFunc(characteristic, par);
    AxisDescription axisDescription = new AxisDescription();
    if (curveType.equalsIgnoreCase(CURVE) || curveType.equalsIgnoreCase("CURVE_INDIVIDUAL") ||
        curveType.equalsIgnoreCase("CURVE_FIXED")) {
      axisDescription.setAttribute("FIX_AXIS");
    }
    else {
      axisDescription.setAttribute(COM_AXIS);
    }
    axisDescription.setConversion(COMPU_METHOD);
    Map<String, CompuMethod> compuMethodMap = new HashMap<>();
    compuMethodMap.put(COMPU_METHOD, characteristic.getCompuMethodRef());
    axisDescription.setCompuMethodRef(compuMethodMap, this.logger);
    ((CharacteristicCurve) characteristic).addAxisDescription(axisDescription);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().put(labelName, characteristic);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicCurveMap().put(labelName,
        ((CharacteristicCurve) characteristic));
    getCaldataPhy(characteristic);
    return characteristic;
  }


  /**
   * @param labelName
   * @return
   */
  private Characteristic createCharacteristValue(final String labelName) {
    Characteristic characteristic;
    characteristic = new CharacteristicValue();
    characteristic.setName(labelName);
    characteristic.setType(VALUE);
    characteristic.setLowerLimit("1.0");
    characteristic.setUpperLimit("10.0");
    characteristic.setConversion(COMPU_METHOD);
    RecLayoutPar par = new RecLayoutPar();
    par.setType((byte) 1);
    par.setDataType((byte) 1);
    characteristic = createRatFunc(characteristic, par);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicsMap().put(labelName, characteristic);
    this.a2lFileInfo.getModuleMap().get("DIM").getCharacteristicValueMap().put(labelName,
        ((CharacteristicValue) characteristic));
    getCaldataPhy(characteristic);
    return characteristic;
  }


  /**
   * @param characteristic
   */
  private Characteristic createRatFunc(final Characteristic characteristic, final RecLayoutPar par) {
    CompuMethodRatFunc ratFunc = new CompuMethodRatFunc();
    Double zeroVal = Double.valueOf(0.0);
    Double oneVal = Double.valueOf(1.0);
    ratFunc.setCoeff(0, zeroVal.toString());
    ratFunc.setCoeff(1, oneVal.toString());
    ratFunc.setCoeff(2, zeroVal.toString());
    ratFunc.setCoeff(3, zeroVal.toString());
    ratFunc.setCoeff(4, zeroVal.toString());
    ratFunc.setCoeff(5, oneVal.toString());
    CompuMethod compuMethod = new CompuMethod();
    compuMethod.setConversion(ratFunc);
    compuMethod.setConversionType("RAT_FUNC");
    Map<String, CompuMethod> compuMethodMap = new HashMap<>();
    compuMethodMap.put(COMPU_METHOD, compuMethod);
    characteristic.setCompuMethodRef(compuMethodMap, this.logger);
    RecordLayout recordLayout = new RecordLayout();
    recordLayout.setName("Val_Wu8");
    RecLayoutPar[] recArray = new RecLayoutPar[1];
    recArray[0] = par;
    recordLayout.setParameter(recArray);
    characteristic.setRecordLayoutRef(recordLayout);

    return characteristic;
  }

  /**
   * @param characteristic a2l char
   * @return physical value
   */
  public CalDataPhy getCaldataPhy(final Characteristic characteristic) {
    CalDataPhy caldataphy = null;
    if (this.calDataMap == null) {
      this.calDataMap = new HashMap<>();
    }
    if (characteristic instanceof CharacteristicValue) {
      caldataphy = new CalDataPhyValue();
      caldataphy.setName(characteristic.getName());
      caldataphy.setType(VALUE);
      AtomicValuePhy value = new AtomicValuePhy(1.0);
      ((CalDataPhyValue) caldataphy).setAtomicValuePhy(value);
      CalData calData = new CalData();
      calData.setCalDataPhy(caldataphy);


      this.calDataMap.put(characteristic.getName(), calData);
    }
    else if (characteristic instanceof CharacteristicMap) {
      caldataphy = new CalDataPhyMap();
      caldataphy.setName(characteristic.getName());
      AtomicValuePhy[] value = new AtomicValuePhy[50];
      AtomicValuePhy[][] atomicValuePhyArray = new AtomicValuePhy[50][50];
      for (int i = 0; i < 50; i++) {
        for (int j = 0; j < 50; j++) {
          AtomicValuePhy atomicValue = new AtomicValuePhy(i);
          atomicValuePhyArray[i][j] = atomicValue;
          value[i] = atomicValue;
        }
      }
      ((CalDataPhyMap) caldataphy).setAtomicValuePhy(atomicValuePhyArray);
      CalDataAxis calDataAxis = getCalDataAxis("MAP");
      calDataAxis.setAtomicValuePhy(value);
      ((CalDataPhyMap) caldataphy).setCalDataAxisX(calDataAxis);
      ((CalDataPhyMap) caldataphy).setCalDataAxisY(calDataAxis);
      CalData calData = new CalData();
      calData.setCalDataPhy(caldataphy);
      this.calDataMap.put(characteristic.getName(), calData);
    }
    else if (characteristic instanceof CharacteristicCurve) {
      caldataphy = new CalDataPhyCurve();
      caldataphy.setName(characteristic.getName());
      AtomicValuePhy[] value = new AtomicValuePhy[50];
      for (int i = 0; i < 50; i++) {
        AtomicValuePhy atomicValue = new AtomicValuePhy(i);
        value[i] = atomicValue;
      }
      ((CalDataPhyCurve) caldataphy).setAtomicValuePhy(value);
      CalDataAxis calDataAxis = getCalDataAxis(CURVE);
      ((CalDataPhyCurve) caldataphy).setCalDataAxisX(calDataAxis);
      calDataAxis.setAtomicValuePhy(value);
      CalData calData = new CalData();
      calData.setCalDataPhy(caldataphy);
      this.calDataMap.put(characteristic.getName(), calData);
    }
    else if (characteristic instanceof AxisPts) {
      caldataphy = new CalDataPhyAxisPts();
      caldataphy.setName(characteristic.getName());
      AtomicValuePhy[] value = new AtomicValuePhy[50];
      for (int i = 0; i < 50; i++) {
        AtomicValuePhy atomicValue = new AtomicValuePhy(i);
        value[i] = atomicValue;
      }
      ((CalDataPhyAxisPts) caldataphy).setAtomicValuePhy(value);
      CalDataAxis calDataAxis = getCalDataAxis(AXIS_PTS);
      ((CalDataPhyAxisPts) caldataphy).setCalDataAxis(calDataAxis);
      calDataAxis.setAtomicValuePhy(value);
      CalData calData = new CalData();
      calData.setCalDataPhy(caldataphy);
      this.calDataMap.put(characteristic.getName(), calData);
    }
    else if (characteristic instanceof CharacteristicValueBlock) {
      caldataphy = getCalDataPhyForValueBlk(characteristic);
    }

    return caldataphy;


  }

  /**
   * @param characteristic
   * @return
   */
  private CalDataPhy getCalDataPhyForValueBlk(final Characteristic characteristic) {
    CalDataPhy caldataphy;
    caldataphy = new CalDataPhyValBlk();
    caldataphy.setName(characteristic.getName());
    AtomicValuePhy[][][] atomicValuePhy = new AtomicValuePhy[1][1][50];
    for (int xAxis = 0; xAxis < 1; xAxis++) {
      for (int yAxis = 0; yAxis < 1; yAxis++) {
        for (int zAxis = 0; zAxis < 50; zAxis++) {
          String value = "Value" + zAxis;

          atomicValuePhy[xAxis][yAxis][zAxis] = new AtomicValuePhy(value);
        }
      }
    }
    caldataphy.setText(true);
    ((CalDataPhyValBlk) caldataphy).setNoOfValues(50);
    ((CalDataPhyValBlk) caldataphy).setAtomicValuePhy(atomicValuePhy);
    CalData calData = new CalData();
    calData.setCalDataPhy(caldataphy);

    this.calDataMap.put(characteristic.getName(), calData);
    return caldataphy;
  }


  /**
   * @param axisType axis
   * @return cal data
   */
  public CalDataAxis getCalDataAxis(final String axisType) {
    CalDataAxis calDataAxisX = new CalDataAxis();
    calDataAxisX.setUnit("-");
    calDataAxisX.setNoOfAxisPts(50);
    calDataAxisX.setText(false);
    if (!axisType.equals(AXIS_PTS)) {
      calDataAxisX.setAxisType((byte) 2);
    }
    else {
      calDataAxisX.setAxisType((byte) 7);
    }
    return calDataAxisX;


  }


  /**
   * @return the calDataMap
   */
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }


  /**
   * @param calDataMap the calDataMap to set
   */
  public void setCalDataMap(final Map<String, CalData> calDataMap) {
    this.calDataMap = calDataMap;
  }


  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }


  /**
   * @param a2lFileInfo the a2lFileInfo to set
   */
  public void setA2lFileInfo(final A2LFileInfo a2lFileInfo) {
    this.a2lFileInfo = a2lFileInfo;
  }


  /**
   * @return the labelCategoryMap
   */
  public Map<String, String> getLabelCategoryMap() {
    return this.labelCategoryMap;
  }


  /**
   * @param labelCategoryMap the labelCategoryMap to set
   */
  public void setLabelCategoryMap(final Map<String, String> labelCategoryMap) {
    this.labelCategoryMap = labelCategoryMap;
  }


}
