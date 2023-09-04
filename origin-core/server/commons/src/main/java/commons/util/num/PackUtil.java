package commons.util.num;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * @author vadim
 */
public class PackUtil {

	public static final Location ZERO = new Location(null, 0, 0, 0);

	public static long packLoc(int x, int y, int z) {
		return ((long) x & 67108863L) << 38 | (long) y & 4095L | ((long) z & 67108863L) << 12;
	}

	public static long packLoc(Location location) {
		return packLoc(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static Location unpackLoc(long location) {
		return new Location(null, (int) (location >> 38), (int) (location << 52 >> 52), (int) (location << 26 >> 38));
	}

	public static byte[] uuid2bytes(UUID uid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uid.getMostSignificantBits());
		bb.putLong(uid.getLeastSignificantBits());
		return bb.array();
	}

	public static UUID bytes2uuid(byte[] buf) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
		return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
	}

	// copied & deobf'd from ChunkCoordIntPair

	public static long packChunk(int x, int z) {
		return (long)x & 0xffffffffL | ((long)z & 0xffffffffL) << 32;
	}

	public static long packChunk(Location location) {
		return packChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
	}

	public static int unpackChunkX(long var0) {
		return (int)(var0 & 0xffffffffL);
	}

	public static int unpackChunkZ(long var0) {
		return (int)(var0 >>> 32 & 0xffffffffL);
	}

	//@formatter:off

	/**
     * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
     *
     * @param playerInventory to turn into an array of strings.
     * @return Array of strings: [ main content, armor content ]
	 * @author graywolf336
	 * @link <a href="https://gist.github.com/graywolf336/8153678">Source</a>
     */
    public static byte[][] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
    	//get the main content part, this doesn't return the armor
    	byte[] content = inventoryToBase64(playerInventory);
    	byte[] armor = itemStackArrayToBase64(playerInventory.getArmorContents());

    	return new byte[][] { content, armor };
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * <p />
     *
     * Based off of {@link #inventoryToBase64 (Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
	 * @author graywolf336
	 * @link <a href="https://gist.github.com/graywolf336/8153678">Source</a>
     */
    public static byte[] itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64.getEncoder().encode(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to serialize an inventory to Base64 string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
	 * @author graywolf336
	 * @link <a href="https://gist.github.com/graywolf336/8153678">Source</a>
	 */
    public static byte[] inventoryToBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64.getEncoder().encode(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
	 * @author graywolf336
	 * @link <a href="https://gist.github.com/graywolf336/8153678">Source</a>
     */
    public static Inventory inventoryFromBase64(byte[] data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
	 * Gets an array of ItemStacks from Base64 string.
	 *
	 * <p />
	 *
	 * Base off of {@link #inventoryFromBase64(byte[])}.
	 *
	 * @param data Base64 string to convert to ItemStack array.
	 * @return ItemStack array created from the Base64 string.
	 * @author graywolf336
	 * @link <a href="https://gist.github.com/graywolf336/8153678">Source</a>
	 */
    public static ItemStack[] itemStackArrayFromBase64(byte[] data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

	//@formatter:on

}
