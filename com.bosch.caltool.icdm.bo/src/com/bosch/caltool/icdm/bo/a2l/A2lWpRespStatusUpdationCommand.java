package com.bosch.caltool.icdm.bo.a2l;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Command class for A2LWPResponsibilityStatus
 *
 * @author UKT1COB
 */
public class A2lWpRespStatusUpdationCommand extends AbstractSimpleCommand {


  private final A2lWpRespStatusUpdationModel inputData;

  /**
   * Map of A2lWpResponsibilityStatus before Updating Wp finished status. Key - A2lWpResponsibilityStatus id, value -
   * A2lWpResponsibilityStatus
   */
  public final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpdateMap = new HashMap<>();
  /**
   * Map of A2lWpResponsibilityStatus after Updating Wp finished status. Key - A2lWpResponsibilityStatus id, value -
   * A2lWpResponsibilityStatus
   */
  public final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpdateMap = new HashMap<>();
  /**
   * List of new created A2lWpResponsibilityStatus entries in T_A2L_WP_RESPONSIBILITY_STATUS table
   */
  public final List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWPRespStatus = new ArrayList<>();


  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lWpRespStatusUpdationCommand(final ServiceData serviceData, final A2lWpRespStatusUpdationModel input)
      throws IcdmException {
    super(serviceData);
    this.inputData = input;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    checkAndAssignInpDataToBeCreateOrUpd();

    List<A2lWpResponsibilityStatus> listOfA2lWpRespStatusToBeCreated =
        this.inputData.getA2lWpRespStatusListToBeCreated();

    if (CommonUtils.isNotEmpty(listOfA2lWpRespStatusToBeCreated)) {
      createListOfNewWpFinishedStatusEntry(listOfA2lWpRespStatusToBeCreated);
    }

    Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapBeforeUpd =
        this.inputData.getA2lWpRespStatusToBeUpdatedMap();

    if (CommonUtils.isNotEmpty(a2lWpRespStatusMapBeforeUpd)) {
      updateListOfA2LWPRespStatus(a2lWpRespStatusMapBeforeUpd);
    }

  }


  /**
   * @throws DataException
   */
  private void checkAndAssignInpDataToBeCreateOrUpd() throws DataException {

    A2lWpResponsibilityStatusLoader a2lWpResponsibilityStatusLoader =
        new A2lWpResponsibilityStatusLoader(getServiceData());

    // Check if the record to be created is already available in Table, if that the case then add the entry to update
    // Map
    List<A2lWpResponsibilityStatus> a2lWpRespStatusToBeRmvFromCreateList = new ArrayList<>();
    for (A2lWpResponsibilityStatus newA2lWpResponsibilityStatus : this.inputData.getA2lWpRespStatusListToBeCreated()) {

      Long variantId = newA2lWpResponsibilityStatus.getVariantId();
      Long varId = CommonUtils.isNull(variantId) || CommonUtils.isEqual(variantId, ApicConstants.NO_VARIANT_ID) ? null
          : variantId;
      A2lWpResponsibilityStatus existingA2lWpRespStatus =
          a2lWpResponsibilityStatusLoader.getA2lWpStatusByVarAndWpRespId(varId,
              newA2lWpResponsibilityStatus.getWpRespId(), newA2lWpResponsibilityStatus.isInheritedFlag() ? null : newA2lWpResponsibilityStatus.getA2lRespId());

      if (CommonUtils.isNotNull(existingA2lWpRespStatus)) {

        // update the 'WP finished' Status
        existingA2lWpRespStatus.setWpRespFinStatus(newA2lWpResponsibilityStatus.getWpRespFinStatus());

        this.inputData.getA2lWpRespStatusToBeUpdatedMap().put(existingA2lWpRespStatus.getId(), existingA2lWpRespStatus);
        a2lWpRespStatusToBeRmvFromCreateList.add(newA2lWpResponsibilityStatus);
      }
    }
    this.inputData.getA2lWpRespStatusListToBeCreated().removeAll(a2lWpRespStatusToBeRmvFromCreateList);

    // Check if the record to be updated is not already available in table, if that the case then add the entry to
    // create List
    Set<Long> a2lWpRespStatusToBeRmvFromUpdateMap = new HashSet<>();
    for (A2lWpResponsibilityStatus a2lWpResponsibilityStatusToBeUpd : this.inputData.getA2lWpRespStatusToBeUpdatedMap()
        .values()) {
      List<BigDecimal> resultList = a2lWpResponsibilityStatusLoader.getTA2lWpStatusByVarAndWpRespId(
          a2lWpResponsibilityStatusToBeUpd.getVariantId(), a2lWpResponsibilityStatusToBeUpd.getWpRespId(),
          a2lWpResponsibilityStatusToBeUpd.isInheritedFlag() ? null : a2lWpResponsibilityStatusToBeUpd.getA2lRespId());
      if (CommonUtils.isNullOrEmpty(resultList)) {
        this.inputData.getA2lWpRespStatusListToBeCreated().add(a2lWpResponsibilityStatusToBeUpd);
        a2lWpRespStatusToBeRmvFromUpdateMap.add(a2lWpResponsibilityStatusToBeUpd.getId());
      }
    }
    this.inputData.getA2lWpRespStatusToBeUpdatedMap().keySet().removeAll(a2lWpRespStatusToBeRmvFromUpdateMap);
  }


