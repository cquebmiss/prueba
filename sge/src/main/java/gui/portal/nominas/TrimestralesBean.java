package gui.portal.nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import modelo.CentroResponsabilidad;
import modelo.Concepto;
import modelo.Empleado;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.RFCCorreccion;
import modelo.Unidad;
import modelo.Unificacion;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class TrimestralesBean implements Serializable
{

    // Atributos para la parte de los datos SAR100
    private List<Integer>   años;
    private int		    añoSeleccionado;
    private int		    trimestreSelec;

    private List<Plaza>	    plazas;
    private List<Plaza>	    plazasSeleccionadas;
    private Plaza	    plazaSeleccionada;

    private String	    anexoSeleccionado;

    private StreamedContent txt;

    public TrimestralesBean()
    {
	this.trimestreSelec = 1;
	setAñosDisponibles();
	this.plazas = utilidades.getPlazas();
	this.anexoSeleccionado = "A3B";

    }

    private void setAñosDisponibles()
    {
	this.años = new ArrayList<>();

	Calendar ahoraCal = Calendar.getInstance();

	int año = +ahoraCal.get(Calendar.YEAR);
	setAñoSeleccionado(año);

	while (año > 2010)
	{
	    años.add(año);
	    año--;

	}

    }

    public void actionGenerarAnexo()
    {
	PreparedStatement prep = null;
	ResultSet rBD = null;

	try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
	{
	    int qnaInicial = -1;
	    int qnaFinal = -1;
	    String fechaFinTrimestre = null;

	    switch (this.trimestreSelec)
	    {
		case 1:

		    qnaInicial = 1;
		    qnaFinal = 6;
		    fechaFinTrimestre = "3103" + this.añoSeleccionado;

		    break;

		case 2:
		    qnaInicial = 7;
		    qnaFinal = 12;
		    fechaFinTrimestre = "3006" + this.añoSeleccionado;

		    break;

		case 3:
		    qnaInicial = 13;
		    qnaFinal = 18;
		    fechaFinTrimestre = "3009" + this.añoSeleccionado;

		    break;

		case 4:
		    qnaInicial = 19;
		    qnaFinal = 24;
		    fechaFinTrimestre = "3112" + this.añoSeleccionado;

		    break;

	    }

	    List<Integer> qnas = Arrays.asList(qnaInicial, qnaFinal);

	    List<CentroResponsabilidad> catCR = utilidades.getCatalogoCentroResponsabilidad();

	    // Lista donde contendrá el análisis de cada plaza
	    Map<Plaza, Map> plazasEmpleados = new HashMap<>();

	    Map<String, Concepto> conceptosNoAsociados = new HashMap<>();
	    Map<String, Concepto> conceptosAsociados = new HashMap<>();

	    List<RFCCorreccion> rfcCorrecciones = new ArrayList<>();

	    // Quincena-año, regDAT
	    Map<String, PlantillaRegistro> empleadosGeneral = new HashMap<>();
	    Map<String, PlantillaRegistro> empleados;
	    List<Producto> prds;
	    Producto prd;

	    Set set;
	    Iterator iterator;

	    Map.Entry me;
	    Concepto conceptoAñadiendo;

	    String claveConceptoNoAsociado;
	    String claveConceptoAsociado;

	    String claveReg;
	    int añoQuincena;
	    PlantillaRegistro regUnicoEmp;
	    int añoQuincenaGuardada;
	    String numEmpleado;
	    PlantillaRegistro registroMapa;
	    
	/*    List<Producto> prdsLambda1 = utilidades.getProductos(qnas.get(0), qnas.get(1), añoSeleccionado,
			0);
		
	    
	    prdsLambda1.stream().filter(p->
	    {
		    p.updateConceptosAsociadosPlaza(true, true, true, false);

		    // Se está generando para añadir funcionalidad de saber qué
		    // conceptos fueron usados y cuáles no durante la generación

		    p.setPlantillaDAT(null);
		    p.setPlantillaTRA(null);
		    p.setRegistrosTRA(null);
		    
		    p.getRegistrosDAT().stream().distinct().
		    
		    
		    return true;
		});

	    this.plazasSeleccionadas.stream().collect(Collectors.groupingBy(pl, pl ->
	    {
		// Se van añadiendo las correcciones de los rfc's al catálogo
		rfcCorrecciones.addAll(utilidades.getRFCCorrecciones(pl.getIdPlaza()));

		List<Producto> prdsLambda = utilidades.getProductos(qnas.get(0), qnas.get(1), añoSeleccionado,
			pl.getIdPlaza());
		
		prdsLambda.
		
		
		return prdsLambda;
	    }));*/

	    for (Plaza pl : this.plazasSeleccionadas)
	    {
		empleados = new HashMap<String, PlantillaRegistro>();

		prds = utilidades.getProductos(qnaInicial, qnaFinal, añoSeleccionado, pl.getIdPlaza());

		rfcCorrecciones.addAll(utilidades.getRFCCorrecciones(pl.getIdPlaza()));

		// Se rechazan los productos que sean de pensión o de
		// cancelación,
		// solo se considerarán los productos de ordinaria y de
		// extraordinaria

		System.out.println("Leyendo la plaza: " + pl.getDescripcionPlaza());

		for (int x = 0; x < prds.size(); x++)
		{
		    prd = prds.get(x);

		    System.out.println("Producto: " + prd.getNombreProducto());

		    // System.out.println("Leyendo producto: " +
		    // prd.getDescripcion());

		    if (prd.getTipoNomina().getDescripcion().contains("Pensi"))
		    {
			prds.remove(prd);
			x--;
			continue;
		    }

		    if (prd.getTipoProducto().getDescripcion().contains("Cancelado"))
		    {
			prds.remove(prd);
			x--;
			continue;
		    }

		    prd.updateConceptosAsociadosPlaza(true, true, true, false);

		    // Se está generando para añadir funcionalidad de saber qué
		    // conceptos fueron usados y cuáles no durante la generación

		    prd.setPlantillaDAT(null);
		    prd.setPlantillaTRA(null);
		    prd.setRegistrosTRA(null);

		    set = prd.getConceptosNoAsociadosPlaza().entrySet();
		    iterator = set.iterator();

		    while (iterator.hasNext())
		    {
			me = (Map.Entry) iterator.next();
			conceptoAñadiendo = (Concepto) me.getValue();

			claveConceptoNoAsociado = "" + conceptoAñadiendo.getTipoConcepto() + "-"
				+ conceptoAñadiendo.getClave() + "-" + conceptoAñadiendo.getPartidaAntecedente();

			if (!conceptosNoAsociados.containsKey(claveConceptoNoAsociado))
			{
			    conceptosNoAsociados.put(claveConceptoNoAsociado, conceptoAñadiendo);
			}

		    }

		    set = prd.getConceptosAsociadosAPlaza().entrySet();
		    iterator = set.iterator();

		    while (iterator.hasNext())
		    {
			me = (Map.Entry) iterator.next();
			conceptoAñadiendo = (Concepto) me.getValue();

			claveConceptoAsociado = "" + conceptoAñadiendo.getTipoConcepto() + "-"
				+ conceptoAñadiendo.getClave() + "-" + conceptoAñadiendo.getPartidaAntecedente();

			if (!conceptosAsociados.containsKey(claveConceptoAsociado))
			{
			    conceptosAsociados.put(claveConceptoAsociado, conceptoAñadiendo);
			}

		    }

		    for (PlantillaRegistro regDAT : prd.getRegistrosDAT())
		    {

			claveReg = prd.getPlaza().getIdPlaza() + "-"
				+ regDAT.getValorPorDescripcionContains("mero de emple");

			añoQuincena = Integer.parseInt(regDAT.getValorPorDescripcionContains("o de proceso")
				+ regDAT.getValorPorDescripcionContains("Quincena de Proceso o Re"));

			regUnicoEmp = empleadosGeneral.get(claveReg);

			if (regUnicoEmp == null)
			{
			    if (regDAT.getValorPorDescripcionContains("mero de emple").equalsIgnoreCase("0400000490"))
			    {
				System.out.println("Producto " + prd.getDescripcion() + " Plaza: "
					+ prd.getPlaza().getDescripcionPlaza()
					+ regDAT.getValorPorDescripcionContains("rfc"));
			    }

			    empleadosGeneral.put(prd.getPlaza().getIdPlaza() + "-"
				    + regDAT.getValorPorDescripcionContains("mero de emple"), regDAT);

			    // regUnicoEmp = regDAT;

			}
			else
			{
			    // Se determina si el registro del trabajador es de
			    // una quincena más reciente, se almacenará un solo
			    // registro
			    añoQuincenaGuardada = Integer
				    .parseInt(regUnicoEmp.getValorPorDescripcionContains("o de proceso")
					    + regUnicoEmp.getValorPorDescripcionContains("Quincena de Proceso o Re"));

			    // Si es de una quincena más reciente se guarda la
			    // información nueva del trabajador
			    if (añoQuincena > añoQuincenaGuardada)
			    {
				if (regDAT.getValorPorDescripcionContains("mero de emple")
					.equalsIgnoreCase("0400000490"))
				{
				    System.out.println("Producto " + prd.getDescripcion() + " " + " Plaza: "
					    + prd.getPlaza().getDescripcionPlaza()
					    + regUnicoEmp.getValorPorDescripcionContains("rfc"));
				    System.out.println("Producto " + prd.getDescripcion() + " " + " Plaza: "
					    + prd.getPlaza().getDescripcionPlaza()
					    + regDAT.getValorPorDescripcionContains("rfc"));
				}

				empleadosGeneral.replace(claveReg, regDAT);
				regUnicoEmp = regDAT;

			    }

			}

			// if (regDAT.getTotalAcumulado().compareTo(new
			// BigDecimal("0.00")) != 0)
			// {

			// Se almacenan en un mapa todos los empleados que
			// contengan valor
			numEmpleado = regDAT.getValorPorDescripcionContains("mero de emple");
			registroMapa = (PlantillaRegistro) empleados.get(numEmpleado);

			if (registroMapa != null)
			{

			    if (regDAT.getValorPorDescripcionContains("mero de emple").equalsIgnoreCase("0400000490"))
			    {
				System.out.println("Producto " + prd.getDescripcion() + " " + " Plaza: "
					+ prd.getPlaza().getDescripcionPlaza()
					+ registroMapa.getValorPorDescripcionContains("rfc"));
				System.out.println("Producto " + prd.getDescripcion() + " " + " Plaza: "
					+ prd.getPlaza().getDescripcionPlaza()
					+ regDAT.getValorPorDescripcionContains("rfc"));
			    }

			    registroMapa.addImporteAcumulado(regDAT.getTotalAcumulado());

			    // Se reemplaza la información del trabajador por la
			    // correcta, es decir, de la quincena más alta donde
			    // se haya quedado el trabajador dentro del
			    // trimestre, únicamente es necesario hacer esto ya
			    // que cuando el empleado no está en el mapa es un
			    // movimiento innecesario
			    regUnicoEmp.setTotalAcumulado(registroMapa.getTotalAcumulado());

			    empleados.replace(numEmpleado, registroMapa);

			}
			else
			{

			    if (regDAT.getValorPorDescripcionContains("mero de emple").equalsIgnoreCase("0400000490"))
			    {
				System.out.println("Producto " + prd.getDescripcion() + " " + " Plaza: "
					+ prd.getPlaza().getDescripcionPlaza()
					+ regDAT.getValorPorDescripcionContains("rfc"));
			    }

			    empleados.put(numEmpleado, regDAT);
			    registroMapa = regDAT;

			}

			prd.setConceptosAsociadosAPlaza(null);
			prd.setConceptosNoAsociadosAPlaza(null);

		    }

		    prd.setRegistrosTRA(new ArrayList<>());
		    prd.setRegistrosDAT(new ArrayList<>());
		    prd.setConceptos(new ArrayList<>());
		    prd.setUnidadResponsable(new ArrayList<>());

		}

		plazasEmpleados.put(pl, empleados);

	    }

	    System.out.println("Obteniendo trabajadores unificados");

	    // Se obtiene el catálogo de trabajadores enlazados
	    List<Unificacion> trabUnificados = utilidades.getUnificaciones(true);

	    // Recorrer todas las unificaciones en busca de trabajadores que se
	    // encuentren en la plantilla del periodo trimestral
	    for (Unificacion unif : trabUnificados)
	    {
		List<PlantillaRegistro> registrosUnificados = new ArrayList<>();

		// Se valida que haya uno en la plantilla de empleados general
		for (Empleado emp : unif.getEmpleados())
		{
		    PlantillaRegistro regGeneral = empleadosGeneral
			    .get("" + emp.getPlaza().getIdPlaza() + "-" + emp.getNumEmpleado().trim());

		    if (regGeneral != null)
		    {
			registrosUnificados.add(regGeneral);

		    }

		}

		if (registrosUnificados.size() > 0)
		{
		    PlantillaRegistro regMasReciente = null;
		    int añoQuincenaRegMasReciente;
		    int añoQuincenaRegComparando;

		    BigDecimal importeAcumulado = new BigDecimal("0.00");

		    for (PlantillaRegistro reg : registrosUnificados)
		    {
			importeAcumulado = importeAcumulado.add(reg.getTotalAcumulado());

			if (regMasReciente != null)
			{

			    añoQuincenaRegMasReciente = Integer.parseInt(
				    regMasReciente.getValorPorDescripcionContains("o de proceso") + regMasReciente
					    .getValorPorDescripcionContains("Quincena de Proceso o Re"));

			    añoQuincenaRegComparando = Integer
				    .parseInt(reg.getValorPorDescripcionContains("o de proceso")
					    + reg.getValorPorDescripcionContains("Quincena de Proceso o Re"));

			    if (añoQuincenaRegComparando > añoQuincenaRegMasReciente)
			    {
				regMasReciente = reg;

			    }

			}
			else
			{
			    regMasReciente = reg;
			}

			reg.setTotalAcumulado(new BigDecimal("0.00"));

		    }

		    regMasReciente.setTotalAcumulado(importeAcumulado);

		}

	    }

	    /*
	     * List<Producto> prds = utilidades.getProductos(qnaInicial, qnaFinal,
	     * añoSeleccionado, this.plazaSeleccionada.getIdPlaza());
	     */

	    // Se obtiene el catálogo de plazas, unidades y fuentes de
	    // financiamiento...
	    List<Plaza> catPlazaUnidadFuenteFinanciamiento = utilidades.getPlazasConUnidadesYFuentesFinancimiento();

	    // Set<?> set = empleados.entrySet();
	    // Iterator<?> iterator = set.iterator();

	    System.out.println("");
	    System.out.println("");
	    System.out.println("");
	    System.out.println("ACUMULADOS FINALES");
	    System.out.println("");
	    System.out.println("");
	    System.out.println("");

	    BigDecimal total = new BigDecimal("0.00");

	    try
	    {

		File f = new File("#{resource['images:temp.txt']}");
		PrintWriter wr = new PrintWriter(
			new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

		String nEmp;
		String nomEmp;

		Set<?> setPlazasEmpleados = plazasEmpleados.entrySet();
		Iterator<?> iteratorPlazasEmpleados = setPlazasEmpleados.iterator();

		Plaza plazaAnalizando;

		while (iteratorPlazasEmpleados.hasNext())
		{
		    Map.Entry men = (Map.Entry) iteratorPlazasEmpleados.next();
		    plazaAnalizando = (Plaza) men.getKey();

		    empleados = (Map) men.getValue();

		    set = empleados.entrySet();
		    iterator = set.iterator();

		    while (iterator.hasNext())
		    {
			me = (Map.Entry) iterator.next();

			PlantillaRegistro regDAT = (PlantillaRegistro) me.getValue();

			if (regDAT.getTotalAcumulado().compareTo(new BigDecimal("0.00")) == 0)
			{
			    continue;
			}

			nEmp = regDAT.getValorPorDescripcionContains("mero de emple");
			nomEmp = regDAT.getValorPorDescripcionContains("nombre");

			nomEmp = nomEmp.replace((char) 157, 'Ñ');
			nomEmp = nomEmp.replace((char) 209, 'Ñ');
			nomEmp = nomEmp.replaceAll("&", "Ñ");

			total = total.add(regDAT.getTotalAcumulado());

			if (regDAT.getValorPorDescripcionContains("mero de emple").equalsIgnoreCase("0400000490"))
			{
			    System.out.println("Plaza Analizando: " + plazaAnalizando.getDescripcionPlaza()
				    + regDAT.getValorPorDescripcionContains("rfc"));
			}

			String RFCValidado = getRFCCorregido(rfcCorrecciones, nEmp, plazaAnalizando.getIdPlaza());

			if (nEmp.equalsIgnoreCase("0407000199"))
			{
			    System.out.println("Aribe");
			}

			wr.println(nEmp + "|" + regDAT.getValorPorDescripcionContains("estado") + "|"
				+ this.trimestreSelec + "|" + this.añoSeleccionado + "|"
				+ regDAT.getValorPorDescripcionContains("unidad res") + "|"
				+ regDAT.getValorPorDescripcionContains("puesto") + "|" + nomEmp + "|"
				+ regDAT.getTotalAcumulado() + "|" + fechaFinTrimestre + "|"
				+ getFuenteFinanciamiento(catPlazaUnidadFuenteFinanciamiento, plazaAnalizando,
					regDAT.getValorPorDescripcionContains("unidad res"), regDAT)
				+ "|" + getCluesCorrectaCR(catCR,
					regDAT.getValorPorDescripcionContains("centro de resp"), true)
				+ "|" + (RFCValidado.equals("") ? "NOVALIDADO" : RFCValidado) + "|");
		    }

		}

		List<String> clavesConceptos = new ArrayList<String>(conceptosNoAsociados.keySet());

		java.util.Collections.sort(clavesConceptos);

		wr.println("\n\nIMPORTE TOTAL: "
			+ utilidades.formatoMoneda(total.toPlainString().replace(",", "").replace("$", "")) + "\n");

		wr.println("\n\nDESGLOSE DE CONCEPTOS NO ASOCIADOS A LA PLAZA ENCONTRADOS EN EL PERIODO\n");

		for (String clave : clavesConceptos)
		{
		    conceptoAñadiendo = conceptosNoAsociados.get(clave);
		    wr.println("" + conceptoAñadiendo.getTipoConcepto() + "-" + conceptoAñadiendo.getClave() + "-"
			    + conceptoAñadiendo.getPartidaAntecedente());
		}

		wr.println("\n\nDESGLOSE DE CONCEPTOS ASOCIADOS A LA PLAZA ENCONTRADOS EN EL PERIODO\n");

		clavesConceptos = new ArrayList<String>(conceptosAsociados.keySet());

		java.util.Collections.sort(clavesConceptos);

		for (String clave : clavesConceptos)
		{
		    conceptoAñadiendo = conceptosAsociados.get(clave);
		    wr.println("" + conceptoAñadiendo.getTipoConcepto() + "-" + conceptoAñadiendo.getClave() + "-"
			    + conceptoAñadiendo.getPartidaAntecedente());
		}

		wr.close();

		// Se especifica las plazas Seleccionadas

		String plazasSeleccionadas = "";

		for (Plaza pla : this.plazasSeleccionadas)
		{
		    plazasSeleccionadas += pla.getDescripcionPlaza() + " - ";
		}

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
			"" + this.anexoSeleccionado + " - " + plazasSeleccionadas + " - " + this.añoSeleccionado + " / "
				+ this.trimestreSelec + ".txt");

		System.out.println("");
		System.out.println("");
		System.out.println("El total acumulado es "
			+ utilidades.formatoMoneda(total.toPlainString().replace(",", "").replace("$", "")));

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		    "Excepción",
		    "Ha ocurrido una excepción al momento de generar el Anexo, favor de contactar con el desarrollador del sistema."));
	}
	finally
	{
	    if (prep != null)
	    {
		try
		{
		    prep.close();
		}
		catch (SQLException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

    }

    // Devuelve el RFC valido o corregido
    public String getRFCCorregido(List<RFCCorreccion> rfcCorrecciones, String numEmpleado, int idPlaza)
    {
	for (RFCCorreccion rfcCorreccion : rfcCorrecciones)
	{
	    if (rfcCorreccion.getIdPlaza() == idPlaza
		    && rfcCorreccion.getNumEmpleado().trim().equalsIgnoreCase(numEmpleado.trim()))
	    {
		if (rfcCorreccion.getRfcCorreccion().length() > 0)
		{
		    return rfcCorreccion.getRfcCorreccion();
		}
		else
		{
		    return rfcCorreccion.getRFC();
		}

	    }

	}

	return "";

    }

    // Devuelve la clues correcta de acuerdo al centro de responsabilidad
    public String getCluesCorrectaCR(List<CentroResponsabilidad> catCR, String cr, boolean cluesParaMexico)
    {

	for (CentroResponsabilidad objCR : catCR)
	{

	    if (cr.trim().equalsIgnoreCase(objCR.getIdCentroResponsabilidad().trim()))
	    {

		if (cluesParaMexico)
		{
		    if (objCR.getCluesParaMexico().trim().length() < 1)
		    {
			return objCR.getClues();
		    }
		    else
		    {
			return objCR.getCluesParaMexico();

		    }
		}
		else
		{
		    return objCR.getClues();
		}

	    }

	}

	return "---";

    }

    // Devuelve la fuente de financiamiento correcta de acuerdo al anexo que se
    // está generando
    // Se pasa el registro del trabajador ya que se debe determinar en base a su
    // clave presupuestal las diferencias situaciones que llevan a la obtención
    // de la correcta unidad
    public String getFuenteFinanciamiento(List<Plaza> plazaFF, Plaza pl, String unidad,
	    PlantillaRegistro registroTrabajadorDAT)
    {
	String unidadCorrecta = utilidades.determinaUnidad(pl, registroTrabajadorDAT);

	try
	{
	    for (Plaza objPlaza : plazaFF)
	    {
		if (objPlaza.getIdPlaza() == pl.getIdPlaza())
		{

		    for (Unidad un : objPlaza.getUnidades())
		    {

			if (un.getDescripcion().trim().equalsIgnoreCase(unidadCorrecta.trim()))
			{
			    return un.getFuenteFinanciamiento().getIdFuenteFinanciamiento();

			}

		    }

		}

	    }

	}
	catch (Exception e)
	{
	    System.out.print("Excepción buscando la fuente de financiamiento para la unidad " + unidad + " de la plaza "
		    + pl.getDescripcionPlaza());
	    e.printStackTrace();
	}

	return "---";

    }

    public List<Integer> getAños()
    {
	return años;
    }

    public void setAños(List<Integer> años)
    {
	this.años = años;
    }

    public int getAñoSeleccionado()
    {
	return añoSeleccionado;
    }

    public void setAñoSeleccionado(int añoSeleccionado)
    {
	this.añoSeleccionado = añoSeleccionado;
    }

    public int getTrimestreSelec()
    {
	return trimestreSelec;
    }

    public void setTrimestreSelec(int trimestreSelec)
    {
	this.trimestreSelec = trimestreSelec;
    }

    public List<Plaza> getPlazas()
    {
	return plazas;
    }

    public void setPlazas(List<Plaza> plazas)
    {
	this.plazas = plazas;
    }

    public Plaza getPlazaSeleccionada()
    {
	return plazaSeleccionada;
    }

    public void setPlazaSeleccionada(Plaza plazaSeleccionada)
    {
	this.plazaSeleccionada = plazaSeleccionada;
    }

    public String getAnexoSeleccionado()
    {
	return anexoSeleccionado;
    }

    public void setAnexoSeleccionado(String anexoSeleccionado)
    {
	this.anexoSeleccionado = anexoSeleccionado;
    }

    public StreamedContent getTxt()
    {
	return txt;
    }

    public void setTxt(StreamedContent txt)
    {
	this.txt = txt;
    }

    public List<Plaza> getPlazasSeleccionadas()
    {
	return plazasSeleccionadas;
    }

    public void setPlazasSeleccionadas(List<Plaza> plazasSeleccionadas)
    {
	this.plazasSeleccionadas = plazasSeleccionadas;
    }

}
