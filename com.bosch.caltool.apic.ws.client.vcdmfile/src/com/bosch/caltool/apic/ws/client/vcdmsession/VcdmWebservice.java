/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.vcdmsession;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.activation.DataHandler;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.vector.easee.application.cdmdomainservice.CDMDomainServiceClient;
import com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ObjInfoEntryType;
import com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.SearchObjectsByExprRequestType;
import com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.SearchObjectsByExprResponseType;
import com.vector.easee.application.cdmdomainservice.SearchObjectsByExpr_faultMsg;
import com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginResponseType;
import com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutResponseType;
import com.vector.easee.application.cdmsessionservice.CDMSessionWebServiceClient;
import com.vector.easee.application.cdmsessionservice.Login_faultMsg;
import com.vector.easee.application.cdmsessionservice.Logout_faultMsg;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceClient;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceStub;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.AttachmentType;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.AttrMapEntryType;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.Base64Binary;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SearchContentRequestType;
import com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SearchContentResponseType;
import com.vector.easee.application.cdmversionservice.FetchArtifact_faultMsg;
import com.vector.easee.application.cdmversionservice.GetVersionAttributes_faultMsg;
import com.vector.easee.application.cdmversionservice.SearchContent_faultMsg;


/**
 * @author imi2si
 */
public class VcdmWebservice {

  ILoggerAdapter logger;

