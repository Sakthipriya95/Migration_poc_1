package com.bosch.calcomp.pacoparser.tagprocessor;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractMapCategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;

/**
 * Revision History<br>
 * 
 * Version      Date            Name                Description<br>
 * 0.1     17-May-2007     Parvathy Unnikrishnan    First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan    Added Exception handling<br>
 * 0.3     19-Jun-2007     Parvathy Unnikrishnan    Added logging<br>
 *                                                  Changed function process and added setValueList<br>
 * 0.4     12-May-2008     Deepa                    SAC-68, modified constructor. Now the target model class name<br>
 *                                                  is derived from the PacoParser class.<br>
 * 0.5     05-Jun-2008     Deepa                    SAC-82, made logging mechanism independant of villalogger<br>
 * 0.6     23-Jun-2008     Madhu Samuel K           SAC-82, Changed PacoParserLogger.getLogger() to <br>
 *                                                  LoggerUtil.getLogger() in all the methods. <br>
 * 0.7     15-Nov-2011     Dikshita                 Modified the method process. <br>
 */
/**
 * Processor which handles the tag which are specific to a MAP.
 * 
 * @author par7kor
 * 
 */
public class MapProcessor implements ITagProcessor {

    /**
     * MapCategoryAdapter instance.
     */
    private AbstractMapCategoryAdapter mapCategoryAdapter;

    /**
     * List which hold the values(WERT) got from paco file.
     */
    private List valueList;

    /**
     * List for holding various WERT values from ValueList.
     */
    private List wertList;

    /**
     * variable to store axis index .<br>
     * 0 - wert values <br>
     * 1 - X values <br>
     * 2 - Y values <br>
     */
    private int axisIndex = -1;

    /**
     * Class name used to initialize villa logger.
     */
    private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.tagprocessor.MapProcessor";

    
    private static final String LOGMSGDELIMITER = ": ";

