package com.observeinc.datadog.events;

import com.datadog.api.client.ServerConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.ApiException;
import com.datadog.api.client.v1.api.EventsApi;
import com.datadog.api.client.v1.model.EventCreateRequest;
import com.datadog.api.client.v1.model.EventCreateResponse;

import java.util.Collections;

@SpringBootApplication
@RestController
public class DatadogEventsApiToObserveApplication
{
	private final static String OBSERVE_CUSTOMER_ID = "REPLACE ME";
	private final static String OBSERVE_TOKEN_VALUE = "REPLACE ME";

	private final static ApiClient client = ApiClient.getDefaultApiClient();
	private static EventsApi events;

	public static void main(String[] args)
	{
		// Configure sending events to Observe
		client.setDebugging(true);
		ServerConfiguration sc = new ServerConfiguration(
				"https://" + OBSERVE_CUSTOMER_ID + ".collect.observeinc.com/v1/http",
				"Observe",
				Collections.emptyMap()
		);
		client.setServers(Collections.singletonList(sc));

		// Set Observe token
		client.addDefaultHeader("Authorization", "Bearer " + OBSERVE_TOKEN_VALUE);

		// Instantiate the events client
		events = new EventsApi(client);

		SpringApplication.run(DatadogEventsApiToObserveApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name)
	{
		EventCreateRequest body =
				new EventCreateRequest()
						.title("This is the title of my event")
						.text("Pickle Rick")
						.host("indiana")
						.tags(Collections.singletonList("dev:Howdy folks"));

		try {
			EventCreateResponse result = events.createEvent(body);
			System.out.println(result);
		}
		catch (ApiException e) {
			System.err.println("Exception when calling EventsApi#createEvent");
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getResponseBody());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}

		return String.format("Hello %s!", name);
	}
}
