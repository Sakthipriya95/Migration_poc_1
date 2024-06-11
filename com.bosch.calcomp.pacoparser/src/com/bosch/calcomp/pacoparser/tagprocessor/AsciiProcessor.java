package com.bosch.calcomp.pacoparser.tagprocessor;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractAsciiCategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     14-Jun-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan	Added exception handling<br>
 * 0.3     19-Jun-2007     Parvathy Unnikrishnan	Added logging<br>
 * 0.4     20-Jun-2007     Parvathy Unnikrishnan	Changed function process and added setValueList<br>
 * 0.5	   12-May-2008	   Deepa					SAC-68, modified constructor. Now the target model class name<br>
 * 													is derived from the PacoParser class.<br>
 * 0.6	   05-Jun-2008	   Deepa					SAC-82, made logging mechanism independant of villalogger<br>
 * 0.7	   23-Jun-2008	   Madhu Samuel K 			SAC-82, Changed PacoParserLogger.getLogger() to <br>
 * 													LoggerUtil.getLogger() in all the methods. <br>
 * 0.8	   15-Nov-2011	   Dikshita					Modified the method process. <br>
 */
/**
 * Class for adapting the specific details of a ASCII (Charateristic Text/ TEXTSTRING) .
 * 
 * @author par7kor
 * 
 */
public class AsciiProcessor implements ITagProcessor {

	/**
	 * AsciiCategoryAdapter instance.
	 */
	private AbstractAsciiCategoryAdapter asciiCategoryAdapter;

	/**
	 * List which hold the values(WERT) got from paco file.
	 */
	private List<String> valueList;

	/**
	 * variable to store axis index .<br>
	 * 0 - wert values <br>
	 */
	private int axisIndex = -1;

	/**
	 * Class name used to initialize villa logger.
	 */
	private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.tagprocessor.AsciiProcessor";

	
	private static final String LOGMSGDELIMITER = ": ";

	/**
	 * Constructor which creates a valBlk cateogry adapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public AsciiProcessor(final PacoParser pacoParser) throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"AsciiProcessor Constructor started");
		if (this.asciiCategoryAdapter == null) {
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
						PacoParserException.CLASS_EXCEPTION,
						classNotFoundException.getMessage(),
						classNotFoundException);
			} catch (IllegalAccessException illegalAccessException) {
				LoggerUtil.getLogger().error("IllegalAccessException from paco parser.",
						illegalAccessException);
				throw new PacoParserException(
						PacoParserException.CLASS_EXCEPTION,
						illegalAccessException.getMessage(),
						illegalAccessException);
			} catch (InstantiationException instantiationException) {
				LoggerUtil.getLogger().error("InstantiationException from paco parser.",
						instantiationException);
				throw new PacoParserException(
						PacoParserException.CLASS_EXCEPTION,
						instantiationException.getMessage(),
						instantiationException);
			}
			ICategoryAdapter categoryAdapter = modelAdapterFactory
					.createAsciiAdapter(pacoParser);
			this.asciiCategoryAdapter = (AbstractAsciiCategoryAdapter) categoryAdapter;
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"AsciiProcessor Constructor ended");
	}

	/**
	 * Processes the ASCII specific tags and sets to ascii adpater.
	 * 
	 * @see com.bosch.calcomp.pacoparser.tagprocessor.ITagProcessor#
	 *      process(String, String)
	 * @param currTagName
	 * @param dataString
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public final void process(final String currTagName, final String dataString)
			throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() started");
		if (this.asciiCategoryAdapter != null) {
			try {
//				setAttr(currTagName, dataString);
//				setValueList(currTagName);
				
			  if (PacoFileTagNames.SW_UNIT_REF.equals(currTagName)) {
                SwUnitsCreator swUnitsCreator = new SwUnitsCreator();
                swUnitsCreator.addToUnitsMap(currTagName, dataString);
                asciiCategoryAdapter.setUnit(dataString, swUnitsCreator);
            } else if (PacoFileTagNames.SW_AXIS_INDEX.equals(currTagName)) {
                axisIndex = Integer.parseInt(new String(dataString));
            } else if (PacoFileTagNames.VT.equals(currTagName)) {
                // value is in the form of text therefore setTextFlag is set
                // to true.
                if (valueList == null) {
                    valueList = new ArrayList();
                }
                valueList.add(dataString);
                asciiCategoryAdapter.setTextFlag(true);
            } else if (PacoFileTagNames.V.equals(currTagName)) {
                if (valueList == null) {
                    valueList = new ArrayList();
                }
                valueList.add(dataString);
                asciiCategoryAdapter.setTextFlag(false);
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
   * @param currTagName
   * @param dataString
   * @throws PacoParserException
   */
  private void setAttr(final String currTagName, final String dataString) throws PacoParserException {
    if (PacoFileTagNames.SW_UNIT_REF.equals(currTagName)) {
        new SwUnitsCreator().addToUnitsMap(currTagName, dataString);
        this.asciiCategoryAdapter.setUnit(dataString);
    } else if (PacoFileTagNames.SW_AXIS_INDEX.equals(currTagName)) {
    	axisIndex = Integer.parseInt(dataString);
    } else if (PacoFileTagNames.VT.equals(currTagName)) {
    	// value is in the form of text therefore setTextFlag is set
    	// to true.
    	if (valueList == null) {
    		valueList = new ArrayList<>();
    	}
    	valueList.add(dataString);
    	this.asciiCategoryAdapter.setTextFlag(true);
    } else if (PacoFileTagNames.V.equals(currTagName)) {
    	if (valueList == null) {
    		valueList = new ArrayList<>();
    	}
    	valueList.add(dataString);
    	this.asciiCategoryAdapter.setTextFlag(false);
    }
  }

	/**
	 * Function sets the values to ascii adapter.
	 * 
	 * @param tagName -
	 *            current tag name.
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public final void setValueList(final String tagName)
			throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setValueList() started.");
		if (this.asciiCategoryAdapter != null) {
			if (valueList != null) {
				clearValueList(tagName);
			}
			if (PacoFileTagNames.SW_INSTANCE.equalsIgnoreCase(tagName)) {
			  this.asciiCategoryAdapter.setPacoData();
			}
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setValueList() ended.");
	}

  /**
   * @param tagName
   * @throws PacoParserException
   */
  private void clearValueList(final String tagName) throws PacoParserException {
    if (PacoFileTagNames.VG.equalsIgnoreCase(tagName)) {
    	if (axisIndex == 0) { // WERT
    		valueList.clear();
    	}
    } else if (PacoFileTagNames.SW_VALUES_PHYS
    		.equalsIgnoreCase(tagName)) {
    	if (axisIndex == 0) {
    	  this.asciiCategoryAdapter.setValues(valueList);
    	}
    	valueList = null;
    }
  }

	/**
	 * Gets the ascii category adapter instance.
	 * 
	 * @return ICategoryAdapter
	 */
	public final ICategoryAdapter getCategoryAdapter() {
		return this.asciiCategoryAdapter;
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
