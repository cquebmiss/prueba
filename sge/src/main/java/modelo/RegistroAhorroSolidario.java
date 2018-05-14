/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author DesarrolloYC
 */
public class RegistroAhorroSolidario implements Serializable
{
    
    private String CURP;
    private int porcentaje;

    
    public RegistroAhorroSolidario(String CURP, int porcentaje)
    {
        this.CURP = CURP;
        this.porcentaje = porcentaje;
    }

    /**
     * @return the CURP
     */
    public String getCURP()
    {
        return CURP;
    }

    /**
     * @param CURP the CURP to set
     */
    public void setCURP(String CURP)
    {
        this.CURP = CURP;
    }

    /**
     * @return the porcentaje
     */
    public int getPorcentaje()
    {
        return porcentaje;
    }

    /**
     * @param porcentaje the porcentaje to set
     */
    public void setPorcentaje(int porcentaje)
    {
        this.porcentaje = porcentaje;
    }
    
    
    
}
