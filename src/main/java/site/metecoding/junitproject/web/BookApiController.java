package site.metecoding.junitproject.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.metecoding.junitproject.service.BookService;
import site.metecoding.junitproject.web.dto.response.BookListRespDto;
import site.metecoding.junitproject.web.dto.response.BookRespDto;
import site.metecoding.junitproject.web.dto.request.BookSaveReqDto;
import site.metecoding.junitproject.web.dto.response.CMSRespDto;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @RequiredArgsConstructor 해줘서 IoC 컨테이너에 있는 bookService를 DI 해준다.
// 27강 시작
@RequiredArgsConstructor
@RestController
public class BookApiController { // 컴포지션 = has 관계

    private final BookService bookService;

    // 1. 책등록
    // { "key": value, "key": value}
    @PostMapping("/api/v1/book")
    public ResponseEntity<?> saveBook(@RequestBody @Valid BookSaveReqDto bookSaveReqDto, BindingResult bindingResult) {

        // AOP 처리하는 게 좋음!!
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            System.out.println("==================================");
            System.out.println(errorMap.toString());
            System.out.println("==================================");

            throw new RuntimeException(errorMap.toString());
        }

        BookRespDto bookRespDto = bookService.책등록하기(bookSaveReqDto);
        return new ResponseEntity<>(CMSRespDto.builder().code(1).msg("글 저장 성공").body(bookRespDto).build(),
                HttpStatus.CREATED); // 201 insert
    }

    @PostMapping("/api/v2/book")
    public ResponseEntity<?> saveBookV2(@RequestBody BookSaveReqDto bookSaveReqDto) {

        BookRespDto bookRespDto = bookService.책등록하기(bookSaveReqDto);
        return new ResponseEntity<>(CMSRespDto.builder().code(1).msg("글 저장 성공").body(bookRespDto).build(),
                HttpStatus.CREATED); // 201 insert
    }

    // 2, 책목록보기
    @GetMapping("/api/vi/book")
    public ResponseEntity<?> getBookList() {
        BookListRespDto bookListRespDto = bookService.책목록보기();
        return new ResponseEntity<>(CMSRespDto.builder().code(1).msg("글 목록보기 성공").body(bookListRespDto).build(),
                HttpStatus.OK); // 200 = ok;
    }

    // 3. 책한건보기
    public ResponseEntity<?> getBookOne() {
        return null;
    }

    // 4. 책삭제
    public ResponseEntity<?> deleteBook() {
        return null;
    }

    // 5. 책수정
    public ResponseEntity<?> updateBook() {
        return null;
    }
}