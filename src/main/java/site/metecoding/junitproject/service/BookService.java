package site.metecoding.junitproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.metecoding.junitproject.domain.Book;
import site.metecoding.junitproject.domain.BookRepository;
import site.metecoding.junitproject.util.MailSender;
import site.metecoding.junitproject.web.dto.response.BookRespDto;
import site.metecoding.junitproject.web.dto.request.BookSaveReqDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final MailSender mailSender;

    // 1. 책등록
    @Transactional(rollbackFor = RuntimeException.class)
    public BookRespDto 책등록하기(BookSaveReqDto dto) {
        Book bookPS = bookRepository.save(dto.toEntity());
        if (bookPS != null) {
            if (!mailSender.send()) {
                throw new RuntimeException("메일이 전송되지 않았습니다.");
            }
        }
        return bookPS.toDto();
    }

    // 2, 책목록보기
    public List<BookRespDto> 책목록보기() {
        List<BookRespDto> dtos = bookRepository.findAll().stream()
                // .map((bookPS) -> bookPS.toDto())
                .map(Book::toDto)
                .collect(Collectors.toList());

        dtos.stream().forEach((b) -> {
            System.out.println(b.getId());
            System.out.println(b.getTitle());
            System.out.println("======= 서비스 레이어");
        });
        return dtos;
    }

    // 3. 책한건보기
    public BookRespDto 책한건보기(Long id) {
        Optional<Book> bookOP = bookRepository.findById(id);
        if (bookOP.isPresent()) {
            // 찾았다면
            Book bookPS = bookOP.get();
            return bookPS.toDto();
        } else {
            throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");
        }
    }

    // 4. 책삭제
    @Transactional(rollbackFor = RuntimeException.class)
    public void 책삭제하기(Long id) { // 4
        bookRepository.deleteById(id); // 1, 2, 3
    }

    // 5. 책수정
    @Transactional(rollbackFor = RuntimeException.class)
    public BookRespDto 책수정하기(Long id, BookSaveReqDto dto) { // id, title, author
        Optional<Book> bookOP = bookRepository.findById(id);
        if (bookOP.isPresent()) {
            Book bookPS = bookOP.get();
            bookPS.update(dto.getTitle(), dto.getAuthor());
            return bookPS.toDto();
        } else {
            throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");
        }
    } // 메서드 종료시에 더티체킹(flush)으로 update 됩니다.
}
