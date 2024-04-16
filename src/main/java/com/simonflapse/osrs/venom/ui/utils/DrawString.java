package com.simonflapse.osrs.venom.ui.utils;

import com.google.common.base.Strings;
import net.runelite.api.Point;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.util.ArrayList;

public final class DrawString {
    private DrawString() {}

    public static void renderTextLocation(Graphics2D graphics, Point txtLoc, ArrayList<StringGraphics> texts) {
        int x = txtLoc.getX();
        int y = txtLoc.getY();

        for (StringGraphics text : texts){
            if (Strings.isNullOrEmpty(text.getString())) {
                return;
            }

            renderTextShadow(graphics, x, y, text.getString());

            graphics.setColor(ColorUtil.colorWithAlpha(text.getColor(), 0xFF));
            graphics.drawString(text.getString(), x, y);
            x += text.width(graphics);
        }
    }

    private static void renderTextShadow(Graphics2D graphics, int x, int y, String text) {
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);

        graphics.setColor(ColorUtil.colorWithAlpha(Color.BLACK,  50));
        graphics.drawString(text, x + 2, y + 2);
    }
}
