package com.candy.sort;

import static com.candy.support.SweetFixtures.caramel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.model.Sweet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class SweetOrderTest {

    private static List<String> idsSortedBy(SweetOrder order,
            Sweet... sweets) {
        List<Sweet> list = new ArrayList<>(List.of(sweets));
        list.sort(order);
        return list.stream().map(Sweet::id).toList();
    }

    @Test
    void hasThreeOrders() {
        assertEquals(3, SweetOrder.values().length);
    }

    @Test
    void everyOrderIsAComparator() {
        for (SweetOrder order : SweetOrder.values()) {
            assertInstanceOf(Comparator.class, order);
        }
    }

    @Test
    void sortsByNameIgnoringCase() {
        Sweet lower = caramel("C001", "alpha", 300, "Factory");
        Sweet upper = caramel("C002", "Beta", 100, "Factory");

        assertEquals(List.of("C001", "C002"),
                idsSortedBy(SweetOrder.BY_NAME, upper, lower));
    }

    @Test
    void sortsByEnergyAscending() {
        Sweet light = caramel("C001", "Light", 100, "Factory");
        Sweet heavy = caramel("C002", "Heavy", 500, "Factory");
        Sweet medium = caramel("C003", "Medium", 300, "Factory");

        assertEquals(List.of("C001", "C003", "C002"),
                idsSortedBy(SweetOrder.BY_ENERGY, heavy, light, medium));
    }

    @Test
    void sortsEqualEnergyByName() {
        Sweet bravo = caramel("C001", "Bravo", 300, "Factory");
        Sweet alpha = caramel("C002", "Alpha", 300, "Factory");

        assertEquals(List.of("C002", "C001"),
                idsSortedBy(SweetOrder.BY_ENERGY, bravo, alpha));
    }

    @Test
    void sortsByProductionIgnoringCase() {
        Sweet first = caramel("C001", "Candy", 100, "alpha Co");
        Sweet second = caramel("C002", "Candy", 100, "Beta Co");

        assertEquals(List.of("C001", "C002"),
                idsSortedBy(SweetOrder.BY_PRODUCTION, second, first));
    }

    @Test
    void sortsEqualProductionByName() {
        Sweet bravo = caramel("C001", "Bravo", 100, "Same Co");
        Sweet alpha = caramel("C002", "Alpha", 100, "Same Co");

        assertEquals(List.of("C002", "C001"),
                idsSortedBy(SweetOrder.BY_PRODUCTION, bravo, alpha));
    }

    @Test
    void comparesSameSweetAsEqual() {
        Sweet sweet = caramel("C001", "Candy", 100, "Factory");

        for (SweetOrder order : SweetOrder.values()) {
            assertEquals(0, order.compare(sweet, sweet));
        }
    }

    @Test
    void compareIsAntisymmetric() {
        Sweet small = caramel("C001", "Alpha", 100, "Alpha Co");
        Sweet large = caramel("C002", "Beta", 200, "Beta Co");

        for (SweetOrder order : SweetOrder.values()) {
            assertTrue(order.compare(small, large) < 0);
            assertTrue(order.compare(large, small) > 0);
        }
    }
}