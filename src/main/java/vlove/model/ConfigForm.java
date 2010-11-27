package vlove.model;

import java.io.Serializable;

public class ConfigForm implements Serializable {
	private String libvirtUrl;
	private String vmbuilderLocation;
	private String sudoPassword;
	
	public ConfigForm() {
		// empty
	}

	public String getLibvirtUrl() {
		return libvirtUrl;
	}

	public void setLibvirtUrl(String libvirtUrl) {
		this.libvirtUrl = libvirtUrl;
	}

	public String getVmbuilderLocation() {
		return vmbuilderLocation;
	}

	public void setVmbuilderLocation(String vmbuilderLocation) {
		this.vmbuilderLocation = vmbuilderLocation;
	}

	public String getSudoPassword() {
		return sudoPassword;
	}

	public void setSudoPassword(String sudoPassword) {
		this.sudoPassword = sudoPassword;
	}
}