package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Observer.Event.FormEvent;

import java.util.Collection;

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
    protected void saveMatricesForTheCurrentState() throws Exception {
        SmithMatrixForm sForm = (SmithMatrixForm) getForm();
        switch (getNumber()) {
            case START:
                matrices.add(new MatrixEntry("A", sForm.getHandler().duplicate(sForm.getStartMatrix())));
                break;
            case INFO:
                matrices.add(new MatrixEntry("A_I", sForm.getHandler().duplicate(sForm.getFinalMatrix())));
                break;
            case END:
                matrices.add(new MatrixEntry("A", sForm.getHandler().duplicate(sForm.getStartMatrix())));
                matrices.add(new MatrixEntry("S", sForm.getHandler().duplicate(sForm.getFinalMatrix())));
                break;
            default:
                //step
                matrices.add(new MatrixEntry("A_I", sForm.getHandler().duplicate(sForm.getFinalMatrix())));
        }
    }

    @Override
    public String getLatexTitle() {
        String title = "\\begin{array}{l}";
        switch (getNumber()) {
            case START:
                title += "\\text{\\LARGE Start }\\cr \\text{\\Large Starting transformation to Smith normal form for matrix:}";
                break;
            case INFO:
                title += "\\text{\\LARGE Info }\\cr \\text{\\Large " + getEvent().getMessage() + "}";
                break;
            case END:
                title += "\\text{\\LARGE Finish }\\cr \\text{\\Large Transformation ended. Result:}";
                break;
            default:
                //step
                title += "\\text{\\LARGE Step " + getNumber() + "}\\cr \\text{\\Large " + getCommandDescription() + " }";
        }

        return title + "\\end{array}";
    }
}
