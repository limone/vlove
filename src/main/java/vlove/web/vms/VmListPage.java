package vlove.web.vms;

import java.text.DecimalFormat;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.libvirt.DomainInfo.DomainState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.model.InternalDomain;
import vlove.model.Pair;
import vlove.virt.VirtManager;
import vlove.web.BasePage;

import static vlove.model.TypeConverter.domainState;

@MountPath(path = "/vms/list")
public class VmListPage extends BasePage {
	private static final Logger log = LoggerFactory.getLogger(VmListPage.class);

	@SpringBean
	private VirtManager vm;

	private final DecimalFormat usage = new DecimalFormat("##0.00%");

	public VmListPage() {
		super();
		
		final WebMarkupContainer container = new WebMarkupContainer("container");
		add(container.setOutputMarkupId(true));

		final ReloadableModel reloadableModel = new ReloadableModel(vm);
		final ListView<InternalDomain> vms = new ListView<InternalDomain>("repeater", reloadableModel) {
			@Override
			protected void populateItem(ListItem<InternalDomain> item) {
				final InternalDomain d = item.getModelObject();
				final Integer domainId = d.getDomainId();
				final String domainName = d.getDomainName();
				final DomainState s = d.getState();

				item.add(new Label("name", domainName));
				item.add(new Label("domainId", Integer.toString(d.getDomainId())));

				final Pair<String, String> domainState = domainState(d.getState());
				item.add(domainState._2 == null ? new WebMarkupContainer("statusImage").setVisible(false) : new Image("statusImage", new ContextRelativeResource(domainState._2)));
				final Label status = new Label("status", domainState._1);
				item.add(status.setOutputMarkupId(true));

				final Label memoryUsage = new Label("memoryUsage", "");
				item.add(memoryUsage.setOutputMarkupId(true));

				final Label cpuUsage = new Label("cpuUsage", "");
				item.add(cpuUsage.setOutputMarkupId(true));

				// Buttons
				final AjaxLink<Object> start = new AjaxLink<Object>("start") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Starting/resuming {}/{}.", domainId, domainName);
						if (domainId == 0) {
							vm.start(domainName);
							reloadableModel.reload();
							target.addComponent(container);
						} else {
							vm.resume(domainId);
						}
					}
				};
				start.add(new Image("startImage", new ContextRelativeResource("/images/start.png")));
				item.add(start.setOutputMarkupId(true).setEnabled(s == DomainState.VIR_DOMAIN_CRASHED || s == DomainState.VIR_DOMAIN_PAUSED || s == DomainState.VIR_DOMAIN_SHUTOFF));

				final AjaxLink<Object> pause = new AjaxLink<Object>("pause") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Pausing down {}.", domainId);
						vm.pause(domainId);
					}
				};
				pause.add(new Image("pauseImage", new ContextRelativeResource("/images/pause.png")));
				item.add(pause.setOutputMarkupId(true).setEnabled(s == DomainState.VIR_DOMAIN_RUNNING));

				final AjaxLink<Object> stop = new AjaxLink<Object>("stop") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Shutting down {}.", domainId);
						vm.shutdown(domainId);
					}
				};
				stop.add(new Image("stopImage", new ContextRelativeResource("/images/stop.png")));
				item.add(stop.setOutputMarkupId(true).setEnabled(s == DomainState.VIR_DOMAIN_RUNNING || s == DomainState.VIR_DOMAIN_PAUSED));
				
				final AjaxLink<Object> destroy = new AjaxLink<Object>("destroy") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						log.debug("Destroying {}.", domainId);
						vm.destroy(domainId);
						
						reloadableModel.reload();
						target.addComponent(container);
					}
				};
				destroy.add(new Image("destroyImage", new ContextRelativeResource("/images/force_off.png")));
				item.add(destroy.setOutputMarkupId(true).setEnabled(s != DomainState.VIR_DOMAIN_SHUTOFF));
				
				item.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)) {
					private long previousCpuUsage = 0l;
					private long currentCpuUsage = 0l;

					private double currentMemoryUsage = 0d;

					@Override
					protected void onPostProcessTarget(AjaxRequestTarget target) {
						if (domainId != 0) {
							InternalDomain tmpDomain = vm.getDomain(domainId);
							if (tmpDomain != null) {
								status.setDefaultModelObject(domainState(tmpDomain.getState())._1);
								
								// Reset the buttons based on the status
								start.setEnabled(tmpDomain.getState() == DomainState.VIR_DOMAIN_CRASHED || tmpDomain.getState() == DomainState.VIR_DOMAIN_PAUSED || tmpDomain.getState() == DomainState.VIR_DOMAIN_SHUTOFF);
								pause.setEnabled(tmpDomain.getState() == DomainState.VIR_DOMAIN_RUNNING);
								stop.setEnabled(tmpDomain.getState() == DomainState.VIR_DOMAIN_RUNNING || tmpDomain.getState() == DomainState.VIR_DOMAIN_PAUSED);
								destroy.setEnabled(tmpDomain.getState() != DomainState.VIR_DOMAIN_SHUTOFF);
								
								target.addComponent(start);
								target.addComponent(pause);
								target.addComponent(stop);
								target.addComponent(destroy);

								previousCpuUsage = currentCpuUsage;
								currentCpuUsage = tmpDomain.getCpuTime();
								cpuUsage.setDefaultModelObject(usage.format((currentCpuUsage - previousCpuUsage) / (5 * Runtime.getRuntime().availableProcessors() * 10e9)));
								target.addComponent(cpuUsage);

								currentMemoryUsage = tmpDomain.getMemoryUsage() >> 10;
								final double maxMemInMegs = d.getTotalMemory() >> 10 >> 10;
								final double usedMem = currentMemoryUsage / maxMemInMegs;
								memoryUsage.setDefaultModelObject(usage.format(usedMem));
							}
						}
					}
				});
			}
		};
		container.add(vms.setMarkupId("repeater").setOutputMarkupId(true));
	}
	
	private class ReloadableModel extends ListModel<InternalDomain> {
		private final VirtManager virtMgr;
		
		public ReloadableModel(VirtManager vm) {
			this.virtMgr = vm;
			setObject(vm.getDomains());
		}
		
		public void reload() {
			setObject(virtMgr.getDomains());
		}
	}
}