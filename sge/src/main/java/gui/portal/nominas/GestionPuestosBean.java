package gui.portal.nominas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.UploadedFile;

import modelo.ArchivoPuesto;
import modelo.ColumnModel;
import modelo.Layout;
import modelo.LayoutVersion;
import modelo.Plaza;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@ViewScoped
public class GestionPuestosBean implements Serializable
{
	private int					idPlazaSelec;
	private int					añoSelec;
	private int					qnaSelec;

	private List<Plaza>			catPlazas;

	private Layout				layoutPuestos;
	private String				versionSeleccionada;
	private String				comentariosArchivo;

	private UploadedFile		archivoPuestos;

	private ArchivoPuesto		archivoPuesto;
	private List<ColumnModel>	columnasRegistros;

	private DataTable			tablaRegistros;

	public GestionPuestosBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postCreate()
	{

		this.catPlazas = utilidades.getPlazas();

		java.util.Calendar fechaActual = java.util.Calendar.getInstance();
		this.añoSelec = fechaActual.get(Calendar.YEAR);

		this.qnaSelec = (fechaActual.get(Calendar.MONTH) + 1) * 2;

		if (fechaActual.get(Calendar.DAY_OF_MONTH) < 16)
		{
			this.qnaSelec -= 1;
		}

	}

	@PreDestroy
	public void preDestroy()
	{

	}

