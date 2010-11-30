/**
 * vlove - web based virtual machine management
 * Copyright (C) 2010 Limone Fresco Limited
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package vlove.web;

import org.apache.wicket.Page;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.watch.ModificationWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

/**
 * The big bit that turns Wicket on.
 * 
 * @author Michael Laccetti
 */
public class WicketApplication extends WebApplication {
	private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);
	
	public WicketApplication() {
		// empty
	}
	
	/**
	 * Do all our deploy-time magic.
	 * 
	 * @See {@link WebApplication}
	 */
	@Override
	protected void init() {
		log.info("vlove coming alive.");
		super.init();
		
		addComponentInstantiationListener(new SpringComponentInjector(this));
		InjectorHolder.getInjector().inject(this);
		new AnnotatedMountScanner().scanPackage("vlove.web").mount(this);

		/*getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
		getApplicationSettings().setInternalErrorPage(ErrorPage.class);
		getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);*/
		
		getMarkupSettings().setStripComments(true);
		getMarkupSettings().setStripWicketTags(true);
		getMarkupSettings().setStripXmlDeclarationFromOutput(true);
		
		final String devType = getConfigurationType();
		if (devType != null && devType == DEVELOPMENT) {
			getResourceSettings().setResourceWatcher(new ModificationWatcher());
			getResourceSettings().setResourcePollFrequency(Duration.seconds(1));
		}

		// getSecuritySettings().setAuthorizationStrategy(new AcsAnnotationAuthorizationStrategy(getLoginPage()));
		getSecuritySettings().setEnforceMounts(true);
		
		log.info("It's alive, aliiiiiive!");
	}

	/**
	 * @see {@link WebApplication#getHomePage()}
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}
}