package com.bosch.calcomp.pacotocaldata.modeladapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryEntryAdapter;
import com.bosch.calcomp.pacoparser.utility.PacoFileTagNames;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.HistoryEntry;

/**
 * Revision History<br>
 * 
 * Version		Date	   Name				Description<br>
 * 0.1     12-May-2008     Deepa			SAC-68, First Draft<br>
 * 0.2	   10-Jun-2008	   Deepa			SAC-82, made logging mechanism independant of VillaLogger<br>
 */

/**
 * CalDataHistoryEntryAdapter
 * This class implements the specific code to populate HistoryEntry.
 * test
 * test
 * 
 * @author dec1kor
 * 
 */

public class CalDataHistoryEntryAdapter implements SWHistoryEntryAdapter {

	/**
	 * HistoryEntry instance.
	 */
	public HistoryEntry historyEntry;
	
	/**
	 * DataElement instance.
	 */
	public DataElement dataElement;

	/**
	 * CalDataHistoryEntryAdapter constructor which creates HistoryEntry.
	 * 
	 */
	public CalDataHistoryEntryAdapter() {
		if (historyEntry == null) {
			historyEntry = new HistoryEntry();
			dataElement = new DataElement();
		}
	}

	/**
	 * Sets the attributes of the entry elements in HistoryEntry.
	 * 
	 * @param attributeMapForEntryElements - attributes to be set, 
	 * 					key=tag name, value=hashmap in which key=attr name, value=attr value.
	 */	
	public void setAttributes(Map attributeMapForEntryElements) {
			DataElement dataEle;
			for (Iterator iter = attributeMapForEntryElements.keySet().iterator(); iter.hasNext();) {
				String entryElement = (String) iter.next();
				HashMap attrMap = (HashMap)attributeMapForEntryElements.get(entryElement);
				for (Iterator attrItr = attrMap.keySet().iterator(); attrItr
						.hasNext();) {
					String attributeName = (String) attrItr.next();
					String attributeValue = (String)attrMap.get(attributeName);
					if(PacoFileTagNames.SW_CS_ENTRY.equals(entryElement)){
						setAttributesForEntry(attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_STATE.equals(entryElement)){
						dataEle = historyEntry.getState();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_CONTEXT.equals(entryElement)){
						dataEle = historyEntry.getContext();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_PROJECT_INFO.equals(entryElement)){
						dataEle = historyEntry.getProject();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_TARGET_VARIANT.equals(entryElement)){
						dataEle = historyEntry.getTargetVariant();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_TEST_OBJECT.equals(entryElement)){
						dataEle = historyEntry.getTestObject();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_PROGRAM_IDENTIFIER.equals(entryElement)){
						dataEle = historyEntry.getProgramIdentifier();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_DATA_IDENTIFIER.equals(entryElement)){
						dataEle = historyEntry.getDataIdentifier();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.SW_CS_PERFORMED_BY.equals(entryElement)){
						dataEle = historyEntry.getPerformedBy();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.REMARK.equals(entryElement)){
						dataEle = historyEntry.getRemark();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}else if(PacoFileTagNames.DATE.equals(entryElement)){
						dataEle = historyEntry.getDate();
						setAttributesForEntryElements(dataEle, attributeName, attributeValue);
					}
				}	
			}		
	}
	
	/**
	 * Sets the attributes of the SW-CS-FIELD in HistoryEntry.
	 * 
	 * @param attributeMapList - list of special data attributes
	 * @throws PacoParserException
	 */
	
	public void setAttributesForSpecialData(List attributeMapList) throws PacoParserException {
		List specialDataEleList;
		if(attributeMapList != null && !attributeMapList.isEmpty()){
			for (int attrMapIndex = 0; attrMapIndex < attributeMapList.size(); attrMapIndex++) {
				HashMap attrMap = (HashMap)attributeMapList.get(attrMapIndex);
				//list consists of HashMap key=attrname, value=attrvalue
				for (Iterator attrMapItr = attrMap.keySet().iterator(); attrMapItr.hasNext();) {
					String attributeName = (String) attrMapItr.next();
					String attributeValue = (String)attrMap.get(attributeName);
					specialDataEleList = historyEntry.getSpecialData();
					setAttr(specialDataEleList, attrMapIndex, attributeName, attributeValue);
				}
			}
		}			
	}

  /**
   * @param specialDataEleList
   * @param attrMapIndex
   * @param attributeName
   * @param attributeValue
   */
  private void setAttr(List specialDataEleList, int attrMapIndex, String attributeName, String attributeValue) {
    if(specialDataEleList != null && !specialDataEleList.isEmpty()){
    	for (int dataEleIndex = 0; dataEleIndex < specialDataEleList.size(); dataEleIndex++) {
    		if(attrMapIndex==dataEleIndex){
    			DataElement dataEle = (DataElement)specialDataEleList.get(dataEleIndex);
    			setAttributesForEntryElements(dataEle, attributeName, attributeValue);
    		}
    	}
    }
  }
	
	/**
	 * Sets the attributes of the children elements of SW-CS-ENTRY in HistoryElement.
	 * 
	 * @param dataEle
	 * @param attributeName
	 * @param attributeValue
	 */
	private void setAttributesForEntryElements(DataElement dataEle, String attributeName, String attributeValue){
		if("VIEW".equals(attributeName)){
			dataEle.setView(attributeValue);		
		}else if("S".equals(attributeName)){
			dataEle.setSignature(attributeValue);		
		}else if("T".equals(attributeName)){
			dataEle.setTimeStamp(attributeValue);		
		}else if("SI".equals(attributeName)){
			dataEle.setSemantic(attributeValue);		
		}
	}
	
	/**
	 * Sets the attributes of the SW-CS-ENTRY in HistoryElement.
	 * 
	 * @param attributeName
	 * @param attributeValue
	 */
	private void setAttributesForEntry(String attributeName, String attributeValue){
		if("VIEW".equals(attributeName)){
			historyEntry.setView(attributeValue);		
		}else if("S".equals(attributeName)){
			historyEntry.setSignature(attributeValue);		
		}else if("T".equals(attributeName)){
			historyEntry.setTimeStamp(attributeValue);		
		}else if("SI".equals(attributeName)){
			historyEntry.setSemantic(attributeValue);		
		}
	}

	/**
	 * Sets the context in HistoryElement.
	 */
	public void setContext(String context) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(context);
		historyEntry.setContext(dataElement);		
	}

