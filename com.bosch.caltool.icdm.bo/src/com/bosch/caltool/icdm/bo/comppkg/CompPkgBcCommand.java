package com.bosch.caltool.icdm.bo.comppkg;

import java.util.SortedSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;


/**
 * @author say8cob
 */
public class CompPkgBcCommand extends AbstractCommand<CompPkgBc, CompPkgBcLoader> {

  private boolean isUp = false;

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CompPkgBcCommand(final ServiceData serviceData, final CompPkgBc input, final boolean isUpdate,
      final boolean isDelete, final boolean isUp) throws IcdmException {
    super(serviceData, input, new CompPkgBcLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE);
    this.isUp = isUp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCompPkgBc entity = new TCompPkgBc();
    TCompPkg compPkg = getEm().find(TCompPkg.class, getInputData().getCompPkgId());
    entity.setTCompPkg(compPkg);
    entity.setBcName(getInputData().getBcName());
    entity.setBcSeqNo(getInputData().getBcSeqNo());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   *
   * @param isUp
   * @throws IcdmException
   */
  @Override
  protected void update() throws IcdmException {
    CompPkgBcLoader loader = new CompPkgBcLoader(getServiceData());
    TCompPkgBc modifiedCpBc = loader.getEntityObject(getInputData().getId());
    TCompPkg compPkg = getEm().find(TCompPkg.class, getInputData().getCompPkgId());
    SortedSet<CompPkgBc> bcSet = loader.getBCByCompId(getInputData().getCompPkgId());
    if (!bcSet.isEmpty() && (bcSet.size() > 1)) {
      for (CompPkgBc compPkgBcObj : bcSet) {
        if ((this.isUp && compPkgBcObj.getBcSeqNo().equals(modifiedCpBc.getBcSeqNo() - 1)) ||
            (!this.isUp && compPkgBcObj.getBcSeqNo().equals(modifiedCpBc.getBcSeqNo() + 1))) {
          final Long seqNo = modifiedCpBc.getBcSeqNo();
          modifiedCpBc.setBcSeqNo(Long.valueOf(0));// Temporarily set this
          getEm().flush();
          final TCompPkgBc bcWithLowerSqNo = getEm().find(TCompPkgBc.class, compPkgBcObj.getId());
          bcWithLowerSqNo.setBcSeqNo(seqNo);
          getEm().flush();
          if (this.isUp) {
            modifiedCpBc.setBcSeqNo(seqNo - 1);
          }
          else {
            modifiedCpBc.setBcSeqNo(seqNo + 1);
          }
          getEm().flush();
          break;
        }
      }

    }
    modifiedCpBc.setTCompPkg(compPkg);
    modifiedCpBc.setBcName(getInputData().getBcName());
    setUserDetails(COMMAND_MODE.UPDATE, modifiedCpBc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    CompPkgBcLoader loader = new CompPkgBcLoader(getServiceData());
    TCompPkgBc deletedBc = loader.getEntityObject(getInputData().getId());


    Long seqNo = deletedBc.getBcSeqNo();
    deletedBc.setBcSeqNo(Long.valueOf(0));// temporarily set the seq no:
    getEm().remove(deletedBc);
    getEm().flush();

    CompPkgBcLoader bcLoader = new CompPkgBcLoader(getServiceData());
    SortedSet<CompPkgBc> bcByCompId = bcLoader.getBCByCompId(getInputData().getCompPkgId());

    if (!bcByCompId.isEmpty() && (bcByCompId.size() > 1)) {
      for (CompPkgBc bc : bcByCompId) {
        if (bc.getBcSeqNo() > seqNo) {
          final TCompPkgBc bcWithLowerSqNo = getEm().find(TCompPkgBc.class, bc.getId());
          bcWithLowerSqNo.setBcSeqNo(bc.getBcSeqNo() - 1);
          getEm().flush();
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }


}
