package org.cnio.appform.groovy.test.integration

import org.cnio.appform.groovy.util.FormRender
import org.cnio.appform.groovy.util.FormRenderUtil
import org.cnio.appform.groovy.util.ItemsFetcher
import org.cnio.appform.groovy.util.TypesCache
import org.cnio.appform.util.HibernateUtil
import org.hibernate.SessionFactory
import org.hibernate.engine.SessionFactoryImplementor
import org.hibernate.dialect.Dialect
import org.cnio.appform.util.IntrvFormCtrl

// def resultSet
def secName
def patId
def secId
def builder

def secOrder = 12
def prjCode = "157"
def pat = "157081003"
def intrvId = 50


/* Types mockCache mocking with mockito
*
TypesCache mockCache = mock (TypesCache.class)

when(mockCache.isEnumType(1400)).thenReturn false
when(mockCache.isEnumType(1480)).thenReturn false
when (mockCache.getSimpleType(1400)).thenReturn ([idansitem:1400, name:"Free text", codintrv:50])
when (mockCache.getSimpleType(1480)).thenReturn ([idansitem:1480, name:"Decimal", codintrv:50])


def mockCache = new groovy.mock.interceptor.MockFor(TypesCache)
mockCache.demand.isEnumType { typeId -> typeId == 1400 || typeId == 1480? false: true }
mockCache.demand.getSimpleType { typeId ->
    if (typeId == 1400)
      [idansitem:1400, name:"Free text", codintrv:50]

    if (typeId == 1480)
      [idansitem:1480, name:"Decimal", codintrv:50]
  }
*/


// MOCKCACHE TEST FOR SIMPLE AND ENUM TYPES. Based on the (mocked) resultset above
// def mockCache = TypesCacheMock.getTypeCache()


java.util.Calendar cal = java.util.Calendar.getInstance()
def time0 = cal.getTimeInMillis()
println "Script started at time0: ${time0}"


final SessionFactoryImplementor sfi = (SessionFactoryImplementor)HibernateUtil.getSessionFactory();
String catalog = sfi.getSettings().getConnectionProvider().getConnection().getMetaData().getURL()
println ( "dbUrl: " + catalog )


String dbName = "appform"
// dbName = "epiquestdb"
String dbServer = "gredos.cnio.es"
dbServer = "localhost"
String dbUser = "gcomesana"
// dbUser = "epiquestusr"

dbServer = catalog.contains("gredos")? "gredos.cnio.es":
          catalog.contains("localhost")? "localhost":
          catalog.contains("ubio")? "ubio.bioinfo.cnio.es": dbServer
// dbServer = "padme.cnio.es"
println ("so, dbServer is ${dbServer}")


// This is the class under testing
// All other objects can be mocked
/*
// GROOVY BASED RENDER INITIALIZATION should be here ///////////////////////////
// * new the ItemsFecher (dbServer, dbName, dbUser) and
// * set the resultSet (ItemsFetcher (intrvId, prjCode, patid, orderSec)
// * new the FormRenderUtil as support for main FormRender
// * new the FormRender (patId, secId, orderSec, resultSet, null)
// * new the TypesCache (intrvId)
// * set the render types cache
// * set the render formRenderUtil
// * set preview or not

patId -> request.getParameter("patid")
secid -> id
intrvId -> (Integer)session.getAttribute ("intrvId")
secOrder -> hay que abrir una session hibernate y coger una Section (secId) :-S

TypesCache cache = new TypesCache (intrvId);
ItemsFetcher fetcher = new ItemsFetcher ("dbServer", "dbName", "dbUsr");
FormRenderUtil fru = new FormRenderUtil ();
int secOrder = sec.getSectionOrder().intValue();
//FormRender fr = new FormRender (patId, Integer.decode(secId).intValue(), secOrder, (List)null, null);
//fr.buildForm()
for (AbstractItem ai : itemsSec) {
render.clearHtmlStr();
render.html4Item (hibSes, ai, Integer.decode(patId), intrCtrl);
out.println (render.getHtmlStr());
} // EO rendering core loop
 */



// prjCode = "157"
intrvId = 100 // 2100 // QES IE
intrvId = 4900 // PatienIdentification_France
// intrvId = 1700 // QES_German
// intrvId = 6150 // Testi
// intrvId = 4651 // pedigree spanish


pat = "18801100200" // "157091012" // "157851074" IE
pat = "157501073" // from klinikum or so; 18 -> reference
// pat = "157851074"
// pat = "157081023"
// pat = "18801114900"
pat = IntrvFormCtrl.NULL_PATIENT
pat = "57F093019005"
secOrder = 1
// secOrder = 1
// pat = "188011060"
// intrvId = 3700 // Identificacin_paciente_ES

ItemsFetcher iFetcher
if (dbServer.compareTo("localhost") != 0)
  iFetcher = new ItemsFetcher (dbServer, dbName, dbUser)
else
  iFetcher = new ItemsFetcher (dbServer, dbName, dbUser, "appform", "4321")

List resultSet = iFetcher.getResultSet (intrvId, pat, secOrder)
iFetcher.printResultSet(resultSet)

def newestItems = iFetcher.getRepeatableItemsBlock(intrvId, secOrder)
patId = iFetcher.getPatientId(pat)

// iFetcher.printResultSet(resultSet)
iFetcher.close()


secName = resultSet[1]?.secname
patId = patId != null? patId: resultSet[0].idpat != null? resultSet[0].idpat:
          resultSet[1].idpat != null? resultSet[1].idpat:
            resultSet[2].idpat != null? resultSet[2].idpat: 20 // OJO, dummy

secId = resultSet[0] != null? resultSet[0].secid:
        resultSet[1] != null? resultSet[1].secid: null;
//resultSet[2].secid

def currentRow = resultSet[0]

FormRenderUtil fru = new FormRenderUtil ()
def typesCache = new TypesCache (intrvId, true)

FormRender fr = new FormRender (patId, secId, secOrder, resultSet, null)
fr.setFormRenderUtil(fru)
fr.setTypesCache(typesCache)
fr.setNewestItems(newestItems)
if (patId == 20)
  fr.setPreview(true)

fr.setPreview (true)


/*
def enums = typesCache.getEnumTypes()
enums.each {
  println "(${it.ansItName}) ${it.typeName} -> ${it.typeValue}"
}
*/


String xmlOut = fr.buildForm()
println "\n*** Result: \n ${xmlOut}"


java.util.Calendar cal2 = java.util.Calendar.getInstance()
def time1 = cal2.getTimeInMillis()
def timeDiff = time1 - time0
println "Script finish in time1: ${time1}"
println "Time spent: ${timeDiff} ms" 

/*
resultSet.each {

  if (fr.isText(it) == false) {
    def idType = it.idansitem

    if (mockCache.isEnumType(idType)) {
      def enumitems = mockCache.getEnumType(idType)
      println enumitems[0].ansitname
      enumitems?.each { enumitem ->
        println "${enumitem.typename} -> ${enumitem.typevalue}"
      }
      println "===================="
    }
    else {
      def simpleitem = mockCache.getSimpleType(idType)
      println simpleitem.name
      println "===================="
    }
  }

}
*/
