// package org.cnio.appform.groovy.test.integration
/*
import org.cnio.appform.groovy.util.OnePerson;

OnePerson one = new OnePerson (firstName:'Rasheed', lastName:'Wallace')
def out = one.toString()
println "The one: ${out}"
*/

// TODO AHORA que se supone sale todo bien hay que ver cómo monar el builder
import org.cnio.appform.entity.*;
import org.cnio.appform.util.*;
import org.hibernate.*
import org.hibernate.criterion.Restrictions;

import org.cnio.appform.groovy.util.ItemsFetcher
import org.cnio.appform.groovy.util.FormRender
import org.cnio.appform.groovy.util.TypesCache


println ("Starting groovy/hibernate script...")
Session hibSes = HibernateUtil.getSessionFactory().openSession()
AppUserCtrl ctrl = new AppUserCtrl (hibSes)


Interview intrv = (Interview)hibSes.get(Interview.class, new Integer (50))
println "Interview: "+intrv.getName()

Criteria crit = hibSes.createCriteria(org.cnio.appform.entity.SimpleTypesView.class);
crit.add(Restrictions.eq ("intrvOwner", intrv));
def res = crit.list();

//def hibQry = hibSes.createQuery("from SimpleTypesView where intrvOwner=:intrv")
//hibQry.setEntity("intrv", intrv)
//def res = hibQry.list()

hibSes.close()



/*
println ("Criteria results: " + res.size());
res.each {
  println ("idansit: "+it.getIdAnsitem()+"; name: "+it.getAnsItName())
}
*/

// def res = writer.toString()
// println res

def secOrder = 1
def prjCode = "188"
def pat = "157051555" // "157851074"
pat = "18801100700"
pat = IntrvFormCtrl.NULL_PATIENT
def intrvId = 50
// intrvId = 2100
intrvId = 6150 // 2400

def dbServer = "gredos.cnio.es"
// dbServer = "localhost"
// dbServer = "ubio.bioinfo.cnio.es"
ItemsFetcher iFetcher = new ItemsFetcher ("appform", dbServer, "gcomesana")
List items = iFetcher.getResultSet (intrvId, pat, secOrder)

int count = 0
/*
items.each {
  mapStr = "[itemord:${it.itemord}, content:\"${it.content.trim()}\", itemid: ${it.itemid},"
  mapStr += "codq: \"${it.codq}\", highlight:${it.highlight}, secname:\"${it.secname}\","
  mapStr += "secid:${it.secid},answer_number:${it.answer_number},"
  mapStr += "answer_order:${it.answer_order},codpatient:\"${it.codpatient}\","
  mapStr += "idpat:${it.idpat},thevalue:\"${it.thevalue}\",idansitem:${it.idansitem}]"

  println mapStr;
  count++
}
*/

count = iFetcher.printResultSet(items)

println "Num of results: ${count}"
int secid = iFetcher.getSectionId(secOrder, intrvId)
int patid = iFetcher.getPatientId(pat)


// TypesCache typesCache = new TypesCache (50)
// TestEasyb fr = new TestEasyb (patid, secid, secOrder-1, typesCache)
// fr.buildForm (items)


/*items.each {
  row -> println "[$row.itemord] ${row.stmt.trim()} ($row.codq): $row.theanswer"
} */


println "END"