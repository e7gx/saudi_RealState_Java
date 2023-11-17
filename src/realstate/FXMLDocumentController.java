package realstate;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import realstate.RealState.Product;

/**
 *
 * @author imalm
 */
public class FXMLDocumentController implements Initializable {

    private static final String DB_NAME = "productsdb_alghamdi_almaajeeni";
    private static final String TABLE_NAME = "realestate_abdullah";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "marmar07";
    private final int currentId = 1;

    private Connection connection;
    private final ObservableList<Product> realEstateList = FXCollections.observableArrayList();

    @FXML
    private Text DateLabel;

    @FXML
    private Text IdLabel;

    @FXML
    private Text LandAreaLabel;

    @FXML
    private Text NameLabel;

    @FXML
    private Text PriceLabel;

    @FXML
    private Text TitleLabel;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Product, Date> dateColumn;

    @FXML
    private DatePicker dateDatePicker;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TextField idTextField;

    @FXML
    private TableColumn<Product, String> itemColumn;

    @FXML
    private TextField itemTextField;

    @FXML
    private Label label;

    @FXML
    private TableColumn<Product, Float> landAreaColumn;

    @FXML
    private TextField landAreaTextField;

    @FXML
    private TableColumn<Product, String> priceColumn;

    @FXML
    private TextField priceTextField;

    @FXML
    private TableView<Product> realEstateTable;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchTextField;

    @FXML
    void handleAddButton(ActionEvent event) {
        addProperty();
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
        deleteProperty();
    }

    @FXML
    void handleSearchButton(ActionEvent event) {
        searchProperties();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectToDatabase();
        loadDataFromDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
        }
    }

    private void loadDataFromDatabase() {

        try {
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String price = resultSet.getString("price");
                    float landArea = resultSet.getFloat("landArea");
                    Date date = resultSet.getDate("date");

                    Product property = new Product(id, name, price, landArea, date);
                    realEstateList.add(property);

                    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                    itemColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
                    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                    landAreaColumn.setCellValueFactory(new PropertyValueFactory<>("landArea"));
                    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

                    realEstateTable.setItems(realEstateList);

                }

            }

            System.out.println("Loaded data from the database.");

        } catch (SQLException e) {
        }
    }

    private void insertPropertyIntoDatabase(Product property) {
        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE_NAME + " (id, name, price, landArea, date) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, property.getId());
                statement.setString(2, property.getName());
                statement.setString(3, property.getPrice());
                statement.setFloat(4, property.getLandArea());
                statement.setDate(5, property.getDate());

                statement.executeUpdate();
            }

            System.out.println("Inserted property into the database: " + property);

        } catch (SQLException e) {
        }
    }

    private void searchProperties() {
        String keyword = searchTextField.getText().toLowerCase();

        if (keyword.isEmpty()) {
            realEstateTable.setItems(realEstateList);
        } else {
            ObservableList<Product> filteredList = FXCollections.observableArrayList();

            realEstateList.stream().filter((property) -> (property.getName().toLowerCase().contains(keyword)
                    || property.getPrice().toLowerCase().contains(keyword))).forEachOrdered((property) -> {
                filteredList.add(property);
            });

            if (filteredList.isEmpty()) {
                showErrorAlert("لا يوجد نتائج", "لا يوجد أي قطعة أرض بالاسم المحدد.");
            } else {
                // إنشاء جدول وعرض النتائج فيه
                TableView<Product> searchResultTable = new TableView<>();
                TableColumn<Product, Integer> ID = new TableColumn<>(" الرقم");
                ID.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

                TableColumn<Product, String> ITEM = new TableColumn<>(" المدينة");
                ITEM.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

                TableColumn<Product, String> PRICE = new TableColumn<>(" السعر");
                PRICE.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

                TableColumn<Product, Integer> LANDAREA = new TableColumn<>(" مساحة الأرض");
                LANDAREA.setCellValueFactory(cellData -> cellData.getValue().landAreaProperty().asObject());

                TableColumn<Product, Date> DATEC = new TableColumn<>(" التاريخ");
                DATEC.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

                searchResultTable.getColumns().addAll(ID, ITEM, PRICE, LANDAREA, DATEC);
                searchResultTable.setItems(filteredList);

                Stage searchResultStage = new Stage();
                searchResultStage.setTitle("نتائج البحث");
                searchResultStage.getIcons().add(new Image("/icon/Logo2.png"));

                BorderPane searchResultPane = new BorderPane();
                searchResultPane.setCenter(searchResultTable);

                Scene searchResultScene = new Scene(searchResultPane);
                searchResultStage.setScene(searchResultScene);

                searchResultStage.show();
            }
        }
    }

    private void deletePropertyFromDatabase(Product property) {
        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + TABLE_NAME + " WHERE id = ?")) {
                statement.setInt(1, property.getId());

                statement.executeUpdate();
            }

            System.out.println("Deleted property from the database: " + property);

        } catch (SQLException e) {
        }
    }

    private void deleteProperty() {
        Product selectedProperty = realEstateTable.getSelectionModel().getSelectedItem();

        if (selectedProperty != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("تأكيد");
            confirmation.setHeaderText("حذف العقار");
            confirmation.setContentText("هل أنت متأكد أنك تريد حذف العقار المحدد؟");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                realEstateList.remove(selectedProperty);
                deletePropertyFromDatabase(selectedProperty);

                System.out.println("تم حذف العقار: " + selectedProperty);
            }
        } else {
            System.out.println("لم يتم تحديد أي عقار.");
        }
    }

    private void addProperty() {
        try {
            int id = Integer.parseInt(idTextField.getText());

            // Check if ID already exists
            boolean idExists = realEstateList.stream().anyMatch(property -> property.getId() == id);
            if (idExists) {
                showErrorAlert("خطأ", "تم إدخال رقم الهوية المدخل مسبقًا.");
                return;
            }

            String name = itemTextField.getText();
            String price = priceTextField.getText();
            float landArea = Float.parseFloat(landAreaTextField.getText());
            LocalDate localDate = dateDatePicker.getValue();
            Date date = Date.valueOf(localDate);

            Product property = new Product(id, name, price, landArea, date);
            realEstateList.add(property);

            insertPropertyIntoDatabase(property);

            idTextField.clear();
            itemTextField.clear();
            priceTextField.clear();
            landAreaTextField.clear();
            dateDatePicker.setValue(null);

            // Show confirmation message
            showInfoAlert("نجاح", "تمت إضافة العقار بنجاح.");

            System.out.println("Added property: " + property);

        } catch (NumberFormatException e) {
            showErrorAlert("خطأ في الإدخال", "يرجى إدخال بيانات صحيحة.");
        }
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/icon/Logo2.png"));

        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/icon/Logo2.png"));

        alert.showAndWait();
    }

}
