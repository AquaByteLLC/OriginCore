package settings.impl.data;

import com.j256.ormlite.dao.Dao;
import commons.data.sql.SessionProvider;
import commons.data.account.impl.ORMLiteAccountStorage;
import settings.impl.setting.key.GKey;
import settings.registry.SectionRegistry;
import settings.setting.Setting;
import settings.setting.SettingOption;
import settings.setting.SettingSection;
import settings.setting.key.GlobalKey;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class SettingsAccountStorage extends ORMLiteAccountStorage<SettingsAccount> {

	private static final char DELIM = ';';
	private static final GlobalKey SECTION = GKey.of("#section#");

	private final SectionRegistry registry;

	public SettingsAccountStorage(SessionProvider provider, SectionRegistry registry) {
		super(SettingsAccount::new, provider, SettingsAccount.class);
		this.registry = registry;
	}

	@Override
	protected void save(SettingsAccount account, Dao<SettingsAccount, UUID> dao) throws SQLException {
		StringBuilder builder = new StringBuilder(1000);
		for (SettingSection section : registry.getSections()) {
			for (Setting setting : account.holder.getLocalSettings(section)) {
				SettingOption option = account.holder.getOption(setting);

				GlobalKey path = section.getKey().append(SECTION).append(setting.getKey()).withTail(option.getKey());
				builder.append(path).append(DELIM);
			}
		}
		account.serialized = Base64.getEncoder().encode(builder.toString().getBytes(StandardCharsets.UTF_8));
		dao.createOrUpdate(account);
	}

	@Override
	protected SettingsAccount load(UUID uuid, Dao<SettingsAccount, UUID> dao) throws SQLException {
		SettingsAccount account = dao.queryForId(uuid);
		if (account == null)
			account = factory.create(uuid);
		account.init();
		if (account.serialized != null && account.serialized.length > 0)
			for (String s : new String(Base64.getDecoder().decode(account.serialized), StandardCharsets.UTF_8).split(String.valueOf(DELIM))) {
				GlobalKey key   = GKey.of(s);
				GlobalKey sec   = null;
				String[]  parts = key.parts();

				int i = 0;
				for (String part : parts) {
					i++;
					if (SECTION.full().equals(part))
						break;

					if (sec == null)
						sec = GKey.of(part);
					else
						sec = sec.append(GKey.of(part));
				}
				if (sec == null) continue;
				SettingSection section = registry.getByKey(sec);
				if(section == null) {
					System.err.println("[settings] w: Removing unknown setting section key " + sec);
					continue;
				}

				GlobalKey set = null;
				for (; i < parts.length; i++) {
					String part = parts[i];
					if (set == null)
						set = GKey.of(part);
					else
						set = set.append(GKey.of(part));
				}
				if(set == null) continue;
				Setting setting = section.querySetting(set);
				if(setting == null) {
					System.err.println("[settings] w: Removing unknown setting key "+set);
					continue;
				}
				set = set.withTail(key.getTail()); // #parts() does not return tail

				System.out.println("[settings] i: Setting "+sec.append(set).withTail(set.getTail())+" on account "+uuid);
				account.holder.setOption(setting, set.hasTail() ? setting.getOption(set.getTail()) : setting.getDefaultOption());
			}
		return account;
	}

}
