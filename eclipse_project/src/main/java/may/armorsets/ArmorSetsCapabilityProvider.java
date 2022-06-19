package may.armorsets;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class ArmorSetsCapabilityProvider implements ICapabilitySerializable<CompoundTag>{

	ItemStackHandler armorSet = new ItemStackHandler(ArmorSets.SIZE_OF_SET);

	public static final Capability<ItemStackHandler> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
	private LazyOptional<ItemStackHandler> armorSetOptional = LazyOptional.of(() -> armorSet);

	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == INSTANCE)
			return armorSetOptional.cast();
		return LazyOptional.empty();
	}


	
	@Override
	public CompoundTag serializeNBT() {
        return armorSet.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		armorSet.deserializeNBT(nbt);		
	}

}
