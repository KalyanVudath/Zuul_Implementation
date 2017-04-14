package com.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ConsumerControllerClient {

	@Autowired
	private DiscoveryClient discoveryClient;
	
	private final RestTemplate restTemplate;
	
	public ConsumerControllerClient(RestTemplate restTemplate){
		this.restTemplate = restTemplate;
	}
	
	@HystrixCommand (fallbackMethod="noEmployeeMethod")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getEmployee() throws RestClientException, IOException {

		List<ServiceInstance> instances=discoveryClient.getInstances("employeeProducer");
		ServiceInstance serviceInstance=instances.get(0);		
		String baseUrl=serviceInstance.getUri().toString();		
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}		
		return response.getBody();
	}

	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
	
	public String noEmployeeMethod(){		
		return "Employee Producer Service is down";
	}
}