/**
 * This file is part of the VNCProxy program.
 * <p>
 * VNCPRoxy Summary :
 * In just one clic (no setup) this Java Applet based solution
 * allows you to run VNC Server / VNC Viewer
 * through an HTTP AES encrypted tunnel.
 * As it is full HTTP, there is no proxy or firewall setup needed.
 * <p>
 * Copyright (C) 2009  - Rémi Serrano - http://www.vncproxy.com
 * <p>
 * VNCProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * VNCProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with VNCProxy.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.vncproxy.hub;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.TimeZone;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.vncproxy.applet.VNCPActionData;
import com.vncproxy.applet.VNCPSession;

/**
 * This class implements the VNCPHubAction Servlet the VNCPHubAction will handle
 * and execute all the ActionData sent from the VNCProxy Applet to the VNCProxy
 * Hub
 * 
 * @author Rémi Serrano
 * 
 */
public class VNCPHubAction extends HttpServlet {
  /**
   * This constant variable is needed to implements java.io.Serializable
   */
  private static final long serialVersionUID = 1L;

  /**
   * This constant variable represents the time out value in milliseconds
   */
  private static final long TIMEOUT          = 60 * 60 * 1000;

  /**
   * This constant variable represents the size of the AES key in bits
   */
  private static final int  AES_KEY_LEN      = 128;
  /**
   * This constant variable represents the sid upper limit
   */
  private static final int  SID_UPPER_LIMIT  = 999999;

  /**
   * This constant variable represents the sid lower limit
   */
  private static final int  SID_LOWER_LIMIT  = 100000;

  /**
   * This method overrides the standard Servlet doPost method It will handle and
   * execute all the ActionData sent from the VNCProxy Applet to the VNCProxy
   * Hub
   * 
   * @param request
   *          The HTTP request
   * @param response
   *          The HTTP response
   * @throws IOException
   *           Any IO Exception
   * @throws ServletException
   *           Any Servlet Exception
   */
  @Override
  public final synchronized void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

    ServletContext context = getServletConfig().getServletContext();
    InputStream in = request.getInputStream();
    OutputStream outstr = response.getOutputStream();

    VNCPActionData objActionDataBack = null;
    response.setContentType("application/x-java-serialized-object");

    VNCPLog log = new VNCPLog();

