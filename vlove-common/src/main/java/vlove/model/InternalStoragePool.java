package vlove.model;

import java.io.Serializable;

import org.libvirt.StoragePoolInfo.StoragePoolState;

public class InternalStoragePool implements Serializable {
	private String name;
	private String uuid;
	private int numVols;
	private StoragePoolState state;
	private long capacity;
	private long available;
	private boolean autostart;
	
	public InternalStoragePool() {
		// empty
	}

	public InternalStoragePool(String name, String uuid, int numVols, StoragePoolState state, long capacity, long available, boolean autostart) {
		this.name = name;
		this.uuid = uuid;
		this.numVols = numVols;
		this.state = state;
		this.capacity = capacity;
		this.available = available;
		this.autostart = autostart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getNumVols() {
		return numVols;
	}

	public void setNumVols(int numVols) {
		this.numVols = numVols;
	}

	public StoragePoolState getState() {
		return state;
	}

	public void setState(StoragePoolState state) {
		this.state = state;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getAvailable() {
		return available;
	}

	public void setAvailable(long available) {
		this.available = available;
	}

	public boolean isAutostart() {
		return autostart;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}
}