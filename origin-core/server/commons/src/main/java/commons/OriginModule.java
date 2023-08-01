package commons;

import commons.data.account.AccountStorage;
import me.vadim.util.conf.ConfigurationManager;

/**
 * @author vadim
 */
public interface OriginModule {

	String getName();

	AccountStorage<?> getAccounts();

	ConfigurationManager getConfigurationManager();

	/**
	 * /reload callback.
	 */
	default void afterReload() throws Exception {}

	/**
	 * Autosave callback.
	 */
	default void onSave() throws Exception {}

}
