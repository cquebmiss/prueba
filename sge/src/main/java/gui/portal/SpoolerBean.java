package gui.portal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;


import modelo.ConfiguracionPdf;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@ViewScoped
public class SpoolerBean implements Serializable
{
	private UploadedFile archivoSpo;
	File spoTemp;
	String nombreArchivoCargado;

	private List<ConfiguracionPdf> catConfiguracionesPdf;

	private int idConfiguracionPdf;
	private ConfiguracionPdf configuracionPdf;

	private StreamedContent pdf;

	public SpoolerBean()
	{

	}

	@PostConstruct
	public void postConstruct()
	{
		this.idConfiguracionPdf = -1;
		updateCatalogoConfiguracionesPdf();
	}

	public void updateCatalogoConfiguracionesPdf()
	{
		this.catConfiguracionesPdf = utilidades.getCatConfiguracionPdf();
		valueChangeListaConfiguraciones();
	}

	public void updateDetallesConfiguracionBD()
	{
		this.configuracionPdf.updateBD();

		this.catConfiguracionesPdf.stream().filter(c -> c.getIdConfiguracionPdf() == this.idConfiguracionPdf).limit(1)
				.findAny().ifPresent(c -> c.update());
	}

	public void actionNuevaConfiguracion()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSpoolerPDF();)
		{

			this.configuracionPdf = new ConfiguracionPdf();
			this.configuracionPdf.setIdConfiguracionPdf(0);

			// Se genera el id y se intenta insertar, en caso de no poder repite la
			// operación incrementando el id
			prep = conexion.prepareStatement("SELECT MAX(idConfiguracionPdf) AS id FROM configuracionpdf LIMIT 1 ");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				this.configuracionPdf.setIdConfiguracionPdf(rBD.getInt("id") + 1);

			}

			prep.close();

			boolean insertCorrecto = false;

			do
			{
				try
				{
					prep = conexion.prepareStatement(" INSERT INTO configuracionpdf (idConfiguracionPdf, "
							+ "Descripcion, " + "TamañoHoja," + "Orientacion, " + "FontFamily, " + "TamañoFuente, "
							+ "EstiloFuente, " + "EspacioEntreCaracteres, " + "EspacioAntesParrafo, "
							+ "EspacioDespuesParrafo, " + "MargenDerecho, " + "MargenIzquierdo, " + "MargenSuperior, "
							+ "MargenInferior) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

					prep.setInt(1, this.configuracionPdf.getIdConfiguracionPdf());
					prep.setString(2, "Nueva Configuración");
					prep.setString(3, "carta");
					prep.setString(4, "vertical");
					prep.setString(5, "Courier");
					prep.setString(6, "10");
					prep.setString(7, "PLAIN");
					prep.setString(8, "0f");
					prep.setString(9, "0f");
					prep.setString(10, "0f");
					prep.setString(11, "0");
					prep.setString(12, "0");
					prep.setString(13, "0");
					prep.setString(14, "0");

					prep.executeUpdate();

					insertCorrecto = true;

				}
				catch (Exception e)
				{
					this.configuracionPdf.setIdConfiguracionPdf((this.configuracionPdf.getIdConfiguracionPdf() + 1));
					e.printStackTrace();
				}

			} while (!insertCorrecto);

			updateCatalogoConfiguracionesPdf();

			this.idConfiguracionPdf = -1;
			this.configuracionPdf = null;

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Configuración Creada",
					"La configuración ha sido creada exitosamente, realice las modificaciones correspondientes y guarde los cambios."));

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al crear la configuración Pdf, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	public void actionEliminarConfiguracion()
	{

		if (this.configuracionPdf == null)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Seleccionar Configuración", "Seleccione una configuración para poder eliminarla."));

			return;

		}

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSpoolerPDF();)
		{

			prep = conexion.prepareStatement("DELETE FROM configuracionpdf WHERE idConfiguracionPdf=?");
			prep.setInt(1, this.configuracionPdf.getIdConfiguracionPdf());

			prep.executeUpdate();

			updateCatalogoConfiguracionesPdf();

			this.idConfiguracionPdf = -1;
			this.configuracionPdf = null;

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Configuración Eliminada", "La configuración ha sido eliminada exitosamente."));

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al eliminar la configuración Pdf, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	public void valueChangeListaConfiguraciones()
	{

		this.configuracionPdf = this.idConfiguracionPdf == -1 ? new ConfiguracionPdf()
				: (ConfiguracionPdf) utilidades.getConfiguracionPdf(this.idConfiguracionPdf);
		this.configuracionPdf.update();

	}

	public void uploadFile(FileUploadEvent evt)
	{
		this.nombreArchivoCargado = "";
		archivoSpo = evt.getFile();
		this.nombreArchivoCargado = archivoSpo.getFileName();

		Random rnd = new Random();

		try
		{
			spoTemp = File.createTempFile("" + ((int) rnd.nextInt() * 6 + 1) + "", ".pdf");
			FileUtils.copyInputStreamToFile(archivoSpo.getInputstream(), spoTemp);

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
/*
	public void actionGenerarPDF()
	{
		try
		{

			if (this.spoTemp == null)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Cargar Archivo", "Debe cargar un archivo para generar el documento PDF."));

				return;
			}

			Random rnd = new Random();

			File temp = File.createTempFile("" + ((int) rnd.nextInt() * 6 + 1) + "", ".pdf");

			FileOutputStream pdfTemporal = new FileOutputStream(temp);

			Document document = new Document();
			Rectangle rect = null;

			switch (this.configuracionPdf.getTamañoHoja())
			{
				case "carta":

					if (this.configuracionPdf.getOrientacion().equals("horizontal"))
					{
						rect = PageSize.LETTER.rotate();
					}
					else
					{
						rect = PageSize.LETTER;
					}

				break;

				case "a4":

					if (this.configuracionPdf.getOrientacion().equals("horizontal"))
					{
						rect = PageSize.A4.rotate();
					}
					else
					{
						rect = PageSize.A4;
					}

				break;

				case "legal":

					if (this.configuracionPdf.getOrientacion().equals("horizontal"))
					{
						rect = PageSize.LEGAL.rotate();
					}
					else
					{
						rect = PageSize.LEGAL;
					}

				break;

				default:
				break;
			}

			document.setPageSize(rect);
			document.setMargins(Integer.parseInt(this.configuracionPdf.getMargenIzquierdo()),
					Integer.parseInt(this.configuracionPdf.getMargenDerecho()),
					Integer.parseInt(this.configuracionPdf.getMargenSuperior()),
					Integer.parseInt(this.configuracionPdf.getMargenInferior()));
			// step 2
			PdfWriter writer = PdfWriter.getInstance(document, pdfTemporal);
			// TableHeader event = new TableHeader();
			// writer.setPageEvent(event);
			// step 3
			document.open();

			writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);

			Font fuenteReporte = new Font();

			fuenteReporte.setFamily(this.configuracionPdf.getFontFamily());
			// fuenteReporte.setSize(7.3f);
			fuenteReporte.setSize(Float.parseFloat(this.configuracionPdf.getTamañoFuente()));

			fuenteReporte.setStyle(Integer.parseInt(this.configuracionPdf.getEstiloFuente()));

			BufferedReader b = new BufferedReader(new FileReader(spoTemp));
			String linea = null;

			// int n=1;

			while ((linea = b.readLine()) != null)
			{

				// System.out.println("Línea no ("+n+") : " + linea + "
				// caractéres");
				// n++;

				Chunk lineaChunk = null;
				Paragraph parrafoLinea = null;

				if (linea.contains("S E C R E T A R I A    D E    S A L U D"))
				{
					Font fuenteEncabezado = new Font();

					fuenteEncabezado.setFamily("Arial");
					fuenteEncabezado.setSize(Float.parseFloat(this.configuracionPdf.getTamañoFuente()) - 3);
					fuenteEncabezado.setStyle(Font.BOLD);

					document.newPage();

					// recorrer 11 líneas por el encabezado para poder ajustarse
					List<String> encabezado = new ArrayList<>();
					encabezado.add(linea.replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));
					encabezado.add(b.readLine().replace("�", "Ñ"));

					for (String l : encabezado)
					{
						lineaChunk = new Chunk(l, fuenteReporte);
						lineaChunk.setCharacterSpacing(
								Float.parseFloat(this.configuracionPdf.getEspacioEntreCaracteres()));
						parrafoLinea = new Paragraph(lineaChunk);
						parrafoLinea.setLeading(6f);
						parrafoLinea.setExtraParagraphSpace(0f);
						parrafoLinea.setSpacingAfter(0f);
						parrafoLinea.setSpacingBefore(0f);
						document.add(parrafoLinea);
					}

				}
				else
				{
					if (linea.isEmpty())
					{
						linea = "     ";
					}
					lineaChunk = new Chunk(linea.replace("�", "Ñ"), fuenteReporte);
					lineaChunk.setCharacterSpacing(Float.parseFloat(this.configuracionPdf.getEspacioEntreCaracteres()));

					parrafoLinea = new Paragraph(lineaChunk);
					parrafoLinea.setSpacingBefore(Float.parseFloat(this.configuracionPdf.getEspacioAntesParrafo()));
					parrafoLinea.setSpacingAfter(Float.parseFloat(this.configuracionPdf.getEspacioDespuesParrafo()));
					document.add(new Paragraph(parrafoLinea));
				}

			}

			document.close();
			// close the writer
			writer.close();

			b.close();
			temp.deleteOnExit();

			pdf = new DefaultStreamedContent(new FileInputStream(temp), "application/pdf",
					this.nombreArchivoCargado + ".pdf");

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al crear el documento Pdf, favor de contactar con el desarrollador del sistema."));
			e.printStackTrace();
		}

	}*/

	public UploadedFile getArchivoSpo()
	{
		return archivoSpo;
	}

	public void setArchivoSpo(UploadedFile archivoSpo)
	{
		this.archivoSpo = archivoSpo;
	}

	public StreamedContent getPdf()
	{
		return pdf;
	}

	public void setPdf(StreamedContent pdf)
	{
		this.pdf = pdf;
	}

	public File getSpoTemp()
	{
		return spoTemp;
	}

	public void setSpoTemp(File spoTemp)
	{
		this.spoTemp = spoTemp;
	}

	public String getNombreArchivoCargado()
	{
		return nombreArchivoCargado;
	}

	public void setNombreArchivoCargado(String nombreArchivoCargado)
	{
		this.nombreArchivoCargado = nombreArchivoCargado;
	}

	public ConfiguracionPdf getConfiguracionPdf()
	{
		return configuracionPdf;
	}

	public void setConfiguracionPdf(ConfiguracionPdf configuracionPdf)
	{
		this.configuracionPdf = configuracionPdf;
	}

	public int getIdConfiguracionPdf()
	{
		return idConfiguracionPdf;
	}

	public void setIdConfiguracionPdf(int idConfiguracionPdf)
	{
		this.idConfiguracionPdf = idConfiguracionPdf;
	}

	public List<ConfiguracionPdf> getCatConfiguracionesPdf()
	{
		return catConfiguracionesPdf;
	}

	public void setCatConfiguracionesPdf(List<ConfiguracionPdf> catConfiguracionesPdf)
	{
		this.catConfiguracionesPdf = catConfiguracionesPdf;
	}

}
