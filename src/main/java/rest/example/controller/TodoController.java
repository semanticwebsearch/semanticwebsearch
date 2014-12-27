package rest.example.controller;

import rest.example.model.TodoModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Path("/examples/todo")
public class TodoController {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Collection<TodoModel> getJson() {
        TodoModel todo = new TodoModel();
        todo.setSummary("This is my first todo");
        todo.setDescription("This is my first todo");
        List<TodoModel> todos = new LinkedList<>();
        todos.add(todo);
        todos.add(todo);
        return todos;
    }
}
