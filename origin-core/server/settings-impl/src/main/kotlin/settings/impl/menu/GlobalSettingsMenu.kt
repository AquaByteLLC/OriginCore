package settings.impl.menu

import commons.menu.MenuAdapter
import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.frame
import me.vadim.util.menu.frameWith
import me.vadim.util.menu.toList
import org.bukkit.event.inventory.ClickType
import settings.setting.Setting
import settings.Settings
import settings.impl.SettingsPlugin
import settings.impl.conf.Config
import settings.registry.SettingsHolder
import settings.setting.SettingSection

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class GlobalSettingsMenu(private val plugin: SettingsPlugin, private val holder: SettingsHolder) : MenuAdapter<SettingSection>() {

	override val MENU_SIZE = 9 * 5
	override val BACK_SLOT = 39
	override val DONE_SLOT = 40
	override val NEXT_SLOT = 41

	private fun config(): Config = plugin.configuration.open(Config::class.java)

	private fun getSection(section: SettingSection): MenuList<Setting> =
		template.toList(holder.getLocalSettings(section), transformer = { it.getMenuItem(holder.getOption(it)) }) {
			title = config().title.format(StringPlaceholder.of("section", section.name))

			next = buttons[NEXT_SLOT]!! to NEXT_SLOT
			back = buttons[BACK_SLOT]!! to BACK_SLOT

			frameWith(blank())
			fill = exclude(*frame())

			select = { event, button, setting ->
				val action = when (event.click) {
					ClickType.LEFT, ClickType.SHIFT_LEFT   -> SettingsHolder.SettingAction.NEXT
					ClickType.RIGHT, ClickType.SHIFT_RIGHT -> SettingsHolder.SettingAction.PREV
					ClickType.MIDDLE                       -> SettingsHolder.SettingAction.DEFAULT
					else                                   -> null
				}
				if (action != null) {
					holder.updateOption(setting, action)
					regen()
				}
			}
		}.apply {
			regen()
		}

	override val menu: MenuList<SettingSection> = template.toList(queryItems(), transformer = { it.menuItem }) {
		title = config().title.format(StringPlaceholder.of("section", "Global"))

		next = buttons[NEXT_SLOT]!! to NEXT_SLOT
		back = buttons[BACK_SLOT]!! to BACK_SLOT

		frameWith(blank())
		fill = exclude(*frame())

		select = { event, button, section ->
			getSection(section).open(event.whoClicked)
		}
	}

	override fun queryItems(): MutableList<SettingSection> = Settings.api().sections.sections.toMutableList()

}