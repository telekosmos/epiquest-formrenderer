package org.cnio.appform.groovy.util

import groovy.sql.*
import java.sql.Connection
import org.cnio.appform.util.IntrvFormCtrl

class ItemsFetcher {

  Sql sql

  public static String DBNAME = "appform"
  public static String DBSERVER = "localhost"
  public static String DBUSER = "gcomesana"

  String dbName
  String dbServer
  String dbUser
  String dbPort
  
  ItemsFetcher (dbserver, dbname, user) {
    dbName = dbname
    dbServer = dbserver
    dbUser = user

    sql = Sql.newInstance ("jdbc:postgresql://${dbServer}:5432/${dbName}",
                           dbUser, dbName,
                          "org.postgresql.Driver")
  }

  

  ItemsFetcher (dbServer, dbName, dbUser, dbPasswd, dbPort) {
    this.dbName = dbName
    this.dbServer = dbServer
    this.dbUser = dbUser
    this.dbPort = dbPort == null? "5432": dbPort

    sql = Sql.newInstance("jdbc:postgresql://${dbServer}:${this.dbPort}/${dbName}",
            dbUser, dbPasswd, "org.postgresql.Driver")
  }



/**
 * Get the resultset for the parameters
 * @param Integer idIntrv, the interview database id
 * @param String codpatient, the code for the patient (i.e. 157011063)
 * @param Integer orderSec, the number of the section as its order. It can not be
 * less than 1.
 *
 * @return the resultset as a list of objects. the members of the objects will be
 * the domain of the query
 */
  List getResultSet (Integer idIntrv, String codpatient, Integer orderSec) {
    
    Integer intrvId = idIntrv, sectionOrder = orderSec
    String defQuery = ""


    defQuery = """select items.*, pgabis.answer_number, pgabis.answer_order,
        pgabis.codpatient, pgabis.idpat, a.thevalue, ai.name, ai.idansitem,
        qai.pattern
        from (
          select it.item_order as itemord, it.content as content, q.codquestion as codq,
            it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
            it."repeatable" as itrep, q.idquestion as idq,
            s.name as secname, s.idsection as secid
          from question q right join item it on (it.iditem = q.idquestion), section s,
          interview i
          where 1 = 1 -- it.idsection = 200
            and i.idinterview = ${intrvId}
            and i.idinterview = s.codinterview
            and s.section_order = ${sectionOrder}
            and it.idsection = s.idsection
          ) items left join question_ansitem qai on (items.idq = qai.codquestion)
                  left join answer_item ai on (qai.codansitem = ai.idansitem)
           left join (
            select *
            from pat_gives_answer2ques pga, patient p
            where pga.codpat = p.idpat
              and p.codpatient = '${codpatient}'
          ) pgabis on (items.itemid = pgabis.codquestion and pgabis.answer_order = qai.answer_order)
          left join answer a on (pgabis.codanswer = a.idanswer)
        order by itemord, answer_number, answer_order;"""


    defQuery = """select items.*, pgabis.answer_number, qai.answer_order, -- pgabis.answer_order,
        pgabis.codpatient, pgabis.idpat, a.thevalue, ai.name, ai.idansitem,
        qai.pattern
        from (
          select it.item_order as itemord, it.content as content, q.codquestion as codq,
            it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
            it."repeatable" as itrep, q.idquestion as idq,
            s.name as secname, s.idsection as secid
          from question q right join item it on (it.iditem = q.idquestion), section s,
          interview i
          where 1 = 1 -- it.idsection = 200
            and i.idinterview = ${intrvId}
            and i.idinterview = s.codinterview
            and s.section_order = ${sectionOrder}
            and it.idsection = s.idsection
          ) items left join question_ansitem qai on (items.idq = qai.codquestion)
                  left join answer_item ai on (qai.codansitem = ai.idansitem)
           left join (
            select *
            from pat_gives_answer2ques pga right join
                  patient p on (pga.codpat = p.idpat)
            where p.codpatient = '${codpatient}'
          ) pgabis on (items.itemid = pgabis.codquestion and pgabis.answer_order = qai.answer_order)
          left join answer a on (pgabis.codanswer = a.idanswer)
        order by itemord, answer_number, answer_order;"""


    String previewQry = """select items.*, 1 as answer_number, qai.answer_order, '15700000000' as codpatient,
            20 as idpat, '9999' as thevalue, ai.name, ai.idansitem, qai.pattern,
          qai.answer_order as answer_order
        from (
          select it.item_order as itemord, it.content as content, q.codquestion as codq,
            it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
            it."repeatable" as itrep, q.idquestion as idq,
            s.name as secname, s.idsection as secid
          from question q right join item it on (it.iditem = q.idquestion), section s,
          interview i
          where 1 = 1 -- it.idsection = 200
            and i.idinterview = ${intrvId}
            and i.idinterview = s.codinterview
            and s.section_order = ${sectionOrder}
            and it.idsection = s.idsection
          ) items
          left join question_ansitem qai on (items.itemid = qai.codquestion)
          left join answer_item ai on (qai.codansitem = ai.idansitem)
      --	  left join patient p on (pgabis.codpat = p.idpat)
      order by itemord, answer_order;"""

    if (codpatient.equalsIgnoreCase(IntrvFormCtrl.NULL_PATIENT)) {
println "Getting results for preview!!"
      sql.rows (previewQry)      
    }
    else
      sql.rows (defQuery)

/*    sql.eachRow (defQuery) {
      row -> println "[$row.itemord] ${row.stmt.trim()} ($row.codq): $row.theanswer"
    }
  */
  }





/**
 * Retrieves the items inside repeatable blocks for a sec'tion
 * @param intrvId, the id of the interview
 * @param secOrder, the order of the section to retrieve the items
 *
 * @return a list with the items selected
 */
  List getRepeatableItemsBlock (int intrvId, int secOrder) {

    def strQry =
          """select it.item_order as itemord, it.content as content, q.codquestion as codq,
                  it.iditem as itemid, it.highlight as highlight, it.ite_iditem as itparent,
                  it."repeatable" as itrep, q.idquestion as idq,
                  s.name as secname, s.idsection as secid, 1 as answer_number,
                  qai.answer_order as answer_order, '' as codpatient, -1 as idpat, '9999' as thevalue,
                  ai.name as ainame, qai.codansitem as idansitem, '' as pattern
              from question q right join item it on (it.iditem = q.idquestion), section s,
              interview i, question_ansitem qai, answer_item ai
              where 1 = 1 -- it.idsection = 200
                and i.idinterview = ${intrvId}
                and i.idinterview = s.codinterview
                and s.section_order = ${secOrder}
                and it.idsection = s.idsection
                and it.ite_iditem in (
                  select ibis.iditem
                  from item ibis
                  where ibis."repeatable" is not null
                )
                and q.idquestion = qai.codquestion
                and qai.codansitem = ai.idansitem
              order by itemord, answer_order;"""

     sql.rows (strQry)
  }



/**
 * Returns a section database id for the section defined by its interview and
 * its order inside de interview
 * @param int secOrder, the position of the section in the interview
 * @param int intrvId, the id of the interview the sections belongs to
 *
 * @return an int which is the database id for the section
 */
  int getSectionId (int secOrder, int intrvId) {
    def qry = """select idsection from section where section_order=$secOrder
              and codinterview=$intrvId"""

    def secid = sql.firstRow(qry)
    return secid != null? (secid.idsection as Integer).intValue(): null
  }





/**
 * Returns the patient database id from the patient code
 * NOTE!!!!
 * SOMETIMES, STATEMENTS FOR QUESTION OR TEXTS HAVE CARRIAGE RETURN
 * MIND THIS ONE AS THEY MIGHT BE CODIFIED INTO A ESCAPE CODE IN ORDER TO
 * GET A PROPER RENDER
 * @param String codPatient, the patient code
 * @return an int number which is the database patient code
 */
  int getPatientId (String codPatient) {
    def qry = "select idpat from patient where codpatient = $codPatient"

    def idpat = sql.firstRow(qry)

    return idpat != null? (idpat.idpat as Integer).intValue(): null
  }










/**
 * Close the connection associated to the Sql object. Do nothing if no connection
 * is associated
 * @return true if the connection was associated to the sql object and it was
 * close; false otherwise, no need to close the connection
 */
  public boolean close () {
    Connection con = sql.getConnection()
    if (con != null) {
      sql.close()
      return true
    }

    false
  }






/**
 * Convenient method to print the resultset straight to be embedded in a groovy script
 * Addressed to testing purposes. 
 */
  public int printResultSet (List rs) {

    String outStr
    String strListItems = "["
    int count = 0
    rs.each {
      String codq = it.codq != null? "\"${it.codq}\"": null
      String typename = it.name != null? "\"${it.name}\"": null
      outStr = "def item${count+1}="
      outStr += "[itemord:${it.itemord},itemid:${it.itemid},content:\"${it.content.trim()}\","
      outStr += "idq:${it.idq},codq:${codq}, itrep:${it.itrep}, highlight:${it.highlight},"
      outStr += "itparent:${it.itparent},secid:${it.secid},secname:\"${it.secname}\","
      outStr += "answer_number:${it.answer_number},answer_order:${it.answer_order},"
      outStr += "idpat:${it.idpat},codpatient:\"${it.codpatient}\","
      outStr += "thevalue:\"${it.thevalue}\",typename:${typename},idansitem:${it.idansitem},"
      outStr += "pattern:\"${it.pattern}\"]"

      println outStr
      strListItems += "item${count+1},"
      count++
    }
    strListItems = strListItems.substring(0, strListItems.length()-1) + "]"
    println strListItems
    count
  }

}