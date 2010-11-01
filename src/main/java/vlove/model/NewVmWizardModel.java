package vlove.model;

import java.io.Serializable;

public class NewVmWizardModel implements Serializable {
	private String vmName;
	private String suite;
	private String arch;
	private Integer numProcs;
	private Integer memSize;
	private String storagePools;
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

	public String getStoragePools() {
		return storagePools;
	}

	public void setStoragePools(String storagePools) {
		this.storagePools = storagePools;
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