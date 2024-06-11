
--
-- ICDM-1942
--
create or replace Procedure Set_SDOM_Info_for_A2L (a2lID    IN NUMBER
                                                 , pverName IN VARCHAR2
												 , varName  IN VARCHAR2
												 , varRev   IN NUMBER
												 , pverID   IN NUMBER
												 , vcdmA2lfileId IN NUMBER) IS
-- declare  

begin
    Set_SDOM_Info_for_A2L@DGSPRO.WORLD@K5ESK_VILLA_RO(a2lID, pverName, varName, varRev, pverID, vcdmA2lfileId);
end;
