package org.cnio.appform.groovy.util

import java.io.StringWriter
import groovy.xml.MarkupBuilder

import org.cnio.appform.entity.AbstractItem
import org.cnio.appform.util.HibController



public class FormRender { // extends Tracer {

  private int patId
  private int secId
  private int currentSec // currently not used; currentSec is got from jsp

// These are fields to highlight the stmts
  private String highIni, highEnd



/**
 * Store the nested items of children when rendering block of children.
 * When counting the items in a block, whether or not repeatable,
 * the children of children are NOT taken into account and, while
 * rendering each child item, it has to be checked for repeating or children and,
 * if got, its children, are added to nestedItems Set. The set size is further
 * added to the children size to set the pointer in the resultset in the right
 * position to continue the correct rendering.
 * This is ONLY used for methods renderChildren - renderTableChildren, as they
 * call each other to do a recursive render when needed. All issues above commented
 * apply in this case.
 * It has to be a <b>Set</b> in order not to repeat the children of children, which
 * are in the resultSet ONLY once even if they are children
 */
//  private Set nestedItems



/**
 * This member is intended for tracking the item id's which have been "used"
 * (rendered) but, as they are not in normal order, they can be after the
 * group of items which belong to and, in such a case, they will be rendered
 * again if not hold.
 * It is only updated in renderSingleItem(), renderMultipleQuestion () and
 * renderPage() methods
 */
  private Set trackedItems


  private TypesCache cacheTypes
  private StringWriter writer
  private MarkupBuilder builder
  private List resultSet

  private List newestItems

  private FormRenderUtil fru

  private final static String TBL_BGCOLOR = "#ffcc99"
  private final static String SELECT_STYLE = "width: 150px;"

  private final static String NO_ANSWER = "9999"
  private final static String MOCK_NO_ANSWER = "null"

  private final static JS_ADDELEM = "ctrl.addElem"
  private final static JS_RMVELEM = "ctrl.rmvElem"



/**
 * Switch the engine to render a preview or a performance. Default is false
 * (render a performance)
 */
  private final boolean isPreview


/**
 * The constructor for an undetermined number of items.
 * @param int patientId, the id of the subject the interview is to be performed to
 * @param int secId, the id of the current section of the interview
 * @param int thisSec, the order of the current section
 * @param MarkupBuilder builder, the builder
 * @param List rs, the resultset of the query, cached here like the types
 * @param TypesCache cache, the cache for the types belonging to the interview
 * 
 */
  public FormRender (int patientId, int secId, int thisSec,
                     List rs, TypesCache cache) {
    this.patId = patientId
    this.secId = secId
    this.currentSec = thisSec
    this.resultSet = rs


    writer = new StringWriter ()
    builder = new MarkupBuilder (writer)

    highIni = ""
    highEnd = ""

//    nestedItems = [] as Set
    trackedItems = [] as Set
    cacheTypes = cache

    isPreview = false
  }




/**
 * Constructor for just one item
 * @param Object item, an item to be rendered
 * @param int patientid, the id of the patient/subject
 * @param int secId, the id of the section containing the item
 */
  public FormRender (Object item, int patientid, int secId) {
    this.patId = patientid
    this.secId = secId

//    this.currentRow = 1
  }




 /**
  * Sets the render utility to call the utility methods, mostly boolean and
  * getter methods
  * @param FormRenderUtil fru, the render utility
  */
  public def setFormRenderUtil (FormRenderUtil fru) {
    this.fru = fru
    this.fru.setFormRender(this)
  }



 /**
  * Handy method to set the types cache in this class
  * @param typesCache, the types cached in an object 
  */
  public def setTypesCache (typesCache) {
    this.cacheTypes = typesCache
  }



  public def setNewestItems (theNewestItems) {
    this.newestItems = theNewestItems
  }

  def setPreview (boolean preview) {
    this.isPreview = preview
  }



