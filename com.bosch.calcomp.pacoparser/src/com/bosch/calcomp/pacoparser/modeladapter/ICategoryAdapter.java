package com.bosch.calcomp.pacoparser.modeladapter;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Interface for a specific category of SW-INSTANCE.
 * @author par7kor
 *
 */
public interface ICategoryAdapter extends IModelAdapter {
	/**
	 * Used to give back the specific model back to calling method .
	 * 
	 * @throws PacoParserException -
	 *             exception thrown byh paco parser plugin.
	 */
	void setPacoData() throws PacoParserException;
}
