package com.bosch.calcomp.pacoparser.modeladapter;

import java.util.List;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Interface which represents SW-INSTANCE-PROPS-VARIANT tag details.The
 * interface has the common methods for the tags under this.
 * 
 * @author par7kor
 * 
 */
public interface ISWInsPropVariantAdapter {
	/**
	 * Sets short name to the adapter.
	 * 
	 * @param shortName - label name.
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	void setShortName(String shortName) throws PacoParserException;

	/**
	 * Sets unit to the adapter.
	 * 
	 * @param unit -
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	void setUnit(String unit) throws PacoParserException;

	/**
	 * Sets values to the adapter.
	 * 
	 * @param values - output values .
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	void setValues(List<?> values) throws PacoParserException;

	/**
	 * Boolean which represents if the values are present in text format.
	 * 
	 * @return boolean.
	 */
	boolean isText();

	/**
	 * Sets text flag to the adapter.
	 * @param flag - boolean
	 */
	void setTextFlag(boolean flag);
}
