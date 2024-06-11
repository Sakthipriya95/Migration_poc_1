package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Test for PiA2lResponsibilityServiceClient
 *
 * @author pdh2cob
 */
public class A2lResponsibilityServiceClientTest extends AbstractRestClientTest {

  /**
   *
   */
  private static final String RESPONSE_SHOULD_NOT_BE_NULL = "Response should not be null";

  /**
   *
   */
  private static final String RESP_TYPE = "Resp Type";

  /**
   *
   */
  private static final String LAST_NAME_IS_EQUAL = "LastName is equal";

  /**
   *
   */
  private static final String FIRST_NAME_IS_EQUAL = "FirstName is equal";

  /**
   *
   */
  private static final String DEPARTMENT_IS_EQUAL = "Department is equal";

  /**
   *
   */
  private static final String CREATED_DATE_IS_NOT_NULL = "CreatedDate is not null";

  /**
   *
   */
  private static final String JUNIT_FNAME = "junit_fname";

  /**
   *
   */
  private static final String PIDC_ID_IS_EQUAL = "PidcId is equal";

  /**
   *
   */
  private static final String ALIAS_NAME_IS_EQUAL = "AliasName is equal ";

  private static final Long A2L_RESP_ID_BOSCH_TYPE = 1558902027L;

  private static final Long INVALID_A2L_RESP_ID = -10L;

  private static final Long PIDC_ID = 781737967L;


