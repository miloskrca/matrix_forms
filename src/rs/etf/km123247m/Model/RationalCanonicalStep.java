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
                        + "\nA_I = " + generateLatexMatrix(rForm.getTransitionalMatrix(rForm.getRound()))
                        + "Q[" + rForm.getRound() + "] = " + generateLatexMatrix(rForm.getQ(rForm.getRound()));
        }

        return text;
    }

    @Override
    protected String generateMupadMatrices() throws Exception {
        RationalCanonicalMatrixForm rForm = (RationalCanonicalMatrixForm) getForm();
        String text;
        switch (getNumber()) {
            case START:
                if(rForm.getRound() == 0) {
                    text = generateMupadMatrix("A", rForm.getStartMatrix())
                            + "\n" + generateMupadMatrix("xIminusA", rForm.getTransitionalMatrix(rForm.getRound()));
                } else {
                    text = generateMupadMatrix("B", rForm.getStartMatrix())
                            + "\n" + generateMupadMatrix("xIminusB", rForm.getTransitionalMatrix(rForm.getRound()));
                }
                break;
            case INFO:
                text = generateMupadMatrix("R", rForm.getFinalMatrix());
                break;
            case END:
                text = generateMupadMatrix("A", rForm.getStartMatrix())
                        + "\n" + generateMupadMatrix("R", rForm.getFinalMatrix())
                        + "\n" + generateMupadMatrix("T", rForm.getT());
                break;
            default:
                //step
                text = generateMupadMatrix("P" + rForm.getRound(), rForm.getP(rForm.getRound()))
                        + "\n" + generateMupadMatrix("A_I", rForm.getTransitionalMatrix(rForm.getRound()))
                        + "\n" + generateMupadMatrix("Q" + rForm.getRound(), rForm.getQ(rForm.getRound()));
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
