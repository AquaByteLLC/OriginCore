package commons.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import commons.data.AccountProvider;
import commons.econ.BankAccount;
import commons.econ.Transaction;
import commons.econ.TransactionResponse;
import commons.impl.OriginCurrency;
import commons.impl.account.PlayerDefaultAccount;
import commons.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * @author vadim
 */
@CommandAlias("econ")
public class EconCommand extends BaseCommand {

	private static final String ECON = "&r&7[&l&6econ&r&7]&r ";

	private final AccountProvider<PlayerDefaultAccount> accounts;

	public EconCommand(AccountProvider<PlayerDefaultAccount> accounts) {
		this.accounts = accounts;
	}

	@Subcommand("get")
	@CommandPermission("*")
	public void get(CommandSender sender, @Flags("other") Player target, OriginCurrency currency) {
		BankAccount account = accounts.getAccount(target);

		// Player has X
		StringUtil.send(sender, ECON + String.format("&b%s&e has &d%s", target.getName(), currency.format(account.getBalance(currency))));
	}

	@Subcommand("set")
	@CommandPermission("*")
	public void set(CommandSender sender, @Flags("other") Player target, OriginCurrency currency, double amount) {
		BankAccount account = accounts.getAccount(target);

		BigDecimal amt = BigDecimal.valueOf(amount);
		account.setBalance(currency, amt);
		// Player now has X
		StringUtil.send(sender, ECON + String.format("&b%s&e now has &d%s", target.getName(), currency.format(amt)));
	}

	@Subcommand("give")
	@CommandPermission("*")
	public void give(CommandSender sender, @Flags("other") Player target, OriginCurrency currency, double amount) {
		BankAccount account = accounts.getAccount(target);

		BigDecimal  amt = BigDecimal.valueOf(amount);
		Transaction txn = account.give(currency, amt);
		// Gave X to Player. New balance: Y
		StringUtil.send(sender,
						ECON + String.format("&aGave &d%s&e to &b%s&a.&e New balance: &d%s",
											 currency.format(BigDecimal.valueOf(amount)),
											 target.getName(), currency.format(account.getBalance(currency)))
					   );
	}

	@Subcommand("take")
	@CommandPermission("*")
	public void take(CommandSender sender, @Flags("other") Player target, OriginCurrency currency, double amount) {
		BankAccount account = accounts.getAccount(target);

		BigDecimal  amt = BigDecimal.valueOf(amount);
		Transaction txn = account.take(currency, amt);
		if (txn.getResult() == TransactionResponse.CONFIRMED)
			// Took X from Player. New balance: Y
			StringUtil.send(sender,
							ECON + String.format("&aTook &d%s&e from &b%s&a.&e New balance: &d%s",
												 currency.format(BigDecimal.valueOf(amount)),
												 target.getName(), currency.format(account.getBalance(currency)))
						   );
		else
			// Cannot take X from Player as they only have Y
			StringUtil.send(sender,
							ECON + String.format("&cCannot take &d%s&c from &b%s&c as they only have &d%s",
												 currency.format(BigDecimal.valueOf(amount)),
												 target.getName(), currency.format(account.getBalance(currency)))
						   );
	}

}
