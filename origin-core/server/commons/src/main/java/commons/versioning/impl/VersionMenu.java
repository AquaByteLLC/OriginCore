package commons.versioning.impl;

import commons.versioning.api.MenuVersioned;
import me.vadim.util.menu.Menu;

public record VersionMenu(Menu legacy, Menu nonLegacy) implements MenuVersioned { }
