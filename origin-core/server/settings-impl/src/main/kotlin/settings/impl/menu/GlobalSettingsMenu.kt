package settings.impl.menu

import me.vadim.util.conf.wrapper.impl.StringPlaceholder
import me.vadim.util.menu.MenuList
import me.vadim.util.menu.frame
import me.vadim.util.menu.frameWith
import me.vadim.util.menu.toList
import org.bukkit.event.inventory.ClickType
import settings.Setting
import settings.Settings
import settings.impl.SettingsPlugin
import settings.option.SettingsOption
import settings.registry.SettingsHolder
import settings.section.SettingSection

/**
 * @author vadim
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class GlobalSettingsMenu(plugin: SettingsPlugin, private val holder: SettingsHolder) : SettingsMenu<SettingSection>(plugin) {

	private fun getSection(section: SettingSection): MenuList<Setting> =
		template.toList(holder.getLocalSettings(section), transformer = { it.menuItem }) {
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

	override fun queryItems(): MutableList<SettingSection> = Settings.api().sections.allSections.toMutableList()

}