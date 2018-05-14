package gui.portal.nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import modelo.Concepto;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.QuincenaDetalle;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class PlantillasBean implements Serializable
{
	private List<Integer>	años;
	private int				añoSeleccionado;
	private int				qnaSeleccionado;

	private List<Plaza>		plazas;
	private Plaza			plazaSeleccionada;

	private StreamedContent	xlsPlantilla;

	private boolean			incluir416;
	private boolean			incluirU00;
	private boolean			incluir610;

	public PlantillasBean()
	{
		super();
		setAñosDisponibles();
		this.plazas = utilidades.getPlazas();
		this.qnaSeleccionado = 1;
		// TODO Auto-generated constructor stub
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

	public void actionDescargarPlantilla()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;
		Map empleados = new HashMap<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{

			List<Producto> prods;
			String numEmpleado = "";

			prods = utilidades.getProductos(this.qnaSeleccionado, this.qnaSeleccionado, this.añoSeleccionado,
					this.plazaSeleccionada.getIdPlaza());

			for (Producto prod : prods)
			{

				if (!(prod.getTipoNomina().getIdTipoNomina() == 0 && prod.getTipoProducto().getIdTipoProducto() == 0))
				{
					continue;
				}

				System.out.println("Leyendo prod: " + prod.getNombreProducto());

				prod.getRegistrosDATFiltrados(this.incluir416, this.incluirU00, this.incluir610);

				for (PlantillaRegistro regDAT : prod.getRegistrosDAT())
				{
					numEmpleado = regDAT.getValorPorDescripcionContains("mero de emplea").trim();

					if (!empleados.containsKey(numEmpleado))
					{
						empleados.put(numEmpleado, regDAT);

					}

				}

			}

			Workbook libroExcel = new HSSFWorkbook();
			List<BigDecimal> totales;

			int fila = 0;

			CellStyle style = libroExcel.createCellStyle();

			Font font = libroExcel.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			style.setFont(font);

			Sheet pagina = libroExcel.createSheet("PLANTILLA " + this.qnaSeleccionado + "-" + this.añoSeleccionado);
			fila = 0;
			int numCel = 0;

			Row filaEncabezado = pagina.createRow(fila);
			Cell c = filaEncabezado.createCell(numCel);
			c.setCellValue("RFC");
			c.setCellStyle(style);
			numCel++;

			Cell c1 = filaEncabezado.createCell(numCel);
			c1.setCellValue("NOMBRE");
			c1.setCellStyle(style);
			numCel++;

			Cell c2 = filaEncabezado.createCell(numCel);
			c2.setCellValue("CLAVE PRESUPUESTAL");
			c2.setCellStyle(style);
			numCel++;

			Cell c3 = filaEncabezado.createCell(numCel);
			c3.setCellValue("CLV ADSCRIPCION");
			c3.setCellStyle(style);
			numCel++;

			Cell c4 = filaEncabezado.createCell(numCel);
			c4.setCellValue("ADSCRIPCION");
			c4.setCellStyle(style);
			numCel++;

			Cell c5 = filaEncabezado.createCell(numCel);
			c5.setCellValue("PERCEPCIONES");
			c5.setCellStyle(style);
			numCel++;

			Cell c6 = filaEncabezado.createCell(numCel);
			c6.setCellValue("DEDUCCIONES");
			c6.setCellStyle(style);
			numCel++;

			Cell c7 = filaEncabezado.createCell(numCel);
			c7.setCellValue("NETO");
			c7.setCellStyle(style);
			numCel++;

			Map empleadosByRFC = utilidades.sortDATByRFC((HashMap) empleados);

			Set set = empleadosByRFC.entrySet();
			Iterator iterator = set.iterator();

			while (iterator.hasNext())
			{
				Map.Entry me = (Map.Entry) iterator.next();

				PlantillaRegistro reg = (PlantillaRegistro) me.getValue();

				fila++;
				numCel = 0;

				Row filaSiguiente = pagina.createRow(fila);

				Cell celdaRFC = filaSiguiente.createCell(numCel);
				celdaRFC.setCellValue(reg.getValorPorDescripcionContains("RFC"));
				celdaRFC.setCellStyle(style);
				numCel++;

				Cell celdaNombre = filaSiguiente.createCell(numCel);
				celdaNombre.setCellValue(reg.getValorPorDescripcionContains("nombre"));
				celdaNombre.setCellStyle(style);
				numCel++;

				Cell celdaCP = filaSiguiente.createCell(numCel);
				celdaCP.setCellValue(reg.getValorPorDescripcionContains("Unidad Respon") + " "
						+ reg.getValorPorDescripcionContains("grupo funci") + " "
						+ reg.getValorPorDescripcion("Función") + " " + reg.getValorPorDescripcionContains("Subfunci")
						+ " " + reg.getValorPorDescripcionContains("Programa Gene") + " "
						+ reg.getValorPorDescripcionContains("actividad ins") + " "
						+ reg.getValorPorDescripcionContains("Proyecto proce") + " "
						+ reg.getValorPorDescripcionContains("partida") + " "
						+ reg.getValorPorDescripcionContains("Puesto") + " "
						+ reg.getValorPorDescripcionContains("mero de puesto"));
				celdaCP.setCellStyle(style);
				numCel++;

				Cell celdaCLVCR = filaSiguiente.createCell(numCel);
				celdaCLVCR.setCellValue(reg.getValorPorDescripcionContains("centro de resp"));
				celdaCLVCR.setCellStyle(style);
				numCel++;

				Cell celdaDescripcionCR = filaSiguiente.createCell(numCel);
				celdaDescripcionCR.setCellValue(reg.getValorPorDescripcionContains("n del tipo de resp"));
				celdaDescripcionCR.setCellStyle(style);
				numCel++;

				Cell celdaPercepciones = filaSiguiente.createCell(numCel);
				celdaPercepciones.setCellValue(reg.getValorPorDescripcionContains("Percepcione"));
				celdaPercepciones.setCellStyle(style);
				numCel++;

				Cell celdaDeducciones = filaSiguiente.createCell(numCel);
				celdaDeducciones.setCellValue(reg.getValorPorDescripcionContains("Deducciones"));
				celdaDeducciones.setCellStyle(style);
				numCel++;

				Cell celdaNeto = filaSiguiente.createCell(numCel);
				celdaNeto.setCellValue(reg.getValorPorDescripcionContains("Neto"));
				celdaNeto.setCellStyle(style);
				numCel++;

			}

			for (int x = 0; x < libroExcel.getNumberOfSheets(); x++)
			{
				for (int y = 0; y < 10; y++)
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
					if (pl.getIdPlaza() == this.plazaSeleccionada.getIdPlaza())
					{
						plazaDescripcion = pl.getDescripcionPlaza();
						break;
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
				
				if( !this.incluir416 && ! this.incluirU00 && ! this.incluir610 )
				{
					incluye = "";
				}

				this.xlsPlantilla = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
						"Plantilla " + plazaDescripcion + " " + this.añoSeleccionado + "/" + this.qnaSeleccionado + " "
								+ incluye + ".xls");

			} catch (Exception e)
			{
				e.printStackTrace();
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento de generar la plantilla, favor de contactar con el desarrollador del sistema."));
		}
		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

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

	public int getQnaSeleccionado()
	{
		return qnaSeleccionado;
	}

	public void setQnaSeleccionado(int qnaSeleccionado)
	{
		this.qnaSeleccionado = qnaSeleccionado;
	}

	public StreamedContent getXlsPlantilla()
	{
		return xlsPlantilla;
	}

	public void setXlsPlantilla(StreamedContent xlsPlantilla)
	{
		this.xlsPlantilla = xlsPlantilla;
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

	public boolean isIncluir610()
	{
		return incluir610;
	}

	public void setIncluir610(boolean incluir610)
	{
		this.incluir610 = incluir610;
	}

}
