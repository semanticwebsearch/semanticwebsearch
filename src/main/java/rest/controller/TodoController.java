package rest.controller;

import com.sun.jersey.api.core.InjectParam;
import rest.model.SearchData;
import rest.model.TodoModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Path("/todo")
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

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void getFormParams(@InjectParam SearchData s) {
       s.isMap();

    }
}
