package com.bosch.caltool.icdm.cdrruleimporter.plausibel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.cdfparser.Cdfparser;
import com.bosch.calcomp.cdfparser.exception.CdfParserException;
import com.bosch.calcomp.cdftocaldata.factory.impl.CalDataModelAdapterFactory;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.cdr.jpa.bo.CDRFunction;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.cdrruleimporter.Activator;
import com.bosch.caltool.icdm.cdrruleimporter.filerepresentation.LabelsWoFunctionsCDRRuleAdapter;
import com.bosch.caltool.icdm.cdrruleimporter.filerepresentation.LabelsWoFunctionsFileRepresentation;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.ExcelFile;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.caltool.icdm.excelimport.ValueGrid;
import com.bosch.caltool.icdm.jpa.CDMDataProvider;
import com.bosch.caltool.icdm.jpa.CDMSession;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDMessage;

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

/**
 * Loads CalibrationDataRules out of an Excel file into iCDM
 */
public class PlausibelImporter {

  /**
   * Database provider for getting the functions and parameters existing in iCDM
   */
  private ApicDataProvider attrDataProvider;
  private CDRDataProvider cdrDataProvider;

  /**
   * Logger for loading CDR Rules
   */
  private CDMLogger logger;

  /**
   * Handler for accessing the SSD-DB
   */
  private SSDServiceHandler ssdService;


  private static final String LOG_FILE_NAME = "c:/temp/PlausibelImporter.log";


  public static final String COL_1_APPL = "Applikationsreihenfolge";
  public static final String COL_2_PARAM = "Parameter";
  public static final String COL_3_KURZB = "Kurzbeschreibung";
  public static final String COL_4_WIEAPPL = "Wie appliziert?";
  public static final String COL_5_OPTE = "optionale Erläuterungen";
  public static final String COL_6_ERFWERTE = "Erfahrungswerte";
  public static final String COL_7_PRUFWERTU = "Prüfwert untere Grenze";
  public static final String COL_8_PRUFWERTO = "Prüfwert obere Grenze";
  public static final String COL_9_PRUFWEISE = "Prüfweise";
  public static final String COL_10_PRUEFSTAND = "Prüfstand";
  public static final String COL_11_EFFEKTE = "Effekte";
  public static final String COL_12_EINZEL = "Einzel-Abbruchkriterien";
  public static final String COL_13_ABKRIT = "Globale Abbruchkriterien";
  public static final String COL_14_CLASS = "Class";

  private final PrintWriter logWriter;

  /**
   * Default constructor, that inits the logger
   *
   * @throws IOException
   */
  public PlausibelImporter() throws IOException {
    initLogger();
    this.logger.setLogLevel(ILoggerAdapter.LEVEL_DEBUG);

    this.logWriter = new PrintWriter(new FileWriter(new File(LOG_FILE_NAME)), true);
  }

  /**
   * Starts the file import for a <b>complete directory</b>. The method tries to load every file that is found in the
   * given folder. Each file must match these preconditions:<br>
   * <ul>
   * <li>File has to contain <b>one</b> sheet with the function name. The syntax of the sheet name is either
   * '<percentage_sign><functionname>' or '<functionname><percentage_sign>'. If more than one sheets matches this name,
   * the first one is taken into account</li>
   * <li>Values to input starting at row 3. The first two rows are headers.</li>
   * <li>Column 2 contains the parameter, which is the unique key. That means, only if this cell is filled, the row will
   * be considered</li>
   * </ul>
   *
   * @param path the path of the folder that contains the excel xls-files to load (for example C:/Temp/)
   * @param overwriteExisting true, if already existing rules should be overwritten. False, if just new rules should be
   *          added without overwriting existing ones.
   * @throws IOException
   */
  public void startImport(final String path, final boolean overwriteExistingRules) {
    ExcelFile excelFile;

    initJPA();

    // The files of the given path
    ArrayList<ExcelFile> files = (ArrayList<ExcelFile>) getAllFiles(path);

    for (int loop = 0; loop < files.size(); loop++) {
      excelFile = files.get(loop);

      this.logWriter.println("============================================================================");
      this.logWriter.println("Importing Review Rules from " + excelFile.getFileName());
      this.logWriter.println("  Function: " + excelFile.getFunctionName());

      try {
        start(excelFile.getFunctionName(), excelFile, excelFile.getSheetName(), overwriteExistingRules);

        this.logWriter.println("Importing finished from " + excelFile.getFileName());
        this.logWriter.println("============================================================================");
      }
      catch (Exception e) {
        this.logger.error("Importing aborted for file " + excelFile.getFileName(), e);
        this.logWriter.println("Importing aborted for file " + excelFile.getFileName() + ":" + e.getLocalizedMessage());
        this.logWriter.println("============================================================================");
      }

      this.logWriter.flush();
    }

    this.logWriter.close();
  }


