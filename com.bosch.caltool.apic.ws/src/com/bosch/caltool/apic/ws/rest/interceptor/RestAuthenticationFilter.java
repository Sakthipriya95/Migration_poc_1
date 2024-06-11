package com.bosch.caltool.apic.ws.rest.interceptor;

import java.io.IOException;
import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.authentication.BasicAuthentication;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author bne4cob
 */
@Provider
@PreMatching
public class RestAuthenticationFilter implements ContainerRequestFilter {

  /**
   * The HTTP Request
   */
  @Context
  private HttpServletRequest httpRequest;

  /**
   * URI Details to check SSL availibility
   */
  @Inject
  private javax.inject.Provider<UriInfo> uriDetails;

  /**
   * {@inheritDoc}
   */
  @Override
  public void filter(final ContainerRequestContext filterContext) throws IOException {
    ServiceData sData = (ServiceData) this.httpRequest.getAttribute(WsCommonConstants.RWS_REQUEST_ATTR_COMMON_SER_DATA);

    new BasicAuthentication(this.httpRequest, sData).authenticate();

    if (!CommonUtils.isEmptyString(sData.getUsername())) {
      filterContext.setSecurityContext(new AuthorizationSecurityContext(sData.getUsername()));
    }
  }

  class AuthorizationSecurityContext implements SecurityContext {

    private final String userName;
    private final Principal secPrincipal;

    public AuthorizationSecurityContext(final String username) {
      this.userName = username;
      this.secPrincipal = () -> AuthorizationSecurityContext.this.userName;
    }

    @Override
    public Principal getUserPrincipal() {
      return this.secPrincipal;
    }

    @Override
    public boolean isUserInRole(final String role) {
      return true;
    }

    @Override
    public boolean isSecure() {
      return "https".equalsIgnoreCase(RestAuthenticationFilter.this.uriDetails.get().getRequestUri().getScheme());
    }

    @Override
    public String getAuthenticationScheme() {
      return SecurityContext.BASIC_AUTH;
    }
  }

}