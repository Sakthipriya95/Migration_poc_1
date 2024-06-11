/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PIDCVersionReport;
import com.bosch.caltool.icdm.model.apic.pidc.SubVariantReport;
import com.bosch.caltool.icdm.model.apic.pidc.VariantReport;


/**
 * ICDM-1712 Class used to load PIDC related data
 *
 * @author jvi6cob
 */
public class PIDCDataLoader {


  /**
   * Attribute level
   */
  private static final int ATTR_LEVEL_FIVE = 5;
  /**
   * Time size for report calculation
   */
  private static final int TIME_SIZE_THOUSAND = 1000;
  private static final String JSON_UNDEFINED_VALUE = "<???>";
  private static final String JSON_NOT_USED_VALUE = "<NOT_USED>";
  private static final String JSON_USED_VALUE = "<USED>";
  private static final String JSON_HIDDEN_VALUE = "HIDDEN";

  private final ApicDataProvider attrDataProvider;
  /**
   * apic USer from request
   */
  private final ApicUser user;

  /**
   * Constructor
   *
   * @param attrDataProvider ApicDataProvider
   * @param user ApicUser
   */
  public PIDCDataLoader(final ApicDataProvider attrDataProvider, final ApicUser user) {
    this.attrDataProvider = attrDataProvider;
    this.user = user;
  }

  /**
   * Method which fetches all PIDCVersions and their attributes
   *
   * @return PIDCVersion's and their mapped attributes
   */
  public Map<String, Map<String, String>> doFetchAllPIDC() {

    Map<String, Map<String, String>> pidcDetailMap = new TreeMap<>();
    Collection<PIDCVersion> dbProjectIdVersions = this.attrDataProvider.getAllActivePIDCVersions().values();

    for (PIDCVersion pidcVersion : dbProjectIdVersions) {
      String pidcVerName = pidcVersion.getName();
      Map<String, String> pidcDetails = pidcDetailMap.get(pidcVerName);
      if (pidcDetails == null) {
        pidcDetails = new HashMap<>();
        pidcDetailMap.put(pidcVerName, pidcDetails);
      }
      Collection<PIDCAttribute> allPIDCAttr = pidcVersion.getAttributesAll().values();
      for (PIDCAttribute pidcAttr : allPIDCAttr) {
        if (pidcAttr.getID() != null) {
          pidcDetails.put(pidcAttr.getName(), extractValue(pidcAttr));
        }
      }
    }
    return pidcDetailMap;
  }

  /**
   * Method which returns PIDCVERSION level attributes,VARIANT level attributes and SUB-VARIANT level attributes
   *
   * @param pidcVersionID Long
   * @param apicUser apicUser
   * @return PIDCReport
   */
  public PIDCVersionReport fetchPIDCVersion(final Long pidcVersionID) {
    long startTime = System.currentTimeMillis();

    PIDCVersion pidcVersion = null;
    try {
      pidcVersion = this.attrDataProvider.getPidcVersion(pidcVersionID, true);
    }
    catch (DataException e) {
      getLogger().error("PIDC version ID" + String.valueOf(pidcVersionID) + " could not be loaded", e);
    }

    PIDCVersionReport pidcReport = constructPIDCReport(pidcVersion);

    long endTime = System.currentTimeMillis();
    getLogger()
        .debug("PIDC Report generation COMPLETED in " + ((endTime - startTime) / TIME_SIZE_THOUSAND) + " seconds");
    return pidcReport;
  }

  /**
   * @param pidcVersion
   * @param passWord
   * @param userName
   * @return
   */
  private PIDCVersionReport constructPIDCReport(final PIDCVersion pidcVersion) {


    PIDCVersionReport pidcReport = new PIDCVersionReport();

    if (!pidcVersion.isHidden(this.user)) {
      pidcReport.setProjectName(pidcVersion.getPidcName());
      pidcReport.setProjectDescription(pidcVersion.getPidc().getDescription());
      pidcReport.setPidcID(pidcVersion.getPidc().getID());
      pidcReport.setPidcVersion(pidcVersion.getPidcVersionName());
      pidcReport.setPidcVersionID(pidcVersion.getID());


      Comparator<PIDCAttribute> customPIDCReportComparator = new Comparator<PIDCAttribute>() {

        @Override
        public int compare(final PIDCAttribute pidcAttr1, final PIDCAttribute pidcAttr2) {
          // To make level attributes come first, normal attributes (i.e attributes with level null) and APRJ and SDOM
          // PVer attributes(i.e attributes with level less than 0) are assigned a level 5 and then compared. 5 is
          // assigned since highest level as of writing this code is 4.
          Integer pidcAttrVal1 = Integer.valueOf(pidcAttr1.getAttribute().getAttrLevel());
          Integer pidcAttrVal2 = Integer.valueOf(pidcAttr2.getAttribute().getAttrLevel());
          pidcAttrVal1 = pidcAttrVal1 == 0 ? ATTR_LEVEL_FIVE : (pidcAttrVal1 < 0 ? ATTR_LEVEL_FIVE : pidcAttrVal1);
          pidcAttrVal2 = pidcAttrVal2 == 0 ? ATTR_LEVEL_FIVE : (pidcAttrVal2 < 0 ? ATTR_LEVEL_FIVE : pidcAttrVal2);

          int result = pidcAttrVal1.compareTo(pidcAttrVal2);
          if (result == 0) {
            result = pidcAttr1.compareTo(pidcAttr2);
          }
          return result;
        }
      };
      Collection<PIDCAttribute> allPIDCAttrColl = pidcVersion.getAttributesAll().values();
      List<PIDCAttribute> allPIDCAttr = new ArrayList<>(allPIDCAttrColl);
      Collections.sort(allPIDCAttr, customPIDCReportComparator);
      Map<String, String> pidcAttrMap = new LinkedHashMap<>();

      fillPIDCAttrMap(pidcAttrMap, allPIDCAttr);

      constructVariantReport(pidcVersion, pidcReport);

      pidcReport.setPidcAttrMap(pidcAttrMap);
    }
    return pidcReport;
  }

