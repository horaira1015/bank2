
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CustomerLoginController {
	static String customername;
	@FXML
	TextField username;
	@FXML
	TextField password;

	public void back() {
		new Bank().changeScene("openingPage.fxml","Start Screen",651,422);
	}

	static public Customer downloadObj(String username) throws SQLException {
		String query2 = "Select * from customerAll where username='" + username + "';";
		Statement st = Bank.con.createStatement();
		st.executeQuery(query2);
		ResultSet rs = st.executeQuery(query2);
		rs.next();
		username = rs.getString(1);
		String fname = rs.getString(2);
		String lname = rs.getString(3);
		String phoneno = rs.getString(4);
		String email = rs.getString(5);
		String dob = rs.getString(6);
		String gender = rs.getString(7);
		Float balance = rs.getFloat(8);
		Customer c = new Customer(username, fname, lname, phoneno, email, dob, gender, balance);
		return c;
	}

	public void signedInCustomer(ActionEvent actionEvent) {
		try {
			String query1 = "Select username,password from customer where username='" + username.getText() + "';";
			Statement st = Bank.con.createStatement();
			ResultSet rs = st.executeQuery(query1);
			rs.next();
			String dbpassword = rs.getString(2);
//			System.out.println("error");
			if (dbpassword.contentEquals(password.getText())) {
				System.out.println("Right password");
				System.out.println(rs.getString(2));
				String query2 = "Select username from customerAll where username='" + username.getText() + "';";
				Statement st2 = Bank.con.createStatement();
				ResultSet rs2 = st2.executeQuery(query2);
				rs2.next();
				customername = rs2.getString(1);
				System.out.println(customername);
				new Bank().changeScene("CustomerAccountSelection.fxml", "Please Select Account", 663, 432);
			}
		} catch (SQLException sqlException) {
			System.out.println("Check username and password");
			Bank.showAlert(Alert.AlertType.ERROR, "Error", "Wrong Information", "Please check username or passwords");

		}
	}

	public void registerCustomer(ActionEvent actionEvent) {
		new Bank().changeScene("register.fxml", "Customer - Registration", 600, 495);
	}
}
