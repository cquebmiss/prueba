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

public class ClasificacionGrupo
{
	private int				idClasificacionGrupo;
	private Clasificacion	clasificacion;
	private Jornada			jornada;
	private int				total;
	private List<Folio>		folios;

	public ClasificacionGrupo()
	{
		super();
		this.folios = new ArrayList<>();
		// TODO Auto-generated constructor stub
	}

	public void sortear(int totalPersonal)
	{
		this.folios = new ArrayList<>();

		PreparedStatement prep = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement("INSERT INTO folio (idClasificacionGrupo,Folio,Encuestado) VALUE (?,?,?)");

			for (int x = 0; x < total; x++)
			{
				Folio folio = new Folio();

				boolean repetido;
				int aleatorio;

				do
				{
					repetido = false;

					aleatorio = ((int) (Math.random() * totalPersonal) + 1);

					for (Folio fol : this.folios)
					{

						if (fol.getNumero() == aleatorio)
						{
							repetido = true;
							break;
						}

					}

				} while (repetido);

				folio.setNumero(aleatorio);

				String folioString = "1" + this.clasificacion.getIdClasificacion() + "" + this.jornada.getIdJornada()
						+ "" + folio.getNumero();
				folio.setIdFolio(Integer.parseInt(folioString));
				this.folios.add(folio);

				prep.setInt(1, this.idClasificacionGrupo);
				prep.setInt(2, folio.getIdFolio());
				prep.setBoolean(3, false);

				prep.addBatch();

			}

			prep.executeBatch();

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al registrar el sorteo de la clasificación, favor de contactar con el desarrollador del sistema."));

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

	public void updateFoliosFromBD()
	{
		PreparedStatement prep = null;
		ResultSet rBD = null;
		this.folios = new ArrayList<>();

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM folio WHERE idClasificacionGrupo=? ");
			prep.setInt(1, this.idClasificacionGrupo);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				do
				{

					Folio fol = new Folio();
					fol.setIdFolio(rBD.getInt("Folio"));
					String folioString = "" + fol.getIdFolio();
					folioString = folioString.substring(2, folioString.length());
					fol.setNumero(Integer.parseInt(folioString));
					fol.setEncuestado(rBD.getBoolean("Encuestado"));

					this.folios.add(fol);

				} while (rBD.next());

			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al obtener los folios registrados, favor de contactar con el desarrollador del sistema."));

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

	public Clasificacion getClasificacion()
	{
		return clasificacion;
	}

	public void setClasificacion(Clasificacion clasificacion)
	{
		this.clasificacion = clasificacion;
	}

	public Jornada getJornada()
	{
		return jornada;
	}

	public void setJornada(Jornada jornada)
	{
		this.jornada = jornada;
	}

	public int getIdClasificacionGrupo()
	{
		return idClasificacionGrupo;
	}

	public void setIdClasificacionGrupo(int idClasificacionGrupo)
	{
		this.idClasificacionGrupo = idClasificacionGrupo;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	public List<Folio> getFolios()
	{
		return folios;
	}

	public void setFolios(List<Folio> folios)
	{
		this.folios = folios;
	}

}
