/*
s * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import gui.portal.idxArchivoDigital;
import modelo.ArchivoBanco;
import modelo.ArchivoPuesto;
import modelo.BaseSiri;
import modelo.CampoPlantilla;
import modelo.CampoVinculado;
import modelo.CentroResponsabilidad;
import modelo.ColumnModel;
import modelo.Columna;
import modelo.Concepto;
import modelo.ConceptoAsociadoPlaza;
import modelo.ConfiguracionPdf;
import modelo.Empleado;
import modelo.Financiera;
import modelo.FuenteFinanciamiento;
import modelo.InstrumentoPago;
import modelo.Layout;
import modelo.LayoutVersion;
import modelo.PermisoSistema;
import modelo.Plantilla;
import modelo.PlantillaRegistro;
import modelo.Plaza;
import modelo.Producto;
import modelo.ProductoTimbrado;
import modelo.RFCCorreccion;
import modelo.RegistroAhorroSolidario;
import modelo.ReporteAnual;
import modelo.Rubro;
import modelo.Status;
import modelo.TipoNomina;
import modelo.TipoPlantilla;
import modelo.TipoProducto;
import modelo.Unidad;
import modelo.UnidadProducto;
import modelo.Unificacion;
import modelo.Usuario;
import modelo.VinculoPlantilla;
import resources.DataBase;

/**
 *
 * @author desarolloyc
 */
public class utilidades implements Serializable
{

	public static String rutaServerArchivos = "/Users/desarolloyc/Downloads/ArchivosDigitales/";

	public static String[] comandos = new String[] { "pp" };

	public static DecimalFormat formato = new DecimalFormat("$#,###.00");

	public static String ajustaMoneda(String valor1)
	{
		int lastPosComas = -1;
		int lastPosPuntos = -1;

		lastPosComas = valor1.lastIndexOf(",");
		lastPosPuntos = valor1.lastIndexOf(".");

		if (lastPosComas > lastPosPuntos)
		{
			valor1 = valor1.replace(".", "");
			valor1 = valor1.replace(",", ".");

		}
		else
		{
			valor1 = valor1.replace(",", "");
		}

		return valor1;
	}

	public static HashMap sortDATByRFC(HashMap map)
	{
		List list = new LinkedList(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator()
		{

			public int compare(Object o1, Object o2)
			{
				PlantillaRegistro reg1 = (PlantillaRegistro) ((Map.Entry) (o1)).getValue();
				PlantillaRegistro reg2 = (PlantillaRegistro) ((Map.Entry) (o2)).getValue();

				return ((Comparable) reg1.getValorPorDescripcionContains("RFC"))
						.compareTo(reg2.getValorPorDescripcionContains("RFC"));
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

	public static String formatoMoneda(String cantidad)
	{
		String cantidadFormato = null;

		try
		{
			cantidadFormato = formato.format(Double.parseDouble(cantidad));
		}
		catch (Exception e)
		{
			cantidadFormato = "NaN";
		}

		return cantidadFormato;
	}

	public static void eliminarRuta(File ruta)
	{
		File[] archivos = null;

		if (ruta.isDirectory())
		{
			archivos = ruta.listFiles();

			if (archivos.length > 0)
			{
				for (File archivo : archivos)
				{
					eliminarRuta(archivo);
				}

			}
		}

		ruta.delete();

	}

	// De igual forma elimina las tabulaciones
	public static String reemplazarCaracteresExtraños(String valor)
	{
		valor = valor.replace((char) 157, 'Ñ');
		valor = valor.replace((char) 209, 'Ñ');
		valor = valor.replaceAll("&", "Ñ");
		valor = valor.replaceAll("\t", " ");

		return valor;
	}

	public static String getRutaExpendientesServer()
	{
		ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

		return ext.getRealPath("/resources/exp/");

	}

	public static String getRutaReportesRepvServer()
	{
		ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

		return ext.getRealPath("/resources/reportes/");

	}

	public static String getRutaTimbrado()
	{
		ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

		return ext.getRealPath("/resources/timbrado/");

	}

	public static String getRutaTimbradoTemp()
	{
		ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

		return ext.getRealPath("/resources/timbrado/temp/");

	}

	public static String getRutaTimbradoTempRespuestas()
	{
		ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();

		return ext.getRealPath("/resources/timbrado/temp/resp/");

	}

	public static String MD5(String md5)
	{
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");

			byte[] array = md.digest(md5.getBytes());

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < array.length; ++i)
			{
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}

			return sb.toString();

		}
		catch (java.security.NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return null;

	}

	public static List<ColumnModel> getColumnModel(Plantilla plan)
	{
		List<ColumnModel> columnas = new ArrayList<>();

		for (CampoPlantilla campo : plan.getCampos())
		{
			columnas.add(new ColumnModel("" + campo.getDescripcion(), "" + (campo.getOrden() - 1)));
		}

		return columnas;

	}

	public static void registrarEnBitacora(int idUsuario, String NumEmpleado, int idPlaza, String movimiento,
			String comentario)
	{

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
		{
			java.util.Date utilDate = new java.util.Date();
			java.sql.Date sqlFecha = new java.sql.Date(utilDate.getTime());
			java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());

			PreparedStatement prep = conexion.prepareStatement(
					"INSERT INTO bitacoraexpediente (idUsuario,NumEmpleado,Movimiento,Comentarios,Fecha,Hora,idPlaza) VALUES (?, ?, ?, ?, ?, ?, ?)");

			prep.setInt(1, idUsuario);
			prep.setString(2, NumEmpleado);
			prep.setString(3, movimiento);
			prep.setString(4, comentario);
			prep.setDate(5, sqlFecha);
			prep.setTime(6, sqlTime);
			prep.setInt(7, idPlaza);

			prep.executeUpdate();

		}
		catch (Exception e)
		{
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"Excepción en la Bitácora",
					"Se ha generado una excepción al registrar el movimiento en la bitácora, favor de contactar con el desarrollador del sistema."));
		}

	}

