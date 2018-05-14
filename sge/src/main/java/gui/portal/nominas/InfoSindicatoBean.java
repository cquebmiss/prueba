package gui.portal.nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@SessionScoped
public class InfoSindicatoBean implements Serializable
{
	private Plaza			plaza;
	private int				año;
	private int				qna;

	private List<Plaza>		plazas;
	private int				plazaSelec;

	private StreamedContent	xlsPlantilla;

	private boolean			incluirProductos;
	private boolean			incluirCancelados;
	private boolean			excluirUnidades;
	private boolean			incluir416;
	private boolean			incluirU00;
	private boolean			incluir610;

	public InfoSindicatoBean()
	{
		super();
		// TODO Auto-generated constructor stub

		this.plazas = utilidades.getPlazas();
		LocalDateTime localDate = LocalDateTime.now();
		this.año = localDate.getYear();

		this.qna = localDate.getMonthValue() * 2;

		if (this.qna == 24)
		{
			this.qna = 23;
		}

	}

	public void actionGenerar()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;
		Map empleados = new HashMap<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{

			List<Producto> prods;
			String numEmpleado = "";

			prods = utilidades.getProductos(this.qna, this.qna, this.año, this.plaza.getIdPlaza());

			for (Producto prod : prods)
			{

				System.out.println("Leyendo prod: " + prod.getNombreProducto());

				prod.updateRegistrosTRAConConceptosEnDAT(this.incluir416, this.incluirU00, this.incluir610, false,
						true);

				System.out.println("Producto leído: " + prod.getNombreProducto());

			}

			HSSFWorkbook libroExcel = new HSSFWorkbook();

			ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

			// add picture data to this workbook.
			InputStream is = new FileInputStream(ext.getRealPath("/resources/images/Logos/Logo_SNTSA.png"));
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = libroExcel.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
			is.close();

			CreationHelper helper = libroExcel.getCreationHelper();

			List<BigDecimal> totales;

			int fila = 0;

			// Se crean fuentes
			HSSFFont font16Negrita = libroExcel.createFont();
			font16Negrita.setBold(true);
			font16Negrita.setFontHeightInPoints((short) 16);

			// Se crean fuentes
			HSSFFont font12Negrita = libroExcel.createFont();
			font12Negrita.setBold(true);
			font12Negrita.setFontHeightInPoints((short) 12);

			Font font = libroExcel.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);

			// Se crean estilos de celda
			HSSFCellStyle style = libroExcel.createCellStyle();
			HSSFCellStyle styleTitulo12 = libroExcel.createCellStyle();
			HSSFCellStyle styleTitulo16 = libroExcel.createCellStyle();

			styleTitulo12.setFont(font12Negrita);
			styleTitulo16.setFont(font16Negrita);
			style.setFont(font);

			HSSFCellStyle estiloMoneda = libroExcel.createCellStyle();
			HSSFDataFormat df = libroExcel.createDataFormat();
			estiloMoneda.setDataFormat(df.getFormat("$#,##0.00"));

			HSSFCellStyle estiloFecha = libroExcel.createCellStyle();
			estiloFecha.setDataFormat(HSSFDataFormat.getBuiltinFormat("dd/mm/yyyy"));

			HSSFSheet pagina = libroExcel.createSheet("Qna-Año " + this.qna + "-" + this.año);

			// Create the drawing patriarch. This is the top level container for
			// all shapes.
			Drawing drawing = pagina.createDrawingPatriarch();

			// add a picture shape
			ClientAnchor anchor = helper.createClientAnchor();
			// set top-left corner of the picture,
			// subsequent call of Picture#resize() will operate relative to it
			anchor.setCol1(0);
			anchor.setRow1(0);

			Picture pict = drawing.createPicture(anchor, pictureIdx);
			// auto-size picture relative to its top-left corner
			pict.resize(1, 11);

			fila = 0;
			int numCel = 0;

			fila++;
			fila++;
			fila++;

			Row filaDescripcionUnidad = pagina.createRow(fila);

			Cell celdaDescripcion = filaDescripcionUnidad.createCell(1);
			celdaDescripcion.setCellValue("INSTITUTO DE SERVICIOS DESCENTRALIZADOS");
			celdaDescripcion.setCellStyle(styleTitulo16);

