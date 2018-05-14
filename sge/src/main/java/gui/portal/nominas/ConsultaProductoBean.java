package gui.portal.nominas;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import application.CatalogosBean;
import modelo.Acumulado;
import modelo.ArchivoPuesto;
import modelo.Concepto;
import modelo.Financiera;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.ReporteVarios;
import modelo.Sesion;
import modelo.UnidadProducto;
import modelo.UnidadRegistros;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@ViewScoped
public class ConsultaProductoBean implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<Producto>    catProductos;
    private List<Producto>    productosFiltro;
    private Producto	      productoSelec;
    private List<Producto>    productosSelec;

    BigDecimal		      bigPercepTotal;
    BigDecimal		      bigDeducTotal;

    private String	      totalPercepiones;
    private String	      totalDeducciones;
    private String	      totalLiquido;
    private int		      totalRegistros;

    private List<Concepto>    conceptos;
    private List<Concepto>    conceptosDeduc;

    private ReporteVarios     reporte;
    private List<Concepto>    conceptosRepv;
    private String	      nombreArchivoRepv;
    private String	      prefijoFinancieras;
    private String	      sufijoFinancieras;
    private Boolean	      acumularConceptosRepv;

    private StreamedContent   txtRepv;

    private boolean	      incluirPension;
    private boolean	      cancelarCheques;
    private boolean	      soloCancelaciones;
    private boolean	      incluirDifSubsidioImpuestos;

    @PostConstruct
    public void postConstruct()
    {
	this.conceptosRepv = new ArrayList<>();
	this.conceptosRepv.add(new Concepto(1, "", "", "", "00", new BigDecimal("0.00")));

	CatalogosBean catBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");
	this.catProductos = catBean.getCatProductos();

    }

    public ConsultaProductoBean()
    {
	super();
	// updateProductos();
    }

    public void addConceptoRepv()
    {
	this.conceptosRepv.add(new Concepto(1, "", "", "", "00", new BigDecimal("0.00")));
    }

    public void removeConceptoRepv(Concepto concepto)
    {
	this.conceptosRepv.remove(concepto);

    }

    public void updateInfoRegistrosProductosSelec()
    {

	for (Producto prod : this.productosSelec)
	{
	    prod.updateRegistrosTRAConConceptos(true, true, true, true, false, true);
	}

    }

    public void updateProductosDAT(boolean clasificarUnidades)
    {

	if (!clasificarUnidades)
	{
	    for (Producto prod : this.productosSelec)
	    {
		prod.updateRegistrosDAT();
	    }
	}
	else
	{
	    this.bigPercepTotal = new BigDecimal(0);
	    this.bigDeducTotal = new BigDecimal(0);
	    this.totalRegistros = 0;

	    for (Producto prod : this.productosSelec)
	    {
		prod.updateRegistrosDAT(true, true, true, true, false);

		bigPercepTotal = bigPercepTotal.add(prod.getBigPercepTotal());
		bigDeducTotal = bigDeducTotal.add(prod.getBigDeducTotal());
		this.totalRegistros += prod.getTotalRegistros();
	    }

	    this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
	    this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
	    this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

	}

    }

    public void updateProductosTRA()
    {
	this.bigPercepTotal = new BigDecimal(0);
	this.bigDeducTotal = new BigDecimal(0);

	this.conceptos = new ArrayList<>();
	this.conceptosDeduc = new ArrayList<>();

	this.totalRegistros = 0;

	boolean conceptoAñadido = false;

	for (Producto prod : this.productosSelec)
	{
	    System.out.println("Leyendo prod: " + prod.getNombreProducto());
	    prod.updateRegistrosTRAConConceptos(true, true, true, true, true, false);

	    bigPercepTotal = bigPercepTotal.add(prod.getBigPercepTotal());
	    bigDeducTotal = bigDeducTotal.add(prod.getBigDeducTotal());
	    this.totalRegistros += prod.getTotalRegistros();

	    for (Concepto con : prod.getConceptos())
	    {
		conceptoAñadido = false;

		for (Concepto conG : this.conceptos)
		{
		    if (con.getClave().equals(conG.getClave()))
		    {
			conG.setTotalCasos(conG.getTotalCasos() + con.getTotalCasos());
			conG.setValor(conG.getValor().add(con.getValor()));
			conceptoAñadido = true;
			break;
		    }
		}

		if (!conceptoAñadido)
		{
		    this.conceptos.add(con.getClone());

		}

	    }

	    for (Concepto con : prod.getConceptosDeduc())
	    {
		conceptoAñadido = false;

		for (Concepto conG : this.conceptosDeduc)
		{
		    if (con.getClave().equals(conG.getClave()))
		    {
			conG.setTotalCasos(conG.getTotalCasos() + con.getTotalCasos());
			conG.setValor(conG.getValor().add(con.getValor()));
			conceptoAñadido = true;
			break;
		    }
		}

		if (!conceptoAñadido)
		{
		    this.conceptosDeduc.add(con.getClone());

		}

	    }

	}

	this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
	this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
	this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

    }

    public void updateProductos()
    {
	this.productosFiltro = null;
	this.productoSelec = null;
    }

    private StreamedContent txt;

    public void actionAcumuladoConDetalleDeProductosTXT2Copia()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, (o1, o2) ->
	{
	    Producto reg1 = (Producto) o1;
	    Producto reg2 = (Producto) o2;

	    String q1 = "" + reg1.getQuincena();
	    String q2 = "" + reg2.getQuincena();

	    if (q1.length() == 1)
	    {
		q1 = "0" + q1;
	    }

	    if (q2.length() == 1)
	    {
		q2 = "0" + q2;
	    }

	    return ((Comparable<?>) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2015);

	// HashMap - clave XSSFSheet de la unidad correspondiente
	// - Valor otro HashMap

	// Valor del HashMap de Unidad
	// - Clave: Concepto
	// - Valor: List<Object>: Almacenará el número de fila de la hoja donde
	// irá el valor y el valor BigDecimal

	// Hojas, con filas y arreglo de columnas valor
	HashMap<SXSSFSheet, List<List<Object>>> mapaHojasFilas = new HashMap<SXSSFSheet, List<List<Object>>>();

	HashMap<SXSSFSheet, List<String>> conceptosPorPagina = new HashMap<SXSSFSheet, List<String>>();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clon;
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		{
		    prds.remove(prds.get(x));
		    x--;
		    continue;
		}

		if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		{
		    chequesCancelados.add(prds.get(x));
		    prds.remove(prds.get(x));
		    x--;

		}

	    }

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook();
	    List<BigDecimal> totales;

	    int fila = 0;
	    int numCel = 0;
	    int posConceptosBase = 25;

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();

	    XSSFFont font = (XSSFFont) libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);

	    boolean cancelado = false;

	    Concepto con;
	    List<Object> conceptoEncontrado;
	    List<Object> nuevoRegistro;
	    List<Object> baseRegs;

	    List<String> nombreConceptosPagina;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Producto: " + producto.getNombreProducto());
		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    SXSSFSheet pagina = libroExcel.getSheet(unid.getDescripcion());
		    List<List<Object>> filasConConceptos = null;
		    int posConceptoNuevo = 25;

		    if (pagina == null)
		    {
			pagina = libroExcel.createSheet("" + unid.getDescripcion());

			filasConConceptos = new ArrayList<>();

			mapaHojasFilas.put(pagina, filasConConceptos);

			nombreConceptosPagina = new ArrayList<>();
			conceptosPorPagina.put(pagina, nombreConceptosPagina);

			// Se crean los encabezados de la página
			fila = 0;
			numCel = 0;

			SXSSFRow filaEncabezado = pagina.createRow(fila);

			SXSSFCell cAño = filaEncabezado.createCell(numCel);
			cAño.setCellValue("AÑO");
			cAño.setCellStyle(style);
			numCel++;

			SXSSFCell cQna = filaEncabezado.createCell(numCel);
			cQna.setCellValue("QUINCENA");
			cQna.setCellStyle(style);
			numCel++;

			SXSSFCell cProducto = filaEncabezado.createCell(numCel);
			cProducto.setCellValue("PRODUCTO");
			cProducto.setCellStyle(style);
			numCel++;

			SXSSFCell cCheque = filaEncabezado.createCell(numCel);
			cCheque.setCellValue("CHEQUE");
			cCheque.setCellStyle(style);
			numCel++;

			SXSSFCell c = filaEncabezado.createCell(numCel);
			c.setCellValue("RFC");
			c.setCellStyle(style);
			numCel++;

			SXSSFCell cPaterno = filaEncabezado.createCell(numCel);
			cPaterno.setCellValue("PATERNO");
			cPaterno.setCellStyle(style);
			numCel++;

			SXSSFCell cMaterno = filaEncabezado.createCell(numCel);
			cMaterno.setCellValue("MATERNO");
			cMaterno.setCellStyle(style);
			numCel++;

			SXSSFCell cNombres = filaEncabezado.createCell(numCel);
			cNombres.setCellValue("NOMBRES");
			cNombres.setCellStyle(style);
			numCel++;

			SXSSFCell cFIngreso = filaEncabezado.createCell(numCel);
			cFIngreso.setCellValue("FECHA DE INGRESO");
			cFIngreso.setCellStyle(style);
			numCel++;

			SXSSFCell cDLaborados = filaEncabezado.createCell(numCel);
			cDLaborados.setCellValue("DIAS LABORADOS");
			cDLaborados.setCellStyle(style);
			numCel++;

			SXSSFCell cTTrabajador = filaEncabezado.createCell(numCel);
			cTTrabajador.setCellValue("TIPO DE TRABAJADOR");
			cTTrabajador.setCellStyle(style);
			numCel++;

			SXSSFCell cTMando = filaEncabezado.createCell(numCel);
			cTMando.setCellValue("TIPO DE MANDO");
			cTMando.setCellStyle(style);
			numCel++;

			SXSSFCell cCMando = filaEncabezado.createCell(numCel);
			cCMando.setCellValue("CLAVE DE PAGO");
			cCMando.setCellStyle(style);
			numCel++;

			SXSSFCell cUR = filaEncabezado.createCell(numCel);
			cUR.setCellValue("UNIDAD RESPONSABLE");
			cUR.setCellStyle(style);
			numCel++;

			SXSSFCell cPlaza = filaEncabezado.createCell(numCel);
			cPlaza.setCellValue("PLAZA");
			cPlaza.setCellStyle(style);
			numCel++;

			SXSSFCell cCR = filaEncabezado.createCell(numCel);
			cCR.setCellValue("C. RESPONSABILIDAD");
			cCR.setCellStyle(style);
			numCel++;

			SXSSFCell cCD = filaEncabezado.createCell(numCel);
			cCD.setCellValue("NOMBRE C. RESPONSABILIDAD");
			cCD.setCellStyle(style);
			numCel++;

			SXSSFCell cClues = filaEncabezado.createCell(numCel);
			cClues.setCellValue("CLUES");
			cClues.setCellStyle(style);
			numCel++;

			SXSSFCell cFechaInicial = filaEncabezado.createCell(numCel);
			cFechaInicial.setCellValue("FECHA INICIAL");
			cFechaInicial.setCellStyle(style);
			numCel++;

			SXSSFCell cFechaFinal = filaEncabezado.createCell(numCel);
			cFechaFinal.setCellValue("FECHA FINAL");
			cFechaFinal.setCellStyle(style);
			numCel++;

			SXSSFCell cIP = filaEncabezado.createCell(numCel);
			cIP.setCellValue("INS.PAGO");
			cIP.setCellStyle(style);
			numCel++;

			SXSSFCell cCURP = filaEncabezado.createCell(numCel);
			cCURP.setCellValue("CURP");
			cCURP.setCellStyle(style);
			numCel++;

			SXSSFCell cPERCEP = filaEncabezado.createCell(numCel);
			cPERCEP.setCellValue("PERCEPCIONES");
			cPERCEP.setCellStyle(style);
			numCel++;

			SXSSFCell cDEDUC = filaEncabezado.createCell(numCel);
			cDEDUC.setCellValue("DEDUCCIONES");
			cDEDUC.setCellStyle(style);
			numCel++;

			SXSSFCell cNETO = filaEncabezado.createCell(numCel);
			cNETO.setCellValue("NETO");
			cNETO.setCellStyle(style);
			numCel++;

			fila++;
		    }
		    else
		    {
			fila = pagina.getLastRowNum();
			filasConConceptos = mapaHojasFilas.get(pagina);

			nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    }

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			// Comparar con los cheques cancelados
			for (Producto chequeCan : chequesCancelados)
			{
			    clonCheq = chequeCan;

			    if (clonCheq.getRegistrosTRA() == null)
			    {
				clonCheq.updateRegistrosTRA();
			    }

			    for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
			    {

				for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				{
				    // Quincena de proceso o real
				    // Año de proceso o real
				    if (regDAT.getValorPorDescripcionContains("mero de emple")
					    .equals(regCheq.getValorPorDescripcionContains("mero de emple")))
				    {

					if (regDAT.getValorPorDescripcionContains("mero de cheque")
						.equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					{

					    if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
						    regCheq.getValorPorDescripcionContains("odo de pago inicia"))
						    && regDAT.getValorPorDescripcionContains("odo de pago fina").equals(
							    regCheq.getValorPorDescripcionContains("odo de pago fina")))
						    || (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
							    .equals(regCheq.getValorPorDescripcionContains(
								    "ena de Proceso o Re"))
							    && regDAT.getValorPorDescripcionContains("o de Proceso o R")
								    .equals(regCheq.getValorPorDescripcionContains(
									    "o de Proceso o R"))))
					    {
						unidCheq.getRegistrosDAT().remove(regCheq);
						cancelado = true;
						break;
					    }

					}

				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado)
			    {
				break;
			    }

			}

			if (cancelado)
			{

			    continue;
			}

			PlantillaRegistro trabajador = regDAT;
			String nombres = trabajador.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			SXSSFRow filaTrab = pagina.createRow(fila);

			SXSSFCell detcAño = filaTrab.createCell(numCel);
			detcAño.setCellValue(producto.getAño());
			numCel++;

			SXSSFCell detcQna = filaTrab.createCell(numCel);
			detcQna.setCellValue(producto.getQuincenaString());
			numCel++;

			SXSSFCell detcProducto = filaTrab.createCell(numCel);
			detcProducto.setCellValue(trabajador.getValorPorDescripcionContains("nombre del producto"));
			numCel++;

			SXSSFCell detcCheque = filaTrab.createCell(numCel);
			detcCheque.setCellValue(trabajador.getValorPorDescripcionContains("mero de cheque"));
			numCel++;

			SXSSFCell cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(trabajador.getValorEnCampo(2));

			numCel++;

			SXSSFCell cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			SXSSFCell cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			SXSSFCell cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			SXSSFCell cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(trabajador.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			SXSSFCell cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue(trabajador.getValorPorDescripcionContains("as laborados"));
			numCel++;

			SXSSFCell cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue(trabajador.getValorPorDescripcionContains("tipo de trab"));
			numCel++;

			SXSSFCell cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(trabajador.getValorPorDescripcionContains("ndicador de mand"));
			numCel++;

			SXSSFCell cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(trabajador.getValorPorDescripcionContains("unidad res")
				+ trabajador.getValorPorDescripcionContains("actividad inst")
				+ trabajador.getValorPorDescripcionContains("proyecto proc")
				+ trabajador.getValorPorDescripcionContains("Partida") + " "
				+ trabajador.getValorPorDescripcion("Puesto")
				+ trabajador.getValorPorDescripcionContains("grupo funcion")
				+ trabajador.getValorPorDescripcion("Función")
				+ trabajador.getValorPorDescripcionContains("subfunci"));

			numCel++;

			SXSSFCell cURT = filaTrab.createCell(numCel);
			cURT.setCellValue(trabajador.getValorPorDescripcionContains("Unidad Respons"));
			numCel++;

			SXSSFCell cPlazaT = filaTrab.createCell(numCel);

			if (unid.getDescripcion().equals("610"))
			{
			    cPlazaT.setCellValue("ENSEÑANZA");
			}
			else
			{
			    cPlazaT.setCellValue("BASE");
			}

			numCel++;

			SXSSFCell cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(trabajador.getValorPorDescripcionContains("Centro"));
			numCel++;

			SXSSFCell cCRD = filaTrab.createCell(numCel);
			cCRD.setCellValue(trabajador.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			SXSSFCell cCCLUES = filaTrab.createCell(numCel);
			cCCLUES.setCellValue(trabajador.getValorPorDescripcionContains("icencia Sin Goce de S"));
			numCel++;

			SXSSFCell cFechaInicialT = filaTrab.createCell(numCel);
			cFechaInicialT.setCellValue(trabajador.getValorPorDescripcionContains("do de Pago Inicial"));
			numCel++;

			SXSSFCell cFechaFinalT = filaTrab.createCell(numCel);
			cFechaFinalT.setCellValue(trabajador.getValorPorDescripcionContains("do de Pago Final"));
			numCel++;

			SXSSFCell cIPT = filaTrab.createCell(numCel);
			cIPT.setCellValue(trabajador.getValorPorDescripcionContains("nstrumento de Pago N"));
			numCel++;

			SXSSFCell cCURPT = filaTrab.createCell(numCel);
			cCURPT.setCellValue(trabajador.getValorPorDescripcionContains("CURP"));
			numCel++;

			totales = trabajador.getPercepDeducNeto();

			SXSSFCell cPERCEPT = filaTrab.createCell(numCel);
			cPERCEPT.setCellValue(utilidades.formato.format(totales.get(0)));
			numCel++;

			SXSSFCell cDEDUCT = filaTrab.createCell(numCel);
			cDEDUCT.setCellValue(utilidades.formato.format(totales.get(1)));
			numCel++;

			SXSSFCell cNETOT = filaTrab.createCell(numCel);
			cNETOT.setCellValue(utilidades.formato.format(totales.get(2)));
			numCel++;

			// Verifica si cada uno de los conceptos del registro se
			// encuentra en el mapa de conceptos de la página
			// en caso de no estarlo lo añade con su indice de fila
			// y valor a su arrayList, en caso de estar añade
			// también a la lista un registro más

			baseRegs = new ArrayList<>();
			filasConConceptos.add(baseRegs);

			SXSSFRow primeraFila = pagina.getRow(0);
			SXSSFRow filaActual = pagina.getRow(fila);

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    String conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Busca para acumular solo los conceptos de la
			    // página
			    boolean existeConcepto = nombreConceptosPagina.contains(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());

			    if (!existeConcepto)
			    {
				Collator comparador = Collator.getInstance();
				comparador.setStrength(Collator.IDENTICAL);

				boolean conceptoEntreConceptos = false;

				// Se recorre el arreglo para ir ordenando
				// alfabéticamente la lista de conceptos
				for (int posCon = 0; posCon < nombreConceptosPagina.size(); posCon++)
				{

				    if (comparador.compare(conceptoNuevo, nombreConceptosPagina.get(posCon)) < 1)
				    {

					SXSSFCell cel = null;
					// En éste momento se corren todas las
					// filas de Excel
					// Aquí empezamos con la cabecera
					for (int posMovimientoCol = posConceptosBase
						+ nombreConceptosPagina.size(); posMovimientoCol >= posConceptosBase
							+ posCon; posMovimientoCol--)
					{
					    cel = primeraFila.getCell(posMovimientoCol);
					    SXSSFCell celDerecha = primeraFila.getCell(posMovimientoCol + 1);

					    if (celDerecha == null)
					    {
						celDerecha = primeraFila.createCell(posMovimientoCol + 1);

					    }

					    if (cel == null)
					    {
						if (celDerecha != null)
						{
						    primeraFila.removeCell(celDerecha);

						}

						continue;
					    }

					    celDerecha.setCellValue(cel.getStringCellValue());

					    // cel.setCellValue(conceptoNuevo);

					}

					nombreConceptosPagina.add(posCon, conceptoNuevo);

					posConceptoNuevo = posConceptosBase + posCon;
					conceptoEntreConceptos = true;

					break;

				    }

				}

				if (!conceptoEntreConceptos)
				{
				    posConceptoNuevo = posConceptosBase + nombreConceptosPagina.size();
				    nombreConceptosPagina.add(conceptoNuevo);

				}

				SXSSFCell celdaNuevoConcepto = primeraFila.getCell(posConceptoNuevo);

				if (celdaNuevoConcepto == null)
				{
				    celdaNuevoConcepto = primeraFila.createCell(posConceptoNuevo);
				}

				celdaNuevoConcepto.setCellValue(conceptoNuevo);
				int a = 12;
				a = 34;

				// ahora se mueven todas las celdas a la derecha
				// desde el inicio hasta la fila actual

			    }
			    else
			    {
				// Localizar la posición del concepto
				posConceptoNuevo = posConceptosBase + nombreConceptosPagina.indexOf(
					con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());
			    }

			    SXSSFCell celdaNuevoConcepto = filaActual.createCell(posConceptoNuevo);
			    celdaNuevoConcepto.setCellValue(utilidades.formato.format(con.getValor()));

			    // System.out.print( primeraFila.toString());
			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		prds.remove(x);
		x--;

	    }

	    SXSSFSheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    SXSSFRow ff = paginaProd.createRow(fila);

	    SXSSFCell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    SXSSFCell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto producto : prds)
	    {
		SXSSFRow fi = paginaProd.createRow(fila);
		SXSSFCell celp = fi.createCell(0);
		celp.setCellValue(
			producto.getAño() + "-" + producto.getQuincena() + " " + producto.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		SXSSFRow fi = paginaProd.getRow(fila);
		SXSSFCell celp = fi.createCell(1);
		celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " + cheq.getNombreProducto());
		fila++;

	    }
	    /*
	     * for (int x = 0; x < libroExcel.getNumberOfSheets(); x++) { for (int c = 0; c
	     * < mapaHojasFilas.size() + 4 + 24; c++) {
	     * libroExcel.getSheetAt(x).autoSizeColumn(c); } }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.close();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionAcumuladoConDetalleDeProductosTXTSinCancelar()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, (o1, o2) ->
	{
	    Producto reg1 = (Producto) o1;
	    Producto reg2 = (Producto) o2;

	    String q1 = "" + reg1.getQuincena();
	    String q2 = "" + reg2.getQuincena();

	    if (q1.length() == 1)
	    {
		q1 = "0" + q1;
	    }

	    if (q2.length() == 1)
	    {
		q2 = "0" + q2;
	    }

	    return ((Comparable<?>) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2015);

	// HashMap - clave XSSFSheet de la unidad correspondiente
	// - Valor otro HashMap

	// Valor del HashMap de Unidad
	// - Clave: Concepto
	// - Valor: List<Object>: Almacenará el número de fila de la hoja donde
	// irá el valor y el valor BigDecimal

	// Hojas, con filas y arreglo de columnas valor
	HashMap<Sheet, List<List<Object>>> mapaHojasFilas = new HashMap<Sheet, List<List<Object>>>();

	HashMap<Sheet, List<String>> conceptosPorPagina = new HashMap<Sheet, List<String>>();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clon;
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		{
		    prds.remove(prds.get(x));
		    x--;
		    continue;
		}

	    }

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);
	    List<BigDecimal> totales;

	    int fila = 0;
	    int numCel = 0;
	    int posConceptosBase = 25;

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();

	    XSSFFont font = (XSSFFont) libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);

	    boolean cancelado = false;

	    Concepto con;
	    List<Object> conceptoEncontrado;
	    List<Object> nuevoRegistro;
	    List<Object> baseRegs;

	    List<String> nombreConceptosPagina = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Producto: " + producto.getNombreProducto());
		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    Sheet pagina = libroExcel.getSheet(unid.getDescripcion());
		    List<List<Object>> filasConConceptos = null;

		    if (pagina == null)
		    {
			pagina = libroExcel.createSheet("" + unid.getDescripcion());

			filasConConceptos = new ArrayList<>();

			mapaHojasFilas.put(pagina, filasConConceptos);

			nombreConceptosPagina = new ArrayList<>();
			conceptosPorPagina.put(pagina, nombreConceptosPagina);

			// Se crean los encabezados de la página
			fila = 0;
			numCel = 0;

			Row filaEncabezado = pagina.createRow(fila);

			Cell cAño = filaEncabezado.createCell(numCel);
			cAño.setCellValue("AÑO");
			cAño.setCellStyle(style);
			numCel++;

			Cell cQna = filaEncabezado.createCell(numCel);
			cQna.setCellValue("QUINCENA");
			cQna.setCellStyle(style);
			numCel++;

			Cell cProducto = filaEncabezado.createCell(numCel);
			cProducto.setCellValue("PRODUCTO");
			cProducto.setCellStyle(style);
			numCel++;

			Cell cCheque = filaEncabezado.createCell(numCel);
			cCheque.setCellValue("CHEQUE");
			cCheque.setCellStyle(style);
			numCel++;

			Cell c = filaEncabezado.createCell(numCel);
			c.setCellValue("RFC");
			c.setCellStyle(style);
			numCel++;

			Cell cPaterno = filaEncabezado.createCell(numCel);
			cPaterno.setCellValue("PATERNO");
			cPaterno.setCellStyle(style);
			numCel++;

			Cell cMaterno = filaEncabezado.createCell(numCel);
			cMaterno.setCellValue("MATERNO");
			cMaterno.setCellStyle(style);
			numCel++;

			Cell cNombres = filaEncabezado.createCell(numCel);
			cNombres.setCellValue("NOMBRES");
			cNombres.setCellStyle(style);
			numCel++;

			Cell cFIngreso = filaEncabezado.createCell(numCel);
			cFIngreso.setCellValue("FECHA DE INGRESO");
			cFIngreso.setCellStyle(style);
			numCel++;

			Cell cDLaborados = filaEncabezado.createCell(numCel);
			cDLaborados.setCellValue("DIAS LABORADOS");
			cDLaborados.setCellStyle(style);
			numCel++;

			Cell cTTrabajador = filaEncabezado.createCell(numCel);
			cTTrabajador.setCellValue("TIPO DE TRABAJADOR");
			cTTrabajador.setCellStyle(style);
			numCel++;

			Cell cTMando = filaEncabezado.createCell(numCel);
			cTMando.setCellValue("TIPO DE MANDO");
			cTMando.setCellStyle(style);
			numCel++;

			Cell cCMando = filaEncabezado.createCell(numCel);
			cCMando.setCellValue("CLAVE DE PAGO");
			cCMando.setCellStyle(style);
			numCel++;

			// Unidad Responsable
			// Grupo Funcional
			// Función
			// Subfunción
			// Programa General
			// Actividad Institucional
			// Proyecto Proceso
			// Partida
			// Puesto

			Cell cPago1 = filaEncabezado.createCell(numCel);
			cPago1.setCellValue("Grupo Funcional");
			cPago1.setCellStyle(style);
			numCel++;

			Cell cPago2 = filaEncabezado.createCell(numCel);
			cPago2.setCellValue("Función");
			cPago2.setCellStyle(style);
			numCel++;

			Cell cPago3 = filaEncabezado.createCell(numCel);
			cPago3.setCellValue("Subfunción");
			cPago3.setCellStyle(style);
			numCel++;

			Cell cPago4 = filaEncabezado.createCell(numCel);
			cPago4.setCellValue("Programa General");
			cPago4.setCellStyle(style);
			numCel++;

			Cell cPago5 = filaEncabezado.createCell(numCel);
			cPago5.setCellValue("Actividad Institucionall");
			cPago5.setCellStyle(style);
			numCel++;

			Cell cPago6 = filaEncabezado.createCell(numCel);
			cPago6.setCellValue("Proyecto Proceso");
			cPago6.setCellStyle(style);
			numCel++;

			Cell cPago7 = filaEncabezado.createCell(numCel);
			cPago7.setCellValue("Partida");
			cPago7.setCellStyle(style);
			numCel++;

			Cell cPago8 = filaEncabezado.createCell(numCel);
			cPago8.setCellValue("Puesto");
			cPago8.setCellStyle(style);
			numCel++;

			Cell cUR = filaEncabezado.createCell(numCel);
			cUR.setCellValue("UNIDAD RESPONSABLE");
			cUR.setCellStyle(style);
			numCel++;

			Cell cPlaza = filaEncabezado.createCell(numCel);
			cPlaza.setCellValue("PLAZA");
			cPlaza.setCellStyle(style);
			numCel++;

			Cell cCR = filaEncabezado.createCell(numCel);
			cCR.setCellValue("C. RESPONSABILIDAD");
			cCR.setCellStyle(style);
			numCel++;

			Cell cCD = filaEncabezado.createCell(numCel);
			cCD.setCellValue("NOMBRE C. RESPONSABILIDAD");
			cCD.setCellStyle(style);
			numCel++;

			Cell cClues = filaEncabezado.createCell(numCel);
			cClues.setCellValue("CLUES");
			cClues.setCellStyle(style);
			numCel++;

			Cell cFechaInicial = filaEncabezado.createCell(numCel);
			cFechaInicial.setCellValue("FECHA INICIAL");
			cFechaInicial.setCellStyle(style);
			numCel++;

			Cell cFechaFinal = filaEncabezado.createCell(numCel);
			cFechaFinal.setCellValue("FECHA FINAL");
			cFechaFinal.setCellStyle(style);
			numCel++;

			Cell cIP = filaEncabezado.createCell(numCel);
			cIP.setCellValue("INS.PAGO");
			cIP.setCellStyle(style);
			numCel++;

			Cell cCURP = filaEncabezado.createCell(numCel);
			cCURP.setCellValue("CURP");
			cCURP.setCellStyle(style);
			numCel++;

			Cell cPERCEP = filaEncabezado.createCell(numCel);
			cPERCEP.setCellValue("PERCEPCIONES");
			cPERCEP.setCellStyle(style);
			numCel++;

			Cell cDEDUC = filaEncabezado.createCell(numCel);
			cDEDUC.setCellValue("DEDUCCIONES");
			cDEDUC.setCellStyle(style);
			numCel++;

			Cell cNETO = filaEncabezado.createCell(numCel);
			cNETO.setCellValue("NETO");
			cNETO.setCellStyle(style);
			numCel++;

			fila++;
		    }
		    else
		    {
			fila = pagina.getLastRowNum();
			filasConConceptos = mapaHojasFilas.get(pagina);

			nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    }

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (cancelado)
			{

			    continue;
			}

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    String conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Busca para acumular solo los conceptos de la
			    // página
			    boolean existeConcepto = nombreConceptosPagina.contains(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());

			    if (!existeConcepto)
			    {
				Collator comparador = Collator.getInstance();
				comparador.setStrength(Collator.IDENTICAL);

				boolean conceptoEntreConceptos = false;

				// Se recorre el arreglo para ir ordenando
				// alfabéticamente la lista de conceptos
				for (int posCon = 0; posCon < nombreConceptosPagina.size(); posCon++)
				{

				    if (comparador.compare(conceptoNuevo, nombreConceptosPagina.get(posCon)) < 1)
				    {

					nombreConceptosPagina.add(posCon, conceptoNuevo);
					conceptoEntreConceptos = true;

					break;

				    }

				}

				if (!conceptoEntreConceptos)
				{
				    nombreConceptosPagina.add(conceptoNuevo);

				}

			    }

			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

	    }

	    // Se ponen los encabezados
	    Set setPaginas = conceptosPorPagina.entrySet();
	    Iterator iteratorPaginas = setPaginas.iterator();

	    int colBase = 33;

	    // Recorre unidades
	    while (iteratorPaginas.hasNext())
	    {
		Map.Entry<?, List<String>> me = (Map.Entry) iteratorPaginas.next();

		Sheet paginaUnidad = (Sheet) me.getKey();

		List<String> registros = (List<String>) me.getValue();

		colBase = 33;

		for (String concepto : registros)
		{

		    Cell celdaEncabezado = paginaUnidad.getRow(0).getCell(colBase);

		    if (celdaEncabezado == null)
		    {
			Cell cel = paginaUnidad.getRow(0).createCell(colBase);
			cel.setCellValue(concepto);
			cel.setCellStyle(style);
		    }

		    colBase++;

		}

	    }

	    String conceptoNuevo = null;
	    String nombres = null;

	    Row filaTrab = null;

	    Cell detcAño = null;

	    Cell detcQna = null;

	    Cell detcProducto = null;

	    Cell detcCheque = null;

	    Cell cTrab = null;

	    Cell cPaternoT = null;

	    Cell cMaternoT = null;

	    Cell cNombresT = null;

	    Cell cFIngresoT = null;

	    Cell cDLaboradosT = null;

	    Cell cTTrabajadorT = null;

	    Cell cTMandoT = null;

	    Cell cCMandoT = null;

	    Cell cURT = null;

	    Cell cPlazaT = null;

	    Cell cCRT = null;

	    Cell cCRD = null;

	    Cell cCCLUES = null;

	    Cell cFechaInicialT = null;

	    Cell cFechaFinalT = null;

	    Cell cIPT = null;

	    Cell cCURPT = null;

	    Cell cPERCEPT = null;

	    Cell cDEDUCT = null;

	    Cell cNETOT = null;

	    Sheet pagina = null;
	    PlantillaRegistro regDAT;
	    Producto producto;
	    int posConceptoNuevo = 0;
	    colBase = 33;

	    for (int x = 0; x < prds.size(); x++)
	    {
		producto = prds.get(x);

		System.out.println("Ingresando conceptos del Producto: " + producto.getNombreProducto());
		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    pagina = libroExcel.getSheet(unid.getDescripcion());

		    fila = pagina.getLastRowNum() + 1;
		    nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (cancelado)
			{

			    continue;
			}

			nombres = regDAT.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			filaTrab = pagina.createRow(fila);

			detcAño = filaTrab.createCell(numCel);
			detcAño.setCellValue(producto.getAño());
			numCel++;

			detcQna = filaTrab.createCell(numCel);
			detcQna.setCellValue(producto.getQuincenaString());
			numCel++;

			detcProducto = filaTrab.createCell(numCel);
			detcProducto.setCellValue(regDAT.getValorPorDescripcionContains("nombre del producto"));
			numCel++;

			detcCheque = filaTrab.createCell(numCel);
			detcCheque.setCellValue(regDAT.getValorPorDescripcionContains("mero de cheque"));
			numCel++;

			cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(regDAT.getValorEnCampo(2));

			numCel++;

			cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(regDAT.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue(regDAT.getValorPorDescripcionContains("as laborados"));
			numCel++;

			cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue(regDAT.getValorPorDescripcionContains("tipo de trab"));
			numCel++;

			cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(regDAT.getValorPorDescripcionContains("ndicador de mand"));
			numCel++;

			cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(regDAT.getValorPorDescripcionContains("unidad res")
				+ regDAT.getValorPorDescripcionContains("actividad inst")
				+ regDAT.getValorPorDescripcionContains("proyecto proc")
				+ regDAT.getValorPorDescripcionContains("Partida") + " "
				+ regDAT.getValorPorDescripcion("Puesto")
				+ regDAT.getValorPorDescripcionContains("grupo funcion")
				+ regDAT.getValorPorDescripcion("Función")
				+ regDAT.getValorPorDescripcionContains("subfunci"));

			numCel++;

			Cell cPago1 = filaTrab.createCell(numCel);
			cPago1.setCellValue(regDAT.getValorPorDescripcionContains("grupo funcion"));
			numCel++;

			Cell cPago2 = filaTrab.createCell(numCel);
			cPago2.setCellValue(regDAT.getValorPorDescripcion("Función"));
			numCel++;

			Cell cPago3 = filaTrab.createCell(numCel);
			cPago3.setCellValue(regDAT.getValorPorDescripcionContains("subfunci"));
			numCel++;

			Cell cPago4 = filaTrab.createCell(numCel);
			cPago4.setCellValue(regDAT.getValorPorDescripcionContains("Programa General"));
			numCel++;

			Cell cPago5 = filaTrab.createCell(numCel);
			cPago5.setCellValue(regDAT.getValorPorDescripcionContains("actividad inst"));
			numCel++;

			Cell cPago6 = filaTrab.createCell(numCel);
			cPago6.setCellValue(regDAT.getValorPorDescripcionContains("proyecto proc"));
			numCel++;

			Cell cPago7 = filaTrab.createCell(numCel);
			cPago7.setCellValue(regDAT.getValorPorDescripcionContains("Partida"));
			numCel++;

			Cell cPago8 = filaTrab.createCell(numCel);
			cPago8.setCellValue(regDAT.getValorPorDescripcion("Puesto"));
			numCel++;

			cURT = filaTrab.createCell(numCel);
			cURT.setCellValue(regDAT.getValorPorDescripcionContains("Unidad Respons"));
			numCel++;

			cPlazaT = filaTrab.createCell(numCel);

			if (unid.getDescripcion().equals("610"))
			{
			    cPlazaT.setCellValue("ENSEÑANZA");
			}
			else
			{
			    cPlazaT.setCellValue("BASE");
			}

			numCel++;

			cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(regDAT.getValorPorDescripcionContains("Centro"));
			numCel++;

			cCRD = filaTrab.createCell(numCel);
			cCRD.setCellValue(regDAT.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			cCCLUES = filaTrab.createCell(numCel);
			cCCLUES.setCellValue(regDAT.getValorPorDescripcionContains("icencia Sin Goce de S"));
			numCel++;

			cFechaInicialT = filaTrab.createCell(numCel);
			cFechaInicialT.setCellValue(regDAT.getValorPorDescripcionContains("do de Pago Inicial"));
			numCel++;

			cFechaFinalT = filaTrab.createCell(numCel);
			cFechaFinalT.setCellValue(regDAT.getValorPorDescripcionContains("do de Pago Final"));
			numCel++;

			cIPT = filaTrab.createCell(numCel);
			cIPT.setCellValue(regDAT.getValorPorDescripcionContains("nstrumento de Pago N"));
			numCel++;

			cCURPT = filaTrab.createCell(numCel);
			cCURPT.setCellValue(regDAT.getValorPorDescripcionContains("CURP"));
			numCel++;

			totales = regDAT.getPercepDeducNeto();

			cPERCEPT = filaTrab.createCell(numCel);
			cPERCEPT.setCellValue(utilidades.formato.format(totales.get(0)));
			numCel++;

			cDEDUCT = filaTrab.createCell(numCel);
			cDEDUCT.setCellValue(utilidades.formato.format(totales.get(1)));
			numCel++;

			cNETOT = filaTrab.createCell(numCel);
			cNETOT.setCellValue(utilidades.formato.format(totales.get(2)));
			numCel++;

			// Verifica si cada uno de los conceptos del registro se
			// encuentra en el mapa de conceptos de la página
			// en caso de no estarlo lo añade con su indice de fila
			// y valor a su arrayList, en caso de estar añade
			// también a la lista un registro más

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Localizar la posición del concepto
			    for (int conPos = 0; conPos < nombreConceptosPagina.size(); conPos++)
			    {

				if (conceptoNuevo.equalsIgnoreCase(nombreConceptosPagina.get(conPos)))
				{
				    posConceptoNuevo = colBase + conPos;
				    break;
				}

			    }

			    Cell celdaNuevoConcepto = filaTrab.createCell(posConceptoNuevo);
			    celdaNuevoConcepto.setCellValue(utilidades.formato.format(con.getValor()));

			    // System.out.print( primeraFila.toString());
			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

		prds.remove(x);
		x--;

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto productoP : prds)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			productoP.getAño() + "-" + productoP.getQuincena() + " " + productoP.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		Row fi = paginaProd.getRow(fila);
		Cell celp = fi.createCell(1);
		celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " + cheq.getNombreProducto());
		fila++;

	    }
	    /*
	     * for (int x = 0; x < libroExcel.getNumberOfSheets(); x++) { for (int c = 0; c
	     * < mapaHojasFilas.size() + 4 + 24; c++) {
	     * libroExcel.getSheetAt(x).autoSizeColumn(c); } }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.dispose();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionAcumuladoConDetalleDeProductosTXT4()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prdsSublist = this.productosSelec.subList(0, this.productosSelec.size());

	prdsSublist.sort((Producto o1, Producto o2) -> o2.getAñoQuincenaString().compareTo(o1.getAñoQuincenaString()));

	// Se obtiene una sublista de clones
	List<Producto> prds = new ArrayList<>();

	prdsSublist.forEach(item ->
	{
	    prds.add((Producto) item.clone());

	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2015);

	BigDecimal sumaImpuestos = new BigDecimal("0.00");
	BigDecimal sumaSubsidio = new BigDecimal("0.00");

	// HashMap - clave XSSFSheet de la unidad correspondiente
	// - Valor otro HashMap

	// Valor del HashMap de Unidad
	// - Clave: Concepto
	// - Valor: List<Object>: Almacenará el número de fila de la hoja donde
	// irá el valor y el valor BigDecimal

	// Hojas, con filas y arreglo de columnas valor
	HashMap<Sheet, List<List<Object>>> mapaHojasFilas = new HashMap<Sheet, List<List<Object>>>();

	HashMap<Sheet, List<String>> conceptosPorPagina = new HashMap<Sheet, List<String>>();

	List<Producto> chequesCancelados = new ArrayList<>();
	List<Producto> chequesCanceladosSublist = new ArrayList<>();

	Producto clon;
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (!this.incluirPension)
		{
		    if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		    {
			prds.remove(prds.get(x));
			x--;
			continue;
		    }

		}

		if (this.cancelarCheques || this.soloCancelaciones)
		{
		    if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		    {
			chequesCancelados.add(prds.get(x));
			prds.remove(prds.get(x));
			x--;

		    }

		}

	    }

	    chequesCancelados.forEach(item ->
	    {
		chequesCanceladosSublist.add((Producto) item.clone());
	    });

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);
	    List<BigDecimal> totales;

	    int fila = 0;
	    int numCel = 0;

	    XSSFFont font = (XSSFFont) libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();
	    style.setFont(font);

	    XSSFCellStyle styleMoneda = (XSSFCellStyle) libroExcel.createCellStyle();
	    styleMoneda.setDataFormat((short) 7);

	    boolean cancelado = false;

	    Concepto con;

	    List<String> nombreConceptosPagina = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Producto: " + producto.getNombreProducto());
		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    Sheet pagina = libroExcel.getSheet(unid.getDescripcion());
		    List<List<Object>> filasConConceptos = null;

		    if (pagina == null)
		    {
			pagina = libroExcel.createSheet("" + unid.getDescripcion());

			filasConConceptos = new ArrayList<>();

			mapaHojasFilas.put(pagina, filasConConceptos);

			nombreConceptosPagina = new ArrayList<>();
			conceptosPorPagina.put(pagina, nombreConceptosPagina);

			// Se crean los encabezados de la página
			fila = 0;
			numCel = 0;

			Row filaEncabezado = pagina.createRow(fila);

			Cell cAño = filaEncabezado.createCell(numCel);
			cAño.setCellValue("AÑO");
			cAño.setCellStyle(style);
			numCel++;

			Cell cQna = filaEncabezado.createCell(numCel);
			cQna.setCellValue("QUINCENA");
			cQna.setCellStyle(style);
			numCel++;

			Cell cProducto = filaEncabezado.createCell(numCel);
			cProducto.setCellValue("PRODUCTO");
			cProducto.setCellStyle(style);
			numCel++;

			Cell cCheque = filaEncabezado.createCell(numCel);
			cCheque.setCellValue("CHEQUE");
			cCheque.setCellStyle(style);
			numCel++;

			Cell c = filaEncabezado.createCell(numCel);
			c.setCellValue("RFC");
			c.setCellStyle(style);
			numCel++;

			Cell cPaterno = filaEncabezado.createCell(numCel);
			cPaterno.setCellValue("PATERNO");
			cPaterno.setCellStyle(style);
			numCel++;

			Cell cMaterno = filaEncabezado.createCell(numCel);
			cMaterno.setCellValue("MATERNO");
			cMaterno.setCellStyle(style);
			numCel++;

			Cell cNombres = filaEncabezado.createCell(numCel);
			cNombres.setCellValue("NOMBRES");
			cNombres.setCellStyle(style);
			numCel++;

			Cell cFIngreso = filaEncabezado.createCell(numCel);
			cFIngreso.setCellValue("FECHA DE INGRESO");
			cFIngreso.setCellStyle(style);
			numCel++;

			Cell cDLaborados = filaEncabezado.createCell(numCel);
			cDLaborados.setCellValue("DIAS LABORADOS");
			cDLaborados.setCellStyle(style);
			numCel++;

			Cell cTTrabajador = filaEncabezado.createCell(numCel);
			cTTrabajador.setCellValue("TIPO DE TRABAJADOR");
			cTTrabajador.setCellStyle(style);
			numCel++;

			Cell cTMando = filaEncabezado.createCell(numCel);
			cTMando.setCellValue("TIPO DE MANDO");
			cTMando.setCellStyle(style);
			numCel++;

			Cell cCMando = filaEncabezado.createCell(numCel);
			cCMando.setCellValue("CLAVE DE PAGO");
			cCMando.setCellStyle(style);
			numCel++;

			Cell cPago1 = filaEncabezado.createCell(numCel);
			cPago1.setCellValue("Grupo Funcional");
			cPago1.setCellStyle(style);
			numCel++;

			Cell cPago2 = filaEncabezado.createCell(numCel);
			cPago2.setCellValue("Función");
			cPago2.setCellStyle(style);
			numCel++;

			Cell cPago3 = filaEncabezado.createCell(numCel);
			cPago3.setCellValue("Subfunción");
			cPago3.setCellStyle(style);
			numCel++;

			Cell cPago4 = filaEncabezado.createCell(numCel);
			cPago4.setCellValue("Programa General");
			cPago4.setCellStyle(style);
			numCel++;

			Cell cPago5 = filaEncabezado.createCell(numCel);
			cPago5.setCellValue("Actividad Institucionall");
			cPago5.setCellStyle(style);
			numCel++;

			Cell cPago6 = filaEncabezado.createCell(numCel);
			cPago6.setCellValue("Proyecto Proceso");
			cPago6.setCellStyle(style);
			numCel++;

			Cell cPago7 = filaEncabezado.createCell(numCel);
			cPago7.setCellValue("Partida");
			cPago7.setCellStyle(style);
			numCel++;

			Cell cPago8 = filaEncabezado.createCell(numCel);
			cPago8.setCellValue("Puesto");
			cPago8.setCellStyle(style);
			numCel++;

			Cell cUR = filaEncabezado.createCell(numCel);
			cUR.setCellValue("UNIDAD RESPONSABLE");
			cUR.setCellStyle(style);
			numCel++;

			Cell cPlaza = filaEncabezado.createCell(numCel);
			cPlaza.setCellValue("PLAZA");
			cPlaza.setCellStyle(style);
			numCel++;

			Cell cCR = filaEncabezado.createCell(numCel);
			cCR.setCellValue("C. RESPONSABILIDAD");
			cCR.setCellStyle(style);
			numCel++;

			Cell cCD = filaEncabezado.createCell(numCel);
			cCD.setCellValue("NOMBRE C. RESPONSABILIDAD");
			cCD.setCellStyle(style);
			numCel++;

			Cell cClues = filaEncabezado.createCell(numCel);
			cClues.setCellValue("CLUES");
			cClues.setCellStyle(style);
			numCel++;

			Cell cFechaInicial = filaEncabezado.createCell(numCel);
			cFechaInicial.setCellValue("FECHA INICIAL");
			cFechaInicial.setCellStyle(style);
			numCel++;

			Cell cFechaFinal = filaEncabezado.createCell(numCel);
			cFechaFinal.setCellValue("FECHA FINAL");
			cFechaFinal.setCellStyle(style);
			numCel++;

			Cell cIP = filaEncabezado.createCell(numCel);
			cIP.setCellValue("INS.PAGO");
			cIP.setCellStyle(style);
			numCel++;

			Cell cCuenta = filaEncabezado.createCell(numCel);
			cCuenta.setCellValue("CUENTA");
			cCuenta.setCellStyle(style);
			numCel++;

			Cell cCURP = filaEncabezado.createCell(numCel);
			cCURP.setCellValue("CURP");
			cCURP.setCellStyle(style);
			numCel++;

			Cell cPERCEP = filaEncabezado.createCell(numCel);
			cPERCEP.setCellValue("PERCEPCIONES");
			cPERCEP.setCellStyle(style);
			numCel++;

			Cell cDEDUC = filaEncabezado.createCell(numCel);
			cDEDUC.setCellValue("DEDUCCIONES");
			cDEDUC.setCellStyle(style);
			numCel++;

			Cell cNETO = filaEncabezado.createCell(numCel);
			cNETO.setCellValue("NETO");
			cNETO.setCellStyle(style);
			numCel++;

			fila++;
		    }
		    else
		    {
			fila = pagina.getLastRowNum();
			filasConConceptos = mapaHojasFilas.get(pagina);

			nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    }

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (this.cancelarCheques || this.soloCancelaciones)
			{

			    // Comparar con los cheques cancelados
			    for (Producto chequeCan : chequesCancelados)
			    {
				clonCheq = chequeCan;

				if (clonCheq.getRegistrosTRA() == null)
				{
				    clonCheq.updateRegistrosTRA();
				}

				for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
				{

				    for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				    {
					// Quincena de proceso o real
					// Año de proceso o real
					if (regDAT.getValorPorDescripcionContains("mero de emple")
						.equals(regCheq.getValorPorDescripcionContains("mero de emple")))
					{

					    if (regDAT.getValorPorDescripcionContains("mero de cheque")
						    .equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					    {

						if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
							regCheq.getValorPorDescripcionContains("odo de pago inicia"))
							&& regDAT.getValorPorDescripcionContains("odo de pago fina")
								.equals(regCheq.getValorPorDescripcionContains(
									"odo de pago fina")))
							|| (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
								.equals(regCheq.getValorPorDescripcionContains(
									"ena de Proceso o Re"))
								&& regDAT
									.getValorPorDescripcionContains(
										"o de Proceso o R")
									.equals(regCheq.getValorPorDescripcionContains(
										"o de Proceso o R"))))
						{

						    System.out.println("Cancelado el cheque "
							    + regCheq.getValorPorDescripcionContains("mero de chequ"));

						    if (!this.soloCancelaciones)
						    {
							// Si no se busca solo
							// las cancelaciones de
							// cheques se remueve
							// del producto
							// unidCheq.getRegistrosDAT().remove(regCheq);
						    }
						    else
						    {
							regDAT = regCheq;
						    }

						    cancelado = true;
						    break;
						}

					    }

					}

				    }

				    if (cancelado)
				    {
					break;
				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado && !this.soloCancelaciones)
			    {

				continue;
			    }

			}

			// Si solo estamos buscando generar el detalle de los
			// cheques que se cancelaron y el cheque no está
			// cancelado se continua
			if (this.soloCancelaciones && !cancelado)
			{
			    continue;
			}

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    String conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Busca para acumular solo los conceptos de la
			    // página
			    boolean existeConcepto = nombreConceptosPagina.contains(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());

			    if (!existeConcepto)
			    {
				Collator comparador = Collator.getInstance();
				comparador.setStrength(Collator.IDENTICAL);

				boolean conceptoEntreConceptos = false;

				// Se recorre el arreglo para ir ordenando
				// alfabéticamente la lista de conceptos
				for (int posCon = 0; posCon < nombreConceptosPagina.size(); posCon++)
				{

				    if (comparador.compare(conceptoNuevo, nombreConceptosPagina.get(posCon)) < 1)
				    {

					nombreConceptosPagina.add(posCon, conceptoNuevo);
					conceptoEntreConceptos = true;

					break;

				    }

				}

				if (!conceptoEntreConceptos)
				{
				    nombreConceptosPagina.add(conceptoNuevo);

				}

			    }

			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

	    }

	    // Se ponen los encabezados
	    Set setPaginas = conceptosPorPagina.entrySet();
	    Iterator iteratorPaginas = setPaginas.iterator();

	    int colBase = 34;

	    // Recorre unidades
	    while (iteratorPaginas.hasNext())
	    {
		Map.Entry<?, List<String>> me = (Map.Entry) iteratorPaginas.next();

		Sheet paginaUnidad = (Sheet) me.getKey();

		List<String> registros = (List<String>) me.getValue();

		colBase = 34;

		for (String concepto : registros)
		{

		    Cell celdaEncabezado = paginaUnidad.getRow(0).getCell(colBase);

		    if (celdaEncabezado == null)
		    {
			Cell cel = paginaUnidad.getRow(0).createCell(colBase);
			cel.setCellValue(concepto);
			cel.setCellStyle(style);
		    }

		    colBase++;

		}

		if (this.incluirDifSubsidioImpuestos)
		{
		    Cell cellImpuesto = paginaUnidad.getRow(0).createCell(colBase);
		    cellImpuesto.setCellValue("Impuesto");
		    cellImpuesto.setCellStyle(style);
		    colBase++;

		    Cell cellSubsidio = paginaUnidad.getRow(0).createCell(colBase);
		    cellSubsidio.setCellValue("Subsidio");
		    cellSubsidio.setCellStyle(style);
		    colBase++;

		    Cell cellDifImpuestoSubsidio = paginaUnidad.getRow(0).createCell(colBase);
		    cellDifImpuestoSubsidio.setCellValue("Dif. Imp/SE");
		    cellDifImpuestoSubsidio.setCellStyle(style);
		    colBase++;

		}

	    }

	    String conceptoNuevo = null;
	    String nombres = null;

	    Row filaTrab = null;

	    Cell detcAño = null;

	    Cell detcQna = null;

	    Cell detcProducto = null;

	    Cell detcCheque = null;

	    Cell cTrab = null;

	    Cell cPaternoT = null;

	    Cell cMaternoT = null;

	    Cell cNombresT = null;

	    Cell cFIngresoT = null;

	    Cell cDLaboradosT = null;

	    Cell cTTrabajadorT = null;

	    Cell cTMandoT = null;

	    Cell cCMandoT = null;

	    Cell cURT = null;

	    Cell cPlazaT = null;

	    Cell cCRT = null;

	    Cell cCRD = null;

	    Cell cCCLUES = null;

	    Cell cFechaInicialT = null;

	    Cell cFechaFinalT = null;

	    Cell cIPT = null;

	    Cell cCuenta = null;

	    Cell cCURPT = null;

	    Cell cPERCEPT = null;

	    Cell cDEDUCT = null;

	    Cell cNETOT = null;

	    Cell cImpuestos = null;

	    Cell cSubsidio = null;

	    Cell cDifImpuestosSubsidio = null;

	    Sheet pagina = null;
	    PlantillaRegistro regDAT;
	    Producto producto;
	    int posConceptoNuevo = 0;
	    colBase = 34;

	    // Se rehacen los cheques
	    /*
	     * for (Producto chequeCan : chequesCancelados) {
	     * chequeCan.setRegistrosTRA(null); chequeCan.updateRegistrosTRA();
	     * 
	     * }
	     */

	    System.out.println("Ingresando conceptos del Producto: ");
	    for (int x = 0; x < prds.size(); x++)
	    {
		producto = prds.get(x);

		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    pagina = libroExcel.getSheet(unid.getDescripcion());

		    fila = pagina.getLastRowNum() + 1;
		    nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (this.cancelarCheques || this.soloCancelaciones)
			{

			    // Comparar con los cheques cancelados
			    for (Producto chequeCan : chequesCancelados)
			    {
				clonCheq = chequeCan;

				if (clonCheq.getRegistrosTRA() == null)
				{
				    clonCheq.updateRegistrosTRA();
				}

				for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
				{

				    for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				    {
					// Quincena de proceso o real
					// Año de proceso o real
					if (regDAT.getValorPorDescripcionContains("mero de emple")
						.equals(regCheq.getValorPorDescripcionContains("mero de emple")))
					{

					    if (regDAT.getValorPorDescripcionContains("mero de cheque")
						    .equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					    {

						if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
							regCheq.getValorPorDescripcionContains("odo de pago inicia"))
							&& regDAT.getValorPorDescripcionContains("odo de pago fina")
								.equals(regCheq.getValorPorDescripcionContains(
									"odo de pago fina")))
							|| (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
								.equals(regCheq.getValorPorDescripcionContains(
									"ena de Proceso o Re"))
								&& regDAT
									.getValorPorDescripcionContains(
										"o de Proceso o R")
									.equals(regCheq.getValorPorDescripcionContains(
										"o de Proceso o R"))))
						{

						    if (!this.soloCancelaciones)
						    {
							// Si no se busca solo
							// las cancelaciones de
							// cheques se remueve
							// del producto
							// unidCheq.getRegistrosDAT().remove(regCheq);
						    }
						    else
						    {
							regDAT = regCheq;
						    }

						    System.out.println("Cancelado el cheque "
							    + regCheq.getValorPorDescripcionContains("mero de chequ")
							    + " producto " + producto.getNombreProducto());
						    cancelado = true;
						    break;

						}

					    }

					}

				    }

				    if (cancelado)
				    {
					break;
				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado && !this.soloCancelaciones)
			    {

				continue;
			    }

			}

			// Si solo estamos buscando generar el detalle de
			// los
			// cheques que se cancelaron y el cheque no está
			// cancelado se continua
			if (this.soloCancelaciones && !cancelado)
			{
			    continue;
			}

			nombres = regDAT.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			filaTrab = pagina.createRow(fila);

			detcAño = filaTrab.createCell(numCel);
			detcAño.setCellValue(producto.getAño());
			numCel++;

			detcQna = filaTrab.createCell(numCel);
			detcQna.setCellValue(producto.getQuincenaString());
			numCel++;

			detcProducto = filaTrab.createCell(numCel);
			detcProducto.setCellValue(regDAT.getValorPorDescripcionContains("nombre del producto"));
			numCel++;

			detcCheque = filaTrab.createCell(numCel);
			detcCheque.setCellValue(regDAT.getValorPorDescripcionContains("mero de cheque"));
			numCel++;

			cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(regDAT.getValorEnCampo(2));

			numCel++;

			cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(regDAT.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue(regDAT.getValorPorDescripcionContains("as laborados"));
			numCel++;

			cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue(regDAT.getValorPorDescripcionContains("tipo de trab"));
			numCel++;

			cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(regDAT.getValorPorDescripcionContains("ndicador de mand"));
			numCel++;

			cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(regDAT.getValorPorDescripcionContains("unidad res")
				+ regDAT.getValorPorDescripcionContains("actividad inst")
				+ regDAT.getValorPorDescripcionContains("proyecto proc")
				+ regDAT.getValorPorDescripcionContains("Partida") + " "
				+ regDAT.getValorPorDescripcion("Puesto")
				+ regDAT.getValorPorDescripcionContains("grupo funcion")
				+ regDAT.getValorPorDescripcion("Función")
				+ regDAT.getValorPorDescripcionContains("subfunci"));

			numCel++;

			Cell cPago1 = filaTrab.createCell(numCel);
			cPago1.setCellValue(regDAT.getValorPorDescripcionContains("grupo funcion"));
			numCel++;

			Cell cPago2 = filaTrab.createCell(numCel);
			cPago2.setCellValue(regDAT.getValorPorDescripcion("Función"));
			numCel++;

			Cell cPago3 = filaTrab.createCell(numCel);
			cPago3.setCellValue(regDAT.getValorPorDescripcionContains("subfunci"));
			numCel++;

			Cell cPago4 = filaTrab.createCell(numCel);
			cPago4.setCellValue(regDAT.getValorPorDescripcionContains("Programa General"));
			numCel++;

			Cell cPago5 = filaTrab.createCell(numCel);
			cPago5.setCellValue(regDAT.getValorPorDescripcionContains("actividad inst"));
			numCel++;

			Cell cPago6 = filaTrab.createCell(numCel);
			cPago6.setCellValue(regDAT.getValorPorDescripcionContains("proyecto proc"));
			numCel++;

			Cell cPago7 = filaTrab.createCell(numCel);
			cPago7.setCellValue(regDAT.getValorPorDescripcionContains("Partida"));
			numCel++;

			Cell cPago8 = filaTrab.createCell(numCel);
			cPago8.setCellValue(regDAT.getValorPorDescripcion("Puesto"));
			numCel++;

			cURT = filaTrab.createCell(numCel);
			cURT.setCellValue(regDAT.getValorPorDescripcionContains("Unidad Respons"));
			numCel++;

			cPlazaT = filaTrab.createCell(numCel);

			if (unid.getDescripcion().equals("610"))
			{
			    cPlazaT.setCellValue("ENSEÑANZA");
			}
			else
			{
			    cPlazaT.setCellValue("BASE");
			}

			numCel++;

			cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(regDAT.getValorPorDescripcionContains("Centro"));
			numCel++;

			cCRD = filaTrab.createCell(numCel);
			cCRD.setCellValue(regDAT.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			cCCLUES = filaTrab.createCell(numCel);
			cCCLUES.setCellValue(regDAT.getValorPorDescripcionContains("icencia Sin Goce de S"));
			numCel++;

			cFechaInicialT = filaTrab.createCell(numCel);
			cFechaInicialT.setCellValue(regDAT.getValorPorDescripcionContains("do de Pago Inicial"));
			numCel++;

			cFechaFinalT = filaTrab.createCell(numCel);
			cFechaFinalT.setCellValue(regDAT.getValorPorDescripcionContains("do de Pago Final"));
			numCel++;

			cIPT = filaTrab.createCell(numCel);
			cIPT.setCellValue(regDAT.getValorPorDescripcionContains("nstrumento de Pago N"));
			numCel++;

			cCuenta = filaTrab.createCell(numCel);
			cCuenta.setCellValue(regDAT.getValorPorDescripcionContains("mero de cuenta"));
			numCel++;

			cCURPT = filaTrab.createCell(numCel);
			cCURPT.setCellValue(regDAT.getValorPorDescripcionContains("CURP"));
			numCel++;

			totales = regDAT.getPercepDeducNeto();

			cPERCEPT = filaTrab.createCell(numCel);
			cPERCEPT.setCellStyle(styleMoneda);
			cPERCEPT.setCellValue(totales.get(0).doubleValue());
			numCel++;

			cDEDUCT = filaTrab.createCell(numCel);
			cDEDUCT.setCellStyle(styleMoneda);
			cDEDUCT.setCellValue(totales.get(1).doubleValue());
			numCel++;

			cNETOT = filaTrab.createCell(numCel);
			cNETOT.setCellStyle(styleMoneda);
			cNETOT.setCellValue(totales.get(2).doubleValue());
			numCel++;

			// Verifica si cada uno de los conceptos del registro se
			// encuentra en el mapa de conceptos de la página
			// en caso de no estarlo lo añade con su indice de fila
			// y valor a su arrayList, en caso de estar añade
			// también a la lista un registro más

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Localizar la posición del concepto
			    for (int conPos = 0; conPos < nombreConceptosPagina.size(); conPos++)
			    {

				if (conceptoNuevo.equalsIgnoreCase(nombreConceptosPagina.get(conPos)))
				{
				    posConceptoNuevo = colBase + conPos;
				    break;
				}

			    }

			    Cell celdaNuevoConcepto = filaTrab.createCell(posConceptoNuevo);
			    celdaNuevoConcepto.setCellType(Cell.CELL_TYPE_NUMERIC);
			    celdaNuevoConcepto.setCellStyle(styleMoneda);

			    celdaNuevoConcepto.setCellValue(con.getValor().doubleValue());

			    // System.out.print( primeraFila.toString());
			}

			if (this.incluirDifSubsidioImpuestos)
			{

			    numCel = numCel + nombreConceptosPagina.size();

			    sumaImpuestos = regDAT.getValorEnListaConceptoAcum("01", null, null, "SE");
			    sumaSubsidio = regDAT.getValorEnListaConceptoAcum("SE", null, null, null);

			    cImpuestos = filaTrab.createCell(numCel);
			    cImpuestos.setCellStyle(styleMoneda);
			    cImpuestos.setCellValue(sumaImpuestos.doubleValue());
			    numCel++;

			    cSubsidio = filaTrab.createCell(numCel);
			    cSubsidio.setCellStyle(styleMoneda);
			    cSubsidio.setCellValue(sumaSubsidio.doubleValue());
			    numCel++;

			    cDifImpuestosSubsidio = filaTrab.createCell(numCel);
			    cDifImpuestosSubsidio.setCellStyle(styleMoneda);
			    cDifImpuestosSubsidio.setCellValue(sumaImpuestos.subtract(sumaSubsidio).doubleValue());
			    numCel++;

			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

		prds.remove(x);
		x--;

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto productoP : prdsSublist)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			productoP.getAño() + "-" + productoP.getQuincena() + " " + productoP.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    /*
	     * for (Producto cheq : chequesCanceladosSublist) { Row fi =
	     * paginaProd.createRow(fila); Cell celp = fi.createCell(1);
	     * celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " +
	     * cheq.getNombreProducto()); fila++;
	     * 
	     * }
	     */
	    /*
	     * for (int x = 0; x < libroExcel.getNumberOfSheets(); x++) { for (int c = 0; c
	     * < mapaHojasFilas.size() + 4 + 24; c++) {
	     * libroExcel.getSheetAt(x).autoSizeColumn(c); } }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.dispose();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
			this.incluirDifSubsidioImpuestos ? "Detalle de Pagos con Subsidio.xlsx"
				: "Detalle de Pagos.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionAcumuladoConDetalleDeConceptos()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prdsSublist = this.productosSelec.subList(0, this.productosSelec.size());

	prdsSublist.sort((Producto o1, Producto o2) -> (o2.getAño() + o2.getQuincenaString())
		.compareTo((o1.getAño() + o1.getQuincenaString())));

	// Se obtiene una sublista de clones
	List<Producto> prds = new ArrayList<>();

	prdsSublist.forEach(item ->
	{
	    prds.add((Producto) item.clone());

	});

	List<Producto> chequesCancelados = new ArrayList<>();
	List<Producto> chequesCanceladosSublist = new ArrayList<>();

	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (!this.incluirPension)
		{
		    if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		    {
			prds.remove(prds.get(x));
			x--;
			continue;
		    }

		}

		if (this.cancelarCheques || this.soloCancelaciones)
		{
		    if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		    {
			chequesCancelados.add(prds.get(x));
			prds.remove(prds.get(x));
			x--;

		    }

		}

	    }

	    chequesCancelados.forEach(item ->
	    {
		chequesCanceladosSublist.add((Producto) item.clone());
	    });

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);

	    int fila = 0;
	    int numCel = 0;

	    XSSFFont font = (XSSFFont) libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();
	    style.setFont(font);

	    XSSFCellStyle styleMoneda = (XSSFCellStyle) libroExcel.createCellStyle();
	    styleMoneda.setDataFormat((short) 7);

	    boolean cancelado = false;

	    Concepto con;
	    SXSSFSheet pagina = null;
	    PlantillaRegistro regDAT;
	    Producto producto;
	    Row filaExcel = null;
	    String conceptoNuevo;
	    Cell cAño = null;
	    Cell cQna = null;
	    Cell cAñoValor = null;
	    Cell cQnaValor = null;
	    Cell cCodigoPuesto = null;
	    Cell cCodigoConcepto = null;
	    Cell cImporteConcepto = null;
	    Cell cCodigoPuestoValor = null;
	    Cell cCodigoConceptoValor = null;
	    Cell cImporteValor = null;
	    Cell cComprobante = null;
	    Cell cComprobanteValor = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		producto = prds.get(x);
		System.out.println("Ingresando conceptos del Producto: " + producto.getDescripcion() + " / "
			+ producto.getNombreProducto());

		producto.updateRegistrosTRAConConceptos(true, true, true, false, false, false);

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    if (unid.getDescripcion().toLowerCase().contains("610"))
		    {
			continue;
		    }

		    pagina = libroExcel.getSheet(unid.getDescripcion());

		    if (pagina == null)
		    {
			pagina = libroExcel.createSheet("" + unid.getDescripcion());
			pagina.trackAllColumnsForAutoSizing();

			// Se crean los encabezados de la página
			fila = 0;
			numCel = 0;

			filaExcel = pagina.createRow(fila);

			cAño = filaExcel.createCell(numCel);
			cAño.setCellValue("AÑO");
			cAño.setCellStyle(style);
			numCel++;

			cQna = filaExcel.createCell(numCel);
			cQna.setCellValue("QUINCENA");
			cQna.setCellStyle(style);
			numCel++;

			cComprobante = filaExcel.createCell(numCel);
			cComprobante.setCellValue("COMPROBANTE");
			cComprobante.setCellStyle(style);
			numCel++;

			cCodigoPuesto = filaExcel.createCell(numCel);
			cCodigoPuesto.setCellValue("CODIGO PUESTO");
			cCodigoPuesto.setCellStyle(style);
			numCel++;

			cCodigoConcepto = filaExcel.createCell(numCel);
			cCodigoConcepto.setCellValue("CONCEPTO");
			cCodigoConcepto.setCellStyle(style);
			numCel++;

			cImporteConcepto = filaExcel.createCell(numCel);
			cImporteConcepto.setCellValue("IMPORTE");
			cImporteConcepto.setCellStyle(style);
			numCel++;

		    }

		    fila = pagina.getLastRowNum() + 1;

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (this.cancelarCheques || this.soloCancelaciones)
			{

			    // Comparar con los cheques cancelados
			    for (Producto chequeCan : chequesCancelados)
			    {
				clonCheq = chequeCan;

				if (clonCheq.getRegistrosTRA() == null)
				{
				    clonCheq.updateRegistrosTRA();
				}

				for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
				{

				    for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				    {
					// Quincena de proceso o real
					// Año de proceso o real
					if (regDAT.getValorPorDescripcionContains("mero de emple")
						.equals(regCheq.getValorPorDescripcionContains("mero de emple")))
					{

					    if (regDAT.getValorPorDescripcionContains("mero de cheque")
						    .equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					    {

						if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
							regCheq.getValorPorDescripcionContains("odo de pago inicia"))
							&& regDAT.getValorPorDescripcionContains("odo de pago fina")
								.equals(regCheq.getValorPorDescripcionContains(
									"odo de pago fina")))
							|| (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
								.equals(regCheq.getValorPorDescripcionContains(
									"ena de Proceso o Re"))
								&& regDAT
									.getValorPorDescripcionContains(
										"o de Proceso o R")
									.equals(regCheq.getValorPorDescripcionContains(
										"o de Proceso o R"))))
						{

						    if (!this.soloCancelaciones)
						    {
							// Si no se busca solo
							// las cancelaciones de
							// cheques se remueve
							// del producto
							// unidCheq.getRegistrosDAT().remove(regCheq);
						    }
						    else
						    {
							regDAT = regCheq;
						    }

						    System.out.println("Cancelado el cheque "
							    + regCheq.getValorPorDescripcionContains("mero de chequ")
							    + " producto " + producto.getNombreProducto());
						    cancelado = true;
						    break;

						}

					    }

					}

				    }

				    if (cancelado)
				    {
					break;
				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado && !this.soloCancelaciones)
			    {

				continue;
			    }

			}

			// Si solo estamos buscando generar el detalle de
			// los
			// cheques que se cancelaron y el cheque no está
			// cancelado se continua
			if (this.soloCancelaciones && !cancelado)
			{
			    continue;
			}

			// Verifica si cada uno de los conceptos del registro se
			// encuentra en el mapa de conceptos de la página
			// en caso de no estarlo lo añade con su indice de fila
			// y valor a su arrayList, en caso de estar añade
			// también a la lista un registro más

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    numCel = 0;

			    con = (Concepto) obj;

			    if (con.getTipoConcepto() == 2 && con.getClave().contentEquals("01")
				    && con.getPartidaAntecedente().equals("SE"))
			    {
				con.setTipoConcepto(1);
				con.setClave("SE");
				con.setPartidaAntecedente("00");
				con.setValor(con.getValor().multiply(new BigDecimal("-1")));
			    }

			    if (con.getTipoConcepto() != 1)
			    {
				continue;
			    }

			    conceptoNuevo = "0" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    filaExcel = pagina.createRow(fila);

			    cAñoValor = filaExcel.createCell(numCel);
			    cAñoValor.setCellValue("" + producto.getAño());
			    numCel++;

			    cQnaValor = filaExcel.createCell(numCel);
			    cQnaValor.setCellValue(producto.getQuincenaString());
			    numCel++;

			    cComprobanteValor = filaExcel.createCell(numCel);
			    cComprobanteValor.setCellValue(regDAT.getValorPorDescripcionContains("mero de cheque"));
			    numCel++;

			    cCodigoPuestoValor = filaExcel.createCell(numCel);

			    if (producto.getPlaza().getIdPlaza() == 4)
			    {
				cCodigoPuestoValor.setCellValue(regDAT.getValorPorDescripcionContains("colonia"));
			    }
			    else
			    {
				cCodigoPuestoValor.setCellValue(regDAT.getValorPorDescripcionContains("puesto"));
			    }
			    numCel++;

			    cCodigoConceptoValor = filaExcel.createCell(numCel);
			    cCodigoConceptoValor.setCellValue(conceptoNuevo);
			    numCel++;

			    cImporteValor = filaExcel.createCell(numCel);
			    cImporteValor.setCellType(Cell.CELL_TYPE_NUMERIC);
			    cImporteValor.setCellValue(con.getValor().doubleValue());
			    numCel++;
			    fila++;

			}

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

		prds.remove(x);
		x--;

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto productoP : prdsSublist)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			productoP.getAño() + "-" + productoP.getQuincena() + " " + productoP.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    /*
	     * for (Producto cheq : chequesCanceladosSublist) { Row fi =
	     * paginaProd.createRow(fila); Cell celp = fi.createCell(1);
	     * celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " +
	     * cheq.getNombreProducto()); fila++;
	     * 
	     * }
	     */
	    /*
	     * for (int x = 0; x < libroExcel.getNumberOfSheets(); x++) { for (int c = 0; c
	     * < mapaHojasFilas.size() + 4 + 24; c++) {
	     * libroExcel.getSheetAt(x).autoSizeColumn(c); } }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.dispose();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (

	Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionFormatoRemuneracion()
    {

	Map<String, ArchivoPuesto> mapaCatalogoPuestos = new HashMap<String, ArchivoPuesto>();

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, (o1, o2) ->
	{
	    Producto reg1 = (Producto) o1;
	    Producto reg2 = (Producto) o2;

	    String q1 = "" + reg1.getQuincena();
	    String q2 = "" + reg2.getQuincena();

	    if (q1.length() == 1)
	    {
		q1 = "0" + q1;
	    }

	    if (q2.length() == 1)
	    {
		q2 = "0" + q2;
	    }

	    return ((Comparable<?>) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	});

	// HashMap - clave XSSFSheet de la unidad correspondiente
	// - Valor otro HashMap

	// Valor del HashMap de Unidad
	// - Clave: Concepto
	// - Valor: List<Object>: Almacenará el número de fila de la hoja donde
	// irá el valor y el valor BigDecimal

	// Hojas, con filas y arreglo de columnas valor
	HashMap<Sheet, List<List<Object>>> mapaHojasFilas = new HashMap<Sheet, List<List<Object>>>();

	HashMap<Sheet, List<String>> conceptosPorPagina = new HashMap<Sheet, List<String>>();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto prd = prds.get(x);
		String key = "" + prd.getPlaza().getIdPlaza() + "-" + prd.getAño() + "-" + prd.getQuincena();

		if (!this.incluirPension)
		{
		    if (prd.getTipoNomina().getDescripcion().contains("Pensi"))
		    {
			prds.remove(prd);
			x--;
			continue;
		    }

		}

		if (this.cancelarCheques)
		{
		    if (prd.getTipoProducto().getDescripcion().contains("Cancelado"))
		    {
			chequesCancelados.add(prd);
			prds.remove(prd);
			x--;

		    }

		}

		// Se van generando los catálogos de puestos para poner en el
		// formato
		ArchivoPuesto catalogo = mapaCatalogoPuestos.get(key);

		if (catalogo == null)
		{

		    List<ArchivoPuesto> catPuestos = utilidades.getArchivosPuesto(prd.getAño(), prd.getQuincena(),
			    prd.getAño(), prd.getQuincena(), prd.getPlaza().getIdPlaza());

		    if (catPuestos.isEmpty())
		    {
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Catálogo de Puestos",
					"Aún no se cuenta con el catálogo para la plaza: "
						+ prd.getPlaza().getDescripcionPlaza() + " en la quincena "
						+ prd.getAño() + "/" + prd.getQuincena()));

			return;
		    }

		    ArchivoPuesto archivoPuesto = catPuestos.get(0);

		    archivoPuesto.updatePlantillaRegistros();
		    archivoPuesto.updateRegistros();

		    mapaCatalogoPuestos.put(key, archivoPuesto);
		}

	    }

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);

	    XSSFFont font = (XSSFFont) libroExcel.createFont();

	    XSSFFont fontBold = (XSSFFont) libroExcel.createFont();
	    fontBold.setBoldweight(Font.BOLDWEIGHT_BOLD);

	    int fila = 0;
	    int numCel = 0;

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();
	    style.setFont(fontBold);

	    XSSFCellStyle styleMoneda = (XSSFCellStyle) libroExcel.createCellStyle();
	    styleMoneda.setDataFormat((short) 7);
	    styleMoneda.setFont(font);

	    boolean cancelado = false;

	    Concepto con;

	    List<String> nombreConceptosPagina = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Producto: " + producto.getNombreProducto());
		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    SXSSFSheet pagina = libroExcel.getSheet(unid.getDescripcion());
		    List<List<Object>> filasConConceptos = null;

		    if (pagina == null)
		    {
			pagina = libroExcel.createSheet("" + unid.getDescripcion());
			pagina.trackAllColumnsForAutoSizing();

			filasConConceptos = new ArrayList<>();

			mapaHojasFilas.put(pagina, filasConConceptos);

			nombreConceptosPagina = new ArrayList<>();
			conceptosPorPagina.put(pagina, nombreConceptosPagina);

			// Se crean los encabezados de la página
			fila = 0;
			numCel = 0;

			Row filaEncabezado = pagina.createRow(fila);

			Cell cAño = filaEncabezado.createCell(numCel);
			cAño.setCellValue("NO");
			cAño.setCellStyle(style);
			numCel++;

			Cell cQna = filaEncabezado.createCell(numCel);
			cQna.setCellValue("MES");
			cQna.setCellStyle(style);
			numCel++;

			Cell cProducto = filaEncabezado.createCell(numCel);
			cProducto.setCellValue("ENTIDAD");
			cProducto.setCellStyle(style);
			numCel++;

			Cell cCheque = filaEncabezado.createCell(numCel);
			cCheque.setCellValue("TIPO DE CENTRO DE SALUD O DE HOSPITAL");
			cCheque.setCellStyle(style);
			numCel++;

			Cell c = filaEncabezado.createCell(numCel);
			c.setCellValue("CLAVE ÚNICA DE ESTABLECIMIENTOS DE SALUD (CLUES)");
			c.setCellStyle(style);
			numCel++;

			Cell cPaterno = filaEncabezado.createCell(numCel);
			cPaterno.setCellValue("NOMBRE DE LA UNIDAD");
			cPaterno.setCellStyle(style);
			numCel++;

			Cell cMaterno = filaEncabezado.createCell(numCel);
			cMaterno.setCellValue("ÁREA DE ADSCRIPCIÓN");
			cMaterno.setCellStyle(style);
			numCel++;

			Cell cNombres = filaEncabezado.createCell(numCel);
			cNombres.setCellValue("PUESTO");
			cNombres.setCellStyle(style);
			numCel++;

			Cell cFIngreso = filaEncabezado.createCell(numCel);
			cFIngreso.setCellValue("CLAVE DE PUESTO");
			cFIngreso.setCellStyle(style);
			numCel++;

			Cell cDLaborados = filaEncabezado.createCell(numCel);
			cDLaborados.setCellValue("SERVICIO");
			cDLaborados.setCellStyle(style);
			numCel++;

			Cell cTTrabajador = filaEncabezado.createCell(numCel);
			cTTrabajador.setCellValue("RAMA");
			cTTrabajador.setCellStyle(style);
			numCel++;

			Cell cTMando = filaEncabezado.createCell(numCel);
			cTMando.setCellValue("NOMBRE COMPLETO");
			cTMando.setCellStyle(style);
			numCel++;

			Cell cCMando = filaEncabezado.createCell(numCel);
			cCMando.setCellValue("RFC CON HOMOCLAVE");
			cCMando.setCellStyle(style);
			numCel++;

			Cell CURPMando = filaEncabezado.createCell(numCel);
			CURPMando.setCellValue("CURP");
			CURPMando.setCellStyle(style);
			numCel++;

			Cell cPago1 = filaEncabezado.createCell(numCel);
			cPago1.setCellValue("TURNO (MAT/VESP/AMB)");
			cPago1.setCellStyle(style);
			numCel++;

			Cell cPago2 = filaEncabezado.createCell(numCel);
			cPago2.setCellValue("FECHA DE INGRESO");
			cPago2.setCellStyle(style);
			numCel++;

			fila++;
		    }
		    else
		    {
			fila = pagina.getLastRowNum();
			filasConConceptos = mapaHojasFilas.get(pagina);

			nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    }

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (this.cancelarCheques)
			{

			    // Comparar con los cheques cancelados
			    for (Producto chequeCan : chequesCancelados)
			    {
				clonCheq = chequeCan;

				if (clonCheq.getRegistrosTRA() == null)
				{
				    clonCheq.updateRegistrosTRA();
				}

				for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
				{

				    for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				    {
					// Quincena de proceso o real
					// Año de proceso o real
					if (regDAT.getValorPorDescripcionContains("mero de emple")
						.equals(regCheq.getValorPorDescripcionContains("mero de emple")))
					{

					    if (regDAT.getValorPorDescripcionContains("mero de cheque")
						    .equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					    {

						if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
							regCheq.getValorPorDescripcionContains("odo de pago inicia"))
							&& regDAT.getValorPorDescripcionContains("odo de pago fina")
								.equals(regCheq.getValorPorDescripcionContains(
									"odo de pago fina")))
							|| (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
								.equals(regCheq.getValorPorDescripcionContains(
									"ena de Proceso o Re"))
								&& regDAT
									.getValorPorDescripcionContains(
										"o de Proceso o R")
									.equals(regCheq.getValorPorDescripcionContains(
										"o de Proceso o R"))))
						{
						    unidCheq.getRegistrosDAT().remove(regCheq);
						    cancelado = true;
						    break;
						}

					    }

					}

				    }

				    if (cancelado)
				    {
					break;
				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado)
			    {

				continue;
			    }

			}

			regDAT.ajustaConcepto3Acum();

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    String conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Busca para acumular solo los conceptos de la
			    // página
			    boolean existeConcepto = nombreConceptosPagina.contains(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());

			    if (!existeConcepto)
			    {
				Collator comparador = Collator.getInstance();
				comparador.setStrength(Collator.IDENTICAL);

				boolean conceptoEntreConceptos = false;

				// Se recorre el arreglo para ir ordenando
				// alfabéticamente la lista de conceptos
				for (int posCon = 0; posCon < nombreConceptosPagina.size(); posCon++)
				{

				    if (comparador.compare(conceptoNuevo, nombreConceptosPagina.get(posCon)) < 1)
				    {

					nombreConceptosPagina.add(posCon, conceptoNuevo);
					conceptoEntreConceptos = true;

					break;

				    }

				}

				if (!conceptoEntreConceptos)
				{
				    nombreConceptosPagina.add(conceptoNuevo);

				}

			    }

			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

	    }

	    // Se ponen los encabezados
	    Set setPaginas = conceptosPorPagina.entrySet();
	    Iterator iteratorPaginas = setPaginas.iterator();

	    int colBase = 16;

	    // Recorre unidades
	    while (iteratorPaginas.hasNext())
	    {
		Map.Entry<?, List<String>> me = (Map.Entry) iteratorPaginas.next();

		Sheet paginaUnidad = (Sheet) me.getKey();

		List<String> registros = (List<String>) me.getValue();

		colBase = 16;

		boolean percepciones = true;
		int totalConceptos = registros.size();

		int x;

		for (x = 0; x < totalConceptos; x++)
		{
		    String concepto = registros.get(x);

		    if (percepciones)
		    {
			// Se añade el indicador de percepciones
			if (concepto.startsWith("2"))
			{
			    percepciones = false;
			    registros.add(x, "percep");

			    Cell celPercep = paginaUnidad.getRow(0).createCell(colBase);
			    celPercep.setCellValue("PERCEPCIÓN TOTAL");
			    celPercep.setCellStyle(style);

			    colBase++;
			    totalConceptos++;

			    continue;

			}

		    }

		    Cell celdaEncabezado = paginaUnidad.getRow(0).getCell(colBase);

		    if (celdaEncabezado == null)
		    {
			Cell cel = paginaUnidad.getRow(0).createCell(colBase);
			cel.setCellValue(concepto);
			cel.setCellStyle(style);
		    }

		    colBase++;

		}

		registros.add(x, "deduc");

		Cell celDeduc = paginaUnidad.getRow(0).createCell(colBase);
		celDeduc.setCellValue("DEDUCCIÓN TOTAL");
		celDeduc.setCellStyle(style);

		colBase++;

		x++;

		registros.add(x, "neto");

		Cell celNeto = paginaUnidad.getRow(0).createCell(colBase);
		celNeto.setCellValue("PERCEPCIÓN NETA");
		celNeto.setCellStyle(style);

		colBase++;

		Cell celDescripcionPuesto = paginaUnidad.getRow(0).createCell(colBase);
		celDescripcionPuesto.setCellValue("DESCRIPCIÓN DEL PUESTO");
		celDescripcionPuesto.setCellStyle(style);

		colBase++;

		Cell celPercetenece = paginaUnidad.getRow(0).createCell(colBase);
		celPercetenece.setCellValue("PERTENECE AL CAUSES/FPGC/SMNG/SP");
		celPercetenece.setCellStyle(style);

		colBase++;

		Cell celRegularizado = paginaUnidad.getRow(0).createCell(colBase);
		celRegularizado.setCellValue("REGULARIZADO");
		celRegularizado.setCellStyle(style);

		colBase++;

		me.setValue(registros);

	    }

	    String conceptoNuevo = null;
	    String nombres = null;

	    Row filaTrab = null;

	    Cell detcAño = null;

	    Cell detcQna = null;

	    Cell detcProducto = null;

	    Cell detcCheque = null;

	    Cell cTrab = null;

	    Cell cPaternoT = null;

	    Cell cMaternoT = null;

	    Cell cNombresT = null;

	    Cell cFIngresoT = null;

	    Cell cDLaboradosT = null;

	    Cell cTTrabajadorT = null;

	    Cell cTMandoT = null;

	    Cell cCMandoT = null;

	    Cell cCURP = null;

	    Sheet pagina = null;
	    PlantillaRegistro regDAT;
	    Producto prd;
	    int posConceptoNuevo = 0;
	    colBase = 16;

	    String key = null;

	    ArchivoPuesto archivoCatPuesto = null;
	    PlantillaRegistro regPuesto = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		prd = prds.get(x);

		System.out.println("Ingresando conceptos del Producto: " + prd.getNombreProducto());
		prd.updateRegistrosTRA();

		for (UnidadProducto unid : prd.getUnidadResponsable())
		{

		    pagina = libroExcel.getSheet(unid.getDescripcion());

		    fila = pagina.getLastRowNum() + 1;
		    nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			// Comparar con los cheques cancelados
			for (Producto chequeCan : chequesCancelados)
			{
			    clonCheq = chequeCan;

			    if (clonCheq.getRegistrosTRA() == null)
			    {
				clonCheq.updateRegistrosTRA();
			    }

			    for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
			    {

				for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				{
				    // Quincena de proceso o real
				    // Año de proceso o real
				    if (regDAT.getValorPorDescripcionContains("mero de emple")
					    .equals(regCheq.getValorPorDescripcionContains("mero de emple")))
				    {

					if (regDAT.getValorPorDescripcionContains("mero de cheque")
						.equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					{

					    if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
						    regCheq.getValorPorDescripcionContains("odo de pago inicia"))
						    && regDAT.getValorPorDescripcionContains("odo de pago fina").equals(
							    regCheq.getValorPorDescripcionContains("odo de pago fina")))
						    || (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
							    .equals(regCheq.getValorPorDescripcionContains(
								    "ena de Proceso o Re"))
							    && regDAT.getValorPorDescripcionContains("o de Proceso o R")
								    .equals(regCheq.getValorPorDescripcionContains(
									    "o de Proceso o R"))))
					    {
						unidCheq.getRegistrosDAT().remove(regCheq);
						cancelado = true;
						break;
					    }

					}

				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado)
			    {
				break;
			    }

			}

			if (cancelado)
			{

			    continue;
			}

			nombres = regDAT.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			filaTrab = pagina.createRow(fila);

			detcAño = filaTrab.createCell(numCel);
			pagina.autoSizeColumn(numCel);
			detcAño.setCellValue(fila);
			numCel++;

			detcQna = filaTrab.createCell(numCel);
			detcQna.setCellValue(utilidades.getNombreMes(prd.getQuincena()));
			numCel++;

			detcProducto = filaTrab.createCell(numCel);
			detcProducto.setCellValue("CAMPECHE");
			numCel++;

			detcCheque = filaTrab.createCell(numCel);
			detcCheque.setCellValue("");
			numCel++;

			cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(regDAT.getValorPorDescripcionContains("icencia Sin Goce de S"));

			numCel++;

			cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(regDAT.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(regDAT.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			// Se obtiene la descripción del puesto según el
			// catálogo de puestos de la quincena
			key = "" + prd.getPlaza().getIdPlaza() + "-" + prd.getAño() + "-" + prd.getQuincena();

			archivoCatPuesto = mapaCatalogoPuestos.get(key);

			regPuesto = archivoCatPuesto.getRegistro(regDAT.getValorPorDescripcion("Puesto").trim());

			if (regPuesto == null)
			{
			    cNombresT = filaTrab.createCell(numCel);
			    cNombresT.setCellValue("Puesto no en catálogo");
			}
			else
			{
			    cNombresT = filaTrab.createCell(numCel);
			    cNombresT.setCellValue(regPuesto.getValorPorDescripcionContains("Nombre del puest"));

			}

			numCel++;

			cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(regDAT.getValorPorDescripcion("Puesto"));
			numCel++;

			cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue("");
			numCel++;

			cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue("");
			numCel++;

			cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(regDAT.getValorPorDescripcionContains("Nombre del Empl"));
			numCel++;

			cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(regDAT.getValorPorDescripcionContains("RFC"));

			numCel++;

			cCURP = filaTrab.createCell(numCel);
			cCURP.setCellValue(regDAT.getValorPorDescripcionContains("CURP"));

			numCel++;

			Cell cPago1 = filaTrab.createCell(numCel);
			cPago1.setCellValue("");
			numCel++;

			Cell cPago2 = filaTrab.createCell(numCel);
			cPago2.setCellValue(regDAT.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			// Verifica si cada uno de los conceptos del registro se
			// encuentra en el mapa de conceptos de la página
			// en caso de no estarlo lo añade con su indice de fila
			// y valor a su arrayList, en caso de estar añade
			// también a la lista un registro más

			// Se hace un ajuste para que el concepto 303 00 salga
			// como 202

			regDAT.ajustaConcepto3Acum();

			// Se obtiene las percepciones deducciones y neto
			List<BigDecimal> percepDeducNeto = regDAT.getPercepDeducNeto();

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    conceptoNuevo = "" + con.getTipoConcepto() + con.getClave() + " "
				    + con.getPartidaAntecedente();

			    // Localizar la posición del concepto
			    for (int conPos = 0; conPos < nombreConceptosPagina.size(); conPos++)
			    {

				if (conceptoNuevo.equalsIgnoreCase(nombreConceptosPagina.get(conPos)))
				{
				    posConceptoNuevo = colBase + conPos;
				    break;
				}

			    }

			    Cell celdaNuevoConcepto = filaTrab.createCell(posConceptoNuevo);
			    celdaNuevoConcepto.setCellStyle(styleMoneda);
			    celdaNuevoConcepto.setCellType(Cell.CELL_TYPE_NUMERIC);

			    celdaNuevoConcepto.setCellValue(con.getValor().doubleValue());

			    // System.out.print( primeraFila.toString());
			}

			// Se localiza la posición de percepciones, deducciones
			// y al lado de deducciones el neto
			conceptoNuevo = "percep";

			// Localizar la posición del concepto
			for (int conPos = 0; conPos < nombreConceptosPagina.size(); conPos++)
			{

			    if (conceptoNuevo.equalsIgnoreCase(nombreConceptosPagina.get(conPos)))
			    {
				posConceptoNuevo = colBase + conPos;
				break;
			    }

			}

			Cell celdaPercep = filaTrab.createCell(posConceptoNuevo);
			celdaPercep.setCellStyle(styleMoneda);
			celdaPercep.setCellType(Cell.CELL_TYPE_NUMERIC);

			celdaPercep.setCellValue(percepDeducNeto.get(0).doubleValue());

			conceptoNuevo = "deduc";

			// Localizar la posición del concepto
			for (int conPos = 0; conPos < nombreConceptosPagina.size(); conPos++)
			{

			    if (conceptoNuevo.equalsIgnoreCase(nombreConceptosPagina.get(conPos)))
			    {
				posConceptoNuevo = colBase + conPos;
				break;
			    }

			}

			Cell celdaDeduc = filaTrab.createCell(posConceptoNuevo);
			celdaDeduc.setCellStyle(styleMoneda);
			celdaDeduc.setCellType(Cell.CELL_TYPE_NUMERIC);
			celdaDeduc.setCellValue(percepDeducNeto.get(1).doubleValue());

			posConceptoNuevo++;

			Cell celdaNeto = filaTrab.createCell(posConceptoNuevo);
			celdaNeto.setCellStyle(styleMoneda);
			celdaNeto.setCellType(Cell.CELL_TYPE_NUMERIC);
			celdaNeto.setCellValue(percepDeducNeto.get(2).doubleValue());

			posConceptoNuevo++;

			if (regPuesto == null)
			{
			    Cell celDescripcionPuesto = filaTrab.createCell(posConceptoNuevo);
			    celDescripcionPuesto.setCellValue("Puesto no en catálogo");
			}
			else
			{
			    Cell celDescripcionPuesto = filaTrab.createCell(posConceptoNuevo);
			    celDescripcionPuesto
				    .setCellValue(regPuesto.getValorPorDescripcionContains("Nombre del puest"));

			}

			posConceptoNuevo++;

			Cell celPercetenece = filaTrab.createCell(posConceptoNuevo);
			celPercetenece.setCellValue("");

			posConceptoNuevo++;

			Cell celRegularizado = filaTrab.createCell(posConceptoNuevo);
			celRegularizado.setCellValue("");

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		prd.setRegistrosTRA(new ArrayList<>());
		prd.setRegistrosDAT(new ArrayList<>());
		prd.setConceptos(new ArrayList<>());
		prd.setUnidadResponsable(new ArrayList<>());

		// prds.remove(x);
		// x--;

	    }

	    // Se ponen los encabezados
	    setPaginas = conceptosPorPagina.entrySet();
	    iteratorPaginas = setPaginas.iterator();

	    colBase = 16;

	    // Recorre unidades
	    while (iteratorPaginas.hasNext())
	    {
		Map.Entry<?, List<String>> me = (Map.Entry) iteratorPaginas.next();

		Sheet paginaUnidad = (Sheet) me.getKey();

		List<String> registros = (List<String>) me.getValue();

		colBase = 16;

		int totalConceptos = registros.size();

		for (int x = 0; x < totalConceptos + colBase + 3; x++)
		{

		    paginaUnidad.autoSizeColumn(x);

		}

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto productoP : prds)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			productoP.getAño() + "-" + productoP.getQuincena() + " " + productoP.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(1);
		celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " + cheq.getNombreProducto());
		fila++;

	    }

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.dispose();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionReporteCodigosConceptos()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prdsSublist = this.productosSelec.subList(0, this.productosSelec.size());

	prdsSublist.sort((Producto o1, Producto o2) -> o2.getAñoQuincenaString().compareTo(o1.getAñoQuincenaString()));

	// Se obtiene una sublista de clones
	List<Producto> prds = new ArrayList<>();

	prdsSublist.forEach(item ->
	{
	    prds.add((Producto) item.clone());

	});

	HashMap<String, List<Concepto>> mapaReporte = new HashMap<String, List<Concepto>>();

	HashMap<Sheet, List<String>> conceptosPorPagina = new HashMap<Sheet, List<String>>();

	List<Producto> chequesCancelados = new ArrayList<>();
	List<Producto> chequesCanceladosSublist = new ArrayList<>();

	Producto clon;
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (!this.incluirPension)
		{
		    if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		    {
			prds.remove(prds.get(x));
			x--;
			continue;
		    }

		}

		if (this.cancelarCheques || this.soloCancelaciones)
		{
		    if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		    {
			chequesCancelados.add(prds.get(x));
			prds.remove(prds.get(x));
			x--;

		    }

		}

	    }

	    chequesCancelados.forEach(item ->
	    {
		chequesCanceladosSublist.add((Producto) item.clone());
	    });

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);
	    List<BigDecimal> totales;

	    int fila = 0;
	    int numCel = 0;

	    XSSFFont font = (XSSFFont) libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();
	    style.setFont(font);

	    XSSFCellStyle styleMoneda = (XSSFCellStyle) libroExcel.createCellStyle();
	    styleMoneda.setDataFormat((short) 7);

	    boolean cancelado = false;

	    Concepto con;

	    List<String> nombreConceptosPagina = null;

	    String codigoPuestoRegistro = null;
	    List<Concepto> conceptosCodigoPuesto = null;

	    boolean acumulado = false;

	    Set setMapaReporte = null;
	    Iterator iteratorReporte = null;

	    Map.Entry<String, Concepto> entrada = null;
	    Concepto conAcumular = null;
	    Concepto conceptoAñadiendo = null;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Producto: " + producto.getNombreProducto());
		producto.updateRegistrosTRA();

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    if (unid.getDescripcion().equals("610"))
		    {
			continue;
		    }

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			regDAT.ajustaConcepto3Acum();

			cancelado = false;

			if (this.cancelarCheques || this.soloCancelaciones)
			{

			    // Comparar con los cheques cancelados
			    for (Producto chequeCan : chequesCancelados)
			    {
				clonCheq = chequeCan;

				if (clonCheq.getRegistrosTRA() == null)
				{
				    clonCheq.updateRegistrosTRA();
				}

				for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
				{

				    for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				    {
					// Quincena de proceso o real
					// Año de proceso o real
					if (regDAT.getValorPorDescripcionContains("mero de emple")
						.equals(regCheq.getValorPorDescripcionContains("mero de emple")))
					{

					    if (regDAT.getValorPorDescripcionContains("mero de cheque")
						    .equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					    {

						if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
							regCheq.getValorPorDescripcionContains("odo de pago inicia"))
							&& regDAT.getValorPorDescripcionContains("odo de pago fina")
								.equals(regCheq.getValorPorDescripcionContains(
									"odo de pago fina")))
							|| (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
								.equals(regCheq.getValorPorDescripcionContains(
									"ena de Proceso o Re"))
								&& regDAT
									.getValorPorDescripcionContains(
										"o de Proceso o R")
									.equals(regCheq.getValorPorDescripcionContains(
										"o de Proceso o R"))))
						{

						    System.out.println("Cancelado el cheque "
							    + regCheq.getValorPorDescripcionContains("mero de chequ"));

						    if (!this.soloCancelaciones)
						    {
							// Si no se busca solo
							// las cancelaciones de
							// cheques se remueve
							// del producto
							// unidCheq.getRegistrosDAT().remove(regCheq);
						    }
						    else
						    {
							regDAT = regCheq;
						    }

						    cancelado = true;
						    break;
						}

					    }

					}

				    }

				    if (cancelado)
				    {
					break;
				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado && !this.soloCancelaciones)
			    {

				continue;
			    }

			}

			// Si solo estamos buscando generar el detalle de los
			// cheques que se cancelaron y el cheque no está
			// cancelado se continua
			if (this.soloCancelaciones && !cancelado)
			{
			    continue;
			}

			if (producto.getPlaza().getIdPlaza() == 4)
			{
			    codigoPuestoRegistro = regDAT.getValorPorDescripcionContains("colonia");

			}
			else
			{
			    codigoPuestoRegistro = regDAT.getValorPorDescripcionContains("puesto");

			}

			if (mapaReporte.containsKey(codigoPuestoRegistro))
			{
			    conceptosCodigoPuesto = mapaReporte.get(codigoPuestoRegistro);

			    setMapaReporte = regDAT.getConceptosAcum().entrySet();
			    iteratorReporte = setMapaReporte.iterator();

			    while (iteratorReporte.hasNext())
			    {
				entrada = (Map.Entry<String, Concepto>) iteratorReporte.next();

				conAcumular = entrada.getValue();

				if (conAcumular.getTipoConcepto() == 2)
				{
				    continue;
				}

				// Recorrer conceptos ya existentes en búsqueda
				// de acumular o añadir
				acumulado = false;

				for (Concepto conExistente : conceptosCodigoPuesto)
				{

				    if (conAcumular.getTipoConcepto() == conExistente.getTipoConcepto()
					    && conAcumular.getClave().equals(conExistente.getClave())
					    && conAcumular.getPartidaAntecedente()
						    .equals(conExistente.getPartidaAntecedente()))
				    {

					if (conAcumular.getTotalCasos() > 1)
					{
					    /*
					     * System.out. println("Más de 1 caso en concepto acumulado " +
					     * conAcumular.getTipoConcepto() + " " + conAcumular.getClave() + " " +
					     * conAcumular.getPartidaAntecedente ());
					     */

					    conExistente.setValor(conExistente.getValor().add(conAcumular.getValor()));
					    conExistente.setTotalCasos(
						    conExistente.getTotalCasos() + conAcumular.getTotalCasos());
					}
					else
					{
					    conExistente.addValor(conAcumular.getValor());

					}

					acumulado = true;
					break;
				    }

				}

				if (!acumulado)
				{
				    conceptosCodigoPuesto.add(conAcumular);
				}

			    }

			}
			else
			{

			    List<Concepto> conceptosAñadiendo = new ArrayList<>();

			    setMapaReporte = regDAT.getConceptosAcum().entrySet();
			    iteratorReporte = setMapaReporte.iterator();

			    while (iteratorReporte.hasNext())
			    {
				entrada = (Map.Entry<String, Concepto>) iteratorReporte.next();

				conceptoAñadiendo = entrada.getValue();

				if (conceptoAñadiendo.getTipoConcepto() == 2)
				{
				    continue;
				}

				conceptosAñadiendo.add(conceptoAñadiendo);

			    }

			    mapaReporte.put(codigoPuestoRegistro, conceptosAñadiendo);

			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

	    }

	    System.out.println("Imprimiendo en Excel");

	    SXSSFSheet paginaReporte = libroExcel.createSheet("Productos");

	    fila = 0;
	    numCel = 0;

	    Row ff = paginaReporte.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("CODIGO DE PUESTO");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CONCEPTO");
	    celC.setCellStyle(style);
	    numCel++;

	    Cell celDescripcion = ff.createCell(numCel);
	    celDescripcion.setCellValue("DESCRIPCION");
	    celDescripcion.setCellStyle(style);
	    numCel++;

	    Cell celPeriodicidad = ff.createCell(numCel);
	    celPeriodicidad.setCellValue("PERIODICIDAD");
	    celPeriodicidad.setCellStyle(style);
	    numCel++;

	    Cell celAmbito = ff.createCell(numCel);
	    celAmbito.setCellValue("AMBITO");
	    celAmbito.setCellStyle(style);
	    numCel++;

	    Cell celPartidaEstatal = ff.createCell(numCel);
	    celPartidaEstatal.setCellValue("PARTIDA ESTATAL");
	    celPartidaEstatal.setCellStyle(style);
	    numCel++;

	    Cell celPRamo33 = ff.createCell(numCel);
	    celPRamo33.setCellValue("PARTIDA RAMO 33");
	    celPRamo33.setCellStyle(style);
	    numCel++;

	    Cell celPRamo12 = ff.createCell(numCel);
	    celPRamo12.setCellValue("PARTIDA RAMO 12");
	    celPRamo12.setCellStyle(style);
	    numCel++;

	    Cell celSeGrava = ff.createCell(numCel);
	    celSeGrava.setCellValue("SE GRAVA");
	    celSeGrava.setCellStyle(style);
	    numCel++;

	    Cell celTipoDeConcepto = ff.createCell(numCel);
	    celTipoDeConcepto.setCellValue("TIPO DE CONCEPTO");
	    celTipoDeConcepto.setCellStyle(style);
	    numCel++;

	    Cell celNumeroCasos = ff.createCell(numCel);
	    celNumeroCasos.setCellValue("NUMERO DE CASOS");
	    celNumeroCasos.setCellStyle(style);
	    numCel++;

	    Cell celImporte = ff.createCell(numCel);
	    celImporte.setCellValue("IMPORTE");
	    celImporte.setCellStyle(style);
	    numCel++;

	    Cell celSoporteDocumental = ff.createCell(numCel);
	    celSoporteDocumental.setCellValue("SOPORTE DOCUMENTAL");
	    celSoporteDocumental.setCellStyle(style);
	    numCel++;

	    Cell celFormulaOFormulacion = ff.createCell(numCel);
	    celFormulaOFormulacion.setCellValue("FORMULAR O FORMULACION");
	    celFormulaOFormulacion.setCellStyle(style);
	    numCel++;

	    fila++;

	    setMapaReporte = mapaReporte.entrySet();
	    iteratorReporte = setMapaReporte.iterator();

	    HashMap<String, String> descripcionConceptos = new HashMap<String, String>();
	    String claveConcepto = null;
	    String descConcepto = null;

	    Map.Entry<String, List<Concepto>> entradaPuesto = null;
	    int conteoObtenciones = 0;

	    while (iteratorReporte.hasNext())
	    {
		entradaPuesto = (Map.Entry<String, List<Concepto>>) iteratorReporte.next();

		// System.out.println("Concepto: " + entrada.getKey());

		for (Concepto conceptoImprimiendo : entradaPuesto.getValue())
		{
		    numCel = 0;

		    conceptoImprimiendo.updateDatosAdicionales();

		    Row fi = paginaReporte.createRow(fila);
		    ((SXSSFSheet) paginaReporte).trackAllColumnsForAutoSizing();

		    Cell celp = fi.createCell(numCel);
		    celp.setCellValue(entradaPuesto.getKey());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    claveConcepto = "0" + conceptoImprimiendo.getTipoConcepto() + conceptoImprimiendo.getClave()
			    + conceptoImprimiendo.getPartidaAntecedente();

		    Cell celCon = fi.createCell(numCel);
		    celCon.setCellValue(claveConcepto);
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celDesc = fi.createCell(numCel);

		    descConcepto = descripcionConceptos.get(claveConcepto);

		    if (descConcepto == null)
		    {
			conteoObtenciones++;

			descConcepto = utilidades.getDescripcionConceptoCatalogo(conceptoImprimiendo.getTipoConcepto(),
				conceptoImprimiendo.getClave(), conceptoImprimiendo.getPartidaAntecedente());

			descripcionConceptos.put(claveConcepto, descConcepto != null ? descConcepto : "");
		    }

		    celDesc.setCellValue(descConcepto);
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celPeriodicidadValor = fi.createCell(numCel);
		    celPeriodicidadValor.setCellValue(conceptoImprimiendo.getPeriodicidad());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celAmbitoValor = fi.createCell(numCel);
		    celAmbitoValor.setCellValue(conceptoImprimiendo.getAmbito());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celPartidaEstatalValor = fi.createCell(numCel);
		    celPartidaEstatalValor.setCellValue(conceptoImprimiendo.getPartidaEstatal());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celPartidaRamo33Valor = fi.createCell(numCel);
		    celPartidaRamo33Valor.setCellValue(conceptoImprimiendo.getPartidaRamo33());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celPartidaRamo12Valor = fi.createCell(numCel);
		    celPartidaRamo12Valor.setCellValue(conceptoImprimiendo.getPartidaRamo12());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celSeGravaValor = fi.createCell(numCel);
		    celSeGravaValor.setCellValue(conceptoImprimiendo.getSeGrava());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celTipoDeConceptoValor = fi.createCell(numCel);
		    celTipoDeConceptoValor.setCellValue(conceptoImprimiendo.getTipoDeConcepto());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celNumeroC = fi.createCell(numCel);
		    celNumeroC.setCellValue(conceptoImprimiendo.getTotalCasos());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celImp = fi.createCell(numCel);
		    celImp.setCellStyle(styleMoneda);
		    celImp.setCellValue(conceptoImprimiendo.getValor().doubleValue());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celSoporteDocumentalValor = fi.createCell(numCel);
		    celSoporteDocumentalValor.setCellValue(conceptoImprimiendo.getSoporteDocumental());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    Cell celFormulaOFormulacionValor = fi.createCell(numCel);
		    celFormulaOFormulacionValor.setCellValue(conceptoImprimiendo.getFormulaOFormulacion());
		    paginaReporte.autoSizeColumn(numCel);
		    numCel++;

		    /*
		     * System.out.println("             " + conceptoImprimiendo.getTipoConcepto() +
		     * " " + conceptoImprimiendo.getClave() + " " +
		     * conceptoImprimiendo.getPartidaAntecedente() + ", casos: " +
		     * conceptoImprimiendo.getTotalCasos() + ", importe: " +
		     * conceptoImprimiendo.getValorString());
		     */
		    fila++;

		}

	    }

	    System.out.println("Se obtuvieron " + conteoObtenciones + " de puestos");

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.dispose();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (

	Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionAcumuladoConDetalleDeProductosTXTCopia()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, (o1, o2) ->
	{
	    Producto reg1 = (Producto) o1;
	    Producto reg2 = (Producto) o2;

	    String q1 = "" + reg1.getQuincena();
	    String q2 = "" + reg2.getQuincena();

	    if (q1.length() == 1)
	    {
		q1 = "0" + q1;
	    }

	    if (q2.length() == 1)
	    {
		q2 = "0" + q2;
	    }

	    return ((Comparable<?>) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2015);

	// HashMap - clave XSSFSheet de la unidad correspondiente
	// - Valor otro HashMap

	// Valor del HashMap de Unidad
	// - Clave: Concepto
	// - Valor: List<Object>: Almacenará el número de fila de la hoja donde
	// irá el valor y el valor BigDecimal

	// Hojas, con filas y arreglo de columnas valor
	HashMap<XSSFSheet, List<List<Object>>> mapaHojasFilas = new HashMap<XSSFSheet, List<List<Object>>>();

	HashMap<XSSFSheet, List<String>> conceptosPorPagina = new HashMap<XSSFSheet, List<String>>();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clon;
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		{
		    prds.remove(prds.get(x));
		    x--;
		    continue;
		}

		if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		{
		    chequesCancelados.add(prds.get(x));
		    prds.remove(prds.get(x));
		    x--;

		}

	    }

	    XSSFWorkbook libroExcel = new XSSFWorkbook();
	    List<BigDecimal> totales;

	    int fila = 0;
	    int numCel = 0;

	    XSSFCellStyle style = libroExcel.createCellStyle();

	    XSSFFont font = libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);

	    boolean cancelado = false;

	    Concepto con;
	    List<Object> conceptoEncontrado;
	    List<Object> nuevoRegistro;
	    List<Object> baseRegs;

	    List<String> nombreConceptosPagina;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Producto: " + producto.getNombreProducto());
		clon = producto.getClone();
		clon.updateRegistrosTRA();

		for (UnidadProducto unid : clon.getUnidadResponsable())
		{

		    XSSFSheet pagina = libroExcel.getSheet(unid.getDescripcion());
		    List<List<Object>> filasConConceptos = null;

		    if (pagina == null)
		    {
			pagina = libroExcel.createSheet("" + unid.getDescripcion());

			filasConConceptos = new ArrayList<>();

			mapaHojasFilas.put(pagina, filasConConceptos);

			nombreConceptosPagina = new ArrayList<>();
			conceptosPorPagina.put(pagina, nombreConceptosPagina);

			// Se crean los encabezados de la página
			fila = 0;
			numCel = 0;

			XSSFRow filaEncabezado = pagina.createRow(fila);

			XSSFCell cAño = filaEncabezado.createCell(numCel);
			cAño.setCellValue("AÑO");
			cAño.setCellStyle(style);
			numCel++;

			XSSFCell cQna = filaEncabezado.createCell(numCel);
			cQna.setCellValue("QUINCENA");
			cQna.setCellStyle(style);
			numCel++;

			XSSFCell cProducto = filaEncabezado.createCell(numCel);
			cProducto.setCellValue("PRODUCTO");
			cProducto.setCellStyle(style);
			numCel++;

			XSSFCell cCheque = filaEncabezado.createCell(numCel);
			cCheque.setCellValue("CHEQUE");
			cCheque.setCellStyle(style);
			numCel++;

			XSSFCell c = filaEncabezado.createCell(numCel);
			c.setCellValue("RFC");
			c.setCellStyle(style);
			numCel++;

			XSSFCell cPaterno = filaEncabezado.createCell(numCel);
			cPaterno.setCellValue("PATERNO");
			cPaterno.setCellStyle(style);
			numCel++;

			XSSFCell cMaterno = filaEncabezado.createCell(numCel);
			cMaterno.setCellValue("MATERNO");
			cMaterno.setCellStyle(style);
			numCel++;

			XSSFCell cNombres = filaEncabezado.createCell(numCel);
			cNombres.setCellValue("NOMBRES");
			cNombres.setCellStyle(style);
			numCel++;

			XSSFCell cFIngreso = filaEncabezado.createCell(numCel);
			cFIngreso.setCellValue("FECHA DE INGRESO");
			cFIngreso.setCellStyle(style);
			numCel++;

			XSSFCell cDLaborados = filaEncabezado.createCell(numCel);
			cDLaborados.setCellValue("DIAS LABORADOS");
			cDLaborados.setCellStyle(style);
			numCel++;

			XSSFCell cTTrabajador = filaEncabezado.createCell(numCel);
			cTTrabajador.setCellValue("TIPO DE TRABAJADOR");
			cTTrabajador.setCellStyle(style);
			numCel++;

			XSSFCell cTMando = filaEncabezado.createCell(numCel);
			cTMando.setCellValue("TIPO DE MANDO");
			cTMando.setCellStyle(style);
			numCel++;

			XSSFCell cCMando = filaEncabezado.createCell(numCel);
			cCMando.setCellValue("CLAVE DE PAGO");
			cCMando.setCellStyle(style);
			numCel++;

			XSSFCell cUR = filaEncabezado.createCell(numCel);
			cUR.setCellValue("UNIDAD RESPONSABLE");
			cUR.setCellStyle(style);
			numCel++;

			XSSFCell cPlaza = filaEncabezado.createCell(numCel);
			cPlaza.setCellValue("PLAZA");
			cPlaza.setCellStyle(style);
			numCel++;

			XSSFCell cCR = filaEncabezado.createCell(numCel);
			cCR.setCellValue("C. RESPONSABILIDAD");
			cCR.setCellStyle(style);
			numCel++;

			XSSFCell cCD = filaEncabezado.createCell(numCel);
			cCD.setCellValue("NOMBRE C. RESPONSABILIDAD");
			cCD.setCellStyle(style);
			numCel++;

			XSSFCell cClues = filaEncabezado.createCell(numCel);
			cClues.setCellValue("CLUES");
			cClues.setCellStyle(style);
			numCel++;

			XSSFCell cFechaInicial = filaEncabezado.createCell(numCel);
			cFechaInicial.setCellValue("FECHA INICIAL");
			cFechaInicial.setCellStyle(style);
			numCel++;

			XSSFCell cFechaFinal = filaEncabezado.createCell(numCel);
			cFechaFinal.setCellValue("FECHA FINAL");
			cFechaFinal.setCellStyle(style);
			numCel++;

			XSSFCell cIP = filaEncabezado.createCell(numCel);
			cIP.setCellValue("INS.PAGO");
			cIP.setCellStyle(style);
			numCel++;

			XSSFCell cCURP = filaEncabezado.createCell(numCel);
			cCURP.setCellValue("CURP");
			cCURP.setCellStyle(style);
			numCel++;

			XSSFCell cPERCEP = filaEncabezado.createCell(numCel);
			cPERCEP.setCellValue("PERCEPCIONES");
			cPERCEP.setCellStyle(style);
			numCel++;

			XSSFCell cDEDUC = filaEncabezado.createCell(numCel);
			cDEDUC.setCellValue("DEDUCCIONES");
			cDEDUC.setCellStyle(style);
			numCel++;

			XSSFCell cNETO = filaEncabezado.createCell(numCel);
			cNETO.setCellValue("NETO");
			cNETO.setCellStyle(style);
			numCel++;

			fila++;
		    }
		    else
		    {
			fila = pagina.getLastRowNum();
			filasConConceptos = mapaHojasFilas.get(pagina);

			nombreConceptosPagina = conceptosPorPagina.get(pagina);

		    }

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			// Comparar con los cheques cancelados
			for (Producto chequeCan : chequesCancelados)
			{
			    clonCheq = chequeCan;

			    if (clonCheq.getRegistrosTRA() == null)
			    {
				clonCheq.updateRegistrosTRA();
			    }

			    for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
			    {

				for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				{
				    // Quincena de proceso o real
				    // Año de proceso o real
				    if (regDAT.getValorPorDescripcionContains("mero de emple")
					    .equals(regCheq.getValorPorDescripcionContains("mero de emple")))
				    {

					if (regDAT.getValorPorDescripcionContains("mero de cheque")
						.equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					{

					    if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
						    regCheq.getValorPorDescripcionContains("odo de pago inicia"))
						    && regDAT.getValorPorDescripcionContains("odo de pago fina").equals(
							    regCheq.getValorPorDescripcionContains("odo de pago fina")))
						    || (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
							    .equals(regCheq.getValorPorDescripcionContains(
								    "ena de Proceso o Re"))
							    && regDAT.getValorPorDescripcionContains("o de Proceso o R")
								    .equals(regCheq.getValorPorDescripcionContains(
									    "o de Proceso o R"))))
					    {
						unidCheq.getRegistrosDAT().remove(regCheq);
						cancelado = true;
						break;
					    }

					}

				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado)
			    {
				break;
			    }

			}

			if (cancelado)
			{

			    continue;
			}

			PlantillaRegistro trabajador = regDAT;
			String nombres = trabajador.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			XSSFRow filaTrab = pagina.createRow(fila);

			XSSFCell detcAño = filaTrab.createCell(numCel);
			detcAño.setCellValue(producto.getAño());
			numCel++;

			XSSFCell detcQna = filaTrab.createCell(numCel);
			detcQna.setCellValue(producto.getQuincenaString());
			numCel++;

			XSSFCell detcProducto = filaTrab.createCell(numCel);
			detcProducto.setCellValue(trabajador.getValorPorDescripcionContains("nombre del producto"));
			numCel++;

			XSSFCell detcCheque = filaTrab.createCell(numCel);
			detcCheque.setCellValue(trabajador.getValorPorDescripcionContains("mero de cheque"));
			numCel++;

			XSSFCell cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(trabajador.getValorEnCampo(2));

			numCel++;

			XSSFCell cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			XSSFCell cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			XSSFCell cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			XSSFCell cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(trabajador.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			XSSFCell cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue(trabajador.getValorPorDescripcionContains("as laborados"));
			numCel++;

			XSSFCell cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue(trabajador.getValorPorDescripcionContains("tipo de trab"));
			numCel++;

			XSSFCell cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(trabajador.getValorPorDescripcionContains("ndicador de mand"));
			numCel++;

			XSSFCell cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(trabajador.getValorPorDescripcionContains("unidad res")
				+ trabajador.getValorPorDescripcionContains("actividad inst")
				+ trabajador.getValorPorDescripcionContains("proyecto proc")
				+ trabajador.getValorPorDescripcionContains("Partida") + " "
				+ trabajador.getValorPorDescripcion("Puesto")
				+ trabajador.getValorPorDescripcionContains("grupo funcion")
				+ trabajador.getValorPorDescripcion("Función")
				+ trabajador.getValorPorDescripcionContains("subfunci"));

			numCel++;

			XSSFCell cURT = filaTrab.createCell(numCel);
			cURT.setCellValue(trabajador.getValorPorDescripcionContains("Unidad Respons"));
			numCel++;

			XSSFCell cPlazaT = filaTrab.createCell(numCel);

			if (unid.getDescripcion().equals("610"))
			{
			    cPlazaT.setCellValue("ENSEÑANZA");
			}
			else
			{
			    cPlazaT.setCellValue("BASE");
			}

			numCel++;

			XSSFCell cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(trabajador.getValorPorDescripcionContains("Centro"));
			numCel++;

			XSSFCell cCRD = filaTrab.createCell(numCel);
			cCRD.setCellValue(trabajador.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			XSSFCell cCCLUES = filaTrab.createCell(numCel);
			cCCLUES.setCellValue(trabajador.getValorPorDescripcionContains("icencia Sin Goce de S"));
			numCel++;

			XSSFCell cFechaInicialT = filaTrab.createCell(numCel);
			cFechaInicialT.setCellValue(trabajador.getValorPorDescripcionContains("do de Pago Inicial"));
			numCel++;

			XSSFCell cFechaFinalT = filaTrab.createCell(numCel);
			cFechaFinalT.setCellValue(trabajador.getValorPorDescripcionContains("do de Pago Final"));
			numCel++;

			XSSFCell cIPT = filaTrab.createCell(numCel);
			cIPT.setCellValue(trabajador.getValorPorDescripcionContains("nstrumento de Pago N"));
			numCel++;

			XSSFCell cCURPT = filaTrab.createCell(numCel);
			cCURPT.setCellValue(trabajador.getValorPorDescripcionContains("CURP"));
			numCel++;

			totales = trabajador.getPercepDeducNeto();

			XSSFCell cPERCEPT = filaTrab.createCell(numCel);
			cPERCEPT.setCellValue(utilidades.formato.format(totales.get(0)));
			numCel++;

			XSSFCell cDEDUCT = filaTrab.createCell(numCel);
			cDEDUCT.setCellValue(utilidades.formato.format(totales.get(1)));
			numCel++;

			XSSFCell cNETOT = filaTrab.createCell(numCel);
			cNETOT.setCellValue(utilidades.formato.format(totales.get(2)));
			numCel++;

			// Verifica si cada uno de los conceptos del registro se
			// encuentra en el mapa de conceptos de la página
			// en caso de no estarlo lo añade con su indice de fila
			// y valor a su arrayList, en caso de estar añade
			// también a la lista un registro más

			baseRegs = new ArrayList<>();
			filasConConceptos.add(baseRegs);

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    con = (Concepto) obj;

			    nuevoRegistro = new ArrayList<>();
			    nuevoRegistro.add(
				    "" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());
			    nuevoRegistro.add(con.getValor());

			    baseRegs.add(nuevoRegistro);

			    // Busca para acumular solo los conceptos de la
			    // página
			    boolean existeConcepto = nombreConceptosPagina.contains(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());

			    if (!existeConcepto)
			    {
				nombreConceptosPagina.add("" + con.getTipoConcepto() + con.getClave() + " "
					+ con.getPartidaAntecedente());
			    }

			}

			fila++;

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		prds.remove(x);
		x--;

	    }

	    Set setPaginas = conceptosPorPagina.entrySet();
	    Iterator iteratorPaginas = setPaginas.iterator();

	    int colBase = 24;

	    // Recorre unidades
	    while (iteratorPaginas.hasNext())
	    {
		Map.Entry<?, List<String>> me = (Map.Entry) iteratorPaginas.next();

		XSSFSheet paginaUnidad = (XSSFSheet) me.getKey();

		List<String> registros = (List<String>) me.getValue();

		List<String> ordenados = registros.subList(0, registros.size());

		Collections.sort(ordenados);

		// Se graban los conceptos ya ordenados
		me.setValue(ordenados);
		colBase = 24;

		for (String concepto : ordenados)
		{
		    colBase++;

		    XSSFCell celdaEncabezado = paginaUnidad.getRow(0).getCell(colBase);

		    if (celdaEncabezado == null)
		    {
			XSSFCell cel = paginaUnidad.getRow(0).createCell(colBase);
			cel.setCellValue(concepto);
			cel.setCellStyle(style);
		    }

		}

	    }

	    setPaginas = mapaHojasFilas.entrySet();
	    iteratorPaginas = setPaginas.iterator();

	    colBase = 25;

	    // Recorre unidades
	    while (iteratorPaginas.hasNext())
	    {
		Map.Entry me = (Map.Entry) iteratorPaginas.next();

		XSSFSheet paginaUnidad = (XSSFSheet) me.getKey();
		List<String> conceptosOrdenados = conceptosPorPagina.get(paginaUnidad);

		// Obtiene los registros de cada fila
		List<Object> filasExcel = (List<Object>) me.getValue();

		for (int x = 0; x < filasExcel.size(); x++)
		{
		    List<Object> filaExcel = (List<Object>) filasExcel.get(x);

		    // Se obtiene la hoja donde se tiene que buscar los
		    // conceptos ordenados

		    for (Object celda : filaExcel)
		    {
			List<Object> celdaExcel = (List<Object>) celda;

			// Se busca la posición de la celda de acuerdo a las
			// posiciones del arreglo de conceptos
			for (int pos = 0; pos < conceptosOrdenados.size(); pos++)
			{
			    String claveConcepto = conceptosOrdenados.get(pos);

			    if (claveConcepto.equals(celdaExcel.get(0)))
			    {
				XSSFCell cel = null;

				try
				{
				    cel = paginaUnidad.getRow((x + 1)).createCell(colBase + pos);
				    cel.setCellValue(utilidades.formato.format(celdaExcel.get(1)));

				}
				catch (Exception e)
				{
				    System.out.println(paginaUnidad);
				    System.out.println(x);
				    System.out.println(pos);
				    e.printStackTrace();
				}

				break;
			    }

			}

		    }

		}

	    }

	    colBase = 24;

	    XSSFSheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    XSSFRow ff = paginaProd.createRow(fila);

	    XSSFCell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    XSSFCell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto producto : prds)
	    {
		XSSFRow fi = paginaProd.createRow(fila);
		XSSFCell celp = fi.createCell(0);
		celp.setCellValue(
			producto.getAño() + "-" + producto.getQuincena() + " " + producto.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		XSSFRow fi = paginaProd.getRow(fila);
		XSSFCell celp = fi.createCell(1);
		celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " + cheq.getNombreProducto());
		fila++;

	    }

	    for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
	    {
		for (int c = 0; c < mapaHojasFilas.size() + 4 + 24; c++)
		{
		    libroExcel.getSheetAt(x).autoSizeColumn(c);
		}
	    }

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.close();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarTxt()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prdsSublist = this.productosSelec.subList(0, this.productosSelec.size());

	prdsSublist.sort((Producto o1, Producto o2) -> o2.getAñoQuincenaString().compareTo(o1.getAñoQuincenaString()));

	// Se obtiene una sublista de clones
	List<Producto> prds = new ArrayList<>();

	prdsSublist.forEach(item ->
	{
	    prds.add((Producto) item.clone());

	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2015);

	HashMap conAcum = new HashMap();
	HashMap conAcumCancelados = new HashMap();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clon;
	Producto clonCheq;
	BigDecimal percepComprob = new BigDecimal("0.00");
	BigDecimal deducComprob = new BigDecimal("0.00");
	BigDecimal netoComprob = new BigDecimal("0.00");

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		{
		    prds.remove(prds.get(x));
		    x--;
		    continue;
		}

		if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		{
		    if (this.cancelarCheques)
		    {
			System.out.println(
				"Preparando cheque cancelado para cancelación: " + prds.get(x).getDescripcion());
			chequesCancelados.add(prds.get(x));
		    }
		    prds.remove(prds.get(x));
		    x--;

		}

	    }

	    boolean cancelado = false;

	    for (Producto producto : prds)
	    {

		// System.out.println("Producto: " +
		// producto.getNombreProducto());
		clon = producto.getClone();
		clon.updateRegistrosTRA();

		for (UnidadProducto unid : clon.getUnidadResponsable())
		{

		    for (PlantillaRegistro regDAT : unid.getRegistrosDAT())
		    {
			cancelado = false;

			// Comparar con los cheques cancelados
			for (Producto chequeCan : chequesCancelados)
			{
			    clonCheq = chequeCan;

			    if (clonCheq.getRegistrosTRA() == null)
			    {
				clonCheq.updateRegistrosTRA();
			    }

			    for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
			    {

				for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				{
				    // Quincena de proceso o real
				    // Año de proceso o real
				    if (regDAT.getValorPorDescripcionContains("mero de emple")
					    .equals(regCheq.getValorPorDescripcionContains("mero de emple")))
				    {

					if (regDAT.getValorPorDescripcionContains("mero de cheque")
						.equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					{

					    if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
						    regCheq.getValorPorDescripcionContains("odo de pago inicia"))
						    && regDAT.getValorPorDescripcionContains("odo de pago fina").equals(
							    regCheq.getValorPorDescripcionContains("odo de pago fina")))
						    || (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
							    .equals(regCheq.getValorPorDescripcionContains(
								    "ena de Proceso o Re"))
							    && regDAT.getValorPorDescripcionContains("o de Proceso o R")
								    .equals(regCheq.getValorPorDescripcionContains(
									    "o de Proceso o R"))))
					    {
						/*
						 * if (regDAT. getValorPorDescripcionContains ("RFC")
						 * .contains("LOCR730402R6A")) { System.out .println( "Producto " +
						 * clon.getNombreProducto() + ", Cancelando cheque " + regDAT.
						 * getValorPorDescripcionContains( "mero de cheque") + "( " + regDAT.
						 * getValorPorDescripcionContains ("RFC") + " - " + regDAT.
						 * getValorPorDescripcionContains( "mero de emple") + " ) de: " +
						 * regDAT. getValorPorDescripcionContains( "Año de proceso o r") + " / "
						 * + regDAT. getValorPorDescripcionContains( "incena de proceso o re") +
						 * " importe " + regCheq. getValorPorDescripcionContains ("Neto"));
						 * 
						 * }
						 */
						System.out.println("Cancelado el cheque "
							+ regCheq.getValorPorDescripcionContains("mero de chequ"));
						unidCheq.getRegistrosDAT().remove(regCheq);
						cancelado = true;
						break;
					    }

					}

				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado)
			    {
				break;
			    }

			}

			if (cancelado)
			{

			    continue;
			}
			/*
			 * if (regDAT.getValorPorDescripcionContains("RFC"). contains("AFGI761018PS0"))
			 * { percepComprob = percepComprob .add(new
			 * BigDecimal(regDAT.getValorPorDescripcionContains( "Percep"))); deducComprob =
			 * deducComprob .add(new BigDecimal(regDAT.getValorPorDescripcionContains(
			 * "Deducc"))); netoComprob = netoComprob .add(new
			 * BigDecimal(regDAT.getValorPorDescripcionContains( "Neto")));
			 * 
			 * System.out.print("Producto " + clon.getNombreProducto() + " " +
			 * regDAT.getValorPorDescripcionContains( "mero de cheque") + " ");
			 * System.out.println(regDAT. getValorPorDescripcionContains("ombre del emp") +
			 * " - " + regDAT.getValorPorDescripcionContains("Percep") + " / " +
			 * regDAT.getValorPorDescripcionContains("Deduc") + " - " +
			 * regDAT.getValorPorDescripcionContains("Neto")); int a = 1; a = 23;
			 * 
			 * 
			 * }
			 */
			/*
			 * if (regDAT.getValorPorDescripcionContains( "ombre del emp") .contains(
			 * "ARCEO,ORTIZ/ALVARO EMILIO")) { System.out.println(regDAT.
			 * getValorPorDescripcionContains("ombre del emp") + " - " +
			 * regDAT.getValorPorDescripcionContains( "odo de Aplicación Inici") + " / " +
			 * regDAT.getValorPorDescripcionContains( "odo de Aplicación Fina") + " - " +
			 * regDAT.getValorPorDescripcionContains( "do de Pago Inicia") + " / " +
			 * regDAT.getValorPorDescripcionContains( "do de Pago Inicial")); int a = 1; a =
			 * 23; }
			 */

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    Concepto con = (Concepto) obj;

			    if (!conAcum.containsKey(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente()))
			    {
				conAcum.put(
					"" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente(),
					con);
			    }

			    /*
			     * if( regDAT.getValorPorDescripcionContains(
			     * "mero de emp").equals("0400004019") && con.getClave().contains("62") ) {
			     * String a = null;
			     * 
			     * }
			     */
			    /*
			     * if (regDAT.getValorPorDescripcionContains("RFC"). contains("AFGI761018PS0")
			     * && con.getClave().equals("62") && con.getTipoConcepto() == 1 ) {
			     * 
			     * System.out.println("Producto " + clon.getNombreProducto() + " " +
			     * regDAT.getValorPorDescripcionContains( "mero de cheque") +
			     * " importe "+con.getValor());
			     * 
			     * }
			     */

			    acum.addTrabajadorVerificandoNominaUnidad(clon.getPlaza().getIdPlaza(),
				    unid.getDescripcion(), regDAT, regDAT.getValorPorDescripcionContains("mero de emp"),
				    con, true);
			}

		    }

		}

	    }

	    System.out.println("Percepciones " + percepComprob);
	    System.out.println("Deducciones " + deducComprob);
	    System.out.println("Neto: " + netoComprob);

	    Workbook libroExcel = new HSSFWorkbook();
	    List<BigDecimal> totales;

	    int fila = 0;

	    Map<Integer, Concepto> map = new TreeMap<Integer, Concepto>(conAcum);

	    CellStyle style = libroExcel.createCellStyle();

	    Font font = libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);

	    List<String> ajustePeriodos = null;

	    // Recorre nóminas
	    for (Object objNominas : acum.getNomina().values())
	    {
		HashMap nomina = (HashMap) objNominas;

		Set set = nomina.entrySet();
		Iterator iterator = set.iterator();

		while (iterator.hasNext())
		{
		    Map.Entry me = (Map.Entry) iterator.next();

		    HashMap unidad = sortByValues((HashMap) me.getValue());

		    Sheet pagina = libroExcel.createSheet("" + me.getKey());
		    fila = 0;
		    int numCel = 0;

		    Row filaEncabezado = pagina.createRow(fila);
		    Cell c = filaEncabezado.createCell(numCel);
		    c.setCellValue("RFC");
		    c.setCellStyle(style);
		    numCel++;

		    Cell cPaterno = filaEncabezado.createCell(numCel);
		    cPaterno.setCellValue("PATERNO");
		    cPaterno.setCellStyle(style);
		    numCel++;

		    Cell cMaterno = filaEncabezado.createCell(numCel);
		    cMaterno.setCellValue("MATERNO");
		    cMaterno.setCellStyle(style);
		    numCel++;

		    Cell cNombres = filaEncabezado.createCell(numCel);
		    cNombres.setCellValue("NOMBRES");
		    cNombres.setCellStyle(style);
		    numCel++;

		    Cell cFIngreso = filaEncabezado.createCell(numCel);
		    cFIngreso.setCellValue("FECHA DE INGRESO");
		    cFIngreso.setCellStyle(style);
		    numCel++;

		    Cell cDLaborados = filaEncabezado.createCell(numCel);
		    cDLaborados.setCellValue("DIAS LABORADOS");
		    cDLaborados.setCellStyle(style);
		    numCel++;

		    Cell cTTrabajador = filaEncabezado.createCell(numCel);
		    cTTrabajador.setCellValue("TIPO DE TRABAJADOR");
		    cTTrabajador.setCellStyle(style);
		    numCel++;

		    Cell cTMando = filaEncabezado.createCell(numCel);
		    cTMando.setCellValue("TIPO DE MANDO");
		    cTMando.setCellStyle(style);
		    numCel++;

		    Cell cCMando = filaEncabezado.createCell(numCel);
		    cCMando.setCellValue("CLAVE DE PAGO");
		    cCMando.setCellStyle(style);
		    numCel++;

		    Cell cUR = filaEncabezado.createCell(numCel);
		    cUR.setCellValue("UNIDAD RESPONSABLE");
		    cUR.setCellStyle(style);
		    numCel++;

		    Cell cPlaza = filaEncabezado.createCell(numCel);
		    cPlaza.setCellValue("PLAZA");
		    cPlaza.setCellStyle(style);
		    numCel++;

		    Cell cCR = filaEncabezado.createCell(numCel);
		    cCR.setCellValue("C. RESPONSABILIDAD");
		    cCR.setCellStyle(style);
		    numCel++;

		    Cell cCD = filaEncabezado.createCell(numCel);
		    cCD.setCellValue("NOMBRE C. RESPONSABILIDAD");
		    cCD.setCellStyle(style);
		    numCel++;

		    Cell cClues = filaEncabezado.createCell(numCel);
		    cClues.setCellValue("CLUES");
		    cClues.setCellStyle(style);
		    numCel++;

		    Cell cFechaInicial = filaEncabezado.createCell(numCel);
		    cFechaInicial.setCellValue("FECHA INICIAL");
		    cFechaInicial.setCellStyle(style);
		    numCel++;

		    Cell cFechaFinal = filaEncabezado.createCell(numCel);
		    cFechaFinal.setCellValue("FECHA FINAL");
		    cFechaFinal.setCellStyle(style);
		    numCel++;

		    Cell cIP = filaEncabezado.createCell(numCel);
		    cIP.setCellValue("INS.PAGO");
		    cIP.setCellStyle(style);
		    numCel++;

		    Cell cCURP = filaEncabezado.createCell(numCel);
		    cCURP.setCellValue("CURP");
		    cCURP.setCellStyle(style);
		    numCel++;

		    Cell celda = null;
		    for (Object obj : map.values())
		    {
			Concepto con = (Concepto) obj;
			celda = filaEncabezado.createCell(numCel);
			celda.setCellValue(
				"" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());
			celda.setCellStyle(style);

			numCel++;
		    }

		    Cell cPERCEP = filaEncabezado.createCell(numCel);
		    cPERCEP.setCellValue("PERCEPCIONES");
		    c.setCellStyle(style);
		    numCel++;

		    Cell cDEDUC = filaEncabezado.createCell(numCel);
		    cDEDUC.setCellValue("DEDUCCIONES");
		    c.setCellStyle(style);
		    numCel++;

		    Cell cNETO = filaEncabezado.createCell(numCel);
		    cNETO.setCellValue("NETO");
		    c.setCellStyle(style);
		    numCel++;

		    fila++;

		    for (Object objTrab : unidad.values())
		    {

			PlantillaRegistro trabajador = (PlantillaRegistro) objTrab;
			String nombres = trabajador.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			Row filaTrab = pagina.createRow(fila);
			Cell cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(trabajador.getValorEnCampo(2));

			numCel++;

			Cell cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			Cell cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			Cell cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			Cell cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(trabajador.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			Cell cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue(trabajador.getValorPorDescripcionContains("as laborados"));
			numCel++;

			Cell cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue(trabajador.getValorPorDescripcionContains("tipo de trab"));
			numCel++;

			Cell cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(trabajador.getValorPorDescripcionContains("ndicador de mand"));
			numCel++;

			Cell cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(trabajador.getValorPorDescripcionContains("unidad res")
				+ trabajador.getValorPorDescripcionContains("actividad inst")
				+ trabajador.getValorPorDescripcionContains("proyecto proc")
				+ trabajador.getValorPorDescripcionContains("Partida") + " "
				+ trabajador.getValorPorDescripcion("Puesto")
				+ trabajador.getValorPorDescripcionContains("grupo funcion")
				+ trabajador.getValorPorDescripcion("Función")
				+ trabajador.getValorPorDescripcionContains("subfunci"));

			numCel++;

			Cell cURT = filaTrab.createCell(numCel);
			cURT.setCellValue(trabajador.getValorPorDescripcionContains("Unidad Respons"));
			numCel++;

			Cell cPlazaT = filaTrab.createCell(numCel);

			if (me.getKey().equals("610"))
			{
			    cPlazaT.setCellValue("ENSEÑANZA");
			}
			else
			{
			    cPlazaT.setCellValue("BASE");
			}
			numCel++;

			Cell cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(trabajador.getValorPorDescripcionContains("Centro"));
			numCel++;

			Cell cCRD = filaTrab.createCell(numCel);
			cCRD.setCellValue(trabajador.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			Cell cCCLUES = filaTrab.createCell(numCel);
			cCCLUES.setCellValue(trabajador.getValorPorDescripcionContains("icencia Sin Goce de S"));
			numCel++;

			ajustePeriodos = acum.ajustaPeriodosPago(trabajador);

			Cell cFechaInicialT = filaTrab.createCell(numCel);
			cFechaInicialT.setCellValue(ajustePeriodos.get(0));
			numCel++;

			Cell cFechaFinalT = filaTrab.createCell(numCel);
			cFechaFinalT.setCellValue(ajustePeriodos.get(1));
			numCel++;

			Cell cIPT = filaTrab.createCell(numCel);
			cIPT.setCellValue(trabajador.getValorPorDescripcionContains("nstrumento de Pago N"));
			numCel++;

			Cell cCURPT = filaTrab.createCell(numCel);
			cCURPT.setCellValue(trabajador.getValorPorDescripcionContains("CURP"));
			numCel++;

			for (Object obj : map.values())
			{
			    Concepto con = (Concepto) obj;

			    Cell celConcep = filaTrab.createCell(numCel);
			    Concepto cn = null;

			    if (trabajador.getConceptosAcum().containsKey(
				    "" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente()))
			    {

				cn = (Concepto) trabajador.getConceptosAcum().get("" + con.getTipoConcepto()
					+ con.getClave() + " " + con.getPartidaAntecedente());

				celConcep.setCellValue(cn.getValorString());
			    }

			    numCel++;

			}

			totales = trabajador.getPercepDeducNeto();

			Cell cPERCEPT = filaTrab.createCell(numCel);
			cPERCEPT.setCellValue(utilidades.formato.format(totales.get(0)));
			numCel++;

			Cell cDEDUCT = filaTrab.createCell(numCel);
			cDEDUCT.setCellValue(utilidades.formato.format(totales.get(1)));
			numCel++;

			Cell cNETOT = filaTrab.createCell(numCel);
			cNETOT.setCellValue(utilidades.formato.format(totales.get(2)));
			numCel++;

			fila++;

		    }

		}

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    int numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto producto : prds)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			producto.getAño() + "-" + producto.getQuincena() + " " + producto.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		/*
		 * Row fi = paginaProd.getRow(fila); Cell celp = fi.createCell(1);
		 * celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " +
		 * cheq.getNombreProducto()); fila++;
		 */

	    }

	    for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
	    {
		for (int c = 0; c < conAcum.size() + 4; c++)
		{
		    libroExcel.getSheetAt(x).autoSizeColumn(c);
		}
	    }

	    /*
	     * for( Object obj : conAcum.values() ) {
	     * 
	     * Row filaLinea = pagina1.createRow(fila); Cell celdaLinea =
	     * filaLinea.createCell(0); celdaLinea.setCellValue("");
	     * 
	     * numCel++;
	     * 
	     * }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.txt']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xls");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarFormatoLTAIPEC()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, (o1, o2) ->
	{
	    Producto reg1 = (Producto) o1;
	    Producto reg2 = (Producto) o2;

	    String q1 = "" + reg1.getQuincena();
	    String q2 = "" + reg2.getQuincena();

	    if (q1.length() == 1)
	    {
		q1 = "0" + q1;
	    }

	    if (q2.length() == 1)
	    {
		q2 = "0" + q2;
	    }

	    return ((Comparable<?>) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2015);

	HashMap conAcum = new HashMap();
	HashMap conAcumCancelados = new HashMap();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clon;
	Producto clonCheq;
	BigDecimal percepComprob = new BigDecimal("0.00");
	BigDecimal deducComprob = new BigDecimal("0.00");
	BigDecimal netoComprob = new BigDecimal("0.00");

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		{
		    prds.remove(prds.get(x));
		    x--;
		    continue;
		}

		if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		{
		    chequesCancelados.add(prds.get(x));
		    prds.remove(prds.get(x));
		    x--;

		}

	    }

	    boolean cancelado = false;

	    for (Producto producto : prds)
	    {

		// System.out.println("Producto: " +
		// producto.getNombreProducto());
		clon = producto.getClone();
		clon.updateRegistrosTRA();

		for (UnidadProducto unid : clon.getUnidadResponsable())
		{

		    for (PlantillaRegistro regDAT : unid.getRegistrosDAT())
		    {
			cancelado = false;

			// Comparar con los cheques cancelados
			for (Producto chequeCan : chequesCancelados)
			{
			    clonCheq = chequeCan;

			    if (clonCheq.getRegistrosTRA() == null)
			    {
				clonCheq.updateRegistrosTRA();
			    }

			    for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
			    {

				for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				{
				    // Quincena de proceso o real
				    // Año de proceso o real
				    if (regDAT.getValorPorDescripcionContains("mero de emple")
					    .equals(regCheq.getValorPorDescripcionContains("mero de emple")))
				    {

					if (regDAT.getValorPorDescripcionContains("mero de cheque")
						.equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					{

					    if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
						    regCheq.getValorPorDescripcionContains("odo de pago inicia"))
						    && regDAT.getValorPorDescripcionContains("odo de pago fina").equals(
							    regCheq.getValorPorDescripcionContains("odo de pago fina")))
						    || (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
							    .equals(regCheq.getValorPorDescripcionContains(
								    "ena de Proceso o Re"))
							    && regDAT.getValorPorDescripcionContains("o de Proceso o R")
								    .equals(regCheq.getValorPorDescripcionContains(
									    "o de Proceso o R"))))
					    {
						/*
						 * if (regDAT. getValorPorDescripcionContains ("RFC")
						 * .contains("LOCR730402R6A")) { System.out .println( "Producto " +
						 * clon.getNombreProducto() + ", Cancelando cheque " + regDAT.
						 * getValorPorDescripcionContains( "mero de cheque") + "( " + regDAT.
						 * getValorPorDescripcionContains ("RFC") + " - " + regDAT.
						 * getValorPorDescripcionContains( "mero de emple") + " ) de: " +
						 * regDAT. getValorPorDescripcionContains( "Año de proceso o r") + " / "
						 * + regDAT. getValorPorDescripcionContains( "incena de proceso o re") +
						 * " importe " + regCheq. getValorPorDescripcionContains ("Neto"));
						 * 
						 * }
						 */
						System.out.println("Cancelado el cheque "
							+ regCheq.getValorPorDescripcionContains("mero de chequ"));
						unidCheq.getRegistrosDAT().remove(regCheq);
						cancelado = true;
						break;
					    }

					}

				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado)
			    {
				break;
			    }

			}

			if (cancelado)
			{

			    continue;
			}
			/*
			 * if (regDAT.getValorPorDescripcionContains("RFC"). contains("AFGI761018PS0"))
			 * { percepComprob = percepComprob .add(new
			 * BigDecimal(regDAT.getValorPorDescripcionContains( "Percep"))); deducComprob =
			 * deducComprob .add(new BigDecimal(regDAT.getValorPorDescripcionContains(
			 * "Deducc"))); netoComprob = netoComprob .add(new
			 * BigDecimal(regDAT.getValorPorDescripcionContains( "Neto")));
			 * 
			 * System.out.print("Producto " + clon.getNombreProducto() + " " +
			 * regDAT.getValorPorDescripcionContains( "mero de cheque") + " ");
			 * System.out.println(regDAT. getValorPorDescripcionContains("ombre del emp") +
			 * " - " + regDAT.getValorPorDescripcionContains("Percep") + " / " +
			 * regDAT.getValorPorDescripcionContains("Deduc") + " - " +
			 * regDAT.getValorPorDescripcionContains("Neto")); int a = 1; a = 23;
			 * 
			 * 
			 * }
			 */
			/*
			 * if (regDAT.getValorPorDescripcionContains( "ombre del emp") .contains(
			 * "ARCEO,ORTIZ/ALVARO EMILIO")) { System.out.println(regDAT.
			 * getValorPorDescripcionContains("ombre del emp") + " - " +
			 * regDAT.getValorPorDescripcionContains( "odo de Aplicación Inici") + " / " +
			 * regDAT.getValorPorDescripcionContains( "odo de Aplicación Fina") + " - " +
			 * regDAT.getValorPorDescripcionContains( "do de Pago Inicia") + " / " +
			 * regDAT.getValorPorDescripcionContains( "do de Pago Inicial")); int a = 1; a =
			 * 23; }
			 */

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    Concepto con = (Concepto) obj;

			    if (!conAcum.containsKey(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente()))
			    {
				conAcum.put(
					"" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente(),
					con);
			    }

			    /*
			     * if( regDAT.getValorPorDescripcionContains(
			     * "mero de emp").equals("0400004019") && con.getClave().contains("62") ) {
			     * String a = null;
			     * 
			     * }
			     */
			    /*
			     * if (regDAT.getValorPorDescripcionContains("RFC"). contains("AFGI761018PS0")
			     * && con.getClave().equals("62") && con.getTipoConcepto() == 1 ) {
			     * 
			     * System.out.println("Producto " + clon.getNombreProducto() + " " +
			     * regDAT.getValorPorDescripcionContains( "mero de cheque") +
			     * " importe "+con.getValor());
			     * 
			     * }
			     */

			    acum.addTrabajadorVerificandoNominaUnidad(clon.getPlaza().getIdPlaza(),
				    unid.getDescripcion(), regDAT, regDAT.getValorPorDescripcionContains("mero de emp"),
				    con, true);
			}

		    }

		}

	    }

	    System.out.println("Percepciones " + percepComprob);
	    System.out.println("Deducciones " + deducComprob);
	    System.out.println("Neto: " + netoComprob);

	    HSSFWorkbook libroExcel = new HSSFWorkbook();
	    List<BigDecimal> totales;

	    int fila = 0;

	    Map<Integer, Concepto> map = new TreeMap<Integer, Concepto>(conAcum);

	    CellStyle style = libroExcel.createCellStyle();

	    HSSFCellStyle styleMoneda = (HSSFCellStyle) libroExcel.createCellStyle();
	    styleMoneda.setDataFormat((short) 7);

	    Font font = libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);

	    Sheet pagina = libroExcel.createSheet("Reporte de Formatos");
	    fila = 0;
	    int numCel = 0;

	    Row filaEncabezado = pagina.createRow(fila);
	    Cell c = filaEncabezado.createCell(numCel);
	    c.setCellValue("TITULO");
	    c.setCellStyle(style);
	    numCel++;

	    Cell cNombreCorto = filaEncabezado.createCell(numCel);
	    cNombreCorto.setCellValue("NOMBRE CORTO");
	    cNombreCorto.setCellStyle(style);
	    numCel++;

	    Cell cDescripcion = filaEncabezado.createCell(numCel);
	    cDescripcion.setCellValue("DESCRIPCION");
	    cDescripcion.setCellStyle(style);
	    numCel++;

	    fila++;
	    numCel = 0;

	    Row filaEncabezado1 = pagina.createRow(fila);
	    Cell c1 = filaEncabezado1.createCell(numCel);
	    c1.setCellValue("F17_LTAIPEC_Art_74_Fr_XVII");
	    c1.setCellStyle(style);
	    numCel++;

	    Cell c2 = filaEncabezado1.createCell(numCel);
	    c2.setCellValue("F17_LTAIPEC_Art_74_Fr_XVI");
	    c2.setCellStyle(style);
	    numCel++;

	    Cell c3 = filaEncabezado1.createCell(numCel);
	    c3.setCellValue(
		    "Información curricular y las sanciones administrativas definitivas de los(as) servidores(as) públicas(os) y/o personas que desempeñen un empleo, cargo o comisión en un sujeto obligado");
	    c3.setCellStyle(style);
	    numCel++;

	    fila += 2;
	    numCel = 0;

	    Row filaEncabezado2 = pagina.createRow(fila);
	    Cell cE1 = filaEncabezado2.createCell(numCel);
	    cE1.setCellValue("Nomina");
	    cE1.setCellStyle(style);
	    numCel++;

	    Cell cE2 = filaEncabezado2.createCell(numCel);
	    cE2.setCellValue("Clave Presupuestal");
	    cE2.setCellStyle(style);
	    numCel++;

	    Cell cE3 = filaEncabezado2.createCell(numCel);
	    cE3.setCellValue("Ejercicio");
	    cE3.setCellStyle(style);
	    numCel++;

	    Cell cE4 = filaEncabezado2.createCell(numCel);
	    cE4.setCellValue("Periodo que se informa");
	    cE4.setCellStyle(style);
	    numCel++;

	    Cell cE41 = filaEncabezado2.createCell(numCel);
	    cE41.setCellValue("Tipo de Integrante del Sujeto obligado");
	    cE41.setCellStyle(style);
	    numCel++;

	    Cell cE5 = filaEncabezado2.createCell(numCel);
	    cE5.setCellValue("Clave o nivel del puesto");
	    cE5.setCellStyle(style);
	    numCel++;
	    Cell cE6 = filaEncabezado2.createCell(numCel);
	    cE6.setCellValue("Denominación o descripción del puesto");
	    cE6.setCellStyle(style);
	    numCel++;

	    Cell cE7 = filaEncabezado2.createCell(numCel);
	    cE7.setCellValue("Denominación del cargo");
	    cE7.setCellStyle(style);
	    numCel++;

	    Cell cE8 = filaEncabezado2.createCell(numCel);
	    cE8.setCellValue("Área de adscripción");
	    cE8.setCellStyle(style);
	    numCel++;

	    Cell cE9 = filaEncabezado2.createCell(numCel);
	    cE9.setCellValue("Nombre (s) del(a) servidor(a) público(a)");
	    cE9.setCellStyle(style);
	    numCel++;

	    Cell cE10 = filaEncabezado2.createCell(numCel);
	    cE10.setCellValue("Primer apellido del(a) servidor(a) público(a)");
	    cE10.setCellStyle(style);
	    numCel++;

	    Cell cE11 = filaEncabezado2.createCell(numCel);
	    cE11.setCellValue("Segundo apellido del(a) servidor(a) público(a)");
	    cE11.setCellStyle(style);
	    numCel++;

	    Cell cE12 = filaEncabezado2.createCell(numCel);
	    cE12.setCellValue("Sexo (femenino/Masculino)");
	    cE12.setCellStyle(style);
	    numCel++;

	    Cell cE13 = filaEncabezado2.createCell(numCel);
	    cE13.setCellValue("Remuneración mensual bruta");
	    cE13.setCellStyle(style);
	    numCel++;

	    Cell cE14 = filaEncabezado2.createCell(numCel);
	    cE14.setCellValue("Remuneración mensual neta");
	    cE14.setCellStyle(style);
	    numCel++;

	    Cell cE15 = filaEncabezado2.createCell(numCel);
	    cE15.setCellValue("Percepciones en efectivo");
	    cE15.setCellStyle(style);
	    numCel++;

	    Cell cE16 = filaEncabezado2.createCell(numCel);
	    cE16.setCellValue("Percepciones adicionales en especie");
	    cE16.setCellStyle(style);
	    numCel++;

	    Cell cE17 = filaEncabezado2.createCell(numCel);
	    cE17.setCellValue("Ingresos");
	    cE17.setCellStyle(style);
	    numCel++;

	    Cell cE18 = filaEncabezado2.createCell(numCel);
	    cE18.setCellValue("Sistemas de compensación");
	    cE18.setCellStyle(style);
	    numCel++;

	    Cell cE19 = filaEncabezado2.createCell(numCel);
	    cE19.setCellValue("Gratificaciones");
	    cE19.setCellStyle(style);
	    numCel++;

	    Cell cE20 = filaEncabezado2.createCell(numCel);
	    cE20.setCellValue("Primas");
	    cE20.setCellStyle(style);
	    numCel++;

	    Cell cE21 = filaEncabezado2.createCell(numCel);
	    cE21.setCellValue("Comisiones");
	    cE21.setCellStyle(style);
	    numCel++;

	    Cell cE22 = filaEncabezado2.createCell(numCel);
	    cE22.setCellValue("Dietas");
	    cE22.setCellStyle(style);
	    numCel++;

	    Cell cE23 = filaEncabezado2.createCell(numCel);
	    cE23.setCellValue("Bonos");
	    cE23.setCellStyle(style);
	    numCel++;

	    Cell cE24 = filaEncabezado2.createCell(numCel);
	    cE24.setCellValue("Estímulos");
	    cE24.setCellStyle(style);
	    numCel++;

	    Cell cE25 = filaEncabezado2.createCell(numCel);
	    cE25.setCellValue("Apoyos económicos");
	    cE25.setCellStyle(style);
	    numCel++;

	    Cell cE26 = filaEncabezado2.createCell(numCel);
	    cE26.setCellValue("Prestaciones económicas");
	    cE26.setCellStyle(style);
	    numCel++;

	    Cell cE27 = filaEncabezado2.createCell(numCel);
	    cE27.setCellValue("Prestaciones en especie");
	    cE27.setCellStyle(style);
	    numCel++;

	    Cell cE28 = filaEncabezado2.createCell(numCel);
	    cE28.setCellValue("Otro tipo de percepción");
	    cE28.setCellStyle(style);
	    numCel++;

	    Cell cE29 = filaEncabezado2.createCell(numCel);
	    cE29.setCellValue("Fecha de validación");
	    cE29.setCellStyle(style);
	    numCel++;

	    Cell cE30 = filaEncabezado2.createCell(numCel);
	    cE30.setCellValue("Área responsable de la información");
	    cE30.setCellStyle(style);
	    numCel++;

	    Cell cE31 = filaEncabezado2.createCell(numCel);
	    cE31.setCellValue("Año");
	    cE31.setCellStyle(style);
	    numCel++;

	    Cell cE32 = filaEncabezado2.createCell(numCel);
	    cE32.setCellValue("Fecha de actualización");
	    cE32.setCellStyle(style);
	    numCel++;

	    Cell cE33 = filaEncabezado2.createCell(numCel);
	    cE33.setCellValue("Nota");
	    cE33.setCellStyle(style);
	    numCel++;

	    fila++;
	    numCel = 0;

	    List<String> ajustePeriodos = null;

	    Set setPrincipal = acum.getNomina().entrySet();
	    Iterator iteratorPrincipal = setPrincipal.iterator();

	    CatalogosBean catsBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");

	    ArchivoPuesto archivoCatPuesto = null;
	    PlantillaRegistro regPuesto = null;

	    Map<String, ArchivoPuesto> mapaCatalogoPuestos = new HashMap<String, ArchivoPuesto>();
	    // Se obtiene la descripción del puesto según el
	    // catálogo de puestos de la quincena
	    String key = "" + 0 + "-" + 2017 + "-08";
	    List<ArchivoPuesto> catPuestos = utilidades.getArchivosPuesto(2017, 8, 2017, 8, 0);

	    ArchivoPuesto archivoPuestos = catPuestos.get(0);
	    archivoPuestos.updatePlantillaRegistros();
	    archivoPuestos.updateRegistros();

	    // Recorre nóminas
	    while (iteratorPrincipal.hasNext())
	    {
		Map.Entry entryPrincipal = (Map.Entry) iteratorPrincipal.next();

		HashMap nomina = (HashMap) entryPrincipal.getValue();
		int claveNomina = (int) entryPrincipal.getKey();
		Plaza plaza = catsBean.getPlaza(claveNomina);

		Set set = nomina.entrySet();
		Iterator iterator = set.iterator();

		while (iterator.hasNext())
		{
		    Map.Entry me = (Map.Entry) iterator.next();

		    HashMap unidad = sortByValues((HashMap) me.getValue());
		    String uni = (String) me.getKey();

		    if (uni.equals("610"))
		    {
			continue;
		    }

		    for (Object objTrab : unidad.values())
		    {

			PlantillaRegistro trabajador = (PlantillaRegistro) objTrab;
			String nombres = trabajador.getValorPorDescripcionContains("Nombre del Empl");
			String CURP = trabajador.getValorPorDescripcionContains("CURP");

			totales = trabajador.getPercepDeducNeto();

			Row filaTrab = pagina.createRow(fila);

			Cell cNomina = filaTrab.createCell(numCel);
			cNomina.setCellValue(plaza.getDescripcionPlaza().toUpperCase());
			cNomina.setCellStyle(style);
			numCel++;

			Cell cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(trabajador.getValorPorDescripcionContains("unidad res")
				+ trabajador.getValorPorDescripcionContains("actividad inst")
				+ trabajador.getValorPorDescripcionContains("proyecto proc")
				+ trabajador.getValorPorDescripcionContains("Partida") + " "
				+ trabajador.getValorPorDescripcion("Puesto")
				+ trabajador.getValorPorDescripcionContains("grupo funcion")
				+ trabajador.getValorPorDescripcion("Función")
				+ trabajador.getValorPorDescripcionContains("subfunci"));

			numCel += 3;

			String clavePuesto = trabajador.getValorPorDescripcionContains("Puesto");

			Cell cTipoIntegrante = filaTrab.createCell(numCel);

			if (clavePuesto.contains("CF"))
			{
			    if (claveNomina == 0 || claveNomina == 2 || claveNomina == 5)
			    {
				cTipoIntegrante.setCellValue("CONFIANZA");
			    }
			    else
			    {
				cTipoIntegrante.setCellValue(plaza.getDescripcionPlaza().toUpperCase());
			    }
			}
			else
			{
			    if (claveNomina == 0 || claveNomina == 2 || claveNomina == 5)
			    {
				cTipoIntegrante.setCellValue("BASE");
			    }
			    else
			    {
				cTipoIntegrante.setCellValue(plaza.getDescripcionPlaza().toUpperCase());
			    }
			}

			numCel++;

			Cell cClaveONivel = filaTrab.createCell(numCel);
			cClaveONivel.setCellValue(clavePuesto);

			numCel++;

			if (claveNomina == 4)
			{
			    Cell cClaveONivelDescripcion = filaTrab.createCell(numCel);

			    cClaveONivelDescripcion.setCellValue(trabajador.getValorPorDescripcionContains("Delegac"));

			    numCel++;

			    Cell cClaveONivelDescripcion1 = filaTrab.createCell(numCel);
			    cClaveONivelDescripcion1.setCellValue(trabajador.getValorPorDescripcionContains("Delegac"));

			    numCel++;

			}
			else
			{
			    PlantillaRegistro rPuesto = archivoPuestos
				    .getRegistro(trabajador.getValorPorDescripcion("Puesto").trim());

			    Cell cClaveONivelDescripcion = filaTrab.createCell(numCel);
			    cClaveONivelDescripcion
				    .setCellValue(rPuesto.getValorPorDescripcionContains("Nombre del puest"));

			    numCel++;

			    Cell cClaveONivelDescripcion1 = filaTrab.createCell(numCel);
			    cClaveONivelDescripcion1
				    .setCellValue(rPuesto.getValorPorDescripcionContains("Nombre del puest"));

			    numCel++;

			}

			Cell cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(trabajador.getValorPorDescripcionContains("n del tipo de responsabili"));
			numCel++;

			Cell cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			Cell cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			Cell cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			Cell cSexo = filaTrab.createCell(numCel);
			cSexo.setCellValue(CURP.substring(10, 11));
			numCel++;

			Cell cPERCEPTBruta = filaTrab.createCell(numCel);
			cPERCEPTBruta.setCellStyle(styleMoneda);
			cPERCEPTBruta.setCellValue(utilidades.formato.format(totales.get(0)));
			numCel++;

			Cell cNETa = filaTrab.createCell(numCel);
			cNETa.setCellStyle(styleMoneda);
			cNETa.setCellValue(utilidades.formato.format(totales.get(2)));
			numCel++;

			numCel += 5;

			BigDecimal sumaPrima = new BigDecimal("0.00");

			Concepto conPrimaDominical = (Concepto) trabajador.getConceptosAcum().get("132" + " PV");
			Concepto conPrimaDominical2 = (Concepto) trabajador.getConceptosAcum().get("132 VD");
			Concepto conPrimaVacacional = (Concepto) trabajador.getConceptosAcum().get("132 PD");

			if (conPrimaDominical != null)
			{
			    sumaPrima = sumaPrima.add(conPrimaDominical.getValor());
			}

			if (conPrimaDominical2 != null)
			{
			    sumaPrima = sumaPrima.add(conPrimaDominical2.getValor());
			}

			if (conPrimaVacacional != null)
			{
			    sumaPrima = sumaPrima.add(conPrimaVacacional.getValor());
			}

			Cell cPrimas = filaTrab.createCell(numCel);
			cPrimas.setCellStyle(styleMoneda);
			cPrimas.setCellValue(utilidades.formato.format(sumaPrima));
			numCel++;

			numCel += 3;

			BigDecimal sumaEst = new BigDecimal("0.00");

			Concepto conDM = (Concepto) trabajador.getConceptosAcum().get("173" + " DM");;
			Concepto conDR = (Concepto) trabajador.getConceptosAcum().get("173" + " DR");
			Concepto DT = (Concepto) trabajador.getConceptosAcum().get("159" + " DT");
			Concepto anual = (Concepto) trabajador.getConceptosAcum().get("168" + " EA");
			Concepto asisP = (Concepto) trabajador.getConceptosAcum().get("175" + " AP");
			Concepto trim = (Concepto) trabajador.getConceptosAcum().get("169" + " TR");

			if (conDM != null)
			{
			    sumaEst = sumaEst.add(conDM.getValor());
			}

			if (conDR != null)
			{
			    sumaEst = sumaEst.add(conDR.getValor());
			}

			if (DT != null)
			{
			    sumaEst = sumaEst.add(DT.getValor());
			}

			if (anual != null)
			{
			    sumaEst = sumaEst.add(anual.getValor());
			}

			if (asisP != null)
			{
			    sumaEst = sumaEst.add(asisP.getValor());
			}

			if (trim != null)
			{
			    sumaEst = sumaEst.add(trim.getValor());
			}

			Cell cEstimulos = filaTrab.createCell(numCel);
			cEstimulos.setCellStyle(styleMoneda);
			cEstimulos.setCellValue(utilidades.formato.format(sumaEst));
			numCel++;

			/*
			 * for (Object obj : map.values()) { Concepto con = (Concepto) obj;
			 * 
			 * Cell celConcep = filaTrab.createCell(numCel); Concepto cn = null;
			 * 
			 * if (trabajador.getConceptosAcum().containsKey( "" + con.getTipoConcepto() +
			 * con.getClave() + " " + con.getPartidaAntecedente())) {
			 * 
			 * cn = (Concepto) trabajador.getConceptosAcum().get("" + con.getTipoConcepto()
			 * + con.getClave() + " " + con.getPartidaAntecedente());
			 * 
			 * celConcep.setCellValue(cn.getValorString()); }
			 * 
			 * numCel++;
			 * 
			 * }
			 * 
			 * totales = trabajador.getPercepDeducNeto();
			 * 
			 * Cell cPERCEPT = filaTrab.createCell(numCel);
			 * cPERCEPT.setCellValue(utilidades.formato.format( totales.get(0))); numCel++;
			 * 
			 * Cell cDEDUCT = filaTrab.createCell(numCel);
			 * cDEDUCT.setCellValue(utilidades.formato.format( totales.get(1))); numCel++;
			 * 
			 * Cell cNETOT = filaTrab.createCell(numCel);
			 * cNETOT.setCellValue(utilidades.formato.format(totales .get(2))); numCel++;
			 */

			fila++;
			numCel = 0;

		    }

		}

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto producto : prds)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			producto.getAño() + "-" + producto.getQuincena() + " " + producto.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		Row fi = paginaProd.getRow(fila);
		Cell celp = fi.createCell(1);
		celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " + cheq.getNombreProducto());
		fila++;

	    }

	    for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
	    {
		for (int celX = 0; celX < conAcum.size() + 4; celX++)
		{

		    if (celX == 2)
		    {
			continue;
		    }

		    libroExcel.getSheetAt(x).autoSizeColumn(celX);
		}
	    }

	    /*
	     * for( Object obj : conAcum.values() ) {
	     * 
	     * Row filaLinea = pagina1.createRow(fila); Cell celdaLinea =
	     * filaLinea.createCell(0); celdaLinea.setCellValue("");
	     * 
	     * numCel++;
	     * 
	     * }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.txt']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xls");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarPerDed()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, new Comparator()
	{

	    public int compare(Object o1, Object o2)
	    {
		Producto reg1 = (Producto) o1;
		Producto reg2 = (Producto) o2;

		String q1 = "" + reg1.getQuincena();
		String q2 = "" + reg2.getQuincena();

		if (q1.length() == 1)
		{
		    q1 = "0" + q1;
		}

		if (q2.length() == 1)
		{
		    q2 = "0" + q2;
		}

		return ((Comparable) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	    }
	});

	// Se recorren los productos seleccionados

	Acumulado acum = new Acumulado(2016);

	HashMap conAcum = new HashMap();
	HashMap conAcumCancelados = new HashMap();

	List<Producto> chequesCancelados = new ArrayList<>();
	Producto clon;
	Producto clonCheq;
	BigDecimal percepComprob = new BigDecimal("0.00");
	BigDecimal deducComprob = new BigDecimal("0.00");
	BigDecimal netoComprob = new BigDecimal("0.00");

	try
	{

	    boolean cancelado = false;

	    for (Producto producto : prds)
	    {

		System.out.println("Producto: " + producto.getNombreProducto());
		clon = producto.getClone();
		clon.updateRegistrosTRA();

		for (UnidadProducto unid : clon.getUnidadResponsable())
		{

		    for (PlantillaRegistro regDAT : unid.getRegistrosDAT())
		    {
			cancelado = false;

			for (Object obj : regDAT.getConceptosAcum().values())
			{
			    Concepto con = (Concepto) obj;

			    if (!conAcum.containsKey(
				    con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente()))
			    {
				conAcum.put(
					"" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente(),
					con);
			    }

			    acum.addTrabajadorVerificandoNominaUnidad(clon.getPlaza().getIdPlaza(),
				    unid.getDescripcion(), regDAT, regDAT.getValorPorDescripcionContains("mero de emp"),
				    con, true);
			}

		    }

		}

	    }

	    System.out.println("Percepciones " + percepComprob);
	    System.out.println("Deducciones " + deducComprob);
	    System.out.println("Neto: " + netoComprob);

	    Workbook libroExcel = new HSSFWorkbook();
	    List<BigDecimal> totales;

	    int fila = 0;

	    Map<Integer, Concepto> map = new TreeMap<Integer, Concepto>(conAcum);

	    CellStyle style = libroExcel.createCellStyle();

	    Font font = libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	    style.setFont(font);

	    List<String> ajustePeriodos = null;

	    // Recorre nóminas
	    for (Object objNominas : acum.getNomina().values())
	    {
		HashMap nomina = (HashMap) objNominas;

		Set set = nomina.entrySet();
		Iterator iterator = set.iterator();

		while (iterator.hasNext())
		{
		    Map.Entry me = (Map.Entry) iterator.next();

		    HashMap unidad = sortByValues((HashMap) me.getValue());

		    Sheet pagina = libroExcel.createSheet("" + me.getKey());
		    fila = 0;
		    int numCel = 0;

		    Row filaEncabezado = pagina.createRow(fila);
		    Cell c = filaEncabezado.createCell(numCel);
		    c.setCellValue("RFC");
		    c.setCellStyle(style);
		    numCel++;

		    Cell cPaterno = filaEncabezado.createCell(numCel);
		    cPaterno.setCellValue("PATERNO");
		    cPaterno.setCellStyle(style);
		    numCel++;

		    Cell cMaterno = filaEncabezado.createCell(numCel);
		    cMaterno.setCellValue("MATERNO");
		    cMaterno.setCellStyle(style);
		    numCel++;

		    Cell cNombres = filaEncabezado.createCell(numCel);
		    cNombres.setCellValue("NOMBRES");
		    cNombres.setCellStyle(style);
		    numCel++;

		    Cell cFIngreso = filaEncabezado.createCell(numCel);
		    cFIngreso.setCellValue("FECHA DE INGRESO");
		    cFIngreso.setCellStyle(style);
		    numCel++;

		    Cell cDLaborados = filaEncabezado.createCell(numCel);
		    cDLaborados.setCellValue("DIAS LABORADOS");
		    cDLaborados.setCellStyle(style);
		    numCel++;

		    Cell cTTrabajador = filaEncabezado.createCell(numCel);
		    cTTrabajador.setCellValue("TIPO DE TRABAJADOR");
		    cTTrabajador.setCellStyle(style);
		    numCel++;

		    Cell cTMando = filaEncabezado.createCell(numCel);
		    cTMando.setCellValue("TIPO DE MANDO");
		    cTMando.setCellStyle(style);
		    numCel++;

		    Cell cCMando = filaEncabezado.createCell(numCel);
		    cCMando.setCellValue("CLAVE DE PAGO");
		    cCMando.setCellStyle(style);
		    numCel++;

		    Cell cUR = filaEncabezado.createCell(numCel);
		    cUR.setCellValue("UNIDAD RESPONSABLE");
		    cUR.setCellStyle(style);
		    numCel++;

		    Cell cPlaza = filaEncabezado.createCell(numCel);
		    cPlaza.setCellValue("PLAZA");
		    cPlaza.setCellStyle(style);
		    numCel++;

		    Cell cCR = filaEncabezado.createCell(numCel);
		    cCR.setCellValue("C. RESPONSABILIDAD");
		    cCR.setCellStyle(style);
		    numCel++;

		    Cell cCD = filaEncabezado.createCell(numCel);
		    cCD.setCellValue("NOMBRE C. RESPONSABILIDAD");
		    cCD.setCellStyle(style);
		    numCel++;

		    Cell cClues = filaEncabezado.createCell(numCel);
		    cClues.setCellValue("CLUES");
		    cClues.setCellStyle(style);
		    numCel++;

		    Cell cFechaInicial = filaEncabezado.createCell(numCel);
		    cFechaInicial.setCellValue("FECHA INICIAL");
		    cFechaInicial.setCellStyle(style);
		    numCel++;

		    Cell cFechaFinal = filaEncabezado.createCell(numCel);
		    cFechaFinal.setCellValue("FECHA FINAL");
		    cFechaFinal.setCellStyle(style);
		    numCel++;

		    Cell cIP = filaEncabezado.createCell(numCel);
		    cIP.setCellValue("INS.PAGO");
		    cIP.setCellStyle(style);
		    numCel++;

		    Cell cCURP = filaEncabezado.createCell(numCel);
		    cCURP.setCellValue("CURP");
		    cCURP.setCellStyle(style);
		    numCel++;

		    Cell celda = null;
		    for (Object obj : map.values())
		    {
			Concepto con = (Concepto) obj;
			celda = filaEncabezado.createCell(numCel);
			celda.setCellValue(
				"" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente());
			celda.setCellStyle(style);

			numCel++;
		    }

		    Cell cPERCEP = filaEncabezado.createCell(numCel);
		    cPERCEP.setCellValue("PERCEPCIONES");
		    c.setCellStyle(style);
		    numCel++;

		    Cell cDEDUC = filaEncabezado.createCell(numCel);
		    cDEDUC.setCellValue("DEDUCCIONES");
		    c.setCellStyle(style);
		    numCel++;

		    Cell cNETO = filaEncabezado.createCell(numCel);
		    cNETO.setCellValue("NETO");
		    c.setCellStyle(style);
		    numCel++;

		    fila++;

		    for (Object objTrab : unidad.values())
		    {

			PlantillaRegistro trabajador = (PlantillaRegistro) objTrab;
			String nombres = trabajador.getValorPorDescripcionContains("Nombre del Empl");

			numCel = 0;

			Row filaTrab = pagina.createRow(fila);
			Cell cTrab = filaTrab.createCell(numCel);
			cTrab.setCellValue(trabajador.getValorEnCampo(2));

			numCel++;

			Cell cPaternoT = filaTrab.createCell(numCel);
			cPaternoT.setCellValue(nombres.substring(0, nombres.indexOf(",")));
			numCel++;

			Cell cMaternoT = filaTrab.createCell(numCel);
			cMaternoT.setCellValue(nombres.substring(nombres.indexOf(",") + 1, nombres.indexOf("/")));
			numCel++;

			Cell cNombresT = filaTrab.createCell(numCel);
			cNombresT.setCellValue(nombres.substring(nombres.indexOf("/") + 1, nombres.length()));
			numCel++;

			Cell cFIngresoT = filaTrab.createCell(numCel);
			cFIngresoT.setCellValue(trabajador.getValorPorDescripcionContains("fecha de ing"));
			numCel++;

			Cell cDLaboradosT = filaTrab.createCell(numCel);
			cDLaboradosT.setCellValue(trabajador.getValorPorDescripcionContains("as laborados"));
			numCel++;

			Cell cTTrabajadorT = filaTrab.createCell(numCel);
			cTTrabajadorT.setCellValue(trabajador.getValorPorDescripcionContains("tipo de trab"));
			numCel++;

			Cell cTMandoT = filaTrab.createCell(numCel);
			cTMandoT.setCellValue(trabajador.getValorPorDescripcionContains("ndicador de mand"));
			numCel++;

			Cell cCMandoT = filaTrab.createCell(numCel);
			cCMandoT.setCellValue(trabajador.getValorPorDescripcionContains("unidad res")
				+ trabajador.getValorPorDescripcionContains("actividad inst")
				+ trabajador.getValorPorDescripcionContains("proyecto proc")
				+ trabajador.getValorPorDescripcionContains("Partida") + " "
				+ trabajador.getValorPorDescripcion("Puesto")
				+ trabajador.getValorPorDescripcionContains("grupo funcion")
				+ trabajador.getValorPorDescripcion("Función")
				+ trabajador.getValorPorDescripcionContains("subfunci"));

			numCel++;

			Cell cURT = filaTrab.createCell(numCel);
			cURT.setCellValue(trabajador.getValorPorDescripcionContains("Unidad Respons"));
			numCel++;

			Cell cPlazaT = filaTrab.createCell(numCel);

			if (me.getKey().equals("610"))
			{
			    cPlazaT.setCellValue("ENSEÑANZA");
			}
			else
			{
			    cPlazaT.setCellValue("BASE");
			}
			numCel++;

			Cell cCRT = filaTrab.createCell(numCel);
			cCRT.setCellValue(trabajador.getValorPorDescripcionContains("Centro"));
			numCel++;

			Cell cCRD = filaTrab.createCell(numCel);
			cCRD.setCellValue(trabajador.getValorPorDescripcionContains("n del tipo de Responsabilidad"));
			numCel++;

			Cell cCCLUES = filaTrab.createCell(numCel);
			cCCLUES.setCellValue(trabajador.getValorPorDescripcionContains("icencia Sin Goce de S"));
			numCel++;

			ajustePeriodos = acum.ajustaPeriodosPago(trabajador);

			Cell cFechaInicialT = filaTrab.createCell(numCel);
			cFechaInicialT.setCellValue(ajustePeriodos.get(0));
			numCel++;

			Cell cFechaFinalT = filaTrab.createCell(numCel);
			cFechaFinalT.setCellValue(ajustePeriodos.get(1));
			numCel++;

			Cell cIPT = filaTrab.createCell(numCel);
			cIPT.setCellValue(trabajador.getValorPorDescripcionContains("nstrumento de Pago N"));
			numCel++;

			Cell cCURPT = filaTrab.createCell(numCel);
			cCURPT.setCellValue(trabajador.getValorPorDescripcionContains("CURP"));
			numCel++;

			for (Object obj : map.values())
			{
			    Concepto con = (Concepto) obj;

			    Cell celConcep = filaTrab.createCell(numCel);
			    Concepto cn = null;

			    if (trabajador.getConceptosAcum().containsKey(
				    "" + con.getTipoConcepto() + con.getClave() + " " + con.getPartidaAntecedente()))
			    {

				cn = (Concepto) trabajador.getConceptosAcum().get("" + con.getTipoConcepto()
					+ con.getClave() + " " + con.getPartidaAntecedente());

				celConcep.setCellValue(cn.getValorString());
			    }

			    numCel++;

			}

			totales = trabajador.getPercepDeducNeto();

			Cell cPERCEPT = filaTrab.createCell(numCel);
			cPERCEPT.setCellValue(utilidades.formato.format(totales.get(0)));
			numCel++;

			Cell cDEDUCT = filaTrab.createCell(numCel);
			cDEDUCT.setCellValue(utilidades.formato.format(totales.get(1)));
			numCel++;

			Cell cNETOT = filaTrab.createCell(numCel);
			cNETOT.setCellValue(utilidades.formato.format(totales.get(2)));
			numCel++;

			fila++;

		    }

		}

	    }

	    Sheet paginaProd = libroExcel.createSheet("Productos");
	    fila = 0;
	    int numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto producto : prds)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			producto.getAño() + "-" + producto.getQuincena() + " " + producto.getNombreProducto());
		fila++;
	    }

	    fila = 1;

	    for (Producto cheq : chequesCancelados)
	    {
		Row fi = paginaProd.getRow(fila);
		Cell celp = fi.createCell(1);
		celp.setCellValue(cheq.getAño() + "-" + cheq.getQuincena() + " " + cheq.getNombreProducto());
		fila++;

	    }

	    for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
	    {
		for (int c = 0; c < conAcum.size() + 4; c++)
		{
		    libroExcel.getSheetAt(x).autoSizeColumn(c);
		}
	    }

	    /*
	     * for( Object obj : conAcum.values() ) {
	     * 
	     * Row filaLinea = pagina1.createRow(fila); Cell celdaLinea =
	     * filaLinea.createCell(0); celdaLinea.setCellValue("");
	     * 
	     * numCel++;
	     * 
	     * }
	     */

	    try
	    {
		File f = new File("#{resource['images:temp1.txt']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xls");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarRepv1(boolean descargarTxt)
    {

	reporte = new ReporteVarios();
	List<Concepto> conceptosIncluidos = new ArrayList<>();

	// Se reutiliza la variable asociadoAPlaza solo con fines de repv para
	// poder verificar cuáles conceptos se van a incluir en el reporte, esto
	// para poder realizar diversas consultas sobre los mismos productos sin
	// tener que borrar los conceptos.
	for (Concepto conObj : this.conceptosRepv)
	{
	    if (conObj.isAsociadoAPlaza())
	    {
		conceptosIncluidos.add(conObj);
	    }
	}

	// Se ordenan los productos seleccionados
	List<Producto> prdsSublist = this.productosSelec.subList(0, this.productosSelec.size());

	prdsSublist.sort((Producto o1, Producto o2) -> o2.getAñoQuincenaString().compareTo(o1.getAñoQuincenaString()));

	// Se obtiene una sublista de clones
	List<Producto> prds = new ArrayList<>();

	prdsSublist.forEach(item ->
	{
	    prds.add((Producto) item.clone());

	});

	File f = null;
	PrintWriter wr = null;

	Sesion sesion = (Sesion) FacesUtils.getManagedBean("Sesion");
	String rutaArchivos = utilidades.getRutaReportesRepvServer() + sesion.getIdUsuario() + "/repvind/";
	File directorioUsuario = new File(rutaArchivos);

	utilidades.eliminarRuta(directorioUsuario);

	directorioUsuario.mkdirs();

	String nombreArchivoRepv = this.nombreArchivoRepv.contains(".txt") ? this.nombreArchivoRepv
		: this.nombreArchivoRepv + ".txt";

	try
	{
	    f = new File(rutaArchivos + nombreArchivoRepv);
	    // FileWriter w = new FileWriter(f);
	    // BufferedWriter bw = new BufferedWriter(w);
	    wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

	    Collator comparador = Collator.getInstance();
	    comparador.setStrength(Collator.IDENTICAL);
	    boolean unidadEncontrada = false;

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Leyendo Producto: " + producto.getNombreProducto());

		producto.filtraConceptosIndividualesRepv(conceptosIncluidos);

		for (UnidadRegistros unid : producto.getUnidadesRegsClasificados())
		{
		    unidadEncontrada = false;

		    for (UnidadRegistros unidReg : reporte.getUnidades())
		    {
			if (unidReg.getPlaza().getIdPlaza() == unid.getPlaza().getIdPlaza())
			{
			    if (unidReg.getDescripcion().trim().toLowerCase()
				    .equalsIgnoreCase(unid.getDescripcion().toLowerCase().trim()))
			    {
				unidadEncontrada = true;

				for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
				{

				    PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);
				    unidReg.getRegistrosDAT().add(regDAT);

				}

			    }

			}

		    }

		    if (!unidadEncontrada)
		    {
			reporte.getUnidades().add(unid);
		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

	    }

	    if (acumularConceptosRepv)
	    {
		// Si se va a acumular los totales...
		for (UnidadRegistros unidReg : reporte.getUnidades())
		{

		    for (int y = 0; y < unidReg.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unidReg.getRegistrosDAT().get(y);

			// Se recorren los registros en busca de unificar los
			// conceptos de los coincidentes
			for (int x = (y + 1); x < unidReg.getRegistrosDAT().size(); x++)
			{

			    // Se determina si es el mismo empleado y se
			    // unifican
			    // dentro del registro del ciclo principal
			    PlantillaRegistro regComparando = unidReg.getRegistrosDAT().get(x);

			    if (regComparando.getValorPorDescripcionContains("mero de emp")
				    .equalsIgnoreCase(regDAT.getValorPorDescripcionContains("mero de emp")))
			    {

				for (Concepto conBuscando : regComparando.getListConceptosAcum())
				{
				    boolean encontrado = false;

				    for (Concepto conUnificar : regDAT.getListConceptosAcum())
				    {
					if (conBuscando.getTipoConcepto() == conUnificar.getTipoConcepto()
						&& conBuscando.getClave().equals(conUnificar.getClave())
						&& conBuscando.getPartida().equals(conUnificar.getPartida())
						&& conBuscando.getPartidaAntecedente()
							.equals(conUnificar.getPartidaAntecedente()))
					{
					    conUnificar.addValor(conBuscando.getValor());
					    encontrado = true;
					    break;

					}

				    }

				    if (!encontrado)
				    {
					regDAT.addConcepto(conBuscando);
				    }

				}

				unidReg.getRegistrosDAT().remove(x);
				x--;

			    }

			}

		    }

		}

	    }

	    int nRegsGeneral = 0;
	    BigDecimal totalGeneral = new BigDecimal("0.00");

	    for (UnidadRegistros unidRegs : reporte.getUnidades())
	    {
		nRegsGeneral += unidRegs.getRegistrosDAT().size();

		wr.println(unidRegs.getPlaza().getDescripcionPlaza().toUpperCase() + " " + unidRegs.getDescripcion());
		wr.println("");

		System.out.println(
			unidRegs.getPlaza().getDescripcionPlaza().toUpperCase() + " " + unidRegs.getDescripcion());

		BigDecimal total = new BigDecimal("0.00");

		Collections.sort(unidRegs.getRegistrosDAT(), (o1, o2) ->
		{
		    PlantillaRegistro reg1 = (PlantillaRegistro) o1;
		    PlantillaRegistro reg2 = (PlantillaRegistro) o2;

		    String rfc1 = reg1.getValorPorDescripcionContains("RFC");

		    String rfc2 = reg2.getValorPorDescripcionContains("RFC");

		    return ((Comparable<String>) rfc1).compareTo(rfc2);

		});

		for (int y = 0; y < unidRegs.getRegistrosDAT().size(); y++)
		{

		    PlantillaRegistro regDAT = unidRegs.getRegistrosDAT().get(y);

		    for (Object obj : regDAT.getListConceptosAcum())
		    {
			String output = String.format("%1$-20s", regDAT.getValorPorDescripcionContains("RFC"))
				+ "| " + String
					.format("%1$-66s",
						regDAT.getValorPorDescripcionContains("Nombre del emp")
							.replace(",", " ").replace("/", " "))
				+ "|	" + String.format("%1$-33s",

					regDAT.getValorPorDescripcionContains("unidad res")
						+ regDAT.getValorPorDescripcionContains("actividad inst")
						+ regDAT.getValorPorDescripcionContains("proyecto proc")
						+ regDAT.getValorPorDescripcionContains("Partida") + " "
						+ regDAT.getValorPorDescripcion("Puesto")
						+ regDAT.getValorPorDescripcionContains("mero de Puesto")
						+ regDAT.getValorPorDescripcionContains("grupo funcion")
						+ regDAT.getValorPorDescripcion("Función")
						+ regDAT.getValorPorDescripcionContains("subfunci"))
				+ "| ";

			System.out.print(output);

			Concepto con = (Concepto) obj;
			total = total.add(con.getValor());

			System.out.println(con.getTipoConcepto() + "" + con.getClave() + "	|	"
				+ con.getPartidaAntecedente() + "	|	" + con.getValor());

			if (descargarTxt)
			{
			    wr.println(
				    output + String.format("%1$-19s", "" + con.getTipoConcepto() + "" + con.getClave())
					    + "| " + String.format("%1$-14s", con.getPartidaAntecedente()) + "| "
					    + String.format("%1$-25s", con.getValor()));

			}

			totalGeneral = totalGeneral.add(con.getValor());

		    }

		}

		unidRegs.setTotal(total);
		unidRegs.setTotalString(utilidades.formato.format(total));
		System.out.println("");
		System.out.println("Registros: " + unidRegs.getRegistrosDAT().size());
		System.out.println("Monto: " + utilidades.formato.format(total));

		wr.println("");
		wr.println("Registros: " + unidRegs.getRegistrosDAT().size());
		wr.println("Monto: " + utilidades.formato.format(total));
		wr.println("");
		wr.println("");

	    }

	    System.out.println("");
	    this.reporte.setNumeroRegistrosGeneral(nRegsGeneral);
	    this.reporte.setTotal(totalGeneral);
	    this.reporte.setTotalString(utilidades.formato.format(totalGeneral));

	    System.out.println("Número de registros total: " + nRegsGeneral);
	    System.out.println("Monto total general: " + utilidades.formato.format(this.reporte.getTotal()));

	    wr.println("");
	    wr.println("Número de registros total: " + nRegsGeneral);
	    wr.println("Monto total general: " + utilidades.formato.format(this.reporte.getTotal()));

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	if (!descargarTxt)
	{
	    return;
	}

	try
	{

	    wr.close();
	    // bw.close();
	    // w.close();

	    txtRepv = new DefaultStreamedContent(new FileInputStream(f), "text/plain", nombreArchivoRepv);

	    utilidades.eliminarRuta(directorioUsuario);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarRepvComprimidoFinancieras()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prds = this.productosSelec.subList(0, this.productosSelec.size());

	Collections.sort(prds, (o1, o2) ->
	{
	    Producto reg1 = (Producto) o1;
	    Producto reg2 = (Producto) o2;

	    String q1 = "" + reg1.getQuincena();
	    String q2 = "" + reg2.getQuincena();

	    if (q1.length() == 1)
	    {
		q1 = "0" + q1;
	    }

	    if (q2.length() == 1)
	    {
		q2 = "0" + q2;
	    }

	    return ((Comparable<?>) reg2.getAño() + "" + q2).compareTo(reg1.getAño() + "" + q1);
	});

	File f = null;
	PrintWriter wr = null;

	List<Financiera> financieras = utilidades.getCatalogoFinancieras();

	List<File> archivosGenerados = new ArrayList<>();

	File zip = null;

	// Se crea una lista de los conceptos que se buscarán para las
	// financieras
	List<Concepto> conceptosBusqueda = null;

	Sesion sesion = (Sesion) FacesUtils.getManagedBean("Sesion");
	String rutaArchivos = utilidades.getRutaReportesRepvServer() + sesion.getIdUsuario() + "/";
	File directorioUsuario = new File(rutaArchivos);

	utilidades.eliminarRuta(directorioUsuario);

	directorioUsuario.mkdirs();

	try
	{

	    for (Financiera financiera : financieras)
	    {
		reporte = new ReporteVarios();

		conceptosBusqueda = new ArrayList<>();

		conceptosBusqueda
			.add(new Concepto(2, "46", "", "", financiera.getPartidaAntecedente(), new BigDecimal("0.00")));

		f = new File(rutaArchivos + this.prefijoFinancieras + financiera.getAbreviacionArchivo()
			+ this.sufijoFinancieras + ".txt");

		archivosGenerados.add(f);
		// FileWriter w = new FileWriter(f);
		// BufferedWriter bw = new BufferedWriter(w);
		wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

		Collator comparador = Collator.getInstance();
		comparador.setStrength(Collator.IDENTICAL);
		boolean unidadEncontrada = false;

		for (int x = 0; x < prds.size(); x++)
		{
		    Producto producto = prds.get(x);

		    System.out.println("Leyendo Producto: " + producto.getNombreProducto());

		    producto.filtraConceptosIndividualesRepv(conceptosBusqueda);

		    for (UnidadRegistros unid : producto.getUnidadesRegsClasificados())
		    {
			unidadEncontrada = false;

			for (UnidadRegistros unidReg : reporte.getUnidades())
			{
			    if (unidReg.getPlaza().getIdPlaza() == unid.getPlaza().getIdPlaza())
			    {
				if (unidReg.getDescripcion().trim().toLowerCase()
					.equalsIgnoreCase(unid.getDescripcion().toLowerCase().trim()))
				{
				    unidadEncontrada = true;

				    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
				    {

					PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);
					unidReg.getRegistrosDAT().add(regDAT);

				    }

				}

			    }

			}

			if (!unidadEncontrada)
			{
			    reporte.getUnidades().add(unid);
			}

		    }

		    producto.setRegistrosTRA(new ArrayList<>());
		    producto.setRegistrosDAT(new ArrayList<>());
		    producto.setConceptos(new ArrayList<>());
		    producto.setUnidadResponsable(new ArrayList<>());
		    producto.reiniciaProducto();

		}

		int nRegsGeneral = 0;
		BigDecimal totalGeneral = new BigDecimal("0.00");

		Collections.sort(reporte.getUnidades(), (o1, o2) ->
		{
		    UnidadRegistros unid1 = (UnidadRegistros) o1;
		    UnidadRegistros unid2 = (UnidadRegistros) o2;

		    return ((Comparable<String>) unid1.getDescripcion()).compareTo(unid2.getDescripcion());

		});

		for (UnidadRegistros unidRegs : reporte.getUnidades())
		{
		    nRegsGeneral += unidRegs.getRegistrosDAT().size();

		    if (unidRegs.getRegistrosDAT().size() == 0)
		    {
			continue;
		    }

		    wr.print(unidRegs.getPlaza().getDescripcionPlaza().toUpperCase() + " " + unidRegs.getDescripcion()
			    + "\r\n");

		    System.out.println(
			    unidRegs.getPlaza().getDescripcionPlaza().toUpperCase() + " " + unidRegs.getDescripcion());

		    BigDecimal total = new BigDecimal("0.00");

		    Collections.sort(unidRegs.getRegistrosDAT(), (o1, o2) ->
		    {
			PlantillaRegistro reg1 = (PlantillaRegistro) o1;
			PlantillaRegistro reg2 = (PlantillaRegistro) o2;

			String rfc1 = reg1.getValorPorDescripcionContains("RFC");

			String rfc2 = reg2.getValorPorDescripcionContains("RFC");

			return ((Comparable<String>) rfc1).compareTo(rfc2);

		    });

		    for (int y = 0; y < unidRegs.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unidRegs.getRegistrosDAT().get(y);

			for (Object obj : regDAT.getListConceptosAcum())
			{
			    String output = String.format("%1$-14s", regDAT.getValorPorDescripcionContains("RFC"))
				    + "| "
				    + String.format("%1$-66s",
					    "      " + regDAT.getValorPorDescripcionContains("Nombre del emp")
						    .replace(",", " ").replace("/", " "))
				    + "|  " + String.format("%1$-33s",

					    unidRegs.getDescripcion()
						    // regDAT.getValorPorDescripcionContains("unidad res")
						    + regDAT.getValorPorDescripcionContains("actividad inst")
						    + regDAT.getValorPorDescripcionContains("proyecto proc")
						    + regDAT.getValorPorDescripcionContains("Partida") + " "
						    + regDAT.getValorPorDescripcion("Puesto")
						    + regDAT.getValorPorDescripcionContains("mero de Puesto")
						    + regDAT.getValorPorDescripcionContains("grupo funcion")
						    + regDAT.getValorPorDescripcion("Función")
						    + regDAT.getValorPorDescripcionContains("subfunci"))
				    + "| ";

			    System.out.print(output);

			    Concepto con = (Concepto) obj;
			    total = total.add(con.getValor());

			    System.out.println(con.getTipoConcepto() + "" + con.getClave() + "	|	"
				    + con.getPartidaAntecedente() + "	|	" + con.getValor());

			    wr.print(output + String.format("%1$-19s", "" + con.getTipoConcepto() + "" + con.getClave())
				    + "| " + String.format("%1$-14s", con.getPartidaAntecedente()) + "| "
				    + String.format("%1$-25s", con.getValor()) + "\r\n");

			    totalGeneral = totalGeneral.add(con.getValor());

			}

		    }

		    unidRegs.setTotal(total);
		    unidRegs.setTotalString(utilidades.formato.format(total));
		    System.out.println("");
		    System.out.println("Registros: " + unidRegs.getRegistrosDAT().size());
		    System.out.println("Monto: " + utilidades.formato.format(total));

		    wr.print("\r\n");
		    wr.print("Registros: " + unidRegs.getRegistrosDAT().size() + "\r\n");
		    wr.print("Monto: " + utilidades.formato.format(total) + "\r\n\r\n");

		}

		if (nRegsGeneral == 0)
		{
		    f.delete();
		    continue;
		}

		System.out.println("");
		this.reporte.setNumeroRegistrosGeneral(nRegsGeneral);
		this.reporte.setTotal(totalGeneral);
		this.reporte.setTotalString(utilidades.formato.format(totalGeneral));

		System.out.println("Número de registros total: " + nRegsGeneral);
		System.out.println("Monto total general: " + utilidades.formato.format(this.reporte.getTotal()));

		wr.print("\r\n");
		wr.print("Número de registros total: " + nRegsGeneral + "\r\n");
		wr.print("Monto total general: " + utilidades.formato.format(this.reporte.getTotal()) + "\r\n");

		wr.close();

	    }

	    final int BUFFER = 2048;

	    try
	    {

		BufferedInputStream origin = null;

		// out.setMethod(ZipOutputStream.DEFLATED);
		byte data[] = new byte[BUFFER];

		// get a list of files from current directory
		File f1 = new File(rutaArchivos);
		String files[] = f1.list();

		FileOutputStream dest = new FileOutputStream(
			rutaArchivos + this.prefijoFinancieras + this.sufijoFinancieras + ".zip");

		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

		for (int i = 0; i < files.length; i++)
		{
		    System.out.println("Adding: " + files[i]);
		    FileInputStream fi = new FileInputStream(rutaArchivos + files[i]);
		    origin = new BufferedInputStream(fi, BUFFER);
		    ZipEntry entry = new ZipEntry(files[i]);
		    out.putNextEntry(entry);
		    int count;

		    while ((count = origin.read(data, 0, BUFFER)) != -1)
		    {
			out.write(data, 0, count);
		    }
		    origin.close();
		}

		out.close();

		zip = new File(rutaArchivos + this.prefijoFinancieras + this.sufijoFinancieras + ".zip");
		txtRepv = new DefaultStreamedContent(new FileInputStream(zip), "text/plain",
			"financieras_" + this.prefijoFinancieras + this.sufijoFinancieras + ".zip");

		utilidades.eliminarRuta(directorioUsuario);

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDetalleISRPagado()
    {

	// Se ordenan los productos seleccionados
	List<Producto> prdsSublist = this.productosSelec.subList(0, this.productosSelec.size());

	prdsSublist.sort((o1, o2) -> (o2.getAñoQuincenaString() + o2.getNombreProducto())
		.compareTo((o1.getAñoQuincenaString() + o1.getNombreProducto())));

	// Se obtiene una sublista de clones
	List<Producto> prds = new ArrayList<>();

	prdsSublist.forEach(item ->
	{
	    prds.add((Producto) item.clone());

	});

	List<Producto> chequesCancelados = new ArrayList<>();
	List<Producto> chequesCanceladosSublist = new ArrayList<>();

	Producto clon;
	Producto clonCheq;

	try
	{
	    for (int x = 0; x < prds.size(); x++)
	    {

		if (!this.incluirPension)
		{
		    if (prds.get(x).getTipoNomina().getDescripcion().contains("Pensi"))
		    {
			prds.remove(prds.get(x));
			x--;
			continue;
		    }

		}

		if (this.cancelarCheques || this.soloCancelaciones)
		{
		    if (prds.get(x).getTipoProducto().getDescripcion().contains("Cancelado"))
		    {
			chequesCancelados.add(prds.get(x));
			prds.remove(prds.get(x));
			x--;

		    }

		}

	    }

	    chequesCancelados.forEach(item ->
	    {
		chequesCanceladosSublist.add((Producto) item.clone());
	    });

	    SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);
	    List<BigDecimal> totales;

	    int fila = 0;
	    int numCel = 0;

	    XSSFFont font = (XSSFFont) libroExcel.createFont();
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);

	    XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();
	    style.setFont(font);

	    XSSFCellStyle styleMoneda = (XSSFCellStyle) libroExcel.createCellStyle();
	    styleMoneda.setDataFormat((short) 7);

	    boolean cancelado = false;

	    Concepto con;

	    // Se crea una sola hoja para todo el reporte, ya que existe una columna que
	    // especifica a qué unidad responsable pertence el registro del trabajador
	    SXSSFSheet pagina = libroExcel.createSheet("Reporte ISR");
	    // pagina.trackAllColumnsForAutoSizing();

	    // Se crean los encabezados de la página
	    fila = 0;
	    numCel = 0;

	    Row filaEncabezado = pagina.createRow(fila);

	    Cell cConcepto = filaEncabezado.createCell(numCel);
	    cConcepto.setCellValue("CONCEPTO DE PAGO");
	    cConcepto.setCellStyle(style);
	    numCel++;

	    Cell cDescripcionConcepto = filaEncabezado.createCell(numCel);
	    cDescripcionConcepto.setCellValue("DESCRIPCION");
	    cDescripcionConcepto.setCellStyle(style);
	    numCel++;

	    Cell cUnidadResponsable = filaEncabezado.createCell(numCel);
	    cUnidadResponsable.setCellValue("UNIDAD RESPONSABLE");
	    cUnidadResponsable.setCellStyle(style);
	    numCel++;

	    Cell nNombreTrabajador = filaEncabezado.createCell(numCel);
	    nNombreTrabajador.setCellValue("NOMBRE DEL TRABAJADOR");
	    nNombreTrabajador.setCellStyle(style);
	    numCel++;

	    Cell cRFC = filaEncabezado.createCell(numCel);
	    cRFC.setCellValue("RFC");
	    cRFC.setCellStyle(style);
	    numCel++;

	    Cell cTipoNomina = filaEncabezado.createCell(numCel);
	    cTipoNomina.setCellValue("TIPO DE NOMINA");
	    cTipoNomina.setCellStyle(style);
	    numCel++;

	    Cell cCodigoPuesto = filaEncabezado.createCell(numCel);
	    cCodigoPuesto.setCellValue("CODIGO DE PUESTO");
	    cCodigoPuesto.setCellStyle(style);
	    numCel++;

	    Cell cFechaPago = filaEncabezado.createCell(numCel);
	    cFechaPago.setCellValue("FECHA DE PAGO");
	    cFechaPago.setCellStyle(style);
	    numCel++;

	    Cell cImporteBruto = filaEncabezado.createCell(numCel);
	    cImporteBruto.setCellValue("IMPORTE BRUTO");
	    cImporteBruto.setCellStyle(style);
	    numCel++;

	    Cell cISR = filaEncabezado.createCell(numCel);
	    cISR.setCellValue("ISR");
	    cISR.setCellStyle(style);
	    numCel++;

	    Cell cImporteNeto = filaEncabezado.createCell(numCel);
	    cImporteNeto.setCellValue("IMPORTE NETO");
	    cImporteNeto.setCellStyle(style);
	    numCel++;

	    List<Concepto> catConceptos = utilidades.getConceptos();

	    for (int x = 0; x < prds.size(); x++)
	    {
		Producto producto = prds.get(x);

		System.out.println("Leyendo Producto: " + producto.getNombreProducto());

		producto.updateRegistrosTRAConConceptos(
			Arrays.asList("416", "610", "U00", "U01", "FOR", "FO2", "FO3", "X00", "CON", "HOM", "U03"),
			false);

		for (UnidadProducto unid : producto.getUnidadResponsable())
		{

		    for (int y = 0; y < unid.getRegistrosDAT().size(); y++)
		    {

			PlantillaRegistro regDAT = unid.getRegistrosDAT().get(y);

			cancelado = false;

			if (this.cancelarCheques || this.soloCancelaciones)
			{

			    // Comparar con los cheques cancelados
			    for (Producto chequeCan : chequesCancelados)
			    {
				clonCheq = chequeCan;

				if (clonCheq.getRegistrosTRA() == null)
				{
				    clonCheq.updateRegistrosTRA();
				}

				for (UnidadProducto unidCheq : clonCheq.getUnidadResponsable())
				{

				    for (PlantillaRegistro regCheq : unidCheq.getRegistrosDAT())
				    {
					// Quincena de proceso o real
					// Año de proceso o real
					if (regDAT.getValorPorDescripcionContains("mero de emple")
						.equals(regCheq.getValorPorDescripcionContains("mero de emple")))
					{

					    if (regDAT.getValorPorDescripcionContains("mero de cheque")
						    .equals(regCheq.getValorPorDescripcionContains("mero de cheque")))
					    {

						if ((regDAT.getValorPorDescripcionContains("odo de pago inicia").equals(
							regCheq.getValorPorDescripcionContains("odo de pago inicia"))
							&& regDAT.getValorPorDescripcionContains("odo de pago fina")
								.equals(regCheq.getValorPorDescripcionContains(
									"odo de pago fina")))
							|| (regDAT.getValorPorDescripcionContains("ena de Proceso o Re")
								.equals(regCheq.getValorPorDescripcionContains(
									"ena de Proceso o Re"))
								&& regDAT
									.getValorPorDescripcionContains(
										"o de Proceso o R")
									.equals(regCheq.getValorPorDescripcionContains(
										"o de Proceso o R"))))
						{

						    System.out.println("Cancelado el cheque "
							    + regCheq.getValorPorDescripcionContains("mero de chequ"));

						    if (!this.soloCancelaciones)
						    {
							// Si no se busca solo
							// las cancelaciones de
							// cheques se remueve
							// del producto
							// unidCheq.getRegistrosDAT().remove(regCheq);
						    }
						    else
						    {
							regDAT = regCheq;
						    }

						    cancelado = true;
						    break;
						}

					    }

					}

				    }

				    if (cancelado)
				    {
					break;
				    }

				}

				if (cancelado)
				{
				    break;
				}

			    }

			    if (cancelado && !this.soloCancelaciones)
			    {

				continue;
			    }

			}

			if (this.soloCancelaciones && !cancelado)
			{
			    continue;
			}

			List<List<String>> regs = regDAT.getConceptosISRCalculados();

			for (List<String> conceptoConISR : regs)
			{
			    fila++;
			    numCel = 0;

			    Row filaConcepto = pagina.createRow(fila);

			    String[] conceptoSplit = conceptoConISR.get(0).split("-");
			    Optional<Concepto> descripcionConcepto = catConceptos.stream()
				    .filter(c -> ("" + c.getTipoConcepto()).equals(conceptoSplit[0])
					    && c.getClave().equals(conceptoSplit[1])
					    && c.getPartidaAntecedente().equals(conceptoSplit[2]))
				    .findFirst();

			    Cell cRegConcepto = filaConcepto.createCell(numCel);
			    cRegConcepto.setCellValue("0" + conceptoConISR.get(0).replaceAll("-", ""));
			    numCel++;

			    Cell cRegDescripcionConcepto = filaConcepto.createCell(numCel);
			    cRegDescripcionConcepto.setCellValue(descripcionConcepto.get().getDescripcion());
			    numCel++;

			    Cell cRegUnidadResponsable = filaConcepto.createCell(numCel);
			    cRegUnidadResponsable.setCellValue(
				    producto.getPlaza().getIdPlaza() == 2 ? "REG" : unid.getDescripcion());
			    numCel++;

			    Cell nRegNombreTrabajador = filaConcepto.createCell(numCel);
			    nRegNombreTrabajador.setCellValue(regDAT.getValorPorDescripcionContains("Nombre"));
			    numCel++;

			    Cell cRegRFC = filaConcepto.createCell(numCel);
			    cRegRFC.setCellValue(regDAT.getValorPorDescripcionContains("RFC"));
			    numCel++;

			    String tipoNomina = "";

			    if (conceptoConISR.get(0).contains("DT"))
			    {
				tipoNomina = "6T";
			    }
			    else if (conceptoConISR.get(0).contains("DM"))
			    {
				tipoNomina = "6M";
			    }
			    else if (conceptoConISR.get(0).contains("DR"))
			    {
				tipoNomina = "6R";
			    }
			    else if (conceptoConISR.get(0).contains("EA"))
			    {
				tipoNomina = "6O";
			    }
			    else if (conceptoConISR.get(0).contains("ES"))
			    {
				tipoNomina = "6S";
			    }
			    else if (conceptoConISR.get(0).contains("57"))
			    {
				tipoNomina = "66";
			    }
			    else if (conceptoConISR.get(0).contains("TP"))
			    {
				tipoNomina = "66";
			    }
			    else if (conceptoConISR.get(0).contains("TR") || conceptoConISR.get(0).contains("AN")
				    || conceptoConISR.get(0).contains("AP"))
			    {
				tipoNomina = "6S";
			    }
			    else
			    {
				tipoNomina = producto.getDescripcion();
			    }

			    Cell cRegTipoNomina = filaConcepto.createCell(numCel);
			    cRegTipoNomina.setCellValue(tipoNomina);
			    numCel++;

			    Cell cRegCodigoPuesto = filaConcepto.createCell(numCel);
			    cRegCodigoPuesto.setCellValue(regDAT.getValorPorDescripcionContains("puesto"));
			    numCel++;

			    Cell cRegFechaPago = filaConcepto.createCell(numCel);
			    cRegFechaPago.setCellValue(regDAT.getValorPorDescripcionContains("Fecha de pag"));
			    numCel++;

			    Cell cRegImporteBruto = filaConcepto.createCell(numCel);
			    cRegImporteBruto.setCellValue(conceptoConISR.get(2));
			    cRegImporteBruto.setCellStyle(styleMoneda);
			    numCel++;

			    Cell cRegISR = filaConcepto.createCell(numCel);
			    cRegISR.setCellValue(conceptoConISR.get(3));
			    cRegISR.setCellStyle(styleMoneda);
			    numCel++;

			    Cell cRegImporteNeto = filaConcepto.createCell(numCel);
			    cRegImporteNeto.setCellValue(conceptoConISR.get(4));
			    cRegImporteNeto.setCellStyle(styleMoneda);
			    numCel++;

			}

			/*
			 * System.out.println("El registro " +
			 * regDAT.getValorPorDescripcionContains("mero de chequ") + " cuenta con " +
			 * regDAT.getConceptosAcum().size() + " conceptos.");
			 */

			unid.getRegistrosDAT().remove(y);
			y--;

		    }

		}

		producto.setRegistrosTRA(new ArrayList<>());
		producto.setRegistrosDAT(new ArrayList<>());
		producto.setConceptos(new ArrayList<>());
		producto.setUnidadResponsable(new ArrayList<>());

	    }

	    SXSSFSheet paginaProd = libroExcel.createSheet("Productos");
	    paginaProd.trackAllColumnsForAutoSizing();
	    fila = 0;
	    numCel = 0;

	    Row ff = paginaProd.createRow(fila);

	    Cell cel = ff.createCell(numCel);
	    cel.setCellValue("PRODUCTOS");
	    cel.setCellStyle(style);
	    numCel++;

	    Cell celC = ff.createCell(numCel);
	    celC.setCellValue("CHEQUES CANCELADOS");
	    celC.setCellStyle(style);
	    numCel++;

	    fila++;

	    for (Producto productoP : prdsSublist)
	    {
		Row fi = paginaProd.createRow(fila);
		Cell celp = fi.createCell(0);
		celp.setCellValue(
			productoP.getAño() + "-" + productoP.getQuincena() + " " + productoP.getNombreProducto());
		fila++;
	    }

	    /*
	     * for (int x = 0; x < libroExcel.getNumberOfSheets(); x++) { for (int c = 0; c
	     * < 11; c++) { libroExcel.getSheetAt(x).autoSizeColumn(c); } }
	     */
	    fila = 1;

	    try
	    {
		File f = new File("#{resource['images:temp1.xlsx']}");

		FileOutputStream out = new FileOutputStream(f);
		libroExcel.write(out);
		out.close();

		libroExcel.dispose();

		txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain", "Acumulado.xlsx");

	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	catch (

	Exception e)
	{
	    e.printStackTrace();
	}

    }

    private static HashMap sortByValues(HashMap map)
    {
	List list = new LinkedList(map.entrySet());

	// Defined Custom Comparator here
	Collections.sort(list, new Comparator()
	{

	    public int compare(Object o1, Object o2)
	    {
		String qnaObj1 = null;
		String añoObj1 = null;
		String qnaObj2 = null;
		String añoObj2 = null;

		PlantillaRegistro reg1 = (PlantillaRegistro) ((Map.Entry) (o1)).getValue();
		PlantillaRegistro reg2 = (PlantillaRegistro) ((Map.Entry) (o2)).getValue();

		añoObj1 = reg1.getValorPorDescripcionContains("Año de proce");
		qnaObj1 = reg1.getValorPorDescripcionContains("quincena de proc");

		if (qnaObj1.length() == 1)
		{
		    qnaObj1 = "0" + qnaObj1;
		}

		añoObj2 = reg2.getValorPorDescripcionContains("Año de proce");
		qnaObj2 = reg2.getValorPorDescripcionContains("quincena de proc");

		if (qnaObj2.length() == 1)
		{
		    qnaObj2 = "0" + qnaObj2;
		}

		return ((Comparable) (añoObj1 + qnaObj1 + reg1.getValorPorDescripcionContains("RFC")))
			.compareTo(añoObj2 + qnaObj2 + reg2.getValorPorDescripcionContains("RFC"));
	    }
	});

	// Here I am copying the sorted list in HashMap
	// using LinkedHashMap to preserve the insertion order
	HashMap sortedHashMap = new LinkedHashMap();
	for (Iterator it = list.iterator(); it.hasNext();)
	{
	    Map.Entry entry = (Map.Entry) it.next();
	    sortedHashMap.put(entry.getKey(), entry.getValue());
	}
	return sortedHashMap;
    }

    public List<Producto> getProductosFiltro()
    {
	return productosFiltro;
    }

    public void setProductosFiltro(List<Producto> productosFiltro)
    {
	this.productosFiltro = productosFiltro;
    }

    public Producto getProductoSelec()
    {
	return productoSelec;
    }

    public void setProductoSelec(Producto productoSelec)
    {
	this.productoSelec = productoSelec;
    }

    public static long getSerialversionuid()
    {
	return serialVersionUID;
    }

    public List<Producto> getProductosSelec()
    {
	return productosSelec;
    }

    public void setProductosSelec(List<Producto> productosSelec)
    {
	this.productosSelec = productosSelec;
    }

    public String getTotalPercepiones()
    {
	return totalPercepiones;
    }

    public void setTotalPercepiones(String totalPercepiones)
    {
	this.totalPercepiones = totalPercepiones;
    }

    public String getTotalDeducciones()
    {
	return totalDeducciones;
    }

    public void setTotalDeducciones(String totalDeducciones)
    {
	this.totalDeducciones = totalDeducciones;
    }

    public String getTotalLiquido()
    {
	return totalLiquido;
    }

    public void setTotalLiquido(String totalLiquido)
    {
	this.totalLiquido = totalLiquido;
    }

    public int getTotalRegistros()
    {
	return totalRegistros;
    }

    public void setTotalRegistros(int totalRegistros)
    {
	this.totalRegistros = totalRegistros;
    }

    public BigDecimal getBigPercepTotal()
    {
	return bigPercepTotal;
    }

    public void setBigPercepTotal(BigDecimal bigPercepTotal)
    {
	this.bigPercepTotal = bigPercepTotal;
    }

    public BigDecimal getBigDeducTotal()
    {
	return bigDeducTotal;
    }

    public void setBigDeducTotal(BigDecimal bigDeducTotal)
    {
	this.bigDeducTotal = bigDeducTotal;
    }

    public List<Concepto> getConceptos()
    {
	return conceptos;
    }

    public void setConceptos(List<Concepto> conceptos)
    {
	this.conceptos = conceptos;
    }

    public List<Concepto> getConceptosDeduc()
    {
	return conceptosDeduc;
    }

    public void setConceptosDeduc(List<Concepto> conceptosDeduc)
    {
	this.conceptosDeduc = conceptosDeduc;
    }

    public StreamedContent getTxt()
    {
	return txt;
    }

    public void setTxt(StreamedContent txt)
    {
	this.txt = txt;
    }

    public List<Concepto> getConceptosRepv()
    {
	return conceptosRepv;
    }

    public void setConceptosRepv(List<Concepto> conceptosRepv)
    {
	this.conceptosRepv = conceptosRepv;
    }

    public String getNombreArchivoRepv()
    {
	return nombreArchivoRepv;
    }

    public void setNombreArchivoRepv(String nombreArchivoRepv)
    {
	this.nombreArchivoRepv = nombreArchivoRepv;
    }

    public ReporteVarios getReporte()
    {
	return reporte;
    }

    public void setReporte(ReporteVarios reporte)
    {
	this.reporte = reporte;
    }

    public StreamedContent getTxtRepv()
    {
	return txtRepv;
    }

    public void setTxtRepv(StreamedContent txtRepv)
    {
	this.txtRepv = txtRepv;
    }

    public String getPrefijoFinancieras()
    {
	return prefijoFinancieras;
    }

    public void setPrefijoFinancieras(String prefijoFinancieras)
    {
	this.prefijoFinancieras = prefijoFinancieras;
    }

    public String getSufijoFinancieras()
    {
	return sufijoFinancieras;
    }

    public void setSufijoFinancieras(String sufijoFinancieras)
    {
	this.sufijoFinancieras = sufijoFinancieras;
    }

    public boolean isIncluirPension()
    {
	return incluirPension;
    }

    public void setIncluirPension(boolean incluirPension)
    {
	this.incluirPension = incluirPension;
    }

    public boolean isCancelarCheques()
    {
	return cancelarCheques;
    }

    public void setCancelarCheques(boolean cancelarCheques)
    {
	this.cancelarCheques = cancelarCheques;
    }

    public List<Producto> getCatProductos()
    {
	return catProductos;
    }

    public void setCatProductos(List<Producto> catProductos)
    {
	this.catProductos = catProductos;
    }

    public boolean isSoloCancelaciones()
    {
	return soloCancelaciones;
    }

    public void setSoloCancelaciones(boolean soloCancelaciones)
    {
	this.soloCancelaciones = soloCancelaciones;
    }

    public boolean isIncluirDifSubsidioImpuestos()
    {
	return incluirDifSubsidioImpuestos;
    }

    public void setIncluirDifSubsidioImpuestos(boolean incluirDifSubsidioImpuestos)
    {
	this.incluirDifSubsidioImpuestos = incluirDifSubsidioImpuestos;
    }

    public Boolean getAcumularConceptosRepv()
    {
	return acumularConceptosRepv;
    }

    public void setAcumularConceptosRepv(Boolean acumularConceptosRepv)
    {
	this.acumularConceptosRepv = acumularConceptosRepv;
    }

}
