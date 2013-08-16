package iot.mike.streetlamp.setting;

import junit.framework.TestCase;

public class SettingManagerTest extends TestCase {
	public void testSettingManager(){
		SettingManager settingManager = SettingManager.getInstance();
		assertEquals("127.0.0.2", 
				settingManager.getProperties().getProperty(Setting.PHPIP));
	}
}
