/* What is an Aggregate?
  - An Aggregate is an entity or a group of entities that's always kept in a consistent state.

  - The Aggregate Root (AR) is an entity responsible for maintaining
    this consistent state within the Aggregate.

  - The AR maintains a list of uncommitted changes in the form of events that
    needs to be applied to the Aggregate & be persisted to the event store.

  - The AR contains a method that can be invoked to commit
    changes which have been applied to the Aggregate.

  - The AR manages which apply method (account.cmd.domain.AccountAggregate)
    is invoked on the Aggregate, based on the event type.

  - This makes the Aggregate the primary building block for
    implementing a command model in any CQRS-based application
*/
package com.techbank.cqrs.core.domain;

import com.techbank.cqrs.core.events.BaseEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AggregateRoot {
	protected String id;
	private int version = -1;

	// this contains all the changes made to the Aggregate in the form of events
	private final List<BaseEvent> changes = new ArrayList<>();

	// this logs exceptions to the Spring container
	private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<BaseEvent> getUncommittedChanges() {
		return this.changes;
	}

	public void markChangesAsCommitted() {
		// the changes list gets emptied to make sure that subsequent events added to the list are new ones
		this.changes.clear();
	}

	protected void applyChange(BaseEvent event, Boolean isNewEvent) {
		try {
			// Java Reflection: getClass()
			var method = getClass().getDeclaredMethod("apply", event.getClass());

			method.setAccessible(true);

			method.invoke(this, event);
		} catch(NoSuchMethodException e) {
			logger.log(
				Level.WARNING,
				MessageFormat.format(
					"the apply method wasn't found in the aggregate for {0}",
					event.getClass().getName()
				)
			);
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Error applying event to aggregate", e);
		} finally {
			if(isNewEvent) {
				changes.add(event);
			}
		}
	}

	public void raiseEvent(BaseEvent event) {
		applyChange(event, true);
	}

	public void replayEvents(Iterable<BaseEvent> events) {
		// events that aren't new will be used to recreate the state of the Aggregate
		events.forEach(event -> applyChange(event, false));
	}
}
