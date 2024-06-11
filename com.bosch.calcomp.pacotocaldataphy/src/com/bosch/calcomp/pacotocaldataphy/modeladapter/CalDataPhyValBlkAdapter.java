package com.bosch.calcomp.pacotocaldataphy.modeladapter;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.calutil.tools.A2LUtil;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.PacoParserObjects;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacoparser.modeladapter.AbstractValBlkCategoryAdapter;
import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;
import com.bosch.calcomp.pacoparser.utility.PacoParserUtil;
import com.bosch.calcomp.pacoparser.utility.SwUnitsCreator;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataPhyValBlk;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     13-Jun-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2     18-Jun-2007     Parvathy Unnikrishnan	Added Exception handling<br>
 * 0.3     19-Jun-2007     Parvathy Unnikrishnan	Added logging<br>
 * 0.4	   12-May-2008	   Deepa					SAC-68, modified setPacoData()<br>
 * 0.5	   10-Jun-2008	   Deepa					SAC-82, made logging mechanism independant of VillaLogger<br>
 * 0.6	   15-Nov-2011	   Dikshita					PACP-17, modified the methods setValues() and getValue().<br>
 */
/**
 * This class implements the specific code for filling of CalDataPhyValBlk.
 * 
 * @author par7kor
 * 
 */
public class CalDataPhyValBlkAdapter extends AbstractValBlkCategoryAdapter {

	/**
	 * CalDataPhyValBlk instance.
	 */
	private CalDataPhyValBlk calDataPhyValBlk;

	/**
	 * Boolean for checking whether the value format is of text.
	 */
	private boolean isText;
	
	private final PacoParser parser;
    
    private final PacoParserObjects pacoParserObjects;
    
    /**
     * @param parser 
     * parser
     * Constructor which creates CalDataPhyCurve.
       */
    public CalDataPhyValBlkAdapter(final PacoParser parser) {
      this.parser = parser;
      this.pacoParserObjects = parser.getPacoParserObjects();
        if (calDataPhyValBlk == null) {
          calDataPhyValBlk = new CalDataPhyValBlk();
          calDataPhyValBlk.setType(A2LUtil.VAL_BLK_TEXT);
        }
    }

	/**
	 * Sets wert values to caldatphy.
	 * 
	 * @param valueList -
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	@Override
    public final void setValues(final List valueList)
	throws PacoParserException {
		//PACP-17 - Changed to 3D array
		AtomicValuePhy[][][] atomicValuePhy = getValue(valueList);
		int atomiValueSize=((ArrayList) valueList.get(0)).size();
		calDataPhyValBlk.setNoOfValues(atomiValueSize);
		calDataPhyValBlk.setAtomicValuePhy(atomicValuePhy);
		calDataPhyValBlk.setText(isText);
	}

	/**
	 * Sets short name.
	 * 
	 * @param shortName -
	 *            label name.
	 */
	public final void setShotName(final String shortName) {
		if (calDataPhyValBlk != null) {
			calDataPhyValBlk.setName(shortName);
		}
	}

	/**
	 * Sets unit.
	 * 
	 * @param unit -
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	@Override
    public final void setUnit(String unit, SwUnitsCreator swUnitsCreator) throws PacoParserException {
		if (calDataPhyValBlk != null) {
			calDataPhyValBlk.setUnit(PacoParserUtil.pacoToDcmUnit(unit, swUnitsCreator));
		}
	}

	/**
	 * This method reads given data and returns the AtomicPhyValue.
	 * 
	 * @param valueList
	 * @return AtomicValuePhy[][][]
	 *///PACP-17
  private AtomicValuePhy[][][] getValue(final List valueList) {
    AtomicValuePhy[][][] atomicValuePhy =
        new AtomicValuePhy[1][valueList.size()][((ArrayList) valueList.get(0)).size()];
    for (int i = 0; i < 1; i++) {
      for (int j = 0; j < valueList.size(); j++) {
        //PACP-16
        @SuppressWarnings("unchecked")
        ArrayList<String> vlist = (ArrayList<String>) valueList.get(j);
        for (int k = 0; k < vlist.size(); k++) {
          String value = vlist.get(k);
          atomicValuePhy[i][j][k] = new AtomicValuePhy(value);
        }
      }
    }
    return atomicValuePhy;
  }

	/**
	 * Returns a boolean to check if the value is in text format.
	 * 
	 * @return boolean.
	 */
	@Override
    public final boolean isText() {
		return isText;
	}

	/**
	 * Sets text flag.
	 * 
	 * @param flag
	 * Sets text flag.
	 */
	@Override
    public final void setTextFlag(final boolean flag) {
		isText = flag;
	}

	/**
	 * Gives back the model. Adds the calDataPhyMap to PacoModelCollection.
	 * 
	 * @throws PacoParserException -
	 *             exception thrown by paco parser plugin.
	 */
	@Override
    public final void setPacoData() throws PacoParserException {
		PacoModelCollection calDataPhyCollection = this.pacoParserObjects.getPacoModelCollection();
		calDataPhyCollection.put(calDataPhyValBlk.getName(), calDataPhyValBlk);

	}

}
