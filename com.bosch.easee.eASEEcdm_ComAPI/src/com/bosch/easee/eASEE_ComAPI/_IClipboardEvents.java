package com.bosch.easee.eASEE_ComAPI  ;

import com4j.*;

/**
 * _IClipboardEvents Interface
 */
@IID("{8ECD3330-915A-4FEE-8A86-1D83441257CD}")
public interface _IClipboardEvents extends Com4jObject {
    /**
     * method OnUpdateEditCut
     */
    @VTID(7)
    void onUpdateEditCut(
        Holder<Integer> pActivate);

    /**
     * method OnUpdateEditPaste
     */
    @VTID(8)
    void onUpdateEditPaste(
        Holder<Integer> pActivate);

    /**
     * method OnUpdateEditRemove
     */
    @VTID(9)
    void onUpdateEditRemove(
        Holder<Integer> pActivate);

    /**
     * method OnEditCut
     */
    @VTID(10)
    void onEditCut();

    /**
     * method OnEditPaste
     */
    @VTID(11)
    void onEditPaste();

    /**
     * method OnEditRemove
     */
    @VTID(12)
    void onEditRemove();

}
