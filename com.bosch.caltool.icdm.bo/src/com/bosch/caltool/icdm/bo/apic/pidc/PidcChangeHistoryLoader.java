package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.difference.PidcDifferenceForVersion;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesAttributesPidcRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesPIDCRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.element.ElementDifferencesVariantRest;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutput.adapter.IcdmPidcLogAdapter;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutputs.AbstractPidcLogOutputNew;
import com.bosch.caltool.icdm.bo.apic.pidc.differences.textoutputs.IcdmPidcLogOutputNew;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcChangeHistory;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedAttrType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcChangedVariantType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.model.user.NodeAccess;


/**
 * Loader class for PidcChangeHistory
 *
 * @author dmr1cob
 */
public class PidcChangeHistoryLoader extends AbstractBusinessObject<PidcChangeHistory, TPidcChangeHistory> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public PidcChangeHistoryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PIDC_CHANGE_HISTORY, TPidcChangeHistory.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcChangeHistory createDataObject(final TPidcChangeHistory entity) throws DataException {
    PidcChangeHistory object = new PidcChangeHistory();

    object.setId(entity.getId());
    object.setPidcId(entity.getPidcId());
    object.setVarId(entity.getVarId());
    object.setSvarId(entity.getSvarId());
    object.setAttrId(entity.getAttrId());
    object.setChangedDate(timestamp2String(entity.getChangedDate()));
    object.setChangedUser(entity.getChangedUser());
    object.setPidcVersion(entity.getPidcVersion());
    object.setOldValueId(null != entity.getTabvAttrOldValue() ? entity.getTabvAttrOldValue().getValueId() : null);
    object.setNewValueId(null != entity.getTabvAttrNewValue() ? entity.getTabvAttrNewValue().getValueId() : null);
    object.setOldUsed(entity.getOldUsed());
    object.setNewUsed(entity.getNewUsed());
    object.setOldPartNumber(entity.getOldPartNumber());
    object.setNewPartNumber(entity.getNewPartNumber());
    object.setOldSpecLink(entity.getOldSpecLink());
    object.setNewSpecLink(entity.getNewSpecLink());
    object.setOldDescription(entity.getOldDescription());
    object.setNewDescription(entity.getNewDescription());
    object.setOldDeletedFlag(entity.getOldDeletedFlag());
    object.setNewDeletedFlag(entity.getNewDeletedFlag());
    object.setOldStatusId(entity.getOldStatusId());
    object.setNewStatusId(entity.getNewStatusId());
    object.setOldIsVariant(entity.getOldIsVariant());
    object.setNewIsVariant(entity.getNewIsVariant());
    object.setVersion(entity.getVersion());
    object.setOldValueDescEng(entity.getOldValueDescEng());
    object.setNewValueDescEng(entity.getNewValueDescEng());
    object.setOldValueDescGer(entity.getOldValueDescGer());
    object.setNewValueDescGer(entity.getNewValueDescGer());
    object.setOldTextvalueEng(entity.getOldTextValueEng());
    object.setNewTextvalueEng(entity.getNewTextValueEng());
    object.setOldTextvalueGer(entity.getOldTextValueGer());
    object.setNewTextvalueGer(entity.getNewTextValueGer());
    object.setPidcVersId(entity.getPidcVersId());
    object.setPidcVersVers(entity.getPidcVerVersion());
    object.setOldAprjId(entity.getOldAprjId());
    object.setNewAprjId(entity.getNewAprjId());
    object.setPidcAction(entity.getPidcAction());
    object.setOldFocusMatrixYn(entity.getOldFocusMatrix());
    object.setNewFocusMatrixYn(entity.getNewFocusMatrix());
    object.setOldTransferVcdmYn(entity.getOldTransferVcdm());
    object.setNewTransferVcdmYn(entity.getNewTransferVcdm());
    object.setFmVersId(entity.getFmVersId());
    object.setOldFmVersName(entity.getOldFmVersName());
    object.setNewFmVersName(entity.getNewFmVersName());
    object.setFmVersRevNum(entity.getFmVersRevNum());
    object.setOldFmVersStatus(entity.getOldFmVersStatus());
    object.setNewFmVersStatus(entity.getNewFmVersStatus());
    object.setOldFmVersReviewedUser(entity.getOldFmVersRvwUser());
    object.setNewFmVersReviewedUser(entity.getNewFmVersRvwUser());
    object.setOldFmVersReviewedDate(timestamp2String(entity.getOldFmVersRvwDate()));
    object.setNewFmVersReviewedDate(timestamp2String(entity.getNewFmVersRvwDate()));
    object.setOldFmVersLink(entity.getOldFmVersLink());
    object.setNewFmVersLink(entity.getNewFmVersLink());
    object.setOldFmVersRvwStatus(entity.getOldFmVersRvwStatus());
    object.setNewFmVersRvwStatus(entity.getNewFmVersRvwStatus());
    object.setFmVersVersion(entity.getFmVersVersion());
    object.setFmId(entity.getFmId());
    object.setFmUcpaId(entity.getFmUcpaId());
    object.setOldFmColorCode(entity.getOldFmColorCode());
    object.setNewFmColorCode(entity.getNewFmColorCode());
    object.setOldFmComments(entity.getOldFmComments());
    object.setNewFmComments(entity.getNewFmComments());
    object.setFmVersion(entity.getFmVersion());
    object.setOldFmLink(entity.getOldFmLink());
    object.setNewFmLink(entity.getNewFmLink());
    object.setUseCaseId(entity.getUseCaseId());
    object.setSectionId(entity.getSectionId());
    object.setOldValueClearingStatus(null != entity.getOldValueClearingStatus()
        ? CLEARING_STATUS.getClearingStatus(entity.getOldValueClearingStatus()).toString() : null);
    object.setNewValueClearingStatus(null != entity.getNewValueClearingStatus()
        ? CLEARING_STATUS.getClearingStatus(entity.getNewValueClearingStatus()).toString() : null);
    return object;
  }

  /**
   * Fetches the PIDC change history // iCDM-672
   *
   * @param fromVersion : version
   * @param pidcVersId : project id card version id
   * @param pidcId :pidcId
   * @param fromVersionWeb false
   * @param attrID attrId
   * @return Sorted set of PidcChangeHistory objects
   * @throws DataException Exception
   */
  public SortedSet<PidcChangeHistory> fetchPIDCChangeHistory(final Long fromVersion, final long pidcVersId,
      final long pidcId, final boolean fromVersionWeb, final Set<Long> attrID)
      throws DataException {
    final SortedSet<PidcChangeHistory> pidcHistoryResultSet = new TreeSet<>();
    String query;
    // From Old Request
    if ((pidcVersId <= 0) && !fromVersionWeb) {
      query = CommonUtils.concatenate("SELECT hist from TPidcChangeHistory hist where  hist.pidcId  = '", pidcId,
          "' and hist.pidcVersion >= ", fromVersion - 1);
    }
    // New Request with Version ID.
    else if (fromVersionWeb && (pidcVersId > 0)) {
      query = CommonUtils.concatenate(
          "SELECT hist from TPidcChangeHistory hist where hist.pidcVerVersion is not null and hist.pidcVersId  = '",
          pidcVersId, "' and hist.pidcVerVersion >= ", fromVersion - 1);
    }
    // New Request without Version ID
    else {
      query = CommonUtils.concatenate(
          "SELECT hist from TPidcChangeHistory hist where hist.pidcVerVersion is not null and hist.pidcId  = '", pidcId,
          "' and hist.pidcVersion >= ", fromVersion - 1);
    }

    final TypedQuery<TPidcChangeHistory> typeQuery = getEntMgr().createQuery(query, TPidcChangeHistory.class);
    typeQuery.setHint(ApicConstants.READ_ONLY, "true");
    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    final List<TPidcChangeHistory> resultList = typeQuery.getResultList();
    PidcChangeHistory pidcChangeHist;
    for (TPidcChangeHistory dbHist : resultList) {
      if (CommonUtils.isNotEmpty(attrID) && (attrID.contains(dbHist.getAttrId()) ||
          attrID.contains(dbHist.getVarId()) || attrID.contains(dbHist.getSvarId()))) {
        continue;
      }
      pidcChangeHist = createDataObject(dbHist);
      pidcHistoryResultSet.add(pidcChangeHist);
    }
    return pidcHistoryResultSet;
  }

  /**
   * @param pidcDiff {@link PidcDiffsType}
   * @return {@link PidcDiffsResponseType}
   * @throws DataException Exception
   */
  public PidcDiffsResponseType getPidcDiffs(final PidcDiffsType pidcDiff) throws DataException {
    PidcDiffsResponseType pidcDiffsResponseType;
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVer = pidcVersLoader.getActivePidcVersion(pidcDiff.getPidcId());
    Long oldPidcChangeNumber = pidcDiff.getOldPidcChangeNumber() + 1;
    Long newPidcChangeNumber = pidcDiff.getNewPidcChangeNumber();
    pidcDiffsResponseType = getDummyResponse(pidcVer, oldPidcChangeNumber, newPidcChangeNumber);

    /**
     * The change history of the PIDC based on the passed arguments getPidcDiffsParameter.getOldPidcChangeNumber(): the
     * lower version number, from which should be started pidc.getPidcId(): the PIDC ID Note: All changes from
     * getOldPidcChangeNumber() until the current version are returned.
     */
    if ((newPidcChangeNumber == -1) || (oldPidcChangeNumber <= newPidcChangeNumber)) {
      PidcVersionAttributeLoader pidcVersAttrLoader = new PidcVersionAttributeLoader(getServiceData());

      Map<Long, PidcVersionAttribute> attributes = pidcVersAttrLoader.getPidcVersionAttribute(pidcVer.getId());

      Set<Long> attrID = new HashSet<>();

      for (PidcVersionAttribute pidcAttr : attributes.values()) {
        if (pidcAttr.isAttrHidden() && isHidden(pidcVer.getPidcId())) {
          attrID.add(pidcAttr.getAttrId());
        }
      }

      if (pidcVersLoader.isHiddenToCurrentUser(pidcVer.getId())) {
        throw new DataException("Pidc of ID " + pidcDiff.getPidcId() + " is not visible to the user.");
      }
      // Send the Pid Version Id to get the Pid Change History
      SortedSet<com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory> changeHistory2 =
          fetchPIDCChangeHistory(oldPidcChangeNumber, pidcVer.getId(), pidcDiff.getPidcId(), false, attrID);
      // If size of map is = 0, there are no changes to report (the tdb-table hasn't any entries for the passed
      // versions)
      if (!CommonUtils.isNullOrEmpty(changeHistory2)) {
        // Get the pidc ID from the active Version
        ElementDifferencesRest pidcElement = new ElementDifferencesPIDCRest(oldPidcChangeNumber, newPidcChangeNumber,
            changeHistory2, pidcVer.getPidcId(), false);
        pidcElement.analyzeDifferences();

        /*
         * In some cases, there's no entry for the PIDC itself, although there are reported changes in attributes of the
         * PIDC. Shouldn't be allowed, but this case exists. The initially created dummy is only overwritten, when the
         * retunrned PIDC array is filled with a PIDC
         */
        if (pidcElement.toArray().length > 0) {
          pidcDiffsResponseType = (PidcDiffsResponseType) pidcElement.toArray()[0];
        }

        // Get the changed attributes of PIDC level
        // Get the pidc ID from the active Version
        ElementDifferencesRest attr = new ElementDifferencesAttributesPidcRest(oldPidcChangeNumber, newPidcChangeNumber,
            changeHistory2, pidcVer.getPidcId(), false);
        attr.analyzeDifferences();
        PidcChangedAttrType[] pidcChangedAttrTypeArray = (PidcChangedAttrType[]) attr.toArray();
        pidcDiffsResponseType.setPidcChangedAttrTypeList(Arrays.asList(pidcChangedAttrTypeArray));

        // Get the changes for all variants, sub variants and their attributes
        // Get the pidc ID from the active Version
        ElementDifferencesRest variants = new ElementDifferencesVariantRest(oldPidcChangeNumber, newPidcChangeNumber,
            changeHistory2, pidcVer.getPidcId(), false);
        variants.analyzeDifferences();
        PidcChangedVariantType[] pidcChangedVariantArray = (PidcChangedVariantType[]) variants.toArray();
        pidcDiffsResponseType.setPidcChangedVariantTypeList(Arrays.asList(pidcChangedVariantArray));
      }
    }
    PidcDiffsResponseCreator response = new PidcDiffsResponseCreator();
    return response.getPidcDiffResponseObj(pidcDiffsResponseType);
  }

  /**
   * @param allPidcDiffVers {@link PidcDiffsForVersType}
   * @return {@link PidcDiffsResponseType}
   * @throws IcdmException Exception
   */
  public PidcDiffsResponseType getAllPidcDiffForVersion(final PidcDiffsForVersType allPidcDiffVers)
      throws IcdmException {
    /**
     * This object contains no information about changes. Just the PIDC and the structure of attributes and variants is
     * returned. Additionally, the PIDCHistory but only for the PIDC itself and not for the attributes is returned
     * through .getPidcHistory().
     */
    PidcDiffsForVersType request = allPidcDiffVers;
    PidcVersion pidcVer = null;
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());


    if (null == request.getPidcVersionId()) {
      pidcVer = pidcVersLoader.getActivePidcVersion(request.getPidcId());
    }
    else {
      pidcVer = pidcVersLoader.getDataObjectByID(request.getPidcVersionId());
    }


    if (pidcVersLoader.isHiddenToCurrentUser(pidcVer.getId())) {
      throw new IcdmException("Pidc of ID " + request.getPidcId() + " is not visible to the user.");
    }

    PidcVersionAttributeLoader pidcVersAttrLoader = new PidcVersionAttributeLoader(getServiceData());

    Map<Long, PidcVersionAttribute> attributes = pidcVersAttrLoader.getPidcVersionAttribute(pidcVer.getId());

    Set<Long> attrID = new HashSet<>();
    PidcChangeHistoryLoader historyLoader = new PidcChangeHistoryLoader(getServiceData());

    for (PidcVersionAttribute pidcAttr : attributes.values()) {
      if (pidcAttr.isAttrHidden() && historyLoader.isHidden(pidcVer.getPidcId())) {
        attrID.add(pidcAttr.getAttrId());
      }
    }

    SortedSet<com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory> changeHistory =
        historyLoader.fetchPIDCChangeHistory(request.getOldPidcChangeNumber() + 1, request.getPidcVersionId(),
            request.getPidcId(), true, attrID);

    PidcDifferenceForVersion diffs =
        new PidcDifferenceForVersion(request, pidcVer, changeHistory, getLogger(), getServiceData());

    // iCDM-2614
    // added focus matrix & its versions here
    diffs.createWsResponse();
    PidcDiffsResponseCreator response = new PidcDiffsResponseCreator();
    return response.getPidcDiffResponseObj(diffs.getWsResponse());
  }

  // invokes when clicking on pidc history icon in iCDM Client
  public List<AttrDiffType> getPidcAttrDiffReportForVersion(final PidcDiffsForVersType attrDiffVers)
      throws IcdmException {

    SortedSet<IcdmPidcLogAdapter> responseNew;

    PidcDiffsResponseType pidcResp = getAllPidcDiffForVersion(attrDiffVers);

    AbstractPidcLogOutputNew pidcLog =
        new IcdmPidcLogOutputNew(getLogger(), getServiceData(), pidcResp, attrDiffVers.getLanguage());
    pidcLog.createWsResponse();
    responseNew = (SortedSet<IcdmPidcLogAdapter>) pidcLog.getWsResponse();

    List<AttrDiffType> allDiffs = new ArrayList<>();

    for (IcdmPidcLogAdapter entry : responseNew) {
      PidcDiffsResponseCreator creator = new PidcDiffsResponseCreator();
      allDiffs.add(creator.getAttrDiffType(entry));
    }
    return allDiffs;
  }

  /**
   * The response object that stores the webservice response. Initialized with a dummy PIDC-Type. This dummy type
   * prevents the web service to fail in case of:<br>
   * <ul>
   * <li>there are no changes to report in the table t_pidc_change_history</li>
   * <li>there's no entry in table t_pidc_change_history for the PIDC, although there are changes for attributes for
   * this PIDC</li>
   * </ul>
   * Note: In this case no incrementation of the old ID is required, because this would lead to the case that old change
   * number is higher then new change number. Thus, the original values are transfered
   */
  private static PidcDiffsResponseType getDummyResponse(final PidcVersion pidcVer, final long oldPidcChangeNumber,
      final long newPidcChangeNumber) {
    PidcDiffsResponseType response = new PidcDiffsResponseType();
    // get the Pidc id from the Active Version
    response.setPidcId(pidcVer.getPidcId());
    response.setOldChangeNumber(oldPidcChangeNumber);
    response.setNewChangeNumber(newPidcChangeNumber);
    response.setOldPidcVersionNumber(pidcVer.getVersion());
    response.setNewPidcVersionNumber(pidcVer.getVersion());
    response.setOldPidcStatus("0");
    response.setNewPidcStatus("0");
    response.setModifiedDate(pidcVer.getModifiedDate());
    response.setModifiedUser(pidcVer.getModifiedUser());
    return response;
  }

  /**
   * @return
   * @throws DataException
   */
  public boolean isHidden(final Long pidcId) throws DataException {
    boolean flag = false;
    ApicAccessRight accessRightsCurrentUser = new ApicAccessRightLoader(getServiceData()).getAccessRightsCurrentUser();
    if ((null != accessRightsCurrentUser) &&
        (accessRightsCurrentUser.isApicWrite() || accessRightsCurrentUser.isApicReadAll())) {
      return flag;
    }
    NodeAccess nodeAccess = new NodeAccessLoader(getServiceData()).getAllNodeAccessForCurrentUser().get(pidcId);
    if ((nodeAccess == null) || !nodeAccess.isRead()) {
      flag = true;
    }
    return flag;
  }
}
