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
    private List<String> quotes, comments;
    private static Comparator TIME_COMPARATOR = new SortByTime();
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
                long tstamp = 0;
                try{
                    tstamp = (long)entity.getProperty("tstamp");
                }catch(NullPointerException ex){
                }
                CommentDisplayer comment = new CommentDisplayer(databaseComment, databaseTimeStamp,false,null,tstamp);
                display.add(comment);
            }
            for(Entity entity : loginResult.asIterable()) {
                String databaseComment = (String) entity.getProperty("comments");
                String databaseTimeStamp = (String)entity.getProperty("timestamp");
                String databaseEmail = (String)entity.getProperty("email");
                long tstamp = 0;
                try{
                    tstamp = (long)entity.getProperty("tstamp");
                }catch(NullPointerException ex){
                }
                CommentDisplayer comment = new CommentDisplayer(databaseComment, databaseTimeStamp, true, databaseEmail,tstamp);
                display.add(comment);
            }
            Collections.sort(display,TIME_COMPARATOR);
            for (CommentDisplayer comment : display) {
                response.getWriter().println("<div class='comment-container'>"+comment+"</div>");
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
        long tStamp = System.currentTimeMillis();
        
        if(inputComment.trim().length() != 0){
            if (userService.isUserLoggedIn()) {
                Entity loginCommentsEntity = new Entity("Login-Comment");
                String timestamp = LocalDateTime.now().format(FORMAT).toString();
                String userEmail = userService.getCurrentUser().getEmail();
                loginCommentsEntity.setProperty("email", userEmail);
                loginCommentsEntity.setProperty("comments", inputComment);
                loginCommentsEntity.setProperty("timestamp", timestamp);
                loginCommentsEntity.setProperty("tstamp",tStamp);
                datastore.put(loginCommentsEntity);
            } else {
                Entity commentsEntity = new Entity("Comments");
                String timestamp = LocalDateTime.now().format(FORMAT).toString();
                commentsEntity.setProperty("comments", inputComment);
                commentsEntity.setProperty("timestamp", timestamp);
                commentsEntity.setProperty("tstamp", tStamp);
                datastore.put(commentsEntity);
            }
        }
        // ****************Redirect to main page ****************
        response.sendRedirect("index.html");

    }

    private static class SortByTime implements Comparator<CommentDisplayer> {
        public int compare(CommentDisplayer c1, CommentDisplayer c2) {
            return Long.compare(c2.getTimeStamp(),c1.getTimeStamp());
        }
    }
}

final class CommentDisplayer implements Comparable<CommentDisplayer> {

    private final String comment;
    private final String timestamp;
    private final boolean login_flag;
    private final String login_email;
    private long tstamp = 0;
    private final int sig_length = 68;
    private final int timestamp_length=37;
    public CommentDisplayer(String comment, String timestamp, boolean login_flag, String login_email, long tstamp) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.login_flag = login_flag;
        this.login_email = login_flag ? login_email:"Anonymous";
        this.tstamp = tstamp;
    }

    public String getComment() {
        return comment;
    }

    public long getTimeStamp() {
        return tstamp;
    }

    @Override
    public String toString() {
        return "<div class='comment'>"+comment+"</div>"+"<br>"+format();
    }

    @Override
    public int compareTo(CommentDisplayer other) {
        return this.comment.compareTo(other.comment);
    }

    public String format(){
        if(!login_email.equals("Anonymous")){
            int lengthOfEmail = login_email.length();
            int numberOf_ = sig_length - lengthOfEmail - timestamp_length;
            String toAdd = "";
            for(int i = 0; i < numberOf_; i++){
                toAdd+="_";
            }
            toAdd+="on ";
            return login_email+toAdd+timestamp;
        }else{
            return login_email+"______________________on "+timestamp;
        }
    }
}


 
