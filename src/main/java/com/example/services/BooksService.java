package com.example.services;

import com.example.model.Book;
import com.example.model.Person;
import com.example.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    // Вывод всех книг (сервис)
    public List<Book> showAllBooks() {
        return booksRepository.findAll();
    }

    // Показ 1 книги (сервис)
    public Book showOneBook(int id) {
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }

    // Создание книги (сервис)
    @Transactional
    public void createBook(Book createdBook) {
        booksRepository.save(createdBook);
    }

    // Изменение данных книги
    @Transactional
    public void updateBook(int id, Book updatedBook) {
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    // Удаление книги (сервис)
    @Transactional
    public void deleteBook(int id) {
        booksRepository.deleteById(id);
    }

    // Поиск владельца книги (сервис)
    public Optional<Person> bookOwnerId(int id) {
        return booksRepository.findOwnerById(id);
    }

    // Назначение книги человеку (сервис)
    @Transactional
    public void appointBook(int id, Person selectedPerson) {
        booksRepository.appointBookById(selectedPerson, id);

        // Установит дату взятия книги
        Book appointedBook = booksRepository.getById(id);
        appointedBook.setTakenAt(LocalDate.now());
    }

    // Возврат книги обратно в библиотеку (сервис)
    @Transactional
    public void refundBook(int id) {
        booksRepository.refundBookById(id);

        Book refundedBook = booksRepository.getById(id);
        // Поле с датой взятия книги (БД) (Обнулит дату возвращения книги)
        refundedBook.setTakenAt(null);
    }

    // Пагинация книг (сервис)
    public List<Book> booksPagination(int page, int booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    // Сортировка книг по году (сервис)
    public List<Book> booksSortByYear(Boolean doSort) {
        if (doSort) return booksRepository.findAll(Sort.by("dateOfCreation"));
        else return booksRepository.findAll();
    }

    // Пагинация + сортировка (сервис)
    public List<Book> paginationAndSorting(int page, int booksPerPage, Boolean doSort) {
        if (doSort) return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("dateOfCreation"))).getContent();
        else return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    // Поиск книги по первым буквам (сервис)
    public List<Book> findBooks(String letters) {
        return booksRepository.findByNameStartingWith(letters);
    }

    // Проверка книги на просрочку (если книга не отдана в течение 10 дней)
    public Boolean isBookOutdated(int id) {
        Book checkBook = booksRepository.getById(id);

        // Создаем переменные для сравнения (время взятия книги и настоящее время)
        LocalDate bookTakeDate = checkBook.getTakenAt();
        LocalDate dateNow = LocalDate.now();

        // Если человек взял книгу более 10 дней назад, но так и не вернул, то возвращается true, иначе - false
        return bookTakeDate != null && ChronoUnit.DAYS.between(bookTakeDate, dateNow) > 10;
    }
}
