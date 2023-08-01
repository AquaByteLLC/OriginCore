package commons.menu

import commons.CommonsPlugin
import commons.data.account.AccountStorage
import commons.impl.data.account.PlayerDefaultAccount
import levels.conf.LevelsYml
import levels.registry.impl.LevelRegistry
import me.vadim.util.conf.ConfigurationProvider

/**
 * @author vadim
 */
abstract class LevelMenu<T>(plugin: CommonsPlugin) : MenuAdapter<T>() {

    final override val MENU_SIZE = 6 * 9
    final override val BACK_SLOT = 48
    final override val DONE_SLOT = 49
    final override val NEXT_SLOT = 50

    protected val conf: ConfigurationProvider = plugin.configurationManager
    protected val accounts: AccountStorage<PlayerDefaultAccount> = plugin.accounts
    protected val reg: LevelRegistry = plugin.levelRegistry

    protected fun config(): LevelsYml = conf.open(LevelsYml::class.java)
}