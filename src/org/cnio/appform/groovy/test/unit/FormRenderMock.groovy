import groovy.xml.MarkupBuilder
import org.cnio.appform.groovy.util.FormRender
import org.cnio.appform.groovy.util.TypesCache
import org.cnio.appform.groovy.util.FormRenderUtil
import org.cnio.appform.groovy.util.ItemsFetcher

import org.cnio.appform.groovy.test.unit.ResultSetMock
import org.cnio.appform.groovy.test.unit.PreviewResultMock
import org.cnio.appform.groovy.test.unit.IEResultMock
import org.cnio.appform.groovy.test.unit.TypesCacheMock


// import static org.mockito.Mockito.*
// import org.mockito.Matchers

// def resultSet
def secName
def patId
def secId
def builder

def secOrder = 3
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

item1 = [itemord:1, content:"Me gustaría que pensara en todos los trabajos que haya tenido en la vida.", itemid: 156,codq: "null", highlight:1, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object
item2 = [itemord:2, content:"B1. Ha trabajado, durante 6 meses o más, solo en turno de noche?", itemid: 157, idq: 157, codq: "B1", highlight:0, secname:"B. Trabajo en turnos",secid:100,answer_number:1,answer_order:1,codpatient:"157081003",idpat:3060,thevalue:"2",idansitem:1403]  as Object
item3 = [itemord:3, content:"ENTREVISTADOR: Se considera turno de noche cualquier trabajo en que se entra a partir de las 8 de la noche y finaliza después de las 4 de la mañana", itemid: 158,codq: "null", highlight:2, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object
item4 = [itemord:5, content:"B2. ¿Ha trabajado, durante al menos 6 meses, alternando turno de noche y otros turnos?", itemid: 159, idq: 157, codq: "B2", highlight:0, secname:"B. Trabajo en turnos",secid:100,answer_number:1,answer_order:1,codpatient:"157081003",idpat:3060,thevalue:"2",idansitem:1403] as Object
item6 = [itemord:6, content:"ENTREVISTADOR: Otros turnos pueden ser: mañana o tarde", itemid: 160, codq: "null", highlight:2, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object


// MOCKCACHE TEST FOR SIMPLE AND ENUM TYPES. Based on the (mocked) resultset above

def mockCache = TypesCacheMock.getTypeCache()
def resultSet = ResultSetMock.getResultSetSec12()
resultSet = IEResultMock.getResultSetSec4Preview()

secName = resultSet[1].secname
patId = resultSet[0].idpat != null? resultSet[0].idpat:
          resultSet[1].idpat != null? resultSet[1].idpat:
            resultSet[2].idpat != null? resultSet[2].idpat: 20 // OJO, dummy

secId = resultSet[2].secid


// This is the class under testing
// All other objects can be mocked
FormRenderUtil fru = new FormRenderUtil ()
FormRender fr = new FormRender (patId, secId, secOrder, resultSet, null)
fr.setFormRenderUtil(fru)
fr.setTypesCache(mockCache)

if (patId == 20)
  fr.setPreview(true)
String xmlOut = fr.buildForm()

println "\nResult: \n ${xmlOut}"


// TYPESCACHE test on a mocked resultSet
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


// Este código debería ir en el core del buildForm
// Debería ir como un método aparte para llamarlo cuando haya grupos,
// pero a su vez debería ser recursivo

 /*
  builder.table () {

  for (i=0; i < resultSet.size(); i++) {
    def item = resultSet[i]

    if (fr.isRepeatable(item)) {
      def repeated = fr.getRepeatedItems(item, resultSet[i..resultSet.size()-1])

      fr.renderSingleItem(item)
      fr.renderRepeated(item, repeated)
*      println ("PARENT: ${item.itemid} - ${repeated.size()} children")
      repeated.each {
        println """order: ${it.itemord}, id: ${it.itemid}, n: ${it.answer_number}, o: ${it.answer_order}, parent: ${it.itparent}, content: ${it.content}"""
      }
*
      i += repeated.size()
    }

// En el renderChildren habría que comprobar si cada item es a su vez padre
// de otros... sino la sección 12, donde hay padres anidados, sale mal
    else if (fr.hasChildren(item, resultSet)) {
      def children = fr.getChildren(item, resultSet)
      i += fr.renderChildren (item, children, 1)
    }

    else {
      if (fr.isText(item) || fr.isSingleQuestion(item, resultSet))
        fr.renderSingleItem (item)
  
      else { // only multiple questions
        def questions = fr.getMultipleAnswers(item, resultSet, 1)
        i += questions.size()-1
        fr.renderMultipleQuestion (item, questions, 1)
      }  
    }

  }

  } // EO table

*/

  
/*
  for (i=0; i < resultSet.size(); i++) {
    def it = resultSet[i]

    if (fr.isText(it) || fr.isSingleQuestion(it, resultSet))
      fr.renderSingleItem (builder, it)

    else { // only multiple questions
      def questions = fr.getMultipleAnswers(it, resultSet, 1)
      i += questions.size()
      fr.renderMultipleQuestion (builder, it, questions, 1)
    }
  } // EO for, resultSet loop

println "\nResult: \n ${writer.toString()}"
*/



