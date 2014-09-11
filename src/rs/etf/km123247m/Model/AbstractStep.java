package rs.etf.km123247m.Model;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.IMatrix;

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
    private ICommand command;
    public String html;

    public AbstractStep(int number, ICommand command, IMatrix matrix) {
        this.number = number;
        this.command = command;
        try {
            html = generateHtml(matrix);
        } catch (Exception e) {
            html = e.getMessage();
        }
    }

    public int getNumber() {
        return number;
    }

    public ICommand getCommand() {
        return command;
    }

    public String getHtml() {
        return html;
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

    protected String generateHtml(IMatrix matrix) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(getTitleForHtml()).append("<div><table border=0 cellpadding=0 cellspacing=0px "
                + "style=\"border-left:1px solid #000; border-right:1px solid #000; color:#000\"><tr>"
                + "<td style =\"border-top:1px solid #000; border-bottom:1px solid #000;\">"
                + "&nbsp</td><td><table border=0 cellpadding=2 cellspacing=2 style=\"color:#000;\">");
        for (int i = 0; i < matrix.getRowNumber(); i++) {
            sb.append("<tr>");
            for (int j = 0; j < matrix.getRowNumber(); j++) {
                sb.append("<td align=\"center\" valign=\"center\">")
                        .append(matrix.get(i, j).getElement().toString()).append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</tr></table></td>"
                + "<td style =\"border-top:1px solid #000; border-bottom:1px solid #000;\">&nbsp</td>"
                + "</tr></table></div>");

        return sb.toString();
    }

    protected abstract String getTitleForHtml();
}
