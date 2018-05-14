package gui.portal.climalaboral;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import modelo.climalaboral.Clasificacion;
import modelo.climalaboral.ClasificacionGrupo;
import modelo.climalaboral.Jornada;
import util.climalaboral.UtilidadesClimaLaboral;

@ManagedBean
@SessionScoped
public class MuestreoBean
{
	private List<Clasificacion>	clasificaciones;
	private List<Jornada>		catJornadas;
	private int					total;

	public MuestreoBean()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void postConstruct()
	{
		this.total = 0;
		this.catJornadas = UtilidadesClimaLaboral.getCatJornadas();

		this.clasificaciones = UtilidadesClimaLaboral.getCatClasificacion();

		//Se obtienen las clasificaciones de las jornadas
		for (Clasificacion clasificacion : this.clasificaciones)
		{
			clasificacion.updateJornadasClasificadasFromBD();

			for (ClasificacionGrupo clasGrup : clasificacion.getJornadasClasificadas())
			{
				clasGrup.updateFoliosFromBD();
				total += clasGrup.getTotal();
				System.out.println(clasificacion.getDescripcion() + ": " + clasGrup.getJornada().getDescripcion() + ": "
						+ clasGrup.getTotal());
			}
		}

	}

	public void realizarSorteo()
	{
		for (Clasificacion clas : this.clasificaciones)
		{
			clas.sortear();

		}
	}

	public List<Clasificacion> getClasificaciones()
	{
		return clasificaciones;
	}

	public void setClasificaciones(List<Clasificacion> clasificaciones)
	{
		this.clasificaciones = clasificaciones;
	}

	public List<Jornada> getCatJornadas()
	{
		return catJornadas;
	}

	public void setCatJornadas(List<Jornada> catJornadas)
	{
		this.catJornadas = catJornadas;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

}
