package settings.impl.builder;

import settings.builder.OptionsBuilder;
import settings.builder.SectionBuilder;
import settings.builder.SettingBuilder;
import settings.builder.SettingsFactory;

/**
 * @author vadim
 */
public class SettingsFactoryImpl implements SettingsFactory {

	@Override
	public OptionsBuilder newOptionsBuilder() {
		return new OptionsBuilderImpl();
	}

	@Override
	public SectionBuilder newSectionBuilder() {
		return new SectionBuilderImpl();
	}

	@Override
	public SettingBuilder newSettingsBuilder() {
		return new SettingBuilderImpl();
	}

}
