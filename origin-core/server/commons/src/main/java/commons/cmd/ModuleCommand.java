package commons.cmd;

import co.aikar.commands.BaseCommand;
import commons.OriginModule;

import java.util.Map;

/**
 * @author vadim
 */
abstract class ModuleCommand extends BaseCommand {

	protected final Map<String, OriginModule> modulesView;

	ModuleCommand(Map<String, OriginModule> modulesView) {
		this.modulesView = modulesView;
	}

}
