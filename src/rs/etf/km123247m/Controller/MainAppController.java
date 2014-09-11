package rs.etf.km123247m.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.MainApp;
import rs.etf.km123247m.Matrix.Forms.Implementation.PolynomialRationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.Implementation.MathITMatrixHandler;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Model.AbstractStep;
import rs.etf.km123247m.Model.RationalCanonicalStep;
import rs.etf.km123247m.Model.SmithStep;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Observer.FormObserver;
import rs.etf.km123247m.Parser.MatrixParser.MathIT.MathITMatrixFileParser;
import rs.etf.km123247m.Parser.ParserTypes.IParser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;

public class MainAppController implements FormObserver {

    private MainApp mainApp;
    private Desktop desktop = Desktop.getDesktop();
    private File selectedFile = null;
    private ObservableList<String> formOptions = FXCollections.observableArrayList(
            "Smith",
            "Rational",
            "Jordan's"
    );

    private MatrixForm matrixForm;

    private LinkedList<AbstractStep> stepObjects = new LinkedList<AbstractStep>();

    @FXML
    private ComboBox selectForm;
    @FXML
    private Label fileNameLabel;
    @FXML
    private ListView<String> stepList;
    @FXML
    private WebView matrixState;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        selectForm.setItems(formOptions);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Choose a file.
     */
    @FXML
    private void chooseFileAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            selectedFile = file;
            fileNameLabel.setText(file.getName());
        }
    }

    /**
     * Edit selected file.
     */
    @FXML
    private void editFile() {
        if(selectedFile != null) {
            try {
                desktop.open(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void stepSelected() {
        String selected = stepList.getSelectionModel().getSelectedItem();
        AbstractStep selectedStep;
        if(selected == null) {
            matrixState.getEngine().loadContent("No steps selected.");
        } else {
            if(selected.equals("All")) {
                String html = "";
                for(AbstractStep step: stepObjects) {
                    html += step.getHtml() + "<br />";
                    matrixState.getEngine().loadContent("<html>" + html + "</html>");
                }
            } else {
                if(selected.equals("Start")) {
                    selectedStep = stepObjects.getFirst();
                } else if(selected.equals("Info")) {
                    // TODO: fix displaying of html
                    selectedStep = stepObjects.getLast();
                } else if(selected.equals("Finish")) {
                    selectedStep = stepObjects.getLast();
                } else {
                    int step = Integer.parseInt(selected.substring("Step ".length()));
                    selectedStep = stepObjects.get(step);
                }
                matrixState.getEngine().loadContent("<html>" + selectedStep.getHtml() + "</html>");
            }
        }
    }

    /**
     * Start transformation.
     */
    @FXML
    private void startTransformation() {
        if(selectedFile != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            stepList.setItems(items);
            IParser parser = new MathITMatrixFileParser(selectedFile);
            try {
                String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
                if(selectedItem != null) {
                    IMatrix matrix = (IMatrix) parser.parseInput();
                    MatrixHandler handler = new MathITMatrixHandler(matrix);
                    matrixForm = null;
                    if(selectedItem.equals("Smith")) {
                        matrixForm = new SmithMatrixForm(handler);
                    } else if(selectedItem.equals("Rational")) {
                        matrixForm = new PolynomialRationalCanonicalMatrixForm(handler);
                    } else if(selectedItem.equals("Jordan's")) {
                        matrixForm = null;
                    }
                    if(matrixForm != null) {
                        matrixForm.addObserver(this);
                        matrixForm.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        FormEvent event = (FormEvent) arg;
        MatrixForm form = (MatrixForm) o;
        AbstractStep step;
        switch (event.getType()) {
            case FormEvent.PROCESSING_START:
                step = getStep(AbstractStep.START, null, event);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                matrixState.getEngine().loadContent(step.getHtml());
                break;
            case FormEvent.PROCESSING_STEP:
                ICommand command = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = getStep(stepList.getItems().size(), command, event);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                matrixState.getEngine().loadContent(step.getHtml());
                break;
            case FormEvent.PROCESSING_INFO:
                step = getStep(AbstractStep.INFO, form.getCommands().getLast(), event);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                matrixState.getEngine().loadContent(step.getHtml());
                break;
            case FormEvent.PROCESSING_END:
                step = getStep(AbstractStep.END, null, event);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                matrixState.getEngine().loadContent(step.getHtml());

                //set all list option
                stepList.getItems().add("All");
                break;
            case FormEvent.PROCESSING_EXCEPTION:
                matrixState.getEngine().loadContent(event.getMessage());
                break;
        }
    }

    private AbstractStep getStep(int type, ICommand command, FormEvent event) {
        String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
        if(selectedItem.equals("Smith")) {
            return new SmithStep(type, command, event.getMatrix());
        } else if(selectedItem.equals("Rational")) {
            return new RationalCanonicalStep(type, command, event.getMatrix());
        } else if(selectedItem.equals("Jordan's")) {
            return null;
        }

        return null;
    }
}
