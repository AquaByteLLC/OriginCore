package blocks.block.illusions;

import blocks.block.util.PacketReceiver;

/**
 * @author vadim
 */
public interface Illusion {

	/**
	 * Send necessary illusion creations to all receivers.
	 */
	void send(PacketReceiver receiver);

	/**
	 * Send necessary illusion removals to all receivers.
	 */
	void remove(PacketReceiver receiver);

}
