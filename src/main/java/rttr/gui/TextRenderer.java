package rttr.gui;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class TextRenderer {

    private static class TextLine {
        private String text;
        private TextLayout layout;
        public TextLine( String t, TextLayout layout ) {
            text = t;
            this.layout = layout;
        }
    }

    public static void drawCenteredString(Graphics2D g2d, String text, Rectangle2D rect) {
        drawCenteredString(g2d, text, rect, false, 0);
    }
    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g2d The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    public static void drawCenteredString(Graphics2D g2d, String text, Rectangle2D rect,
                                          boolean leftJustify, int leftPadding) {
        String [] parts = text.split("\\n");

        Font font = g2d.getFont();
        FontRenderContext frc = g2d.getFontRenderContext();

        ArrayList<TextLine> lines = new ArrayList<>();
        double totalHeight = 0.0;
        for( int i = 0; i < parts.length; i++ ) {
            TextLayout tl = new TextLayout(parts[i], font, frc);
            lines.add( new TextLine(parts[i], tl));
            totalHeight += tl.getBounds().getHeight();
        }
        double spacing = (rect.getHeight() - totalHeight) / (parts.length + 1);

        double y = rect.getY() + spacing;
        for( int i = 0; i < lines.size(); i++ ) {
            TextLayout layout = lines.get(i).layout;
            Rectangle2D box = layout.getBounds();

            double x = 0.0;
            if( leftJustify ) {
                x = rect.getX() + leftPadding;
            } else {
                x = Math.round((rect.getWidth() - box.getWidth()) / 2.0 - box.getX() + rect.getX());
            }
            g2d.drawString(lines.get(i).text, (int)x, (int)(y + layout.getAscent()));
            y += spacing + box.getHeight();
        }

    }
}
