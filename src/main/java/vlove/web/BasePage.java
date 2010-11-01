package vlove.web;

import java.util.Arrays;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WicketEventReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.virt.VirtManager;
import vlove.web.config.ConfigPage;
import vlove.web.vms.VmListPage;

public class BasePage extends WebPage {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@SpringBean
	VirtManager vm;
	
	private enum Links {
		HOME("home", HomePage.class), VMS("vms", VmListPage.class), CONFIG("config", ConfigPage.class);
		
		private String name;
		private Class<? extends BasePage> impl;
		
		private Links(String name, Class<? extends BasePage> impl) {
			this.name = name;
			this.impl = impl;
		}

		public String getName() {
			return name;
		}

		public Class<? extends BasePage> getImpl() {
			return impl;
		}
	}
	private static final Links[] links = new Links[]{ Links.HOME, Links.VMS, Links.CONFIG };
	
	public BasePage() {
		add(JavascriptPackageResource.getHeaderContribution(WicketEventReference.INSTANCE));
		add(JavascriptPackageResource.getHeaderContribution(WicketAjaxReference.INSTANCE));
		
		WebMarkupContainer missingConfig = null;
		final boolean isConfigPage = this instanceof ConfigPage;
		if (!vm.validateConfig() && !isConfigPage) {
			log.debug("Injecting diaglog redirect.");
			missingConfig = new Fragment("config", "missingConfig", this);
			missingConfig.add(new BookmarkablePageLink<Object>("configPageLink", ConfigPage.class));
		} else {
			log.debug("Config is okay/we're the config page, keeping things blank");
			missingConfig = new WebMarkupContainer("config");
			missingConfig.setVisible(false);
			
			// And since all is well, let us connect
			if (!vm.isConnected()) {
				vm.connect();
			}
		}
		add(missingConfig);
		
		ListView<Links> menu = new ListView<Links>("menu", Arrays.asList(links)) {
			@Override
			protected void populateItem(ListItem<Links> item) {
				final Class<? extends Page> currentPage = getPage().getClass();
				
				final WebMarkupContainer listItem = new WebMarkupContainer("item");
				if (currentPage.equals(item.getModelObject().getImpl())) {
					listItem.add(new AttributeAppender("class", true, new Model<String>("active"), " "));
				}
				item.add(listItem);
				
				final BookmarkablePageLink<Object> link = new BookmarkablePageLink<Object>("link", item.getModelObject().getImpl());
				link.add(new Label("name", item.getModelObject().getName()));
				listItem.add(link);
			}
		};
		add(menu);
	}
}