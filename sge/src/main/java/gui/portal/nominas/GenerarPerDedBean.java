package gui.portal.nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import modelo.Concepto;
import modelo.ConceptoTimbre;
import modelo.Plaza;
import modelo.Producto;
import modelo.ProductoTimbrado;
import modelo.QuincenaDetalle;
import modelo.TipoProducto;
import util.utilidades;

@ManagedBean
@SessionScoped
public class GenerarPerDedBean implements Serializable
{

    private Plaza	       plaza;
    private int		       año;
    private TipoProducto       tipoProducto;

    private List<Plaza>	       plazas;
    private int		       plazaSelec;
    private List<TipoProducto> tiposProducto;
    int			       tipoProductoSelec;

    private StreamedContent    txt;

    BigDecimal		       bigPercepTotal;
    BigDecimal		       bigDeducTotal;

    private String	       totalPercepiones;
    private String	       totalDeducciones;
    private String	       totalLiquido;
    private int		       totalRegistros;
    private List<Concepto>     conceptos;
    private List<Concepto>     conceptosDeduc;

    private HashMap	       conceptosAdicional;
    private HashMap	       conceptosDeducAdicional;

    private boolean	       incluirProductos;
    private boolean	       incluirCancelados;
    private boolean	       incluirPension;
    private boolean	       excluirUnidades;
    private boolean	       incluir416;
    private boolean	       incluirU00;
    private boolean	       incluirU01;
    private boolean	       incluir610;
    /**
     * 
     */
    private static final long  serialVersionUID	= 1L;

    public GenerarPerDedBean()
    {
	super();
	// TODO Auto-generated constructor stub

	java.util.Calendar fechaActual = java.util.Calendar.getInstance();
	this.año = fechaActual.get(Calendar.YEAR);
	this.tiposProducto = utilidades.getTiposProducto();
	this.plazas = utilidades.getPlazas();
    }

    List<QuincenaDetalle> qnas;
    List<Concepto>	  catPercepciones;
    List<Concepto>	  catDeducciones;

