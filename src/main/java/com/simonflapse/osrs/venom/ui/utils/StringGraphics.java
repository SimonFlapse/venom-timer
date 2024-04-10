package com.simonflapse.osrs.venom.ui.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public class StringGraphics {
    private String string;
    private Color color;

    public int width(Graphics graphics) {
        return graphics.getFontMetrics().stringWidth(string);
    }
}
