package com.chat.services;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Path("chat")
public class ChatServiceResource {
    class Message {
        String id;
        String message;
        Message next;
    }

    protected Message first;
    protected Message last;
    protected int maxMessages = 10;
    protected LinkedHashMap<String, Message> messages = new LinkedHashMap<String, Message>() {
        //This method is invoked by put and putAll after inserting a new entry into the map.
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Message> eldest) {
            boolean remove = size() > maxMessages;
            if (remove) first = eldest.getValue();
            return remove;




        }
    };



    protected AtomicLong counter = new AtomicLong(0);

    LinkedList<AsyncResponse> listeners = new LinkedList<AsyncResponse>();

    ExecutorService writer = Executors.newSingleThreadExecutor();

    public void saveMessages() throws SQLException {

        Connection connect = null;

        String url = "jdbc:mysql://localhost:3306/mensajes";

        String username = "root";
        String password = "pass";

        try {


            connect = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established"+connect);

        } catch (SQLException ex) {
            System.out.println("in exec");
            System.out.println(ex.getMessage());
        }





    };

    @POST
    @Consumes("text/plain")
    public void post(final String text) {
        writer.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (messages) {
                    Message message = new Message();
                    message.id = Long.toString(counter.incrementAndGet());
                    message.message = text;


                    System.out.println("MENSAJE---------> "+text );



                    if (messages.size() == 0) {
                        first = message;
                    } else {
                        last.next = message;
                    }
                    messages.put(message.id, message);
                    last = message;

                    for (AsyncResponse async : listeners) {
                        try {
                            send(async, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    listeners.clear();
                }
            }
        });


        try {
            saveMessages();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    @GET
    public void receive(@QueryParam("current") String next, @Suspended AsyncResponse async) {

        Message message = null;
        synchronized (messages) {
            Message current = messages.get(next);
            if (current == null) message = first;
            else message = current.next;

            if (message == null) {
                queue(async);
            }
        }
        // do this outside of synchronized block to reduce lock hold time
        if (message != null) send(async, message);
    }

    protected void queue(AsyncResponse async) {
        listeners.add(async);
    }

    protected void send(AsyncResponse async, Message message) {

      String url =  "http://localhost:8080/AsyncRESTChat/chat?current=" +  message.id;

        Response response = Response.ok(message.message, MediaType.TEXT_PLAIN_TYPE).header("Location", url).build();
        async.resume(response);
    }
}
