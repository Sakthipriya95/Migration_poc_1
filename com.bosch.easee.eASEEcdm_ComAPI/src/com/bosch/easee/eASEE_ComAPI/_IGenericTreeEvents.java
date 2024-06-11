package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IGenericTreeEvents Interface
 */
@IID("{881C4C32-AF34-4068-8712-9E024A656BBB}")
public interface _IGenericTreeEvents extends Com4jObject {
    /**
     * method OnOpenGtvInstance
     */
    @VTID(7)
    void onOpenGtvInstance(
        java.lang.String templateName,
        java.lang.String name,
        Holder<Boolean> openGtv);

    /**
     * method OnNewGtvInstance
     */
    @VTID(8)
    void onNewGtvInstance(
        java.lang.String templateName,
        Holder<Boolean> newGtv);

    /**
     * method OnDeleteGtvInstance
     */
    @VTID(9)
    void onDeleteGtvInstance(
        java.lang.String templateName,
        java.lang.String name,
        Holder<Boolean> deleteGtv);

    /**
     * method OnRenameGtvInstance
     */
    @VTID(10)
    void onRenameGtvInstance(
        java.lang.String templateName,
        java.lang.String name,
        Holder<Boolean> renameGtv);

}