  /**
   * Starts the file import for a <b>single</b> file. Import of values is started from the given sheet name at line 3.
   * Startline is fix because a unique template is used to load data.
   *
   * @param functionName the name of the function in iCDM that should be updated
   * @param fileName the URL of the file that should be loaded, for example C:/Temp/FileToLoad.xls
   * @param excelSheetName the name of the excel sheet that contains the data to be loaded
   */
  public void startImport(final String functionName, final String fileName, final String excelSheetName) {
    initJPA();

    /*
     * Examples for the parameters functionName = new String("BGLWM"); fileName = new
     * String("C:/Archiv/Labelliste_BGLWM_Kai.xls"); excelSheetName = new String("BGLWM");
     */

    // start(functionName, new ExcelFile(fileName, excelSheetName, this.logger), excelSheetName, true);
    startTestNewLabelList();
  }

  public void startImport() {
    initJPA();
    startImport("C:\\temp\\Label_Niehr", true);


    // startTestNewLabelList();

    /*
     * Examples for the parameters functionName = new String("BGLWM"); fileName = new
     * String("C:/Archiv/Labelliste_BGLWM_Kai.xls"); excelSheetName = new String("BGLWM");
     */


  }

  public void createRuleObjects() {
    initJPA();
    try {

      // pass a logger object and the path to the Plausibel file
      PlausibelFile file =
          new PlausibelFile(this.logger, "U:\\Files_For_Testcases\\Plausibel\\Labelliste_BGLWM_Kai.xls");

      // pass the above created file and a CDRDataProvider
      PlausibelCDRRules rules = new PlausibelCDRRules(file, this.cdrDataProvider);

      // get the rules
      List<CDRRule> plausibelRules = rules.getRules();


      for (CDRRule entry : rules.getRules()) {
        System.out.println("*******************************************");
        System.out.println("Parameter Name: " + entry.getParameterName());
        System.out.println("Reference Value Display String: " + entry.getRefValueDispString());
        System.out.println("Unit: " + entry.getUnit());
        System.out.println("Hint: " + entry.getHint());
      }

      // pass the above created file and a CDRDataProvider
      PlausibelClasses cl = new PlausibelClasses(file);

      // get the rules
      Map<String, String> classes = cl.getClasses();

      for (Entry<String, String> entry : classes.entrySet()) {
        System.out.println("Parameter: " + entry.getKey() + "; " + entry.getValue());
      }


      // startImport("C:\\Temp\\Import", true);
    }
    catch (IOException e) {

      e.printStackTrace();
    }

    // startTestNewLabelList();

    /*
     * Examples for the parameters functionName = new String("BGLWM"); fileName = new
     * String("C:/Archiv/Labelliste_BGLWM_Kai.xls"); excelSheetName = new String("BGLWM");
     */


  }

  public void startReport() {
    CDMSession.getInstance().setProductVersion("1.18.0");
    initJPA();

    List<CDRRule> rules = this.attrDataProvider.getParamCDRRules("UEGO_tiDurnRpcFinish_C");
    Map<String, CDRFuncParameter> param = this.cdrDataProvider.getCDRFuncParameters("UEGO_tiDurnRpcFinish_C");

    for (Entry<String, CDRFuncParameter> entry : param.entrySet()) {

      List<CDRRule> x = entry.getValue().getReviewRuleList();


      for (CDRRule cdrRule : entry.getValue().getRulesSet()) {
        System.out.println("New Rule");
        System.out.println(cdrRule.getDependenciesForDisplay());
        System.out.println(cdrRule.getLabelFunction());
        System.out.println(cdrRule.getLowerLimit());
        System.out.println(cdrRule.getUpperLimit());
      }
    }
  }

