package com.simonflapse.osrs.venom.ui.utils;

import org.mockito.Mockito;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsUtil {
    public static Graphics2D getSpyGraphics2D() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return Mockito.spy(image.createGraphics());
    }
}
