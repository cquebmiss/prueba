package application;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import modelo.InstrumentoPago;
import modelo.Layout;
import modelo.Plaza;
import modelo.Producto;
import util.utilidades;

@ManagedBean
@ApplicationScoped
public class CatalogosBean implements Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private List<Producto>			catProductos;
	private List<Layout>			catLayout;
	private List<Layout>			catLayoutIP;
	private List<InstrumentoPago>	catInstrumentosPago;
	private List<Plaza>				catPlazas;

	public CatalogosBean()
	{
		super();
	}

	public List<Layout> getCatLayout()
	{
		if (this.catLayout == null)
		{
			this.catLayout = utilidades.getLayoutsBD();
		}

		return catLayout;
	}

	public void setCatLayout(List<Layout> catLayout)
	{
		this.catLayout = catLayout;
	}

	public List<Layout> getCatLayoutIP()
	{
		if (this.catLayoutIP == null)
		{
			this.catLayoutIP = utilidades.getLayoutsInstrumentosPago();
		}

		return catLayoutIP;
	}

	public void setCatLayoutIP(List<Layout> catLayoutIP)
	{
		this.catLayoutIP = catLayoutIP;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public void updateCatInstrumentosPago()
	{
		this.catInstrumentosPago = null;
		getCatInstrumentosPago();
	}

	public List<InstrumentoPago> getCatInstrumentosPago()
	{
		if (this.catInstrumentosPago == null)
		{
			this.catInstrumentosPago = utilidades.getCatInstrumentosPago();
		}

		return catInstrumentosPago;
	}

	public void setCatInstrumentosPago(List<InstrumentoPago> catInstrumentosPago)
	{
		this.catInstrumentosPago = catInstrumentosPago;
	}

	public void updateCatProductos()
	{
		this.catProductos = null;
		getCatProductos();
	}

	public List<Producto> getCatProductos()
	{
		if (this.catProductos == null)
		{
			this.catProductos = utilidades.getProductos();
		}
		return catProductos;
	}

	public void setCatProductos(List<Producto> catProductos)
	{
		this.catProductos = catProductos;
	}

	public void updateCatPlazas()
	{
		this.catPlazas = null;

		getCatPlazas();
	}

	public List<Plaza> getCatPlazas()
	{
		if (this.catPlazas == null)
		{
			this.catPlazas = utilidades.getPlazas();
		}

		return catPlazas;
	}

	public Plaza getPlaza(int idPlaza)
	{

		if (this.catPlazas == null)
		{
			this.catPlazas = utilidades.getPlazas();
		}

		for (Plaza plaza : this.catPlazas)
		{
			if (plaza.getIdPlaza() == idPlaza)
			{
				return plaza;
			}

		}

		return null;

	}

	public void setCatPlazas(List<Plaza> catPlazas)
	{
		this.catPlazas = catPlazas;
	}

}