  public boolean isPreview () {
    isPreview
  }

  

/**
 * This is a method to render a text item throw the builders. It takes into
 * account the highlight by calling the private method renderTdText
 * @param MarkupBuilder builder, the wrapper or global builder
 * @param Object text, is the current row which was identified as a text item
 */
  def renderSingleTextItem (Object text) {
    builder.tr () {
      if (text.highlight == AbstractItem.HIGHLIGHT_NORMAL)
        td (text.content.trim())

      else
        td () {
          fru.renderTdText (text, this.builder)
        }
    }
  }




/**
 * Render the form components (text or select) for a question with multiple
 * answers (i.e. day-month-year, would be three numbers for one answer). The
 * question statment is also rendered depending on the switch parameter
 *
 * @param Object item, the item (question) which have the question statement
 * @param List rows, the rows with the set of answers, both type and value
 * @param int ansNumber, the number of the question. This is necessary when
 * repeatable múltiple questions (i.e. a set of day-month-year answers)
 * @param boolean renderStmt, if true, the statement for the item will be rendered.
 * Necessary when rendering repeating answers
 */
  private renderMultipleQuestion (Object item, List rows, int ansNumber, boolean renderStmt) {
//    List items = getMultipleAnswers(item, rows, ansNumber)
// println ("MQ: [${item.itemord}] ${item.itemid} childOf ${item.itparent}")
// println "[multipleQuestion] Adding ${item.itemid} (nº ${item.itemord}) to trackedItems (${item.content.trim()}"
    trackedItems.add(item.itemid)
    if (renderStmt)
      this.renderSingleTextItem (item)

    builder.tr () {
      td () {
        rows.each {
// println ("renderMultipleQuestion -> ${it.itemid}:${it.answer_number}:${it.answer_order}")
          if (cacheTypes.isEnumType(it.idansitem)) {
            this.renderEnumTypeAnswer(it)
          }
          else
            this.renderSimpleAnswer (it)

          if (it.equals(rows.last()) == false) 
            mkp.yield (" - ")
        }
      }
    }
  }





/**
 * Render the textfield form component with the right attributes
 * @param question, the item object representing the question
 */
  private def renderSimpleAnswer (Object question) {

// Curation issues for questions with no answers at all (probably, legacy questions)
    String qAnsNumber = question.answer_number == null? 1: question.answer_number
    String qAnsOrder = question.answer_order == null? 1: question.answer_order
    String itemName = "q${question.itemid}-${qAnsNumber}-${qAnsOrder}"

    int maxlength, size
    String onblur, onfocus, style
    def ranges = ["-", "-", "-"] as String[]

// Check for answer value for legacy questions, prior to add the auto-answer    
// functionality by default
    String qValue = (String)question.thevalue
    boolean qValueNull = (qValue == null ||
                      qValue.compareTo(FormRender.MOCK_NO_ANSWER) == 0)
    def displayVal = qValueNull ? "":
      qValue.compareTo(FormRender.NO_ANSWER) == 0? "": qValue// EO check

    if (question.pattern != null && question.pattern.length() != 0)
      ranges = question.pattern.split (";")


    def ansType = cacheTypes.getSimpleType(question.idansitem)[0]
    def type = ansType.ansItName.toUpperCase()
    switch (type) {
      case HibController.TYPE_DECIMAL:
      case HibController.TYPE_NUMBER:
        maxlength = 10
        size = 6
        def jsType = (type == HibController.TYPE_DECIMAL)? "D": "N";

        onblur = "event.stopPropagation();"
        onblur += "intrvFormCtrl.sendItem('${jsType}', this, '${ranges[0]}', '${ranges[1]}', '${ranges[2]}')"
        onfocus = "intrvFormCtrl.onfocus(this.name)"
        break

      case HibController.TYPE_LABEL:
      default:
        maxlength = 4192
        size = 24
        onblur = "event.stopPropagation();intrvFormCtrl.sendItem('L', this)"
        onfocus = "intrvFormCtrl.onfocus(this.name)"
        break
    }
/*
String inputStr = "<!-- (name: ${itemName}, id:${itemName}, maxlength:${maxlength}, size:${size},"
   inputStr += " type:text, onblur:${onblur}, onfocus:${onfocus}, value: ${displayVal} -->"
println inputStr
*/
    builder.input (name:"${itemName}", id:"${itemName}", maxlength:"${maxlength}",
          size:"${size}", type:"text", onblur:"${onblur}", onfocus:"${onfocus}",
          value: "${displayVal}")
  }





/**
 * Render a select form component for a enumerated answer type. The components
 * of the enumerated types are retrieved from the types cache member
 * @param Object question, the question where the answer type is indicated
 */
  private def renderEnumTypeAnswer (Object question) {
    String qAnsNumber = question.answer_number == null? 1: question.answer_number
    String qAnsOrder = question.answer_order == null? 1: question.answer_order
    String itemName = "q${question.itemid}-${qAnsNumber}-${qAnsOrder}"

    int maxlength, size
    String onblur = "intrvFormCtrl.sendItem('S', this)"
    String onfocus = "intrvFormCtrl.onfocus(this.name)"

    def enumItems = cacheTypes.getEnumType(question.idansitem)
// in the case of question.thevalue could be null :-S
    String ansTheValue = question.thevalue == null? "": question.thevalue

    builder.select (id: itemName, name: itemName, style: FormRender.SELECT_STYLE,
                    onblur: onblur, onfocus: onfocus) {
      builder.option (value: 9999) {}
      enumItems.each {
        if (((String)it.typeValue).compareTo(ansTheValue) == 0)
          builder.option (value:it.typeValue, selected:"selected", it.typeName)
        else
          builder.option (value:it.typeValue, it.typeName)
      }
    }

  }




/**
 * Render a single question, which is a question with:
 * <li>not repeatable</li>
 * <li>without children</li>
 * The render will be a text rendering first (for the question statement) and
 * then a custom form element render to hold the answer (textfield or combobox):
 * <br/>
 * <code>
 * <tr><td>
 * <input type="text" onfocus="intrvFormCtrl.onfocus(this.name)"
 * onblur="event.stopPropagation();intrvFormCtrl.onBlur(this.name)"
 * value="" size="31" maxlength="4192"
 * id="q16485-1-1" name="q16485-1-1"
 * style="border: 1px solid darkblue;
 * background: white none repeat scroll 0% 0%; -moz-background-clip: border; -moz-background-origin: padding; -moz-background-inline-policy: continuous; color: black;">
 * </td></tr>
 * </code>
 *
 * @param Object currentRow, the question item
 */
  def renderSingleQuestion (Object currentRow, boolean renderStmt) {
//    def simples = cacheTypes.getSimpleType(typeId)

    if (renderStmt)
      this.renderSingleTextItem (currentRow)

// By legacy reasons, can be questions without question code and
// no answer item related either, such that idansitem will be null in query
// This is a protection to avoid a sort of nullpointerexception
    if (currentRow.idansitem != null)  {
      builder.tr () {
        td () {
          if (cacheTypes.isEnumType(currentRow.idansitem))
            this.renderEnumTypeAnswer(currentRow)
          else
            this.renderSimpleAnswer (currentRow)
        }
      }
    }

  }




/**
 * Render a single item, either a text item or a question item
 * @param Object item, the item to render
 */
  def renderSingleItem (Object item) {
// println ("SI: [${item.itemord}] ${item.itemid} childOf ${item.itparent}")
// println "[singelItem] Adding ${item.itemid} (nº ${item.itemord}) to trackedItems (${item.content.trim()}"
    trackedItems.add(item.itemid)
    if (fru.isText(item))
      this.renderSingleTextItem(item)

    else
      this.renderSingleQuestion(item, true)
  }






/**
 * Render all repeated items for the parent item. This method is intended ONLY
 * for repeated items WITHOUT CHILDREN (so, not applicable to repeating blocks).
 * It is only used with single or multiple, and repeatable, questions. Repeatable
 * blocks are rendered by using <code>renderRepeatedBlock</code> method
 *
 * @param Object parent, the "original" or first item, which will be always answered
 * @param List repeated, the rest of this item's answer. It can be empty
 */
  private def renderRepeatedItem (Object parent, List repeated) {
    int i = 0
// Get the number of item answers per answer (i.e. glasses per hour => number will be 2)
    List multiAnswers = fru.getMultipleAnswers(parent, repeated, 1)
    int numMulAnswers = multiAnswers.size()

// Weird case: a text item with no children and repeatable.
// In this case, parent and repeated[0] are the same, repeated.size is 1
    if (repeated.size() == 1 && repeated[0] == parent && fru.isText(parent)) {
      this.renderSingleItem(parent)
      return
    }


    for (i=0; i < repeated.size(); i+=numMulAnswers) {
      def rows = repeated[i..i+numMulAnswers-1]
      boolean renderStmt = i == 0

      int contAnswer = numMulAnswers == 0? (int)1: ((int)i/numMulAnswers)

// the last row has to be rendered differently that the others as the buttons
// has to be added in the same row!!!!
      if (i+numMulAnswers == repeated.size()) {
        if (renderStmt)
          this.renderSingleTextItem (rows[0])

        builder.tr () {
          td () {
            rows.each {
              if (cacheTypes.isEnumType(it.idansitem))
                this.renderEnumTypeAnswer(it)
              else
                this.renderSimpleAnswer (it)

// This is to see whether or not the buttons have to be added in this iteration
              if (it.equals(rows.last())) {

                String ansOrder = it.answer_order == null? 1: it.answer_order
                String ansNum = it.answer_number == null? 1: it.answer_number
                def newId = "q"+it.itemid+"-"+ansNum+"-"+ansOrder
                def btnAddId = i==0? "btn-${newId}": "add-${newId}"

                mkp.yieldUnescaped ("&nbsp;")
                input (type:"button",
                  onclick:FormRender.JS_ADDELEM+"('${newId}')", id:"${btnAddId}",
                  name:"${btnAddId}", value:"+")

                if (i>0) {
                  mkp.yieldUnescaped ("&nbsp;")
                  input (type:"button",
                    onclick:FormRender.JS_RMVELEM+"('${newId}')", id:"rmv-${newId}",
                        name:"rmv-${newId}", value:"-")
                }
              }
              else
                mkp.yield (" - ")
            } // EO each

          }
        } // EO builder
      }
      else // a case where parent and rows are the same content
        this.renderMultipleQuestion(parent, rows, contAnswer, renderStmt)

    } // EO for

  }





/**
 * This method renders ONLY the table for the children elements of an item.
 * AS THERE CAN BE CHILDREN OF CHILDREN (AT LEAST CONFIRMED IN ONE QUESTIONNAIRE)
 * EVERY ELEMENT HERE HAS TO BE CHECKED FOR CHILDREN AND REPEATEABLE
 *
 * @param List children, the list of children items to render
 * @param String tblName, the name for the table
 * @parem int numAnswer, in the case the children belongs to a repeateable parent,
 * @param addBtns, true if buttons at the end of the table have to be added.
 * this is necessary in order to properly render children with answers more than 1
 *
 * @return the number of NESTED items in order to set the right pointer position
 * in caller method
 */
  private def renderTableChildren (List children, String tblName,
                                   int numAnswer, boolean addBtns) {
    def i = 0
    def numNestedItems = 0
/*
println "**** renderTableChildren: repeatable group size: ${children.size()}"
children.each {
  println "#${numAnswer}# ${it.itemord} - ${it.codq} (${it.itemid}-${numAnswer}-${it.answer_order})" 
}
println ("====================================")
*/

    builder.table (id:"${tblName}", name:"${tblName}", bgcolor:FormRender.TBL_BGCOLOR) {

      for (i=0; i < children.size(); i++) {
        def item = children[i]

// println ("i: $i")
// Check for repeating
        if (fru.isRepeatable (item)) {
          def repeated = fru.getRepeatedItems(item, resultSet)
// Add all items to the nested items Set.
//          nestedItems.addAll(repeated)

          this.renderRepeated(item, repeated)
          if (!fru.isSingleQuestion(item, children)) {
            def multQues = fru.getMultipleAnswers(item, children, numAnswer)
            i += multQues.size()
          }
            
          i += repeated.size()-1
        }

// Check for children of children
        else if (fru.hasChildren(item, resultSet)) {
          def childrenBis = fru.getChildren(item, resultSet)
// Similar to the previous if block, add the items into the nestedItems Set
//          nestedItems.addAll(childrenBis)
// println ("PTC: (${item.itemord}-) ${item.itemid} -> ${childrenBis.itemid} || i: $i")
// println "renderTableChildren: children: $children.itemid <=> childrenBis: $childrenBis.itemid || i: $i"
          def childSize = this.renderChildren (item, childrenBis, numAnswer)

// This is to skip forward the already rendered children, as they are in the
// same set than the only children
          if (children.containsAll(childrenBis))
            i += childSize-1

// When the previous condition is not satisfied but a multiple question is
// found (item is a multiple question with children) then
// the index i has to be incremented with the number of multiple answers
// in order to not repeat the group as many answers as the question has
          if (!fru.isSingleQuestion(item, resultSet)) {
            def multipleQs = fru.getMultipleAnswers(item, children, numAnswer)
            i += multipleQs.size() - 1
// println "after adding multipleQs=$multipleQs index is i=$i"
          }

          numNestedItems = childSize
        } // EO fru.hasChildren(...)

        else if (fru.isText(item) || fru.isSingleQuestion(item, children))
          this.renderSingleItem (item)

        else { // only multiple questions
          def questions = fru.getMultipleAnswers(item, children, numAnswer == null?0: numAnswer)
// This is a guard to protect the loop in the case one multiple question is not
// in this set of questions (children variable) and be able to continue rendering 
          if (questions.size() > 1)
            i += questions.size()-1
          else
            continue;

          this.renderMultipleQuestion (item, questions, numAnswer == null? 0: numAnswer, true)
        }

      } // EO for/while, children loop

      if (addBtns) {
        tr () {
          td (align:"right") {

            tblName = tblName.substring(0, tblName.indexOf("-"))
            input (type:"button",
                  onclick: FormRender.JS_ADDELEM+"('${tblName}')",
                  id:"btn-${tblName}",
                  name:"btn-${tblName}", value:" + ")

            if (numAnswer > 1) {
              mkp.yieldUnescaped ("&nbsp;")
              input (type:"button",
                    onclick: FormRender.JS_RMVELEM+"('${tblName}')",
                    id:"btn-${tblName}",
                    name:"btn-${tblName}", value:" - ")
            }

          }
        }
      }
    }

// println "EO renderTblChild: nestedItems -> $numNestedItems"
    builder.br () {}

    numNestedItems
  }






/**
 * Render the children items of a parent item.
 * This is a recursive method!!!!!
 * Recursion has to be there as any item can have "nested" children, so this
 * method should be called itself to render the "nested" items
 * @param Object parent, the parent object
 * @param List children, the list of containing the children
 * @param int numRep, the number of repeating block, in the case of a repeatable question
 * @return the number of the children plus the nested items inside the group
 * if any
 */
  private int renderChildren (Object parent, List children, int numRep) {

    def tblName = "t${parent.itemid}-children"
    int countNested = 0, multipleQuestions = 0

// Render the parent, which can be a single item or a multiple answers item
    if (fru.isText(parent) || fru.isSingleQuestion(parent, resultSet))
      this.renderSingleItem (parent)

    else { // only multiple questions
      def questions = fru.getMultipleAnswers(parent, resultSet,
                    parent.answer_number == null? 0: parent.answer_number)
      multipleQuestions += questions.size()-1
      this.renderMultipleQuestion (parent, questions, 1, true)
    }
// println "** renderChildren: parent: $parent.itemid <=> childrenBis: $children.itemid"
// render the children
    builder.tr () {
      td () {
        countNested += this.renderTableChildren (children, tblName, numRep, false)
      } // EO td
    } // EO tr

    def fullCounter = children.size() + countNested + multipleQuestions
// println "** renderChildren: fullCount: $fullCounter"
    fullCounter
  }







/**
 * Keeps the bussiness logic to render repeated blocks of answers
 * @param Object parent, the parent object for the block enclosing the items
 * @param List repeated, the list of all repeated items with their answersw
 *
 * @return number of elements processed, NOT the number of elementes rendered
 * as the latter is greater than the former
 */
  private def renderRepeatedBlock (Object parent, List repeated) {
// Get a block for each repeated answer and build a table with him
    def repGroup // list of answers for any answer_number
    def textsGroup // list of text items, will be injected in answers list

// list of the next iteration group. this is useful to see whether or not
// the plus and minus buttons have to be added into the last group. the switch
// to indicate whether or not add the buttons
    def nextGroup
    boolean addBtns

// Questions added after the interview was first performed and unable
// to retrieve as they does not have any answer (not present in pat_gives_answer2question
// table). Only blocks of repeatable are included in here
    def newestGroup = []

    def numRep = 1 // current question number, starting in 1
//    def itemId = parent.itemid

    String tblName = "t${parent.itemid}-children"
    int numItemsProcessed = 0

// Get the first group, texts (no answer_number for them) included
    repGroup = repeated.findAll { it -> it.answer_number == numRep || it.answer_number == null }
// Get the question statements (text items!!) necessary for every repeatable
// This will be added to every group greater than the first one
    textsGroup = repGroup.findAll { it -> it.answer_number == null }
    nextGroup = repeated.findAll { it.answer_number == numRep+1 }

// get the newest items inside this repgroup
    newestGroup = this.newestItems.findAll { it.itparent == parent.itemid }
    repGroup = fru.mergeRepGroups(newestGroup, repGroup, numRep)
// newestGroup.each { println "## ${it.itemord} - ${it.codq} (${it.itemid})" }

    addBtns = (nextGroup == [] || nextGroup == null)

    while (repGroup != null && repGroup != []) {
// if higher than first group of answers, inject the texts in the list
      numItemsProcessed += repGroup.size()
      if (numRep > 1) {
        repGroup += textsGroup
        repGroup.sort { it.itemord }
      }


// TODO VER POR QUÉ AQUÍ HAY 14 ELEMENTOS Y DESSPUES NO LOS PONE EN EL HTML
// TODO VER EL PUTO RENDER_TABLE_CHILDREN ENTONCES :-/
// println "!#!puto repGroup size: ${repGroup.size}"
      builder.tr () {
        td () {
          numItemsProcessed += this.renderTableChildren (repGroup, tblName, numRep, addBtns)
        }
      }

      numRep++
      repGroup = repeated.findAll { it.answer_number == numRep }
      nextGroup = repeated.findAll { it.answer_number == numRep+1 }
      addBtns = (nextGroup == [] || nextGroup == null)


      newestGroup = this.newestItems.findAll { it.itparent == parent.itemid }
//      newestGroup = fru.diffGroups(newestGroup, repGroup)
      if (repGroup != null && repGroup != [])
        repGroup = fru.mergeRepGroups (newestGroup, repGroup, numRep)

    } // EO while repeating groups

// println "renderRepBlock: numItemsProcessed -> $numItemsProcessed"
//    numItemsProcessed
  }






/**
 * Render the "parent" item and their repeated elements. The elements can be
 * the (repeated) answers for a question (in the case of parent is a simple
 * question item) or the repeated group of children in the case of parent is
 * a repeatable item and, besides, has children.
 * This two cases must be differentiated as the render is different between them
 * @param Object parent, the object which has the repeatable mark
 * @param List repeated, the list of repeated answers
 *
 * @return the number of items processed in order to right process the next
 * elements of the main resultset
 */
  def renderRepeated (Object parent, List repeated) {

    int itemsProcessed = 0
    if (fru.hasChildren(parent, repeated)) {

//      def multipleQuestions = 0
      if (fru.isSingleQuestion(parent, resultSet))
        this.renderSingleItem(parent)

      else {
        def questions = fru.getMultipleAnswers(parent, resultSet,
                    parent.answer_number == null? 0: parent.answer_number)
        this.renderMultipleQuestion (parent, questions, 1, true)
//        multipleQuestions = questions.size()
      }

      this.renderRepeatedBlock(parent, repeated)
//      itemsProcessed = this.renderRepeatedBlock(parent, repeated)
//      itemsProcessed += multipleQuestions
    }

    else { // there is no groups, this is only a question with one answer item
      this.renderRepeatedItem(parent, repeated)
      itemsProcessed = repeated.size()
    } // EO

  }