			fila++;
			Row filaDescripcionUnidad1 = pagina.createRow(fila);
			Cell celdaDescripcion2 = filaDescripcionUnidad1.createCell(1);
			celdaDescripcion2.setCellValue("DE SALUD PÚBLICA DEL ESTADO DE CAMPECHE");
			celdaDescripcion2.setCellStyle(styleTitulo16);

			pagina.addMergedRegion(new CellRangeAddress(3, 3, 1, 10));
			pagina.addMergedRegion(new CellRangeAddress(4, 4, 1, 10));
			pagina.addMergedRegion(new CellRangeAddress(8, 8, 1, 10));
			pagina.addMergedRegion(new CellRangeAddress(0, 10, 0, 0));

			numCel = 0;
			fila++;
			fila++;
			fila++;
			fila++;
			Row filaTitulo = pagina.createRow(fila);
			Cell celdaTitulo = filaTitulo.createCell(1);
			celdaTitulo.setCellValue(
					"REPORTE DE PLANTILLA DEL PERSONAL HOMOLOGADO-ESTATAL, REGULARIZADO Y FORMALIZADO EN LAS ENTIDADES FEDERATIVAS");
			celdaTitulo.setCellStyle(styleTitulo12);
			fila++;

			fila++;

			Row filaAnexo = pagina.createRow(fila);
			Cell celdaAnexo = filaAnexo.createCell(10);
			celdaAnexo.setCellValue("ANEXO 1");

			fila++;

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
			c2.setCellValue("FECHA DE INGRESO");
			c2.setCellStyle(style);
			numCel++;

			Cell c3 = filaEncabezado.createCell(numCel);
			c3.setCellValue("SUELDO BASE");
			c3.setCellStyle(style);
			numCel++;

			Cell c4 = filaEncabezado.createCell(numCel);
			c4.setCellValue("C-58");
			c4.setCellStyle(style);
			numCel++;

			Cell c5 = filaEncabezado.createCell(numCel);
			c5.setCellValue("C-70");
			c5.setCellStyle(style);
			numCel++;

			Cell c6 = filaEncabezado.createCell(numCel);
			c6.setCellValue("C-21");
			c6.setCellStyle(style);
			numCel++;

			Cell c7 = filaEncabezado.createCell(numCel);
			c7.setCellValue("CODIGO DEL PUESTO");
			c7.setCellStyle(style);
			numCel++;

			Cell c8 = filaEncabezado.createCell(numCel);
			c8.setCellValue("TIPO DE NOMINA");
			c8.setCellStyle(style);
			numCel++;

			Cell c9 = filaEncabezado.createCell(numCel);
			c9.setCellValue("AÑO");
			c9.setCellStyle(style);
			numCel++;

			Cell c10 = filaEncabezado.createCell(numCel);
			c10.setCellValue("QUINCENA");
			c10.setCellStyle(style);
			numCel++;

			BigDecimal sueldoBase = null;
			BigDecimal c58 = null;
			BigDecimal c70 = null;
			BigDecimal c21 = null;
			String nomEmp;
			String tipoPago;
			String fechaIngreso;

