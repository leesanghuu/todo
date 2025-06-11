package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoViewController {

    @GetMapping("/todos")
    public String showTodoList() {
        return "forward:/todo-list.html";
    }
}
