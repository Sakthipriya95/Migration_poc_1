package com.bosch.caltool.icdm.bo.a2l;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Command class for Attribute
 *
 * @author dmo5cob
 */
public class A2lFileUploadCommand extends AbstractSimpleCommand {


  private final A2LFile a2lFile;
  private final boolean canSavePver;


  /**
   * iCDM-778 <br>
   * Add BC info to A2L in database. Calls a stored procedure Get_BC_Info <br>
   * ICDM - 1942 <br>
   * Set SDOM Pver details to A2L in db.
   *
   * @param serviceData the service data
   * @param a2lFile the a 2 l file
   * @throws IcdmException the icdm exception
   */
  public A2lFileUploadCommand(final ServiceData serviceData, final A2LFile a2lFile, final boolean canSavePver)
      throws IcdmException {
    super(serviceData);
    this.a2lFile = a2lFile;
    this.canSavePver = canSavePver;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // STEP :1 Save Sdom PVer details

    getLogger().debug("Saving SDOM pVER details . . .");
    Query sdomQuery = getEm().createNativeQuery("BEGIN SET_SDOM_INFO_FOR_A2L(?,?,?,?,?,?); END;");
    sdomQuery.setParameter(ApicConstants.COLUMN_INDEX_1, this.a2lFile.getId());
    sdomQuery.setParameter(ApicConstants.COLUMN_INDEX_2, this.a2lFile.getSdomPverName());
    sdomQuery.setParameter(ApicConstants.COLUMN_INDEX_3, this.a2lFile.getSdomPverVariant());
    sdomQuery.setParameter(ApicConstants.COLUMN_INDEX_4, this.a2lFile.getSdomPverRevision());
    sdomQuery.setParameter(ApicConstants.COLUMN_INDEX_5, new SdomPverLoader(getServiceData()).getPVERId(
        this.a2lFile.getSdomPverName(), this.a2lFile.getSdomPverVariant(), this.a2lFile.getSdomPverRevision()));
    sdomQuery.setParameter(ApicConstants.COLUMN_INDEX_6, this.a2lFile.getVcdmA2lfileId());
    sdomQuery.executeUpdate();

    if (this.canSavePver) {
      // STEP 2 : Save BC if SDOM is valid from MvSdomPver
      getLogger().debug("Adding BC details . . .");
      Query bcQuery = getEm().createNativeQuery("BEGIN Get_BC_Info(?); END;");
      bcQuery.setParameter(1, this.a2lFile.getId());
      bcQuery.executeUpdate();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Refresh needed to get sdom details from a2lfileinfo-view
    getEm().refresh(new A2LFileInfoLoader(getServiceData()).getEntityObject(this.a2lFile.getId()));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }
}
