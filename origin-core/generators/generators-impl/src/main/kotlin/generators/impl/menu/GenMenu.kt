package generators.impl.menu

import commons.data.account.AccountStorage
import commons.menu.MenuListAdapter
import generators.GeneratorRegistry
import generators.impl.GensPlugin
import generators.impl.conf.Config
import generators.impl.conf.Messages
import generators.impl.data.GenAccount
import me.vadim.util.conf.ConfigurationProvider

/**
 * @author vadim
 */
abstract class GenMenu<T>(plugin: GensPlugin) : MenuListAdapter<T>() {

    final override val MENU_SIZE = 6 * 9
    final override val BACK_SLOT = 48
    final override val DONE_SLOT = 49
    final override val NEXT_SLOT = 50

    protected val conf: ConfigurationProvider = plugin.configurationManager
    protected val accounts: AccountStorage<GenAccount> = plugin.accounts
    protected val reg: GeneratorRegistry = plugin.registry

    protected fun config(): Config = conf.open(Config::class.java)
    protected fun msg(): Messages = conf.open(Messages::class.java)

}