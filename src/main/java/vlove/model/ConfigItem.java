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

/**
 * An item that can be persisted to a database and later retrieved.
 * 
 * @author Michael Laccetti
 */
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