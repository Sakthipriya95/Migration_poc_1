/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework.impl;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;

/**
 * @author bne4cob
 */
public final class TestUtils {

  private TestUtils() {
    // private constructor
  }

  public static A createA(final Long id, final String name, final Long version) {
    A ret = new A();
    ret.setId(id);
    ret.setName(name);
    ret.setVersion(version);

    return ret;
  }

  public static B createB(final Long id, final String name, final String desc, final Long version) {
    B ret = new B();
    ret.setId(id);
    ret.setName(name);
    ret.setDesc(desc);
    ret.setVersion(version);

    return ret;
  }


  public static <D extends IModel> ChangeData<D> createChangeData(final long objId, final CHANGE_OPERATION changeType,
      final IModelType type, final D oldData, final D newData) {

    ChangeData<D> ret = new ChangeData<>();
    ret.setChangeEventId(1000L);
    ret.setChangeType(changeType);
    ret.setNewData(newData);
    ret.setOldData(oldData);
    ret.setType(type);
    ret.setObjId(objId);

    return ret;
  }

}

