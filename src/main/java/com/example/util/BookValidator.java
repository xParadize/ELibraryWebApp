//package com.example.util;
//
//import com.example.dao.BookDAO;
//import com.example.model.Book;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//@Component
//public class BookValidator implements Validator {
//
//    private final BookDAO bookDAO;
//
//    @Autowired
//    public BookValidator(BookDAO bookDAO) {
//        this.bookDAO = bookDAO;
//    }
//
//    // Для какого класса используется валидация
//    @Override
//    public boolean supports(Class<?> aClass) {
//        return Book.class.equals(aClass);
//    }
//
//    @Override
//    public void validate(Object o, Errors errors) {
//        Book book = (Book) o;
//
//        // Валидация на уникальность названия книги (book_title) (чтобы не вылетала 500 ошибка SQL)
//        if (bookDAO.show(book.getBook_title()).isPresent()) {
//            errors.rejectValue("book_title", "", "Книга с таким названием уже существует"); // <На какое поле пришла ошибка> <опц. код ошибки> <сообщение ошибки>
//        }
//    }
//}
