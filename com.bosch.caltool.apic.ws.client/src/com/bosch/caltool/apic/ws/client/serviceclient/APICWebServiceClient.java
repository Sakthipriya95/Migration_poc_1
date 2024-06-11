/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.databinding.ADBBean;
import org.apache.axis2.databinding.types.HexBinary;
import org.apache.axis2.transport.http.HTTPConstants;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ws.client.A2LCallbackHandler;
import com.bosch.caltool.apic.ws.client.APICCallbackHandler;
import com.bosch.caltool.apic.ws.client.APICStub;
import com.bosch.caltool.apic.ws.client.APICStub.A2LFileDataType;
import com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffResponse;
import com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffVersResponse;
import com.bosch.caltool.apic.ws.client.APICStub.AllProjectIdCardVersType;
import com.bosch.caltool.apic.ws.client.APICStub.AttrDiffResponse;
import com.bosch.caltool.apic.ws.client.APICStub.AttrDiffType;
import com.bosch.caltool.apic.ws.client.APICStub.AttrDiffVersResponse;
import com.bosch.caltool.apic.ws.client.APICStub.Attribute;
import com.bosch.caltool.apic.ws.client.APICStub.AttributeValue;
import com.bosch.caltool.apic.ws.client.APICStub.GetAllAttributesResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetAllProjectIdCardVersionsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetAllProjectIdCardsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetAttrGroupsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetAttributeValuesResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetParameterReviewResultResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsExtResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsFileResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffForVersionResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetPidcFavouritesResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardForVersionResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetUseCasesResponse;
import com.bosch.caltool.apic.ws.client.APICStub.GetVcdmLabelStatisticsResponse;
import com.bosch.caltool.apic.ws.client.APICStub.LabelStatReq;
import com.bosch.caltool.apic.ws.client.APICStub.LoginResponse;
import com.bosch.caltool.apic.ws.client.APICStub.ParameterStatisticsRequestType;
import com.bosch.caltool.apic.ws.client.APICStub.PidcAccessRightResponse;
import com.bosch.caltool.apic.ws.client.APICStub.PidcAccessRights;
import com.bosch.caltool.apic.ws.client.APICStub.PidcActiveVersionResponse;
import com.bosch.caltool.apic.ws.client.APICStub.PidcActiveVersionResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.PidcActiveVersionType;
import com.bosch.caltool.apic.ws.client.APICStub.PidcScoutResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.PidcScoutVersResponseType;
import com.bosch.caltool.apic.ws.client.APICStub.PidcSearchConditions;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardAllVersInfoType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardInfoType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardVersType;
import com.bosch.caltool.apic.ws.client.APICStub.ProjectIdCardWithVersion;
import com.bosch.caltool.apic.ws.client.APICStub.ReviewResultsType;
import com.bosch.caltool.apic.ws.client.APICStub.SuperGroupType;
import com.bosch.caltool.apic.ws.client.APICStub.UseCaseType;
import com.bosch.caltool.apic.ws.client.APICStub.ValueList;
import com.bosch.caltool.apic.ws.client.APICStub.VcdmLabelStats;
import com.bosch.caltool.apic.ws.client.ParameterStatisticCallbackHandler;
import com.bosch.caltool.apic.ws.client.pidcsearch.PidcSearchCondition;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.wsadapter.WsAdapterFactory;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;


/**
 * @author hef2fe
 */
public class APICWebServiceClient {

  /**
   * @author hef2fe
   * @deprecated not used
   */
  @Deprecated
  public enum APICWsServer {
                            /**
                             * local server
                             */
                            // ICDM-218
                            LOCAL_SERVER("LOCAL_SERVER"),
                            /**
                             * iCDM Server 01
                             */
                            ICDM_01_SERVER("ICDM_01_SERVER"),
                            /**
                             * iCDM Server 02
                             */
                            ICDM_02_SERVER("ICDM_02_SERVER"),
                            /**
                             * iCDM Server 05
                             */
                            ICDM_05_SERVER("ICDM_05_SERVER"),
                            /**
                             * iCDM Server 06
                             */
                            ICDM_06_SERVER("ICDM_06_SERVER"),
                            /**
                             * iCDM Server 07
                             */
                            ICDM_07_SERVER("ICDM_07_SERVER"),
                            /**
                             * iCDM Server 08
                             */
                            ICDM_08_SERVER("ICDM_08_SERVER");

    private String literal;

    /**
     * @return String
     */
    // ICDM-218
    public String getLiteral() {
      return this.literal;
    }

    /**
     * This method returns the APICWsServer
     *
     * @param literl defines server type
     * @return APICWsServer
     */
    // ICDM-218
    public static APICWsServer getAPICWsServer(final String literl) {
      for (APICWsServer apicWsServer : APICWsServer.values()) {
        if (apicWsServer.literal.equals(literl)) {
          return apicWsServer;
        }
      }
      return null;
    }

