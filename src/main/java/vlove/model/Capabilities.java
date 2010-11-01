package vlove.model;

import java.io.Serializable;

public class Capabilities implements Serializable {
	private String cpuArch;
	private String model;
	private String vendor;
	private Integer numProcs;

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