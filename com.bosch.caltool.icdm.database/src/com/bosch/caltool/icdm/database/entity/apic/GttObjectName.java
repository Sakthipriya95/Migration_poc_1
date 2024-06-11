package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;


/**
 * The persistent class for the GTT_OBJECT_NAMES database table.
 */
@Entity
@Table(name = "GTT_OBJECT_NAMES")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = GttObjectName.NNS_INS_TEMP_TABLE_A2L_PARAMS, query = "                             " +
        "INSERT into GTT_OBJECT_NAMES (id, obj_name)                                                          " +
        "    SELECT                                                                                           " +
        "        param.id,                                                                                    " +
        "        param.name                                                                                   " +
        "    FROM                                                                                             " +
        "        T_PARAMETER param,                                                                           " +
        "        TA2L_FILEINFO a2lfile,                                                                       " +
        "        TA2L_MODULES a2lmodule,                                                                      " +
        "        TA2L_CHARACTERISTICS a2lparam                                                                " +
        "    WHERE                                                                                            " +
        "            a2lfile.id          = a2lmodule.FILE_ID                                                  " +
        "        AND a2lmodule.MODULE_ID = a2lparam.MODULE_ID                                                 " +
        "        AND a2lparam.name       = param.name                                                         " +
        "        AND a2lparam.DTYPE      = param.PTYPE                                                        " +
        "        AND a2lfile.id = ?                                                                           "),
    @NamedNativeQuery(name = GttObjectName.NNS_INS_TEMP_TABLE_A2L_FUNS, query = "                             " +
        "insert into gtt_object_names (id, obj_name)                                                        " +
        "( select afun.function_id, upper(afun.name)                                                        " +
        "  from ta2l_fileinfo a2l, ta2l_modules amod, ta2l_functions afun                                   " +
        "  where amod.file_id = a2l.id                                                                      " +
        "    and afun.module_id = amod.module_id                                                            " +
        "    and a2l.id = ?                                                                                 " +
        ")                                                                                                  ") })

@NamedQueries(value = {
    @NamedQuery(name = GttObjectName.NS_DELETE_GTT_OBJ_NAMES, query = "delete from GttObjectName temp") })
public class GttObjectName implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Named Native statement to insert parameters of an A2L file to temporary table
   *
   * @param a2lFileID A2L File ID, as in TA2L_FILE_INFO
   */
  public static final String NNS_INS_TEMP_TABLE_A2L_PARAMS = "GttObjectName.InsertTempTableA2LParams";

  /**
   * Named Native statement to insert functions of an A2L file to temporary table
   *
   * @param a2lFileId A2L File ID, as in in TA2L_FILE_INFO
   */
  public static final String NNS_INS_TEMP_TABLE_A2L_FUNS = "GttObjectName.InsertTempTableA2LFunctions";

  /**
   * statement to clear the temp table
   */
  public static final String NS_DELETE_GTT_OBJ_NAMES = "GttObjectName.deletegttobjnames";

  @Id
  private long id;

  @Column(name = "OBJ_NAME")
  private String objName;

  public GttObjectName() {
    // NA
  }

  public long getId() {
    return this.id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public String getObjName() {
    return this.objName;
  }

  public void setObjName(final String objName) {
    this.objName = objName;
  }

}