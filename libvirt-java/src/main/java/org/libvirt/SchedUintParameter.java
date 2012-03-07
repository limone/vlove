package org.libvirt;

/**
 * Class for representing an unsigned int scheduler parameter
 * 
 * 
 * @author stoty
 * 
 */
public final class SchedUintParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public int value;

    public SchedUintParameter() {

    }

    public SchedUintParameter(int value) {
        this.value = value;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_UINT";
    }

    @Override
    public String getValueAsString() {
        return Integer.toString(value);
    }
}
