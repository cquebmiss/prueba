/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
public class Institucion implements Serializable
{
    
    private List<CampoPlantilla> campos;
    private List<RegistroAhorroSolidario> plantillaAhorroSolidario;

    public Institucion()
    {
        this.campos = new ArrayList<>();
        this.plantillaAhorroSolidario = new ArrayList<>();
    }
    
    public Institucion(List<CampoPlantilla> campos)
    {
        this.campos = campos;
    }
    
    
    public void getCamposInstitucionales()
    {
        
        setCampos( utilidades.getCamposInstitucionales() );
        
        //Si no se obtuvo ningún campo, se vuelve a inicializar el arreglo
        if ( this.campos == null )
        {
            this.campos = new ArrayList<>();
        }
        
    }
    
    
    public boolean getValorCoincidente(CampoPlantilla campoBusqueda)
    {
        for( CampoPlantilla campo : this.campos )
        {
            if ( campo.getIdCampo() == campoBusqueda.getIdCampo() )
            {
                campoBusqueda.setValor( campo.getValor() );
                return true;
            }
            
        }
        
        return false;
    }
    
    
    public void updatePlantillaAhorroSolidario()
    {
        this.plantillaAhorroSolidario = utilidades.getRegistrosAhorroSolidario();
    }
    
    /**
     * @return the campos
     */
    public List<CampoPlantilla> getCampos()
    {
        return campos;
    }

    /**
     * @param campos the campos to set
     */
    public void setCampos(List<CampoPlantilla> campos)
    {
        this.campos = campos;
    }

    /**
     * @return the plantillaAhorroSolidario
     */
    public List<RegistroAhorroSolidario> getPlantillaAhorroSolidario()
    {
        return plantillaAhorroSolidario;
    }

    /**
     * @param plantillaAhorroSolidario the plantillaAhorroSolidario to set
     */
    public void setPlantillaAhorroSolidario(List<RegistroAhorroSolidario> plantillaAhorroSolidario)
    {
        this.plantillaAhorroSolidario = plantillaAhorroSolidario;
    }
    
}
