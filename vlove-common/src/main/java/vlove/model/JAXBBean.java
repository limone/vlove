package vlove.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JAXBBean {

  public String value;

  public JAXBBean() {
    super();
  }

  public JAXBBean(String str) {
    super();
    value = str;
  }
}