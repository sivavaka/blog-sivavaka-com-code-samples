package com.sivavaka.test.puma;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import com.ibm.portal.um.PagingIterator;
import com.ibm.portal.um.PumaEnvironment;
import com.ibm.portal.um.PumaHome;
import com.ibm.portal.um.PumaLocator;
import com.ibm.portal.um.PumaProfile;
import com.ibm.portal.um.User;
/**
* @author Siva R Vaka
* 
* <p>SivaTestPumaUtil class which describes not so frequently used PUMA scenario's like below. </p>
* <p>
* 1. Accessing the PUMA from webapp deployed on the Portal or Making the PUMA api calls without logging into portal.
* 2. Searching for multiple users by passing list of Uid’s (or any attribute) without using the wild chars.
* 3. Paged Search in PUMA.
* </p>
* 
* <p> NOTE: Done or coded it for quick testing, you can optimize code little more. </p> 
*/
public class SivaTestPumaUtil {
	
	private PumaHome pumaHome;
	
	public SivaTestPumaUtil() throws Exception{
		InitialContext ctx = new InitialContext();
		pumaHome = (PumaHome)ctx.lookup(PumaHome.JNDI_NAME);

	}
    
     
     /*
      * <p>Execute as authenticated admin user(retrieve users without login to portal)</p>
      * <p> You can return the list of users instead of printing them by just uncommenting couple of statement in this method </p> 
      */
     //public List<User> getUsersByLastName(final String lastName) throws Exception{
     public String printUsersByLastName(final String lastName) throws Exception{
    	          
          final PumaLocator pumaLocator = pumaHome.getLocator();
          final PumaProfile pumaProfile = pumaHome.getProfile();
          
          final List<String> attribList = new ArrayList<String>();
          attribList.add("uid");
          attribList.add("cn");
          attribList.add("sn");
         
          //final List<User> usersList = new ArrayList<User>();
          
          final StringBuffer sb = new StringBuffer();
        	  
          PrivilegedExceptionAction<Void> privilegedAction = new PrivilegedExceptionAction<Void>() {
              @Override
              public Void run() {
                   try{
                	   //search users who have the same last name i.e users with last name as "vaka"
                	  List<User> users = pumaLocator.findUsersByAttribute("sn", lastName);
                	  //usersList.addAll(users);
                	  for(User user : users){
                		  Map<String, Object> attributesMap = pumaProfile.getAttributes(user, attribList);

            			  for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
            			      sb.append(entry.getKey() + " : " + entry.getValue() +" , \t");
            			  } 
            			  sb.append("\n").append("</br>");
                	  }
                   
                   }catch(Exception ex){
                        System.err.println("Error while gettting the users from the PUMA");
                   }
                   return null;
              }
         }; 

         try {
              PumaEnvironment pumaEnv = pumaHome.getEnvironment();
              pumaEnv.runUnrestricted(privilegedAction);
         } catch (PrivilegedActionException ex) {
              System.err.println("error while executing the previleged action");
              ex.printStackTrace();
         }
         
