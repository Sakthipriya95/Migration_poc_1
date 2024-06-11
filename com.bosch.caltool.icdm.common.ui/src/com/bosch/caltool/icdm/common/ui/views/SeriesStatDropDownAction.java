/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.caltool.icdm.common.ui.actions.SeriesStatisticsCacheHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.data.SeriesStatCache;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * Dynamic menu contribution for series statistics search results <br>
 * iCDM-913
 *
 * @author adn1cob
 */
public class SeriesStatDropDownAction extends Action implements IMenuCreator {

  /*
   * Holds the menu
   */
  private Menu fMenu;
  /*
   * List of cached labels
   */
  private List<LabelInfoVO> listLabelInfo;

  SeriesStatDropDownAction() {
    super();
    setMenuCreator(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    if (this.fMenu != null) {
      this.fMenu.dispose();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Menu getMenu(final Control parent) {
    this.fMenu = new Menu(parent);

    Map<String, LabelInfoVO> mapOfSeriesStatistics = SeriesStatCache.getInstance().getMapOfSeriesStatistics();
    // Sort the list
    this.listLabelInfo = sortLabels(mapOfSeriesStatistics);
    // Create title menu
    createTitleMenu();
    // Create dynamic menus
    createMenuActions(this.listLabelInfo);
    return this.fMenu;
  }

  /**
   * Creates the title for the menu
   */
  private void createTitleMenu() {
    // iCDM-1198
    SeriesStatisticsCacheHandler clearHist = new SeriesStatisticsCacheHandler(true);
    clearHist.setText("Clear history ...");

    clearHist.setEnabled(!this.listLabelInfo.isEmpty());

    ImageDescriptor imgDesc = ImageManager.getImageDescriptor(ImageKeys.CLEAR_HIST_16X16);
    clearHist.setImageDescriptor(imgDesc);
    addActionToMenu(this.fMenu, clearHist);
    addSeperator(this.fMenu);
  }

  /**
   * @param listLabelInfo
   */
  private void createMenuActions(final List<LabelInfoVO> listLabelInfo) {
    for (LabelInfoVO obj : listLabelInfo) {
      SeriesStatisticsCacheHandler seriesAction = new SeriesStatisticsCacheHandler(obj.getLabelName(), obj);
      seriesAction.setText(obj.getLabelName());
      ImageDescriptor imgDesc = ImageManager.getImageDescriptor(ImageKeys.SERIES_STATISTICS_16X16);
      if (obj.getPeakValue() != null) {
        imgDesc = CommonUiUtils.getIconForParamType(obj.getPeakValue().getType());
        if (imgDesc == null) {
          imgDesc = ImageManager.getImageDescriptor(ImageKeys.SERIES_STATISTICS_16X16);
        }
      }
      seriesAction.setImageDescriptor(imgDesc);
      addActionToMenu(this.fMenu, seriesAction);
    }
  }

  /**
   * @param mapOfSeriesStatistics
   * @return
   */
  private List<LabelInfoVO> sortLabels(final Map<String, LabelInfoVO> mapOfSeriesStatistics) {
    List<LabelInfoVO> tempListLabelInfo = new ArrayList<LabelInfoVO>();
    tempListLabelInfo.addAll(mapOfSeriesStatistics.values());
    Collections.sort(tempListLabelInfo, new Comparator<LabelInfoVO>() {

      /**
       * Compare collections
       *
       * @param paramT1 param1
       * @param paramT2 param2
       * @return 0 if equal
       */
      @Override
      public int compare(final LabelInfoVO paramT1, final LabelInfoVO paramT2) {
        return ApicUtil.compare(paramT1.getLabelName(), paramT2.getLabelName());
      }
    });
    return tempListLabelInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Menu getMenu(final Menu parent) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param parent
   * @param action
   */
  protected void addActionToMenu(final Menu parent, final Action action) {
    ActionContributionItem item = new ActionContributionItem(action);
    item.fill(parent, -1);
  }

  /**
   * iCDM-1198 <br>
   * Add seperator
   */
  protected void addSeperator(final Menu parent) {
    Separator item = new Separator();
    item.fill(parent, -1);
  }

}
