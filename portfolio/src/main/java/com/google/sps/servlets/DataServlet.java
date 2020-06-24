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
  private static Comparator COMMENTS_COMPARATOR = new SortByName();
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
    response.setContentType("text/html;");
    //response.getWriter().println("<h1>Hello Dequan!</h1>");
    //DataServlet.TIME_VISTED++;
    //response.getWriter().println("<p1>You have visted "+DataServlet.TIME_VISTED+" Times </p1>");
    /*for(String quote : quotes){
        response.getWriter().println(quote);
    }*/
    response.getWriter().println(quotes);
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
       String input = request.getParameter("text-input");
       String[] inputs = input.split(",");
       comments = Arrays.asList(inputs);
       if(Boolean.parseBoolean(request.getParameter("sort-by-length"))){
           Collections.sort(comments,LENGTH_COMPARATOR);
       }
       if(Boolean.parseBoolean(request.getParameter("sort-by-comments"))){
            Collections.sort(comments,COMMENTS_COMPARATOR);
       }
       printComments(comments, response);

       // Below is use for data bases
        Entity commentsEntity = new Entity("Comments");
        long timestamp = System.currentTimeMillis();

        commentsEntity.setProperty("comments", comments);
        commentsEntity.setProperty("timestamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentsEntity);
   }

    private void printComments(List<String> li, HttpServletResponse response) throws IOException{
        response.setContentType("text/html");
        for(String comments : li){
            response.getWriter().println(comments+"</br>");
        }
   }
    private static class SortByName implements Comparator<String>{
        public int compare(String c1, String c2){
            return c1.compareTo(c2);
        }
    }

    private static class SortByLength implements Comparator<String>{
        public int compare(String c1, String c2){
            return Integer.compare(c1.length(), c2.length());
        }
    }
}

 
