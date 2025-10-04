package com.glodblock.github.modularbees.mixins;

import com.glodblock.github.modularbees.xmod.ModIDs;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MBMixinConfig implements IMixinConfigPlugin {

    private static final Object2ObjectMap<String, String> MOD_MIXINS = new Object2ObjectOpenHashMap<>();

    static {
        MOD_MIXINS.put("com.glodblock.github.modularbees.mixins.NeoForgeClientXplatImplMixin", ModIDs.PATCHOULI);
        MOD_MIXINS.put("com.glodblock.github.modularbees.mixins.ItemBlockCardboardBoxMixin", ModIDs.MEKANISM);
    }

    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !MOD_MIXINS.containsKey(mixinClassName) || isModLoaded(MOD_MIXINS.get(mixinClassName));
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    private static boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modId::equals);
        } else {
            return ModList.get().isLoaded(modId);
        }
    }

}