    try {
      log.logInfo("------------------------------------");
      log.logInfo("VNCP_HubInit");
      // Make some cleaning
      Enumeration<?> attributeEnum = context.getAttributeNames();
      while (attributeEnum.hasMoreElements()) {
        String key = (String) attributeEnum.nextElement();
        if (key.startsWith("VNCProxySession_")) {
          VNCPSession theSessionToTest = (VNCPSession) context.getAttribute(key);
          long lm = theSessionToTest.getLastMove();
          if (System.currentTimeMillis() - lm > TIMEOUT) {
            log.logInfo("- Cleaning Oudated Session : [" + theSessionToTest.getSid() + "]");
            closeSession(theSessionToTest, context);
            logStats(theSessionToTest);
          }
        }
      }
      // READ Init From Applet
      ObjectInputStream inputFromApplet = new ObjectInputStream(in);
      VNCPActionData objActionData = (VNCPActionData) inputFromApplet.readObject();

      // Validation is needed for any other action than SERVER or VIEWER
      if (objActionData.getAction() > 1) {
        log.logInfo("- Validation needed for action : " + objActionData.getAction());
        VNCPSession theSession = (VNCPSession) context.getAttribute("VNCProxySession_" + objActionData.getSessionId());
        if (theSession != null) {
          if (objActionData.getValidationCode() != null) {
            String validation = new String(decryptInBytesWithAES(objActionData.getValidationCode(), theSession.getKey()), "UTF-8");
            if (!validation.startsWith("Validation OK")) {
              log.logInfo("- Invalid validation code !");
              // Error
              objActionDataBack = new VNCPActionData("Invalid Validation Code");
              // Send response
              ObjectOutputStream oos = new ObjectOutputStream(outstr);
              oos.writeObject(objActionDataBack);
              oos.flush();
              oos.close();
              return;
            }
          } else {
            log.logInfo("- Null validation code !");
            // Error
            objActionDataBack = new VNCPActionData("Null Validation Code");
            // Send response
            ObjectOutputStream oos = new ObjectOutputStream(outstr);
            oos.writeObject(objActionDataBack);
            oos.flush();
            oos.close();
            return;
          }
        } else {
          log.logInfo("- The Session is null ! ");
          // Error
          objActionDataBack = new VNCPActionData("Session Closed");
          // Send response
          ObjectOutputStream oos = new ObjectOutputStream(outstr);
          oos.writeObject(objActionDataBack);
          oos.flush();
          oos.close();
          return;
        }
        log.logInfo("- Validation code OK");
      }

      // PERFORM Given Action
      switch (objActionData.getAction()) {

        case 0:
          // SERVER
          log.logInfo("SERVER :");
          // VNC Server ask for Session creation
          // Random session number
          Random rand = new java.util.Random();
          int sessionId = SID_LOWER_LIMIT + rand.nextInt(SID_UPPER_LIMIT - SID_LOWER_LIMIT);
          // Check if session number if free
          while (context.getAttribute("VNCProxySession_" + sessionId) != null) {
            sessionId++;
          }
          log.logInfo("- SID generated : " + sessionId);
          // Create VNCPSession Object
          VNCPSession newSession = new VNCPSession(sessionId);
          SecretKey sk = generateKeyAES128();
          newSession.setKey(sk.getEncoded());
          newSession.setStartDate(System.currentTimeMillis());
          newSession.setServerIPWan(getIPWan(request));
          // Put the new Session in context
          log.logInfo("- Putting session : [" + "VNCProxySession_" + newSession.getSid() + "] in context");
          context.setAttribute("VNCProxySession_" + newSession.getSid(), newSession);
          // Send back dataInit with new Session info
          objActionDataBack = new VNCPActionData("" + newSession.getSid(), newSession.getKey());
          break;

        case 1:
          // VIEWER
          log.logInfo("VIEWER :");
          // Viewer asking for session
          // Check sessionId sent by viewer
          if (context.getAttribute("VNCProxySession_" + objActionData.getSessionId()) != null) {
            VNCPSession theSession = (VNCPSession) context.getAttribute("VNCProxySession_" + objActionData.getSessionId());
            log.logInfo("- The Session [" + theSession.getSid() + "] is in context");
            if (!theSession.getActive()) {
              log.logInfo("- The Session [" + theSession.getSid() + "] is not active yet");
              objActionDataBack = new VNCPActionData("" + theSession.getSid(), theSession.getKey());
              theSession.setActive(true);
              theSession.setViewerIPWan(getIPWan(request));
              context.setAttribute("VNCProxySession_" + objActionData.getSessionId(), theSession);
            } else {
              // pirate ?
              log.logInfo("- The Session [" + theSession.getSid() + "] already active !");
              objActionDataBack = new VNCPActionData("Session already active");
            }
          } else {
            // pirate ?
            log.logInfo("- Invalid sessionId [" + objActionData.getSessionId() + "] !");
            objActionDataBack = new VNCPActionData("Invalid Session ID");
          }
          break;

        case 2:
          // CLOSE
          log.logInfo("CLOSE :");
          // Send Back Session
          VNCPSession theSession = (VNCPSession) context.getAttribute("VNCProxySession_" + objActionData.getSessionId());
          if (theSession != null) {
            log.logInfo("- The Session [" + theSession.getSid() + "] is in context, closing...");
            objActionDataBack = new VNCPActionData("Session closed OK");
            // Close...
            closeSession(theSession, context);
            // Send the session for history
            logStats(theSession);
          } else {
            log.logInfo("- The Session [" + objActionData.getSessionId() + "] is closed");
            objActionDataBack = new VNCPActionData("Session closed");
          }
          break;

        default:
          // ERROR
          log.logInfo("- Invalid Action !");
          objActionDataBack = new VNCPActionData("Invalid Action");
          break;
      }

      // Send response
      ObjectOutputStream oos = new ObjectOutputStream(outstr);
      oos.writeObject(objActionDataBack);
      oos.flush();
      oos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * This method overrides the Servlet doGet method. It is just there for
   * testing purpose.
   * 
   * @param request
   *          The HTTP request
   * @param response
   *          The HTTP response
   * @throws IOException
   *           Any IO Exception
   * @throws ServletException
   *           Any Servlet Exception
   */
  @Override
  public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    DataOutputStream dos = new DataOutputStream(response.getOutputStream());
    StringBuffer sb = new StringBuffer();
    sb.append("I'm VNCPHubAction...");
    dos.write(sb.toString().getBytes());
    dos.flush();

  }

