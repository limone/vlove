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
package vlove.virt;

import org.codehaus.plexus.util.cli.StreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.VirtException;
import vlove.model.NewVmWizardModel;

/**
 * Thread to run in the background to build a VM.
 * 
 * @author Michael Laccetti
 */
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