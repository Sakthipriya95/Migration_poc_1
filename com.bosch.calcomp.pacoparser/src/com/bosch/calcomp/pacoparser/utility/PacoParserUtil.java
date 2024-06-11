package com.bosch.calcomp.pacoparser.utility;

import java.io.File;

import com.bosch.calcomp.pacoparser.exception.PacoParserException;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     15-Jun-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2	   02-Sep-2008	   Parvathy					SAC-113, added validateFilePath <br>
 * 0.3 	   22-Oct-2010	   Jagan					PACP-6,2, added getFunctionVersion
 */
/**
 * Utility class for paco parser.
 * 
 * @author par7kor
 * 
 */
public final class PacoParserUtil {
	/**
	 * Empty String.
	 */
	 //PACP-10
	public static final String EMPTY_STRING = "";
	
	/**
	 * Function which converts the paco unit into readable DCM unit eg: q0 of
	 * paco into "-" and degqNC of paco into DCM "deg C". This gets the value
	 * stored in a Map whose key is the pacoUnit.
	 * 
	 * @param pacoUnit -
	 *            the unit representation in the paco file.
	 * @return String
	 */
	public static String pacoToDcmUnit(final String pacoUnit, final SwUnitsCreator swUnitsCreator) {
		String dcmUnit = pacoUnit;
//		SwUnitsCreator swUnitsCreator = new SwUnitsCreator();
		if (swUnitsCreator.getUnitsMap() != null) {
			dcmUnit = (String) swUnitsCreator.getUnitsMap().get(pacoUnit);
		}
		if(dcmUnit == null){//PACP-10
			dcmUnit = PacoParserUtil.EMPTY_STRING;
		}
		return dcmUnit;
	}
	
	/**
	 * Function which gives the function version for the given function name
	 * 
	 * @param functionName -
	 *            the shortName of the SW-FEATURE.
	 * @return String - the function version (REVISION-LABEL), returns null if not present
	 */ //PACP-2,6 , final SwFeatureCreator swFeatureCreator
	public static String getFunctionVersion(final String functionName, final SwFeatureCreator swFeatureCreator) {
		String functionVersion = null;
//		SwFeatureCreator swFeatureCreator = new SwFeatureCreator();
		if (swFeatureCreator.getVersionMap() != null) {
			functionVersion = swFeatureCreator.getVersionMap().get(functionName);
		}		
		return functionVersion;
	}

	/**
	 * Validates the file path and file extension of paco file.
	 * 
	 * @param filePath
	 * @throws PacoParserException
	 */
	public static void validateFilePath(String filePath)
			throws PacoParserException {
		if (filePath != null) {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new PacoParserException(PacoParserException.INVALID_FILE,
						"The Paco file path given is invalid.");
			} else {
				if (file.isDirectory()) {
					throw new PacoParserException(
							PacoParserException.INVALID_FILE,
							"Please provide file path and not the folder location.");
				} else if (!(file.getAbsolutePath().endsWith(".xml") || file
						.getAbsolutePath().endsWith(".XML"))) {
					throw new PacoParserException(
							PacoParserException.INVALID_FILE,
							"The file type of the input file is invalid.");
				}
			}
		} else {
			throw new PacoParserException(PacoParserException.INVALID_FILE,
					"The input file given is invalid.");
		}
	}

}
