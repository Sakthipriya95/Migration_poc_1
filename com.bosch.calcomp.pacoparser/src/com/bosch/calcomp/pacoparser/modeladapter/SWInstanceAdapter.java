package com.bosch.calcomp.pacoparser.modeladapter;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Revision History<br>
 * 
 * Version		Date		Name			Description<br>
 * 0.1		May-2007		Parvathy		First draft.<br>
 * 0.2		12-May-2008		Deepa			SAC-68, added setLongName(), setHistory(), getShortName(),<br>
 * 											getLongName(), setCalDataPhyToCalData()<br>
 * 0.3		22-Aug-2008		Parvathy		SAC-107, added setFunctionName, getFunctionName<br>
 * 0.4		08-Dec-2008		Deepa			SAC-79: Added comments<br>
 * 0.5		22-Oct-2010		Jagan			PACP-2,6 : Added setFunctionVersion <br>
 * 0.6		05-Apr-2011		Dikshitha   	PACP-15 : Added setCategoryName <br>
 */

/**
 * Adapter for SW-INSTANCE tag.
 * Class for adpating the specific details of a SW-INSTANCE.
 * 
 * @author par7kor
 * 
 */

public interface SWInstanceAdapter extends IModelAdapter{
	/**
	 * Sets short name to the adapter.
	 * 
	 * @param shortName 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public abstract void setShortName(String shortName)
			throws PacoParserException;

	/**
	 * Sets category to the adapter.
	 * 
	 * @param categoryAdapter the category adapter.
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	public abstract void setCategory(ICategoryAdapter categoryAdapter)
			throws PacoParserException;
	
	/**
	 * Sets long name to the adapter.
	 * 
	 * @param longName
	 * @throws PacoParserException
	 */
	public abstract void setLongName(String longName)
			throws PacoParserException;
	
	/**
	 * Sets the history of the sw-instance to the adapter.
	 * 
	 * @param swHistoryAdapter - SWHistoryAdapter
	 * @throws PacoParserException
	 */
	public abstract void setHistory(SWHistoryAdapter swHistoryAdapter) throws PacoParserException;
	
	/**
	 * Returns the short name of the adapter.
	 * 
	 * @return the short name of the adapter.
	 * @throws PacoParserException
	 */
	public abstract String getShortName() throws PacoParserException;
	
	/**
	 * Returns the long name of the adapter.
	 * 
	 * @return the long name of the adapter.
	 * @throws PacoParserException
	 */
	public abstract String getLongName() throws PacoParserException;
	
	/**
	 * Sets the CalDataPhy data to the CalData instance.
	 * 
	 * @throws PacoParserException
	 */
	public abstract void setCalDataPhyToCalData() throws PacoParserException;
	
	/**
	 * Sets function name to the adapter.
	 * 
	 * @param functionName
	 * @throws PacoParserException
	 */
	public abstract void setFunctionName(String functionName)
			throws PacoParserException;
	
	/**
	 * Sets function version to the adapter.
	 * 
	 * @param functionVersion
	 * @throws PacoParserException
	 */ //PACP-2,6
	public abstract void setFunctionVersion(String functionVersion)
			throws PacoParserException;
	
	/**
	 * Sets category name to the adapter.
	 * 
	 * @param categoryName
	 * @throws PacoParserException
	 */ //PACP-15
	public abstract void setCategoryName(String categoryName)
	throws PacoParserException;
	
	/**
	 * Returns the category name of the adapter.
	 * 
	 * @return the category name of the adapter.
	 * @throws PacoParserException
	 */ //PACP-15
	public abstract String getCategoryName() throws PacoParserException;
	
}
