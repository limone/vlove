package vlove.model;

import java.io.Serializable;

import org.libvirt.DomainInfo.DomainState;

public class InternalDomain implements Serializable {
	private int domainId;
	private String domainName;
	private DomainState state;
	private long totalMemory;
	private long cpuTime;
	private long memoryUsage;
	
	public InternalDomain() {
		// empty
	}

	public InternalDomain(int domainId, String domainName, DomainState state, long totalMemory, long cpuTime, long memoryUsage) {
		this.domainId = domainId;
		this.domainName = domainName;
		this.state = state;
		this.totalMemory = totalMemory;
		this.cpuTime = cpuTime;
		this.memoryUsage = memoryUsage;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
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