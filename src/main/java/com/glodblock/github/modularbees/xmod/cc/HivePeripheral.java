package com.glodblock.github.modularbees.xmod.cc;

import com.glodblock.github.modularbees.ModularBees;
import com.glodblock.github.modularbees.common.tileentities.hive.TileModularBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HivePeripheral implements GenericPeripheral {

    @LuaFunction(mainThread = true)
    public final int getBeesCount(TileModularBeehive hive) {
        if (!hive.isFormed()) {
            return -1;
        } else {
            return hive.getBeeTable().getBeeCount();
        }
    }

    @LuaFunction(mainThread = true)
    public final Object getBeeInfo(TileModularBeehive hive) {
        if (!hive.isFormed()) {
            return null;
        } else {
            Map<String, BeeInfo> result = new HashMap<>();
            var raw = hive.getBeeTable().getData();
            for (var cache : raw) {
                if (!result.containsKey(cache.getID())) {
                    result.put(cache.getID(), new BeeInfo());
                }
                var info = result.get(cache.getID());
                if (cache.needLookup()) {
                    hive.getBeeTable().link(cache);
                }
                if (cache.isBind()) {
                    info.active = true;
                }
                info.count ++;
            }
            Map<String, Object> kv = new HashMap<>();
            for (var e : result.entrySet()) {
                kv.put(ProductiveBee.getBeeName(ResourceLocation.parse(e.getKey())), wrapInfo(e.getValue()));
            }
            return kv;
        }
    }

    private Map<String, Object> wrapInfo(BeeInfo info) {
        Map<String, Object> kv = new HashMap<>();
        kv.put("status", info.active ? "Working" : "Idle");
        kv.put("count", info.count);
        return kv;
    }

    @Override
    public String id() {
        return ModularBees.MODID + ":" + "hive";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("hive");
    }

    private static class BeeInfo {

        int count = 0;
        boolean active = false;

    }

}
