package com.bosch.caltool.icdm.ws.rest.client.caldataimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportInput;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CalDataImportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class CalDataImportClientTest extends AbstractRestClientTest {

  private final static String filePath = "testdata/cdr/CDFX_Export_Report_1.cdfx";

  private final static String filePath1 = "testdata/cdr/CDFX_Export_Report.cdfx";
  /**
   * Expected exception
   */
  public final ExpectedException thrown = ExpectedException.none();


  /**
   * Test method for {@link com.bosch.caltool.icdm.CalDataImportClientTest.client.a2l.ParameterServiceClientTest#get()}
   */
  @Test
  public void testParseFile() {
    CalDataImportServiceClient servClient = new CalDataImportServiceClient();
    try {

      Set<String> filePathSet = new HashSet<>();
      filePathSet.add(filePath);
      filePathSet.add(filePath1);

      CalDataImportData ret = servClient.parsefile(filePathSet, "445674365", "FUNCTION", null);
      testOuput(ret);
      assertFalse("Response should not be null", (ret == null));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }
  }


  @Test
  public void testGetCalCompareObj() {
    CalDataImportServiceClient servClient = new CalDataImportServiceClient();
    try {

      CalDataImportInput data = new CalDataImportInput();
      Set<String> filePathSet = new HashSet<>();
      filePathSet.add(filePath);
      filePathSet.add(filePath1);
      CalDataImportData calDataImpData = servClient.parsefile(filePathSet, "445674365", "FUNCTION", null);
      data.setFuncId("445674365");
      data.setParamColType("FUNCTION");
      data.setCalDataImportData(calDataImpData);
      CalDataImportData ret = servClient.getCalDataCompareList(data);
      // testOuput(ret);
      assertFalse("Response should not be null", (ret == null));
    }
    catch (Exception excep) {
      LOG.error("Error in WS call", excep);
      assertNull("Error in WS call", excep);
    }

  }

  /**
   * Test method for {@link CalDataImportServiceClient#createParmandRules(CalDataImportInput)}
   * 
   * @throws ApicWebServiceException
   */
  @Test
  public void testCreateParmandRules() throws ApicWebServiceException {
    CalDataImportServiceClient servClient = new CalDataImportServiceClient();
    CalDataImportInput data = new CalDataImportInput();
    Set<String> filePathSet = new HashSet<>();
    filePathSet.add(filePath);
    filePathSet.add(filePath1);
    CalDataImportData calDataImpData = servClient.parsefile(filePathSet, "445674365", "FUNCTION", null);
    data.setFuncId("445674365");
    data.setParamColType("FUNCTION");
    data.setCalDataImportData(calDataImpData);
    CalDataImportSummary calSummary = servClient.createParmandRules(data);
    assertNotNull("Null object is returned", calSummary);
    assertEquals(4, calSummary.getTotalNoOfParams());
    assertEquals(0, calSummary.getSkippedParamsCount());
  }

  /**
   * @param ret
   */
  private void testOuput(final CalDataImportData ret) {
    assertNotNull("Response should not be null", ret);
    assertNotNull("Response  rule map should not be null", ret.getInputDataMap());

  }


}
