package settings.impl.setting.key;

import java.util.regex.Pattern;

/**
 * @author vadim
 */
class SKey {

	static final Pattern LEGAL = Pattern.compile("[0-9_a-z#\\-]+");
	static final Pattern ILLEGAL = Pattern.compile("[^.0-9_a-z#\\-]");

	static final char DELIM = '.';
	static final char TAIL = ':';

}
