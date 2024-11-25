package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final PageableHandlerMethodArgumentResolver pageableResolver;
    private final UserRepository userRepository;

    @Transactional
    public TodoSaveResponse saveTodo(String username, TodoSaveRequest todoSaveRequest) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String weather = weatherClient.getTodayWeather();

        Todo todo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                todoSaveRequest.getWeather(),
                user
        );
        Todo savedTodo = todoRepository.save(todo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getNickname(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDateTime startDate, LocalDateTime endDate) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findByConditions(weather, startDate, endDate, pageRequest);

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getNickname(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getNickname(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
