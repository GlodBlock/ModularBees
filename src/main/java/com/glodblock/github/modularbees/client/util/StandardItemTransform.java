package com.glodblock.github.modularbees.client.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransforms;

public class StandardItemTransform {

    private static final Gson GSON = new Gson();
    private static ItemTransforms INSTANCE;

    public static ItemTransforms get() {
        if (INSTANCE == null) {
            throw new NullPointerException("Item Transforms aren't ready yet.");
        }
        return INSTANCE;
    }

    public static void init(JsonDeserializationContext ctx) {
        if (INSTANCE == null) {
            INSTANCE = create(ctx);
        }
    }

    private static ItemTransforms create(JsonDeserializationContext ctx) {
        var json = GSON.fromJson(
                """
                     {
                                "gui": {
                                    "rotation": [ 30, 225, 0 ],
                                    "translation": [ 0, 0, 0],
                                    "scale":[ 0.625, 0.625, 0.625 ]
                                },
                                "ground": {
                                    "rotation": [ 0, 0, 0 ],
                                    "translation": [ 0, 3, 0],
                                    "scale":[ 0.25, 0.25, 0.25 ]
                                },
                                "fixed": {
                                    "rotation": [ 0, 0, 0 ],
                                    "translation": [ 0, 0, 0],
                                    "scale":[ 0.5, 0.5, 0.5 ]
                                },
                                "thirdperson_righthand": {
                                    "rotation": [ 75, 45, 0 ],
                                    "translation": [ 0, 2.5, 0],
                                    "scale": [ 0.375, 0.375, 0.375 ]
                                },
                                "firstperson_righthand": {
                                    "rotation": [ 0, 45, 0 ],
                                    "translation": [ 0, 0, 0 ],
                                    "scale": [ 0.40, 0.40, 0.40 ]
                                },
                                "firstperson_lefthand": {
                                    "rotation": [ 0, 225, 0 ],
                                    "translation": [ 0, 0, 0 ],
                                    "scale": [ 0.40, 0.40, 0.40 ]
                                }
                     }
                """,
                JsonObject.class
        );
        return ctx.deserialize(json, ItemTransforms.class);
    }

}
