# `OriginCore`

# dev.originmc.originmc.PacketAPI


__SETUP__

```java
public class MainJavaPlugin extends JavaPlugin {
	
	public void onEnable() {
		dev.originmc.originmc.PacketAPI.
    }
}
```

__USAGE__

```java
public class TestPacketClass {
    private static Field blockPositionDigIn;
    private static Field enumPlayerDigTypeInDig;
	
    static {
        try {
            blockPositionDigIn = PacketPlayInBlockDig.class.getDeclaredField("a");
            enumPlayerDigTypeInDig = PacketPlayInBlockDig.class.getDeclaredField("c");
            blockPositionDigIn.setAccessible(true);
            enumPlayerDigTypeInDig.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
	
    public TestPacketClass() {
        EventSubscriptions.instance.subscribe(this, getClass());
    }
	
    @EventSubscription
    private void onPacketPlayInDig(PacketPlayInBlockDigImpl event) {
        PacketPlayInBlockDig packet = event.getPacket();
        BlockPosition blockPos = (BlockPosition) blockPositionDigIn.get(packet);
    }
}
```