    // ICDM-218
    APICWsServer(final String literl) {
      this.literal = literl;
    }
  }

  public enum PidcAccessRightsTyp {

                                   ACCESS_TRUE("User has access", 1),
                                   ACCESS_FALSE("User has noaccess", 0),
                                   ALL("All access rights", 2);

    private String description;
    private int code;

    private PidcAccessRightsTyp(final String description, final int code) {
      this.description = description;
      this.code = code;
    }

    public int getCode() {
      return this.code;
    }

  }

  /**
   * Logger for this class
   */
  private static ILoggerAdapter LOG = ClientConfiguration.getDefault().getLogger();

  private APICStub stub = null;

  /**
   * End point of SOAP web service
   */
  private String soapEndPoint = "";

  private final ClientConfiguration config;

  /**
   * @param apicWsServer server name
   * @deprecated not required. Use {@link #APICWebServiceClient()} instead
   */
  @Deprecated
  public APICWebServiceClient(final APICWsServer apicWsServer) {
    this();
  }

  /**
   * The setting of soap end client
   */
  public APICWebServiceClient() {
    this(ClientConfiguration.getDefault());
  }


  /**
   * @param config ClientConfiguration
   */
  public APICWebServiceClient(final ClientConfiguration config) {
    this.config = config;
    this.soapEndPoint = config.getBaseUri() + "/services/APIC";
    this.stub = getStub(this.soapEndPoint, 30 * 60 * 1000);
  }

  /**
   * Set logger
   *
   * @param log logger
   */
  public void setLogger(final ILoggerAdapter log) {
    LOG = log;
  }

  /**
   * Login and create session id. credentials would be taken from the client configuration userd
   *
   * @return session id
   * @throws Exception error while logging in
   */
  public String login() throws Exception {
    APICStub.Login login = (APICStub.Login) getTestObject(APICStub.Login.class);

    login.setUserName(this.config.getUserName());
    login.setPassword(this.config.getPassword());

    login.setTimezone(this.config.getTimezone());

    LoginResponse response = this.stub.login(login);

    return response.getSessID();
  }

  /**
   * Login using user and password
   *
   * @param userName userName
   * @param passWord passWord
   * @return session id
   * @throws Exception error while logging in
   */
  public String login(final String userName, final String passWord) throws Exception {
    APICStub.Login login = (APICStub.Login) getTestObject(APICStub.Login.class);
    login.setUserName(userName);
    login.setPassword(passWord);
    login.setTimezone(this.config.getTimezone());

    LoginResponse response = this.stub.login(login);

    return response.getSessID();
  }

  /**
   * @return
   * @throws Exception
   */
  public SuperGroupType[] getAttrGroups() throws Exception {
    APICStub.GetAttrGroups getAttrGroups;

    getAttrGroups = (APICStub.GetAttrGroups) getTestObject(APICStub.GetAttrGroups.class);

    getAttrGroups.setSessID(login());

    GetAttrGroupsResponse response = this.stub.getAttrGroups(getAttrGroups);

    return response.getGetAttrGroupsResponse().getSuperGroups();

  }

  /**
   * @return
   * @throws Exception
   */
  public Attribute[] getAllAttributes() throws Exception {
    APICStub.GetAllAttributes getAllAttributes;

    getAllAttributes = (APICStub.GetAllAttributes) getTestObject(APICStub.GetAllAttributes.class);

    getAllAttributes.setSessID(login());

    GetAllAttributesResponse response = this.stub.getAllAttributes(getAllAttributes);

    return response.getGetAllAttributesResponse().getAttributesList().getAttributes();

  }

  /**
   * @return
   * @throws Exception
   */
  public ProjectIdCardInfoType[] getAllPidc() throws Exception {
    APICStub.GetAllProjectIdCards getAllProjectIdCards;

    getAllProjectIdCards = (APICStub.GetAllProjectIdCards) getTestObject(APICStub.GetAllProjectIdCards.class);

    getAllProjectIdCards.setSessID(login());

    GetAllProjectIdCardsResponse response = this.stub.getAllProjectIdCards(getAllProjectIdCards);

    return response.getGetAllProjectIdCardsResponse().getProjectIdCards();
  }


  /**
   * @param pidcID
   * @return
   * @throws Exception
   */
  public PidcActiveVersionResponseType getPidcActiveVersionId(final long pidcID) throws Exception {

    APICStub.PidcActiveVersion getPidcActiveVersion;

    getPidcActiveVersion = (APICStub.PidcActiveVersion) getTestObject(APICStub.PidcActiveVersion.class);
    PidcActiveVersionType type = new PidcActiveVersionType();
    getPidcActiveVersion.setPidcActiveVersion(type);
    getPidcActiveVersion.getPidcActiveVersion().setSessId(login());
    getPidcActiveVersion.getPidcActiveVersion().setPidcId(pidcID);


    PidcActiveVersionResponse response = this.stub.getPidcActiveVersionId(getPidcActiveVersion);

    return response.getPidcActiveVersionResponse();
  }

