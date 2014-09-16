package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;

import java.util.Arrays;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Controller
 */
public abstract class AbstractStep {

    public final static int START = -1;
    public final static int INFO = -2;
    public final static int END = -3;
    private int number;

    private MatrixForm form;
    private FormEvent event;
    private ICommand command;
    private String matrixState;

    public AbstractStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        this.number = number;
        this.command = command;
        this.event = event;
        this.form = form;
        try {
            matrixState = generateMatrix();
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public String getMatrixState() {
        return matrixState;
    }

    public String getTitle() {
        switch (number) {
            case START:
                return "Start";
            case INFO:
                return "Info";
            case END:
                return "Finish";
            default:
                return "Step " + number;
        }
    }

    protected abstract String generateMatrix() throws Exception;

    public abstract String getHtmlTitle();

    protected String generateLatexMatrix(IMatrix matrix) throws Exception {

        String f = "\\begin{bmatrix}";
        for (int row = 0; row < matrix.getRowNumber(); row++) {
            for (int column = 0; column < matrix.getColumnNumber(); column++) {
                f += matrix.get(row, column).getElement().toString();
                if(column < matrix.getColumnNumber() - 1) {
                    f += " & ";
                }
            }
            f += " \\\\";
        }
        f += "\\end{bmatrix}";

        return f;
    }

    public int getNumber() {
        return number;
    }

    public ICommand getCommand() {
        return command;
    }

    public MatrixForm getForm() {
        return form;
    }

    public FormEvent getEvent() {
        return event;
    }
}
