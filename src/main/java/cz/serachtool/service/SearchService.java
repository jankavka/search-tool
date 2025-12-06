package cz.serachtool.service;

import cz.serachtool.dto.Item;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface SearchService {

    List<Item> getResults(String query) throws IOException;

    ResponseEntity<byte[]> downloadResults(String query) throws IOException;
}
