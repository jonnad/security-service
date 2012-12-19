package com.ventyx.security.webservice.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sample HTTP post/get actions
 */
public class BusinessServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("<!DOCTYPE html><html><head><title>GET</title></head><body><h1>GET</h1><p>You have infiltrated the system</p></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("<!DOCTYPE html><html><head><title>POST</title></head><body><h1>POST</h1><p>You have infiltrated the system</p></body></html>");
    }
}
