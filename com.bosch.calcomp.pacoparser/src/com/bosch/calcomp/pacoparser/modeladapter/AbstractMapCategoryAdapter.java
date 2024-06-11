package com.bosch.calcomp.pacoparser.modeladapter;

import java.util.List;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;

/**
 * Class for adpating the specific details of a MAP .
 * 
 * @author par7kor
 * 
 */
public abstract class AbstractMapCategoryAdapter implements ISWInsPropVariantAdapter,
		ICategoryAdapter {
	/**
	 * Sets short name to the adapter.
	 * 
	 * @see com.bosch.calcomp.pacoparser.modeladapter.ISWInsPropVariantAdapter
	 *      #setShortName(String)
	 * @param shortName -
	 *            label name.
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	@Override
    public void setShortName(final String shortName) throws PacoParserException {
	  //
	}

	/**
	 * Sets unit to the adapter.
	 * 
	 * @see com.bosch.calcomp.pacoparser.modeladapter.ISWInsPropVariantAdapter
	 *      #setUnit(String)
	 * @param unit -
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	@Override
    public void setUnit(final String unit) throws PacoParserException {
	  //
	}

	/**
	 * Sets values to the adapter.
	 * 
	 * @see com.bosch.calcomp.pacoparser.modeladapter.ISWInsPropVariantAdapter
	 *      #setValues(List)
	 * @param values -
	 *            output values .
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	@Override
    public void setValues(final List<?> values) throws PacoParserException {
	  //
	}

	/**
	 * Sets x-axis values to the adapter.
	 * 
	 * @param values -
	 *            x axis values .
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public abstract void setXValues(List<?> values) throws PacoParserException;

	/**
	 * Sets y-axis values to the adapter.
	 * 
	 * @param values -
	 *            y axis values .
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public abstract void setYValues(List<?> values) throws PacoParserException;

  /**
   * @param dataString dataString
   * @param swUnitsCreator swUnitsCreator
   * @throws PacoParserException PacoParserException
   */
  public void setUnit(String dataString, SwUnitsCreator swUnitsCreator) throws PacoParserException {
//    not required
  }

}
