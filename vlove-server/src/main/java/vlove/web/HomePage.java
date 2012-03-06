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

import org.apache.wicket.markup.html.basic.Label;
import org.wicketstuff.annotation.mount.MountPath;

import vlove.VirtException;
import vlove.model.Capabilities;

/**
 * The welcome page.
 * 
 * @author Michael Laccetti
 */
@MountPath("/home")
public class HomePage extends BasePage {
	
	public HomePage() {
		super();
		
		String capabilities = null;
		if (vm.validateConfig()) {
			try {
				Capabilities c = vm.getCapabilities();
				capabilities = String.format("%s %s - %s - %d cores", c.getVendor(), c.getModel(), c.getCpuArch(), c.getNumProcs());
			} catch (VirtException ve) {
				log.warn("Could not retrieve capabilities.", ve);
			}
		}
		add(new Label("capabilities", capabilities));
	}
}