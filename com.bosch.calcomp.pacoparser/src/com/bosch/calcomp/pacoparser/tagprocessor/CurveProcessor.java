package com.bosch.calcomp.pacoparser.tagprocessor;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractCurveCategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     13-Jun-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan	Added Exception handling<br>
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
 * Processor which handles the tag whcih are specific to a CURVE.
 * 
 * @author par7kor
 * 
 */
public class CurveProcessor implements ITagProcessor {

	/**
	 * CurveCategoryAdapter instance.
	 */
	private AbstractCurveCategoryAdapter curveCategoryAdapter;

	/**
	 * List which hold the values(WERT) got from paco file.
	 */
	private List valueList;

	/**
	 * variable to store axis index .<br>
	 * 0 - wert values <br>
	 * 1 - X values <br>
	 */
	private int axisIndex = -1;

	/**
	 * Class name used to initialize villa logger.
	 */
	private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.tagprocessor.CurveProcessor";

	private static final String LOGMSGDELIMITER = ": ";

	/**
	 * Constructor which creates a curve cateogry adapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public CurveProcessor(final PacoParser pacoParser) throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"CurveProcessor Constructor started.");
		if (this.curveCategoryAdapter == null) {
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
					.createCurveAdapter(pacoParser);
			this.curveCategoryAdapter = (AbstractCurveCategoryAdapter) categoryAdapter;
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"CurveProcessor Constructor ended.");
	}

	/**
	 * Processes the curve specific tags and sets to curve adpater.
	 * 
	 * @see com.bosch.calcomp.pacoparser.tagprocessor.ITagProcessor#process(String,
	 *      String)
	 * @param currTagName
	 * @param dataString
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public final void process(final String currTagName, final String dataString)
			throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() started.");
		if (this.curveCategoryAdapter != null) {
			try {
			  
//				setAttr(currTagName, dataString);
//				setValueList(currTagName);
			  
			  
			  if (PacoFileTagNames.SW_UNIT_REF.equals(currTagName)) {
//                SwUnitsCreator.getInstance().addToUnitsMap(currTagName, dataString);
			    SwUnitsCreator swUnitsCreator = new SwUnitsCreator();
			    swUnitsCreator.addToUnitsMap(currTagName, dataString);
                curveCategoryAdapter.setUnit(dataString, swUnitsCreator);
            } else if (PacoFileTagNames.SW_AXIS_INDEX.equals(currTagName)) {
                axisIndex = Integer.parseInt(dataString);
            } else if (PacoFileTagNames.VT.equals(currTagName)) {
                // value is in the form of text therefore setTextFlag is set
                // to true.
                if (valueList == null) {
                    valueList = new ArrayList();
                }
                valueList.add(dataString);
                curveCategoryAdapter.setTextFlag(true);
            } else if (PacoFileTagNames.V.equals(currTagName)) {
                // value is in numerical format.
                if (valueList == null) {
                    valueList = new ArrayList();
                    curveCategoryAdapter.setTextFlag(false);
                }
                valueList.add(dataString);
            }
            setValueList(currTagName);
			  
			  
			} catch (Exception ex) {
				LoggerUtil.getLogger().error("Unexpected error from paco parser.", ex);
				throw new PacoParserException(
						PacoParserException.UNEXPECTED_ERROR, ex);
			}
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() ended.");
	}

  /**
   * @param currTagName
   * @param dataString
   * @throws PacoParserException
   */
  private void setAttr(final String currTagName, final String dataString) throws PacoParserException {
    if (PacoFileTagNames.SW_UNIT_REF.equals(currTagName)) {
        new SwUnitsCreator().addToUnitsMap(currTagName, dataString);
        this.curveCategoryAdapter.setUnit(dataString);
    } else if (PacoFileTagNames.SW_AXIS_INDEX.equals(currTagName)) {
    	axisIndex = Integer.parseInt(dataString);
    } else if (PacoFileTagNames.VT.equals(currTagName)) {
    	// value is in the form of text therefore setTextFlag is set
    	// to true.
    	if (valueList == null) {
    		valueList = new ArrayList();
    	}
    	valueList.add(dataString);
    	this.curveCategoryAdapter.setTextFlag(true);
    } else if (PacoFileTagNames.V.equals(currTagName)) {
    	// value is in numerical format.
    	if (valueList == null) {
    		valueList = new ArrayList();
    		this.curveCategoryAdapter.setTextFlag(false);
    	}
    	valueList.add(dataString);
    }
  }

	/**
	 * Function which set the values to the curve adpter.
	 * 
	 * @param tagName -
	 *            current tag name.
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public final void setValueList(final String tagName)
			throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"end() started.");
		if (this.curveCategoryAdapter != null) {
//			setPacoData(tagName);
		  if (valueList != null) {
            // when the physical value label ends valuelist is set to the
            // adapter.
            if (PacoFileTagNames.SW_VALUES_PHYS.equalsIgnoreCase(tagName)) {
                if (axisIndex == 0) { // WERT
                    curveCategoryAdapter.setValues(valueList);
                } else if (axisIndex == 1) { // X-Axis
                    curveCategoryAdapter.setXValues(valueList);
                }
                valueList = null;
            }
        }
        if (PacoFileTagNames.SW_INSTANCE.equalsIgnoreCase(tagName)) {
            curveCategoryAdapter.setPacoData();
        }
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"end() ended.");
	}

  /**
   * @param tagName
   * @throws PacoParserException
   */
  private void setPacoData(final String tagName) throws PacoParserException {
    if (valueList != null) {
    	// when the physical value label ends valuelist is set to the
    	// adapter.
    	if (PacoFileTagNames.SW_VALUES_PHYS.equalsIgnoreCase(tagName) && axisIndex == 0) {
    	  this.curveCategoryAdapter.setValues(valueList);
    	} else if (PacoFileTagNames.SW_VALUES_PHYS.equalsIgnoreCase(tagName) && axisIndex == 1) {
    	  this.curveCategoryAdapter.setXValues(valueList);
    	}
//    	valueList = null;
    }
    if (PacoFileTagNames.SW_INSTANCE.equalsIgnoreCase(tagName)) {
      this.curveCategoryAdapter.setPacoData();
    }
  }

	/**
	 * Gets the curve category adapter instance.
	 * 
	 * @return ICategoryAdapter
	 */
	public final ICategoryAdapter getCategoryAdapter() {
		return this.curveCategoryAdapter;
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
