package com.udemy.demo.book;

import com.udemy.demo.borrow.Borrow;
import com.udemy.demo.borrow.BorrowRepository;
import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BorrowRepository borrowRepository;



    @GetMapping("/books")
    public ResponseEntity listBooks(@RequestParam(required = false) BookStatus status,Principal principal) {

        Integer userConnected = getUserConnectedId(principal);

        List<Book> books;

        if (status != null && status == BookStatus.FREE) {

            //Free Books
            books =  bookRepository.findByStatusAndUserIdNotAndDeletedFalse(status,userConnected);
        } else {

            books = bookRepository.findByUserIdAndDeletedFalse(userConnected);
        }
        //My Books

        return  new ResponseEntity(books, HttpStatus.OK);

    }



    @PostMapping("/books")
    public ResponseEntity addBook(@Valid @RequestBody Book book,Principal principal) {

        Integer userConnected = getUserConnectedId(principal);

        Optional<UserInfo> user = userRepository.findById(userConnected);

        Optional<Category> category =  categoryRepository.findById(book.getCategoryId());

        if (category.isPresent()) {
            book.setCategory(category.get());
        } else {
            return  new ResponseEntity("You must provide a valid category",HttpStatus.BAD_REQUEST);
        }
        if (user.isPresent()) {
            book.setUser(user.get());
        } else {
            return  new ResponseEntity("You must provide a valid user",HttpStatus.BAD_REQUEST);
        }

        book.setDeleted(false);
        book.setStatus(BookStatus.FREE);
        bookRepository.save(book);
        return  new ResponseEntity(book,HttpStatus.CREATED);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity deleteBook(@PathVariable("{bookId}") String bookId) {

        Optional<Book> bookToDelete = bookRepository.findById(Integer.valueOf(bookId));

        if (!bookToDelete.isPresent()) {
            return new ResponseEntity("Book not found",HttpStatus.BAD_REQUEST);
        }

        Book book = bookToDelete.get();

       List<Borrow> borrows =  borrowRepository.findByBookId(book.getId());

       for (Borrow borrow : borrows) {
           if (borrow.getCloseDate() == null) {
               UserInfo borrower = borrow.getBorrower();
               return  new ResponseEntity(borrower,HttpStatus.CONFLICT);
           }
       }
       book.setDeleted(true);
       bookRepository.save(book);

        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/books/{bookId}")
    public ResponseEntity updateBook(@PathVariable("{bookId}") String bookId,@Valid @RequestBody Book book) {

       Optional<Book> bookToUpdate =  bookRepository.findById(Integer.valueOf(bookId));

       if(! bookToUpdate.isPresent()) {
           return new ResponseEntity<>("Book does'nt exist",HttpStatus.BAD_REQUEST);
       }
       Book bookToSave = bookToUpdate.get();

       Optional<Category> newCategory =  categoryRepository.findById(book.getCategoryId());

       bookToSave.setCategory(newCategory.get());
       bookToSave.setTitle(book.getTitle());
       bookRepository.save(bookToSave);

       return new ResponseEntity<>(bookToSave,HttpStatus.OK);

    }

    @GetMapping("/categories")
    public ResponseEntity listCategories() {return  new ResponseEntity(categoryRepository.findAll(), HttpStatus.OK);}

    public  Integer getUserConnectedId(Principal principal) {
        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            throw new RuntimeException("User not found");
        }

        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
        UserInfo onebYEmail = userRepository.findOneByEmail(user.getName());

        return onebYEmail.getId();
    }


    @GetMapping("/books/{bookId}")
    public ResponseEntity loadBook(@PathVariable String bookId ) {
       Optional<Book> book =  bookRepository.findById(Integer.valueOf(bookId));
        if (!book.isPresent()) {
            return new ResponseEntity<>("Book not found",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(book,HttpStatus.OK);
    }






}
