package settings.impl.setting.key;

import commons.util.StringUtil;
import settings.setting.key.LocalKey;

import java.util.Objects;

import static settings.impl.setting.key.SKey.*;

/**
 * @author vadim
 */
public class LKey implements LocalKey {

	private final String identifier;

	public LKey(String identifier) {
		if(identifier == null || !LEGAL.matcher(identifier).matches())
			throw new IllegalArgumentException("Invalid identifier: "+identifier);
		this.identifier = identifier;
	}

	public static LocalKey of(String identifier) {
		if(identifier == null) throw new NullPointerException();
		return new LKey(identifier);
	}

	public static LocalKey convert(String illegal) {
		if(illegal == null) throw new NullPointerException();
		illegal = StringUtil.stripColor(illegal);
		illegal = illegal.toLowerCase().replace(' ', '_');
		illegal = ILLEGAL.matcher(illegal).replaceAll("");
		if(illegal.isBlank())
			throw new IllegalArgumentException("Key is blank after conversion.");
		return of(illegal);
	}

	@Override
	public String identifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LocalKey key)) return false;
		return Objects.equals(identifier, key.identifier());
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	@Override
	public String toString() {
		return identifier;
	}

}
