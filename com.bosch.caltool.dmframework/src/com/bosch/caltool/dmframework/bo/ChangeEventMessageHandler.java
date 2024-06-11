/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataMerger;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;

/**
 * @author bne4cob
 */
class ChangeEventMessageHandler extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData Service Data
   */
  public ChangeEventMessageHandler(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param executor executor
   */
  public void send(final CommandExecuter executor) {
    ChangeEvent event = createEvent(executor);
    CnsMessageSender.send(getServiceData(), event);
  }

  private ChangeEvent createEvent(final CommandExecuter executor) {
    ChangeEvent event = new ChangeEvent();
    long eventId = getNextChangeId();
    event.setChangeId(eventId);
    event.setServiceId(executor.getServiceData().getServiceId());
    event.setChangeDataMap(createChangeDataMap(eventId, executor));
    getLogger().debug("Change event created. Id = {},  Total changes = {}", eventId, getChangeCount(event));
    return event;
  }

  /**
   * @param eventId
   * @param executor
   * @return
   */
  private Map<String, Map<Long, ChangeData<?>>> createChangeDataMap(final long eventId,
      final CommandExecuter executor) {

    Map<String, Map<Long, ChangeData<?>>> retMap = new HashMap<>();
    ChangeDataCreatorForCommand creator = new ChangeDataCreatorForCommand();
    ChangeDataMerger merger = new ChangeDataMerger();

    // Prepare change data from main commands
    addToChangeDataMap(creator.createData(eventId, executor.getMainCommandList(), false), retMap, merger);

    // Prepare change data from child commands
    addToChangeDataMap(creator.createData(eventId, executor.getChildCommandList(), true), retMap, merger);

    return retMap;
  }

  private void addToChangeDataMap(final List<ChangeData<?>> chDtaList,
      final Map<String, Map<Long, ChangeData<?>>> consCdMap, final ChangeDataMerger merger) {

    Map<Long, ChangeData<?>> typeDataMap;

    for (ChangeData<?> data : chDtaList) {
      typeDataMap = consCdMap.computeIfAbsent(data.getType().getTypeCode(), e -> new HashMap<>());
      merger.merge(data, typeDataMap);
    }
  }

  /**
   * @return
   */
  private long getNextChangeId() {
    Query cdIdQry = getEntMgr().createNativeQuery("select SEQV_CHANGE_EVENT_ID.nextval from DUAL");
    BigDecimal nextCdId = (BigDecimal) cdIdQry.getSingleResult();
    return nextCdId.longValue();
  }

  private int getChangeCount(final ChangeEvent event) {
    int count = 0;
    for (Map<Long, ChangeData<?>> data : event.getChangeDataMap().values()) {
      count += data.size();
    }
    return count;
  }

}
