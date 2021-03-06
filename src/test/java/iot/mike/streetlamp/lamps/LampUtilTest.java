package iot.mike.streetlamp.lamps;

import iot.mike.streetlamp.lamps.LampUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

import junit.framework.TestCase;

public class LampUtilTest extends TestCase{
	
	public void testLampUtil(){
		System.out.println(new Date(System.currentTimeMillis()).toString());
		File file = new File(
				new Date(System.currentTimeMillis()).toString());
		if (file.exists()) {
			System.out.println("A");
		}else {
			file.mkdirs();
			System.out.println("B");
		}
		try {
			LampUtil.writeHeartBeat("Mike");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(LampUtil.removeKey("#2227csfj001dd*"));
		
		System.out.println(CRC16.caluCRC("#2017mike001*"));
		
		//assertEquals(DealType.LampHeartBeat, LampUtil.classifyOrder("#2018cesfj001de*"));
		//assertEquals("cesfj001", LampUtil.getKeyID("#2018cesfj001de*"));
		
		char[] d = new char[32];
		d[0] = (char)255;
		d[1] = (char)49;
		d[2] = (char)68;
		System.out.println(new String(d));
		System.out.println(d[0]);
		System.out.println((Integer.toBinaryString(d[0])));
		String a = "240";
		String c = String.valueOf(Integer.toBinaryString((Integer.valueOf(a))));
		int size = c.length();
		if (c.length() != 8) {
			for (int i = 0; i < 8 - size; i++) {
				c = "0" + c;
			}
		}
		System.out.println(c);
		
		char achar = (char)255;
		System.out.println(achar + "" + (int)achar);
	}
}
