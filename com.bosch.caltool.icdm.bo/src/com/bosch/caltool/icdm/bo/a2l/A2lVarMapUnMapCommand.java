package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpMapCmdModel;
import com.bosch.caltool.icdm.model.a2l.A2lVarGrpVariantMapping;


/**
 * Command class for PredefinedValidity
 *
 * @author dmo5cob
 */
public class A2lVarMapUnMapCommand extends AbstractSimpleCommand {


  /**
   * Input model
   */
  private final A2lVarGrpMapCmdModel cmdObjModel;

  /**
   * Constructor
   *
   * @param input input data
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lVarMapUnMapCommand(final ServiceData serviceData, final A2lVarGrpMapCmdModel input) throws IcdmException {
    super(serviceData);
    this.cmdObjModel = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    List<A2lVarGrpVariantMapping> mappingCreated = new ArrayList<>();
    List<A2lVarGrpVariantMapping> mappingDeleted = new ArrayList<>();
    if (this.cmdObjModel != null) {
      List<A2lVarGrpVariantMapping> mappingTobeCreated = this.cmdObjModel.getMappingTobeCreated();

      if (mappingTobeCreated != null) {
        for (A2lVarGrpVariantMapping a2lVarGrpVarMapping : mappingTobeCreated) {
          A2lVarGrpVarMappingCommand cmd =
              new A2lVarGrpVarMappingCommand(getServiceData(), a2lVarGrpVarMapping, false, false);
          executeChildCommand(cmd);
          mappingCreated.add(cmd.getNewData());
        }
        this.cmdObjModel.setMappingCreated(mappingCreated);
      }

      List<A2lVarGrpVariantMapping> mappingTobeDeleted = this.cmdObjModel.getMappingTobeDeleted();

      if (mappingTobeDeleted != null) {
        for (A2lVarGrpVariantMapping a2lVarGrpVarMapping : mappingTobeDeleted) {
          A2lVarGrpVarMappingCommand cmd =
              new A2lVarGrpVarMappingCommand(getServiceData(), a2lVarGrpVarMapping, false, true);
          executeChildCommand(cmd);
          mappingDeleted.add(cmd.getNewData());
        }
        this.cmdObjModel.setMappingDeleted(mappingDeleted);
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
  protected boolean hasPrivileges() throws IcdmException {

    return true;
  }

}
