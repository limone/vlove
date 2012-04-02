/**
 * This file is part of the VNCProxy program.
 * <p>
 * VNCPRoxy Summary :
 * In just one clic (no setup) this Java Applet based solution
 * allows you to run VNC Server / VNC Viewer
 * through an HTTP AES encrypted tunnel.
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

package com.vncproxy.hub;

import java.util.Date;
import java.util.TimeZone;

/**
 * This class implements the VNCPLog object. The VNPLog is a very basic console
 * logger that manages 2 log level (info, error) and format the messages to log
 * by adding the GMTDate.
 * 
 * @author Remi Serrano
 * 
 */
public class VNCPLog {
  /**
   * This variable is used to hold the GMT TimeZone and the Date format
   */
  private java.text.SimpleDateFormat sdf = null;

  /**
   * This constructor is used to initialize the VNCPLog object When it is
   * called, it initialized the sdf variable with the GMT TimeZone and the right
   * Date format
   */
  public VNCPLog() {
    TimeZone tz = TimeZone.getTimeZone("GMT");
    sdf = new java.text.SimpleDateFormat("zzz yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(tz);

  }

  /**
   * This method sends the formated message to the console on the "System.out"
   * channel
   * 
   * @param message
   *          The message to send to the console
   */
  public final void logInfo(final String message) {
    System.out.println("[" + gmtDate() + "] : " + message);
  }

  /**
   * This method sends the formated message to the console on the "System.err"
   * channel
   * 
   * @param message
   *          The message to send to the console
   */
  public final void logError(final String message) {
    System.err.println("[" + gmtDate() + "] : " + message);
  }

  /**
   * This method returns a SimpleDateFormat formated String of the current
   * system time
   * 
   * @return Formated string of the current system time
   */
  private String gmtDate() {
    return sdf.format(new Date(System.currentTimeMillis())).toString();
  }
}