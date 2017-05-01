package com.ntga.tools;

import java.util.Date;

public class GpsUtils {

	private double getOaDate(Date d) {
		double cj = 25569.3333333333;
		long c = d.getTime();
		double dc = ((double) c / (24 * 60 * 60 * 1000)) + cj;
		return dc;
	}

	public Date getDateFromOa(double d) {
		double cj = 25569.3333333333;
		long l = (long) ((d - cj) * (24 * 60 * 60 * 1000));
		return new Date(l);
	}

	private byte[] getJybhByte(String jybh) {
		// 00 00 00 00 12 34
		for (int i = jybh.length(); i < 12; i++) {
			jybh = '0' + jybh;
		}
		byte[] id = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int x = 0;
		for (int i = 0; i < id.length; i++) {
			id[i] = Byte.valueOf(jybh.substring(x, x + 2));
			x = x + 2;
		}
		return id;
	}

	public byte[] getByteFromGps(String yhbh, double jd, double wd, byte sd,
			byte fx, short xs, int hb, Date wxsj) {
		byte[] b = new byte[0];
		// 数据头
		b = TypeCenvert.addByte(b, TypeCenvert.short2Byte((short) 0xAA));
		b = TypeCenvert.addByte(b, TypeCenvert.short2Byte((short) 0xCC));
		b = TypeCenvert.addByte(b, getJybhByte(yhbh));// 警员编号
		b = TypeCenvert.addByte(b, TypeCenvert.double2Byte(jd));// 经度
		b = TypeCenvert.addByte(b, TypeCenvert.double2Byte(wd));// 纠度
		b = TypeCenvert.addByte(b, (byte) sd);// 速度
		b = TypeCenvert.addByte(b, (byte) fx);// 方向
		b = TypeCenvert.addByte(b, TypeCenvert.short2Byte(xs));// 星数
		b = TypeCenvert.addByte(b, new byte[] { 0x01, 0x00 });// 标识
		b = TypeCenvert.addByte(b, TypeCenvert.int2Byte(hb));// 高度
		double sj = getOaDate(wxsj);
		b = TypeCenvert.addByte(b, TypeCenvert.double2Byte(sj));
		return b;
	}

}
