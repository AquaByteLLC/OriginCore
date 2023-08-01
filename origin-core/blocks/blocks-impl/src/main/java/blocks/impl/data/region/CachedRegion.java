package blocks.impl.data.region;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * @author vadim
 */
@DatabaseTable
class CachedRegion {

	@DatabaseField
	String wgRegion;

	@DatabaseField
	String block;

	@DatabaseField
	UUID world;

}