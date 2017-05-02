package application;

import java.io.FileNotFoundException;
import javax.script.ScriptException;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tabdetachable.TabPaneDetacher;
 
@SuppressWarnings("restriction")
public class Main extends Application {
    
    static ObservableList<String> filter = FXCollections.observableArrayList();
    private FilteredList<Input> inputData;
    private FilteredList<Result> resultData;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void start(final Stage stage) throws FileNotFoundException, ScriptException, NoSuchMethodException {
        GUI gui = new GUI();
        BorderPane border = new BorderPane();
        VBox vbox = gui.addVBox(stage);
        border.setLeft(vbox);
        
        final GridPane inputGridPane = new GridPane();
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        
        final TextArea textArea = GUI.textArea;
		textArea.textProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue,
	            Object newValue) {
					textArea.setScrollTop(Double.MAX_VALUE);
	    		}
			});
        textArea.appendText("");
        textArea.setPrefSize(600, 140);
        GUI.textArea = textArea;
        
        final VBox vbox1 = new VBox(textArea);
        border.setBottom(vbox1);
        textArea.setText("");
        
        final Label label = new Label("Median Tree");
        label.setFont(new Font("Arial", 18));
        
        final TableView<Result> table = new TableView();
        table.setTableMenuButtonVisible(true);
        
        final ComboBox comboBox = new ComboBox(filter);
        filter.add("All");
        comboBox.setValue("File Input");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.add(label,0,1);
        grid.add(comboBox,12,1);
        
        
        table.setRowFactory( tv -> {
            TableRow<Result> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Result rowData = row.getItem();
                    Tab tab = new Tab();
    				tab.setText("Median tree " + rowData.getNumber());
    				String tree = rowData.getTree();
    				
    				final VBox vBox = new VBox(5);
					WebView webView = new BrowserView().webView(tree, null);
					vBox.getChildren().setAll(webView);
                    tab.setContent(vBox);
    					
    				GUI.tabPane.getTabs().add(tab);
    				TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
    				SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
    				selectionModel.select(tab);
                }
            });       

            row.hoverProperty().addListener((observable) -> {
                final Result result = row.getItem();

                if (row.isHover() && result != null) {
                	final Tooltip tooltip = new Tooltip();
                	tooltip.setText(result.getInputName() + " Algo: " + result.getAlgo() + " Score : " + result.getBestScore());
                	row.setTooltip(tooltip);
                }
            });
            
            return row ;
        });
        
        final TableColumn col5 = new TableColumn("No.");
        col5.setCellValueFactory(new PropertyValueFactory("number"));
        col5.setResizable(false);
        col5.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        
        final TableColumn col6 = new TableColumn("For Input");
        col6.setCellValueFactory(new PropertyValueFactory("inputName"));
        col6.setResizable(false);
        col6.prefWidthProperty().bind(table.widthProperty().multiply(0.55));
        
        final TableColumn col7 = new TableColumn("Algo");
        col7.setCellValueFactory(new PropertyValueFactory("algo"));
        col7.setResizable(false);
        col7.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        
        final TableColumn col8 = new TableColumn("Score");
        col8.setCellValueFactory(new PropertyValueFactory("bestScore"));
        col8.setResizable(false);
        col8.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        
        final TableColumn col9 = new TableColumn("Input");
        col9.setCellValueFactory(new PropertyValueFactory("fileInput"));
        col9.setVisible(false);
        
        final TableColumn col10 = new TableColumn("Taxa");
        col10.setCellValueFactory(new PropertyValueFactory("leaves"));
        col10.setResizable(false);
        col10.prefWidthProperty().bind(table.widthProperty().multiply(0.10));
        
        table.getColumns().addAll(col5, col6, col8, col10, col7, col9);
        resultData = new FilteredList<>(gui.getResult());
        table.setItems(resultData);
        table.setMinWidth(380);
        table.setMaxWidth(380);
        
        final VBox vboxTable = new VBox();
        vboxTable.setSpacing(2);
        vboxTable.setPadding(new Insets(10, 0, 0, 10));
        vboxTable.getChildren().addAll(grid, table);     
       
        final Label labelInput = new Label("Input Trees");
        labelInput.setFont(new Font("Arial", 18));      
        
        final TableView<Input> tableInput = new TableView();
        tableInput.setTableMenuButtonVisible(true);
        
        tableInput.setRowFactory( tv -> {
            TableRow<Input> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Input rowData = row.getItem();
                    Tab tab = new Tab();
    				tab.setText(rowData.getInput());
    				
    				String tree = rowData.getTree().replaceAll("\t","");
    				final VBox vBox = new VBox(5);
					WebView webView = new BrowserViewWithoutComparison().webView(tree);
					vBox.getChildren().setAll(webView);
                    tab.setContent(vBox);

    				GUI.tabPane.getTabs().add(tab);
    				TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
    				SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
    				selectionModel.select(tab);
                }
            });
            
            row.hoverProperty().addListener((observable) -> {
                final Input input = row.getItem();

                if (row.isHover() && input != null) {
                	final Tooltip tooltip = new Tooltip();
                	tooltip.setText(input.getInput());
                	row.setTooltip(tooltip);
                }
            });
            
            return row ;
        });
        
        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String previous, String current) {
            inputData.setPredicate(input -> true);
            if(!current.equals("All"))
            	inputData.setPredicate(input -> {
                     for (TableColumn<Input, ?> col : tableInput.getColumns()) {
                         String cellValue = col.getCellData(input).toString();
                         if (cellValue.equals(current)) {
                             return true;
                         }
                     }
                     return false;
                 });
            	 
            resultData.setPredicate(result -> true);
            if(!current.equals("All"))
            resultData.setPredicate(result -> {
            	for (TableColumn<Result, ?> col : table.getColumns()) {
                    String cellValue = col.getCellData(result).toString();
                    if (cellValue.equals(current)) {
                        return true;
                    }
                }
                return false;
             });
            }    
        });
        
        final TableColumn colInput1 = new TableColumn("No.");
        colInput1.setCellValueFactory(new PropertyValueFactory("number"));
        colInput1.setResizable(false);
        colInput1.prefWidthProperty().bind(tableInput.widthProperty().multiply(0.1));
        
        final TableColumn colInput2 = new TableColumn("Name");
        colInput2.setCellValueFactory(new PropertyValueFactory("input"));
        colInput2.setResizable(false);
        colInput2.prefWidthProperty().bind(tableInput.widthProperty().multiply(0.6));
        
        final TableColumn colInput5 = new TableColumn("Input");
        colInput5.setCellValueFactory(new PropertyValueFactory("fileInput"));
        colInput5.setResizable(false);
        colInput5.prefWidthProperty().bind(tableInput.widthProperty().multiply(0.20));
        
        final TableColumn colInput4 = new TableColumn("Taxa");
        colInput4.setCellValueFactory(new PropertyValueFactory("leaves"));
        colInput4.setResizable(false);
        colInput4.prefWidthProperty().bind(tableInput.widthProperty().multiply(0.10));
        
        tableInput.getColumns().addAll(colInput1, colInput2, colInput4, colInput5);
        inputData = new FilteredList<>(gui.getInput());
        tableInput.setItems(inputData); 
        tableInput.setMinWidth(380);
        tableInput.setMaxWidth(380);
        
        vboxTable.getChildren().addAll(labelInput, tableInput);        
        border.setRight(vboxTable);
        
    	TabPaneDetacher.create().makeTabsDetachable(gui.getTabPane());     
        border.setCenter(gui.getTabPane());	
    	
    	Tab tab = new Tab();
    	tab.setText("Homepage");
		
		final VBox vBox = new VBox(5);
		WebView webView = new WelcomePage().webView();
		vBox.getChildren().setAll(webView);
        tab.setContent(vBox);
        
		GUI.tabPane.getTabs().add(tab);
		TabPaneDetacher.create().makeTabsDetachable(GUI.tabPane);
		SingleSelectionModel<Tab> selectionModel = GUI.tabPane.getSelectionModel();
		selectionModel.select(tab);
		
		GUI.tabPane.getSelectionModel().selectedItemProperty().addListener(
			    new ChangeListener<Tab>() {
			        @Override
			        public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
			            
			        }
			    }
			);
        
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(border);
        rootGroup.setPadding(new Insets(10,10,10,10));
        
        final Scene scene = new Scene(rootGroup, 1000, 850); 
    	stage.setTitle("Genie");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

	public static void main(String[] args) {
        Application.launch(args);
    }
}