package org.cnio.appform.groovy.util

import org.codehaus.groovy.runtime.InvokerHelper
/**
 * Created by IntelliJ IDEA.
 * User: bioinfo
 * Date: Feb 7, 2011
 * Time: 7:42:14 PM
 * To change this template use File | Settings | File Templates.
 */

class Tracer implements GroovyInterceptable {

  Writer traceWriter = new PrintWriter (System.out)
  private int indent = 0


  Object invokeMethod(String name, Object args) {
/*    if (name.matches("render\\D*") && !name.matches("renderSi\\D*") &&
        !name.matches("renderEn\\D*")) {
//      writer.write("\n" + '  ' * indent + "before method '$name'")
*/
    if (name.compareToIgnoreCase("renderChildren") == 0 ||
        name.compareToIgnoreCase("renderTableChildren") == 0 ||
        name.compareToIgnoreCase ("renderRepeated") == 0 ||
        name.compareToIgnoreCase ("renderRepeatedBlock") == 0) {
      def prtArgs = "${args[0].itemord}, ${args[0].itemid}, ${args[0].itparent}"
      traceWriter.write "** $name (${prtArgs}) **\n"
      traceWriter.flush()
      indent++
    }

    def metaClass = InvokerHelper.getMetaClass(this)
    def result = metaClass.invokeMethod(this, name, args)
/*
    if (name.matches("render\\D*") && !name.matches("renderSi\\D*") &&
        !name.matches("renderEn\\D*")) {
      indent--
      writer.write("\n" + '  ' * indent + "after  method '$name'")
      writer.flush()
    }
*/
    return result
  }
}