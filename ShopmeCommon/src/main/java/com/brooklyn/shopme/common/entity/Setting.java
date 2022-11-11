package com.brooklyn.shopme.common.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.qos.logback.core.subst.Token.Type;

@Entity
@Table(name = "settings")
public class Setting {
	@Id
	@Column(nullable = false,length = 128, name = "`key`")
	private String key;
	@Column(nullable = false,length = 1024)
	private String value;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 64)
	private SettingCategory category;

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

	public SettingCategory getCategory() {
		return category;
	}

	public void setCategory(SettingCategory category) {
		this.category = category;
	}
	public Setting(String key) {
		this.key = key;
	}
	public Setting(String key, String value, SettingCategory category) {
		this.key = key;
		this.value = value;
		this.category = category;
	}
	public Setting(){
		
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Setting other = (Setting) obj;
		return Objects.equals(key, other.key);
	}

	@Override
	public String toString() {
		return "Setting [key=" + key + ", value=" + value + ", category=" + category + "]";
	}
		
	
}