    // tipoGeneracion 0 para productos normales, 1 para productos timbrados
    public void actionDescargarPerDed(int tipoGeneracion)
    {

	this.bigPercepTotal = new BigDecimal(0);
	this.bigDeducTotal = new BigDecimal(0);

	this.conceptos = new ArrayList<>();
	this.conceptosDeduc = new ArrayList<>();
	this.conceptosAdicional = new HashMap();
	this.conceptosDeducAdicional = new HashMap<>();

	this.totalRegistros = 0;

	catPercepciones = utilidades.getConceptos(1);
	catDeducciones = utilidades.getConceptos(2);

	// Se busca el concepto 201 SE, se transforma en 1SE 00 y se pasa a
	// percepciones
	for (Concepto con : catDeducciones)
	{
	    if (con.getTipoConcepto() == 2 && con.getClave().contentEquals("01")
		    && con.getPartidaAntecedente().equals("SE"))
	    {
		con.setTipoConcepto(1);
		con.setClave("SE");
		con.setPartidaAntecedente("00");

		catPercepciones.add(con);
		catDeducciones.remove(con);

		break;
	    }

	}

	// Se crean los objetos correspondientes para cada quincena del año
	qnas = new ArrayList<>();

	for (int x = 0; x < 24; x++)
	{
	    qnas.add(new QuincenaDetalle(this.año, (x + 1)));

	}

	// Se obtienen los productos de cada quincena y se van acumulando
	// los
	// conceptos
	List<Producto> prods = null;
	List<ProductoTimbrado> prodsTim = null;

	for (int x = 0; x < 24; x++)
	{

	    if (tipoGeneracion == 0)
	    {
		prods = utilidades.getProductosAño(this.año, (x + 1), this.incluirProductos, this.incluirCancelados,
			this.plazaSelec, this.incluirPension);

		for (Producto prod : prods)
		{
		    System.out.println("Leyendo prod: " + prod.getNombreProducto());
		    prod.updateRegistrosTRAConConceptos(this.incluir416, this.incluirU00, this.incluirU01,
			    this.incluir610, true, false);

		    bigPercepTotal = bigPercepTotal.add(prod.getBigPercepTotal());
		    bigDeducTotal = bigDeducTotal.add(prod.getBigDeducTotal());
		    this.totalRegistros += prod.getTotalRegistros();

		    for (Concepto con : prod.getConceptos())
		    {
			qnas.get(x).addConceptoExcepcion03(con);
		    }

		    for (Concepto con : prod.getConceptosDeduc())
		    {
			// Se hace el ajuste para convertir el concepto 201 SE a
			// 1SE
			// 00

			if (con.getTipoConcepto() == 2 && con.getClave().contentEquals("01")
				&& con.getPartidaAntecedente().equals("SE"))
			{
			    con.setTipoConcepto(1);
			    con.setClave("SE");
			    con.setPartidaAntecedente("00");
			    con.setValor(con.getValor().multiply(new BigDecimal("-1")));
			}

			qnas.get(x).addConcepto(con);
		    }

		}
	    }
	    else if (tipoGeneracion == 1)
	    {
		prodsTim = utilidades.getProductosAñoTim(this.año, (x + 1), this.incluirProductos,
			this.incluirCancelados, this.plazaSelec);

		if (prodsTim.isEmpty())
		{
		    continue;

		}

		for (ProductoTimbrado prod : prodsTim)
		{

		    System.out.println("Leyendo prodTim: " + prod.getIdProductoTimbrado());

		    prod.updateTimbres(false);

		    prod.updateConceptosTimbres(true, true, false);

		    bigPercepTotal = bigPercepTotal.add(prod.getTotalPercepciones());
		    bigDeducTotal = bigDeducTotal.add(prod.getTotalDeducciones());
		    this.totalRegistros += prod.getTotalRegistros();

		    BigDecimal tPer = new BigDecimal(0.00);
		    BigDecimal tDed = new BigDecimal(0.00);

		    for (ConceptoTimbre con : prod.getConceptosPercep())
		    {
			con.updateTotal();
			tPer = tPer.add(con.getValor());
			qnas.get(x).addConceptoTipoTimbre(con, 0);
		    }

		    for (ConceptoTimbre con : prod.getConceptosDeduc())
		    {
			con.updateTotal();
			tDed = tDed.add(con.getValor());
			qnas.get(x).addConceptoTipoTimbre(con, -1);
		    }

		    System.out.println(
			    "Percepciones: " + tPer + ", Deducciones: " + tDed + ", neto: " + tPer.subtract(tDed));

		}

	    }

	}

	this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
	this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
	this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

	SXSSFWorkbook libroExcel = new SXSSFWorkbook(50);

	List<BigDecimal> totales;

	int fila = 0;

	XSSFFont font = (XSSFFont) libroExcel.createFont();
	font.setBoldweight(Font.BOLDWEIGHT_BOLD);

	XSSFCellStyle style = (XSSFCellStyle) libroExcel.createCellStyle();
	style.setFont(font);

	XSSFCellStyle styleMoneda = (XSSFCellStyle) libroExcel.createCellStyle();
	styleMoneda.setDataFormat((short) 7);

	SXSSFSheet pagina = libroExcel.createSheet("PERCEPCIONES");
	pagina.trackAllColumnsForAutoSizing();
	fila = 0;
	int numCel = 0;

	Row filaEncabezado = pagina.createRow(fila);
	Cell c = filaEncabezado.createCell(numCel);
	c.setCellValue("CONCEPTO");
	c.setCellStyle(style);
	numCel++;

	Cell c1 = filaEncabezado.createCell(numCel);
	c1.setCellValue("PARTIDA ANTECEDENTE");
	c1.setCellStyle(style);
	numCel++;

	Cell c2 = filaEncabezado.createCell(numCel);
	c2.setCellValue("PARTIDA");
	c2.setCellStyle(style);
	numCel++;

	Cell c3 = filaEncabezado.createCell(numCel);
	c3.setCellValue("CUENTA CONTABLE");
	c3.setCellStyle(style);
	numCel++;

	Cell c4 = filaEncabezado.createCell(numCel);
	c4.setCellValue("DESCRIPCION");
	c4.setCellStyle(style);
	numCel++;

	for (int x = 0; x < 24; x++)
	{
	    Cell c5 = filaEncabezado.createCell(numCel);
	    c5.setCellValue("QNA. (" + (x + 1) + ")");
	    c5.setCellStyle(style);
	    numCel++;

	}

	Cell c6 = filaEncabezado.createCell(numCel);
	c6.setCellValue("TOTAL ANUAL");
	c6.setCellStyle(style);
	numCel++;

	Cell c7 = filaEncabezado.createCell(numCel);
	c7.setCellValue("NUMERO DE CASOS");
	c7.setCellStyle(style);
	numCel++;

	fila++;

	BigDecimal totalGeneralPercepciones = new BigDecimal("0.00");
	int totalNumeroCasosPercepciones = 0;

	for (Concepto con : catPercepciones)
	{

	    numCel = 0;

	    Row filaC = pagina.createRow(fila);
	    Cell cC = filaC.createCell(numCel);
	    cC.setCellValue(con.getClave());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c1C = filaC.createCell(numCel);
	    c1C.setCellValue(con.getPartidaAntecedente());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c2C = filaC.createCell(numCel);
	    c2C.setCellValue(con.getPartida());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c3C = filaC.createCell(numCel);
	    c3C.setCellValue("");

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c4C = filaC.createCell(numCel);
	    c4C.setCellValue(con.getDescripcion());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    BigDecimal totalConceptoBig = new BigDecimal("0.00");
	    int totalNumeroCasos = 0;

	    Concepto conFinded = null;

	    for (int x = 0; x < 24; x++)
	    {
		Cell c5 = filaC.createCell(numCel);
		c5.setCellStyle(styleMoneda);

		QuincenaDetalle qnaDet = qnas.get(x);
		conFinded = qnaDet.getConceptoPercep(con);

		pagina.autoSizeColumn(numCel);
		numCel++;

		if (conFinded == null)
		{

		    continue;
		}

		totalNumeroCasos += conFinded.getTotalCasos();

		c5.setCellValue(conFinded.getValor().doubleValue());
		totalConceptoBig = totalConceptoBig.add(conFinded.getValor());
		qnaDet.setTotalPercep(qnaDet.getTotalPercep().add(conFinded.getValor()));

		qnaDet.getConceptos().remove(conFinded);

	    }

	    Cell celTotalAnual = filaC.createCell(numCel);
	    celTotalAnual.setCellStyle(styleMoneda);

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell celNumeroCasosValor = filaC.createCell(numCel);

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    if (totalNumeroCasos > 0)
	    {
		celTotalAnual.setCellValue(totalConceptoBig.doubleValue());
		celNumeroCasosValor.setCellValue(totalNumeroCasos);
	    }
	    else
	    {
		celTotalAnual.setCellValue(0.00);
		celNumeroCasosValor.setCellValue(0);
	    }

	    totalNumeroCasosPercepciones += totalNumeroCasos;
	    totalGeneralPercepciones = totalGeneralPercepciones.add(totalConceptoBig);

	    fila++;

	}

	for (int x = 0; x < 24; x++)
	{
	    QuincenaDetalle qnaDet = qnas.get(x);

	    for (int y = 0; y < qnaDet.getConceptos().size(); y++)
	    {

		Concepto con = qnaDet.getConceptos().get(y);

		if (this.conceptosAdicional.get(
			"" + con.getTipoConcepto() + "-" + con.getClave() + "-" + con.getPartidaAntecedente()) == null)
		{
		    this.conceptosAdicional.put(
			    "" + con.getTipoConcepto() + "-" + con.getClave() + "-" + con.getPartidaAntecedente(), con);
		}

	    }

	}

	// Conceptos Adicionales
	for (Object co : this.conceptosAdicional.values())
	{
	    Concepto con = (Concepto) co;

	    numCel = 0;

	    Row filaC = pagina.createRow(fila);
	    Cell cC = filaC.createCell(numCel);
	    cC.setCellValue(con.getClave());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c1C = filaC.createCell(numCel);
	    c1C.setCellValue(con.getPartidaAntecedente());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    // Buscar la partida correspondiente de acuerdo al catálogo
	    for (Concepto cc : catPercepciones)
	    {
		if (cc.getClave().toLowerCase().trim().equalsIgnoreCase(con.getClave().toLowerCase().trim()))
		{
		    Cell c2C = filaC.createCell(numCel);
		    c2C.setCellValue(cc.getPartida());
		    break;
		}

	    }

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c3C = filaC.createCell(numCel);
	    c3C.setCellValue("");

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c4C = filaC.createCell(numCel);
	    c4C.setCellValue(con.getDescripcion());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Concepto conFinded = null;
	    BigDecimal totalConceptoBig = new BigDecimal("0.00");
	    int totalNumeroCasos = 0;

	    for (int x = 0; x < 24; x++)
	    {
		Cell c5 = filaC.createCell(numCel);
		c5.setCellStyle(styleMoneda);

		QuincenaDetalle qnaDet = qnas.get(x);
		conFinded = qnaDet.getConceptoPercep(con);

		pagina.autoSizeColumn(numCel);
		numCel++;

		if (conFinded == null)
		{
		    continue;
		}

		totalNumeroCasos += conFinded.getTotalCasos();

		c5.setCellValue(conFinded.getValor().doubleValue());
		totalConceptoBig = totalConceptoBig.add(conFinded.getValor());
		qnaDet.setTotalPercep(qnaDet.getTotalPercep().add(conFinded.getValor()));

		qnaDet.getConceptos().remove(conFinded);

	    }

	    Cell celTotalAnual = filaC.createCell(numCel);
	    celTotalAnual.setCellStyle(styleMoneda);

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell celNumeroCasosValor = filaC.createCell(numCel);

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    if (totalNumeroCasos > 0)
	    {
		celTotalAnual.setCellValue(totalConceptoBig.doubleValue());
		celNumeroCasosValor.setCellValue(totalNumeroCasos);
	    }
	    else
	    {
		celTotalAnual.setCellValue(0.00);
		celNumeroCasosValor.setCellValue(0);
	    }

	    totalNumeroCasosPercepciones += totalNumeroCasos;
	    totalGeneralPercepciones = totalGeneralPercepciones.add(totalConceptoBig);

	    fila++;

	}

	// totales
	numCel = 0;

	Row filaTotales = pagina.createRow(fila);
	Cell celC = filaTotales.createCell(numCel);
	celC.setCellValue("TOTALES");
	celC.setCellStyle(style);

	pagina.autoSizeColumn(numCel);
	numCel++;

	numCel++;

	numCel++;

	numCel++;

	numCel++;

	for (int x = 0; x < 24; x++)
	{
	    Cell c5 = filaTotales.createCell(numCel);
	    c5.setCellStyle(styleMoneda);

	    QuincenaDetalle qnaDet = qnas.get(x);

	    c5.setCellValue(qnaDet.getTotalPercep().doubleValue());
	    pagina.autoSizeColumn(numCel);
	    numCel++;

	}

	Cell cTotalPercepcionesGeneral = filaTotales.createCell(numCel);
	cTotalPercepcionesGeneral.setCellStyle(styleMoneda);

	cTotalPercepcionesGeneral.setCellValue(totalGeneralPercepciones.doubleValue());

	numCel++;

	Cell cTotalNumeroCasos = filaTotales.createCell(numCel);
	cTotalNumeroCasos.setCellValue(totalNumeroCasosPercepciones);

	SXSSFSheet pagina2 = libroExcel.createSheet("DEDUCCIONES");
	pagina2.trackAllColumnsForAutoSizing();

	fila = 0;
	numCel = 0;

	Row filaEncabezadop2 = pagina2.createRow(fila);
	Cell cp2 = filaEncabezadop2.createCell(numCel);
	cp2.setCellValue("CONCEPTO");
	cp2.setCellStyle(style);
	numCel++;

	Cell c1p2 = filaEncabezadop2.createCell(numCel);
	c1p2.setCellValue("PARTIDA ANTECEDENTE");
	c1p2.setCellStyle(style);
	numCel++;

	Cell c2p2 = filaEncabezadop2.createCell(numCel);
	c2p2.setCellValue("PARTIDA");
	c2p2.setCellStyle(style);
	numCel++;

	Cell c3p2 = filaEncabezadop2.createCell(numCel);
	c3p2.setCellValue("CUENTA CONTABLE");
	c3p2.setCellStyle(style);
	numCel++;

	Cell c4p2 = filaEncabezadop2.createCell(numCel);
	c4p2.setCellValue("DESCRIPCION");
	c4p2.setCellStyle(style);
	numCel++;

	for (int x = 0; x < 24; x++)
	{
	    Cell c5 = filaEncabezadop2.createCell(numCel);
	    c5.setCellValue("QNA. (" + (x + 1) + ")");
	    c5.setCellStyle(style);
	    numCel++;

	}

	Cell c6p2 = filaEncabezadop2.createCell(numCel);
	c6p2.setCellValue("TOTAL ANUAL");
	c6p2.setCellStyle(style);
	numCel++;

	Cell c7p2 = filaEncabezadop2.createCell(numCel);
	c7p2.setCellValue("NUMERO DE CASOS");
	c7p2.setCellStyle(style);
	numCel++;

	fila++;

	BigDecimal totalGeneralDeducciones = new BigDecimal("0.00");
	int totalNumeroCasosDeducciones = 0;

	for (Concepto con : catDeducciones)
	{

	    numCel = 0;

	    Row filaC = pagina2.createRow(fila);
	    Cell cC = filaC.createCell(numCel);
	    cC.setCellValue(con.getClave());

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    Cell c1C = filaC.createCell(numCel);
	    c1C.setCellValue(con.getPartidaAntecedente());

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    Cell c2C = filaC.createCell(numCel);
	    c2C.setCellValue(con.getPartida());

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    Cell c3C = filaC.createCell(numCel);
	    c3C.setCellValue("");

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    Cell c4C = filaC.createCell(numCel);
	    c4C.setCellValue(con.getDescripcion());

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    BigDecimal totalConceptoBig = new BigDecimal("0.00");
	    int totalNumeroCasos = 0;
	    Concepto conFinded = null;

	    for (int x = 0; x < 24; x++)
	    {
		QuincenaDetalle qnaDet = qnas.get(x);
		conFinded = qnaDet.getConceptoDeduc(con);

		Cell c5 = filaC.createCell(numCel);
		c5.setCellStyle(styleMoneda);

		pagina2.autoSizeColumn(numCel);
		numCel++;

		if (conFinded == null)
		{
		    continue;
		}

		totalNumeroCasos += conFinded.getTotalCasos();
		c5.setCellValue(conFinded.getValor().doubleValue());

		totalConceptoBig = totalConceptoBig.add(conFinded.getValor());

		qnaDet.setTotalDeduc(qnaDet.getTotalDeduc().add(conFinded.getValor()));
		qnaDet.getConceptosDeduc().remove(conFinded);

	    }

	    Cell celTotalAnual = filaC.createCell(numCel);
	    celTotalAnual.setCellStyle(styleMoneda);

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    Cell celNumeroCasosValor = filaC.createCell(numCel);

	    pagina2.autoSizeColumn(numCel);
	    numCel++;

	    if (totalNumeroCasos > 0)
	    {
		celTotalAnual.setCellValue(totalConceptoBig.doubleValue());
		celNumeroCasosValor.setCellValue(totalNumeroCasos);
	    }
	    else
	    {
		celTotalAnual.setCellValue(0.00);
		celNumeroCasosValor.setCellValue(0);
	    }

	    totalNumeroCasosDeducciones += totalNumeroCasos;
	    totalGeneralDeducciones = totalGeneralDeducciones.add(totalConceptoBig);

	    fila++;

	}

	// se realiza el ajuste de conceptos adicionales

	for (int x = 0; x < 24; x++)
	{
	    QuincenaDetalle qnaDet = qnas.get(x);

	    for (int y = 0; y < qnaDet.getConceptosDeduc().size(); y++)
	    {

		Concepto con = qnaDet.getConceptosDeduc().get(y);

		if (this.conceptosDeducAdicional.get(
			"" + con.getTipoConcepto() + "-" + con.getClave() + "-" + con.getPartidaAntecedente()) == null)
		{
		    this.conceptosDeducAdicional.put(
			    "" + con.getTipoConcepto() + "-" + con.getClave() + "-" + con.getPartidaAntecedente(), con);
		}

	    }

	}

	// Conceptos Adicionales de Deducciones
	for (Object co : this.conceptosDeducAdicional.values())
	{
	    Concepto con = (Concepto) co;

	    numCel = 0;

	    Row filaC = pagina2.createRow(fila);
	    Cell cC = filaC.createCell(numCel);
	    cC.setCellValue(con.getClave());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c1C = filaC.createCell(numCel);
	    c1C.setCellValue(con.getPartidaAntecedente());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    // Buscar la partida correspondiente de acuerdo al catálogo
	    for (Concepto cc : catDeducciones)
	    {
		if (cc.getClave().toLowerCase().trim().equalsIgnoreCase(con.getClave().toLowerCase().trim()))
		{
		    Cell c2C = filaC.createCell(numCel);
		    c2C.setCellValue(cc.getPartida());
		    break;
		}

	    }

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c3C = filaC.createCell(numCel);
	    c3C.setCellValue("");

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell c4C = filaC.createCell(numCel);
	    c4C.setCellValue(con.getDescripcion());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Concepto conFinded = null;
	    BigDecimal totalConceptoBig = new BigDecimal("0.00");
	    int totalNumeroCasos = 0;

	    for (int x = 0; x < 24; x++)
	    {
		Cell c5 = filaC.createCell(numCel);
		c5.setCellStyle(styleMoneda);

		QuincenaDetalle qnaDet = qnas.get(x);
		conFinded = qnaDet.getConceptoDeduc(con);

		pagina.autoSizeColumn(numCel);
		numCel++;

		if (conFinded == null)
		{
		    continue;
		}

		totalNumeroCasos += conFinded.getTotalCasos();

		c5.setCellValue(conFinded.getValor().doubleValue());

		totalConceptoBig = totalConceptoBig.add(conFinded.getValor());
		qnaDet.setTotalDeduc(qnaDet.getTotalDeduc().add(conFinded.getValor()));

		qnaDet.getConceptosDeduc().remove(con);

	    }

	    Cell celTotalAnual = filaC.createCell(numCel);
	    celTotalAnual.setCellStyle(styleMoneda);

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    Cell celNumeroCasosValor = filaC.createCell(numCel);

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	    if (totalNumeroCasos > 0)
	    {
		celTotalAnual.setCellValue(totalConceptoBig.doubleValue());
		celNumeroCasosValor.setCellValue(totalNumeroCasos);
	    }
	    else
	    {
		celTotalAnual.setCellValue(0.00);
		celNumeroCasosValor.setCellValue(0);
	    }

	    totalNumeroCasosDeducciones += totalNumeroCasos;
	    totalGeneralDeducciones = totalGeneralDeducciones.add(totalConceptoBig);

	    fila++;

	}

	// totales
	numCel = 0;

	Row filaTotales2 = pagina2.createRow(fila);

	Cell cC = filaTotales2.createCell(numCel);
	cC.setCellValue("TOTALES");
	cC.setCellStyle(style);
	numCel++;

	numCel++;

	numCel++;

	numCel++;

	numCel++;

	for (int x = 0; x < 24; x++)
	{
	    Cell c5 = filaTotales2.createCell(numCel);
	    c5.setCellStyle(styleMoneda);

	    QuincenaDetalle qnaDet = qnas.get(x);

	    c5.setCellValue(qnaDet.getTotalDeduc().doubleValue());

	    pagina.autoSizeColumn(numCel);
	    numCel++;

	}

	Cell cTotalDeduccionesGeneral = filaTotales2.createCell(numCel);
	cTotalDeduccionesGeneral.setCellStyle(styleMoneda);

	cTotalDeduccionesGeneral.setCellValue(totalGeneralDeducciones.doubleValue());
	numCel++;

	Cell cTotalNumeroCasosDeducciones = filaTotales2.createCell(numCel);
	cTotalNumeroCasosDeducciones.setCellValue(totalNumeroCasosDeducciones);

	fila++;
	/*
	 * for (int x = 0; x < libroExcel.getNumberOfSheets(); x++) { for (int y = 0; y
	 * < 50; y++) { libroExcel.getSheetAt(x).autoSizeColumn(y); } }
	 */
	try
	{
	    File f = new File("#{resource['images:temp1.txt']}");

	    FileOutputStream out = new FileOutputStream(f);
	    libroExcel.write(out);
	    out.close();

	    String plazaDescripcion = "";

	    for (Plaza pl : this.plazas)
	    {
		if (pl.getIdPlaza() == this.plazaSelec)
		{
		    plazaDescripcion = pl.getDescripcionPlaza();
		    break;
		}
	    }

	    String prodCan = "";

	    if (this.incluirPension)
	    {
		prodCan = "con pensión";
	    }

	    if (this.incluirProductos)
	    {
		prodCan = "(Productos)";
	    }

	    if (this.incluirCancelados)
	    {
		if (this.incluirProductos)
		{
		    prodCan = prodCan.substring(0, prodCan.length() - 1) + " y Cancelados)";
		}
		else
		{
		    prodCan = "(Cancelados)";
		}

	    }

	    String incluye = "incluye";

	    if (this.incluir416)
	    {
		incluye += " 416 -";
	    }

	    if (this.incluirU00)
	    {
		incluye += " U00 -";
	    }

	    if (this.incluirU01)
	    {
		incluye += " U01 -";
	    }

	    if (this.incluir610)
	    {
		incluye += " 610 - ";
	    }

	    if (this.incluir416 || this.incluirU00 || this.incluirU01 || this.incluir610)
	    {
		incluye = incluye.substring(0, incluye.length() - 2);
	    }

	    txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
		    "PerDed-" + plazaDescripcion + " " + this.año + " " + prodCan + " " + incluye + ".xls");

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public void actionDescargarFlujo()
    {

	this.bigPercepTotal = new BigDecimal(0);
	this.bigDeducTotal = new BigDecimal(0);

	this.conceptos = new ArrayList<>();
	this.conceptosDeduc = new ArrayList<>();
	this.conceptosAdicional = new HashMap();
	this.conceptosDeducAdicional = new HashMap<>();

	this.totalRegistros = 0;

	catPercepciones = utilidades.getConceptos(1);
	catDeducciones = utilidades.getConceptos(2);

	// Se crean los objetos correspondientes para cada quincena del año
	qnas = new ArrayList<>();

	for (int x = 0; x < 24; x++)
	{
	    qnas.add(new QuincenaDetalle(this.año, (x + 1)));

	}

	// Se obtienen los productos de cada quincena y se van acumulando
	// los
	// conceptos
	List<Producto> prods;

	for (int x = 0; x < 24; x++)
	{

	    prods = utilidades.getProductosAño(this.año, (x + 1), this.incluirProductos, this.incluirCancelados,
		    this.plazaSelec, true);

	    for (Producto prod : prods)
	    {
		System.out.println("Leyendo prod: " + prod.getNombreProducto());
		prod.updateRegistrosTRAConConceptos(this.incluir416, this.incluirU00, this.incluirU01, this.incluir610,
			true, false);

		bigPercepTotal = bigPercepTotal.add(prod.getBigPercepTotal());
		bigDeducTotal = bigDeducTotal.add(prod.getBigDeducTotal());
		this.totalRegistros += prod.getTotalRegistros();

		for (Concepto con : prod.getConceptos())
		{
		    qnas.get(x).addConceptoExcepcion03(con);
		}

		for (Concepto con : prod.getConceptosDeduc())
		{
		    qnas.get(x).addConcepto(con);
		}

	    }

	}

	this.totalPercepiones = utilidades.formato.format(bigPercepTotal);
	this.totalDeducciones = utilidades.formato.format(bigDeducTotal);
	this.totalLiquido = utilidades.formato.format(bigPercepTotal.subtract(bigDeducTotal));

	Workbook libroExcel = new HSSFWorkbook();
	List<BigDecimal> totales;

	int fila = 0;

	CellStyle style = libroExcel.createCellStyle();

	Font font = libroExcel.createFont();
	font.setBoldweight(Font.BOLDWEIGHT_BOLD);
	style.setFont(font);

	Sheet pagina = libroExcel.createSheet("" + this.getPlazaSelec());
	fila = 0;
	int numCel = 0;

	Row filaEncabezado = pagina.createRow(fila);
	Cell c8000 = filaEncabezado.createCell(numCel);
	c8000.setCellValue("Capítulo 8000");
	c8000.setCellStyle(style);
	numCel++;

	Cell c = filaEncabezado.createCell(numCel);
	c.setCellValue("Partida Específica");
	c.setCellStyle(style);
	numCel++;

	Cell c1 = filaEncabezado.createCell(numCel);
	c1.setCellValue("Concepto");
	c1.setCellStyle(style);
	numCel++;

	Cell c2 = filaEncabezado.createCell(numCel);
	c2.setCellValue("Autorizado");
	c2.setCellStyle(style);
	numCel++;

	Cell c3 = filaEncabezado.createCell(numCel);
	c3.setCellValue("Red / Amp");
	c3.setCellStyle(style);
	numCel++;

	Cell c4 = filaEncabezado.createCell(numCel);
	c4.setCellValue("Modificado");
	c4.setCellStyle(style);
	numCel++;

	Cell cEnero = filaEncabezado.createCell(numCel);
	cEnero.setCellValue("ENERO");
	cEnero.setCellStyle(style);
	numCel++;

	Cell cFebrero = filaEncabezado.createCell(numCel);
	cFebrero.setCellValue("FEBRERO");
	cFebrero.setCellStyle(style);
	numCel++;

	Cell cMarzo = filaEncabezado.createCell(numCel);
	cMarzo.setCellValue("MARZO");
	cMarzo.setCellStyle(style);
	numCel++;

	Cell cAbril = filaEncabezado.createCell(numCel);
	cAbril.setCellValue("ABRIL");
	cAbril.setCellStyle(style);
	numCel++;

	Cell cMayo = filaEncabezado.createCell(numCel);
	cMayo.setCellValue("MAYO");
	cMayo.setCellStyle(style);
	numCel++;

	Cell cJunio = filaEncabezado.createCell(numCel);
	cJunio.setCellValue("JUNIO");
	cJunio.setCellStyle(style);
	numCel++;

	Cell cJulio = filaEncabezado.createCell(numCel);
	cJulio.setCellValue("JULIO");
	cJulio.setCellStyle(style);
	numCel++;

	Cell cAgosto = filaEncabezado.createCell(numCel);
	cAgosto.setCellValue("AGOSTO");
	cAgosto.setCellStyle(style);
	numCel++;

	Cell cSeptiembre = filaEncabezado.createCell(numCel);
	cSeptiembre.setCellValue("SEPTIEMBRE");
	cSeptiembre.setCellStyle(style);
	numCel++;

	Cell cOctubre = filaEncabezado.createCell(numCel);
	cOctubre.setCellValue("OCTUBRE");
	cOctubre.setCellStyle(style);
	numCel++;

	Cell cNoviembre = filaEncabezado.createCell(numCel);
	cNoviembre.setCellValue("NOVIEMBRE");
	cNoviembre.setCellStyle(style);
	numCel++;

	Cell cDiciembre = filaEncabezado.createCell(numCel);
	cDiciembre.setCellValue("DICIEMBRE");
	cDiciembre.setCellStyle(style);
	numCel++;

	Cell cTotal = filaEncabezado.createCell(numCel);
	cTotal.setCellValue("TOTAL");
	cTotal.setCellStyle(style);
	numCel++;

	Cell cDisponibilidad = filaEncabezado.createCell(numCel);
	cDisponibilidad.setCellValue("DISPONIBILIDAD");
	cDisponibilidad.setCellStyle(style);
	numCel++;

	numCel = 0;
	fila++;

	Row filaUno = pagina.createRow(fila);
	Cell cSueldoBase0 = filaUno.createCell(numCel);
	cSueldoBase0.setCellValue("83101");
	cSueldoBase0.setCellStyle(style);
	numCel++;

	Cell cSueldoBase1 = filaUno.createCell(numCel);
	cSueldoBase1.setCellValue("11301");
	cSueldoBase1.setCellStyle(style);
	numCel++;

	Cell cSueldoBase2 = filaUno.createCell(numCel);
	cSueldoBase2.setCellValue("Sueldo Base");
	cSueldoBase2.setCellStyle(style);
	numCel++;

	Cell cSueldoBase3 = filaUno.createCell(numCel);
	cSueldoBase3.setCellValue("286,863,564.00");
	cSueldoBase3.setCellStyle(style);
	numCel++;

	Cell cSueldoBase4 = filaUno.createCell(numCel);
	cSueldoBase4.setCellValue("");
	cSueldoBase4.setCellStyle(style);
	numCel++;

	Cell cSueldoBase5 = filaUno.createCell(numCel);
	cSueldoBase5.setCellValue("286,863,564.00");
	cSueldoBase5.setCellStyle(style);
	numCel++;

	QuincenaDetalle qnaEnero1 = qnas.get(0);
	Concepto conFinded = qnaEnero1
		.getConceptoPercep(new Concepto(1, "07", "Sueldo Base", "00", "00", new BigDecimal("0.00"), true));

	QuincenaDetalle qnaEnero2 = qnas.get(1);
	Concepto conFinded1 = qnaEnero2
		.getConceptoPercep(new Concepto(1, "07", "Sueldo Base", "00", "00", new BigDecimal("0.00"), true));

	BigDecimal EneroSueldoBase = new BigDecimal("0.00");

	if (conFinded == null || conFinded1 == null)
	{
	    if (conFinded == null)
	    {
		EneroSueldoBase = conFinded1.getValor();
	    }
	    if (conFinded1 == null)
	    {
		EneroSueldoBase = conFinded.getValor();
	    }
	}
	else
	{
	    EneroSueldoBase = conFinded.getValor().add(conFinded1.getValor());
	}

	Cell cSueldoBaseEnero = filaUno.createCell(numCel);
	cSueldoBaseEnero
		.setCellValue(utilidades.formatoMoneda(EneroSueldoBase.toString().replace(",", "").replace("$", "")));
	cSueldoBaseEnero.setCellStyle(style);
	numCel++;

	QuincenaDetalle qnaFeberero1 = qnas.get(2);
	Concepto conFindedFeb = qnaFeberero1
		.getConceptoPercep(new Concepto(1, "07", "Sueldo Base", "00", "00", new BigDecimal("0.00"), true));

	QuincenaDetalle qnaFeberero2 = qnas.get(3);
	Concepto conFindedFeb1 = qnaFeberero2
		.getConceptoPercep(new Concepto(1, "07", "Sueldo Base", "00", "00", new BigDecimal("0.00"), true));

	BigDecimal FebreroSueldoBase = new BigDecimal("0.00");

	if (conFindedFeb == null || conFindedFeb1 == null)
	{
	    if (conFindedFeb == null)
	    {
		FebreroSueldoBase = conFindedFeb1.getValor();
	    }
	    if (conFindedFeb1 == null)
	    {
		FebreroSueldoBase = conFindedFeb.getValor();
	    }

	}
	else
	{
	    FebreroSueldoBase = conFindedFeb.getValor().add(conFindedFeb1.getValor());
	}

	Cell cSueldoBaseFebrero = filaUno.createCell(numCel);
	cSueldoBaseFebrero
		.setCellValue(utilidades.formatoMoneda(FebreroSueldoBase.toString().replace(",", "").replace("$", "")));
	cSueldoBaseFebrero.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseMarzo = filaUno.createCell(numCel);
	cSueldoBaseMarzo.setCellValue("");
	cSueldoBaseMarzo.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseAbril = filaUno.createCell(numCel);
	cSueldoBaseAbril.setCellValue("");
	cSueldoBaseAbril.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseMayo = filaUno.createCell(numCel);
	cSueldoBaseMayo.setCellValue("");
	cSueldoBaseMayo.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseJunio = filaUno.createCell(numCel);
	cSueldoBaseJunio.setCellValue("");
	cSueldoBaseJunio.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseJulio = filaUno.createCell(numCel);
	cSueldoBaseJulio.setCellValue("");
	cSueldoBaseJulio.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseAgosto = filaUno.createCell(numCel);
	cSueldoBaseAgosto.setCellValue("");
	cSueldoBaseAgosto.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseSeptiembre = filaUno.createCell(numCel);
	cSueldoBaseSeptiembre.setCellValue("");
	cSueldoBaseSeptiembre.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseOctubre = filaUno.createCell(numCel);
	cSueldoBaseOctubre.setCellValue("");
	cSueldoBaseOctubre.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseNoviembre = filaUno.createCell(numCel);
	cSueldoBaseNoviembre.setCellValue("");
	cSueldoBaseNoviembre.setCellStyle(style);
	numCel++;

	Cell cSueldoBaseDiciembre = filaUno.createCell(numCel);
	cSueldoBaseDiciembre.setCellValue("");
	cSueldoBaseDiciembre.setCellStyle(style);
	numCel++;

	for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
	{
	    for (int y = 0; y < 50; y++)
	    {
		libroExcel.getSheetAt(x).autoSizeColumn(y);
	    }
	}

	try
	{
	    File f = new File("#{resource['images:temp1.txt']}");

	    FileOutputStream out = new FileOutputStream(f);
	    libroExcel.write(out);
	    out.close();

	    String plazaDescripcion = "";

	    for (Plaza pl : this.plazas)
	    {
		if (pl.getIdPlaza() == this.plazaSelec)
		{
		    plazaDescripcion = pl.getDescripcionPlaza();
		    break;
		}
	    }

	    String exclusion = " con ";

	    if (!this.excluirUnidades)
	    {
		exclusion = " sin ";
	    }

	    exclusion += "Filtro Unidades Proyecto";

	    String soloCanc = "";

	    if (this.incluirCancelados)
	    {
		soloCanc = " (Cancelados)";
	    }

	    String prodCan = "";

	    if (this.incluirProductos)
	    {
		prodCan = "(Productos)";
	    }

	    if (this.incluirCancelados)
	    {
		if (this.incluirProductos)
		{
		    prodCan = prodCan.substring(0, prodCan.length() - 1) + " y Cancelados)";
		}
		else
		{
		    prodCan = "(Cancelados)";
		}

	    }

	    String incluye = "incluye ";

	    if (this.incluir416)
	    {
		incluye += " 416 -";
	    }

	    if (this.incluirU00)
	    {
		incluye += " U00 -";
	    }

	    if (this.incluir610)
	    {
		incluye += " 610 - ";
	    }

	    if (this.incluir416 || this.incluirU00 || this.incluir610)
	    {
		incluye = incluye.substring(0, incluye.length() - 2);
	    }

	    txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
		    "PerDed-" + plazaDescripcion + " " + this.año + " " + prodCan + " " + incluye + ".xls");

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    public Plaza getPlaza()
    {
	return plaza;
    }

    public void setPlaza(Plaza plaza)
    {
	this.plaza = plaza;
    }

    public int getAño()
    {
	return año;
    }

    public void setAño(int año)
    {
	this.año = año;
    }

    public TipoProducto getTipoProducto()
    {
	return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto)
    {
	this.tipoProducto = tipoProducto;
    }

    public List<Plaza> getPlazas()
    {
	return plazas;
    }

    public void setPlazas(List<Plaza> plazas)
    {
	this.plazas = plazas;
    }

    public int getPlazaSelec()
    {
	return plazaSelec;
    }

    public void setPlazaSelec(int plazaSelec)
    {
	this.plazaSelec = plazaSelec;
    }

    public List<TipoProducto> getTiposProducto()
    {
	return tiposProducto;
    }

    public void setTiposProducto(List<TipoProducto> tiposProducto)
    {
	this.tiposProducto = tiposProducto;
    }

    public int getTipoProductoSelec()
    {
	return tipoProductoSelec;
    }

    public void setTipoProductoSelec(int tipoProductoSelec)
    {
	this.tipoProductoSelec = tipoProductoSelec;
    }

    public static long getSerialversionuid()
    {
	return serialVersionUID;
    }

    public StreamedContent getTxt()
    {
	return txt;
    }

    public void setTxt(StreamedContent txt)
    {
	this.txt = txt;
    }

    public boolean isExcluirUnidades()
    {
	return excluirUnidades;
    }

    public void setExcluirUnidades(boolean excluirUnidades)
    {
	this.excluirUnidades = excluirUnidades;
    }

    public boolean isIncluir416()
    {
	return incluir416;
    }

    public void setIncluir416(boolean incluir416)
    {
	this.incluir416 = incluir416;
    }

    public boolean isIncluirU00()
    {
	return incluirU00;
    }

    public void setIncluirU00(boolean incluirU00)
    {
	this.incluirU00 = incluirU00;
    }

    public boolean isIncluirProductos()
    {
	return incluirProductos;
    }

    public void setIncluirProductos(boolean incluirProductos)
    {
	this.incluirProductos = incluirProductos;
    }

    public boolean isIncluirCancelados()
    {
	return incluirCancelados;
    }

    public void setIncluirCancelados(boolean incluirCancelados)
    {
	this.incluirCancelados = incluirCancelados;
    }

    public boolean isIncluir610()
    {
	return incluir610;
    }

    public void setIncluir610(boolean incluir610)
    {
	this.incluir610 = incluir610;
    }

    public boolean isIncluirU01()
    {
	return incluirU01;
    }

    public void setIncluirU01(boolean incluirU01)
    {
	this.incluirU01 = incluirU01;
    }

    public boolean isIncluirPension()
    {
	return incluirPension;
    }

    public void setIncluirPension(boolean incluirPension)
    {
	this.incluirPension = incluirPension;
    }

}
