package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Observer.Event.FormEvent;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class SmithStep extends AbstractStep {
    public SmithStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected String generateMatrix() throws Exception {
        SmithMatrixForm rForm = (SmithMatrixForm) getForm();
        return "A = " + generateLatexMatrix(rForm.getFinalMatrix());
    }

    public String getHtmlTitle() {
        String title;
        switch (getNumber()) {
            case START:
                title = "<h2>Start</h2><h3>Starting transformation to Smith normal form for matrix:<h3>";
                break;
            case INFO:
                title = "<h2>Info</h2><h3>" + getEvent().getMessage() + "</h3>";
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

    @Override
    public String getLatexTitle() {
        String title;
        switch (getNumber()) {
            case START:
                title = "{\\LARGE Start }\\\\{\\Large Starting transformation to Smith normal form for matrix: }";
                break;
            case INFO:
                title = "{\\LARGE Info }\\\\{\\Large " + getEvent().getMessage() + " }";
                break;
            case END:
                title = "{\\LARGE Finish }\\\\{\\Large Transformation ended. Result:}";
                break;
            default:
                //step
                title = "{\\LARGE Step " + getNumber() + "}\\\\{\\Large " + (getCommand() == null ? "" : getCommand().getDescription()) + "}";
        }

        return title;
    }
}
