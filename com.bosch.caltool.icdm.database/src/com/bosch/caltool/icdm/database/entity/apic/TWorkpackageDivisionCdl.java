/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_WORKPACKAGE_DIVISION_CDL database table.
 * 
 */
@Entity
@Table(name="T_WORKPACKAGE_DIVISION_CDL")
@NamedQueries(value = {
  @NamedQuery(name=TWorkpackageDivisionCdl.GET_ALL_WPDIVCDL, query="SELECT t FROM TWorkpackageDivisionCdl t"),
  @NamedQuery(name=TWorkpackageDivisionCdl.GET_ALL_WPDIVCDL_BY_WPDIVID, query="SELECT cdl FROM TWorkpackageDivisionCdl cdl where cdl.tWorkpackageDivision.wpDivId=:wpDivId")})

public class TWorkpackageDivisionCdl implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Named query to get all users from database
     * list of TWorkpackageDivisionCdl
     */
    public static final String GET_ALL_WPDIVCDL = "TWorkpackageDivisionCdl.findAll";
    /**
     * Named query to get all users from database
     * list of TWorkpackageDivisionCdl
     */
    public static final String GET_ALL_WPDIVCDL_BY_WPDIVID = "TWorkpackageDivisionCdl.findByWpDivId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
    @Column(name="WP_DIV_CDL_ID")
    private long wpDivCdlId;

    @Column(name="CREATED_DATE")
    private Timestamp createdDate;

    @Column(name="CREATED_USER")
    private String createdUser;

    @Column(name="MODIFIED_DATE")
    private Timestamp modifiedDate;

    @Column(name="MODIFIED_USER")
    private String modifiedUser;

    @Column(name="\"VERSION\"", nullable = false)
    @Version
    private Long version;

    //bi-directional many-to-one association to TabvApicUser
    @ManyToOne
    @JoinColumn(name="USER_ID")
    private TabvApicUser tabvApicUser;

    //bi-directional many-to-one association to TRegion
    @ManyToOne
    @JoinColumn(name="REGION_ID")
    private TRegion tRegion;

    //bi-directional many-to-one association to TWorkpackageDivision
    @ManyToOne
    @JoinColumn(name="WP_DIV_ID")
    private TWorkpackageDivision tWorkpackageDivision;

    public TWorkpackageDivisionCdl() {
    }

    public long getWpDivCdlId() {
        return this.wpDivCdlId;
    }

    public void setWpDivCdlId(long wpDivCdlId) {
        this.wpDivCdlId = wpDivCdlId;
    }

    public Timestamp getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedUser() {
        return this.createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public Timestamp getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedUser() {
        return this.modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public TabvApicUser getTabvApicUser() {
        return this.tabvApicUser;
    }

    public void setTabvApicUser(TabvApicUser tabvApicUser) {
        this.tabvApicUser = tabvApicUser;
    }

    public TRegion getTRegion() {
        return this.tRegion;
    }

    public void setTRegion(TRegion tRegion) {
        this.tRegion = tRegion;
    }

    public TWorkpackageDivision getTWorkpackageDivision() {
        return this.tWorkpackageDivision;
    }

    public void setTWorkpackageDivision(TWorkpackageDivision tWorkpackageDivision) {
        this.tWorkpackageDivision = tWorkpackageDivision;
    }

}