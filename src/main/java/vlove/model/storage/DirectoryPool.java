package vlove.model.storage;

public class DirectoryPool extends AbstractStoragePool {
	public DirectoryPool() {
		this.type = StoragePoolType.DIR;
	}
	
	private String targetPath;

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
}