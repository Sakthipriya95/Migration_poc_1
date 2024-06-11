package com.bosch.calcomp.caldataanalyzer.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.bosch.calmodel.caldataphy.CalDataPhy;


/**
 * <pre>
 * Revision History<br>
 * 
 * Version	Date			Name			Description<br>
 * 0.1		Unknown	    	Frank Henze		First Draft<br>
 * 0.2		10-Jun-2009		Parvathy		CDAGUI-12, Made class serializable.
 * 0.3		27-Jul-2009		Deepa			CDA-93, added interface methods.<br>
 * </pre>
 */

/**
 * 
 * Interface to implement a IDatasetRecord.
 * 
 * A IDatasetRecord can be used when analyzing calibration data to
 * filter specific datasets.
 * 
 * @author hef2fe
 * 
 */
public interface IDatasetRecord extends Serializable{
		
	/**
	 * Gets the file id.
	 * @return fileID
	 */
	//CDA-93
	public long getFileId();
	
	/**
	 * Gets the label value id map in the data set record.
	 * @return label value id map.
	 */
	//CDA-93
	public HashMap<String, Long> getLabelValueIdMap();
	
	/**
	 * Gets the list of label names present in that dataset record.
	 * @return the list of label names
	 */
	public List<String> getLabelNames();
	
	/**
	 * Return the CalDataPhy of the label at the particular index in that dataset record.
	 * @param labelIndex
	 * @return CalDataPhy
	 */
	public CalDataPhy getValue(int labelIndex);

}
