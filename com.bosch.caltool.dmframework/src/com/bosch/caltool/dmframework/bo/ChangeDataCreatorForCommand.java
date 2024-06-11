/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.exception.DataException;

/**
 * @author bne4cob
 */
class ChangeDataCreatorForCommand {

  List<ChangeData<?>> createData(final long eventId, final List<AbstractSimpleCommand> commandList,
      final boolean childCommand) {

    List<ChangeData<?>> retList = new ArrayList<>();

    for (AbstractSimpleCommand scmd : commandList) {
      // If command is not Abstract command or , as a child command, no CNS relevance, skip CNS
      if (!(scmd instanceof AbstractCommand) || (childCommand && !scmd.isRelevantForCns())) {
        continue;
      }

      ChangeData<?> data = createData(eventId, (AbstractCommand<?, ?>) scmd);
      if (data != null) {
        retList.add(data);
      }
    }

    return retList;

  }

  private ChangeData<?> createData(final long eventId, final AbstractCommand<?, ?> cmd) {
    ChangeData<?> data = null;
    ChangeDataCreator<IModel> creator = new ChangeDataCreator<>();
    try {
      if (COMMAND_MODE.CREATE == cmd.getCmdMode()) {
        data = creator.createDataForCreate(eventId, cmd.getNewData());
      }
      else if (COMMAND_MODE.UPDATE == cmd.getCmdMode()) {
        data = creator.createDataForUpdate(eventId, cmd.getOldData(), cmd.getNewData());
      }
      else {
        data = creator.createDataForDelete(eventId, cmd.getOldData());
      }
    }
    catch (DataException e) {
      ObjectStore.getInstance().getLogger().error("Error while creating change data - " + e.getMessage(), e);
    }
    return data;
  }
}