  /**
   * @param userId User NT ID
   * @return GetPidcFavouritesResponse object
   * @throws Exception exception in ws call
   */
  public GetPidcFavouritesResponse getPidcFavourites(final String userId) throws Exception {
    APICStub.GetPidcFavouritesReq getPidcFavouritesReq;

    getPidcFavouritesReq = (APICStub.GetPidcFavouritesReq) getTestObject(APICStub.GetPidcFavouritesReq.class);

    APICStub.PidcFavouritesReq pidcFavouritesReq =
        (APICStub.PidcFavouritesReq) getTestObject(APICStub.PidcFavouritesReq.class);

    pidcFavouritesReq.setSessID(login());
    pidcFavouritesReq.setUserID(userId);

    getPidcFavouritesReq.setGetPidcFavouritesReq(pidcFavouritesReq);

    return this.stub.getPidcFavourites(getPidcFavouritesReq);
  }


  /**
   * @return
   * @throws Exception
   */
  public ProjectIdCardAllVersInfoType[] getAllPidcVersion() throws Exception {

    APICStub.GetAllProjectIdCardVersions getAllProjectIdCards;

    getAllProjectIdCards =
        (APICStub.GetAllProjectIdCardVersions) getTestObject(APICStub.GetAllProjectIdCardVersions.class);
    AllProjectIdCardVersType type = new AllProjectIdCardVersType();
    getAllProjectIdCards.setGetAllProjectIdCardVersions(type);
    getAllProjectIdCards.getGetAllProjectIdCardVersions().setSessID(login());
    GetAllProjectIdCardVersionsResponse response = this.stub.getAllProjectIdCardVersions(getAllProjectIdCards);

    return response.getGetAllProjectIdCardVersionsResponse().getProjectIdCards();
  }

  public ValueList[] getAttrValues(final long attrID) throws Exception {
    return getAttrValues(new long[] { attrID });
  }

  /**
   * @param attrID
   * @return
   * @throws Exception
   */
  public ValueList[] getAttrValues(final long[] attrIDs) throws Exception {
    APICStub.GetAttributeValues getAttributeValues;

    getAttributeValues = (APICStub.GetAttributeValues) getTestObject(APICStub.GetAttributeValues.class);

    getAttributeValues.setSessID(login());
    getAttributeValues.setAttributeIDs(attrIDs);

    GetAttributeValuesResponse response = this.stub.getAttributeValues(getAttributeValues);

    ValueList[] valueLists = response.getGetAttributeValuesResponse().getValueLists();

    // To avoid returning null as AttributeValue array
    for (ValueList valueList : valueLists) {
      if (valueList.getValues() == null) {
        valueList.setValues(new AttributeValue[0]);
      }
    }

    return valueLists;
  }

  /**
   * @param pidcID
   * @return
   * @throws Exception
   */
  public ProjectIdCardType getPidcDetails(final Long pidcID) throws Exception {
    APICStub.GetProjectIdCard getProjectIdCard;

    getProjectIdCard = (APICStub.GetProjectIdCard) getTestObject(APICStub.GetProjectIdCard.class);

    getProjectIdCard.setSessID(login());
    getProjectIdCard.setProjectIdCardID(pidcID);

    GetProjectIdCardResponse response = this.stub.getProjectIdCard(getProjectIdCard);

    return response.getGetProjectIdCardResponse().getProjectIdCard();

  }

  /**
   * @param pidcVersionID
   * @return
   * @throws Exception
   */
  public ProjectIdCardWithVersion getPidcForVersion(final Long pidcVersionID) throws Exception {

    APICStub.GetProjectIdCardForVersion getProjectIdCard;

    getProjectIdCard = (APICStub.GetProjectIdCardForVersion) getTestObject(APICStub.GetProjectIdCardForVersion.class);
    ProjectIdCardVersType type = new ProjectIdCardVersType();
    getProjectIdCard.setGetProjectIdCardForVersion(type);
    getProjectIdCard.getGetProjectIdCardForVersion().setSessId(login());
    getProjectIdCard.getGetProjectIdCardForVersion().setPidcVersionId(pidcVersionID);
    GetProjectIdCardForVersionResponse response = this.stub.getProjectIdCardForVersion(getProjectIdCard);

    return response.getGetProjectIdCardForVersionResponse().getProjectIdCard();

  }

  /**
   * Get all UseCases from the WebService
   *
   * @return an array with all UseCases
   * @throws Exception
   */
  public UseCaseType[] getUseCases() throws Exception {

    return getUseCases(new long[0]);
  }

