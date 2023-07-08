package commons.cmd;

/**
 * @author vadim
 */
final class Prefix {

	static final String ECON = "&r&7[&l&6econ&r&7]&r ";
	static final String MODULES = "&r&7[&l&dmodules&r&7]&r ";
	static final String ERR = "&4[&c!&4]&r ";

	static String exception(Throwable e) {
		String msg = e.getMessage();
		if(msg == null || "null".equals(msg)) msg = "";
		return ERR + String.format("&c&l%s&4(&d%s&4)", e.getClass().getSimpleName(), msg);
	}
}
