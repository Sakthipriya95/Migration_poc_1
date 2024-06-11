/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author msp5cob
 */
public class ActiveDirectoryGroupBO extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData service data
   */
  public ActiveDirectoryGroupBO(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @throws IcdmException IcdmException
   */
  public void syncGroupUserDetails() throws IcdmException {
    LdapAuthenticationWrapper wrapper = new LdapAuthenticationWrapper();
    ActiveDirectoryGroupLoader loader = new ActiveDirectoryGroupLoader(getServiceData());
    ActiveDirectoryGroupUserLoader usrLoader = new ActiveDirectoryGroupUserLoader(getServiceData());
    List<AbstractSimpleCommand> usrSyncCmdList = new ArrayList<>();
    List<String> invalidGrpNameList = new ArrayList<>();
    // Used only for Logging
    Set<String> userNamesSyncedSet = new HashSet<>();

    try {

      CommonParamLoader paramLoader = new CommonParamLoader(getServiceData());
      String toAddr = paramLoader.getValue(CommonParamKey.ICDM_INVALID_AD_GRP_MAIL_TO);
      boolean mailFlag = ApicConstants.CODE_YES.equals(paramLoader.getValue(CommonParamKey.ICDM_AD_GRP_MAIL_ENABLED));

      for (ActiveDirectoryGroup activeDirGrp : loader.getAll().values()) {

        String grpName = activeDirGrp.getGroupName();
        Long grpId = activeDirGrp.getId();
        // Getting user names for AD Group from LDAP
        // Converting it to upper case since user name is stored in Upper case in iCDM
        Set<String> userNameSetFromLdap =
            wrapper.searchByDisplayName(grpName).stream().map(String::toUpperCase).collect(Collectors.toSet());
        Map<String, ActiveDirectoryGroupUser> userNameUserMap = usrLoader.getUsersMapByADGroupId(grpId);
        Set<String> userNameSetFromIcdm = userNameUserMap.keySet();


        // If there are users in iCDM and no users for group in LDAP,
        // The group is considered deleted or renamed
        // If flag is true, trigger a mail and don't do delete
        // else proceed with deleting all users for group in iCDM
        if ((CommonUtils.isNullOrEmpty(userNameSetFromLdap) && CommonUtils.isNotEmpty(userNameSetFromIcdm)) &&
            mailFlag) {
          invalidGrpNameList.add(grpName + " ( AD_GROUP_ID = " + grpId + " )");
        }
        else {
          Set<String> usersToRemove = new HashSet<>(userNameSetFromIcdm);

          // Comparing the Users from LDAP and determining Users to add and delete in iCDM for this AD Group
          usersToRemove.removeAll(userNameSetFromLdap);
          userNameSetFromLdap.removeAll(userNameSetFromIcdm);

          for (String userName : userNameSetFromLdap) {
            usrSyncCmdList.add(new ActiveDirectoryGroupUserCommand(getServiceData(),
                usrLoader.createDataObjectFromFields(activeDirGrp, userName), false, false));
            userNamesSyncedSet.add(grpName);
            getLogger().debug("User : {} marked for insert to Active Directory Group : {} ", userName, grpName);
          }

          for (String userName : usersToRemove) {
            usrSyncCmdList
                .add(new ActiveDirectoryGroupUserCommand(getServiceData(), userNameUserMap.get(userName), false, true));
            userNamesSyncedSet.add(grpName);
            getLogger().debug("User : {} marked for delete from Active Directory Group : {} ", userName, grpName);
          }
        }

      }

      if (CommonUtils.isNotEmpty(usrSyncCmdList)) {
        getServiceData().getCommandExecutor().execute(usrSyncCmdList);
        getLogger().info("Following Active Directory Group Users Synced with LDAP data {} ", userNamesSyncedSet);
      }

      if (CommonUtils.isNotEmpty(invalidGrpNameList)) {
        sendInvalidAdGrpEmail(invalidGrpNameList, toAddr);
        getLogger().info("Following Active Directory Groups are found to be invalid and Mail is sent {} ",
            invalidGrpNameList);
      }

    }
    catch (LdapException e) {
      throw new IcdmException(e.getLocalizedMessage(), e);
    }


  }

  private void sendInvalidAdGrpEmail(final List<String> invalidGrpNameList, final String toAddr) throws LdapException {
    String fromAddr = new LdapAuthenticationWrapper().getUserDetails(getServiceData().getUsername()).getEmailAddress();

    MailHotline hotline = new MailHotline(fromAddr, toAddr, true);

    hotline.sendADGroupMissingMail(invalidGrpNameList);
  }

}
