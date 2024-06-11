package com.bosch.caltool.icdm.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * Editor input class for A2L File contents Editor class <code>A2LContentsEditor</code>.
 *
 * @author adn1cob
 */
public class A2LContentsEditorInput implements IEditorInput, ILinkSelectionProvider {

  /**
   * The A2LFile instance for the input class.
   */
  private final PidcA2LBO pidcA2lBo;

  /** The a 2 l editor data handler. */
  private A2LFileInfoBO a2lFileInfoBO;

  private A2LEditorDataHandler dataHandler;

  private final A2LWPInfoBO a2lWPInfoBO;

  /**
   * Constructor that initialises the class.
   *
   * @param pidcA2lBo the pidc A 2 l bo
   * @param a2lFileInfoBO the a 2 l editor model BO
   * @param a2lWpInfo pidc wp info bo
   */
  public A2LContentsEditorInput(final PidcA2LBO pidcA2lBo, final A2LFileInfoBO a2lFileInfoBO,
      final A2LWPInfoBO a2lWpInfo) {
    this.pidcA2lBo = pidcA2lBo;
    this.a2lFileInfoBO = a2lFileInfoBO;
    this.a2lWPInfoBO = a2lWpInfo;
    if ((this.a2lFileInfoBO != null) && (this.a2lWPInfoBO != null)) {
      this.dataHandler = new A2LEditorDataHandler(this.a2lWPInfoBO);
    }
  }


  /**
   * @return the a2lWPInfoBO
   */
  public A2LWPInfoBO getA2lWPInfoBO() {
    return this.a2lWPInfoBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.pidcA2lBo.getA2LFileName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return "File Name : " + getName() + "\nProject ID Card : " + this.pidcA2lBo.getPidcVersion().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.pidcA2lBo.getA2lFileBO().getA2LFileID().intValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    A2LContentsEditorInput other = (A2LContentsEditorInput) obj;
    return !((this.pidcA2lBo.getA2lFileBO().getA2LFileID().longValue() != other.getPidcA2lBO().getA2lFileBO()
        .getA2LFileID().longValue()) || !this.pidcA2lBo.getPidcVersion().equals(other.getPidcA2lBO().getPidcVersion()));
  }

  /**
   * Gets the a 2 l file contents.
   *
   * @return the a2lFileContents
   */
  public A2LFileInfo getA2lFileContents() {
    return this.a2lFileInfoBO.getA2lFileInfo();
  }

  /**
   * Gets the a 2 l file.
   *
   * @return the A2LFile instance
   */

  public A2LFile getA2lFile() {
    return this.pidcA2lBo.getA2lFile();
  }

  /**
   * Gets the pidc A 2 l BO.
   *
   * @return the A2LFile instance
   */

  public PidcA2LBO getPidcA2lBO() {
    return this.pidcA2lBo;
  }

  // iCDM-1241
  /**
   * {@inheritDoc}
   */
  @Override
  public Object getEditorInputSelection() {
    return this.pidcA2lBo;
  }

  /**
   * Gets the a 2 l editor data handler.
   *
   * @return the a2lFileInfoBO
   */
  public A2LFileInfoBO getA2lFileInfoBO() {
    return this.a2lFileInfoBO;
  }


  /**
   * Sets the a 2 l editor data handler.
   *
   * @param a2lFileInfoBO the new a 2 l editor data handler
   */
  public void setA2lFileInfoBO(final A2LFileInfoBO a2lFileInfoBO) {
    this.a2lFileInfoBO = a2lFileInfoBO;
  }


  /**
   * @return the dataHandler
   */
  public A2LEditorDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * Gets the pidc version.
   *
   * @return the pidc version
   */
  public PidcVersion getPidcVersion() {
    return this.pidcA2lBo.getPidcVersion();
  }
}
