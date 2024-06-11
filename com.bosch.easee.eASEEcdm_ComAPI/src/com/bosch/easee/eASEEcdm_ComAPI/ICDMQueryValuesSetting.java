package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMQueryValuesSetting Interface
 */
@IID("{A5547682-356B-47EC-A104-F8C6069C981E}")
public interface ICDMQueryValuesSetting extends Com4jObject {
    /**
     * property Raw
     */
    @VTID(7)
    boolean raw();

    /**
     * property Raw
     */
    @VTID(8)
    void raw(
        boolean pVal);

    /**
     * property IncludeBaseDataSets
     */
    @VTID(9)
    boolean includeBaseDataSets();

    /**
     * property IncludeBaseDataSets
     */
    @VTID(10)
    void includeBaseDataSets(
        boolean pVal);

    /**
     * property ParameterFilter
     */
    @VTID(11)
    com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection parameterFilter();

    @VTID(11)
    @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection.class})
    java.lang.Object parameterFilter(
        int index);

    /**
     * property ParameterFilter
     */
    @VTID(12)
    void parameterFilter(
        com.bosch.easee.eASEEcdm_ComAPI.ICDMCollection iParameterFilter);

}
