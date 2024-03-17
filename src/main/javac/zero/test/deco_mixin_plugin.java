package zero.test;

import btw.AddonHandler;
import btw.BTWAddon;

import org.objectweb.asm.tree.ClassNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

import zero.test.ZeroUtil;

#include "util.h"
#include "feature_flags.h"

public class ZeroTestDecoMixinPlugin implements IMixinConfigPlugin {
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return ZeroUtil.isDecoLoaded();
    }
    
    @Override
    public void onLoad(String mixinPackage) {
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Unclear what this does
    }
    @Override
    public String getRefMapperConfig() {
        // Use default
        return null;
    }
    @Override
    public List<String> getMixins() {
        // No extra mixins needed I think?
        return null;
    }
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // None
    }
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // None
    }
}