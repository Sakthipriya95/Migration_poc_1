package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwUserCmntHistory;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;

/**
 * Loader class for Review Comment History
 *
 * @author PDH2COB
 */
public class RvwUserCmntHistoryLoader extends AbstractBusinessObject<RvwUserCmntHistory, TRvwUserCmntHistory> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwUserCmntHistoryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_CMNT_HISTORY, TRvwUserCmntHistory.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwUserCmntHistory createDataObject(final TRvwUserCmntHistory entity) throws DataException {
    RvwUserCmntHistory object = new RvwUserCmntHistory();

    setCommonFields(object, entity);

    object.setRvwComment(entity.getRvwComment());

    object.setRvwCmntUserId(entity.getRvwCmntUser().getUserId());

    return object;
  }

  /**
   * @param userId user id
   * @return list of comments for the user
   */
  public Map<Long, RvwUserCmntHistory> getRvwCmntHistoryForUser(final Long userId) {

    Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = new HashMap<>();

    List<TRvwUserCmntHistory> rvwCmntHistList =
        new UserLoader(getServiceData()).getEntityObject(userId).gettRvwUserCmntHistoryList();

    if (CommonUtils.isNotEmpty(rvwCmntHistList)) {

      for (TRvwUserCmntHistory tRvwUserCmntHistory : rvwCmntHistList) {
        try {
          RvwUserCmntHistory rvwCmntHistory = getDataObjectByID(tRvwUserCmntHistory.getRvwCmntUserHistoryId());
          rvwCmntHistoryMap.put(rvwCmntHistory.getId(), rvwCmntHistory);
        }
        catch (DataException e) {
          getLogger().error(e.getMessage(), e);
        }
      }

    }
    return rvwCmntHistoryMap;

  }

}
