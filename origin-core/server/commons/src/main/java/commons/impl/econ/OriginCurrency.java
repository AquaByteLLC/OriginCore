package commons.impl.econ;

import commons.econ.Currency;
import commons.util.StringUtil;

import java.math.BigDecimal;

/**
 * @author vadim
 */
public enum OriginCurrency implements Currency {

	/**
	 * Usage
	 */
	TOKEN,

	/**
	 * Usage
	 */
	CURRENCY;


	@Override
	public String getName() {
		return StringUtil.convertToUserFriendlyCase(name());
	}

	@Override
	public String format(BigDecimal amount) {
		if(amount == null)
			return "\u221E"; // ∞
		String fmt = StringUtil.formatNumber(amount.doubleValue());

		if(this == TOKEN)
			fmt += " $TOK";
		if(this == CURRENCY)
			fmt = "$" + fmt;

		return fmt;
	}

}
