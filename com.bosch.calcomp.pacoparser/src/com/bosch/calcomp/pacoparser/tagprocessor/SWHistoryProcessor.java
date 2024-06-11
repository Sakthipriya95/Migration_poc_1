package com.bosch.calcomp.pacoparser.tagprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryEntryAdapter;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;

/**
 * Revision History<br>
 * 
 * Version		Date		Name				Description<br>
 * 0.1		12-May-2008		Deepa				SAC-68, first draft<br>
 * 0.2		05-Jun-2008		Deepa				SAC-82, made logging mechanism independant of villalogger<br>
 * 0.3	    23-Jun-2008	    Madhu Samuel K 		SAC-82, Changed PacoParserLogger.getLogger() to <br>
 * 												LoggerUtil.getLogger() in all the methods. <br>
 * 0.4		08-Dec-2008		Deepa				SAC-79: Added comments<br>
 */

/**
 * Processor which handles the tag which are specific to a SW-CS-HISTORY.
 * 
 * @author dec1kor
 *
 */
public class SWHistoryProcessor implements ITagProcessor {
	
	/**
	 * class name.
	 */
	private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.tagprocessor.SWHistoryProcessor";
	
	
	private static final String LOGMSGDELIMITER = ": ";
	
	/**
	 * SWHistoryAdapter instance
	 */
	private SWHistoryAdapter swHistoryAdapter;
	
	/**
	 * list of the history entries.
	 */
	private List<SWHistoryEntryAdapter> swHistoryEntryList;
	
	/**
	 * SWHistoryProcessor constructor.
	 * 
	 * @param attributesMap - attributes of SW-CS-HISTORY.
	 * @throws PacoParserException
	 */
	public SWHistoryProcessor(Map attributesMap) throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"SWHistoryProcessor Constructor started.");
		createSWHistoryAdapter();
		setAttributesForHistory(attributesMap);		
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"SWHistoryProcessor Constructor ended.");
	}

	/**
	 * Creates a SWHistoryAdapter.
	 * 
	 * @throws PacoParserException
	 */
	private void createSWHistoryAdapter() throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"createSWHistoryAdapter() started.");
		if(this.swHistoryAdapter == null){
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
			this.swHistoryAdapter = modelAdapterFactory.createSWHistoryAdapter();
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"createSWHistoryAdapter() ended.");
	}
	
	/**
	 * Sets the attributes of SW-CS-HISTORY to the adapter.
	 * 
	 * @param attributesMap - a hashmap with key=attribute name, value=attribute value
	 * @throws PacoParserException
	 */
	private void setAttributesForHistory(Map attributesMap) throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setAttributesForHistory() started.");
		if(this.swHistoryAdapter != null && attributesMap != null){			
			this.swHistoryAdapter.setAttributes(attributesMap);
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setAttributesForHistory() ended.");
	}

	/**
	 * Returns the category adapter.
	 */
	public ICategoryAdapter getCategoryAdapter() {		
		return null;
	}

	/**
	 * Process the current tag.
	 * implemeneted
	 */
	public void process(String currTagName, String dataString) throws PacoParserException {		
	  //
	}

	/**
	 * Adds the history entry to the history.
	 * 
	 * @param historyEntryAdapter - SWHistoryEntryAdapter
	 */
	public void addHistoryEntry(SWHistoryEntryAdapter historyEntryAdapter) {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"addHistoryEntry() started.");
		if(swHistoryEntryList == null){
			swHistoryEntryList = new ArrayList<>();
		}
		swHistoryEntryList.add(historyEntryAdapter);		
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"addHistoryEntry() ended.");
	}
	
	/**
	 * Sets the history entries to the adapter.
	 * 
	 * @throws PacoParserException
	 */
	public void setHistoryEntries() throws PacoParserException{
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setHistoryEntries() started.");
		if(this.swHistoryAdapter != null && swHistoryEntryList != null){
			this.swHistoryAdapter.setHistoryEntries(swHistoryEntryList);
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setHistoryEntries() ended.");
	}
	
	/**
	 * Returns the history entries.
	 * 
	 * @return a List of SWHistoryEntryAdapter.
	 */
	public List getHistoryEntries() {
		return swHistoryEntryList;
	}
	
	/**
	 * Returns the SWHistoryAdapter.
	 * @return SWHistoryAdapter
	 */
	public SWHistoryAdapter getSWHistoryAdapter(){
		return this.swHistoryAdapter;
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
