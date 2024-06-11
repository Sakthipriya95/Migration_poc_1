/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the T_REGION database table.
 * 
 */
@Entity
@Table(name="T_REGION")
@NamedQuery(name=TRegion.GET_ALL_REGION , query="SELECT t FROM TRegion t")
public class TRegion implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Named query to get all users from database
     * list of TREGION
     */
    public static final String GET_ALL_REGION = "TRegion.findAll";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
    @Column(name="REGION_ID")
    private long regionId;

    @Column(name="CREATED_DATE")
    private Timestamp createdDate;

    @Column(name="CREATED_USER")
    private String createdUser;

    @Column(name="MODIFIED_DATE")
    private Timestamp modifiedDate;

    @Column(name="MODIFIED_USER")
    private String modifiedUser;

    @Column(name="REGION_CODE")
    private String regionCode;

    @Column(name="REGION_NAME_ENG")
    private String regionNameEng;

    @Column(name="REGION_NAME_GER")
    private String regionNameGer;

    @Column(name="\"VERSION\"", nullable = false)
    @Version
    private Long version;

    //bi-directional many-to-one association to TWorkpackageDivisionCdl
    @OneToMany(mappedBy="tRegion",fetch = FetchType.LAZY)
    private List<TWorkpackageDivisionCdl> tWorkpackageDivisionCdls;

    public TRegion() {
    }

    public long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
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

    public String getRegionCode() {
        return this.regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionNameEng() {
        return this.regionNameEng;
    }

    public void setRegionNameEng(String regionNameEng) {
        this.regionNameEng = regionNameEng;
    }

    public String getRegionNameGer() {
        return this.regionNameGer;
    }

    public void setRegionNameGer(String regionNameGer) {
        this.regionNameGer = regionNameGer;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<TWorkpackageDivisionCdl> getTWorkpackageDivisionCdls() {
        return this.tWorkpackageDivisionCdls;
    }

    public void setTWorkpackageDivisionCdls(List<TWorkpackageDivisionCdl> tWorkpackageDivisionCdls) {
        this.tWorkpackageDivisionCdls = tWorkpackageDivisionCdls;
    }

    public TWorkpackageDivisionCdl addTWorkpackageDivisionCdl(TWorkpackageDivisionCdl tWorkpackageDivisionCdl) {
        getTWorkpackageDivisionCdls().add(tWorkpackageDivisionCdl);
        tWorkpackageDivisionCdl.setTRegion(this);

        return tWorkpackageDivisionCdl;
    }

    public TWorkpackageDivisionCdl removeTWorkpackageDivisionCdl(TWorkpackageDivisionCdl TWorkpackageDivisionCdl) {
        getTWorkpackageDivisionCdls().remove(TWorkpackageDivisionCdl);
        

        return TWorkpackageDivisionCdl;
    }
    
}


