package com.bosch.easee.eASEEcdm_ComAPI;

import com4j.*;


/**
 * ICDMParameterValue Interface
 */
@IID("{FE502807-8379-4D1F-9EB6-683B669224E5}")
public interface ICDMParameterValue extends Com4jObject {
    /**
     * property Type
     */
    @VTID(7)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamType type();

    /**
     * property ValueType
     */
    @VTID(8)
    com.bosch.easee.eASEEcdm_ComAPI.ECdmAsamValueType valueType();

    /**
     * property Unit
     */
    @VTID(9)
    java.lang.String unit();

    /**
     * property AxisUnitsSize
     */
    @VTID(10)
    int axisUnitsSize();

    /**
     * method AxisUnit
     */
    @VTID(11)
    java.lang.String axisUnit(int index);

    /**
     * property AxisNumber
     */
    @VTID(12)
    int axisNumber();

    /**
     * method GetSizeOfAxis
     */
    @VTID(13)
    int getSizeOfAxis(int iAxis);

    /**
     * method SetValueString
     */
    @VTID(14)
    void setValueString(java.lang.String iValue);

    /**
     * method SetValueDouble
     */
    @VTID(15)
    void setValueDouble(double iValue);

    /**
     * method SetValueStringCurve
     */
    @VTID(16)
    void setValueStringCurve(int x, java.lang.String iValue);

    /**
     * method SetValueDoubleCurve
     */
    @VTID(17)
    void setValueDoubleCurve(int x, double iValue);

    /**
     * method SetValueStringMap
     */
    @VTID(18)
    void setValueStringMap(int x, int y, java.lang.String iValue);

    /**
     * method SetValueDoubleMap
     */
    @VTID(19)
    void setValueDoubleMap(int x, int y, double iValue);

    /**
     * method SetAxisValueString
     */
    @VTID(20)
    void setAxisValueString(int iDim, int iIndex, java.lang.String iValue);

    /**
     * method SetAxisValueDouble
     */
    @VTID(21)
    void setAxisValueDouble(int iDim, int iIndex, double iValue);

    /**
     * method SetValueAscii
     */
    @VTID(22)
    void setValueAscii(java.lang.String iValue);

    /**
     * method GetValue
     */
    @VTID(23)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object getValue(int[] index);

    /**
     * method GetValueOfAxis
     */
    @VTID(24)
    @ReturnValue(type = NativeType.VARIANT)
    java.lang.Object getValueOfAxis(int dim, int index);
}
