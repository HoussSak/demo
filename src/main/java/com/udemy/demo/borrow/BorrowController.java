package com.udemy.demo.borrow;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class BorrowController {

    @GetMapping("/borrows")
    public ResponseEntity getMyBorrows() {

        //TODO
        Borrow borrow = new Borrow();
        borrow.setAskDate(LocalDate.now());

        return new ResponseEntity(borrow, HttpStatus.OK);
    }


    @PostMapping("/borrows/{bookId}")
    public ResponseEntity createBorrow(@PathVariable("bookId") String bookId) {
        //TODO
        Borrow borrow = new Borrow();
        borrow.setAskDate(LocalDate.now());

        return new ResponseEntity(borrow, HttpStatus.CREATED);
    }

    @DeleteMapping("/borrows/{bookId}")
    public ResponseEntity deleteBorrow(@PathVariable("bookId") String bookId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