  /**
   * @param listOfA2lWpRespStatusToBeCreated
   * @throws DataException
   * @throws IcdmException
   */
  private void createListOfNewWpFinishedStatusEntry(
      final List<A2lWpResponsibilityStatus> listOfA2lWpRespStatusToBeCreated)
      throws IcdmException {

    for (A2lWpResponsibilityStatus newA2lWpResponsibilityStatus : listOfA2lWpRespStatusToBeCreated) {

      A2lWpResponsibilityStatusCommand cmd =
          new A2lWpResponsibilityStatusCommand(getServiceData(), newA2lWpResponsibilityStatus, false, false);
      executeChildCommand(cmd);

      this.listOfNewlyCreatedA2lWPRespStatus.add(cmd.getNewData());
    }
  }


  /**
   * @param a2lWpRespStatusMapBeforeUpd
   * @throws DataException
   * @throws IcdmException
   */
  private void updateListOfA2LWPRespStatus(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapBeforeUpd)
      throws IcdmException {

    this.a2lWpRespStatusBeforeUpdateMap.putAll(a2lWpRespStatusMapBeforeUpd);

    // update multiple wp Resp values
    for (A2lWpResponsibilityStatus a2lWpResponsibilityStatus : a2lWpRespStatusMapBeforeUpd.values()) {

      String wpRespStatusBeforeUpd = new A2lWpResponsibilityStatusLoader(getServiceData())
          .getDataObjectByID(a2lWpResponsibilityStatus.getId()).getWpRespFinStatus();
      String wpRespFinStatusToBeUpd = a2lWpResponsibilityStatus.getWpRespFinStatus();

      // if there is change in Status then update the status
      if (CommonUtils.isNotEqual(wpRespFinStatusToBeUpd, wpRespStatusBeforeUpd)) {

        a2lWpResponsibilityStatus.setWpRespFinStatus(wpRespFinStatusToBeUpd);
        A2lWpResponsibilityStatusCommand cmd =
            new A2lWpResponsibilityStatusCommand(getServiceData(), a2lWpResponsibilityStatus, true, false);
        executeChildCommand(cmd);

        A2lWpResponsibilityStatus updatedData = cmd.getNewData();
        this.a2lWpRespStatusAfterUpdateMap.put(updatedData.getId(), updatedData);
      }
      else {
        // remove not updated A2LWPResponsibilityStatus
        this.a2lWpRespStatusBeforeUpdateMap.remove(a2lWpResponsibilityStatus.getId());
      }
    }
  }


  /**
   * @return the a2lWpRespStatusBeforeUpdateMap
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusBeforeUpdateMap() {
    return this.a2lWpRespStatusBeforeUpdateMap;
  }


  /**
   * @return the a2lWpRespStatusAfterUpdateMap
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusAfterUpdateMap() {
    return this.a2lWpRespStatusAfterUpdateMap;
  }


  /**
   * @return the listOfNewlyCreatedA2lWPRespStatus
   */
  public List<A2lWpResponsibilityStatus> getListOfNewlyCreatedA2lWPRespStatus() {
    return this.listOfNewlyCreatedA2lWPRespStatus;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