    /**
     * Constructor which creates a map cateogry adapter.
     * 
     * @throws PacoParserException -
     *             exception thrown by paco parser plugin.
     */
    public MapProcessor(final PacoParser pacoParser) throws PacoParserException {
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"MapProcessor Constructor started.");
        if (this.mapCategoryAdapter == null) {
            ModelAdapterFactory modelAdapterFactory = null;
            try {
                // creates instance of the target model which is accessing
                // paco parser plugin
                Class targetModelAdapterFactory = Class.forName(
                        PacoParser.getTargetModelClassName(), true,
                        PacoParser.getTargetModelAdapterFactoryClassLoader());
                modelAdapterFactory = (ModelAdapterFactory) targetModelAdapterFactory
                        .newInstance();
            } catch (ClassNotFoundException classNotFoundException) {
                LoggerUtil.getLogger().error("ClassNotFoundException from paco parser.",
                        classNotFoundException);
                throw new PacoParserException(
                        PacoParserException.PACOPARSER_ERROR,
                        classNotFoundException.getMessage(),
                        classNotFoundException);
            } catch (IllegalAccessException illegalAccessException) {
                LoggerUtil.getLogger().error("IllegalAccessException from paco parser.",
                        illegalAccessException);
                throw new PacoParserException(
                        PacoParserException.PACOPARSER_ERROR,
                        illegalAccessException.getMessage(),
                        illegalAccessException);
            } catch (InstantiationException instantiationException) {
                LoggerUtil.getLogger().error("InstantiationException from paco parser.",
                        instantiationException);
                throw new PacoParserException(
                        PacoParserException.PACOPARSER_ERROR,
                        instantiationException.getMessage(),
                        instantiationException);
            }
            ICategoryAdapter categoryAdapter = modelAdapterFactory
                    .createMapAdapter(pacoParser);
            this.mapCategoryAdapter = (AbstractMapCategoryAdapter) categoryAdapter;
        }
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"MapProcessor Constructor ended.");
    }

    /**
     * Processes the map specific tags and sets to map adpater.
     * 
     * @see com.bosch.calcomp.pacoparser.tagprocessor.ITagProcessor#
     *      process(String, String)
     * @param currTagName
     * @param dataString
     * @throws PacoParserException -
     *             exception thrown byh paco parser plugin.
     */
    public final void process(final String currTagName, final String dataString)
            throws PacoParserException {
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() started");
        if (this.mapCategoryAdapter != null) {
            try {
                if (wertList == null) {
                    wertList = new ArrayList();
                }
                if (PacoFileTagNames.SW_UNIT_REF.equals(currTagName)) {
                  SwUnitsCreator swUnitsCreator = new SwUnitsCreator();
                  swUnitsCreator.addToUnitsMap(currTagName, dataString);
                    this.mapCategoryAdapter.setUnit(dataString, swUnitsCreator);
                } else if (PacoFileTagNames.SW_AXIS_INDEX.equals(currTagName)) {
                    axisIndex = Integer.parseInt(dataString);
                } else if (PacoFileTagNames.VT.equals(currTagName)) {
                    // value is in the form of text therefore setTextFlag is set
                    // to true.
                    if (valueList == null) {
                        valueList = new ArrayList();
                    }
                    valueList.add(dataString);
                    this.mapCategoryAdapter.setTextFlag(true);
                } else if (PacoFileTagNames.V.equals(currTagName)) {
                    // value is in numerical format.
                    if (valueList == null) {
                        valueList = new ArrayList();
                    }
                    valueList.add(dataString);
                    this.mapCategoryAdapter.setTextFlag(false);
                }
                setValueList(currTagName);
            } catch (Exception ex) {
                LoggerUtil.getLogger().error("Unexpected error from paco parser.", ex);
                throw new PacoParserException(
                        PacoParserException.UNEXPECTED_ERROR, ex);
            }
        }
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() ended");
    }

    /**
     * Function sets the X, Y and Z values to the map category adapter.
     * 
     * @param tagName -
     *            current tag name.
     * @throws PacoParserException -
     *             exception thrown by paco parser plugin.
     */
    private void setValueList(final String tagName) throws PacoParserException {
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setValueList() started");
        if (this.mapCategoryAdapter != null) {
            if (valueList != null && wertList != null) {
                // if a group of values label(VG) ends it is added to the wert
                // list
                // and
                // valuelist is cleared to store next data.
                if (PacoFileTagNames.VG.equalsIgnoreCase(tagName)) {
                    if (axisIndex == 0) {
                        List values = new ArrayList(valueList);
                        wertList.add(values);
                        valueList.clear();
                    }
                } else if (PacoFileTagNames.SW_VALUES_PHYS
                        .equalsIgnoreCase(tagName)) {
                    setXYValues();
                    valueList = null;
                }
            }
            if (PacoFileTagNames.SW_INSTANCE.equalsIgnoreCase(tagName)) {
                this.mapCategoryAdapter.setPacoData();
            }
        }
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setValueList() ended");
    }

    /**
     * @throws PacoParserException
     */
    private void setXYValues() throws PacoParserException {
      if (axisIndex == 0) { // WERT
          // create 2d list for setting the values.
          List twoDimentionalList = create2DimentionalList();
          this.mapCategoryAdapter.setValues(twoDimentionalList);
      } else if (axisIndex == 1) { // X-Axis
          this.mapCategoryAdapter.setXValues(valueList);
      } else if (axisIndex == 2) { // Y-Axis
          this.mapCategoryAdapter.setYValues(valueList);
      }
    }

    /**
     * Creates a two dimentional list from the all the values stored in wert
     * list.
     * 
     * @return List
     */
    private List create2DimentionalList() {
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"create2DimentionalList() started.");
        List twoDimenList = new ArrayList();
        if (wertList != null && !wertList.isEmpty()) {
            int count = ((ArrayList) wertList.get(0)).size();
            for (int i = 0; i < count; i++) {
                List innerList = new ArrayList();
                for (int j = 0; j < wertList.size(); j++) {
                    List valueList1 = (ArrayList) wertList.get(j);
                    for (int k = 0; k < valueList1.size(); k++) {
                        String value = (String) valueList1.get(i);
                        innerList.add(value);
                        break;
                    }
                }
                twoDimenList.add(innerList);
            }
        }
        LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"create2DimentionalList() ended.");
        return twoDimenList;
    }

    /**
     * Gets the map category adapter instance.
     * 
     * @return ICategoryAdapter.
     */
    public final ICategoryAdapter getCategoryAdapter() {
        return this.mapCategoryAdapter;
    }
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public void process(String currTagName, String dataString, SwFeatureCreator swFeatureCreator)
        throws PacoParserException {
      // swFeatureCreator is not required here. This method is useful only SWInstanceProcessor
    }

}
