/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * @author mga1cob
 */
public final class BrowserUtil {

  /**
   * BrowserUtil instance
   */
  private static BrowserUtil browserUtilIns;
  private static Object mutex = new Object();

  /**
   * The private constructor
   */
  private BrowserUtil() {
    // The private constructor
  }

  /**
   * This method returns BrowserUtil instance
   * 
   * @return BrowserUtil
   */
  public static BrowserUtil getInstance() {
    synchronized (mutex) {
      if (browserUtilIns == null) {
        browserUtilIns = new BrowserUtil();
      }
    }
    return browserUtilIns;
  }

  /**
   * This method opens external default browser
   * 
   * @param url defines website url
   * @throws PartInitException if the browser fails to navigate to the provided url for any reason
   * @throws MalformedURLException this exception will throws when url is invalid
   */
  public void openExternalBrowser(final String url) throws PartInitException, MalformedURLException {
    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(url));

  }

  /**
   * This method opens shared path location
   * 
   * @param str defines path
   * @throws URISyntaxException URISyntaxException
   * @throws IOException IOException
   */
  public void openSharedPath(final String str) throws URISyntaxException, IOException {

    String str1 = str.trim();
    str1 = str1.replaceAll(Matcher.quoteReplacement("\\"), Matcher.quoteReplacement("/"));
    if (!str1.startsWith("file:")) {
      str1 = "file:" + str1.trim();
    }
    // mozilla links
    else if (str1.startsWith("file://///")) {
      str1 = str1.replaceAll(Matcher.quoteReplacement("/////"), Matcher.quoteReplacement("//"));
      str1 = URLDecoder.decode(str1, "ISO-8859-1"); // Assuming Mozilla link uses ISO-8859-1 encoding
    }
    URL url = new URL(str1);
    URI uri =
        new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(),
        url.getRef());
    Desktop.getDesktop().browse(uri);

  }

  /**
   * This method escapes characters which are not applicable for URL parsing .</br></br> This method in addition to
   * other substitutions replaces '\' with %5C which might not work in certain cases(such as link inside cell). In such
   * cases it is required to call queryToEncode with replace("\\","/") and then call the below method.</br>This handling
   * is specific to the callers
   * 
   * @param queryToEncode String
   * @return encoded String
   * @throws URIException - uri exception
   */
  public String encodeQuery(String queryToEncode) throws URIException {
    String encodedQuery = URIUtil.encodeQuery(queryToEncode);
    return encodedQuery.isEmpty() ? "" : encodedQuery;
  }

  /**
   * ICDM 533 Opens the path that is given as parameter
   * 
   * @param link String
   * @throws URISyntaxException handling should be done
   * @throws IOException
   */
  public void openPath(final String link) throws URISyntaxException, IOException {

    final URI uri = new URI(link);
    Desktop.getDesktop().browse(uri);

  }
}