 /**
  * Main method to render the resultset. Loop over the resultSet and, depending
  * on the current item, calls out some other methods to make the render properly
  */
  def renderPage () {
    int i;
    int rsSize = resultSet.size()
    
    for (i=0; i < resultSet.size(); i++) {
      def item = resultSet[i]
// println ("renderPage: ${item}")      
      if (trackedItems.contains(item.itemid))
        continue

      if (fru.isRepeatable(item)) {
        def repeated = fru.getRepeatedItems(item, resultSet[i..resultSet.size()-1])

        this.renderRepeated(item, repeated)
/*        i += repeated.size() + nestedItems.size()
        if (!fru.hasChildren(item, resultSet[i..resultSet.size()-1]))
          i-- // this is because parent item can not be taken into account
*/
        def nextOne = resultSet[i+1..resultSet.size()-1].find {
          it.itparent == null && it.itemid != item.itemid
        }
        i = nextOne == null? rsSize: resultSet.indexOf(nextOne)-1

      }
  
      else if (fru.hasChildren(item, resultSet)) {
        def children = fru.getChildren(item, resultSet)
        this.renderChildren(item, children, 1)
//        i += children.size() + nestedItems.size()
        def nextOne = resultSet[i+1..resultSet.size()-1].find {
          it.itparent == null // a block finishes in the next element w/o parent
        }
        i = nextOne == null? rsSize: resultSet.indexOf(nextOne)-1

      }
  
      else {
        if (fru.isText(item) || fru.isSingleQuestion(item, resultSet))
          this.renderSingleItem (item)
    
        else { // only multiple questions
          def questions = fru.getMultipleAnswers(item, resultSet,
                        item.answer_number == null? 0: item.answer_number)
          i += questions.size()-1
          this.renderMultipleQuestion (item, questions, 1, true)
        }  
      }

//      println "Next index: $i"
//      nestedItems.clear()
    } // EO for loop
    trackedItems.clear()

    writer.toString()
  }
  
  
  


