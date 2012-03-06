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
package vlove.model;

import org.libvirt.DomainInfo.DomainState;

/**
 * Class that allows us to convert from libvirt DomainState to a String
 * representation.
 * 
 * @author Michael Laccetti
 */
public class TypeConverter {
  public static Pair<String, String> domainState(DomainState state) {
    switch (state) {
      case VIR_DOMAIN_BLOCKED:
        return new Pair<>("BLOCKED", state.toString());
      case VIR_DOMAIN_CRASHED:
        return new Pair<>("CRASHED", state.toString());
      case VIR_DOMAIN_NOSTATE:
        return new Pair<>("NO STATE", state.toString());
      case VIR_DOMAIN_PAUSED:
        return new Pair<>("PAUSED", state.toString());
      case VIR_DOMAIN_RUNNING:
        return new Pair<>("RUNNING", state.toString());
      case VIR_DOMAIN_SHUTDOWN:
        return new Pair<>("SHUTDOWN", state.toString());
      case VIR_DOMAIN_SHUTOFF:
        return new Pair<>("SHUTOFF", state.toString());
    }
    return null;
  }
}