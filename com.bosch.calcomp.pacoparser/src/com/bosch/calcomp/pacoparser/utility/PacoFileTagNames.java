package com.bosch.calcomp.pacoparser.utility;

/**
 * Revision History<br>
 * 
 * Version		Date			Name				Description<br>
 * 0.1     15-May-2007     Parvathy Unnikrishnan	First Draft<br>
 * 0.2	   12-May-2008	   Deepa					SAC-68, added tag names related to the SW-CS-HISTORY<br>
 * 													and SW-CS-ENTRY<br>
 * 0.3	   22-Oct-2010	   Jagan                    PACP-2,6, added tag names to support function version <br>
 * 													<REVISION-LABEL>
 * 0.4	   28-Mar-2011	   Dikshitha                PACP-15, added a new category with the value SW_COMPONENT <br>
 */
/**
 * This class contains the tags of paco file which are used for parsing.
 * 
 * @author par7kor
 * 
 */
public class PacoFileTagNames {
  
    private PacoFileTagNames() {
      //restricting from create object
    }
  
	/**
	 * SW_UNITS tag which is container for SW-UNIT tags.
	 */
	public static final String SW_UNITS = "SW-UNITS";

	/**
	 * SW_UNIT tag which describe a unit of measurement for calibration items.
	 */
	public static final String SW_UNIT = "SW-UNIT";

	/**
	 * SW-INSTANCE tag which represents the textual presentation of a unit of
	 * measurement.
	 */
	public static final String SW_UNIT_DISPLAY = "SW-UNIT-DISPLAY";

	/**
	 * SW-INSTANCE-TREE tag which contains set of SW-INSTANCE.
	 */
	public static final String SW_INSTANCE_TREE = "SW-INSTANCE-TREE";

	/**
	 * SW-INSTANCE tag which represents one calibration item.
	 */
	public static final String SW_INSTANCE = "SW-INSTANCE";

	/**
	 * LONG-NAME tag which holds long name of the label.
	 */
	public static final String LONGNAME = "LONG-NAME";

	/**
	 * SHORT-NAME tag which hold shot name of the label.
	 */
	public static final String SHORTNAME = "SHORT-NAME";

	/**
	 * CATEGORY tag which represents the nature of the sw-instance like cure,
	 * map, value etc.
	 */
	public static final String CATEGORY = "CATEGORY";

	/**
	 * SW-FEATURE-REF tag represents a link to SW-FEATURE.
	 */
	public static final String SW_FEATURE_REF = "SW-FEATURE-REF";

	/**
	 * SW-INSTANCE-PROPS-VARIANTS tag VARIANTS> is the container which takes all
	 * variants of one particular calibration item.
	 */
	public static final String SW_INSTANCE_PROPS_VARIANTS = "SW-INSTANCE-PROPS-VARIANTS";

	/**
	 * SW-INSTANCE-PROPS-VARIANT tag describes one particular variant of the
	 * current calibration item.
	 */
	public static final String SW_INSTANCE_PROPS_VARIANT = "SW-INSTANCE-PROPS-VARIANT";

	/**
	 * SW-AXIS-CONTS tag represents the contents of all axes of one particular
	 * calibration item.
	 */
	public static final String SW_AXIS_CONTS = "SW-AXIS-CONTS";

	/**
	 * SW-AXIS-CONT tag takes the contents of one particular axis (denoted by
	 * SW-AXIS-INDEX of the current calibration item.
	 */
	public static final String SW_AXIS_CONT = "SW-AXIS-CONT";

	/**
	 * SW-UNIT-REF tag is a formal reference to a measurement unit.
	 */
	public static final String SW_UNIT_REF = "SW-UNIT-REF";

	/**
	 * SW-AXIS-INDEX denotes the index of the current axis for which the
	 * contents is specified. The index uses the following convention: <br>
	 * 0: This is the value axis of a curve or a map <br>
	 * 1: This is the X-axis of a curve or a map. <br>
	 * 2: This is the Y-axis of a map <br>
	 * 3: This is the Z-axis of a cuboid
	 */
	public static final String SW_AXIS_INDEX = "SW-AXIS-INDEX";

	/**
	 * SW-VALUES-PHYS tag represents the physical values of the calibration
	 * item.
	 */
	public static final String SW_VALUES_PHYS = "SW-VALUES-PHYS";

