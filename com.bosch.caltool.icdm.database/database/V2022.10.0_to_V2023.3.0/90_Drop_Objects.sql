spool c:\temp\90_Drop_Objects.log

--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 637826 - Server Side changes , Table Modification for linking WP-RESP
--------------------------------------------------------------------------------------------------------------------------------

-- removing unwanted columns from t_rvw_qnaire_response table
ALTER TABLE t_rvw_qnaire_response 
    DROP 
    ( pidc_vers_id,
      variant_id,
      a2l_resp_id,
      a2l_wp_id 
    );



spool off
