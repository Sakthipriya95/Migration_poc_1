--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Package PK_GROUP2PAL
--------------------------------------------------------

create or replace PACKAGE PK_GROUP2PAL AS 

  -- attribute value ID for root group Workpackage
  cWpRootWP_ID     constant number := 259569; -- WP
  -- attribute value ID for root group Responsibilitied
  cWpRootRESP_ID   constant number := 259568; -- RESP
  -- RESP_TYPE for Robert Bosch
  cRespTypeBoschID constant varchar2(1) := 'R';
  -- defult WP name
  cDefaultWpName constant varchar2(12) := '_DEFAULT_WP_';

  --
  -- Transfer GROUPS from A2L to PAL WP
  -- - executes CreatePalWP, CreatePalRESP, MoveUniqueRESP
  --
  procedure TransferGroup2PalWP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2);

  --
  -- Delete all PAL WP information for aPIDC A2L
  -- - delete will be done only if there is a single WP with a single RESP (default situation)
  -- - except variant groups
  --
  procedure DeletePalWP(pPidcA2LID IN NUMBER);

  --
  -- Create <DEFAULT_WP> for an A2L file
  -- - only if no WP_DEFINITION_VERSION existing for the PIDC A2L
  --
  procedure CreateDefaultPalWP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2);

  --
  -- Transfer WorkPackages out of A2L GROUPS PAL WP
  -- - requires a <DEFAULT_WP>
  -- - create WP
  -- - assign parameter to WP
  -- - WP will have the same responsibility type (Robert Bosch, Customer, Other) as the A2L based GROUP
  --
  procedure CreatePalWP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2);

  --
  -- Transfer Responsibilities out of A2L GROUPS PAL WP
  -- - create responsibility based on GROUP name as ALIAS_NAME in T_PIDC_WP_RESP
  -- - assign responsibility to parameter
  --
  procedure CreatePalRESP(pPidcA2LID IN NUMBER, pCreUser VARCHAR2);

  --
  -- move Responsibilities from parameter to WP if all parameter of WP do have the same responsibility
  -- - set RESP inheritance to 'Y'
  -- - set parameter responsibility to null
  --
  procedure MoveUniqueRESP(pPidcA2LID IN NUMBER, pModUser VARCHAR2);

  --
  -- Get the Default Robert Bosch Responsibility ID
  --
  function getDefaultA2lRespID(pPidcID IN NUMBER, pCreUser IN VARCHAR2) return number ;

  --
  -- Get the Pidc A2l 
  --
  function getPidcA2L(pPidcA2LID IN NUMBER) return t_pidc_a2l%ROWTYPE;

END PK_GROUP2PAL;

/

