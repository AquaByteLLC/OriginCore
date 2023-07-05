package commons.econ.impl;

import commons.econ.BankAccount;
import commons.econ.Currency;
import commons.econ.Transaction;
import commons.econ.TransactionResponse;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * @author vadim
 */
public class Txn implements Transaction {

	private final BigDecimal amount;
	private final Currency currency;
	private final TransactionResponse resp;

	public Txn(@NonNull BigDecimal amount, @NonNull Currency currency, @NonNull TransactionResponse resp) {
		this.amount   = amount;
		this.currency = currency;
		this.resp     = resp;
	}

	@Override
	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public Currency getCurrency() {
		return currency;
	}

	@Override
	public TransactionResponse getResult() {
		return resp;
	}

}
