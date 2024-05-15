import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Transfer implements Transaction {
	private Customer customer;
	private Account account;
	private int payeeAccountNumber;
	private float amount;

	public Transfer(Customer customer, Account account, int payeeAccountNumber, float amount) {
		this.customer = customer;
		this.account = account;
		this.payeeAccountNumber = payeeAccountNumber;
		this.amount = amount;
	}

	@Override
	public void validate() throws SQLException, IllegalArgumentException {
		if (amount <= 0) {
			throw new IllegalArgumentException("Transfer amount must be positive");
		}
		if (amount > account.balance) {
			throw new IllegalArgumentException("Insufficient balance");
		}

		// Check if payee account is a saving account
		String query2 = "SELECT accountNumber FROM advance WHERE accountNumber = ?";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query2)) {
			pstmt.setInt(1, payeeAccountNumber);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// If payee account is found in advance, don't throw an exception
				System.out.println("Payee account found in advance table");
				throw new IllegalArgumentException("It's an advance account");
			} else {
				// Payee account is not a saving account, continue with other validations
				String query = "SELECT COUNT(*) FROM useraccounts WHERE accountnumber = ?";
				try (PreparedStatement pstmt2 = Bank.con.prepareStatement(query)) {
					pstmt2.setInt(1, payeeAccountNumber);
					ResultSet rs2 = pstmt2.executeQuery();
					if (rs2.next() && rs2.getInt(1) == 0) {
						throw new IllegalArgumentException("Payee account not found");
					}
				}
			}
		}
	}

	@Override
	public void execute() throws SQLException {
		validate();

		String getPayeeName = "SELECT username FROM useraccounts WHERE accountnumber = ?";
		String payeeName;
		try (PreparedStatement pstmt = Bank.con.prepareStatement(getPayeeName)) {
			pstmt.setInt(1, payeeAccountNumber);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				payeeName = rs.getString(1);
			} else {
				throw new SQLException("Payee account not found");
			}
		}

		Account payeeAccount = CustomerAccountSelectionCon.downloadAccount(payeeAccountNumber);

		String query1 = "UPDATE useraccounts SET `Balance` = `Balance` - ? WHERE `accountNumber` = ?";
		String query2 = "UPDATE useraccounts SET `Balance` = `Balance` + ? WHERE `accountNumber` = ?";
		String query3 = "UPDATE customerall SET `Balance` = `Balance` - ? WHERE `username` = ?";
		String query4 = "UPDATE customerall SET `Balance` = `Balance` + ? WHERE `username` = ?";

		try (PreparedStatement pstmt1 = Bank.con.prepareStatement(query1);
			 PreparedStatement pstmt2 = Bank.con.prepareStatement(query2);
			 PreparedStatement pstmt3 = Bank.con.prepareStatement(query3);
			 PreparedStatement pstmt4 = Bank.con.prepareStatement(query4)) {

			pstmt1.setFloat(1, amount);
			pstmt1.setInt(2, account.acnumber);
			pstmt1.executeUpdate();

			pstmt2.setFloat(1, amount);
			pstmt2.setInt(2, payeeAccountNumber);
			pstmt2.executeUpdate();

			pstmt3.setFloat(1, amount);
			pstmt3.setString(2, CustomerLoginController.customername);
			pstmt3.executeUpdate();

			pstmt4.setFloat(1, amount);
			pstmt4.setString(2, payeeName);
			pstmt4.executeUpdate();
		}

		String query = "INSERT INTO transactions (AccountNumber, CustomerName, Transactiontype, Amount, ToAccountNumber) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query)) {
			pstmt.setInt(1, account.acnumber);
			pstmt.setString(2, customer.getUsername());
			pstmt.setString(3, "transfer");
			pstmt.setFloat(4, amount);
			pstmt.setInt(5, payeeAccountNumber);
			pstmt.executeUpdate();
		}

		payeeAccount.depo(amount);
		account.withdraw(amount);
		customer.balance+=amount;
	}
}
