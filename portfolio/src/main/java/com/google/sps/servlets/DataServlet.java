// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Servlet that returns some example content. TODO: modify this file to handle
 * comments data
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private static int TIME_VISTED = 0;
    private List<String> quotes, comments;
    private static Comparator LENGTH_COMPARATOR = new SortByLength();
    private static final DateTimeFormatter FORMAT = DateTimeFormatter
            .ofPattern("EEEE, LLLL/dd/YYYY 'at' HH:mm:ss a");


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
            //Below is for data not associate with email
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Query query = new Query("Comments");
            Query loginQuery = new Query("Login-Comment");
            PreparedQuery results = datastore.prepare(query);
            PreparedQuery loginResult = datastore.prepare(loginQuery);

            ArrayList<CommentDisplayer> display = new ArrayList<>();
            for(Entity entity : results.asIterable()) {
                String databaseComment = (String) entity.getProperty("comments");
                String databaseTimeStamp = (String)entity.getProperty("timestamp");
                CommentDisplayer comment = new CommentDisplayer(databaseComment, databaseTimeStamp,false,null);
                display.add(comment);
            }
            for(Entity entity : loginResult.asIterable()) {
                String databaseComment = (String) entity.getProperty("comments");
                String databaseTimeStamp = (String)entity.getProperty("timestamp");
                String databaseEmail = (String)entity.getProperty("email");
                CommentDisplayer comment = new CommentDisplayer(databaseComment, databaseTimeStamp, true, databaseEmail);
                display.add(comment);
            }
            for (CommentDisplayer comment : display) {
                response.getWriter().println(comment);
            }    
        }

    private String toJason(ArrayList<String> list) {
        String jason = "{";
        for (int i = 0; i < list.size(); i++) {
            jason += "\"quote" + i + "\"+: ";
            jason += list.get(i);
            if (i != list.size() - 1) {
                jason += ",";
            }
        }
        jason += "}";
        return jason;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        String inputComment = request.getParameter("text-input");
        // *****Below is use for putting data to data bases
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<CommentDisplayer> display = new ArrayList<>();

        if (userService.isUserLoggedIn()) {
            Entity loginCommentsEntity = new Entity("Login-Comment");
            String timestamp = LocalDateTime.now().format(FORMAT).toString();
            String userEmail = userService.getCurrentUser().getEmail();
            loginCommentsEntity.setProperty("email", userEmail);
            loginCommentsEntity.setProperty("comments", inputComment);
            loginCommentsEntity.setProperty("timestamp", timestamp);
            datastore.put(loginCommentsEntity);
            /*
            Query loginQuery = new Query("Login-Comment");
            PreparedQuery loginResult = datastore.prepare(loginQuery);
            for(Entity entity : loginResult.asIterable()) {
                String databaseComment = (String) entity.getProperty("comments");
                String databaseTimeStamp = (String)entity.getProperty("timestamp");
                String databaseEmail = (String)entity.getProperty("email");
                CommentDisplayer comment = new CommentDisplayer(databaseComment, databaseTimeStamp, true, databaseEmail);
                display.add(comment);
            }*/
        } else {
            Entity commentsEntity = new Entity("Comments");
            String timestamp = LocalDateTime.now().format(FORMAT).toString();
            commentsEntity.setProperty("comments", inputComment);
            commentsEntity.setProperty("timestamp", timestamp);
            datastore.put(commentsEntity);
        }
        // *** Below is printing to the page
        /*
        if (Boolean.parseBoolean(request.getParameter("sort-by-length"))) {
            Collections.sort(display, LENGTH_COMPARATOR);
        } else {
            Collections.sort(display);
        }
        */
        
        // ****************Redirect to main page ****************
        response.sendRedirect("index.html");

    }

    private void printComments(List<CommentDisplayer> li, HttpServletResponse response) throws IOException {
        response.setContentType("head/html");
        response.getWriter().println("Comments Added!");
        response.setContentType("text/html");
        for (CommentDisplayer comments : li) {
            response.getWriter().println(comments);
        }
    }

    private static class SortByLength implements Comparator<CommentDisplayer> {
        public int compare(CommentDisplayer c1, CommentDisplayer c2) {
            return Integer.compare(c1.getComment().length(), c2.getComment().length());
        }
    }
}

final class CommentDisplayer implements Comparable<CommentDisplayer> {

    private final String comment;
    private final String timestamp;
    private final boolean login_flag;
    private final String login_email;

    public CommentDisplayer(String comment, String timestamp, boolean login_flag, String login_email) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.login_flag = login_flag;
        this.login_email = login_flag ? login_email:"Anonymous";
    }

    public String getComment() {
        return comment;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return comment+"\n"+login_email+"______________________on " + timestamp;
    }

    @Override
    public int compareTo(CommentDisplayer other) {
        return this.comment.compareTo(other.comment);
    }
}


 
