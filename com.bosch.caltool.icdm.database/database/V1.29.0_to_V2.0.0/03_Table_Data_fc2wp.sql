spool c:\temp\03_Table_Data_fc2wp.log

-------------------------------------------------
--Task 231531 Schema for FC2WP definition
-------------------------------------------------

INSERT INTO T_POWER_TRAIN_TYPE (PT_TYPE, PT_TYPE_DESC) VALUES ('C', 'Common');
INSERT INTO T_POWER_TRAIN_TYPE (PT_TYPE, PT_TYPE_DESC) VALUES ('G', 'Gasoline');
INSERT INTO T_POWER_TRAIN_TYPE (PT_TYPE, PT_TYPE_DESC) VALUES ('D', 'Diesel');
INSERT INTO T_POWER_TRAIN_TYPE (PT_TYPE, PT_TYPE_DESC) VALUES ('E', 'Electrical Vehicle');
INSERT INTO T_POWER_TRAIN_TYPE (PT_TYPE, PT_TYPE_DESC) VALUES ('GH', 'Gasoline Hybrid');
INSERT INTO T_POWER_TRAIN_TYPE (PT_TYPE, PT_TYPE_DESC) VALUES ('DH', 'Diesel Hybrid');


commit;

spool off