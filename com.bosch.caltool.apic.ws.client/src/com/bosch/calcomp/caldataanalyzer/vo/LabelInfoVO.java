package com.bosch.calcomp.caldataanalyzer.vo;

import java.io.Serializable;
import java.util.Map;

import com.bosch.calmodel.caldataphy.CalDataPhy;

/**
 * 
 * @author par7kor
 * 
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.2		10-Jun-2009		Parvathy		CDAGUI-12, Added serial version ID, revision history.
 * </pre>
 * 
 */
/**
 * LabelInfoVO class holds the statistical information of a Label like the
 * minimum, maximum, average, median, peak values of the label found across
 * datasets in the Database.
 */
// CDA-62
public class LabelInfoVO implements Serializable {

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = -2242263461717003615L;

	private static final String VALUE_NOT_AVAILABLE_STRING = "n.a.";

	/**
	 * Label Name.
	 */
	private String labelName;

	/**
	 * The ID of the minimum value of this label
	 */
	private long minValueId;
	/**
	 * The ID of the maximum value of this label
	 */
	private long maxValueId;
	/**
	 * The ID of the label which is used in most datasets.
	 */
	private long peakValueId;
	/**
	 * The occurrence percentage of the peak value of this label .
	 */
	private double peakValuePercentage;
	/**
	 * The number of datasets which are using this label.
	 */
	private long sumDataSets;
	/**
	 * The minimum value of this label .
	 */
	private CalDataPhy minValue;
	/**
	 * The maximum value of this label
	 */
	private CalDataPhy maxValue;
	/**
	 * The average value of this label
	 */
	private CalDataPhy avgValue;
	/**
	 * The median value of this label
	 */
	private CalDataPhy medianValue;
	/**
	 * The value of this label which is used in most datasets
	 */
	private CalDataPhy peakValue;
	/**
	 * The list of different values of this label .
	 */
	private Map<Long, LabelValueInfoVO> valuesList;
	/**
	 * The lower Quartile Value of this label.
	 */
	private CalDataPhy lowerQuartileValue;
	/**
	 * The lower Quartile value of this label.
	 */
	private CalDataPhy upperQuartileValue;

	/**
	 * Gets Label Name.
	 * 
	 * @return label name.
	 */
	public String getLabelName() {
		return labelName;
	}

	/**
	 * Sets label name.
	 * 
	 * @param labelName
	 */
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	/**
	 * Gets ID of the minimum value of this label.
	 * 
	 * @return ID of the minimum value of this label.
	 */
	public long getMinValueId() {
		return minValueId;
	}

	/**
	 * Sets ID of the minimum value of this label.
	 * 
	 * @param minValueId
	 */
	public void setMinValueId(long minValueId) {
		this.minValueId = minValueId;
	}

	/**
	 * Gets ID of the maximum value of this label.
	 * 
	 * @return ID of the maximum value of this label.
	 */
	public long getMaxValueId() {
		return maxValueId;
	}

	/**
	 * Sets ID of the maximum value of this label.
	 * 
	 * @param maxValueId
	 */
	public void setMaxValueId(long maxValueId) {
		this.maxValueId = maxValueId;
	}

	/**
	 * Gets ID of the peak value(ID of the label which is used in most datasets)
	 * of this label.
	 * 
	 * @return ID of the peak value of this label.
	 */
	public long getPeakValueId() {
		return peakValueId;
	}

	/**
	 * Sets ID of the peak value(ID of the label which is used in most datasets)
	 * of this label.
	 * 
	 * @param peakValueId
	 */
	public void setPeakValueId(long peakValueId) {
		this.peakValueId = peakValueId;
	}

	/**
	 * Gets the occurrence percentage of the peak value of this label .
	 * 
	 * @return occurrence percentage of the peak value of this label.
	 */
	public double getPeakValuePercentage() {
		return peakValuePercentage;
	}

	/**
	 * Sets the occurrence percentage of the peak value of this label .
	 * 
	 * @param peakValuePercentage
	 */
	public void setPeakValuePercentage(double peakValuePercentage) {
		this.peakValuePercentage = peakValuePercentage;
	}

	/**
	 * Gets the number of datasets which are using this label.
	 * 
	 * @return the number of datasets which are using this label.
	 */
	public long getSumDataSets() {
		return sumDataSets;
	}

	/**
	 * Sets the number of datasets which are using this label.
	 * 
	 * @param sumDataSets
	 */
	public void setSumDataSets(long sumDataSets) {
		this.sumDataSets = sumDataSets;
	}

