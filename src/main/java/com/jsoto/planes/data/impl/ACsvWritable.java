package com.jsoto.planes.data.impl;

import java.lang.reflect.Field;

import com.jsoto.planes.data.ICsvWritable;

public abstract class ACsvWritable implements ICsvWritable{
	
	protected Class<?> clazz;

	public ACsvWritable(Class<?> clazz) {
		this.clazz = clazz ;
	}
	
	@Override
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		for (Field field : clazz.getDeclaredFields()) {
			try {
				sb.append(field.get(this));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
