package api.option;

public interface SettingsOption {
	String getName();

	String getDescription();

	boolean isActive();

	void activate();

	void deactivate();
}
