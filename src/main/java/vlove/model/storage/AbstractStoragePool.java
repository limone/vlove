package vlove.model.storage;

public abstract class AbstractStoragePool implements IStoragePool {
	protected StoragePoolType type;
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StoragePoolType getType() {
		return type;
	}

	public void setType(StoragePoolType type) {
		this.type = type;
	}
}