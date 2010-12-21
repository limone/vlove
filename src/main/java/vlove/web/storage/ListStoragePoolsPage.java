package vlove.web.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.libvirt.StoragePoolInfo.StoragePoolState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.VirtException;
import vlove.model.InternalStoragePool;
import vlove.virt.VirtManager;
import vlove.web.BasePage;
import vlove.web.error.ErrorPage;

@MountPath(path="/storage/list")
public class ListStoragePoolsPage extends BasePage {
	transient final Logger log = LoggerFactory.getLogger(getClass());
	
	@SpringBean
	private VirtManager vm;
	
	public ListStoragePoolsPage() {
		super();
		
		add(new BookmarkablePageLink<Object>("createPoolLink", CreateStoragePoolPage.class));
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		add(container.setOutputMarkupId(true));
		
		final ReloadableModel reloadableModel;
		try {
			reloadableModel = new ReloadableModel(vm);
		} catch (VirtException ve) {
			log.error("Could not create VM model.", ve);
			setRedirect(true);
			setResponsePage(ErrorPage.class);
			return;
		}
		
		final ListView<InternalStoragePool> vms = new ListView<InternalStoragePool>("repeater", reloadableModel) {
			@Override
			protected void populateItem(ListItem<InternalStoragePool> item) {
				final InternalStoragePool sp = item.getModelObject();
				final String poolId = sp.getUuid();
				final StoragePoolState s = sp.getState();

				item.add(new Label("name", sp.getName()));
				item.add(new Label("volumes", Integer.toString(sp.getNumVols())));
				item.add(new Label("capacity", getMbString(sp.getCapacity())));
				item.add(new Label("available", getMbString(sp.getAvailable())));
				item.add(new Label("state", s.toString()));

				// Buttons
				final AjaxLink<Object> power = new AjaxLink<Object>("power") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						// TODO
					}
				};
				if (s == StoragePoolState.VIR_STORAGE_POOL_RUNNING) {
					power.add(new NonCachingImage("powerImage", new ContextRelativeResource("/images/power_off.png")));
				} else {
					power.add(new NonCachingImage("powerImage", new ContextRelativeResource("/images/power_on.png")));
				}
				item.add(power.setOutputMarkupId(true));

				final AjaxLink<Object> destroy = new AjaxLink<Object>("destroy") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Destroying {}.", poolId);
						// TODO
					}
				};
				destroy.add(new Image("destroyImage", new ContextRelativeResource("/images/destroy.png")));
				item.add(destroy.setOutputMarkupId(true).setEnabled(s != StoragePoolState.VIR_STORAGE_POOL_INACTIVE));
			}
		};
		container.add(vms.setMarkupId("repeater").setOutputMarkupId(true));
	}
	
	private static final class ReloadableModel extends ListModel<InternalStoragePool> {
		private final VirtManager virtMgr;

		public ReloadableModel(VirtManager vm) throws VirtException {
			this.virtMgr = vm;
			setObject(vm.getStoragePools());
		}

		public void reload() throws VirtException {
			setObject(virtMgr.getStoragePools());
		}
	}
	
	final String getMbString(long size) {
		return Long.toString(size / (1024l * 1024l));
	}
}