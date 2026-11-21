package com.candy.support;

import com.candy.model.CandyType;
import com.candy.model.ChocolateKind;
import com.candy.model.Ingredients;
import com.candy.model.NutritionValue;
import com.candy.model.Sweet;
import java.util.Optional;

public final class SweetFixtures {

    private SweetFixtures() {
    }

    public static Sweet caramel() {
        return new Sweet("C001", "Sunny Caramel", 390, CandyType.CARAMEL,
                new Ingredients(8, 62000, 9000, Optional.empty(), 12),
                new NutritionValue(0.0, 0.1, 97.5), "Kyiv Confectionery");
    }

    public static Sweet chocolate() {
        return new Sweet("C003", "Dark Truffle", 550,
                CandyType.CHOCOLATE_FILLED,
                new Ingredients(2, 38000, 1500,
                        Optional.of(ChocolateKind.DARK), 35),
                new NutritionValue(5.5, 35.0, 52.0), "Lviv Sweets");
    }

    public static Sweet caramel(String id, String name, int energy,
            String production) {
        return new Sweet(id, name, energy, CandyType.CARAMEL,
                new Ingredients(8, 62000, 9000, Optional.empty(), 12),
                new NutritionValue(0.0, 0.1, 97.5), production);
    }
}