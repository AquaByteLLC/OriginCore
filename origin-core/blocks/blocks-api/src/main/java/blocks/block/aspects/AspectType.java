package blocks.block.aspects;

import lombok.Getter;

public enum AspectType {

	OVERLAYABLE("overlayable"),
	LOCATABLE("locatable"),
	EFFECTABLE("effectable"),
	HARDENABLE("hardenable"),
	REGENABLE("regenable"),
	PROJECTABLE("projectable"),
	DROPABLE("drop");

	@Getter
	private final String name;

	AspectType(String name) {
		this.name = name;
	}


}
