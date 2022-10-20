package com.udemy.demo.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Arrays;

@RestController
public class BookController {


    @GetMapping("/books")
    public ResponseEntity listBooks() {

        Book book = new Book();

        book.setTitle("MyBook");
        book.setCategory(new Category("BD"));

        return  new ResponseEntity(Arrays.asList(book), HttpStatus.OK);

    }
    @PostMapping("/books")
    public ResponseEntity addBook(@Valid @RequestBody Book book) {
        //TODO persist
        return  new ResponseEntity(book,HttpStatus.CREATED);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity deleteBook(@PathVariable("{bookId}") String bookId) {
        //TODO delete bookId
        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/books/{bookId}")
    public ResponseEntity updateBook(@PathVariable("{bookId}") String bookId,@Valid @RequestBody Book book) {
        //TODO delete bookId
        return  new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity listCategories() {

        Category category1 = new Category("BD");
        Category category2 = new Category("Roman");

        return  new ResponseEntity(Arrays.asList(category1,category2), HttpStatus.OK);

    }






}
