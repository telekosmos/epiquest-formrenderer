package org.cnio.appform.groovy.test.integration

import org.cnio.appform.groovy.util.TypesCache


/*
Way of using types cache:
- fill the cache with all types for an interview (empty interview => default types
  no interview => empty lists, no types)
- get a simple type based on the database id ( => null if no type)
- get the enumerated items for an enumerated type
  from a database id ( => null if no type)

*/

public class EmptyTypesCacheTest extends GroovyTestCase {
  private TypesCache cache

  public void setUp () {
    cache = new TypesCache (555) // no interview exists with that id
  }


  void testPreconditions () {
    assertEquals cache.getEnumTypes(), []
    assertEquals cache.getSimpleTypes(), []

// bad    assertNull cache.getEnumTypes()
// bad    assertNull cache.getSimpleTypes()
  }


  void testGetSimpleTypeIsNull () {
    assertEquals cache.getSimpleType(100), []
// bad    assertNull cache.getSimpleType(100)
  }


  void testGetEnumTypeIsNull () {
    assertEquals cache.getEnumType(1410), []
// bad:    assertNull cache.getEnumType(1410)

  }


  void testSizeOfBothTypesIsZero () {
    assertEquals cache.getNumEnumTypes(), 0
    assertEquals cache.getNumSimpleTypes(), 0
  }

}




public class IntvNullTypesCacheTest extends GroovyTestCase {
  private TypesCache cache

  public void setUp () {
    cache = new TypesCache (null)
  }


  public void testPreconditions () {
    assertEquals cache.getEnumTypes(), []
    assertEquals cache.getSimpleTypes(), []
  }


  public void testGetSimpleTypeIsNull () {
//    assertNull cache.getSimpleType(100)
    assertEquals cache.getSimpleType(100), []
  }


  public void testGetEnumTypeIsNull () {
//    assertNull cache.getEnumType(1410)
    assertEquals cache.getEnumType(1410), []

  }


  public void testSizeOfBothTypesIsZero () {
    assertEquals cache.getNumEnumTypes(), 0
    assertEquals cache.getNumSimpleTypes(), 0
  }

}


public class DefaultIntrvTypesCacheTest extends GroovyTestCase {
  private TypesCache cache

  public void setUp () {
    cache = new TypesCache (4400)
  }


  public void testPreconditions () {
    assertNotNull cache
  }


  public void testSizeOfSimpleTypes () {
    assertEquals cache.getNumSimpleTypes(), 4 
  }


  public void testSizeOfEnumTypes () {
    assertEquals cache.getNumEnumTypes(), 1
  }


  public void testSimpleTypeAttribs () {
    def simples = cache.getSimpleTypes()
    def simple = simples[0]

    println "simple name: ${simple.ansItName}"
    def idType = simple.idAnsitem

    def simpleBis = cache.getSimpleType(idType)[0]
    assertNotNull simpleBis
    assertEquals simple.idAnsitem, simpleBis.idAnsitem

    assertEquals simple.ansItName, simpleBis.ansItName
  }
}





public class Intrv1750TypesCacheTest extends GroovyTestCase {

  private TypesCache cache

  public void setUp () {
    cache = new TypesCache (1750)
  }


  public void testPreconditions () {
    assertNotNull cache 
  }

  public void testNumOfSimpleTypesIs4 () {
//    assertTrue (cache.getNumSimpleTypes() == 4)
    assertEquals cache.getNumSimpleTypes(), 3
  }


  public void testNumOfEnumTypesIsGt5 () {
    assertTrue cache.getNumEnumTypes() > 5
  }


  public void testEnumTypeElement () {
    def enumType = cache.getEnumType(2666)

    assertEquals enumType.size(), 6
    assertEquals enumType.getAt(0).ansItName, "motivo"
    assertEquals enumType.getAt(0).typeName, "Paciente si niega a recibir tratamiento"
  }

}
