package com.bosch.easee.eASEEcdm_ComAPI  ;

import com4j.*;

/**
 * ICDMTestSuite Interface
 */
@IID("{6A69774B-4D5F-4FB0-B83B-ACB24D38FEB1}")
public interface ICDMTestSuite extends Com4jObject {
    /**
     * method StartTestCase
     */
    @VTID(7)
    void startTestCase(
        java.lang.String iTestCaseFile,
        Holder<java.lang.String> oLogFile,
        Holder<Integer> lSubCases,
        Holder<Integer> lSubCasesFailed);

    /**
     * method GetModuleInfo
     */
    @VTID(8)
    java.lang.String getModuleInfo();

}
