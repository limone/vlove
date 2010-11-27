package vlove.web.test;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.icepush.integration.wicket.core.PushPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.model.NewVmWizardModel;
import vlove.virt.VirtBuilder;
import vlove.virt.VirtBuilderThread;

public class OutputPushPanel extends PushPanel {
	transient final Logger log = LoggerFactory.getLogger(getClass());

	@SpringBean
	transient VirtBuilder vb;

	final LinkedBlockingQueue<String> q = new LinkedBlockingQueue<String>();
	final NewVmWizardModel wizMod = getModel();

	public OutputPushPanel(String id) {
		super(id);

		add(new AjaxFallbackLink<Object>("submit") {
			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(AjaxRequestTarget target) {
				push();
				target.addComponent(this.getParent());
				new VirtBuilderContentThread(WebApplication.get(), target).start();
			}
		});
	}

	@Override
	protected void pushCallback(AjaxRequestTarget target) {
		synchronized (q) {
			final String lastVal = q.poll();
			if (lastVal != null) {
				target.appendJavascript("updateOutput('output', '" + StringEscapeUtils.escapeJavaScript(lastVal) + "');");
			}

			if (q.peek() != null) {
				push();
			}
		}
	}

	private NewVmWizardModel getModel() {
		NewVmWizardModel vm = new NewVmWizardModel();
		vm.setArch("");
		vm.setNetworks("manual");
		vm.setBridge("br0");
		vm.setDiskSize(50);
		vm.setMemSize(512);
		vm.setNumProcs(1);
		vm.setSuite("");
		vm.setVmName("test");
		return vm;
	}

	protected class VirtBuilderContentThread extends Thread {
		private final WebApplication app;
		private final AjaxRequestTarget target;

		public VirtBuilderContentThread(WebApplication app, AjaxRequestTarget target) {
			this.app = app;
			this.target = target;
		}

		@Override
		public void run() {
			log.debug("Creating thread and starting.");
			VirtBuilderThread t = new VirtBuilderThread(vb, wizMod, new ContentListener(app, target, false), new ContentListener(app, target, true));
			t.start();

			while (!t.isComplete()) {
				try {
					sleep(50);
				} catch (InterruptedException ie) {
					log.warn("Thread interrupted.", ie);
				}
			}
			if (t.isHadError()) {
				log.warn("Error running thread.", t.getError());
			}
		}
	}

	protected class ContentListener implements StreamConsumer {
		private final WebApplication app;
		private final AjaxRequestTarget target;
		private final boolean isErr;

		public ContentListener(WebApplication app, AjaxRequestTarget target, boolean isErr) {
			this.app = app;
			this.target = target;
			this.isErr = isErr;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void consumeLine(String line) {
			Application.set(app);
			synchronized (q) {
				try {
					q.put(line);
				} catch (InterruptedException e) {
					log.warn("Interrupted.", e);
				}
				push();
			}
		}
	}
}