package commons.econ;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Represents an account that holds {@linkplain Currency currencies},
 * and may be transacted {@linkplain #give(Currency, BigDecimal) to} and {@linkplain #take(Currency, BigDecimal) from}.
 * @author vadim
 */
public interface BankAccount {

	/**
	 * @param currency the {@link Currency} to query for
	 * @return whether or not this account holds {@code currency}
	 */
	boolean hasBalance(@NotNull Currency currency);

	/**
	 * @param currency the {@link Currency} to query for
	 * @return the current amount of {@code currency} this account is holding, or {@code null} if this account does not {@linkplain #hasBalance(Currency) hold} {@code currency}
	 */
	@Nullable BigDecimal getBalance(@NotNull Currency currency);

	/**
	 * Update an account's balance.
	 * @param currency the {@link Currency} to set
	 * @param balance the new balance to set
	 */
	void setBalance(@NotNull Currency currency, BigDecimal balance);

	/**
	 * Deposit {@code amount} of {@code currency} into this account.
	 * @param currency the {@link Currency} in this {@link Transaction}
	 * @param amount the {@link Transaction#getAmount() amount} of {@link Currency} involved in this {@link Transaction}
	 * @return the resulting {@link Transaction}
	 */
	@NotNull Transaction give(@NotNull Currency currency, @NotNull BigDecimal amount);

	/**
	 * Withdraw {@code amount} of {@code currency} from this account.
	 * @param currency the {@link Currency} in this {@link Transaction}
	 * @param amount the {@link Transaction#getAmount() amount} of {@link Currency} involved in this {@link Transaction}
	 * @return the resulting {@link Transaction}
	 */
	@NotNull Transaction take(@NotNull Currency currency, @NotNull BigDecimal amount);

}
