/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataMerger;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;

/**
 * Create display change envent from local/remote changes
 *
 * @author bne4cob
 */
public class DisplayEventCreator {

  /**
   * @param chgDataList changes
   * @return DisplayChangeEvent
   */
  public DisplayChangeEvent createLocalDce(final List<ChangeData<?>> chgDataList) {
    DisplayChangeEvent dce = new DisplayChangeEvent();
    ChangeDataMerger merger = new ChangeDataMerger();
    dce.setSource(CHANGE_SOURCE.LOCAL);
    chgDataList.forEach(chData -> addToDce(chData, dce, merger));
    return dce;
  }

  /**
   * @param changeEventMap list of remote changes
   * @return DisplayChangeEvent
   */
  public DisplayChangeEvent createRemoteDce(final Map<Long, ChangeEvent> changeEventMap) {
    DisplayChangeEvent dce = new DisplayChangeEvent();
    dce.setSource(CHANGE_SOURCE.REMOTE);

    ChangeDataMerger merger = new ChangeDataMerger();

    SortedSet<Long> sortedEventSet = new TreeSet<>(changeEventMap.keySet());
    sortedEventSet.forEach(evntId -> addToDce(merger, dce, changeEventMap.get(evntId)));
    return dce;
  }

  /**
   * @param dce DisplayChangeEvent
   * @param changeEvent ChangeEvent
   */
  private void addToDce(final ChangeDataMerger merger, final DisplayChangeEvent dce, final ChangeEvent changeEvent) {
    changeEvent.getChangeDataMap().values()
        .forEach(chMap -> chMap.values().forEach(chData -> addToDce(chData, dce, merger)));
  }

  /**
   * @param chData ChangeData
   * @param dce DisplayChangeEvent
   * @param merger ChangeDataMerger
   */
  private void addToDce(final ChangeData<?> chData, final DisplayChangeEvent dce, final ChangeDataMerger merger) {
    Map<Long, ChangeData<?>> typeDataMap =
        dce.getConsChangeData().computeIfAbsent(chData.getType(), e -> new HashMap<>());
    merger.merge(chData, typeDataMap);
  }


}
