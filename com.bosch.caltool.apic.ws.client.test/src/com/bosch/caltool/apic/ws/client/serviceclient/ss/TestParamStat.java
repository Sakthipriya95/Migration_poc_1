/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Test;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calcomp.caldataanalyzer.vo.LabelValueInfoVO;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.apic.ws.client.ParameterStatisticCallbackHandler;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.apic.ws.client.serviceclient.AbstractSoapClientTest;


/**
 * @author ELM1COB
 **/
public class TestParamStat extends AbstractSoapClientTest {


  private final APICWebServiceClient stub = new APICWebServiceClient();


  /**
   * Test GetParameterStatistics service
   *
   * @throws Exception response error
   */
  @Test
  public void testGetParameterStatistics() throws Exception {
    String paramInput = "AC_pCritLo_C";
    LabelInfoVO stats = this.stub.getParameterStatistics(paramInput, new DummyProgressMonitor());

    assertNotNull(stats);

    assertEquals("param name equal", paramInput, stats.getLabelName());

    LOG.info(stats.getLabelName());
    LOG.info("Label: {}", stats.getLabelName());
    LOG.info("  number of values  : {}", stats.getNumberOfValues());
    LOG.info("  number of datasets: {}", stats.getSumDataSets());
    LOG.info("  min. value        : {}", stats.getMinValueSimpleDisplay());
    LOG.info("  max. value        : {}", stats.getMaxValueSimpleDisplay());
    LOG.info("  avg. value        : {}", stats.getAvgValueSimpleDisplay());
    LOG.info("  median value      : {}", stats.getMedianValueSimpleDisplay());
    LOG.info("  lower quartile    : {}", stats.getLowerQuartileValueSimpleDisplay());
    LOG.info("  upper quartile    : {}", stats.getUpperQuartileValueSimpleDisplay());
    LOG.info("  peak value        : {}", stats.getPeakValueSimpleDisplay());
    LOG.info("  peak value pct.   : {}%", stats.getPeakValuePercentage());

    for (LabelValueInfoVO labelValueInfo : stats.getValuesMap().values()) {
      LOG.info("  value: {}, unit: {}, datasets: {}", labelValueInfo.getCalDataPhy().getSimpleDisplayValue(),
          labelValueInfo.getCalDataPhy().getUnit(), labelValueInfo.getFileIDList().size());
    }
  }


  /**
   * Detailed tests for getParameterStatistics service
   * 
   * @throws Exception error in service call
   */
  @Test
  public void testGetParameterStatistics2() throws Exception {
    LabelInfoVO labelInfo;
    Map<Long, LabelValueInfoVO> allValues;
    CalDataPhyValue curValue;
    int rowNum = 0;


    labelInfo = this.stub.getParameterStatistics("APP_tiLowPosPT1_C", null);
    allValues = labelInfo.getValuesMap();
    assertTrue("Number of records for label APP_tiLowPosPT1_C: " + allValues.size() + ". Exceptet : >=48",
        allValues.size() >= 48);

    TreeSet<LabelValueInfoVO> sortedSet = new TreeSet<>((o1, o2) -> {
      Integer order = Integer.valueOf(o1.getFileIDList().size()).compareTo(Integer.valueOf(o2.getFileIDList().size()));
      order = (order == 0 ? -1 : order);

      return order.intValue();
    });

    sortedSet.addAll(allValues.values());

    for (LabelValueInfoVO entry : sortedSet) {
      rowNum++;
      curValue = (CalDataPhyValue) entry.getCalDataPhy();
      LOG.debug("Value " + rowNum + ": " + curValue.getAtomicValuePhy().getSValue() + "; Number of Datasets: " +
          entry.getFileIDList().size());
    }

    // Verify sorting
    assertTrue("Number of records for label APP_tiLowPosPT1_C: " + sortedSet.size() + ". Exceptet : >=48",
        allValues.size() >= 48);

  }

