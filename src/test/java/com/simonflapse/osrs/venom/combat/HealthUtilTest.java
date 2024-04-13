package com.simonflapse.osrs.venom.combat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthUtilTest {

    @Nested
    class getApproximateHealth {
        @ParameterizedTest
        @MethodSource({
                "com.simonflapse.osrs.venom.combat.HealthArgument#badHealthValues",
                "com.simonflapse.osrs.venom.combat.HealthArgument#healthDecreasingByOne",
                "com.simonflapse.osrs.venom.combat.HealthArgument#healthValues"
        })
        void should_get_expected_health(HealthArgument argument) {
            int approximateHealth = HealthUtil.getApproximateHealth(
                    argument.getMaxHealth(),
                    argument.getHealthRatio(),
                    argument.getHealthScale()
            );
            assertEquals(argument.getExpectedHealth(), approximateHealth);
        }
    }
}