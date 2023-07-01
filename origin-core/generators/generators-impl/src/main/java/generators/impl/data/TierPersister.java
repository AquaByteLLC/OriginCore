package generators.impl.data;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import generators.impl.conf.Tiers;
import generators.wrapper.Tier;
import me.vadim.util.conf.ConfigurationProvider;

import java.sql.SQLException;

/**
 * @author vadim
 */
public class TierPersister extends BaseDataType {

	private final ConfigurationProvider conf;

	public TierPersister(ConfigurationProvider conf) {
		super(SqlType.INTEGER, new Class[]{Tier.class});
		this.conf = conf;
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return ((Tier) javaObject).getIndex();
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) {
		return conf.open(Tiers.class).findTier(Integer.parseInt(defaultStr));
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return conf.open(Tiers.class).findTier(results.getInt(columnPos));
	}

}
