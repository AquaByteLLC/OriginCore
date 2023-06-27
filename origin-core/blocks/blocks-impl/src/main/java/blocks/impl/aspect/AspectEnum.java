package blocks.impl.aspect;

import lombok.Getter;

public enum AspectEnum {

	OVERLAYABLE("overlayable"),
	LOCATABLE("locatable"),
	EFFECTABLE("effectable"),
	HARDENABLE("hardenable"),
	REGENABLE("regenable"),
	FAKE_BLOCK("fake_block"),
	DROPABLE("drop");

	@Getter private final String name;
	AspectEnum(String name){this.name = name;}


}