	/**
	 * Gets the minimum value of this label.
	 * 
	 * @return the minimum value of this label.
	 */
	public CalDataPhy getMinValue() {
		return minValue;
	}

	public String getMinValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getMinValue());
	}

	/**
	 * Sets the minimum value of this label.
	 * 
	 * @param minValue
	 */
	public void setMinValue(CalDataPhy minValue) {
		this.minValue = minValue;
	}

	/**
	 * Gets the maximum value of this label.
	 * 
	 * @return the maximum value of this label.
	 */
	public CalDataPhy getMaxValue() {
		return maxValue;
	}

	public String getMaxValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getMaxValue());
	}

	/**
	 * Get the SimpleDiaplay String of a CalDataPhy object.
	 * Take care about NULL values for the CalDataPhy object.
	 * 
	 * @return The simpleDisplay String or VALUE_NOT_AVAILABLE_STRING 
	 */
	private String getCalDataPhySimpleDisplay(CalDataPhy calDataPhy) {
		if (calDataPhy == null) {
			return VALUE_NOT_AVAILABLE_STRING;
		} else {
			return calDataPhy.getSimpleDisplayValue();
		}
	}

	/**
	 * Sets the maximum value of this label.
	 * 
	 * @param maxValue
	 */
	public void setMaxValue(CalDataPhy maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Gets the average value of this label.
	 * 
	 * @return the average value of this label.
	 */
	public CalDataPhy getAvgValue() {
		return avgValue;
	}

	public String getAvgValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getAvgValue());
	}

	/**
	 * Sets the average value of this label.
	 * 
	 * @param avgValue
	 */
	public void setAvgValue(CalDataPhy avgValue) {
		this.avgValue = avgValue;
	}

	/**
	 * Gets the median value of this label.
	 * 
	 * @return the median value of this label.
	 */
	public CalDataPhy getMedianValue() {
		return medianValue;
	}

	public String getMedianValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getMedianValue());
	}

	/**
	 * Sets the median value of this label.
	 * 
	 * @param medianValue
	 */
	public void setMedianValue(CalDataPhy medianValue) {
		this.medianValue = medianValue;
	}

	/**
	 * Gets the peak value(value which is used in most datasets) of this label.
	 * 
	 * @return the peak value(value which is used in most datasets) of this
	 *         label.
	 */
	public CalDataPhy getPeakValue() {
		return peakValue;
	}

	public String getPeakValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getPeakValue());
	}

	/**
	 * Sets the peak value(value which is used in most datasets) of this label.
	 * 
	 * @param peakValue
	 */
	public void setPeakValue(CalDataPhy peakValue) {
		this.peakValue = peakValue;
	}

	/**
	 * Gets the list which contains the value Id for this label and information
	 * on the list of file ids which use this label value.
	 * 
	 * @return HashMap with valueId and LabelValueInfo.
	 */
	public Map<Long, LabelValueInfoVO> getValuesMap() {
		return valuesList;
	}

	/**
	 * Sets the list which contains the value Id for this label and information
	 * on the list of file ids which use this label value.
	 * 
	 * @param valuesList
	 */
	public void setValuesMap(Map<Long, LabelValueInfoVO> valuesList) {
		this.valuesList = valuesList;
	}

	/**
	 * @return the number of values
	 */
	public int getNumberOfValues() {
		if(valuesList != null){
			return valuesList.size();	
		}
		return 0;
	}

	/**
	 * Gets the lower quartile value of this label.
	 * 
	 * @return the lower quartile value of this label.
	 */
	// CDA-138
	public CalDataPhy getLowerQuartileValue() {
		return lowerQuartileValue;
	}

	public String getLowerQuartileValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getLowerQuartileValue());
	}


	/**
	 * Sets the lower quartile value of this label.
	 * 
	 * @param lowerQuartileValue
	 */
	// CDA-138
	public void setLowerQuartileValue(CalDataPhy lowerQuartileValue) {
		this.lowerQuartileValue = lowerQuartileValue;
	}

	/**
	 * Gets the upper quartile value of this label.
	 * 
	 * @return the upper quartile value of this label.
	 */
	// CDA-138
	public CalDataPhy getUpperQuartileValue() {
		return upperQuartileValue;
	}

	public String getUpperQuartileValueSimpleDisplay() {
		
		return getCalDataPhySimpleDisplay(getUpperQuartileValue());
	}

	/**
	 * Sets the upper quartile value of this label.
	 * 
	 * @param upperQuartileValue
	 */
	// CDA-138
	public void setUpperQuartileValue(CalDataPhy upperQuartileValue) {
		this.upperQuartileValue = upperQuartileValue;
	}

}
