package com.bosch.calcomp.pacotocaldataphy.modeladapter;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     13-Jun-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan	Added Exception handling<br>
 * 0.3     19-Jun-2007     Parvathy Unnikrishnan	Added logging<br>
 * 0.4	   12-May-2008	   Deepa					SAC-68, added getLongName(), setLongName(),<br>
 * 													setHistory(), setCalDataPhyToCalData()<br>
 * 0.5	   10-Jun-2008	   Deepa					SAC-82, made logging mechanism independant of VillaLogger<br>
 * 0.6	   22-Aug-2008	   Parvathy				    SAC-107, Added setFunctionName <br>
 * 0.7	   22-Oct-2010     Jagan					PACP-2,6; added setFunctionVersion <br>
 * 0.8	   05-Apr-2011     Dikshitha				PACP-15; added setCategoryName <br>
 */
/**
 * SWInstanceAdapter which implements CalDataPhy specific code.
 * 
 * @author par7kor
 * 
 */
public class CalDataPhySWInstanceAdapter implements SWInstanceAdapter {

	/**
	 * Variable to store the short name.
	 */
	private String shortName;

	/**
     * ICategoryAdapter instance.
     */
    private ICategoryAdapter categoryAdapter;

	/**
	 * Sets short name to the adapter.
	 * 
	 * @see com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter
	 *      #setShortName(String)
	 * @param shName -
	 *            label name.
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public void setShortName(String shName)
			throws PacoParserException {
		this.shortName = shName;
	}

	/**
	 * Sets category and short name to the adapter.
	 * 
	 * @see com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter
	 *      #setCategory(ICategoryAdapter)
	 * @param categoryAdapter -
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	public final void setCategory(final ICategoryAdapter categoryAdapter)
			throws PacoParserException {
		if (categoryAdapter instanceof CalDataPhyValueAdapter) {
			CalDataPhyValueAdapter calDataPhyValueAdapter = (CalDataPhyValueAdapter) categoryAdapter;
			calDataPhyValueAdapter.setShotName(shortName);
		} else if (categoryAdapter instanceof CalDataPhyAsciiAdapter) {
			CalDataPhyAsciiAdapter calDataPhyAsciiAdapter = (CalDataPhyAsciiAdapter) categoryAdapter;
			calDataPhyAsciiAdapter.setShotName(shortName);
		} else if (categoryAdapter instanceof CalDataPhyAxisPtsAdapter) {
			CalDataPhyAxisPtsAdapter calDataPhyAxisPtsAdapter = (CalDataPhyAxisPtsAdapter) categoryAdapter;
			calDataPhyAxisPtsAdapter.setShotName(shortName);
		} else if (categoryAdapter instanceof CalDataPhyValBlkAdapter) {
			CalDataPhyValBlkAdapter calDataPhyValBlkAdapter = (CalDataPhyValBlkAdapter) categoryAdapter;
			calDataPhyValBlkAdapter.setShotName(shortName);
		} else if (categoryAdapter instanceof CalDataPhyCurveAdapter) {
			CalDataPhyCurveAdapter calDataPhyCurveAdapter = (CalDataPhyCurveAdapter) categoryAdapter;
			calDataPhyCurveAdapter.setShotName(shortName);
		} else if (categoryAdapter instanceof CalDataPhyMapAdapter) {
			CalDataPhyMapAdapter calDataPhyMapAdapter = (CalDataPhyMapAdapter) categoryAdapter;
			calDataPhyMapAdapter.setShotName(shortName);
		}
		this.categoryAdapter = categoryAdapter;
	}

	/**
	 * Returns the short name.
	 * implemented
	 */
	public String getShortName() throws PacoParserException {
		return this.shortName;
	}

	/**
	 * Returns the long name. But it is irrelevant to CalDataPhy.
	 * implemented
	 */
	public String getLongName() throws PacoParserException {
		return null;
	}
	
	/**
	 * Returns the category name.
	 */ 
	public String getCategoryName() throws PacoParserException {
		return null;
	}

	/**
	 * Sets the history of the sw-instance. But it is irrelevant to CalDataPhy.
	 * implemented
	 */
	public void setHistory(SWHistoryAdapter swHistoryAdapter) throws PacoParserException {
	}

	/**
	 * Sets the long name. But it is irrelevant to CalDataPhy.
	 * implemented
	 */
	public void setLongName(String longName) throws PacoParserException {
	}

	/**
	 * Sets the data to the CalData instance. But it is irrelevant to CalDataPhy.
	 * implemented
	 */
	public void setCalDataPhyToCalData() throws PacoParserException {
	}


	/**
	 * Sets the sw-feature of the sw-instance. But it is irrelevant to CalDataPhy.
	 * implemented
	 */
	public void setFunctionName(String functionName) throws PacoParserException {
	}

	/**
	 * Sets the <REVISION-LABEL>(function version) value to <SW-FEATURE> (Function).
	 * implemented
	 */ 
	public void setFunctionVersion(String functionVersion) throws PacoParserException {
				
	}

	/**
	 * Sets the category name.
	 * implemented
	 */ 
	public void setCategoryName(String categoryName) throws PacoParserException {	
		
	}


}
