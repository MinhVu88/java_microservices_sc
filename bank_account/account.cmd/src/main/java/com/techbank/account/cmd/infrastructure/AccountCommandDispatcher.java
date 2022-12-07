package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountCommandDispatcher implements CommandDispatcher {
	// the routes field represents a collection of registered command handler methods
	private final Map<
		Class<? extends BaseCommand>,
		List<CommandHandlerMethod>
	> routes = new HashMap<>();

	@Override
	public <T extends BaseCommand> void registerHandler(
		Class<T> type,
		CommandHandlerMethod<T> handler
	) {
		// the computeIfAbsent method computes a new value & associates it with
		// the specified key if the key isn't associated with any value in the HashMap yet.
		var handlers = routes.computeIfAbsent(
			type,
			c -> new LinkedList<>()
		);

		// The handlers variable is a list of command handler methods.
		// handlers.add() adds a command handler method to routes & that means
		// registering a new command handler, denoted by "Class<T> type", which is passed to registerHandler()
		handlers.add(handler);
	}

	// send() is a dispatch method that dispatches a given command to a registered command handler method
	@Override
	public void send(BaseCommand command) {
		var handlers = routes.get(command.getClass());

		// check if there's any command handler method registered for a given command type
		if(handlers == null || handlers.size() == 0) {
			throw new RuntimeException("no command handler was registered");
		}

		// check if there are duplicate command handler methods for the same command type
		if(handlers.size() > 1) {
			throw new RuntimeException("can't send a command to more than 1 handler");
		}

		handlers.get(0).handle(command);
	}
}