  /**
   * Gets the uses cases of the IDs that were passed as arguments
   *
   * @return an array with all UseCases
   * @throws Exception
   */
  public UseCaseType[] getUseCases(final long[] useCaseIDs) throws Exception {
    APICStub.GetUseCases getUsecases;

    getUsecases = (APICStub.GetUseCases) getTestObject(APICStub.GetUseCases.class);

    getUsecases.setSessID(login());

    if ((useCaseIDs != null) && (useCaseIDs.length > 0)) {
      getUsecases.setUseCaseIDs(useCaseIDs);
    }

    GetUseCasesResponse response = this.stub.getUseCases(getUsecases);

    return response.getUseCases();
  }


  /**
   * Get all differences of a PIDC between two different changeNumbers
   *
   * @param pidcID
   * @param oldChangeNumber
   * @param newChangeNumber
   * @return
   * @throws Exception
   */
  public GetPidcDiffsResponse getPidcDiffs(final Long pidcID, final Long oldChangeNumber, final Long newChangeNumber)
      throws Exception {

    APICStub.GetPidcDiffsType parameter;

    parameter = (APICStub.GetPidcDiffsType) getTestObject(APICStub.GetPidcDiffsType.class);

    parameter.setSessID(login());

    parameter.setPidcID(pidcID);
    parameter.setOldPidcChangeNumber(oldChangeNumber);
    parameter.setNewPidcChangeNumber(newChangeNumber);

    APICStub.GetPidcDiffs wsParameter = (APICStub.GetPidcDiffs) getTestObject(APICStub.GetPidcDiffs.class);

    wsParameter.setGetPidcDiffs(parameter);

    GetPidcDiffsResponse response = this.stub.getPidcDiffs(wsParameter);

    return response;
  }


  /**
   * Get all differences of a PIDC between two different changeNumbers
   *
   * @param pidcID
   * @param oldChangeNumber
   * @param newChangeNumber
   * @return
   * @throws Exception
   */
  public GetPidcDiffForVersionResponse getPidcDiffForVersion(final Long pidcID, final Long oldChangeNumber,
      final Long newChangeNumber, final Long pidVersID)
      throws Exception {

    APICStub.GetPidcDiffsVersType parameter;
    parameter = (APICStub.GetPidcDiffsVersType) getTestObject(APICStub.GetPidcDiffsVersType.class);

    parameter.setSessID(login());

    parameter.setPidcID(pidcID);
    if (pidVersID == null) {
      parameter.setPidcVersionID(0);
    }
    else {
      parameter.setPidcVersionID(pidVersID);
    }
    parameter.setOldPidcChangeNumber(oldChangeNumber);
    parameter.setNewPidcChangeNumber(newChangeNumber);

    APICStub.GetPidcDiffForVersion wsParameter =
        (APICStub.GetPidcDiffForVersion) getTestObject(APICStub.GetPidcDiffForVersion.class);

    wsParameter.setGetPidcDiffForVersion(parameter);

    GetPidcDiffForVersionResponse response = this.stub.getPidcDiffForVersion(wsParameter);

    return response;
  }


  /**
   * Get all differences of a PIDC between two different changeNumbers
   *
   * @param pidcID
   * @param oldChangeNumber
   * @param newChangeNumber
   * @return
   * @throws Exception
   */
  public AllPidcDiffResponse getAllPidcDiffs(final Long pidcID, final Long oldChangeNumber, final Long newChangeNumber)
      throws Exception {

    APICStub.AllPidcDiffType parameter;

    parameter = (APICStub.AllPidcDiffType) getTestObject(APICStub.AllPidcDiffType.class);

    parameter.setSessID(login());

    parameter.setPidcID(pidcID);
    parameter.setOldPidcChangeNumber(oldChangeNumber);
    parameter.setNewPidcChangeNumber(newChangeNumber);
    parameter.setAttributeId(new long[0]);

    APICStub.AllPidcDiff wsParameter = (APICStub.AllPidcDiff) getTestObject(APICStub.AllPidcDiff.class);

    wsParameter.setAllPidcDiff(parameter);

    AllPidcDiffResponse response = this.stub.getAllPidcDiff(wsParameter);

    return response;
  }


  /**
   * Get all differences of a PIDC between two different changeNumbers
   *
   * @param pidcID
   * @param oldChangeNumber
   * @param newChangeNumber
   * @return
   * @throws Exception
   */
  public AllPidcDiffVersResponse getAllPidcDiffForVersion(final Long pidcID, final Long oldChangeNumber,
      final Long newChangeNumber, final Long pidVersID)
      throws Exception {

    APICStub.AllPidcDiffVersType parameter;

    parameter = (APICStub.AllPidcDiffVersType) getTestObject(APICStub.AllPidcDiffVersType.class);

    parameter.setSessID(login());

    parameter.setPidcID(pidcID);
    if (pidVersID == null) {
      parameter.setPidcVersionID(0);
    }
    else {
      parameter.setPidcVersionID(pidVersID);
    }
    parameter.setOldPidcChangeNumber(oldChangeNumber);
    parameter.setNewPidcChangeNumber(newChangeNumber);
    parameter.setAttributeId(new long[0]);

    APICStub.AllPidcDiffVers wsParameter = (APICStub.AllPidcDiffVers) getTestObject(APICStub.AllPidcDiffVers.class);

    wsParameter.setAllPidcDiffVers(parameter);

    AllPidcDiffVersResponse response = this.stub.getAllPidcDiffForVersion(wsParameter);

    return response;
  }

