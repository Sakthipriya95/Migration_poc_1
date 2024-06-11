package com.bosch.calcomp.pacoparser.tagprocessor;

import java.util.Map;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.factory.ProcessorFactory;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 16-May-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 18-Jun-2007 Parvathy Unnikrishnan Added Exception handling<br>
 * 0.3 19-Jun-2007 Parvathy Unnikrishnan Added logging<br>
 * 0.4 20-Jun-2007 Parvathy Unnikrishnan Changed function process and added setValueList<br>
 * 0.5 12-May-2008 Deepa SAC-68, modified process(), added createProcessorForHistory()<br>
 * , isHistoryEntryElement()<br>
 * 0.6 26-May-2008 Deepa SAC-68, Modified process()<br>
 * 0.7 05-Jun-2008 Deepa SAC-82, made logging mechanism independant of villalogger<br>
 * 0.8 23-Jun-2008 Madhu Samuel K SAC-82, Changed PacoParserLogger.getLogger() to <br>
 * LoggerUtil.getLogger() in all the methods. <br>
 * 0.9 22-Aug-2008 Parvathy SAC-107<br>
 * 1.0 22-Oct-2010 Jagan PACP-2,6; Modified process() to add getFunctionVersion() <br>
 * 1.1 28-Mar-2011 Dikshitha PACP-15; Modified process()<br>
 */
/**
 * Processor which handles the tag whcih are specific to a SW-INSTANCE.
 * 
 * @author par7kor
 */
public class SWInstanceProcessor implements ITagProcessor {

  /**
   * Processor instance.
   */
  private ITagProcessor processor;

  /**
   * History processor instance.
   */
  private ITagProcessor historyProcessor;

  /**
   * History entry processor instance.
   */
  private ITagProcessor historyEntryProcessor;

  /**
   * SWInstanceAdapter instance.
   */
  private SWInstanceAdapter swInstanceAdapter;

  /**
   * Class name used to initialize villa logger.
   */
  private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.tagprocessor.SWInstanceProcessor";

  private static final String LOGMSGDELIMITER = ": ";

  private final PacoParser pacoParser;
  
  final PacoParserObjects pacoParserObjects;

  /**
   * @param pacoParser Cdf parser
   * @param pacoParserObjects  pacoParserObjects
   */
  public SWInstanceProcessor(final PacoParser pacoParser
      , final PacoParserObjects pacoParserObjects
      ) {
    this.pacoParser = pacoParser;
    this.pacoParserObjects = pacoParserObjects;
  }
  
  private SwFeatureCreator swFeatureCreator = null;

