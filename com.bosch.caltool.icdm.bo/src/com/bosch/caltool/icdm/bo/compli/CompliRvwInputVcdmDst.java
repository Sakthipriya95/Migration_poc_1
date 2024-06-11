/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetLoader;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMProgramkey;

/**
 * @author nip4cob
 */
public class CompliRvwInputVcdmDst {

  private final CompliReviewInputMetaData compliReviewInputMetaData;
  /**
   * Key - hex file index, value dst id
   */
  private Map<String, Long> hexFileDstIdMap;
  private final ServiceData serviceData;

  /**
   * @param complianceReportInputModel complianceReportInputModel
   * @param serviceData serviceData
   */
  public CompliRvwInputVcdmDst(final CompliReviewInputMetaData complianceReportInputModel,
      final ServiceData serviceData) {
    this.compliReviewInputMetaData = complianceReportInputModel;
    this.serviceData = serviceData;
  }

  /**
   *
   */
  public void fillDstMap() {
    List<VCDMApplicationProject> vcdmAppList =
        new VcdmDataSetLoader(this.serviceData).getDataSets(this.compliReviewInputMetaData.getPverName(),
            this.compliReviewInputMetaData.getPverVariant(), this.compliReviewInputMetaData.getPverRevision());


    this.hexFileDstIdMap = new HashMap<>();

    for (VCDMApplicationProject appProj : vcdmAppList) {
      Map<String, VCDMProductKey> vcdmVariants = appProj.getVcdmVariants();

      // dst name template --> 005-AUDI-EA888-GEN3BZ : MQB_1628_A0HRa_EU_OPF_Polo . DMG1111A01C1628_MY19D00_withGroups
      // ; 1
      for (Entry<Long, String> dstEntrySet : this.compliReviewInputMetaData.getDstMap().entrySet()) {
        String programKeyName = dstEntrySet.getValue()
            .substring(dstEntrySet.getValue().indexOf('.') + 1, dstEntrySet.getValue().indexOf(';')).trim();
        String varName = dstEntrySet.getValue()
            .substring(dstEntrySet.getValue().indexOf(':') + 1, dstEntrySet.getValue().indexOf('.')).trim();
        String dstRevision = dstEntrySet.getValue().substring(dstEntrySet.getValue().indexOf(';') + 1).trim();

        VCDMProductKey productKey = vcdmVariants.get(varName);
        if (productKey == null) {
          continue;
        }
        VCDMProgramkey programKey = productKey.getProgramKeys().get(programKeyName);
        if (null != programKey) {
          VCDMDSTRevision vcdmDstRevision = programKey.getvCDMDSTRevisions().get(new BigDecimal(dstRevision));
          this.hexFileDstIdMap.put(dstEntrySet.getKey().toString(), vcdmDstRevision.getDstID());
        }
      }
    }
  }


  /**
   * @return the hexFileDstIdMap
   */
  public Map<String, Long> getHexFileDstIdMap() {
    return this.hexFileDstIdMap;
  }

}