	/**
	 * VG represents a group of Values which belong together.
	 */
	public static final String VG = "VG";

	/**
	 * VT represents one particular textual value of the the calibration item.
	 */
	public static final String VT = "VT";

	/**
	 * V represents one particular numerical value of the the calibration item.
	 */
	public static final String V = "V";

	/**
	 * Represents characteristic object type VALUE.
	 */
	public static final String CAT_VALUE = "VALUE";

	/**
	 * Represents characteristic object type VALUE BLOCK.
	 */
	public static final String CAT_VALUE_BLOCK = "VALUE_BLOCK";

	/**
	 * Represents characteristic object type Characteric text.
	 */
	public static final String CAT_ASCII = "ASCII";

	/**
	 * Represents characteristic object type group data points.
	 */
	public static final String CAT_AXIS_VALUES = "AXIS_VALUES";

	/**
	 * Represents characteristic object type characteristic curve.
	 */
	public static final String CAT_CURVE_INDIVIDUAL = "CURVE_INDIVIDUAL";

	/**
	 * Represents characteristic object type fixed characteristic curve.
	 */
	public static final String CAT_CURVE_FIXED = "CURVE_FIXED";

	/**
	 * Represents characteristic object type group characteristic curve.
	 */
	public static final String CAT_CURVE_GROUPED = "CURVE_GROUPED";

	/**
	 * Represents characteristic object type characteristic map.
	 */
	public static final String CAT_MAP_INDIVIDUAL = "MAP_INDIVIDUAL";

	/**
	 * Represents characteristic object type fixed characteristic map.
	 */
	public static final String CAT_MAP_FIXED = "MAP_FIXED";

	/**
	 * Represents characteristic object type group characteristic map.
	 */
	public static final String CAT_MAP_GROUPED = "MAP_GROUPED";
	
	/**
	 * History tag of the sw-instance.
	 */
	public static final String SW_CS_HISTORY = "SW-CS-HISTORY";
	
	/**
	 * Entry tag of the sw-cs-history.
	 */
	public static final String SW_CS_ENTRY = "SW-CS-ENTRY";
	
	/**
	 * State of the entry.
	 */
	public static final String SW_CS_STATE = "SW-CS-STATE";
	
	/**
	 * Context of the entry.
	 */
	public static final String SW_CS_CONTEXT = "SW-CS-CONTEXT";
	
	/**
	 * Project information of the entry.
	 */
	public static final String SW_CS_PROJECT_INFO = "SW-CS-PROJECT-INFO";
	
	/**
	 * Target variant of the entry.
	 */
	public static final String SW_CS_TARGET_VARIANT = "SW-CS-TARGET-VARIANT";
	
	/**
	 * Test object of the entry.
	 */
	public static final String SW_CS_TEST_OBJECT = "SW-CS-TEST-OBJECT";
	
	/**
	 * Program identifier of the entry.
	 */
	public static final String SW_CS_PROGRAM_IDENTIFIER = "SW-CS-PROGRAM-IDENTIFIER";
	
	/**
	 * Data identifier of the entry.
	 */
	public static final String SW_CS_DATA_IDENTIFIER = "SW-CS-DATA-IDENTIFIER";
	
	/**
	 * User of the entry.
	 */
	public static final String SW_CS_PERFORMED_BY = "SW-CS-PERFORMED-BY";
	
	/**
	 * Remarks of the entry.
	 */
	public static final String REMARK = "REMARK";
	
	/**
	 * Paragraph of the remarks in the entry.
	 */
	public static final String REMARK_PARAGRAPH = "P";
	
	/**
	 * Date of the entry.
	 */
	public static final String DATE = "DATE";
	
	/**
	 * Special data of the entry.
	 */
	public static final String SW_CS_FIELD = "SW-CS-FIELD";
	
	/**
	 * Represents the no status symbol for a label.
	 */
	public static final String NO_STATE_SYMBOL = "---";
	
	/**
	 * Represents different components of the Label, Components consists of one or more SW-FEATURE
	 */ //PACP-2,6
	public static final String SW_COMPONENTS = "SW-COMPONENTS";
	
	/**
	 * Represents the function version
	 */ //PACP-2,6
	public static final String REVISION_LABEL = "REVISION-LABEL";
	
	/**
	 * Represents characteristic object type SW_COMPONENT.
	 */ //PACP-15
	public static final String CAT_SW_COMPONENT = "SW_COMPONENT";
}
