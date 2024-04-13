package com.simonflapse.osrs.venom.combat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
class HealthArgument {
    private final String name;
    private final int maxHealth;
    private final int healthRatio;
    private final int healthScale;
    private final int expectedHealth;

    @Override
    public String toString() {
        if (name == null) {
            return String.format("Max Health: %s HealthScale %s/%s ", maxHealth, healthRatio, healthScale);
        }
        return String.format("%s ---- Max Health: %s ---- HealthScale %s/%s ----", name, maxHealth, healthRatio, healthScale);
    }

    static Stream<HealthArgument> badHealthValues() {
        return Stream.of(
                new HealthArgument("Dead without health scale",10, 0, 0, 0),
                new HealthArgument("Dead with health scale",10, 0, 30, 0),
                new HealthArgument("Dead with bugged healthRation",10, 29, 0, 0),
                new HealthArgument("Invalid health ratio",10, -1, 30, -1),
                new HealthArgument("Invalid health scale",10, 29, -1, -1),
                new HealthArgument("Invalid health scale and ratio",10, -1, -1, -1),
                new HealthArgument("Invalid maxHealth",-1, 29, 30, -1)
        );
    }

    static Stream<HealthArgument> healthDecreasingByOne() {
        int healthScale = 30;
        ArrayList<HealthArgument> arguments = new ArrayList<>();
        for (int healthRatio = 30; healthRatio >= 0; healthRatio--) {
            arguments.add(new HealthArgument(
                    String.format("Health left %s", healthRatio),
                    healthScale,
                    healthRatio,
                    healthScale,
                    healthRatio)
            );
        }

        return Stream.of(arguments.toArray(new HealthArgument[0]));
    }

    static Stream<HealthArgument> healthValues() {
        return Stream.of(
                new HealthArgument("Simple taken 1 damage",10, 29, 30, 9),
                new HealthArgument("Truncated taken 1 damage",99, 29, 30, 98),
                new HealthArgument("Simple Boss health ratio",1250, 10, 100, 126),
                new HealthArgument("Simple Boss full health",1250, 10, 100, 126),
                new HealthArgument("Truncated Boss health ratio",3070, 63, 75, 2613),
                new HealthArgument("Truncated Boss full health",3070, 75, 75, 3070),
                new HealthArgument("1 HP only",1, 1, 1, 1)
        );
    }
}
