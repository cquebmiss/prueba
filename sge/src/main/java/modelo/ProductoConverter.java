package modelo;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class ProductoConverter implements Converter
{
	List<Producto> prods;

	public ProductoConverter()
	{
		super();
	}

	public ProductoConverter(List<Producto> prods)
	{
		this.prods = prods;
	}

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String string)
	{

		for (Producto cam : this.prods)
		{
			if (("" + cam.hashCode()).equalsIgnoreCase(string))
			{
				return cam;
			}
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o)
	{
		if (o != null)
		{
			return "" + o.hashCode();
		}

		return null;

	}

	public List<Producto> getProds()
	{
		return prods;
	}

	public void setProds(List<Producto> prods)
	{
		this.prods = prods;
	}

}