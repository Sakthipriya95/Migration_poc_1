package com.bosch.calcomp.pacoparser.factory;

import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryEntryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     16-May-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan	Added exception handling<br>
 * 0.3	   12-May-2007	   Deepa					SAC-68, added createSWHistoryAdapter(),<br> 
 * 													createSWHistoryEntryAdapter()<br>
 */
/**
 * Factory class which creates the adapters for adapting the paco file details
 * to any model.
 * 
 * @author par7kor
 * 
 */

public interface ModelAdapterFactory {

	/**
	 * Factory method which creates an instance of SWInstanceAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return SWInstanceAdapter
	 */
	public abstract SWInstanceAdapter createSWInstanceAdapter(final PacoParser pacoParser
	    , final PacoParserObjects pacoParserObjects
	    )
			throws PacoParserException;

	/**
	 * Factory method which creates an instance of MapAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return ICategoryAdapter
	 */
	public abstract ICategoryAdapter createMapAdapter(final PacoParser pacoParser)
			throws PacoParserException;

	/**
	 * Factory method which creates an instance of CurveAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return ICategoryAdapter
	 */
	public abstract ICategoryAdapter createCurveAdapter(final PacoParser pacoParser)
			throws PacoParserException;

	/**
	 * Factory method which creates an instance of ValBlkAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return ICategoryAdapter
	 */
	public abstract ICategoryAdapter createValBlkAdapter(final PacoParser pacoParser)
			throws PacoParserException;

	/**
	 * Factory method which creates an instance of AxisPtsAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return ICategoryAdapter
	 */
	public abstract ICategoryAdapter createAxisPtsAdapter(final PacoParser pacoParser)
			throws PacoParserException;

	/**
	 * Factory method which creates an instance of AsciiAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return ICategoryAdapter
	 */
	public abstract ICategoryAdapter createAsciiAdapter(final PacoParser pacoParser)
			throws PacoParserException;

	/**
	 * Factory method which creates an instance of ValueAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 * @return ICategoryAdapter
	 */
	public abstract ICategoryAdapter createValueAdapter(final PacoParser pacoParser)
			throws PacoParserException;
	
	/**
	 * Factory method which creates an instance of HistoryAdapter.
	 * 
	 * @return SWHistoryAdapter
	 * @throws PacoParserException - exception thrown by the paco parser plugin.
	 */
	public abstract SWHistoryAdapter createSWHistoryAdapter()
			throws PacoParserException;
	
	/**
	 * Factory method which creates an instance of HistoryEntryAdapter.
	 * 
	 * @return SWHistoryEntryAdapter
	 * @throws PacoParserException - exception thrown by the paco parser plugin.
	 */
	public abstract SWHistoryEntryAdapter createSWHistoryEntryAdapter()
			throws PacoParserException;
}
