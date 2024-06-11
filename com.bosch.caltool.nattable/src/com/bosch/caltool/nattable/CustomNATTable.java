package com.bosch.caltool.nattable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.stack.DummyGridLayerStack;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.nattable.exception.NatTableRuntimeException;


/**
 * Extension of nebula NAT table
 *
 * @author adn1cob
 */
public class CustomNATTable extends NatTable {

  /**
   *
   */
  private static final String NAT_TABLE_STATE = "NAT_TABLE_STATE";
  /**
   *
   */
  private static final String PROPKEY_COLUMN_INDEX_MAP = "CustomNATTable.COLUMN_INDEX_MAP";
  /**
   * ID of the Nat table (store the properties)
   */
  private String tableId;
  private String pVersion;
  private boolean isComboBoxFilterHeaderEnabled;
  /**
   * Column Index (key) and Label (value) Map
   */
  private Map<Integer, String> columnIndexLabelMap;

  /**
   * Constructor
   *
   * @param parent parent
   */
  public CustomNATTable(final Composite parent) {
    super(parent);
  }

  /**
   * Constructor
   *
   * @param parent parent
   * @param autoconfigure boolean
   */
  public CustomNATTable(final Composite parent, final boolean autoconfigure) {
    super(parent, autoconfigure);
  }

  /**
   * Constructor
   *
   * @param parent parent
   * @param layer layer
   */
  public CustomNATTable(final Composite parent, final ILayer layer) {
    super(parent, layer);
  }

  /**
   * Constructor
   *
   * @param parent parent
   * @param layer layer
   * @param autoconfigure boolean
   */
  public CustomNATTable(final Composite parent, final ILayer layer, final boolean autoconfigure) {
    super(parent, layer, autoconfigure);
  }

  /**
   * Constructor
   *
   * @param parent parent
   * @param style style
   */
  public CustomNATTable(final Composite parent, final int style) {
    super(parent, style, new DummyGridLayerStack());
  }

  /**
   * Constructor
   *
   * @param parent parent
   * @param style style
   * @param autoconfigure boolean
   */
  public CustomNATTable(final Composite parent, final int style, final boolean autoconfigure) {
    super(parent, style, new DummyGridLayerStack(), autoconfigure);
  }

  /**
   * Constructor
   *
   * @param parent parent
   * @param style style
   * @param layer layer
   */
  public CustomNATTable(final Composite parent, final int style, final ILayer layer) {
    super(parent, style, layer, true);
  }

  /**
   * Constructor with table id(to save properties)
   *
   * @param parent parent
   * @param style style
   * @param layer layer
   * @param autoconfigure boolean
   * @param tableId Nat table id for storing the properties
   */
  public CustomNATTable(final Composite parent, final int style, final ILayer layer, final boolean autoconfigure,
      final String tableId) {
    super(parent, style, layer, autoconfigure);
    this.tableId = tableId;
  }

  /**
   * Constructor with table id(to save properties) and column properties map to compare columns if there is a version
   * change
   *
   * @param parent parent
   * @param style style
   * @param layer layer
   * @param autoconfigure boolean
   * @param tableId Nat table id for storing the properties
   * @param columnIndexLabelMap Column Index (key) and Label (value) Map
   */
  public CustomNATTable(final Composite parent, final int style, final ILayer layer, final boolean autoconfigure,
      final String tableId, final Map<Integer, String> columnIndexLabelMap) {
    super(parent, style, layer, autoconfigure);
    this.tableId = tableId;
    this.columnIndexLabelMap = columnIndexLabelMap;
  }


  /**
   * Load saved state of NAT table
   *
   * @throws IOException io exception
   */
  public void loadState() throws IOException {
    // Check if the col index label map is not initialized
    // This method should not be called without initializing the map
    // throw run time error to notify the user
    assertColIndexLabelMapNotEmpty();

    Properties prop = new Properties();
    String path = getNATProperties();
    File propFile = new File(path);
    if (propFile.exists()) {
      try (FileInputStream napPropFileStream = new FileInputStream(propFile)) {
        prop.load(napPropFileStream);
      }
      String saveStateVersion = prop.getProperty("VERSION");
      saveStateVersion = (null == saveStateVersion) ? "" : saveStateVersion;
      this.pVersion = (null == this.pVersion) ? "" : this.pVersion;
      // Check if Tool version is updated
      if (saveStateVersion.equals(this.pVersion)) {
        this.loadState(this.tableId, prop);
      }
      else {
        loadStateForVersionChange(prop);
      }
    }

  }

