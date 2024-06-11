/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.axis2.databinding.types.HexBinary;
import org.apache.commons.beanutils.BeanUtils;

import com.bosch.calcomp.a2lloader.A2LLoader;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.caldataanalyzer.CalDataAnalyzer;
import com.bosch.calcomp.caldataanalyzer.dao.sql.FetchCalDataSqlDAO;
import com.bosch.calcomp.caldataanalyzer.model.OperatorAndDataModel;
import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.apic.ws.*;
import com.bosch.caltool.apic.ws.applicationlog.ServerEventLogger;
import com.bosch.caltool.apic.ws.applicationlog.request.PidcSearchRequest;
import com.bosch.caltool.apic.ws.attribute.bo.AttributeValueWsBo;
import com.bosch.caltool.apic.ws.attribute.bo.AttributeValueWsBoV2;
import com.bosch.caltool.apic.ws.attribute.bo.AttributeWsBo;
import com.bosch.caltool.apic.ws.attribute.bo.AttributeWsBoV2;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatLabelAdapter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.FilterAdapter;
import com.bosch.caltool.apic.ws.differences.PidcDifferenceForVersion;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferences;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesAttributesPidc;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesPIDC;
import com.bosch.caltool.apic.ws.differences.element.ElementDifferencesVariant;
import com.bosch.caltool.apic.ws.differences.textoutput.AbstractPidcLogOutput;
import com.bosch.caltool.apic.ws.differences.textoutput.IcdmPidcLogOutput;
import com.bosch.caltool.apic.ws.differences.textoutput.adapter.IcdmPidcLogAdapter;
import com.bosch.caltool.apic.ws.group.bo.GroupTypeWsBo;
import com.bosch.caltool.apic.ws.group.bo.SuperGroupTypeWsBo;
import com.bosch.caltool.apic.ws.pidc.AbstractPidc;
import com.bosch.caltool.apic.ws.pidc.IcdmPidc;
import com.bosch.caltool.apic.ws.pidc.accessright.AbstractAccessRight;
import com.bosch.caltool.apic.ws.pidc.accessright.IcdmPidcAccessRight;
import com.bosch.caltool.apic.ws.pidc.bo.ProjectIdCardInfoTypeWsBo;
import com.bosch.caltool.apic.ws.pidc.bo.ProjectIdCardInfoTypeWsBoV2;
import com.bosch.caltool.apic.ws.pidc.bo.ProjectIdCardVariantInfoTypeWsBo;
import com.bosch.caltool.apic.ws.pidc.bo.ProjectIdCardVariantInfoTypeWsBoV2;
import com.bosch.caltool.apic.ws.pidc.bo.ProjectIdCardVersInfoTypeWsBo;
import com.bosch.caltool.apic.ws.progress.Rule;
import com.bosch.caltool.apic.ws.progress.WsRuleFetchChangedListener;
import com.bosch.caltool.apic.ws.progress.WsStandardFetchChangedListener;
import com.bosch.caltool.apic.ws.reviewresult.AbstractReviewResult;
import com.bosch.caltool.apic.ws.reviewresult.IcdmReviewResults;
import com.bosch.caltool.apic.ws.session.LoginOperation;
import com.bosch.caltool.apic.ws.session.Session;
import com.bosch.caltool.apic.ws.timezone.TimeZoneFactory;
import com.bosch.caltool.apic.ws.usecase.bo.UseCaseItemTypeWsBo;
import com.bosch.caltool.apic.ws.usecase.bo.UseCaseSectionTypeWsBo;
import com.bosch.caltool.apic.ws.usecase.bo.UseCaseTypeWsBo;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.caltool.dmframework.bo.CommandExecuter;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetLoader;
import com.bosch.caltool.icdm.bo.apic.VcdmDataSetWPStatsLoader;
import com.bosch.caltool.icdm.bo.apic.WebflowElementMainCommand;
import com.bosch.caltool.icdm.bo.apic.attr.AttrSuperGroupLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcChangeHistoryLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcFavouriteLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoaderExternal;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcScout;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatisticsReportLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.bo.uc.UseCaseLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.ICDMLoggerConstants;
import com.bosch.caltool.icdm.logger.WSLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.VcdmDataSet;
import com.bosch.caltool.icdm.model.apic.VcdmDataSetWPStats;
import com.bosch.caltool.icdm.model.apic.WebFlowAttrValues;
import com.bosch.caltool.icdm.model.apic.WebFlowAttribute;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchCondition;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionStatisticsReport;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributesV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowData;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.uc.UsecaseSectionType;
import com.bosch.caltool.security.Decryptor;

/**
 * Service implimentations for SOAP service
 */
public class ApicWebServiceDBImpl {


  /**
   * pidc of given id
   */
  private static final String PIDC_ID = "Pidc of the given Id ";
  /**
   *
   */
  private static final String PIDC_PREFIX = "PIDC:";

  /**
   * string constant for PIDC_EDITOR
   */
  private static final String PIDC_EDITOR_STR = "PIDC_EDITOR";

  // database username for CalDataAnalyzer
  private static final String DB_PASSWORD =
      Decryptor.getInstance().decrypt(Messages.getString("CommonUtils.ANALYZER_DB_USER_PASS"), CDMLogger.getInstance());

  // database password for CalDataAnalyzer
  private static final String DB_USERNAME = Messages.getString("CommonUtils.ANALYZER_DB_USER");

  // database connection for CalDataAnalyzer
  private static final String DB_CONNECTION = Messages.getString("CommonUtils.ANALYZER_DB_CONNECTION");

  private static volatile Map<String, Session> statusAsyncExecution = new ConcurrentHashMap<>();

  // the logger
  private final ILoggerAdapter logger;
  private final ILoggerAdapter a2lLogger;
  private final ILoggerAdapter cdaLogger;
  private final ILoggerAdapter cdfLogger;


  /**
   * @param logger Logger to be used
   */
  public ApicWebServiceDBImpl(final ILoggerAdapter logger) {
    super();

    this.logger = logger;

    // Init the A2L Logger
    this.a2lLogger = WSLogger.getLogger(ICDMLoggerConstants.A2L_LDR_LOGGER);
    this.cdaLogger = WSLogger.getLogger(ICDMLoggerConstants.CDA_LOGGER);
    this.cdfLogger = WSLogger.getLogger(ICDMLoggerConstants.PARSER_LOGGER);
  }


  public static Double getStatusAsync(final String sessID) {
    if (ApicWebServiceDBImpl.statusAsyncExecution.containsKey(sessID)) {
      return ApicWebServiceDBImpl.statusAsyncExecution.get(sessID).getPercentageFinished();
    }

    return 0D;
  }

  public static Session getSession(final String sessID) {
    return ApicWebServiceDBImpl.statusAsyncExecution.get(sessID);
  }

  public static void addSession(final Session session) {
    ApicWebServiceDBImpl.statusAsyncExecution.put(session.getSessionId(), session);
  }

  public static void removeSession(final String sessID) {
    ApicWebServiceDBImpl.statusAsyncExecution.remove(sessID);
  }

  /**
   * @param getParameterReviewResult getParameterReviewResult
   * @return GetParameterReviewResultResponse
   * @throws IcdmException Error during webservice call
   */
  public GetParameterReviewResultResponse getParameterReviewResult(
      final GetParameterReviewResult getParameterReviewResult)
      throws IcdmException {
    /**
     * Session Variable for current call
     */
    Session session =
        getSession(getParameterReviewResult.getGetParameterReviewResult().getSessID(), "GetParameterReviewResult",
            String.valueOf(getParameterReviewResult.getGetParameterReviewResult().getParameterId()));
    AbstractReviewResult reviewResult;
    try (ServiceData serviceData = createServiceData(session)) {
      AbstractPidc pidc = new IcdmPidc(serviceData);
      reviewResult = new IcdmReviewResults(session, getParameterReviewResult, pidc, serviceData);
      reviewResult.createWsResponse();
    }
    catch (ClassNotFoundException | IOException exp) {
      this.logger.error(exp.getMessage(), exp);
      throw new IcdmException(exp.getMessage());
    }
    return reviewResult.getWsResponse();
  }

  /**
   * Get all attributes from the database, convert them into the WebService objects and return them
   *
   * @param params the parameters passed to this method
   * @return the response object
   * @throws GetAllAttributesFaultException any exception
   */
  public GetAllAttributesResponseType getAllAttr(final GetAllAttributes params) throws GetAllAttributesFaultException {
    /**
     * Session Variable for current call
     */
    Session session = getSession(params.getSessID(), "GetAllAttributes", "All Attributes");

    // the attribute list for the response object
    AttributesList wsAttributes = new AttributesList();


    try (ServiceData serviceData = createServiceData(session)) {
      AttributeLoader attrLdr = new AttributeLoader(serviceData);
      Map<Long, com.bosch.caltool.icdm.model.apic.attr.Attribute> allAttrMap = attrLdr.getAllAttributes(true);
      // transfer the database objects into the response
      for (com.bosch.caltool.icdm.model.apic.attr.Attribute dbAttribute : allAttrMap.values()) {
        // transfer and add the attribute to the attributes list
        wsAttributes.addAttributes(getWsAttribute(session, dbAttribute));
      }
    }
    catch (IcdmException e) {
      getLogger().error(e.getMessage(), e);
    }

    // the response object
    GetAllAttributesResponseType response = new GetAllAttributesResponseType();
    // add the attributes list to the response object
    response.setAttributesList(wsAttributes);

    return response;
  }

  private ILoggerAdapter getLogger() {
    return WSLogger.getInstance();
  }

