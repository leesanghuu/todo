package com.example.todo.repository;

import com.example.todo.dto.TodoResponseDto;
import com.example.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {


    List<Todo> findByDate(LocalDate date);

    List<Todo> findByDateBetween(LocalDate dateAfter, LocalDate dateBefore);

    List<Todo> findByDateAndCompletedFalse(LocalDate date);

    List<Todo> findByUserIdentifierAndDate(String userIdentifier, LocalDate date);

    List<Todo> findByUserIdentifier(String userIdentifier);

    List<Todo> findByUserIdentifierAndDateBetween(String userIdentifier, LocalDate dateAfter, LocalDate dateBefore);
}
