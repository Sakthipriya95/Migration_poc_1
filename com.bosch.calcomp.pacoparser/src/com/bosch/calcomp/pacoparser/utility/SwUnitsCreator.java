package com.bosch.calcomp.pacoparser.utility;

import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1		18-Jun-2007		Parvathy Unnikrishnan	First Draft<br>
 * 0.2		08-Dec-2008		Deepa					SAC-79: Added comments<br>		
 */

/**
 * The class which creates a map of SW-Units for the paco parser plugin.
 * <p>
 * 
 * @author par7kor
 */
public final class SwUnitsCreator {

	/**
	 * Instance of SwUnitsCreator which will be created once.
	 */
	private SwUnitsCreator swUnitsCreator;

	/**
	 * HashMap which stores the paco unit short name as key and actual unit of
	 * measurement as value.
	 */
	private HashMap unitsMap;

	/**
	 * String which stores the key of the hash map.
	 */
	private String hashKey;

	/**
	 * Creates an instance of SwUnitsCreator if it is null.
	 * 
	 * @return PacoModelCollection
	 */
	public SwUnitsCreator() {
	  this.unitsMap = new HashMap();
	}

	/**
	 * Adds the short name and SW-UNIT-DISPLAY into the map.
	 * 
	 * @param currTagName the current tag name inside SW-UNIT.
	 * @param dataString the data of the current tag name.
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public void addToUnitsMap(final String currTagName,
			final String dataString) throws PacoParserException {
		try {
			if (PacoFileTagNames.SHORTNAME.equals(currTagName)) {
			  this.hashKey = dataString;
				if ("emptyUnit".equalsIgnoreCase(dataString.trim())) {
				  this.unitsMap.put(this.hashKey, "");
				}
			} else if (PacoFileTagNames.SW_UNIT_DISPLAY.equals(currTagName)) {
			  this.unitsMap.put(this.hashKey, dataString);
			}
			else if(PacoFileTagNames.SW_UNIT_REF.equals(currTagName) && !this.unitsMap.containsKey(dataString))
			{
			  this.unitsMap.put(dataString, dataString);
			}
		} catch (Exception exception) {
			throw new PacoParserException(PacoParserException.UNEXPECTED_ERROR,
					exception);
		}
	}

	/**
	 * Gives tha hashMap which stores the units.
	 * 
	 * @return HashMap
	 */
	public Map getUnitsMap() {
		return this.unitsMap;
	}

}
