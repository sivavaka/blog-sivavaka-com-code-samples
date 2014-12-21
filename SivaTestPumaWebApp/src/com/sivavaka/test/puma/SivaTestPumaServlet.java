package com.sivavaka.test.puma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Siva R Vaka
 *  * <p> SivaTestPumaServlet is test servlet to invoke the different PUMA test scenarios.</p>
 */
public class SivaTestPumaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	SivaTestPumaUtil pumaUtil;
	
    @Override
    public void init() throws ServletException {
    	super.init();
    	try {
			pumaUtil = new SivaTestPumaUtil();
		}catch(Exception ex){
			System.out.println("Cant initialize PUMA");
			throw new ServletException();
		}
    }   

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.getWriter().print("<html><body>");
		try{
			response.getWriter().print("<h2><font color='red'>Executing PUMA query as Admin user without logging in portal (i.e without security context in request)</font></h2> ");
			response.getWriter().println(pumaUtil.printUsersByLastName("vaka"));
			
			response.getWriter().print("<h2><font color='red'>Querying for list of users by passing specific uids..etc without wild char search (using findusersbyquery with 'or' condition)</font></h2> ");
			List<String> userIds = new ArrayList<String>();
			userIds.add("sivavaka");
			userIds.add("portaluser1");
			userIds.add("portaluser2");
			
			response.getWriter().println(pumaUtil.printListOfUsers(userIds));
			
			response.getWriter().print("<h2><font color='red'>Fetching all users (Paged Search)</font></h2> ");
			response.getWriter().println(pumaUtil.printAllUsers());
			
		}catch(Exception ex){
			ex.printStackTrace();
			response.getWriter().print("Error while processing the PUMA related code " );
		}
		response.getWriter().print("</body></html>");
	}


}
