import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomerAccountSelectionCon implements Initializable {

	@FXML
	public Label TotalAccount;
	@FXML
	public Label Fullbalance;
	@FXML
	TableView tableView;
	@FXML
	Label username;
	@FXML
	TextField acNumber;
	Customer c;
	static int acnumber;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		int countAccounts = 0;
		float countBalance = 0;
		try {
			System.out.println("ff");
			c = CustomerLoginController.downloadObj(CustomerLoginController.customername);
			System.out.println(c);
			username.setText(c.getUsername());
			countAccounts = 0;
			String selectQuery = "SELECT accountNumber, balance FROM useraccounts where username='" + CustomerLoginController.customername + "';";
			PreparedStatement pstmt = Bank.con.prepareStatement(selectQuery);
			ResultSet rs = pstmt.executeQuery();
			countBalance = 0;
			tableView.getColumns().clear(); // Clear existing columns

			// Create TableColumn instances for each column in the ResultSet
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;
				//countBalance += rs.getInt(2);
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1)); // Column index starts from 1
				col.prefWidthProperty().bind(tableView.widthProperty().divide(2));
				col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
						new SimpleStringProperty(param.getValue().get(j).toString()));

				tableView.getColumns().add(col);
			}

			ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
			while (rs.next()) {
				countAccounts++;
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					if (i == 2) { // Check if it's the balance column
						//countBalance += rs.getFloat(i); // Add balance to countBalance
					}
					row.add(rs.getString(i));
				}
				data.add(row);
			}

			tableView.setItems(data);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String quer = "SELECT balance FROM customerall where username='" + CustomerLoginController.customername + "';";
		PreparedStatement pstmt = null;
		try {
			pstmt = Bank.con.prepareStatement(quer);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try {
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			countBalance = rs.getFloat("balance");

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		TotalAccount.setText(String.valueOf(countAccounts));
		Fullbalance.setText(String.valueOf(countBalance));
	}

	public static Account downloadAccount(int id) throws SQLException {

		String query2 = "Select * from useraccounts where accountNumber='" + id + "';";
		Statement st = Bank.con.createStatement();
		st.executeQuery(query2);
		ResultSet rs = st.executeQuery(query2);
		rs.next();
		int accountNumber = rs.getInt(2);
		int balance = rs.getInt(3);
		Account c = new Account(accountNumber, balance, CustomerLoginController.downloadObj(CustomerLoginController.customername));
		return c;
	}

	public void enterAccount(ActionEvent actionEvent) throws SQLException {
		acnumber = 0;
		try {
			acnumber=Integer.parseInt(acNumber.getText());
		} catch (Exception e) {
			Bank.showAlert(Alert.AlertType.ERROR, "Error", "Account selection Error", "Please insert correct account number");
			return;
		}
		System.out.println(acnumber);
		int x = 0;
		try {
			String query2 = "Select accountNumber from advance where accountNumber='" + acnumber + "'AND accountType='fixedDeposit'and username ='" + CustomerLoginController.customername + "';";
			Statement st = Bank.con.createStatement();
			st.executeQuery(query2);
			ResultSet rs = st.executeQuery(query2);
			rs.next();
			if (rs.getString(1) == null) {
				System.out.println("amiNull");
			}
			System.out.println(rs);
			x = 1;
			new Bank().changeScene("savingAccountEnterView.fxml", "Saving Account", 600, 400);
			System.out.println("hereIam");
		} catch (Exception e) {
			System.out.println("it's not a depo");
		}
		if (x == 1) {
			return;
		}
		try {
			String query2 = "Select accountNumber from advance where accountNumber='" + acnumber + "'AND accountType='loan' and username ='" + CustomerLoginController.customername + "';";
			Statement st = Bank.con.createStatement();
			st.executeQuery(query2);
			ResultSet rs = st.executeQuery(query2);
			rs.next();
			if (rs.getString(1) == null) {
				System.out.println("amiNull");
			}
			x = 1;
			System.out.println("hereIam2");
			new Bank().changeScene("LoanAccountEnterView.fxml", "Loan Account", 600, 400);
		} catch (Exception e) {
			System.out.println("it's not a saving");
		}
		if (x == 1) {
			return;
		}
		try {
			String query2 = "Select accountNumber from useraccounts where accountNumber='" + acnumber + "'AND username='" + CustomerLoginController.customername + "';";
			System.out.println(CustomerLoginController.customername);
			Statement st = Bank.con.createStatement();
			st.executeQuery(query2);
			ResultSet rs = st.executeQuery(query2);
			rs.next();
			System.out.println(rs.getString(1));
			System.out.println("tonmoy ekhane ache");
		} catch (Exception e) {
			Bank.showAlert(Alert.AlertType.ERROR, "Error", "Registration Error", "Please select correct account number");
			return;

		}
		System.out.println(CustomerAccountSelectionCon.acnumber);
		System.out.println("hereIam3");
		new Bank().changeScene("AccountTransactionHere.fxml", "accountPage", 720, 530);
	}

	public void signout(ActionEvent actionEvent) {
		new Bank().changeScene("CustomerLogin.fxml", "Customer - Login", 553, 457);
	}

	public void makeNewAccount(ActionEvent actionEvent) throws SQLException {
		Statement st = Bank.con.createStatement();
		String query = "INSERT INTO useraccounts (username, balance) VALUES ('" + CustomerLoginController.customername + "', 0)";
		int i = st.executeUpdate(query);
		if (i == 1) System.out.println("Updated");
		else System.out.println("Not updated");
		//showAlert(Alert.AlertType.INFORMATION, "Information", "Update Completed", "Thank You");

		new Bank().changeScene("CustomerAccountSelection.fxml", "Please Select Account", 663, 432);
	}

	public void takeLoan(ActionEvent actionEvent) {
		new Bank().changeScene("LoanAccountStartView.fxml", "loan", 700, 500);

	}

	public void fixedDeposit(ActionEvent actionEvent) {
		new Bank().changeScene("savingAccountStartView.fxml", "fixedDeposit", 600, 400);
	}
}
