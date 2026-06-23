package com.glodblock.github.modularbees.client.util;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

public class ElementGroup implements Iterable<Renderable> {

    private final Object2ReferenceMap<String, Renderable> elements = new Object2ReferenceOpenHashMap<>();

    @SuppressWarnings("unchecked")
    public <W extends GuiEventListener & Renderable & NarratableEntry> void populate(Consumer<Renderable> renderAdder, Consumer<W> widgetAdder) {
        for (var e : this.elements.values()) {
            if (e instanceof GuiEventListener && e instanceof NarratableEntry) {
                widgetAdder.accept((W) e);
            } else {
                renderAdder.accept(e);
            }
        }
    }

    public void reposition(int offsetX, int offsetY) {
        for (var e : this.elements.values()) {
            if (e instanceof RelativePosition r) {
                r.setOffset(offsetX, offsetY);
            }
        }
    }

    public ElementGroup add(String id, Renderable element) {
        if (this.elements.containsKey(id)) {
            throw new IllegalArgumentException("ID: %s has been used".formatted(id));
        }
        this.elements.put(id, element);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <W> W get(String id) {
        if (!this.elements.containsKey(id)) {
            throw new IllegalArgumentException("ID: %s doesn't existed".formatted(id));
        }
        return (W) this.elements.get(id);
    }

    @NotNull
    @Override
    public Iterator<Renderable> iterator() {
        return this.elements.values().iterator();
    }

}
