
package org.cnio.appform.groovy.easyb

// import static org.mockito.Mockito.*
// import org.mockito.ArgumentMatcher
import groovy.xml.MarkupBuilder
import org.cnio.appform.groovy.util.FormRender


def resultSet
def secName
def patId
def secId
def builder

def secOrder = 3
def prjCode = "157"
def pat = "157081003"
def intrvId = 50


/*
class IsInRange extends ArgumentMatcher<Integer> {
  public boolean matches (Object index) {
    return (index >= 0) && (index < 5)
  }

}
*/


// MOCKITO stubbing:
// "When the x method is called then return y"


before "retrieve resultset", {
  given "all parameters are provided"
    and "the db connection is well configured", {
      println "execute query..."

item1 = [itemord:1, content:"Me gustaría que pensara en todos los trabajos que haya tenido en la vida.", itemid: 156,codq: "null", highlight:1, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object
item2 = [itemord:2, content:"B1. Ha trabajado, durante 6 meses o más, solo en turno de noche?", itemid: 157, idq: 157, codq: "B1", highlight:0, secname:"B. Trabajo en turnos",secid:100,answer_number:1,answer_order:1,codpatient:"157081003",idpat:3060,thevalue:"2",idansitem:1403]  as Object
item3 = [itemord:3, content:"ENTREVISTADOR: Se considera turno de noche cualquier trabajo en que se entra a partir de las 8 de la noche y finaliza después de las 4 de la mañana", itemid: 158,codq: "null", highlight:2, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object
item4 = [itemord:5, content:"B2. ¿Ha trabajado, durante al menos 6 meses, alternando turno de noche y otros turnos?", itemid: 159, idq: 157, codq: "B2", highlight:0, secname:"B. Trabajo en turnos",secid:100,answer_number:1,answer_order:1,codpatient:"157081003",idpat:3060,thevalue:"2",idansitem:1403] as Object
item5 = [itemord:6, content:"ENTREVISTADOR: Otros turnos pueden ser: mañana o tarde", itemid: 160, codq: "null", highlight:2, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object


item6 = [itemord:16,itemid:5411,content:"A3C. Abuela materna (la madre de su madre):",idq:5411,codq:"A3C", highlight:0,itparent:null,secid:50,secname:"A. Información Demográfica",answer_number:1,answer_order:1,idpat:3060,codpatient:"157081003",thevalue:"Colombia",typename:"Free text",idansitem:1400,pattern:""]
item7 = [itemord:17,itemid:5412,content:"A3D. Abuelo materno (el padre de su madre):",idq:5412,codq:"A3D", highlight:0,itparent:null,secid:50,secname:"A. Información Demográfica",answer_number:1,answer_order:1,idpat:3060,codpatient:"157081003",thevalue:"Colombia",typename:"Free text",idansitem:1400,pattern:""]
item8 = [itemord:18,itemid:5413,content:"A3E. Abuela paterna (la madre de su padre):",idq:5413,codq:"A3E", highlight:0,itparent:null,secid:50,secname:"A. Información Demográfica",answer_number:1,answer_order:1,idpat:3060,codpatient:"157081003",thevalue:"Colombia",typename:"Free text",idansitem:1400,pattern:""]
item9 = [itemord:19,itemid:5414,content:"A3F. Abuelo paterno (el padre de su padre):",idq:5414,codq:"A3F", highlight:0,itparent:null,secid:50,secname:"A. Información Demográfica",answer_number:1,answer_order:1,idpat:3060,codpatient:"157081003",thevalue:"Colombia",typename:"Free text",idansitem:1400,pattern:""]
item10 = [itemord:24,itemid:1193,content:"A4B. ¿Cuántos años completos de estudios tiene – sin contar repeticiones?  (NS -->  8888)",idq:1193,codq:"A4B", highlight:0,itparent:null,secid:50,secname:"A. Información Demográfica",answer_number:1,answer_order:1,idpat:3060,codpatient:"157081003",thevalue:"5",typename:"Decimal",idansitem:1480,pattern:"0;70;8888"]

      resultSet = [item1, item2, item3, item4, item5, item6, item7, item8, item9, item10]

      secName = resultSet[1].secname
      patId = resultSet[1].idpat
      secId = resultSet[1].secid

    }
}


before "create beginning of form", {
  given "parameters intrvid and secid are provided", {
    println "build the beginning of the form"

//    mockedBuilder = mock (MarkupBuilder.class)
//    mockedFormRender = mock (FormRender.class)
//    mockedResultSet = mock (List.class)

//    when (mockedResultSet.get(argThat(new IsInRange()))).thenReturn (item1)


/*
    def writer = new StringWriter ()
    builder = new MarkupBuilder (writer)
    builder.setDoubleQuotes(true)

    builder.form (id:"sectionForm", name:"sectionForm") {
      span (class:"titleForm", secName) {
      }
      br {}
      br {}
      table {
        input (id:"patId", name:"patId", value:patId, type:"hidden") {}
        input (id:"secId", name:"secId", value:secId, type:"hidden") {}

        renderItems (builder)
      }
    } // EO builder
*/
  } // EO given
}




// EASYB behaviors

scenario "Rendering an item text", {
  given "an item from the list", {
//    when (mockedResultSet.get(0)).thenReturn (item1)
    itemText = item1 as Object

  }

  when "an item is a text item", {


  }

  and "an item text is rendered", {


  }

  then "(verification) build HTML for a text item", {

  }

}



/*
scenario "Rendering a question", {
  given "an item question from the list", {
    theItem = item2
  }
  when "the item represents a question", {

  }
   and "the answer type is a simple type"

  then "render the question statement"
   and "render the answer type as a textfield"
}
*/


after "render the form buttons and close", {
  then "render close tags and buttons"
}
