package gui.portal.nominas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.csvreader.CsvReader;

import modelo.ConceptoTimbre;
import modelo.JavaZip;
import modelo.Plaza;
import modelo.Producto;
import modelo.ProductoConverter;
import modelo.ProductoTimbrado;
import modelo.Status;
import modelo.Timbre;
import modelo.TipoNomina;
import modelo.TipoProducto;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@ViewScoped
public class GestionTimbradoBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<ProductoTimbrado> productosTimbrados;
	private ProductoTimbrado productoSelec;
	private List<ProductoTimbrado> productosTimbradosSelec;
	private List<ProductoTimbrado> productosTimbradosFilter;

	private List<Timbre> timbresSelec;
	private List<Timbre> timbresFilter;
	private Timbre timbreSelec;

	private String carpetaTempRandom;

	private String outputSubirCancelaciones;
	private String outputSubirYaCancelados;
	private String outputSubirCancelacionesErrores;

	private StreamedContent txtRespProceso;
	private StreamedContent txtRespProcesoCancelaciones;

	private int añoSeleccionado;
	private List<Integer> añosDisponibles;

	private TreeNode cfdiXml;

	// Registro Manual
	private List<ProductoTimbrado> productosEnZip;
	private ProductoConverter prodConverter;

	public GestionTimbradoBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postContruct()
	{
		getAñosDisponiblesTimbrado();

		cfdiXml = new DefaultTreeNode("Raíz", null);

	}

	public List<Producto> completeProductos(String consulta)
	{
		List<Producto> prods = utilidades.getProductos(consulta);
		this.prodConverter = new ProductoConverter(prods);

		return prods;
	}

	public void getAñosDisponiblesTimbrado()
	{

		this.añosDisponibles = new ArrayList<>();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{
			prep = conexion.prepareStatement(
					" SELECT DISTINCT(SUBSTRING(idProductoTimbrado,3,2)) AS añodispo FROM timbrado.productotimbrado");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.añosDisponibles.add((2000 + rBD.getInt("añodispo")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
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

	private BigDecimal subTotalTimbres;
	private BigDecimal descuentoTimbres;
	private BigDecimal totalTimbres;
	private int numeroTimbres;

	private PreparedStatement prepProductos;
	private PreparedStatement prepProductosCancelacion;
	private PreparedStatement prepProductosImportes;

	private PreparedStatement prepTimbres;
	private PreparedStatement prepConceptos;

	public void handleFileUpload(FileUploadEvent event)
	{
		UploadedFile file = event.getFile();

		try
		{
			this.valorProgreso = 10;

			Random rnd = new Random();
			this.carpetaTempRandom = "" + ((int) rnd.nextInt() * 6 + 1) + "/";
			String rutaRandom = utilidades.getRutaTimbradoTemp() + this.carpetaTempRandom;

			this.f = new File(utilidades.getRutaTimbradoTempRespuestas()
					+ carpetaTempRandom.substring(0, carpetaTempRandom.length() - 1) + ".txt");
			this.wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

			this.wr.println("Archivo Subido: " + file.getFileName() + "\r\n");
			this.wr.println("ID de proceso generado:" + this.carpetaTempRandom + "\r\n");

			utilidades.creaUploadedADisco(rutaRandom,
					file.getFileName().substring(0, file.getFileName().lastIndexOf(".")), file);

			JavaZip javaZip = new JavaZip();

			this.wr.println("Descomprimiendo archivo." + "\r\n");
			javaZip.unzip(new File(rutaRandom + file.getFileName()), new File(rutaRandom + "unzip/"));

			this.wr.println("Archivo descomprimido." + "\r\n");

			File ruta = new File(rutaRandom + file.getFileName());
			ruta.delete();

			this.wr.println("Archivo original eliminado, iniciando análisis de contenido." + "\r\n");

			ultimoProductoAnalizado = null;

			if (actionSubirRutaTimbrado(rutaRandom, false))
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo de Timbrado Cargado Exitosamente",
								"Los archivos de timbrado se han cargado exitosamente."));

			}

		}
		catch (Exception e)
		{
			this.wr.println(e + "\r\n");
			// e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción al momento de subir el producto." + e));
		}
		finally
		{
			this.valorProgreso = 0;

		}

	}

	public void actionSubirZipIndividual(FileUploadEvent event)
	{
		UploadedFile file = event.getFile();

		try
		{
			this.valorProgreso = 10;

			Random rnd = new Random();
			this.carpetaTempRandom = "" + ((int) rnd.nextInt() * 6 + 1) + "/";
			String rutaRandom = utilidades.getRutaTimbradoTemp() + this.carpetaTempRandom;

			this.f = new File(utilidades.getRutaTimbradoTempRespuestas()
					+ carpetaTempRandom.substring(0, carpetaTempRandom.length() - 1) + ".txt");
			this.wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

			this.wr.println("Archivo Subido: " + file.getFileName() + "\r\n");
			this.wr.println("ID de proceso generado:" + this.carpetaTempRandom + "\r\n");

			utilidades.creaUploadedADisco(rutaRandom,
					file.getFileName().substring(0, file.getFileName().lastIndexOf(".")), file);

			JavaZip javaZip = new JavaZip();

			this.wr.println("Descomprimiendo archivo." + "\r\n");
			javaZip.unzip(new File(rutaRandom + file.getFileName()), new File(rutaRandom + "unzip/"));

			this.wr.println("Archivo descomprimido." + "\r\n");

			File ruta = new File(rutaRandom + file.getFileName());
			ruta.delete();

			this.wr.println("Archivo original eliminado, iniciando análisis de contenido." + "\r\n");

			ultimoProductoAnalizado = null;

			File validacionNombreCarpeta = new File(rutaRandom + "unzip/timbrado/");

			if (!validacionNombreCarpeta.exists())
			{
				this.wr.println(
						"El nombre de la carpeta contenedora no es válida, debe llamarse 'TIMBRADO' (No sensible a mayúsculas o minúsculas).");
				return;
			}

			this.productosEnZip = new ArrayList<>();

			File[] archivosEnZip = validacionNombreCarpeta.listFiles();

			for (File fileInd : archivosEnZip)
			{
				this.productosEnZip.add(new ProductoTimbrado(fileInd.getName()));

			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Archivo Descomprimido", "Los archivos de timbrado se han identificado exitosamente."));

		}
		catch (Exception e)
		{
			this.valorProgreso = 0;
			this.wr.println(e + "\r\n");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción en el bloque principal de batch." + e));
		}
		finally
		{

		}

	}

	public boolean actionSubirRutaTimbrado(String rutaRandom, boolean verificarProdsEquivalentes)
	{

		File validacionNombreCarpeta = new File(rutaRandom + "unzip/timbrado/");

		if (!validacionNombreCarpeta.exists())
		{
			this.wr.println(
					"El nombre de la carpeta contenedora no es válida, debe llamarse 'TIMBRADO' (No sensible a mayúsculas o minúsculas).");
			return false;
		}

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{
			// Se establece la propiedad autocommit false
			conexion.setAutoCommit(false);

			this.prepTimbres = conexion.prepareStatement(
					"INSERT INTO timbre (folio, UUID, receptor, rfc,  idProductoTimbrado, FechaComprobante,"
							+ " FechaTimbrado, NoCertificado, noCertificadoSAT, FechaPago, TotalPercepciones, TotalDeducciones, "
							+ "Total, TotalGravados, TotalExcento, TotalSueldos, TotalImpuestosRetenidos, TotalOtrasDeducciones, TotalGravadoDeduc, TotalExentoDeduc, Version) VALUES"
							+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?  );");

			this.prepConceptos = conexion.prepareStatement(
					"INSERT INTO conceptotimbre (idProductoTimbrado, Folio, Clave, ImporteExento, ImporteGravado, ImporteDeduc, TipoPercepDeduc) VALUES ( ?, ?, ?, ?, ?, ?, ?)");

			this.prepProductos = conexion.prepareStatement(
					" INSERT INTO productotimbrado (idProductoTimbrado, idProductoNomina, TotalPercepciones, TotalDeducciones,"
							+ " Total, TotalRegistros, idStatus) VALUES ( ?, ?, ?, ?, ?, ?, 0); ");

			this.prepProductosCancelacion = conexion.prepareStatement(
					" UPDATE productotimbrado SET idStatus=?, TotalCancelados = TotalRegistros WHERE idProductoTimbrado = ?");

			this.prepProductosImportes = conexion.prepareStatement(
					" UPDATE productotimbrado SET Total= ( Total + ?), TotalPercepciones = (TotalPercepciones + ? ), "
							+ "TotalDeducciones = (TotalDeducciones + ? ), TotalRegistros=? WHERE idProductoTimbrado = ? ");

			analizaRutaTimbrado(validacionNombreCarpeta, verificarProdsEquivalentes);

			// Se ejecuta un solo comando Batch tanto de timbres como de conceptos y
			// inserción de productos y su actualización y cancelaciones
			// esto ahorra ejecuciones y reduce el tiempo de latencia de forma efectiva
			this.valorProgreso = 40;
			this.prepProductos.executeBatch();
			this.prepProductosImportes.executeBatch();

			this.valorProgreso = 60;
			this.prepTimbres.executeBatch();

			this.valorProgreso = 80;
			this.prepConceptos.executeBatch();
			this.prepProductosCancelacion.executeBatch();

			conexion.commit();

			this.valorProgreso = 90;

			// Ahora se copian todos los archivos dentro de la carpeta principal que se
			// subió
			File[] productos = validacionNombreCarpeta.listFiles();

			for (File prod : productos)
			{
				Files.move(prod.toPath(), Paths.get(utilidades.getRutaTimbrado() + prod.getName()),
						StandardCopyOption.REPLACE_EXISTING);

			}

			this.valorProgreso = 100;

			FileUtils.deleteDirectory(new File(rutaRandom));

		}
		catch (Exception e)
		{
			this.valorProgreso = 0;
			this.wr.println(e + "\r\n");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción en el bloque principal de batch." + e));

			return false;
		}
		finally
		{
			if (this.prepTimbres != null)
			{
				try
				{
					this.prepTimbres.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (this.prepConceptos != null)
			{
				try
				{
					this.prepConceptos.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (this.prepProductos != null)
			{
				try
				{
					this.prepProductos.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (this.prepProductosCancelacion != null)
			{
				try
				{
					this.prepProductosCancelacion.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (this.prepProductosImportes != null)
			{
				try
				{
					this.prepProductosImportes.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return true;
	}

	public void actionRegistrarProductosManual()
	{
		if (actionSubirRutaTimbrado(utilidades.getRutaTimbradoTemp() + this.carpetaTempRandom, true))
		{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo de Timbrado Cargado Exitosamente",
							"Los archivos de timbrado se han cargado exitosamente."));

			this.productosEnZip = null;

		}
	}

	// Ejecuta el procedimiento almacenado en mysql para vincular los registros de
	// los timbres con los registros correspondientes de los productos de nómina
	public void vincularPrdTim_PrdNomina(int año)
	{

		CallableStatement call = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{

			call = conexion.prepareCall("{call vincularProductosConNomina(" + this.añoSeleccionado + ") }");
			call.execute();

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Timbres Vinculados",
							"Los timbres del año han sido vinculados con sus registros de nómina exitosamente."));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción al vincular timbrado con nómina:" + e));
		}
		finally
		{
			if (call != null)
			{
				try
				{
					call.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public void handleFileUploadCancelaciones(FileUploadEvent event)
	{
		UploadedFile file = event.getFile();

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try
		{
			this.outputSubirCancelaciones = "";
			this.outputSubirYaCancelados = "";
			this.outputSubirCancelacionesErrores = "";

			this.valorProgresoCancelacion = 0;

			Random rnd = new Random();
			this.carpetaTempRandom = "" + ((int) rnd.nextInt() * 6 + 1) + "/";
			String rutaRandom = utilidades.getRutaTimbradoTemp() + this.carpetaTempRandom;

			this.outputSubirCancelaciones += "Archivo Cancelaciones Subido: " + file.getFileName() + "\r\n";
			this.outputSubirCancelaciones += "ID de proceso generado:" + this.carpetaTempRandom + "\r\n";

			utilidades.creaUploadedADisco(rutaRandom,
					file.getFileName().substring(0, file.getFileName().lastIndexOf(".")), file);

			this.outputSubirCancelaciones += "Leyendo archivo." + "\r\n";

			File ruta = new File(rutaRandom + file.getFileName());

			try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
			{
				long totalLineas;
				int nLinea = 0;

				try (Stream<String> s = Files.lines(Paths.get(rutaRandom + file.getFileName()),
						Charset.defaultCharset()))
				{
					totalLineas = s.count();
				}

				CsvReader reader = new CsvReader(new FileReader(ruta));

				reader.readHeaders();

				while (reader.readRecord())
				{
					nLinea++;

					this.valorProgresoCancelacion = (int) ((nLinea * 100) / totalLineas);

					prep = conexion.prepareStatement("  SELECT * FROM timbre WHERE idProductoTimbrado=? AND folio=? ");

					prep.setString(1, reader.get("PRODUCTO"));
					prep.setString(2, reader.get("ID"));

					rBD = prep.executeQuery();

					if (rBD.next())
					{

						if (rBD.getInt("idStatus") == -1)
						{
							this.outputSubirYaCancelados += "Timbre: " + reader.get("ID") + " en producto: "
									+ reader.get("PRODUCTO") + " ya cancelado." + "\r\n";
						}
						else
						{

							prep.close();

							prep = conexion.prepareStatement(
									" UPDATE timbre SET idStatus='-1', FechaCancelacion=?, Motivo_Can=?, Remesa=? WHERE folio=? and idProductoTimbrado=? ");

							prep.setString(1, reader.get("FECHACANCELACION"));
							prep.setString(2, reader.get("MOTIVO_CAN"));
							prep.setString(3, reader.get("REMESA"));

							prep.setString(4, reader.get("ID"));
							prep.setString(5, reader.get("PRODUCTO"));

							prep.executeUpdate();

							prep.close();

							// Se actualiza el conteo de cancelados
							prep = conexion.prepareStatement(
									"UPDATE productotimbrado SET TotalCancelados = (TotalCancelados+1) WHERE idProductoTimbrado=?");
							prep.setString(1, reader.get("PRODUCTO"));

							prep.executeUpdate();

							prep.close();

							this.outputSubirCancelaciones += "Timbre: " + reader.get("ID") + " en producto: "
									+ reader.get("PRODUCTO") + " cancelado exitosamente." + "\r\n";

						}

					}
					else
					{
						this.outputSubirCancelacionesErrores += "Timbre: " + reader.get("ID") + " en producto: "
								+ reader.get("PRODUCTO") + " no localizado para cancelación." + "\r\n";

					}

					prep.close();

				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.outputSubirCancelacionesErrores += e + "\r\n";
			}
			finally
			{
				this.valorProgresoCancelacion = 0;

				if (prep != null)
				{
					prep.close();
				}
			}

			ruta.delete();
			FileUtils.deleteDirectory(new File(rutaRandom));

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo de Cancelaciones Cargado Exitosamente",
							"Los archivos de cancelaciones se han cargado exitosamente."));

		}
		catch (Exception e)
		{
			this.wr.println(e + "\r\n");
			// e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Se ha generado una excepción al momento de cancelar los timbres." + e));
		}

	}

	private PrintWriter wr = null;
	private File f = null;

	public void actionDescargarRepuestaProceso()
	{

		/*
		 * try { f = new File(utilidades.getRutaTimbradoTempRespuestas() +
		 * carpetaTempRandom.substring(0, carpetaTempRandom.length() - 1) + ".txt"); //
		 * FileWriter w = new FileWriter(f); // BufferedWriter bw = new
		 * BufferedWriter(w); wr = new PrintWriter(new OutputStreamWriter(new
		 * FileOutputStream(f), StandardCharsets.UTF_8), true);
		 * 
		 * wr.println("PROCESO GENERAL"); wr.println("");
		 * wr.println(this.outputSubirTimbrado);
		 * 
		 * wr.println(this.getOutputSubirYaTimbrados().isEmpty() ?
		 * "TIMBRES YA REGISTRADOS ANTERIORMENTE (Ninguno)" :
		 * "TIMBRES YA REGISTRADOS ANTERIORMENTE "); wr.println("");
		 * wr.println(this.outputSubirYaTimbrados);
		 * 
		 * wr.println(this.outputSubirErrores.isEmpty() ?
		 * "ERRORES DEL PROCESO (Ninguno)" : "ERRORES DEL PROCESO"); wr.println("");
		 * wr.println(this.outputSubirErrores);
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 */

		try
		{

			wr.close();

			this.txtRespProceso = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
					"Respuesta Carga Timbrado");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void actionDescargarRepuestaProcesoCancelaciones()
	{

		File f = null;
		PrintWriter wr = null;

		try
		{
			f = new File(utilidades.getRutaTimbradoTempRespuestas()
					+ carpetaTempRandom.substring(0, carpetaTempRandom.length() - 1) + "-Cancelaciones.txt");
			// FileWriter w = new FileWriter(f);
			// BufferedWriter bw = new BufferedWriter(w);
			wr = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true);

			wr.println("PROCESO GENERAL");
			wr.println("");
			wr.println(this.outputSubirCancelaciones);

			wr.println(this.getOutputSubirYaCancelados().isEmpty() ? "TIMBRES YA CANCELADOS ANTERIORMENTE (Ninguno)"
					: "TIMBRES YA CANCELADOS ANTERIORMENTE ");
			wr.println("");
			wr.println(this.outputSubirYaCancelados);

			wr.println(this.outputSubirCancelacionesErrores.isEmpty() ? "ERRORES DEL PROCESO (Ninguno)"
					: "ERRORES DEL PROCESO");
			wr.println("");
			wr.println(this.outputSubirCancelacionesErrores);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{

			wr.close();

			this.txtRespProcesoCancelaciones = new DefaultStreamedContent(new FileInputStream(f), "text/plain",
					"Respuesta Cancelaciones Timbres Individuales");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// Se utilizan para recolectar información del producto de nómina
	// correspondiente al producto timbrado que se está analizando para poder
	// encontrar el registro correspondiente y enlazarlo
	String ultimoProductoAnalizado = null;
	int ultimoIdProductoNomina = -1;
	int ultimaPosNumeroCheque = -1;

	int valorProgreso = 0;
	int valorProgresoCancelacion = 0;

	// Analiza la ruta proporcionada, recorriendo cada carpeta y cada archivo
	// para ingresarlo al sistema
	public void analizaRutaTimbrado(File ruta, boolean verificarEquivalenciasManuales)
	{
		this.wr.println("\r\n" + "Leyendo carpeta: " + ruta.getName() + "\r\n");
		this.subTotalTimbres = new BigDecimal(0.00);
		this.descuentoTimbres = new BigDecimal(0.00);
		this.totalTimbres = new BigDecimal(0.00);
		this.numeroTimbres = 0;

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{

			File[] archivosRuta = ruta.listFiles();
			int totalCarpetas = archivosRuta.length;
			int nCarpeta = 0;

			for (File archivo : archivosRuta)
			{
				nCarpeta++;

				if (ruta.getName().equalsIgnoreCase("TIMBRADO"))
				{
					// this.valorProgreso = ((nCarpeta) * 100) / totalCarpetas;

				}

				if (archivo.isDirectory())
				{
					analizaRutaTimbrado(archivo, verificarEquivalenciasManuales);
				}
				else
				{

					if (archivo.getName().contains(".xml"))
					{
						añadeTimbreASistema(archivo, verificarEquivalenciasManuales);
					}
				}

			}

			// Se actualiza la información de cada producto
			if (!ruta.getName().equalsIgnoreCase("TIMBRADO"))
			{

				/*
				 * prep = conexion.prepareStatement(
				 * " UPDATE productotimbrado SET Total= ( Total + ?), TotalPercepciones = (TotalPercepciones + ? ), "
				 * +
				 * "TotalDeducciones = (TotalDeducciones + ? ), TotalRegistros=? WHERE idProductoTimbrado = ? "
				 * );
				 */

				this.prepProductosImportes.setBigDecimal(1, this.totalTimbres);
				this.prepProductosImportes.setBigDecimal(2, this.subTotalTimbres);
				this.prepProductosImportes.setBigDecimal(3, this.descuentoTimbres);
				this.prepProductosImportes.setInt(4, this.numeroTimbres);

				this.prepProductosImportes.setString(5, carpetaProducto);

				this.prepProductosImportes.addBatch();
				// prep.close();

			}

		}
		catch (Exception e)
		{
			this.wr.println(carpetaProducto + " - " + nuevoTimbre.getFolio() + ": " + e + "\r\n");
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
					this.wr.println(carpetaProducto + " - " + nuevoTimbre.getFolio() + ": " + e + "\r\n");
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}

		}

	}

	private String carpetaProducto;
	private int resultadoAñadirCarpeta = 0;
	private FileOutputStream rutaCopiaTimbreXml;
	private FileOutputStream rutaCopiaTimbrePdf;
	private InputStream isXML;
	private String pathPDF;
	private InputStream isPDF;
	private DocumentBuilderFactory factory;
	private InputStream inputStream;
	private Reader reader;
	private InputSource is;
	private DocumentBuilder builder;
	private Document doc;
	private Timbre nuevoTimbre;
	private String version;

	public void añadeTimbreASistema(File rutaTimbre, boolean verificarEquivalenciasManuales)
	{

		carpetaProducto = rutaTimbre.getParentFile().getName();

		if (!carpetaProducto.equalsIgnoreCase(this.ultimoProductoAnalizado))
		{
			if (verificarEquivalenciasManuales)
			{
				int prodEquiv = -1;

				for (ProductoTimbrado prod : this.productosEnZip)
				{
					if (carpetaProducto.equalsIgnoreCase(prod.getIdProductoTimbrado()))
					{
						prodEquiv = prod.getProductoNomina().getIdProducto();

						break;
					}

				}

				try
				{
					this.prepProductos.setString(1, carpetaProducto);
					this.prepProductos.setInt(2, prodEquiv);
					this.prepProductos.setDouble(3, 0);
					this.prepProductos.setDouble(4, 0);
					this.prepProductos.setDouble(5, 0);
					this.prepProductos.setInt(6, 0);

					this.prepProductos.addBatch();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else
			{
				resultadoAñadirCarpeta = updateCarpetaProductoTimbrado(carpetaProducto, rutaTimbre);

			}

			this.ultimoProductoAnalizado = carpetaProducto;

			if (resultadoAñadirCarpeta == 2)
			{
				this.wr.println("No se encontró el equivalente en productos de nómina, carpeta: "
						+ this.ultimoProductoAnalizado + "\r\n");
			}

		}
		// -1 Excepción, 0 carpeta válida registrada,
		// 1 carpeta ya registrada,
		// 2 carpeta equivalente no encontrado en productos de nómina
		if (resultadoAñadirCarpeta == -1)
		{
			this.wr.println("Excepción procesando la carpeta: " + this.ultimoProductoAnalizado + "\r\n");
			return;
		}

		// System.out.println(" Timbre: " + rutaTimbre.getName());
		// Se realiza la lectura del documento XML
		factory = DocumentBuilderFactory.newInstance();

		try
		{

			inputStream = new FileInputStream(rutaTimbre);

			// Captura la excepción en caso de no corresponder con la codificación del xml
			try
			{
				reader = new InputStreamReader(inputStream, "Cp1252");

				is = new InputSource(reader);
				// is.setEncoding("UTF-8"); -> This line causes error! Content is
				// not allowed in prolog

				// Se crea el documento
				builder = factory.newDocumentBuilder();
				doc = builder.parse(is);

			}
			catch (Exception e)
			{
				// Cuando ocurre un error que marca que el prólogo es inválido se pasa
				// directamente el FileInputStream al parser
				e.printStackTrace();

				inputStream = new FileInputStream(rutaTimbre);
				doc = builder.parse(inputStream);

			}

			nuevoTimbre = new Timbre();
			nuevoTimbre.inicializaListaConceptos();

			this.leyendoPercepciones = false;
			this.leyendoDeducciones = false;

			this.version = null;
			leerNodeList(doc.getChildNodes(), nuevoTimbre);

			PreparedStatement prep = null;
			ResultSet rBD = null;

			String insertConceptoTimbre = " INSERT INTO conceptotimbre (idProductoTimbrado, Folio, Clave, ImporteExento, ImporteGravado, ImporteDeduc, TipoPercepDeduc) VALUES ( ?, ?, ?, ?, ?, ?, ?) ";

			try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
			{

				prep = conexion.prepareStatement("SELECT * FROM timbre WHERE idProductoTimbrado=? AND folio=? ");

				prep.setString(1, carpetaProducto);
				prep.setString(2, nuevoTimbre.getFolio());

				rBD = prep.executeQuery();

				if (!rBD.next())
				{
					prep.close();

					// Se encuentra su producto equivalente en la nómina
					/*
					 * prep = conexion.prepareStatement(
					 * "INSERT INTO timbre (folio, UUID, receptor, rfc,  idProductoTimbrado, FechaComprobante,"
					 * +
					 * " FechaTimbrado, NoCertificado, noCertificadoSAT, FechaPago, TotalPercepciones, TotalDeducciones, "
					 * +
					 * "Total, TotalGravados, TotalExcento, TotalSueldos, TotalImpuestosRetenidos, TotalOtrasDeducciones, TotalGravadoDeduc, TotalExentoDeduc, idRegistroProductoNomina, Version) VALUES"
					 * +
					 * " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ( SELECT idRegistro FROM nominas.datvalores dt WHERE dt.idProducto=? AND dt.Orden=? AND dt.Valor=? LIMIT 1), ?  );"
					 * );
					 */
					this.prepTimbres.setString(1, nuevoTimbre.getFolio());
					this.prepTimbres.setString(2, nuevoTimbre.getUUID());
					this.prepTimbres.setString(3, nuevoTimbre.getReceptor());
					this.prepTimbres.setString(4, nuevoTimbre.getRfc());
					this.prepTimbres.setString(5, carpetaProducto);
					this.prepTimbres.setTimestamp(6,
							new java.sql.Timestamp(nuevoTimbre.getFechaComprobante().getTime()));

					this.prepTimbres.setTimestamp(7, new java.sql.Timestamp(nuevoTimbre.getFechaTimbrado().getTime()));
					this.prepTimbres.setString(8, nuevoTimbre.getNoCertificado());
					this.prepTimbres.setString(9, nuevoTimbre.getNoCertificadoSAT());
					this.prepTimbres.setTimestamp(10, new java.sql.Timestamp(nuevoTimbre.getFechaPago().getTime()));
					this.prepTimbres.setBigDecimal(11,
							nuevoTimbre.getSubTotal() == null ? new BigDecimal(0.00) : nuevoTimbre.getSubTotal());
					this.prepTimbres.setBigDecimal(12,
							nuevoTimbre.getDescuento() == null ? new BigDecimal(0.00) : nuevoTimbre.getDescuento());

					this.prepTimbres.setBigDecimal(13,
							nuevoTimbre.getTotal() == null ? new BigDecimal(0.00) : nuevoTimbre.getTotal());
					this.prepTimbres.setBigDecimal(14, nuevoTimbre.getTotalGravados() == null ? new BigDecimal(0.00)
							: nuevoTimbre.getTotalGravados());
					this.prepTimbres.setBigDecimal(15, nuevoTimbre.getTotalExcento() == null ? new BigDecimal(0.00)
							: nuevoTimbre.getTotalExcento());
					this.prepTimbres.setBigDecimal(16, nuevoTimbre.getTotalSueldos() == null ? new BigDecimal(0.00)
							: nuevoTimbre.getTotalSueldos());
					this.prepTimbres.setBigDecimal(17,
							nuevoTimbre.getTotalImpuestosRetenidos() == null ? new BigDecimal(0.00)
									: nuevoTimbre.getTotalImpuestosRetenidos());
					this.prepTimbres.setBigDecimal(18,
							nuevoTimbre.getTotalOtrasDeducciones() == null ? new BigDecimal(0.00)
									: nuevoTimbre.getTotalOtrasDeducciones());
					this.prepTimbres.setBigDecimal(19,
							nuevoTimbre.getTotalGravadosDeduc() == null ? new BigDecimal(0.00)
									: nuevoTimbre.getTotalGravadosDeduc());
					this.prepTimbres.setBigDecimal(20, nuevoTimbre.getTotalExcentoDeduc() == null ? new BigDecimal(0.00)
							: nuevoTimbre.getTotalExcentoDeduc());

					/*
					 * this.prepTimbres.setInt(21, this.ultimoIdProductoNomina);
					 * this.prepTimbres.setInt(22, this.ultimaPosNumeroCheque);
					 * this.prepTimbres.setString(23, nuevoTimbre.getFolio());
					 */
					this.prepTimbres.setString(21, nuevoTimbre.getVersion());

					this.prepTimbres.addBatch();

					// prep.executeUpdate();

					// this.outputSubirTimbrado += carpetaProducto + " - Timbre
					// registrado con folio: "
					// + nuevoTimbre.getFolio() + "\r\n";

					// prep.close();

					this.totalTimbres = this.totalTimbres.add(nuevoTimbre.getTotal());
					this.subTotalTimbres = this.subTotalTimbres.add(nuevoTimbre.getSubTotal());
					this.descuentoTimbres = this.descuentoTimbres.add(nuevoTimbre.getDescuento());
					this.numeroTimbres++;

					// Se inserta el detalle del timbre
					// " INSERT INTO conceptotimbre (idProductoTimbrado, Folio,
					// Clave,
					// ImporteExento, ImporteGravado, ImporteDeduc,
					// TipoPercepDeduc) VALUES ( ?, ?, ?, ?, ?, ?) ";
					// prep = conexion.prepareStatement(insertConceptoTimbre);

					for (ConceptoTimbre con : nuevoTimbre.getConceptos())
					{

						this.prepConceptos.setString(1, carpetaProducto);
						this.prepConceptos.setString(2, nuevoTimbre.getFolio());
						this.prepConceptos.setString(3,
								con.getTipoConcepto() + con.getClave() + con.getPartidaAntecedente());

						this.prepConceptos.setBigDecimal(4,
								con.getImporteExento() == null ? new BigDecimal(0.00) : con.getImporteExento());
						this.prepConceptos.setBigDecimal(5,
								con.getImporteGravado() == null ? new BigDecimal(0.00) : con.getImporteGravado());
						this.prepConceptos.setBigDecimal(6,
								con.getImporteDeduc() == null ? new BigDecimal(0.00) : con.getImporteDeduc());

						this.prepConceptos.setString(7, con.getTipoPercepDeduc());

						this.prepConceptos.addBatch();

					}

					/*
					 * if (!nuevoTimbre.getConceptos().isEmpty()) { prep.executeBatch();
					 * 
					 * prep.close(); }
					 */
					// Se copia el timbre a la carpeta correspondiente
					/*
					 * rutaCopiaTimbreXml = new FileOutputStream( new
					 * File(utilidades.getRutaTimbrado() + carpetaProducto + "/" +
					 * rutaTimbre.getName()));
					 * 
					 * rutaCopiaTimbrePdf = new FileOutputStream(new
					 * File(utilidades.getRutaTimbrado() + carpetaProducto + "/" +
					 * rutaTimbre.getName().substring(0, rutaTimbre.getName().length() - 3) +
					 * "pdf"));
					 * 
					 * isXML = new FileInputStream(rutaTimbre);
					 * 
					 * pathPDF = rutaTimbre.getPath(); pathPDF = pathPDF.substring(0,
					 * pathPDF.length() - 3) + "pdf";
					 * 
					 * isPDF = new FileInputStream(pathPDF);
					 * 
					 * IOUtils.copy(isXML, rutaCopiaTimbreXml); IOUtils.copy(isPDF,
					 * rutaCopiaTimbrePdf);
					 */

				}
				else
				{
					this.wr.println(
							carpetaProducto + " - Timbre ya registrado. Folio: " + nuevoTimbre.getFolio() + "\r\n");

				}

				// System.out.println("");

			}
			catch (Exception e)
			{
				/*
				 * FacesContext.getCurrentInstance().addMessage(null, new
				 * FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
				 * "Ha ocurrido una excepción al añadir el timbre al sistema(" +
				 * nuevoTimbre.getFolio() +
				 * "), favor de contactar con el desarrollador del sistema."));
				 */

				this.wr.println(carpetaProducto + " - " + nuevoTimbre.getFolio() + ": " + e + "\r\n");
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
						this.wr.println(carpetaProducto + " - " + nuevoTimbre.getFolio() + ": " + e + "\r\n");
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}

				if (this.isXML != null)
				{
					isXML.close();
				}

				if (this.isPDF != null)
				{
					isPDF.close();
				}

				if (this.rutaCopiaTimbrePdf != null)
				{
					rutaCopiaTimbrePdf.close();
				}
				if (this.rutaCopiaTimbreXml != null)
				{
					rutaCopiaTimbreXml.close();
				}

				if (inputStream != null)
				{
					inputStream.close();
				}

				if (reader != null)
				{
					reader.close();
				}

			}

			// Solo hay un nodo principal en los Cfdi

		}
		/*
		 * catch (ParserConfigurationException e) { // TODO Auto-generated catch block
		 * this.wr.println(carpetaProducto + " - " + rutaTimbre.getName() +
		 * ": Archivo no válido: " + "\r\n"); // e.printStackTrace(); }
		 */
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			this.wr.println(carpetaProducto + " - " + rutaTimbre.getName() + ": " + e + "\r\n");
			// e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			this.wr.println(carpetaProducto + " - " + rutaTimbre.getName() + " : " + e + "\r\n");
			// e.printStackTrace();
		}

	}

	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	private java.util.Date date;
	private java.sql.Date sqlDate;
	private NamedNodeMap nnm;
	private Node nodoAtributo;
	private boolean leyendoPercepciones;
	private boolean leyendoDeducciones;
	private ConceptoTimbre conceptoAñadiendo;

	// Se leen los nodos del archivo XML y se va rellenando el objeto timbre con
	// la información requerida
	public void leerNodeList(NodeList listaNodos, Timbre timbre)
	{

		Node nodo = null;

		for (int x = 0; x < listaNodos.getLength(); x++)
		{

			nodo = listaNodos.item(x);
			// System.out.println("Nombre del Nodo: " + nodo.getNodeName());

			nnm = nodo.getAttributes();

			if (nnm == null)
			{
				continue;
			}

			if (nodo.getNodeName().contains("cfdi:Comprobante"))
			{
				if (nnm.getNamedItem("xmlns:nomina12") != null)
				{
					version = "1.2";
					timbre.setVersion(version);
				}
				else if (nnm.getNamedItem("xmlns:nomina") != null)
				{
					version = "1.1";
					timbre.setVersion(version);
				}

			}
			else
			{
				if (nodo.getNodeName().contains("Percepciones"))
				{
					this.leyendoPercepciones = true;
					this.leyendoDeducciones = false;
				}
				else if (nodo.getNodeName().contains("Deducciones"))
				{
					this.leyendoDeducciones = true;
					this.leyendoPercepciones = false;
				}
				else if (nodo.getNodeName().contains("OtroPago"))
				{
					this.leyendoPercepciones = false;
					this.leyendoDeducciones = false;
				}
			}

			for (int y = 0; y < nnm.getLength(); y++)
			{

				nodoAtributo = nnm.item(y);

				switch (version)
				{
					case "1.1":
						// System.out.println("Nombre del Atributo: " +
						// nodoAtributo.getNodeName());

						switch (nodoAtributo.getNodeName().toLowerCase())
						{

							case "folio":
								timbre.setFolio(nodoAtributo.getNodeValue());
							break;

							case "uuid":
								timbre.setUUID(nodoAtributo.getNodeValue());
							break;

							case "nombre":
								if (nodo.getNodeName().contains("cfdi:Receptor"))
								{
									timbre.setReceptor(nodoAtributo.getNodeValue());
								}

							break;

							case "rfc":
								if (nodo.getNodeName().contains("cfdi:Receptor"))
								{
									timbre.setRfc(nodoAtributo.getNodeValue());
								}

							break;

							case "fecha":
								try
								{
									date = sdf1.parse(nodoAtributo.getNodeValue());
								}
								catch (DOMException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								sqlDate = new Date(date.getTime());
								timbre.setFechaComprobante(sqlDate);

							break;

							case "fechatimbrado":
								try
								{
									date = sdf1.parse(nodoAtributo.getNodeValue());
								}
								catch (DOMException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								sqlDate = new Date(date.getTime());
								timbre.setFechaTimbrado(sqlDate);
							break;

							case "nocertificado":
								timbre.setNoCertificado(nodoAtributo.getNodeValue());
							break;

							case "nocertificadosat":
								timbre.setNoCertificadoSAT(nodoAtributo.getNodeValue());
							break;

							case "fechapago":
								try
								{
									date = sdf2.parse(nodoAtributo.getNodeValue());
								}
								catch (DOMException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								sqlDate = new Date(date.getTime());
								timbre.setFechaPago(sqlDate);
							break;

							case "clave":
								String valor = nodoAtributo.getNodeValue();

								if (valor.length() < 5)
								{
									this.conceptoAñadiendo = new ConceptoTimbre(1, valor, "", null, "00");
								}
								else
								{
									this.conceptoAñadiendo = new ConceptoTimbre(Integer.parseInt(valor.substring(0, 1)),
											valor.substring(1, 3), "", null, valor.substring(3, 5));
								}

							break;

							case "concepto":
								this.conceptoAñadiendo.setDescripcion(nodoAtributo.getNodeValue());
							break;

							case "importeexento":
								this.conceptoAñadiendo.setImporteExento(new BigDecimal(nodoAtributo.getNodeValue()));

							break;

							case "importegravado":
								this.conceptoAñadiendo.setImporteGravado(new BigDecimal(nodoAtributo.getNodeValue()));

							break;

							case "tipopercepcion":
							case "tipodeduccion":
							case "tipootroPago":
								this.conceptoAñadiendo.setTipoPercepDeduc(nodoAtributo.getNodeValue());
								timbre.addConcepto(this.conceptoAñadiendo);

							break;

							case "importe":
								if (this.leyendoDeducciones)
								{
									this.conceptoAñadiendo.setImporteDeduc(new BigDecimal(nodoAtributo.getNodeValue()));
								}

							break;

							case "totalpercepciones":
								timbre.setTotalPercepciones(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totaldeducciones":
								timbre.setTotalDeducciones(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "total":
								timbre.setTotal(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "subtotal":
								timbre.setSubTotal(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "descuento":
								timbre.setDescuento(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalgravado":

								if (this.leyendoPercepciones)
								{
									timbre.setTotalGravados(new BigDecimal(nodoAtributo.getNodeValue()));
								}
								else if (this.leyendoDeducciones)
								{
									timbre.setTotalGravadosDeduc(new BigDecimal(nodoAtributo.getNodeValue()));
								}
							break;

							case "totalexento":

								if (this.leyendoPercepciones)
								{
									timbre.setTotalExcento(new BigDecimal(nodoAtributo.getNodeValue()));
								}
								else if (this.leyendoDeducciones)
								{
									timbre.setTotalExcentoDeduc(new BigDecimal(nodoAtributo.getNodeValue()));
								}
							break;

							case "totalsueldos":
								timbre.setTotalSueldos(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalimpuestosretenidos":
								timbre.setTotalImpuestosRetenidos(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalotrasdeducciones":
								timbre.setTotalOtrasDeducciones(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

						}

					break;

					case "1.2":

						switch (nodoAtributo.getNodeName().toLowerCase())
						{
							case "folio":
								timbre.setFolio(nodoAtributo.getNodeValue());
							break;

							case "uuid":
								timbre.setUUID(nodoAtributo.getNodeValue());
							break;

							case "nombre":
								if (nodo.getNodeName().contains("cfdi:Receptor"))
								{
									timbre.setReceptor(nodoAtributo.getNodeValue());
								}

							break;

							case "rfc":
								if (nodo.getNodeName().contains("cfdi:Receptor"))
								{
									timbre.setRfc(nodoAtributo.getNodeValue());
								}

							break;

							case "fecha":
								try
								{
									date = sdf1.parse(nodoAtributo.getNodeValue());
								}
								catch (DOMException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								sqlDate = new Date(date.getTime());
								timbre.setFechaComprobante(sqlDate);

							break;

							case "fechatimbrado":
								try
								{
									date = sdf1.parse(nodoAtributo.getNodeValue());
								}
								catch (DOMException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								sqlDate = new Date(date.getTime());
								timbre.setFechaTimbrado(sqlDate);
							break;

							case "nocertificado":
								timbre.setNoCertificado(nodoAtributo.getNodeValue());
							break;

							case "nocertificadosat":
								timbre.setNoCertificadoSAT(nodoAtributo.getNodeValue());
							break;

							case "fechapago":
								try
								{
									date = sdf2.parse(nodoAtributo.getNodeValue());
								}
								catch (DOMException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									this.wr.println(e + "\r\n");
									// e.printStackTrace();
								}
								sqlDate = new Date(date.getTime());
								timbre.setFechaPago(sqlDate);
							break;

							case "clave":
								String valor = nodoAtributo.getNodeValue();

								if (valor.length() < 5)
								{
									this.conceptoAñadiendo = new ConceptoTimbre(1, valor, "", null, "00");
								}
								else
								{
									this.conceptoAñadiendo = new ConceptoTimbre(Integer.parseInt(valor.substring(0, 1)),
											valor.substring(1, 3), "", null, valor.substring(3, 5));
								}

							break;

							case "concepto":
								this.conceptoAñadiendo.setDescripcion(nodoAtributo.getNodeValue());
							break;

							case "importeexento":
								this.conceptoAñadiendo.setImporteExento(new BigDecimal(nodoAtributo.getNodeValue()));

							break;

							case "importegravado":
								this.conceptoAñadiendo.setImporteGravado(new BigDecimal(nodoAtributo.getNodeValue()));

							break;

							case "tipopercepcion":
							case "tipodeduccion":
							case "tipootropago":
								this.conceptoAñadiendo.setTipoPercepDeduc(nodoAtributo.getNodeValue());
								timbre.addConcepto(this.conceptoAñadiendo);

							break;

							case "importe":
								if (this.leyendoDeducciones)
								{
									this.conceptoAñadiendo.setImporteDeduc(new BigDecimal(nodoAtributo.getNodeValue()));
								}

							break;

							case "totalpercepciones":
								timbre.setTotalPercepciones(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totaldeducciones":
								timbre.setTotalDeducciones(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "total":
								timbre.setTotal(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "subtotal":
								timbre.setSubTotal(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "descuento":
								timbre.setDescuento(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalgravado":
								timbre.setTotalGravados(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalexento":
								timbre.setTotalExcento(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalsueldos":
								timbre.setTotalSueldos(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalimpuestosretenidos":
								timbre.setTotalImpuestosRetenidos(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

							case "totalotrasdeducciones":
								timbre.setTotalOtrasDeducciones(new BigDecimal(nodoAtributo.getNodeValue()));
							break;

						}

					break;

				}

			}

			if (nodo.getChildNodes() != null)
			{
				leerNodeList(nodo.getChildNodes(), timbre);
			}

		}

	}

	// -1 Excepción, 0 carpeta válida registrada,
	// 1 carpeta ya registrada,
	// 2 carpeta equivalente no encontrado en productos de nómina
	public int updateCarpetaProductoTimbrado(String carpetaProducto, File rutaTimbre)
	{
		PreparedStatement prep = null;

		String productoEquivalenteNomina = "PRD";

		// PE170121R1 - PA16AGI0B
		this.wr.println("Analizando carpeta: " + carpetaProducto);

		switch (("" + carpetaProducto.charAt(1)))
		{

			case "O":
				productoEquivalenteNomina += "O";
			break;

			case "E":
				productoEquivalenteNomina += "E";
			break;

			case "A":
				productoEquivalenteNomina += "AG";
			break;

			case "R":
				productoEquivalenteNomina += "R";
			break;

			case "C":
				productoEquivalenteNomina += "C";
			break;

			case "V":
				productoEquivalenteNomina += "AV";
			break;

		}
		// PE170121R1 - PA16AGI0B
		switch (carpetaProducto.substring(4, 7))
		{

			case "AGI":
				productoEquivalenteNomina += "I";
			break;

			case "AG1":
				productoEquivalenteNomina += "1";
			break;

			case "AG2":
				productoEquivalenteNomina += "2";
			break;

			default:
				productoEquivalenteNomina += carpetaProducto.substring(4, 7);
			break;

		}

		int idNomina = -1;
		int año = Integer.parseInt(carpetaProducto.substring(2, 4));

		int idProductoNominaCorrespondiente = -1;

		if (carpetaProducto.contains("U0") || carpetaProducto.contains("U1"))
		{
			idNomina = 2;
		}
		else
		{
			switch ("" + carpetaProducto.charAt(carpetaProducto.length() - 1))
			{
				case "B":
					idNomina = 0;
				break;

				case "R":
					idNomina = 2;
				break;

				case "F":
					idNomina = 5;
				break;

				case "C":
					idNomina = 4;
				break;

				case "H":
					idNomina = 6;
				break;

				case "S":
					idNomina = 7;
				break;

				case "A":
					idNomina = -1;
				break;

				case "V":
					idNomina = 8;
				break;

			}
		}

		int indicador = 0;

		try
		{
			indicador = Integer.parseInt("" + carpetaProducto.charAt(carpetaProducto.length() - 2));

		}
		catch (NumberFormatException e)
		{
			this.wr.println(carpetaProducto + ": Producto no localizable para cancelación.");
		}

		this.wr.println("		Equivalente aproximado: " + productoEquivalenteNomina + "\r\n");

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{

			if (indicador > 0)
			{

				// Cancelando los productos anteriores
				for (int x = 0; x < indicador; x++)
				{
					/*
					 * prep = conexion.prepareStatement(
					 * " UPDATE productotimbrado SET idStatus=?, TotalCancelados = TotalRegistros WHERE idProductoTimbrado = ?"
					 * );
					 */
					this.prepProductosCancelacion.setInt(1, -1);
					this.prepProductosCancelacion.setString(2,
							carpetaProducto.substring(0, carpetaProducto.length() - 2) + x
									+ carpetaProducto.charAt(carpetaProducto.length() - 1));

					this.prepProductosCancelacion.addBatch();

					// prep.close();

					this.wr.println("Producto cancelado: " + (carpetaProducto.substring(0, carpetaProducto.length() - 2)
							+ x + carpetaProducto.charAt(carpetaProducto.length() - 1)) + ".\r\n");

				}

			}

			prep = conexion.prepareStatement("SELECT * FROM productotimbrado WHERE idProductoTimbrado=?");

			prep.setString(1, carpetaProducto);

			ResultSet rBD = prep.executeQuery();

			if (!rBD.next())
			{
				prep.close();

				// Se encuentra su producto equivalente en la nómina
				prep = conexion.prepareStatement(
						"SELECT * FROM nominas.producto WHERE idPlaza=? AND Ano=? AND NombreProducto LIKE ?");

				prep.setInt(1, idNomina);
				prep.setInt(2, (2000 + año));
				prep.setString(3, "%" + productoEquivalenteNomina + "%");

				rBD = prep.executeQuery();
				int tipoReturn;

				if (rBD.next())
				{

					idProductoNominaCorrespondiente = rBD.getInt("idProducto");
					this.ultimoIdProductoNomina = idProductoNominaCorrespondiente;

					prep.close();

					/*
					 * prep = conexion.prepareStatement(
					 * " INSERT INTO productotimbrado (idProductoTimbrado, idProductoNomina, TotalPercepciones, TotalDeducciones,"
					 * + " Total, TotalRegistros, idStatus) VALUES ( ?, ?, ?, ?, ?, ?, 0); ");
					 */

					this.prepProductos.setString(1, carpetaProducto);
					this.prepProductos.setInt(2, idProductoNominaCorrespondiente);
					this.prepProductos.setDouble(3, 0);
					this.prepProductos.setDouble(4, 0);
					this.prepProductos.setDouble(5, 0);
					this.prepProductos.setInt(6, 0);

					/*
					 * // Se crea la carpeta del producto timbrado físicamente File
					 * carpetaProductoTimbradoLocal = new File(utilidades.getRutaTimbrado() +
					 * carpetaProducto); carpetaProductoTimbradoLocal.mkdir();
					 */
					// this.wr.println("Producto creado exitosamente.\r\n");

					Producto prodNomina = utilidades.getProducto(idProductoNominaCorrespondiente);
					prodNomina.updatePlantillaDAT_TRA();
					this.ultimaPosNumeroCheque = prodNomina.getPlantillaDAT()
							.getPosicionValorPorDescripcionContains("mero de cheque");

					tipoReturn = 0;
				}
				else
				{
					prep.close();

					this.prepProductos.setString(1, carpetaProducto);
					this.prepProductos.setNull(2, Types.INTEGER);
					this.prepProductos.setDouble(3, 0);
					this.prepProductos.setDouble(4, 0);
					this.prepProductos.setDouble(5, 0);
					this.prepProductos.setInt(6, 0);

					this.wr.println("Producto equivalente no encontrado en los productos de nómina." + "\r\n");
					tipoReturn = 2;

				}

				this.prepProductos.addBatch();

				return tipoReturn;
			}
			else
			{
				// System.out.println("Producto ya encontrado en los producto
				// timbrados.");
				this.wr.println("\r\n");

				return 1;
			}

		}
		catch (Exception e)
		{
			/*
			 * FacesContext.getCurrentInstance().addMessage(null, new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
			 * "Ha ocurrido una excepción al actualizar el producto timbrado (" +
			 * carpetaProducto +
			 * "), favor de contactar con el desarrollador del sistema."));
			 */
			this.wr.println(e + "\r\n");
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
					this.wr.println(e + "\r\n");
					// e.printStackTrace();
				}
			}

		}

		return -1;

	}

	public void actionEliminarProductoTim()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{
			// Se elimina la ruta del producto timbrado
			FileUtils.deleteDirectory(
					new File(utilidades.getRutaTimbrado() + this.productoSelec.getIdProductoTimbrado() + "/"));

			prep = conexion.prepareStatement("DELETE FROM conceptotimbre WHERE idProductoTimbrado=?");
			prep.setString(1, this.productoSelec.getIdProductoTimbrado());
			prep.executeUpdate();
			prep.close();

			prep = conexion.prepareStatement("DELETE FROM timbre WHERE idProductoTimbrado=?");
			prep.setString(1, this.productoSelec.getIdProductoTimbrado());
			prep.executeUpdate();
			prep.close();

			prep = conexion.prepareStatement("DELETE FROM productotimbrado WHERE idProductoTimbrado=? ");
			prep.setString(1, this.productoSelec.getIdProductoTimbrado());
			prep.executeUpdate();
			prep.close();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Producto Eliminado Exitosamente", "El producto se ha eliminado exitosamente del sistema."));

			actionGetProductosTimbrados();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Al eliminar el producto timbrado. Favor de contactar con el desarrollador."));
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
					// e.printStackTrace();
				}
			}

		}

	}

	public void actionGetProductosTimbrados()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.productoSelec = null;
		this.productosTimbrados = new ArrayList<>();
		this.productosTimbradosFilter = null;
		this.productosTimbradosSelec = null;

		this.timbreSelec = null;
		this.timbresFilter = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionTimbrado();)
		{
			/*prep = conexion.prepareStatement(
					" SELECT pt.*,prd.*, pl.descripcion AS descripcionPlaza, tp.descripcion AS descripcionTipoProd, tn.descripcion AS descripcionTipoNom, st.descripcion AS descripcionStatus "
							+ " FROM timbrado.productotimbrado pt, nominas.producto prd, webrh.plaza pl, webrh.tipoproducto tp, webrh.tiponomina tn, status st "
							+ " WHERE pt.idProductoTimbrado LIKE ? AND pt.idProductoNomina = prd.idProducto AND prd.idPlaza = pl.idPlaza "
							+ " AND prd.idTipoProducto = tp.idTipoProducto AND prd.idTipoNomina = tn.idTipoNomina AND pt.idStatus = st.idStatus");
		*/	
			prep = conexion.prepareStatement("SELECT pt.*,prd.*, pl.descripcion AS descripcionPlaza, tp.descripcion AS descripcionTipoProd, tn.descripcion AS descripcionTipoNom, st.descripcion AS descripcionStatus " + 
				"FROM timbrado.productotimbrado pt " + 
				"LEFT JOIN  nominas.producto prd ON pt.idProductoNomina = prd.idProducto " + 
				"LEFT JOIN  webrh.plaza pl ON prd.idPlaza = pl.idPlaza " + 
				"LEFT JOIN webrh.tipoproducto tp ON prd.idTipoProducto = tp.idTipoProducto " + 
				"LEFT JOIN webrh.tiponomina tn ON prd.idTipoNomina = tn.idTipoNomina " + 
				"LEFT JOIN status st ON pt.idStatus = st.idStatus " + 
				"WHERE pt.idProductoTimbrado LIKE ?");

			prep.setString(1, "P_" + (this.añoSeleccionado - 2000) + "%");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					this.productosTimbrados.add(new ProductoTimbrado(rBD.getString("idProductoTimbrado"),
							new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"),
									new Plaza(rBD.getInt("idPlaza"), rBD.getString("DescripcionPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("DescripcionTipoNom")),
									new TipoProducto(rBD.getInt("idTipoProducto"),
											rBD.getString("DescripcionTipoProd")),
									rBD.getString("descripcion")),
							rBD.getBigDecimal("TotalPercepciones"), rBD.getBigDecimal("TotalDeducciones"),
							rBD.getBigDecimal("Total"), rBD.getInt("TotalRegistros"), rBD.getInt("TotalCancelados"),
							null, new Status(rBD.getInt("idStatus"), rBD.getString("descripcionStatus"))));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción",
							"Al encontrar los productos timbrados del año. Favor de contactar con el desarrollador."));
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
					// e.printStackTrace();
				}
			}

		}

	}

	public List<ProductoTimbrado> getProductosTimbrados()
	{
		return productosTimbrados;
	}

	public void setProductosTimbrados(List<ProductoTimbrado> productosTimbrados)
	{
		this.productosTimbrados = productosTimbrados;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public List<ProductoTimbrado> getProductosTimbradosSelec()
	{
		return productosTimbradosSelec;
	}

	public void setProductosTimbradosSelec(List<ProductoTimbrado> productosTimbradosSelec)
	{
		this.productosTimbradosSelec = productosTimbradosSelec;
	}

	public List<ProductoTimbrado> getProductosTimbradosFilter()
	{
		return productosTimbradosFilter;
	}

	public void setProductosTimbradosFilter(List<ProductoTimbrado> productosTimbradosFilter)
	{
		this.productosTimbradosFilter = productosTimbradosFilter;
	}

	public StreamedContent getTxtRespProceso()
	{
		return txtRespProceso;
	}

	public void setTxtRespProceso(StreamedContent txtRespProceso)
	{
		this.txtRespProceso = txtRespProceso;
	}

	public int getAñoSeleccionado()
	{
		return añoSeleccionado;
	}

	public void setAñoSeleccionado(int añoSeleccionado)
	{
		this.añoSeleccionado = añoSeleccionado;
	}

	public List<Integer> getAñosDisponibles()
	{
		return añosDisponibles;
	}

	public void setAñosDisponibles(List<Integer> añosDisponibles)
	{
		this.añosDisponibles = añosDisponibles;
	}

	public ProductoTimbrado getProductoSelec()
	{
		return productoSelec;
	}

	public void setProductoSelec(ProductoTimbrado productoSelec)
	{
		this.productoSelec = productoSelec;
	}

	public List<Timbre> getTimbresSelec()
	{
		return timbresSelec;
	}

	public void setTimbresSelec(List<Timbre> timbresSelec)
	{
		this.timbresSelec = timbresSelec;
	}

	public Timbre getTimbreSelec()
	{
		return timbreSelec;
	}

	public void setTimbreSelec(Timbre timbreSelec)
	{
		this.timbreSelec = timbreSelec;
	}

	public List<Timbre> getTimbresFilter()
	{
		return timbresFilter;
	}

	public void setTimbresFilter(List<Timbre> timbresFilter)
	{
		this.timbresFilter = timbresFilter;
	}

	public TreeNode getCfdiXml()
	{
		return cfdiXml;
	}

	public void setCfdiXml(TreeNode cfdiXml)
	{
		this.cfdiXml = cfdiXml;
	}

	public String getOutputSubirCancelaciones()
	{
		return outputSubirCancelaciones;
	}

	public void setOutputSubirCancelaciones(String outputSubirCancelaciones)
	{
		this.outputSubirCancelaciones = outputSubirCancelaciones;
	}

	public String getOutputSubirYaCancelados()
	{
		return outputSubirYaCancelados;
	}

	public void setOutputSubirYaCancelados(String outputSubirYaCancelados)
	{
		this.outputSubirYaCancelados = outputSubirYaCancelados;
	}

	public String getOutputSubirCancelacionesErrores()
	{
		return outputSubirCancelacionesErrores;
	}

	public void setOutputSubirCancelacionesErrores(String outputSubirCancelacionesErrores)
	{
		this.outputSubirCancelacionesErrores = outputSubirCancelacionesErrores;
	}

	public StreamedContent getTxtRespProcesoCancelaciones()
	{
		return txtRespProcesoCancelaciones;
	}

	public void setTxtRespProcesoCancelaciones(StreamedContent txtRespProcesoCancelaciones)
	{
		this.txtRespProcesoCancelaciones = txtRespProcesoCancelaciones;
	}

	public int getValorProgreso()
	{
		return valorProgreso;
	}

	public void setValorProgreso(int valorProgreso)
	{
		this.valorProgreso = valorProgreso;
	}

	public int getValorProgresoCancelacion()
	{
		return valorProgresoCancelacion;
	}

	public void setValorProgresoCancelacion(int valorProgresoCancelacion)
	{
		this.valorProgresoCancelacion = valorProgresoCancelacion;
	}

	public List<ProductoTimbrado> getProductosEnZip()
	{
		return productosEnZip;
	}

	public void setProductosEnZip(List<ProductoTimbrado> productosEnZip)
	{
		this.productosEnZip = productosEnZip;
	}

	public ProductoConverter getProdConverter()
	{
		return prodConverter;
	}

	public void setProdConverter(ProductoConverter prodConverter)
	{
		this.prodConverter = prodConverter;
	}

}
