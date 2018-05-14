package gui.portal.climalaboral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import modelo.climalaboral.Area;
import modelo.climalaboral.Clasificacion;
import modelo.climalaboral.Encuesta;
import modelo.climalaboral.Jornada;
import modelo.climalaboral.Profesion;
import resources.DataBase;
import util.FacesUtils;
import util.climalaboral.UtilidadesClimaLaboral;

@ManagedBean
@SessionScoped
public class PersistEncuestaBean
{

	private List<Area>		catAreas;
	private List<Profesion>	catProfesiones;
	private List<Jornada>	catJornadas;
	private Area			areaSelec;
	private Profesion		profesionSelec;
	private Jornada			jornadaSelec;
	private int				folio;

	public PersistEncuestaBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postConstruct()
	{
		this.folio = 0;
		iniciarCatalogos();

	}

	public void iniciarCatalogos()
	{
		this.folio = 0;
		this.catAreas = UtilidadesClimaLaboral.getCatAreas();
		this.catProfesiones = UtilidadesClimaLaboral.getCatProfesiones();
		this.catJornadas = UtilidadesClimaLaboral.getCatJornadas();
	}

	//0 folio válido, 1 ya encuestado, -1 inexistente
	public int validaFolio()
	{
		int resultado = -1;

		PreparedStatement prep = null;
		ResultSet rBD = null;

		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionClimaLaboral();)
		{
			prep = conexion.prepareStatement(" SELECT * FROM folio WHERE folio=?");
			prep.setInt(1, this.folio);

			rBD = prep.executeQuery();

			if (rBD.next())
			{
				return rBD.getInt("Encuestado");
			}

		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excepción",
					"Ha ocurrido una excepción al validar el folio, favor de contactar con el desarrollador del sistema."));

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

		return resultado;

	}

	public void actionValidarFolio()
	{

		switch (validaFolio())
		{
			case 0:
				EncuestasBean bean = (EncuestasBean) FacesUtils.getManagedBean("encuestasBean");
				bean.setEstadoModulo(0);
			break;

			case 1:
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Folio Ya Registrado", "El folio ya ha sido utilizado para contestar la encuesta."));
			break;

			case -1:
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Folio Inexistente", "Por favor verifique el folio."));

			break;

		}
	}

	public void actionRegistrarEncuesta(Encuesta encuesta)
	{
		encuesta.persistRegistroEncuesta(this.areaSelec, this.profesionSelec, this.jornadaSelec, this.folio);
	}

	public Area getAreaSelec()
	{
		return areaSelec;
	}

	public void setAreaSelec(Area areaSelec)
	{
		this.areaSelec = areaSelec;
	}

	public Profesion getProfesionSelec()
	{
		return profesionSelec;
	}

	public void setProfesionSelec(Profesion profesionSelec)
	{
		this.profesionSelec = profesionSelec;
	}

	public Jornada getJornadaSelec()
	{
		return jornadaSelec;
	}

	public void setJornadaSelec(Jornada jornadaSelec)
	{
		this.jornadaSelec = jornadaSelec;
	}

	public List<Area> getCatAreas()
	{
		return catAreas;
	}

	public void setCatAreas(List<Area> catAreas)
	{
		this.catAreas = catAreas;
	}

	public List<Profesion> getCatProfesiones()
	{
		return catProfesiones;
	}

	public void setCatProfesiones(List<Profesion> catProfesiones)
	{
		this.catProfesiones = catProfesiones;
	}

	public List<Jornada> getCatJornadas()
	{
		return catJornadas;
	}

	public void setCatJornadas(List<Jornada> catJornadas)
	{
		this.catJornadas = catJornadas;
	}

	public int getFolio()
	{
		return folio;
	}

	public void setFolio(int folio)
	{
		this.folio = folio;
	}

}
