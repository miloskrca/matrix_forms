package rs.etf.km123247m.Controller;

import edu.jas.arith.Rational;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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
import rs.etf.km123247m.Model.*;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Observer.FormObserver;
import rs.etf.km123247m.Parser.MatrixParser.MathIT.MathITMatrixFileParser;
import rs.etf.km123247m.Parser.MatrixParser.MathIT.MathITMatrixStringParser;
import rs.etf.km123247m.Parser.ParserTypes.IParser;
import rs.etf.km123247m.Parser.ParserTypes.StringParser;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Observable;

public class MainAppController implements FormObserver {

    public static final String SMITH_FORM = "Smith normal form";
    public static final String RATIONAL_FORM = "Rational canonical form";
    public static final String JORDANS_FORM = "Jordans canonical form";

    private MainApp mainApp;
    private ObservableList<String> formOptions = FXCollections.observableArrayList(
            SMITH_FORM,
            RATIONAL_FORM,
            JORDANS_FORM
    );

    private MatrixForm matrixForm;
    private LinkedList<AbstractStep> stepObjects = new LinkedList<AbstractStep>();
    private int count = 0;

    @FXML
    private ComboBox selectForm;
    @FXML
    private Label fileNameLabel;
    @FXML
    private ListView<String> stepList;
    @FXML
    private TextArea inlineInput;
    @FXML
    private VBox matrixStateVBox;
    @FXML
    private Label statusLabel;

    private int range;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        selectForm.setItems(formOptions);
    }

    protected LaTexCanvas addCanvas(String formula) {
        LaTexCanvas canvas = new LaTexCanvas();

        Pane pane = new AnchorPane();
        pane.setMinHeight(20 + range*20);
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

    @FXML
    private void setSmithMatrixExampleAction() {
        fileNameLabel.setText("");
        statusLabel.setText("Loaded example for Smith normal form.");
        inlineInput.setText(MatrixExamples.SMITH_EXAMPLE_MATRIX);
    }

    @FXML
    private void setRationalMatrixExampleAction() {
        statusLabel.setText("Loaded example for Rational canonical form.");
        fileNameLabel.setText("");
        inlineInput.setText(MatrixExamples.RATIONAL_EXAMPLE_MATRIX);
    }

    @FXML
    private void setJordansMatrixExampleAction() {
        statusLabel.setText("Loaded example for Jordans canonical form.");
        fileNameLabel.setText("");
        inlineInput.setText(MatrixExamples.JORDANS_EXAMPLE_MATRIX);
    }

    @FXML
    private void closeAction() {
        System.exit(0);
    }

    /**
     * Choose a file.
     */
    @FXML
    private void chooseFileAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            fileNameLabel.setText(file.getName());
            statusLabel.setText("Opened file " + file.getName());
            FileReader reader;
            BufferedReader br;
            try {
                reader = new FileReader(file);
                br = new BufferedReader(reader);
                inlineInput.setText("");
                String line;
                while((line = br.readLine()) != null) {
                    inlineInput.appendText(line + "\n");
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
            if(selected > 0 && selected < stepObjects.size() - 1) {
                addCanvas(stepObjects.get(selected - 1).getMatrixState()).render();
            }
            addCanvas(selectedStep.getLatexTitle()).render();
            addCanvas(selectedStep.getMatrixState()).render();
        }
    }

    /**
     * Start transformation.
     */
    @FXML
    private void startTransformation() {
        if (inlineInput.getText().length() > 0) {
            statusLabel.setText("Working...");
            ObservableList<String> items = FXCollections.observableArrayList();
            stepList.setItems(items);
            count = 1;
            stepObjects.clear();
            StringParser parser = new MathITMatrixStringParser();
            parser.setInputString(inlineInput.getText());
            try {
                String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    IMatrix matrix = (IMatrix) parser.parseInput();
                    range = matrix.getRowNumber();
                    MatrixHandler handler = new MathITMatrixHandler(matrix);
                    matrixForm = null;
                    if (selectedItem.equals(SMITH_FORM)) {
                        matrixForm = new SmithMatrixForm(handler);
                    } else if (selectedItem.equals(RATIONAL_FORM)) {
                        matrixForm = new PolynomialRationalCanonicalMatrixForm(handler);
                    } else if (selectedItem.equals(JORDANS_FORM)) {
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
                stepList.getSelectionModel().select("Finish");
                stepSelected();
                statusLabel.setText("Done.");
                break;
            case FormEvent.PROCESSING_EXCEPTION:
//                stepDetailsTitle.getEngine().loadContent(event.getMessage());
                break;
        }
    }

    private AbstractStep getStep(int type, ICommand command, FormEvent event, MatrixForm form) {
        String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
        if (selectedItem.equals(SMITH_FORM)) {
            return new SmithStep(type, command, event, form);
        } else if (selectedItem.equals(RATIONAL_FORM)) {
            return new RationalCanonicalStep(type, command, event, form);
        } else if (selectedItem.equals(JORDANS_FORM)) {
            return null;
        }

        return null;
    }
}