package vlove.web.vms;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.virt.VirtManager;
import vlove.web.BasePage;

@MountPath(path="/vms/list")
public class VmListPage extends BasePage {
	@SpringBean
	private VirtManager vm;
	
	public VmListPage() {
		super();
	}
}