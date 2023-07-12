package com.example.controllers;

import com.example.model.Book;
import com.example.model.Person;
import com.example.services.BooksService;
import com.example.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    // Внедрение сервисов через конструктор
    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    // Главная страница с показом всех книг, есть 3 необязательных параметра (см. внизу)
    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) Boolean doSort,
                        Model model) {

        // Реализация пагинации + сортировки (передаются все 3 параметра)
        // TODO: для проверки http://localhost:8080/books?page=1&books_per_page=3&sort_by_year=true
        if (page != null && booksPerPage != null && doSort != null) {
            List<Book> paginatedAndSortedBooks = booksService.paginationAndSorting(page, booksPerPage, doSort);
            model.addAttribute("books", paginatedAndSortedBooks);
        }

        // Реализация пагинации: если в параметрах запроса передадутся page и booksPerPage, то будет использоваться if с методом пагинации из сервиса
        // TODO: для проверки пагинации http://localhost:8080/books?page=1&books_per_page=3
        else if (page != null && booksPerPage != null) {
            List<Book> paginatedBooks = booksService.booksPagination(page, booksPerPage);
            model.addAttribute("books", paginatedBooks);
        }

        // Реализация сортировки: если в параметрах запроса передастся sortByYear, то будет использоваться if с методом пагинации из сервиса
        // TODO: для проверки сортировки http://localhost:8080/books?sort_by_year=true
        else if (doSort != null && page == null && booksPerPage == null) {
            List<Book> sortedBooksByYear = booksService.booksSortByYear(doSort);
            model.addAttribute("books", sortedBooksByYear);
        }

        // Если запрос не содержит параметры, то используется обычный вывод всех людей на страницу
        else {
            List<Book> booksByDefault = booksService.showAllBooks();
            model.addAttribute("books", booksByDefault);
        }

        return "books/index";
    }

    // Страница с добавлением новой книги в БД
    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") Book book, BindingResult bindingResult) {

        // bookValidator.validate(book, bindingResult);

        if (bindingResult.hasErrors()) return "books/new";
        booksService.createBook(book);
        return "redirect:/books";
    }

    // Страница с просмотром информации о книге + проверка, есть ли владелец у книги
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model,
                       @ModelAttribute("person") Person person) {

        model.addAttribute("book", booksService.showOneBook(id));

        // Проверка на владельца книги (если владелец существует, то передаём атрибут класса Person в модель,
        // иначе передаём список со всеми людьми, чтобы назначить книгу)
        Optional<Person> bookOwner = booksService.bookOwnerId(id);

        if (bookOwner.isPresent()) {
            model.addAttribute("hasOwner", bookOwner.get());
        }

        else {
            model.addAttribute("people", peopleService.showAllPeople());
        }

        return "books/show";
    }

    // Страница редактирования данных о книге
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", booksService.showOneBook(id));
        return "books/edit";
    }

    // Метод для кнопки "Редактировать данные о книге"
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        if (bindingResult.hasErrors()) return "books/edit";

        booksService.updateBook(id, book);
        return "redirect:/books";
    }

    // Метод для кнопки "Удалить книгу"
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.deleteBook(id);
        return "redirect:/books";
    }

    // Возврат книги
    @PatchMapping("/{id}/refundBook")
    public String refund(@PathVariable("id") int id) {
        booksService.refundBook(id);
        return "redirect:/books/" + id;
    }

    // Назначение книги человеку
    @PatchMapping("/{id}/appointBook")
    public String appoint(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        booksService.appointBook(id, selectedPerson);
        return "redirect:/books/" + id;
    }

    // Поиск книг по первым буквам
    // Делаем гет и пост маппинги, чтобы обойти блокировку метода POST
    @GetMapping("/search")
    public String searchBookForm() {
        return "books/search";
    }

    // При нажатии кнопки под формой, передает текст из формы как параметр в адресную строку (first_letters)
    @PostMapping("/search")
    public String searchBookSubmit(@RequestParam(value = "first_letters") String letters,
                                   @ModelAttribute("book") Book book, Model model) {
        List<Book> foundedBooks = booksService.findBooks(letters);
        model.addAttribute("booksFounded", foundedBooks);
        return "books/search";
    }
}
