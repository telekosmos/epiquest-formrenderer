

import org.cnio.appform.groovy.util.*

class TestNested {


  private List rs
  private FormRenderUtil fru
  private Set nestedItems

  public TestNested (List paramRS) {
    rs = paramRS
    fru = new FormRenderUtil ()

    nestedItems = [] as Set

  }


  def processTblChildren (List children) {

    int i
    int numNestedItems = 0

    for (i=0; i < children.size(); i++) {
      def item = children[i]
println ("PTC: [${item.itemord}] ${item.itemid} of ${children.itemid}")
      if (fru.hasChildren(item, rs)) {
        def childrenBis = fru.getChildren(item, rs)
        nestedItems.addAll(childrenBis)

println ("PTC: [${item.itemord}] ${item.itemid} -> ${childrenBis.itemid}")
        def childSize = processChildren (item, childrenBis)
        i += childSize-1

println ("PTC: [${item.itemord}] ${item.itemid}: next i=$i")
        numNestedItems = childSize
      }
      else
        processItem (item)
    } // EO for
    numNestedItems
  }



  def processTblChildrenBis (List children) {

    int numNestedItems = 0

    while (!children.isEmpty()) {
      def item = children.remove(0)

println ("PTCB: [${item.itemord}] ${item.itemid} of ${children.itemid}")
      if (fru.hasChildren(item, rs)) {
        def childrenBis = fru.getChildren(item, rs)
        nestedItems.addAll(childrenBis)

println ("PTCB: [${item.itemord}] ${item.itemid} -> ${childrenBis.itemid}")
        def childSize = processChildren (item, childrenBis)
//        i += childSize-1

println ("PTCB: [${item.itemord}] ${item.itemid}")
        numNestedItems = childSize
      }
      else
        processItem (item)
    } // EO while

    numNestedItems
  }



  def processChildren (Object parent, List children){
    int countNested = 0
 println ("PC: [${parent.itemord}] ${parent.itemid} -> ${children.itemid}")
    
    processItem (parent)
    countNested = processTblChildrenBis (children)

    children.size() + countNested

  }


  def processItem (Object item) {
    println ("PI: [${item.itemord}] ${item.itemid} childOf ${item.itparent}")
  }


  def processRs () {

    int rsSize = rs.size()
    int i
    for (i=0; i < rs.size(); i++) {
      def item = rs[i]

      if (fru.hasChildren(item, rs)) {
        def children = fru.getChildren(item, rs)
        this.processChildren(item, children)
//        i += children.size() + nestedItems.size()
        def nextOne = rs[i+1..rs.size()-1].find {
          it.itparent == null // a block finishes in the next element w/o parent
        }
        i = nextOne == null? rsSize: rs.indexOf(nextOne)-1
      }

      else {
        processItem (item)
      }
      nestedItems.clear()
    }
  } // EO loop for

}


def dbServer = "padme.cnio.es"
def dbName = "appform"
def dbUser = "gcomesana"

def intrvId = 2100 // IE
def pat = "157851074"
def secOrder = 5 // 8
ItemsFetcher iFetcher = new ItemsFetcher (dbName, dbServer, dbUser)
List<Object[]> resultSet = iFetcher.getResultSet(intrvId, pat, secOrder)
patId = iFetcher.getPatientId(pat)
iFetcher.close()


TestNested test = new TestNested(resultSet)
test.processRs()
