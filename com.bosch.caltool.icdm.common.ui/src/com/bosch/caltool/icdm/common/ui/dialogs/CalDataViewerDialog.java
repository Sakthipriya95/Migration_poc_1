package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldatacomparison.CalDataAttributes;
import com.bosch.calmodel.caldatacomparison.CalDataComparison;
import com.bosch.calmodel.caldatacomparison.CalDataComparisonQuantized;
import com.bosch.calmodel.caldataphyutils.CalDataTableGraphComposite;
import com.bosch.calmodel.caldataphyutils.exception.CalDataTableGraphException;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParamInfo;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * A Modeless dialog used to display table and graph of a parameter
 *
 * @author jvi6cob
 */
public class CalDataViewerDialog extends AbstractDialog {


  /**
   * the message to be shown for the title display
   */
  private final String message;

  /**
   * the parameter for which the table/graph is shown
   */
  // iCDM-1408
  private String paramName;

  // create graph objects
  private int graphColor;
  /**
   * caldata comparison object
   */
  private CalDataComparison calDataComparison;
  /**
   * caldata tableGraph comp object
   */
  private CalDataTableGraphComposite tableGraphComposite;
  // ICDM-1320
  /**
   * List of caldata objects
   */
  // iCDM-1408
  private Map<String, CalData> calDataMap = new TreeMap<String, CalData>();

  /**
   * Map of Names with corresponding colors
   */
  private Map<String, Integer> colorMap;

  // iCDM-1408
  private Composite area;

  // ICDM-2304
  private final boolean isSynchronizedDialogNeeded;

  /**
   * if true, then the difference of values would be indicated as Exclamatory symbol with yellow background in the
   * table/graph
   */
  private final boolean isShowDifferenceNeeded;

  private Map<String, Characteristic> characteristicsMap;


  /**
   * Constructor
   *
   * @param parentShell Shell the parent shell
   * @param calData , the calData to be shown in table/graph
   * @param paramName, the parameter for which the table/graph to be drawn
   * @param message, the message to be displayed
   */
  @SuppressWarnings("javadoc")
  public CalDataViewerDialog(final Shell parentShell, final CalData calData, final String paramName,
      final String message) {
    super(parentShell);
    this.calDataMap.put("Value", calData);
    this.message = message;
    this.paramName = paramName;
    // ICDM-2304
    this.isSynchronizedDialogNeeded = false;
    this.isShowDifferenceNeeded = false;
  }

  /**
   * Constructor
   *
   * @param parentShell Shell the parent shell
   * @param calDataMap the required calDataMap
   * @param paramName the parameter for which the table/graph to be drawn
   * @param message the message to be displayed
   * @param isSynchronizedDialogNeeded , if true then the dialog will be created in synchronization mode
   * @param isShwDiffceNeeded , if true then the diff bt the values will be indicated in the table/graph
   */
  // ICDM-2304
  public CalDataViewerDialog(final Shell parentShell, final Map<String, CalData> calDataMap, final String paramName,
      final String message, final boolean isSynchronizedDialogNeeded, final boolean isShwDiffceNeeded) {
    super(parentShell);
    this.message = message;
    this.paramName = paramName;
    this.isSynchronizedDialogNeeded = isSynchronizedDialogNeeded;
    this.isShowDifferenceNeeded = isShwDiffceNeeded;
    this.calDataMap.putAll(calDataMap);
  }

  @Override
  public void create() {
    super.create();
    setTitleMethod();
    setMessage(this.message);
  }

  /**
   *
   */
  // ICDM-2304
  private void setTitleMethod() {
    if (isSynchronizedDialog()) {
      setTitle("Synchronized Table/Graph Viewer -" + this.paramName);
    }
    else {
      setTitle("Table/Graph Viewer -" + this.paramName);

    }
  }

