import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Withdraw implements Transaction {
	private Customer customer;
	 Account account;
	private float amount;

	public Withdraw(Customer customer, Account account, float amount) {
		this.customer = customer;
		this.account = account;
		this.amount = amount;
	}

	@Override
	public void validate() throws SQLException, IllegalArgumentException {
		if (amount <= 0) {
			throw new IllegalArgumentException("Withdrawal amount must be positive");
		}
		if (amount > account.balance) {
			System.out.println("beshi thik ase2" );
			throw new IllegalArgumentException("Insufficient balance");
		}
	}

	@Override
	public void execute() throws SQLException {
		System.out.println("beshi thik ase3" );
		validate();
		System.out.println("beshi thik ase" );
		float total = customer.getBalance() - amount;
		String query = "UPDATE customerall SET `Balance` = ? WHERE `username` = ?";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query)) {
			pstmt.setFloat(1, total);
			pstmt.setString(2, CustomerLoginController.customername);
			pstmt.executeUpdate();
		}

		total = account.balance - amount;
		System.out.println(total+"---------");
		query = "UPDATE useraccounts SET `balance` = ? WHERE `accountNumber` = ?";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query)) {
			pstmt.setFloat(1, total);
			pstmt.setInt(2, account.acnumber);
			pstmt.executeUpdate();
		}

		query = "INSERT INTO transactions (AccountNumber, CustomerName, Transactiontype, Amount, ToAccountNumber) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query)) {
			pstmt.setInt(1, account.acnumber);
			pstmt.setString(2, customer.getUsername());
			pstmt.setString(3, "withdraw");
			pstmt.setFloat(4, amount);
			pstmt.setInt(5, account.acnumber);
			pstmt.executeUpdate();
		}

		account.withdraw(amount);
		customer.balance-=amount;
	}
}
