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


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static int TIME_VISTED = 0;
  private List<String> quotes, comments;
  private static Comparator TIME_COMPARATOR = new SortByTime();
  private static Comparator LENGTH_COMPARATOR = new SortByLength();
  
  @Override
  public void init() {
    quotes = new ArrayList<String>();
    quotes.add(
        "A ship in port is safe, but that is not what ships are for. "
            + "Sail out to sea and do new things. - Grace Hopper");
    quotes.add("They told me computers could only do arithmetic. - Grace Hopper");
    quotes.add("A ship in port is safe, but that's not what ships are built for. - Grace Hopper");
    quotes.add("It is much easier to apologise than it is to get permission. - Grace Hopper");
    quotes.add("If you can't give me poetry, can't you give me poetical science? - Ada Lovelace");
    quotes.add("I am in a charming state of confusion. - Ada Lovelace");
    quotes.add(
        "The Analytical Engine weaves algebraic patterns, "
            + "just as the Jacquard loom weaves flowers and leaves. - Ada Lovelace");
    quotes.add(
        "Sometimes it is the people no one can imagine anything of "
            + "who do the things no one can imagine. - Alan Turing");
    quotes.add("Those who can imagine anything, can create the impossible. - Alan Turing");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //response.setContentType("text/html;");
    //response.getWriter().println("<h1>Hello Dequan!</h1>");
    //DataServlet.TIME_VISTED++;
    //response.getWriter().println("<p1>You have visted "+DataServlet.TIME_VISTED+" Times </p1>");
    /*for(String quote : quotes){
        response.getWriter().println(quote);
    }*/
    //response.getWriter().println(quotes);

    /*
    Below is example getting data from the data base
    */
  }
  
 private String toJason(ArrayList<String> list){
    String jason = "{";
     for(int i = 0; i<list.size(); i++){
         jason+="\"quote"+i+"\"+: ";
         jason+=list.get(i);
         if(i != list.size()-1){
             jason+=",";
         }
     }
     jason+="}";
     return jason;     
  }

   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
       String inputComment = request.getParameter("text-input");
       String[] inputs = inputComment.split(",");
       comments = Arrays.asList(inputs);
       //*****Below is use for putting data to data bases
        Entity commentsEntity = new Entity("Comments");
        long timestamp = System.currentTimeMillis();

        commentsEntity.setProperty("comments", inputComment);
        commentsEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentsEntity);

        //*****Below is retriving from data base

        Query query = new Query("Comments").addSort("timestamp", SortDirection.DESCENDING);

        //DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        List<CommentDisplayer> display = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            //long id = entity.getKey().getId();
            String databaseComment = (String)entity.getProperty("comments");
            long databaseTimeStamp = (long) entity.getProperty("timestamp");

            CommentDisplayer comment = new CommentDisplayer(databaseComment, databaseTimeStamp);
            display.add(comment);
        }

        //Gson gson = new Gson();
        //response.setContentType("application/json;");
        //response.getWriter().println(gson.toJson(display));

        //*** Below is printing to the page
        if(Boolean.parseBoolean(request.getParameter("sort-by-length"))){
            Collections.sort(display,LENGTH_COMPARATOR);
        }
        if(Boolean.parseBoolean(request.getParameter("sort-by-time"))){
            Collections.sort(display,TIME_COMPARATOR);
        }else{
            Collections.sort(display);
        }
        //(display, response);
        for(CommentDisplayer comment: display){
            response.getWriter().println(comment);
        }
    }

    private void printComments(List<CommentDisplayer> li, HttpServletResponse response) throws IOException{
        response.setContentType("head/html");
        response.getWriter().println("Comments Added!");
        response.setContentType("text/html");
        for(CommentDisplayer comments : li){
            response.getWriter().println(comments);
        }
   }
    private static class SortByTime implements Comparator<CommentDisplayer>{
        public int compare(CommentDisplayer c1, CommentDisplayer c2){
            return Long.compare(c1.getTimeStamp(), c2.getTimeStamp());
        }
    }

    private static class SortByLength implements Comparator<CommentDisplayer>{
        public int compare(CommentDisplayer c1, CommentDisplayer c2){
            return Integer.compare(c1.getComment().length(),c2.getComment().length());
        }
    }
}
final class CommentDisplayer implements Comparable<CommentDisplayer>{

  private final String comment;
  private final long timestamp;

  public CommentDisplayer(String comment, long timestamp) {
    this.comment = comment;
    this.timestamp = timestamp;
  }

   public String getComment(){
       return comment;
   }
   public long getTimeStamp(){
       return timestamp;
   }
  @Override
  public String toString(){
      return comment + " : " + timestamp;
  }
  
  @Override
  public int compareTo(CommentDisplayer other){
      return this.comment.compareTo(other.comment);
  }
}

 
