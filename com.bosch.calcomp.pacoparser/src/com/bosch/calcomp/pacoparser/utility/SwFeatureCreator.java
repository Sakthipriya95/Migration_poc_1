package com.bosch.calcomp.pacoparser.utility;

import java.util.HashMap;
import java.util.Map;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1		21-Oct-2010		    Jagan				First Draft, PACP-6,2<br>
 * 		
 */

/**
 * The class which creates a map of Function name & function version for the paco parser plugin.
 * <p>
 * 
 * @author adn1cob
 */
//PACP-2,6
public final class SwFeatureCreator {

	/**
	 * Instance of SwUnitsCreator which will be created once.
	 */
	private SwFeatureCreator swFeatureCreator;

	/**
	 * HashMap which stores the paco unit short name as key and actual unit of
	 * measurement as value.
	 */
	private HashMap<String, String> versionMap;

	/**
	 * String which stores the key of the hash map.
	 */
	private String hashKey;
	
	/**
	 * Creates an instance of SwFeatureCreator if it is null.
	 * 
	 * @return PacoModelCollection
	 */
	public SwFeatureCreator() {
		this.versionMap = new HashMap<String, String> ();
	}

	/**
	 * Adds the function name and REVISION-LABEL into the map.
	 * 
	 * @param currTagName the current tag name inside SW-FEATURE.
	 * @param dataString the data of the current tag name.
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public void addToVersionMap(final String currTagName,
			final String dataString) throws PacoParserException {
		try {
			if (PacoFileTagNames.SHORTNAME.equals(currTagName)) {
				this.hashKey = dataString;
				this.versionMap.put(this.hashKey, null);				
			} else if (PacoFileTagNames.REVISION_LABEL.equals(currTagName)) {
			  this.versionMap.put(this.hashKey, dataString);
			}
		} catch (Exception exception) {
			throw new PacoParserException(PacoParserException.UNEXPECTED_ERROR,
					exception);
		}
	}

	/**
	 * Gives the hashMap which has mapping of function name & version.
	 * 
	 * @return HashMap
	 */
	public Map<String, String>  getVersionMap() {
		return this.versionMap;
	}	

}
