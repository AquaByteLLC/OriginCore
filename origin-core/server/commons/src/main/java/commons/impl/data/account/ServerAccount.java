package commons.impl.data.account;

import commons.econ.BankAccount;
import commons.econ.Currency;
import commons.econ.Transaction;
import commons.econ.TransactionResponse;
import commons.econ.impl.Txn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * @author vadim
 */
public class ServerAccount implements BankAccount {

	@Override
	public boolean hasBalance(@NotNull Currency currency) {
		return true;
	}

	@Override
	public @Nullable BigDecimal getBalance(@NotNull Currency currency) {
		return new BigDecimal(Double.MAX_VALUE); // infinity
	}

	@Override
	public void setBalance(@NotNull Currency currency, BigDecimal balance) {
		// NOP
	}

	@Override
	public @NotNull Transaction give(@NotNull Currency currency, @NotNull BigDecimal amount) {
		return new Txn(amount, currency, TransactionResponse.CONFIRMED);
	}

	@Override
	public @NotNull Transaction take(@NotNull Currency currency, @NotNull BigDecimal amount) {
		return new Txn(amount, currency, TransactionResponse.CONFIRMED);
	}

}
