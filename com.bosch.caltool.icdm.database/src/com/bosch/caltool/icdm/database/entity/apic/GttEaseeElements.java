package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the GTT_EASEE_ELEMENTS database table.
 */
@Entity
@Table(name = "GTT_EASEE_ELEMENTS")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedQueries(value = {
    @NamedQuery(name = GttEaseeElements.NQ_GET_GTT_EASEE_ELTS, query = "  " +
        "SELECT easee FROM GttEaseeElements easee ") })
public class GttEaseeElements implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named query to get Easee elements
   */
  public static final String NQ_GET_GTT_EASEE_ELTS = "GttEaseeElements.getEaseeElements";

  @Id
  private long id;

  @Column(name = "ELEMENT_CLASS")
  private String elementClass;

  @Column(name = "ELEMENT_NAME")
  private String elementName;

  @Column(name = "ELEMENT_VARIANT")
  private String elementVariant;

  @Column(name = "ELEMENT_REVISION")
  private long elementRevision;

  @Column(name = "CONTAINER_ID")
  private long containerId;

  public GttEaseeElements() {}

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }


  /**
   * @return the elementClass
   */
  public String getElementClass() {
    return this.elementClass;
  }


  /**
   * @param elementClass the elementClass to set
   */
  public void setElementClass(final String elementClass) {
    this.elementClass = elementClass;
  }


  /**
   * @return the elementName
   */
  public String getElementName() {
    return this.elementName;
  }


  /**
   * @param elementName the elementName to set
   */
  public void setElementName(final String elementName) {
    this.elementName = elementName;
  }


  /**
   * @return the elementVariant
   */
  public String getElementVariant() {
    return this.elementVariant;
  }


  /**
   * @param elementVariant the elementVariant to set
   */
  public void setElementVariant(final String elementVariant) {
    this.elementVariant = elementVariant;
  }


  /**
   * @return the elementRevision
   */
  public long getElementRevision() {
    return this.elementRevision;
  }


  /**
   * @param elementRevision the elementRevision to set
   */
  public void setElementRevision(final long elementRevision) {
    this.elementRevision = elementRevision;
  }


  /**
   * @return the containerId
   */
  public long getContainerId() {
    return this.containerId;
  }


  /**
   * @param containerId the containerId to set
   */
  public void setContainerId(final long containerId) {
    this.containerId = containerId;
  }

}