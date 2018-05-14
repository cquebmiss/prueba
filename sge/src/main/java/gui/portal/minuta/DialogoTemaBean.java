package gui.portal.minuta;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;

import modelo.minutas.Compromiso;
import modelo.minutas.TemaMinuta;
import util.FacesUtils;
import util.minutas.UtilidadesMinutas;

@ManagedBean
@ViewScoped
public class DialogoTemaBean
{

	private TemaMinuta temaMinuta;

	private List<Compromiso> compromisosNoFinalizados;
	private Compromiso temaCompromiso;

	public DialogoTemaBean()
	{
		super();

	}

	@PostConstruct
	public void postConstruct()
	{
		addTemaNuevo();
	}

	public void addTemaNuevo()
	{
		this.temaMinuta = new TemaMinuta(null, -1, null, null, null);
	}
	
	public void preparaDialogoCompromisosNoResueltos()
	{
		addTemaNuevo();
		this.compromisosNoFinalizados = UtilidadesMinutas.getHistorialCompromisosBD(true);
	}

	public void actionGuardarTema()
	{
		MinutaBean minutaBean = (MinutaBean) FacesUtils.getManagedBean("minutaBean");
		boolean correcto = true;

		if (this.temaMinuta.getOrden() == -1)
		{
			this.temaMinuta.setMinuta(minutaBean.getMinuta());

			if (!minutaBean.getMinuta().addNuevoTemaBD(this.temaMinuta))
			{
				correcto = false;
			}
		}
		else
		{
			if (!minutaBean.getMinuta().updateTemaMinutaBD(this.temaMinuta))
			{
				correcto = false;
			}
		}

		PrimeFaces.current().ajax().addCallbackParam("temaAñadido", correcto);

	}

	public void actionUpdateDesarrolloTemaMinuta()
	{
		MinutaBean minutaBean = (MinutaBean) FacesUtils.getManagedBean("minutaBean");
		boolean correcto = true;

		if (!minutaBean.getMinuta().updateDesarrolloTemaMinutaBD(this.temaMinuta))
		{
			correcto = false;
		}

		PrimeFaces.current().ajax().addCallbackParam("temaActualizado", correcto);

	}

	public void updateDesarrolloTema()
	{

	}
	
	public void actionGuardarTemaDeCompromiNoFinalizado()
	{
		//Se añade el tema del compromiso como la descripción del tema de la minuta
		this.temaMinuta.setDescripcion(this.temaCompromiso.getDescripcion());
		actionGuardarTema();
		
	}

	public TemaMinuta getTemaMinuta()
	{
		return temaMinuta;
	}

	public void setTemaMinuta(TemaMinuta temaMinuta)
	{
		this.temaMinuta = temaMinuta;
	}

	public Compromiso getTemaCompromiso()
	{
		return temaCompromiso;
	}

	public void setTemaCompromiso(Compromiso temaCompromiso)
	{
		this.temaCompromiso = temaCompromiso;
	}

	public List<Compromiso> getCompromisosNoFinalizados()
	{
		return compromisosNoFinalizados;
	}

	public void setCompromisosNoFinalizados(List<Compromiso> compromisosNoFinalizados)
	{
		this.compromisosNoFinalizados = compromisosNoFinalizados;
	}

}
