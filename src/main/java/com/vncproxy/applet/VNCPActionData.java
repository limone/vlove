/**
 * This file is part of the VNCProxy program.
 * <p>
 * VNCPRoxy Summary :
 * In just one clic (no setup) this Java Applet based solution allows you to run VNC Server / VNC Viewer through an HTTP AES encrypted tunnel.
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
package com.vncproxy.applet;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class implements the VNCPActionData object
 * 
 * @author Rémi Serrano
 * 
 */
public class VNCPActionData implements java.io.Serializable {
  /**
   * This variable is needed to implements java.io.Serializable
   */
  private static final long serialVersionUID = 1234567L;

  /**
   * This variable is used when the VNCProxy Hub sends back the SessionID + the
   * AES Key to the VNCProxy Applet
   */
  private byte[]            key;

  /**
   * This variable is used when an action needs to be validated by the AES key
   */
  private byte[]            validationCode;

  /**
   * This variable is used to identify the sessionId
   */
  private String            sessionId;

  /**
   * This variable represents the action code to execute on the VNCProxy Hub
   */
  private int               action;

  /**
   * This variable holds the result message after an action had been executed
   */
  private String            result;

  /**
   * This variable holds the error after an action had been executed
   */
  private String            error;

  /**
   * This constructor is used to build the ActionData that will be sent from the
   * VNCProxy Applet to the VNCProxy Hub. Action code 0 = Start a SERVER session
   * Action code 1 = Start a VIEWER session Action code 2 = Close the given
   * session
   * 
   * @param pSessionId
   *          The sessionId to perform the action on
   * @param pAction
   *          The action code to perform on the VNCProxy Hub
   * @param pKey
   *          The AES key used for ActionData that need to be secure
   */
  public VNCPActionData(final String pSessionId, final int pAction, final byte[] pKey) {
    this.sessionId = pSessionId;
    this.action = pAction;
    this.result = null;
    this.error = null;
    this.key = null;
    if (pKey != null) {
      try {
        this.validationCode = cryptWithAES(("Validation OK" + System.currentTimeMillis()).getBytes("UTF-8"), pKey);
      } catch (Exception e) {
        e.printStackTrace();
        this.validationCode = null;
      }
    }
  }

  /**
   * This constructor is used to build the ActionData that will be sent from The
   * VNCproxy Hub to the VNCProxy Applet after the session Init
   * 
   * @param pKey
   *          The Session AES Key
   * @param pSessionId
   *          The SessionId
   */
  public VNCPActionData(final String pSessionId, final byte[] pKey) {
    this.sessionId = pSessionId;
    this.action = -1;
    this.result = null;
    this.error = null;
    this.key = pKey;
    this.validationCode = null;
  }

  /**
   * This constructor is used to build the ActionData that will be sent from The
   * VNCproxy Hub to the VNCProxy Applet to send the result of an action
   * 
   * @param pMsg
   *          The result message to send back to the VNCProxy Applet
   * @param pWarn
   *          The warning message to send back to the VNCProxy Applet
   */
  public VNCPActionData(final String pMsg, final String pWarn) {
    this.sessionId = null;
    this.action = -1;
    this.result = pMsg;
    this.error = pWarn;
    this.key = null;
    this.validationCode = null;
  }

  /**
   * This constructor is used to build the ActionData that will be sent from The
   * VNCproxy Hub to the VNCProxy Applet to send the error message of an action
   * 
   * @param pErr
   *          The error message to send back to the VNCProxy Applet
   */
  public VNCPActionData(final String pErr) {
    this.sessionId = null;
    this.action = -1;
    this.result = null;
    this.error = pErr;
    this.key = null;
    this.validationCode = null;
  }

  /**
   * This method is used to encrypt a plain text string using an AES key
   * 
   * @param pPlainText
   *          Plain text to encrypt
   * @param pBytesKey
   *          AES key used to encrypt
   * @throws Exception
   *           Any AES encryption error
   * @return A byte array of the encrypted text
   */
  private byte[] cryptWithAES(final byte[] pPlainText, final byte[] pBytesKey) throws Exception {
    byte[] encrypted = null;
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(pBytesKey, "AES"));
    encrypted = cipher.doFinal(pPlainText);
    return encrypted;
  }

  // Setters / Getters
  /**
   * @return The AES Key for this session
   */
  public final byte[] getKey() {
    return this.key;
  }

  /**
   * @return The validation code of this ActionData
   */
  public final byte[] getValidationCode() {
    return this.validationCode;
  }

  /**
   * @return The sessionId
   */
  public final String getSessionId() {
    return this.sessionId;
  }

  /**
   * @return The action code
   */
  public final int getAction() {
    return this.action;
  }

  /**
   * @return The result message
   */
  public final String getResult() {
    return this.result;
  }

  /**
   * @return The error message
   */
  public final String getError() {
    return this.error;
  }

  /**
   * @param b
   *          The AES key
   */
  public final void setKey(final byte[] b) {
    this.key = b;
  }

  /**
   * @param b
   *          The validation code
   */
  public final void setValidationCode(final byte[] b) {
    this.validationCode = b;
  }

  /**
   * @param s
   *          The sessionId
   */
  public final void setSessionId(final String s) {
    this.sessionId = s;
  }

  /**
   * @param i
   *          The action code
   */
  public final void setAction(final int i) {
    this.action = i;
  }

  /**
   * @param s
   *          The result message
   */
  public final void setResult(final String s) {
    this.result = s;
  }

  /**
   * @param s
   *          The error message
   */
  public final void setError(final String s) {
    this.error = s;
  }

}
