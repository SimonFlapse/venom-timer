package com.simonflapse.osrs.venom.ui;

import com.google.common.base.Strings;
import com.simonflapse.osrs.venom.ui.utils.StringGraphics;
import net.runelite.api.Actor;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.util.ArrayList;

public class VenomTimerOverlay extends Overlay {

    private Actor actor;
    private int venomDamage;

    public void updateVenom(Actor actor, int venomDamage) {
        this.actor = actor;
        this.venomDamage = venomDamage;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (actor == null || venomDamage == 0) {
            return null;
        }

        ArrayList<StringGraphics> stringGraphics = new ArrayList<>();

        stringGraphics.add(new StringGraphics("Venom damage ", ColorUtil.fromHex("#49977e")));
        stringGraphics.add(new StringGraphics("" + this.venomDamage, ColorUtil.fromHex("#163028")));

        Point textLocation = actor.getCanvasTextLocation(graphics, getUnformattedString(stringGraphics), actor.getLogicalHeight() + 100);
        if (textLocation != null)
        {
            renderTextLocation(graphics, textLocation, stringGraphics);
        }

        return null;
    }

    private static String getUnformattedString(ArrayList<StringGraphics> texts) {
        StringBuilder unformattedString = new StringBuilder();
        for (StringGraphics text : texts) {
            unformattedString.append(text.getString());
        }
        return unformattedString.toString();
    }

    private static void renderTextLocation(Graphics2D graphics, Point txtLoc, ArrayList<StringGraphics> texts) {
        int x = txtLoc.getX();
        int y = txtLoc.getY();

        for (StringGraphics text : texts){
            if (Strings.isNullOrEmpty(text.getString())) {
                return;
            }

            graphics.setColor(Color.BLACK);
            graphics.drawString(text.getString(), x + 1, y + 1);

            graphics.setColor(ColorUtil.colorWithAlpha(text.getColor(), 0xFF));
            graphics.drawString(text.getString(), x, y);

            x += text.width(graphics);
        }
    }
}
