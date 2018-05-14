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
//ALMACENA LOS REGISTROS DE UN DETERMINADO ANEXO Y SU VERSION, ES DECIR, DE UN ARCHIVO SAR100
public class PlantillaBimestre implements Serializable
{
    private Plaza plaza;
    private Unidad unidad;
    private int año;
    private int bimestre;
    
    private Plantilla plantillaEncabezado;
    private Plantilla plantillaDetalles;
    
    private List<PlantillaRegistro> rencabezado;
    private List<PlantillaRegistro> registros;

    public PlantillaBimestre(Plaza plaza, Unidad unidad, int año, int bimestre, Plantilla planEncabezado, Plantilla planDetalles)
    {
        this.plaza = plaza;
        this.unidad = unidad;
        this.año = año;
        this.bimestre = bimestre;
        this.plantillaEncabezado = planEncabezado;
        this.plantillaDetalles = planDetalles;
    }
    
    
    public void updateEncabezado()
    {
        this.rencabezado = utilidades.getPlantillaBimestre(this.plaza.getIdPlaza(), this.unidad.getIdUnidad(), this.año, this.bimestre, this.plantillaEncabezado);
    }
    
    public void updateRegistrosDetalle()
    {
        this.registros = utilidades.getPlantillaBimestre(this.plaza.getIdPlaza(), this.unidad.getIdUnidad(), this.año, this.bimestre, this.plantillaDetalles);
    }
    
    /**
     * @return the plaza
     */
    public Plaza getPlaza()
    {
        return plaza;
    }

    /**
     * @param plaza the plaza to set
     */
    public void setPlaza(Plaza plaza)
    {
        this.plaza = plaza;
    }

    /**
     * @return the unidad
     */
    public Unidad getUnidad()
    {
        return unidad;
    }

    /**
     * @param unidad the unidad to set
     */
    public void setUnidad(Unidad unidad)
    {
        this.unidad = unidad;
    }

    /**
     * @return the año
     */
    public int getAño()
    {
        return año;
    }

    /**
     * @param año the año to set
     */
    public void setAño(int año)
    {
        this.año = año;
    }

    /**
     * @return the bimestre
     */
    public int getBimestre()
    {
        return bimestre;
    }

    /**
     * @param bimestre the bimestre to set
     */
    public void setBimestre(int bimestre)
    {
        this.bimestre = bimestre;
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

    /**
     * @return the rencabezado
     */
    public List<PlantillaRegistro> getRencabezado()
    {
        return rencabezado;
    }

    /**
     * @param rencabezado the rencabezado to set
     */
    public void setRencabezado(List<PlantillaRegistro> rencabezado)
    {
        this.rencabezado = rencabezado;
    }

    /**
     * @return the plantillaEncabezado
     */
    public Plantilla getPlantillaEncabezado()
    {
        return plantillaEncabezado;
    }

    /**
     * @param plantillaEncabezado the plantillaEncabezado to set
     */
    public void setPlantillaEncabezado(Plantilla plantillaEncabezado)
    {
        this.plantillaEncabezado = plantillaEncabezado;
    }

    /**
     * @return the plantillaDetalles
     */
    public Plantilla getPlantillaDetalles()
    {
        return plantillaDetalles;
    }

    /**
     * @param plantillaDetalles the plantillaDetalles to set
     */
    public void setPlantillaDetalles(Plantilla plantillaDetalles)
    {
        this.plantillaDetalles = plantillaDetalles;
    }

    
}
