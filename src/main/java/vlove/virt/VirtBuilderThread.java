package vlove.virt;

import org.codehaus.plexus.util.cli.StreamConsumer;

import vlove.VirtException;
import vlove.model.NewVmWizardModel;

public class VirtBuilderThread extends Thread {
	private final VirtBuilder vb;
	private final NewVmWizardModel vm;
	private final StreamConsumer out;
	private final StreamConsumer err;
	
	private boolean isComplete;
	private boolean hadError;
	private int returnCode;
	private VirtException error;

	public VirtBuilderThread(VirtBuilder vb, NewVmWizardModel vm, StreamConsumer out, StreamConsumer err) {
		this.vb = vb;
		this.vm = vm;
		this.out = out;
		this.err = err;
	}

	@Override
	public void run() {
		try {
			returnCode = vb.createVirtualMachine(vm, out, err);
		} catch (VirtException ve) {
			hadError = true;
			error = ve;
		}
		isComplete = true;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public boolean isHadError() {
		return hadError;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public VirtException getError() {
		return error;
	}
}