  /**
   * Get all differences of a PIDC between two different changeNumbers
   *
   * @param pidcID
   * @param oldChangeNumber
   * @param newChangeNumber
   * @param language
   * @return
   * @throws Exception
   */
  public AttrDiffType[] getPidcAttrDiffReport(final Long pidcID, final Long oldChangeNumber, final Long newChangeNumber,
      final String language)
      throws Exception {

    APICStub.AllPidcDiffType parameter;

    parameter = (APICStub.AllPidcDiffType) getTestObject(APICStub.AllPidcDiffType.class);

    parameter.setSessID(login());

    parameter.setPidcID(pidcID);
    parameter.setOldPidcChangeNumber(oldChangeNumber);
    parameter.setNewPidcChangeNumber(newChangeNumber);
    parameter.setAttributeId(new long[0]);
    parameter.setLanguage(language);
    APICStub.AttrDiff wsParameter = (APICStub.AttrDiff) getTestObject(APICStub.AttrDiff.class);

    wsParameter.setAttrDiff(parameter);

    AttrDiffResponse response = this.stub.getPidcAttrDiffReport(wsParameter);

    return response.getAttrDiffResponse().getDifferences();
  }


  /**
   * Get all differences of a PIDC between two different changeNumbers
   *
   * @param pidcID
   * @param oldChangeNumber
   * @param newChangeNumber
   * @param language
   * @return
   * @throws Exception
   */
  public AttrDiffType[] getPidcAttrDiffReportForVersion(final Long pidcID, final Long oldChangeNumber,
      final Long newChangeNumber, final String language, final Long pidVersID, final String sessionID)
      throws Exception {

    APICStub.AllPidcDiffVersType parameter;

    parameter = (APICStub.AllPidcDiffVersType) getTestObject(APICStub.AllPidcDiffVersType.class);


    parameter.setSessID(sessionID);

    parameter.setPidcID(pidcID);
    parameter.setLanguage(language);
    parameter.setOldPidcChangeNumber(oldChangeNumber);
    parameter.setNewPidcChangeNumber(newChangeNumber);
    parameter.setAttributeId(new long[0]);
    if (pidVersID == null) {
      parameter.setPidcVersionID(0);
    }
    else {
      parameter.setPidcVersionID(pidVersID);
    }
    APICStub.AttrDiffVers wsParameter = (APICStub.AttrDiffVers) getTestObject(APICStub.AttrDiffVers.class);

    wsParameter.setAttrDiffVers(parameter);

    AttrDiffVersResponse response = this.stub.getPidcAttrDiffReportForVersion(wsParameter);

    return response.getAttrDiffVersResponse().getDifferences();
  }

  /**
   * @param parameterName define parameter label name
   * @param monitor defines IProgressMonitor - ICDM-218
   * @return LabelInfoVO
   * @throws Exception
   */
  // ICDM-218
  public LabelInfoVO getParameterStatistics(final String parameterName, final IProgressMonitor monitor)
      throws Exception {
    APICStub.GetParameterStatistics getParameterStatistics;

    getParameterStatistics = (APICStub.GetParameterStatistics) getTestObject(APICStub.GetParameterStatistics.class);
    // ICDM-218
    if ((monitor != null) && monitor.isCanceled()) {
      return null;
    }
    getParameterStatistics.setSessID(login());
    getParameterStatistics.setParameterName(parameterName);


    // ICDM-218
    if ((monitor != null) && monitor.isCanceled()) {
      return null;
    }
    GetParameterStatisticsResponse response = this.stub.getParameterStatistics(getParameterStatistics);
    ByteArrayInputStream bais =
        new ByteArrayInputStream(response.getGetParameterStatisticsResponse().getParameterStatistics().getBytes());

    return (LabelInfoVO) new ObjectInputStream(bais).readObject();

  }

  /**
   * @param parameterNames
   * @param monitor
   * @return
   * @throws Exception
   */
  public CalData[] getParameterStatisticsExt(final String[] parameterNames, final IProgressMonitor monitor)
      throws Exception {
    APICStub.GetParameterStatisticsExt getParameterStatisticsExt;

    getParameterStatisticsExt =
        (APICStub.GetParameterStatisticsExt) getTestObject(APICStub.GetParameterStatisticsExt.class);
    // ICDM-218
    if ((monitor != null) && monitor.isCanceled()) {
      return null;
    }
    getParameterStatisticsExt.setSessID(login());
    getParameterStatisticsExt.setParameterNames(parameterNames);


    // ICDM-218
    if ((monitor != null) && monitor.isCanceled()) {
      return null;
    }

    GetParameterStatisticsExtResponse response = this.stub.getParameterStatisticsExt(getParameterStatisticsExt);
    HexBinary[] results = response.getGetParameterStatisticsExtResponse().getParameterStatistics();

    CalData[] calDataObjects = new CalData[results.length];

    for (int i = 0; i < results.length; i++) {

      ByteArrayInputStream bais = new ByteArrayInputStream(results[i].getBytes());

      calDataObjects[i] = (CalData) new ObjectInputStream(bais).readObject();
    }


    return calDataObjects;

  }

