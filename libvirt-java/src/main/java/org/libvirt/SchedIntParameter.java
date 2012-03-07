package org.libvirt;

/**
 * Parameter to hold an int.
 */
public final class SchedIntParameter extends SchedParameter {
    public int value;

    public SchedIntParameter() {

    }

    public SchedIntParameter(int value) {
        this.value = value;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_INT";
    }

    @Override
    public String getValueAsString() {
        return Integer.toString(value);
    }
}
