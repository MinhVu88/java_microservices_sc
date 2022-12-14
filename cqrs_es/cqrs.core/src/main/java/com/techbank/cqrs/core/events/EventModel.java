package com.techbank.cqrs.core.events;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "eventStore") // tells Spring that EventModel can be persisted to MongoDB
public class EventModel {
	@Id
	private String id;
	private Date timeStamp;
	private String aggregateIdentifier;
	private String aggregateType;
	private int version;
	private String eventType;
	private BaseEvent eventData;
}
