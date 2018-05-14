package gui.portal.minuta;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;

import modelo.actividades.Actividad;
import modelo.actividades.StatusActividad;
import modelo.minutas.Compromiso;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class DialogoCompromisoBean
{

	private Compromiso compromiso;

	public DialogoCompromisoBean()
	{
		super();
	}

	@PostConstruct
	public void postConstruct()
	{
		addCompromisoNuevo();
	}

	//Inicializa una nueva actividad asignada a la minuta con status -1 de actividad creada 
	//El status del compromiso es el status de la actividad creada para el compromiso
	public void addCompromisoNuevo()
	{
		MinutaBean minutaBean = (MinutaBean) FacesUtils.getManagedBean("minutaBean");
		this.compromiso = new Compromiso(-1, minutaBean.getMinuta(), new Actividad(-1, "", new StatusActividad(-1, "")),
				"", -1, null, null, null);
	}

	public void actionGuardarCompromiso()
	{
		MinutaBean minutaBean = (MinutaBean) FacesUtils.getManagedBean("minutaBean");
		boolean correcto = true;

		if (this.compromiso.getOrden() == -1)
		{
			this.compromiso.setMinuta(minutaBean.getMinuta());

			if (!minutaBean.getMinuta().addCompromisoBD(this.compromiso))
			{
				correcto = false;
			}
		}
		else
		{
			if (!minutaBean.getMinuta().updateCompromisoBD(this.compromiso))
			{
				correcto = false;
			}
		}

		PrimeFaces.current().ajax().addCallbackParam("compromisoAñadido", correcto);

	}

	public Compromiso getCompromiso()
	{
		return compromiso;
	}

	public void setCompromiso(Compromiso compromiso)
	{
		this.compromiso = compromiso;
	}

}
