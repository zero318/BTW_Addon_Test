package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;

import java.util.List;
import java.util.ArrayList;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(World.class)
public interface IWorldAccessMixins {
    @Accessor
    public boolean getScanningTileEntities();
    //@Accessor
    //public ArrayList getCollidingBoundingBoxes();
    @Accessor
    public void setScanningTileEntities(boolean value);
}