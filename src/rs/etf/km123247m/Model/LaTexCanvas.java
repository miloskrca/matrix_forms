package rs.etf.km123247m.Model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class LaTexCanvas extends Canvas {
    private FXGraphics2D g2;

    private TeXIcon icon;

    public LaTexCanvas() {
        this.g2 = new FXGraphics2D(getGraphicsContext2D());

        // Redraw canvas when size changes.
        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                draw();
            }
        });
        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                draw();
            }
        });
    }

    public void setFormula(String formula) {
        // create a formula
        TeXFormula teXFormula = new TeXFormula(formula);
        // render the teXFormula to an icon of the same size as the formula.
        this.icon = teXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
    }

    public void render() {
        draw();
    }

    private void draw() {
        if(this.icon == null) {
            return;
        }
        double width = getWidth();
        double height = getHeight();
        getGraphicsContext2D().clearRect(0, 0, width, height);

        // ideally it should be possible to draw directly to the FXGraphics2D
        // instance without creating an image first...but this does not generate
        // good output
        //this.icon.paintIcon(new JLabel(), g2, 50, 50);

        // now create an actual image of the rendered equation
        BufferedImage image = new BufferedImage(icon.getIconWidth(),
                icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = image.createGraphics();
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(new Color(0, 0, 0));
        icon.paintIcon(jl, gg, 0, 0);
        // at this point the image is created, you could also save it with ImageIO

        this.g2.drawImage(image, 0, 0, null);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void clear() {
        if(this.icon == null) {
            return;
        }
        this.g2.setBackground(Color.white);
        this.g2.clearRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
    }
}
