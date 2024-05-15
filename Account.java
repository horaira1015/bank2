public class Account {
	float balance;
	Customer customer;
	int acnumber;

	Account(int acnumber, float balance, Customer customer) {
		this.acnumber = acnumber;
		this.balance = balance;
		this.customer = customer;
	}

	void depo(float am) {
		this.balance += am;
		this.customer.balance += am;
		System.out.println(customer.balance + "eta account e");
		System.out.println(customer.balance);
	}

	int withdraw(float am) {
		if (this.balance < am) {
			return 0;
		}
		this.balance -= am;
		this.customer.balance -= am;
		return 1;
	}
}
