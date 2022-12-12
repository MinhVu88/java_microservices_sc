package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {
	@Autowired
	private EventStore eventStore;

	@Override
	public void save(AggregateRoot aggregateRoot) {
		eventStore.saveEvents(
			aggregateRoot.getId(),
			aggregateRoot.getUncommittedChanges(),
			aggregateRoot.getVersion()
		);

		aggregateRoot.markChangesAsCommitted();
	}

	@Override
	public AccountAggregate getById(String id) {
		var accountAggregate = new AccountAggregate();

		var events = eventStore.getEvents(id);

		if(events != null && !events.isEmpty()) {
			accountAggregate.replayEvents(events);

			var latestVersion = events.stream().map(e -> e.getVersion()).max(Comparator.naturalOrder());

			accountAggregate.setVersion(latestVersion.get());
		}

		return accountAggregate;
	}
}
