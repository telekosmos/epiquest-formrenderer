package org.cnio.appform.groovy.test.unit

import org.cnio.appform.groovy.util.TypesCache

/**
 * Created by IntelliJ IDEA.
 * User: bioinfo
 * Date: Dec 28, 2010
 * Time: 6:49:40 PM
 * To change this template use File | Settings | File Templates.
 */

public class TypesCacheMock {

  public static TypesCache getTypeCache () {
    def mockCache
    mockCache = [
      isEnumType: { typeId -> typeId == 1400 || typeId == 1401 || typeId == 1480? false: true },
      getSimpleType: { typeId ->
    //    def idIntrv = 50
        def idAnsIt = typeId
        def theName = typeId == 1400? "Free text": typeId == 1401? "Number": "Decimal"

        [[idAnsItem:idAnsIt, ansItName:theName, codintrv:50]]
      },

    // must return a list of items, every item is a list as above
      getEnumType: { typeId ->
    // 1402
        def man = [idansit:1402,ansItName:"Sexo",codintrv:50,typeName:"Hombre",typeValue:1,theOrder:1],
          woman = [idansit:1402,ansItName:"Sexo",codintrv:50,typeName:"Mujer",typeValue:2,theOrder:2]

        def si = [idansit:1403,ansItName:"Sí-No",codintrv:50,typeName:"Sí",typeValue:1,theOrder:1],
            no = [idansit:1403,ansItName:"Sí-No",codintrv:50,typeName:"No",typeValue:2,theOrder:2],
            nsnc = [idansit:1403,ansItName:"Sí-No",codintrv:50,typeName:"NSNC",typeValue:8888,theOrder:3]

    // 1404
        def blanco = [idansit:1404,ansItName:"Raza",codintrv:50,typeName:"Blanco",typeValue:1,theOrder:1],
            negro = [idansit:1404,ansItName:"Raza",codintrv:50,typeName:"Negro",typeValue:2,theOrder:2],
            chino = [idansit:1404,ansItName:"Raza",codintrv:50,typeName:"Asiático",typeValue:3,theOrder:3],
            moro = [idansit:1404,ansItName:"Raza",codintrv:50,typeName:"Moro",typeValue:4,theOrder:6],
            otro = [idansit:1404,ansItName:"Raza",codintrv:50,typeName:"Otro",typeValue:5,theOrder:7]


// 1411 Aguas
        def aguaNs = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"NS",typeValue:8888,theOrder:7]
        def aguaMunP = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"Agua municipal y de pozo por igual",typeValue:	6,theOrder:	6]
        def aguaEmb = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"Agua embotellada",typeValue:	1,theOrder:	1]
        def aguaPoz = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"Agua de pozo privado, manantial o de otras fuentes que no provengan de distribución municipal",typeValue:	3,theOrder:	3]
        def aguaEmbM = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"Agua embotellada y municipal por igual",typeValue:	4,theOrder:	4]
        def aguaEmbP = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"Agua embotellada y de pozo por igual",typeValue:	5,theOrder:	5]
        def aguaMun = [idansit:1411,ansItName:"Aguas",codintrv:50,typeName:"Agua municipal (grifo)",typeValue:2,theOrder:	2]


// 1412 Higiene corporal
        def ducharse = [idansit: 1412,ansItName:"Higiene corporal",codintrv:50,typeName:"Ducharse",typeValue:1,theOrder:1]
        def partes = [idansit: 1412,ansItName:"Higiene corporal",codintrv:	50,typeName:"Por partes (a trozos)",typeValue:3,theOrder:3]
        def duchbath = [idansit: 1412,ansItName:"Higiene corporal",codintrv:	50,typeName:"Ducharse y bañarse",typeValue:4,theOrder:4]
        def higieneNs = [idansit: 1412,ansItName:"Higiene corporal",codintrv:	50,typeName:"NS",typeValue:	8888,theOrder:7]
        def duchpartes = [idansit: 1412,ansItName:"Higiene corporal",codintrv:	50,typeName:"Ducharse y a trozos",typeValue:5,theOrder:5 ]
        def bathpartes = [idansit: 1412,ansItName:"Higiene corporal",codintrv:	50,typeName:"Bañarse y a trozos",typeValue:6,theOrder:	6]
        def bath = [idansit: 1412,ansItName:"Higiene corporal",codintrv:50,typeName:"Bañarse",typeValue:2,theOrder:2]


// 1413 Dia-mes-año
        def dsmaNs = [idansit:1413,ansItName:"Dia-Sem-Mes-Año",codintrv:50,typeName:"NS",typeValue:	8888,theOrder:5]
        def anno = [idansit:1413,ansItName:"Dia-Sem-Mes-Año",codintrv:50,typeName:"Año",typeValue:4,theOrder:	4]
        def mes = [idansit:1413,ansItName:"Dia-Sem-Mes-Año",codintrv:50,typeName:"Mes",typeValue:	3,theOrder:	3]
        def sem = [idansit:1413,ansItName:"Dia-Sem-Mes-Año",codintrv:50,typeName:"Semana",typeValue:2,theOrder:	2]
        def dia = [idansit:1413,ansItName:"Dia-Sem-Mes-Año",codintrv:50,typeName:"Dia",typeValue:	1,theOrder:	1]

// 1421 piscina
        def pisExt = [idansit:1421,ansItName:"Piscina",codintrv:50,typeName:"Exterior",typeValue:2,theOrder:2]
        def pisNs = [idansit:1421,ansItName:"Piscina",codintrv:	50,typeName:"NS",typeValue:8888,theOrder:4]
        def pisBoth = [idansit:1421,ansItName:"Piscina",codintrv:	50,typeName:"	Ambos interior e exterior",typeValue:3,theOrder:3]
        def pisInt = [idansit:1421,ansItName:"Piscina",codintrv:50,typeName:"Interior",typeValue:1,theOrder:1]


        def types = [man, woman, nsnc, si, no, blanco, negro, chino, otro, moro,
                     aguaNs, aguaMunP, aguaEmb, aguaPoz, aguaEmbM, aguaEmbP, aguaMun,
                     ducharse, partes, duchbath, higieneNs, duchpartes, bathpartes, bath,
                     dsmaNs, anno, mes, sem, dia,
                     pisExt, pisNs, pisBoth, pisInt]

        def selTypes = types.findAll { it.idansit == typeId }
        selTypes.sort { it -> it.theOrder }
      }
    ] as TypesCache

    mockCache

  }

}