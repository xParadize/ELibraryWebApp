package com.example.repositories;

import com.example.model.Book;;
import com.example.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    // Поиск владельца книги (репозиторий)
    @Query("SELECT b.owner FROM Book b WHERE b.id = :id")
    Optional<Person> findOwnerById(@Param("id") int id);

    // Поиск книг по владельцу (репозиторий)
    List<Book> findBooksByOwnerId(int id);

    // Назначение книги человеку (репозиторий)
    @Modifying // говорим hibernate, что данный метод будет изменять БД
    @Query("UPDATE Book b SET b.owner = ?1 WHERE b.id = ?2")
    void appointBookById(Person person, int id);

    // Возврат книги обратно в библиотеку (репозиторий)
    @Modifying
    @Query("UPDATE Book b SET b.owner = null WHERE b.id = ?1")
    void refundBookById(int id);

    // Пагинация книг (репозиторий)
    // Метод findAll() в JPA репозитории перегружен и может принимать разные аргументы. В частности, одна из версий этого метода может принимать Pageable
    Page<Book> findAll(Pageable var1);

    // Сортировка книг по году (репозиторий)
    List<Book> findAll(Sort var1);

    // Поиск книги по первым буквам (репозиторий)
    List<Book> findByNameStartingWith(String letters);
}
