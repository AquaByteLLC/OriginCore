package settings.builder;

/**
 * @author vadim
 */
public interface SettingsFactory {

	OptionsBuilder newOptionsBuilder();

	SectionBuilder newSectionBuilder();

	SettingBuilder newSettingsBuilder();

}
