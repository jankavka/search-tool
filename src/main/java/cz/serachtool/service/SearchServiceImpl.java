package cz.serachtool.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.serachtool.dto.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${google.api-key}")
    private String apiKey;

    @Value("${google.cx}")
    private String cx;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();

    private List<Item> results;


    public List<Item> getResults() {
        return results;
    }

    public void setResults(List<Item> results) {
        this.results = results;
    }

    @Override
    public synchronized List<Item> getResults(String query) throws IOException {

        //Url with api key and cx
        var urlString = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + cx + "&q=";

        //config for not throwing exception while not reading all attributes of incoming object
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Response entity with results as String
        var response = restTemplate.getForEntity(urlString + query, String.class);

        //Items as JsonNode
        var itemsNode = objectMapper.readTree(response.getBody()).path("items");

        //returned final object as list of items
        return objectMapper.readValue(itemsNode.toString(), new TypeReference<List<Item>>() {
        });


    }

    @Override
    public synchronized ResponseEntity<byte[]> downloadResults(String query) throws IOException {
        var queryForFileName = "";
        if (query.contains(" ")) {
            queryForFileName = query.replace(" ", "_");
            queryForFileName = queryForFileName.trim();
        } else {
            queryForFileName = query;
        }
        var file = new File(queryForFileName + "_" + "search-results.json");

        var path = Path.of(file.getPath());

        try {

            objectMapper.writeValue(file, getResults(query));

            byte[] bytes = Files.readAllBytes(path);

            System.out.println(file.getName());

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(bytes);


        } finally {
            Files.delete(path);
        }


    }


}
