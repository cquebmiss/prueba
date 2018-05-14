/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.portal;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import modelo.PermisoSistema;
import modelo.Usuario;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import resources.DataBase;
import util.FacesUtils;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
@ManagedBean
@SessionScoped

public class UsuariosBean implements Serializable
{

    private List<Usuario> usuariosRegistrados;
    private Usuario usuarioSeleccionado;
    private Usuario nuevoUsuario;

    private List<SelectItem> status;
    private int statusSeleccionado;

    private List<SelectItem> permisoPortal;
    private int idPermisoPortalSeleccionado;

    private List<SelectItem> permisosSistema;
    private String confirmacionContrasena;

    //variable que va a controlar el tipo de acción que se esté ejecutando dentro del panel de opciones
    //-1 solo visualizar panel
    //0 nuevo usuario
    //1 edición de usuario
    private int modoAccion;

    public UsuariosBean()
    {
        setModoAccion(-1);

        setUsuariosRegistrados(utilidades.getUsuariosRegistrados());
        usuarioSeleccionado = null;
        nuevoUsuario = null;

    }

    /**
     * @return the usuariosRegistrados
     */
    public List<Usuario> getUsuariosRegistrados()
    {
        return usuariosRegistrados;
    }

    /**
     * @param usuariosRegistrados the usuariosRegistrados to set
     */
    public void setUsuariosRegistrados(List<Usuario> usuariosRegistrados)
    {
        this.usuariosRegistrados = usuariosRegistrados;
    }

    /**
     * @return the usuarioSeleccionado
     */
    public Usuario getUsuarioSeleccionado()
    {
        return usuarioSeleccionado;
    }

