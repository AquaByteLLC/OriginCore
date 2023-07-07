package enderchests.impl.data;


import com.j256.ormlite.table.DatabaseTable;
import commons.Commons;
import commons.data.account.impl.AbstractAccount;
import enderchests.NetworkColor;
import enderchests.impl.LinkedEnderChest;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

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

}