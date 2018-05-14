/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;
import java.util.List;
import util.utilidades;
/**
 *
 * @author desarolloyc
 */
public class BaseSiri implements Serializable
{
    private int idBaseSiri;
    private Date fecha;
    private Time hora;
    private String comentarios;
    private int numTrabajadores;
    
    private PlantillaSiri plantilla;

    public BaseSiri(int idBaseSiri, Date fecha, Time hora, String comentarios)
    {
        this.idBaseSiri = idBaseSiri;
        this.fecha = fecha;
        this.hora = hora;
        this.comentarios = comentarios;
        
        this.numTrabajadores = utilidades.getNumTrabajadoresPlantilla(idBaseSiri);
    }
    
    
    //Actualiza la plantilla de SIRI con todos los registros
    public void updatePlantilla()
    {
        
        List<String> versionDescripcionPlantillaSIRI = utilidades.getDatosVersionDescripcionPlantillaBaseSiri( this.idBaseSiri );
        
        this.plantilla = new PlantillaSiri( this.idBaseSiri, Integer.parseInt(versionDescripcionPlantillaSIRI.get(2) ), versionDescripcionPlantillaSIRI.get(0), versionDescripcionPlantillaSIRI.get(1) );
        
        this.plantilla.setPlantilla( utilidades.getPlantillaLayout(this.plantilla.getIdPlantilla()) );
        this.plantilla.getPlantilla().updateCampos();
        
        this.plantilla.updateRegistros();
        
    }
    
    /**
     * @return the idBaseSiri
     */
    public int getIdBaseSiri()
    {
        return idBaseSiri;
    }

    /**
     * @param idBaseSiri the idBaseSiri to set
     */
    public void setIdBaseSiri(int idBaseSiri)
    {
        this.idBaseSiri = idBaseSiri;
    }

    /**
     * @return the fecha
     */
    public Date getFecha()
    {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha)
    {
        this.fecha = fecha;
    }

    /**
     * @return the hora
     */
    public Time getHora()
    {
        return hora;
    }

    /**
     * @param hora the hora to set
     */
    public void setHora(Time hora)
    {
        this.hora = hora;
    }

    /**
     * @return the comentarios
     */
    public String getComentarios()
    {
        return comentarios;
    }

    /**
     * @param comentarios the comentarios to set
     */
    public void setComentarios(String comentarios)
    {
        this.comentarios = comentarios;
    }

    /**
     * @return the plantilla
     */
    public PlantillaSiri getPlantilla()
    {
        return plantilla;
    }

    /**
     * @param plantilla the plantilla to set
     */
    public void setPlantilla(PlantillaSiri plantilla)
    {
        this.plantilla = plantilla;
    }

    /**
     * @return the numTrabajadores
     */
    public int getNumTrabajadores()
    {
        return numTrabajadores;
    }

    /**
     * @param numTrabajadores the numTrabajadores to set
     */
    public void setNumTrabajadores(int numTrabajadores)
    {
        this.numTrabajadores = numTrabajadores;
    }
    
}
