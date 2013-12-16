package org.cnio.appform.groovy.util

import groovy.xml.MarkupBuilder
import org.cnio.appform.entity.AbstractItem

/**
 * Created by IntelliJ IDEA.
 * User: bioinfo
 * Date: Feb 1, 2011
 * Time: 1:28:56 PM
 * To change this template use File | Settings | File Templates.
 */

public class FormRenderUtil {

  private final FormRender fr


  public FormRenderUtil () {
  }


  public def setFormRender (FormRender fr) {
    this.fr = fr
  }



// BOOLEAN METHODS /////////////////////////////////////////////////////////
/**
 * Checks if the item is a text item or a question item. This is done by
 * checking the field idq from the retrieved data
 * @param Object item, the item as retrieved from the database
 *
 * @return true if the item is considered a text item; false otherwise (=>
 * should be a question item)
 */
  public boolean isText (Object item) {
    boolean isText = false
    isText = item.idq == null

    isText
  }






/**
 * Checks if this is a question with single answer, this means, only ONE ANSWER
 * ITEM IS POSSIBLE.
 * To check it out, another row with the same idquestion has to
 * be found, in such a case the question is not a single question (more than ONE
 * item answer has to be provided, see Numbers sheet)
 * The requirements to be accept as a question with multiple answers is:
 * - to have same qId, itemId and
 * * answer_number is null in one of then but answer_order is higher
 * * answer_number is the same
 * @param item, the item to check whether or not is a single question
 * @param rows, the resultset
 * @return true if the item is a single-answer question; false otherwise
 */
  public boolean isSingleQuestion (Object item, List rows) {
    def itemid = item.itemid
    def idq = item.idq
    def number = item.answer_number
    def order = item.answer_order
    def codq = item.codq

// This could be maybe a findAny { } instead a findAll
// There are several conditions for a question to be single or not
// All this stuff is addressed to set the proper set of questions when nulls are involved
    def single = rows.findAll { it -> (it.itemid == itemid) && (it.idq == idq) &&
      (it.answer_number == number ||
        (it.answer_order > order && it.answer_number == null) ||
        (it.codq == codq))
    }

    single.size() == 1
  }




/**
 * Just checks if the item is repeatable, for itself or for his children
 * @param Object item, the item
 * @return true if the field itrep is different from 0
 */
  public boolean isRepeatable (Object item) {
    item.itrep == 1
  }




/**
 * Checks ONLY if the item is a parent item. This check is done by using the
 * groovy collections method <code>any</code>:
 * the method returns true if ANY element in the list meets the closure. In this
 * case, the closure checks if the item id is equals to the parent item id for
 * the entire collection
 *
 * @param Object item, the current item to render
 * @param List rows, the resultset as retrieved from database
 * @return true if the item has children; false otherwise
 */
  public boolean hasChildren (Object item, List rows) {

    def idRef = ((String)item.itemid).toInteger()

    rows.any {idRef == it.itparent}
  }
// EO BOOLEAN METHODS ////////////////////////////////////////////////////////




// GETTER METHODS ////////////////////////////////////////////////////////////
/**
 * Returns a list with all answer items for a multiple-answers question.
 * I.e. when asking for dates (year, month, day) this method would return a
 * three element (for year, month and day) list for the answer number as
 * indicated by ansNumber
 * @param Object item, the current item to render
 * @param List rows, the resultset as retrieved from database
 * @param int ansNumber, this is the number of the answer when the parent item
 * (or this item) is repeatable (e.g., when several dates are requested and
 * the result is an undefined number of answers, 3 items each)
 * 
 * @return the list of the items; default is 1 if item is not a multiple question
 */
  public List getMultipleAnswers (Object item, List rows, int ansNumber) {
    def itemid = item.itemid
    def idq = item.idq

    def answers

    if (fr.isPreview())
      answers = rows.findAll { // there is no answer_number if preview
        it -> (it.itemid == itemid) && (it.idq == idq)
      }
    else
// it.answer_number is null => ERR
      answers = rows.findAll {
        it -> (it.itemid == itemid) && (it.idq == idq) &&
              (ansNumber == 0 ||
               (it.answer_number == null? 1: it.answer_number) == ansNumber)
      }

    answers.asList()
  }



/**
 * Gets the children's list of the item, which is the parent item
 * @param Object item, the current item to render
 * @param List rows, the resultset as retrieved from database
 * @return the list of the items
 */
  public List getChildren (Object item, List rows) {
    def children = rows.findAll { it.itparent == item.itemid }
    children?.asList()
  }




/**
 * Retrieves all repeat items yielded as answers, where <code>parent</code> is
 * the repeatable item. To do this in a programatic way, a difference between
 * whether or not the item is container is set.<br/>
 * If the item is a container all its children are retrieved.<br/>
 * If the item is a question (but repeatable) the repeated items are the
 * items which have same itemid (but different answer_number and/or
 * answer_order.<br/>
 * @param Object parent, the repeateble item detected
 * @parem List rs, the whole resultset
 *
 * @return a list with the repeated answer ordered by the answer_number
 */
  public List getRepeatedItems (Object parent, List rs) {

    def repeated
    if (hasChildren (parent, rs))
      repeated = getChildren (parent, rs)

    else
      repeated = rs.findAll { it.itemid == parent.itemid }

// This "mess" is necessary as there can be text items without answer_number
// and answer_order neither, in such a way they would not be sorted
    repeated.sort { a, b ->
      def numA, numB
      numA = a.answer_number == null? 1: a.answer_number
      numB = b.answer_number == null? 1: b.answer_number
      numA <=> numB
    }

    repeated
  }

// EO GETTER METHODS /////////////////////////////////////////////////////////



/**
 * Render only the text of an item only, just adding decorations depending on
 * item style
 * @param Object currentRow, the object row to render as an item text
 */
  def renderTdText (Object currentRow, MarkupBuilder builder) {
    switch (currentRow.highlight) {
      case AbstractItem.HIGHLIGHT_BOLD:
            builder.b (currentRow.content.trim())
            break

      case AbstractItem.HIGHLIGHT_ITALIC:
            builder.i (currentRow.content.trim())
            break

      case AbstractItem.HIGHLIGHT_UNDERLINE:
              builder.u (currentRow.content.trim())
            break;

      case AbstractItem.HIGHLIGHT_ITABOLD:
            builder.i {
              b (currentRow.content.trim())
            }
            break;

      case AbstractItem.HIGHLIGHT_UNDERBOLD:
            builder.u {
              b (currentRow.content.trim())
            }
            break;

      case AbstractItem.HIGHLIGHT_UNDERITAL:
            builder.u {
              i (currentRow.content.trim())
            }
            break;

      case AbstractItem.HIGHLIGHT_FULL:
            builder.b {
              i {
                u (currentRow.content.trim())
              }
            }
            break;

      case AbstractItem.HIGHLIGHT_NORMAL:
      default: builder ( currentRow.content.trim() )
              break;
    }
  }




/**
 * Makes a render of a item, which can be a text item, single-answer question or
 * multiple-answer question (NOT repeateble!!!). This is handy proxy method
 * which only calls the methods that performs the logic
 * @param Object item, the item which is gonna be "classified"
 * @param List rs, the resultset, necessary to check if is single or multiple question
 * @param boolean renderStmt, when rendering questions, true if statement has to be
 * rendered; false otherwise
 */
  def renderItem (Object item, List rs, boolean renderStmt) {
    if (isText(item) || isSingleQuestion(item, resultSet))
      fr.renderSingleItem (item)

    else { // only multiple questions
      def questions = getMultipleAnswers(item, rs,
                    item.answer_number == null? 0: item.answer_number)
      i += questions.size()-1
      fr.renderMultipleQuestion (item, questions, 1, true)
    }

  }




