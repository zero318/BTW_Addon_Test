package zero.test.block;

import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import zero.test.IBaseRailBlockMixins;

#include "..\feature_flags.h"
#include "..\util.h"

public class SteelRailBlock extends BlockRail implements IBaseRailBlockMixins {
    
    public SteelRailBlock(int blockId) {
        super(blockId);
        this.setPicksEffectiveOn();
        this.setHardness(0.7F);
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName("steel_rail");
    }
    
    @Override
    public double getRailMaxSpeedFactor() {
        return 2.0D;
    }
    
    @Environment(EnvType.CLIENT)
    private Icon cornerIcon;
    
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.cornerIcon = iconRegister.registerIcon("steel_rail_corner");
    }
    
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par2 >= 6 ? this.cornerIcon : this.blockIcon;
    }
}