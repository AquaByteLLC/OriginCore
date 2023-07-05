package commons.econ;

import java.math.BigDecimal;

/**
 * @author vadim
 */
public interface Transaction {

	BigDecimal getAmount();

	Currency getCurrency();

	TransactionResponse getResult();

}
