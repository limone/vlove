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
 * POJO representing the fields on the ConfigPage
 * 
 * @author Michael Laccetti
 */
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