  public void startImport(final String cdfxPath) {
    Cdfparser cdfParser = new Cdfparser();
    cdfParser.setLogger(this.logger);
    ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
    cdfParser.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
    cdfParser.setTargetModelClassLoader(classLoader);
    this.logger.info("Invoking cdf parser...");

    try {
      Map<String, CalData> calData = cdfParser.parse(cdfxPath);


    }
    catch (CdfParserException e) {
      e.printStackTrace();
    }
  }

  private List<TParameter> getParameter(final String paramName) {
    final StringBuilder query = new StringBuilder();
    // Build query string
    query.append("SELECT param from TParameter param where param.name = '" + paramName + "'");

    final EntityManager entMgr = ObjectStore.getInstance().getEntityManager();


    final TypedQuery<TParameter> typeQuery = entMgr.createQuery(query.toString(), TParameter.class);
    final List<TParameter> paramList = typeQuery.getResultList();

    return paramList;
  }

  private void setParameter(final TParameter param) {

    final EntityManager entMgr = ObjectStore.getInstance().getEntityManager();

    entMgr.getTransaction().begin();
    entMgr.persist(param);
    entMgr.getTransaction().commit();
  }

  private void setLabelClassAndName(final LabelsWoFunctionsCDRRuleAdapter label) {
    for (TParameter param : getParameter(label.getParameterName())) {
      System.out.println(param.getPclass());
      if (label.getPClass() != "-") {
        param.setPclass(label.getPClass());
        System.out.println(param.getPclass());
        setParameter(param);
      }
    }
  }

  private void startTestNewLabelList() {

    ExcelFile excel = new ExcelFile("C:\\temp\\Labelliste_ExhMod_DewDetRels.xls", "%ExhMod_DewDetRels", this.logger);
    LabelsWoFunctionsFileRepresentation labels =
        new LabelsWoFunctionsFileRepresentation(excel, this.cdrDataProvider, true, true, 16);
    List<CDRRule> rules = labels.getCDRRules(true, true);

    // For Testing: Label KFFTPKR in Function KRKE
    for (CDRRule rule : rules) {
      this.logger.info("Tyring to create rule for parameter " + rule.getParameterName() + " in function " +
          rule.getLabelFunction());

      setLabelClassAndName((LabelsWoFunctionsCDRRuleAdapter) rule);

      createRuleInSSD(rule, true);
    }
  }

  private void startTestParam() {

    String label = "DLAHTRMON";
    String function = "BGLSUOFFS";
    SSDMessage message = null;

    CDRRule rule = new CDRRule();
    rule.setParameterName(label);
    rule.setRefValueDCMString("GRUPPENKENNLINIE ASDrf_CoefAGripNeg_CUR 2 EINHEIT_X \"-\" EINHEIT_W \"1/(Nm*s)\" ST/X 1.000 2.860 WERT 1.00 1.00 END");
    rule.setDcm2ssd(true);
    rule.setLowerLimit(new BigDecimal(0));
    rule.setUpperLimit(new BigDecimal(12));
    // rule.setLabelFunction(function);

    CDRFunction cdrFunction = null;
    CDRFuncParameter funcParameter;

    try {
      cdrFunction = this.cdrDataProvider.getCDRFunction(function);
      Map<String, CDRFuncParameter> funcParameters = cdrFunction.getAllParameters(false);

      for (Iterator<CDRFuncParameter> iFuncParameters = funcParameters.values().iterator(); iFuncParameters.hasNext();) {
        funcParameter = iFuncParameters.next();
        if (funcParameter.getName().equals(label)) {
          this.logger.debug("Parameter " + label + " found. ");
          rule.setValueType(funcParameter.getType());

          List<CDRRule> rules = this.ssdService.readReviewRule(funcParameter.getName());


          if (rules.size() == 0) {

            message = this.ssdService.createReviewRule(rule, this.attrDataProvider, rule.getParameterName());

          }
          else {

            CDRRule oldRule = rules.get(0);

            boolean rulesEqual = compareRules(rule, oldRule);

            if (!rulesEqual) {


              // update the rule
              rule.setRuleId(oldRule.getRuleId());
              rule.setRevId(oldRule.getRevId());
              rule.setLabelId(oldRule.getLabelId());

              message = this.ssdService.updateReviewRule(rule);

              this.logger.info("rule for label " + rule.getParameterName() +
                  " is different to existing rule - existing value has been overwritten");


            }
            else {
              // rules are equal, no update
              this.logger.info("rule for label " + rule.getParameterName() + " still existing in database - no update");
              this.logWriter.println("- rule for label " + rule.getParameterName() +
                  " still existing in database - no update");
            }
          }


          if (message != null) {
            switch (message) {
              case LABELCREATED:
                this.logger.info("Label created: " + funcParameter.getName());
                this.logWriter.println("- Label created: " + funcParameter.getName());
                break;

              case SUCCESS:
                this.logger.info("Rule successfully created: " + funcParameter.getName());
                this.logWriter.println("- Rule successfully created: " + funcParameter.getName());
                break;

              default:
                this.logger.warn(funcParameter.getName() + " failed with " + message.toString() + " Type: " +
                    rule.getValueType());
                this.logWriter.println("- " + funcParameter.getName() + " failed with " + message.toString() +
                    " Type: " + rule.getValueType());
                break;
            }
          }


        }
      }
    }
    catch (Exception e) {}

    System.out.println(rule.getRefValueDCMString());


  }

