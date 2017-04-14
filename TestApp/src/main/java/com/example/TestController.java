package com.example;

import java.io.IOException;
import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

@RestController
public class TestController {

	private final EurekaClient discoveryClient;
	private final RestTemplate restTemplate;

    public TestController(RestTemplate restTemplate, EurekaClient discoveryClient) {
    	this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;        
    }
    
    /*public TestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }*/
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
	public String getEmployee() throws RestClientException, IOException {

		
		String baseUrl = getGatewayAppUrl();
		baseUrl=baseUrl+"employeeconsumer/";
		
		//RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
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
	
	private String getGatewayAppUrl() {
        return discoveryClient
           .getNextServerFromEureka("gatewayapp", false)
           .getHomePageUrl();
    }
}
