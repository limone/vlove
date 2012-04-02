/**
 * This file is part of the VNCProxy program.
 * <p>
 * VNCProxy Summary :
 * In just one clic (no setup) this Java Applet based solution allows you to run VNC Server / VNC Viewer through an HTTP AES encrypted tunnel.
 * As it is full HTTP, there is no proxy or firewall setup needed.
 * <p>
 * Copyright (C) 2009  - Remi Serrano - http://www.vncproxy.com
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

/**
 * This class implements the VNCPSession object.
 * 
 * @author Remi Serrano
 * 
 */
public class VNCPSession implements java.io.Serializable {
  /**
   * This constant variable is needed to implements java.io.Serializable
   */
  private static final long serialVersionUID = 12345678L;

  /**
   * This constant variable represents the AES Key size in Bytes
   */
  private static final int  KEY_SIZE         = 16;

  /**
   * This variable represents the VNCPSession state
   */
  private boolean           active;

  /**
   * This variable represents the AES key byte array
   */
  private byte[]            key;

  /**
   * This variable represents the VNCPSession id
   */
  private int               sid;

  /**
   * This variable represents the total amount of data handled by the
   * VNCPSession
   */
  private long              dataSize;

  /**
   * This variable represents the last move time
   */
  private long              lastMove;

  /**
   * This variable represents the total amount of requests handled by the
   * VNCPSession
   */
  private long              nbRequests;

  /**
   * This variable represents the start date time
   */
  private long              startDate;

  /**
   * This variable represents the VNCProxy Viewer Applet IP Wan address
   */
  private String            viewerIPWan;

  /**
   * This variable represents the VNCProxy Server Applet IP Wan address
   */
  private String            serverIPWan;

  /**
   * This constructor is used to initialize the VNCPSession object
   * 
   * @param pSid
   *          The VNCPSession Id
   */
  public VNCPSession(final int pSid) {
    this.sid = pSid;
    key = new byte[KEY_SIZE];
    this.startDate = System.currentTimeMillis();
    this.lastMove = this.startDate;
    this.dataSize = 0;
    this.nbRequests = 0;
    this.active = false;
  }

  // SETTERS / GETTERS

  /**
   * @return True if the session is active. False otherwise.
   */
  public final boolean getActive() {
    return this.active;
  }

  /**
   * @return The session AES key.
   */
  public final byte[] getKey() {
    return this.key;
  }

  /**
   * @return The session Id
   */
  public final int getSid() {
    return this.sid;
  }

  /**
   * @return The total amount of data that had been transfered by the session.
   */
  public final long getDataSize() {
    return this.dataSize;
  }

  /**
   * @return The timestamp of the session last data transfer.
   */
  public final long getLastMove() {
    return this.lastMove;
  }

  /**
   * @return The session number of requests.
   */
  public final long getNbRequests() {
    return this.nbRequests;
  }

  /**
   * @return The timestamp of the session start.
   */
  public final long getStartDate() {
    return this.startDate;
  }

  /**
   * @return The WAN IP address of the viewer side.
   */
  public final String getViewerIPWan() {
    return this.viewerIPWan;
  }

  /**
   * @return The WAN IP Address of the server side.
   */
  public final String getServerIPWan() {
    return this.serverIPWan;
  }

  /**
   * @param b
   *          The boolean saying if the session is active or not.
   */
  public final void setActive(final boolean b) {
    this.active = b;
  }

  /**
   * @param b
   *          TODO
   */
  public final void setKey(final byte[] b) {
    this.key = b;
  }

  /**
   * @param i
   *          TODO
   */
  public final void setSid(final int i) {
    this.sid = i;
  }

  /**
   * @param l
   *          TODO
   */
  public final void setDataSize(final long l) {
    this.dataSize = l;
  }

  /**
   * @param l
   *          TODO
   */
  public final void setLastMove(final long l) {
    this.lastMove = l;
  }

  /**
   * @param l
   *          TODO
   */
  public final void setNbRequests(final long l) {
    this.nbRequests = l;
  }

  /**
   * @param l
   *          TODO
   */
  public final void setStartDate(final long l) {
    this.startDate = l;
  }

  /**
   * @param s
   *          TODO
   */
  public final void setViewerIPWan(final String s) {
    this.viewerIPWan = s;
  }

  /**
   * @param s
   *          TODO
   */
  public final void setServerIPWan(final String s) {
    this.serverIPWan = s;
  }
}