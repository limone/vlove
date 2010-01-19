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

public class WicketApplication extends WebApplication {
	private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);
	
	public WicketApplication() {
		// empty
	}
	
	@Override
	protected void init() {
		log.info("Limone coming alive.");
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

	@Override
	public Class<? extends Page> getHomePage() {
		return HomePage.class;
	}
}