  /**
   * ICDM-218. Test GetParameterStatisticsExt service
   *
   * @throws Exception response error
   */
  @Test
  public void testGetParameterStatisticsExt() throws Exception {
    List<String> paramInputList = Arrays.asList("AirMod_FacFilDmCyl2ExhMnfld_T", "CWBGLWM", "CWBGLWM2", "CWPABEF",
        "DIPABERGVP", "DMSSRKRPV", "DPABEFO", "DPABEFU", "DPBRINTTN", "DPBRINTTX", "DWGVLIMFE", "EXP1DKAPPA",
        "EXPKAPPA", "EXPTERMKAP", "FAFBRKRVP", "FAFRSPVP", "FFUPSRLTN", "FFUPSRLTX", "FIMXFADK", "FIMXVP", "FTMSDV",
        "FUPSRLMN", "FUPSRLMX", "FVBR", "KFDPBRMXWE", "KFDPBRWE", "KFIPABERG", "KFIPABESC", "MSNBRKR", "MSNRIRSP",
        "PBRINTMN", "PBRINTMX", "RIRINTGV");

    CalData[] resultCalDatas =
        this.stub.getParameterStatisticsExt(paramInputList.toArray(new String[paramInputList.size()]), null);
    assertNotNull("Response not null", resultCalDatas);
    assertTrue("Response is not empty", resultCalDatas.length > 0);

    for (CalData resultCalData : resultCalDatas) {
      String paramName = resultCalData.getShortName();
      assertTrue("verify with input", paramInputList.contains(paramName));
      LOG.info("Label : {} , value : {}", paramName, resultCalData.getCalDataPhy().getSimpleDisplayValue());
    }
  }


  /**
   * test getParameterStatisticsFile service
   *
   * @throws Exception service error
   */
  @Test
  public void testGetParameterStatisticsFile() throws Exception {

    String statusForAsyncExecutionResponse = this.stub.getParameterStatisticsFile("AC_pCritLo_C");

    assertNotNull(statusForAsyncExecutionResponse);

    LOG.info("\nasync status : {}", statusForAsyncExecutionResponse);
  }

  /**
   * test GetParameterStatisticsExtAsync service
   *
   * @throws Exception service error
   */
  @Test
  public void testGetParameterStatisticsExtAsync() throws Exception {

    String[] parameterNames = new String[] {
        "DSM_ClaDfp_Gearbx_Dia1_C",
        "LSUCtl_dcycHtMin_C",
        "DSM_Class24Dlc_C",
        "CrCtl_tiGearOvrRun_C",
        "DSM_DInDfp_LSUMonDiff_C",
        "DSM_EnvDfp_RailMeUn1_C",
        "DSM_Class28DlcKd_C",
        "DSM_Class9DlcPen_C",
        "DSM_CDKDfp_FrmMng_BRK_TO_C",
        "NSC_stLmbdNOxLoUpStp_C",
        "FrmMng_dvolFlConsumSlp_C",
        "TrbChLPDyc_ATS.CnvOfs_C",
        "GlwCtl_nPre_C",
        "Shuttr_rOpen_C" };

    ParameterStatisticCallbackHandler callbackHandler =
        (ParameterStatisticCallbackHandler) this.stub.getParameterStatisticsExtAsync(parameterNames);
    while (!callbackHandler.isParameterAvailable() && !callbackHandler.isBroken()) {
      Thread.sleep(1000);
      LOG.info("   Progress {}%", this.stub.getStatusForAsyncExecutionResponse(callbackHandler.getSessionID()));
    }

    assertFalse("Error while recieving data", callbackHandler.isBroken());
    CalData[] cd = callbackHandler.getFiles();
    assertTrue("Response not empty", cd.length > 0);

    LOG.info("Parameters received:");
    for (CalData element : cd) {
      LOG.info("  {}", element.getCalDataPhy().getName());
    }

  }


}