  private void start(final String functionName, final ExcelFile excel, final String excelSheetName,
      final boolean overwriteExistingRules) {

    CDRFunction function = null;

    try {

      function = this.cdrDataProvider.getCDRFunction(functionName);

    }
    catch (Exception e) {
      this.logger.error("Error when getting Parameter for function " + functionName);
      this.logger.error(e.getLocalizedMessage());
      return;
    }


    this.logger.info("FunctionMap Size: " + function.getAllParameters(false).size());

    Map<String, CDRFuncParameter> funcParameters = function.getAllParameters(false);

    this.logger.info("FunctionParameters Size: " + funcParameters.size());

    CDRFuncParameter funcParameter = null;

    String hintText;

    // Reference to the column list
    ColumnList colList = new ColumnList();

    // Add columns to the column list
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Appli"), Column.STRING,
        PlausibelImporter.COL_1_APPL));
    colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Parameter", "Name"), Column.STRING, true,
        PlausibelImporter.COL_2_PARAM));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Kurzbeschreibung", "Long Name"), Column.STRING,
        PlausibelImporter.COL_3_KURZB));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Wie appliziert?", "Calibration Hint"),
        Column.STRING, PlausibelImporter.COL_4_WIEAPPL));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "optionale Erläuterungen", "Remarks"),
        Column.STRING, PlausibelImporter.COL_5_OPTE));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Erfahrungswerte", "Reference Value"),
        Column.DOUBLE, PlausibelImporter.COL_6_ERFWERTE));
    colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Prüfwert untere Grenze", "Lower Limit"),
        Column.DOUBLE, PlausibelImporter.COL_7_PRUFWERTU));
    colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Prüfwert obere Grenze", "Upper Limit"),
        Column.DOUBLE, PlausibelImporter.COL_8_PRUFWERTO));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Prüfweise", "Review Method"), Column.STRING,
        PlausibelImporter.COL_9_PRUFWEISE));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Prüfstand"), Column.STRING,
        PlausibelImporter.COL_10_PRUEFSTAND));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Effekte"), Column.STRING,
        PlausibelImporter.COL_11_EFFEKTE));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Einzel"), Column.STRING,
        PlausibelImporter.COL_12_EINZEL));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Globale"), Column.STRING,
        PlausibelImporter.COL_13_ABKRIT));
    colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Class"), Column.STRING,
        PlausibelImporter.COL_14_CLASS));

    ValueGrid grid = new ValueGrid(excel, colList, 2);

    // BitSet for the output; 0=false not show invalid - 1=true show valid
    BitSet bs = new BitSet(2);
    bs.set(0, true);
    bs.set(1, true);


    for (Entry<Integer, Row> entry : grid.getGrid(bs).entrySet()) {
      boolean parameterFound = false;

      for (Iterator<CDRFuncParameter> iFuncParameters = funcParameters.values().iterator(); iFuncParameters.hasNext();) {
        funcParameter = iFuncParameters.next();

        if (funcParameter.getName().equalsIgnoreCase(
            entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_2_PARAM)))) {
          this.logger.debug("Parameter " +
              entry.getValue().getValueAt(1) +
              " found. " +
              (overwriteExistingRules ? "Try to insert or update rule."
                  : "Try to insert new Rule. Overwriting existing rule is not active."));

          CDRRule rule = new CDRRule();
          hintText = "Loaded via PlausibeL Importer";
          parameterFound = true;

          rule.setParameterName(funcParameter.getName());
          rule.setLabelFunction(function.getName());
          rule.setValueType(funcParameter.getType());

          /*
           * Store solumns 3,4,9,10,11,12 as variable hintText. These columns have no representation in iCDM and are
           * stored as comment
           */
          if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_4_WIEAPPL)).equals("")) {
            hintText =
                new String(hintText + System.getProperty("line.separator") + "How to calibrate?: " +
                entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_4_WIEAPPL)));
          }

          if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_5_OPTE)).equals("")) {
            hintText =
                new String(hintText + System.getProperty("line.separator") + "Optional Hints: " +
                entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_5_OPTE)));
          }


          if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_10_PRUEFSTAND)).equals("")) {
            hintText =
                new String(hintText + System.getProperty("line.separator") + "Testbench: " +
                entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_10_PRUEFSTAND)));
          }

          if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_11_EFFEKTE)).equals("")) {
            hintText =
                new String(hintText + System.getProperty("line.separator") + "Effects: " +
                entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_11_EFFEKTE)));
          }

          if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_12_EINZEL)).equals("")) {
            hintText =
                new String(hintText + System.getProperty("line.separator") + "Singel Stop Condition: " +
                entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_12_EINZEL)));
          }

          if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_13_ABKRIT)).equals("")) {
            hintText =
                new String(hintText + System.getProperty("line.separator") + "Global Stop Condition: " +
                entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_13_ABKRIT)));
          }

          if (funcParameter.getType().equals("VALUE")) {

            // In column "Erfahrungswerte" there might by numbers and strings.
            if ((entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE))
                .getDValue() != 0.0) ||
                (entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE))
                    .getSValue().equals("0.0") && (entry.getValue()
                    .getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE)).getDValue() == 0.0))) {
              rule.setRefValue(BigDecimal.valueOf(entry.getValue()
                  .getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE)).getDValue()));
            }
            else {
              if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE)).equals("")) {
                hintText =
                    new String(hintText +
                        System.getProperty("line.separator") +
                        "Value based on experience: " +
                        entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE))
                            .getSValue());
              }
            }
          }
          else {
            if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE)).equals("")) {
              // Get SValue of AtomicValuePhy because in this column there might also be Strings
              hintText =
                  new String(hintText +
                      System.getProperty("line.separator") +
                      "Value based on experience: " +
                      entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_6_ERFWERTE))
                          .getSValue());
            }
          }

          if (funcParameter.getType().equals("VALUE") || funcParameter.getType().equals("MAP") ||
              funcParameter.getType().equals("CURVE")) {

            if ((entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_7_PRUFWERTU))
                .getDValue() != 0.0) ||
                ((entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_7_PRUFWERTU))
                    .getSValue().equals("0.0") && (entry.getValue()
                    .getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_7_PRUFWERTU)).getDValue() == 0.0)))) {
              rule.setLowerLimit(BigDecimal.valueOf(entry.getValue()
                  .getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_7_PRUFWERTU)).getDValue()));
            }

            if ((entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_8_PRUFWERTO))
                .getDValue() != 0.0) ||
                (entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_8_PRUFWERTO))
                    .getSValue().equals("0.0") && (entry.getValue()
                    .getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_8_PRUFWERTO)).getDValue() == 0.0))) {
              rule.setUpperLimit(BigDecimal.valueOf(entry.getValue()
                  .getAtomicValuePhy(colList.getColumnByName(PlausibelImporter.COL_8_PRUFWERTO)).getDValue()));
            }
          }
          else {
            // logger.info("Values will be filled only for type \"VALUE\"");
            if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_7_PRUFWERTU)).equals("")) {
              hintText =
                  new String(hintText + System.getProperty("line.separator") + "Lower Limit: " +
                  entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_7_PRUFWERTU)));
            }

            if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_8_PRUFWERTO)).equals("")) {
              hintText =
                  new String(hintText + System.getProperty("line.separator") + "Upper Limit: " +
                  entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_8_PRUFWERTO)));
            }
          }

          rule.setHint(hintText);

          switch (entry.getValue().getValueAt(colList.getColumnByName(PlausibelImporter.COL_9_PRUFWEISE)).toLowerCase()) {
            case "manuell":
              rule.setReviewMethod("M");
              break;
            case "manual":
              rule.setReviewMethod("M");
              break;
            case "automatic":
              rule.setReviewMethod("A");
              break;
            case "automatisch":
              rule.setReviewMethod("A");
              break;

          }

          try {
            SSDMessage message = null;

            List<CDRRule> rules = this.ssdService.readReviewRule(funcParameter.getName());

            if (rules.size() == 0) {

              message = this.ssdService.createReviewRule(rule, this.attrDataProvider, rule.getParameterName());

            }
            else {

              CDRRule oldRule = rules.get(0);

              boolean rulesEqual = compareRules(rule, oldRule);

              if (!rulesEqual) {
                if (overwriteExistingRules) {

                  // update the rule
                  rule.setRuleId(oldRule.getRuleId());
                  rule.setRevId(oldRule.getRevId());
                  rule.setLabelId(oldRule.getLabelId());

                  message = this.ssdService.updateReviewRule(rule);

                  this.logger.info("rule for label " + rule.getParameterName() +
                      " is different to existing rule - existing value has been overwritten");
                }
                else {
                  this.logger.info("rule for label " + rule.getParameterName() +
                      " is different to existing rule - no Update because overwriting existing rules is not enabled");
                }

              }
              else {
                // rules are equal, no update
                this.logger.info("rule for label " + rule.getParameterName() +
                    " still existing in database - no update");
                this.logWriter.println("- rule for label " + rule.getParameterName() +
                    " still existing in database - no update");
              }
            }

            if (message != null) {
              switch (message) {
                case LABELCREATED:
                  this.logger.info("Label created: " + funcParameter.getName());
                  this.logWriter.println("- Label created: " + funcParameter.getName());
                  break;

                case SUCCESS:
                  this.logger.info("Rule successfully created: " + funcParameter.getName());
                  this.logWriter.println("- Rule successfully created: " + funcParameter.getName());
                  break;

                default:
                  this.logger.warn(funcParameter.getName() + " failed with " + message.toString() + " Type: " +
                      rule.getValueType());
                  this.logWriter.println("- " + funcParameter.getName() + " failed with " + message.toString() +
                      " Type: " + rule.getValueType());
                  break;
              }
            }

          }
          catch (Exception e) {
            this.logger.error("Error when loading Parameter " + entry.getValue().getValueAt(1) + ": " + e.getMessage(),
                e);
          }

        }
      }

      if (!parameterFound) {
        this.logger.info("Parameter " + entry.getValue().getValueAt(1) +
            " not existing in iCDM DB. No rule has been created.");
        this.logWriter.println("Parameter " + entry.getValue().getValueAt(1) +
            " not existing in iCDM DB. No rule has been created.");
      }
    }
  }


  private void createRuleInSSD(final CDRRule rule, final boolean overwriteExistingRules) {
    try {
      SSDMessage message = null;

      List<CDRRule> rules = this.ssdService.readReviewRule(rule.getParameterName());

      if (rules.size() == 0) {

        message = this.ssdService.createReviewRule(rule, this.attrDataProvider, rule.getParameterName());

      }
      else {

        CDRRule oldRule = rules.get(0);

        boolean rulesEqual = compareRules(rule, oldRule);

        if (!rulesEqual) {
          if (overwriteExistingRules) {

            // update the rule
            rule.setRuleId(oldRule.getRuleId());
            rule.setRevId(oldRule.getRevId());
            rule.setLabelId(oldRule.getLabelId());

            message = this.ssdService.updateReviewRule(rule);

            this.logger.info("rule for label " + rule.getParameterName() +
                " is different to existing rule - existing value has been overwritten" + " (Function " +
                rule.getLabelFunction() + ")");
          }
          else {
            this.logger.info("rule for label " + rule.getParameterName() +
                " is different to existing rule - no Update because overwriting existing rules is not enabled" +
                " (Function " + rule.getLabelFunction() + ")");
          }

        }
        else {
          // rules are equal, no update
          this.logger.info("rule for label " + rule.getParameterName() + " still existing in database - no update" +
              " (Function " + rule.getLabelFunction() + ")");
          this.logWriter.println("- rule for label " + rule.getParameterName() +
              " still existing in database - no update" + " (Function " + rule.getLabelFunction() + ")");
        }
      }

      if (message != null) {
        switch (message) {
          case LABELCREATED:
            this.logger.info("Label created: " + rule.getParameterName() + " (Function " + rule.getLabelFunction() +
                ")");
            this.logWriter.println("- Label created: " + rule.getParameterName() + " (Function " +
                rule.getLabelFunction() + ")");
            break;

          case SUCCESS:
            this.logger.info("Rule successfully created: " + rule.getParameterName() + " (Function " +
                rule.getLabelFunction() + ")");
            this.logWriter.println("- Rule successfully created: " + rule.getParameterName() + " (Function " +
                rule.getLabelFunction() + ")");
            break;

          default:
            this.logger.warn(rule.getParameterName() + " failed with " + message.toString() + " Type: " +
                rule.getValueType() + " (Function " + rule.getLabelFunction() + ")");
            this.logWriter.println("- " + rule.getParameterName() + " failed with " + message.toString() + " Type: " +
                rule.getValueType() + " (Function " + rule.getLabelFunction() + ")");
            break;
        }
      }

    }
    catch (Exception e) {
      this.logger.error("Error when loading Parameter " + rule.getParameterName() + ": " + e.getMessage(), e);
    }

  }

  /**
   * @param rule
   * @param oldRule
   * @return
   */
  private boolean compareRules(final CDRRule rule, final CDRRule oldRule) {

    boolean result = false;

    if (objectsEqual(rule.getHint(), oldRule.getHint()) &&
        objectsEqual(rule.getLowerLimit(), oldRule.getLowerLimit()) &&
        objectsEqual(rule.getUpperLimit(), oldRule.getUpperLimit()) &&
        objectsEqual(rule.getRefValue(), oldRule.getRefValue()) &&
        objectsEqual(rule.getRefValueDCMString(), oldRule.getRefValueDCMString()) &&
        objectsEqual(rule.getReviewMethod(), oldRule.getReviewMethod()) &&
        objectsEqual(rule.getUnit(), oldRule.getUnit())) {
      result = true;
    }

    return result;
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
        this.logger.error(object1.getClass() + " not supported in objectEqual!");
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

  /**
   * Returns all files that are in the folder of the given path. Subfolders won't be considered
   *
   * @param path a String with the path to the folder that contains all files that should be imported
   * @return a Files array that contains all files found in the given directory
   */
  public List<ExcelFile> getAllFiles(final String path) {
    // A file object that represents the directory
    File directory = new File(path);

    File[] files = directory.listFiles();
    ExcelFile excelFile;
    List<ExcelFile> excelFiles = new ArrayList<ExcelFile>();

    // SHow the found files in the logger file
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          this.logger.info("Sub-Directory found (won't be considered for import): " + file.getAbsolutePath());
        }
        else {
          this.logger.info("File found: " + file.getAbsolutePath());
          excelFile = new ExcelFile(file.getAbsolutePath(), this.logger);
          this.logger.info("Functionname: " + excelFile.getFunctionName());
          this.logger.info("Sheetname: " + excelFile.getSheetName());

          excelFiles.add(excelFile);
        }
      }
    }

    return excelFiles;
  }


  private void initJPA() {
    try {

      CDMSession.getInstance().setProductVersion("1.25.0");
      CDMSession.getInstance().initialize("DGS_ICDM");

      this.attrDataProvider = CDMDataProvider.getInstance().getApicDataProvider();
      this.cdrDataProvider = new CDRDataProvider(this.attrDataProvider);

      this.ssdService = this.attrDataProvider.getSsdServiceHandler();

      this.logger.info("JPA initialized for user: " + this.attrDataProvider.getAppUsername());

    }
    catch (IcdmException exp) {
      this.logger.error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  private void initLogger() {
    this.logger = CDMLogger.getInstance();

    this.logger.info("logger initialized");
  }

  public static void main(final String args[]) {
    /*
     * PlausibelImporter importer = new PlausibelImporter(); CDMLogger logger = CDMLogger.getInstance(); ExcelFile
     * excelFile = new ExcelFile("", logger);
     */


  }
}
