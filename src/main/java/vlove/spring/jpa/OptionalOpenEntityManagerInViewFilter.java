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
package vlove.spring.jpa;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * Spring object that allows us to open EMF connections only if we really need to.
 * 
 * @author Michael Laccetti
 */
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