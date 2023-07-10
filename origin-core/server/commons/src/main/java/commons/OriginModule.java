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

	default void afterReload() throws Exception {}

	default void onSave() throws Exception {}

}
