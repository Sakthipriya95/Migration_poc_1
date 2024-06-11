#################################################
Read Me - iCDM Upgrade - v2021.3.0 to v2021.6.0
DB scripts
#################################################

------------------
Order of execution
------------------

----------------------
Step 1 : Alter scripts
----------------------
01_Table_Alters.sql
02_Table_Alters_Qnaire.sql

-----------------
Step 2 : Triggers
-----------------
21_Create_Trigger_Qnaire.sql

---------------------
Step 3 : Data scripts
---------------------
30_Table_Data.sql
31_Help_Links.sql
31_Table_Data_Qnaire.sql

----------------------------------------
Step 4 : Questionnaire Migration scripts
----------------------------------------

Step 4a : 
---------
32_QnaireMig_Step0-PK_LOG_CreationScripts.sql

Note : Some sample scripts to monitor migration progress
32_QnaireMig_Step0-PK_LOG_SampleScripts.sql


Step 4b : 
---------
32_QnaireMig_Step0-Validations.sql

Step 4c : 
---------
a) Migration
32_QnaireMig_Step1A-MergedQnaireDefVerCreation.sql

b) Validations
32_QnaireMig_Step1B-Validations.sql

NOTE : Run commit if validation is successful

Step 4d :
---------
a) Migration
32_QnaireMig_Step2A-FillQnaireEnhancementNewDataModel.sql

b) Validations
32_QnaireMig_Step2B-Validation1.sql
32_QnaireMig_Step2B-Validation2.sql

NOTE : Run commit if validation is successful

Step 4e :
---------

a) Create migration tables for 3A
32_QnaireMig_Step2C-MigTablesFor3A.sql

b) Migration
32_QnaireMig_Step3A-AssociatingWpAndRespToQnaireResponse.sql

c) Validations
32_QnaireMig_Step3B-Validations1-A.sql
32_QnaireMig_Step3B-Validations1-B.sql
32_QnaireMig_Step3B-Validations2.sql

Step 4f :
---------
a) Run this only if validations 1-B fails above
32_QnaireMig_Step3C-DoNotRunUnlessIssuesFoundByValidationScript1.sql

b) Run this only if validation 2 fails above
32_QnaireMig_Step3C-DoNotRunUnlessIssuesFoundByValidationScript2.sql

Step 4g :
---------
a) 32_QnaireMig_Step4A-MergingAnswersToGeneralQuestions.sql


---------------------------
Step 5 : Grants to JPA user
---------------------------
50_Grants.sql
51_Grants_Qnaire.sql


-------------------------------
Step 6 : Synonyms to new tables
-------------------------------
60_Synonyms.sql
61_Synonmys_Qnaire.sql


--------------------------------------------
Step 7 : Questionnaire Migration - Java side
--------------------------------------------
Run method (as junit test) com.bosch.caltool.icdm.bo.test/QnaireMigration.qnaireMigration()

command line arguments : 
-ea -Xmx16G -DWebServiceConfPath="<dir to messages.properties containing DB connection details" -Djavax.net.ssl.trustStore="<cacerts full file path>"
For example,
-ea -Xmx16G -DWebServiceConfPath="D:\RBEI\iCDM\Bebith\iCDM-WS\ORWS8\conf" -Djavax.net.ssl.trustStore="D:/RBEI/iCDM/common/iCDM-Config/CACert/cacerts"


-----------------------------------------------
Step 8 :Post migration - additional constraints
-----------------------------------------------
40_Table_Alters_Qnaire_Part2.sql


-------------------------------------------------------------------
Step 9 : Drop obsolete constraints, colummns etc.
NOTE : to be executed ONLY at the end, if migration is COMPLETE !!!
-------------------------------------------------------------------
91_Drop_Objects_Qnaire.sql
