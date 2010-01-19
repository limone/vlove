package vlove.spring.jpa;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

public class OptionalOpenEntityManagerInViewFilter extends OpenEntityManagerInViewFilter {
	private boolean emfAvailable;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (this.emfAvailable) {
			super.doFilterInternal(request, response, filterChain);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * Disable per-request lookups of the EMF. This is a performance
	 * optimization, allowing us to only check once for the EMF and remember
	 * whether it was found.
	 */
	@Override
	protected final EntityManagerFactory lookupEntityManagerFactory(HttpServletRequest request) {
		return super.lookupEntityManagerFactory(request);
	}

	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();

		try {
			EntityManagerFactory emf = lookupEntityManagerFactory();
			this.emfAvailable = (emf != null);
		} catch (Exception e) {
			this.emfAvailable = false;
		}
	}
}