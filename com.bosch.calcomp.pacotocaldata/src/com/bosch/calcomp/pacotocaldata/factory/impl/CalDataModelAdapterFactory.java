package com.bosch.calcomp.pacotocaldata.factory.impl;

import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryEntryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWInstanceAdapter;
import com.bosch.calcomp.pacotocaldata.modeladapter.CalDataHistoryAdapter;
import com.bosch.calcomp.pacotocaldata.modeladapter.CalDataHistoryEntryAdapter;
import com.bosch.calcomp.pacotocaldata.modeladapter.CalDataSWInstanceAdapter;
import com.bosch.calcomp.pacotocaldataphy.factory.impl.CalDataPhyModelAdapterFactory;

/**
 * Revision History<br>
 * 
 * Version		Date	  Name			Description<br>
 * 0.1     12-May-2008	  Deepa			SAC-68, First Draft<br>
 */

/**
 * CalDataModelAdapterFactory Factory class that implements the caldata specific
 * code for the paco parser model adapter factory.
 * 
 * It can be used as the target model to the PaCoParser to get the CalData
 * objects as shown below.
 * 
 * <p>
 * PacoParser pacoParser = new PacoParser();<br>
 * pacoParser.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());<br>
 * pacoParser.setTargetModelClassLoader(CalDataModelAdapterFactory.class.getClassLoader());<br>
 * HashMap calDataHashMap = (HashMap)pacoParser.parse(pacoFile);<br>
 * <p>
 * 
 * @author dec1kor
 * 
 */

public class CalDataModelAdapterFactory extends CalDataPhyModelAdapterFactory {

	/**    
	 * Factory method which creates an instance of CalDataSWInstanceAdapter.
	 * 
	 * @throws PacoParserException - exception thrown by the paco parser plugin.
	 * @return SWInstanceAdapter
	 */
	@Override
    public final SWInstanceAdapter createSWInstanceAdapter(final PacoParser pacoParser, 
        final PacoParserObjects pacoParserObjects) throws PacoParserException {
		return new CalDataSWInstanceAdapter(pacoParser, pacoParserObjects);
	}
	

	/**
	 * Factory method which creates an instance of CalDataHistoryAdapter.
	 * 
	 * @throws PacoParserException - exception thrown by the paco parser plugin.
	 * @return SWHistoryAdapter
	 */
	@Override
    public SWHistoryAdapter createSWHistoryAdapter() throws PacoParserException {
		return new CalDataHistoryAdapter();
	}

	/**
	 * Factory method which creates an instance of CalDataHistoryEntryAdapter.
	 * 
	 * @throws PacoParserException - exception thrown by the paco parser plugin.
	 * @return SWHistoryEntryAdapter
	 */
	@Override
    public SWHistoryEntryAdapter createSWHistoryEntryAdapter() throws PacoParserException {
		return new CalDataHistoryEntryAdapter();
	}
}
