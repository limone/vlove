/**
 * vlove - web based virtual machine management
 * Copyright (C) 2010 Limone Fresco Limited
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package vlove.model;

import java.io.Serializable;

/**
 * POJO representing the hardware capabilities of the machine that libvirt is
 * running on.
 * 
 * @author Michael Laccetti
 */
public class Capabilities implements Serializable {
  private String  cpuArch;
  private String  model;
  private String  vendor;
  private Integer numProcs;

  public Capabilities() {
    // empty
  }

  public Capabilities(String cpuArch, String model, String vendor, Integer numProcs) {
    this.cpuArch = cpuArch;
    this.model = model;
    this.vendor = vendor;
    this.numProcs = numProcs;
  }

  public String getCpuArch() {
    return cpuArch;
  }

  public void setCpuArch(String cpuArch) {
    this.cpuArch = cpuArch;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getVendor() {
    return vendor;
  }

  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public Integer getNumProcs() {
    return numProcs;
  }

  public void setNumProcs(Integer numProcs) {
    this.numProcs = numProcs;
  }
}