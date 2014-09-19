package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Observer.Event.FormEvent;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class JordanStep extends AbstractStep {
    public JordanStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected String generateMatrix() throws Exception {
        JordanMatrixForm rForm = (JordanMatrixForm) getForm();
        switch (getNumber()) {
            case START:
                return "A = " + generateLatexMatrix(rForm.getStartMatrix());
            case INFO:
                return "A_I = " + generateLatexMatrix(rForm.getTransitionalMatrix());
            case END:
                return "A = " + generateLatexMatrix(rForm.getStartMatrix())
                        + "J = " + generateLatexMatrix(rForm.getFinalMatrix());
            default:
                //step
                return "A_I " + generateLatexMatrix(rForm.getTransitionalMatrix());
        }
    }

    @Override
    protected String generateMupadMatrices() throws Exception {
        JordanMatrixForm rForm = (JordanMatrixForm) getForm();
        switch (getNumber()) {
            case START:
                return generateMupadMatrix("A", rForm.getStartMatrix());
            case INFO:
                return generateMupadMatrix("A_I", rForm.getTransitionalMatrix());
            case END:
                return generateMupadMatrix("A", rForm.getStartMatrix())
                        + "\n" + generateMupadMatrix("J", rForm.getFinalMatrix());
            default:
                //step
                return generateMupadMatrix("A_I", rForm.getTransitionalMatrix());
        }
    }


    @Override
    public String getLatexTitle() {
        String title = "\\begin{array}{l}";
        switch (getNumber()) {
            case START:
                title += "\\text{\\LARGE Start }\\cr \\text{\\Large Starting transformation to Jordan canonical form for matrix:}";
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
