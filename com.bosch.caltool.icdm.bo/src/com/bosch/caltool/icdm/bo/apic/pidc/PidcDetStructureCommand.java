package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;


/**
 * Command class for PIDC Details Structure
 *
 * @author mkl2cob
 */
public class PidcDetStructureCommand extends AbstractCommand<PidcDetStructure, PidcDetStructureLoader> {

  /**
   * Constant for - Level sort descending
   */
  private static final int SORT_ORDR_LVL_DSC = 1;
  /**
   * Constant for - Level sort ascending
   */
  private static final int SORT_ORDR_LVL_ASC = 0;

  private final boolean isNewRevision;

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public PidcDetStructureCommand(final ServiceData serviceData, final PidcDetStructure input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new PidcDetStructureLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
    this.isNewRevision = false;
  }

  /**
   * @param serviceData
   * @param input
   * @param isUpdate
   * @param isDelete
   * @param isNewRevision
   * @throws IcdmException
   */
  public PidcDetStructureCommand(final ServiceData serviceData, final PidcDetStructure input, final boolean isUpdate,
      final boolean isDelete, final boolean isNewRevision) throws IcdmException {
    super(serviceData, input, new PidcDetStructureLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
    this.isNewRevision = isNewRevision;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvPidcDetStructure entity = new TabvPidcDetStructure();

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));

    // set pidc version
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tPidcVersion = pidcVersionLoader.getEntityObject(getInputData().getPidcVersId());
    entity.setTPidcVersion(tPidcVersion);

    List<TabvPidcDetStructure> pdsSet = tPidcVersion.getTabvPidcDetStructures();
    if (null == pdsSet) {
      pdsSet = new ArrayList<>();
      tPidcVersion.setTabvPidcDetStructures(pdsSet);
    }
    // strucure should be copied exactly as the parent version during creating new pidcversion from versions page
    if (this.isNewRevision) {
      entity.setPidAttrLevel(getInputData().getPidAttrLevel());
    }
    else {
      entity.setPidAttrLevel(getInputData().getPidAttrLevel() + 1);
      // Sort the set DESC, to perform the increment in order
      SortedSet<TabvPidcDetStructure> pdsSortedSet = getSortedSet(pdsSet, SORT_ORDR_LVL_DSC);
      // first, check and increment level for all nodes available under this node
      if (!pdsSortedSet.isEmpty()) {
        for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
          if (tabvPidcDetStructure.getPidAttrLevel() > getInputData().getPidAttrLevel()) {
            // set level for other nodes under this node
            tabvPidcDetStructure.setPidAttrLevel(tabvPidcDetStructure.getPidAttrLevel() + 1);
            // send it to db, such that further updates needs to be done
            getEm().flush();
          }
        }
      }
    }


    pdsSet.add(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param pdsSet
   * @return
   */
  private SortedSet<TabvPidcDetStructure> getSortedSet(final List<TabvPidcDetStructure> pdsSet, final int sortOrder) {
    // Sort the set, to maintain the order when batch update with flushh
    // First, implement a comparator
    Comparator<TabvPidcDetStructure> pdsComparator = new Comparator<TabvPidcDetStructure>() {

      @Override
      public int compare(final TabvPidcDetStructure pds1, final TabvPidcDetStructure pds2) {
        // if order is desceding
        if (sortOrder == SORT_ORDR_LVL_DSC) {
          return pds2.getPidAttrLevel().compareTo(pds1.getPidAttrLevel());
        }
        // default by asc
        return pds1.getPidAttrLevel().compareTo(pds2.getPidAttrLevel());
      }
    };
    // Create sorted set with the comparator
    SortedSet<TabvPidcDetStructure> pdsSortedSet = new TreeSet<TabvPidcDetStructure>(pdsComparator);
    pdsSortedSet.addAll(pdsSet);

    return pdsSortedSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    PidcDetStructureLoader loader = new PidcDetStructureLoader(getServiceData());
    TabvPidcDetStructure entity = loader.getEntityObject(getInputData().getId());

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));
    entity.setPidAttrLevel(getInputData().getPidAttrLevel());
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tPidcVersion = pidcVersionLoader.getEntityObject(getInputData().getPidcVersId());
    entity.setTPidcVersion(tPidcVersion);
    List<TabvPidcDetStructure> tabvPidcDetStructures = tPidcVersion.getTabvPidcDetStructures();
    if (null == tabvPidcDetStructures) {
      tabvPidcDetStructures = new ArrayList<>();
      tPidcVersion.setTabvPidcDetStructures(tabvPidcDetStructures);
    }
    tabvPidcDetStructures.add(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tPidcVersion = pidcVersionLoader.getEntityObject(getInputData().getPidcVersId());
    // Sort the set ASC, to perform the decrement in order
    SortedSet<TabvPidcDetStructure> pdsSortedSet =
        getSortedSet(tPidcVersion.getTabvPidcDetStructures(), SORT_ORDR_LVL_ASC);
    // Delete from intermediate nodes
    if (getInputData().getPidAttrLevel() > 0) {
      // delete the node
      if (!pdsSortedSet.isEmpty()) {
        deleteNodeFromDB(pdsSortedSet, tPidcVersion);

        // now, decrement the levels for the nodes under this node

        decrementChildNodeLvls(pdsSortedSet);
      }
    }
    else {
      deleteTopLevelNodes(pdsSortedSet, tPidcVersion);
    }

  }

  /**
   * @param pdsSortedSet
   */
  private void decrementChildNodeLvls(final SortedSet<TabvPidcDetStructure> pdsSortedSet) {
    for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
      if (tabvPidcDetStructure.getPidAttrLevel() > getInputData().getPidAttrLevel()) {
        // decrement the level
        tabvPidcDetStructure.setPidAttrLevel(tabvPidcDetStructure.getPidAttrLevel() - 1);
        // send it to db, such that further updates needs to be done
        getEm().flush();
      }
    }
  }

  /**
   * @param pdsSortedSet SortedSet<TabvPidcDetStructure>
   * @param tPidcVersion
   * @throws CommandException
   */
  private void deleteNodeFromDB(final SortedSet<TabvPidcDetStructure> pdsSortedSet, final TPidcVersion tPidcVersion)
      throws CommandException {
    for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
      if (tabvPidcDetStructure.getPidAttrLevel().longValue() == getInputData().getPidAttrLevel().longValue()) {
        // remove entity, removes the node to be deleted
        removeDbEntity(tabvPidcDetStructure, tPidcVersion);
        // send it to db, such that further updates needs to be done
        getEm().flush();
        break;
      }
    }
  }


  /**
   * @param tabvPidcDetStructure
   * @param tPidcVersion
   */
  private void removeDbEntity(final TabvPidcDetStructure tabvPidcDetStructure, final TPidcVersion tPidcVersion) {
    tPidcVersion.getTabvPidcDetStructures().remove(tabvPidcDetStructure);
    getEm().remove(tabvPidcDetStructure);

  }

  /**
   * @param pdsSortedSet SortedSet<TabvPidcDetStructure>
   * @param tPidcVersion
   * @throws CommandException
   */
  private void deleteTopLevelNodes(final SortedSet<TabvPidcDetStructure> pdsSortedSet, final TPidcVersion tPidcVersion)
      throws CommandException {
    for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
      if (tabvPidcDetStructure.getTabvAttribute().getAttrId() == getInputData().getAttrId().longValue()) {
        // remove entity, removes the node to be deleted
        removeDbEntity(tabvPidcDetStructure, tPidcVersion);
        // send it to db, such that further updates needs to be done, if there are any
        getEm().flush();
        break;
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