	public void actionObtenerRegistrosCatalogoPuestos()
	{
		this.columnasRegistros = new ArrayList<>();
		this.archivoPuesto = null;
		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM archivopuesto WHERE idPlaza=? AND Ano=? AND Qna=?");

			prep.setInt(1, this.idPlazaSelec);
			prep.setInt(2, this.añoSelec);
			prep.setInt(3, this.qnaSelec);

			rBD = prep.executeQuery();

			if (rBD.next())
			{

				this.archivoPuesto = utilidades.getArchivosPuesto(this.añoSelec, this.qnaSelec, this.añoSelec,
						this.qnaSelec, this.idPlazaSelec).get(0);
				this.archivoPuesto.updatePlantillaRegistros();
				this.archivoPuesto.updateRegistros();
				this.columnasRegistros = utilidades.getColumnModel(this.archivoPuesto.getPlantilla());

			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo No Cargado",
								"Aún no se ha cargado el archivo de catálogo de puestos para la quincena indicada."));
				return;

			}

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener el índice del archivo de puestos, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	public void actionPrepararDialogoIngresoArchivo()
	{
		this.versionSeleccionada = "";
		this.comentariosArchivo = "";
		this.layoutPuestos = new Layout(10, "Catálogo de Puestos");
		this.layoutPuestos.updateVersiones();
	}

	public void actionEliminarCatalogoPuestos()
	{
		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{
			prep = conexion.prepareStatement(
					"DELETE FROM archivopuestovalores WHERE idArchivoPuesto IN( SELECT idArchivoPuesto FROM archivopuesto"
							+ " WHERE idPlaza=? AND Qna=? AND Ano=?)");

			prep.setInt(1, this.idPlazaSelec);
			prep.setInt(2, this.qnaSelec);
			prep.setInt(3, this.añoSelec);

			prep.executeUpdate();

			prep.close();

			prep = conexion.prepareStatement("DELETE FROM archivopuesto WHERE idPlaza=? AND Qna=? AND Ano=?");

			prep.setInt(1, this.idPlazaSelec);
			prep.setInt(2, this.qnaSelec);
			prep.setInt(3, this.añoSelec);

			prep.executeUpdate();

			actionObtenerRegistrosCatalogoPuestos();

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al eliminar el archivo de puestos, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	public void actionGuardarArchivo()
	{

		PreparedStatement prep = null;
		BufferedReader buffer = null;

		try
		{
			buffer = new BufferedReader(new InputStreamReader(this.archivoPuestos.getInputstream()));

		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción", "Ha ocurrido una excepción al cargar el archivo, favor de verificar el archivo."));

			e1.printStackTrace();
		}

		LayoutVersion versionArchivo = this.layoutPuestos.getVersion(this.versionSeleccionada);
		versionArchivo.updatePlantillasDetalle(false);

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			// Se valida que ya exista el archivo de puestos en el sistema
			prep = conexion.prepareStatement(" SELECT * FROM archivopuesto WHERE idPlaza=? AND Ano=? AND Qna=? ");

			prep.setInt(1, this.idPlazaSelec);
			prep.setInt(2, this.añoSelec);
			prep.setInt(3, this.qnaSelec);

			ResultSet rBD = prep.executeQuery();

			int idArchivoBanco = 0;

			if (rBD.next())
			{

				idArchivoBanco = rBD.getInt("idArchivopuesto");

				prep.close();

				prep = conexion.prepareStatement(
						"UPDATE archivopuesto SET Descripcion=?, Observaciones=? WHERE idArchivoPuesto=? ");
				prep.setString(1, this.archivoPuestos.getFileName());
				prep.setString(2, this.comentariosArchivo);
				prep.setInt(3, idArchivoBanco);

				prep.executeUpdate();

			}
			else
			{
				prep.close();

				// Se inserta primero el registro y luego se obtiene el índice
				// que se obtuvo ya que solo puede haber un archivo de puestos
				// por cada nómina en una quincena y año específico

				prep = conexion.prepareStatement(
						"INSERT INTO archivopuesto (Descripcion, idPlaza, Ano, Qna, Observaciones, idPlantilla) VALUES ( ?, ?, ?, ?, ?, ?) ");

				prep.setString(1, this.archivoPuestos.getFileName());
				prep.setInt(2, this.idPlazaSelec);
				prep.setInt(3, this.añoSelec);
				prep.setInt(4, this.qnaSelec);
				prep.setString(5, this.comentariosArchivo);
				prep.setInt(6, versionArchivo.getDetalles().get(0).getIdPlantilla());

				prep.executeUpdate();

				// Se obtiene el id resultante

				prep.close();

				prep = conexion.prepareStatement("SELECT * FROM archivopuesto WHERE idPlaza=? AND Ano=? AND Qna=?");

				prep.setInt(1, this.idPlazaSelec);
				prep.setInt(2, this.añoSelec);
				prep.setInt(3, this.qnaSelec);

				rBD = prep.executeQuery();

				if (rBD.next())
				{
					idArchivoBanco = rBD.getInt("idArchivoPuesto");
				}

			}

			// Ahora se inserta todo el contenido de el archivo dentro de la
			// tabla de datos
			String linea;
			int orden;
			boolean lecturanula = false;
			int nLinea = 1;
			int nLineaTotal = 1;
			String[] camposLinea = null;
			prep = conexion.prepareStatement(
					"INSERT INTO archivopuestovalores (idArchivoPuesto, idPlantilla, Orden, Valor, idRegistro) VALUES (?, ?, ?, ?, ?) ");

			while (!lecturanula)
			{

				try
				{

					linea = buffer.readLine();

					if (linea == null)
					{

						lecturanula = true;
						System.out.println("terminó lectura");

					}
					else
					{

						if (linea.trim().length() < 1)
						{
							return;
						}

						orden = 1;

						camposLinea = linea
								.split("[" + versionArchivo.getDetalles().get(0).getCaracterSeparador() + "]");

						System.out.println(nLinea);

						for (String valorCampo : camposLinea)
						{

							prep.setInt(1, idArchivoBanco);
							prep.setInt(2, versionArchivo.getDetalles().get(0).getIdPlantilla());
							prep.setInt(3, orden);
							prep.setString(4, valorCampo);
							prep.setInt(5, nLinea);

							prep.addBatch();

							orden++;

						}

						prep.executeBatch();

					}

					nLinea++;
					nLineaTotal++;

				} catch (IOException ex)
				{
					System.out.println(ex);
					return;

				}

			}

			actionObtenerRegistrosCatalogoPuestos();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Archivo Cargado", "La información del archivo se ha cargado exitosamente."));

		} catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al cargar el archivo de puestos, favor de contactar con el desarrollador del sistema."));

			e.printStackTrace();
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

	public int getIdPlazaSelec()
	{
		return idPlazaSelec;
	}

	public void setIdPlazaSelec(int idPlazaSelec)
	{
		this.idPlazaSelec = idPlazaSelec;
	}

	public int getAñoSelec()
	{
		return añoSelec;
	}

	public void setAñoSelec(int añoSelec)
	{
		this.añoSelec = añoSelec;
	}

	public int getQnaSelec()
	{
		return qnaSelec;
	}

	public void setQnaSelec(int qnaSelec)
	{
		this.qnaSelec = qnaSelec;
	}

	public List<Plaza> getCatPlazas()
	{
		return catPlazas;
	}

	public void setCatPlazas(List<Plaza> catPlazas)
	{
		this.catPlazas = catPlazas;
	}

	public Layout getLayoutPuestos()
	{
		return layoutPuestos;
	}

	public void setLayoutPuestos(Layout layoutPuestos)
	{
		this.layoutPuestos = layoutPuestos;
	}

	public String getComentariosArchivo()
	{
		return comentariosArchivo;
	}

	public void setComentariosArchivo(String comentariosArchivo)
	{
		this.comentariosArchivo = comentariosArchivo;
	}

	public UploadedFile getArchivoPuestos()
	{
		return archivoPuestos;
	}

	public void setArchivoPuestos(UploadedFile archivoPuestos)
	{
		this.archivoPuestos = archivoPuestos;
	}

	public String getVersionSeleccionada()
	{
		return versionSeleccionada;
	}

	public void setVersionSeleccionada(String versionSeleccionada)
	{
		this.versionSeleccionada = versionSeleccionada;
	}

	public ArchivoPuesto getArchivoPuesto()
	{
		return archivoPuesto;
	}

	public void setArchivoPuesto(ArchivoPuesto archivoPuesto)
	{
		this.archivoPuesto = archivoPuesto;
	}

	public List<ColumnModel> getColumnasRegistros()
	{
		return columnasRegistros;
	}

	public void setColumnasRegistros(List<ColumnModel> columnasRegistros)
	{
		this.columnasRegistros = columnasRegistros;
	}

	public DataTable getTablaRegistros()
	{
		return tablaRegistros;
	}

	public void setTablaRegistros(DataTable tablaRegistros)
	{
		this.tablaRegistros = tablaRegistros;
	}

}
