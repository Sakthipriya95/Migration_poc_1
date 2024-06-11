package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the TABV_UCP_ATTRS database table.
 */
@Entity
@NamedQueries(value = { @NamedQuery(name = TabvUcpAttr.GET_ALL_UCP_ATTR, query = "SELECT ucpa FROM TabvUcpAttr ucpa") })
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TabvUcpAttr.GET_UCP_ATTR_BY_USE_CASE_GROUP_IDS, query = "SELECT COLUMN_VALUE AS ATTR_ID FROM TABLE(F_GET_ATTRS_BY_UC_GROUP_IDS(?1))") })
@Table(name = "TABV_UCP_ATTRS")
public class TabvUcpAttr implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * GET_ALL_UCP_ATTR
   */
  public static final String GET_ALL_UCP_ATTR = "TabvUcpAttr.GET_ALL_UCP_ATTR";

  /**
   * GET_UCP_ATTR_BY_USE_CASE_GROUP_IDS
   */
  public static final String GET_UCP_ATTR_BY_USE_CASE_GROUP_IDS = "TabvUcpAttr.GET_UCP_ATTR_BY_USE_CASE_GROUP_IDS";


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "UCPA_ID", unique = true, nullable = false)
  private long ucpaId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TabvAttribute
  @ManyToOne
  @JoinColumn(name = "ATTR_ID", nullable = false)
  private TabvAttribute tabvAttribute;

  // bi-directional many-to-one association to TabvUseCase
  @ManyToOne
  @JoinColumn(name = "USE_CASE_ID", nullable = false)
  private TabvUseCase tabvUseCas;

  // bi-directional many-to-one association to TabvUseCaseSection
  @ManyToOne
  @JoinColumn(name = "SECTION_ID")
  private TabvUseCaseSection tabvUseCaseSection;

  @Column(name = "MAPPING_FLAGS")
  private Long mappingFlags;

  // bi-directional many-to-one association to TFocusMatrix
  @OneToMany(mappedBy = "tabvUcpAttr")
  private List<TFocusMatrix> TFocusMatrixs;

  public TabvUcpAttr() {}

  public long getUcpaId() {
    return this.ucpaId;
  }

  public void setUcpaId(final long ucpaId) {
    this.ucpaId = ucpaId;
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

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public TabvAttribute getTabvAttribute() {
    return this.tabvAttribute;
  }

  public void setTabvAttribute(final TabvAttribute tabvAttribute) {
    this.tabvAttribute = tabvAttribute;
  }

  public TabvUseCase getTabvUseCas() {
    return this.tabvUseCas;
  }

  public void setTabvUseCas(final TabvUseCase tabvUseCas) {
    this.tabvUseCas = tabvUseCas;
  }

  public TabvUseCaseSection getTabvUseCaseSection() {
    return this.tabvUseCaseSection;
  }

  public void setTabvUseCaseSection(final TabvUseCaseSection tabvUseCaseSection) {
    this.tabvUseCaseSection = tabvUseCaseSection;
  }

  public List<TFocusMatrix> getTFocusMatrixs() {
    return this.TFocusMatrixs;
  }

  public void setTFocusMatrixs(final List<TFocusMatrix> TFocusMatrixs) {
    this.TFocusMatrixs = TFocusMatrixs;
  }


  /**
   * @return the mappingFlags
   */
  public Long getMappingFlags() {
    return this.mappingFlags;
  }


  /**
   * @param mappingFlags the mappingFlags to set
   */
  public void setMappingFlags(final Long mappingFlags) {
    this.mappingFlags = mappingFlags;
  }


}