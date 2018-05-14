package gui.portal.nominas;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import application.CatalogosBean;
import modelo.Producto;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

@ManagedBean
@ViewScoped
public class InfoProductosBean implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private List<Producto>		productos;
	private List<Producto>		productosFiltro;
	private Producto			productoSelec;

	public InfoProductosBean()
	{
		super();

		updateProductos();

	}

	public void updateProductos()
	{
		this.productos = null;

		CatalogosBean catBean = (CatalogosBean) FacesUtils.getManagedBean("catalogosBean");
		catBean.updateCatProductos();
		this.productos = catBean.getCatProductos();

		this.productosFiltro = null;
		this.productoSelec = null;
	}

	// Eliminar el producto seleccionado
	public void actionEliminarProductoSeleccionado()
	{
		try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionNominas();)
		{

			PreparedStatement prep = conexion.prepareStatement("DELETE FROM datvalores WHERE idProducto=? ");
			prep.setInt(1, this.productoSelec.getIdProducto());

			prep.executeUpdate();

			prep = conexion.prepareStatement("DELETE FROM travalores WHERE idProducto=?");
			prep.setInt(1, this.productoSelec.getIdProducto());

			prep.executeUpdate();

			prep = conexion.prepareStatement("DELETE FROM producto WHERE idProducto=?");
			prep.setInt(1, this.productoSelec.getIdProducto());

			prep.executeUpdate();

			updateProductos();

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Producto Eliminado", "El producto se ha eliminado exitosamente."));

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<Producto> getProductos()
	{
		return productos;
	}

	public void setProductos(List<Producto> productos)
	{
		this.productos = productos;
	}

	public List<Producto> getProductosFiltro()
	{
		return productosFiltro;
	}

	public void setProductosFiltro(List<Producto> productosFiltro)
	{
		this.productosFiltro = productosFiltro;
	}

	public Producto getProductoSelec()
	{
		return productoSelec;
	}

	public void setProductoSelec(Producto productoSelec)
	{
		this.productoSelec = productoSelec;
	}

}
