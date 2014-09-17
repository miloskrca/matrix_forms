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
        String text;
        switch (getNumber()) {
            case START:
                if(rForm.getRound() == 0) {
                    text = "A = " + generateLatexMatrix(rForm.getStartMatrix())
                            + "\nxI - A = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()));
                } else {
                    text = "B = " + generateLatexMatrix(rForm.getStartMatrix())
                            + "\nxI - B = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()));
                }
                break;
            case INFO:
                text = "R = " + generateLatexMatrix(rForm.getFinalMatrix());
                break;
            case END:
                text = "A = " + generateLatexMatrix(rForm.getStartMatrix())
                        + "\nR = " + generateLatexMatrix(rForm.getFinalMatrix())
                        + "\nT = " + generateLatexMatrix(rForm.getT());
                break;
            default:
                //step
                text = "P[" + rForm.getRound() + "] = " + generateLatexMatrix(rForm.getP(rForm.getRound()))
                        + "\nA_I = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()));
        }

        return text;
    }

    @Override
    public String getLatexTitle() {
        String title = "\\begin{array}{l}";
        switch (getNumber()) {
            case START:
                title += "\\text{\\LARGE Start }\\cr \\text{\\Large Starting transformation to Rational canonical form for matrix:}";
                break;
            case INFO:
                title += "\\text{\\LARGE Info }\\cr \\text{\\Large " + getEvent().getMessage() + "}";
                break;
            case END:
                title += "\\text{\\LARGE Finish }\\cr \\text{\\Large Transformation ended. Result:}";
                break;
            default:
                //step
                title += "\\text{\\LARGE Step " + getNumber() + "}\\cr \\text{\\Large " + (getCommand() == null ? "" : getCommand().getDescription()) + " }";
        }

        return title + "\\end{array}";
    }
}
