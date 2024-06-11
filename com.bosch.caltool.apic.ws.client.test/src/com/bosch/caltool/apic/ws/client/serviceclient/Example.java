/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.apic.ws.client.APICStub.VcdmLabelStats;
import com.bosch.caltool.apic.ws.client.pidcsearch.PidcSearchCondition;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;


/**
 * @author imi2si
 */
public class Example {

  public static void main(final String args[]) {


    /*
     * APICWebServiceClient x = new APICWebServiceClient(APICWebServiceClient.APICWsServer.ICDM_01_SERVER); ValueList[]
     * y; try { y = x.getAttrValues(new long[] { 249280L }); for (ValueList vl : y) { for (AttributeValue atv :
     * vl.getValues()) { System.out.println(atv.getClearingStatus()); } } } catch (Exception e1) { e1.printStackTrace();
     * }
     */

    APICWebServiceClient stub = new APICWebServiceClient(APICWebServiceClient.APICWsServer.LOCAL_SERVER);

    try {

      // ProjectIdCardWithVersion pidcForVersion = stub.getPidcForVersion(771743215L);
      // System.out.println(pidcForVersion.getProjectIdCardDetails().getName());
      // for (long x = 0L; x < 133L; x++) {

      // Old Call

      // stub.getPidcScoutResults(null);

      // stub.getAllPidcVersion();
      // PidcDiffAttrReportOutput diffAttrOutput =
      // new PidcDiffAttrReportOutput(stub.getPidcAttrDiffReport(352717L, 0L, -1L));
      // diffAttrOutput.createOutput();
      // System.out.println(diffAttrOutput.getOutput());

      // for (ProjectIdCardInfoType pidc : stub.getAllPidcVersion()) {
      // System.out.println(pidc.getName());
      // System.out.println(pidc.getVersionNumber());
      // }

      // PidcActiveVersionResponseType pidcActiveVersionId = stub.getPidcActiveVersionId(769584967L);
      // System.out.println(pidcActiveVersionId.getPidcVersionId());

      VcdmLabelStats[] vcdmLabelStats = stub.getVcdmLabelStats(770816168l, 300);

      for (VcdmLabelStats vcdmLabelStat : vcdmLabelStats) {
        System.out.println(vcdmLabelStat.getAprjName());
      }

      Set<PidcSearchCondition> searchConditions = new HashSet<>();

      // Example for a condition. Pass the attribute ID in the constructor.
      // PidcSearchCondition condition = new PidcSearchCondition(84);
      // Pass the value ID. The method van be called several times with different values.
      // All passed values will be considered for the Search
      // condition.addValue(96);

      // Add the condition to the map
      // searchConditions.add(condition);

      try {
        // Call the webservice. The matching PIDC-IDs for the Serach condition will be returned in a long array.
        // PidcScoutVersResponseType pidcScoutResp = stub.getPidcScoutResultForVersionExt(searchConditions, true);
        // System.out.println("-----------------------" + pidcScoutResp.getPidcIds().length + "---------");
      }

      catch (Exception e) {

        assertNull("Error when showing PIDC Scout result", e);
      }
      // for (AttrDiffType entry : stub.getPidcAttrDiffReport(758553267L, 0L,
      // 135L).getAttrDiffResponse().getDifferences()) {
      // System.out.println(new StringBuilder().append("Level: ").append(entry.getLevel()).append("Attribute: ")
      // .append(entry.getAttribute().getNameE()).append("Changed Item: ").append(entry.getChangedItem())
      // .append("Old Value: ").append(entry.getOldValue()).append("New Value: ").append(entry.getNewValue())
      // .append("Modified by: ").append(entry.getModifiedName()).append("Modified on: ")
      // .append(new SimpleDateFormat().format(entry.getModifiedOn().getTime())).append("Version: ")
      // .append(entry.getVersionId()).toString());
      // }


      // }


      // ProjectIdCardInfoType[] allPidc = stub.getAllPidc();

      // System.out.println(stub.getWebServiceVersion());
      // ProjectIdCardType response = stub.getPidcDetails(2747L);
      // System.out.println(response.getProjectIdCardDetails().getClearingStatus());
      // stub.getParameterStatisticsFile("KFKE0");

      /*
       * APICClientAsyncThread x = new APICClientAsyncThread(new String[] { "Test_with_a_nonsense_parameter" }, "x",
       * APICWebServiceClient.APICWsServer.LOCAL_SERVER); x.start(); while (!x.getFinished()) { try {
       * Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
       * System.out.println("Returned Message: " + x.getCallbackHandler().getParameterException().getMessage()); }
       */

      // Set<PidcSearchCondition> x = new HashSet<PidcSearchCondition>();
      // stub.getPidcScoutResults(x);
      /*
       * stub.getPidcStatisticResult("C:\\Temp\\TextXYZ.xlsx"); ProjectIdCardType pidc = stub.getPidcDetails((long)
       * 699017); for (AttributeWithValueType entry : pidc.getAttributes()) { if (entry.getAttribute().getId() == 2801)
       * { System.out.println("1. SOP " + entry.getUsed()); System.out.println("1. SOP " +
       * entry.getValue().getValueE()); } }
       */

    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    /*
     * APICWebServiceClient x = new APICWebServiceClient(APICWebServiceClient.APICWsServer.ICDM_01_SERVER); for (int y =
     * 1; y < 5000; y++) { try { System.out.print(y); x.getPidcDiffs(6739L, 1L, -1L); } catch (Exception e1) { // TODO
     * Auto-generated catch block e1.printStackTrace(); } }
     */


    /*
     * Map<String, CalData> localFiles = new HashMap<>(); Map<String, CalData> remoteFiles = new HashMap<>(); String
     * labels[] = { "UBSQBKVMX" }; DefaultSeriesStatisticsFilter filter = new DefaultSeriesStatisticsFilter("ABKVP",
     * ISeriesStatisticsFilter.DataType.FUNCTION_FILTER, ISeriesStatisticsFilter.ValueType.EQUALS_VALUE, new
     * AtomicValuePhy("100.21")); APICClientAsyncThread localResult = new APICClientAsyncThread(labels, "Local",
     * APICWebServiceClient.APICWsServer.LOCAL_SERVER, new DefaultSeriesStatisticsFilter[] { filter });
     * localResult.start(); while (!localResult.getFinished()) { try { Thread.sleep(1000); } catch (InterruptedException
     * e) { // TODO Auto-generated catch block e.printStackTrace(); } } try { System.out.println(((CalDataPhyValue)
     * localResult.getFiles()[0].getCalDataPhy()).getValue().getSValue()); } catch (ClassNotFoundException | IOException
     * e) { // TODO Auto-generated catch block }
     */
  }
}
