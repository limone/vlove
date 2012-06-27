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

import java.io.File;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.xml.xpath.XPathConstants;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.Network;
import org.libvirt.StoragePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import vlove.VirtException;
import vlove.model.Capabilities;
import vlove.model.DomainState;
import vlove.model.InternalDomain;
import vlove.model.InternalStoragePool;
import vlove.model.StoragePoolState;
import vlove.util.XPathUtils;

import com.sun.management.OperatingSystemMXBean;

/**
 * The meat and potatoes of vlove - the interface to libvirt.
 * 
 * @author Michael Laccetti
 */
@SuppressWarnings("restriction")
public class VirtManager implements Serializable {
  private static final Logger log         = LoggerFactory.getLogger(VirtManager.class);
  private boolean                isConnected = false;

  // Connection information
  private String                 libvirtUrl;
  private Connect                connection;

  // MBean
  private OperatingSystemMXBean  osBean      = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

  public VirtManager() {
    // empty, for Spring
  }

  private void checkConnected() {
    if (!isConnected) {
      log.error("Not connected to libvirt, failing operation.");
      throw new RuntimeException("Not connected to libvirt, failing operation.");
    }
  }

  /**
   * Post-constructor init method - connects to libvirt.
   */
  public void init() {
    // System.setProperty("jna.library.path", "C:/Tools/dev/libvirt/bin");
    // System.setProperty("java.library.path", "C:/Tools/dev/libvirt/bin");
    
    final String os = System.getProperty("os.name");
    log.debug("Loading driver for OS: {}", os);

    String driver = null;
    if (os != null && os.toLowerCase().contains("windows")) {
      driver = "C:/Tools/dev/libvirt/bin/libvirt-0.dll";
    } else if (os != null && os.toLowerCase().contains("linux")) {
      driver = "/usr/lib/libvirt.so.0";
    }
    
    if (new File(driver).exists()) {
      System.load(driver);
    } else {
      throw new RuntimeException(String.format("Could not load driver from %s.", driver));
    }

    // Set the default URL
    libvirtUrl = "qemu:///session";
    if (libvirtUrl == null || libvirtUrl.length() == 0) {
      log.warn("No libvirt URL defined, setting to default: test:///default");
      libvirtUrl = "test:///default";
    }
  }

  /**
   * Used to rebuild the connection to libvirt on the fly.
   * 
   * @param newLibvirtUrl
   *          URL for libvirt
   */
  public void init(String newLibvirtUrl) {
    init();
  }

  /**
   * Doesn't shut down a VM, but the VirtManager itself.
   */
  @PreDestroy
  public void shutdown() {
    disconnect();
  }

  /**
   * Check to see if we are connected to libvirt.
   * 
   * @return
   */
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Connect to libvirt using the URL pulled out during the init() process.
   * 
   * @return
   */
  public boolean connect() {
    try {
      log.debug("Attempting connection to {}.", libvirtUrl);
      connection = new Connect(libvirtUrl);
      isConnected = true;
      log.debug("Connected to libvirt.");
      return true;
    } catch (LibvirtException le) {
      log.error("Could not connect to libvirt.", le);
      return false;
    }
  }

  /**
   * Disconnect from libvirt.
   */
  public void disconnect() {
    if (isConnected) {
      try {
        log.debug("Disconnecting from libvirt.");
        connection.close();
      } catch (LibvirtException le) {
        log.warn("Could not close connection to libvirt.", le);
      }
      isConnected = false;
    } else {
      log.info("Not connected to libvirt, no need to disconnect.");
    }
  }

