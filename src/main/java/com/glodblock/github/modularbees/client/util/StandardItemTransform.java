package com.glodblock.github.modularbees.client.util;

import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;

public class StandardItemTransform {

    public static final ItemTransforms INSTANCE = create();

    private static ItemTransforms create() {
        return CuboidModel.GSON.fromJson(
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
                ItemTransforms.class
        );
    }

}
