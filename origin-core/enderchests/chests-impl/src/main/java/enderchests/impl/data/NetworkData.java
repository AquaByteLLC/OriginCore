package enderchests.impl.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.util.num.PackUtil;
import enderchests.NetworkColor;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.UUID;

/**
 * ORMLite version of {@link enderchests.impl.EnderChestNetwork}. I though this might be cleaner than cluttering up the impl class.
 * @author vadim
 */
@DatabaseTable
class NetworkData {

	@DatabaseField(columnName = "owner_uuid")
	UUID owner;

	@DatabaseField
	NetworkColor color;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	byte[] inventory;

	Inventory getInventory() throws IOException {
		return PackUtil.inventoryFromBase64(inventory);
	}

	void setInventory(Inventory inventory) throws IOException {
		this.inventory = PackUtil.inventoryToBase64(inventory);
	}

}