  /**
   * @param pidcAttrMap Map<String, String>
   * @param allPIDCAttr List<PIDCAttribute>
   */
  private void fillPIDCAttrMap(final Map<String, String> pidcAttrMap, final List<PIDCAttribute> allPIDCAttr) {
    for (PIDCAttribute pidcAttr : allPIDCAttr) {
      if ((pidcAttr.getID() == null) || !pidcAttr.isVariant()) {
        pidcAttrMap.put(pidcAttr.getName(), extractValue(pidcAttr));
      }
    }
  }


  /**
   * @param pidcVersion
   * @param pidcReport
   * @param user
   */
  private void constructVariantReport(final PIDCVersion pidcVersion, final PIDCVersionReport pidcReport) {
    Collection<PIDCVariant> variants = pidcVersion.getVariantsMap(false).values();
    for (PIDCVariant pidcVariant : variants) {
      VariantReport variantReport = new VariantReport();
      variantReport.setName(pidcVariant.getName());
      variantReport.setDescription(pidcVariant.getDescription());
      variantReport.setVariantID(pidcVariant.getVariantID());


      Map<String, String> varAttrMap = new HashMap<>();
      Collection<PIDCAttributeVar> varAttrMapICDM = pidcVariant.getAttributes(true, false).values();


      for (PIDCAttributeVar varAttr : varAttrMapICDM) {
        if ((varAttr.getID() == null) || !varAttr.isVariant()) {
          varAttrMap.put(varAttr.getName(), extractValue(varAttr));
        }
      }

      constructSubVariantReport(pidcVariant, variantReport);

      variantReport.setPidcVarAttrMap(varAttrMap);
      if (pidcReport.getVariants() == null) {
        pidcReport.setVariants(new ArrayList<VariantReport>());
      }
      pidcReport.getVariants().add(variantReport);
    }
  }

  /**
   * @param pidcVariant
   * @param variantReport
   * @param user
   */
  private void constructSubVariantReport(final PIDCVariant pidcVariant, final VariantReport variantReport) {
    Collection<PIDCSubVariant> subVariants = pidcVariant.getSubVariantsMap(false).values();
    for (PIDCSubVariant pidcSubVariant : subVariants) {
      SubVariantReport subVariantReport = new SubVariantReport();
      subVariantReport.setName(pidcSubVariant.getName());
      subVariantReport.setDescription(pidcSubVariant.getDescription());
      subVariantReport.setsVariantID(pidcSubVariant.getSubVariantID());

      Map<String, String> subVarAttrMap = new HashMap<>();
      Collection<PIDCAttributeSubVar> subVarAllAttr = pidcSubVariant.getAttributes(true, false).values();
      for (PIDCAttributeSubVar svarAttr : subVarAllAttr) {
        if ((svarAttr.getID() == null) || !svarAttr.isVariant()) {
          subVarAttrMap.put(svarAttr.getName(), extractValue(svarAttr));
        }
      }
      subVariantReport.setPidcSubVarAttrMap(subVarAttrMap);
      if (variantReport.getSubvariants() == null) {
        variantReport.setSubvariants(new ArrayList<SubVariantReport>());
      }
      variantReport.getSubvariants().add(subVariantReport);
    }
  }

  /**
   * @param ipidcAttribute
   * @param user
   * @return
   */
  private String extractValue(final IPIDCAttribute ipidcAttribute) {
    String value = "";
    String usedStr = ipidcAttribute.getIsUsed();
    if (usedStr.equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) {
      value =
          ipidcAttribute.getAttributeValue() != null ? !ipidcAttribute.getAttributeValue().getValue().trim().isEmpty()
              ? ipidcAttribute.getAttributeValue().getValue() : JSON_USED_VALUE : JSON_USED_VALUE;
    }
    else if (usedStr.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType())) {
      value = JSON_NOT_USED_VALUE;
    }
    else if (usedStr.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType())) {
      value = JSON_UNDEFINED_VALUE;
    }

    if (ipidcAttribute.isHiddenToUser(this.user)) {
      value = JSON_HIDDEN_VALUE;
    }
    return value;
  }

  /**
   *
   */
  private ILoggerAdapter getLogger() {
    return this.attrDataProvider.getLogger();
  }
}
