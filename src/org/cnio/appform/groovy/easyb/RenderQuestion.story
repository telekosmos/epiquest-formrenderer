
import org.cnio.appform.util.HibController
import org.cnio.appform.entity.*

import org.cnio.appform.groovy.util.FormRender
import groovy.xml.MarkupBuilder

before "retrieve resultset", {
  given "all parameters are provided"
    and "the db connection is well configured", {
      println "execute query..."

      item1 = [itemord:1, idq: null, content:"Me gustaría que pensara en todos los trabajos que haya tenido en la vida.", itemid: 156,codq: "null", highlight:1, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object
      item2 = [itemord:2, idq: 157, content:"B1. Ha trabajado, durante 6 meses o más, solo en turno de noche?", itemid: 157,codq: "B1", highlight:0, secname:"B. Trabajo en turnos",secid:100,answer_number:1,answer_order:1,codpatient:"157081003",idpat:3060,thevalue:"2",idansitem:1403]  as Object
      item3 = [itemord:3, idq: null, content:"ENTREVISTADOR: Se considera turno de noche cualquier trabajo en que se entra a partir de las 8 de la noche y finaliza después de las 4 de la mañana", itemid: 158,codq: "null", highlight:2, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object
      item4 = [itemord:5, idq: 159, content:"B2. ¿Ha trabajado, durante al menos 6 meses, alternando turno de noche y otros turnos?", itemid: 159,codq: "B2", highlight:0, secname:"B. Trabajo en turnos",secid:100,answer_number:1,answer_order:1,codpatient:"157081003",idpat:3060,thevalue:"2",idansitem:1403] as Object
      item5 = [itemord:6, idq: null, content:"ENTREVISTADOR: Otros turnos pueden ser: mañana o tarde", itemid: 160,codq: "null", highlight:2, secname:"B. Trabajo en turnos",secid:100,answer_number:null,answer_order:null,codpatient:"null",idpat:null,thevalue:"null",idansitem:null] as Object

      resultSet = [item1, item2, item3, item4, item5]

      secName = resultSet[1].secname
      patId = resultSet[1].idpat
      secId = resultSet[1].secid

      writer = new StringWriter ()
      builder = new MarkupBuilder (writer)
    }
}



scenario "Rendering a question", {
  given "an item question from the list", {
    theItem = item2
  }
  when "the item represents a question", {
    theItem.idq == null

  }
  and "the answer type is a simple type" {
    theItem.idansitem == HibController.TYPE_DECIMAL ||
    theItem.idansitem == HibController.TYPE_NUMBER ||
    theItem.idansitem == HibController.TYPE_LABEL
  }

  then "render the question statement" , {
    FormRender.renderSingleTextItem (builder, theItem)

  }
   and "render the answer type as a textfield", {
     FormRender

   }
}





after "render the form buttons and close", {
  then "render close tags and buttons"
}