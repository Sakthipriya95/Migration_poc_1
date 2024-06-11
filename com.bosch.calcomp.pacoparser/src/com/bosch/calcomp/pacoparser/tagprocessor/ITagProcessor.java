package com.bosch.calcomp.pacoparser.tagprocessor;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.ICategoryAdapter;
import com.bosch.calcomp.pacoparser.utility.SwFeatureCreator;

/**
 * Revision History<br>
 * 
 * Version		Date		Name			Description<br>
 * 0.1		May-2007		Parvathy		First draft.<br>
 * 0.2		12-May-2008		Deepa			SAC-68, removed TARGET_MODEL_ADAPTER_FACTORY<br>
 * 0.3		08-Dec-2008		Deepa			SAC-79: Added comments<br>
 */

/**
 * Interface for a paco tag.
 * 
 * @author par7kor
 * 
 */
public interface ITagProcessor {
	 //
	 // Class name used to instatiate target model adapter factory. Only this
	 // needs to be changed to adapt to a new model.Now it is calDataPhy.
	 //

	/**
	 * Process the current tag.
	 * 
	 * @param currTagName
	 * @param dataString
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	void process(final String currTagName, final String dataString, final SwFeatureCreator swFeatureCreator)
			throws PacoParserException;
	
	/**
	 * @param currTagName
	 * @param dataString
	 * @throws PacoParserException
	 */
	void process(final String currTagName, final String dataString)
        throws PacoParserException;

	/**
	 * Gets the category adapter created by the processro.
	 * 
	 * @return ICategoryAdapter
	 */
	ICategoryAdapter getCategoryAdapter();
}
