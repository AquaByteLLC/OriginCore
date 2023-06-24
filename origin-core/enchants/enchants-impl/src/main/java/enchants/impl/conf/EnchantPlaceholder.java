package enchants.impl.conf;

import me.vadim.util.conf.wrapper.impl.StringPlaceholder;

import java.util.Map;

/**
 * @author vadim
 */
public class EnchantPlaceholder extends StringPlaceholder {

	private static final String format = "%%%s%%";

	public EnchantPlaceholder(String format, Map<String, String> placeholders) {
		super(format, placeholders);
	}

	public static Builder builder() { return StringPlaceholder.builder().setFormat(format); }

	public static StringPlaceholder of(String key, String value) { return builder().set(key, value).build(); }

}
