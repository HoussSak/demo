package com.udemy.demo.borrow;

import com.udemy.demo.book.Book;
import com.udemy.demo.book.BookController;
import com.udemy.demo.book.BookRepository;
import com.udemy.demo.book.BookStatus;
import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class BorrowController {
    @Autowired
    private BorrowRepository borrowRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookController bookController;


    @GetMapping("/borrows")
    public ResponseEntity getMyBorrows(Principal principal) {
       List<Borrow> borrows =  borrowRepository.findBorrowByBorrowerId(bookController.getUserConnectedId(principal));

        return new ResponseEntity(borrows, HttpStatus.OK);
    }


    @PostMapping("/borrows/{bookId}")
    public ResponseEntity createBorrow(@PathVariable("bookId") String bookId,Principal principal) {

        Integer userConnectedId = bookController.getUserConnectedId(principal);
        Optional<UserInfo> borrower = userRepository.findById(userConnectedId);

        Optional<Book> book = bookRepository.findById(Integer.valueOf(bookId));
        if (borrower.isPresent() && book.isPresent() && book.get().getStatus().equals(BookStatus.FREE)) {
            Borrow borrow = new Borrow();
            Book bookEntity = book.get();

            borrow.setBorrower(borrower.get());
            borrow.setLender(bookEntity.getUser());
            borrow.setBook(bookEntity);
            borrow.setAskDate(LocalDate.now());

            borrowRepository.save(borrow);
            bookEntity.setStatus(BookStatus.BORROWED);

            bookRepository.save(bookEntity);
            return new ResponseEntity(borrow, HttpStatus.CREATED);
        }

        return new ResponseEntity( HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/borrows/{borrowId}")
    public ResponseEntity deleteBorrow(@PathVariable  String borrowId) {

        Optional<Borrow> borrow = borrowRepository.findById(Integer.valueOf(borrowId));
        if (borrow.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Borrow borrowEntity = borrow.get();
        borrowEntity.setCloseDate(LocalDate.now());
        borrowRepository.save(borrowEntity);

        Book book = borrowEntity.getBook();
        book.setStatus(BookStatus.FREE);
        bookRepository.save(book);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
