package vlove.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name="config")
@NamedQueries({
	@NamedQuery(name="ConfigItem.GetByKey", query="select ci from ConfigItem ci where ci.key=:key")
})
public class ConfigItem implements Serializable {
	@Id
	@GeneratedValue(generator="ci_seq", strategy=GenerationType.SEQUENCE)
	@SequenceGenerator(name="ci_seq", sequenceName="ci_seq")
	private Long id;
	
	@Index(name="idx_config")
	@Column(nullable=false, unique=true)
	private String key;
	
	@Column(nullable=false)
	private String value;
	
	public ConfigItem() {
		// empty
	}

	public ConfigItem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}