import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Bank extends Application {
	public static Connection con;
	static Stage pstage;
	static float balance;
	public static void main(String[] args) throws SQLException {
		connectDB("jdbc:mysql://localhost:3306/CSEDU_BANK","root","12345678"); //connectDB(url,user,password);
		launch(args);
	}
	public static void connectDB(String url,String user,String pass) throws SQLException { //Connecting to database at the start of the program
		con = DriverManager.getConnection(url, user, pass);
		System.out.println(con);
	}

	public void changeScene(String gui, String title, int width, int height) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(gui));//loading FXML file
			Scene scene = new Scene(root, width, height);  //setting width and height of the window //loading CSS file if used
			pstage.setScene(scene);  // scene=content inside window
			pstage.setTitle(title); //Title of the window
			pstage.setResizable(false); //Making window non resizable
			pstage.show();  //Showing window to user
		} catch (Exception e) {
			e.printStackTrace();  //Printing Exception
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		pstage=primaryStage;
		changeScene("openingPage.fxml","Start Screen",651,422);
		String query = "SELECT balance FROM advance WHERE accountType = 'fixedDeposit'";
		try {
			System.out.println("ff..");
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {;
				balance+=resultSet.getFloat("balance");
				System.out.println("Balance: " + balance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		query = "SELECT balance FROM advance WHERE accountType = 'loan'";
		System.out.println("balance1");
		try {
			Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {;
				balance-=resultSet.getFloat("balance");
				System.out.println("Balance: " + balance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("balance1");
	}
	public static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}


}