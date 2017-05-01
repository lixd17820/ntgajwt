package com.ntga.tools;

import java.lang.reflect.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import com.ntga.dao.GlobalMethod;

import android.content.ContentValues;
import android.database.Cursor;

public class RefectResult {
	public static List<Object> copy(Class<?> classType, Cursor rs)
			throws Exception {
		List<Object> list = new ArrayList<Object>();
		Class<?> ct = rs.getClass();
		if (rs.moveToFirst()) {
			do {
				// 调用无参数构造方法实例化对象
				Object objectCopy = classType.getConstructor(new Class[] {})
						.newInstance(new Object[] {});
				// 取得对象的字段数组
				Field fields[] = classType.getDeclaredFields();

				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					String fieldName = field.getName();
					// 取得POJO对象字段的SET方法名和方法
					String setMethodName = "set"
							+ fieldName.substring(0, 1).toUpperCase()
							+ fieldName.substring(1);

					Method setMethod = classType.getMethod(setMethodName,
							new Class[] { field.getType() });
					// 结果集对象字段属性名称如 getString
					String rsGetMethod = "get"
							+ field.getType().getSimpleName();

					if ("java.util.Date".equals(field.getType().getName())) {
						rsGetMethod = "getString";
					}
					// 取得结果集字段的GET方法，参数为INT类型
					Method rsGetMe = ct.getMethod(rsGetMethod,
							new Class[] { int.class });
					// 调用结果集的GET方法，参数为序数
					Object value = rsGetMe.invoke(rs,
							new Object[] { new Integer(i) });
					// 调用POJO类的SET方法，向对象赋值
					if ("java.util.Date".equals(field.getType().getName())
							&& value != null) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date riqi = sdf.parse((String) value);
						setMethod.invoke(objectCopy, riqi);
					} else {
						setMethod.invoke(objectCopy, new Object[] { value });
					}
				}
				list.add(objectCopy);
			} while (rs.moveToNext());
		}
		return list;
	}

	public static ContentValues createContentByObject(Object pojo) {
		ContentValues cv = new ContentValues();
		Class<?> classType = pojo.getClass();
		Field[] fields = classType.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			// 结果集对象字段属性名称如 getString
			String rsGetMethod = "get"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);

			try {
				Method rsGetMe;
				rsGetMe = classType.getMethod(rsGetMethod, new Class[] {});
				Object value = rsGetMe.invoke(pojo, new Object[] {});
				if (value != null) {
					if ("java.lang.Long".equals(field.getType().getName()))
						cv.put(fieldName, (Long) (value));
					else if ("java.lang.Integer".equals(field.getType()
							.getName()))
						cv.put(fieldName, (Integer) (value));
					else
						cv
								.put(fieldName, GlobalMethod.ifNull(value
										.toString()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cv;
	}

	public static <T> List<T> rsToObject(Class<T> classType, Cursor rs) {
		List<T> list = new ArrayList<T>();
		Class<?> ct = rs.getClass();
		try {
			if (rs.moveToFirst()) {
				do {
					// 调用无参数构造方法实例化对象
					T objectCopy = classType.getConstructor(new Class[] {})
							.newInstance(new Object[] {});
					// 取得对象的字段数组
					Field fields[] = classType.getDeclaredFields();

					for (int i = 0; i < fields.length; i++) {
						Field field = fields[i];

						String fieldName = field.getName();
						// 取得POJO对象字段的SET方法名和方法
						String setMethodName = "set"
								+ fieldName.substring(0, 1).toUpperCase()
								+ fieldName.substring(1);

						Method setMethod = classType.getMethod(setMethodName,
								new Class[] { field.getType() });
						// 结果集对象字段属性名称如 getString
						String typeName = field.getType().getSimpleName();
						String rsGetMethod = "get"
								+ typeName.substring(0, 1).toUpperCase()
								+ typeName.substring(1);

						if ("java.util.Date".equals(field.getType().getName())) {
							rsGetMethod = "getString";
						}
						// 取得结果集字段的GET方法，参数为INT类型
						Method rsGetMe = ct.getMethod(rsGetMethod,
								new Class[] { int.class });
						// 调用结果集的GET方法，参数为序数
						Object value = rsGetMe.invoke(rs, new Object[] { rs
								.getColumnIndex(fieldName) });
						// 调用POJO类的SET方法，向对象赋值
						if ("java.util.Date".equals(field.getType().getName())
								&& value != null) {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date riqi = sdf.parse((String) value);
							setMethod.invoke(objectCopy, riqi);
						} else {
							setMethod
									.invoke(objectCopy, new Object[] { value });
						}
					}
					list.add(objectCopy);
				} while (rs.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// �媒峁蚨韵蟾持�
	public static <T> T rsToPojo(Class<T> classType, ResultSet rs)
			throws Exception {
		Class<?> ct = rs.getClass();

		// 调用无参数构造方法实例化对象
		T objectCopy = classType.getConstructor(new Class[] {}).newInstance(
				new Object[] {});
		// 取得对象的字段数组
		Field fields[] = classType.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			String fieldName = field.getName();
			// 取得POJO对象字段的SET方法名和方法
			String setMethodName = "set"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);

			Method setMethod = classType.getMethod(setMethodName,
					new Class[] { field.getType() });
			// 结果集对象字段属性名称如 getString
			String rsGetMethod = "get" + field.getType().getSimpleName();

			if ("java.util.Date".equals(field.getType().getName())) {
				rsGetMethod = "getString";
			}
			// 取得结果集字段的GET方法，参数为INT类型
			Method rsGetMe = ct.getMethod(rsGetMethod,
					new Class[] { int.class });
			// 调用结果集的GET方法，参数为序数
			Object value = rsGetMe.invoke(rs,
					new Object[] { new Integer(i + 1) });
			// 调用POJO类的SET方法，向对象赋值
			if ("java.util.Date".equals(field.getType().getName())
					&& value != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date riqi = sdf.parse((String) value);
				setMethod.invoke(objectCopy, riqi);
			} else {
				setMethod.invoke(objectCopy, new Object[] { value });
			}
		}
		return objectCopy;
	}

	public static <T> String createSetMethod(Class<T> classType,
			String objName, String csName) throws Exception {
		StringBuilder sb = new StringBuilder();
		// 取得对象的字段数组
		Field fields[] = classType.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			String fieldName = field.getName();
			// 取得POJO对象字段的SET方法名和方法
			String setMethodName = "set"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			sb.append(objName).append(".").append(setMethodName).append("(")
					.append(csName).append(".getString(").append(i).append(
							"));\n");
		}
		return sb.toString();
	}

	public static <T> String createGetMethod(Class<T> classType,
			String contentName, String objName) throws Exception {
		StringBuilder sb = new StringBuilder();
		// 取得对象的字段数组
		Field fields[] = classType.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			String fieldName = field.getName();
			// 取得POJO对象字段的SET方法名和方法
			String getMethodName = "get"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			sb.append(contentName).append(".").append(fieldName.toUpperCase())
					.append(",").append(objName).append(".").append(
							getMethodName).append("());\n");
		}
		return sb.toString();
	}

	public static String[] listToArray(Object pojo) throws Exception {
		Class<?> classType = pojo.getClass();
		Field[] fields = classType.getDeclaredFields();
		String[] result = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			// 结果集对象字段属性名称如 getString
			String rsGetMethod = "get"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			Method rsGetMe = classType.getMethod(rsGetMethod, new Class[] {});
			Object value = rsGetMe.invoke(pojo, new Object[] {});
			if (value != null) {
				if ("java.util.Date".equals(field.getType().getName())
						&& value != null) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					result[i] = sdf.format((Date) value);
				} else {
					result[i] = value.toString();
				}
			}
		}
		return result;
	}

	public static String[] printPojo(Object pojo) throws Exception {
		Class<?> classType = pojo.getClass();
		Field[] fields = classType.getDeclaredFields();
		String[] result = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			// 结果集对象字段属性名称如 getString
			String rsGetMethod = "get"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			Method rsGetMe = classType.getMethod(rsGetMethod, new Class[] {});
			Object value = rsGetMe.invoke(pojo, new Object[] {});
			if (value != null) {
				if ("java.util.Date".equals(field.getType().getName())) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");

					result[i] = fieldName + ": "
							+ sdf.format((Date) value);
				} else {
					result[i] = fieldName + ": " + value.toString();
				}
			}
		}
		return result;
	}
}
