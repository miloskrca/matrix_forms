package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.IMatrix;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class SmithStep extends AbstractStep {
    public SmithStep(int number, ICommand command, IMatrix matrix) {
        super(number, command, matrix);
    }

    protected String getTitleForHtml() {
        String title;
        switch (getNumber()) {
            case START:
                title = "<h2>Start</h2><h3>Starting transformation to Smith normal form for matrix:<h3>";
                break;
            case INFO:
                title = "<h2>Info</h2>";
                break;
            case END:
                title = "<h2>Finish</h2><h3>Transformation ended. Result:</h3>";
                break;
            default:
                //step
                title = "<h2>Step " + getNumber() + "</h2><h4>" + (getCommand() == null ? "" : getCommand().getDescription()) + "</h4>";
        }

        return title;
    }
}
