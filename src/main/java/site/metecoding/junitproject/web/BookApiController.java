package site.metecoding.junitproject.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.metecoding.junitproject.service.BookService;
import site.metecoding.junitproject.web.dto.response.BookRespDto;
import site.metecoding.junitproject.web.dto.request.BookSaveReqDto;
import site.metecoding.junitproject.web.dto.response.CMSRespDto;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

// @RequiredArgsConstructor 해줘서 IoC 컨테이너에 있는 bookService를 DI 해준다.
// 25강 시작
@RequiredArgsConstructor
@RestController
public class BookApiController { // 컴포지션 = has 관계

    private final BookService bookService;

    // 1. 책등록
    // { "key": value, "key": value}
    @PostMapping("/api/v1/book")
    public ResponseEntity<?> saveBook(@RequestBody @Valid BookSaveReqDto bookSaveReqDto, BindingResult bindingResult) {
        BookRespDto bookRespDto = bookService.책등록하기(bookSaveReqDto);
        CMSRespDto<?> cmsRespDto = CMSRespDto.builder().code(1).msg("글 저장 성공").body(bookRespDto).build();

        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fer : bindingResult.getFieldErrors()) {
                errorMap.put(fer.getField(), fer.getDefaultMessage());
            }
            System.out.println("=======================================");
            System.out.println(bindingResult.hasErrors());
            System.out.println("=======================================");


        }
        return new ResponseEntity<>(cmsRespDto, HttpStatus.CREATED); // 201 = insert
    }

    // 2, 책목록보기
    public ResponseEntity<?> getBookList() {
        return null;
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