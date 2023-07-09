package commons.conf.wrapper;

import commons.util.StringUtil;
import me.vadim.util.conf.wrapper.Placeholder;
import me.vadim.util.conf.wrapper.PlaceholderMessage;
import me.vadim.util.conf.wrapper.impl.UnformattedMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author vadim
 */
public class OptionalMessage extends UnformattedMessage {

	private final boolean send;

	public OptionalMessage(@Nullable String value) {
		super(Optional.ofNullable(value).orElse(""));
		this.send = value != null;
	}

	public OptionalMessage(@Nullable PlaceholderMessage value) {
		this(Optional.ofNullable(value).map(PlaceholderMessage::raw).orElse(null));
	}

	public void sendTo(CommandSender sender, Placeholder placeholder) {
		if(send)
			StringUtil.send(sender, format(placeholder));
	}

}