    /**
     * @param usuarioSeleccionado the usuarioSeleccionado to set
     */
    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado)
    {
        this.usuarioSeleccionado = usuarioSeleccionado;
    }

    /**
     * @return the nuevoUsuario
     */
    public Usuario getNuevoUsuario()
    {
        return nuevoUsuario;
    }

    /**
     * @param nuevoUsuario the nuevoUsuario to set
     */
    public void setNuevoUsuario(Usuario nuevoUsuario)
    {
        this.nuevoUsuario = nuevoUsuario;
    }

    /**
     * @return the modoAccion
     */
    public int getModoAccion()
    {
        return modoAccion;
    }

    /**
     * @param modoAccion the modoAccion to set
     */
    public void setModoAccion(int modoAccion)
    {
        this.modoAccion = modoAccion;
    }

    /**
     * @return the status
     */
    public List<SelectItem> getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(List<SelectItem> status)
    {
        this.status = status;
    }

    /**
     * @return the statusSeleccionado
     */
    public int getStatusSeleccionado()
    {
        return statusSeleccionado;
    }

    /**
     * @param statusSeleccionado the statusSeleccionado to set
     */
    public void setStatusSeleccionado(int statusSeleccionado)
    {
        this.statusSeleccionado = statusSeleccionado;
    }

    /**
     * @return the permisoPortal
     */
    public List<SelectItem> getPermisoPortal()
    {
        return permisoPortal;
    }

    /**
     * @param permisoPortal the permisoPortal to set
     */
    public void setPermisoPortal(List<SelectItem> permisoPortal)
    {
        this.permisoPortal = permisoPortal;
    }

    /**
     * @return the idPermisoPortalSeleccionado
     */
    public int getIdPermisoPortalSeleccionado()
    {
        return idPermisoPortalSeleccionado;
    }

    /**
     * @param idPermisoPortalSeleccionado the idPermisoPortalSeleccionado to set
     */
    public void setIdPermisoPortalSeleccionado(int idPermisoPortalSeleccionado)
    {
        this.idPermisoPortalSeleccionado = idPermisoPortalSeleccionado;
    }

    /**
     * @return the permisosSistema
     */
    public List<SelectItem> getPermisosSistema()
    {
        return permisosSistema;
    }

    /**
     * @param permisosSistema the permisosSistema to set
     */
    public void setPermisosSistema(List<SelectItem> permisosSistema)
    {
        this.permisosSistema = permisosSistema;
    }

    /**
     * @return the confirmacionContrasena
     */
    public String getConfirmacionContrasena()
    {
        return confirmacionContrasena;
    }

    /**
     * @param confirmacionContrasena the confirmacionContrasena to set
     */
    public void setConfirmacionContrasena(String confirmacionContrasena)
    {
        this.confirmacionContrasena = confirmacionContrasena;
    }

    public void cambiarModoNuevoUsuario()
    {
        setModoAccion(0);
    }

    public void cambiarModoVerUsuarios()
    {
        setModoAccion(-1);
    }

    
    public void cambiarModo(int modo)
    {
        setModoAccion(modo);

        //Nuevo Usuario
        if (modo == 0)
        {
            preparaNuevoUsuario();

        }//Modo visualización usuarios registrados
        else if ( modo == -1 )
        {
            setUsuariosRegistrados(utilidades.getUsuariosRegistrados());
        }
        else if ( modo == 1 )
        {
            if ( getUsuarioSeleccionado() != null )
            {
                preparaFormularioNuevoUsuario();
                setNuevoUsuario( getUsuarioSeleccionado() );
                
            }
        }
    }
    
    private void preparaFormularioNuevoUsuario()
    {
        setPermisoPortal(utilidades.getPermisosPortal());
        setIdPermisoPortalSeleccionado(2);

        setStatus(utilidades.getStatus());
        setStatusSeleccionado(1);
        
        setPermisosSistema( utilidades.getPermisosSistema() );
    }

    private void preparaNuevoUsuario()
    {
        preparaFormularioNuevoUsuario();
        setNuevoUsuario(new Usuario(-1, null, null, 2, null, 1, null, null, null, null, null));

    }
    
    //actualiza la descripción del permiso en el objeto de permisosistema dentro del objeto nuevo usuario para la correcta visualización al momento de la selección en el formulario de nuevo usuario
    public void onCellEditPermisoSistema(CellEditEvent evt)
    {
        getNuevoUsuario().getPermisoSistemas().get(evt.getRowIndex()).updateDescripcionPermiso();

    }

    public void changeEventListaTipoUsuario(ValueChangeEvent evt)
    {

    }

    public void actionBotonRegistrar(ActionEvent evt)
    {
        FacesContext context = FacesContext.getCurrentInstance();

        Usuario nuevoUsuario = getNuevoUsuario();

        boolean verificarContraseñas = true;
        
        if ( modoAccion == 1 )
        {
            if ( ( nuevoUsuario.getContrasena() == null  && getConfirmacionContrasena() == null ) || ( nuevoUsuario.getContrasena().equals("") && getConfirmacionContrasena().equals("") ) )
            {
                verificarContraseñas = false;
            }
        }
        
        //primero se confirma que las contraseñas coincidan tanto la normal como la confirmación de contraseña
        if ( verificarContraseñas && ! nuevoUsuario.getContrasena().equals(getConfirmacionContrasena()))
        {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La contraseña y su confirmación no coinciden.", null));
            return; 
        }

        if ( nuevoUsuario.getVigenciaInicial() != null && nuevoUsuario.getVigenciaFinal() != null )
        {
            if (nuevoUsuario.getVigenciaInicial().after(nuevoUsuario.getVigenciaFinal()))
            {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "La vigencia inicial debe ser anterior a la vigencia final.", null));
                return;
            }
        }

        try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
        {
            PreparedStatement prep = null;
            int idUsuario = 0;
            String mensajeExito = null;
            String tituloExito = null;
            
            if ( modoAccion == 0 )
            {
                
                prep = conexion.prepareStatement("SELECT * FROM usuario WHERE Nombre=?");
                
                prep.setString(1, nuevoUsuario.getNombre());
                
                ResultSet rBD = prep.executeQuery();
                
                if ( rBD.next() )
                {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre de usuario no está disponible.", null));
                    return;
                }
                
                
                prep = conexion.prepareStatement(" SELECT * FROM Usuario ORDER BY idUsuario DESC");
                
                rBD = prep.executeQuery();
                
                
                boolean idCorrecto = true;
                
                if (rBD.next())
                {
                    idUsuario = (rBD.getInt("idUsuario") + 1);
                }
                
                do
                {
                    idCorrecto = true;
                    
                    prep = conexion.prepareStatement(" INSERT INTO usuario (idUsuario, Nombre, Contrasena, NivelPermiso, idStatus, NombreReal, CuentaCorreo, VigenciaInicial, VigenciaFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
                    
                    prep.setInt(1, idUsuario);
                    prep.setString(2, nuevoUsuario.getNombre());
                    prep.setString(3, utilidades.MD5(nuevoUsuario.getContrasena()));
                    prep.setInt(4, nuevoUsuario.getNivelPermiso());
                    prep.setInt(5, nuevoUsuario.getIdStatus());
                    prep.setString(6, nuevoUsuario.getNombreReal());
                    prep.setString(7, nuevoUsuario.getCuentaCorreo());
                    
                    java.sql.Date sqlDateVigenciaInicial = null;
                    
                    if (nuevoUsuario.getVigenciaInicial() != null)
                    {
                        sqlDateVigenciaInicial = new java.sql.Date(nuevoUsuario.getVigenciaInicial().getTime());
                    }
                    
                    prep.setDate(8, sqlDateVigenciaInicial);
                    
                    java.sql.Date sqlDateVigenciaFinal = null;
                    
                    if (nuevoUsuario.getVigenciaFinal() != null)
                    {
                        sqlDateVigenciaFinal = new java.sql.Date(nuevoUsuario.getVigenciaFinal().getTime());
                    }
                    
                    prep.setDate(9, sqlDateVigenciaFinal);
                    
                    try
                    {
                        prep.executeUpdate();
                        
                    }
                    catch (Exception e)
                    {
                        idCorrecto = false;
                    }
                    
                }
                while (!idCorrecto);
                
                tituloExito = "Usuario Registrado";
                mensajeExito = "El usuario se ha dado de alta exitosamente.";
            }
            else
            {
                idUsuario = nuevoUsuario.getIdUsuario();
                
                int noContraseña = 0;
                
                if ( nuevoUsuario.getContrasena().equals("") )
                {
                    noContraseña++;
                    prep = conexion.prepareStatement(" UPDATE usuario SET Nombre=?, NivelPermiso=?, idStatus=?, NombreReal=?, CuentaCorreo=?, VigenciaInicial=?, VigenciaFinal=? WHERE idUsuario=?  ");
                }
                else
                {
                    prep = conexion.prepareStatement(" UPDATE usuario SET Nombre=?, Contrasena=?, NivelPermiso=?, idStatus=?, NombreReal=?, CuentaCorreo=?, VigenciaInicial=?, VigenciaFinal=? WHERE idUsuario=?  ");
                    prep.setString(2, utilidades.MD5(nuevoUsuario.getContrasena()));
                }
                
                prep.setString(1, nuevoUsuario.getNombre());
                
                prep.setInt(3-noContraseña, nuevoUsuario.getNivelPermiso());
                prep.setInt(4-noContraseña, nuevoUsuario.getIdStatus());
                prep.setString(5-noContraseña, nuevoUsuario.getNombreReal());
                prep.setString(6-noContraseña, nuevoUsuario.getCuentaCorreo());

                java.sql.Date sqlDateVigenciaInicial = null;

                if (nuevoUsuario.getVigenciaInicial() != null)
                {
                    sqlDateVigenciaInicial = new java.sql.Date(nuevoUsuario.getVigenciaInicial().getTime());
                }

                prep.setDate(7-noContraseña, sqlDateVigenciaInicial);

                java.sql.Date sqlDateVigenciaFinal = null;

                if (nuevoUsuario.getVigenciaFinal() != null)
                {
                    sqlDateVigenciaFinal = new java.sql.Date(nuevoUsuario.getVigenciaFinal().getTime());
                }

                prep.setDate(8-noContraseña, sqlDateVigenciaFinal);
                
                prep.setInt(9-noContraseña, idUsuario);
                
                prep.executeUpdate();
                
                prep = conexion.prepareStatement("DELETE FROM permisousuariosistema WHERE idUsuario=?");
                
                prep.setInt(1, idUsuario);
                
                prep.executeUpdate();
                
                tituloExito = "Modificaciones Guardadas";
                mensajeExito = "Las modificaciones se han registrado exitosamente.";
            
            }
            
            //si es permiso usuario se añaden los permisos con los que contará en cada uno de los sistemas desarrollados
            if ( nuevoUsuario.getNivelPermiso() == 2 )
            {
                for( PermisoSistema permisoSistema: nuevoUsuario.getPermisoSistemas() )
                {
                    prep = conexion.prepareStatement(" INSERT INTO permisousuariosistema (idUsuario, idSistema, idPermiso) VALUES (?, ?, ?) ");
                    
                    prep.setInt(1, idUsuario);
                    prep.setInt(2, permisoSistema.getIdSistema() );
                    prep.setInt(3, permisoSistema.getTipoPermiso() );
                    
                    prep.executeUpdate();
                    
                }
            }
            
            
            if ( modoAccion == 1 )
            {
                cambiarModo(-1);
                setUsuariosRegistrados( utilidades.getUsuariosRegistrados() );
            }
            
            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, tituloExito, mensajeExito));
            
        }
        catch (Exception e)
        {
            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "Excepción", "Ha ocurrido una excepción al momento de registrar el usuario, favor de contactar con el desarrollador del sistema."));
            e.printStackTrace();
        }

    }
        
    //Método que sirve para eliminar todos los registros del usuario a eliminarse
    public void actionBotonEliminarUsuario(ActionEvent evt)
    {
        try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionArchivo();)
        {
            
            
            PreparedStatement prep = conexion.prepareStatement("SELECT * FROM bitacoraexpediente WHERE idUsuario=?");
            
            prep.setInt(1, getUsuarioSeleccionado().getIdUsuario() );
            
            ResultSet rBD = prep.executeQuery();
            
            if ( rBD.next() )
            {
                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_WARN, "Imposible Eliminar", "El usuario no se puede eliminar ya que ha registrado documentos, para una eliminación segura contacte con el desarrollador del sistema."));
                return;
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
        {
            
            PreparedStatement prep = conexion.prepareStatement("DELETE FROM permisousuariosistema WHERE idUsuario=?");
            
            prep.setInt(1, getUsuarioSeleccionado().getIdUsuario() );
            
            prep.executeUpdate();
            
            prep = conexion.prepareStatement("DELETE FROM usuario WHERE idUsuario=?");
            
            prep.setInt(1, getUsuarioSeleccionado().getIdUsuario() );
            
            prep.executeUpdate();
            
            setUsuariosRegistrados( utilidades.getUsuariosRegistrados() );
            
            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario Eliminado","El usuario se ha eliminado exitosamente del sistema."));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        
    }

}
