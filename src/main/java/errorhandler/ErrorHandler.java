package errorhandler;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ErrorHandlerServlet", value = "/ErrorHandlerServlet")
public class ErrorHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // get all details about exceptions
        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String servletName = (String) request.getAttribute("jakarta.servlet.error.servlet_name");
        String message=(String) request.getAttribute("jakarta.servlet.error.message");

        if (servletName == null) {
            servletName = "Unknown";
        }
        if(message==null){
            message="Unknown";
        }


        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String mes= "{\"error\":\"" +statusCode+"\"," + "\"message\":\"" + message + "\"," + "\"exception\":\"" + throwable + "\"}";
        //set response code
        response.setStatus(statusCode);
        out.println(mes);
        out.flush();
        out.close();


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}