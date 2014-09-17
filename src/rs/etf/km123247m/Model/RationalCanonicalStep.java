package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.RationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Observer.Event.FormEvent;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class RationalCanonicalStep extends AbstractStep {

    public RationalCanonicalStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected String generateMatrix() throws Exception {
        RationalCanonicalMatrixForm rForm = (RationalCanonicalMatrixForm) getForm();
        switch (getNumber()) {
            case START:
                if(rForm.getRound() == 0) {
                    return "A = " + generateLatexMatrix(rForm.getStartMatrix())
                            + "\nxI - A = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()));
                } else {
                    return "B = " + generateLatexMatrix(rForm.getStartMatrix())
                            + "\nxI - B = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()));
                }
            case INFO:
                return "R = " + generateLatexMatrix(rForm.getFinalMatrix());
            case END:
                return "A = " + generateLatexMatrix(rForm.getStartMatrix())
                        + "\nR = " + generateLatexMatrix(rForm.getFinalMatrix())
                        + "\nT = " + generateLatexMatrix(rForm.getT());
            default:
                //step
                return "P[" + rForm.getRound() + "] = " + generateLatexMatrix(rForm.getP(rForm.getRound()))
                        + "\nA_I = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()))
                        + "\nQ[" + rForm.getRound() + "] = " + generateLatexMatrix(rForm.getQ(rForm.getRound()));
        }
    }

    public String getHtmlTitle() {
        String title;
        switch (getNumber()) {
            case START:
                title = "<h2>Start</h2><h3>Starting transformation to Rational canonical form for matrix:<h3>";
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

    @Override
    public String getLatexTitle() {
        String title;
        switch (getNumber()) {
            case START:
                title = "{\\LARGE Start }\\\\{\\Large Starting transformation to Rational canonical form for matrix: }";
                break;
            case INFO:
                title = "{\\LARGE Info }";
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
