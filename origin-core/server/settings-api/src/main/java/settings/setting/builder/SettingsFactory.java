package settings.setting.builder;

import settings.setting.SettingOption;

public interface SettingsFactory {

	SectionBuilder newSectionBuilder();

	SettingBuilder newSettingBuilder();

	OptionBuilder newOptionBuilder();

	String BOOL_ON = "&2ON";
	String BOOL_OFF = "&cOFF";

	/**
	 * @return a new [ON, OFF] option pair in such an order that the default state would be ON
	 */
	SettingOption[] boolOptON(String descOn, String descOff);

	/**
	 * @return a new [ON, OFF]] option pair in such an order that the default state would be OFF
	 */
	SettingOption[] boolOptOFF(String descOn, String descOff);

}