  /**
   * This methods generate a 128 Bit AES Key
   * 
   * @return The AES SecretKey
   */
  public final SecretKey generateKeyAES128() throws Exception {
    VNCPLog log = new VNCPLog();
    KeyGenerator keyGen = null;
    try {
      keyGen = KeyGenerator.getInstance("AES");
      keyGen.init(AES_KEY_LEN);
      return keyGen.generateKey();
    } catch (Exception e) {
      log.logError("Error generateKeyAES128 : " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * This methods decrypt an AES encoded byte array using a given AES key
   * 
   * @param ciphertext
   *          The byte array to decrypt
   * @param bytesKey
   *          The AES key
   * @return The decrypted byte array
   * @throws Exception
   *           Any Exception
   */
  private byte[] decryptInBytesWithAES(final byte[] ciphertext, final byte[] bytesKey) throws Exception {
    byte[] decrypted = null;
    Cipher cipher = Cipher.getInstance("AES"); //$NON-NLS-1$
    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(bytesKey, "AES"));
    decrypted = cipher.doFinal(ciphertext);
    return decrypted;
  }

  /**
   * This methods retrieves the IP Wan address of the VNCProxy Applet that sends
   * the request
   * 
   * @param request
   *          The request
   * @return The WAN IP Address the request is coming from
   * @throws Exception
   *           Any Exception
   */
  private String getIPWan(final HttpServletRequest request) throws Exception {
    return request.getHeader("X-Forwarded-For") + "/" + request.getRemoteAddr();
  }

  /**
   * This methods close the given VNCPSession
   * 
   * @param theSession
   *          The current VNCPSession
   * @param context
   *          The current Servlet request context
   */
  public final void closeSession(final VNCPSession theSession, final ServletContext context) {
    VNCPLog log = new VNCPLog();
    TimeZone tz = TimeZone.getTimeZone("GMT");
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("zzz yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(tz);
    log.logInfo("------------------------------------");
    log.logInfo("Removing Session : " + theSession.getSid());
    log.logInfo(" - startDate : " + sdf.format(new Date(theSession.getStartDate())).toString());
    log.logInfo(" - lastMove : " + sdf.format(new Date(theSession.getLastMove())).toString());
    log.logInfo(" - nbRequests : " + theSession.getNbRequests());
    log.logInfo(" - dataSize : " + theSession.getDataSize());
    context.removeAttribute("VNCProxySession_" + theSession.getSid());
    Enumeration<?> attributeEnum = context.getAttributeNames();
    while (attributeEnum.hasMoreElements()) {
      String key = (String) attributeEnum.nextElement();
      if (key.startsWith("" + theSession.getSid())) {
        log.logInfo("Removing Key : " + key);
        context.removeAttribute(key);
      }
    }

  }

  /**
   * This method logs some statistics about the VNCPSession when closing
   * 
   * @param theSession
   *          The VNCPSession
   * @throws Exception
   *           Any Exception
   */
  public final void logStats(final VNCPSession theSession) throws Exception {
    VNCPLog log = new VNCPLog();

    StringBuffer stats = new StringBuffer();
    stats.append("'" + new Timestamp(theSession.getStartDate()) + "',");
    stats.append("'" + theSession.getDataSize() + "',");
    stats.append("'" + theSession.getSid() + "',");
    stats.append("'" + new Timestamp(theSession.getLastMove()) + "',");
    stats.append("'" + theSession.getNbRequests() + "',");
    stats.append("'" + theSession.getServerIPWan() + "',");
    stats.append("'" + theSession.getViewerIPWan() + "',");

    //
    log.logInfo("------------------------------------");
    log.logInfo("SESSION STATS : [" + stats.toString() + "]");

  }

}