  /**
   * Test method for {@link A2lResponsibilityServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException - Exception from service
   */
  @Test
  public void testGetBoschResp() throws ApicWebServiceException {
    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();
    A2lResponsibility ret = servClient.get(A2L_RESP_ID_BOSCH_TYPE);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, ret);
    validateA2lResp(ret);
  }

  /**
   * Test method for {@link A2lResponsibilityServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException - Exception from service
   */
  @Test
  public void testGetCustResp() throws ApicWebServiceException {
    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();

    // only last name
    // 1558904177 HONDA_EP _RESP__HONDA_EP
    A2lResponsibility ret = servClient.get(1558904177L);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));

    assertEquals(RESP_TYPE, "C", ret.getRespType());
    assertEquals("name", "HONDA_EP", ret.getName());
    assertEquals(FIRST_NAME_IS_EQUAL, null, ret.getLFirstName());
    assertEquals(DEPARTMENT_IS_EQUAL, null, ret.getLDepartment());
    assertEquals(LAST_NAME_IS_EQUAL, "HONDA_EP", ret.getLLastName());
    assertNull("UserId is null", ret.getUserId());
    assertEquals(ALIAS_NAME_IS_EQUAL, "_RESP__HONDA_EP", ret.getAliasName());

    // first name, last name ,department present
    // 1580562348 Klaus Maier CustCal Maier,Klaus(CustCal)
    ret = servClient.get(1580562348L);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));

    assertEquals(RESP_TYPE, "C", ret.getRespType());
    assertEquals("name", "Maier, Klaus (CustCal)", ret.getName());
    assertEquals(FIRST_NAME_IS_EQUAL, "Klaus", ret.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, "Maier", ret.getLLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, "CustCal", ret.getLDepartment());
    assertEquals(ALIAS_NAME_IS_EQUAL, "Maier,Klaus(CustCal)", ret.getAliasName());

    // last name ,department present
    // 1748539377 PEA AUDI _RESP__AU_EF_INT_PEA
    ret = servClient.get(1748539377L);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));

    assertEquals(RESP_TYPE, "C", ret.getRespType());
    assertEquals("name", "PEA (AUDI)", ret.getName());
    assertEquals(FIRST_NAME_IS_EQUAL, null, ret.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, "PEA", ret.getLLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, "AUDI", ret.getLDepartment());
    assertEquals(ALIAS_NAME_IS_EQUAL, "_RESP__AU_EF_INT_PEA", ret.getAliasName());

    // first name, last name present
    // 1748540177 Alexander Winkler Winkler,Alexander
    ret = servClient.get(1748540177L);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));

    assertEquals(RESP_TYPE, "C", ret.getRespType());
    assertEquals("name", "Winkler, Alexander", ret.getName());
    assertEquals(FIRST_NAME_IS_EQUAL, "Alexander", ret.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, "Winkler", ret.getLLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, null, ret.getLDepartment());
    assertEquals(ALIAS_NAME_IS_EQUAL, "Winkler,Alexander", ret.getAliasName());
  }

  /**
   * Test method for {@link A2lResponsibilityServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException - Exception from service
   */
  @Test
  public void testGetOtherResp() throws ApicWebServiceException {
    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();

    // 1568134877 762 EA832_IAV_FSPGST IAV _RESP__EA832_IAV_FSPGST
    A2lResponsibility ret = servClient.get(1568134877L);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (ret == null));

    assertEquals(RESP_TYPE, "O", ret.getRespType());
    assertEquals("name", "EA832_IAV_FSPGST (IAV)", ret.getName());
    assertEquals(FIRST_NAME_IS_EQUAL, null, ret.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, "EA832_IAV_FSPGST", ret.getLLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, "IAV", ret.getLDepartment());
    assertNull("UserId is null", ret.getUserId());
    assertEquals(ALIAS_NAME_IS_EQUAL, "_RESP__EA832_IAV_FSPGST", ret.getAliasName());
    assertEquals("CreatedUser is equal", "HEF2FE", ret.getCreatedUser());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, ret.getCreatedDate());
  }

  /**
   * Test method for invalid input {@link A2lResponsibilityServiceClient#get(Long)}
   *
   * @throws ApicWebServiceException - Exception from service
   */
  @Test
  public void testGetNegative() throws ApicWebServiceException {
    this.thrown.expect(ApicWebServiceException.class);
    this.thrown.expectMessage("A2L Responsibility Definition with ID '" + INVALID_A2L_RESP_ID + "' not found");
    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();
    servClient.get(INVALID_A2L_RESP_ID);
    fail("Expected Exception not thrown");
  }

  /**
   * Test method for invalid input {@link A2lResponsibilityServiceClient#getByPidc(Long)}
   *
   * @throws ApicWebServiceException - Exception from service
   */
  @Test
  public void testGetByPidc() throws ApicWebServiceException {
    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();
    A2lResponsibilityModel model = servClient.getByPidc(PIDC_ID);
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, model == null);
    validateA2lRespModel(model);
  }

  /**
   * Method to validate A2lResponsibilityModel
   */
  private void validateA2lRespModel(final A2lResponsibilityModel model) {
    Map<Long, A2lResponsibility> a2lRespMap = model.getA2lResponsibilityMap();
    Map<Long, User> userMap = model.getUserMap();
    assertFalse("A2lResponsibility map should not be empty", a2lRespMap.isEmpty());
    assertFalse("User map should not be empty", userMap.isEmpty());
    validateA2lResp(a2lRespMap.get(A2L_RESP_ID_BOSCH_TYPE));
    validateUser(userMap.get(a2lRespMap.get(A2L_RESP_ID_BOSCH_TYPE).getUserId()));
  }


  /**
   * Validate A2lResponsibility
   */
  private void validateA2lResp(final A2lResponsibility obj) {
    assertEquals(PIDC_ID_IS_EQUAL, Long.valueOf(781737967), obj.getProjectId());
    assertEquals("name", "Takimoto, Yuta (PS-EC/ECH1-JP)", obj.getName());
    assertEquals(LAST_NAME_IS_EQUAL, null, obj.getLLastName());
    assertEquals(FIRST_NAME_IS_EQUAL, null, obj.getLFirstName());
    assertEquals(DEPARTMENT_IS_EQUAL, "_RESP__Takimoto__Yuta", obj.getLDepartment());
    assertEquals("UserId is equal", Long.valueOf(916798466), obj.getUserId());
    assertEquals(ALIAS_NAME_IS_EQUAL, "_RESP__Takimoto__Yuta", obj.getAliasName());
    assertEquals("CreatedUser is equal", "HEF2FE", obj.getCreatedUser());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, obj.getCreatedDate());
    assertEquals("Resp Type is R", "R", obj.getRespType());
  }

  /**
   * Validate User
   */
  private void validateUser(final User user) {
    assertEquals("User Name is equal", "TMY2YH", user.getName());
    assertEquals("First Name is equal", "Yuta", user.getFirstName());
    assertEquals("Last Name is equal", "Takimoto", user.getLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, "PS-EC/ECH1-JP", user.getDepartment());
    assertEquals("Created User is equal", "HAM5SI", user.getCreatedUser());
    assertNotNull(CREATED_DATE_IS_NOT_NULL, user.getCreatedDate());
  }

  /**
   * @throws ApicWebServiceException web service error create and update for others and customers scenario
   */
  @Test
  public void createUpdate() throws ApicWebServiceException {
    A2lResponsibility input = new A2lResponsibility();
    input.setLFirstName(JUNIT_FNAME + getRunId());
    input.setLLastName("junit_lname");
    input.setLDepartment("junit_dept");
    input.setRespType(WpRespType.OTHERS.getCode());
    input.setAliasName(input.getLFirstName() + input.getLLastName() + input.getLDepartment());
    input.setProjectId(PIDC_ID);
    input.setDeleted(false);
    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();
    // invoke create method
    A2lResponsibility customerandotherCreate = servClient.create(input);
    assertFalse("Response should not be null ", (customerandotherCreate == null));
    assertEquals(FIRST_NAME_IS_EQUAL, JUNIT_FNAME + getRunId(), customerandotherCreate.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, "junit_lname", customerandotherCreate.getLLastName());
    assertEquals("Department", "junit_dept", customerandotherCreate.getLDepartment());
    assertEquals("Resp Type is equal", WpRespType.OTHERS.getCode(), customerandotherCreate.getRespType());
    assertEquals(ALIAS_NAME_IS_EQUAL, input.getLFirstName() + input.getLLastName() + input.getLDepartment(),
        customerandotherCreate.getAliasName());
    assertEquals(PIDC_ID_IS_EQUAL, PIDC_ID, customerandotherCreate.getProjectId());
    customerandotherCreate
        .setAliasName("junit_" + input.getLFirstName() + input.getLLastName() + input.getLDepartment());
    // invoke update method
    List<A2lResponsibility> respList = new ArrayList<>();
    respList.add(customerandotherCreate);
    A2lResponsibility customerandotherUpdate = servClient.update(respList).get(customerandotherCreate.getId());
    assertFalse(RESPONSE_SHOULD_NOT_BE_NULL, (customerandotherUpdate == null));
    assertEquals(ALIAS_NAME_IS_EQUAL, "junit_" + input.getLFirstName() + input.getLLastName() + input.getLDepartment(),
        customerandotherUpdate.getAliasName());
  }

  /**
   * Test method for valid input {@link A2lResponsibilityServiceClient#maintainA2lResp(A2lRespMaintenanceData)} Flow
   * Create Data Output: Data is Correct
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testMaintainA2lRespFlowCreate() throws ApicWebServiceException {
    A2lRespMaintenanceData a2lRespMaintenanceData = new A2lRespMaintenanceData();

    String firstName = JUNIT_FNAME + getRunId();
    String lastName = "junit_Nam_dev_id" + getRunId();
    String departmentName = "junit_BSV42-EA";
    String aliasName = firstName + lastName + departmentName;

    A2lResponsibility a2lResponsibility = new A2lResponsibility();
    a2lResponsibility.setLFirstName(firstName);
    a2lResponsibility.setLLastName(lastName);
    a2lResponsibility.setLDepartment(departmentName);
    a2lResponsibility.setRespType(WpRespType.OTHERS.getCode());
    a2lResponsibility.setAliasName(aliasName);
    a2lResponsibility.setProjectId(PIDC_ID);
    a2lResponsibility.setDeleted(false);

    A2lRespBoschGroupUser a2lRespBoschGroupUser = new A2lRespBoschGroupUser();
    a2lRespBoschGroupUser.setA2lRespId(26557343178L); // RB_TestDept
    a2lRespBoschGroupUser.setDescription(JUNIT_FNAME);
    a2lRespBoschGroupUser.setUserId(778745966L); // ABG2KOR

    List<A2lRespBoschGroupUser> groupUsers = new ArrayList<>(1);

    groupUsers.add(a2lRespBoschGroupUser);
    a2lRespMaintenanceData.setA2lRespToCreate(a2lResponsibility);
    a2lRespMaintenanceData.setBoschUsrsCreationList(groupUsers);

    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();
    // invoke create method
    A2lRespMaintenanceData actualResp = servClient.maintainA2lResp(a2lRespMaintenanceData);

    // then create
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, actualResp);

    A2lResponsibility customerandotherCreate = actualResp.getA2lRespToCreate();

    assertNotNull("Response For Creator should not be null", customerandotherCreate);

    assertEquals(FIRST_NAME_IS_EQUAL, firstName, customerandotherCreate.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, lastName, customerandotherCreate.getLLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, departmentName, customerandotherCreate.getLDepartment());

    assertEquals(ALIAS_NAME_IS_EQUAL,
        a2lResponsibility.getLFirstName() + a2lResponsibility.getLLastName() + a2lResponsibility.getLDepartment(),
        customerandotherCreate.getAliasName());
    assertEquals(PIDC_ID_IS_EQUAL, PIDC_ID, customerandotherCreate.getProjectId());
    assertNotNull("Group Bosch is not null When creating data", actualResp.getBoschUsrsCreationList());
    assertTrue("Group Bosch is not empty When creating data", !actualResp.getBoschUsrsCreationList().isEmpty());

  }

  /**
   * Test method for valid input {@link A2lResponsibilityServiceClient#maintainA2lResp(A2lRespMaintenanceData)} Flow
   * Update Data Output: Data is Correct
   *
   * @throws ApicWebServiceException
   */
  @Test
  public void testMaintainA2lRespFlowUpdate() throws ApicWebServiceException {

    A2lResponsibilityServiceClient servClient = new A2lResponsibilityServiceClient();

    A2lRespMaintenanceData a2lRespMaintenanceData = new A2lRespMaintenanceData();

    String firstName = "junit fname/" + getRunId();
    String lastName = "junit Nam_dev/" + getRunId();
    String departmentName = "junit BSV42-EA/";
    String aliasName = firstName + lastName + departmentName;

    A2lResponsibility a2lResponsibility = new A2lResponsibility();
    a2lResponsibility.setLFirstName(firstName);
    a2lResponsibility.setLLastName(lastName);
    a2lResponsibility.setLDepartment(departmentName);
    a2lResponsibility.setRespType(WpRespType.OTHERS.getCode());
    a2lResponsibility.setAliasName(aliasName);
    a2lResponsibility.setProjectId(PIDC_ID);
    a2lResponsibility.setDeleted(false);


    // invoke method create
    A2lResponsibility response = servClient.create(a2lResponsibility);

    A2lResponsibilityModel model = servClient.getByPidc(27630732429L);
    assertNotNull(RESPONSE_SHOULD_NOT_BE_NULL, model);

    Map<Long, Map<Long, A2lRespBoschGroupUser>> map2BschGroupUsr = model.getA2lBoschGrpUserMap();
    assertNotNull("Map Bosch Group User And ProjectId should not be null", map2BschGroupUsr);
    assertNotNull("Map Bosch Group User And ProjectId should not be empty", !map2BschGroupUsr.isEmpty());
    // get first
    Long projectId = map2BschGroupUsr.keySet().iterator().next();
    assertNotNull("Project Id should not be null", projectId);
    Map<Long, A2lRespBoschGroupUser> mapA2lResponsiibityAndBschGroupUsr = map2BschGroupUsr.get(projectId);
    assertNotNull("Map Bosch Group User and A2lResponsiibity should not be null", mapA2lResponsiibityAndBschGroupUsr);
    assertNotNull("Map Bosch Group User and A2lResponsiibity should not be empty",
        !mapA2lResponsiibityAndBschGroupUsr.isEmpty());

    // get first
    Long a2lRespId = mapA2lResponsiibityAndBschGroupUsr.keySet().iterator().next();
    assertNotNull("A2lRespId should not be null", a2lRespId);

    A2lRespBoschGroupUser bschGroupUsr = mapA2lResponsiibityAndBschGroupUsr.get(a2lRespId);

    assertNotNull("A2lRespBoschGroupUser should not be null", bschGroupUsr);
    assertNotNull("A2lRespBoschGroupUser.getA2lRespId() should not be null", bschGroupUsr.getA2lRespId());
    assertNotNull("A2lRespBoschGroupUser.getId() should not be null", bschGroupUsr.getId());
    assertNotNull("A2lRespBoschGroupUser.getUserId() should not be null", bschGroupUsr.getUserId());
    assertNotNull("A2lRespBoschGroupUser.getVersion() should not be null", bschGroupUsr.getVersion());
    assertNotNull("A2lRespBoschGroupUser.getName() should not be null", bschGroupUsr.getName());


    // add new Member
    A2lRespBoschGroupUser newMember = new A2lRespBoschGroupUser();
    newMember.setA2lRespId(bschGroupUsr.getA2lRespId());
    newMember.setDescription("new Member");
    newMember.setUserId(bschGroupUsr.getUserId());
    newMember.setId(bschGroupUsr.getId());
    newMember.setName(bschGroupUsr.getName());
    newMember.setVersion(bschGroupUsr.getVersion());
    a2lRespMaintenanceData.setA2lRespToUpdate(response);

    List<A2lRespBoschGroupUser> boschUsrsCreationList = new ArrayList<>();
    boschUsrsCreationList.add(newMember);
    a2lRespMaintenanceData.setBoschUsrsCreationList(boschUsrsCreationList);

    // delete user
    List<A2lRespBoschGroupUser> boschUsrsDeletionList = new ArrayList<>();

    A2lRespBoschGroupUser removeUser = new A2lRespBoschGroupUser();
    removeUser.setA2lRespId(bschGroupUsr.getA2lRespId());
    removeUser.setDescription(JUNIT_FNAME);
    removeUser.setUserId(bschGroupUsr.getUserId());
    removeUser.setId(bschGroupUsr.getId());
    removeUser.setName(bschGroupUsr.getName());
    removeUser.setVersion(bschGroupUsr.getVersion());
    boschUsrsDeletionList.add(removeUser);
    a2lRespMaintenanceData.setBoschUsrsDeletionList(boschUsrsDeletionList);

    // invoke Update

    A2lRespMaintenanceData actualRespFlowUpdate = servClient.maintainA2lResp(a2lRespMaintenanceData);

    A2lResponsibility customerandotherUpdate = actualRespFlowUpdate.getA2lRespToUpdate();

    assertNotNull("Responde For Update should not be null", customerandotherUpdate);

    assertEquals(FIRST_NAME_IS_EQUAL, firstName, customerandotherUpdate.getLFirstName());
    assertEquals(LAST_NAME_IS_EQUAL, lastName, customerandotherUpdate.getLLastName());
    assertEquals(DEPARTMENT_IS_EQUAL, departmentName, customerandotherUpdate.getLDepartment());

    assertEquals(ALIAS_NAME_IS_EQUAL,
        a2lResponsibility.getLFirstName() + a2lResponsibility.getLLastName() + a2lResponsibility.getLDepartment(),
        customerandotherUpdate.getAliasName());
    assertEquals(PIDC_ID_IS_EQUAL, PIDC_ID, customerandotherUpdate.getProjectId());

    assertNotNull("Group Bosch is not null When adding new Memember", actualRespFlowUpdate.getBoschUsrsCreationList());
    assertTrue("Deletion Group Bosch is not empty", !a2lRespMaintenanceData.getBoschUsrsDeletionList().isEmpty());
  }
}
