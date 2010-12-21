package vlove.web.vms;

import static vlove.model.TypeConverter.domainState;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.libvirt.DomainInfo.DomainState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.VirtException;
import vlove.model.InternalDomain;
import vlove.model.Pair;
import vlove.virt.VirtManager;
import vlove.web.BasePage;
import vlove.web.error.ErrorPage;

import com.sun.management.OperatingSystemMXBean;

@MountPath(path = "/vms/list")
public class VmListPage extends BasePage {
	transient final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	VirtManager vm;
	
	final DecimalFormat usage = new DecimalFormat("##0.00");

	final Map<String, List<Long>> cpuStat = new HashMap<String, List<Long>>();
	final Map<String, List<String>> memStat = new HashMap<String, List<String>>();
	
	long lastTime = 0l;
	long lastCpuTime = 0l;
	
	private final long totalMem;

	public VmListPage() {
		super();
		
		// Init stuff
		OperatingSystemMXBean os = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		totalMem = os.getTotalPhysicalMemorySize()/1000;

		add(JavascriptPackageResource.getHeaderContribution("js/jquery-sparkline-1.5.1.min.js"));

		add(new BookmarkablePageLink<Object>("createVmLink", VmCreatePage.class));

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

		final ListView<InternalDomain> vms = new ListView<InternalDomain>("repeater", reloadableModel) {
			@Override
			protected void populateItem(ListItem<InternalDomain> item) {
				final InternalDomain d = item.getModelObject();
				final Integer domainId = d.getDomainId();
				final String uuid = d.getUuid();
				final String domainName = d.getDomainName();
				final DomainState s = d.getState();

				item.add(new Label("name", domainName));
				item.add(new Label("domainId", Integer.toString(d.getDomainId())));
				item.add(new HiddenField<String>("uuid", new PropertyModel<String>(d, "uuid")));

				final Pair<String, String> domainState = domainState(d.getState());
				item.add(domainState._2 == null ? new WebMarkupContainer("statusImage").setVisible(false) : new Image("statusImage", new ContextRelativeResource(domainState._2)));

				final Label status = new Label("status", domainState._1);
				item.add(status.setOutputMarkupId(true));

				final Label memoryUsage = new Label("memoryUsage", "");
				item.add(memoryUsage.setOutputMarkupId(true).setMarkupId("memoryUsage-" + uuid));

				final Label cpuUsage = new Label("cpuUsage", "");
				item.add(cpuUsage.setOutputMarkupId(true).setMarkupId("cpuUsage-" + uuid));

				// Buttons
				final AjaxLink<Object> power = new AjaxLink<Object>("power") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							if (s == DomainState.VIR_DOMAIN_RUNNING) {
								log.debug("Shutting down {}/{}", domainId, domainName);
								vm.shutdown(domainId);
							} else {
								log.debug("Starting/resuming {}/{}.", domainId, domainName);
								if (domainId == 0) {
									vm.start(domainName);
								} else {
									vm.resume(domainId);
								}
							}
							reloadableModel.reload();
							target.addComponent(container);
						} catch (VirtException ve) {
							log.error(String.format("Could not %s domain %s.", s.toString(), domainName), ve);
							// TODO show the user an error
						}
					}
				};
				if (s == DomainState.VIR_DOMAIN_RUNNING) {
					power.add(new NonCachingImage("powerImage", new ContextRelativeResource("/images/power_off.png")));
				} else {
					power.add(new NonCachingImage("powerImage", new ContextRelativeResource("/images/power_on.png")));
				}
				item.add(power.setOutputMarkupId(true));

