package cz.serachtool.controller;

import cz.serachtool.dto.Item;
import cz.serachtool.service.SearchService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {


    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }


    @GetMapping
    public List<Item> getResult(@RequestBody String query) throws IOException {
        return service.getResults(query);

    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody String query) throws IOException{
        return service.downloadResults(query);
    }
}
