package vlove.web;

import java.util.Arrays;

import org.apache.wicket.Page;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import vlove.web.config.ConfigPage;
import vlove.web.vms.VmListPage;

public class BasePage extends WebPage {
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