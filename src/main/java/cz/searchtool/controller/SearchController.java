package cz.searchtool.controller;

import cz.searchtool.dto.Item;
import cz.searchtool.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }


    @GetMapping
    public List<Item> getResult(
            @RequestParam("query") @NotBlank @Size(max = 200) String query) throws IOException {
        return searchService.getResults(query);

    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam("query") @NotBlank @Size(max = 200) String query) throws IOException {
        return searchService.downloadResults(query);
    }
}