  private boolean containsField (List reps, Object item) {
    boolean belongsToReps = false
    reps.each {
      belongsToReps = (it.itemid == item.itemid &&
                       it.answer_order == item.answer_order)? true: belongsToReps | false
    }
    belongsToReps
  }




 /**
  * Gets the elements which are in the newest list but not in the reps list, based
  * on the containsField method semantics (see above).
  * For this case, conatinsField takes itemid and answer order into account
  * @param newest, the theoretically largest list
  * @param reps, the theoretically smallest list
  *
  * @return a list with the elements in first list but not in second one
  */
  def diffGroups (List newest, List reps) {
    List diffList = []

    newest.each {
      if (containsField (reps, it) == false) {
        diffList << it
      }
    }

    diffList
  }





/**
 * Merge the items in the first list (newest) with the second one. It is supposed
 * both lists are ordered.
 * @param newest, the list of all items in the repeated blocks, included those added
 * after questionnaire composition and performing (which is the real cause this
 * was implemented :-S)
 * @param reps, the current repeated items to process
 * @param numAnswer, the current answer number
 *
 * @return the current block of repeated items with the new items added
 */
  def mergeRepGroups (List newest, List reps, int numAnswer) {
    def result = reps.clone()

    int insertIdx = 0
    newest.each { it ->
      if (!containsField (reps, it)) {
// put it in reps, as it is not in the current block of repeated
        def beginIdx = insertIdx
        def endIdx = result.size-1
        it.answer_number = numAnswer
        if (endIdx < beginIdx)
          result << it
        else
          result = result[0..insertIdx-1] + it + result[beginIdx..endIdx]
//        result.putAt (insertIdx, it)
      }
      insertIdx++
    }

    result
  }
  
}