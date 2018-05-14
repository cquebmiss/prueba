package modelo.climalaboral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import resources.DataBase;
import util.FacesUtils;

public class Clasificacion
{

	private int							idClasificacion;
	private String						descripcion;
	private int							totalPersonal;
	private List<ClasificacionGrupo>	jornadasClasificadas;

	public Clasificacion()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Clasificacion(int idClasificacion, String descripcion, int totalPersonal)
	{
		super();
		this.idClasificacion = idClasificacion;
		this.descripcion = descripcion;
		this.totalPersonal = totalPersonal;
	}

	public void sortear()
	{
		for (ClasificacionGrupo clasGrup : this.jornadasClasificadas)
		{
			clasGrup.sortear(this.totalPersonal);
		}
	}

	public int getTotalPorJornadaClasificada(int idJornada)
	{
		int total = 0;

		for (ClasificacionGrupo clasGrupo : this.jornadasClasificadas)
		{
			if (clasGrupo.getJornada().getIdJornada() == idJornada)
			{
				return clasGrupo.getTotal();
			}

		}

		return total;
	}

	public void updateJornadasClasificadasFromBD()
	{

		PreparedStatement prep = null;
		ResultSet rBD = null;

		this.jornadasClasificadas = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(
					" SELECT cg.idClasificacionGrupo, cg.idClasificacion, cg.idJornada, cg.Total, j.Descripcion AS descJornada from clasificaciongrupo cg, jornada j\n"
							+ "WHERE cg.idJornada = j.idJornada AND cg.idClasificacion=? order by cg.idJornada ASC");
			prep.setInt(1, this.idClasificacion);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					ClasificacionGrupo obj = new ClasificacionGrupo();
					obj.setIdClasificacionGrupo(rBD.getInt("idClasificacionGrupo"));
					obj.setClasificacion(this);
					obj.setJornada(new Jornada(rBD.getInt("idJornada"), rBD.getString("descJornada")));
					obj.setTotal(rBD.getInt("Total"));

					this.jornadasClasificadas.add(obj);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener las jornadas clasificadas, favor de contactar con el desarrollador del sistema."));

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

	public int getIdClasificacion()
	{
		return idClasificacion;
	}

	public void setIdClasificacion(int idClasificacion)
	{
		this.idClasificacion = idClasificacion;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public List<ClasificacionGrupo> getJornadasClasificadas()
	{
		return jornadasClasificadas;
	}

	public void setJornadasClasificadas(List<ClasificacionGrupo> jornadasClasificadas)
	{
		this.jornadasClasificadas = jornadasClasificadas;
	}

	public int getTotalPersonal()
	{
		return totalPersonal;
	}

	public void setTotalPersonal(int totalPersonal)
	{
		this.totalPersonal = totalPersonal;
	}

}
