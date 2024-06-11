package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.PrivateOwned;


/**
 * The persistent class for the T_PIDC_RM_DEFINITION database table.
 */
@OptimisticLocking(cascade = true)
@Entity
@Table(name = "T_PIDC_RM_DEFINITION")
@NamedQueries({ @NamedQuery(name = TPidcRmDefinition.NQ_GET_PIDC_RM_DEF, query = "SELECT t FROM TPidcRmDefinition t") })
public class TPidcRmDefinition implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String NQ_GET_PIDC_RM_DEF = "Get RM defintion";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "PIDC_RM_ID", unique = true, nullable = false)
  private long pidcRmId;


  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "CREATED_USER")
  private String createdUser;

  @Column(name = "IS_VARIANT")
  private String isVariant;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER")
  private String modifiedUser;


  @Column(name = "RM_DESC_ENG")
  private String rmDescEng;

  @Column(name = "RM_DESC_GER")
  private String rmDescGer;

  @Column(name = "RM_NAME_ENG")
  private String rmNameEng;

  @Column(name = "RM_NAME_GER")
  private String rmNameGer;

  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private long version;

  // bi-directional many-to-one association to TabvProjectSubVariant
  @OneToMany(mappedBy = "TPidcRmDefinition")
  private Set<TabvProjectSubVariant> tabvProjectSubVariants;

  // bi-directional many-to-one association to TabvProjectVariant
  @OneToMany(mappedBy = "tPidcRmDefinition")
  private Set<TabvProjectVariant> tabvProjectVariants;


  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_VERS_ID")
  private TPidcVersion TPidcVersion;

  // bi-directional many-to-one association to TPidcRmProjectCharacter
  @PrivateOwned
  @OneToMany(mappedBy = "TPidcRmDefinition")
  private Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters;

  public TPidcRmDefinition() {}

  public long getPidcRmId() {
    return this.pidcRmId;
  }

  public void setPidcRmId(final long pidcRmId) {
    this.pidcRmId = pidcRmId;
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

  public String getIsVariant() {
    return this.isVariant;
  }

  public void setIsVariant(final String isVariant) {
    this.isVariant = isVariant;
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


  public String getRmDescEng() {
    return this.rmDescEng;
  }

  public void setRmDescEng(final String rmDescEng) {
    this.rmDescEng = rmDescEng;
  }

  public String getRmDescGer() {
    return this.rmDescGer;
  }

  public void setRmDescGer(final String rmDescGer) {
    this.rmDescGer = rmDescGer;
  }

  public String getRmNameEng() {
    return this.rmNameEng;
  }

  public void setRmNameEng(final String rmNameEng) {
    this.rmNameEng = rmNameEng;
  }

  public String getRmNameGer() {
    return this.rmNameGer;
  }

  public void setRmNameGer(final String rmNameGer) {
    this.rmNameGer = rmNameGer;
  }

  public long getVersion() {
    return this.version;
  }

  public void setVersion(final long version) {
    this.version = version;
  }

  public Set<TabvProjectSubVariant> getTabvProjectSubVariants() {
    return this.tabvProjectSubVariants;
  }

  public void setTabvProjectSubVariants(final Set<TabvProjectSubVariant> tabvProjectSubVariants) {
    this.tabvProjectSubVariants = tabvProjectSubVariants;
  }

  public TabvProjectSubVariant addTabvProjectSubVariant(final TabvProjectSubVariant tabvProjectSubVariant) {
    getTabvProjectSubVariants().add(tabvProjectSubVariant);
    tabvProjectSubVariant.setTPidcRmDefinition(this);

    return tabvProjectSubVariant;
  }

  public TabvProjectSubVariant removeTabvProjectSubVariant(final TabvProjectSubVariant tabvProjectSubVariant) {
    getTabvProjectSubVariants().remove(tabvProjectSubVariant);
    tabvProjectSubVariant.setTPidcRmDefinition(null);

    return tabvProjectSubVariant;
  }

  public Set<TabvProjectVariant> getTabvProjectVariants() {
    return this.tabvProjectVariants;
  }

  public void setTabvProjectVariants(final Set<TabvProjectVariant> tabvProjectVariants) {
    this.tabvProjectVariants = tabvProjectVariants;
  }


  /**
   * @return the tPidcVersion
   */
  public TPidcVersion getTPidcVersion() {
    return this.TPidcVersion;
  }


  /**
   * @param tPidcVersion the tPidcVersion to set
   */
  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.TPidcVersion = tPidcVersion;
  }

  public TabvProjectVariant addTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    getTabvProjectVariants().add(tabvProjectVariant);
    tabvProjectVariant.setTPidcRmDefinition(this);

    return tabvProjectVariant;
  }

  public TabvProjectVariant removeTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    getTabvProjectVariants().remove(tabvProjectVariant);
    tabvProjectVariant.setTPidcRmDefinition(null);

    return tabvProjectVariant;
  }

  public Set<TPidcRmProjectCharacter> getTPidcRmProjectCharacters() {
    return this.TPidcRmProjectCharacters;
  }

  public void setTPidcRmProjectCharacters(final Set<TPidcRmProjectCharacter> TPidcRmProjectCharacters) {
    this.TPidcRmProjectCharacters = TPidcRmProjectCharacters;
  }

  public TPidcRmProjectCharacter addTPidcRmProjectCharacter(final TPidcRmProjectCharacter TPidcRmProjectCharacter) {
    getTPidcRmProjectCharacters().add(TPidcRmProjectCharacter);
    TPidcRmProjectCharacter.setTPidcRmDefinition(this);

    return TPidcRmProjectCharacter;
  }

  public TPidcRmProjectCharacter removeTPidcRmProjectCharacter(final TPidcRmProjectCharacter TPidcRmProjectCharacter) {
    getTPidcRmProjectCharacters().remove(TPidcRmProjectCharacter);
    TPidcRmProjectCharacter.setTPidcRmDefinition(null);

    return TPidcRmProjectCharacter;
  }

}