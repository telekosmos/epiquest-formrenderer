package org.cnio.appform.java;

import java.util.List;
import org.cnio.appform.groovy.util.*;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println (ItemsFetcher.DBNAME+"; "+ItemsFetcher.DBSERVER);


    ItemsFetcher ife = new ItemsFetcher ("appform", ItemsFetcher.DBSERVER, 
                                ItemsFetcher.DBUSER);
/*
    List rs = ife.getResultSet(50, "157", "157011001", 5);
    Integer rslen = rs.size();

    System.out.println ("Number of rows in java is: "+rslen);
*/
  }

}