  /**
   * This method returns Attribute object
   *
   * @param dbAttribute
   * @param attr
   * @return Attribute
   * @throws IcdmException
   */
  private Attribute getWsAttribute(final Session session,
      final com.bosch.caltool.icdm.model.apic.attr.Attribute dbAttribute)
      throws IcdmException {

    Attribute wsAttribute = new AttributeWsBo(session);

    // Set attribute id
    wsAttribute.setId(dbAttribute.getId());
    // Set attribute English name
    String attrNameE = (CommonUtils.checkNull(dbAttribute.getNameEng()));
    wsAttribute.setNameE(attrNameE);
    // Set attribute German name
    String attrNameG = dbAttribute.getNameGer();
    if (CommonUtils.isEmptyString(attrNameG)) {
      attrNameG = attrNameE;
    }
    wsAttribute.setNameG(attrNameG);

    // Set attribute English description
    String attrDescE = (CommonUtils.checkNull(dbAttribute.getDescriptionEng()));
    wsAttribute.setDescrE(attrDescE);
    // Set attribute German description
    String attrDescG = dbAttribute.getDescriptionGer();
    if (CommonUtils.isEmptyString(attrDescG)) {
      attrDescG = attrDescE;
    }
    wsAttribute.setDescrG(attrDescG);

    // Set attribute deleted information
    wsAttribute.setIsDeleted(dbAttribute.isDeleted());
    // Set attribute normalized information
    wsAttribute.setIsNormalized(dbAttribute.isNormalized());
    // Set attribute mandatory information
    wsAttribute.setIsMandatory(dbAttribute.isMandatory());
    // Set attribute unit information
    wsAttribute.setUnit(dbAttribute.getUnit());
    // Set attribute format information
    wsAttribute.setFormat(CommonUtils.checkNull(dbAttribute.getFormat()));
    // Set attribute created date information

    wsAttribute
        .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, dbAttribute.getCreatedDate()));

    // Set attribute created user information
    wsAttribute.setCreateUser(dbAttribute.getCreatedUser());
    // Set attribute modified date information

    wsAttribute
        .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, dbAttribute.getModifiedDate()));

    // Set attribute modified user information
    wsAttribute.setModifyUser(dbAttribute.getModifiedUser());

    // Set attribute value type id
    AttributeValueType valType = AttributeValueType.getType(dbAttribute.getValueType());
    wsAttribute.setTypeID(valType.getValueTypeID());

    // Set attribute group id
    wsAttribute.setGroupID(dbAttribute.getAttrGrpId());

    // Set attribute version
    wsAttribute.setChangeNumber(dbAttribute.getVersion());

    return wsAttribute;
  }

  /**
   * get the attribute values
   *
   * @param params
   * @return GetAttributeValuesResponseType
   * @throws GetAttributeValuesFaultException
   */
  public GetAttributeValuesResponseType getAttrValues(final GetAttributeValues params)
      throws GetAttributeValuesFaultException {

    Session session = getSession(params.getSessID(), "GetAttrValues",
        String.valueOf(params.getAttributeIDs().length) + " Attributes.");

    GetAttributeValuesResponseType response = new GetAttributeValuesResponseType();


    try (ServiceData serviceData = createServiceData(session)) {
      Set<Long> attrIdSet = new HashSet<>();

      for (Long attrId : params.getAttributeIDs()) {
        attrIdSet.add(attrId);
      }
      Map<Long, com.bosch.caltool.icdm.model.apic.attr.Attribute> attrMap =
          new AttributeLoader(serviceData).getDataObjectByID(attrIdSet);


      Map<Long, Map<Long, com.bosch.caltool.icdm.model.apic.attr.AttributeValue>> allAttrValueMap =
          new AttributeValueLoader(serviceData).getValuesByAttribute(attrIdSet);

      ValueList wsAttrValueList;

      for (Entry<Long, Map<Long, com.bosch.caltool.icdm.model.apic.attr.AttributeValue>> entry : allAttrValueMap
          .entrySet()) {
        wsAttrValueList = new ValueList();

        wsAttrValueList.setAttribute(getWsAttribute(session, attrMap.get(entry.getKey())));

        for (com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrVal : entry.getValue().values()) {
          wsAttrValueList.addValues(createWsAttributeValue(session, attrVal));

        }
        response.addValueLists(wsAttrValueList);

      }


    }
    catch (IcdmException e) {
      getLogger().error(e.getMessage(), e);
      throw new GetAttributeValuesFaultException(e);
    }

    return response;
  }

  /**
   * Get all Project ID Cards without their details
   *
   * @param getAllProjectIdCardsObj (the parameters)
   * @return the result
   * @throws GetAllProjectIdCardsFaultException Exception
   * @throws IcdmException Exception
   */
  public GetAllProjectIdCardsResponseType getAllProjIdCard(final GetAllProjectIdCards getAllProjectIdCardsObj)
      throws GetAllProjectIdCardsFaultException {


    Map<Long, PidcVersionInfo> pidcVerMap;
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel;

    // Session Variable for current call
    Session session = getSession(getAllProjectIdCardsObj.getSessID(), "GetAllProjIdCard", "All PIDCs.");
    try (ServiceData servData = createServiceData(session)) {
      // Get the all project id cards
      pidcVerMap = (new PidcVersionLoader(servData)).getAllActiveVersionsWithStructureAttributes();
      maxLevel = (new AttributeLoader(servData)).getMaxStructAttrLevel();
    }
    catch (DataException e) {
      throw new GetAllProjectIdCardsFaultException(e.getMessage(), e);
    }

    // the project id card list for the response object
    ProjectIdCardInfoType[] wsProjIdCards = new ProjectIdCardInfoType[pidcVerMap.size()];

    // Transfer the PIDCs into the response object
    int idx = 0;
    for (PidcVersionInfo pidcVersionInfo : pidcVerMap.values()) {

      wsProjIdCards[idx] = new ProjectIdCardInfoTypeWsBo(session);

      // set the PIDC ID from active pidc Version Pidc
      wsProjIdCards[idx].setId(pidcVersionInfo.getPidc().getId());

      // use the english name of the PIDC as the name
      wsProjIdCards[idx].setName(pidcVersionInfo.getPidc().getName());

      // get the PIDC version number
      wsProjIdCards[idx].setVersionNumber(pidcVersionInfo.getPidcVersion().getProRevId());

      // get the PIDC version
      wsProjIdCards[idx].setChangeNumber(pidcVersionInfo.getPidc().getVersion());

      // get deleted flag of the PIDC
      wsProjIdCards[idx].setIsDeleted(pidcVersionInfo.getPidcVersion().isDeleted());

      // get create and modify user of the PIDC
      wsProjIdCards[idx].setCreateUser(pidcVersionInfo.getPidcVersion().getCreatedUser());
      wsProjIdCards[idx].setModifyUser(pidcVersionInfo.getPidcVersion().getModifiedUser());

      // get create and modify date of the PIDC
      try {
        wsProjIdCards[idx].setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
            pidcVersionInfo.getPidcVersion().getCreatedDate()));
        wsProjIdCards[idx].setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
            pidcVersionInfo.getPidcVersion().getModifiedDate()));
      }
      catch (IcdmException exp) {
        getLogger().warn(exp.getMessage(), exp);
      }

      // Add information regarding the clearing status
      String clearingStatus = pidcVersionInfo.getPidc().getClearingStatus();
      wsProjIdCards[idx].setClearingStatus(clearingStatus);
      CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
      wsProjIdCards[idx].setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

      // Send the pidc Version Id rather than the Pidc ID
      wsProjIdCards[idx].setLevelAttrInfoList(getLevelAttrInfo(pidcVersionInfo, minLevel, maxLevel));

      idx++;
    }

    // the response object
    GetAllProjectIdCardsResponseType response = new GetAllProjectIdCardsResponseType();
    response.setProjectIdCards(wsProjIdCards);

    return response;
  }

  private ServiceData createServiceData(final Session session) {
    ServiceData serviceData = new ServiceData();

    if (session != null) {
      // Set user name and password from session
      if (CommonUtils.isNotEmptyString(session.getUserName())) {
        serviceData.setUsername(session.getUserName());
      }
      if (CommonUtils.isNotEmptyString(session.getPassWord())) {
        serviceData.setPassword(session.getPassWord());
      }
      // 496335 - PIDC differences SOAP service not returning time in proper timezone
      serviceData.setTimezone(session.getTimezone().getID());
    }

    serviceData.setLanguage("English");

    UserLoader userLdr = new UserLoader(serviceData);
    userLdr.authenticateCurrentUser();

    return serviceData;
  }

  /**
   * @param getAttrGroups
   * @return
   */
  public GetAttrGroupsResponseType getAttrGroups(final GetAttrGroups getAttrGroups) {
    /**
     * Session Variable for current call
     */
    Session session = getSession(getAttrGroups.getSessID(), "GetAttrGroups", "Get all groups/supergroups");

    // the response object
    GetAttrGroupsResponseType response = new GetAttrGroupsResponseType();

    try (ServiceData serviceData = createServiceData(session)) {
      AttrGroupModel model = (new AttrSuperGroupLoader(serviceData)).getAttrGroupModel(true);


      List<SuperGroupType> wsSuperGroups = new ArrayList<>();
      List<GroupType> wsGroups;

      SuperGroupType newSuperGroup;
      GroupType newGroup;

      for (AttrSuperGroup attrSuperGroup : model.getAllSuperGroupMap().values()) {

        newSuperGroup = new SuperGroupTypeWsBo(session);

        newSuperGroup.setId(attrSuperGroup.getId());
        newSuperGroup.setNameE(attrSuperGroup.getNameEng());

        if (CommonUtils.isEmptyString(attrSuperGroup.getNameGer())) {
          newSuperGroup.setNameG(attrSuperGroup.getNameEng());
        }
        else {
          newSuperGroup.setNameG(attrSuperGroup.getNameGer());
        }

        newSuperGroup.setDescrE(attrSuperGroup.getDescriptionEng());

        if (CommonUtils.isEmptyString(attrSuperGroup.getDescriptionGer())) {
          newSuperGroup.setDescrG(attrSuperGroup.getDescriptionEng());
        }
        else {
          newSuperGroup.setDescrG(attrSuperGroup.getDescriptionGer());
        }

        newSuperGroup.setIsDeleted(false);

        newSuperGroup.setChangeNumber(attrSuperGroup.getVersion());

        newSuperGroup.setCreateDate(DateUtil.dateToCalendar(attrSuperGroup.getCreatedDate()));
        newSuperGroup.setCreateUser(attrSuperGroup.getCreatedUser());

        newSuperGroup.setModifyDate(DateUtil.dateToCalendar(attrSuperGroup.getModifiedDate()));
        newSuperGroup.setModifyUser(attrSuperGroup.getModifiedUser());

        Set<Long> groupIdSet = model.getGroupBySuperGroupMap().get(attrSuperGroup.getId());

        wsGroups = new ArrayList<>();

        if (groupIdSet != null) {
          for (Long groupId : groupIdSet) {
            AttrGroup attrGroup = model.getAllGroupMap().get(groupId);

            newGroup = new GroupTypeWsBo(session);

            newGroup.setId(attrGroup.getId());

            newGroup.setNameE(attrGroup.getNameEng());
            // get German Group name
            if (CommonUtils.isEmptyString(attrGroup.getNameGer())) {
              newGroup.setNameG(attrGroup.getNameEng());
            }
            else {
              newGroup.setNameG(attrGroup.getNameGer());
            }

            newGroup.setDescrE(attrGroup.getDescriptionEng());
            // get German Group description
            if (CommonUtils.isEmptyString(attrGroup.getDescriptionGer())) {
              newGroup.setDescrG(attrGroup.getDescriptionEng());
            }
            else {
              newGroup.setDescrG(attrGroup.getDescriptionGer());
            }

            newGroup.setIsDeleted(false);

            newGroup.setCreateDate(DateUtil.dateToCalendar(attrGroup.getCreatedDate()));
            newGroup.setCreateUser(attrGroup.getCreatedUser());

            newGroup.setModifyDate(DateUtil.dateToCalendar(attrGroup.getModifiedDate()));
            newGroup.setModifyUser(attrGroup.getModifiedUser());

            newGroup.setChangeNumber(attrGroup.getVersion());

            wsGroups.add(newGroup);
          }
        }
        newSuperGroup.setGroups(wsGroups.toArray(new GroupType[0]));

        wsSuperGroups.add(newSuperGroup);


      }

      response.setSuperGroups(wsSuperGroups.toArray(new SuperGroupType[0]));
    }
    catch (IcdmException e) {
      getLogger().error(e.getMessage(), e);
    }
    return response;
  }


  /**
   * @param pidcVer
   * @return
   */
  private LevelAttrInfo[] getLevelAttrInfo(final Map<Long, PidcVersionAttribute> structureAttributes,
      final long minLevel, final long maxLevel) {

    LevelAttrInfo[] levelAttrInfo = new LevelAttrInfo[structureAttributes.size()];
    int idx;

    for (long level = minLevel; level <= maxLevel; level++) {
      idx = (int) (level - minLevel);

      PidcVersionAttribute structAttr = structureAttributes.get(level);

      levelAttrInfo[idx] = new LevelAttrInfo();
      levelAttrInfo[idx].setLevelNo((int) level);
      levelAttrInfo[idx].setLevelAttrId(structAttr.getAttrId());
      if (null != structAttr.getValueId()) {
        levelAttrInfo[idx].setLevelAttrValueId(structAttr.getValueId());
      }
      levelAttrInfo[idx].setLevelName(structAttr.getValue());
    }
    return levelAttrInfo;
  }

  /**
   * @param pidcVer
   * @return
   */
  private LevelAttrInfo[] getLevelAttrInfo(final PidcVersionInfo pidcVer, final long minLevel, final long maxLevel) {

    LevelAttrInfo[] levelAttrInfo = new LevelAttrInfo[pidcVer.getLevelAttrMap().size()];
    int idx;

    for (long level = minLevel; level <= maxLevel; level++) {
      idx = (int) (level - minLevel);

      PidcVersionAttribute structAttr = pidcVer.getLevelAttrMap().get(level);

      levelAttrInfo[idx] = new LevelAttrInfo();
      levelAttrInfo[idx].setLevelNo((int) level);
      levelAttrInfo[idx].setLevelAttrId(structAttr.getAttrId());
      levelAttrInfo[idx].setLevelAttrValueId(structAttr.getValueId());
      levelAttrInfo[idx].setLevelName(structAttr.getValue());
    }
    return levelAttrInfo;
  }

  /**
   * This method returns APICWsRequestFault object
   *
   * @param paramName
   * @param paramValue
   * @param methodName
   * @param errorCode
   * @param errorText
   * @return APICWsRequestFault
   */
  private APICWsRequestFault getRequestFault(final String paramName, final String paramValue, final String methodName,
      final int errorCode, final String errorText) {

    APICWsRequestFaultType faultDetails = new APICWsRequestFaultType();

    MethodParameterType[] methodParameters = new MethodParameterType[1];
    MethodParameterType parameter = new MethodParameterType();
    parameter.setParameterName(paramName);
    parameter.setParameterValue(paramValue);
    methodParameters[0] = parameter;

    faultDetails.setErrorCode(errorCode);
    faultDetails.setErrorText(errorText);
    faultDetails.setMethodName(methodName);

    faultDetails.setMethodParameters(methodParameters);

    APICWsRequestFault faultMsg = new APICWsRequestFault();
    faultMsg.setAPICWsRequestFault(faultDetails);
    return faultMsg;
  }

  /**
   * This method returns ProjectIdCardType object for given project id card id
   *
   * @param getProjectIdCard GetProjectIdCard
   * @return ProjectIdCardType
   * @throws GetProjectIdCardFaultException exception during
   */
  public GetProjectIdCardResponseType getProjIdCard(final GetProjectIdCard getProjectIdCard)
      throws GetProjectIdCardFaultException {

    /**
     * Session Variable for current call
     */
    Session session = getSession(getProjectIdCard.getSessID(), "GetProjIdCard",
        String.valueOf(getProjectIdCard.getProjectIdCardID()));

    // Task 269417
    PidcVersionWithAttributes pidcWithAttrs;
    Set<Long> useCaseIdSet;
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel;
    ProjectIdCardType wsProjectIdCard = new ProjectIdCardType();
    try (ServiceData servData = createServiceData(session)) {
      maxLevel = (new AttributeLoader(servData)).getMaxStructAttrLevel();
      // Create loader object
      PidcLoaderExternal loader = new PidcLoaderExternal(servData);

      long[] useCaseIds = getProjectIdCard.getUseCaseID();

      // convert usecase id array to set
      useCaseIdSet = new HashSet<>();
      if (null != useCaseIds) {
        for (Long useCaseId : useCaseIds) {
          useCaseIdSet.add(useCaseId);
        }
      }

      // get PidcVersionWithAttributes instance
      pidcWithAttrs = loader.getProjectIdCardWithAttrs(getProjectIdCard.getProjectIdCardID(), useCaseIdSet, null);


      // Set project Id card information
      wsProjectIdCard.setProjectIdCardDetails(
          getProjIdCardInfoFromPidcVersionWithAttr(pidcWithAttrs, session, minLevel, maxLevel));

      // Set attribute with value type information
      wsProjectIdCard.setAttributes(getPidcAttrAndValues(session, pidcWithAttrs, servData));

      // Set project id card variant type information
      wsProjectIdCard.setVariants(getPidcVariantWithAttrs(session, pidcWithAttrs));

    }
    catch (IcdmException exp) {
      throw new GetProjectIdCardFaultException(exp.getMessage(), exp);
    }
    // the response object
    GetProjectIdCardResponseType response = new GetProjectIdCardResponseType();
    response.setProjectIdCard(wsProjectIdCard);

    return response;
  }

  /**
   * Task 269417
   *
   * @param session
   * @param pidcWithAttrs
   * @return ProjectIdCardVariantType[]
   * @throws IcdmException
   */
  private ProjectIdCardVariantType[] getPidcVariantWithAttrs(final Session session,
      final PidcVersionWithAttributes pidcWithAttrs)
      throws IcdmException {


    // the response object
    ProjectIdCardVariantType[] wsPidcVariants;

    // Get project id card variants
    Collection<ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> dbPidcVariants =
        pidcWithAttrs.getVariantMap().values();

    // create the response object
    wsPidcVariants = new ProjectIdCardVariantType[dbPidcVariants.size()];

    int iCounter = 0;
    for (ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant : dbPidcVariants) {

      ProjectIdCardVariantType wsPidcVariant = new ProjectIdCardVariantType();

      // Set project id card variant information
      ProjectIdCardVariantInfoType wsVariantInfo = getWsVariantInfo(session, dbPidcVariant.getProjectObject());
      wsVariantInfo.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsPidcVariant.setPIdCVariant(wsVariantInfo);

      // Get variant attributes
      Map<Long, PidcVariantAttribute> dbVariantAttrs = dbPidcVariant.getProjectAttrMap();

      if ((dbVariantAttrs != null) && (dbVariantAttrs.size() > 0)) {
        fillVariantInfoForProject(session, dbPidcVariant, wsPidcVariant, dbVariantAttrs, pidcWithAttrs);
      }

      wsPidcVariants[iCounter] = wsPidcVariant;

      iCounter++;
    }

    return wsPidcVariants;

  }

  /**
   * Task 269417
   *
   * @param session
   * @param dbPidcVariant
   * @param wsPidcVariant
   * @param dbVariantAttrs
   * @param pidcWithAttrs
   * @throws IcdmException
   */
  private void fillVariantInfoForProject(final Session session,
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final ProjectIdCardVariantType wsPidcVariant, final Map<Long, PidcVariantAttribute> dbVariantAttrs,
      final PidcVersionWithAttributes pidcWithAttrs)
      throws IcdmException {
    AttributeWithValueType[] wsVariantAttrs = new AttributeWithValueType[dbVariantAttrs.size()];

    int index = 0;

    for (PidcVariantAttribute varAttr : dbVariantAttrs.values()) {

      AttributeWithValueType wsVariantAttr = new AttributeWithValueType();

      // set used information
      if (null == varAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsVariantAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag());
        wsVariantAttr.setUsed(usedEnum.getUiType());
      }


      // set isVariant information
      wsVariantAttr.setIsVariant(varAttr.isAtChildLevel());

      // get attribute info
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(varAttr.getAttrId());
      wsVariantAttr.setAttribute(getWsAttributeInfo(session, attribute));

      // get optional attribute information
      wsVariantAttr.setSpecLink(varAttr.getSpecLink());
      wsVariantAttr.setPartNumber(varAttr.getPartNumber());
      wsVariantAttr.setDescription(varAttr.getAdditionalInfoDesc());

      // get PIDC attribute version
      wsVariantAttr.setChangeNumber(varAttr.getVersion());

      // Task 262097
      if ((varAttr.getValueId() != null) && !varAttr.isAtChildLevel()) {

        com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
            pidcWithAttrs.getAttributeValueMap().get(varAttr.getValueId());

        // set fields for variant attribute value
        wsVariantAttr.setValue(createWsAttributeValue(session, attributeValue));
      }

      wsVariantAttrs[index] = wsVariantAttr;

      index++;

    }

    wsPidcVariant.setAttributes(wsVariantAttrs);
    if (CommonUtils.isNotEmpty(pidcWithAttrs.getVarWithSubVarIds().get(dbPidcVariant.getProjectObject().getId()))) {
      wsPidcVariant.setSubVariants(getSubVariantInfo(session, dbPidcVariant, pidcWithAttrs));
    }

  }

  /**
   * Task 269417
   *
   * @param session
   * @param dbPidcVariant
   * @param pidcWithAttrs
   * @return
   * @throws IcdmException
   */
  private ProjectIdCardVariantType[] getSubVariantInfo(final Session session,
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final PidcVersionWithAttributes pidcWithAttrs)
      throws IcdmException {
    // the response object
    ProjectIdCardVariantType[] wsSubVariants;

    // Get sub-variants of the variant
    Collection<Long> dbSubVariants = pidcWithAttrs.getVarWithSubVarIds().get(dbPidcVariant.getProjectObject().getId());

    // create the response object
    wsSubVariants = new ProjectIdCardVariantType[dbSubVariants.size()];

    int iCounter = 0;
    for (Long subVariantId : dbSubVariants) {

      ProjectIdCardVariantType wsSubVariant = new ProjectIdCardVariantType();

      ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> subVarProjObjWithAttr =
          pidcWithAttrs.getSubVariantMap().get(subVariantId);
      // Set sub-variant information
      ProjectIdCardVariantInfoType wsSubVariantInfo =
          getWsSubVariantInfo(session, subVarProjObjWithAttr.getProjectObject());
      wsSubVariantInfo.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsSubVariant.setPIdCVariant(wsSubVariantInfo);

      // Get variant attributes
      Map<Long, PidcSubVariantAttribute> dbSubVariantAttrs = subVarProjObjWithAttr.getProjectAttrMap();

      if ((dbSubVariantAttrs != null) && (dbSubVariantAttrs.size() > 0)) {
        AttributeWithValueType[] wsSubVariantAttrs = new AttributeWithValueType[dbSubVariantAttrs.size()];

        int index = 0;

        for (PidcSubVariantAttribute svarAttr : dbSubVariantAttrs.values()) {


          AttributeWithValueType wsVariantAttr = new AttributeWithValueType();

          // set used information
          if (null == svarAttr.getUsedFlag()) {
            // in case of hidden attributes
            wsVariantAttr.setUsed("");
          }
          else {
            PROJ_ATTR_USED_FLAG usedEnum;
            usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(svarAttr.getUsedFlag());
            wsVariantAttr.setUsed(usedEnum.getUiType());
          }

          // set isVariant information
          wsVariantAttr.setIsVariant(svarAttr.isAtChildLevel());

          // get attribute info
          com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
              pidcWithAttrs.getAttributeMap().get(svarAttr.getAttrId());
          wsVariantAttr.setAttribute(getWsAttributeInfo(session, attribute));

          // get optional attribute information
          wsVariantAttr.setSpecLink(svarAttr.getSpecLink());
          wsVariantAttr.setPartNumber(svarAttr.getPartNumber());
          wsVariantAttr.setDescription(svarAttr.getAdditionalInfoDesc());

          // get PIDC attribute version
          wsVariantAttr.setChangeNumber(svarAttr.getVersion());

          if ((svarAttr.getValueId() != null) && !svarAttr.isAtChildLevel()) {

            com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
                pidcWithAttrs.getAttributeValueMap().get(svarAttr.getValueId());

            // set fields for variant attribute value
            wsVariantAttr.setValue(getWsAttrValue(session, attributeValue));
          }


          wsSubVariantAttrs[index] = wsVariantAttr;

          index++;

        }

        wsSubVariant.setAttributes(wsSubVariantAttrs);
      }

      wsSubVariants[iCounter] = wsSubVariant;

      iCounter++;
    }

    return wsSubVariants;
  }

  /**
   * Task 269417
   *
   * @param session
   * @param subVar
   * @return
   * @throws IcdmException
   */
  private ProjectIdCardVariantInfoType getWsSubVariantInfo(final Session session, final PidcSubVariant subVar)
      throws IcdmException {
    // the result object
    ProjectIdCardVariantInfoType wsVariantInfo = new ProjectIdCardVariantInfoTypeWsBo(session);

    // get the variant information

    if (subVar.getName() != null) {
      // Set variant ID
      wsVariantInfo.setId(subVar.getId());

      wsVariantInfo.setName(subVar.getName());

      wsVariantInfo.setChangeNumber(subVar.getVersion());


      wsVariantInfo.setIsDeleted(subVar.isDeleted());

      wsVariantInfo.setCreateUser(subVar.getCreatedUser());
      wsVariantInfo
          .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, subVar.getCreatedDate()));

      wsVariantInfo.setModifyUser(subVar.getModifiedUser());
      wsVariantInfo
          .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, subVar.getModifiedDate()));

    }

    return wsVariantInfo;
  }

  /**
   * Task 269417
   *
   * @param session
   * @param pidcVar
   * @return
   * @throws IcdmException
   */
  private ProjectIdCardVariantInfoType getWsVariantInfo(final Session session, final PidcVariant pidcVar)
      throws IcdmException {
    // the result object
    ProjectIdCardVariantInfoType wsVariantInfo = new ProjectIdCardVariantInfoTypeWsBo(session);

    // get the variant information

    if (pidcVar.getName() != null) {
      // Set variant ID
      wsVariantInfo.setId(pidcVar.getId());

      wsVariantInfo.setName(pidcVar.getName());

      wsVariantInfo.setChangeNumber(pidcVar.getVersion());


      wsVariantInfo.setIsDeleted(pidcVar.isDeleted());

      wsVariantInfo.setCreateUser(pidcVar.getCreatedUser());
      wsVariantInfo
          .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, pidcVar.getCreatedDate()));

      wsVariantInfo.setModifyUser(pidcVar.getModifiedUser());
      wsVariantInfo
          .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, pidcVar.getModifiedDate()));

    }

    return wsVariantInfo;
  }

  /**
   * Task 269417
   *
   * @param session
   * @param pidcWithAttrs
   * @return AttributeWithValueType[]
   * @throws IcdmException
   */
  private AttributeWithValueType[] getPidcAttrAndValues(final Session session,
      final PidcVersionWithAttributes pidcWithAttrs, final ServiceData servData)
      throws IcdmException {

    // get the attributes of the PIDC
    Collection<PidcVersionAttribute> dbPidcAttrs = pidcWithAttrs.getPidcAttributeMap().values();
    int size = dbPidcAttrs.size();

    // the response object
    AttributeWithValueType[] wsPidcAttrs = new AttributeWithValueType[size + 1];

    int iCounter = 0;
    for (PidcVersionAttribute dbPidcAttr : dbPidcAttrs) {
      AttributeWithValueType wsPidcAttr = new AttributeWithValueType();

      // set used information
      if (null == dbPidcAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsPidcAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbPidcAttr.getUsedFlag());
        wsPidcAttr.setUsed(usedEnum.getUiType());
      }


      // set isVariant information
      wsPidcAttr.setIsVariant(dbPidcAttr.isAtChildLevel());
      // set attribute information
      wsPidcAttr.setAttribute(getWsAttributeInfo(session, pidcWithAttrs.getAttributeMap().get(dbPidcAttr.getAttrId())));

      // Task 262097
      // set attribute value information
      if ((dbPidcAttr.getValueId() != null) && !dbPidcAttr.isAtChildLevel()) {
        // Set attribute value information
        wsPidcAttr.setValue(getWsAttrValueInfo(session, pidcWithAttrs, dbPidcAttr));
      }

      // get optional attribute information
      wsPidcAttr.setSpecLink(dbPidcAttr.getSpecLink());
      wsPidcAttr.setPartNumber(dbPidcAttr.getPartNumber());
      wsPidcAttr.setDescription(dbPidcAttr.getAdditionalInfoDesc());

      // set PIDC attribute version
      if (null != dbPidcAttr.getVersion()) {
        wsPidcAttr.setChangeNumber(dbPidcAttr.getVersion());
      }

      wsPidcAttrs[iCounter] = wsPidcAttr;

      iCounter++;

    }


    // Task 242055
    // set the dummy attribute and value for last confirmation date
    AttributeWithValueType wsPidcAttr = new AttributeWithValueType();
    wsPidcAttr.setAttribute(getWsAttrForConfrmDate(session, servData));
    wsPidcAttr.setUsed("Y");
    wsPidcAttr.setIsVariant(false);
    wsPidcAttr.setValue(getWSValueForConfrmDate(session, pidcWithAttrs, servData));
    wsPidcAttr.setChangeNumber(1);
    wsPidcAttrs[iCounter] = wsPidcAttr;

    return wsPidcAttrs;

  }

  /**
   * Task 269417
   *
   * @param session
   * @param pidcWithAttrs
   * @return
   * @throws IcdmException
   */
  private AttributeValue getWSValueForConfrmDate(final Session session, final PidcVersionWithAttributes pidcWithAttrs,
      final ServiceData servData)
      throws IcdmException {

    // create the response object
    AttributeValue attrVal = new AttributeValueWsBo(session);

    // Set Attribute Id
    attrVal.setAttrID(Long.parseLong("-1"));

    // set the English attribute value depending on the confirmation date
    Calendar lastConfirmationDate = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getLastConfirmationDate());
    if (null == lastConfirmationDate) {
      // Review 253218
      attrVal.setValueE("Not Defined");
    }
    else {
      attrVal.setValueE(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, lastConfirmationDate));
      // Task 271557
      // create a new instance for calculating the last valid date from last confirmation date
      Calendar lastValidDate = Calendar.getInstance();
      lastValidDate.setTimeInMillis(lastConfirmationDate.getTimeInMillis());
      // get the interval days between last confirmation and valid date from TABV_COMMON_PARAMS
      Long intervalDays =
          Long.valueOf(new CommonParamLoader(servData).getValue(CommonParamKey.PIDC_UP_TO_DATE_INTERVAL));
      // add the interval days to find out the last valid date
      lastValidDate.add(Calendar.DAY_OF_MONTH, intervalDays.intValue());
      // set the last valid date
      attrVal.setValueG(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, lastValidDate));
      // set the interval days
      attrVal.setValueDescE(intervalDays.toString());
    }


    // Set attribute value id
    attrVal.setValueID(Long.parseLong("-1"));

    // Set attribute value deleted information
    attrVal.setIsDeleted(false);


    // Attribute value created date
    Calendar createdDate = getCreatedDateForDummyAttr();
    attrVal.setCreateDate(createdDate);

    // Attribute value created user
    attrVal.setCreateUser("DGS_ICDM");


    // Attribute value version
    attrVal.setChangeNumber(1);

    // Clearing Status
    attrVal.setClearingStatus("Y");

    return attrVal;


  }

  /**
   * Task 269417
   *
   * @param session
   * @param pidcWithAttrs
   * @param dbPidcAttr
   * @return
   * @throws IcdmException
   */
  private AttributeValue getWsAttrValueInfo(final Session session, final PidcVersionWithAttributes pidcWithAttrs,
      final PidcVersionAttribute dbPidcAttr)
      throws IcdmException {
    // create the response object

    com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
        pidcWithAttrs.getAttributeValueMap().get(dbPidcAttr.getValueId());


    return createWsAttributeValue(session, attributeValue);
  }

  /**
   * Task 269417
   *
   * @param pidcWithAttrs
   * @param valueType
   * @param respAttrVal
   * @param attrVal
   * @throws IcdmException
   */
  private AttributeValue createWsAttributeValue(final Session session,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrVal)
      throws IcdmException {


    AttributeValue respAttrVal = new AttributeValueWsBo(session);


    // Set Attribute Id
    respAttrVal.setAttrID(attrVal.getAttributeId());


    // set the English attribute value depending on the value type
    respAttrVal.setValueE(attrVal.getNameRaw());
    // set the German attribute value depending on the value type
    respAttrVal.setValueG(null == attrVal.getTextValueGer() ? attrVal.getNameRaw() : attrVal.getTextValueGer());

    // Set attribute value id
    respAttrVal.setValueID(attrVal.getId());

    // Set attribute value deleted information
    respAttrVal.setIsDeleted(attrVal.isDeleted());

    // Attribute value created date
    respAttrVal.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attrVal.getCreatedDate()));

    // Attribute value created user
    respAttrVal.setCreateUser(attrVal.getCreatedUser());

    // Attribute value modified date
    respAttrVal.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attrVal.getModifiedDate()));

    // Attribute value modified user
    respAttrVal.setModifyUser(attrVal.getModifiedUser());

    // Attribute value version
    respAttrVal.setChangeNumber(attrVal.getVersion());

    // Clearing Status
    String clearingStatus = attrVal.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    respAttrVal.setClearingStatus(clStatus.getUiText());

    // Attribute isCleraed()
    respAttrVal.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    // iCDM-1273
    respAttrVal.setValueDescE(attrVal.getDescriptionEng());
    respAttrVal.setValueDescG(CommonUtils.checkNull(attrVal.getDescriptionGer()));

    if (AttributeValueType.ICDM_USER.getDisplayText().equals(attrVal.getValueType())) {
      respAttrVal.setValueE(attrVal.getOtherValue() == null ? "" : attrVal.getOtherValue());
      respAttrVal.setValueDescE(attrVal.getTextValueEng());
    }
    return respAttrVal;
  }

  /**
   * Task 269417
   *
   * @param session
   * @param attribute
   * @return
   * @throws IcdmException
   */
  private Attribute getWsAttributeInfo(final Session session,
      final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute)
      throws IcdmException {
    Attribute wsAttribute = new AttributeWsBo(session);

    // Set attribute id
    wsAttribute.setId(attribute.getId());
    // Set attribute English name
    String attrNameE = (CommonUtils.checkNull(attribute.getNameEng()));
    wsAttribute.setNameE(attrNameE);
    // Set attribute German name
    String attrNameG = attribute.getNameGer();
    if (CommonUtils.isEmptyString(attrNameG)) {
      wsAttribute.setNameG(attrNameE);
    }
    else {
      wsAttribute.setNameG(attrNameG);
    }
    // Set attribute English description
    String attrDescE = (CommonUtils.checkNull(attribute.getDescriptionEng()));
    wsAttribute.setDescrE(attrDescE);
    // Set attribute German description
    String attrDescG = attribute.getDescriptionGer();
    if (CommonUtils.isNotEmptyString(attrDescG)) {
      wsAttribute.setDescrG(attrDescG);
    }
    else {
      wsAttribute.setDescrG(attrDescE);
    }
    // Set attribute deleted information
    wsAttribute.setIsDeleted(attribute.isDeleted());
    // Set attribute normalized information
    wsAttribute.setIsNormalized(attribute.isNormalized());
    // Set attribute mandatory information
    wsAttribute.setIsMandatory(attribute.isMandatory());
    // Set attribute unit information
    wsAttribute.setUnit(attribute.getUnit());
    // Set attribute format information
    if (null == attribute.getFormat()) {
      wsAttribute.setFormat("");
    }
    else {
      wsAttribute.setFormat(attribute.getFormat());
    }
    // Set attribute created date information
    wsAttribute
        .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attribute.getCreatedDate()));
    // Set attribute created user information
    wsAttribute.setCreateUser(attribute.getCreatedUser());
    // Set attribute modified date information
    wsAttribute
        .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attribute.getModifiedDate()));
    // Set attribute modified user information
    wsAttribute.setModifyUser(attribute.getModifiedUser());
    // Set attribute value type id
    String valueType = attribute.getValueType();
    wsAttribute.setTypeID(AttributeValueType.getType(valueType).getValueTypeID());
    // Set attribute group id
    wsAttribute.setGroupID(attribute.getAttrGrpId().longValue());

    // Set attribute version
    wsAttribute.setChangeNumber(attribute.getVersion());

    return wsAttribute;
  }

  /**
   * // Task 269417
   *
   * @param pidcWithAttrs PidcVersionWithAttributes
   * @param session
   * @param maxLevel
   * @param minLevel
   * @return ProjectIdCardInfoType
   * @throws IcdmException
   */
  private ProjectIdCardInfoType getProjIdCardInfoFromPidcVersionWithAttr(final PidcVersionWithAttributes pidcWithAttrs,
      final Session session, final long minLevel, final long maxLevel)
      throws IcdmException {
    // the response object
    ProjectIdCardInfoType wsProjIdCard = new ProjectIdCardInfoTypeWsBo(session);

    // Set PIDC name
    wsProjIdCard.setName(pidcWithAttrs.getPidcVersionInfo().getPidc().getName());

    // set the PIDC ID from the Active Pidc version
    wsProjIdCard.setId(pidcWithAttrs.getPidcVersionInfo().getPidc().getId());

    // set the PIDC version number
    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // get deleted flag
    wsProjIdCard.setIsDeleted(pidcWithAttrs.getPidcVersionInfo().getPidc().isDeleted());

    // get create user and date
    wsProjIdCard.setCreateUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedUser());
    wsProjIdCard.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedDate()));

    // get modify user and date
    wsProjIdCard.setModifyUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedUser());
    wsProjIdCard.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedDate()));

    // clearing status
    CLEARING_STATUS clStatus =
        CLEARING_STATUS.getClearingStatus(pidcWithAttrs.getPidcVersionInfo().getPidc().getClearingStatus());
    wsProjIdCard.setClearingStatus(clStatus.getUiText());
    wsProjIdCard.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // level attribute info list
    wsProjIdCard.setLevelAttrInfoList(getLevelAttrInfo(pidcWithAttrs.getPidcVersionInfo(), minLevel, maxLevel));

    return wsProjIdCard;
  }


  /**
   * @param getProjectIdCard {@link GetProjectIdCardV2}
   * @return {@link GetProjectIdCardV2ResponseType}
   * @throws GetProjectIdCardV2FaultException exception
   */
  public GetProjectIdCardV2ResponseType getProjectIdCardV2(final GetProjectIdCardV2 getProjectIdCard)
      throws GetProjectIdCardV2FaultException {

    /**
     * Session Variable for current call
     */
    Session session = getSession(getProjectIdCard.getSessID(), "GetProjIdCardV2",
        String.valueOf(getProjectIdCard.getProjectIdCardID()));

    PidcVersionWithAttributesV2 pidcWithAttrs;
    Set<Long> useCaseIdSet;
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel;
    ProjectIdCardTypeV2 wsProjectIdCard = new ProjectIdCardTypeV2();
    try (ServiceData servData = createServiceData(session)) {
      PidcVersionLoader versionLoader = new PidcVersionLoader(servData);
      PidcVersion pidcVers = versionLoader.getActivePidcVersion(getProjectIdCard.getProjectIdCardID());
      if (CommonUtils.isNotNull(pidcVers)) {
        if (versionLoader.isHiddenToCurrentUser(pidcVers.getId())) {
          throw new GetProjectIdCardV2FaultException(
              PIDC_ID + getProjectIdCard.getProjectIdCardID() + " is not visible to the user");
        }

        maxLevel = (new AttributeLoader(servData)).getMaxStructAttrLevel();
        PidcLoaderExternal loader = new PidcLoaderExternal(servData);
        long[] useCaseIds = getProjectIdCard.getUseCaseID();

        // convert usecase id array to set
        useCaseIdSet = new HashSet<>();
        if (null != useCaseIds) {
          for (Long useCaseId : useCaseIds) {
            useCaseIdSet.add(useCaseId);
          }
        }
        // get PidcVersionWithAttributes instance
        pidcWithAttrs = loader.getProjectIdCardV2WithAttrs(getProjectIdCard.getProjectIdCardID(), useCaseIdSet, null);

        // Set project Id card information
        wsProjectIdCard.setProjectIdCardDetails(getProjIdCardInfoV2(pidcWithAttrs, session, minLevel, maxLevel));

        // Set attribute with value type information
        wsProjectIdCard.setAttributes(getPidcAttributesWithValuesV2(session, pidcWithAttrs, servData));

        // Set project id card variant type information
        wsProjectIdCard.setVariants(getPidcVariantInfoV2(session, pidcWithAttrs));
      }
      else {
        this.logger.warn("Project-ID-Card not found, ID: " + getProjectIdCard.getProjectIdCardID());

        // Get current method name
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        // Get APIC web service request fault
        GetProjectIdCardV2Fault faultMsg = getRequestFaultV2("SessionID", getProjectIdCard.getSessID(), methodName,
            -999999, "Project-ID-Card not found, ID: " + getProjectIdCard.getProjectIdCardID());

        GetProjectIdCardV2FaultException exception = new GetProjectIdCardV2FaultException();
        // Set APIC web service request fault information
        exception.setFaultMessage(faultMsg);
        throw exception;
      }
    }
    catch (IcdmException exp) {
      throw new GetProjectIdCardV2FaultException(exp.getMessage(), exp);
    }
    // the response object
    GetProjectIdCardV2ResponseType response = new GetProjectIdCardV2ResponseType();
    response.setProjectIdCard(wsProjectIdCard);
    return response;
  }

  private GetProjectIdCardV2Fault getRequestFaultV2(final String paramName, final String paramValue,
      final String methodName, final int errorCode, final String errorText) {

    GetProjectIdCardV2FaultType faultDetails = new GetProjectIdCardV2FaultType();

    MethodParameterTypeV2[] methodParameters = new MethodParameterTypeV2[1];
    MethodParameterTypeV2 parameter = new MethodParameterTypeV2();
    parameter.setParameterName(paramName);
    parameter.setParameterValue(paramValue);
    methodParameters[0] = parameter;

    faultDetails.setErrorCode(String.valueOf(errorCode));
    faultDetails.setErrorText(errorText);
    faultDetails.setMethodName(methodName);

    faultDetails.setMethodParameters(methodParameters);

    GetProjectIdCardV2Fault faultMsg = new GetProjectIdCardV2Fault();
    faultMsg.setGetProjectIdCardV2Fault(faultDetails);
    return faultMsg;
  }

  private ProjectIdCardVariantTypeV2[] getPidcVariantInfoV2(final Session session,
      final PidcVersionWithAttributesV2 pidcWithAttrs)
      throws IcdmException {

    // the response object
    ProjectIdCardVariantTypeV2[] wsPidcVariants;

    // Get project id card variants
    Collection<ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> dbPidcVariants =
        pidcWithAttrs.getVariantMap().values();

    // create the response object
    wsPidcVariants = new ProjectIdCardVariantTypeV2[dbPidcVariants.size()];

    int iCounter = 0;
    for (ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant : dbPidcVariants) {

      ProjectIdCardVariantTypeV2 wsPidcVariant = new ProjectIdCardVariantTypeV2();

      // Set project id card variant information
      ProjectIdCardVariantInfoTypeV2 wsVariantInfoV2 = getWsVariantInfoV2(session, dbPidcVariant.getProjectObject());
      wsVariantInfoV2.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsPidcVariant.setPidcVariant(wsVariantInfoV2);

      // Get variant attributes
      Map<Long, PidcVariantAttribute> dbVariantAttrs = dbPidcVariant.getProjectAttrMap();

      if ((dbVariantAttrs != null) && (dbVariantAttrs.size() > 0)) {
        fillVaraintInfoForProjectV2(session, dbPidcVariant, wsPidcVariant, dbVariantAttrs, pidcWithAttrs);
      }

      wsPidcVariants[iCounter] = wsPidcVariant;

      iCounter++;
    }

    return wsPidcVariants;
  }

  private void fillVaraintInfoForProjectV2(final Session session,
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final ProjectIdCardVariantTypeV2 wsPidcVariant, final Map<Long, PidcVariantAttribute> dbVariantAttrs,
      final PidcVersionWithAttributesV2 pidcWithAttrs)
      throws IcdmException {
    AttributeWithValueTypeV2[] wsVariantAttrs = new AttributeWithValueTypeV2[dbVariantAttrs.size()];

    int index = 0;

    for (PidcVariantAttribute varAttr : dbVariantAttrs.values()) {

      AttributeWithValueTypeV2 wsVariantAttr = new AttributeWithValueTypeV2();

      // set used information
      if (null == varAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsVariantAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag());
        wsVariantAttr.setUsed(usedEnum.getUiType());
      }


      // set isVariant information
      wsVariantAttr.setIsVariant(varAttr.isAtChildLevel());

      // get attribute info
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(varAttr.getAttrId());
      Characteristic characteristic = pidcWithAttrs.getCharacteristicMap().get(varAttr.getAttrId());
      wsVariantAttr.setAttribute(getWsAttributeV2(session, attribute, characteristic));

      // get optional attribute information
      wsVariantAttr.setSpecLink(varAttr.getSpecLink());
      wsVariantAttr.setPartNumber(varAttr.getPartNumber());
      wsVariantAttr.setDescription(varAttr.getAdditionalInfoDesc());

      // get PIDC attribute version
      wsVariantAttr.setChangeNumber(varAttr.getVersion());

      // Task 262097
      if ((varAttr.getValueId() != null) && !varAttr.isAtChildLevel()) {
        com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
            pidcWithAttrs.getAttributeValueMap().get(varAttr.getValueId());
        // set fields for variant attribute value
        wsVariantAttr.setValue(createWsAttributeValueV2(session, attributeValue));
      }

      wsVariantAttrs[index] = wsVariantAttr;

      index++;
    }

    wsPidcVariant.setAttributes(wsVariantAttrs);
    if (CommonUtils.isNotEmpty(pidcWithAttrs.getVarWithSubVarIds().get(dbPidcVariant.getProjectObject().getId()))) {
      wsPidcVariant.setSubVariants(getSubVariantInfoV2(session, dbPidcVariant, pidcWithAttrs));
    }
  }

  private ProjectIdCardVariantTypeV2[] getSubVariantInfoV2(final Session session,
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final PidcVersionWithAttributesV2 pidcWithAttrs)
      throws IcdmException {

    // the response object
    ProjectIdCardVariantTypeV2[] wsSubVariants;

    // Get sub-variants of the variant
    Collection<Long> dbSubVariants = pidcWithAttrs.getVarWithSubVarIds().get(dbPidcVariant.getProjectObject().getId());

    // create the response object
    wsSubVariants = new ProjectIdCardVariantTypeV2[dbSubVariants.size()];

    int iCounter = 0;
    for (Long subVariantId : dbSubVariants) {

      ProjectIdCardVariantTypeV2 wsSubVariant = new ProjectIdCardVariantTypeV2();

      ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> subVarProjObjWithAttr =
          pidcWithAttrs.getSubVariantMap().get(subVariantId);
      // Set sub-variant information
      ProjectIdCardVariantInfoTypeV2 wsSubVariantInfoV2 =
          getWsSubVariantInfoV2(session, subVarProjObjWithAttr.getProjectObject());
      wsSubVariantInfoV2.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsSubVariant.setPidcVariant(wsSubVariantInfoV2);

      // Get variant attributes
      Map<Long, PidcSubVariantAttribute> dbSubVariantAttrs = subVarProjObjWithAttr.getProjectAttrMap();

      if ((dbSubVariantAttrs != null) && (dbSubVariantAttrs.size() > 0)) {
        AttributeWithValueTypeV2[] wsSubVariantAttrs = new AttributeWithValueTypeV2[dbSubVariantAttrs.size()];

        int index = 0;

        for (PidcSubVariantAttribute svarAttr : dbSubVariantAttrs.values()) {

          AttributeWithValueTypeV2 wsVariantAttr = new AttributeWithValueTypeV2();
          // set used information
          if (null == svarAttr.getUsedFlag()) {
            // in case of hidden attributes
            wsVariantAttr.setUsed("");
          }
          else {
            PROJ_ATTR_USED_FLAG usedEnum;
            usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(svarAttr.getUsedFlag());
            wsVariantAttr.setUsed(usedEnum.getUiType());
          }

          // set isVariant information
          wsVariantAttr.setIsVariant(svarAttr.isAtChildLevel());

          // get attribute info
          com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
              pidcWithAttrs.getAttributeMap().get(svarAttr.getAttrId());
          Characteristic characteristic = pidcWithAttrs.getCharacteristicMap().get(svarAttr.getAttrId());
          wsVariantAttr.setAttribute(getWsAttributeV2(session, attribute, characteristic));

          // get optional attribute information
          wsVariantAttr.setSpecLink(svarAttr.getSpecLink());
          wsVariantAttr.setPartNumber(svarAttr.getPartNumber());
          wsVariantAttr.setDescription(svarAttr.getAdditionalInfoDesc());

          // get PIDC attribute version
          wsVariantAttr.setChangeNumber(svarAttr.getVersion());

          if ((svarAttr.getValueId() != null) && !svarAttr.isAtChildLevel()) {

            com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
                pidcWithAttrs.getAttributeValueMap().get(svarAttr.getValueId());

            // set fields for variant attribute value
            wsVariantAttr.setValue(createWsAttributeValueV2(session, attributeValue));
          }

          wsSubVariantAttrs[index] = wsVariantAttr;

          index++;
        }

        wsSubVariant.setAttributes(wsSubVariantAttrs);
      }

      wsSubVariants[iCounter] = wsSubVariant;

      iCounter++;
    }

    return wsSubVariants;
  }

  private ProjectIdCardVariantInfoTypeV2 getWsSubVariantInfoV2(final Session session, final PidcSubVariant subVar)
      throws IcdmException {

    // the result object
    ProjectIdCardVariantInfoTypeV2 wsVariantInfo = new ProjectIdCardVariantInfoTypeWsBoV2(session);

    // get the variant information

    if (subVar.getName() != null) {
      // Set variant ID
      wsVariantInfo.setId(subVar.getId());

      wsVariantInfo.setName(subVar.getName());

      wsVariantInfo.setChangeNumber(subVar.getVersion());


      wsVariantInfo.setIsDeleted(subVar.isDeleted());

      wsVariantInfo.setCreateUser(subVar.getCreatedUser());
      wsVariantInfo
          .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, subVar.getCreatedDate()));

      wsVariantInfo.setModifyUser(subVar.getModifiedUser());
      wsVariantInfo
          .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, subVar.getModifiedDate()));

    }

    return wsVariantInfo;
  }

  private ProjectIdCardVariantInfoTypeV2 getWsVariantInfoV2(final Session session, final PidcVariant pidcVar)
      throws IcdmException {

    // the result object
    ProjectIdCardVariantInfoTypeV2 wsVariantInfo = new ProjectIdCardVariantInfoTypeWsBoV2(session);

    // get the variant information

    if (pidcVar.getName() != null) {
      // Set variant ID
      wsVariantInfo.setId(pidcVar.getId());

      wsVariantInfo.setName(pidcVar.getName());

      wsVariantInfo.setChangeNumber(pidcVar.getVersion());

      wsVariantInfo.setIsDeleted(pidcVar.isDeleted());

      wsVariantInfo.setCreateUser(pidcVar.getCreatedUser());
      wsVariantInfo
          .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, pidcVar.getCreatedDate()));

      wsVariantInfo.setModifyUser(pidcVar.getModifiedUser());
      wsVariantInfo
          .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, pidcVar.getModifiedDate()));
    }

    return wsVariantInfo;
  }

  private AttributeWithValueTypeV2[] getPidcAttributesWithValuesV2(final Session session,
      final PidcVersionWithAttributesV2 pidcWithAttrs, final ServiceData serviceData)
      throws IcdmException {

    // get the attributes of the PIDC
    Collection<PidcVersionAttribute> dbPidcAttrs = pidcWithAttrs.getPidcAttributeMap().values();
    int size = dbPidcAttrs.size();
    // the response object
    AttributeWithValueTypeV2[] wsPidcAttrs = new AttributeWithValueTypeV2[size + 1];

    int iCounter = 0;
    for (PidcVersionAttribute dbPidcAttr : dbPidcAttrs) {

      AttributeWithValueTypeV2 wsPidcAttr = new AttributeWithValueTypeV2();

      // set used information
      if (null == dbPidcAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsPidcAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbPidcAttr.getUsedFlag());
        wsPidcAttr.setUsed(usedEnum.getUiType());
      }

      // set isVariant information
      wsPidcAttr.setIsVariant(dbPidcAttr.isAtChildLevel());
      // set attribute information
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(dbPidcAttr.getAttrId());
      wsPidcAttr.setAttribute(getWsAttributeV2(session, attribute,
          pidcWithAttrs.getCharacteristicMap().get(attribute.getCharacteristicId())));

      // set attribute value information
      if ((dbPidcAttr.getValueId() != null) && !dbPidcAttr.isAtChildLevel()) {
        // Set attribute value information
        wsPidcAttr.setValue(getWsAttrValueV2(session, pidcWithAttrs, dbPidcAttr));
      }

      // get optional attribute information
      wsPidcAttr.setSpecLink(dbPidcAttr.getSpecLink());
      wsPidcAttr.setPartNumber(dbPidcAttr.getPartNumber());
      wsPidcAttr.setDescription(dbPidcAttr.getAdditionalInfoDesc());

      // set PIDC attribute version
      if (null != dbPidcAttr.getVersion()) {
        wsPidcAttr.setChangeNumber(dbPidcAttr.getVersion());
      }
      wsPidcAttrs[iCounter] = wsPidcAttr;

      iCounter++;
    }


    // Task 242055
    // set the dummy attribute and value for last confirmation date
    AttributeWithValueTypeV2 wsPidcAttr = new AttributeWithValueTypeV2();
    wsPidcAttr.setAttribute(getWsAttrForConfrmDateV2(session, serviceData));
    wsPidcAttr.setUsed("Y");
    wsPidcAttr.setIsVariant(false);
    wsPidcAttr.setValue(getWSValueForConfrmDateV2(session, pidcWithAttrs, serviceData));
    wsPidcAttr.setChangeNumber(1);
    wsPidcAttrs[iCounter] = wsPidcAttr;

    return wsPidcAttrs;
  }

  private AttributeValueV2 getWsAttrValueV2(final Session session, final PidcVersionWithAttributesV2 pidcWithAttrs,
      final PidcVersionAttribute dbPidcAttr)
      throws IcdmException {

    // create the response object
    com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
        pidcWithAttrs.getAttributeValueMap().get(dbPidcAttr.getValueId());

    return createWsAttributeValueV2(session, attributeValue);
  }

  private AttributeValueV2 createWsAttributeValueV2(final Session session,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrVal)
      throws IcdmException {

    AttributeValueV2 respAttrVal = new AttributeValueWsBoV2(session);

    // Set Attribute Id
    respAttrVal.setAttrID(attrVal.getAttributeId());

    // set the English attribute value depending on the value type
    respAttrVal.setValueE(attrVal.getNameRaw());
    // set the German attribute value depending on the value type

    if (null == attrVal.getTextValueGer()) {
      // if text value German is not found
      respAttrVal.setValueG("");
    }
    else {
      respAttrVal.setValueG(attrVal.getTextValueGer());
    }

    // Set attribute value id
    respAttrVal.setValueID(attrVal.getId());

    // Set attribute value deleted information
    respAttrVal.setIsDeleted(attrVal.isDeleted());

    // Attribute value created date
    respAttrVal.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attrVal.getCreatedDate()));

    // Attribute value created user
    respAttrVal.setCreateUser(attrVal.getCreatedUser());

    // Attribute value modified date
    respAttrVal.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attrVal.getModifiedDate()));

    // Attribute value modified user
    respAttrVal.setModifyUser(attrVal.getModifiedUser());

    // Attribute value version
    respAttrVal.setChangeNumber(attrVal.getVersion());

    // Clearing Status
    String clearingStatus = attrVal.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    respAttrVal.setClearingStatus(clStatus.getUiText());

    // Attribute isCleraed()
    respAttrVal.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    // iCDM-1273
    respAttrVal.setValueDescE(attrVal.getDescriptionEng());
    if (null == attrVal.getDescriptionGer()) {
      respAttrVal.setValueDescG("");
    }
    else {
      respAttrVal.setValueDescG(attrVal.getDescriptionGer());
    }
    return respAttrVal;
  }

  private AttributeValueV2 getWSValueForConfrmDateV2(final Session session,
      final PidcVersionWithAttributesV2 pidcWithAttrs, final ServiceData serviceData)
      throws IcdmException {


    // create the response object
    AttributeValueV2 attrVal = new AttributeValueWsBoV2(session);

    // Set Attribute Id
    attrVal.setAttrID(Long.parseLong("-1"));

    // set the English attribute value depending on the confirmation date
    Calendar lastConfirmationDate = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getLastConfirmationDate());
    if (null == lastConfirmationDate) {
      // Review 253218
      attrVal.setValueE("Not Defined");
    }
    else {
      attrVal.setValueE(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, lastConfirmationDate));
      // Task 271557
      // create a new instance for calculating the last valid date from last confirmation date
      Calendar lastValidDate = Calendar.getInstance();
      lastValidDate.setTimeInMillis(lastConfirmationDate.getTimeInMillis());
      // get the interval days between last confirmation and valid date from TABV_COMMON_PARAMS
      Long intervalDays =
          Long.valueOf(new CommonParamLoader(serviceData).getValue(CommonParamKey.PIDC_UP_TO_DATE_INTERVAL));
      // add the interval days to find out the last valid date
      lastValidDate.add(Calendar.DAY_OF_MONTH, intervalDays.intValue());
      // set the last valid date
      attrVal.setValueG(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, lastValidDate));
      // set the interval days
      attrVal.setValueDescE(intervalDays.toString());
    }


    // Set attribute value id
    attrVal.setValueID(Long.parseLong("-1"));

    // Set attribute value deleted information
    attrVal.setIsDeleted(false);


    // Attribute value created date
    Calendar createdDate = getCreatedDateForDummyAttr();
    attrVal.setCreateDate(createdDate);

    // Attribute value created user
    attrVal.setCreateUser("DGS_ICDM");


    // Attribute value version
    attrVal.setChangeNumber(1);

    // Clearing Status
    attrVal.setClearingStatus("Y");

    return attrVal;

  }

  private AttributeV2 getWsAttrForConfrmDateV2(final Session session, final ServiceData serviceData) {
    AttributeV2 wsAttribute = new AttributeWsBoV2(session);
    MessageLoader messageLoader = new MessageLoader(serviceData);
    // Set attribute id
    wsAttribute.setId(Long.valueOf("-1"));
    // Set attribute English name
    String attrNameEng = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_TEXT_ENG");
    wsAttribute.setNameE(attrNameEng);
    // Set attribute German name
    String attrNameGer = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_TEXT_GER");
    wsAttribute.setNameG(attrNameGer);

    // Set attribute English description
    String attrDescEng = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_DESC_ENG");
    wsAttribute.setDescrE(attrDescEng);
    // Set attribute German description
    String attrDescGer = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_DESC_GER");
    wsAttribute.setDescrG(attrDescGer);

    // Set attribute deleted information
    wsAttribute.setIsDeleted(false);
    // Set attribute normalized information
    wsAttribute.setIsNormalized(false);
    // Set attribute mandatory information
    wsAttribute.setIsMandatory(false);
    // Set attribute unit information
    wsAttribute.setUnit("");
    // Set attribute format information
    wsAttribute.setFormat(DateFormat.DATE_FORMAT_09);
    // Set attribute created date information
    Calendar createdDate = getCreatedDateForDummyAttr();
    wsAttribute.setCreateDate(createdDate);
    // Set attribute created user information
    wsAttribute.setCreateUser("DGS_ICDM");

    // Set attribute value type id
    wsAttribute.setTypeID(AttributeValueType.DATE.getValueTypeID());
    // Set attribute group id

    String attrGroupId = new CommonParamLoader(serviceData).getValue(CommonParamKey.PIDC_UP_TO_DATE_ATTR_GROUP_ID);
    wsAttribute.setGroupID(Long.parseLong(attrGroupId));

    // Set attribute version
    wsAttribute.setChangeNumber(1);

    return wsAttribute;
  }

  private AttributeV2 getWsAttributeV2(final Session session,
      final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute, final Characteristic characteristic)
      throws IcdmException {
    AttributeV2 wsAttribute = new AttributeWsBoV2(session);

    // Set attribute id
    wsAttribute.setId(attribute.getId());
    // Set attribute English name
    wsAttribute.setNameE(attribute.getNameEng());
    // Set attribute German name
    String attrNameG = attribute.getNameGer();
    if ((null == attrNameG) || "".equals(attrNameG)) {
      wsAttribute.setNameG(attribute.getNameEng());
    }
    else {
      wsAttribute.setNameG(attrNameG);
    }
    // Set attribute English description
    wsAttribute.setDescrE(attribute.getDescriptionEng());
    // Set attribute German description
    String attrDescG = attribute.getDescriptionGer();
    if (CommonUtils.isNotEmptyString(attrDescG)) {
      wsAttribute.setDescrG(attrDescG);
    }
    else {
      wsAttribute.setDescrG(attribute.getDescriptionEng());
    }
    // Set attribute deleted information
    wsAttribute.setIsDeleted(attribute.isDeleted());
    // Set attribute normalized information
    wsAttribute.setIsNormalized(attribute.isNormalized());
    // Set attribute mandatory information
    wsAttribute.setIsMandatory(attribute.isMandatory());
    // Set attribute unit information
    wsAttribute.setUnit(attribute.getUnit());
    // Set attribute format information
    if (null == attribute.getFormat()) {
      wsAttribute.setFormat("");
    }
    else {
      wsAttribute.setFormat(attribute.getFormat());
    }
    // Set attribute created date information
    wsAttribute
        .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attribute.getCreatedDate()));
    // Set attribute created user information
    wsAttribute.setCreateUser(attribute.getCreatedUser());
    // Set attribute modified date information
    wsAttribute
        .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attribute.getModifiedDate()));
    // Set attribute modified user information
    wsAttribute.setModifyUser(attribute.getModifiedUser());
    // Set attribute value type id
    String valueType = attribute.getValueType();
    wsAttribute.setTypeID(AttributeValueType.getType(valueType).getValueTypeID());
    // Set attribute group id
    wsAttribute.setGroupID(attribute.getAttrGrpId().longValue());
    // Set attribute version
    wsAttribute.setChangeNumber(attribute.getVersion());

    if (null != characteristic) {
      wsAttribute.setCharacterID(characteristic.getId());
      wsAttribute.setCharacterName(characteristic.getName());
    }
    return wsAttribute;
  }

  private ProjectIdCardInfoTypeV2 getProjIdCardInfoV2(final PidcVersionWithAttributesV2 pidcWithAttrs,
      final Session session, final long minLevel, final long maxLevel)
      throws IcdmException {

    // the response object
    ProjectIdCardInfoTypeV2 wsProjIdCard = new ProjectIdCardInfoTypeWsBoV2(session);

    // Set PIDC name
    wsProjIdCard.setName(pidcWithAttrs.getPidcVersionInfo().getPidc().getName());

    // set the PIDC ID from the Active Pidc version
    wsProjIdCard.setId(pidcWithAttrs.getPidcVersionInfo().getPidc().getId());

    // set the PIDC version number
    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // get deleted flag
    wsProjIdCard.setIsDeleted(pidcWithAttrs.getPidcVersionInfo().getPidc().isDeleted());

    // get create user and date
    wsProjIdCard.setCreateUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedUser());
    wsProjIdCard.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedDate()));

    // get modify user and date
    wsProjIdCard.setModifyUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedUser());
    wsProjIdCard.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedDate()));

    // clearing status
    CLEARING_STATUS clStatus =
        CLEARING_STATUS.getClearingStatus(pidcWithAttrs.getPidcVersionInfo().getPidc().getClearingStatus());
    wsProjIdCard.setClearingStatus(clStatus.getUiText());
    wsProjIdCard.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // level attribute info list
    wsProjIdCard.setLevelAttrInfoList(getLevelAttrInfoV2(pidcWithAttrs.getPidcVersionInfo(), minLevel, maxLevel));

    return wsProjIdCard;
  }

  private LevelAttrInfoV2[] getLevelAttrInfoV2(final PidcVersionInfo pidcVer, final long minLevel,
      final long maxLevel) {
    synchronized (this) {
      LevelAttrInfoV2[] levelAttrInfo = new LevelAttrInfoV2[pidcVer.getLevelAttrMap().size()];
      int idx;

      for (long level = minLevel; level <= maxLevel; level++) {
        idx = (int) (level - minLevel);

        PidcVersionAttribute structAttr = pidcVer.getLevelAttrMap().get(level);

        levelAttrInfo[idx] = new LevelAttrInfoV2();
        levelAttrInfo[idx].setLevelNo((int) level);
        levelAttrInfo[idx].setLevelAttrId(structAttr.getAttrId());
        levelAttrInfo[idx].setLevelAttrValueId(structAttr.getValueId());
        levelAttrInfo[idx].setLevelName(structAttr.getValue());
      }
      return levelAttrInfo;
    }
  }

  /**
   * This method sets project id card variant type information
   *
   * @param projIdCardType
   * @param pidcVer
   * @param allUseCaseAttrs
   * @param useCaseInput
   * @param user
   * @throws IcdmException
   */
  private ProjectIdCardVariantType[] getPidcVariantInfo(final Session session,
      final PidcVersionWithAttributes pidcWithAttrs)
      throws IcdmException {

    // the response object
    ProjectIdCardVariantType[] wsPidcVariants;

    // Get project id card variants
    Collection<ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> dbPidcVariants =
        pidcWithAttrs.getVariantMap().values();

    // create the response object
    wsPidcVariants = new ProjectIdCardVariantType[dbPidcVariants.size()];

    int iCounter = 0;
    for (ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant : dbPidcVariants) {

      ProjectIdCardVariantType wsPidcVariant = new ProjectIdCardVariantType();

      // Set project id card variant information
      ProjectIdCardVariantInfoType wsVariantInfo = getWsVariantInfo(session, dbPidcVariant.getProjectObject());
      wsVariantInfo.setVersionNumber(pidcWithAttrs.getPidcVersionInfo().getPidcVersion().getProRevId());
      wsPidcVariant.setPIdCVariant(wsVariantInfo);

      // Get variant attributes
      Map<Long, PidcVariantAttribute> dbVariantAttrs = dbPidcVariant.getProjectAttrMap();

      if ((dbVariantAttrs != null) && (dbVariantAttrs.size() > 0)) {
        fillVaraintInfoForProject(session, dbPidcVariant, wsPidcVariant, dbVariantAttrs, pidcWithAttrs);
      }

      wsPidcVariants[iCounter] = wsPidcVariant;

      iCounter++;
    }

    return wsPidcVariants;
  }

  /**
   * @param session
   * @param allUseCaseAttrs
   * @param useCaseInput
   * @param dbPidcVariant
   * @param wsPidcVariant
   * @param dbVariantAttrs
   * @throws IcdmException
   */
  private void fillVaraintInfoForProject(final Session session,
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> dbPidcVariant,
      final ProjectIdCardVariantType wsPidcVariant, final Map<Long, PidcVariantAttribute> dbVariantAttrs,
      final PidcVersionWithAttributes pidcWithAttrs)
      throws IcdmException {
    AttributeWithValueType[] wsVariantAttrs = new AttributeWithValueType[dbVariantAttrs.size()];

    int index = 0;

    for (PidcVariantAttribute varAttr : dbVariantAttrs.values()) {
      AttributeWithValueType wsVariantAttr = new AttributeWithValueType();

      // set used information
      if (null == varAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsVariantAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(varAttr.getUsedFlag());
        wsVariantAttr.setUsed(usedEnum.getUiType());
      }

      // set isVariant information
      wsVariantAttr.setIsVariant(varAttr.isAtChildLevel());

      // get attribute info
      // get attribute info
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          pidcWithAttrs.getAttributeMap().get(varAttr.getAttrId());
      wsVariantAttr.setAttribute(getWsAttribute(session, attribute));

      // get optional attribute information
      wsVariantAttr.setSpecLink(varAttr.getSpecLink());
      wsVariantAttr.setPartNumber(varAttr.getPartNumber());
      wsVariantAttr.setDescription(varAttr.getAdditionalInfoDesc());

      // get PIDC attribute version
      wsVariantAttr.setChangeNumber(varAttr.getVersion());

      // Task 262097
      if ((varAttr.getValueId() != null) && !varAttr.isAtChildLevel()) {
        com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
            pidcWithAttrs.getAttributeValueMap().get(varAttr.getValueId());
        wsVariantAttr.setValue(getWsAttrValue(session, attributeValue));
      }

      wsVariantAttrs[index] = wsVariantAttr;

      index++;
    }

    wsPidcVariant.setAttributes(wsVariantAttrs);

    wsPidcVariant.setSubVariants(getSubVariantInfo(session, dbPidcVariant, pidcWithAttrs));
  }

  /**
   * This method returns AttributeValue object
   *
   * @param user
   * @param tabvAttrVal
   * @param attrVal
   * @return AttributeValue
   * @throws IcdmException
   */
  private AttributeValue getWsAttrValue(final Session session, final PidcVersionWithAttributes pidcWithAttrs,
      final PidcVersionAttribute dbPidcAttr)
      throws IcdmException {


    // create the response object
    AttributeValue attrVal = new AttributeValueWsBo(session);

    com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue =
        pidcWithAttrs.getAttributeValueMap().get(dbPidcAttr.getValueId());

    // Set Attribute Id
    attrVal.setAttrID(attributeValue.getAttributeId());

    // set the English attribute value depending on the value type
    attrVal.setValueE(attributeValue.getNameRaw());

    // set the German attribute value depending on the value type
    attrVal.setValueG(attributeValue.getNameRaw());
    if (pidcWithAttrs.getAttributeMap().get(dbPidcAttr.getAttrId())
        .getValueTypeId() == ApicConstants.ATTR_VALUE_TYPE_TEXT) {
      // is TEXT value
      attrVal.setValueG(null == attributeValue.getTextValueGer() ? attributeValue.getTextValueEng()
          : attributeValue.getTextValueGer());
    }

    // Set attribute value id
    attrVal.setValueID(attributeValue.getId());

    // Set attribute value deleted information
    attrVal.setIsDeleted(attributeValue.isDeleted());

    // Attribute value created date
    attrVal
        .setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attributeValue.getCreatedDate()));

    // Attribute value created user
    attrVal.setCreateUser(attributeValue.getCreatedUser());

    // Attribute value modified date
    attrVal
        .setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attributeValue.getModifiedDate()));

    // Attribute value modified user
    attrVal.setModifyUser(attributeValue.getModifiedUser());

    // Attribute value version
    attrVal.setChangeNumber(attributeValue.getVersion());

    // Clearing Status
    String clearingStatus = attributeValue.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    attrVal.setClearingStatus(clStatus.getUiText());

    // Attribute isCleraed()
    attrVal.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    // iCDM-1273
    attrVal.setValueDescE(attributeValue.getDescriptionEng());
    attrVal.setValueDescG(CommonUtils.checkNull(attributeValue.getDescriptionGer()));

    if (AttributeValueType.ICDM_USER.getDisplayText().equals(attributeValue.getValueType())) {
      attrVal.setValueE(attributeValue.getOtherValue() == null ? "" : attributeValue.getOtherValue());
      attrVal.setValueDescE(attributeValue.getTextValueEng());
    }

    return attrVal;
  }

  /**
   * This method returns AttributeValue object
   *
   * @param user
   * @param tabvAttrVal
   * @param attrVal
   * @return AttributeValue
   * @throws IcdmException
   */
  private AttributeValue getWsAttrValue(final Session session,
      final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attrVal)
      throws IcdmException {


    // create the response object
    AttributeValue respAttrVal = new AttributeValueWsBo(session);

    // Set Attribute Id
    respAttrVal.setAttrID(attrVal.getAttributeId());

    // set the English attribute value depending on the value type
    respAttrVal.setValueE(attrVal.getNameRaw());

    // set the German attribute value depending on the value type
    respAttrVal.setValueG(attrVal.getNameRaw());
    if (AttributeValueType.getType(attrVal.getValueType()).getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_TEXT) {
      // is TEXT value
      respAttrVal.setValueG(null == attrVal.getTextValueGer() ? attrVal.getTextValueEng() : attrVal.getTextValueGer());
    }

    // Set attribute value id
    respAttrVal.setValueID(attrVal.getId());

    // Set attribute value deleted information
    respAttrVal.setIsDeleted(attrVal.isDeleted());

    // Attribute value created date
    respAttrVal.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attrVal.getCreatedDate()));

    // Attribute value created user
    respAttrVal.setCreateUser(attrVal.getCreatedUser());

    // Attribute value modified date
    respAttrVal.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, attrVal.getModifiedDate()));

    // Attribute value modified user
    respAttrVal.setModifyUser(attrVal.getModifiedUser());

    // Attribute value version
    respAttrVal.setChangeNumber(attrVal.getVersion());

    // Clearing Status
    String clearingStatus = attrVal.getClearingStatus();
    CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
    respAttrVal.setClearingStatus(clStatus.getUiText());

    // Attribute isCleraed()
    respAttrVal.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    // iCDM-1273
    respAttrVal.setValueDescE(attrVal.getDescriptionEng());
    respAttrVal.setValueDescG(CommonUtils.checkNull(attrVal.getDescriptionGer()));

    if (AttributeValueType.ICDM_USER.getDisplayText().equals(attrVal.getValueType())) {
      respAttrVal.setValueE(attrVal.getOtherValue() == null ? "" : attrVal.getOtherValue());
      respAttrVal.setValueDescE(attrVal.getTextValueEng());
    }

    return respAttrVal;
  }

  /**
   * Get the PIDC Info from database information
   *
   * @param pidcVer
   * @param attrVal
   * @return AttributeValue
   * @throws IcdmException
   */
  private ProjectIdCardInfoType getProjIdCardInfo(final PidcVersionWithAttributes pidcWithAttrs, final Session session,
      final long minLevel, final long maxLevel)
      throws IcdmException {

    // the response object
    ProjectIdCardInfoType wsProjIdCard = new ProjectIdCardInfoTypeWsBo(session);

    // Set PIDC name
    wsProjIdCard.setName(pidcWithAttrs.getPidcVersionInfo().getPidc().getName());

    // set the PIDC ID from the Active Pidc version
    wsProjIdCard.setId(pidcWithAttrs.getPidcVersionInfo().getPidc().getId());

    // set the PIDC version number
    wsProjIdCard.setChangeNumber(pidcWithAttrs.getPidcVersionInfo().getPidc().getVersion());

    // get deleted flag
    wsProjIdCard.setIsDeleted(pidcWithAttrs.getPidcVersionInfo().getPidc().isDeleted());

    // get create user and date
    wsProjIdCard.setCreateUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedUser());
    wsProjIdCard.setCreateDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidc().getCreatedDate()));

    // get modify user and date
    wsProjIdCard.setModifyUser(pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedUser());
    wsProjIdCard.setModifyDate(DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15,
        pidcWithAttrs.getPidcVersionInfo().getPidc().getModifiedDate()));

    // clearing status
    CLEARING_STATUS clStatus =
        CLEARING_STATUS.getClearingStatus(pidcWithAttrs.getPidcVersionInfo().getPidc().getClearingStatus());
    wsProjIdCard.setClearingStatus(clStatus.getUiText());
    wsProjIdCard.setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

    wsProjIdCard.setLevelAttrInfoList(getLevelAttrInfo(pidcWithAttrs.getPidcVersionInfo(), minLevel, maxLevel));

    return wsProjIdCard;
  }

  /**
   * This method sets attribute with value type information for a PIDC
   *
   * @param projIdCardType
   * @param pidcVer
   * @param allUseCaseAttrs
   * @param useCaseInput
   * @param user
   * @throws IcdmException
   */
  private AttributeWithValueType[] getPidcAttributesWithValues(final Session session,
      final PidcVersionWithAttributes pidcWithAttrs, final ServiceData servData)
      throws IcdmException {

    // get the attributes of the PIDC
    Collection<PidcVersionAttribute> dbPidcAttrs = pidcWithAttrs.getPidcAttributeMap().values();
    int size = dbPidcAttrs.size();

    // the response object
    AttributeWithValueType[] wsPidcAttrs = new AttributeWithValueType[size + 1];

    int iCounter = 0;
    for (PidcVersionAttribute dbPidcAttr : dbPidcAttrs) {

      AttributeWithValueType wsPidcAttr = new AttributeWithValueType();

      // set used information
      if (null == dbPidcAttr.getUsedFlag()) {
        // in case of hidden attributes
        wsPidcAttr.setUsed("");
      }
      else {
        PROJ_ATTR_USED_FLAG usedEnum;
        usedEnum = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbPidcAttr.getUsedFlag());
        wsPidcAttr.setUsed(usedEnum.getUiType());
      }
      // set isVariant information
      wsPidcAttr.setIsVariant(dbPidcAttr.isAtChildLevel());
      // set attribute information
      wsPidcAttr.setAttribute(getWsAttribute(session, pidcWithAttrs.getAttributeMap().get(dbPidcAttr.getAttrId())));

      // Task 262097
      // set attribute value information
      if ((dbPidcAttr.getValueId() != null) && !dbPidcAttr.isAtChildLevel()) {
        // Set attribute value information
        wsPidcAttr.setValue(getWsAttrValue(session, pidcWithAttrs, dbPidcAttr));
      }

      // get optional attribute information
      wsPidcAttr.setSpecLink(dbPidcAttr.getSpecLink());
      wsPidcAttr.setPartNumber(dbPidcAttr.getPartNumber());
      wsPidcAttr.setDescription(dbPidcAttr.getAdditionalInfoDesc());

      // set PIDC attribute version
      if (null != dbPidcAttr.getVersion()) {
        wsPidcAttr.setChangeNumber(dbPidcAttr.getVersion());
      }

      wsPidcAttrs[iCounter] = wsPidcAttr;

      iCounter++;
    }


    // Task 242055
    // set the dummy attribute and value for last confirmation date
    AttributeWithValueType wsPidcAttr = new AttributeWithValueType();
    wsPidcAttr.setAttribute(getWsAttrForConfrmDate(session, servData));
    wsPidcAttr.setUsed("Y");
    wsPidcAttr.setIsVariant(false);
    wsPidcAttr.setValue(getWSValueForConfrmDate(session, pidcWithAttrs, servData));
    wsPidcAttr.setChangeNumber(1);
    wsPidcAttrs[iCounter] = wsPidcAttr;

    return wsPidcAttrs;
  }

  /**
   * Task 242055
   *
   * @param session
   * @return
   */
  private Attribute getWsAttrForConfrmDate(final Session session, final ServiceData servData) {
    Attribute wsAttribute = new AttributeWsBo(session);
    MessageLoader messageLoader = new MessageLoader(servData);
    // Set attribute id
    wsAttribute.setId(Long.valueOf("-1"));
    // Set attribute English name
    String attrNameEng = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_TEXT_ENG");
    wsAttribute.setNameE(attrNameEng);
    // Set attribute German name
    String attrNameGer = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_TEXT_GER");
    wsAttribute.setNameG(attrNameGer);

    // Set attribute English description
    String attrDescEng = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_DESC_ENG");
    wsAttribute.setDescrE(CommonUtils.checkNull(attrDescEng));
    // Set attribute German description
    String attrDescGer = messageLoader.getMessage(PIDC_EDITOR_STR, "CONFIRMATION_ATTR_DESC_GER");
    wsAttribute.setDescrG(CommonUtils.checkNull(attrDescGer));

    // Set attribute deleted information
    wsAttribute.setIsDeleted(false);
    // Set attribute normalized information
    wsAttribute.setIsNormalized(false);
    // Set attribute mandatory information
    wsAttribute.setIsMandatory(false);
    // Set attribute unit information
    wsAttribute.setUnit("");
    // Set attribute format information
    wsAttribute.setFormat(DateFormat.DATE_FORMAT_09);
    // Set attribute created date information
    Calendar createdDate = getCreatedDateForDummyAttr();
    wsAttribute.setCreateDate(createdDate);
    // Set attribute created user information
    wsAttribute.setCreateUser("DGS_ICDM");

    // Set attribute value type id
    wsAttribute.setTypeID(AttributeValueType.DATE.getValueTypeID());
    // Set attribute group id
    String attrGroupId = new CommonParamLoader(servData).getValue(CommonParamKey.PIDC_UP_TO_DATE_ATTR_GROUP_ID);
    wsAttribute.setGroupID(Long.parseLong(attrGroupId));

    // Set attribute version
    wsAttribute.setChangeNumber(1);

    return wsAttribute;
  }

  /**
   * @return 01-NOV-2017 as Calendar
   */
  private Calendar getCreatedDateForDummyAttr() {
    Calendar createdDate = Calendar.getInstance();
    createdDate.set(2017, 11, 1);
    return createdDate;
  }

  public GetParameterStatisticsResponseType getParameterStatistics(final GetParameterStatistics getParameterStatistics)
      throws GetParameterStatisticsFaultException {

    /**
     * Session Variable for current call
     */
    getSession(getParameterStatistics.getSessID(), "GetParameterStatistics", getParameterStatistics.getParameterName());

    String parameterName = getParameterStatistics.getParameterName();


    byte[] resultBytes = null;

    try {
      // create the CalDataAnalyzer instance
      CalDataAnalyzer analyzer = new CalDataAnalyzer();

      // set the logger
      analyzer.setLogger(this.cdaLogger);

      // set the database login parameter
      analyzer.setDbUsername(DB_USERNAME);
      analyzer.setDbpassword(DB_PASSWORD);
      analyzer.setDataBase(DB_CONNECTION);


      // create a list of label to be analyzed
      List<String> labelList = new ArrayList<>();

      // fill the labelList
      labelList.add(parameterName);

      // set the label list
      analyzer.setLabelList(labelList);

      // fetch the different values for the list of label
      analyzer.getCalDataAnalyzerUtil().setDatabaseMode(true);
      analyzer.fetchLabelValues(null);

      // fetch the data sets using the label
      Map<String, LabelInfoVO> lblInfoVOMap = analyzer.fetchCalData(null);

      if ((lblInfoVOMap == null) || (lblInfoVOMap.get(parameterName).getPeakValue() == null)) {
        throw new GetParameterStatisticsFaultException("no statistics found for the label " + parameterName);
      }

      // iterate over the results list and display the analysis details
      for (String label : labelList) {
        LabelInfoVO labelInfoVO = lblInfoVOMap.get(label);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(labelInfoVO);
        oos.close();
        resultBytes = baos.toByteArray();


      }

    }
    catch (GetParameterStatisticsFaultException e) {
      this.logger.warn(e.toString());
      throw e;
    }
    catch (Exception e) {
      this.logger.error("Error  - " + e.toString());
      throw new GetParameterStatisticsFaultException(e);
    }
    GetParameterStatisticsResponseType response = new GetParameterStatisticsResponseType();
    response.setParameterStatistics(new HexBinary(resultBytes));

    return response;
  }

  /**
   * @param getParameterStatisticsFile input
   * @return statistics
   * @throws GetParameterStatisticsFaultException error while retrieving data
   */
  public GetParameterStatisticsFileResponse getParameterStatisticsFile(
      final GetParameterStatisticsFile getParameterStatisticsFile)
      throws GetParameterStatisticsFaultException {

    /**
     * Session Variable for current call
     */
    Session session = getSession(getParameterStatisticsFile.getGetParameterStatisticsFile().getSessID(),
        "GetParameterStatistics", getParameterStatisticsFile.getGetParameterStatisticsFile().getParameterName());

    GetParameterStatistics getParameterStatistics = new GetParameterStatistics();
    getParameterStatistics
        .setParameterName(getParameterStatisticsFile.getGetParameterStatisticsFile().getParameterName());
    getParameterStatistics.setSessID(session.getSessionId());

    GetParameterStatisticsResponseType paramStatRes = getParameterStatistics(getParameterStatistics);
    HexBinary result = paramStatRes.getParameterStatistics();
    byte[] resultBytes = result.getBytes();
    HashMap<String, CalData> calData = new HashMap<>();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    GetParameterStatisticsFileResponse resp = new GetParameterStatisticsFileResponse();
    ParameterStatisticsFileResponseType respType = new ParameterStatisticsFileResponseType();
    resp.setGetParameterStatisticsFileResponse(respType);

    try {
      ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(resultBytes));
      LabelInfoVO labelInfoVo = (LabelInfoVO) stream.readObject();

      CalData labelResultCalData = new CalData();
      labelResultCalData.setCalDataPhy(labelInfoVo.getPeakValue());
      labelResultCalData.setShortName(labelInfoVo.getLabelName());

      getCalDataHistory(ApicWebServiceDBImpl.getSession(getParameterStatistics.getSessID()), labelInfoVo, DB_USERNAME,
          labelResultCalData, "");

      calData.put(labelResultCalData.getShortName(), labelResultCalData);
      CDFWriter cdfWriter;

      cdfWriter = new CDFWriter(calData, this.cdfLogger);
      cdfWriter.writeCalDataToXML(outputStream);
    }
    catch (CDFWriterException | IOException | ClassNotFoundException e) {
      throw new GetParameterStatisticsFaultException("Error when fetching Parameter Statistics in a file", e);
    }

    respType.setFile(new String(outputStream.toByteArray()));
    return resp;
  }

  /**
   * Get multiple parameter statistics in one web service call. In theorie, an unlimited amaount of labels can be
   * returned. In fact, the memory of the server and the session timeout limits the number of records that can be
   * gathered. To limit the memory consumption, request to gather series statistics are split into packages of 10
   * labels. Just the returning CalData objects are stored completely until all labels are fetched.
   *
   * @param getParameterStatisticsExt contains labels that should be returned
   * @return GetParameterStatisticsExtResponseType the CalData objects retruned by the webservice
   * @throws GetParameterStatisticsExtFaultException if an error occurs when gathering series statistics
   */
  public GetParameterStatisticsExtResponseType getParameterStatisticsExt(
      final GetParameterStatisticsExt getParameterStatisticsExt)
      throws GetParameterStatisticsExtFaultException {

    /**
     * Defines the number of labels that should be fetched in one run of the CalDataAnalyzer. This number should not be
     * too high to limit memory consumption of the server.
     */
    int splitAfterLabels = 10;

    /**
     * The number of steps in which fetching CalData is divided
     */
    int splitSize = 0;

    /**
     * The number of calls of the CalDataAnalyzer that are necessary to fetch all labels (number of labels / split after
     * labels)
     */
    int splitCount = 0;

    /**
     * Used for calculating the progress: Number of values found for the labels
     */
    int countValuesForLabels = 0;

    /**
     * Used for calculating the progress: Number of files found for the labels
     */
    int countFileIDs = 0;

    /**
     * The session ID of the client
     */
    String sessionId = getParameterStatisticsExt.getSessID();

    /**
     * byte representation of a CalData object before it is converted to a HexBinary object
     */
    byte[] resultBytes = null;

    /**
     * HexBinary representation of a CalData object before it gets added to the labelResults ArrayList
     */
    HexBinary labelResult = new HexBinary();

    /**
     * ArrayList for storing all CalData objects as HexBinary collection. This list is converted to an array before it
     * will be returned to the client
     */
    ArrayList<HexBinary> labelResults = new ArrayList<>();

    /**
     * The CalDataAnalyzer returns a set of LabelInfoVO objects, from which the CalData objects are extracted. This map
     * stores the LabelInfoVO object temporarily.
     */
    Map<String, LabelInfoVO> lblInfoVOMap = new HashMap<>();

    /**
     * The array of parameter names requested by the client
     */
    String[] parameterNames = getParameterStatisticsExt.getParameterNames();

    /**
     * The CalDataAnalyzer expects a list of all labels. This list contains all labels, based on the array of parameters
     * given to the webservice.
     */
    List<String> labelList = new ArrayList<>();

    /**
     * Each list contains at maximun the number of labels that should be fetched for one run of the Analyzer.
     */
    List<List<String>> lblSplitLists = null;


    /**
     * The CalDataAnalyzer instance for the current request
     */
    CalDataAnalyzer analyzer = new CalDataAnalyzer();

    /**
     * Translates the Analyzer filters passed from the webservice to a format the Analyzer can understand
     */
    FilterAdapter filterAdapter;

    /**
     * Session Variable for current call
     */
    Session session = getSession(sessionId, "ParamStatAsync", String.valueOf(parameterNames.length) + " parameters.");


    try {
      analyzer.setLogger(this.cdaLogger);
      analyzer.setDbUsername(DB_USERNAME);
      analyzer.setDbpassword(DB_PASSWORD);
      analyzer.setDataBase(DB_CONNECTION);
      filterAdapter = new FilterAdapter(getParameterStatisticsExt.getParameterFilters(), analyzer.getDataHolder());

      /*
       * The CalDataAnalyzer expects a list of all labels. This list is generated of the array given to the webservice.
       */
      for (String parameterName : parameterNames) {
        labelList.add(parameterName);
      }
      analyzer.setLabelList(labelList);

      // Set the filter list with an adapter
      analyzer.setFilterList(filterAdapter.getFilterList());

      // To limit memory consumption of the server, analyzing is limitet to 10 parameters at once
      analyzer.setLabelSizeForAnalysis(splitAfterLabels);

      // Returns a list of lists having the defined labels size for each run of the analyzer
      lblSplitLists = analyzer.getLabelLists();
      splitSize = lblSplitLists.size();

      // Loop to fetch CalData of CalDataAnalyzer in steps of defined no
      for (List<String> splitList : lblSplitLists) {

        /*
         * Loop can take very long when many parameters are requested. That's why after every iteration a check ist
         * performed if the user cancelled the operation.
         */
        if (session.isCancelRequested()) {
          session.setSessionCancelled();
          throw new GetParameterStatisticsExtFaultException("Session cancelled due to user request");
        }

        splitCount++;
        analyzer.initilizeAnalyzer();

        this.logger.info("Start executing LabelList " + splitCount + " of " + splitSize + " size: " + splitList.size());

        // set the filter list (this has to be done in each loop separately!)
        analyzer.setFilterList(filterAdapter.getFilterList());

        // Pass the current list of x labels to the analyzer. Just for these labels the analysis is started
        analyzer.labelSplitList(splitList);

        // First Step within CalDataAnalyzer: Fetch label values
        analyzer.getCalDataAnalyzerUtil().setDatabaseMode(true);
        analyzer.fetchLabelValues(new WsStandardFetchChangedListener(session, (100D / splitSize) * splitCount,
            splitList.size(), 10D / Double.valueOf(splitSize), 1D /*
                                                                   * / Double.valueOf( splitSize)
                                                                   */));

        // Second Step within CalDataAnalyzer: Fetch CalData
        try {
          countValuesForLabels = analyzer.getNumberOfRecords(null, FetchCalDataSqlDAO.COUNT_VALUES_FOR_LABELS);
          countFileIDs = analyzer.getNumberOfRecords(null, FetchCalDataSqlDAO.COUNT_FILE_IDS);
        }
        catch (Exception exp) {
          this.logger.error(exp.getMessage(), exp);
          countValuesForLabels = 0;
          countFileIDs = 0;
        }

        Rule rule1 = new Rule(0, countValuesForLabels, 45D / splitSize);
        Rule rule2 = new Rule(countValuesForLabels + 1, countFileIDs + countValuesForLabels + 1, 45D / splitSize);
        WsRuleFetchChangedListener ruleListener =
            new WsRuleFetchChangedListener(session, (100D / splitSize) * splitCount, rule1, rule2);

        lblInfoVOMap = analyzer.fetchCalData(ruleListener);

        // lblInfoVOMap might be null, when during this split no label was found. Goto next split list in this case.
        if (lblInfoVOMap == null) {
          continue;
        }

        /*
         * Goes through the returned Map of LabelInfoVO objects and extracts the CalData object. The Main memory saving
         * effect is due to not storing the complete statistics for all labels but just the CalData object of the PEak
         * values.
         */
        for (Entry<String, LabelInfoVO> entry : lblInfoVOMap.entrySet()) {

          LabelInfoVO labelInfoVO = entry.getValue();

          /*
           * No action within one pass necessary when - labelInfoVO object is empty (no label info found) - the found
           * label is not part of the current split list. This can happen when none of the labels of the split list have
           * been found. In this case the label with the filter condition is returned.
           */
          if ((labelInfoVO == null) || (labelInfoVO.getPeakValue() == null)) {
            // no statistics for label
            this.logger.warn("No info found for label " + entry.getKey());
            continue;
          }

          if (!splitList.contains(entry.getKey())) {
            // contains a label, that is not in the split list
            this.logger.warn("Label found, that is not in the split list: " + entry.getKey());
            continue;
          }

          /*
           * Gets the CalData object and stores it in an List of HexBinary objects. The CalData Object is returned as
           * HEX-Binary object
           */
          CalData labelResultCalData = new CalData();

          labelResultCalData.setCalDataPhy(labelInfoVO.getPeakValue());
          labelResultCalData.setShortName(labelInfoVO.getLabelName());

          getCalDataHistory(session, labelInfoVO, DB_USERNAME, labelResultCalData, filterAdapter.getFilters());

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(labelResultCalData);
          oos.close();
          resultBytes = baos.toByteArray();

          labelResult = new HexBinary(resultBytes);
          labelResults.add(labelResult);

        }
      }

      if (labelResults.isEmpty()) {
        throw new GetParameterStatisticsExtFaultException("no statistics found for the passed labels");
      }
    }
    catch (GetParameterStatisticsExtFaultException e) {
      this.logger.error(e.toString());
      throw e;
    }
    catch (Exception e) {
      this.logger.error("Error  - " + e.toString());
      throw new GetParameterStatisticsExtFaultException("Error when getting Series Statistics: " + e.toString(), e);
    }
    /**
     * The webservice reponse object, that will be returned to the user after fetching all data
     */
    GetParameterStatisticsExtResponseType response = new GetParameterStatisticsExtResponseType();
    response.setParameterStatistics(labelResults.toArray(new HexBinary[0]));
    session.setSessionFinished();
    return response;
  }

  private Object getClusterLabelValueFilter(final List<DefaultSeriesStatLabelAdapter> filterListParam,
      final int index) {
    if (filterListParam.size() > (index + 1)) {
      return new OperatorAndDataModel(filterListParam.get(index),
          getClusterLabelValueFilter(filterListParam, index + 1));
    }

    return filterListParam.get(index);
  }

  /**
   * @param seriesStatisticsType
   * @param labelInfo
   * @param lblValInfoVO
   * @param currentUserName
   * @param calData
   */
  private void getCalDataHistory(final Session session, final LabelInfoVO labelInfo, final String currentUserName,
      final CalData calData, final String filterText) {
    DataElement dataElement;
    final CalDataHistory calDataHistory = new CalDataHistory();

    final List<HistoryEntry> historyEntryList = new ArrayList<>();
    calDataHistory.setHistoryEntryList(historyEntryList);

    final HistoryEntry historyEntry = new HistoryEntry();
    historyEntryList.add(historyEntry);


    dataElement = new DataElement();
    dataElement.setValue("prelimCalibrated");
    historyEntry.setState(dataElement);

    dataElement = new DataElement();
    String formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_16,
        DateUtil.getCalendarFromDate(TimeZoneFactory.adjustTimeZone(session.getTimezone(), new Date()), false));
    dataElement.setValue(formattedDate);


    historyEntry.setDate(dataElement);

    dataElement = new DataElement();
    // Set current user name
    dataElement.setValue(currentUserName);
    historyEntry.setPerformedBy(dataElement);

    dataElement = new DataElement();

    StringBuilder remark = new StringBuilder();

    remark.append("Most Frequent Value (MFV)" + " from Series Statistics" + "\n");
    remark.append(labelInfo.getSumDataSets() + " datasets using this label" + "\n");
    remark.append("Label has " + labelInfo.getNumberOfValues() + " different values" + "\n");

    remark.append(labelInfo.getValuesMap().get(labelInfo.getPeakValueId()).getFileIDList().size() +
        " datasets using MFV (" + labelInfo.getPeakValuePercentage() + " %)" + "\n");

    if (filterText != null) {
      remark.append("SeriesStatistics Filters used for this label: " + filterText);
    }

    dataElement.setValue(remark.toString());
    historyEntry.setRemark(dataElement);

    dataElement = new DataElement();
    dataElement.setValue("iCDM pre-calibration from SeriesStatistics");
    historyEntry.setContext(dataElement);

    dataElement = new DataElement();
    dataElement.setValue("MFVpct: " + labelInfo.getPeakValuePercentage());
    historyEntry.setTestObject(dataElement);

    dataElement = new DataElement();
    dataElement.setValue("SumDST: " + labelInfo.getSumDataSets());
    historyEntry.setDataIdentifier(dataElement);

    dataElement = new DataElement();
    dataElement.setValue("NumVAL: " + labelInfo.getNumberOfValues());
    historyEntry.setTargetVariant(dataElement);

    List<DataElement> specialData = new ArrayList<>();

    dataElement = new DataElement();
    dataElement.setGID("SumDST");
    dataElement.setValue(Long.toString(labelInfo.getSumDataSets()));
    specialData.add(dataElement);

    dataElement = new DataElement();
    dataElement.setGID("NumVAL");
    dataElement.setValue(Long.toString(labelInfo.getNumberOfValues()));
    specialData.add(dataElement);

    dataElement = new DataElement();
    dataElement.setGID("MFVpct");
    dataElement.setValue(Double.toString(labelInfo.getPeakValuePercentage()));
    specialData.add(dataElement);

    historyEntry.setSpecialData(specialData);


    calData.setCalDataHistory(calDataHistory);
  }


  /**
   * @param getUseCases
   * @return
   * @throws ParseException
   */
  public UseCaseType[] getUseCases(final GetUseCases getUseCases) throws ParseException {
    /**
     * Session Variable for current call
     */
    Session session =
        getSession(getUseCases.getSessID(), "GetUseCases", CommonUtils.isNotNull(getUseCases.getUseCaseIDs())
            ? String.valueOf(getUseCases.getUseCaseIDs().length) : "All" + " use cases.");

    List<UseCaseType> allWsUseCases = new ArrayList<>();
    try (ServiceData serviceData = createServiceData(session)) {
      Set<Long> inputUcIds = new HashSet<>();
      if (null != getUseCases.getUseCaseIDs()) {
        inputUcIds.addAll(Arrays.stream(getUseCases.getUseCaseIDs()).boxed().collect(Collectors.toList()));
      }
      UseCaseLoader ucLoader = new UseCaseLoader(serviceData);
      Set<com.bosch.caltool.icdm.model.uc.UsecaseType> ucTypes = ucLoader.getUsecaseTypes(inputUcIds);
      for (com.bosch.caltool.icdm.model.uc.UsecaseType ucType : ucTypes) {
        allWsUseCases.add(createSoapUcTypeObj(ucType, session));
      }
    }
    catch (IcdmException exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    return allWsUseCases.toArray(new UseCaseType[allWsUseCases.size()]);
  }

  /**
   * @param ucType
   * @param session
   * @return
   */
  private UseCaseType createSoapUcTypeObj(final com.bosch.caltool.icdm.model.uc.UsecaseType ucType,
      final Session session) {
    UseCaseType wsUseCase = new UseCaseTypeWsBo(session);
    wsUseCase.setUseCaseID(ucType.getUcId().longValue());
    wsUseCase.setUseCaseGroup(ucType.getUcGroupName());
    if ((wsUseCase.getUseCaseGroup() == null) || "".equals(wsUseCase.getUseCaseGroup())) {
      wsUseCase.setUseCaseGroup("???");
    }
    wsUseCase.setNameE(ucType.getNameEng());
    if (CommonUtils.isNotEmptyString(ucType.getNameGer())) {
      wsUseCase.setNameG(ucType.getNameGer());
    }
    else {
      wsUseCase.setNameG(ucType.getNameEng());
    }
    wsUseCase.setDescrE(ucType.getDescEng());
    if (CommonUtils.isNotEmptyString(ucType.getDescGer())) {
      wsUseCase.setDescrG(ucType.getDescGer());
    }
    else {
      wsUseCase.setDescrG(ucType.getDescEng());
    }
    wsUseCase.setIsDeleted(ucType.isDeleted());

    wsUseCase.setChangeNumber(ucType.getChangeNumber().longValue());

    wsUseCase.setCreateUser(ucType.getCreatedUser());
    wsUseCase.setCreateDate(ucType.getCreatedDate());

    wsUseCase.setModifyUser(ucType.getModifiedUser());
    wsUseCase.setModifyDate(ucType.getModifiedDate());
    UseCaseItemType[] wsUcItemType = new UseCaseItemType[ucType.getUcItemTypeSet().size()];
    int index = 0;
    for (com.bosch.caltool.icdm.model.uc.UseCaseItemType ucItemType : ucType.getUcItemTypeSet()) {
      wsUcItemType[index] = createWsUcItemType(ucItemType, session);
      index++;
    }
    wsUseCase.setUseCaseItems(wsUcItemType);
    return wsUseCase;
  }

  /**
   * @param ucItemType
   * @param session
   * @return
   */
  private UseCaseItemType createWsUcItemType(final com.bosch.caltool.icdm.model.uc.UseCaseItemType ucItemType,
      final Session session) {
    UseCaseItemType wsUseCaseItem = new UseCaseItemTypeWsBo(session);
    wsUseCaseItem.setItemID(ucItemType.getUcId().longValue());
    wsUseCaseItem.setNameE(ucItemType.getNameEng());
    if (CommonUtils.isNotEmptyString(ucItemType.getNameGer())) {
      wsUseCaseItem.setNameG(ucItemType.getNameGer());
    }
    else {
      wsUseCaseItem.setNameG(ucItemType.getNameEng());
    }
    wsUseCaseItem.setDescrE(ucItemType.getDescEng());
    if (CommonUtils.isNotEmptyString(ucItemType.getDescGer())) {
      wsUseCaseItem.setDescrG(ucItemType.getDescGer());
    }
    else {
      wsUseCaseItem.setDescrG(ucItemType.getDescEng());
    }
    wsUseCaseItem.setCreateUser(ucItemType.getCreatedUser());
    wsUseCaseItem.setCreateDate(ucItemType.getCreatedDate());
    wsUseCaseItem.setModifyUser(ucItemType.getModifiedUser());
    wsUseCaseItem.setModifyDate(ucItemType.getModifiedDate());
    wsUseCaseItem.setChangeNumber(ucItemType.getChangeNumber());
    wsUseCaseItem.setMappedAttributeIDs(
        Arrays.stream(ucItemType.getMappedAttrIds().toArray(new Long[ucItemType.getMappedAttrIds().size()]))
            .mapToLong(Long::longValue).toArray());
    return wsUseCaseItem;
  }

  /**
   * Gets the use case with all the use case sections
   *
   * @param getUseCaseWithSec use case sections
   * @return use case section type
   */
  // Task 262098
  public UseCaseSectionType[] getUseCaseWithSectionTree(final GetUseCaseWithSectionTree getUseCaseWithSec) {
    /**
     * Session Variable for current call
     */
    Session session = getSession(getUseCaseWithSec.getSessID(), "getUseCaseWithSectionTree",
        CommonUtils.isNotNull(getUseCaseWithSec.getUseCaseIDs())
            ? String.valueOf(getUseCaseWithSec.getUseCaseIDs().length) : "All" + " use cases.");

    List<UseCaseSectionType> allWsUseCases = new ArrayList<>();
    try (ServiceData serviceData = createServiceData(session)) {
      Set<Long> inputUcIds = new HashSet<>();
      if (null != getUseCaseWithSec.getUseCaseIDs()) {
        inputUcIds.addAll(Arrays.stream(getUseCaseWithSec.getUseCaseIDs()).boxed().collect(Collectors.toList()));
      }
      UseCaseLoader ucLoader = new UseCaseLoader(serviceData);
      Set<UsecaseSectionType> ucSTypes = ucLoader.getUsecaseSectionWithTree(inputUcIds);
      for (com.bosch.caltool.icdm.model.uc.UsecaseSectionType ucSType : ucSTypes) {
        if ((null != ucSType.getUcSItemTypeSet()) && !ucSType.getUcSItemTypeSet().isEmpty()) {
          allWsUseCases.add(createSoapUcSTypeObj(ucSType, session));
        }
      }
    }
    catch (IcdmException exp) {
      getLogger().error(exp.getMessage(), exp);
    }
    return allWsUseCases.toArray(new UseCaseSectionType[allWsUseCases.size()]);

  }

  /**
   * @param ucSType
   * @param session
   * @return
   */
  private UseCaseSectionType createSoapUcSTypeObj(final UsecaseSectionType ucSType, final Session session) {
    UseCaseSectionType wsUseCase = new UseCaseSectionTypeWsBo(session);
    wsUseCase.setUseCaseID(ucSType.getUcId().longValue());
    wsUseCase.setUseCaseGroup(ucSType.getUcGroupName());
    if ((wsUseCase.getUseCaseGroup() == null) || "".equals(wsUseCase.getUseCaseGroup())) {
      wsUseCase.setUseCaseGroup("???");
    }
    wsUseCase.setNameE(ucSType.getNameEng());
    if (CommonUtils.isNotEmptyString(ucSType.getNameGer())) {
      wsUseCase.setNameG(ucSType.getNameGer());
    }
    else {
      wsUseCase.setNameG(ucSType.getNameEng());
    }
    wsUseCase.setDescrE(ucSType.getDescEng());
    if (CommonUtils.isNotEmptyString(ucSType.getDescGer())) {
      wsUseCase.setDescrG(ucSType.getDescGer());
    }
    else {
      wsUseCase.setDescrG(ucSType.getDescEng());
    }
    wsUseCase.setIsDeleted(ucSType.isDeleted());

    wsUseCase.setChangeNumber(ucSType.getChangeNumber().longValue());

    wsUseCase.setCreatedUser(ucSType.getCreatedUser());
    wsUseCase.setCreatedDate(ucSType.getCreatedDate());

    wsUseCase.setModifiedUser(ucSType.getModifiedUser());
    wsUseCase.setModifiedDate(ucSType.getModifiedDate());

    UseCaseSectionItemType[] wsUcItemType = new UseCaseSectionItemType[ucSType.getUcSItemTypeSet().size()];

    int index = 0;
    for (com.bosch.caltool.icdm.model.uc.UseCaseSectionItemType ucSItemType : ucSType.getUcSItemTypeSet()) {
      wsUcItemType[index] = createWsUcSItemType(ucSItemType, session);
      index++;
    }
    wsUseCase.setUseCaseSectionItems(wsUcItemType);
    return wsUseCase;
  }

  /**
   * @param ucSItemType
   * @param session
   * @return
   */
  private UseCaseSectionItemType createWsUcSItemType(
      final com.bosch.caltool.icdm.model.uc.UseCaseSectionItemType ucSItemType, final Session session) {
    UseCaseSectionItemType wsUseCaseItem = new UseCaseSectionItemType();
    wsUseCaseItem.setItemID(ucSItemType.getUcId().longValue());
    wsUseCaseItem.setNameE(ucSItemType.getNameEng());
    if (CommonUtils.isNotEmptyString(ucSItemType.getNameGer())) {
      wsUseCaseItem.setNameG(ucSItemType.getNameGer());
    }
    else {
      wsUseCaseItem.setNameG(ucSItemType.getNameEng());
    }
    wsUseCaseItem.setDescrE(ucSItemType.getDescEng());
    if (CommonUtils.isNotEmptyString(ucSItemType.getDescGer())) {
      wsUseCaseItem.setDescrG(ucSItemType.getDescGer());
    }
    else {
      wsUseCaseItem.setDescrG(ucSItemType.getDescEng());
    }
    wsUseCaseItem.setCreatedUser(ucSItemType.getCreatedUser());
    wsUseCaseItem.setCreatedDate(ucSItemType.getCreatedDate());
    wsUseCaseItem.setModifiedUser(ucSItemType.getModifiedUser());
    wsUseCaseItem.setModifiedDate(ucSItemType.getModifiedDate());
    wsUseCaseItem.setChangeNumber(ucSItemType.getChangeNumber());
    UseCaseSectionItemType[] wsUcItemType = new UseCaseSectionItemType[ucSItemType.getUcSectionItemType().size()];

    int index = 0;
    for (com.bosch.caltool.icdm.model.uc.UseCaseSectionItemType ucSectionItemType : ucSItemType
        .getUcSectionItemType()) {
      wsUcItemType[index] = createWsUcSItemType(ucSectionItemType, session);
      index++;
    }
    wsUseCaseItem.setUseCaseSectionItems(wsUcItemType);
    wsUseCaseItem.setMappedAttributeIDs(
        Arrays.stream(ucSItemType.getMappedAttrIds().toArray(new Long[ucSItemType.getMappedAttrIds().size()]))
            .mapToLong(Long::longValue).toArray());
    return wsUseCaseItem;
  }

  /**
   * Gathers and returns the differences of PIDCs and attributes within two version.
   *
   * @param getPidcDiffsParameter a GetPidcDiffsType with the PIDC and the lower and upper version that should be taken
   *          into account
   * @return a GetPidcDiffsResponseType object holding the response for the object
   */
  public GetPidcDiffsResponseType getPidcDiffs(final GetPidcDiffsType getPidcDiffsParameter)
      throws GetPidcDiffsFaultException {
    Session session =
        getSession(getPidcDiffsParameter.getSessID(), "PIDCDiffs", "PIDC: " + getPidcDiffsParameter.getPidcID());

    this.logger.info(
        "getPidcDiffs for PidcID " + getPidcDiffsParameter.getPidcID() + " using session " + session.getSessionId());

    /**
     * The PIDC version were to start reporting changes. Together with newPidcChangeNumber this attribute determines the
     * bandwith in which changes are reported. + 1 is added because the chance in the last (old) change nummer should
     * not be considered, just the change that happens after the passed old chance ID.
     */
    long oldPidcChangeNumber = getPidcDiffsParameter.getOldPidcChangeNumber() + 1;

    /**
     * The PIDC version were to end reporting changes. Together with oldPidcChangeNumber this attribute determines the
     * bandwith in which changes are reported.
     */
    long newPidcChangeNumber = getPidcDiffsParameter.getNewPidcChangeNumber();

    PidcVersion pidcVer;
    GetPidcDiffsResponseType response;
    try (ServiceData servData = createServiceData(session)) {
      PidcVersionLoader pidcVersLoader = new PidcVersionLoader(servData);
      pidcVer = pidcVersLoader.getActivePidcVersion(getPidcDiffsParameter.getPidcID());

      /**
       * The response object that stores the webservice response. Initialized with a dummy PIDC-Type. This dummy type
       * prevents the web service to fail in case of:<br>
       * <ul>
       * <li>there are no changes to report in the table t_pidc_change_history</li>
       * <li>there's no entry in table t_pidc_change_history for the PIDC, although there are changes for attributes for
       * this PIDC</li>
       * </ul>
       * Note: In this case no incrementation of the old ID is required, because this would lead to the case that old
       * change number is higher then new change number. Thus, the original values are transfered
       */
      response = ElementDifferencesPIDC.getDummyResponse(pidcVer, getPidcDiffsParameter.getOldPidcChangeNumber(),
          getPidcDiffsParameter.getNewPidcChangeNumber());

      /**
       * The change history of the PIDC based on the passed arguments getPidcDiffsParameter.getOldPidcChangeNumber():
       * the lower version number, from which should be started pidc.getPidcId(): the PIDC ID Note: All changes from
       * getOldPidcChangeNumber() until the current version are returned.
       */
      if ((newPidcChangeNumber == -1) || (oldPidcChangeNumber <= newPidcChangeNumber)) {

        PidcVersionAttributeLoader pidcVersAttrLoader = new PidcVersionAttributeLoader(servData);

        Map<Long, PidcVersionAttribute> attributes = pidcVersAttrLoader.getPidcVersionAttribute(pidcVer.getId());

        Set<Long> attrID = new HashSet<>();
        PidcChangeHistoryLoader historyLoader = new PidcChangeHistoryLoader(servData);
        for (PidcVersionAttribute pidcAttr : attributes.values()) {
          if (pidcAttr.isAttrHidden() && historyLoader.isHidden(pidcVer.getPidcId())) {
            attrID.add(pidcAttr.getAttrId());
          }
        }

        if (pidcVersLoader.isHiddenToCurrentUser(pidcVer.getId())) {
          throw new GetPidcDiffsFaultException(
              "Pidc of ID " + getPidcDiffsParameter.getPidcID() + " is not visible to the user.");
        }
        // Send the Pid Version Id to get the Pid Change History

        SortedSet<com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory> changeHistory2 =
            historyLoader.fetchPIDCChangeHistory(oldPidcChangeNumber, pidcVer.getId(),
                getPidcDiffsParameter.getPidcID(), false, attrID);

        // If size of map is = 0, there are no changes to report (the tdb-table hasn't any entries for the passed
        // versions)
        if (!CommonUtils.isNullOrEmpty(changeHistory2)) {
          // Get the pidc ID from the active Version
          ElementDifferences pidcElement = new ElementDifferencesPIDC(oldPidcChangeNumber, newPidcChangeNumber,
              changeHistory2, pidcVer.getPidcId(), false);
          pidcElement.analyzeDifferences();

          /*
           * In some cases, there's no entry for the PIDC itself, although there are reported changes in attributes of
           * the PIDC. Shouldn't be allowed, but this case exists. The initially created dummy is only overwritten, when
           * the retunrned PIDC array is filled with a PIDC
           */
          if (pidcElement.toArray().length > 0) {
            response = (GetPidcDiffsResponseType) pidcElement.toArray()[0];
          }

          // Get the changed attributes of PIDC level
          // Get the pidc ID from the active Version
          ElementDifferences attr = new ElementDifferencesAttributesPidc(oldPidcChangeNumber, newPidcChangeNumber,
              changeHistory2, pidcVer.getPidcId(), false);
          attr.analyzeDifferences();
          response.setChangedAttributes((ProjectIdCardChangedAttributeType[]) attr.toArray());

          // Get the changes for all variants, sub variants and their attributes
          // Get the pidc ID from the active Version
          ElementDifferences variants = new ElementDifferencesVariant(oldPidcChangeNumber, newPidcChangeNumber,
              changeHistory2, pidcVer.getPidcId(), false);
          variants.analyzeDifferences();
          response.setChangedVariants((ProjectIdCardChangedVariantsType[]) variants.toArray());
        }
      }
    }
    catch (IcdmException exp) {
      throw new GetPidcDiffsFaultException(exp.getMessage(), exp);
    }


    return response;
  }

  public com.bosch.caltool.apic.ws.LoadA2LFileDataResponse loadA2LFileData(
      final com.bosch.caltool.apic.ws.LoadA2LFileData loadA2LFileData) {

    this.logger.info("Start Loading A2L File");

    // The response parameters
    LoadA2LFileDataResponse response = new LoadA2LFileDataResponse();
    A2LFileDataResponseType a2lFileResponse = new A2LFileDataResponseType();

    // Get the parameters for the A2LLoader from the Messages File
    String serverTempDir = System.getenv("TEMP");

    // vCDM Login Information
    String vcdmUser = Messages.getString("EASEEService.USER_NAME");
    String passwordKey = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_PASS);
    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(this.logger);
    String vcdmPasswort = Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), this.logger);
    String vcdmDomain = Messages.getString("EASEEService.DOMAAIN_NAME");
    String vcdmServerString = Messages.getString("EASEEService.WS_SERVER");

    // Villa Login Information
    String villaUser = Messages.getString("Villa.DB_USER");
    String villaPasswort = Decryptor.getInstance().decrypt(Messages.getString("Villa.DB_USER_PASS"), this.logger);
    String villaURL = Messages.getString("Villa.DB_URL");

    long vcdmVersionNumber = loadA2LFileData.getLoadA2LFileData().getVcdmVersionNumber();
    int vcdmServer = 0;

    // Store the int value for the vCDM server
    if ("DGS_CDM_PRO".equals(vcdmServerString)) {
      vcdmServer = 1;
    }
    else if ("DGS_CDM_QUA".equals(vcdmServerString)) {
      vcdmServer = 2;
    }

    A2LLoader a2lLoader = new A2LLoader(villaUser, villaPasswort, villaURL, serverTempDir, this.a2lLogger, vcdmUser,
        vcdmPasswort, vcdmDomain, vcdmServer);
    a2lFileResponse.setA2LFileId(a2lLoader.loadVcdmFile(String.valueOf(vcdmVersionNumber)));
    response.setLoadA2LFileDataResponse(a2lFileResponse);

    return response;
  }

  /**
   * Gets the change history of a PIDC from the specified oldPidcChangeNumber(inclusive) to the specified
   * newPidcChangeNumber(inclusive), containing changes on all levels (PIDC, Variant, SubVariant)
   *
   * @param pidc a long containing the PIDC
   * @param keyLevel the attribut on which changes should be reported. That means, if you want to see only changes of
   *          attributes use the attribut level. If you only want to see changes for Variants (Variant itself and not
   *          the attributes on the variant), choose the variant level. Valid arguments are:<br>
   *          PIDC: ApicWebServiceDBImpl.LEVEL_PIDC<br>
   *          Variant: ApicWebServiceDBImpl.LEVEL_VARIANT<br>
   *          Sub-Variant: ApicWebServiceDBImpl.LEVEL_SUB_VARIANT<br>
   *          Attribut: ApicWebServiceDBImpl.LEVEL_ATTRIBUTE
   * @param oldPidcChangeNumber the lowest version for that changes should be returned
   * @param newPidcChangeNumber the highest version for that changes should be returned
   * @return a Set of all changes done in the PIDC between version oldPidcChangeNumber and newPidcChangeNumber
   */

  public CancelSessionResponse cancelSession(final CancelSession cancelSession) {

    String sessionId = cancelSession.getCancelSession().getSessionId();
    CancelSessionResponse sesRes = new CancelSessionResponse();
    CancelSessionResponseType sesResType = new CancelSessionResponseType();
    Session session = statusAsyncExecution.get(sessionId);

    this.logger.info("Recieved request to abort session: " + sessionId);

    sesRes.setCancelSessionResponse(sesResType);
    sesResType.setIsCancelled(false);

    // It makes only sense to abort running, not already marked as cancelled sessions
    if ((session != null) && session.isActive() && !session.isCancelRequested()) {
      statusAsyncExecution.get(sessionId).setCancelRequested(true);
      sesResType.setIsCancelled(true);
      this.logger.info("Session: " + sessionId + " marked as cancelled.");
    }
    else {
      this.logger.info("Session: " + sessionId +
          " couldn't be marked as cancelled, because the session is already finished or cancelled.");
    }

    return sesRes;
  }

  public LoginResponse login(final Login login) throws LoginFaultException {

    LoginOperation loginOp = new LoginOperation(login);
    ApicWebServiceDBImpl.addSession(loginOp.getSession());

    return loginOp.getLoginResponse();
  }

  public LogoutResponse logout(final Logout logout) {

    LogoutResponse logoutResp = new LogoutResponse();
    ApicWebServiceDBImpl.removeSession(logout.getSessID());
    logoutResp.setResult(true);

    return logoutResp;
  }

  private Session getSession(final String sessionId, final String operation, final String operationParameters) {

    Session session = ApicWebServiceDBImpl.getSession(getSessionId(sessionId));
    session.setOperation(operation);
    session.setOperationParameters(operationParameters);

    ServerEventLogger.INSTANCE.logSession(session.toString());
    return session;
  }

  private String getSessionId(final String sessionId) {
    if (ApicWebServiceDBImpl.getSession(sessionId) != null) {
      return sessionId;
    }

    try {
      return login(new Login()).getSessID();
    }
    catch (LoginFaultException e) {
      this.logger.error("Error when trying to create a dummy session", e);
      return null;
    }
  }

  /**
   * PIDC Search results with PIDC IDs. Only searched in the active projects versions.
   *
   * @param pidcSearchConditions search conditions
   * @return PIDC IDs
   * @throws GetPidcScoutResultFaultException exception
   */
  public PidcScoutResponse getPidcScoutResult(final PidcSearchConditions pidcSearchConditions)
      throws GetPidcScoutResultFaultException {

    PidcSearchRequest reqLog = new PidcSearchRequest(pidcSearchConditions.getPidcSearchConditions());
    Session session = getSession(pidcSearchConditions.getSessId(), "PIDC Scout", reqLog.toString());

    PidcScoutResponse response = new PidcScoutResponse();
    PidcScoutResponseType responseType = new PidcScoutResponseType();
    response.setPidcScoutResponse(responseType);

    PidcSearchInput searchIp = createPidcSearchConditions(pidcSearchConditions.getPidcSearchConditions());
    // Task 251282 Exclude focus matrix status in pidc search with a flag
    searchIp.setSearchFocusMatrix(false);

    try (ServiceData serviceData = createServiceData(session)) {

      PidcSearchResponse srResp = new PidcScout(serviceData, searchIp).findProjects();

      int index = 0;
      long[] pidcIds = new long[srResp.getResults().size()];

      for (PidcSearchResult searchResult : srResp.getResults()) {
        pidcIds[index] = searchResult.getPidc().getId();
        index++;

        PidcScoutInfo pidcScoutInfo = new PidcScoutInfo();
        pidcScoutInfo.setPidcId(searchResult.getPidc().getId());
        pidcScoutInfo.setHasA2LFiles(searchResult.isA2lFilesMapped());
        pidcScoutInfo.setHasReviews(searchResult.isReviewResultsFound());

        responseType.addPidcScoutInfo(pidcScoutInfo);
      }
      responseType.setPidcIds(pidcIds);
    }
    catch (IcdmException exp) {
      throw new GetPidcScoutResultFaultException(exp);
    }

    return response;
  }


  /**
   * @param pidcStatistic
   * @return
   * @throws IcdmException
   */
  public PidcStatisticResponse getPidcStatisticResult(final PidcStatisticType pidcStatistic) throws IcdmException {
    throw new IcdmException("This service is no longer available!");
  }


  public PidcAccessRightResponse getPidcAccessRight(final PidcAccessRight pidcAccessRight) {

    Session session =
        getSession(pidcAccessRight.getPidcAccessRight().getSessId(), "GetPidcAccessRight", "Access rights for PIDC");
    try (ServiceData servData = createServiceData(session)) {
      // Get the all project id cards

      AbstractAccessRight accessRight = new IcdmPidcAccessRight(this.logger, pidcAccessRight, servData);

      accessRight.createWsResponse();


      return (PidcAccessRightResponse) accessRight.getWsResponse();
    }

  }

  /**
   * @param allPidcDiff input
   * @return response
   * @deprecated this method is deprecated
   */
  @Deprecated
  public AllPidcDiffResponse getAllPidcDiff(final AllPidcDiffType allPidcDiff) throws GetAllPidcDiffFaultException {
    throw new GetAllPidcDiffFaultException(getObsoleteServiceErrMsg());
  }

  private String getObsoleteServiceErrMsg() {
    String message = "This service is no longer available";
    try (ServiceData serviceData = new ServiceData()) {
      message = new MessageLoader(serviceData).getMessage("GENERAL", "OBSOLETE_SERVICE");
    }
    return message;
  }

  /**
   * @param attrDiff
   * @return
   * @deprecated this method is deprecated
   */
  @Deprecated
  public AttrDiffResponse getPidcAttrDiffReport(final AllPidcDiffType allPidcDiff) throws GetAllPidcDiffFaultException {
    throw new GetAllPidcDiffFaultException(getObsoleteServiceErrMsg());
  }

  /**
   * @param getAllProjectIdVersions
   * @return
   * @throws GetProjectIdCardFaultException
   */
  public GetAllProjectIdCardVersResponseType getAllProjectIdCardVersions(
      final GetAllProjectIdCardVersions getAllProjectIdVersions)
      throws GetProjectIdCardFaultException {
    /**
     * Session Variable for current call
     */
    Session session = getSession(getAllProjectIdVersions.getGetAllProjectIdCardVersions().getSessID(),
        "GetAllProjIdCardVersions", "All PIDCs.");


    List<PidcVersion> dbProjectIdVersions = new ArrayList<>();
    Map<Pidc, List<PidcVersion>> pidcVersionMap = new ConcurrentHashMap<>();
    ProjectIdCardAllVersInfoType[] wsProjIdCardVers;
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel;
    try (ServiceData servData = createServiceData(session)) {
      maxLevel = (new AttributeLoader(servData)).getMaxStructAttrLevel();
      PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(servData);
      PidcVersionAttributeLoader pidcVersionAttributeLoader = new PidcVersionAttributeLoader(servData);
      pidcVersionLoader.getAll().values().stream().forEach(dbProjectIdVersions::add);
      fillPidcVersMap(dbProjectIdVersions, pidcVersionMap, servData);
      // the project id card list for the response object
      wsProjIdCardVers = new ProjectIdCardAllVersInfoType[pidcVersionMap.size()];
      // Transfer the PIDCs into the response object
      int index = 0;
      for (Entry<Pidc, List<PidcVersion>> dbProjectIdCardVer : pidcVersionMap.entrySet()) {
        Pidc pidCard = dbProjectIdCardVer.getKey();
        PidcVersion activeVersion = pidcVersionLoader.getActivePidcVersion(pidCard.getId());
        Map<Long, PidcVersionAttribute> structureAttributes =
            pidcVersionAttributeLoader.getStructureAttributes(activeVersion.getId());
        List<PidcVersion> pidcVersionsList = dbProjectIdCardVer.getValue();
        if (!pidcVersionLoader.isHiddenToCurrentUser(activeVersion.getId())) {
          wsProjIdCardVers[index] = new ProjectIdCardVersInfoTypeWsBo(session);

          String pidcName = pidCard.getName();

          // set the PIDC ID from active pidc Version Pidc
          wsProjIdCardVers[index].setId(pidCard.getId());

          // use the english name of the PIDC as the name
          wsProjIdCardVers[index].setName(pidcName);

          // get the PIDC version number
          wsProjIdCardVers[index].setVersionNumber(activeVersion.getProRevId());


          // get the PIDC version
          wsProjIdCardVers[index].setChangeNumber(pidCard.getVersion());

          // get deleted flag of the PIDC
          wsProjIdCardVers[index].setIsDeleted(activeVersion.isDeleted());

          // get create and modify user of the PIDC
          wsProjIdCardVers[index].setCreateUser(activeVersion.getCreatedUser());
          wsProjIdCardVers[index].setModifyUser(activeVersion.getModifiedUser());

          // get create and modify date of the PIDC
          wsProjIdCardVers[index].setCreateDate(
              DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, activeVersion.getCreatedDate()));
          wsProjIdCardVers[index].setModifyDate(
              DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, activeVersion.getModifiedDate()));

          // Add information regarding the clearing status
          String clearingStatus = pidCard.getClearingStatus();
          CLEARING_STATUS clStatus = CLEARING_STATUS.getClearingStatus(clearingStatus);
          wsProjIdCardVers[index].setClearingStatus(clStatus.getUiText());

          // Attribute isCleraed()
          wsProjIdCardVers[index].setIsCleared(clStatus == CLEARING_STATUS.CLEARED);

          // Send the pidc Version Id rather than the Pidc ID
          wsProjIdCardVers[index].setLevelAttrInfoList(getLevelAttrInfo(structureAttributes, minLevel, maxLevel));

          PidcVersionType[] pidcVersionType = new PidcVersionType[pidcVersionsList.size()];
          for (int j = 0; j < pidcVersionsList.size(); j++) {
            pidcVersionType[j] = new PidcVersionType();
            pidcVersionType[j].setDescription(pidcVersionsList.get(j).getDescription());
            pidcVersionType[j].setDescriptionE(pidcVersionsList.get(j).getVersDescEng());
            pidcVersionType[j].setDescriptionG(pidcVersionsList.get(j).getVersDescGer());
            pidcVersionType[j].setIsActive(pidCard.getProRevId() == pidcVersionsList.get(j).getProRevId());
            pidcVersionType[j].setChangeNumber(pidcVersionsList.get(j).getVersion());
            pidcVersionType[j].setLongName(pidcVersionsList.get(j).getVersionName());
            pidcVersionType[j].setPidcVersionId(pidcVersionsList.get(j).getId());
            pidcVersionType[j].setProRevId(pidcVersionsList.get(j).getProRevId());
            if (pidcVersionsList.get(j).getPidStatus() == null) {
              pidcVersionType[j].setVersionStatus("");
            }
            else {
              pidcVersionType[j].setVersionStatus(com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatus
                  .getStatus(pidcVersionsList.get(j).getPidStatus()).getUiStatus());
            }
          }
          wsProjIdCardVers[index].setPidcVersions(pidcVersionType);
          index++;
        }
      }
    }
    catch (Exception exp) {
      throw new GetProjectIdCardFaultException(exp.getMessage(), exp);
    }

    pidcVersionMap.size();
    // the response object
    GetAllProjectIdCardVersResponseType response = new GetAllProjectIdCardVersResponseType();
    response.setProjectIdCards(wsProjIdCardVers);

    return response;
  }

  /**
   * @param dbProjectIdVersions
   * @param pidcVersionMap
   * @throws DataException
   */
  private void fillPidcVersMap(final List<PidcVersion> dbProjectIdVersions,
      final Map<Pidc, List<PidcVersion>> pidcVersionMap, final ServiceData servData)
      throws DataException {
    PidcLoader loader = new PidcLoader(servData);
    Map<Long, List<PidcVersion>> pidcIdMap = new HashMap<>();
    for (PidcVersion pidcVersion : dbProjectIdVersions) {
      List<PidcVersion> pidcVersionList = new ArrayList<>();

      if (pidcIdMap.get(pidcVersion.getPidcId()) == null) {
        pidcIdMap.put(pidcVersion.getPidcId(), pidcVersionList);
        pidcVersionMap.put(loader.getDataObjectByID(pidcVersion.getPidcId()), pidcVersionList);
      }
      else {
        pidcVersionList = pidcIdMap.get(pidcVersion.getPidcId());
      }
      pidcVersionList.add(pidcVersion);
    }
  }

  /**
   * @param getProjectIdCardForVersion getProjectIdCardForVersion
   * @return
   */
  public ProjectIdCardVersResponseType getProjIdCardForVersion(
      final GetProjectIdCardForVersion getProjectIdCardForVersion)
      throws GetProjectIdCardFaultException {

    this.logger.debug("getProjectIdCard for PidcVersion Id " +
        getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId() + " using session " +
        getProjectIdCardForVersion.getGetProjectIdCardForVersion().getSessId());

    /**
     * Session Variable for current call
     */
    Session session =
        getSession(getProjectIdCardForVersion.getGetProjectIdCardForVersion().getSessId(), "GetProjIdCardForVersion",
            String.valueOf(getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId()));

    PidcVersionWithAttributes pidcWithAttrs;
    Set<Long> useCaseIdSet;
    long minLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL;
    long maxLevel;

    ProjectIdCardWithVersion wsProjectIdCard = new ProjectIdCardWithVersion();
    try (ServiceData servData = createServiceData(session)) {

      long projectIdCardId = getProjectIdCardForVersion.getGetProjectIdCardForVersion().getProjectIdCardId();
      PidcVersionLoader versionLoader = new PidcVersionLoader(servData);
      PidcVersion pidcVers = versionLoader
          .getDataObjectByID(getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId());

      if (CommonUtils.isNotNull(pidcVers)) {

        if (versionLoader.isHiddenToCurrentUser(pidcVers.getId())) {
          throw new GetProjectIdCardFaultException("Pidc Version of the given id " +
              getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId() +
              " is not visible to the user");
        }

        maxLevel = (new AttributeLoader(servData)).getMaxStructAttrLevel();
        // Create loader object
        PidcLoaderExternal loader = new PidcLoaderExternal(servData);

        long[] useCaseIds = getProjectIdCardForVersion.getGetProjectIdCardForVersion().getUseCaseID();

        // convert usecase id array to set
        useCaseIdSet = new HashSet<>();
        if (null != useCaseIds) {
          for (Long useCaseId : useCaseIds) {
            useCaseIdSet.add(useCaseId);
          }
        }
        // get PidcVersionWithAttributes instance

        pidcWithAttrs = loader.getProjectIdCardWithAttrs(projectIdCardId == 0 ? null : projectIdCardId, useCaseIdSet,
            getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId());
        ProjectIdCardInfoType projIdCardInfo = getProjIdCardInfo(pidcWithAttrs, session, minLevel, maxLevel);

        PidcVersionType projIdCardVersType = getProjIdCardVersType(pidcWithAttrs.getPidcVersionInfo());

        ProjectIdCardVersInfoType pidcVersionInfoType = new ProjectIdCardVersInfoType();
        pidcVersionInfoType.setPidcVersion(projIdCardVersType);
        pidcVersionInfoType.setChangeNumber(projIdCardInfo.getChangeNumber());
        pidcVersionInfoType.setClearingStatus(projIdCardInfo.getClearingStatus());
        pidcVersionInfoType.setCreateDate(projIdCardInfo.getCreateDate());
        pidcVersionInfoType.setCreateUser(projIdCardInfo.getCreateUser());
        pidcVersionInfoType.setId(projIdCardInfo.getId());
        pidcVersionInfoType.setIsCleared(projIdCardInfo.getIsCleared());
        pidcVersionInfoType.setIsDeleted(projIdCardInfo.getIsDeleted());
        pidcVersionInfoType
            .setLevelAttrInfoList(getLevelAttrInfo(pidcWithAttrs.getPidcVersionInfo(), minLevel, maxLevel));
        pidcVersionInfoType.setModifyDate(projIdCardInfo.getModifyDate());
        pidcVersionInfoType.setModifyUser(projIdCardInfo.getModifyUser());
        pidcVersionInfoType.setName(projIdCardInfo.getName());
        pidcVersionInfoType.setPidcVersion(projIdCardVersType);
        pidcVersionInfoType.setVersionNumber(projIdCardInfo.getVersionNumber());
        // pidcVersionInfoType.set
        // Set project Id card information
        wsProjectIdCard.setProjectIdCardDetails(pidcVersionInfoType);

        // Set attribute with value type information
        wsProjectIdCard.setAttributes(getPidcAttributesWithValues(session, pidcWithAttrs, servData));

        // Set project id card variant type information
        wsProjectIdCard.setVariants(getPidcVariantInfo(session, pidcWithAttrs));
      }
      else {
        this.logger.warn("Project-ID-Card-Version not found, ID: " +
            getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId());

        // Get current method name
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        // Get APIC web service request fault
        APICWsRequestFault faultMsg =
            getRequestFault("SessionID", getProjectIdCardForVersion.getGetProjectIdCardForVersion().getSessId(),
                methodName, -999999, "Project-ID-Card-Version not found, ID: " +
                    getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId());

        GetProjectIdCardFaultException exception = new GetProjectIdCardFaultException();
        // Set APIC web service request fault information
        exception.setFaultMessage(faultMsg);

        throw exception;
      }
    }
    catch (Exception exp) {
      throw new GetProjectIdCardFaultException("The Pidc Version of the given id" +
          getProjectIdCardForVersion.getGetProjectIdCardForVersion().getPidcVersionId() + " is not found.", exp);
    }
    ProjectIdCardVersResponseType response = new ProjectIdCardVersResponseType();
    response.setProjectIdCard(wsProjectIdCard);
    return response;
  }

  /**
   * @param dbProjectIdCardVers dbProjectIdCard
   * @returnversionType
   */
  private PidcVersionType getProjIdCardVersType(final PidcVersionInfo dbProjectIdCardInfo) {
    PidcVersion dbProjectIdCardVers = dbProjectIdCardInfo.getPidcVersion();
    PidcVersionType versionType = new PidcVersionType();
    versionType.setDescription(dbProjectIdCardVers.getDescription());
    versionType.setDescriptionE(dbProjectIdCardVers.getVersDescEng());
    versionType.setDescriptionG(CommonUtils.checkNull(dbProjectIdCardVers.getVersDescGer()));
    versionType.setIsActive(dbProjectIdCardInfo.getPidc().getProRevId() == dbProjectIdCardVers.getProRevId());
    versionType.setLongName(dbProjectIdCardVers.getVersionName());
    versionType.setPidcVersionId(dbProjectIdCardVers.getId());
    versionType.setProRevId(dbProjectIdCardVers.getProRevId());
    versionType.setVersionStatus(dbProjectIdCardVers.getPidStatus() != null
        ? com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatus.getStatus(dbProjectIdCardVers.getPidStatus())
            .getUiStatus()
        : com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionStatus.IN_WORK.getUiStatus());
    versionType.setChangeNumber(dbProjectIdCardVers.getVersion());
    return versionType;
  }

  /**
   * @param getPidcActiveVersionId getPidcActiveVersionId
   * @return the PidcActiveVersionResponseType with the Active Pidc Version Id
   * @throws GetPidcActiveVersionException the exception to be thrown
   */
  public PidcActiveVersionResponseType getPidcActiveVersionId(final PidcActiveVersion getPidcActiveVersionId)
      throws GetPidcActiveVersionException {
    // ICDM-2400
    long pidcId = getPidcActiveVersionId.getPidcActiveVersion().getPidcId();
    String sessId = getPidcActiveVersionId.getPidcActiveVersion().getSessId();
    Session session = getSession(sessId, "GetPIDCActiveVersionID", String.valueOf(pidcId));
    PidcVersion pidcVers;
    try (ServiceData servData = createServiceData(session)) {
      PidcVersionLoader loader = new PidcVersionLoader(servData);

      pidcVers = loader.getActivePidcVersion(pidcId);
      if (loader.isHiddenToCurrentUser(pidcVers.getId())) {
        throw new GetPidcActiveVersionException(generateErrorMessage(pidcId, true));
      }
    }
    catch (IcdmException exp) {
      throw new GetPidcActiveVersionException(exp.getMessage(), exp);
    }
    PidcActiveVersionResponseType response = new PidcActiveVersionResponseType();
    response.setPidcVersionId(pidcVers.getId());
    return response;
  }

  /**
   * PIDC Search results with PIDC Version ID and PIDC ID. Project versions to be searched dependends on the flag
   * 'activerversiononly' in the search condition.
   *
   * @param pidcVersSearchCondition search conditions
   * @return PIDC ID, Version ID, flags for review result and A2L file availiblity
   */
  public PidcScoutVersResponse getPidcScoutResultForVersion(final PidcSearchConditions pidcVersSearchCondition) {
    PidcSearchRequest reqLog = new PidcSearchRequest(pidcVersSearchCondition.getPidcSearchConditions());
    Session session = getSession(pidcVersSearchCondition.getSessId(), "PIDC Scout", reqLog.toString());

    PidcScoutVersResponse response = new PidcScoutVersResponse();
    PidcScoutVersResponseType responseType = new PidcScoutVersResponseType();
    response.setPidcScoutVersResponse(responseType);

    PidcSearchInput searchIp = createPidcSearchConditions(pidcVersSearchCondition.getPidcSearchConditions());
    // Task 251282 Exclude focus matrix status in pidc search with a flag
    searchIp.setSearchFocusMatrix(false);

    try (ServiceData serviceData = createServiceData(session)) {
      PidcSearchResponse srResp = new PidcScout(serviceData, searchIp).findProjects();

      int index = 0;
      long[] pidcIds = new long[srResp.getResults().size()];

      for (PidcSearchResult searchResult : srResp.getResults()) {
        pidcIds[index] = searchResult.getPidc().getId();
        index++;

        PidcScoutVersInfo pidcScoutVerInfo = new PidcScoutVersInfo();

        pidcScoutVerInfo.setPidcVersionId(searchResult.getPidcVersion().getId());
        pidcScoutVerInfo.setPidcId(searchResult.getPidc().getId());
        // iCDM-1526- New method to check if the Pidc has A2l file.
        pidcScoutVerInfo.setHasA2LFiles(searchResult.isA2lFilesMapped());
        pidcScoutVerInfo.setHasReviews(searchResult.isReviewResultsFound());

        responseType.addPidcScoutInfo(pidcScoutVerInfo);

      }
      responseType.setPidcIds(pidcIds);
    }
    catch (IcdmException e) {
      this.logger.error("Error when creating the PIDC Scout Result", e);
    }

    return response;
  }

  /**
   * Convert SOAP search conditions to business object input
   *
   * @param conditionTypes SOAP input
   * @return PIDC Search BO input.
   */
  private PidcSearchInput createPidcSearchConditions(final PidcSearchConditionType... conditionTypes) {
    PidcSearchConditionType[] conditionTypesIp = conditionTypes;
    if (conditionTypesIp == null) {
      conditionTypesIp = new PidcSearchConditionType[0];
    }

    PidcSearchInput searchIp = new PidcSearchInput();

    for (PidcSearchConditionType type : conditionTypesIp) {
      PidcSearchCondition condition = new PidcSearchCondition();
      condition.setAttributeId(type.getAttributeId());

      Set<Long> attrValList = new HashSet<>();
      if (type.getAttributeValueIds() != null) {
        for (Long valID : type.getAttributeValueIds()) {
          attrValList.add(valID);
        }
      }
      condition.setAttributeValueIds(attrValList);
      condition.setUsedFlag(type.getUsedFlag());

      searchIp.getSearchConditions().add(condition);
    }

    return searchIp;
  }

  /**
   * @param allPidcDiffVers allPidcDiffVers
   * @return
   */
  public AllPidcDiffVersResponse getAllPidcDiffForVersion(final AllPidcDiffVersType allPidcDiffVers)
      throws GetAllPidcDiffForVersionFaultException {
    Session session = getSession(allPidcDiffVers.getSessID(), "getAllPidcDiffForVersion", "Get All Pidc Diff Version");
    AllPidcDiffVersResponse response = new AllPidcDiffVersResponse();
    try (ServiceData servData = createServiceData(session)) {
      response = getAllPidcDiffForVersion(allPidcDiffVers, servData, session);
    }
    catch (IcdmException exp) {
      throw new GetAllPidcDiffForVersionFaultException(exp.getLocalizedMessage(), exp);
    }
    return response;
  }

  /**
   * @param allPidcDiffVers allPidcDiffVers
   * @param servData
   * @param session
   * @return
   * @throws IcdmException
   */
  public AllPidcDiffVersResponse getAllPidcDiffForVersion(final AllPidcDiffVersType allPidcDiffVers,
      final ServiceData servData, final Session session)
      throws GetAllPidcDiffForVersionFaultException, IcdmException {
    /**
     * This object contains no information about changes. Just the PIDC and the structure of attributes and variants is
     * returned. Additionally, the PIDCHistory but only for the PIDC itself and not for the attributes is returned
     * through .getPidcHistory().
     */
    AllPidcDiffVersType request = allPidcDiffVers;
    PidcVersion pidcVer = null;
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(servData);

    try {
      if (request.getPidcVersionID() <= 0) {
        pidcVer = pidcVersLoader.getActivePidcVersion(request.getPidcID());
      }
      else {
        pidcVer = pidcVersLoader.getDataObjectByID(request.getPidcVersionID());
      }
    }
    catch (IcdmException exp) {
      throw new GetAllPidcDiffForVersionFaultException("Pidc of the id " + request.getPidcID() + " is not existing",
          exp);
    }


    if (pidcVersLoader.isHiddenToCurrentUser(pidcVer.getId())) {
      throw new GetAllPidcDiffForVersionFaultException(
          "Pidc of ID " + request.getPidcID() + " is not visible to the user.");
    }

    PidcVersionAttributeLoader pidcVersAttrLoader = new PidcVersionAttributeLoader(servData);

    Map<Long, PidcVersionAttribute> attributes = pidcVersAttrLoader.getPidcVersionAttribute(pidcVer.getId());

    Set<Long> attrID = new HashSet<>();
    PidcChangeHistoryLoader historyLoader = new PidcChangeHistoryLoader(servData);

    for (PidcVersionAttribute pidcAttr : attributes.values()) {
      if (pidcAttr.isAttrHidden() && historyLoader.isHidden(pidcVer.getPidcId())) {
        attrID.add(pidcAttr.getAttrId());
      }
    }


    SortedSet<com.bosch.caltool.icdm.model.apic.pidc.PidcChangeHistory> changeHistory =
        historyLoader.fetchPIDCChangeHistory(request.getOldPidcChangeNumber() + 1, request.getPidcVersionID(),
            request.getPidcID(), true, attrID);

    PidcDifferenceForVersion diffs =
        new PidcDifferenceForVersion(request, pidcVer, changeHistory, session.getTimezone(), this.logger, servData);

    // iCDM-2614
    // added focus matrix & its versions here
    diffs.createWsResponse();

    AllPidcDiffVersResponse response = new AllPidcDiffVersResponse();
    response.setAllPidcDiffVersResponse(diffs.getWsResponse());

    return response;
  }

  /**
   * @param getPidcDiffForVersion getPidcDiffForVersion
   * @return GetPidcDiffsResponseType
   * @throws GetPidcDiffForVersionFaultException error
   * @deprecated this method is deprecated
   */
  @Deprecated
  public GetPidcDiffsResponseType getPidcDiffForVersion(final GetPidcDiffsVersType getPidcDiffForVersion)
      throws GetPidcDiffForVersionFaultException {

    throw new GetPidcDiffForVersionFaultException(getObsoleteServiceErrMsg());
  }

  /**
   * @param attrDiffVers attrDiffVers
   * @return AttrDiffVersResponse
   * @throws GetAllPidcDiffForVersionFaultException error while getting data
   */
  // invokes when clicking on pidc history icon in iCDM Client
  public AttrDiffVersResponse getPidcAttrDiffReportForVersion(final AllPidcDiffVersType attrDiffVers)
      throws GetAllPidcDiffForVersionFaultException {

    Session session = getSession(attrDiffVers.getSessID(), "getPidcAttrDiffReportForVersion",
        "Get Pidc Attr Diff Report For Version");
    SortedSet<IcdmPidcLogAdapter> adapterSet = new TreeSet<>();
    try (ServiceData servData = createServiceData(session)) {

      AllPidcDiffVersResponse pidcResp = getAllPidcDiffForVersion(attrDiffVers, servData, session);

      AbstractPidcLogOutput pidcLog = new IcdmPidcLogOutput(this.logger, servData,
          pidcResp.getAllPidcDiffVersResponse(), attrDiffVers.getLanguage());
      pidcLog.createWsResponse();
      adapterSet = (SortedSet<IcdmPidcLogAdapter>) pidcLog.getWsResponse();
    }
    catch (IcdmException exp) {
      throw new GetAllPidcDiffForVersionFaultException(exp.getLocalizedMessage(), exp);
    }

    AttrDiffs allDiffs = new AttrDiffs();

    for (IcdmPidcLogAdapter entry : adapterSet) {
      allDiffs.addDifferences(entry);
      this.logger.debug("Added Attribute Difference to result set: " + entry.toString());
    }

    AttrDiffVersResponse response = new AttrDiffVersResponse();
    response.setAttrDiffVersResponse(allDiffs);

    return response;
  }


  /**
   * @param getVcdmLabelStatReq
   * @return
   * @throws GetVcdmLabelStatisticsFaultException GetVcdmLabelStatisticsFaultException
   */
  // Icdm-1485 - New Method for Getting the statistics
  public GetVcdmLabelStatisticsResponse getVcdmLabelStatistics(final LabelStatReq getVcdmLabelStatReq)
      throws GetVcdmLabelStatisticsFaultException {

    Set<VcdmDataSet> vcdmStatsSet;
    try (ServiceData serviceData = createServiceData(null)) {
      VcdmDataSetLoader vcdmDtaLdr = new VcdmDataSetLoader(serviceData);
      vcdmStatsSet = vcdmDtaLdr.getStatisticsByPidcId(getVcdmLabelStatReq.getPidcID(), getVcdmLabelStatReq.getTime());
    }
    catch (Exception exp) {
      throw new GetVcdmLabelStatisticsFaultException(exp.getMessage(), exp);
    }
    GetVcdmLabelStatisticsResponse response = new GetVcdmLabelStatisticsResponse();
    if (CommonUtils.isNotEmpty(vcdmStatsSet)) {
      VcdmLabelStats[] labelSetList = new VcdmLabelStats[vcdmStatsSet.size()];
      int i = 0;
      for (VcdmDataSet dataSet : vcdmStatsSet) {
        labelSetList[i] = new VcdmLabelStats();
        labelSetList[i].setAprjId(dataSet.getAprjId());
        labelSetList[i].setAprjName(dataSet.getAprjName());
        labelSetList[i].setCalibratedLabels(dataSet.getCalibratedLabels());
        labelSetList[i].setChangedLabels(dataSet.getChangedLabels());
        labelSetList[i].setCheckedLabels(dataSet.getCheckedLabels());
        labelSetList[i].setCompletedLabels(dataSet.getCompletedLabels());
        labelSetList[i].setNoStateLabels(dataSet.getNoStateLabels());
        labelSetList[i].setPreLimCalibratedLabels(dataSet.getPreLimLabels());
        labelSetList[i].setStatus(dataSet.getEaseedstState());
        labelSetList[i].setTotalNumberOfLabels(dataSet.getTotalLabels());
        labelSetList[i].setPartitionLabels(dataSet.getParitionLabels());
        labelSetList[i].setVcdmSoftware(dataSet.getProgramKey());
        labelSetList[i].setVcdmVariant(dataSet.getProductKey());
        labelSetList[i].setFreezeDateOfDST(dataSet.getEaseedstModDate());
        // 221731 - easeedst id
        labelSetList[i].setEaseeDstId(dataSet.getId());
        // 221731 - revision no
        labelSetList[i].setRevisionNo(dataSet.getRevisionNo());
        i++;
      }
      response.setLabelStats(labelSetList);
    }
    return response;
  }

  /**
   * @param vcdmLabelStatWpType vcdmLabelStatWpType
   * @return GetVcdmLabelStatisticsForWPResponse1 the response for the vcdm label statistics for Work package
   * @throws GetVcdmLabelStatisticsFaultException GetVcdmLabelStatisticsFaultException
   */
  // ICDM-2469
  public GetVcdmLabelStatisticsForWPResponse1 getVcdmLabelStatisticsForWP(
      final GetVcdmLabelStatisticsForWP vcdmLabelStatWpType)
      throws GetVcdmLabelStatisticsFaultException {

    VcdmLabelStatWpType input = vcdmLabelStatWpType.getGetVcdmLabelStatisticsForWP();

    Set<VcdmDataSetWPStats> wpStatsSet;
    try (ServiceData serviceData = createServiceData(null)) {
      VcdmDataSetWPStatsLoader loader = new VcdmDataSetWPStatsLoader(serviceData);
      wpStatsSet = loader.getStatisticsByPidcId(input.getPidcID(), input.getEaseeDstId(), input.getTimeperiod());
    }
    catch (Exception exp) {
      throw new GetVcdmLabelStatisticsFaultException(exp.getMessage(), exp);
    }

    VcdmLabelStatsWPType[] vcdmLabelStatsWPType = new VcdmLabelStatsWPType[wpStatsSet.size()];

    int i = 0;
    for (VcdmDataSetWPStats vcdmDatasetsWorkpkgStat : wpStatsSet) {
      VcdmLabelStatsWPType vcdmLabelStatsWPTypeObject = new VcdmLabelStatsWPType();
      vcdmLabelStatsWPTypeObject.setAprjId(vcdmDatasetsWorkpkgStat.getAprjId());
      vcdmLabelStatsWPTypeObject.setAprjName(vcdmDatasetsWorkpkgStat.getAprjName());
      vcdmLabelStatsWPTypeObject.setCalibratedLabels(vcdmDatasetsWorkpkgStat.getCalibratedLabels());
      vcdmLabelStatsWPTypeObject.setChangedLabels(vcdmDatasetsWorkpkgStat.getChangedLabels());
      vcdmLabelStatsWPTypeObject.setCheckedLabels(vcdmDatasetsWorkpkgStat.getCheckedLabels());
      vcdmLabelStatsWPTypeObject.setCompletedLabels(vcdmDatasetsWorkpkgStat.getCompletedLabels());
      vcdmLabelStatsWPTypeObject.setNostateLabels(vcdmDatasetsWorkpkgStat.getNostateLabels());
      vcdmLabelStatsWPTypeObject.setPrelimcalibratedLabels(vcdmDatasetsWorkpkgStat.getPrelimcalibratedLabels());
      vcdmLabelStatsWPTypeObject.setStatusId(vcdmDatasetsWorkpkgStat.getStatusId());
      vcdmLabelStatsWPTypeObject.setVcdmSoftware(vcdmDatasetsWorkpkgStat.getVcdmSoftware());
      vcdmLabelStatsWPTypeObject.setVcdmVariant(vcdmDatasetsWorkpkgStat.getVcdmVariant());
      vcdmLabelStatsWPTypeObject.setWorkpkgName(vcdmDatasetsWorkpkgStat.getWorkpkgName());
      vcdmLabelStatsWPTypeObject.setEaseedstModDate(vcdmDatasetsWorkpkgStat.getEaseedstModDate());
      // 221731 - easeedst id
      vcdmLabelStatsWPTypeObject.setEaseeDstId(vcdmDatasetsWorkpkgStat.getEaseeDstId());
      // 221731 - revision no
      vcdmLabelStatsWPTypeObject.setRevisionNo(vcdmDatasetsWorkpkgStat.getRevisionNo());

      vcdmLabelStatsWPType[i] = vcdmLabelStatsWPTypeObject;
      i++;
    }
    GetVcdmLabelStatWpResp getVcdmLabelStatWpResp = new GetVcdmLabelStatWpResp();
    getVcdmLabelStatWpResp.setLabelStatsWp(vcdmLabelStatsWPType);
    GetVcdmLabelStatisticsForWPResponse1 getVcdmLabelStatisticsForWPRes = new GetVcdmLabelStatisticsForWPResponse1();
    getVcdmLabelStatisticsForWPRes.setGetVcdmLabelStatisticsForWPResponse1(getVcdmLabelStatWpResp);
    return getVcdmLabelStatisticsForWPRes;
  }

  /**
   * @param getPidcVersStatReq getPidcVersionStatisticsRequest
   * @return GetPidcStatisticResponse
   * @throws GetPidcVersionStatisticsFaultException GetPidcVersionStatisticsFaultException
   */
  public GetPidcVersionStatisticsResponse getPIdcVersionStatistics(final PidcVersionStatisticsReq getPidcVersStatReq)
      throws GetPidcVersionStatisticsFaultException {
    Session session =
        getSession(getPidcVersStatReq.getSessID(), "getPIdcVersionStatistics", "get PIdc Version Statistics");

    long pidcID = getPidcVersStatReq.getPidcID();
    String type = getPidcVersStatReq.getType();
    GetPidcVersionStatisticsResponse response = new GetPidcVersionStatisticsResponse();

    PidcVersion activeVersion = null;
    try (ServiceData servData = createServiceData(session)) {
      PidcLoader pidcLoader = new PidcLoader(servData);
      PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(servData);
      if ("pidc".equalsIgnoreCase(type)) {
        Pidc pidc = pidcLoader.getDataObjectByID(pidcID);
        if (pidc == null) {
          throw new GetPidcVersionStatisticsFaultException(
              CommonUtils.concatenate(PIDC_ID, pidcID, " is not existing"));
        }
        activeVersion = pidcVersionLoader.getActivePidcVersion(pidcID);
      }
      else if ("pidcVersion".equalsIgnoreCase(type)) {
        activeVersion = pidcVersionLoader.getActivePidcVersion(pidcID);
      }
      else {
        throw new GetPidcVersionStatisticsFaultException("The type should be either pidc or pidcVersion");
      }
      if (pidcVersionLoader.isHiddenToCurrentUser(activeVersion.getId())) {
        if ("pidc".equalsIgnoreCase(type)) {
          throw new GetPidcVersionStatisticsFaultException(
              CommonUtils.concatenate(PIDC_ID, pidcID, " is not existing"));
        }
        else if ("pidcVersion".equalsIgnoreCase(type)) {
          throw new GetPidcVersionStatisticsFaultException(generateErrorMessage(pidcID, true));
        }
      }
      PidcVersionStatisticsReportLoader loader = new PidcVersionStatisticsReportLoader(servData, activeVersion);
      PidcVersionStatisticsReport statisticResp = loader.createStatResponse();
      PIDCVersionStatResponseType versRespType = new PIDCVersionStatResponseType();

      BeanUtils.copyProperties(versRespType, statisticResp);

      response.setVersionStatResponse(versRespType);
    }
    catch (InvocationTargetException | IllegalAccessException | ParseException | IcdmException exp) {
      throw new GetPidcVersionStatisticsFaultException("The Pidc Version of the given id " + pidcID + " is not found.",
          exp);
    }
    return response;
  }

  /**
   * @param getPidcFavouritesReq getPidcFavouritesReq
   * @return the PidcFavouritesResponse
   */
  public GetPidcFavouritesResponse getPidcFavourites(final PidcFavouritesReq getPidcFavouritesReq) {

    String sessId = getPidcFavouritesReq.getSessID();

    // ICDM-2400
    Session session = getSession(sessId, "getPidcFavourites", "Access rights for Pidc Favourites");

    List<PidcFavouriteResponseType> responseList = new ArrayList<>();

    try (ServiceData serviceData = createServiceData(session)) {
      PidcFavouriteLoader favLdr = new PidcFavouriteLoader(serviceData);

      String userID = getPidcFavouritesReq.getUserID();

      Map<String, Map<Long, PidcFavourite>> retMap = userID == null ? favLdr.getAllFavourites()
          : favLdr.getAllFavouritesByUser(new HashSet<>(Arrays.asList(userID)));

      for (Entry<String, Map<Long, PidcFavourite>> allFavEntry : retMap.entrySet()) {

        String user = allFavEntry.getKey();

        for (PidcFavourite fav : allFavEntry.getValue().values()) {
          // create the reponse obj and set values
          PidcFavouriteResponseType respObj = new PidcFavouriteResponseType();
          respObj.setPidcID(fav.getPidcId());
          respObj.setUserID(user);
          responseList.add(respObj);
        }
      }
    }
    catch (IcdmException exp) {
      getLogger().error(exp.getMessage(), exp);
    }

    GetPidcFavouritesResponse response = new GetPidcFavouritesResponse();
    // create response array
    PidcFavouriteResponseType[] responseArray = new PidcFavouriteResponseType[responseList.size()];
    // Convert the list to the array
    responseArray = responseList.toArray(responseArray);
    // Set the response
    response.setPidcFavouriteResponse(responseArray);
    return response;
  }

  /**
   * @param getPidcWebFlowData getPidcWebFlowData
   * @return GetPidcWebFlowDataResponse GetPidcWebFlowDataResponse
   * @throws GetPidcWebFlowDataFault1Exception GetPidcWebFlowDataFault1Exception
   */
  public PidcWebFlowReponseType getPidcWebFlowData(final GetPidcWebFlowData getPidcWebFlowData)
      throws GetPidcWebFlowDataFault1Exception {


    // get the session from the
    Session session = getSession(getPidcWebFlowData.getGetPidcWebFlowData().getSessID(), "getPidcWebFlowData",
        String.valueOf(getPidcWebFlowData.getGetPidcWebFlowData().getElementID()));

    // get the element Id
    long elementID = getPidcWebFlowData.getGetPidcWebFlowData().getElementID();
    PidcWebFlowData fetchDataForWebFlow = null;

    // fetch the data from the service
    try (ServiceData servData = createServiceData(session)) {
      // create the web loader Object. Pass user as Argument
      com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader webFlowLoader =
          new com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader(servData);
      fetchDataForWebFlow = webFlowLoader.fetchDataForWebFlow(elementID);
    }
    catch (IcdmException exp) {
      throw new GetPidcWebFlowDataFault1Exception(exp.getMessage(), exp);
    }
    // create the response type object.
    PidcWebFlowReponseType reponseType = new PidcWebFlowReponseType();
    PidcDetailsType detailsType = new PidcDetailsType();
    detailsType.setId(fetchDataForWebFlow.getElemementID());
    detailsType.setName(fetchDataForWebFlow.getName());
    detailsType.setVcdmElementName(fetchDataForWebFlow.getVcdmElementName());
    detailsType.setChangeNumber(fetchDataForWebFlow.getChangeNum());
    reponseType.setPidcDetailsType(detailsType);
    // Set the id
    // Set the name
    Set<WebFlowAttribute> webFlowAttr = fetchDataForWebFlow.getWebFlowAttr();
    WebflowAttributesType[] attrArray = new WebflowAttributesType[webFlowAttr.size()];
    // populkate the attribute names
    try {
      populateAttr(webFlowAttr, attrArray);
    }
    catch (IcdmException exp) {
      throw new GetPidcWebFlowDataFault1Exception(exp.getMessage(), exp);
    }
    reponseType.setWebFlowAttributes(attrArray);
    return reponseType;
  }

  /**
   * @param getPidcWebFlowDataV2 GetPidcWebFlowDataV2
   * @return GetPidcWebFlowDataResponse GetPidcWebFlowDataResponse
   * @throws GetPidcWebFlowDataV2FaultException GetPidcWebFlowDataV2FaultException
   */
  public PidcWebFlowV2ReponseType getPidcWebFlowDataV2(final GetPidcWebFlowDataV2 getPidcWebFlowDataV2)
      throws GetPidcWebFlowDataV2FaultException {

    Session session = getSession(getPidcWebFlowDataV2.getGetPidcWebFlowDataV2().getSessID(), "getPidcWebFlowDataV2",
        String.valueOf(getPidcWebFlowDataV2.getGetPidcWebFlowDataV2().getElementID()));

    // get the element Id
    long elementID = getPidcWebFlowDataV2.getGetPidcWebFlowDataV2().getElementID();
    PidcWebFlowData fetchDataForWebFlow = null;

    // fetch the data from the service
    ServiceData servData = createServiceData(session);

    Set<PidcVariant> pidcVarSet = new HashSet<>();

    try // (ServiceData servData = createServiceData(session))
    {
      // create the web loader Object. Pass user as Argument
      com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader webFlowLoader =
          new com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader(servData);
      fetchDataForWebFlow = webFlowLoader.fetchDataForWebFlowV2(elementID);

      String elementType = webFlowLoader.getElementTypeV2(elementID);
      PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(servData);
      if (ApicConstants.WEBFLOW_PIDC.equals(elementType)) {
        pidcVarSet.clear();
      }
      else if (ApicConstants.WEBFLOW_VARIANT.equals(elementType)) {
        PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(elementID);
        pidcVarSet.add(pidcVariant);
      }
      else if (ApicConstants.WEBFLOW_ELEMENT.equals(elementType)) {
        pidcVarSet = webFlowLoader.getAllWebFlowVariants(elementID);
      }
      else {
        throw new GetPidcWebFlowDataV2FaultException("Invalid Element ID or Variant ID or PIDC Version ID");
      }
    }
    catch (IcdmException exp) {
      throw new GetPidcWebFlowDataV2FaultException(exp.getMessage(), exp);
    }
    // create the response type object.
    PidcWebFlowV2ReponseType responseType = new PidcWebFlowV2ReponseType();

    PidcDetailsType detailsType = new PidcDetailsType();
    detailsType.setId(fetchDataForWebFlow.getElemementID());
    detailsType.setName(fetchDataForWebFlow.getName());
    detailsType.setVcdmElementName(fetchDataForWebFlow.getVcdmElementName());
    detailsType.setChangeNumber(fetchDataForWebFlow.getChangeNum());
    responseType.setPidcDetailsType(detailsType);

    if (!pidcVarSet.isEmpty()) {
      PidcVariantDetailsType variantDetailsType = new PidcVariantDetailsType();
      VariantType[] varTypeArray = new VariantType[pidcVarSet.size()];
      int index = 0;
      for (PidcVariant pidcVariant : pidcVarSet) {
        // create the variantType object
        varTypeArray[index] = new VariantType();
        varTypeArray[index].setVariantID(pidcVariant.getId());
        varTypeArray[index].setVariantName(pidcVariant.getName());
        index++;
      }
      variantDetailsType.setVariantType(varTypeArray);

      responseType.setPidcVariantDetailsType(variantDetailsType);
    }

    Set<WebFlowAttribute> webFlowAttr = fetchDataForWebFlow.getWebFlowAttr();
    WebflowAttributesType[] attrArray = new WebflowAttributesType[webFlowAttr.size()];
    // populate the attribute names
    try {
      populateAttr(webFlowAttr, attrArray);
    }
    catch (IcdmException exp) {
      throw new GetPidcWebFlowDataV2FaultException(exp.getMessage(), exp);
    }
    responseType.setWebFlowAttributes(attrArray);
    return responseType;
  }

  /**
   * @param getPidcWebFlowElementReq
   * @return PidcWebFlowElementRespType GetPidcWebFlowDataResponse
   * @throws GetPidcWebFlowElementFaultException
   */
  public PidcWebFlowElementRespType getPidcWebFlowElement(final GetPidcWebFlowElementReq getPidcWebFlowElementReq)
      throws GetPidcWebFlowElementFaultException {


    // get the session from the
    Session session = getSession(getPidcWebFlowElementReq.getGetPidcWebFlowElementReq().getSessID(),
        "getPidcWebFlowElement", String.valueOf(getPidcWebFlowElementReq.getGetPidcWebFlowElementReq().getElementID()));

    // get the element Id
    long elementID = getPidcWebFlowElementReq.getGetPidcWebFlowElementReq().getElementID();
    Set<PidcWebFlowData> pidcWebFlowDataSet = new HashSet<>();

    try (ServiceData servData = createServiceData(session)) {
      // create the web loader Object. Pass user as Argument
      com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader webFlowLoader =
          new com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader(servData);
      pidcWebFlowDataSet = webFlowLoader.fetchDataForWebFlowElement(elementID);
    }
    catch (IcdmException exp) {
      throw new GetPidcWebFlowElementFaultException("An error occured. " + exp.getMessage(), exp);
    }

    // create the response type object.
    PidcWebFlowElementRespType reponseType = new PidcWebFlowElementRespType();
    Set<WebFlowAttribute> webFlowAttr = new HashSet<>();
    Map<WebFlowAttribute, Set<WebFlowAttrValues>> webFlowAttrMap = new HashMap<>();

    reponseType.setElementSelection(getElementSelType(elementID, pidcWebFlowDataSet));

    for (PidcWebFlowData webFlowData : pidcWebFlowDataSet) {
      ElementDetailsType detailsType = new ElementDetailsType();
      detailsType.setId(webFlowData.getElemementID());
      StringBuilder elementName = new StringBuilder();
      elementName.append(PIDC_PREFIX);
      elementName.append(webFlowData.getName());
      if ((null != webFlowData.getVarName()) && !webFlowData.getVarName().isEmpty()) {
        elementName.append("/Variant:");
        elementName.append(webFlowData.getVarName());
      }
      detailsType.setName(elementName.toString());
      detailsType.setVariantName(webFlowData.getVarName());
      detailsType.setChangeNumber(webFlowData.getChangeNum());

      for (WebFlowAttribute attr : webFlowData.getWebFlowAttr()) {
        if (webFlowAttrMap.containsKey(attr)) {
          webFlowAttrMap.get(attr).addAll(attr.getWebFlowAttrValues());
        }
        else {
          webFlowAttrMap.put(attr, attr.getWebFlowAttrValues());
        }
      }

      webFlowAttr.addAll(webFlowData.getWebFlowAttr());
      reponseType.addElementDetails(detailsType);
    }

    WebflowAttributesType[] attrArray = new WebflowAttributesType[webFlowAttr.size()];
    // populate the attribute names
    try {
      populateAttr(webFlowAttrMap, attrArray);
    }
    catch (IcdmException exp) {
      throw new GetPidcWebFlowElementFaultException("An error occured. " + exp.getMessage(), exp);
    }
    reponseType.setWebFlowAttributes(attrArray);
    return reponseType;
  }

  /**
   * @param elementID
   * @param pidcWebFlowDataSet
   * @param elementSel
   * @return
   */
  private ElementSelectionType getElementSelType(final long elementID, final Set<PidcWebFlowData> pidcWebFlowDataSet) {
    ElementSelectionType elementSel = new ElementSelectionType();
    if (!pidcWebFlowDataSet.isEmpty()) {
      PidcWebFlowData webFlowEle = pidcWebFlowDataSet.iterator().next();
      if (pidcWebFlowDataSet.size() > 1) {
        elementSel.setId(elementID);
        elementSel.setName(PIDC_PREFIX + webFlowEle.getName() + "/Multiple variants");
        elementSel.setType("Variant Selection");
        elementSel.setVcdmElementName(webFlowEle.getVcdmElementName());
      }
      else {
        elementSel.setId(elementID);
        if ((null != webFlowEle.getVarName()) && !webFlowEle.getVarName().isEmpty()) {
          elementSel.setName(PIDC_PREFIX + webFlowEle.getName() + "/Variant:" + webFlowEle.getVarName());
          elementSel.setType("Single Variant");
        }
        else {
          elementSel.setName(PIDC_PREFIX + webFlowEle.getName());
          elementSel.setType("Single PID Card");
        }
        elementSel.setVcdmElementName(webFlowEle.getVcdmElementName());
      }
    }
    return elementSel;
  }


  /**
   * @param invaliWebFlowElementReq request
   * @return InvalidateWebFlowElementResponseType InvalidateWebFlowElementResponse
   */
  public InvalidateWebFlowElementResponseType invalidateWebFlowElement(
      final InvalidateWebFlowElementReq invaliWebFlowElementReq) {

    // get the session from the
    Session session =
        getSession(invaliWebFlowElementReq.getInvalidateWebFlowElementReq().getSessID(), "invalidateWebFlowElement",
            String.valueOf(invaliWebFlowElementReq.getInvalidateWebFlowElementReq().getElementID()));

    // get the element Id
    long elementID = invaliWebFlowElementReq.getInvalidateWebFlowElementReq().getElementID();
    String result = "Success : The Element Id is marked as Invalid : " + elementID;

    try (ServiceData serviceData = createServiceData(session)) {
      WebflowElementMainCommand cmd = new WebflowElementMainCommand(serviceData, elementID);
      CommandExecuter cmdExecute = new CommandExecuter(serviceData);
      cmdExecute.execute(cmd);
    }
    catch (IcdmException exp) {
      result = "An error occured." + exp.getMessage();
      getLogger().error(exp.getMessage(), exp);
    }
    InvalidateWebFlowElementResponseType response = new InvalidateWebFlowElementResponseType();
    response.setResult(result);
    return response;
  }

  /**
   * @param webFlowAttr
   * @param attrArray
   */
  private void populateAttr(final Set<WebFlowAttribute> webFlowAttr, final WebflowAttributesType[] attrArray)
      throws IcdmException {
    int index = 0;

    for (WebFlowAttribute attr : webFlowAttr) {
      // Create the new Object for Attributes Type
      attrArray[index] = new WebflowAttributesType();
      // Set the attr Id
      attrArray[index].setAttrID(attr.getAttrID());
      // set the attr name
      attrArray[index].setAttrNameEng(attr.getAttrNameEng());
      // Set the Attr Desc
      attrArray[index].setAttrDescEng(attr.getAttrDescEng());
      // Set Attr Type
      attrArray[index].setAttrType(attr.getAttrtype());
      // Is alias field
      attrArray[index].setAliasName(attr.isAliasPresent());
      // Values for the attr
      WebFlowAttrValuesType[] valueArray = new WebFlowAttrValuesType[attr.getWebFlowAttrValues().size()];
      populateAttrValues(attr.getWebFlowAttrValues(), valueArray);
      // Set the array to the
      attrArray[index].setWebFlowAttrValues(valueArray);
      index++;
    }
  }

  /**
   * @param webFlowAttrMap
   * @param attrArray
   */
  private void populateAttr(final Map<WebFlowAttribute, Set<WebFlowAttrValues>> webFlowAttrMap,
      final WebflowAttributesType[] attrArray)
      throws IcdmException {
    int index = 0;

    for (Entry<WebFlowAttribute, Set<WebFlowAttrValues>> attr : webFlowAttrMap.entrySet()) {
      // Create the new Object for Attributes Type
      attrArray[index] = new WebflowAttributesType();
      // Set the attr Id
      attrArray[index].setAttrID(attr.getKey().getAttrID());
      // set the attr name
      attrArray[index].setAttrNameEng(attr.getKey().getAttrNameEng());
      // Set the Attr Desc
      attrArray[index].setAttrDescEng(attr.getKey().getAttrDescEng());
      // Set Attr Type
      attrArray[index].setAttrType(attr.getKey().getAttrtype());
      // Is alias field
      attrArray[index].setAliasName(attr.getKey().isAliasPresent());
      // Values for the attr
      WebFlowAttrValuesType[] valueArray = new WebFlowAttrValuesType[attr.getValue().size()];
      populateAttrValues(attr.getValue(), valueArray);
      // Set the array to the
      attrArray[index].setWebFlowAttrValues(valueArray);
      index++;
    }
  }


  /**
   * @param webFlowAttrValues
   * @param valueArray
   */
  private void populateAttrValues(final Set<WebFlowAttrValues> webFlowAttrValues,
      final WebFlowAttrValuesType[] valueArray)
      throws IcdmException {
    try {
      int index = 0;
      for (WebFlowAttrValues attrValues : webFlowAttrValues) {
        // Create new Object
        valueArray[index] = new WebFlowAttrValuesType();
        // Copy the properties
        BeanUtils.copyProperties(valueArray[index], attrValues);
        index++;
      }

    }
    catch (IllegalAccessException | InvocationTargetException exp) {
      throw new IcdmException("Cannot set the properties properly ", exp);
    }
  }

  // ICDM-2400
  private static String generateErrorMessage(final long pidcId, final boolean isHidden) {
    if (isHidden) {
      return CommonUtils.concatenate("Pidc Version of the given Id ", pidcId, " is not visible to the user");
    }
    return CommonUtils.concatenate(PIDC_ID, pidcId, " is not existing");
  }


  /**
   * @return WebServiceVersionResponse
   * @throws IcdmException error
   * @deprecated service is deprecated
   */
  @Deprecated
  public WebServiceVersionResponse getWebServiceVersion() throws IcdmException {
    throw new IcdmException(getObsoleteServiceErrMsg());
  }


  /**
   * @return ParameterStatisticsXmlResponse
   * @throws IcdmException error
   * @deprecated service is deprecated
   */
  @Deprecated
  public ParameterStatisticsXmlResponse getParameterStatisticsXML() throws IcdmException {
    throw new IcdmException(getObsoleteServiceErrMsg());
  }
}
