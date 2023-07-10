package enderchests.impl.data;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import commons.Commons;
import commons.data.account.impl.AbstractAccount;
import enderchests.ChestNetwork;
import enderchests.ChestRegistry;
import enderchests.LinkedChest;
import enderchests.NetworkColor;
import enderchests.impl.EnderChestMenu;
import enderchests.impl.EnderChestsPlugin;
import enderchests.impl.LinkedEnderChest;
import me.vadim.util.menu.Menu;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author vadim
 */
@DatabaseTable
public class EnderChestAccount extends AbstractAccount {

	private EnderChestAccount() { // ORMLite
		super(null);
	}

	EnderChestAccount(UUID uuid) {
		super(uuid);
	}

	public NetworkColor temp = NetworkColor.WHITE;

	private LinkedEnderChest currentLinkedInventory;
	private final Object invLock = new Object();

	/**
	 * Note that this method is <i>not</i> atomic.
	 *
	 * @return {@code true} if the player is viewing a linked chest inventory
	 */
	public boolean isViewingLinkedInventory() {
		return currentLinkedInventory != null;
	}

	/**
	 * Note that this method is <i>not</i> atomic.
	 *
	 * @return the current linked chest that is being viewed, or {@code null}
	 */
	public LinkedEnderChest getOpenLinkedInventory() {
		return currentLinkedInventory;
	}

	/**
	 * @see #openNewLinkedInventory(LinkedEnderChest, boolean)
	 */
	public void openNewLinkedInventory(LinkedEnderChest newInventory) {
		openNewLinkedInventory(newInventory, false);
	}

	/**
	 * This method <i>atomically</i> sets the currently linked inventory.
	 *
	 * @param newInventory  the new inventory to open, or {@code null} to indicate that the player has closed any linked inventory
	 * @param delayedEffect whether or not to delay the opening animation (this is necessary under some circumstances)
	 */
	public void openNewLinkedInventory(LinkedEnderChest newInventory, boolean delayedEffect) {
		Player player = getOfflineOwner().getPlayer();
		if (player != null) {
			if (newInventory != null) {
				player.swingMainHand();
				Commons.scheduler().getBukkitSync().runTask(this::openInventory0);
			}
			// do not call player.closeInventory() due to recursive event invokation
		}

		synchronized (invLock) { // updateAnimation does not work here due to the update order of getViewers()
			if (currentLinkedInventory != null)
				currentLinkedInventory.setOpen(currentLinkedInventory.getInventory().getViewers().size() > 1);
			currentLinkedInventory = newInventory;
			if (delayedEffect) {
				// for some reason, 6L is the shortest amount of time that this will still run
				// when clicking on the bottom of the chest... so let's just do it twice
				Commons.scheduler().getBukkitSync().runLater(() -> {
					if (currentLinkedInventory != null)
						currentLinkedInventory.setOpen(true);
				}, 6L);
			} else {
				if (currentLinkedInventory != null)
					currentLinkedInventory.setOpen(true);
			}
		}
	}

	private void openInventory0() {
		OfflinePlayer player = getOfflineOwner();
		synchronized (invLock) {
			if (player.getPlayer() != null && player.isOnline() && currentLinkedInventory != null)
				player.getPlayer().openInventory(currentLinkedInventory.getInventory());
		}
	}

	@DatabaseField
	public int slotLimit = EnderChestsPlugin.singletonCringe().config().getDefaultsSlots();

	public boolean atSlotLimit(NetworkColor net) {
		return EnderChestsPlugin.singletonCringe().getChestRegistry().getNetwork(net, getOfflineOwner()).getSlotsUsed() >= slotLimit;
	}

	private EnderChestMenu menu;

	private Location pendingLocation;
	private BlockFace pendingFace;

	public void setPending(Location location, BlockFace face) {
		Player player = getOfflineOwner().getPlayer();
		if (player == null)
			throw new UnsupportedOperationException("owner offline");

		pendingLocation = location.clone();
		pendingFace     = face;

		if (menu == null) // lazy menu creation b/c of ORMLite
			menu = new EnderChestMenu(EnderChestsPlugin.singletonCringe(), getOwnerUUID());

		Menu menu = this.menu.getMenu();
		menu.regen();
		menu.open(player);
	}

	public void selectColor(NetworkColor color) {
		if (pendingLocation == null || pendingFace == null)
			throw new UnsupportedOperationException("no color selection pending");
		ChestRegistry reg   = EnderChestsPlugin.singletonCringe().getChestRegistry();
		ChestNetwork  net   = reg.getNetwork(color, getOfflineOwner());
		LinkedChest   chest = reg.createChest(net, pendingLocation, pendingFace);
	}

	public void clearPending() {
		pendingLocation = null;
		pendingFace     = null;
	}

}