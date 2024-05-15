import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Deposit implements Transaction {
	private Customer customer;
	private Account account;
	private float amount;

	public Deposit(Customer customer, Account account, float amount) {
		this.customer = customer;
		this.account = account;
		this.amount = amount;
	}

	@Override
	public void validate() throws IllegalArgumentException {
		if (amount <= 0) {
			throw new IllegalArgumentException("Deposit amount must be positive");
		}
	}

	@Override
	public void execute() throws SQLException {
		validate();
		System.out.println("ashlam depo te");
		float total = customer.getBalance() + amount;
		System.out.println(customer.getBalance());

		String query = "UPDATE customerall SET `Balance` = ? WHERE `username` = ?";
		try (PreparedStatement pstmt = Bank.con.prepareStatement(query)) {
			pstmt.setFloat(1, total);
			pstmt.setString(2, CustomerLoginController.customername);
			pstmt.executeUpdate();
		}

		total = account.balance + amount;
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
			pstmt.setString(3, "deposit");
			pstmt.setFloat(4, amount);
			pstmt.setInt(5, account.acnumber);
			pstmt.executeUpdate();
		}

		account.depo(amount);
		customer.balance+=amount;
		System.out.println("depo sesh te");
		System.out.println(customer.balance+"99999999999");
	}
}
