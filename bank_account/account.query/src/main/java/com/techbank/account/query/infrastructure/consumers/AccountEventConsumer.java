package com.techbank.account.query.infrastructure.consumers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.infrastructure.handlers.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class AccountEventConsumer implements EventConsumer {
	@Autowired
	private EventHandler eventHandler;

	@Override
	@KafkaListener(topics = "AccountOpenedEvent", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(AccountOpenedEvent event, Acknowledgment acknowledgment) {
		eventHandler.on(event);
		acknowledgment.acknowledge();
	}

	@Override
	@KafkaListener(topics = "FundsDepositedEvent", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(FundsDepositedEvent event, Acknowledgment acknowledgment) {
		eventHandler.on(event);
		acknowledgment.acknowledge();
	}

	@Override
	@KafkaListener(topics = "FundsWithdrawnEvent", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(FundsWithdrawnEvent event, Acknowledgment acknowledgment) {
		eventHandler.on(event);
		acknowledgment.acknowledge();
	}

	@Override
	@KafkaListener(topics = "AccountClosedEvent", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(AccountClosedEvent event, Acknowledgment acknowledgment) {
		eventHandler.on(event);
		acknowledgment.acknowledge();
	}
}
