package site.metecoding.junitproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import site.metecoding.junitproject.domain.Book;
import site.metecoding.junitproject.domain.BookRepository;
import site.metecoding.junitproject.web.dto.request.BookSaveReqDto;
import static org.assertj.core.api.Assertions.*;

// 통합테스트(C, S, R)
// 컨트롤러만 테스트하는 것이 아님
//https://seongjin.me/how-to-use-jsonpath-in-kubernetes/
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookApiControllerTest {
    @Autowired
    private TestRestTemplate rt;

    // 메인메서드가 실행되기전이 JVM이 start 될 때 최초로 단 1번 메모리에 해당 필드가 뜨는거
    private static ObjectMapper om;
    private static HttpHeaders headers;

    @Autowired // DI
    private BookRepository bookRepository;

    @BeforeAll
    public static  void init() {
        om = new ObjectMapper();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach // 각 테스트 시작전에 한번씩 실행
    public void 데이터준비() {
        String title = "junit";
        String author = "겟인데어";
        Book book = Book.builder()
                .title(title)
                .author(author)
                .build();
        bookRepository.save(book);
    }

    @Sql("classpath:db/tableInit.sql")
    @Test
    public void updateBook_test() throws Exception {
        // given
        Integer id = 1;
        BookSaveReqDto bookSaveReqDto = new BookSaveReqDto();
        bookSaveReqDto.setTitle("스프링1강");
        bookSaveReqDto.setAuthor("메타코딩");

        String body = om.writeValueAsString(bookSaveReqDto);

        // when
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = rt.exchange("/api/v1/book" + id, HttpMethod.PUT, request, String.class);


        // then
        // System.out.println("updateBook_test() : " + response.getStatusCodeValue());


        DocumentContext dc = JsonPath.parse(response.getBody());
        String title = dc.read("$.body.title");
        assertThat(title).isEqualTo("spring");

    }






    @Sql("classpath:db/tableInit.sql")
    @Test
    public void deleteBook_test() {

        // given
        Integer id = 1;

        // when
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = rt.exchange("/api/v1/book" + id, HttpMethod.GET, request, String.class);

        // then
//        System.out.println("deleteBook_test() : " + response.getStatusCodeValue());
        DocumentContext dc = JsonPath.parse(response.getBody());
        Integer code = dc.read("$.code");
        assertThat(code).isEqualTo(1);
    }


    @Sql("classpath:db/tableInit.sql")
    @Test
    public void getBookOne_test() { // 1. getBookOne_test 시작전에 BeforeEach를 시작하는데 !!! 이 모든 것 전에 테이블을 초기화를 한번 함.
        // given
        Integer id = 1;

        // when
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = rt.exchange("/api/v1/book" + id, HttpMethod.GET, request, String.class);

        // then
        DocumentContext dc = JsonPath.parse(response.getBody());
        Integer code = dc.read("$.code");
        String title = dc.read("$.body.items[0].title");

        assertThat(code).isEqualTo(1);
        assertThat(title).isEqualTo("junit");
    }

    @Sql("classpath:db/tableInit.sql")
    @Test
    public void getBookList_test() {

        // given

        // when
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = rt.exchange("/api/v1/book", HttpMethod.GET, request, String.class);


//        System.out.println("==============================");
//        System.out.println(response.getBody());
//        System.out.println("==============================");

        // then
        // 34강 - 컨트롤러 레이어 테스트 - 책목록보기 완료 Error
        DocumentContext dc = JsonPath.parse(response.getBody());
        Integer code = dc.read("$.code");
        String title = dc.read("$.body.items[0].title");

        assertThat(code).isEqualTo(1);
        assertThat(title).isEqualTo("junit");


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
