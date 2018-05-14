/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.List;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */
//ALMACENA LOS REGISTROS DE UNA PLANTILLA SIRI Y SU VERSION ESPECIFICA, ES DECIR, DE UN ARCHIVO CATALOGO SIRI
public class PlantillaSiri implements Serializable
{
    private int idBaseSIRI;
    private int idPlantilla;
    private String version;
    private String descripcion;
    
    private Plantilla plantilla;
    private List<PlantillaRegistro> registros;

    public PlantillaSiri(int idBaseSIRI, int idPlantilla, String version, String descripcion)
    {
        this.idBaseSIRI = idBaseSIRI;
        this.idPlantilla = idPlantilla;
        this.version = version;
        this.descripcion = descripcion;
    }
    
    public void updateRegistros()
    {
        
        this.setRegistros(utilidades.getPlantillaRegistrosSIRI(this.idBaseSIRI, getPlantilla()));
        
    }
    
    public PlantillaRegistro findRegistro(int ordenCampoABuscar, String valorBuscado)
    {
        
        String valorEnCampo = null; 
        
        for( PlantillaRegistro registro : this.getRegistros() )
        {
            valorEnCampo = registro.getValorEnCampo(ordenCampoABuscar);
            
            if ( valorEnCampo.equals( valorBuscado ) )
            {
                return registro;
            }
            
        }
        
        
        return null;
        
    }
    

    /**
     * @return the idPlantilla
     */
    public int getIdPlantilla()
    {
        return idPlantilla;
    }

    /**
     * @param idPlantilla the idPlantilla to set
     */
    public void setIdPlantilla(int idPlantilla)
    {
        this.idPlantilla = idPlantilla;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion()
    {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return the idBaseSIRI
     */
    public int getIdBaseSIRI()
    {
        return idBaseSIRI;
    }

    /**
     * @param idBaseSIRI the idBaseSIRI to set
     */
    public void setIdBaseSIRI(int idBaseSIRI)
    {
        this.idBaseSIRI = idBaseSIRI;
    }

    /**
     * @return the plantilla
     */
    public Plantilla getPlantilla()
    {
        return plantilla;
    }

    /**
     * @param plantilla the plantilla to set
     */
    public void setPlantilla(Plantilla plantilla)
    {
        this.plantilla = plantilla;
    }

    /**
     * @return the registros
     */
    public List<PlantillaRegistro> getRegistros()
    {
        return registros;
    }

    /**
     * @param registros the registros to set
     */
    public void setRegistros(List<PlantillaRegistro> registros)
    {
        this.registros = registros;
    }
    
    
    
}
