package commons.impl.data.sql;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import commons.util.PackUtil;
import org.bukkit.Location;

import java.sql.SQLException;

/**
 * @author vadim
 */
public class LocationPersister extends BaseDataType {

	public LocationPersister() {
		super(SqlType.LONG, new Class[]{Location.class});
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return PackUtil.packLoc((Location) javaObject);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) {
		return new Location(null, 0, 0, 0);
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return PackUtil.unpackLoc(results.getLong(columnPos));
	}


}
