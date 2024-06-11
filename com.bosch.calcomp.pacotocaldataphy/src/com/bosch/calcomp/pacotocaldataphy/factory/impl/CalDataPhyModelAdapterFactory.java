package com.bosch.calcomp.pacotocaldataphy.factory.impl;

import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryEntryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhyAsciiAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhyAxisPtsAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhyCurveAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhyMapAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhySWInstanceAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhyValBlkAdapter;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhyValueAdapter;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     13-Jun-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan	Added exception handling<br>
 * 0.3	   12-May-2008	   Deepa					SAC-68, modified createSWInstanceAdapter()<br>
 * 													added createSWHistoryAdapter(), createSWHistoryEntryAdapter()<br>
 */
/**
 * Factory class that implements the caldatphy specific code for the paco parser
 * model adapter factory.
 * 
 * It can be used as the target model to the PaCoParser to get the CalDataPhy objects as shown below.
 * 
 * <p>
 * PacoParser pacoParser = new PacoParser();<br>
 * pacoParser.setTargetModelClassName(CalDataPhyModelAdapterFactory.class.getName());<br>
 * pacoParser.setTargetModelClassLoader(CalDataPhyModelAdapterFactory.class.getClassLoader());<br>
 * HashMap calDataPhyHashMap = (HashMap)pacoParser.parse(pacoFile);<br>
 * <p>
 * 
 * @author par7kor
 * 
 */
public class CalDataPhyModelAdapterFactory implements ModelAdapterFactory {

	/**
	 * Factory method which creates an instance of CalDataPhyMapAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return ICategoryAdapter
	 */
	@Override
  public final ICategoryAdapter createMapAdapter(final PacoParser pacoParser) throws PacoParserException {
		return new CalDataPhyMapAdapter(pacoParser);
	}

	/**
	 * Factory method which creates an instance of CalDataPhyMapAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return SWInstanceAdapter
	 */
	@Override
  public SWInstanceAdapter createSWInstanceAdapter(final PacoParser pacoParser, final PacoParserObjects pacoParserObjects)
			throws PacoParserException {
		return new CalDataPhySWInstanceAdapter();
	}

	/**
	 * Factory method which creates an instance of CalDataPhyCurveAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return ICategoryAdapter
	 */
	@Override
  public final ICategoryAdapter createCurveAdapter(final PacoParser pacoParser)
			throws PacoParserException {
		ICategoryAdapter curveCategoryAdapter = (ICategoryAdapter) new CalDataPhyCurveAdapter(pacoParser);
		return curveCategoryAdapter;
	}

	/**
	 * Factory method which creates an instance of CalDataPhyValBlkAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return ICategoryAdapter
	 */
	@Override
    public final ICategoryAdapter createValBlkAdapter(final PacoParser pacoParser)
			throws PacoParserException {
		return new CalDataPhyValBlkAdapter(pacoParser);
	}

	/**
	 * Factory method which creates an instance of CalDataPhyAxisPtsAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return ICategoryAdapter
	 */
	@Override
  public final ICategoryAdapter createAxisPtsAdapter(final PacoParser pacoParser)
			throws PacoParserException {
		return  (ICategoryAdapter) new CalDataPhyAxisPtsAdapter(pacoParser);
	}

	/**
	 * Factory method which creates an instance of CalDataPhyAsciiAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return ICategoryAdapter
	 */
	@Override
  public final ICategoryAdapter createAsciiAdapter(final PacoParser pacoParser)
			throws PacoParserException {
		return new CalDataPhyAsciiAdapter(pacoParser);
	}

	/**
	 * Factory method which creates an instance of CalDataPhyValueAdapter.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 * @return ICategoryAdapter
	 */
	@Override
    public final ICategoryAdapter createValueAdapter(final PacoParser pacoParser)
			throws PacoParserException {
		return (ICategoryAdapter) new CalDataPhyValueAdapter(pacoParser);
	}

	
	/**
	 * Factory method to create an instance of SWHistoryAdapter.
	 * But this is irrelevant for CalDataPhy.
	 */
	@Override
  public SWHistoryAdapter createSWHistoryAdapter() throws PacoParserException {
		return null;
	}

	/**
	 * Factory method to create an instance of SWHistoryEntryAdapter.
	 * But this is irrelevant for CalDataPhy.
	 */
	@Override
  public SWHistoryEntryAdapter createSWHistoryEntryAdapter() throws PacoParserException {
		return null;
	}
}
