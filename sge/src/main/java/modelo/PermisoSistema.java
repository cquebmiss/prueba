/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import resources.DataBase;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
public class PermisoSistema extends Sistema
{
    private int tipoPermiso;
    private String descripcionPermiso;

    
    public PermisoSistema(int idSistema, String descripcion, int tipoPermiso, String descripcionPermiso)
    {
        super(idSistema, descripcion);
        this.tipoPermiso = tipoPermiso;
        this.descripcionPermiso = descripcionPermiso;
    }
    
    
    /**
     * @return the tipoPermiso
     */
    public int getTipoPermiso()
    {
        return tipoPermiso;
    }

    /**
     * @param tipoPermiso the tipoPermiso to set
     */
    public void setTipoPermiso(int tipoPermiso)
    {
        this.tipoPermiso = tipoPermiso;
    }

    /**
     * @return the descripcionPermiso
     */
    public String getDescripcionPermiso()
    {
        return descripcionPermiso;
    }

    /**
     * @param descripcionPermiso the descripcionPermiso to set
     */
    public void setDescripcionPermiso(String descripcionPermiso)
    {
        this.descripcionPermiso = descripcionPermiso;
    }
    
    public void updateDescripcionPermiso()
    {
        if ( getTipoPermiso() == -1 )
        {
            setDescripcionPermiso("Ninguno");
            return;
        }
        
        try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnection();)
        {
            PreparedStatement prep = conexion.prepareStatement(" SELECT Descripcion FROM permisoSistema WHERE idPermiso=? ");
            
            prep.setInt(1, getTipoPermiso());
            
            ResultSet rBD = prep.executeQuery();
            
            if ( rBD.next() )
            {
                do
                {
                    setDescripcionPermiso( rBD.getString("Descripcion") );
                    
                } while ( rBD.next() );
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
