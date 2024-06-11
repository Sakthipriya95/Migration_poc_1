/**
 * 
 */
package com.bosch.caltool.icdm.jpa;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.comppkg.jpa.bo.CPDataProvider;

/**
 * Class: CDMDataProvider This class holds the DataProviders of different topics (APIC, SSD, ICDM...)
 * 
 * @author adn1cob
 */
public enum CDMDataProvider {
  /**
   * Single instance
   */
  INSTANCE;

  /**
   * Instance for APIC Data
   */
  private ApicDataProvider apicDataProvider;

  /**
   * Instance for A2LDataProvider
   */
  private A2LDataProvider a2lDataProvider;

  /**
   * Instance of CDR Data provider
   */
  private CDRDataProvider cdrDataProvider;
  /**
   * Instance of CP Data provider
   */
  private CPDataProvider cpDataProvider;

  /**
   * GetInstance()
   * 
   * @return thte instance of CDMDataProvider class.
   */
  public static CDMDataProvider getInstance() {
    return INSTANCE;
  }


  /**
   * @param apicDataProvider the apicDataProvider to set
   */
  protected void setApicDataProvider(final ApicDataProvider apicDataProvider) {
    this.apicDataProvider = apicDataProvider;
  }

  /**
   * @return the apicDataProvider
   */
  public ApicDataProvider getApicDataProvider() {
    return this.apicDataProvider;
  }

  /**
   * @return the apicUser
   */
  public ApicUser getApicUser() {
    return this.apicDataProvider.getCurrentUser();
  }


  /**
   * @return the a2lDataProvider
   */
  public A2LDataProvider getA2lDataProvider() {
    synchronized (IcdmJpaConstants.A2L_DATA_SYNC_LOCK) {
      return this.a2lDataProvider;
    }
  }


  /**
   * @param a2lDataProvider the a2lDataProvider to set
   */
  protected void setA2lDataProvider(final A2LDataProvider a2lDataProvider) {
    this.a2lDataProvider = a2lDataProvider;
  }


  /**
   * @param cdrDataProvider the cdrDataProvider to set
   */
  public void setCdrDataProvider(final CDRDataProvider cdrDataProvider) {
    this.cdrDataProvider = cdrDataProvider;
  }


  /**
   * @return the cdrDataProvider
   */
  public CDRDataProvider getCdrDataProvider() {
    return this.cdrDataProvider;
  }


  /**
   * @return the cpDataProvider
   */
  public CPDataProvider getCpDataProvider() {
    synchronized (IcdmJpaConstants.CP_DATA_SYNC_LOCK) {
      return this.cpDataProvider;
    }
  }


  /**
   * @param cpDataProvider the cpDataProvider to set
   */
  public void setCpDataProvider(final CPDataProvider cpDataProvider) {
    this.cpDataProvider = cpDataProvider;
    //ICDM-2066
    synchronized (IcdmJpaConstants.CP_DATA_SYNC_LOCK) {
      IcdmJpaConstants.CP_DATA_SYNC_LOCK.notifyAll();
    }
  }

}