  public APICCallbackHandler getParameterStatisticsExtAsync(final String[] parameterNames) throws Exception {

    return getParameterStatisticsExtAsync(parameterNames, new ISeriesStatisticsFilter[0]);
  }


  public APICCallbackHandler getParameterStatisticsExtAsync(final String[] parameterNames,
      final ISeriesStatisticsFilter[] filter)
      throws Exception {

    /*
     * Create a separate Stub for Parameter Statistics Async because Stub is not threadsafe. Multiple calls of async
     * webservices through the same stub object can affect each other, e.g. when the Configuration (Timeout) of a later
     * call is changed -> the former still running async call will also use this changed settings. Timeout set to two
     * days (48h * 60mi * 60s * 100ms). Long running operations are excepted for this call.
     */
    APICStub statisticsStub = getStub(this.soapEndPoint, 48 * 60 * 60 * 1000);

    // Factory to create filters recievable by the webservivce
    WsAdapterFactory wsAdpatFactory = new WsAdapterFactory(filter);

    // Create a session ID
    String sessionID = login();

    // Create an object for the parameters to pass to the webservice
    APICStub.GetParameterStatisticsExt getParameterStatisticsExt;
    getParameterStatisticsExt =
        (APICStub.GetParameterStatisticsExt) getTestObject(APICStub.GetParameterStatisticsExt.class);

    getParameterStatisticsExt.setSessID(sessionID);
    getParameterStatisticsExt.setParameterNames(parameterNames);

    // TODO Change to ObjectStream instead of using a wrapper class
    getParameterStatisticsExt.setParameterFilters(wsAdpatFactory.getDefaultSeriesStatFilterWsAdapter());

    ParameterStatisticCallbackHandler paramCallback = new ParameterStatisticCallbackHandler(sessionID);
    if (statisticsStub != null) {
      statisticsStub.startgetParameterStatisticsExt(getParameterStatisticsExt, paramCallback);
    }

    return paramCallback;
  }

  /**
   * @param parameterName define parameter label name
   * @return LabelInfoVO
   * @throws Exception
   */
  // ICDM-218
  public String getParameterStatisticsFile(final String parameterName) throws Exception {
    APICStub.GetParameterStatisticsFile getParameterStatisticsFile;
    ParameterStatisticsRequestType requestType = new ParameterStatisticsRequestType();

    getParameterStatisticsFile =
        (APICStub.GetParameterStatisticsFile) getTestObject(APICStub.GetParameterStatisticsFile.class);

    requestType.setParameterName(parameterName);
    requestType.setSessID(login());
    getParameterStatisticsFile.setGetParameterStatisticsFile(requestType);

    GetParameterStatisticsFileResponse response = this.stub.getParameterStatisticsFile(getParameterStatisticsFile);

    return response.getGetParameterStatisticsFileResponse().getFile();
  }

  public double getStatusForAsyncExecutionResponse(final String uuid) throws Exception {

    // Create an object for the parameters to pass to the webservice
    APICStub.GetStatusForAsyncExecution getStatusForAsyncExecution;
    getStatusForAsyncExecution =
        (APICStub.GetStatusForAsyncExecution) getTestObject(APICStub.GetStatusForAsyncExecution.class);

    getStatusForAsyncExecution.setIn(uuid);

    APICStub.GetStatusForAsyncExecutionResponse response =
        this.stub.getStatusForAsyncExecution(getStatusForAsyncExecution);

    return response.getOut();
  }

  public long loadA2LFileData(final long vcdmVersionNumber) throws Exception {

    // Create an object for the parameters to pass to the webservice
    APICStub.LoadA2LFileData a2l;
    A2LFileDataType fd = new A2LFileDataType();
    fd.setVcdmVersionNumber(vcdmVersionNumber);
    fd.setSessionID(login());
    a2l = (APICStub.LoadA2LFileData) getTestObject(APICStub.LoadA2LFileData.class);
    a2l.setLoadA2LFileData(fd);

    APICStub.LoadA2LFileDataResponse response = this.stub.loadA2LFileData(a2l);

    return response.getLoadA2LFileDataResponse().getA2LFileId();
  }

