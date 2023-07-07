package settings.impl.setting.builder;

import settings.setting.SettingOption;
import settings.setting.builder.OptionBuilder;
import settings.setting.builder.SectionBuilder;
import settings.setting.builder.SettingBuilder;
import settings.setting.builder.SettingsFactory;

/**
 * @author vadim
 */
public class SettingsFactoryImpl implements SettingsFactory {

	@Override
	public SectionBuilder newSectionBuilder() {
		return new SectionBuilderImpl();
	}

	@Override
	public SettingBuilder newSettingBuilder() {
		return new SettingBuilderImpl();
	}

	@Override
	public OptionBuilder newOptionBuilder() {
		return new OptionBuilderImpl();
	}

	@Override
	public SettingOption[] boolOptON(String descOn, String descOff) {
		return new SettingOption[] {
				newOptionBuilder()
						.setName(BOOL_ON)
						.setDescription(descOn)
						.build(),
				newOptionBuilder()
						.setName(BOOL_OFF)
						.setDescription(descOff)
						.build(),
		};
	}

	@Override
	public SettingOption[] boolOptOFF(String descOn, String descOff) {
		return new SettingOption[] {
				newOptionBuilder()
						.setName(BOOL_OFF)
						.setDescription(descOff)
						.build(),
				newOptionBuilder()
						.setName(BOOL_ON)
						.setDescription(descOn)
						.build(),
				};
	}

}