	// Métodos de utilidad en el sistema para obtener usuarios, permisos, etc y
	// denpendiendo de lo que se vaya necesotando dentro del desarrollo de la
	// aplicación se va a ir icnluyendo poco a poco
	public static StreamedContent getImagenStreamed(String rutaServer, String tituloImagen)
	{
		try
		{
			File directorioimagen = new File(rutaServer);

			if (!directorioimagen.exists())
			{
				return null;
			}

			String[] archivos = directorioimagen.list();

			for (String archivo : archivos)
			{
				if (archivo.contains(tituloImagen))
				{
					FileInputStream fis = new FileInputStream(rutaServer + "/" + archivo);

					DefaultStreamedContent nuevo = new DefaultStreamedContent(fis, "image/jpeg", tituloImagen);
					return nuevo;

				}

			}

		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			Logger.getLogger(idxArchivoDigital.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	public static boolean creaUploadedADisco(String rutaDestino, String nombreArchivo, UploadedFile uploadedFile)
	{

		try
		{

			String nombreArchivoUploaded = uploadedFile.getFileName();

			String extension = nombreArchivoUploaded.substring(nombreArchivoUploaded.lastIndexOf("."),
					nombreArchivoUploaded.length());

			File archivo = new File(rutaDestino);

			if (!archivo.exists())
			{
				archivo.mkdirs();
			}

			InputStream input = uploadedFile.getInputstream();
			OutputStream output = new FileOutputStream(new File(rutaDestino, nombreArchivo + extension));

			try
			{
				IOUtils.copy(input, output);
			}
			finally
			{
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static List<Usuario> getUsuariosRegistrados()
	{
		List<Usuario> usuarios = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					" SELECT u.*, pp.Descripcion AS DescripcionNivelPermiso, s.Descripcion AS DescripcionStatus FROM usuario u, permisoportal pp, status s WHERE u.NivelPermiso = pp.idPermiso AND u.idStatus = s.idStatus ORDER BY u.idUsuario ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				usuarios = new ArrayList<>();

				do
				{
					usuarios.add(new Usuario(rBD.getInt("idUsuario"), rBD.getString("Nombre"), null,
							rBD.getInt("NivelPermiso"), rBD.getString("DescripcionNivelPermiso"),
							rBD.getInt("idStatus"), rBD.getString("DescripcionStatus"), rBD.getString("NombreReal"),
							rBD.getString("CuentaCorreo"), rBD.getDate("VigenciaInicial"),
							rBD.getDate("VigenciaFinal")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return usuarios;
	}

	public static List<PermisoSistema> getPermisosSistemasUsuario(int idUsuario)
	{

		List<PermisoSistema> permisos = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			PreparedStatement prep = conexion.prepareStatement(
					"SELECT pus.idSistema, s.Descripcion AS DescripcionSistema, pus.idPermiso, ps.Descripcion AS DescripcionPermisoSistema FROM permisousuariosistema pus, sistemaplataforma s, permisosistema ps WHERE pus.idSistema = s.idSistema AND pus.idPermiso = ps.idPermiso AND pus.idUsuario=? ORDER BY pus.idSistema ASC");

			prep.setInt(1, idUsuario);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				permisos = new ArrayList<>();

				do
				{
					permisos.add(new PermisoSistema(rBD.getInt("idSistema"), rBD.getString("DescripcionSistema"),
							rBD.getInt("idPermiso"), rBD.getString("DescripcionPermisoSistema")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return permisos;
	}

	// SE UTILIZA PARA PROVEER LA ESTRUCTURA DE ESQUEMA DE PERMISOS DE LOS
	// SISTEMAS DENTRO DE LA PLATAFORMA PARA NUEVO USUARIO
	public static List<PermisoSistema> getEstructuraPermisosSistemas()
	{

		List<PermisoSistema> permisos = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			PreparedStatement prep = conexion.prepareStatement("SELECT * FROM sistemaplataforma ORDER BY idSistema");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				permisos = new ArrayList<>();

				do
				{
					permisos.add(
							new PermisoSistema(rBD.getInt("idSistema"), rBD.getString("Descripcion"), -1, "Ninguno"));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return permisos;
	}

	public static List<SelectItem> getStatus()
	{
		List<SelectItem> status = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM status ORDER BY Descripcion ASC");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				status = new ArrayList<>();

				do
				{
					status.add(new SelectItem(rBD.getInt("idStatus"), rBD.getString("Descripcion")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return status;
	}

	public static List<SelectItem> getPermisosPortal()
	{
		List<SelectItem> permisosPortal = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM permisoportal ORDER BY idPermiso DESC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				permisosPortal = new ArrayList<>();

				do
				{
					permisosPortal.add(new SelectItem(rBD.getInt("idPermiso"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return permisosPortal;
	}

	public static List<SelectItem> getPermisosSistema()
	{
		List<SelectItem> permisosSistema = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM permisosistema ORDER BY idPermiso ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				permisosSistema = new ArrayList<>();

				do
				{
					permisosSistema.add(new SelectItem(rBD.getInt("idPermiso"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return permisosSistema;
	}

	public static List<SelectItem> getPlazasSistema()
	{
		List<SelectItem> plazas = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement("SELECT * FROM plaza ORDER BY idPlaza ASC");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				plazas = new ArrayList<>();

				do
				{
					plazas.add(new SelectItem(rBD.getInt("idPlaza"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return plazas;
	}

	public static List<Plaza> getPlazasConUnidadesYFuentesFinancimiento()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<Plaza> plazas = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			plazas = utilidades.getPlazas();

			for (Plaza pl : plazas)
			{
				pl.setUnidades(utilidades.getUnidadesPlaza(pl.getIdPlaza()));

				if (pl.getUnidades() == null)
				{
					continue;
				}

				for (Unidad un : pl.getUnidades())
				{
					prep = conexion.prepareStatement(
							" SELECT uff.*, u.Descripcion AS descripcionUnidad, pl.descripcion AS descripcionPlaza, ff.descripcion AS descripcionFuenteFinanciamiento, ff.idRegistro, ff.idFuenteFinanciamiento FROM webrh.unidadfuentefinanciamiento AS uff, webrh.unidad u, webrh.plaza pl, webrh.fuentefinanciamiento ff WHERE uff.idUnidad = u.idUnidad AND uff.idPlaza = pl.idPlaza AND uff.idRegistroFuenteFinanciamiento = ff.idFuenteFinanciamiento AND uff.idPlaza=? AND uff.idUnidad=? ");

					prep.setInt(1, pl.getIdPlaza());
					prep.setInt(2, un.getIdUnidad());

					rBD = prep.executeQuery();

					if (rBD.next())
					{
						un.setFuenteFinanciamiento(new FuenteFinanciamiento(rBD.getInt("idRegistro"),
								rBD.getString("idFuenteFinanciamiento"),
								rBD.getString("descripcionFuenteFinanciamiento")));

					}

				}

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

		return plazas;
	}

	public static List<Plaza> getPlazas()
	{
		List<Plaza> plazas = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement("SELECT * FROM plaza ORDER BY idPlaza ASC");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				plazas = new ArrayList<>();

				do
				{
					plazas.add(new Plaza(rBD.getInt("idPlaza"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return plazas;
	}

	public static List<Unidad> getUnidades()
	{
		List<Unidad> unidades = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement("SELECT * FROM unidad");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				unidades = new ArrayList<>();

				do
				{
					unidades.add(new Unidad(rBD.getInt("idUnidad"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return unidades;
	}

	public static List<Unidad> getUnidadesPlaza(int idPlaza)
	{
		List<Unidad> unidades = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					"SELECT up.*, p.descripcion FROM unidadplaza up, unidad p WHERE up.idUnidad = p.idUnidad AND up.idPlaza=?");

			prep.setInt(1, idPlaza);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				unidades = new ArrayList<>();

				do
				{
					unidades.add(new Unidad(rBD.getInt("idUnidad"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return unidades;
	}

	public static List<BaseSiri> getBasesSiri()
	{
		List<BaseSiri> basesSiri = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{

			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM basesiri ORDER BY idBaseSiri DESC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				basesSiri = new ArrayList<>();

				do
				{
					basesSiri.add(new BaseSiri(rBD.getInt("idBaseSiri"), rBD.getDate("Fecha"), rBD.getTime("Hora"),
							rBD.getString("Comentarios")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return basesSiri;

	}

	public static List<CampoPlantilla> getCamposBD()
	{
		List<CampoPlantilla> campos = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion
					.prepareStatement(" SELECT * FROM campo ORDER BY LENGTH(Descripcion),Descripcion ASC");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				campos = new ArrayList<>();

				do
				{
					campos.add(new CampoPlantilla(rBD.getInt("idCampo"), rBD.getString("Descripcion"),
							rBD.getString("Tipo"), rBD.getInt("Entero"), rBD.getInt("Decimales")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return campos;

	}

	public static List<Layout> getLayoutsBD()
	{
		List<Layout> layouts = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM layout");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				layouts = new ArrayList<>();

				do
				{
					layouts.add(new Layout(rBD.getInt("idLayout"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return layouts;

	}

	public static List<Layout> getLayoutsBDExcluyendoSARSIRI()
	{
		List<Layout> layouts = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion
					.prepareStatement(" SELECT * FROM layout WHERE idLayout IN (5, 6) = FALSE");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				layouts = new ArrayList<>();

				do
				{
					layouts.add(new Layout(rBD.getInt("idLayout"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return layouts;

	}

	public static List<Layout> getLayoutsInstrumentosPago()
	{
		List<Layout> layouts = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion
					.prepareStatement(" SELECT * FROM layout WHERE idLayout IN (11, 12, 13, 14) ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				layouts = new ArrayList<>();

				do
				{
					layouts.add(new Layout(rBD.getInt("idLayout"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return layouts;

	}

	public static Layout getLayoutsBD(int idLayout)
	{
		Layout layout = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM layout WHERE idLayout=?");
			prep.setInt(1, idLayout);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				layout = new Layout(rBD.getInt("idLayout"), rBD.getString("Descripcion"));

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return layout;

	}

	public static List<LayoutVersion> getVersionesLayoutsBD(int idLayout)
	{
		List<LayoutVersion> versiones = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM layoutversion WHERE idLayout=? ");
			prep.setInt(1, idLayout);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				versiones = new ArrayList<>();

				do
				{
					versiones.add(new LayoutVersion(idLayout, rBD.getString("version"), rBD.getDate("FechaEmision")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return versiones;

	}

	// Devuleve una lista de LayoutVersion con un solo resultado ya que se está
	// buscando específicamente una versión con el idPlantilla
	public static List<LayoutVersion> getVersionLayoutsBD(int idPlantilla)
	{
		List<LayoutVersion> versiones = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion
					.prepareStatement(" SELECT pl.*, lv.FechaEmision FROM webrh.plantilla pl, webrh.layoutversion lv "
							+ "WHERE pl.idPlantilla=? AND pl.idLayout= lv.idLayout AND pl.Version = lv.Version ");

			prep.setInt(1, idPlantilla);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				versiones = new ArrayList<>();

				do
				{
					versiones.add(new LayoutVersion(rBD.getInt("idLayout"), rBD.getString("version"),
							rBD.getDate("FechaEmision")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return versiones;

	}

	// tipo plantilla 1 para encabezado y 2 para detalle ya sea encabezado o
	// detalle (el detalle es los tipos de tipos de movimiento para los anexos
	// correspondiente)
	// obtiene las plantillas pero sin los campos correspondientes, esto para
	// solamente la presentación de los datos en pantalla
	public static List<Plantilla> getPlantillasLayout(int idLayout, String version, int idTipoPlantilla)
	{
		List<Plantilla> plantillas = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					"SELECT p.*, tp.Descripcion AS descripcionTipoPlantilla  FROM plantilla p, tipoplantilla tp WHERE p.idTipoPlantilla = tp.idTipoPlantilla AND p.idLayout=? AND p.Version=? AND p.idTipoPlantilla=? ");

			prep.setInt(1, idLayout);
			prep.setString(2, version);
			prep.setInt(3, idTipoPlantilla);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				plantillas = new ArrayList<>();

				do
				{
					plantillas.add(new Plantilla(rBD.getInt("idPlantilla"),
							new TipoPlantilla(rBD.getInt("idTipoPlantilla"), rBD.getString("descripcionTipoPlantilla")),
							rBD.getString("Descripcion"), null, rBD.getString("Separador")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return plantillas;

	}

	public static Plantilla getPlantillaLayout(int idPlantilla)
	{
		Plantilla plantillas = null;

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement(
					"SELECT p.*, tp.Descripcion AS descripcionTipoPlantilla  FROM plantilla p, tipoplantilla tp WHERE p.idTipoPlantilla = tp.idTipoPlantilla AND p.idPlantilla=? ");

			prep.setInt(1, idPlantilla);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				plantillas = new Plantilla(rBD.getInt("idPlantilla"),
						new TipoPlantilla(rBD.getInt("idTipoPlantilla"), rBD.getString("descripcionTipoPlantilla")),
						rBD.getString("Descripcion"), null, rBD.getString("Separador"));

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

		return plantillas;

	}

	public static List<CampoPlantilla> getCamposPlantilla(int idPlantilla)
	{
		List<CampoPlantilla> campos = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					" SELECT pd.*, c.* FROM plantilladatos pd, campo c WHERE pd.idPlantilla = ? AND pd.idCampo = c.idCampo ORDER BY pd.Orden ASC ");

			prep.setInt(1, idPlantilla);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				campos = new ArrayList<>();

				do
				{
					CampoPlantilla nCampo = new CampoPlantilla(rBD.getInt("idCampo"), rBD.getString("Descripcion"),
							rBD.getString("Tipo"), rBD.getInt("Entero"), rBD.getInt("Decimales"), null,
							rBD.getInt("Orden"), null);

					if (rBD.getBoolean("indice"))
					{
						nCampo.setIndice(true);
					}

					// Se elimina esta asignación, ya que ahora etá relacionado
					// con el vínculo creado la inclusión o exclusión de retiro
					// y vivienda
					/*
					 * if (rBD.getBoolean("retiro")) { nCampo.setRetiro(true); } if
					 * (rBD.getBoolean("vivienda")) { nCampo.setVivienda(true); }
					 */
					campos.add(nCampo);

					// campos.add(new CampoPlantilla(rBD.getInt("idCampo"),
					// rBD.getString("Descripcion"), rBD.getString("Tipo"),
					// rBD.getInt("Entero"), rBD.getInt("Decimales"), null,
					// rBD.getInt("Orden"), null));
				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return campos;

	}

	// Se obtienen los campos vinculados de una plantilla especificando el id
	// del vinculo al que pertenece el vínculo
	public static void updateCamposVinculados(Plantilla plantilla, int idVinculo)
	{
		// Primero se obtiene las plantillas (SIRI y SAR100) a las que se
		// encuentra vinculada la plantilla

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{

			PreparedStatement prep = null;
			ResultSet rBD = null;

			int idPlantillaSiriVinculado = -1;
			int idPlantillaSarVinculado = -1;

			prep = conexion.prepareStatement(
					" SELECT DISTINCT(idPlantillaVinculo), p.idLayout FROM anexodatosvinculados ad, webrh.plantilla p WHERE ad.idPlantilla=? AND ad.idVinculoPlantilla=? AND ad.idPlantillaVinculo = p.idPlantilla AND ( ad.idPlantillaVinculo IS  NULL ) = FALSE ");
			prep.setInt(1, plantilla.getIdPlantilla());
			prep.setInt(2, idVinculo);

			rBD = prep.executeQuery();

			if (rBD.next())
			{

				do
				{
					switch (rBD.getInt("idLayout"))
					{
						case 5:

							idPlantillaSiriVinculado = rBD.getInt("idPlantillaVinculo");

							break;

						case 6:

							idPlantillaSarVinculado = rBD.getInt("idPlantillaVinculo");

							break;

					}

				} while (rBD.next());

			}

			// se crean las plantillas de vínculo
			if (idPlantillaSiriVinculado > -1)
			{
				plantilla.setSiriVinculado(new Plantilla(idPlantillaSiriVinculado, plantilla.getTipoPlantilla(), "SIRI",
						null, plantilla.getCaracterSeparador()));
				plantilla.getSiriVinculado().updateCampos();
			}

			if (idPlantillaSarVinculado > -1)
			{
				plantilla.setSarVinculado(new Plantilla(idPlantillaSarVinculado, plantilla.getTipoPlantilla(), "SAR",
						null, plantilla.getCaracterSeparador()));
				plantilla.getSarVinculado().updateCampos();

			}

			for (CampoPlantilla campo : plantilla.getCampos())
			{
				prep = conexion.prepareStatement(
						" SELECT * FROM anexodatosvinculados WHERE idVinculoPlantilla=? AND idPlantilla=? AND Orden=? ");

				prep.setInt(1, idVinculo);
				prep.setInt(2, plantilla.getIdPlantilla());
				prep.setInt(3, campo.getOrden());

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					campo.setCampoVinculado(new CampoVinculado(rBD.getInt("idPlantillaVinculo"),
							rBD.getInt("OrdenVinculo"), null, null));

					if (rBD.getString("ValorDefecto") != null && !rBD.getString("ValorDefecto").equals("-1"))
					{
						campo.getCampoVinculado().setValorPorDefecto(rBD.getString("ValorDefecto"));
						continue;
					}

					// se busca a la plantilla a la que pertenece, si es siri o
					// sar
					if (plantilla.getSiriVinculado() != null)
					{
						if (campo.getCampoVinculado().getIdPlantilla() == plantilla.getSiriVinculado().getIdPlantilla())
						{
							CampoPlantilla campoVinc = plantilla.getSiriVinculado()
									.getCampoOrden(rBD.getInt("OrdenVinculo"));

							if (campoVinc != null)
							{
								campo.getCampoVinculado().setDescripcionYVersionPlantilla("SIRI");
								campo.getCampoVinculado().setCampo(campoVinc);
								continue;
							}
						}

					}

					if (plantilla.getSarVinculado() != null)
					{
						if (campo.getCampoVinculado().getIdPlantilla() == plantilla.getSarVinculado().getIdPlantilla())
						{
							CampoPlantilla campoVinc = plantilla.getSarVinculado()
									.getCampoOrden(rBD.getInt("OrdenVinculo"));

							if (campoVinc != null)
							{
								campo.getCampoVinculado().setDescripcionYVersionPlantilla("SAR");
								campo.getCampoVinculado().setCampo(campoVinc);
							}
						}

					}

				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static int getNumTrabajadoresPlantilla(int idBaseSiri)
	{
		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep = conexion
					.prepareStatement(" SELECT MAX(idregistro) FROM plantillasirivalores WHERE idBaseSiri=? ");

			prep.setInt(1, idBaseSiri);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				return rBD.getInt("MAX(idregistro)");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	// idPlantilla se enviará por separado el encabezado y la plantilla de
	// detalle
	// La plantilla ya debe de venir con la actualización de los campos, es
	// decir, con los campos ya actualizados en orden y demás con el método
	// correspondiente
	public static List<PlantillaRegistro> getPlantillaBimestre(int idPlaza, int idUnidad, int año, int bimestre,
			Plantilla plan)
	{

		List<PlantillaRegistro> plantilla = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep;
			ResultSet rBD;

			int registros = 0;

			prep = conexion.prepareStatement(
					" SELECT COUNT(*) AS maximo FROM (SELECT idRegistro FROM plantillavalores WHERE idPlaza=? AND idUnidad=? AND ano=? AND bimestre=? AND idPlantilla=? GROUP BY idRegistro ) AS tabla ");

			prep.setInt(1, idPlaza);
			prep.setInt(2, idUnidad);
			prep.setInt(3, año);
			prep.setInt(4, bimestre);
			prep.setInt(5, plan.getIdPlantilla());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				registros = rBD.getInt("maximo");

				plantilla = new ArrayList<>();

			}

			int nRegistro = 1;
			String consultaBase = " SELECT *  FROM plantillavalores WHERE idPlaza=? AND idUnidad=? AND ano=? AND bimestre=? AND idPlantilla=?  AND idRegistro=? AND Orden IN(";
			String consultaBaseFinal = " ORDER BY idRegistro ASC, Orden ASC";
			String consultaComplemento = null;

			while (nRegistro <= registros)
			{

				PlantillaRegistro plantillaR = new PlantillaRegistro(nRegistro, plan.getClone());
				plantilla.add(plantillaR);

				List<CampoPlantilla> campos = plantillaR.getPlantilla().getCampos();

				int totalCampos = campos.size();
				consultaComplemento = "";

				for (int x = 0; x < totalCampos; x++)
				{
					consultaComplemento += "?,";
				}

				consultaComplemento = consultaComplemento.substring(0, consultaComplemento.length() - 1);
				consultaComplemento += ")";

				prep = conexion.prepareStatement(consultaBase + consultaComplemento + consultaBaseFinal);
				prep.setInt(1, idPlaza);
				prep.setInt(2, idUnidad);
				prep.setInt(3, año);
				prep.setInt(4, bimestre);
				prep.setInt(5, plan.getIdPlantilla());
				prep.setInt(6, nRegistro);

				for (CampoPlantilla campo : campos)
				{
					prep.setInt((6 + campo.getOrden()), campo.getOrden());
				}

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					do
					{
						plantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

					} while (rBD.next());

				}

				nRegistro++;

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return plantilla;

	}

	public static List<String> getDatosVersionDescripcionPlantillaBaseSiri(int idBaseSiri)
	{
		int idPlantilla = -1;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();
				Connection conexionSIRI = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{

			PreparedStatement prep = conexionSIRI
					.prepareStatement(" SELECT idPlantilla FROM plantillasirivalores WHERE idBaseSiri=? LIMIT 1 ");
			prep.setInt(1, idBaseSiri);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				idPlantilla = rBD.getInt("idPlantilla");

			}

			prep = conexion.prepareStatement(" SELECT Version, Descripcion FROM plantilla WHERE idPlantilla=? ");
			prep.setInt(1, idPlantilla);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				List<String> datos = new ArrayList<>();

				datos.add(rBD.getString("Version"));
				datos.add(rBD.getString("Descripcion"));
				datos.add("" + idPlantilla);
				return datos;

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;

	}

	public static List<PlantillaRegistro> getPlantillaRegistrosSIRI(int idBaseSIRI, Plantilla plan)
	{

		List<PlantillaRegistro> plantilla = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep;
			ResultSet rBD;

			int registros = 0;

			prep = conexion.prepareStatement(
					" SELECT COUNT(*) AS maximo FROM ( SELECT idRegistro FROM plantillasirivalores WHERE idBaseSiri=? AND idPlantilla=?  GROUP BY idRegistro) AS TABLA ");

			prep.setInt(1, idBaseSIRI);
			prep.setInt(2, plan.getIdPlantilla());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				registros = rBD.getInt("maximo");

				plantilla = new ArrayList<>();

			}

			int nRegistro = 1;
			String consultaBase = " SELECT * FROM plantillasirivalores WHERE idBaseSiri=? AND idPlantilla=?  AND idRegistro=? AND Orden IN (";
			String consultaComplemento = null;
			int totalCampos = 0;

			while (nRegistro <= registros)
			{

				PlantillaRegistro plantillaR = new PlantillaRegistro(nRegistro, plan.getClone());
				plantilla.add(plantillaR);

				List<CampoPlantilla> campos = plantillaR.getPlantilla().getCampos();
				totalCampos = campos.size();
				consultaComplemento = "";

				for (int x = 0; x < totalCampos; x++)
				{
					consultaComplemento += "?,";
				}

				consultaComplemento = consultaComplemento.substring(0, consultaComplemento.length() - 1);
				consultaComplemento += ")";
				prep = conexion.prepareStatement(consultaBase + consultaComplemento);

				prep.setInt(1, idBaseSIRI);
				prep.setInt(2, plan.getIdPlantilla());
				prep.setInt(3, nRegistro);

				for (CampoPlantilla campo : campos)
				{
					prep.setInt((3 + campo.getOrden()), campo.getOrden());
				}

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					do
					{
						plantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

					} while (rBD.next());

				}

				nRegistro++;

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return plantilla;

	}

	public static List<CampoPlantilla> getCamposInstitucionales()
	{
		List<CampoPlantilla> campos = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					" SELECT di.*, c.* FROM datosinstitucionales di, webrh.campo c WHERE di.idCampo = c.idCampo ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				campos = new ArrayList<>();

				do
				{
					CampoPlantilla nCampo = new CampoPlantilla(rBD.getInt("idCampo"), rBD.getString("Descripcion"),
							rBD.getString("Tipo"), rBD.getInt("Entero"), rBD.getInt("Decimales"),
							rBD.getString("Valor"), -1, null);

					campos.add(nCampo);

					// campos.add(new CampoPlantilla(rBD.getInt("idCampo"),
					// rBD.getString("Descripcion"), rBD.getString("Tipo"),
					// rBD.getInt("Entero"), rBD.getInt("Decimales"), null,
					// rBD.getInt("Orden"), null));
				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return campos;

	}

	// Métodos para el ahorro solidario
	public static List<RegistroAhorroSolidario> getRegistrosAhorroSolidario()
	{
		List<RegistroAhorroSolidario> registrosAhorro = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM ahorrosolidario ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				do
				{
					registrosAhorro.add(new RegistroAhorroSolidario(rBD.getString("CURP"), rBD.getInt("Porcentaje")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return registrosAhorro;

	}

	public static List<String> getCURPSDisponiblesParaAhorroSolidario()
	{
		List<String> curpsDisponibles = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					" SELECT DISTINCT(Valor) FROM plantillasirivalores WHERE Orden IN( SELECT Orden FROM webrh.plantilladatos WHERE idPlantilla IN ( SELECT idPlantilla FROM webrh.plantilla WHERE Descripcion LIKE '%siri%') AND idCampo IN ( SELECT idCampo FROM webrh.campo WHERE Descripcion LIKE '%CURP%' ) ) ORDER BY length(Valor) ASC,Valor ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{

				do
				{
					curpsDisponibles.add(rBD.getString("Valor"));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return curpsDisponibles;
	}

	// Métodos para la sección de Productos de Nómina
	public static List<TipoNomina> getTiposNomina()
	{
		List<TipoNomina> tipos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM tiponomina ORDER BY Descripcion ASC");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					tipos.add(new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return tipos;

	}

	public static List<TipoProducto> getTiposProducto()
	{
		List<TipoProducto> tipos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM tipoproducto ORDER BY Descripcion ASC");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					tipos.add(new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("Descripcion")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return tipos;

	}

	// Método que obtiene o actualiza las plantillas DAT y TRA sin datos
	public static void updateProductoPlantillaDATTRA(Producto prod)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion.prepareStatement(
					" SELECT DISTINCT(idPlantilla) FROM nominas.datvalores WHERE idProducto=? LIMIT 1");
			prep.setInt(1, prod.getIdProducto());
			rBD = prep.executeQuery();

			if (rBD.next())
			{
				prod.setPlantillaDAT(utilidades.getPlantillaLayout(rBD.getInt("idPlantilla")));
				prod.getPlantillaDAT().updateCampos();

			}

			prep.close();

			prep = conexion.prepareStatement(
					" SELECT DISTINCT(idPlantilla) FROM nominas.travalores WHERE idProducto=? LIMIT 1");
			prep.setInt(1, prod.getIdProducto());
			rBD = prep.executeQuery();

			if (rBD.next())
			{
				prod.setPlantillaTRA(utilidades.getPlantillaLayout(rBD.getInt("idPlantilla")));
				prod.getPlantillaTRA().updateCampos();

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
					e.printStackTrace();
				}
			}

		}

	}

	public static List<Producto> getProductos()
	{
		List<Producto> productos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					"  SELECT p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina FROM nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl WHERE p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza ORDER BY p.NombreProducto DESC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					productos
							.add(new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
									new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
									rBD.getString("NombreProducto")));

				} while (rBD.next());

				/*
				 * for (Producto prod : productos) { prep = conexion.prepareStatement(
				 * " SELECT DISTINCT(idPlantilla) FROM nominas.datvalores WHERE idProducto=? LIMIT 1"
				 * ); prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
				 * (rBD.next()) { prod.setPlantillaDAT(utilidades.getPlantillaLayout(rBD.getInt
				 * ("idPlantilla"))); prod.getPlantillaDAT().updateCampos(); } prep =
				 * conexion.prepareStatement(
				 * " SELECT DISTINCT(idPlantilla) FROM nominas.travalores WHERE idProducto=? LIMIT 1"
				 * ); prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
				 * (rBD.next()) { prod.setPlantillaTRA(utilidades.getPlantillaLayout(rBD.getInt
				 * ("idPlantilla"))); prod.getPlantillaTRA().updateCampos(); } }
				 */

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return productos;

	}

	// Obtiene un producto por ID
	public static Producto getProducto(int idProducto)
	{
		Producto producto = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					"  SELECT p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina FROM nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl WHERE p.idProducto=? AND p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza ORDER BY p.NombreProducto DESC ");

			prep.setInt(1, idProducto);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				producto = new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
						rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
						new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
						new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
						rBD.getString("NombreProducto"));
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return producto;

	}

	public static List<Producto> getProductos(int qnaInicial, int qnaFinal, int año, int idPlaza)
	{
		List<Producto> productos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					"  SELECT p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina FROM nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl WHERE p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza  AND p.Ano=? AND p.Qna>=? AND p.Qna<=? AND p.idPlaza=?  ORDER BY p.NombreProducto DESC ");

			prep.setInt(1, año);
			prep.setInt(2, qnaInicial);
			prep.setInt(3, qnaFinal);
			prep.setInt(4, idPlaza);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					productos
							.add(new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
									new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
									rBD.getString("NombreProducto")));

				} while (rBD.next());

				/*
				 * for (Producto prod : productos) { prep = conexion.prepareStatement(
				 * " SELECT DISTINCT(idPlantilla) FROM nominas.datvalores WHERE idProducto=? LIMIT 1"
				 * ); prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
				 * (rBD.next()) { prod.setPlantillaDAT(utilidades.getPlantillaLayout(rBD.getInt
				 * ("idPlantilla"))); prod.getPlantillaDAT().updateCampos(); } prep =
				 * conexion.prepareStatement(
				 * " SELECT DISTINCT(idPlantilla) FROM nominas.travalores WHERE idProducto=? LIMIT 1"
				 * ); prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
				 * (rBD.next()) { prod.setPlantillaTRA(utilidades.getPlantillaLayout(rBD.getInt
				 * ("idPlantilla"))); prod.getPlantillaTRA().updateCampos(); } }
				 */

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return productos;

	}

	// Consulta (año-nombre del producto) junto y sin espacios
	public static List<Producto> getProductos(String consulta)
	{
		List<Producto> productos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					"SELECT p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina "
							+ "FROM nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl "
							+ "WHERE p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza AND CONCAT(p.Ano,p.NombreProducto,pl.descripcion) LIKE ? ");

			prep.setString(1, "%" + consulta + "%");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					productos
							.add(new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
									new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
									rBD.getString("NombreProducto")));

				} while (rBD.next());

				/*
				 * for (Producto prod : productos) { prep = conexion.prepareStatement(
				 * " SELECT DISTINCT(idPlantilla) FROM nominas.datvalores WHERE idProducto=? LIMIT 1"
				 * ); prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
				 * (rBD.next()) { prod.setPlantillaDAT(utilidades.getPlantillaLayout(rBD.getInt
				 * ("idPlantilla"))); prod.getPlantillaDAT().updateCampos(); } prep =
				 * conexion.prepareStatement(
				 * " SELECT DISTINCT(idPlantilla) FROM nominas.travalores WHERE idProducto=? LIMIT 1"
				 * ); prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
				 * (rBD.next()) { prod.setPlantillaTRA(utilidades.getPlantillaLayout(rBD.getInt
				 * ("idPlantilla"))); prod.getPlantillaTRA().updateCampos(); } }
				 */

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return productos;

	}

	// tipoProducto para obtener ya sea cancelados u ordinarios
	public static List<Producto> getProductosAño(int año, int quincena, boolean incluirProductos,
			boolean incluirCancelados, int nomina, boolean incluirPension)
	{
		List<Producto> productos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = null;

			String sentencia = "  SELECT p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina "
					+ "FROM nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl "
					+ "WHERE p.ano=? AND p.qna=? AND p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza  ";

			if (incluirProductos)
			{
				sentencia += " AND ( p.idTipoProducto!=4 )";
			}

			if (incluirCancelados)
			{

				if (incluirProductos)
				{
					sentencia = sentencia.substring(0, sentencia.length() - 1) + " OR p.idTipoProducto=4 )";

				}
				else
				{
					sentencia += " AND ( p.idTipoProducto=4 )";

				}
			}

			if (!incluirPension)
			{
				sentencia += " AND (p.idTipoNomina != 1) ";
			}

			sentencia += " AND p.idPlaza=? ORDER BY p.NombreProducto DESC ";

			prep = conexion.prepareStatement(sentencia);

			prep.setInt(1, año);
			prep.setInt(2, quincena);
			prep.setInt(3, nomina);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					productos
							.add(new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
									new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
									rBD.getString("NombreProducto")));

				} while (rBD.next());

				for (Producto prod : productos)
				{
					prep = conexion.prepareStatement(
							" SELECT DISTINCT(idPlantilla) FROM nominas.datvalores WHERE idProducto=? LIMIT 1");
					prep.setInt(1, prod.getIdProducto());
					rBD = prep.executeQuery();

					if (rBD.next())
					{
						prod.setPlantillaDAT(utilidades.getPlantillaLayout(rBD.getInt("idPlantilla")));
						prod.getPlantillaDAT().updateCampos();

					}

					prep = conexion.prepareStatement(
							" SELECT DISTINCT(idPlantilla) FROM nominas.travalores WHERE idProducto=? LIMIT 1");
					prep.setInt(1, prod.getIdProducto());
					rBD = prep.executeQuery();

					if (rBD.next())
					{
						prod.setPlantillaTRA(utilidades.getPlantillaLayout(rBD.getInt("idPlantilla")));
						prod.getPlantillaTRA().updateCampos();

					}

				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return productos;

	}

	// tipoProducto para obtener ya sea cancelados u ordinarios de productos
	// timbrados
	public static List<ProductoTimbrado> getProductosAñoTim(int año, int quincena, boolean incluirProductos,
			boolean incluirCancelados, int nomina)
	{
		List<ProductoTimbrado> productos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = null;

			String sentencia = "  SELECT pt.*, p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina "
					+ "FROM timbrado.productotimbrado pt, nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl "
					+ "WHERE pt.idProductoNomina = p.idProducto AND p.ano=? AND p.Qna=? AND p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza    ";

			if (incluirProductos)
			{
				sentencia += " AND ( pt.idStatus = 0 )";
			}

			if (incluirCancelados)
			{

				sentencia += " AND ( pt.idStatus = -1 )";
			}

			sentencia += " AND p.idPlaza=? ORDER BY p.NombreProducto DESC ";

			prep = conexion.prepareStatement(sentencia);

			prep.setInt(1, año);
			prep.setInt(2, quincena);
			prep.setInt(3, nomina);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					productos.add(new ProductoTimbrado(rBD.getString("idProductoTimbrado"),
							new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
									new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
									rBD.getString("NombreProducto")),
							rBD.getBigDecimal("totalPercepciones"), rBD.getBigDecimal("totalDeducciones"),
							rBD.getBigDecimal("total"), rBD.getInt("totalRegistros"), rBD.getInt("totalCancelados"),
							null, new Status())

					);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return productos;

	}

	public static List<Producto> getProductosPeriodo(int añoInicial, int quincenaInicial, int añoFinal,
			int quincenaFinal, boolean incluirProductos, boolean incluirCancelados, int nomina)
	{
		String qnaInicialString = null;
		String qnaFinalString = null;

		if (quincenaInicial < 10)
		{
			qnaInicialString = "0" + quincenaInicial;
		}
		else
		{
			qnaInicialString = "" + quincenaInicial;
		}

		if (quincenaFinal < 10)
		{
			qnaFinalString = "0" + quincenaFinal;
		}
		else
		{
			qnaFinalString = "" + quincenaFinal;
		}

		List<Producto> productos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = null;

			String sentencia = "  SELECT p.*, pl.descripcion AS descPlaza, tp.descripcion AS descTipoProducto, tn.Descripcion AS descTipoNomina FROM nominas.producto p, webrh.tipoproducto tp, webrh.tiponomina tn, webrh.plaza pl "

					+ "WHERE CONVERT( CONCAT(Ano, ( IF(LENGTH(Qna)=1, CONCAT('0',p.Qna), p.Qna) ) ), SIGNED INTEGER ) >= ? AND "

					+ "CONVERT( CONCAT(Ano, ( IF(LENGTH(Qna)=1, CONCAT('0',p.Qna), p.Qna) ) ), SIGNED INTEGER ) <= ? "
					+ "AND p.idTipoProducto = tp.idTipoProducto AND p.idTipoNomina = tn.idTipoNomina AND p.idPlaza = pl.idPlaza  ";

			if (incluirProductos)
			{
				sentencia += " AND ( p.idTipoProducto!=4 )";
			}

			if (incluirCancelados)
			{

				if (incluirProductos)
				{
					sentencia = sentencia.substring(0, sentencia.length() - 1) + " OR p.idTipoProducto=4 )";

				}
				else
				{
					sentencia += " AND ( p.idTipoProducto=4 )";

				}
			}

			sentencia += " AND p.idPlaza=? ORDER BY p.NombreProducto DESC ";

			prep = conexion.prepareStatement(sentencia);

			prep.setInt(1, Integer.parseInt("" + añoInicial + qnaInicialString));
			prep.setInt(2, Integer.parseInt("" + añoFinal + qnaFinalString));

			prep.setInt(3, nomina);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					productos
							.add(new Producto(rBD.getInt("idProducto"), rBD.getString("Descripcion"), rBD.getInt("Ano"),
									rBD.getInt("Qna"), new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")),
									new TipoNomina(rBD.getInt("idTipoNomina"), rBD.getString("descTipoNomina")),
									new TipoProducto(rBD.getInt("idTipoProducto"), rBD.getString("descTipoProducto")),
									rBD.getString("NombreProducto")));

				} while (rBD.next());

				for (Producto prod : productos)
				{
					prep = conexion.prepareStatement(
							" SELECT DISTINCT(idPlantilla) FROM nominas.datvalores WHERE idProducto=? LIMIT 1");
					prep.setInt(1, prod.getIdProducto());
					rBD = prep.executeQuery();

					if (rBD.next())
					{
						prod.setPlantillaDAT(utilidades.getPlantillaLayout(rBD.getInt("idPlantilla")));
						prod.getPlantillaDAT().updateCampos();

					}

					prep = conexion.prepareStatement(
							" SELECT DISTINCT(idPlantilla) FROM nominas.travalores WHERE idProducto=? LIMIT 1");
					prep.setInt(1, prod.getIdProducto());
					rBD = prep.executeQuery();

					if (rBD.next())
					{
						prod.setPlantillaTRA(utilidades.getPlantillaLayout(rBD.getInt("idPlantilla")));
						prod.getPlantillaTRA().updateCampos();

					}

				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return productos;

	}

	// Se utiliza para obtener un rango de archivos de puesto según una
	// determinada nómina
	// Para obtener el de una quincena específica puede fácilmente ser utilizado
	// el método
	public static List<ArchivoPuesto> getArchivosPuesto(int añoInicial, int quincenaInicial, int añoFinal,
			int quincenaFinal, int nomina)
	{
		String qnaInicialString = null;
		String qnaFinalString = null;

		if (quincenaInicial < 10)
		{
			qnaInicialString = "0" + quincenaInicial;
		}
		else
		{
			qnaInicialString = "" + quincenaInicial;
		}

		if (quincenaFinal < 10)
		{
			qnaFinalString = "0" + quincenaFinal;
		}
		else
		{
			qnaFinalString = "" + quincenaFinal;
		}

		List<ArchivoPuesto> archivosPuesto = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = null;

			String sentencia = "  SELECT p.*, pl.descripcion AS descPlaza FROM nominas.archivopuesto p, webrh.plaza pl "

					+ "WHERE CONVERT( CONCAT(Ano, ( IF(LENGTH(Qna)=1, CONCAT('0',p.Qna), p.Qna) ) ), SIGNED INTEGER ) >= ? AND "

					+ "CONVERT( CONCAT(Ano, ( IF(LENGTH(Qna)=1, CONCAT('0',p.Qna), p.Qna) ) ), SIGNED INTEGER ) <= ? "
					+ "AND p.idPlaza = pl.idPlaza  ";

			sentencia += " AND p.idPlaza=? ORDER BY p.descripcion DESC ";

			prep = conexion.prepareStatement(sentencia);

			prep.setInt(1, Integer.parseInt("" + añoInicial + qnaInicialString));
			prep.setInt(2, Integer.parseInt("" + añoFinal + qnaFinalString));

			prep.setInt(3, nomina);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					archivosPuesto.add(new ArchivoPuesto(rBD.getInt("idArchivoPuesto"), rBD.getString("Descripcion"),
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("descPlaza")), rBD.getInt("Ano"),
							rBD.getInt("Qna"), rBD.getString("Observaciones"),
							new Plantilla(rBD.getInt("idPlantilla"))));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return archivosPuesto;

	}

	public static List<ArchivoBanco> getArchivosBancoPlazaIP(int idPlaza, int año, int qna, String idInstrumentoPago)
	{

		List<ArchivoBanco> listaArchivosBanco = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			PreparedStatement prep = null;

			String sentencia = "  SELECT ap.*, pl.descripcion AS descripcionPlaza FROM archivopago ap, webrh.plaza pl "
					+ "WHERE ap.idPlaza=? AND ap.Ano=? AND ap.Qna=? AND ap.idInstrumentoPago=? AND ap.idPlaza = pl.idPlaza";

			prep = conexion.prepareStatement(sentencia);

			prep.setInt(1, idPlaza);
			prep.setInt(2, año);
			prep.setInt(3, qna);
			prep.setString(4, idInstrumentoPago);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					ArchivoBanco archivoBanco = new ArchivoBanco();

					archivoBanco.setIdArchivoPago(rBD.getInt("idArchivoPago"));
					archivoBanco.setDescripcion(rBD.getString("Descripcion"));
					archivoBanco.setPlaza(new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcionPlaza")));
					archivoBanco.setIdInstrumentoPago(rBD.getString("idInstrumentoPago"));
					archivoBanco.setAño(rBD.getInt("Ano"));
					archivoBanco.setQna(rBD.getInt("Qna"));
					archivoBanco.setObservaciones(rBD.getString("Observaciones"));
					archivoBanco.setPlantilla(new Plantilla(rBD.getInt("idPlantilla")));

					listaArchivosBanco.add(archivoBanco);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return listaArchivosBanco;

	}

	public static ArchivoBanco getArchivoBanco(int idPlaza, int año, int qna, String idInstrumentoPago,
			int idArchivoPago)
	{

		ArchivoBanco archivoBanco = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			PreparedStatement prep = null;

			String sentencia = "  SELECT ap.*, pl.descripcion AS descripcionPlaza FROM archivopago ap, webrh.plaza pl "
					+ "WHERE ap.idPlaza=? AND ap.Ano=? AND ap.Qna=? AND ap.idInstrumentoPago=? AND ap.idArchivoPago=? AND ap.idPlaza = pl.idPlaza";

			prep = conexion.prepareStatement(sentencia);

			prep.setInt(1, idPlaza);
			prep.setInt(2, año);
			prep.setInt(3, qna);
			prep.setString(4, idInstrumentoPago);
			prep.setInt(5, idArchivoPago);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				archivoBanco = new ArchivoBanco();
				archivoBanco.setIdArchivoPago(rBD.getInt("idArchivoPago"));
				archivoBanco.setDescripcion(rBD.getString("Descripcion"));
				archivoBanco.setPlaza(new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcionPlaza")));
				archivoBanco.setIdInstrumentoPago(rBD.getString("idInstrumentoPago"));
				archivoBanco.setAño(rBD.getInt("Ano"));
				archivoBanco.setQna(rBD.getInt("Qna"));
				archivoBanco.setObservaciones(rBD.getString("Observaciones"));
				archivoBanco.setPlantilla(new Plantilla(rBD.getInt("idPlantilla")));

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return archivoBanco;

	}

	public static List<FuenteFinanciamiento> getCatalogoFuentesFinanciamiento()
	{
		List<FuenteFinanciamiento> fuentesFinanciamiento = new ArrayList<>();
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement(
					" SELECT * FROM fuentefinanciamiento ORDER BY LENGTH(idFuenteFinanciamiento) ASC, idFuenteFinanciamiento");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					fuentesFinanciamiento.add(new FuenteFinanciamiento(rBD.getInt("idRegistro"),
							rBD.getString("idFuenteFinanciamiento"), rBD.getString("Descripcion")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener el catálogo de fuentes de financiamiento, favor de contactar con el desarrollador del sistema."));
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

		return fuentesFinanciamiento;

	}

	// Obtiene el catálogo de conceptos en orden, incluye todos los conceptos
	// registrados
	public static List<Concepto> getConceptos()
	{
		List<Concepto> conceptos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM concepto ORDER BY Orden ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					conceptos.add(new Concepto(rBD.getInt("TipoConcepto"), rBD.getString("idConcepto"),
							rBD.getString("Descripcion"), rBD.getString("Partida"), rBD.getString("PartidaAntecedente"),
							new BigDecimal("0.00"), rBD.getBoolean("AsociadoAPlaza")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return conceptos;

	}

	public static void updateConceptoDatosAdicionales(Concepto con)
	{

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion.prepareStatement(
					" SELECT * FROM concepto WHERE TipoConcepto=? AND idConcepto=? AND PartidaAntecedente=? ");

			prep.setInt(1, con.getTipoConcepto());
			prep.setString(2, con.getClave());
			prep.setString(3, con.getPartidaAntecedente());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				con.setPeriodicidad(rBD.getString("Periodicidad"));
				con.setAmbito(rBD.getString("Ambito"));
				con.setPartidaEstatal(rBD.getString("PartidaEstatal"));
				con.setPartidaRamo33(rBD.getString("PartidaRamo33"));
				con.setPartidaRamo12(rBD.getString("PartidaRamo12"));
				con.setSeGrava(rBD.getString("SeGrava"));
				con.setTipoDeConcepto(rBD.getString("TipoDeConcepto"));
				con.setTipoDeTrabajador(rBD.getString("tipoDeTrabajador"));
				con.setSoporteDocumental(rBD.getString("soporteDocumental"));
				con.setFormulaOFormulacion(rBD.getString("formulaOFormulacion"));

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// Obtiene el catálogo de conceptos en orden
	// tipoGeneracion 1 percepciones, 2 deducciones
	public static List<Concepto> getConceptos(int tipoGeneracion)
	{
		List<Concepto> conceptos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			PreparedStatement prep = conexion
					.prepareStatement(" SELECT * FROM concepto WHERE TipoConcepto=? ORDER BY Orden ASC ");

			prep.setInt(1, tipoGeneracion);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					conceptos.add(new Concepto(rBD.getInt("TipoConcepto"), rBD.getString("idConcepto"),
							rBD.getString("Descripcion"), rBD.getString("Partida"), rBD.getString("PartidaAntecedente"),
							new BigDecimal("0.00"), rBD.getBoolean("AsociadoAPlaza")));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return conceptos;

	}

	// Tipo concepto 0 percepción -1 deducción
	public static void addConcepto(Concepto conceptoAñadiendo, List<Concepto> conceptos, List<Concepto> conceptosDeduc,
			boolean añadirTipoTimbre, int tipoConcepto)
	{

		boolean añadidoConcepto = false;

		if (conceptos == null)
		{
			conceptos = new ArrayList<>();

		}

		if (conceptosDeduc == null)
		{
			conceptosDeduc = new ArrayList<>();
		}

		int tipoCon = conceptoAñadiendo.getTipoConcepto();

		if (añadirTipoTimbre)
		{
			tipoCon = (conceptoAñadiendo.getTipoConcepto() == 2 && tipoConcepto == 0) ? 0
					: (conceptoAñadiendo.getTipoConcepto() == 1 && tipoConcepto == -1) ? -1
							: conceptoAñadiendo.getTipoConcepto();
		}

		switch (tipoCon)
		{
			case 0:
			case 1:
			case 3:
			case 5:
				// se recorre toda la lista de conceptos para saber si ya se
				// encuentra dentro, si esta se suma, si no, se crea
				for (Concepto con : conceptos)
				{
					if (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto()
							&& con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave())
							&& con.getPartidaAntecedente().trim()
									.equalsIgnoreCase(conceptoAñadiendo.getPartidaAntecedente().trim()))
					{
						añadidoConcepto = true;

						// se suma la cantidad del valor del concepto
						con.setTotalCasos(con.getTotalCasos() + conceptoAñadiendo.getTotalCasos());
						con.setValor(con.getValor().add(conceptoAñadiendo.getValor()));

						break;
					}

				}

				if (!añadidoConcepto)
				{
					conceptos.add((Concepto) conceptoAñadiendo.clone());
				}

				break;

			case -1:
			case 2:
			case 4:
			case 6:

				// se recorre toda la lista de conceptos para saber si ya se
				// encuentra dentro, si esta se suma, si no, se crea
				for (Concepto con : conceptosDeduc)
				{
					if (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto()
							&& con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave())
							&& con.getPartidaAntecedente().trim()
									.equalsIgnoreCase(conceptoAñadiendo.getPartidaAntecedente().trim()))
					{
						añadidoConcepto = true;
						// se suma la cantidad del valor del concepto
						con.setTotalCasos(con.getTotalCasos() + conceptoAñadiendo.getTotalCasos());
						con.setValor(con.getValor().add(conceptoAñadiendo.getValor()));
						break;
					}

				}

				if (!añadidoConcepto)
				{
					conceptosDeduc.add((Concepto) conceptoAñadiendo.getClone());
				}

				break;

			default:
				System.out.println("Default");

				break;
		}

	}

	/*
	 * // Actualiza los registrosDAT de un determinado producto en específico, //
	 * opción 0 es DAT y 1 TRA public static List<PlantillaRegistro>
	 * getRegistrosDATTRA(Producto prod, int opcion) { int nRegistros; String tabla;
	 * PreparedStatement prep; ResultSet rBD; int nRegistro; String consultaBase;
	 * String consultaBaseFinal; String ordenCamposComplemento; int lastnrRegistro;
	 * String camposColumnas = ""; tabla = null; Plantilla plantillaDATTRA = null;
	 * switch (opcion) { case 0: tabla = "nominas.datvalores"; plantillaDATTRA =
	 * prod.getPlantillaDAT(); break; case 1: tabla = "nominas.travalores";
	 * plantillaDATTRA = prod.getPlantillaTRA(); break; } List<PlantillaRegistro>
	 * registros = null; prep = null; try (Connection conexion = ((DataBase)
	 * FacesUtils.getManagedBean("database")).getConnectionNominas();) { nRegistros
	 * = 0; prep = conexion .prepareStatement(
	 * " SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idProducto=? ");
	 * prep.setInt(1, prod.getIdProducto()); rBD = prep.executeQuery(); if
	 * (rBD.next()) { nRegistros = rBD.getInt("maximo"); registros = new
	 * ArrayList<>(); } nRegistro = 1; ordenCamposComplemento = "AND Orden IN(";
	 * consultaBaseFinal =
	 * " GROUP BY idRegistro ORDER BY idRegistro ASC, Orden ASC";
	 * List<CampoPlantilla> campos = plantillaDATTRA.getCampos(); for
	 * (CampoPlantilla campo : campos) { ordenCamposComplemento += campo.getOrden()
	 * + ","; camposColumnas += "group_concat(IF (Orden=" + campo.getOrden() +
	 * ",Valor, NULL)) AS v" + campo.getOrden() + ","; } camposColumnas =
	 * camposColumnas.substring(0, camposColumnas.length() - 1); consultaBase =
	 * " SELECT idRegistro, " + camposColumnas + "  FROM " + tabla +
	 * " WHERE idProducto=? AND idPlantilla=? AND idRegistro<=? ";
	 * ordenCamposComplemento = ordenCamposComplemento.substring(0,
	 * ordenCamposComplemento.length() - 1); ordenCamposComplemento += ")";
	 * prep.close(); prep = conexion.prepareStatement(consultaBase +
	 * ordenCamposComplemento + consultaBaseFinal); prep.setInt(1,
	 * prod.getIdProducto()); prep.setInt(2, plantillaDATTRA.getIdPlantilla());
	 * prep.setInt(3, nRegistros); rBD = prep.executeQuery(); lastnrRegistro = 0;
	 * PlantillaRegistro plantillaR; if (rBD.next()) { do { nRegistro =
	 * rBD.getInt("idRegistro"); plantillaR = new PlantillaRegistro(nRegistro,
	 * plantillaDATTRA.getClone()); for (int x = 0; x <
	 * plantillaDATTRA.getCampos().size(); x++) { plantillaR.getCampo((x +
	 * 1)).setValor(rBD.getString((x + 2))); } registros.add(plantillaR); } while
	 * (rBD.next()); } } catch (Exception e) {
	 * System.out.println("Excepción en producto " + prod.getNombreProducto());
	 * e.printStackTrace(); } finally { if (prep != null) { try { prep.close(); }
	 * catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } } return registros; }
	 */

	// Actualiza los registrosDAT de un determinado producto en específico,
	// opción 0 es DAT y 1 TRA

	// Se establace la opción tipo 3 para filtrar de los registros DAT los
	// empleados que ya están dado de alta en el registro general de empleados

	public static List<PlantillaRegistro> getRegistrosDATTRA(Producto prod, int opcion)
	{
		int nRegistros;
		String tabla;
		PreparedStatement prep;
		ResultSet rBD;
		int nRegistro;
		String consultaBase;
		String consultaBaseFinal;
		String complementoFiltroRegistroGeneral;
		String ordenCamposComplemento;
		int lastnrRegistro;

		tabla = null;
		Plantilla plantillaDATTRA = null;

		switch (opcion)
		{
			case 0:
			case 3:
				tabla = "nominas.datvalores";
				plantillaDATTRA = prod.getPlantillaDAT();
				break;

			case 1:
				tabla = "nominas.travalores";
				plantillaDATTRA = prod.getPlantillaTRA();
				break;
		}

		List<PlantillaRegistro> registros = null;
		prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			nRegistros = 0;

			prep = conexion
					.prepareStatement(" SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idProducto=? ");

			prep.setInt(1, prod.getIdProducto());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				nRegistros = rBD.getInt("maximo");

				registros = new ArrayList<>();

			}

			nRegistro = 1;
			consultaBase = " SELECT idRegistro, Orden, Valor  FROM " + tabla
					+ " WHERE idProducto=? AND idPlantilla=? AND idRegistro<=? ";
			ordenCamposComplemento = "AND Orden IN(";

			consultaBaseFinal = " ORDER BY idRegistro ASC, Orden ASC";

			List<CampoPlantilla> campos = plantillaDATTRA.getCampos();

			for (CampoPlantilla campo : campos)
			{
				ordenCamposComplemento += campo.getOrden() + ",";
			}

			ordenCamposComplemento = ordenCamposComplemento.substring(0, ordenCamposComplemento.length() - 1);
			ordenCamposComplemento += ")";

			prep.close();

			if (opcion == 3)
			{
				int indiceNumEmpleado = prod.getPlantillaDAT().getPosicionValorPorDescripcionContains("mero de emple");

				complementoFiltroRegistroGeneral = " AND idRegistro NOT IN (" + "SELECT idRegistro FROM ("
						+ "SELECT *  FROM nominas.datvalores WHERE idProducto=? AND idPlantilla=? AND idRegistro<=? AND Orden IN(?)"
						+ "AND (?,Valor) IN (  SELECT t.idPlaza,t.NumEmpleado FROM webrh.trabajador t   )) as t1 ) ";

				prep = conexion.prepareStatement(
						consultaBase + ordenCamposComplemento + complementoFiltroRegistroGeneral + consultaBaseFinal);
				prep.setInt(1, prod.getIdProducto());
				prep.setInt(2, plantillaDATTRA.getIdPlantilla());
				prep.setInt(3, nRegistros);
				prep.setInt(4, prod.getIdProducto());
				prep.setInt(5, plantillaDATTRA.getIdPlantilla());
				prep.setInt(6, nRegistros);
				prep.setInt(7, indiceNumEmpleado);
				prep.setInt(8, prod.getPlaza().getIdPlaza());

			}
			else
			{
				prep = conexion.prepareStatement(consultaBase + ordenCamposComplemento + consultaBaseFinal);
				prep.setInt(1, prod.getIdProducto());
				prep.setInt(2, plantillaDATTRA.getIdPlantilla());
				prep.setInt(3, nRegistros);

			}

			rBD = prep.executeQuery();

			lastnrRegistro = 0;
			PlantillaRegistro lastiPlantillaR = null;
			PlantillaRegistro plantillaR;

			if (rBD.next())
			{

				do
				{
					nRegistro = rBD.getInt("idRegistro");

					if (lastnrRegistro != nRegistro)
					{
						plantillaR = new PlantillaRegistro(nRegistro, plantillaDATTRA.getClone());
						registros.add(plantillaR);
						lastiPlantillaR = plantillaR;
					}

					lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

					lastnrRegistro = nRegistro;

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			System.out.println("Excepción en producto " + prod.getNombreProducto());
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
				{ // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return registros;

	}

	public static BigDecimal getTotalConceptosTRA(Producto prod, List<Concepto> conceptosIncluir)
	{
		int nRegistros;
		String tabla;
		PreparedStatement prep;
		ResultSet rBD;
		int nRegistro;
		String consultaBase;
		String consultaBaseFinal;
		String ordenCamposComplemento;
		int lastnrRegistro;

		tabla = null;
		Plantilla plantillaDATTRA = null;

		tabla = "nominas.travalores";
		plantillaDATTRA = prod.getPlantillaTRA();

		List<PlantillaRegistro> registros = null;
		prep = null;

		BigDecimal total = new BigDecimal("0.00");

		int indiceTipoConcepto = plantillaDATTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = plantillaDATTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartida = plantillaDATTRA.getPosicionValorPorDescripcion("partida");
		int indicePartidaAntecedente = plantillaDATTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = plantillaDATTRA.getPosicionValorPorDescripcionContains("Importe");

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			nRegistros = 0;

			prep = conexion
					.prepareStatement(" SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idProducto=? ");

			prep.setInt(1, prod.getIdProducto());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				nRegistros = rBD.getInt("maximo");

				registros = new ArrayList<>();

			}

			nRegistro = 1;
			consultaBase = " SELECT idRegistro, Orden, Valor  FROM " + tabla
					+ " WHERE idProducto=? AND idPlantilla=? AND idRegistro<=? ";
			ordenCamposComplemento = "AND Orden IN(";
			consultaBaseFinal = " ORDER BY idRegistro ASC, Orden ASC";

			List<CampoPlantilla> campos = plantillaDATTRA.getCampos();

			for (CampoPlantilla campo : campos)
			{
				ordenCamposComplemento += campo.getOrden() + ",";
			}

			ordenCamposComplemento = ordenCamposComplemento.substring(0, ordenCamposComplemento.length() - 1);
			ordenCamposComplemento += ")";

			prep.close();

			prep = conexion.prepareStatement(consultaBase + ordenCamposComplemento + consultaBaseFinal);
			prep.setInt(1, prod.getIdProducto());
			prep.setInt(2, plantillaDATTRA.getIdPlantilla());
			prep.setInt(3, nRegistros);

			rBD = prep.executeQuery();

			lastnrRegistro = 0;
			PlantillaRegistro lastiPlantillaR = null;
			PlantillaRegistro plantillaR;

			if (rBD.next())
			{

				do
				{
					nRegistro = rBD.getInt("idRegistro");

					if (lastnrRegistro != nRegistro)
					{
						plantillaR = new PlantillaRegistro(nRegistro, plantillaDATTRA.getClone());

						for (Concepto con : conceptosIncluir)
						{

							if (con.getTipoConcepto() == Integer
									.parseInt(plantillaR.getValorEnCampo(indiceTipoConcepto)))
							{
								if (con.getClave().trim()
										.equalsIgnoreCase(plantillaR.getValorEnCampo(indiceConcepto).trim()))
								{
									if (con.getPartidaAntecedente().trim().equalsIgnoreCase(
											plantillaR.getValorEnCampo(indicePartidaAntecedente).trim()))
									{

										total = total.add(new BigDecimal(plantillaR.getValorEnCampo(indiceImporte)));
									}

								}

							}

						}

						// registros.add(plantillaR);
						lastiPlantillaR = plantillaR;
					}

					lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

					lastnrRegistro = nRegistro;

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			System.out.println("Excepción en producto " + prod.getNombreProducto());
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

		return total;

	}

	// Obtiene los datos TRA o dat de un registro en específico, orden indica el
	// índice del campo clave de búsqueda
	public static List<PlantillaRegistro> getRegistrosDATTRA(Producto prod, int opcion, int indiceBusqueda,
			String valorBuscado)
	{

		int nRegistros;
		String tabla;
		PreparedStatement prep = null;
		ResultSet rBD;
		int nRegistro;
		String consultaBase;
		String consultaBaseFinal;
		String ordenCamposComplemento;
		int lastnrRegistro;

		tabla = null;
		Plantilla plantillaDATTRA = null;

		switch (opcion)
		{
			case 0:
				tabla = "nominas.datvalores";
				plantillaDATTRA = prod.getPlantillaDAT();
				break;

			case 1:
				tabla = "nominas.travalores";
				plantillaDATTRA = prod.getPlantillaTRA();
				break;
		}

		List<PlantillaRegistro> registros = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			nRegistros = 0;

			prep = conexion
					.prepareStatement(" SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idProducto=? ");

			prep.setInt(1, prod.getIdProducto());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				nRegistros = rBD.getInt("maximo");

				registros = new ArrayList<>();

			}

			nRegistro = 1;
			consultaBase = " SELECT idRegistro, Orden, Valor  FROM " + tabla
					+ " WHERE idProducto=? AND idPlantilla=? AND idRegistro<=? ";
			ordenCamposComplemento = "AND Orden IN(";
			consultaBaseFinal = " AND idRegistro IN ( SELECT idRegistro  FROM nominas.travalores WHERE idProducto=? AND idPlantilla=? AND Orden=? AND Valor=? ORDER BY idRegistro ASC, Orden ASC ) ORDER BY idRegistro ASC, Orden ASC";

			List<CampoPlantilla> campos = plantillaDATTRA.getCampos();

			for (CampoPlantilla campo : campos)
			{
				ordenCamposComplemento += campo.getOrden() + ",";
			}

			ordenCamposComplemento = ordenCamposComplemento.substring(0, ordenCamposComplemento.length() - 1);
			ordenCamposComplemento += ")";

			prep.close();

			prep = conexion.prepareStatement(consultaBase + ordenCamposComplemento + consultaBaseFinal);
			prep.setInt(1, prod.getIdProducto());
			prep.setInt(2, plantillaDATTRA.getIdPlantilla());
			prep.setInt(3, nRegistros);

			prep.setInt(4, prod.getIdProducto());
			prep.setInt(5, plantillaDATTRA.getIdPlantilla());
			prep.setInt(6, indiceBusqueda);
			prep.setString(7, valorBuscado);

			rBD = prep.executeQuery();

			lastnrRegistro = 0;
			PlantillaRegistro lastiPlantillaR = null;

			if (rBD.next())
			{

				do
				{
					nRegistro = rBD.getInt("idRegistro");

					if (lastnrRegistro != nRegistro)
					{
						PlantillaRegistro plantillaR = new PlantillaRegistro(nRegistro, plantillaDATTRA.getClone());
						registros.add(plantillaR);
						lastiPlantillaR = plantillaR;
					}

					lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

					lastnrRegistro = nRegistro;

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

		return registros;

	}

	public static void getTRAenBaseDAT(Producto prod)
	{
		String tabla = null;
		Plantilla plantillaDATTRA = null;

		tabla = "nominas.travalores";
		plantillaDATTRA = prod.getPlantillaTRA();

		PreparedStatement prep = null;
		ResultSet rBD;

		int nRegistros;
		int nRegistro = 1;
		int indiceNumeroCheque = plantillaDATTRA.getPosicionValorPorDescripcionContains("mero de cheque");
		int indiceTipoConcepto = plantillaDATTRA.getPosicionValorPorDescripcionContains("ipo de concep");
		int indiceConcepto = plantillaDATTRA.getPosicionValorPorDescripcion("Concepto");
		int indicePartidaAntecedente = plantillaDATTRA.getPosicionValorPorDescripcionContains("partida de ante");
		int indiceImporte = plantillaDATTRA.getPosicionValorPorDescripcionContains("Importe");

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			// Se recorrre cada unidad y sus registros DAT, y por cada registro
			// DAT se obtienen sus TRA
			for (UnidadProducto unid : prod.getUnidadResponsable())
			{
				for (PlantillaRegistro regDAT : unid.getRegistrosDAT())
				{

					prep = conexion.prepareStatement(
							" SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idProducto=? ");

					prep.setInt(1, prod.getIdProducto());

					rBD = prep.executeQuery();

					if (rBD.next())
					{
						nRegistros = rBD.getInt("maximo");

					}

					nRegistro = 1;

					String consultaBase = " SELECT idRegistro, Orden, Valor  FROM " + tabla
							+ " WHERE idProducto=? AND idPlantilla=? AND Orden=? AND Valor=? ";
					String ordenCamposComplemento = "AND Orden IN(";
					String consultaBaseFinal = " ORDER BY idRegistro ASC, Orden ASC";

					List<CampoPlantilla> campos = plantillaDATTRA.getCampos();

					for (CampoPlantilla campo : campos)
					{
						ordenCamposComplemento += campo.getOrden() + ",";
					}

					ordenCamposComplemento = ordenCamposComplemento.substring(0, ordenCamposComplemento.length() - 1);
					ordenCamposComplemento += ")";

					prep = conexion.prepareStatement(consultaBase + ordenCamposComplemento + consultaBaseFinal);
					prep.setInt(1, prod.getIdProducto());
					prep.setInt(2, plantillaDATTRA.getIdPlantilla());
					prep.setInt(3, indiceNumeroCheque);
					prep.setString(4, regDAT.getValorEnCampo(indiceNumeroCheque));

					rBD = prep.executeQuery();

					int lastnrRegistro = 0;
					PlantillaRegistro lastiPlantillaR = null;

					if (rBD.next())
					{

						do
						{
							nRegistro = rBD.getInt("idRegistro");

							if (lastnrRegistro != nRegistro)
							{
								Concepto conceptoAñadiendo = new Concepto(
										Integer.parseInt(lastiPlantillaR.getValorEnCampo(indiceTipoConcepto)),
										lastiPlantillaR.getValorEnCampo(indiceConcepto), "", "",
										lastiPlantillaR.getValorEnCampo(indicePartidaAntecedente),
										new BigDecimal(lastiPlantillaR.getValorEnCampo(indiceImporte).trim()));
								unid.addConcepto(conceptoAñadiendo);

								PlantillaRegistro plantillaR = new PlantillaRegistro(nRegistro,
										plantillaDATTRA.getClone());
								regDAT.getRegistrosVinculados().add(plantillaR);
								lastiPlantillaR = plantillaR;
							}

							lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

							lastnrRegistro = nRegistro;

						} while (rBD.next());

					}

				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// Actualiza los registros del archivo de puestos
	public static List<PlantillaRegistro> getRegistrosArchivoPuesto(ArchivoPuesto archivoPuesto)
	{
		int nRegistros;
		String tabla;
		PreparedStatement prep;
		ResultSet rBD;
		int nRegistro;
		String consultaBase;
		String consultaBaseFinal;
		String ordenCamposComplemento;
		int lastnrRegistro;

		tabla = null;
		Plantilla plantilla = archivoPuesto.getPlantilla();

		tabla = "nominas.archivopuestovalores";

		List<PlantillaRegistro> registros = null;
		prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			nRegistros = 0;

			prep = conexion
					.prepareStatement(" SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idArchivoPuesto=? ");

			prep.setInt(1, archivoPuesto.getIdArchivoPuesto());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				nRegistros = rBD.getInt("maximo");

				registros = new ArrayList<>();

			}

			nRegistro = 1;
			consultaBase = " SELECT idRegistro, Orden, Valor  FROM " + tabla
					+ " WHERE idArchivoPuesto=? AND idPlantilla=? AND idRegistro<=? ";
			ordenCamposComplemento = "AND Orden IN(";
			consultaBaseFinal = " ORDER BY idRegistro ASC, Orden ASC";

			List<CampoPlantilla> campos = plantilla.getCampos();

			for (CampoPlantilla campo : campos)
			{
				ordenCamposComplemento += campo.getOrden() + ",";
			}

			ordenCamposComplemento = ordenCamposComplemento.substring(0, ordenCamposComplemento.length() - 1);
			ordenCamposComplemento += ")";

			prep.close();

			prep = conexion.prepareStatement(consultaBase + ordenCamposComplemento + consultaBaseFinal);
			prep.setInt(1, archivoPuesto.getIdArchivoPuesto());
			prep.setInt(2, plantilla.getIdPlantilla());
			prep.setInt(3, nRegistros);

			rBD = prep.executeQuery();

			lastnrRegistro = 0;
			PlantillaRegistro lastiPlantillaR = null;
			PlantillaRegistro plantillaR;

			if (rBD.next())
			{

				do
				{
					nRegistro = rBD.getInt("idRegistro");

					if (lastnrRegistro != nRegistro)
					{
						plantillaR = new PlantillaRegistro(nRegistro, plantilla.getClone());
						registros.add(plantillaR);
						lastiPlantillaR = plantillaR;
					}

					lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));

					lastnrRegistro = nRegistro;

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			System.out.println("Excepción en producto " + archivoPuesto.getIdArchivoPuesto());
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
				{ // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return registros;

	}

	// Actualiza los registros del archivo de puestos, si se pasa el id de
	// registro únicamente extraerá dicho registro
	public static List<PlantillaRegistro> getRegistrosArchivoBanco(ArchivoBanco archivoBanco, Integer idRegistro)
	{
		int nRegistros;
		String tabla;
		PreparedStatement prep;
		ResultSet rBD;
		int nRegistro;
		String consultaBase;
		String consultaBaseFinal;
		String ordenCamposComplemento;
		int lastnrRegistro;
		String restricRegistros = "";

		if (idRegistro != null)
		{
			restricRegistros = " AND pv.idRegistro=" + idRegistro + " ";
		}
		else
		{
			restricRegistros = " AND pv.idRegistro<=?  ";

		}

		tabla = "archivopagovalores pv ";
		Plantilla plantilla = archivoBanco.getPlantilla();

		List<PlantillaRegistro> registros = null;
		prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			if (idRegistro != null)
			{
				nRegistros = 1;
				registros = new ArrayList<>();
			}
			else
			{
				nRegistros = 0;

				prep = conexion.prepareStatement(
						" SELECT MAX(idRegistro) AS maximo FROM " + tabla + " WHERE idArchivoPago=? ");

				prep.setInt(1, archivoBanco.getIdArchivoPago());

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					nRegistros = rBD.getInt("maximo");

					registros = new ArrayList<>();

				}

				prep.close();

			}

			nRegistro = 1;

			consultaBase = " SELECT pv.idRegistro, pv.Orden, pv.Valor, pv.ValorModificado, IF(rb.idRegistro IS NULL, NULL,-1) AS inactivo  FROM "
					+ tabla
					+ " LEFT JOIN nominas.registropagoinactivo rb ON pv.idArchivoPago = rb.idArchivoPago AND pv.idRegistro = rb.idRegistro WHERE pv.idArchivoPago=? AND pv.idPlantilla=? "
					+ restricRegistros;
			ordenCamposComplemento = "AND pv.Orden IN(";
			consultaBaseFinal = " ORDER BY pv.idRegistro ASC, pv.Orden ASC";

			List<CampoPlantilla> campos = plantilla.getCampos();

			for (CampoPlantilla campo : campos)
			{
				ordenCamposComplemento += campo.getOrden() + ",";
			}

			ordenCamposComplemento = ordenCamposComplemento.substring(0, ordenCamposComplemento.length() - 1);
			ordenCamposComplemento += ")";

			prep = conexion.prepareStatement(consultaBase + ordenCamposComplemento + consultaBaseFinal);
			prep.setInt(1, archivoBanco.getIdArchivoPago());
			prep.setInt(2, plantilla.getIdPlantilla());

			if (idRegistro == null)
			{
				prep.setInt(3, nRegistros);
			}

			rBD = prep.executeQuery();

			lastnrRegistro = 0;
			PlantillaRegistro lastiPlantillaR = null;
			PlantillaRegistro plantillaR;

			if (rBD.next())
			{

				do
				{
					nRegistro = rBD.getInt("idRegistro");

					if (lastnrRegistro != nRegistro)
					{
						plantillaR = new PlantillaRegistro(nRegistro, plantilla.getClone());
						plantillaR.setStatus(rBD.getInt("inactivo"));

						registros.add(plantillaR);
						lastiPlantillaR = plantillaR;
					}

					lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValor(rBD.getString("Valor"));
					lastiPlantillaR.getCampo(rBD.getInt("Orden")).setValorModificado(rBD.getString("ValorModificado"));

					lastnrRegistro = nRegistro;

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			System.out.println("Excepción en producto " + archivoBanco.getIdArchivoPago());
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
				{ // TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return registros;

	}

	// Obtiene el catálogo de conceptos asociados a la plaza
	// ipoGeneración 0 asociados, 1 no asociados, 2 ambos
	public static List<ConceptoAsociadoPlaza> getCatalogoConceptosAsociadosPlaza(int tipoGeneracion)
	{
		List<ConceptoAsociadoPlaza> conceptos = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			switch (tipoGeneracion)
			{
				case 0:
					prep = conexion.prepareStatement(
							" SELECT * FROM webrh.conceptoasociadoplaza WHERE asociado=true ORDER BY Asociado DESC, PartidaEspecifica ASC ");

					break;

				case 1:
					prep = conexion.prepareStatement(
							" SELECT * FROM webrh.conceptoasociadoplaza WHERE asociado=false ORDER BY Asociado DESC, PartidaEspecifica ASC ");

					break;

				case 2:
					prep = conexion.prepareStatement(
							" SELECT * FROM webrh.conceptoasociadoplaza ORDER BY Asociado DESC, PartidaEspecifica ASC ");

					break;

				default:
					break;
			}

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					conceptos.add(new ConceptoAsociadoPlaza(rBD.getInt("idRegistro"),
							rBD.getString("PartidaEspecifica"), rBD.getString("Concepto"), rBD.getBoolean("Asociado")));
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

		return conceptos;

	}

	public static List<CentroResponsabilidad> getCatalogoCentroResponsabilidad()
	{
		List<CentroResponsabilidad> catCR = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion
					.prepareStatement(" SELECT * FROM centroresponsabilidad ORDER BY idCentroResponsabilidad ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					catCR.add(new CentroResponsabilidad(rBD.getString("idCentroResponsabilidad"),
							rBD.getString("descripcion"), rBD.getString("clues"), rBD.getString("descripcionClues"),
							rBD.getString("tipoUnidad"), rBD.getString("tipologia"), rBD.getString("cluesParaMexico")));
				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener el catálogo de centros de responsabilidad, favor de contactar con el desarrollador del sistema."));

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

		return catCR;

	}

	// Obtiene el catálogo de correcciones de RFC validados por el SAT
	public static List<RFCCorreccion> getRFCCorrecciones(int tipoPlaza)
	{
		List<RFCCorreccion> rfccorrecciones = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion.prepareStatement("SELECT * FROM rfccorrecciones WHERE idPlaza=? ORDER BY NumEmpleado ASC");
			prep.setInt(1, tipoPlaza);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					rfccorrecciones.add(new RFCCorreccion(rBD.getInt("idPlaza"), rBD.getString("NumEmpleado"),
							rBD.getString("RFC"), rBD.getBoolean("Valido"), rBD.getString("RFCCorreccion")));
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

		return rfccorrecciones;

	}

	public static List<ReporteAnual> getReportesAnuales()
	{
		List<ReporteAnual> reportesAnuales = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(
					" SELECT ra.*, pl.Descripcion AS descripcionPlaza FROM reporteanual ra, webrh.plaza pl WHERE ra.idPlaza = pl.idPlaza ORDER BY ra.idReporteAnual DESC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					ReporteAnual rep = new ReporteAnual();
					rep.setIdReporteAnual(rBD.getInt("idReporteAnual"));
					rep.setAño(rBD.getInt("Año"));
					rep.setDescripcion(rBD.getString("Descripcion"));
					rep.setPlaza(new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcionPlaza")));
					rep.setComentarios(rBD.getString("Comentarios"));
					rep.setIncluirProducto(rBD.getBoolean("IncluirProducto"));
					rep.setIncluirCancelado(rBD.getBoolean("incluirCancelado"));
					rep.setTipoGeneracion(rBD.getInt("TipoGeneracion"));

					reportesAnuales.add(rep);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener el catálogo de reportes anuales, favor de contactar con el desarrollador del sistema."));

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

		return reportesAnuales;

	}

	public static List<Unidad> getUnidadesReporteAnual(int idReporteAnual)
	{
		List<Unidad> unidades = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(
					" SELECT ur.*, u.descripcion AS descripcionUnidad " + "FROM unidadreporteanual ur, webrh.unidad u "
							+ "WHERE ur.idReporteAnual = ? AND ur.idUnidad = u.idUnidad ");

			prep.setInt(1, idReporteAnual);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					unidades.add(new Unidad(rBD.getInt("idUnidad"), rBD.getString("descripcionUnidad")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener las unidades del reporte anual, favor de contactar con el desarrollador del sistema."));

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

		return unidades;

	}

	public static List<Integer> getEtapasReporteAnual(int idReporteAnual)
	{
		List<Integer> etapas = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(" SELECT * FROM etapareporteanual WHERE idReporteAnual=? ");

			prep.setInt(1, idReporteAnual);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					etapas.add(rBD.getInt("idEtapa"));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener las etapas del reporte anual, favor de contactar con el desarrollador del sistema."));

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

		return etapas;

	}

	public static List<Columna> getColumnasReporteAnual(ReporteAnual reporteAnual)
	{
		List<Columna> columnas = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(" SELECT * FROM columnareporteanual WHERE idReporteAnual=? ");

			prep.setInt(1, reporteAnual.getIdReporteAnual());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					columnas.add(new Columna(rBD.getInt("idColumnaReporteAnual"), reporteAnual, rBD.getInt("Orden"),
							rBD.getBoolean("AntesDelDetalle"), rBD.getString("Descripcion"), rBD.getString("Formula"),
							rBD.getInt("TipoDato")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener las columnas del reporte anual, favor de contactar con el desarrollador del sistema."));

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

		return columnas;

	}

	public static List<Rubro> getRubrosReporteAnual(ReporteAnual reporteAnual)
	{
		List<Rubro> rubros = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(" SELECT * FROM rubro WHERE idReporteAnual=? ");

			prep.setInt(1, reporteAnual.getIdReporteAnual());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					rubros.add(new Rubro(rBD.getInt("idRubro"), reporteAnual, rBD.getInt("Orden"),
							rBD.getString("Descripcion"), rBD.getString("Formula"), null, null));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener los rubros del reporte anual, favor de contactar con el desarrollador del sistema."));

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

		return rubros;

	}

	public static List<Unificacion> getUnificaciones(boolean obtenerEmpleados)
	{
		List<Unificacion> unificacion = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(" SELECT * FROM unificacion ORDER BY nombre ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					Unificacion unif = new Unificacion(rBD.getInt("idUnificacion"), rBD.getString("nombre"), "", null);

					if (obtenerEmpleados)
					{
						unif.setEmpleados(utilidades.getEmpleadosUnificacion(unif));
						// unif.setEmpleadosMap(utilidades.getEmpleadosMapUnificacion(unif));

					}

					unificacion.add(unif);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener las unificaciones, favor de contactar con el desarrollador del sistema."));

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

		return unificacion;

	}

	public static List<Empleado> getEmpleadosUnificacion(Unificacion unif)
	{

		List<Empleado> empleados = new ArrayList<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(
					" SELECT eu.*, emp.nombre, emp.observaciones, pl.descripcion FROM empleadounificado eu, empleado emp, webrh.Plaza pl WHERE eu.idPlaza= emp.idPlaza AND eu.NumEmpleado = emp.NumEmpleado AND idUnificacion=? AND eu.idPlaza = pl.idPlaza ");

			prep.setInt(1, unif.getIdUnificacion());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					empleados.add(new Empleado(rBD.getString("NumEmpleado"), rBD.getString("Nombre"),
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcion")),
							rBD.getString("Observaciones")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener los empleados de la unificación, favor de contactar con el desarrollador del sistema."));

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

		return empleados;

	}

	public static Map<String, Empleado> getEmpleadosMapUnificacion(Unificacion unif)
	{

		Map<String, Empleado> empleados = new HashMap<>();
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			prep = conexion.prepareStatement(
					" SELECT eu.*, emp.nombre, emp.observaciones, pl.descripcion FROM empleadounificado eu, empleado emp, webrh.Plaza pl WHERE eu.idPlaza= emp.idPlaza AND eu.NumEmpleado = emp.NumEmpleado AND idUnificacion=? AND eu.idPlaza = pl.idPlaza ");

			prep.setInt(1, unif.getIdUnificacion());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					Empleado emp = new Empleado(rBD.getString("NumEmpleado"), rBD.getString("Nombre"),
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcion")),
							rBD.getString("Observaciones"));
					empleados.put("" + emp.getPlaza().getIdPlaza() + "-" + emp.getNumEmpleado().trim(), emp);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener los empleados de la unificación, favor de contactar con el desarrollador del sistema."));

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

		return empleados;

	}

	public static List<Financiera> getCatalogoFinancieras()
	{

		List<Financiera> catFinancieras = new ArrayList<>();

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion.prepareStatement(" SELECT * FROM financiera ORDER BY Descripcion ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					catFinancieras.add(new Financiera(rBD.getInt("idFinanciera"), rBD.getString("Descripcion"),
							rBD.getString("AbreviacionArchivo"), rBD.getString("PartidaAntecedente")));

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener el catálogo de financieras, favor de contactar con el desarrollador del sistema."));

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

		return catFinancieras;

	}

	public static List<InstrumentoPago> getCatInstrumentosPago()
	{

		List<InstrumentoPago> catInstrumentosPago = new ArrayList<>();

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{

			prep = conexion.prepareStatement(
					" SELECT ip.*, ipl.idLayout, ipl.version, pl.descripcion AS descripcionPlaza, ly.descripcion AS descripcionLayout "
							+ "FROM webrh.instrumentopago ip "
							+ "LEFT JOIN nominas.instrumentopagolayout ipl ON ip.idInstrumentoPago = ipl.idInstrumentoPago AND ip.idPlaza = ipl.idPlaza "
							+ "JOIN webrh.plaza pl ON ip.idPlaza = pl.idPlaza "
							+ "LEFT JOIN webrh.layout ly ON ipl.idLayout = ly.idLayout "
							+ "ORDER BY ip.idPlaza ASC, ip.idInstrumentoPago ASC ");

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					InstrumentoPago instrumentoPago = new InstrumentoPago(
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("DescripcionPlaza")),
							rBD.getString("idInstrumentoPago"), rBD.getString("descripcion"),
							rBD.getString("Abreviacion"), rBD.getString("CaracterTerminador"),
							rBD.getString("IndicativoArchivo"), rBD.getString("Separador"));

					if (rBD.getString("idLayout") != null)
					{
						instrumentoPago
								.setLayout(new Layout(rBD.getInt("idLayout"), rBD.getString("descripcionLayout")));

						instrumentoPago
								.setLayoutVersion(new LayoutVersion(rBD.getInt("idLayout"), rBD.getString("version")));

					}

					catInstrumentosPago.add(instrumentoPago);

				} while (rBD.next());
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al momento obtener el catálogo de instrumentos de pago, favor de contactar con el desarrollador del sistema."));

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

		return catInstrumentosPago;

	}

	// Método que ayuda a la determinación de la unidad correcta de acuerdo a la
	// plaza
	public static String determinaUnidad(Plaza plaza, PlantillaRegistro regDAT)
	{
		String unidad = null;

		// String funcionCompleta;

		Plantilla plantillaDAT = regDAT.getPlantilla();

		int indiceUnidadResponsable = plantillaDAT.getPosicionValorPorDescripcionContains("Unidad R");
		int indiceProyecto = plantillaDAT.getPosicionValorPorDescripcionContains("Proyecto Proce");
		int indiceGrupoFuncional = plantillaDAT.getPosicionValorPorDescripcionContains("Grupo Funcion");
		int indiceFuncion = plantillaDAT.getPosicionValorPorDescripcion("Función");
		int indiceSubfuncion = plantillaDAT.getPosicionValorPorDescripcion("Subfunción");
		int indiceActividadIns = plantillaDAT.getPosicionValorPorDescripcionContains("actividad ins");
		int indicePagaduria = plantillaDAT.getPosicionValorPorDescripcionContains("Pagadur");

		// funcionCompleta =
		// regDAT.getValorEnCampo(indiceGrupoFuncional).toLowerCase()
		// + regDAT.getValorEnCampo(indiceFuncion).toLowerCase()
		// + regDAT.getValorEnCampo(indiceSubfuncion).toLowerCase();

		if (plaza.getDescripcionPlaza().toLowerCase().contains("base"))
		{
			unidad = regDAT.getValorEnCampo(indiceUnidadResponsable);

		}
		else if (plaza.getDescripcionPlaza().toLowerCase().contains("reg"))
		{

			if (regDAT.getValorEnCampo(indiceProyecto).toLowerCase().contains("i204"))
			{
				unidad = "416";
			}
			else if (regDAT.getValorEnCampo(indiceProyecto).toLowerCase().contains("004"))
			{

				if (regDAT.getValorEnCampo(indiceActividadIns).toLowerCase().contains("022"))
				{
					unidad = "U00";
				}
				else if (regDAT.getValorEnCampo(indiceActividadIns).toLowerCase().contains("004"))
				{
					unidad = "U01";
				}
			}

		}
		else if (plaza.getDescripcionPlaza().toLowerCase().contains("formal"))
		{

			/*
			 * if (regDAT.getValorEnCampo(indicePagaduria).contains("U0004")) { unidad =
			 * "U00"; } else { unidad = "416"; }
			 */

			unidad = regDAT.getValorEnCampo(indiceUnidadResponsable);

			//    unidad = "FORMAL";

		}
		else if (plaza.getDescripcionPlaza().toLowerCase().contains("abp"))
		{

			unidad = "CON";

		}
		else
		{
			unidad = regDAT.getValorEnCampo(indiceUnidadResponsable);

		}

		return unidad;

	}

	public static String getNombreMes(int quincena)
	{
		String mes = null;

		switch (quincena)
		{
			case 1:
			case 2:
				mes = "ENERO";
				break;

			case 3:
			case 4:
				mes = "FEBRERO";
				break;

			case 5:
			case 6:
				mes = "MARZO";
				break;

			case 7:
			case 8:
				mes = "ABRIL";
				break;

			case 9:
			case 10:
				mes = "MAYO";
				break;

			case 11:
			case 12:
				mes = "JUNIO";
				break;

			case 13:
			case 14:
				mes = "JULIO";
				break;

			case 15:
			case 16:
				mes = "AGOSTO";
				break;

			case 17:
			case 18:
				mes = "SEPTIEMBRE";
				break;

			case 19:
			case 20:
				mes = "OCTUBRE";
				break;

			case 21:
			case 22:
				mes = "NOVIEMBRE";
				break;

			case 23:
			case 24:
				mes = "DICIEMBRE";
				break;

			default:
				break;
		}

		return mes;

	}

	public static LayoutVersion getVersionLayoutIP(int idPlaza, String idInstrumentoPago)
	{
		PreparedStatement prep = null;
		LayoutVersion version = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
		{
			prep = conexion.prepareStatement(" SELECT ipl.*, lv.* FROM nominas.instrumentopagolayout ipl "
					+ "LEFT JOIN webrh.layoutversion lv ON lv.idLayout = ipl.idLayout AND lv.version = ipl.version "
					+ "WHERE ipl.idPlaza=? AND ipl.idInstrumentoPago=? ");

			prep.setInt(1, idPlaza);
			prep.setString(2, idInstrumentoPago);

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				version = new LayoutVersion(rBD.getInt("idLayout"), rBD.getString("version"),
						rBD.getDate("FechaEmision"));

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

		return version;

	}

	public static int getMaxIdRegistro(ArchivoBanco archivoBanco)
	{
		PreparedStatement prep = null;
		LayoutVersion version = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement(
					" SELECT MAX(idRegistro)+1  AS id FROM archivopagovalores WHERE idArchivoPago=? ");

			prep.setInt(1, archivoBanco.getIdArchivoPago());

			ResultSet rBD = prep.executeQuery();

			if (rBD.next())
			{
				return rBD.getInt("id");

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

		return 0;

	}

	public static PlantillaRegistro añadirRegistroArchivoBanco(ArchivoBanco archivoBanco,
			PlantillaRegistro regDuplicando)

	{
		PreparedStatement prep = null;

		PlantillaRegistro registroNuevo = null;

		List<CampoPlantilla> camposGrabarEnBD = null;

		int idRegistroNuevo = utilidades.getMaxIdRegistro(archivoBanco);

		if (regDuplicando != null)
		{
			registroNuevo = (PlantillaRegistro) regDuplicando.clone();
			registroNuevo.setIdRegistro(idRegistroNuevo);
			camposGrabarEnBD = regDuplicando.getPlantilla().getCampos();

		}
		else
		{
			registroNuevo = new PlantillaRegistro(idRegistroNuevo, (Plantilla) archivoBanco.getPlantilla().clone());
			camposGrabarEnBD = archivoBanco.getPlantilla().getCampos();

		}

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			// Se registran todos los campos de la plantilla del registro del
			// archivo del banco en blanco para que se pueda cargar nuevamente
			// los registros del archivo de pago de bancos nuevo

			for (CampoPlantilla campo : camposGrabarEnBD)
			{

				prep = conexion.prepareStatement(
						" INSERT INTO archivopagovalores (idArchivoPago, idPlantilla, Orden, Valor, idRegistro) VALUES (?, ?, ?, ?, ?); ");

				prep.setInt(1, archivoBanco.getIdArchivoPago());
				prep.setInt(2, archivoBanco.getPlantilla().getIdPlantilla());
				prep.setInt(3, campo.getOrden());

				if (regDuplicando != null)
				{
					prep.setString(4, campo.getValor());

				}
				else
				{
					prep.setString(4, "");

				}

				prep.setInt(5, idRegistroNuevo);

				prep.executeUpdate();
				prep.close();

				if (regDuplicando == null)
				{
					campo.setValor("");
				}

			}

			prep = conexion.prepareStatement(
					" INSERT INTO registropagonuevo (idArchivoPago, idPlantilla, idRegistro) VALUES (?, ?, ?); ");

			prep.setInt(1, archivoBanco.getIdArchivoPago());
			prep.setInt(2, archivoBanco.getPlantilla().getIdPlantilla());
			prep.setInt(3, registroNuevo.getIdRegistro());

			prep.executeUpdate();

			prep.close();

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

		return registroNuevo;
	}

	public static List<ArchivoBanco> getCatalogoArchivosBanco()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<ArchivoBanco> catArchivosBancos = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement(
					" SELECT ap.idArchivoPago, ap.idInstrumentoPago, ap.Observaciones,  pl.idPlaza, ap.idPlantilla, pl.descripcion AS descripcionPlaza, ap.Descripcion, ap.Ano, ap.Qna, ap.FechaCarga, ap.HoraCarga, ip.descripcion FROM nominas.archivopago ap, webrh.plaza pl, webrh.instrumentopago ip WHERE ap.idPlaza = pl.idPlaza AND ( ap.idPlaza = ip.idPlaza AND ap.idInstrumentoPago = ip.idInstrumentoPago )  ORDER BY ap.Ano DESC, ap.Qna DESC, ap.idInstrumentoPago ASC ");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					catArchivosBancos.add(new ArchivoBanco(rBD.getInt("idArchivoPago"), rBD.getString("Descripcion"),
							new Plaza(rBD.getInt("idPlaza"), rBD.getString("descripcionPlaza")),
							rBD.getString("idInstrumentoPago"), rBD.getInt("Ano"), rBD.getInt("Qna"),
							rBD.getString("Observaciones"), new Plantilla(rBD.getInt("idPlantilla"))));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al guardar la edición del dato, favor de contactar con el desarrollador del sistema."));

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

		return catArchivosBancos;

	}

	// Se obtiene los vínculos creados de las plantillas de los anexos
	public static List<VinculoPlantilla> getVinculosPlantilla(Plantilla plantilla)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<VinculoPlantilla> catVinculosPlantilla = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			prep = conexion.prepareStatement(" SELECT vp.* FROM siri.vinculoplantilla vp WHERE vp.idPlantilla=? ");

			prep.setInt(1, plantilla.getIdPlantilla());

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					catVinculosPlantilla.add(new VinculoPlantilla(rBD.getInt("idVinculoPlantilla"),
							rBD.getString("Descripcion"), null, null));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los vínculos creados para la plantilla, favor de contactar con el desarrollador del sistema."));

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

		return catVinculosPlantilla;

	}

	public static String getDescripcionConceptoCatalogo(int tipo, String clave, String partidaAntecedente)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
		{
			prep = conexion.prepareStatement(
					" SELECT descripcion FROM webrh.concepto WHERE tipoConcepto=? AND idConcepto=? AND PartidaAntecedente=?");

			prep.setInt(1, tipo);
			prep.setString(2, clave);
			prep.setString(3, partidaAntecedente);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				return rBD.getString("Descripcion");

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener la descripción del concepto en el catálogo, favor de contactar con el desarrollador del sistema."));

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

		return null;

	}

	// Se obtiene el catálogo de configuraciones para imprimir en pdf registradas en
	// el sistema
	public static List<ConfiguracionPdf> getCatConfiguracionPdf()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		List<ConfiguracionPdf> catConfiguracionPdf = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSpoolerPDF();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM configuracionpdf order by descripcion ASC");

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					ConfiguracionPdf conf = new ConfiguracionPdf();

					conf.setIdConfiguracionPdf(rBD.getInt("idConfiguracionPdf"));
					conf.setDescripcion(rBD.getString("Descripcion"));

					catConfiguracionPdf.add(conf);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los catálogos de configuración Pdf, favor de contactar con el desarrollador del sistema."));

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

		return catConfiguracionPdf;

	}

	public static ConfiguracionPdf getConfiguracionPdf(int idConfiguracionPdf)
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;

		ConfiguracionPdf conf = new ConfiguracionPdf();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSpoolerPDF();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM configuracionpdf WHERE idConfiguracionPDF=?");
			prep.setInt(1, idConfiguracionPdf);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{
					conf.setIdConfiguracionPdf(rBD.getInt("idConfiguracionPdf"));
					conf.setDescripcion(rBD.getString("Descripcion"));

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener la configuración Pdf, favor de contactar con el desarrollador del sistema."));

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

		return conf;

	}

}