			for (Producto prod : prods)
			{

				for (PlantillaRegistro reg : prod.getRegistrosDAT())
				{
					sueldoBase = reg.getValorConceptoAcum("07", null, null);
					c58 = reg.getValorConceptoAcum("58", null, null);
					c70 = reg.getValorConceptoAcum("70", null, null);
					c21 = reg.getValorConceptoAcum("21", null, null);
					nomEmp = reg.getValorPorDescripcionContains("nombre");
					tipoPago = reg.getValorPorDescripcionContains("tipo de pago");

					switch (tipoPago)
					{
						case "1":
							tipoPago = "ORDINARIA";
							break;
						case "2":
							tipoPago = "RETROACTIVO A/C";
							break;
						case "3":
							tipoPago = "RETROACTIVO A/A";
							break;
						case "4":
							tipoPago = "RESPONSABILIDAD A/C";
							break;
						case "5":
							tipoPago = "RESPONSABILIDAD A/A";
							break;
						case "6":
							tipoPago = "EXTRAORDINARIA";
							break;
						case "7":
							tipoPago = "CHEQUE CANCELADO";
							break;
						case "8":
							tipoPago = "REINTEGRO";
							break;
					}

					nomEmp = nomEmp.replace((char) 157, 'Ñ');
					nomEmp = nomEmp.replace((char) 209, 'Ñ');
					nomEmp = nomEmp.replaceAll("&", "Ñ");

					/*if (sueldoBase.equals(new BigDecimal("0.00")))
					{
						continue;
					}*/

					if (c58.compareTo(new BigDecimal("0.00")) == 0 && c70.compareTo(new BigDecimal("0.0")) == 0
							&& c21.compareTo(new BigDecimal("0.00")) == 0)
					{
						continue;
					}

					fila++;
					numCel = 0;

					Row filaSiguiente = pagina.createRow(fila);

					Cell celdaRFC = filaSiguiente.createCell(numCel);
					celdaRFC.setCellValue(reg.getValorPorDescripcionContains("RFC"));
					numCel++;

					Cell celdaNombre = filaSiguiente.createCell(numCel);
					celdaNombre.setCellValue(nomEmp);
					numCel++;

					Cell celdaCP = filaSiguiente.createCell(numCel);
					celdaCP.setCellStyle(estiloFecha);

					fechaIngreso = reg.getValorPorDescripcionContains("fecha de ing");
					fechaIngreso = fechaIngreso.substring(6, 8) + "/" + fechaIngreso.substring(4, 6) + "/"
							+ fechaIngreso.substring(0, 4);

					celdaCP.setCellValue(fechaIngreso);
					numCel++;

					Cell celdaSueldoBase = filaSiguiente.createCell(numCel);
					celdaSueldoBase.setCellStyle(estiloMoneda);
					celdaSueldoBase.setCellType(Cell.CELL_TYPE_NUMERIC);
					celdaSueldoBase.setCellValue(sueldoBase.doubleValue());
					numCel++;

					Cell c58det = filaSiguiente.createCell(numCel);
					c58det.setCellStyle(estiloMoneda);
					c58det.setCellType(Cell.CELL_TYPE_NUMERIC);
					c58det.setCellValue(c58.doubleValue());
					numCel++;

					Cell c70det = filaSiguiente.createCell(numCel);
					c70det.setCellStyle(estiloMoneda);
					c70det.setCellType(Cell.CELL_TYPE_NUMERIC);
					c70det.setCellValue(c70.doubleValue());
					numCel++;

					Cell c21det = filaSiguiente.createCell(numCel);
					c21det.setCellType(Cell.CELL_TYPE_NUMERIC);
					c21det.setCellStyle(estiloMoneda);
					c21det.setCellValue(c21.doubleValue());
					numCel++;

					Cell cCPdet = filaSiguiente.createCell(numCel);
					cCPdet.setCellValue(reg.getValorPorDescripcionContains("puesto"));
					numCel++;

					Cell cTipoNomina = filaSiguiente.createCell(numCel);
					cTipoNomina.setCellValue(tipoPago);
					numCel++;

					Cell cAño = filaSiguiente.createCell(numCel);
					cAño.setCellType(Cell.CELL_TYPE_NUMERIC);
					cAño.setCellValue(this.año);
					numCel++;

					Cell cQuincena = filaSiguiente.createCell(numCel);
					cQuincena.setCellType(Cell.CELL_TYPE_NUMERIC);
					cQuincena.setCellValue(this.qna);
					numCel++;

				}

			}

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
					if (pl.getIdPlaza() == this.plaza.getIdPlaza())
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

				if (!this.incluir416 && !this.incluirU00 && !this.incluir610)
				{
					incluye = "";
				}

				this.xlsPlantilla = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
						"Información para Sindicato " + plazaDescripcion + " " + this.año + "/" + this.qna + " "
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

	public int getQna()
	{
		return qna;
	}

	public void setQna(int qna)
	{
		this.qna = qna;
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

	public boolean isIncluir610()
	{
		return incluir610;
	}

	public void setIncluir610(boolean incluir610)
	{
		this.incluir610 = incluir610;
	}

	public StreamedContent getXlsPlantilla()
	{
		return xlsPlantilla;
	}

	public void setXlsPlantilla(StreamedContent xlsPlantilla)
	{
		this.xlsPlantilla = xlsPlantilla;
	}

}