  public VcdmWebservice(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  public boolean getVersion(final String vcdmVersion) {
    CDMSessionWebServiceClient ws =
        new CDMSessionWebServiceClient(CDMSessionWebServiceClient.CDMSessionWebService.PROD_SERVER);
    String sessionHandle = null;

    try {
      LoginResponseType resp = ws.login();
      sessionHandle = resp.getSesHandle();
      this.logger.info("Session Handle: " + sessionHandle);

      // Get file information
      CDMVersionServiceClient versServ =
          new CDMVersionServiceClient(CDMVersionServiceClient.CDMVersionService.PROD_SERVER);
      CDMVersionServiceStub.GetVersionAttributesRequestType versReq =
          new CDMVersionServiceStub.GetVersionAttributesRequestType();
      versReq.setSesHandle(sessionHandle);
      versReq.setVersionNo(vcdmVersion);
      CDMVersionServiceStub.GetVersionAttributesResponseType versResp = versServ.getVersionAttributes(versReq);
      this.logger.info("File returned: " + versResp.isReturnValueSpecified());
      return versResp.isReturnValueSpecified();
    }
    catch (RemoteException | Login_faultMsg | GetVersionAttributes_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      LogoutResponseType logout;
      try {
        logout = ws.logout(sessionHandle);
        System.out.println("Logout Return: " + logout.getReturnValue());
      }
      catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (Logout_faultMsg e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return false;
  }

  public String getFileName(final String vcdmVersion) {
    CDMSessionWebServiceClient ws =
        new CDMSessionWebServiceClient(CDMSessionWebServiceClient.CDMSessionWebService.PROD_SERVER);
    String sessionHandle = null;

    try {
      LoginResponseType resp = ws.login();
      sessionHandle = resp.getSesHandle();
      System.out.println(sessionHandle);

      // Get file information
      CDMVersionServiceClient versServ =
          new CDMVersionServiceClient(CDMVersionServiceClient.CDMVersionService.PROD_SERVER);
      CDMVersionServiceStub.GetVersionAttributesRequestType versReq =
          new CDMVersionServiceStub.GetVersionAttributesRequestType();
      versReq.setSesHandle(sessionHandle);
      versReq.setVersionNo(vcdmVersion);
      CDMVersionServiceStub.GetVersionAttributesResponseType versResp = versServ.getVersionAttributes(versReq);
      System.out.println("File returned: " + versResp.isReturnValueSpecified());

      for (AttrMapEntryType entry : versResp.getReturnValue()) {
        if (entry.getKey().equals("ORIGINAL FILE")) {
          return entry.getValuesArr()[0];
        }

      }

      return null;
    }
    catch (RemoteException | Login_faultMsg | GetVersionAttributes_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      LogoutResponseType logout;
      try {
        logout = ws.logout(sessionHandle);
        System.out.println("Logout Return: " + logout.getReturnValue());
      }
      catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (Logout_faultMsg e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  public InputStream getFile(final String vcdmVersion, final String filename) {
    CDMSessionWebServiceClient ws =
        new CDMSessionWebServiceClient(CDMSessionWebServiceClient.CDMSessionWebService.PROD_SERVER);
    String sessionHandle = null;

    try {
      LoginResponseType resp = ws.login();
      sessionHandle = resp.getSesHandle();
      System.out.println(sessionHandle);


      // Get file information
      CDMVersionServiceClient versServ =
          new CDMVersionServiceClient(CDMVersionServiceClient.CDMVersionService.PROD_SERVER);
      CDMVersionServiceStub.GetVersionAttributesRequestType versReq =
          new CDMVersionServiceStub.GetVersionAttributesRequestType();
      versReq.setSesHandle(sessionHandle);
      versReq.setVersionNo(vcdmVersion);
      CDMVersionServiceStub.GetVersionAttributesResponseType versResp = versServ.getVersionAttributes(versReq);
      System.out.println("File returned: " + versResp.isReturnValueSpecified());

      // Get the file
      CDMVersionServiceStub.FetchArtifactRequestType fileReq = new CDMVersionServiceStub.FetchArtifactRequestType();
      fileReq.setSesHandle(sessionHandle);
      fileReq.setVersionNo(vcdmVersion);
      CDMVersionServiceStub.FetchArtifactResponseType fileRes = versServ.fetchArtifact(fileReq);

      AttachmentType at = fileRes.getReturnValue();
      Base64Binary b = at.getBinaryData();
      System.out.println("File fetched: " + at.getFileName());

      DataHandler dataHandler = b.getBase64Binary();
      InputStream fromAxis = dataHandler.getInputStream();
      InputStream dataHere = new BufferedInputStream(fromAxis);
      InputStream unzipped = new GZIPInputStream(dataHere);


      try (FileOutputStream fos = new FileOutputStream(new File(filename));) {
        byte ba[] = new byte[0xFFFF];
        int read = 0;
        while ((read = unzipped.read(ba, 0, ba.length - 1)) > -1) {
          fos.write(ba, 0, read);
        }
      }


      return unzipped;

    }
    catch (RemoteException | Login_faultMsg | GetVersionAttributes_faultMsg | FetchArtifact_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      LogoutResponseType logout;
      try {
        logout = ws.logout(sessionHandle);
        System.out.println("Logout Return: " + logout.getReturnValue());
      }
      catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (Logout_faultMsg e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  public ObjInfoEntryType getVersionByExpression(final BigDecimal vcdmVersion) {
    CDMSessionWebServiceClient ws =
        new CDMSessionWebServiceClient(CDMSessionWebServiceClient.CDMSessionWebService.PROD_SERVER);
    String sessionHandle = null;


    try {
      LoginResponseType resp = ws.login();
      sessionHandle = resp.getSesHandle();
      CDMDomainServiceClient domServ =
          new CDMDomainServiceClient(CDMDomainServiceClient.CDMDomainnWebService.PROD_SERVER);
      SearchObjectsByExprRequestType req = new SearchObjectsByExprRequestType();
      req.setSesHandle(sessionHandle);
      req.setSqlString("((Bereich='CDM') " + " AND ( vers_nummer = '" + vcdmVersion + "'))");
      SearchObjectsByExprResponseType respObjSearch = domServ.searchObjectsByExpr(req);

      if (respObjSearch.isReturnValueSpecified() && (respObjSearch.getReturnValue().length > 0)) {
        return respObjSearch.getReturnValue()[0];
      }
      else {
        return null;
      }
    }
    catch (RemoteException | Login_faultMsg | SearchObjectsByExpr_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      LogoutResponseType logout;
      try {
        logout = ws.logout(sessionHandle);
        System.out.println("Logout Return: " + logout.getReturnValue());
      }
      catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (Logout_faultMsg e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  public List<CDMVersionServiceStub.ObjInfoEntryType> searchContent(final String objClass, final String containerVersion) {
    CDMSessionWebServiceClient ws =
        new CDMSessionWebServiceClient(CDMSessionWebServiceClient.CDMSessionWebService.PROD_SERVER);
    String sessionHandle = null;
    List<CDMVersionServiceStub.ObjInfoEntryType> respList = new ArrayList<>();


    try {
      LoginResponseType resp = ws.login();
      sessionHandle = resp.getSesHandle();
      CDMVersionServiceClient versServ =
          new CDMVersionServiceClient(CDMVersionServiceClient.CDMVersionService.PROD_SERVER);
      SearchContentRequestType req = new SearchContentRequestType();
      req.setSesHandle(sessionHandle);
      req.setObjClass(objClass);
      req.setGroupNo(containerVersion);

      SearchContentResponseType respSearchContent = versServ.searchContent(req);

      CDMVersionServiceStub.ObjInfoEntryType[] respInfoEntryTypes = respSearchContent.getReturnValue();

      if (respInfoEntryTypes != null) {
        for (CDMVersionServiceStub.ObjInfoEntryType entry : respInfoEntryTypes) {
          respList.add(entry);
        }
      }
    }
    catch (RemoteException | Login_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (SearchContent_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      LogoutResponseType logout;
      try {
        logout = ws.logout(sessionHandle);
        System.out.println("Logout Return: " + logout.getReturnValue());
      }
      catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (Logout_faultMsg e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return respList;
  }

  public static void main(final String args[]) {

    CDMSessionWebServiceClient ws =
        new CDMSessionWebServiceClient(CDMSessionWebServiceClient.CDMSessionWebService.PROD_SERVER);
    String sessionHandle = null;

    try {
      LoginResponseType resp = ws.login();
      sessionHandle = resp.getSesHandle();
      System.out.println(sessionHandle);


      // Get file information
      CDMVersionServiceClient versServ =
          new CDMVersionServiceClient(CDMVersionServiceClient.CDMVersionService.PROD_SERVER);
      CDMVersionServiceStub.GetVersionAttributesRequestType versReq =
          new CDMVersionServiceStub.GetVersionAttributesRequestType();
      versReq.setSesHandle(sessionHandle);
      versReq.setVersionNo("18671994");
      CDMVersionServiceStub.GetVersionAttributesResponseType versResp = versServ.getVersionAttributes(versReq);
      System.out.println("File returned: " + versResp.isReturnValueSpecified());

      // Get the file
      CDMVersionServiceStub.FetchArtifactRequestType fileReq = new CDMVersionServiceStub.FetchArtifactRequestType();
      fileReq.setSesHandle(sessionHandle);
      fileReq.setVersionNo("18671994");
      CDMVersionServiceStub.FetchArtifactResponseType fileRes = versServ.fetchArtifact(fileReq);

      AttachmentType at = fileRes.getReturnValue();
      Base64Binary b = at.getBinaryData();
      System.out.println("File fetched: " + at.getFileName());

      DataHandler dataHandler = b.getBase64Binary();
      InputStream fromAxis = dataHandler.getInputStream();
      InputStream dataHere = new BufferedInputStream(fromAxis);
      InputStream unzipped = new GZIPInputStream(dataHere);


      try (FileOutputStream fos = new FileOutputStream(new File("C:\\temp\\test.a2l"));) {
        byte ba[] = new byte[0xFFFF];
        int read = 0;
        while ((read = unzipped.read(ba, 0, ba.length - 1)) > -1) {
          fos.write(ba, 0, read);
        }
      }

    }
    catch (RemoteException | Login_faultMsg | GetVersionAttributes_faultMsg | FetchArtifact_faultMsg e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      LogoutResponseType logout;
      try {
        logout = ws.logout(sessionHandle);
        System.out.println("Logout Return: " + logout.getReturnValue());
      }
      catch (RemoteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (Logout_faultMsg e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
