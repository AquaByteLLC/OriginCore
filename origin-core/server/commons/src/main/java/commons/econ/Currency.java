package commons.econ;

import java.math.BigDecimal;

/**
 * Abstraction for potential CurrencyType enums.
 * @author vadim
 */
public interface Currency {

	/**
	 * @return the enumerated name of this currency
	 */
	String name();

	/**
	 * @return the human-readable name of this currency
	 */
	String getName();

	/**
	 * @param amount the number to format
	 * @return the human-readable formatted value of this currency, including prefix/postfix and decimal format notation
	 */
	String format(BigDecimal amount);

}