         //return usersList;
         return sb.toString();
     }

     
     /*
      * <p> Execute the FindUsersByQuery with or condition(retrieve users without login to portal)</p>
      * <p> You can return the list of users instead of printing them by just uncommenting couple of statement in this method </p> 
      */
     //public List<User> getListOfUsers(List<String> userIds) throws Exception{
     public String printListOfUsers(List<String> userIds) throws Exception{
    	 
          System.out.println("Inside the getUsersByLastName method");
          
          final PumaLocator pumaLocator = pumaHome.getLocator();
          final PumaProfile pumaProfile = pumaHome.getProfile();
          
          final List<String> attribList = new ArrayList<String>();
          attribList.add("uid");
          attribList.add("cn");
          attribList.add("sn");
         
          //final List<User> usersList = new ArrayList<User>();
          final StringBuffer usersDetailsStr = new StringBuffer();
          
          if(null == userIds || 0 == userIds.size()) return "Empty userIds List passed";
          final StringBuilder userIdsListQuery = new StringBuilder();
          int i = 0;
          if( 1 == userIds.size() ){
        	  userIdsListQuery.append("(uid='"+userIds.get(0)+"*')");
          } else {
               for (String uid : userIds) {
                    if(i == 0) {
                    	userIdsListQuery.append("((uid='"+uid+"*') or ");
                    }else if(i == userIds.size()-1)
                    	userIdsListQuery.append("(uid='"+uid+"*'))");
                    else
                    	userIdsListQuery.append("(uid='"+uid+"*') or ");
                    ++i;
                }
          }
        	  
          PrivilegedExceptionAction<Void> privilegedAction = new PrivilegedExceptionAction<Void>() {
              @Override
              public Void run() {
                   try{
                	   //search users who have the same last name i.e users with last name as "vaka"
                	  List<User> users = pumaLocator.findUsersByQuery(userIdsListQuery.toString());
                	  //usersList.addAll(users);
                	  for(User user : users){
                		  Map<String, Object> attributesMap = pumaProfile.getAttributes(user, attribList);

            			  for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
            				  usersDetailsStr.append(entry.getKey() + " : " + entry.getValue() +" , \t");
            			  } 
            			  usersDetailsStr.append("\n").append("</br>");
                	  }
                   
                   }catch(Exception ex){
                        System.err.println("Error while gettting the users from the PUMA");
                   }
                   return null;
              }
         }; 

         try {
              PumaEnvironment pumaEnv = pumaHome.getEnvironment();
              pumaEnv.runUnrestricted(privilegedAction);
         } catch (PrivilegedActionException ex) {
              System.err.println("error while executing the previleged action");
              ex.printStackTrace();
         }
         
         //return usersList;
         return usersDetailsStr.toString();
     } 
     
     
     /*
      * Print All users (using the paged search)
      * <p> You can return the list of users instead of printing them by just uncommenting couple of statement in this method </p> 
      */
     //public List<User> getAllUsers() throws Exception{
     public String printAllUsers() throws Exception{
    	          
          final PumaLocator pumaLocator = pumaHome.getLocator();
          final PumaProfile pumaProfile = pumaHome.getProfile();
          
          //Page size is 5
          final Map<String, Object> configMap = new HashMap<String, Object>();
          configMap.put(PumaLocator.RESULTS_PER_PAGE, new Integer(5));
          
          final List<String> attribList = new ArrayList<String>();
          attribList.add("uid");
          attribList.add("cn");
          attribList.add("sn");
         
          //final List<User> usersList = new ArrayList<User>();
          final StringBuffer sb = new StringBuffer();
        	  
          PrivilegedExceptionAction<Void> privilegedAction = new PrivilegedExceptionAction<Void>() {
              @Override
              public Void run() {
                   try{
                	   //you can use findUsersByQuery or findUsersByAttribute
                 	  //PagingIterator<User> users = pumaLocator.findUsersByQuery("uid = '*'", configMap);
                	  PagingIterator<User> users = pumaLocator.findUsersByAttribute("uid", "*", configMap);
                	  List<User> temp = new ArrayList<User>();
                      while(users.hasNextPage()){
                          temp = users.getNextPage(temp);
                          //usersList.addAll(temp);
                          if(null != temp && 0 < temp.size()){
                               for (User user : temp) {                                           
                                    Map<String, Object> attributesMap = pumaProfile.getAttributes(user, attribList);
									for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
										sb.append(entry.getKey() + " : " + entry.getValue() +" , \t");
									}
									sb.append("\n").append("</br>");
                                }
                          }
                     }                	  
                   
                   }catch(Exception ex){
                        System.err.println("Error while gettting the users from the PUMA");
                   }
                   return null;
              }
         }; 

         try {
              PumaEnvironment pumaEnv = pumaHome.getEnvironment();
              pumaEnv.runUnrestricted(privilegedAction);
         } catch (PrivilegedActionException ex) {
              System.err.println("error while executing the previleged action");
              ex.printStackTrace();
         }
         
         //return usersList;
         return sb.toString();
     }
     
}