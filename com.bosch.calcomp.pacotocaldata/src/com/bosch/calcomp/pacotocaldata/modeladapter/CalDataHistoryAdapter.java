package com.bosch.calcomp.pacotocaldata.modeladapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.SWHistoryAdapter;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 12-May-2008 Deepa SAC-68, First Draft<br>
 * 0.2 26-May-2008 Deepa Modified constructor<br>
 * 0.3 10-Jun-2008 Deepa SAC-82, made logging mechanism independant of VillaLogger<br>
 */

/**
 * CalDataHistoryAdapter This class implements the specific code to populate CalDataHistory.
 * 
 * @author dec1kor
 */
public class CalDataHistoryAdapter implements SWHistoryAdapter {

  /**
   * CalDataHistory instance.
   */
  private static CalDataHistory calDataHistory;

  /**
   * CalDataHistoryAdapter instance.
   */
  static CalDataHistoryAdapter calDataHistoryAdapter;


  /**
   * CalDataHistoryAdapter constructor.
   */
  public CalDataHistoryAdapter() {
    calDataHistory = new CalDataHistory();
  }

  /**
   * Sets the attributes of the SW-CS-HISTORY to the CalDataHistory.
   * 
   * @param attributeMap - attributes to be set
   * @throws PacoParserException
   */
  public void setAttributes(Map<?, ?> attributeMap) throws PacoParserException {
    if (attributeMap != null) {
      calDataHistory.setAttributes((Map<String, String>) attributeMap);
      for (Iterator attrMapItr = attributeMap.keySet().iterator(); attrMapItr.hasNext();) {
        String attributeName = (String) attrMapItr.next();
        String attributeValue = (String) attributeMap.get(attributeName);
        if ("VIEW".equals(attributeName)) {
          calDataHistory.setView(attributeValue);
        }
        else if ("S".equals(attributeName)) {
          calDataHistory.setSignature(attributeValue);
        }
        else if ("T".equals(attributeName)) {
          calDataHistory.setTimeStamp(attributeValue);
        }
        else if ("SI".equals(attributeName)) {
          calDataHistory.setSemantic(attributeValue);
        }
      }
    }
  }

  /**
   * Sets the history entries in CalDataHistory.
   * 
   * @param historyEntryList - the entries to be set.
   * @throws PacoParserException
   */
  public void setHistoryEntries(List<?> historyEntryList) throws PacoParserException {
    List<HistoryEntry> historyEntryClassList = new ArrayList<>();
    for (Iterator entryListIter = historyEntryList.iterator(); entryListIter.hasNext();) {
      CalDataHistoryEntryAdapter histEntryAdapter = (CalDataHistoryEntryAdapter) entryListIter.next();
      histEntryAdapter.getTargetClass();
      // add to the list
      historyEntryClassList.add(histEntryAdapter.getTargetClass());
    }
    calDataHistory.setHistoryEntryList(historyEntryClassList);
  }

  /**
   * Returns the target class instance.
   * 
   * @return CalDataHistory
   */
  public static CalDataHistory getTargetClass() {
    return calDataHistory;
  }
}