	/**
	 * Sets the data identifier in HistoryElement.
	 */
	public void setDataIdentifier(String dataIdentifier) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(dataIdentifier);
		historyEntry.setDataIdentifier(dataElement);
	}

	/**
	 * Sets the date in HistoryElement.
	 */
	public void setDate(String date) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(date);
		historyEntry.setDate(dataElement);
	}

	/**
	 * Sets the user in HistoryElement.
	 */
	public void setPerformedBy(String performedBy) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(performedBy);
		historyEntry.setPerformedBy(dataElement);
	}

	/**
	 * Sets the program identifier in HistoryElement.
	 */
	public void setProgramIdentifier(String programIdentifier) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(programIdentifier);
		historyEntry.setProgramIdentifier(dataElement);
	}

	/**
	 * Sets the project information in HistoryElement.
	 */
	public void setProjectInfo(String projectInfo) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(projectInfo);
		historyEntry.setProject(dataElement);
	}

	/**
	 * Sets the remark in HistoryElement.
	 */
	public void setRemark(String remark) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(remark);
		historyEntry.setRemark(dataElement);
	}
	
	/**
	 * Sets the special data in HistoryElement.
	 */
	public void setSpecialData(List specialData) throws PacoParserException {
		List<DataElement> dataEleList = new ArrayList<DataElement>();
		for (Iterator iter = specialData.iterator(); iter.hasNext();) {
			String splData = (String) iter.next();
			dataElement = new DataElement();
			dataElement.setValue(splData);
			dataEleList.add(dataElement);
		}
		historyEntry.setSpecialData(dataEleList);
	}

	/**
	 * Sets the state in HistoryElement.
	 */
	public void setState(String state) throws PacoParserException {
		dataElement = new DataElement();
        //PACP-25
            dataElement.setValue(state);
            historyEntry.setState(dataElement);
	}

	/**
	 * Sets the target variant in HistoryElement.
	 */
	public void setTargetVariant(String targetVariant) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(targetVariant);
		historyEntry.setTargetVariant(dataElement);
	}

	/**
	 * Sets the test object in HistoryElement.
	 */
	public void setTestObject(String testObject) throws PacoParserException {
		dataElement = new DataElement();
		dataElement.setValue(testObject);
		historyEntry.setTestObject(dataElement);
	}
	
	/**
	 * Returns the target class instance.
	 */
	public HistoryEntry getTargetClass(){
		return this.historyEntry;
	}

  

}
