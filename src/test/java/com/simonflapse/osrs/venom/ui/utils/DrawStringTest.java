package com.simonflapse.osrs.venom.ui.utils;

import net.runelite.api.Point;
import net.runelite.client.util.ColorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class DrawStringTest {
    Graphics2D graphics2D;

    @BeforeEach
    void setUp() {
        this.graphics2D = GraphicsUtil.getSpyGraphics2D();
    }
    @Nested
    class renderTextLocation {
        @Test
        void should_not_render_text_if_empty_string_list() {
            Point point = new Point(0, 0);
            ArrayList<StringGraphics> strings = new ArrayList<>();
            DrawString.renderTextLocation(graphics2D, point, strings);

            verifyNoInteractions(graphics2D);
        }

        @Test
        void should_not_render_text_if_list_has_empty_string() {
            Point point = new Point(0, 0);
            ArrayList<StringGraphics> strings = new ArrayList<>();
            strings.add(new StringGraphics("", Color.BLACK));
            DrawString.renderTextLocation(graphics2D, point, strings);

            verifyNoInteractions(graphics2D);
        }

        @Test
        void should_render_shadow() {
            Point point = new Point(0, 0);
            ArrayList<StringGraphics> strings = new ArrayList<>();
            strings.add(new StringGraphics("Test", Color.WHITE));
            DrawString.renderTextLocation(graphics2D, point, strings);

            verifyShadow(point);
        }

        private void verifyShadow(Point point) {
            verifyStringRenderedWithColor(Color.BLACK, "", point.getX() + 1, point.getY() + 1);
            verifyStringRenderedWithColor(ColorUtil.colorWithAlpha(Color.BLACK, 50), "", point.getX() + 2, point.getY() + 2);
        }

        @Test
        void should_render_text() {
            Point point = new Point(0, 0);
            ArrayList<StringGraphics> strings = new ArrayList<>();
            String expectedString = "Test";
            Color expectedColor = Color.WHITE;

            strings.add(new StringGraphics(expectedString, expectedColor));
            DrawString.renderTextLocation(graphics2D, point, strings);

            verifyStringRenderedWithColor(expectedColor, expectedString, point.getX(), point.getY());
        }

        @Test
        void should_render_string_consistent_of_multiple_string_graphics() {
            Point point = new Point(0, 0);
            ArrayList<StringGraphics> strings = new ArrayList<>();
            String expectedFirstString = "Test";
            String expectedSecondString = "Hello World";
            Color expectedFirstColor = Color.WHITE;
            Color expectedSecondColor = Color.YELLOW;

            strings.add(new StringGraphics(expectedFirstString, expectedFirstColor));
            strings.add(new StringGraphics(expectedSecondString, expectedSecondColor));
            DrawString.renderTextLocation(graphics2D, point, strings);

            verifyStringRenderedWithColor(expectedFirstColor, expectedFirstString, point.getX(), point.getY());
            verifyStringRenderedWithColor(expectedFirstColor, expectedFirstString, point.getX(), point.getY());
        }
    }

    private void verifyStringRenderedWithColor(Color expectedColor, String string, int expectedX, int expectedY) {
        verifyStringRenderedWithColor(graphics2D, expectedColor, string, expectedX, expectedY);
    }

    public static void verifyStringRenderedWithColor(Graphics2D graphics2D, Color expectedColor, String string, int expectedX, int expectedY) {
        verify(graphics2D).setColor(expectedColor);
        verify(graphics2D).drawString(contains(string), eq(expectedX), eq(expectedY));
    }
}