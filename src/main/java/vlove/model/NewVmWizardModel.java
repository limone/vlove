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
 * POJO representing a new VM.
 * 
 * @author Michael Laccetti
 */
public class NewVmWizardModel implements Serializable {
	private String vmName;
	private String suite;
	private String arch;
	private Integer numProcs;
	private Integer memSize;
	private InternalStoragePool storagePool;
	private Integer diskSize;
	private String networks;
	private String bridge;

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getSuite() {
		return suite;
	}

	public void setSuite(String suite) {
		this.suite = suite;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public Integer getNumProcs() {
		return numProcs;
	}

	public void setNumProcs(Integer numProcs) {
		this.numProcs = numProcs;
	}

	public Integer getMemSize() {
		return memSize;
	}

	public void setMemSize(Integer memSize) {
		this.memSize = memSize;
	}

	public InternalStoragePool getStoragePool() {
		return storagePool;
	}

	public void setStoragePool(InternalStoragePool storagePool) {
		this.storagePool = storagePool;
	}

	public Integer getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(Integer diskSize) {
		this.diskSize = diskSize;
	}

	public String getNetworks() {
		return networks;
	}

	public void setNetworks(String networks) {
		this.networks = networks;
	}

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}
}