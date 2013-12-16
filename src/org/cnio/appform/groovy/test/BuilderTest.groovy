/**
 * Created by IntelliJ IDEA.
 * User: bioinfo
 * Date: Oct 28, 2010
 * Time: 3:39:56 PM
 * To change this template use File | Settings | File Templates.
 */
import groovy.xml.MarkupBuilder

import java.util.regex.*

/*
class MyConfig{
  static x = null
  static nodes = {
     if (x == null)
         'theentry'(key: "ekis")
     else
        'theentry'( key: "${x}" )
  }
}

def scriptNodes = {
     if (MyConfig.x == null)
         'theentry'(key: "ekis")
     else
        'theentry'( key: "${MyConfig.x}" )
  }




def writer = new StringWriter ()
def builder = new MarkupBuilder (writer)
builder.setDoubleQuotes (true)


// MarkupBuilder spaces = TestEasyb.testBuilder(4)
def auxWrtr = new StringWriter ()
def aux = new MarkupBuilder (auxWrtr)
def clsre = {
  for (j in 0..3) {
    aux.space (sp:j)
  }
//  auxWrtr.toString()
  return aux
}



builder.times (count: 5) {
  for (i in 0..4) {
    time (value: "$i", i) {
//        clsre()
        MyConfig.x = 11+i
        builder.invokeMethod ('td', scriptNodes)
    }
  }
}

// def resNodes = builder.invokeMethod ('td', MyConfig.nodes)

def res = writer.toString()
println res

*/


def fooBuild (aBuilder) {
//  aBuilder.foo () {
    aBuilder.pub(attr:'hello', type:"text")
//  }
}

def writerBis = new StringWriter()
def builderBis = new MarkupBuilder(writerBis)
def createWrapper = { Closure c ->
  builderBis.Something(id: 123) {
    AnyInfo() {
//      c()
      (1..3).each {
        fooBuild(builderBis)
      }

    }
  }
}


createWrapper {
  builderBis.foo() {
    bar('hello')
  }
}

/*
createWrapper {
  builderBis.fuu() {
    crap(is: "shit")
  }
}
*/

builderBis.group (id:"anId") {
  input (type:"text", id:"1")
  mkp.yield (" xxx ")
  input (type:"text", id:"2")
//  input (type:"text", id:"3")
}
println "Envelope builder:"
println writerBis.toString()


println "================="
println "Pattern matching expression"
List<String> methods = ["buildForm", "renderChildren","renderTblChildren","renderMultiple","renderSingleQuestion"]
String regexp = "render\\D*"

methods.each {

  if (it.matches(regexp))
    println ("match: "+it)
  else
    println ("NO match: "+it)
}
println "EO Pattern matching"
