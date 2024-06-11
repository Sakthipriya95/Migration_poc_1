package com.bosch.calcomp.pacoparser.tagprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.factory.ModelAdapterFactory;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryEntryAdapter;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
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
 * Processor which handles the tag which are specific to a SW-CS-ENTRY.
 * 
 * @author dec1kor
 * 
 */
public class SWHistoryEntryProcessor implements ITagProcessor {
	
	/**
	 * class name
	 */
	private static final String CLASSNAME = "com.bosch.calcomp.pacoparser.tagprocessor.SWHistoryEntryProcessor";
	
	
	private static final String LOGMSGDELIMITER = ": ";

	/**
	 * SWHistoryEntryAdapter instance.
	 */
	private SWHistoryEntryAdapter swHistoryEntryAdapter;
	
	/**
	 * String buffer to hold the remark paragraph data.
	 */
	private StringBuilder remarkBuffer;
	
	/**
	 * List consists of all the SW-CS-FIELD of the entry tag.
	 */
	private List<String> specialDataList;
	
	/**
	 * Hashmap which contains the attributes
	 */
	private HashMap<String, HashMap> mapTagAttrs;
	
	/**
	 * List consists of the attributes of SW-CS-FIELD.
	 */
	private List<HashMap> specialDataAttrList;
	
	/**
	 * SWHistoryEntryProcessor constructor.
	 * 
	 * @param attributesMap - attributes of the SW-CS-ENTRY
	 * @throws PacoParserException
	 */
	public SWHistoryEntryProcessor(Map attributesMap) throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"SWHistoryEntryProcessor Constructor started.");
		createSWEntryAdapter();
		setAttributes(PacoFileTagNames.SW_CS_ENTRY, attributesMap);	
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"SWHistoryEntryProcessor Constructor ended.");
	}
	
	/**
	 * Creates a SW-CS-ENTRY adapter.
	 * 
	 * @throws PacoParserException
	 */
	private void createSWEntryAdapter() throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"createSWEntryAdapter() started.");
		if(this.swHistoryEntryAdapter == null){
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
			this.swHistoryEntryAdapter = modelAdapterFactory.createSWHistoryEntryAdapter();
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"createSWEntryAdapter() ended.");	
	}
	
	/**
	 * Sets the attributes for the given tag inside the entry element.
	 * 
	 * @param tagName - entry or its children tag name.
	 * @param attributesMap - a hashmap, with key=attribute name, value=attribute value
	 */
	public void setAttributes(String tagName, Map attributesMap) {		
		if(mapTagAttrs == null){
			mapTagAttrs = new HashMap<String, HashMap>();
		}
		if(attributesMap != null){
			mapTagAttrs.put(tagName, (HashMap) attributesMap);
		}
	}
	
	/**
	 * Sets the attributes of the elements to the adapter.
	 * 
	 * @throws PacoParserException
	 */
	public void setAttributes() throws PacoParserException{
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setAttributes() started.");
		if(this.swHistoryEntryAdapter != null){
			this.swHistoryEntryAdapter.setAttributes(mapTagAttrs);
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setAttributes() ended.");
	}
	
	/**
	 * Sets the attributes of the SW-CS-FIELD.
	 * 
	 * @param attributesMap - a hashmap, with key=attribute name, value=attribute value
	 */
	public void setAttributesForSpecialData(Map attributesMap){		
		if(specialDataAttrList == null){
			specialDataAttrList = new ArrayList<HashMap>();
		}
		if(attributesMap != null){
			specialDataAttrList.add((HashMap) attributesMap);
		}
	}
	
	/**
	 * Sets the attributes of SW-CS-FIELD to the adapter.
	 * 
	 * @throws PacoParserException
	 */
	public void setAttributesForSpecialData() throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setAttributesForSpecialData() started.");
		if(this.swHistoryEntryAdapter != null && specialDataAttrList != null && !specialDataAttrList.isEmpty()){
			this.swHistoryEntryAdapter.setAttributesForSpecialData(specialDataAttrList);
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setAttributesForSpecialData() ended.");
	}
	
	/**
	 * Returns the category adapter.
	 */
	public ICategoryAdapter getCategoryAdapter() {
		return null;
	}
	
	/**
	 * Returns the SWHistoryEntryAdapter.
	 * @return SWHistoryEntryAdapter
	 */
	public SWHistoryEntryAdapter getHistoryEntryAdapter(){
		return this.swHistoryEntryAdapter;
	}

	/**
	 * Processes the tags inside SW-CS-ENTRY.
	 * 
	 * @param currTagName - the current tag name inside SW-CS-ENTRY.
	 * @param dataString - the data of the current tag.
	 * @throws PacoParserException
	 */
	public void process(String currTagName, String dataString) throws PacoParserException {
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() started.");
		if(this.swHistoryEntryAdapter != null){
			if(PacoFileTagNames.SW_CS_STATE.equals(currTagName)){
				this.swHistoryEntryAdapter.setState(dataString);
			}else if(PacoFileTagNames.SW_CS_CONTEXT.equals(currTagName)){
				this.swHistoryEntryAdapter.setContext(dataString);
			}else if(PacoFileTagNames.SW_CS_PROJECT_INFO.equals(currTagName)){
				this.swHistoryEntryAdapter.setProjectInfo(dataString);
			}else if(PacoFileTagNames.SW_CS_TARGET_VARIANT.equals(currTagName)){
				this.swHistoryEntryAdapter.setTargetVariant(dataString);
			}else if(PacoFileTagNames.SW_CS_TEST_OBJECT.equals(currTagName)){
				this.swHistoryEntryAdapter.setTestObject(dataString);
			}else if(PacoFileTagNames.SW_CS_PROGRAM_IDENTIFIER.equals(currTagName)){
				this.swHistoryEntryAdapter.setProgramIdentifier(dataString);
			}else if(PacoFileTagNames.SW_CS_DATA_IDENTIFIER.equals(currTagName)){
				this.swHistoryEntryAdapter.setDataIdentifier(dataString);
			}else if(PacoFileTagNames.SW_CS_PERFORMED_BY.equals(currTagName)){
				this.swHistoryEntryAdapter.setPerformedBy(dataString);
			}else if(PacoFileTagNames.REMARK_PARAGRAPH.equals(currTagName)){
				if(remarkBuffer == null){
					remarkBuffer = new StringBuilder();
				}
				remarkBuffer.append(dataString);
			}else if(PacoFileTagNames.DATE.equals(currTagName)){
				this.swHistoryEntryAdapter.setDate(dataString);
			}else if(PacoFileTagNames.SW_CS_FIELD.equals(currTagName)){
				if(specialDataList == null){
					specialDataList = new ArrayList<>();
				}
				specialDataList.add(dataString);
			}			
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"process() ended.");
	}
	
	/**
	 * Sets the remark to the adapter.
	 * 
	 * @throws PacoParserException
	 */
	public void setRemark() throws PacoParserException{
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setRemark() started.");
		if(remarkBuffer != null){
			this.swHistoryEntryAdapter.setRemark(remarkBuffer.toString());
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setRemark() ended.");
	}
	
	/**
	 * Sets the SW-CS-FIELDs to the adapter.
	 * 
	 * @throws PacoParserException
	 */
	public void setSpecialData() throws PacoParserException{
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setSpecialData() started.");
		if(specialDataList != null){
			this.swHistoryEntryAdapter.setSpecialData(specialDataList);
		}
		LoggerUtil.getLogger().debug(CLASSNAME+LOGMSGDELIMITER+"setSpecialData() ended.");
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
