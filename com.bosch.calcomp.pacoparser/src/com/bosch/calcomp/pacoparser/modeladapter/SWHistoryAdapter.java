package com.bosch.calcomp.pacoparser.modeladapter;

import java.util.List;
import java.util.Map;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Revision History<br>
 * 
 * Version		Date		Name			Description<br>
 * 0.1		12-May-2008		Deepa			SAC-68, First draft.<br>
 * 0.2		08-Dec-2008		Deepa			SAC-79: Added comments<br>
 */

/**
 * Adapter for SW-CS-HISTORY tag. Class for adpating the specific details of a
 * SW-CS-HISTORY.
 * 
 * @author dec1kor
 * 
 */
public interface SWHistoryAdapter extends IModelAdapter{
	
	/**
	 * Sets the attributes of SW-CS-HISTORY to the adapter.
	 * 
	 * @param attributeMap - HashMap, where key = attribute name, value = attribute value
	 * @throws PacoParserException - exception thrown by the paco parser plugin
	 */
	public abstract void setAttributes(final Map<?, ?> attributeMap) throws PacoParserException;
	
	/**
	 * Sets the history entries to the adapter.
	 * 
	 * @param historyEntryList - list of history entries
	 * @throws PacoParserException - exception thrown by the paco parser plugin
	 */
	public abstract void setHistoryEntries(final List<?> historyEntryList) throws PacoParserException;

}
