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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.vncproxy.applet.VNCPSession;

/**
 * This class implements the VNCPHubListener Servlet. The VNCPHubListener is in
 * charge of the data transfer from a VNCProxy Applet to another.
 * 
 * @author Rémi Serrano
 * 
 */
public class VNCPHubListener extends HttpServlet {
  /**
   * This constant variable represents the NUMBER_24
   */
  private static final int  NUMBER_24          = 24;

  /**
   * This constant variable represents the NUMBER_16
   */
  private static final int  NUMBER_16          = 16;

  /**
   * This constant variable represents the NUMBER_8
   */
  private static final int  NUMBER_8           = 8;

  /**
   * This constant variable represents the NUMBER_3
   */
  private static final int  NUMBER_3           = 3;

  /**
   * This constant variable represents the NUMBER_0XFF
   */
  private static final int  NUMBER_0XFF        = 0xff;

  /**
   * This constant variable is needed to implements java.io.Serializable
   */
  private static final long serialVersionUID   = 1L;

  /**
   * This constant variable represents the time out value in milliseconds
   */
  private static final long TIMEOUT            = 60 * 60 * 1000;

  /**
   * This constant variable represents the byte size of the "sid" integer
   */
  private static final int  SID_BYTE_SIZE      = 4;

  /**
   * This constant variable represents the Offset used in the data length
   * computation
   */
  private static final int  CONTENT_LEN_OFFSET = 1;

  /**
   * This constant variable represents the time to wait for while waiting for
   * the buffer to be ready, in millisecond
   */
  private static final long SLEEP_TIME         = 2;

  /**
   * This method overrides the Servlet doPost method. It is in charge of the
   * data transfer from a VNCProxy Applet to another.
   * 
   * @param request
   *          The HTTP request
   * @param response
   *          The HTTP response
   * @throws IOException
   *           Any IO Exception
   * @throws ServletException
   *           Any Servlet Exception
   * 
   */
  @Override
  public final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

    ServletContext context = getServletConfig().getServletContext();
    DataInputStream dis = new DataInputStream(request.getInputStream());
    DataOutputStream dos = new DataOutputStream(response.getOutputStream());

    try {
      // READ From Applet
      byte[] sessionId = new byte[SID_BYTE_SIZE];
      byte[] type = new byte[1];
      byte[] data = new byte[request.getContentLength() - (SID_BYTE_SIZE + CONTENT_LEN_OFFSET)];
      dis.readFully(sessionId);
      dis.readFully(type);
      String strSessionId = "" + byteArrayToInt(sessionId, 0);
      String bufferName = strSessionId + "_" + type[0];
      String flagName = bufferName + "_F";

      // Check if Session is alive
      if (context.getAttribute("VNCProxySession_" + strSessionId) == null) {
        response.setContentLength(0);
        dos.flush();
        return;
      }

      if (data.length == 0) {
        // READ FROM HUB
        // Wait for the buffer data to be ready
        long timeOutBegin = System.currentTimeMillis();
        while (context.getAttribute(flagName) == null && (System.currentTimeMillis() - timeOutBegin) < TIMEOUT) {
          try {
            Thread.sleep(SLEEP_TIME);
          } catch (Exception e) {
            System.err.println("Error while sleeping : " + e.getMessage());
          }
        }
        // Detects time-out
        if ((System.currentTimeMillis() - timeOutBegin) > TIMEOUT) { return; }
        if (context.getAttribute(bufferName) != null) {
          // Send back buffer
          byte[] returnedBuffer = (byte[]) context.getAttribute(bufferName);
          response.setContentLength(returnedBuffer.length);
          dos.write(returnedBuffer);
          context.removeAttribute(bufferName);
          context.removeAttribute(flagName);
        } else {
          response.setContentLength(0);
        }
      } else {
        // SEND TO HUB
        dis.readFully(data);
        if (data.length > 0) {
          // Wait for the buffer to be released before putting data in it
          long timeOutBegin = System.currentTimeMillis();
          while (context.getAttribute(flagName) != null && (System.currentTimeMillis() - timeOutBegin) < TIMEOUT) {
            try {
              Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {
              System.err.println("Error while sleeping : " + e.getMessage());
            }
          }
          // Detects time-out
          if ((System.currentTimeMillis() - timeOutBegin) > TIMEOUT) { return; }
          // Put data in buffer
          context.setAttribute(bufferName, data);
          context.setAttribute(flagName, "f");
        }
        response.setContentLength(0);
      }
      // Send response in any case
      dos.flush();
      // Statistics on the session
      updateSession(context, strSessionId, data);

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
    sb.append("I'm VNCPHubListener...");
    dos.write(sb.toString().getBytes());
    dos.flush();

  }

  /**
   * This method is used to update the current VNCPSession (data length
   * transfered + last move update time)
   * 
   * @param context
   *          The Servlet request context
   * @param sessionId
   *          The current sessionId
   * @param data
   *          The data byte array handled by the VNCPHubListener
   */
  public final synchronized void updateSession(final ServletContext context, final String sessionId, final byte[] data) {
    VNCPSession theSession = (VNCPSession) context.getAttribute("VNCProxySession_" + sessionId);
    if (theSession != null) {
      theSession.setLastMove(System.currentTimeMillis());
      theSession.setNbRequests(theSession.getNbRequests() + 1);
      theSession.setDataSize(theSession.getDataSize() + data.length);
      context.setAttribute("VNCProxySession_" + sessionId, theSession);

    }

  }

  /**
   * This method returns the integer value of an integer byte array
   * representation
   * 
   * @param b
   *          The input byte array
   * @param offset
   *          The offset
   * @return The integer value of the given byte array
   */
  private int byteArrayToInt(final byte[] b, final int offset) {
    return (b[0] << NUMBER_24) + ((b[1] & NUMBER_0XFF) << NUMBER_16) + ((b[2] & NUMBER_0XFF) << NUMBER_8) + (b[NUMBER_3] & NUMBER_0XFF);
  }

}