  public APICCallbackHandler loadA2LFileDataAsync(final long vcdmVersionNumber) throws Exception {

    String sessionId = login();

    // Create an object for the parameters to pass to the webservice
    APICStub.LoadA2LFileData a2l;
    A2LFileDataType fd = new A2LFileDataType();
    fd.setVcdmVersionNumber(vcdmVersionNumber);
    fd.setSessionID(sessionId);
    a2l = (APICStub.LoadA2LFileData) getTestObject(APICStub.LoadA2LFileData.class);
    a2l.setLoadA2LFileData(fd);

    A2LCallbackHandler paramCallback = new A2LCallbackHandler(sessionId);
    this.stub.startloadA2LFileData(a2l, paramCallback);

    return paramCallback;
  }

  public Attribute getAttributeByID(final long attributeId) throws Exception {
    Attribute[] attributes = getAllAttributes();

    for (Attribute attribute : attributes) {
      if (attribute.getId() == attributeId) {
        return attribute;
      }
    }

    return null;
  }

  public AttributeValue getAttributeValueByID(final long attributeId, final long valueId) throws Exception {
    ValueList[] values = getAttrValues(new long[] { attributeId });

    for (ValueList valueList : values) {
      for (AttributeValue value : valueList.getValues()) {
        if (value.getValueID() == valueId) {
          return value;
        }
      }
    }

    return null;
  }

  public boolean cancelSession(final UUID sessionId) throws Exception {
    return cancelSession(sessionId.toString());
  }

  public boolean cancelSession(final String sessionId) throws Exception {
    APICStub.CancelSession cancelSession = new APICStub.CancelSession();
    APICStub.CancelSessionType cancelSessionType = new APICStub.CancelSessionType();

    cancelSessionType.setSessionId(sessionId);
    cancelSession.setCancelSession(cancelSessionType);

    APICStub.CancelSessionResponse response = this.stub.cancelSession(cancelSession);

    return response.getCancelSessionResponse().getIsCancelled();
  }

  public GetParameterReviewResultResponseType getParameterReviewResults(final long parameterId) throws Exception {

    // There's just one returning response type when one parameter is passed. Thus the array index can be hard coded.
    return getParameterReviewResults(new long[] { parameterId })[0];
  }

  public GetParameterReviewResultResponseType[] getParameterReviewResults(final long... parameterId) throws Exception {
    // Create an object for the parameters to pass to the webservice
    APICStub.GetParameterReviewResult request;
    APICStub.GetParameterReviewResultsType requestType;

    request = (APICStub.GetParameterReviewResult) getTestObject(APICStub.GetParameterReviewResult.class);
    requestType = (APICStub.GetParameterReviewResultsType) getTestObject(APICStub.GetParameterReviewResultsType.class);

    requestType.setSessID(login());
    requestType.setParameterId(parameterId);
    request.setGetParameterReviewResult(requestType);

    APICStub.GetParameterReviewResultResponse response = this.stub.getParameterReviewResult(request);

    for (GetParameterReviewResultResponseType reviewResult : response.getGetParameterReviewResultResponse()
        .getParameterReviewResults()) {
      if (reviewResult.getReviewDetails() == null) {
        reviewResult.setReviewDetails(new ReviewResultsType[0]);
      }
    }

    return response.getGetParameterReviewResultResponse().getParameterReviewResults();
  }

  public long[] getPidcScoutResults(final Set<PidcSearchCondition> searchConditions) throws Exception {

    return getPidcScoutResultsExt(searchConditions).getPidcIds();
  }

  public PidcScoutResponseType getPidcScoutResultsExt(final Set<PidcSearchCondition> searchConditions)
      throws Exception {
    Set<PidcSearchCondition> locSearchCond =
        (searchConditions == null) ? new TreeSet<PidcSearchCondition>() : searchConditions;

    // Create an object for the parameters to pass to the webservice
    com.bosch.caltool.apic.ws.client.APICStub.PidcSearchCondition searchCondition;
    PidcSearchConditions searchCond = new PidcSearchConditions();

    searchCondition = (APICStub.PidcSearchCondition) getTestObject(APICStub.PidcSearchCondition.class);
    searchCondition.setPidcSearchCondition(searchCond);
    searchCond.setSessId(login());

    for (PidcSearchCondition entry : locSearchCond) {
      searchCond.addPidcSearchConditions(entry);
    }

    APICStub.PidcScoutResponse response = this.stub.getPidcScoutResult(searchCondition);

    if (response.getPidcScoutResponse().getPidcIds() == null) {
      response.getPidcScoutResponse().setPidcIds(new long[0]);
    }

    return response.getPidcScoutResponse();
  }

