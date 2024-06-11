package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the T_FUNCTIONS database table.
 */
@Entity
@ReadOnly
@Table(name = "T_FUNCTIONS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)

@NamedQueries(value = {
    @NamedQuery(name = TFunction.NQ_SEARCH_FUNC, query = "SELECT t FROM TFunction t where t.relevantName='Y' and t.upperName like :searchstring"),
    @NamedQuery(name = TFunction.NQ_GET_ALL_FUNC, query = "SELECT t FROM TFunction t"),
    @NamedQuery(name = TFunction.NQ_FUNC_IN_LIST, query = "SELECT t FROM TFunction t where t.upperName in :functionNamesList"),
    @NamedQuery(name = TFunction.NQ_FUNC_IN_GTT_LIST, query = "SELECT t FROM TFunction t,GttObjectName temp  where " +
        "temp.objName=t.upperName"),
    @NamedQuery(name = TFunction.NQ_GET_FUNC_BY_GTT_PARAM_NAME, query = "SELECT distinct f.id,f.name,param.name FROM TFunction f, TFunctionversion funcver" +
        ",TParameter param,GttObjectName temp  where funcver.defcharname = param.name and funcver.funcname =f.name " +
        "and param.name=temp.objName"),
    @NamedQuery(name = TFunction.NQ_GET_INVALID_FUNC, query = "SELECT temp.objName FROM TFunction f,GttObjectName temp  where " +
        "temp.objName=f.upperName"),
    @NamedQuery(name = TFunction.NQ_GET_FUNC_BY_ID, query = "SELECT fnc from TFunction fnc,GttObjectName gtt where fnc.id=gtt.id"),
    @NamedQuery(name = TFunction.NQ_GET_FUNC_BY_PARAMNAME, query = "SELECT distinct (tFunction) from TFunctionversion funcVer,TFunction tFunction where tFunction.relevantName='Y' and tFunction.upperName=funcVer.funcNameUpper and funcVer.defcharname= :paramName ") })


public class TFunction implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query - Find all functions
   */
  public static final String NQ_SEARCH_FUNC = "TFunction.getSearchFunctions";

  public static final String NQ_FUNC_IN_LIST = "TFunction.getFunctionsInList";

  public static final String NQ_FUNC_IN_GTT_LIST = "TFunction.getFunctionsInGttList";

  public static final String NQ_GET_FUNC_BY_ID = "TFunction.getFunctionsById";

  public static final String NQ_GET_FUNC_BY_PARAMNAME = "TFunction.getFunctionByParamName";

  /**
   * Named query - Find all functions
   */
  public static final String NQ_GET_ALL_FUNC = "TFunction.getAllFunctions";

  /**
   * Named query - To fetch function based on paramName list
   */
  public static final String NQ_GET_FUNC_BY_GTT_PARAM_NAME = "TFunction.getFunctionByGttParamName";

  /**
   * Named query - To fetch function list
   */
  public static final String NQ_GET_INVALID_FUNC = "TFunction.getInvalidFunc";
  @Id
  @SequenceGenerator(name = "T_FUNCTIONS_ID_GENERATOR", sequenceName = "SEQ_ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_FUNCTIONS_ID_GENERATOR")
  @Column(unique = true, nullable = false)
  private long id;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", length = 100)
  private String createdUser;

  @Column(length = 1)
  private String iscustfunc;

  @Column(length = 255)
  private String longname;

  @Column(name = "MODIFED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFED_USER", length = 100)
  private String modifiedUser;

  @Column(length = 255)
  private String name;


  @Column(name = "BIG_FUNCTION", length = 100)
  private String bigFunction;


  @Column(name = "RELEVANT_NAME", length = 1)
  private String relevantName;

  @Column(name = "FUNCTION_NAME_UPPER", length = 255)
  private String upperName;


  @Column(name = "\"VERSION\"")
  private Long version;

  public TFunction() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public String getIscustfunc() {
    return this.iscustfunc;
  }

  public void setIscustfunc(final String iscustfunc) {
    this.iscustfunc = iscustfunc;
  }

  public String getLongname() {
    return this.longname;
  }

  public void setLongname(final String longname) {
    this.longname = longname;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getBigFunction() {
    return this.bigFunction;
  }

  public void setBigFunction(final String bigFunction) {
    this.bigFunction = bigFunction;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the relevantName
   */
  public String getRelevantName() {
    return this.relevantName;
  }


  /**
   * @param relevantName the relevantName to set
   */
  public void setRelevantName(final String relevantName) {
    this.relevantName = relevantName;
  }

  /**
   * @return the upperName
   */
  public String getUpperName() {
    return this.upperName;
  }


  /**
   * @param upperName the upperName to set
   */
  public void setUpperName(final String upperName) {
    this.upperName = upperName;
  }

}