package io.covidTracker.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.covidTracker.coronavirustracker.models.LocationStats;

@Service
public class CoronaVirusDataService {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();
	
	@PostConstruct //Annotation basically instructs program to execute this method before class is put into service
	@Scheduled(cron = "* * 1 * * *") //making it run every hour 
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		
		//Getting the response from the request as a String and storing it
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(httpResponse.body());
		
		//Parsing data from the stored String
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
			
			//Getting data from the specific headers from the URL
			locationStat.setState(record.get("Province/State"));
			locationStat.setState(record.get("Country/Region"));
		}
	}
}
