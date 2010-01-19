package vlove.model;

import java.io.Serializable;

public class ConfigForm implements Serializable {
	private String libvirtUrl;
	
	public ConfigForm() {
		// empty
	}

	public String getLibvirtUrl() {
		return libvirtUrl;
	}

	public void setLibvirtUrl(String libvirtUrl) {
		this.libvirtUrl = libvirtUrl;
	}
}