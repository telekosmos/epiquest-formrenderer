package org.cnio.appform.groovy.util

import org.hibernate.*
import org.hibernate.criterion.Restrictions
import org.cnio.appform.entity.*
import org.cnio.appform.util.HibernateUtil
import org.cnio.appform.util.HibController
import org.hibernate.engine.SessionFactoryImplementor
import org.hibernate.dialect.Dialect
import java.sql.DatabaseMetaData
import org.hibernate.criterion.Criterion
import org.hibernate.criterion.Order

public class TypesCache {

  private List simpleTypes
  private List enumTypes

  public TypesCache () {
// list of simpletypesview objects, enough for simple types
// each row/element is a simple type belonging to an interview
    simpleTypes = []

// list of enumTypes objects, not enumtypesview objects
// each row is an element belonging to a enumerate type. the whole enumerated
// type can be retrieved by filtering using codintrv and ansitname or idansit
    enumTypes = []
  }



/**
 * Constructor.
 * @param Integer intIntrvId, the interview database id
 * @param boolean populate, if true, data is retrieved and type caches populated;
 * if false, do nothing
 */
  public TypesCache (Integer intIntrvId, boolean populate) {
    this ()

    if (intIntrvId != null && populate) {
      int intrvId = intIntrvId.intValue()

      SessionFactory sf = HibernateUtil.getSessionFactory();
      Session hibSes = sf.openSession()
      Interview intrv = (Interview)hibSes.get(Interview.class, new Integer(intrvId))

      Criteria simpleCrit = hibSes.createCriteria(SimpleTypesView.class)
      //      simpleCrit.add(Restrictions.eq("intrvOwner", intrv))
      Criterion intrvRestr = Restrictions.eq ("intrvOwner", intrv)
      Criterion nullRestr = Restrictions.isNull("intrvOwner")
      simpleCrit.add(Restrictions.or(intrvRestr, nullRestr))
      simpleTypes = simpleCrit.list()

      Criteria enumCrit = hibSes.createCriteria(EnumTypesView.class)
//      enumCrit.add(Restrictions.eq("intrvOwner", intrv))
      Criterion enIntrvRestr = Restrictions.eq("intrvOwner", intrv)
      Criterion enNullRestr = Restrictions.isNull("intrvOwner")
      enumCrit.add(Restrictions.or(enIntrvRestr, enNullRestr))
      enumCrit.addOrder(Order.asc("idAnsit").asc("theOrder"))
      enumTypes = enumCrit.list()

      hibSes.close()
    }
  }




/**
 * Constructor 3 paremeters:<br/>
 * @param Session hibSes, a hibernate session
 * @param Integer intIntrvId, the interview database id
 * @param boolean populate, if true, populate the cache
 */
  public TypesCache (Session hibSes, Integer intIntrvId, boolean populate) {
    this ()

    if (hibSes != null && intIntrvId != null && populate) {
      boolean closeSes = false
      if (!hibSes.isOpen()) {
        hibSes = HibernateUtil.getSessionFactory().openSession()
        closeSes = true
      }

      int intrvId = intIntrvId.intValue()

      Interview intrv = (Interview)hibSes.get(Interview.class, new Integer(intrvId))

      Criteria simpleCrit = hibSes.createCriteria(SimpleTypesView.class)
//      simpleCrit.add(Restrictions.eq("intrvOwner", intrv))
      Criterion intrvRestr = Restrictions.eq ("intrvOwner", intrv)
      Criterion nullRestr = Restrictions.isNull("intrvOwner")
      simpleCrit.add(Restrictions.or(intrvRestr, nullRestr))
      simpleTypes = simpleCrit.list()

      Criteria enumCrit = hibSes.createCriteria(EnumTypesView.class)
//      enumCrit.add(Restrictions.eq("intrvOwner", intrv))
      Criterion enIntrvRestr = Restrictions.eq("intrvOwner", intrv)
      Criterion enNullRestr = Restrictions.isNull("intrvOwner")
      enumCrit.add(Restrictions.or(enIntrvRestr, enNullRestr))
      enumCrit.addOrder(Order.asc("idAnsit").asc("theOrder"))
      enumTypes = enumCrit.list()

      if (closeSes)
        hibSes.close()

    } // EO else
  }


/**
 * Retrieves the answer types for the interview represented by intrvId and
 * populates the lists with them
 * @param Integer intrvId, the interview database id whose types want to
 * be retrieved
 */
  def populateCache (Integer intrvId) {
    if (intrvId != null) {

      SessionFactory sf = HibernateUtil.getSessionFactory();
      Session hibSes = sf.openSession()
      Interview intrv = (Interview)hibSes.get(Interview.class, new Integer(intrvId))

      Criteria simpleCrit = hibSes.createCriteria(SimpleTypesView.class)
      simpleCrit.add(Restrictions.eq("intrvOwner", intrv))
      simpleTypes = simpleCrit.list()

      Criteria enumCrit = hibSes.createCriteria(EnumTypesView.class)
      enumCrit.add(Restrictions.eq("intrvOwner", intrv))
      enumTypes = enumCrit.list()

      hibSes.close()
    }
  }




/**
 * Clear the cache to be able to get another types
 */
  def clearCache () {
    simpleTypes.clear()
    enumTypes.clear()
  }




/**
 * Retrieve the enumerated items belonging to the enumerated type given the
 * parameter value
 * @param int idAnsItem, the enumerated type database id
 *
 * @return a list with all belonging EnumTypeView objects as retrieved from
 * the enumtypes_view in database
 */
  def getEnumType (int idAnsItem) {
    def requested = enumTypes.findAll { item -> item.idAnsit == idAnsItem }

    requested
  }



/**
 * Retrieve a single simple type from the simpletypes_view as given by the
 * parameter value
 * @param int idAnsItem, the database id of the simple type is wanted to be retrieved
 *
 * @returns a List of SimpleTypeView objects with the properties for that simple type
 */
  def getSimpleType (int idAnsItem) {
    def requested = simpleTypes.findAll { item -> item.idAnsitem == idAnsItem }

    requested
  }


  public boolean isEnumType (int idAnsItem) {
    enumTypes.any { it.idAnsit == idAnsItem }
  }




/**
 * Return the names of the enumerated types for this interview as a Set
 *
 * @return a Set with the names of the enumerated types for the interview. As
 * it is a set, the names are not repeated
 */
  private Set getDistinctEnumTypes () {
    def aux = [] as Set

    enumTypes.each {
      it -> aux << it.ansItName
    }
    return aux
  }


  public int getNumEnumTypes () {
    return this.getDistinctEnumTypes().size()
  }



  public int getNumSimpleTypes () {
    return simpleTypes.size ()
  }



  public List getSimpleTypes () {
    return simpleTypes
  }


  public List getEnumTypes () {


    return enumTypes
  }

}