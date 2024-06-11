package com.bosch.calcomp.pacotocaldata.modeladapter;

import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calcomp.pacotocaldataphy.modeladapter.CalDataPhySWInstanceAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldataphy.CalDataPhy;

/**
 * Revision History<br>
 * 
 * Version 	Date 			Name 		Description<br>
 * 0.1 		12-May-2008 	Deepa 		SAC-68, First Draft<br>
 * 0.2 		22-Aug-2008 	Parvathy 	SAC-107, Added setFunctionName <br>
 * 0.3		21-Oct-2010		Jagan       PACP-2, Added setFunctionVersion <br>
 * 0.4		05-Apr-2011		Dikshitha   PACP-15, Added setCategoryName. Modified the 
 * 										method setCalDataPhyToCalData()<br>
 */

/**
 * CalDataSWInstanceAdapter
 * This class implements the specific code to populate CalData.
 * 
 * @author dec1kor
 * 
 */
public class CalDataSWInstanceAdapter extends CalDataPhySWInstanceAdapter {

	/**
	 * CalData instance.
	 */
	CalData calData;

	/**
	 * long name.
	 */
	private String longName;

	/**
	 * SWHistoryAdapter instance.
	 */
	SWHistoryAdapter swHisAdapter = null;

	/**
	* category name
    */ //PACP-15
	private String categoryName;
	PacoParserObjects pacoParserObjects ;
	PacoParser pacoParser ;
	/**
	 * CalDataSWInstanceAdapter constructor, which creates CalData instance.
	 * @param pacoParser 
	 * 
	 */
	public CalDataSWInstanceAdapter(PacoParser pacoParser, PacoParserObjects pacoParserObjects) {
	  this.pacoParser = pacoParser;
	  this.pacoParserObjects = pacoParserObjects;
		calData = new CalData();
	}

	/**
	 * Sets the short name in CalData.
	 */
	public final void setShortName(final String shName)
			throws PacoParserException {
		super.setShortName(shName);
		calData.setShortName(shName);
	}

	/**
	 * Sets the history in CalData.
	 */
	@Override
  public void setHistory(SWHistoryAdapter swHistoryAdapter)
			throws PacoParserException {
		this.swHisAdapter = swHistoryAdapter;
		calData.setCalDataHistory(CalDataHistoryAdapter.getTargetClass());
	}

	/**
	 * Sets the long name in CalData.
	 */
	public void setLongName(final String longName) throws PacoParserException {
		this.longName = longName;
		calData.setLongName(longName);
	}
	
	/**
	 * Sets the category name in CalData.
	 */ //PACP-15
	public void setCategoryName(final String categoryName) throws PacoParserException {
		this.categoryName = categoryName;
		calData.setCategoryName(categoryName);
	}
	

	/**
	 * Returns the long name in CalData.
	 */
	@Override
  public String getLongName() throws PacoParserException {
		return this.longName;
	}
	
	/**
	 * Returns the category name in CalData.
	 */ //PACP-15
	@Override
  public String getCategoryName() throws PacoParserException {
		return this.categoryName;
	}


	/**
	 * Sets the caldataphy in CalData and put in the PacoModelCollection, in
	 * which key is the short name and caldata instance is the value.
	 */
	public void setCalDataPhyToCalData() throws PacoParserException {
//	  new object is created here that is the issue
//	  pacoParserObjects = new PacoParserObjects(this.pacoParser)
	  PacoModelCollection calDataCollection = pacoParserObjects.getPacoModelCollection();
		CalDataPhy calDataPhy = (CalDataPhy) calDataCollection.get(calData
				.getShortName());
		
		calData.setCalDataPhy(calDataPhy);
		//PACP-15
		if(!(calData.getCategoryName().equalsIgnoreCase(PacoFileTagNames.CAT_SW_COMPONENT)))
		calDataCollection.put(calData.getShortName(), calData);
	}

	/**
	 * Sets the function name in CalData.
	 */
	public void setFunctionName(String functionName) throws PacoParserException {
		calData.setFunctionName(functionName);
	}
	
	/**
	 * Sets the function version in CalData.
	 */ //PACP-2,6
	public void setFunctionVersion(String functionVersion) throws PacoParserException {
		calData.setFunctionVersion(functionVersion);
	}

}
