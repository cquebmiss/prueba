/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class Usuario implements Serializable
{
    private int idUsuario;
    private String Nombre;
    private String Contrasena;
    private int NivelPermiso;
    private String NivelPermisoLegible;
    private int idStatus;
    private String DescripcionStatus;
    private String NombreReal;
    private String CuentaCorreo;
    private Date VigenciaInicial;
    private Date VigenciaFinal;
    private List<PermisoSistema> permisoSistemas;

    public Usuario(int idUsuario, String Nombre, String Contrasena, int NivelPermiso, String NivelPermisoLegible, int idStatus, String DescripcionStatus, String NombreReal, String CuentaCorreo, Date VigenciaInicial, Date VigenciaFinal)
    {
        this.idUsuario = idUsuario;
        this.Nombre = Nombre;
        this.Contrasena = Contrasena;
        this.NivelPermiso = NivelPermiso;
        this.NivelPermisoLegible = NivelPermisoLegible;
        this.idStatus = idStatus;
        this.DescripcionStatus = DescripcionStatus;
        this.NombreReal = NombreReal;
        this.CuentaCorreo = CuentaCorreo;
        this.VigenciaInicial = VigenciaInicial;
        this.VigenciaFinal = VigenciaFinal;
        
        setPermisoSistemas( utilidades.getEstructuraPermisosSistemas() );
        
        
        if ( idUsuario > -1 )
        {
            List<PermisoSistema> permisosExistentes = utilidades.getPermisosSistemasUsuario( idUsuario );
            
            if ( permisosExistentes == null )
            {
                return;
            }
            
            for( PermisoSistema sistemaPermiso : getPermisoSistemas() )
            {
                for( PermisoSistema permExisten : permisosExistentes )
                {
                    if ( sistemaPermiso.getIdSistema()== permExisten.getIdSistema())
                    {
                        sistemaPermiso.setTipoPermiso( permExisten.getTipoPermiso() );
                        sistemaPermiso.setDescripcionPermiso( permExisten.getDescripcionPermiso() );
                        break;
                    }
                }
                
            }
                    
        }
            
    }

    /**
     * @return the idUsuario
     */
    public int getIdUsuario()
    {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(int idUsuario)
    {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the Nombre
     */
    public String getNombre()
    {
        return Nombre;
    }

    /**
     * @param Nombre the Nombre to set
     */
    public void setNombre(String Nombre)
    {
        this.Nombre = Nombre;
    }

    /**
     * @return the Contrasena
     */
    public String getContrasena()
    {
        return Contrasena;
    }

    /**
     * @param Contrasena the Contrasena to set
     */
    public void setContrasena(String Contrasena)
    {
        this.Contrasena = Contrasena;
    }

    /**
     * @return the NivelPermiso
     */
    public int getNivelPermiso()
    {
        return NivelPermiso;
    }

    /**
     * @param NivelPermiso the NivelPermiso to set
     */
    public void setNivelPermiso(int NivelPermiso)
    {
        this.NivelPermiso = NivelPermiso;
    }

    /**
     * @return the NivelPermisoLegible
     */
    public String getNivelPermisoLegible()
    {
        return NivelPermisoLegible;
    }

    /**
     * @param NivelPermisoLegible the NivelPermisoLegible to set
     */
    public void setNivelPermisoLegible(String NivelPermisoLegible)
    {
        this.NivelPermisoLegible = NivelPermisoLegible;
    }

    /**
     * @return the idStatus
     */
    public int getIdStatus()
    {
        return idStatus;
    }

    /**
     * @param idStatus the idStatus to set
     */
    public void setIdStatus(int idStatus)
    {
        this.idStatus = idStatus;
    }

    /**
     * @return the DescripcionStatus
     */
    public String getDescripcionStatus()
    {
        return DescripcionStatus;
    }

    /**
     * @param DescripcionStatus the DescripcionStatus to set
     */
    public void setDescripcionStatus(String DescripcionStatus)
    {
        this.DescripcionStatus = DescripcionStatus;
    }

    /**
     * @return the NombreReal
     */
    public String getNombreReal()
    {
        return NombreReal;
    }

    /**
     * @param NombreReal the NombreReal to set
     */
    public void setNombreReal(String NombreReal)
    {
        this.NombreReal = NombreReal;
    }

    /**
     * @return the CuentaCorreo
     */
    public String getCuentaCorreo()
    {
        return CuentaCorreo;
    }

    /**
     * @param CuentaCorreo the CuentaCorreo to set
     */
    public void setCuentaCorreo(String CuentaCorreo)
    {
        this.CuentaCorreo = CuentaCorreo;
    }

    /**
     * @return the VigenciaInicial
     */
    public Date getVigenciaInicial()
    {
        return VigenciaInicial;
    }

    /**
     * @param VigenciaInicial the VigenciaInicial to set
     */
    public void setVigenciaInicial(Date VigenciaInicial)
    {
        this.VigenciaInicial = VigenciaInicial;
    }

    /**
     * @return the VigenciaFinal
     */
    public Date getVigenciaFinal()
    {
        return VigenciaFinal;
    }

    /**
     * @param VigenciaFinal the VigenciaFinal to set
     */
    public void setVigenciaFinal(Date VigenciaFinal)
    {
        this.VigenciaFinal = VigenciaFinal;
    }

    /**
     * @return the permisoSistemas
     */
    public List<PermisoSistema> getPermisoSistemas()
    {
        return permisoSistemas;
    }

    /**
     * @param permisoSistemas the permisoSistemas to set
     */
    public void setPermisoSistemas(List<PermisoSistema> permisoSistemas)
    {
        this.permisoSistemas = permisoSistemas;
    }

    
    
}
