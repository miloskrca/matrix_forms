package rs.etf.km123247m.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
import rs.etf.km123247m.Model.LaTexCanvas;
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
    private LaTexCanvas canvasPrev;
    private LaTexCanvas canvas;
    private LinkedList<AbstractStep> stepObjects = new LinkedList<AbstractStep>();
    private int count = 0;

    @FXML
    private ComboBox selectForm;
    @FXML
    private Label fileNameLabel;
    @FXML
    private ListView<String> stepList;
//    @FXML
//    private WebView stepDetailsTitle;1
//    @FXML
//    private Pane prevStepDetailsMatrixState;
//    @FXML
//    private FlowPane stepDetailsMatrixState;
    @FXML
    private VBox matrixStateVBox;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        selectForm.setItems(formOptions);
//        canvasPrev = new LaTexCanvas();
//        canvas = new LaTexCanvas();
//        prevStepDetailsMatrixState.getChildren().add(canvasPrev);
//        stepDetailsMatrixState.getChildren().add(canvas);
//        // Bind canvas size to stack pane size.
//        canvas.widthProperty().bind(stepDetailsMatrixState.widthProperty());
//        canvas.heightProperty().bind(stepDetailsMatrixState.heightProperty());
//        // Bind canvas size to stack pane size.
//        canvasPrev.widthProperty().bind(prevStepDetailsMatrixState.widthProperty());
//        canvasPrev.heightProperty().bind(prevStepDetailsMatrixState.heightProperty());
    }

    protected LaTexCanvas addCanvas(String formula) {
        LaTexCanvas canvas = new LaTexCanvas();

        Pane pane = new AnchorPane();
        pane.setMinHeight(50);
        pane.getChildren().add(canvas);
        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        matrixStateVBox.getChildren().add(pane);

        canvas.setFormula(formula);

        return canvas;
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
        if (selectedFile != null) {
            try {
                desktop.open(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void stepSelected() {
        matrixStateVBox.getChildren().clear();
        Integer selected = stepList.getSelectionModel().getSelectedIndices().get(0);
        AbstractStep selectedStep;
        if (selected == -1) {
//            stepDetailsTitle.getEngine().loadContent("No steps selected.");
        } else {
            selectedStep = stepObjects.get(selected);
//            stepDetailsTitle.getEngine().loadContent("<html>" + selectedStep.getHtmlTitle() + "</html>");
            if(selected > 0) {
                addCanvas(stepObjects.get(selected - 1).getMatrixState()).render();
            }
            addCanvas(selectedStep.getMatrixState()).render();

//            canvas.setFormula(selectedStep.getMatrixState());
//            canvas.render();
        }
    }

    /**
     * Start transformation.
     */
    @FXML
    private void startTransformation() {
        if (selectedFile != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            stepList.setItems(items);
            count = 1;
            stepObjects.clear();
            IParser parser = new MathITMatrixFileParser(selectedFile);
            try {
                String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    IMatrix matrix = (IMatrix) parser.parseInput();
                    MatrixHandler handler = new MathITMatrixHandler(matrix);
                    matrixForm = null;
                    if (selectedItem.equals("Smith")) {
                        matrixForm = new SmithMatrixForm(handler);
                    } else if (selectedItem.equals("Rational")) {
                        matrixForm = new PolynomialRationalCanonicalMatrixForm(handler);
                    } else if (selectedItem.equals("Jordan's")) {
                        matrixForm = null;
                    }
                    if (matrixForm != null) {
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
                step = getStep(AbstractStep.START, null, event, form);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                break;
            case FormEvent.PROCESSING_STEP:
                ICommand stepCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = getStep(count++, stepCommand, event, form);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                break;
            case FormEvent.PROCESSING_INFO:
                ICommand infoCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = getStep(AbstractStep.INFO, infoCommand, event, form);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                break;
            case FormEvent.PROCESSING_END:
                step = getStep(AbstractStep.END, null, event, form);
                stepList.getItems().add(step.getTitle());
                stepObjects.add(step);
                break;
            case FormEvent.PROCESSING_EXCEPTION:
//                stepDetailsTitle.getEngine().loadContent(event.getMessage());
                break;
        }
    }

    private AbstractStep getStep(int type, ICommand command, FormEvent event, MatrixForm form) {
        String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
        if (selectedItem.equals("Smith")) {
            return new SmithStep(type, command, event, form);
        } else if (selectedItem.equals("Rational")) {
            return new RationalCanonicalStep(type, command, event, form);
        } else if (selectedItem.equals("Jordan's")) {
            return null;
        }

        return null;
    }
}