  /**
   * Processes the current tag.
   * 
   * @see com.bosch.calcomp.pacoparser.tagprocessor.ITagProcessor#process(String, String)
   * @param currTagName currTagName
   * @throws PacoParserException - exception thrown byh paco parser plugin. , final SwFeatureCreator swFeatureCreator
   */
  @Override
  public final void process(final String currTagName, String data, final SwFeatureCreator swFeatureCreator1) throws PacoParserException {

    String dataString = data;
//    We are getting swFeatureCreator handler to get version
    this.swFeatureCreator = swFeatureCreator1;

    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "process() started.");
    createSwInstanceAdapter(this.pacoParser
        , this.pacoParserObjects
        );
    if (this.swInstanceAdapter != null) {

      // Support whitespace for ASCII values
      // Remove /n and /t as, it will cause difference in values
      dataString = dataString.replaceAll("\n", "");
      dataString = dataString.replaceAll("\t", "");
      // Replace double quotes with \"
      dataString = dataString.replaceAll("\"", "\\\\\"");
      // PACP-10
      String categoryType = "";
      categoryType = setAttre(currTagName, dataString, categoryType);
      // if a category type is got from the file the specific processor is
      // created.
      if (categoryType != null && categoryType.trim().length() > 0) {
        createProcessor(categoryType);
      }
      // this check implies a CATEGORY tag has been found and a specific
      // processor is created
      if (processor != null) {
        this.swInstanceAdapter.setCategory(processor.getCategoryAdapter());
        processor.process(currTagName, dataString);
//        processor.process(currTagName, dataString, swFeatureCreator);
      }

      // delegates the processing of the tags of SW-CS-ENTRY to the
      // history entry
      // processor and sets data.
      setValueForTag(currTagName, dataString);

      // adds the history entry to the history processor.
      addHistoryEntry(currTagName);

      // sets the history entries to the history.
      // sets the history to the sw-instance.
      addHistory(currTagName);


      // sets the caldata instance to the sw-instance.
      if (PacoFileTagNames.SW_INSTANCE.equals(currTagName)) {
        this.swInstanceAdapter.setCalDataPhyToCalData();
      }

    }
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "process() ended.");
  }

  /**
   * @param currTagName
   * @throws PacoParserException
   */
  private void addHistory(final String currTagName) throws PacoParserException {
    if (PacoFileTagNames.SW_CS_HISTORY.equals(currTagName)) {
      if (historyProcessor != null && ((SWHistoryProcessor) historyProcessor).getSWHistoryAdapter() != null) {
        ((SWHistoryProcessor) historyProcessor).setHistoryEntries();
        this.swInstanceAdapter.setHistory(((SWHistoryProcessor) historyProcessor).getSWHistoryAdapter());
      }
      historyEntryProcessor = null;
      historyProcessor = null;
    }
  }

  /**
   * @param currTagName
   */
  private void addHistoryEntry(final String currTagName) {
    if (historyProcessor != null &&
        ((SWHistoryProcessor) historyProcessor).getSWHistoryAdapter() != null &&
        currTagName.equals(PacoFileTagNames.SW_CS_ENTRY) && historyEntryProcessor != null) {

      ((SWHistoryProcessor) historyProcessor)
          .addHistoryEntry(((SWHistoryEntryProcessor) historyEntryProcessor).getHistoryEntryAdapter());

    }
  }

  /**
   * @param currTagName
   * @param dataString
   * @throws PacoParserException
   */
  private void setValueForTag(final String currTagName, String dataString) throws PacoParserException {
    if (historyEntryProcessor != null &&
        ((SWHistoryEntryProcessor) historyEntryProcessor).getHistoryEntryAdapter() != null &&
        isHistoryEntryElement(currTagName)) {

      historyEntryProcessor.process(currTagName, dataString);
      this.setValuesIfcurrTagNameIsSwCsENTRY(currTagName);
    }
  }

  /**
   * @param currTagName
   * @param dataString
   * @param categoryType
   * @return
   * @throws PacoParserException  , final SwFeatureCreator swFeatureCreator
   */ 
  private String setAttre(final String currTagName, String dataString, String categoryType) throws PacoParserException {
    if (PacoFileTagNames.SHORTNAME.equalsIgnoreCase(currTagName)) {
      this.swInstanceAdapter.setShortName(dataString);
    }
    else if (PacoFileTagNames.LONGNAME.equalsIgnoreCase(currTagName)) {
      this.swInstanceAdapter.setLongName(dataString);
    }
    else if (PacoFileTagNames.CATEGORY.equalsIgnoreCase(currTagName)) {
      categoryType = dataString;
      // PACP-15
      this.swInstanceAdapter.setCategoryName(categoryType);
      if (categoryType.equalsIgnoreCase(PacoFileTagNames.CAT_SW_COMPONENT)) {
        categoryType = null;
        processor = null;
      }
      // SAC-107
    }
    else if (PacoFileTagNames.SW_FEATURE_REF.equalsIgnoreCase(currTagName)) {
      this.swInstanceAdapter.setFunctionName(dataString);
      // PACP-2,6: Get & set the corresponding function version from the version map
      // sets null if function version is not presented
//      Here in setFunctionVersion swFeatureCreator is passed to get the version
      this.swInstanceAdapter.setFunctionVersion(PacoParserUtil.getFunctionVersion(dataString, this.swFeatureCreator));
    }
    return categoryType;
  }

  private void setValuesIfcurrTagNameIsSwCsENTRY(final String currTagName) throws PacoParserException {
    if (currTagName.equals(PacoFileTagNames.SW_CS_ENTRY)) {
      ((SWHistoryEntryProcessor) historyEntryProcessor).setRemark();
      ((SWHistoryEntryProcessor) historyEntryProcessor).setSpecialData();
      ((SWHistoryEntryProcessor) historyEntryProcessor).setAttributes();
      ((SWHistoryEntryProcessor) historyEntryProcessor).setAttributesForSpecialData();
    }
  }

  /**
   * Creates respective processors based on the category type.
   * 
   * @throws PacoParserException - exception thrown byh paco parser plugin.
   */
  private void createProcessor(final String categoryType) throws PacoParserException {
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "createProcessor() started.");
    ProcessorFactory factory = new ProcessorFactory(this.pacoParser
        , this.pacoParserObjects
        );
    if (PacoFileTagNames.CAT_VALUE.equalsIgnoreCase(categoryType)) {
      this.processor = factory.createValueProcessor(this.pacoParser);
    }
    else if (PacoFileTagNames.CAT_VALUE_BLOCK.equalsIgnoreCase(categoryType)) {
      this.processor = factory.createValBlkProcessor(this.pacoParser);
    }
    else if (PacoFileTagNames.CAT_ASCII.equalsIgnoreCase(categoryType)) {
      this.processor = factory.createAsciiProcessor(this.pacoParser);
    }
    else if (PacoFileTagNames.CAT_AXIS_VALUES.equalsIgnoreCase(categoryType)) {
      this.processor = factory.createAxisPtsProcessor(this.pacoParser);
    }
    else if (PacoFileTagNames.CAT_CURVE_INDIVIDUAL.equalsIgnoreCase(categoryType) ||
        PacoFileTagNames.CAT_CURVE_FIXED.equalsIgnoreCase(categoryType) ||
        PacoFileTagNames.CAT_CURVE_GROUPED.equalsIgnoreCase(categoryType)) {
      this.processor = factory.createCurveProcessor(this.pacoParser);
    }
    else if (PacoFileTagNames.CAT_MAP_INDIVIDUAL.equalsIgnoreCase(categoryType) ||
        PacoFileTagNames.CAT_MAP_FIXED.equalsIgnoreCase(categoryType) ||
        PacoFileTagNames.CAT_MAP_GROUPED.equalsIgnoreCase(categoryType)) {
      this.processor = factory.createMapProcessor(this.pacoParser);
    }
    else {
      PacoParserException pacoParserException =
          new PacoParserException(PacoParserException.ILLEGAL_CHARACTERISTIC_TYPE);
      String errMsg = "Illegal characteristic type found while parsing paco file : " + categoryType;
      LoggerUtil.getLogger().error(errMsg);
      throw new PacoParserException(pacoParserException.getErrorCode(), errMsg);
    }
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "createProcessor() ended.");
  }

  /**
   * This method is not used for the SW-INSTANCE as it the start tag and the category comes as its property.
   * 
   * @return ICategoryAdapter
   */
  @Override
  public final ICategoryAdapter getCategoryAdapter() {
    return null;
  }

  /**
   * Creates SWIntanceAdapter.
   * 
   * @throws PacoParserException - exception thrown byh paco parser plugin.
   */
  private void createSwInstanceAdapter(final PacoParser pacoParser
      , final PacoParserObjects pacoParserObjects
      ) throws PacoParserException {
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "createSwInstanceAdapter() started.");
    try {
      if (this.swInstanceAdapter == null && PacoParser.getTargetModelAdapterFactoryClassLoader() != null) {

        ModelAdapterFactory modelAdapterFactory = null;
        modelAdapterFactory = this.setModelAdapterFactory();
        this.swInstanceAdapter = modelAdapterFactory.createSWInstanceAdapter(pacoParser
            , pacoParserObjects
            );
      }
    }
    catch (Exception ex) {
      LoggerUtil.getLogger().error("Unexpected error from paco parser.", ex);
      throw new PacoParserException(PacoParserException.UNEXPECTED_ERROR, ex);
    }
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "createSwInstanceAdapter() ended.");
  }

  private ModelAdapterFactory setModelAdapterFactory() throws PacoParserException {

    try {
      Class targetModelAdapterFactory =
          Class.forName(PacoParser.getTargetModelClassName(), true, PacoParser.getTargetModelAdapterFactoryClassLoader());
      return (ModelAdapterFactory) targetModelAdapterFactory.newInstance();
    }
    catch (ClassNotFoundException classNotFoundException) {
      LoggerUtil.getLogger().error("ClassNotFoundException from paco parser.", classNotFoundException);
      throw new PacoParserException(PacoParserException.PACOPARSER_ERROR, classNotFoundException);
    }
    catch (IllegalAccessException illegalAccessException) {
      LoggerUtil.getLogger().error("IllegalAccessException from paco parser.", illegalAccessException);
      throw new PacoParserException(PacoParserException.PACOPARSER_ERROR, illegalAccessException);
    }
    catch (InstantiationException instantiationException) {
      LoggerUtil.getLogger().error("InstantiationException from paco parser.", instantiationException);
      throw new PacoParserException(PacoParserException.PACOPARSER_ERROR, instantiationException);
    }

  }

  /**
   * Creates processors for history and its entries. Also, sets its attributes.
   * 
   * @param tagName       - the tag name of the history or entry
   * @param attributesMap - attributes to be set
   * @throws PacoParserException PacoParserException
   */
  public void createProcessorForHistory(String tagName, Map attributesMap) throws PacoParserException {
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "createProcessorForHistory() started.");
    if (PacoFileTagNames.SW_CS_HISTORY.equals(tagName)) {
      historyProcessor = ProcessorFactory.createSWHistoryProcessor(attributesMap);
    }
    else if (PacoFileTagNames.SW_CS_ENTRY.equals(tagName)) {
      historyEntryProcessor = ProcessorFactory.createSWHistoryEntryProcessor(attributesMap);
    }

    if (historyEntryProcessor != null && isHistoryEntryElement(tagName)) {
      if (!PacoFileTagNames.SW_CS_FIELD.equals(tagName)) {
        ((SWHistoryEntryProcessor) historyEntryProcessor).setAttributes(tagName, attributesMap);
      }
      else {
        ((SWHistoryEntryProcessor) historyEntryProcessor).setAttributesForSpecialData(attributesMap);
      }
    }
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "createProcessorForHistory() ended.");
  }

  /**
   * Checks whether it is a history entry element.
   * 
   * @param currTagName - tag name of the history entry or its children.
   * @return - true, if it is an entry element, false otherwise.
   */
  public boolean isHistoryEntryElement(String currTagName) {
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "isHistoryEntryElement() started.");
    String[] hisEntryEleArr = new String[] { PacoFileTagNames.SW_CS_ENTRY, PacoFileTagNames.SW_CS_STATE,
        PacoFileTagNames.SW_CS_CONTEXT, PacoFileTagNames.SW_CS_PROJECT_INFO, PacoFileTagNames.SW_CS_TARGET_VARIANT,
        PacoFileTagNames.SW_CS_TEST_OBJECT, PacoFileTagNames.SW_CS_PROGRAM_IDENTIFIER,
        PacoFileTagNames.SW_CS_DATA_IDENTIFIER, PacoFileTagNames.SW_CS_PERFORMED_BY, PacoFileTagNames.REMARK,
        PacoFileTagNames.REMARK_PARAGRAPH, PacoFileTagNames.DATE, PacoFileTagNames.SW_CS_FIELD };
    for (int i = 0; i < hisEntryEleArr.length; i++) {
      if (hisEntryEleArr[i].equals(currTagName)) {
        return true;
      }
    }
    LoggerUtil.getLogger().debug(CLASSNAME + LOGMSGDELIMITER + "isHistoryEntryElement() ended.");
    return false;
  }
  
  /** 
   * {@inheritDoc} , final SwFeatureCreator swFeatureCreator
   */ 
  @Override
  public void process(String currTagName, String dataString)
      throws PacoParserException {
    // swFeatureCreator is not required here. 
  }

}