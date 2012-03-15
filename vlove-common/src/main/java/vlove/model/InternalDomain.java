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
 * The internal representation of a libvirt Domain object.
 * 
 * @author Michael Laccetti
 */
public class InternalDomain implements Serializable {
	private int domainId;
	private String uuid;
	private String domainName;
	private DomainState state;
	private long totalMemory;
	private long cpuTime;
	private long memoryUsage;
	
	public InternalDomain() {
		// empty
	}

	public InternalDomain(int domainId, String uuid, String domainName, DomainState state, long totalMemory, long cpuTime, long memoryUsage) {
		this.domainId = domainId;
		this.uuid = uuid;
		this.domainName = domainName;
		this.state = state;
		this.totalMemory = totalMemory;
		this.cpuTime = cpuTime;
		this.memoryUsage = memoryUsage;
	}

	@Override
	public String toString() {
		return "InternalDomain [domainId=" + domainId + ", uuid=" + uuid + ", domainName=" + domainName + ", state=" + state + ", totalMemory=" + totalMemory + ", cpuTime=" + cpuTime + ", memoryUsage=" + memoryUsage + "]";
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public DomainState getState() {
		return state;
	}

	public void setState(DomainState state) {
		this.state = state;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(long cpuTime) {
		this.cpuTime = cpuTime;
	}

	public long getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(long memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
}