/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import resources.DataBase;
import util.FacesUtils;

/**
 *
 * @author desarolloyc
 */
public class CorrectorCURP implements Serializable
{
    private Map curps;

    
    public CorrectorCURP()
    {
        getCURPSCorregidosBD();
        
    }
    
    public String getCURPCorreguido(String curpABuscar)
    {
        Object curpCorregido = this.curps.get(curpABuscar);
        
        if ( curpCorregido != null )
        {
            return curpCorregido.toString();
        }
        
        return null;
    }
    
    
    public void getCURPSCorregidosBD()
    {
        try (Connection conexion = ((DataBase) FacesUtils.getManagedBean("database")).getConnectionSIRI();)
        {
            PreparedStatement prep = conexion.prepareStatement(" SELECT * FROM curpcorreccion");
            
            ResultSet rBD = prep.executeQuery();
            
            if ( rBD.next() )
            {
                curps = new HashMap<>();
                
                do
                {
                    curps.put( rBD.getString("CURP"), rBD.getString("CURPCorreccion") );
                    
                } while( rBD.next() );
                
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * @return the curps
     */
    public Map getCurps()
    {
        return curps;
    }

    /**
     * @param curps the curps to set
     */
    public void setCurps(Map curps)
    {
        this.curps = curps;
    }

    
}
