package commons.impl.data.account;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.Commons;
import commons.data.account.impl.AbstractAccount;
import commons.econ.BankAccount;
import commons.econ.Currency;
import commons.econ.Transaction;
import commons.econ.TransactionResponse;
import commons.econ.impl.Txn;
import commons.impl.econ.OriginCurrency;
import commons.levels.Level;
import levels.event.ExperienceGainEvent;
import levels.event.LevelUpEvent;
import levels.registry.impl.LevelRegistry;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@DatabaseTable
public class PlayerDefaultAccount extends AbstractAccount implements BankAccount {

	private PlayerDefaultAccount() { // ORMLite
		super(null);
	}

	PlayerDefaultAccount(UUID uuid) {
		super(uuid);
	}

	@DatabaseField
	double currency;
	@DatabaseField
	int level;
	@DatabaseField
	double experience;
	@DatabaseField
	double token;

	final ReadWriteLock cL = new ReentrantReadWriteLock();

	static OriginCurrency checkCurrency(Currency currency) {
		if (!(currency instanceof OriginCurrency o))
			throw new IllegalArgumentException("incompatible currency " + currency);
		return o;
	}

	public void addExperience(BigDecimal value) {
		final LevelRegistry levelRegistry = Commons.commons().levelRegistry;
		if (level > levelRegistry.getLevels().size()) return;

		experience += value.doubleValue();

		new ExperienceGainEvent("commons", getOfflineOwner().getPlayer(), value.doubleValue(), false).callEvent();
	}

	public void addLevel(BigInteger value) {

		final LevelRegistry levelRegistry = Commons.commons().levelRegistry;

		if ((level + value.intValueExact()) > levelRegistry.getLevels().size()) return;

		level += value.intValue();

		final Level lev = levelRegistry.getLevels().get(getLevel().intValueExact() - 1);

		experience -= lev.getRequiredExperience();
		if (experience < 0) experience = 0;

		new LevelUpEvent("commons", getOfflineOwner().getPlayer(), level, false).callEvent();
	}

	public @Nullable BigInteger getLevel() {
		return BigInteger.valueOf(level);
	}

	public @Nullable BigDecimal getExperience() {
		return BigDecimal.valueOf(experience);
	}

	@Override
	public boolean hasBalance(@NotNull Currency currency) {
		return currency instanceof OriginCurrency;
	}

	@Override
	public @Nullable BigDecimal getBalance(@NotNull Currency currency) {
		BigDecimal value;
		if (hasBalance(currency))
			try {
				cL.readLock().lock();
				value = BigDecimal.valueOf(switch (checkCurrency(currency)) {
					case CURRENCY -> this.currency;
					case TOKEN -> this.token;
				});
			} finally {
				cL.readLock().unlock();
			}
		else
			value = null;
		return value;
	}

	@Override
	public void setBalance(@NotNull Currency currency, BigDecimal balance) {
		cL.writeLock().lock();
		try {
			switch (checkCurrency(currency)) {
				case CURRENCY -> this.currency = balance.doubleValue();
				case TOKEN -> this.token = balance.doubleValue();
			}
		} finally {
			cL.writeLock().unlock();
		}
	}

	@Override
	public @NotNull Transaction give(@NotNull Currency currency, @NotNull @NonNull BigDecimal amount) {
		OriginCurrency o = checkCurrency(currency);

		cL.writeLock().lock();
		try {
			switch (o) {
				case CURRENCY -> this.currency += amount.doubleValue();
				case TOKEN -> this.token += amount.doubleValue();
			}
		} finally {
			cL.writeLock().unlock();
		}

		return new Txn(amount, currency, TransactionResponse.CONFIRMED);
	}

	@Override
	public @NotNull Transaction take(@NotNull Currency currency, @NotNull BigDecimal amount) {
		OriginCurrency o = checkCurrency(currency);

		TransactionResponse resp = TransactionResponse.CONFIRMED;
		cL.writeLock().lock();
		try {
			if (switch (o) {
				case CURRENCY -> this.currency;
				case TOKEN -> this.token;
			} >= amount.doubleValue())
				switch (o) {
					case CURRENCY -> this.currency -= amount.doubleValue();
					case TOKEN -> this.token -= amount.doubleValue();
				}
			else
				resp = TransactionResponse.REJECTED;
		} finally {
			cL.writeLock().unlock();
		}

		return new Txn(amount, currency, resp);
	}

}
