import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoanAccountEnterViewControl implements Initializable {
	public DatePicker starting;
	public Label interestRateShow;
	public DatePicker ending;
	public TextField amount;
	public TextField amount1;
	public TextField repayAcNu;
	static Loan x;

	public void goToAccountSelection(ActionEvent actionEvent) {
		new Bank().changeScene("CustomerAccountSelection.fxml", "Please Select Account", 663, 432);

	}
	public void deleteAc_go_to_selection() throws SQLException {
		if(ending.getValue() == null){
			Bank.showAlert(Alert.AlertType.ERROR, "Error", "Selection Error", "Please select date");
			return;
		}
		try {
			String p = String.valueOf(ending.getValue());
		} catch (Exception e) {
			Bank.showAlert(Alert.AlertType.ERROR, "Error", "Selection Error", "Please select date");
			return;
		}
		float depoAmount = Float.parseFloat(amount1.getText());

		x.setEndDate(ending.getValue().toString());
		float totalBalance = x.calculateBalanceToDate(ending.getValue().toString());

		Deposit depoTransaction = new Deposit(x.customer, x, totalBalance);
		//withdrawTransaction.account.balance = Bank.balance;
		float  p = x.customer.balance;
		float zz =Float.parseFloat( amount.getText());
		System.out.println(x.customer.balance + "eta    ...");
		depoTransaction.execute();
		System.out.println("customer balance ekhon" + x.customer.balance);
		String query = "UPDATE customerall SET `Balance` = ? WHERE `username` = ?";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query)) {
			pstmt.setFloat(1, p + zz);
			pstmt.setString(2, CustomerLoginController.customername);
			pstmt.executeUpdate();
		}

		deleteAccount(x.acnumber);
		System.out.println("===========");
		System.out.println(Bank.balance);
		System.out.println(depoAmount);
		Bank.balance += depoAmount;

		new Bank().changeScene("CustomerAccountSelection.fxml", "Please Select Account", 663, 432);
	}

//	public void deleteAc_go_to_selection() throws SQLException {
//		System.out.println("transaction ends");
//		Bank.balance += Float.parseFloat(amount1.getText());
//		Statement st = Bank.con.createStatement();
//		String yy = "delete from useraccounts where accountnumber = '" + x.acnumber + "'";
//		st.executeUpdate(yy);
//		yy = "delete from advance where accountnumber = '" + x.acnumber + "'";
//		st.executeUpdate(yy);
//		String query = "insert into transactions (AccountNumber, CustomerName, Transactiontype,Amount,ToAccountNumber) values (?,?,?,?,?)";
//		PreparedStatement pstmt = Bank.con.prepareStatement(query);
//
//		// Set the variable values using the appropriate setter methods
//		pstmt.setInt(1, x.acnumber); // Assuming accountNumber is an int variable
//		pstmt.setString(2, x.customer.getUsername()); // Assuming customerName is a String variable
//		pstmt.setString(3, "deposit"); // Assuming transactionType is a String variable
//		pstmt.setInt(4,(int)Float.parseFloat(amount1.getText())); // Assuming amount is a double variable
//		pstmt.setInt(5, x.acnumber); // Assuming toAccountNumber is an int variable
//
//		// Execute the INSERT query
//		pstmt.executeUpdate();
//		new Bank().changeScene("CustomerAccountSelection.fxml", "Please Select Account", 663, 432);
//	}
	private void deleteAccount(int accountNumber) throws SQLException {
		try (Statement st = Bank.con.createStatement()) {
			st.executeUpdate("DELETE FROM useraccounts WHERE accountNumber = " + accountNumber);
			st.executeUpdate("DELETE FROM advance WHERE accountNumber = " + accountNumber);
		}
	}


	public void calculate(ActionEvent actionEvent) throws SQLException {
		float total = x.calculateBalanceToDate(ending.getValue().toString());
		amount1.setText(String.valueOf(total));
	}

	public void cashPaid(ActionEvent actionEvent) throws SQLException {
		System.out.println("Paid_Cash");
		deleteAc_go_to_selection();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		try {
			x = LoanAccountStartViewControl.downloadLoanAccount(CustomerAccountSelectionCon.acnumber);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		amount.setText(String.valueOf(x.balance));
		interestRateShow.setText(String.valueOf(AdvanceFeature.LOAN_INTEREST_RATE));
	}
}
