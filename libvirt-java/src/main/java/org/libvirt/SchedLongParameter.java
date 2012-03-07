package org.libvirt;

/**
 * Class for representing a long int scheduler parameter
 * 
 * @author stoty
 * 
 */
public final class SchedLongParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public long value;

    public SchedLongParameter() {

    }

    public SchedLongParameter(long value) {
        this.value = value;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_LLONG";
    }

    @Override
    public String getValueAsString() {
        return Long.toString(value);
    }

}
