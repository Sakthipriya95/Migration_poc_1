/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cdrruleimporter.plausibel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.cdfparser.Cdfparser;
import com.bosch.calcomp.cdfparser.exception.CdfParserException;
import com.bosch.calcomp.cdftocaldata.factory.impl.CalDataModelAdapterFactory;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.jpa.CDMDataProvider;
import com.bosch.caltool.icdm.jpa.CDMSession;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.FeatureValueModel;


/**
 * @author imi2si
 */
public class CdfxPlausibelImporter {

  private final static ILoggerAdapter LOG = new Log4JLoggerAdapterImpl("C:\\Temp\\CdfxImporterLogger.txt");

  /**
   * Handler for the connection to the iCDM database
   */
  private ApicDataProvider attrDataProvider;
  private CDRDataProvider cdrDataProvider;

  /**
   * Handler for accessing the SSD-DB
   */
  private SSDServiceHandler ssdService;

  private List<FeatureValueModel> dependencyList;

  private String cdfxPath;

  public CdfxPlausibelImporter() {
    initJPA();
  }

  public CdfxPlausibelImporter(final String cdfxPath, final List<FeatureValueModel> dependencyList) {
    this.cdfxPath = cdfxPath;
    this.dependencyList = dependencyList;
    initJPA();
  }

  public void createRule() {
    Map<String, CalData> calDataMap = null;
    CalData calData = null;

    try {
      calDataMap = getCdfxFile();
    }
    catch (CdfParserException e) {
      LOG.error("Error when loading CDFx file");
      e.printStackTrace();
      return;
    }

    for (Entry<String, CalData> entry : calDataMap.entrySet()) {
      calData = entry.getValue();

      Map<String, CDRFuncParameter> cdrFuncParam = getCDRFuncParameters(calData.getShortName());

      for (Entry<String, CDRFuncParameter> cdrEntry : cdrFuncParam.entrySet()) {

        try {
          CDRRule rule = new CDRRule();
          String hintText = "Loaded via PlausibeL Importer";

          rule.setParameterName(cdrEntry.getValue().getName());
          rule.setLabelFunction(cdrEntry.getKey());
          rule.setValueType(cdrEntry.getValue().getType());
          rule.setRefValCalData(entry.getValue());
          rule.setReviewMethod("A");
          rule.setDependencyList(this.dependencyList);

          if (cdrEntry.getValue().getName().equalsIgnoreCase("AZKELDYN")) {
            System.out.println(cdrEntry.getKey());
            System.out.println(cdrEntry.getValue().getName());

            for (CDRRule ruleEntry : cdrEntry.getValue().getReviewRuleList()) {
              System.out.println("**********************************************");
              System.out.println(ruleEntry.getLabelFunction());
              System.out.println(ruleEntry.getReviewMethod());
              System.out.println(ruleEntry.getRefValueDCMString());
              for (FeatureValueModel model : ruleEntry.getDependencyList()) {
                //
                System.out.println("Feature ID: " + model.getFeatureId() + "\n" + "Value ID: " + model.getValueId());

              }
            }
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }

    }
  }

  private Map<String, CalData> getCdfxFile() throws CdfParserException {

    Cdfparser cdfParser = new Cdfparser();
    cdfParser.setLogger(LOG);

    ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
    cdfParser.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
    cdfParser.setTargetModelClassLoader(classLoader);
    LOG.info("Invoking cdf parser...");

    return cdfParser.parse(this.cdfxPath);
  }

  public Map<String, CDRFuncParameter> getCDRFuncParameters(final String paramName) {

    Map<String, CDRFuncParameter> allParams = this.cdrDataProvider.getCDRFuncParameters(paramName);

    return allParams;
  }

  private void initJPA() {
    try {
      LOG.info("initializing database connection ...");
      CDMSession.getInstance().setProductVersion("1.17.0");
      CDMSession.getInstance().initialize("WebService");

      LOG.info("getting the data provider ...");
      this.attrDataProvider = CDMDataProvider.getInstance().getApicDataProvider();
      this.cdrDataProvider = CDMDataProvider.getInstance().getCdrDataProvider();

      LOG.info("initializing the database updater ...");
    }
    catch (Exception e) {
      LOG.fatal("Database Connection couldn't be established", e);
      e.printStackTrace();
    }
  }

  private boolean objectsEqual(final Object object1, final Object object2) {
    boolean result;

    if ((object1 != null) && (object2 != null)) {
      // both objects are not NULL => compare them

      if (object1.getClass() != object2.getClass()) {
        result = false;
      }
      else if (object1 instanceof String) {
        result = object1.equals(object2);
      }
      else if (object1 instanceof BigDecimal) {
        result = ((BigDecimal) object1).compareTo((BigDecimal) object2) == 0;
      }
      else {
        LOG.error(object1.getClass() + " not supported in objectEqual!");
        result = false;
      }

    }
    else if ((object1 == null) && (object2 == null)) {
      // both objects are NULL => equal
      result = true;
    }
    else {
      // only one object is NULL => not equal
      result = false;
    }

    return result;
  }

  private boolean equalsFeatureValue(final List<FeatureValueModel> dependencyListSource,
      final List<FeatureValueModel> dependencyListTarget) {

    if (dependencyListSource.size() != dependencyListTarget.size()) {
      return false;
    }

    for (FeatureValueModel sourceEntry : dependencyListSource) {
      for (FeatureValueModel targetEntry : dependencyListTarget) {
        if (sourceEntry.getFeatureId().equals(targetEntry.getFeatureId()) &&
            sourceEntry.getValueId().equals(targetEntry.getValueId())) {
          continue;
        }
      }

      // One not existing feature/value combination is enough to leave the loop and recognizing equals = false
      return false;
    }

    return true;
  }

  public static void main(final String[] args) {
    CdfxPlausibelImporter importer =
        new CdfxPlausibelImporter("C:\\temp\\fun-DTECUOFFE_params_Set1-Updated.cdfx", null);
    importer.createRule();
  }
}
