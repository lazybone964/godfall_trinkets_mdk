package com.lazybones.godfalltrinkets.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

// 自定义槽位数量变更事件（继承Forge的Event基类）
public class SlotCountChangeEvent extends Event {
    private final Entity entity;
    // 存储原始槽位数量（key=槽位ID，value=原始数量）
    private final Map<String, Integer> originalCounts;
    // 存储要修改后的槽位数量
    private final Map<String, Integer> modifiedCounts;

    // 构造方法：初始化实体和原始槽位数量
    public SlotCountChangeEvent(Entity entity, @Nonnull Map<String, Integer> originalCounts) {
        this.entity = entity;
        this.originalCounts = new HashMap<>(originalCounts);
        this.modifiedCounts = new HashMap<>(originalCounts); // 初始和原始一致
    }

    // 获取事件关联的实体（对应原代码的getEntity()）
    public Entity getEntity() {
        return entity;
    }

    // 获取指定槽位的原始数量（对应原代码的getOriginalCount(String)）
    public int getOriginalCount(String slotId) {
        return originalCounts.getOrDefault(slotId, 0);
    }

    // 设置指定槽位的最终数量（对应原代码的setCount(String, int)）
    public void setCount(String slotId, int count) {
        modifiedCounts.put(slotId, Math.max(0, count)); // 确保数量非负
    }

    // （可选）获取修改后的槽位数量（事件触发方需要这个方法读取最终值）
    public int getModifiedCount(String slotId) {
        return modifiedCounts.getOrDefault(slotId, 0);
    }
}