  @Override
  protected Control createDialogArea(final Composite parent) {
    this.area = (Composite) super.createDialogArea(parent);
    // iCDM-1408
    this.tableGraphComposite = new CalDataTableGraphComposite(this.area, this.area.getHorizontalBar(),
        this.area.getVerticalBar(), CDMLogger.getInstance(), null);

    // iCDM-1408
    // The reason for this method creation is to provide the synchronisation between the Review Result Editor/ Compare
    // HEX Reference Editor & Cal Data
    // Viewer Dialog during a particular parameter selection.
    populateData(this.area);

    final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    final DropTarget target =
        new DropTarget(this.area, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      @Override
      public void dragEnter(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void drop(final DropTargetEvent event) {
        final Object dragData = event.data;
        final IStructuredSelection structuredSelection = (StructuredSelection) dragData;
        List calObjList = structuredSelection.toList();
        for (Object element : calObjList) {
          if (element instanceof SeriesStatisticsInfo) {
            SeriesStatisticsInfo seriesStatisticsInfo = (SeriesStatisticsInfo) element;
            final CalData calData = seriesStatisticsInfo.getCalData();
            addToTableGraphComponent(calData, seriesStatisticsInfo.getCalDataPhyValType().getLabel(),
                seriesStatisticsInfo);
          }
          // Drop From ParametersPage
          else if (element instanceof A2LParamInfo) {
            A2LParamInfo a2lParamInfo = (A2LParamInfo) element;
            CalData calData = a2lParamInfo.getA2lParam().getCalData();
            addToTableGraphComponent(calData, "Value", null);
          }

          else if (element instanceof ScratchPadDataFetcher) {
            ScratchPadDataFetcher data = (ScratchPadDataFetcher) element;
            CalData calData = data.getSeriesStatsInfo().getCalData();
            addToTableGraphComponent(calData, data.getSeriesStatsInfo().getCalDataPhyValType().getLabel(),
                data.getSeriesStatsInfo());
          }
        }
      }
    });

    return this.area;
  }


  /**
   * @return the area
   */
  // iCDM-1408
  public Composite getArea() {
    return this.area;
  }

