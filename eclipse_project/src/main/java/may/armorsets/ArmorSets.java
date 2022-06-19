package may.armorsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import may.armorsets.networking.ArmorSetsPacketHandler;
import may.armorsets.networking.ArmorSetsSwitchSetsPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemStackHandler;

/**
 * The base class of this mod. Here, blocks, items and entities are registered,
 * saving and loading is intiated and basic, general functionality is provided.
 * 
 * @author Natascha May
 * @since 1.18.2-1.0
 */
//The value here should match an entry in the META-INF/mods.toml file
@Mod("armorsets")
public class ArmorSets {

	public static final String MODID = "armorsets";

	public static final Logger LOGGER = LogManager.getLogger();
	public static Minecraft mc = Minecraft.getInstance();

	public static int packetMsgId = 0;
	public static int containerMenuId = 0;
	
	
	public static final KeyMapping AMOR_SETS_SWITCH_KEY = new KeyMapping("Armor Sets Switch", 67, KeyMapping.CATEGORY_INTERFACE);
	
	
	private static final int NUM_OF_SETS = 1;
	static final int SIZE_OF_SET = 4;
	
	public ArmorSets() {
		
		MinecraftForge.EVENT_BUS.register(this);
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		// Register the setup method for modloading
		bus.addListener(this::onFMLClientSetup_registerKeyBindings);
		
		
		ArmorSetsPacketHandler.INSTANCE.registerMessage(packetMsgId++, ArmorSetsSwitchSetsPacket.class,
				(msg, buff) -> msg.write(buff), (buff) -> new ArmorSetsSwitchSetsPacket(buff), (msg, ctx) -> ArmorSetsSwitchSetsPacket.handle(msg, ctx));
		
		// CapabilityManager.INSTANCE.register
		// ItemStackHandler
		// CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
	}
	
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
	    //CapabilityManager.INSTANCE.register(ItemStackHandler.class, new ItemStackHandler(), ItemStackHandler::new);
	}


	
	private void onFMLClientSetup_registerKeyBindings(final FMLClientSetupEvent event) {
		net.minecraftforge.client.ClientRegistry.registerKeyBinding(AMOR_SETS_SWITCH_KEY);
	}
	
	@SubscribeEvent
	public void onWorldLoaded(final WorldEvent.Load event){
	}
	
	
	@SubscribeEvent
    public void onKeyPress (InputEvent.KeyInputEvent e) {
        if (AMOR_SETS_SWITCH_KEY.isDown()) {
        	ArmorSets.LOGGER.debug("Key press recognized");
    		ArmorSetsPacketHandler.INSTANCE.sendToServer(new ArmorSetsSwitchSetsPacket());
        }
    }

	
	@SubscribeEvent
    public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof  Player) {
            event.addCapability(new ResourceLocation(MODID, "armor_set"), new ArmorSetsCapabilityProvider());
        }
    }
	
	@SubscribeEvent
	public void register(RegisterCapabilitiesEvent event) {
        event.register(ItemStackHandler.class);
    }

	public static boolean switchSets(Player player) {
		ArmorSets.LOGGER.debug("Switch Armor Sets");
		
		LazyOptional<ItemStackHandler> capOptional = player.getCapability(ArmorSetsCapabilityProvider.INSTANCE, null);
		if(!capOptional.isPresent()) return false;
		
		ItemStackHandler cap = capOptional.orElse(null);
		
		 
		for(int i = 0; i < SIZE_OF_SET; i++) {
			
			// EXTRACT FROM INVENTORY
			ItemStack stackFromInv = player.getInventory().armor.get(i);
			ArmorSets.LOGGER.debug("extracted " + stackFromInv.toString() + " from inventory");
			
			// EXTRACT FROM ARMOR SET
			ItemStack stackFromSet = cap.getStackInSlot(i);
			
			ArmorSets.LOGGER.debug("extracted " + stackFromSet.toString() + " from armor set");
	
			// INSERT TO INVENTORY
			player.getInventory().armor.set(i, stackFromSet);
	
			// INSERT TO ARMOR SET
			cap.setStackInSlot(i, stackFromInv);
			
		}
		return true;
	}
    

}