 /**
  * Builds a form from the list of results from
  *
  */
  public String buildForm () {

//    def writer = new StringWriter ()
//    builder = new MarkupBuilder(writer)
//    builder.setDoubleQuotes (true)

//    def fetcher = new ItemsFetcher ("appform", "localhost", "gcomesana")
//    def rowSet = fetcher.getResultSet(50, "157", "157081003", 3)

    def secName = resultSet[0][4]
    secName = resultSet[0].secname

    def theForm = ""
    builder.form (id:"sectionForm", name:"sectionForm") {
      span (class:"titleForm", secName) {
      }
      br {}
      br {}
      table {
        input (id:"patId", name:"patId", value:this.patId, type:"hidden") {}
        input (id:"secId", name:"secId", value:this.secId, type:"hidden") {}

        renderPage ()

// bottom part of the form
        br {}
        tr () {
          td () {
            input (name:"currentsec", id:"currentsec",
                value: this.currentSec, type:"hidden") {}
            input (name:"btnSend", value:" Continuar ",
                onclick: "intrvFormCtrl.send(this.form);" ,type:"button") {}
            input (name:"btnEnd", value:" Guardar ",
                onclick: "intrvFormCtrl.finish(this.form, 2);", type:"button") {}
          }
        }


      } // EO table
    } // EO form

//    builder
//    println (writer.toString())
    writer.toString()
  }




  public static StringWriter testBuilder (int n) {
    def writer = new StringWriter ()
    def builder = new MarkupBuilder(writer)
    builder.setDoubleQuotes (true)

    builder.spaces {
      (0..n).each {i -> space(dim: i, i) }
    }

    return writer
//    return builder
  }

}