  /**
   * populates data either from ReviewResultNatActionSet or this class
   *
   * @param area, the area of caldataviewer dialog
   */
  // iCDM-1408
  public void populateData(final Composite area) {
    // ICDM-2304
    setTitleMethod();
    if (CommonUtils.isNullOrEmpty(CalDataViewerDialog.this.characteristicsMap)) {
      CalDataViewerDialog.this.calDataComparison = new CalDataComparison();
    }
    else {
      // Task 234466 include T/G viewer V1.9.0
      CalDataViewerDialog.this.calDataComparison = new CalDataComparisonQuantized();
    }
    // ICDM-1320
    if (!this.calDataMap.isEmpty()) {

      // To avoid hanging when tableGraph is filled for the first time when application is started
      Runnable busyRunnable = new Runnable() {

        @Override
        public void run() {
          try {
            for (Entry<String, CalData> entrySet : CalDataViewerDialog.this.calDataMap.entrySet()) {
              CalDataAttributes checkAttr = new CalDataAttributes(entrySet.getValue(), pickColor(entrySet.getKey()));
              checkAttr.setLabelPrefix(" (" + entrySet.getKey() + ") ");
              // ICDM-2498
              checkAttr.setShowDifferenceIndicator(isShowDifferenceNeeded());
              // Task 234466 include T/G viewer V1.9.0
              if (!CommonUtils.isNullOrEmpty(CalDataViewerDialog.this.characteristicsMap)) {
                CalDataComparisonQuantized calDataComparsonQuntized =
                    (CalDataComparisonQuantized) CalDataViewerDialog.this.calDataComparison;
                calDataComparsonQuntized
                    .setA2lCharacteristic(CalDataViewerDialog.this.characteristicsMap.get(checkAttr.getLabelName()));
              }
              CalDataViewerDialog.this.calDataComparison.addCalDataAttr(checkAttr);
            }
            CalDataViewerDialog.this.tableGraphComposite.fillTableAndGraph(CalDataViewerDialog.this.calDataComparison);
          }
          catch (CalDataTableGraphException excep) {
            CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
          }

        }

      };
      BusyIndicator.showWhile(Display.getDefault(), busyRunnable);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    if (buttonId == IDialogConstants.CLOSE_ID) {
      close();
    }
    super.buttonPressed(buttonId);
  }

  @Override
  protected boolean isResizable() {
    // Not applicable
    return true;
  }


  /**
   * configures the table/graph shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Table/Graph Viewer");
    final Image windowImage = ImageManager.getInstance().getRegisteredImage(ImageKeys.TABLE_GRAPH_16X16);
    newShell.setImage(windowImage);
  }

  /**
   * sets the table/graph shell
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
    setBlockOnOpen(false);
  }

  /**
   * @param calData
   * @param seriesStatisticsInfo
   * @param seriesStatisticsInfo
   */
  private void addToTableGraphComponent(final CalData calData, final String labelPrefix,
      final SeriesStatisticsInfo seriesStatisticsInfo) {
    if (calData != null) {
      CalDataAttributes calAttr = new CalDataAttributes(calData, pickColor(null));
      calAttr.setShowDifferenceIndicator(true);
      if (CommonUtils.isNotNull(seriesStatisticsInfo)) {
        calAttr.setLabelPrefix(" (" + seriesStatisticsInfo.getCalData().getShortName() + "::" + labelPrefix + "::" +
            seriesStatisticsInfo.getCalData().getCalDataPhy().getSimpleDisplayValue() +
            seriesStatisticsInfo.getDataSetName() + ") ");
      }
      else {
        calAttr.setLabelPrefix(" (" + labelPrefix + ") ");
      }
      if ((CalDataViewerDialog.this.calDataComparison.getCalDataAttr(0) != null) &&
          !CalDataViewerDialog.this.calDataComparison.getCalDataAttr(0).getCalDataPhy().getType()
              .equals(calAttr.getCalDataPhy().getType())) {
        CDMLogger.getInstance().errorDialog("Only parameters of the same type can be compared",
            com.bosch.caltool.icdm.common.util.Activator.PLUGIN_ID);
        return;
      }
      CalDataViewerDialog.this.calDataComparison.addCalDataAttr(calAttr);
      try {
        CalDataViewerDialog.this.tableGraphComposite.fillTableAndGraph(CalDataViewerDialog.this.calDataComparison);
      }
      catch (CalDataTableGraphException excep) {
        CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * ICDM-1657 Picks color from available colors. Dropping caldata objects cannot be assigned colors as they are always
   * considered as null because dropping objects with a predefined color creates a scenario where the predefined color
   * is already used.
   *
   * @return
   */
  private int pickColor(final String labelName) {
    if (CalDataViewerDialog.this.colorMap != null) {
      Integer mappedColor = CalDataViewerDialog.this.colorMap.get(labelName);
      if (mappedColor != null) {
        return mappedColor;
      }
      while (CalDataViewerDialog.this.colorMap.values().contains(CalDataViewerDialog.this.graphColor) &&
          (CalDataViewerDialog.this.graphColor <= 9)) {
        CalDataViewerDialog.this.graphColor++;
      }
    }
    int colorToUse = CalDataViewerDialog.this.graphColor;
    CalDataViewerDialog.this.graphColor++;
    return colorToUse;
  }

  /**
   * Dropping caldata objects cannot be assigned colors as they are always considered as null because dropping objects
   * with a predefined color creates a scenario where the predefined color is already used.
   *
   * @param colorMap the colorMap to set
   */
  public void setColorMap(final Map<String, Integer> colorMap) {
    this.colorMap = colorMap;
  }


  /**
   * @return the calDataMap
   */
  // iCDM-1408
  public Map<String, CalData> getCalDataMap() {
    return this.calDataMap;
  }


  /**
   * @param calDataMap the calDataMap to set
   */
  // iCDM-1408
  public void setCalDataMap(final Map<String, CalData> calDataMap) {
    this.calDataMap = calDataMap;
  }


  /**
   * gets the parameter name
   *
   * @return the paramName
   */
  // iCDM-1408
  public String getParamName() {
    return this.paramName;
  }

  /**
   * sets the parameter name
   *
   * @param paramName
   */
  // iCDM-1408
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the tableGraphComposite
   */
  // iCDM-1408
  public CalDataTableGraphComposite getTableGraphComposite() {
    return this.tableGraphComposite;
  }


  /**
   * @return the isUnSynchronizedDialog
   */
  // ICDM-2304
  private boolean isSynchronizedDialog() {
    return this.isSynchronizedDialogNeeded;
  }


  /**
   * this variable is set in the constructor declaration
   *
   * @return the isShowDifferenceNeeded
   */
  // ICDM-2498
  private boolean isShowDifferenceNeeded() {
    return this.isShowDifferenceNeeded;
  }


  /**
   * @return the characteristicsMap
   */
  public Map<String, Characteristic> getCharacteristicsMap() {
    return this.characteristicsMap;
  }


  /**
   * @param characteristicsMap the characteristicsMap to set
   */
  public void setCharacteristicsMap(final Map<String, Characteristic> characteristicsMap) {
    this.characteristicsMap = characteristicsMap;
  }

}