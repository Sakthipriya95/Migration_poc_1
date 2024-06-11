package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * IActionHandler Interface
 */
@IID("{7D7C49B3-5A8C-480F-85FA-572BD0F22F36}")
public interface IActionHandler extends Com4jObject {
    /**
     * method CanExecute
     */
    @VTID(7)
    boolean canExecute(
        java.lang.Object sourceVersions,
        int targetVersion,
        int viewType);

    /**
     * method Execute
     */
    @VTID(8)
    boolean execute(
        java.lang.Object sourceVersions,
        int targetVersion,
        int viewType);

}
