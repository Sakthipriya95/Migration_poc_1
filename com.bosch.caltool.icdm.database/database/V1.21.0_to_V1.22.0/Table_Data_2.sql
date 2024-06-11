---------------------------------------------------------------

--  2015-10-20
---------------------------------------------------------------

---------------------------------------------------------------
----ICDM-1566  - 
----deleting entries for tip of the day file
---------------------------------------------------------------
---------------------------------------------------------------
--TABV_ICDM_FILES, TABV_ICDM_FILE_DATA, TABV_COMMON_PARAMS
---------------------------------------------------------------

delete from TABV_ICDM_FILE_DATA tifd where tifd.FILE_ID = (select tcp.PARAM_VALUE from TABV_COMMON_PARAMS tcp where tcp.PARAM_ID='TIP_OF_THE_DAY_FILE_ID');

delete from TABV_ICDM_FILES tif where tif.FILE_ID= (select tcp.PARAM_VALUE from TABV_COMMON_PARAMS tcp where tcp.PARAM_ID='TIP_OF_THE_DAY_FILE_ID');

delete from TABV_COMMON_PARAMS tcp where tcp.PARAM_ID='TIP_OF_THE_DAY_FILE_ID';

COMMIT;