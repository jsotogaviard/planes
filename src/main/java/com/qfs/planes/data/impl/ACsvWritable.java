package com.qfs.planes.data.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.qfs.planes.data.ICsvWritable;

public abstract class ACsvWritable implements ICsvWritable{

	protected Class<?> clazz;
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public ACsvWritable(Class<?> clazz) {
		this.clazz = clazz ;
	}

	@Override
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		for (Field field : clazz.getDeclaredFields()) {
			if(Modifier.isProtected(field.getModifiers())){
				try {
					Object object = field.get(this);
					if (object instanceof Date) {
						sb.append(ACsvWritable.SDF.format(object));
					} else {
						sb.append(object);	
					}
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				sb.append(ICsvWritable.SEPARATOR);
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
