package vlove.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import vlove.VirtException;
import vlove.model.Capabilities;
import vlove.model.InternalDomain;
import vlove.model.InternalStoragePool;

@Service
public class VirtManager {
  /**
   * @return
   * @throws VirtException
   */
  public List<InternalStoragePool> getStoragePools() throws VirtException {
    return new ArrayList<>();
  }

  /**
   * @param network
   * @return
   * @throws VirtException
   */
  public String getNetwork(String network) throws VirtException {
    return "br0";
  }

  /**
   * @return
   * @throws VirtException
   */
  public List<String> getNetworks() throws VirtException {
    return new ArrayList<>();
  }

  public boolean validateConfig() {
    return true;
  }

  /**
   * @return
   * @throws VirtException
   */
  public Capabilities getCapabilities() throws VirtException {
    return new Capabilities("x86_64", "i7", "Intel", 8);
  }

  /**
   * @param sPool
   * @return
   */
  public InternalStoragePool getStoragePool(String sPool) {
    return null;
  }

  /**
   * @return
   * @throws VirtException
   */
  public List<InternalDomain> getDomains() throws VirtException {
    return new ArrayList<>();
  }

  /**
   * @param domainId
   * @throws VirtException
   */
  public void shutdown(Integer domainId) throws VirtException {
    // TODO
  }

  /**
   * @param domainName
   * @throws VirtException
   */
  public void start(String domainName) throws VirtException {
    // TODO
  }

  /**
   * @param domainId
   * @throws VirtException
   */
  public void resume(Integer domainId) throws VirtException {
    // TODO
  }

  /**
   * @param domainId
   * @throws VirtException
   */
  public void pause(Integer domainId) throws VirtException {
    // TODO
  }

  /**
   * @param domainId
   * @return
   * @throws VirtException
   */
  public InternalDomain getDomain(Integer domainId) throws VirtException {
    return null;
  }

  /**
   * @param domainId
   * @throws VirtException
   */
  public void destroy(Integer domainId) throws VirtException {
    // TODO
  }

  /**
   * @param uuid
   * @return
   * @throws VirtException
   */
  public InternalDomain getDomainByUUID(String uuid) throws VirtException {
    return null;
  }
}