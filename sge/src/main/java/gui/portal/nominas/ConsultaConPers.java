package gui.portal.nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import modelo.Columna;
import modelo.Concepto;
import modelo.PeriodoQuincenal;
import modelo.Plaza;
import modelo.Quincena;
import modelo.Unidad;
import modelo.UnidadQuincena;
import util.utilidades;

@SessionScoped
@ManagedBean
public class ConsultaConPers implements Serializable
{
	private List<Integer>	años;
	private int				añoSeleccionado;
	private int				idPlaza;
	private List<Unidad>	unidades;
	private List<Unidad>	unidadesSelec;
	private List<Integer>	etapas;

	private List<Integer>	etapasIncluidas;

	private List<Plaza>		catPlazas;

	private List<Columna>	columnasReporte;

	private List<Concepto>	catConceptos;
	private List<Concepto>	catConceptosFilter;
	private List<Concepto>	conceptosSelec;

	private Columna			columnaSeleccionandoConceptos;

	private StreamedContent	txt;

	public ConsultaConPers()
	{
		setAñosDisponibles();
		this.columnasReporte = new ArrayList<>();

		this.etapas = new ArrayList<>();
		this.etapas.add(1);
		this.etapas.add(2);

		this.catPlazas = utilidades.getPlazas();
		this.unidades = utilidades.getUnidades();

		this.catConceptos = utilidades.getConceptos();

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

	public void prepararSeleccionConceptos(Columna col)
	{
		this.columnaSeleccionandoConceptos = col;
	}

	public void addColumnaVacia()
	{
		this.columnasReporte.add(new Columna(-1, null, this.columnasReporte.size() + 1, false,
				"" + this.columnasReporte.size() + 1, "", 0));
	}

	public void removerColumna(Columna col)
	{
		this.columnasReporte.remove(col);

		// Se reenumera el orden de las columnas
		for (int x = 0; x < this.columnasReporte.size(); x++)
		{
			this.columnasReporte.get(x).setOrden((x + 1));

		}

	}

	public void addConceptoColumna()
	{
		// Se añaden a la columna los conceptos seleccionados
		this.columnaSeleccionandoConceptos.setConceptosIncluidos(this.conceptosSelec);

		// Se pasa a string para poder visualizar en la tabla
		String conceptosString = "";

		for (Concepto con : this.columnaSeleccionandoConceptos.getConceptosIncluidos())
		{
			conceptosString += "[" + con.getTipoConcepto() + "" + con.getClave() + "-" + con.getPartidaAntecedente()
					+ "  ] \n";

		}

		this.columnaSeleccionandoConceptos.setConceptosSelecString(conceptosString);

	}

	// 0 bajar, 1 subir
	public void moverColumna(Columna col, int opcion)
	{

		for (int x = 0; x < this.columnasReporte.size(); x++)
		{

			if (this.columnasReporte.get(x).equals(col))
			{

				switch (opcion)
				{
					case 0:

						if (x > 0)
						{
							// Se intercambian las posiciones de la columna
							Columna colAbajo = this.columnasReporte.get((x - 1));
							Columna columna = this.columnasReporte.get(x);

							this.columnasReporte.set((x - 1), columna);
							this.columnasReporte.set(x, colAbajo);

							colAbajo.setOrden(colAbajo.getOrden() + 1);
							columna.setOrden(columna.getOrden() - 1);

						}

						break;

					case 1:

						if (x < this.columnasReporte.size() - 1)
						{
							Columna colArriba = this.columnasReporte.get((x + 1));
							Columna columna = this.columnasReporte.get(x);

							this.columnasReporte.set((x + 1), columna);
							this.columnasReporte.set(x, colArriba);

							colArriba.setOrden(colArriba.getOrden() - 1);
							columna.setOrden(columna.getOrden() + 1);

						}

						break;

				}

				break;

			}

		}

	}

	public void actionDescargarReporte()
	{

		// Se crea el objeto principal de periodoquincena
		PeriodoQuincenal objPeriodo = new PeriodoQuincenal();
		Plaza plazaSelec = null;

		for (Plaza pl : this.catPlazas)
		{
			if (pl.getIdPlaza() == this.idPlaza)
			{
				plazaSelec = pl;
				break;
			}
		}

		// Se crean las quincenas del periodo
		for (int x = 0; x < 23; x++)
		{
			System.out.println("Leyendo quincena " + (x + 1));

			Quincena qna = new Quincena(this.añoSeleccionado, (x + 1), plazaSelec);
			qna.addUnidades(this.unidadesSelec);

			qna.updateProductos();
			qna.updateCancelados();

			qna.updateDATsProductos();
			qna.updateDATsCancelados();

			qna.filtraDATUnidadesProductos();
			qna.filtraDATUnidadesCancelados();

			qna.addColumnasReporte(this.columnasReporte);

			qna.procesaColumnas();

			objPeriodo.addQuincena(qna);

		}

		try
		{
			Workbook libroExcel = new HSSFWorkbook();

			int fila = 0;

			CellStyle style = libroExcel.createCellStyle();

			Font font = libroExcel.createFont();
			font.setBold(true);
			style.setFont(font);

			HSSFCellStyle estiloMoneda = (HSSFCellStyle) libroExcel.createCellStyle();
			HSSFDataFormat df = (HSSFDataFormat) libroExcel.createDataFormat();
			estiloMoneda.setDataFormat(df.getFormat("$#,##0.00"));

			Sheet pagina = libroExcel.createSheet("Hoja 1");
			fila = 0;
			int numCel = 0;

			Row filaEncabezado = pagina.createRow(fila);
			Cell c = filaEncabezado.createCell(numCel);
			c.setCellValue("TIPO NOMINA");
			c.setCellStyle(style);
			numCel++;

			Cell c1 = filaEncabezado.createCell(numCel);
			c1.setCellValue("QNA");
			c1.setCellStyle(style);
			numCel++;

			for (Columna col : this.columnasReporte)
			{

				Cell colCelda = filaEncabezado.createCell(numCel);
				colCelda.setCellValue("" + col.getDescripcion());
				colCelda.setCellStyle(style);
				numCel++;

			}

			for (Quincena qna : objPeriodo.getQuincenas())
			{

				for (UnidadQuincena unid : qna.getUnidades())
				{
					fila++;
					Row filaDetalle = pagina.createRow(fila);
					numCel = 0;

					Cell celTipoNomina = filaDetalle.createCell(numCel);
					celTipoNomina.setCellValue(unid.getDescripcion().toUpperCase());
					numCel++;

					String qnaString = qna.getnQuincena() < 10 ? "0" + qna.getnQuincena() : "" + qna.getnQuincena();

					Cell celQna = filaDetalle.createCell(numCel);
					celQna.setCellValue("" + qna.getAño() + qnaString);
					numCel++;

					for (Columna col : unid.getColumnas())
					{

						Cell colCelda = filaDetalle.createCell(numCel);
						colCelda.setCellStyle(estiloMoneda);
						colCelda.setCellType(Cell.CELL_TYPE_NUMERIC);

						// Si no hay fórmula añade el total de la unidad,
						// dependiendo de si se incluye productos o cheques y si
						// se descuenta pensión
						if (col.getConceptosIncluidos() == null || col.getConceptosIncluidos().isEmpty())
						{
							BigDecimal percepsTotal = new BigDecimal("0.00");

							if (col.isProductos())
							{
								percepsTotal = percepsTotal.add(unid.getPercepciones());

								if (col.isPension())
								{
									percepsTotal = percepsTotal.add(unid.getPercepcionesPension());

								}

							}

							if (col.isCancelados())
							{
								percepsTotal = percepsTotal.add(unid.getPercepcionesCancelados());

								if (col.isPension())
								{
									percepsTotal = percepsTotal.add(unid.getPercepcionesCanceladosPension());
								}

							}
							colCelda.setCellValue(percepsTotal.doubleValue());

						}
						else
						{
							colCelda.setCellValue(col.getTotal().doubleValue());
						}

						
						numCel++;

					}

				}

			}

			fila++;

			numCel = 1;

			Row filaTotales = pagina.createRow(fila);
			Cell cCeldaTotal = filaTotales.createCell(numCel);
			cCeldaTotal.setCellValue("TOTALES");
			cCeldaTotal.setCellStyle(style);
			numCel++;

			for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
			{
				for (int y = 0; y < this.columnasReporte.size() + 2; y++)
				{
					libroExcel.getSheetAt(x).autoSizeColumn(y);
				}
			}

			File f = new File("#{resource['images:temp1.txt']}");

			FileOutputStream out = new FileOutputStream(f);
			libroExcel.write(out);
			out.close();

			String plazaDescripcion = "";

			for (Plaza pl : this.catPlazas)
			{
				if (pl.getIdPlaza() == this.idPlaza)
				{
					plazaDescripcion = pl.getDescripcionPlaza();
					break;
				}
			}

			txt = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
					"Reporte personalizado -" + plazaDescripcion + " " + this.añoSeleccionado + ".xls");

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public List<Columna> getColumnasReporte()
	{
		return columnasReporte;
	}

	public void setColumnasReporte(List<Columna> columnasReporte)
	{
		this.columnasReporte = columnasReporte;
	}

	public List<Unidad> getUnidades()
	{
		return unidades;
	}

	public void setUnidades(List<Unidad> unidades)
	{
		this.unidades = unidades;
	}

	public List<Integer> getEtapas()
	{
		return etapas;
	}

	public void setEtapas(List<Integer> etapas)
	{
		this.etapas = etapas;
	}

	public List<Plaza> getCatPlazas()
	{
		return catPlazas;
	}

	public void setCatPlazas(List<Plaza> catPlazas)
	{
		this.catPlazas = catPlazas;
	}

	public int getIdPlaza()
	{
		return idPlaza;
	}

	public void setIdPlaza(int idPlaza)
	{
		this.idPlaza = idPlaza;
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

	public List<Unidad> getUnidadesSelec()
	{
		return unidadesSelec;
	}

	public void setUnidadesSelec(List<Unidad> unidadesSelec)
	{
		this.unidadesSelec = unidadesSelec;
	}

	public List<Integer> getEtapasIncluidas()
	{
		return etapasIncluidas;
	}

	public void setEtapasIncluidas(List<Integer> etapasIncluidas)
	{
		this.etapasIncluidas = etapasIncluidas;
	}

	public List<Concepto> getCatConceptos()
	{
		return catConceptos;
	}

	public void setCatConceptos(List<Concepto> catConceptos)
	{
		this.catConceptos = catConceptos;
	}

	public List<Concepto> getCatConceptosFilter()
	{
		return catConceptosFilter;
	}

	public void setCatConceptosFilter(List<Concepto> catConceptosFilter)
	{
		this.catConceptosFilter = catConceptosFilter;
	}

	public List<Concepto> getConceptosSelec()
	{
		return conceptosSelec;
	}

	public void setConceptosSelec(List<Concepto> conceptosSelec)
	{
		this.conceptosSelec = conceptosSelec;
	}

	public Columna getColumnaSeleccionandoConceptos()
	{
		return columnaSeleccionandoConceptos;
	}

	public void setColumnaSeleccionandoConceptos(Columna columnaSeleccionandoConceptos)
	{
		this.columnaSeleccionandoConceptos = columnaSeleccionandoConceptos;
	}

	public StreamedContent getTxt()
	{
		return txt;
	}

	public void setTxt(StreamedContent txt)
	{
		this.txt = txt;
	}

}