  /**
   * @param searchConditions searchConditions
   * @param searchAllVersions if <code>true</code>, the search will be done through all versions
   * @return PidcScoutVersResponseType PidcScoutVersResponseType
   * @throws Exception exception
   */
  public PidcScoutVersResponseType getPidcScoutResultForVersionExt(final Set<PidcSearchCondition> searchConditions,
      final boolean searchAllVersions)
      throws Exception {

    Set<PidcSearchCondition> locSearchCond =
        (searchConditions == null) ? new TreeSet<PidcSearchCondition>() : searchConditions;

    // Create an object for the parameters to pass to the webservice
    com.bosch.caltool.apic.ws.client.APICStub.PidcVersSearchCondition searchCondition;
    PidcSearchConditions searchCond = new PidcSearchConditions();

    searchCondition = (APICStub.PidcVersSearchCondition) getTestObject(APICStub.PidcVersSearchCondition.class);
    searchCondition.setPidcVersSearchCondition(searchCond);
    searchCond.setSessId(login());
    searchCond.setActiveVersions(!searchAllVersions);

    for (PidcSearchCondition entry : locSearchCond) {
      searchCond.addPidcSearchConditions(entry);
    }

    APICStub.PidcScoutVersResponse response = this.stub.getPidcScoutResultForVersion(searchCondition);

    if (response.getPidcScoutVersResponse().getPidcIds() == null) {
      response.getPidcScoutVersResponse().setPidcIds(new long[0]);
    }

    return response.getPidcScoutVersResponse();
  }

  public PidcAccessRights getAllPidcAccessRights() throws Exception {
    // Create an object for the parameters to pass to the webservice
    APICStub.PidcAccessRight accessRight;
    APICStub.PidcAccessRightType accessRightType = new APICStub.PidcAccessRightType();

    accessRight = (APICStub.PidcAccessRight) getTestObject(APICStub.PidcAccessRight.class);
    accessRight.setPidcAccessRight(accessRightType);
    accessRightType.setSessId(login());

    PidcAccessRightResponse response = this.stub.getPidcAccessRight(accessRight);

    return response.getPidcAccessRightResponse();
  }

  public PidcAccessRights getPidcAccessRights(final APICWebServiceClient.PidcAccessRightsTyp owner,
      final APICWebServiceClient.PidcAccessRightsTyp write, final APICWebServiceClient.PidcAccessRightsTyp grant,
      final String userName, final long... pidcIds)
      throws Exception {
    // Create an object for the parameters to pass to the webservice
    APICStub.PidcAccessRight accessRight;
    APICStub.PidcAccessRightType accessRightType = new APICStub.PidcAccessRightType();

    accessRight = (APICStub.PidcAccessRight) getTestObject(APICStub.PidcAccessRight.class);
    accessRight.setPidcAccessRight(accessRightType);
    accessRightType.setSessId(login());
    accessRightType.setShowGrant(grant.getCode());
    accessRightType.setShowWrite(write.getCode());
    accessRightType.setShowOwner(owner.getCode());
    accessRightType.setPidcId(pidcIds);
    // iCDM-1946
    accessRightType.setUserName(userName);

    PidcAccessRightResponse response = this.stub.getPidcAccessRight(accessRight);

    return response.getPidcAccessRightResponse();
  }

  public String getWebServiceVersion() throws Exception {

    APICStub.WebServiceVersionResponse response = this.stub.getWebServiceVersion();

    return response.getWebServiceVersionResponse().getVersion();
  }

  /**
   * @param pidcId pidcId
   * @param timePeriod
   * @return
   * @throws Exception
   */
  public VcdmLabelStats[] getVcdmLabelStats(final Long pidcId, final int timePeriod) throws Exception {

    APICStub.GetVcdmLabelStatReq getVcdmLabelStatReq =
        (APICStub.GetVcdmLabelStatReq) getTestObject(APICStub.GetVcdmLabelStatReq.class);
    LabelStatReq request = new LabelStatReq();
    request.setPidcID(pidcId);
    request.setTime(timePeriod);
    getVcdmLabelStatReq.setGetVcdmLabelStatReq(request);
    GetVcdmLabelStatisticsResponse response = this.stub.getVcdmLabelStatistics(getVcdmLabelStatReq);

    return response.getLabelStats();
  }

  // Create an ADBBean and provide it as the test object
  private ADBBean getTestObject(final java.lang.Class type) throws java.lang.Exception {
    return (ADBBean) type.newInstance();
  }

  private APICStub getStub(final String endpoint, final Integer timeout) {
    try {
      APICStub locStub = new APICStub(endpoint);

      Options op = locStub._getServiceClient().getOptions();
      op.setManageSession(true);
      // APIC Web service session time out 30 minutes (30mi * 60s * 1000ms)
      // Set session timeout
      op.setProperty(HTTPConstants.SO_TIMEOUT, timeout);
      // Set connection timeout
      op.setProperty(HTTPConstants.CONNECTION_TIMEOUT, timeout);
      op.setProperty(HTTPConstants.CHUNKED, "false");
      op.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
      locStub._getServiceClient().setOptions(op);

      return locStub;

    }
    catch (AxisFault e) {
      LOG.error(e.getMessage(), e);
    }

    return null;
  }

  /**
   * @return the clientTimeZone
   */
  public String getClientTimeZone() {
    return this.config.getTimezone();
  }

}