  /**
   * Retrieve the hardware capabilities of the host that libvirt is runnign on.
   * 
   * @return
   * @throws VirtException
   */
  public Capabilities getCapabilities() throws VirtException {
    checkConnected();

    Capabilities c = new Capabilities();

    try {
      final String capabilities = connection.getCapabilities();
      Document d = XPathUtils.loadDocument(capabilities.getBytes());
      c.setCpuArch(((String) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/arch", XPathConstants.STRING)));
      c.setModel(((String) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/model", XPathConstants.STRING)));
      c.setVendor(((String) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/vendor", XPathConstants.STRING)));

      // To be added later, just dump it to the log for now:
      Node t = (Node) XPathUtils.parseXPathExpression(d, "/capabilities/host/cpu/topology", XPathConstants.NODE);
      Integer sockets = Integer.parseInt(t.getAttributes().getNamedItem("sockets").getNodeValue());
      Integer cores = Integer.parseInt(t.getAttributes().getNamedItem("cores").getNodeValue());
      Integer threads = Integer.parseInt(t.getAttributes().getNamedItem("threads").getNodeValue());

      // log.debug("Topology - sockets: {}, cores: {}, threads: {}", new
      // Object[] { sockets, cores, threads });
      c.setNumProcs(sockets * cores * threads);
    } catch (LibvirtException le) {
      log.error("Could not retrieve capabilities from libvirt.", le);
      throw new VirtException("Could not retrieve capabilities from libvirt.", le);
    }

    return c;
  }

  /**
   * Retrieve all the VMs defined.
   * 
   * @return
   */
  public List<InternalDomain> getDomains() throws VirtException {
    checkConnected();
    try {
      List<InternalDomain> domains = new ArrayList<>();
      for (int did : connection.listDomains()) {
        Domain d = connection.domainLookupByID(did);
        domains.add(new InternalDomain(did, d.getUUIDString(), d.getName(), DomainState.valueOf(d.getInfo().state.toString()), osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory));
      }

      for (String domainName : connection.listDefinedDomains()) {
        Domain d = connection.domainLookupByName(domainName);
        domains.add(new InternalDomain(0, d.getUUIDString(), d.getName(), DomainState.valueOf(d.getInfo().state.toString()), osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory));
      }
      return domains;
    } catch (LibvirtException le) {
      log.error("Could not list domains.", le);
      throw new VirtException("Could not list domains.", le);
    }
  }

  /**
   * Retrieve a specific VM, by numeric ID.
   * 
   * @param domainId
   * @return
   * @throws VirtException
   */
  public InternalDomain getDomain(Integer domainId) throws VirtException {
    checkConnected();
    try {
      Domain d = connection.domainLookupByID(domainId);
      return new InternalDomain(domainId, d.getUUIDString(), d.getName(), DomainState.valueOf(d.getInfo().state.toString()), osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
    } catch (LibvirtException le) {
      throw new VirtException(String.format("Could not retrieve domain for ID %d.", domainId));
    }
  }

  /**
   * Retrieve a specific VM, by name.
   * 
   * @param domainName
   * @return
   * @throws VirtException
   */
  public InternalDomain getDomain(String domainName) throws VirtException {
    checkConnected();
    try {
      Domain d = connection.domainLookupByName(domainName);
      return new InternalDomain(d.getID(), d.getUUIDString(), d.getName(), DomainState.valueOf(d.getInfo().state.toString()), osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
    } catch (LibvirtException le) {
      throw new VirtException(String.format("Could not retrieve domain for name %s.", domainName), le);
    }
  }

  /**
   * Retrieve a specific VM, by UUID.
   * 
   * @param uuid
   * @return
   * @throws VirtException
   */
  public InternalDomain getDomainByUUID(String uuid) throws VirtException {
    try {
      Domain d = connection.domainLookupByUUIDString(uuid);
      return new InternalDomain(d.getID(), d.getUUIDString(), d.getName(), DomainState.valueOf(d.getInfo().state.toString()), osBean.getTotalPhysicalMemorySize(), d.getInfo().cpuTime, d.getInfo().memory);
    } catch (LibvirtException le) {
      throw new VirtException(String.format("Could not retrieve domain for UUID %s.", uuid));
    }
  }

  /**
   * Start a VM up.
   * 
   * @param domainName
   * @return
   */
  // TODO convert from a PAIR to a TRIPLE so that we can capture the error
  // message, if any
  public Integer start(String domainName) throws VirtException {
    checkConnected();
    try {
      Domain d = connection.domainLookupByName(domainName);
      log.debug("Starting domain {}.", domainName);
      d.create();
      d = connection.domainLookupByName(domainName);
      return d.getID();
    } catch (LibvirtException le) {
      log.error(String.format("Could not start domain %s.", domainName), le);
      throw new VirtException(String.format("Could not start domain %s.", domainName), le);
    }
  }

  /**
   * Resume a paused VM.
   * 
   * @param domainId
   * @return
   */
  public void resume(Integer domainId) throws VirtException {
    checkConnected();
    try {
      Domain d = connection.domainLookupByID(domainId);
      log.debug("Resuming domain {}.", domainId);
      d.resume();
    } catch (LibvirtException le) {
      log.error(String.format("Could not resume domain %d.", domainId), le);
      throw new VirtException(String.format("Could not resume domain %d.", domainId), le);
    }
  }

  /**
   * Pause a running VM.
   * 
   * @param domainId
   * @return
   */
  public void pause(Integer domainId) throws VirtException {
    checkConnected();
    try {
      log.debug("Pausing domain {}.", domainId);
      Domain d = connection.domainLookupByID(domainId);
      d.suspend();
    } catch (LibvirtException le) {
      log.error(String.format("Could not pause domain %d.", domainId), le);
      throw new VirtException(String.format("Could not pause domain %d.", domainId), le);
    }
  }

  /**
   * Send the ACPI shutdown command to a running VM.
   * 
   * @param domainId
   * @return
   */
  public void shutdown(Integer domainId) throws VirtException {
    checkConnected();
    try {
      log.debug("Shutting down domain {}.", domainId);
      Domain d = connection.domainLookupByID(domainId);
      d.shutdown();
    } catch (LibvirtException le) {
      log.error(String.format("Could not shutdown domain %d.", domainId), le);
      throw new VirtException(String.format("Could not shutdown domain %d.", domainId), le);
    }
  }

  /**
   * Forcibly terminate a running VM.
   * 
   * @param domainId
   * @return
   */
  public void destroy(Integer domainId) throws VirtException {
    checkConnected();
    try {
      log.debug("Destroying domain {}.", domainId);
      Domain d = connection.domainLookupByID(domainId);
      d.destroy();
    } catch (LibvirtException le) {
      log.error(String.format("Could not destroy domain %d.", domainId), le);
      throw new VirtException(String.format("Could not destroy domain %d.", domainId), le);
    }
  }

  /**
   * Retrieve a list of storage pools.
   * 
   * @return
   * @throws VirtException
   */
  public List<InternalStoragePool> getStoragePools() throws VirtException {
    checkConnected();
    try {
      List<InternalStoragePool> pools = new ArrayList<>();
      for (String poolName : connection.listStoragePools()) {
        final StoragePool pool = connection.storagePoolLookupByName(poolName);
        pools.add(new InternalStoragePool(pool.getName(), pool.getUUIDString(), pool.numOfVolumes(), StoragePoolState.valueOf(pool.getInfo().state.toString()), pool.getInfo().capacity, pool.getInfo().available, pool.getAutostart()));
      }
      return pools;
    } catch (LibvirtException le) {
      throw new VirtException("Could not retrieve list of storage pools.", le);
    }
  }

  /**
   * Retrieve a specific storage pool.
   * 
   * @param name
   * @return
   * @throws VirtException
   */
  public InternalStoragePool getStoragePool(String name) throws VirtException {
    checkConnected();
    try {
      final StoragePool pool = connection.storagePoolLookupByName(name);
      return new InternalStoragePool(pool.getName(), pool.getUUIDString(), pool.numOfVolumes(), StoragePoolState.valueOf(pool.getInfo().state.toString()), pool.getInfo().capacity, pool.getInfo().available, pool.getAutostart());
    } catch (LibvirtException le) {
      throw new VirtException("Could not retrieve storage pool.", le);
    }
  }

  /**
   * Retrieve all defined networks.
   * 
   * @return
   * @throws VirtException
   */
  public List<String> getNetworks() throws VirtException {
    checkConnected();
    try {
      return Arrays.asList(connection.listNetworks());
    } catch (LibvirtException le) {
      throw new VirtException("Could not retrieve list of networks.", le);
    }
  }

  /**
   * Retrieve a specific network.
   * 
   * @param name
   * @return
   * @throws VirtException
   */
  public Network getNetwork(String name) throws VirtException {
    checkConnected();
    try {
      return connection.networkLookupByName(name);
    } catch (LibvirtException le) {
      throw new VirtException("Could not retrieve network information.", le);
    }
  }
}