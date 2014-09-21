package rs.etf.km123247m.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.MainApp;
import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.PolynomialRationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.Implementation.SymJaMatrixHandler;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Model.*;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Observer.FormObserver;
import rs.etf.km123247m.Parser.MatrixParser.SymJa.IExprMatrixStringParser;
import rs.etf.km123247m.Parser.ParserTypes.StringParser;

import java.io.*;
import java.util.Collection;
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

    protected LaTexCanvas getCanvas(String formula) {
        LaTexCanvas canvas = new LaTexCanvas();

        Pane pane = new AnchorPane();
        pane.setMinHeight(20 + range*20);
        pane.getChildren().add(canvas);
        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.setFormula(formula);

        return canvas;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void set2x2ExampleMatrixAction() {
        fileNameLabel.setText("");
        statusLabel.setText("Loaded example 2x2 matrix.");
        inlineInput.setText(MatrixExamples.TWOxTWO);
    }

    @FXML
    private void set3x3ExampleMatrixAction() {
        statusLabel.setText("Loaded example 3x3 matrix.");
        fileNameLabel.setText("");
        inlineInput.setText(MatrixExamples.THREExTHREE);
    }

    @FXML
    private void set3x3PolyExampleMatrixAction() {
        statusLabel.setText("Loaded example 4x4 matrix.");
        fileNameLabel.setText("");
        inlineInput.setText(MatrixExamples.FOURxFOUR);
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
    private void getInMuPadFormatAction() {
        StackPane secondaryLayout = new StackPane();
        Integer selected = stepList.getSelectionModel().getSelectedIndices().get(0);
        AbstractStep selectedStep;

        if (selected == -1) {
            secondaryLayout.getChildren().add(new Label("No steps selected."));
        } else {
            try {
                selectedStep = stepObjects.get(selected);
                secondaryLayout.getChildren().add(new TextArea(selectedStep.getMuPadCommands()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Scene secondScene = new Scene(secondaryLayout, 400, 200);
        Stage secondStage = new Stage();
        secondStage.setTitle("Matrices in MuPad format");
        secondStage.setScene(secondScene);
        //Set position of second window, related to primary window.
        secondStage.setX(mainApp.getPrimaryStage().getX() + 250);
        secondStage.setY(mainApp.getPrimaryStage().getY() + 100);
        secondStage.show();
    }

    @FXML
    private void stepSelected() {
        matrixStateVBox.getChildren().clear();
        Integer selected = stepList.getSelectionModel().getSelectedIndices().get(0);
        AbstractStep selectedStep;
        if (selected == -1) {
            LaTexCanvas canvas = getCanvas("\\text{No steps selected.}");
            matrixStateVBox.getChildren().add(canvas);
            canvas.render();
        } else {
            try {
                selectedStep = stepObjects.get(selected);
                Collection<Pane> panes = selectedStep.getPanes();
                for(Pane pane: panes) {
                    matrixStateVBox.getChildren().add(pane);
                }
            } catch (Exception e) {
                e.printStackTrace();
                for (StackTraceElement s: e.getStackTrace()) {
                    matrixStateVBox.getChildren().add(new Label(s.toString()));
                }
            }
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
            StringParser parser = new IExprMatrixStringParser(true);
            parser.setInputString(inlineInput.getText());
            try {
                String selectedItem = (String) selectForm.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    IMatrix matrix = (IMatrix) parser.parseInput();
                    range = matrix.getRowNumber();
                    MatrixHandler handler = new SymJaMatrixHandler(matrix);
                    matrixForm = null;
                    if (selectedItem.equals(SMITH_FORM)) {
                        matrixForm = new SmithMatrixForm(handler);
                    } else if (selectedItem.equals(RATIONAL_FORM)) {
                        matrixForm = new PolynomialRationalCanonicalMatrixForm(handler);
                    } else if (selectedItem.equals(JORDANS_FORM)) {
                        matrixForm = new JordanMatrixForm(handler);
                    }
                    if (matrixForm != null) {
                        matrixForm.addObserver(this);
                        matrixForm.start();
                    }
                }
            } catch (Exception e) {
                statusLabel.setText(e.getMessage());
                matrixStateVBox.getChildren().add(new Label(e.getMessage()));
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
                statusLabel.setText(event.getMessage());
                System.out.println(event.getMessage());
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
            return new JordanStep(type, command, event, form);
        }

        return null;
    }
}