				final AjaxLink<Object> pause = new AjaxLink<Object>("pause") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Pausing down {}.", domainId);
						try {
							vm.pause(domainId);
							reloadableModel.reload();
							target.addComponent(container);
						} catch (VirtException ve) {
							log.error("Could not pause VM.", ve);
							// TODO show an error to the user
						}
					}
				};
				pause.add(new Image("pauseImage", new ContextRelativeResource("/images/pause.png")));
				item.add(pause.setOutputMarkupId(true).setEnabled(s == DomainState.VIR_DOMAIN_RUNNING));

				final AjaxLink<Object> destroy = new AjaxLink<Object>("destroy") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Destroying {}.", domainId);
						try {
							InternalDomain id = vm.getDomain(domainId);
							memStat.remove(id.getUuid());
							cpuStat.remove(id.getUuid());
							vm.destroy(domainId);
							
							reloadableModel.reload();
							target.addComponent(container);
						} catch (VirtException ve) {
							log.error(String.format("Could not destroy VM with ID %d.", domainId), ve);
						}
					}
				};
				destroy.add(new Image("destroyImage", new ContextRelativeResource("/images/destroy.png")));
				item.add(destroy.setOutputMarkupId(true).setEnabled(s != DomainState.VIR_DOMAIN_SHUTOFF));
			}
		};
		container.add(vms.setMarkupId("repeater").setOutputMarkupId(true));

		final AbstractDefaultAjaxBehavior abstractAjaxBehavior = new AbstractDefaultAjaxBehavior() {
			@Override
			protected void respond(AjaxRequestTarget target) {
				WebRequest r = ((WebRequestCycle) RequestCycle.get()).getWebRequest();
				String[] uuids = r.getParameterMap().get("uuids[]");
				if (uuids != null && uuids.length > 0) {
					for (String uuid : uuids) {
						try {
							updatePerformanceStats(uuid);
						} catch (VirtException ve) {
							log.error(String.format("Could not process stats for %s.", uuid), ve);
						}
					}
				}
				final String cpuStats = String.format("cpuStat=%s;", JSONObject.fromObject(cpuStat).toString());
				final String memStats = String.format("memStat=%s;", JSONObject.fromObject(memStat).toString());
				
				target.appendJavascript(cpuStats);
				target.appendJavascript(memStats);
			}
		};
		add(abstractAjaxBehavior);
		StringBuffer sb = new StringBuffer("var callbackUrl = '");
		sb.append(abstractAjaxBehavior.getCallbackUrl());
		sb.append("';");
		add(new Label("callbackUrl", sb.toString()).setEscapeModelStrings(false));
	}

	void updatePerformanceStats(String uuid) throws VirtException {
		InternalDomain id = vm.getDomainByUUID(uuid);
		if (id.getDomainId() < 0) {
			return;
		}
		
		List<Long> cpu = cpuStat.get(uuid);
		if (cpu == null) {
			cpu = new ArrayList<Long>();
			cpuStat.put(uuid, cpu);
		}
		if (cpu.size() >= 10) {
			cpu.remove(0);
		}
		
		final long currTime = id.getCpuTime();
		if (lastTime == 0) {
			lastCpuTime = currTime;
			lastTime = System.nanoTime();
			cpu.add(0l);
		} else {
			Long rightNow = System.nanoTime();
			
			final long cpuTime = (id.getCpuTime() - lastCpuTime) * 100;
			final long timeChange = (rightNow - lastTime);
			long avg = cpuTime / (timeChange * vm.getCapabilities().getNumProcs());
			cpu.add(avg);
			
			lastTime = rightNow;
			lastCpuTime = currTime;
		}

		List<String> mem = memStat.get(uuid);
		if (mem == null) {
			mem = new ArrayList<String>();
			memStat.put(uuid, mem);
		}

		if (mem.size() >= 10) {
			mem.remove(0);
		}
		final double memUsage = ((double)id.getMemoryUsage()/(double)totalMem)*100;
		mem.add(usage.format(memUsage));
	}

	private static final class ReloadableModel extends ListModel<InternalDomain> {
		private final VirtManager virtMgr;

		public ReloadableModel(VirtManager vm) throws VirtException {
			this.virtMgr = vm;
			setObject(vm.getDomains());
		}

		public void reload() throws VirtException {
			setObject(virtMgr.getDomains());
		}
	}
}