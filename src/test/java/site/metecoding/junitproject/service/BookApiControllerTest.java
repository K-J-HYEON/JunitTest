package site.metecoding.junitproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import site.metecoding.junitproject.web.dto.request.BookSaveReqDto;
import static org.assertj.core.api.Assertions.*;

// 통합테스트(C, S, R)
// 컨트롤러만 테스트하는 것이 아님
//https://seongjin.me/how-to-use-jsonpath-in-kubernetes/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookApiControllerTest {
    @Autowired
    private TestRestTemplate rt;

    // 메인메서드가 실행되기전이 JVM이 start 될 때 최초로 단 1번 메모리에 해당 필드가 뜨는거
    private static ObjectMapper om;
    private static HttpHeaders headers;

    @BeforeAll
    public static  void init() {
        om = new ObjectMapper();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void saveBook_test() throws Exception {
        // given
        BookSaveReqDto bookSaveReqDto = new BookSaveReqDto();
        bookSaveReqDto.setTitle("스프링1강");
        bookSaveReqDto.setAuthor("겟인데어");

        String body = om.writeValueAsString(bookSaveReqDto);

        // when
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt.exchange("/api/v1/book", HttpMethod.POST, request, String.class);

        // then
        DocumentContext dc = JsonPath.parse(response.getBody());
        String title = dc.read("$.body.title");
        String author = dc.read("$.body.author");

        assertThat(title).isEqualTo("스프링1강");
        assertThat(author).isEqualTo("겟인데어");

    }
}
