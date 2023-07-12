package com.example.services;

import com.example.model.Book;
import com.example.model.Person;
import com.example.repositories.BooksRepository;
import com.example.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final BooksRepository booksRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, BooksRepository booksRepository) {
        this.peopleRepository = peopleRepository;
        this.booksRepository = booksRepository;
    }

    // Показ всех людей на /people
    public List<Person> showAllPeople() {
        return peopleRepository.findAll();
    }

    // Показ информации о человеке (сервис)
    public Person showOnePerson(int id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElse(null);
    }

    // Создание человека (сервис)
    @Transactional
    public void createPerson(Person createdPerson) {
        peopleRepository.save(createdPerson);
    }

    // Изменение данных о человеке (сервис)
    @Transactional
    public void updatePerson(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    // Удаление человека (сервис)
    @Transactional
    public void deletePerson(int id) {
        peopleRepository.deleteById(id);
    }

    // Показывает все книги, которые есть у человека на данный момент (сервис)
    public List<Book> availableBooks(int id) {
        return booksRepository.findBooksByOwnerId(id);
    }
}
