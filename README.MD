# `OriginCore`


# BlocksAPI
__SETUP__
- When using the blocks system you want to make sure you first register everything which is required. Below you can see the different code blocks you'll need to add to the plugin for it to function with the BlocksAPI.  
```kts
compileOnly(project(":blocks-api"))
compileOnly(project(":blocks-impl"))
```

- Once you've added this to the build script you can begin to implement the api.
```java
private EventRegistry eventRegistry;
private LiteConfig lfc;
private MiningAPI miningApi;
private BlocksPlugin blocksPlugin;

void onEnable(){
    final this.eventRegistry = CommonsPlugin.commons().getEventRegistry();
    final this.lfc = new LiteConfig(this);
    final this.miningApi = new MiningAPI(this);
    final this.blocksPlugin new BlocksPlugin(this,lfc,eventRegistry);

    lfc.register(BlocksConfig.class,BlocksConfig::new);
    lfc.reload();
}
```

- Once you've added the stuff above to the MainClass of the plugin you will then need to register the blocks which will be in use. 

```java
BlockHandler.init(conf -> {
	String name = conf.getString("name");
	double hardness = conf.getDouble("hardness");
	int modelData = conf.getInt("modelData");
	
	OriginBlockBuilder blockBuilder = new OriginBlockFactoryImpl().newBlock();
	AspectFactory factory = blockBuilder.getFactory();
	
	blockBuilder.setName(name).setModelData(modelData);
	
	Hardenable harden = factory.newHardenable();
	harden.setHardnessMultiplier(hardness);
	
	blockBuilder.createAspect(harden);
});
```

- Above is a simple use of the api, you can use it to do much more such as overlays, custom break speed, faking block projection, summon custom packet based entities, custom drops, etc. The different aspects are listed below.
### Types
- Hardenable - Allows for custom hardness multipliers and break times on any block.
- Dropable - Allows for custom blocks to be dropped from the block.
- Effectable - Allows for different effects to be done when block is broken. (Sound, Particle, etc)
- FakeBlock - Allows you to change the way the viewer sees the block.
- Overlayable - Allows for a custom overlay to be added to the block changing the shading.
- Locatable - Allows for an object of the block to be located. 
- BlockLocatable - Allows for the block to be located.
- Regenable - Allows for the block to regenerate. 
