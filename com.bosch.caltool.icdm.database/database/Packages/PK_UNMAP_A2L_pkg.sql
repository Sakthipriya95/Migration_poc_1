------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594541: Server side changes - for deletion of A2L related DB entries
------------------------------------------------------------------------------------------------------------------

/* 
* Package to remove all PIDC-A2L references, before unmapping A2L from PIDC
*/
CREATE OR REPLACE PACKAGE PK_UNMAP_A2L AS 
	/* 
	* Procedure to delete A2L related entries and unmap A2L from PIDC Version
	*/
    PROCEDURE P_UNMAP_A2L
	    (
	        p_pidc_a2l_id NUMBER
	    );

END PK_UNMAP_A2L;

/
