package lib.brainsynder.anvil;

public enum AnvilSlot {
	INPUT_LEFT(0),
	INPUT_CENTER(1),
	OUTPUT(2);

	private int slot;

	AnvilSlot(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	public static AnvilSlot bySlot(int slot) {
		for (AnvilSlot anvilSlot : values()) {
			if (anvilSlot.getSlot() == slot) {
				return anvilSlot;
			}
		}

		return null;
	}
}