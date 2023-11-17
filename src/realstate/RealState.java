package realstate;

import java.io.IOException;
import java.sql.Date;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author imalm
 */
public class RealState extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("برنامج العقارات");
            primaryStage.getIcons().add(new Image("/icon/Logo2.png"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/realstate/FXMLDocument.fxml"));
            Parent root = loader.load();

            // Set up the scene, stage, and show the GUI
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Product {

        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty price;
        private final SimpleObjectProperty<Date> date;
        private final SimpleIntegerProperty landArea;

        public Product(int id, String name, String price, float landArea, Date date) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.price = new SimpleStringProperty(price);
            this.date = new SimpleObjectProperty<>(date);
            this.landArea = new SimpleIntegerProperty((int) landArea);
        }

        public int getId() {
            return id.get();
        }

        public SimpleIntegerProperty idProperty() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getPrice() {
            return price.get();
        }

        public SimpleStringProperty priceProperty() {
            return price;
        }

        public Date getDate() {
            return date.get();
        }

        public SimpleObjectProperty<Date> dateProperty() {
            return date;
        }

        public int getLandArea() {
            return landArea.get();
        }

        public SimpleIntegerProperty landAreaProperty() {
            return landArea;
        }

        @Override
        public String toString() {
            return "Product{"
                    + "id=" + id.get()
                    + ", name='" + name.get() + '\''
                    + ", price='" + price.get() + '\''
                    + ", date=" + date.get()
                    + ", landArea=" + landArea.get()
                    + '}';
        }
    }
}