  /**
   * @param prop
   */
  private void loadStateForVersionChange(final Properties prop) {
    // If the version is changed, get the column index map from properties file
    // Comparing it with the propertyToLabelMap from the NAT Table page
    // load the saved state only if both the values are same
    String columnMapProp = prop.getProperty(PROPKEY_COLUMN_INDEX_MAP);
    // If the column map property is null, then the page is opened 1st time after releasing this feature
    // So in this case, do not load state as well as do not display info message
    if (columnMapProp != null) {
      if (columnMapProp.equalsIgnoreCase(this.columnIndexLabelMap.toString())) {
        // if the column and label index mapping in the class and properties matches ,
        // then load the state
        this.loadState(this.tableId, prop);
      }
      else {
        // Information dialog to the user to inform when NAT table conf is reset
        MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Table Configurations Reset",
            "Table columns are modified in the new version. Previous table preferences will be cleared.");
      }
    }
  }

  /**
   * Get NAT table properties path for the specified table name
   *
   * @return nat properties for the table
   * @throws IOException
   */
  private String getNATProperties() {
    String url = Platform.getInstanceLocation().getURL().getPath();
    File dir = new File(url + "nattable_configuration");
    if (!dir.exists() && !dir.mkdir()) {
      return url + "/" + this.tableId + ".properties";
    }
    return dir.getAbsolutePath() + "/" + this.tableId + ".properties";
  }


  /**
   * Save current state for the NAT table
   *
   * @throws IOException io exception
   */
  public void saveState() throws IOException {
    // Check if the col index label map is not initialized
    // This method should not be called without initializing the map
    // throw run time error to notify the user
    assertColIndexLabelMapNotEmpty();

    Properties prop = new Properties();
    String path = getNATProperties();
    this.saveState(this.tableId, prop);
    if ((null != this.pVersion) && !this.pVersion.isEmpty()) {
      prop.setProperty("VERSION", this.pVersion);
    }

    addColIndexLabelMapToProps(prop);

    if (this.isComboBoxFilterHeaderEnabled) {
      // all the values in columns are set when saving the state
      String oldValue = (String) prop.get(this.tableId + ".Grid.COLUMN_HEADER.FILTER_ROW.filterTokens");
      String[] valArray = oldValue.split("\\|");
      StringBuilder filterRowHeader = new StringBuilder();
      for (String currentVal : valArray) {
        String[] subVal = currentVal.split(":");
        subVal[1] = ":SELECT_ALL|";
        filterRowHeader.append(subVal[0]).append(subVal[1]);
      }
      // Property is set for combobox filter to fix issue with saving the filter state
      prop.setProperty(this.tableId + ".Grid.COLUMN_HEADER.FILTER_ROW.filterTokens", filterRowHeader.toString());
    }
    try (OutputStream propOutStream = new FileOutputStream(path)) {
      prop.store(propOutStream, NAT_TABLE_STATE);
    }
  }

  /**
   * @param prop
   */
  private void addColIndexLabelMapToProps(final Properties prop) {
    // Storing the column index label mapping to check if columns are modified on ICDM version update
    prop.setProperty(PROPKEY_COLUMN_INDEX_MAP, this.columnIndexLabelMap.toString());
  }

  /**
   * Reset current state for the NAT table
   *
   * @throws IOException io exception
   */
  public void resetState() throws IOException {
    // Check if the col index label map is not initialized
    // This method should not be called without initializing the map
    // throw run time error to notify the user
    assertColIndexLabelMapNotEmpty();

    Properties prop = new Properties();
    String path = getNATProperties();
    prop.clear();
    this.saveState(this.tableId, prop);
    addColIndexLabelMapToProps(prop);
    try (OutputStream propOutStream = new FileOutputStream(path)) {
      prop.store(propOutStream, NAT_TABLE_STATE);
    }
  }

  private void assertColIndexLabelMapNotEmpty() {
    if ((null == this.columnIndexLabelMap) || this.columnIndexLabelMap.isEmpty()) {
      throw new NatTableRuntimeException(
          "Column Index and Label Property Map is Null or Empty, This value to be passed to the contructor while creating NAT table object to use this function.");
    }
  }

  /**
   * @param pVersion This method sets the application version . If this method is invoked after creating the
   *          CustomNATTable instance , only if the version while saving and the version while loading are equal, the
   *          saved state is loaded.
   */
  public void setProductVersion(final String pVersion) {
    this.pVersion = pVersion;
  }

  /**
   * @return the isComboBoxFilterHeaderEnabled
   */
  public boolean isComboBoxFilterHeaderEnabled() {
    return this.isComboBoxFilterHeaderEnabled;
  }


  /**
   * @param isComboBoxFilterHeaderEnabled the isComboBoxFilterHeaderEnabled to set
   */
  public void setComboBoxFilterHeaderEnabled(final boolean isComboBoxFilterHeaderEnabled) {
    this.isComboBoxFilterHeaderEnabled = isComboBoxFilterHeaderEnabled;
  }
}
