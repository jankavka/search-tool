package cz.searchtool.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.searchtool.dto.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.util.List;


@Slf4j
@Setter
@Getter
@Service
public class SearchServiceImpl implements SearchService {

    @Value("${google.api-key}")
    private String apiKey;

    @Value("${google.cx}")
    private String cx;

    @Value("${google.url-prefix}")
    private String urlPrefix;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public SearchServiceImpl(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;

    }

    /**
     * Method calls Google custom search API with query param and also with generated api key and
     * cx. Then with using RestTemplate result data as String object is fetched from GSC API and then
     * mapped with using ObjectMapper to JsonNone, based on path "items", and then to List<Item> object.
     *
     * @param query String which contains subject of searching
     * @return First page of returned list of items
     * @throws IOException when there error appears during I/O operation
     */
    @Override
    public synchronized List<Item> getResults(String query) throws IOException {

        //Url with api key and cx
        var finalUrlString = urlPrefix + "key=" + apiKey + "&cx=" + cx + "&q=";

        //config for not throwing exception while not reading all attributes of incoming object
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Response entity with results as String
        var response = restTemplate.getForEntity(finalUrlString + query, String.class);

        //Items as JsonNode
        var itemsNode = objectMapper.readTree(response.getBody()).path("items");

        //returned final object as list of items
        return objectMapper.readValue(itemsNode.toString(), new TypeReference<List<Item>>() {
        });

    }

    /**
     * Method creates temporary file, then writes returned values of Google search API
     * call to the file and returns ResponseEntity with file content as byte array.
     *
     * @param query String which contains subject of searching
     * @return Response entity with file content as body
     * @throws IOException when there error appears during I/O operation
     */
    @Override
    public synchronized ResponseEntity<byte[]> downloadResults(String query) throws IOException {

        //Replacing whitespaces
        var queryForFileName = "";
        if (query.contains(" ")) {
            queryForFileName = query.replace(" ", "_");
            queryForFileName = queryForFileName.trim();
        } else {
            queryForFileName = query;
        }

        //Temporary file where search results will be written
        var path = Files.createTempFile(queryForFileName + "_search-results", ".json");

        //Creates File instance of created temp file
        var file = new File(path.toString());

        //Writing results to temporary file
        objectMapper.writeValue(file, getResults(query));

        log.info("Current search result file name: {}", file.getName());

        //Parsing file to byte array
        byte[] bytes = Files.readAllBytes(path);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .body(bytes);


    }


}



