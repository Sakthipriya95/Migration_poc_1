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
 * Adapter for SW-CS-ENTRY tag. Class for adpating the specific details of a
 * SW-CS-ENTRY.
 * 
 * @author dec1kor
 * 
 */

public interface SWHistoryEntryAdapter {
	
	/**
	 * Sets the attributes of SW-CS-ENTRY to the adapter.
	 * 
	 * @param attributeMap - HashMap, where key = attribute name, value = attribute value
	 * @throws PacoParserException - exception thrown by the paco parser plugin
	 */
	public abstract void setAttributes(final Map attributeMap) throws PacoParserException;
	
	/**
	 * Sets the attributes of SW-CS-FIELD to the adapter.
	 * 
	 * @param attributeMapList - list of attributes
	 * @throws PacoParserException - exception thrown by the paco parser plugin
	 */
	public abstract void setAttributesForSpecialData(final List attributeMapList) throws PacoParserException;
	
	/**
	 * Sets the state, SW-CS-STATE, of the history entry
	 * 
	 * @param state
	 * @throws PacoParserException
	 */
	public abstract void setState(final String state) throws PacoParserException;
	
	/**
	 * Sets the context, SW-CS-CONTEXT, of the history entry
	 * 
	 * @param context
	 * @throws PacoParserException
	 */
	public abstract void setContext(final String context) throws PacoParserException;
	
	/**
	 * Sets the project, SW-CS-PROJECT-INFO, info of the history entry
	 * 
	 * @param projectInfo
	 * @throws PacoParserException
	 */
	public abstract void setProjectInfo(final String projectInfo) throws PacoParserException;
	
	/**
	 * Sets the target variant, SW-CS-TARGET-VARIANT, of the history entry
	 * 
	 * @param targetVariant
	 * @throws PacoParserException
	 */
	public abstract void setTargetVariant(final String targetVariant) throws PacoParserException;
	
	/**
	 * Sets the test object, SW-CS-TEST-OBJECT, of the history entry
	 * 
	 * @param testObject
	 * @throws PacoParserException
	 */
	public abstract void setTestObject(final String testObject) throws PacoParserException;
	
	/**
	 * Sets the program identifier, SW-CS-PROGRAM-IDENTIFIER, of the history entry
	 * 
	 * @param programIdentifier
	 * @throws PacoParserException
	 */
	public abstract void setProgramIdentifier(final String programIdentifier) throws PacoParserException;
	
	/**
	 * Sets the data identifier, SW-CS-DATA-IDENTIFIER, of the history entry
	 * 
	 * @param dataIdentifier
	 * @throws PacoParserException
	 */
	public abstract void setDataIdentifier(final String dataIdentifier) throws PacoParserException;
	
	/**
	 * Sets the user, SW-CS-PERFORMED-BY, of the history entry
	 * 
	 * @param performedBy
	 * @throws PacoParserException
	 */
	public abstract void setPerformedBy(final String performedBy) throws PacoParserException;
	
	/**
	 * Sets the remark, REMARK, of the history entry
	 * 
	 * @param remark
	 * @throws PacoParserException
	 */
	public abstract void setRemark(final String remark) throws PacoParserException;
	
	/**
	 * Sets the date, DATE, of the history entry
	 * 
	 * @param date
	 * @throws PacoParserException
	 */
	public abstract void setDate(final String date) throws PacoParserException;
	
	/**
	 * Sets the special data, SW-CS-FIELD, of the history entry
	 * 
	 * @param specialData
	 * @throws PacoParserException
	 */
	public abstract void setSpecialData(final List specialData) throws PacoParserException;

}
