package cz.searchtool.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    RestTemplate restTemplate;

    ObjectMapper objectMapper;

    SearchServiceImpl service;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();
        service = new SearchServiceImpl(objectMapper,restTemplate);

        service.setApiKey("1234");
        service.setCx("abc");
        service.setUrlPrefix("http://www.abc.cz?");
    }

    @Test
    void shouldReturnListOfItems() throws IOException {
        String responseBody = """
                {
                  "items": [
                    { "title": "First result",  "link": "https://example.com/1", "snippet": "This is snippet" },
                    { "title": "Second result", "link": "https://example.com/2", "snippet": "This is also snippet"}
                  ]
                }
                """;

        when(restTemplate.getForEntity(any(URI.class), eq(String.class))).thenReturn(ResponseEntity.ok(responseBody));

        var result = service.getResults("java");

        assertNotNull(result);
        assertEquals("This is snippet", result.get(0).getSnippet());

        verify(restTemplate, times(1)).getForEntity(any(URI.class), eq(String.class));

    }

    @Test
    void shouldReturnEmptyList() throws IOException {
        String responseBody = """
                {
                    "items":[]
                }
                """;

        when(restTemplate.getForEntity(any(URI.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body(responseBody));

        var result = service.getResults("query");

        assertEquals(List.of(), result);
        verify(restTemplate, times(1)).getForEntity(any(URI.class), eq(String.class));
    }

    @Test
    void shouldReturnExceptionMessage() {

        when(restTemplate.getForEntity(any(URI.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class, () -> service.getResults("query"));

    }


    @Test
    void shouThrowIOException() {
        when(restTemplate.getForEntity(any(URI.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok().body("{ Invalid json"));

        var exception = assertThrows(IOException.class, () -> service.getResults("query"));

        assertEquals(JsonParseException.class, exception.getClass());

        assertInstanceOf(IOException.class, exception);
    }

    @Test
    void shouldReturnResponseEntityWithByteArray() throws IOException {
        var responseJson = """ 
                {
                      "items": [
                      { "title": "First result",  "link": "https://example.com/1", "snippet": "This is snippet" },
                      { "title": "Second result", "link": "https://example.com/2", "snippet": "This is also snippet"}
                       ]
                }
                """;

        var responseBytes =
                """
                        [{"title":"First result","link":"https://example.com/1","snippet":"This is snippet"},
                        {"title":"Second result","link":"https://example.com/2","snippet":"This is also snippet"}]""";

        byte[] bytes = responseBytes.getBytes();

        ResponseEntity<String> response = ResponseEntity.ok().body(responseJson);

        when(restTemplate.getForEntity(any(URI.class), eq(String.class))).thenReturn(response);

        var result = service.downloadResults("query");

        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaders().getContentType());
        assertInstanceOf(bytes.getClass(), result.getBody());


    }


}
