package vlove.virt;

import org.codehaus.plexus.util.cli.StreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.VirtException;
import vlove.model.NewVmWizardModel;

public class VirtBuilderThread extends Thread {
	private transient final Logger log = LoggerFactory.getLogger(getClass());
	
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
			log.debug("VirtBuilder finished with return code: {}.", returnCode);
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