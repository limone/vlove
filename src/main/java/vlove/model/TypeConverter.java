package vlove.model;

import org.libvirt.DomainInfo.DomainState;

public class TypeConverter {
	public static Pair<String, String> domainState(DomainState state) {
		switch (state) {
			case VIR_DOMAIN_BLOCKED:
				return new Pair<String, String>("BLOCKED", state.toString());
			case VIR_DOMAIN_CRASHED:
				return new Pair<String, String>("CRASHED", state.toString());
			case VIR_DOMAIN_NOSTATE:
				return new Pair<String, String>("NO STATE", state.toString());
			case VIR_DOMAIN_PAUSED:
				return new Pair<String, String>("PAUSED", state.toString());
			case VIR_DOMAIN_RUNNING:
				return new Pair<String, String>("RUNNING", state.toString());
			case VIR_DOMAIN_SHUTDOWN:
				return new Pair<String, String>("SHUTDOWN", state.toString());
			case VIR_DOMAIN_SHUTOFF:
				return new Pair<String, String>("SHUTOFF", state.toString());
		}
		return null;
	}
}