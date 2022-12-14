package com.techbank.account.cmd.domain;

import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.cqrs.core.domain.AggregateRoot;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class AccountAggregate extends AggregateRoot {
	private Boolean active;
	private double balance;

	public double getBalance() {
		return balance;
	}

	// This constructor method handles the AccountOpenedEvent event via the OpenAccountCommand command.
	// A command that "creates" an Aggregate instance should always be handled in an Aggregate constructor like this one.
	public AccountAggregate(OpenAccountCommand command) {
		raiseEvent(
			// build() returns a constructed AccountOpenedEvent instance
			AccountOpenedEvent.builder()
				.id(command.getId())
				.accountHolder(command.getAccountHolder())
				.createdDate(new Date())
				.accountType(command.getAccountType())
				.openingBalance(command.getOpeningBalance())
				.build()
		);
	}

	// apply() applies the AccountOpenedEvent event to the Aggregate
	public void apply(AccountOpenedEvent event) {
		this.id = event.getId();
		this.active = true;

		// set the existing balance to the newly created account's initial balance
		this.balance = event.getOpeningBalance();
	}

	public void depositFunds(double amount) {
		if(!this.active) {
			throw new IllegalStateException("funds can't be deposited into a closed bank account");
		}

		if(amount <= 0) {
			throw new IllegalStateException("the deposited amount must be greater than zero");
		}

		raiseEvent(
			FundsDepositedEvent.builder()
				.id(this.id)
				.amount(amount)
				.build()
		);
	}

	public void apply(FundsDepositedEvent event) {
		this.id = event.getId();

		// add the deposited amount to the existing balance
		this.balance += event.getAmount();
	}

	public void withdrawFunds(double amount) {
		if(!this.active) {
			throw new IllegalStateException("funds can't be withdrawn from a closed bank account");
		}

		raiseEvent(
			FundsWithdrawnEvent.builder()
				.id(this.id)
				.amount(amount)
				.build()
		);
	}

	public void apply(FundsWithdrawnEvent event) {
		this.id = event.getId();

		// subtract the withdrawn amount from the existing balance
		this.balance -= event.getAmount();
	}

	public void closeAccount() {
		if(!this.active) {
			throw new IllegalStateException("the bank account has already been closed");
		}

		raiseEvent(
			AccountClosedEvent.builder().id(this.id).build()
		);
	}

	public void apply(AccountClosedEvent event) {
		this.id = event.getId();
		this.active = false;
	}
}
