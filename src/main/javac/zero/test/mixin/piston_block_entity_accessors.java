package zero.test.mixin;

import net.minecraft.src.*;

import java.util.List;

import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(TileEntityPiston.class)
public interface IBlockEntityPistonAccessMixins {
    @Accessor
    public List getPushedObjects();
    @Accessor
    public float getProgress();
    @Accessor
    public void setProgress(float value);
    @Accessor
    public float getLastProgress();
    @Accessor
    public void setLastProgress(float value);
    
    @Invoker("destroyAndDropIfShoveled")
    public abstract boolean callDestroyAndDropIfShoveled();
    @Invoker("preBlockPlaced")
    public abstract void callPreBlockPlaced();
}